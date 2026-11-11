package csa.sti;

import java.text.Normalizer;
import java.util.Locale;

/** A normalized-matchable survey answer and the number of people who gave it. */
public final class FastMoneyResponse {
    private final String answer;
    private final int count;

    public FastMoneyResponse(String answer, int count) {
        if (answer == null || answer.isBlank()) {
            throw new IllegalArgumentException("Answer must not be blank");
        }
        if (count < 1) {
            throw new IllegalArgumentException("Answer count must be positive");
        }

        this.answer = answer.trim();
        this.count = count;
    }

    public String getAnswer() {
        return answer;
    }

    public int getCount() {
        return count;
    }

    public boolean matches(String candidate) {
        // Blank input never earns points, even if normalization would otherwise match.
        return candidate != null && !candidate.isBlank()
                && normalize(candidate).equals(normalize(answer));
    }

    static String normalize(String answer) {
        // NFKC and locale-independent lowercasing make equivalent text comparable.
        String normalized = Normalizer.normalize(answer, Normalizer.Form.NFKC)
                .toLowerCase(Locale.ROOT);
        StringBuilder result = new StringBuilder(normalized.length());
        boolean pendingSpace = false;

        for (int index = 0; index < normalized.length(); index++) {
            char current = normalized.charAt(index);
            if (Character.isLetterOrDigit(current)) {
                // Collapse runs of separators to a single space between meaningful characters.
                if (pendingSpace && result.length() > 0) {
                    result.append(' ');
                }
                result.append(current);
                pendingSpace = false;
            } else if (isNumericPunctuation(normalized, index)) {
                // Keep punctuation that distinguishes numeric answers such as 2.5 or -3.
                result.append(current);
                pendingSpace = false;
            } else {
                pendingSpace = true;
            }
        }

        return result.toString();
    }

    private static boolean isNumericPunctuation(String answer, int index) {
        char current = answer.charAt(index);
        // Only preserve punctuation when it is part of a number, not ordinary prose.
        if (index + 1 >= answer.length() || !Character.isDigit(answer.charAt(index + 1))) {
            return false;
        }
        if ((current == '+' || current == '-') && (index == 0
                || !Character.isLetterOrDigit(answer.charAt(index - 1)))) {
            return true;
        }
        return (current == '.' || current == ',' || current == ':' || current == '-')
                && index > 0
                && Character.isDigit(answer.charAt(index - 1));
    }
}
