package csa.sti;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Fetches the Google Sheets response data and aggregates answers for the Fast Money round. */
public class FastMoneyService {
    // The FXML screen and survey are designed around five question columns.
    private static final int QUESTION_COUNT = 5;
    private static final String DEFAULT_RANGE = "B:F";

    // Reuse a client across calls and cap how long a connection attempt may take.
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public List<FastMoneyQuestion> fetchQuestions() throws IOException, InterruptedException {
        // Environment variables override values from the optional local .env file.
        Map<String, String> config = loadConfiguration();
        String apiKey = requiredConfig(config, "apiKey");
        String spreadsheetId = requiredConfig(config, "spreadsheetId");
        String range = config.getOrDefault("sheetRange", DEFAULT_RANGE);
        String requestUrl = "https://sheets.googleapis.com/v4/spreadsheets/"
                + encodePathSegment(spreadsheetId) + "/values/" + encodePathSegment(range)
                + "?key=" + encodeQueryParameter(apiKey);

        HttpRequest request = HttpRequest.newBuilder(URI.create(requestUrl))
                .timeout(Duration.ofSeconds(20))
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Translate API failures into actionable messages instead of treating them as empty results.
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            String explanation = switch (response.statusCode()) {
                case 400 -> " Check that sheetRange uses valid A1 notation, for example B:F for columns B through F "
                        + "on the first sheet.";
                case 403 -> " Check that the Sheets API is enabled and that this sheet is readable with an API key.";
                default -> "";
            };
            String details = response.body().length() > 300
                    ? response.body().substring(0, 300) + "..."
                    : response.body();
            throw new IOException("Google Sheets API returned HTTP " + response.statusCode()
                    + "." + explanation + " " + details);
        }

        return countAnswers(parseResponseValues(response.body()));
    }

    static List<FastMoneyQuestion> countAnswers(List<List<String>> rows) {
        // One insertion-ordered map per question keeps the first submitted spelling for each answer.
        List<Map<String, FastMoneyResponse>> countsByQuestion = new ArrayList<>(QUESTION_COUNT);
        for (int question = 0; question < QUESTION_COUNT; question++) {
            countsByQuestion.add(new LinkedHashMap<>());
        }

        // Row zero contains the question headers; subsequent rows contain contestant submissions.
        for (int rowIndex = 1; rowIndex < rows.size(); rowIndex++) {
            List<String> row = rows.get(rowIndex);
            for (int question = 0; question < QUESTION_COUNT && question < row.size(); question++) {
                String answer = row.get(question);
                if (answer == null || answer.isBlank()) {
                    continue;
                }

                Map<String, FastMoneyResponse> answerCounts = countsByQuestion.get(question);
                String normalized = FastMoneyResponse.normalize(answer);
                FastMoneyResponse previous = answerCounts.get(normalized);
                // Aggregate equivalent spellings while retaining the first display form.
                answerCounts.put(normalized, previous == null
                        ? new FastMoneyResponse(answer, 1)
                        : new FastMoneyResponse(previous.getAnswer(), Math.addExact(previous.getCount(), 1)));
            }
        }

        List<FastMoneyQuestion> result = new ArrayList<>(QUESTION_COUNT);
        for (int question = 0; question < QUESTION_COUNT; question++) {
            // Sort counts from most to least common for consistent response ordering.
            List<FastMoneyResponse> responses = countsByQuestion.get(question).values().stream()
                    .sorted(Comparator.comparingInt(FastMoneyResponse::getCount).reversed())
                    .toList();
            String questionText = rows.isEmpty() || question >= rows.get(0).size()
                    || rows.get(0).get(question).isBlank()
                    ? "Question " + (question + 1)
                    : rows.get(0).get(question).trim();
            result.add(new FastMoneyQuestion(questionText, responses));
        }
        return result;
    }

    private static List<List<String>> parseResponseValues(String body) throws IOException {
        // Parse just the JSON structures needed from the Sheets API without an external JSON library.
        Object parsed = new JsonReader(body).read();
        if (!(parsed instanceof Map<?, ?> responseObject)) {
            throw new IOException("Google Sheets returned an unexpected JSON response.");
        }

        Object rawValues = responseObject.get("values");
        if (rawValues == null) {
            return List.of();
        }
        if (!(rawValues instanceof List<?> rows)) {
            throw new IOException("Google Sheets response did not contain a valid values array.");
        }

        List<List<String>> result = new ArrayList<>(rows.size());
        for (Object rawRow : rows) {
            if (!(rawRow instanceof List<?> cells)) {
                throw new IOException("Google Sheets response contained an invalid row.");
            }
            List<String> row = new ArrayList<>(cells.size());
            for (Object cell : cells) {
                row.add(cell == null ? "" : cell.toString());
            }
            result.add(row);
        }
        return result;
    }

    private static Map<String, String> loadConfiguration() throws IOException {
        Map<String, String> config = new LinkedHashMap<>();
        Path workingDirectory = Path.of("").toAbsolutePath();
        Path envFile = workingDirectory.resolve(".env");
        // Also check the parent to support launching the Maven app from its module directory.
        if (!Files.isRegularFile(envFile) && workingDirectory.getParent() != null) {
            envFile = workingDirectory.getParent().resolve(".env");
        }

        if (Files.isRegularFile(envFile)) {
            for (String line : Files.readAllLines(envFile, StandardCharsets.UTF_8)) {
                String trimmed = line.trim();
                // Ignore blank and commented lines; accept simple KEY=VALUE entries.
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                int separator = trimmed.indexOf('=');
                if (separator <= 0) {
                    continue;
                }
                String name = trimmed.substring(0, separator).trim();
                String value = trimmed.substring(separator + 1).trim();
                if (value.length() >= 2
                        && ((value.startsWith("\"") && value.endsWith("\""))
                                || (value.startsWith("'") && value.endsWith("'")))) {
                    value = value.substring(1, value.length() - 1);
                }
                config.put(name, value);
            }
        }

        // Environment variables take precedence over values loaded from disk.
        for (String name : List.of("apiKey", "spreadsheetId", "sheetRange")) {
            String environmentValue = System.getenv(name);
            if (environmentValue != null && !environmentValue.isBlank()) {
                config.put(name, environmentValue.trim());
            }
        }
        return config;
    }

    private static String requiredConfig(Map<String, String> config, String name) throws IOException {
        String value = config.get(name);
        if (value == null || value.isBlank()) {
            throw new IOException("Missing " + name
                    + " configuration. Set it in the repository .env file or as an environment variable.");
        }
        return value;
    }

    private static String encodePathSegment(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private static String encodeQueryParameter(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static final class JsonReader {
        private final String json;
        // Cursor is advanced as each JSON token is consumed.
        private int position;

        private JsonReader(String json) {
            this.json = Objects.requireNonNull(json);
        }

        private Object read() throws IOException {
            Object value = readValue();
            skipWhitespace();
            // Reject valid prefixes followed by trailing garbage.
            if (position != json.length()) {
                throw malformedJson();
            }
            return value;
        }

        private Object readValue() throws IOException {
            skipWhitespace();
            if (position >= json.length()) {
                throw malformedJson();
            }

            // Dispatch on the next JSON token and return a Java representation of that value.
            return switch (json.charAt(position)) {
                case '{' -> readObject();
                case '[' -> readArray();
                case '"' -> readString();
                case 't' -> readLiteral("true", Boolean.TRUE);
                case 'f' -> readLiteral("false", Boolean.FALSE);
                case 'n' -> readLiteral("null", null);
                default -> readNumber();
            };
        }

        private Map<String, Object> readObject() throws IOException {
            // Read key/value pairs until the matching closing brace.
            position++;
            Map<String, Object> result = new LinkedHashMap<>();
            skipWhitespace();
            if (consume('}')) {
                return result;
            }

            while (true) {
                skipWhitespace();
                if (position >= json.length() || json.charAt(position) != '"') {
                    throw malformedJson();
                }
                String key = readString();
                skipWhitespace();
                require(':');
                result.put(key, readValue());
                skipWhitespace();
                if (consume('}')) {
                    return result;
                }
                require(',');
            }
        }

        private List<Object> readArray() throws IOException {
            // Read comma-separated values until the matching closing bracket.
            position++;
            List<Object> result = new ArrayList<>();
            skipWhitespace();
            if (consume(']')) {
                return result;
            }

            while (true) {
                result.add(readValue());
                skipWhitespace();
                if (consume(']')) {
                    return result;
                }
                require(',');
            }
        }

        private String readString() throws IOException {
            require('"');
            StringBuilder result = new StringBuilder();
            while (position < json.length()) {
                char current = json.charAt(position++);
                if (current == '"') {
                    return result.toString();
                }
                if (current != '\\') {
                    if (current < 0x20) {
                        throw malformedJson();
                    }
                    result.append(current);
                    continue;
                }

                // Decode the JSON escape sequences supported by strings.
                if (position >= json.length()) {
                    throw malformedJson();
                }
                char escaped = json.charAt(position++);
                switch (escaped) {
                    case '"', '\\', '/' -> result.append(escaped);
                    case 'b' -> result.append('\b');
                    case 'f' -> result.append('\f');
                    case 'n' -> result.append('\n');
                    case 'r' -> result.append('\r');
                    case 't' -> result.append('\t');
                    case 'u' -> result.append(readUnicodeEscape());
                    default -> throw malformedJson();
                }
            }
            throw malformedJson();
        }

        private char readUnicodeEscape() throws IOException {
            // A Unicode escape consists of four hexadecimal digits after the backslash.
            if (position + 4 > json.length()) {
                throw malformedJson();
            }
            try {
                char value = (char) Integer.parseInt(json.substring(position, position + 4), 16);
                position += 4;
                return value;
            } catch (NumberFormatException exception) {
                throw malformedJson();
            }
        }

        private Object readNumber() throws IOException {
            int start = position;
            // BigDecimal validates the token after the candidate numeric characters are collected.
            while (position < json.length()
                    && "-+0123456789.eE".indexOf(json.charAt(position)) >= 0) {
                position++;
            }
            if (start == position) {
                throw malformedJson();
            }
            try {
                return new BigDecimal(json.substring(start, position));
            } catch (NumberFormatException exception) {
                throw malformedJson();
            }
        }

        private Object readLiteral(String literal, Object value) throws IOException {
            if (!json.startsWith(literal, position)) {
                throw malformedJson();
            }
            position += literal.length();
            return value;
        }

        private void require(char expected) throws IOException {
            skipWhitespace();
            if (!consume(expected)) {
                throw malformedJson();
            }
        }

        private boolean consume(char expected) {
            // Consume only on a match so callers can test for optional delimiters.
            if (position < json.length() && json.charAt(position) == expected) {
                position++;
                return true;
            }
            return false;
        }

        private void skipWhitespace() {
            while (position < json.length() && Character.isWhitespace(json.charAt(position))) {
                position++;
            }
        }

        private IOException malformedJson() {
            // Include the cursor location to make malformed API responses diagnosable.
            return new IOException("Google Sheets returned malformed JSON near character " + position + ".");
        }
    }
}
