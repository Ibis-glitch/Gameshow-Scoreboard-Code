package csa.sti;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FastMoney {

    @FXML
    private GridPane responseGrid;

    @FXML
    private Button refreshButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Label contestant1Total;

    @FXML
    private Label contestant2Total;

    @FXML
    private Label winnerLabel;

    private final FastMoneyService service = new FastMoneyService();
    private final List<QuestionRow> questionRows = new ArrayList<>();

    @FXML
    private void initialize() {
        for (int row = 1; row <= 5; row++) {
            QuestionRow questionRow = new QuestionRow(
                    cell(1, row, Label.class),
                    cell(2, row, TextField.class),
                    cell(3, row, Label.class),
                    cell(4, row, TextField.class),
                    cell(5, row, Label.class));
            questionRows.add(questionRow);
        }

        for (QuestionRow row : questionRows) {
            row.contestant1Answer.textProperty().addListener((observable, oldValue, newValue) -> updateScores());
            row.contestant2Answer.textProperty().addListener((observable, oldValue, newValue) -> updateScores());
        }

        refreshResponses();
    }

    @FXML
    private void refreshResponses() {
        refreshButton.setDisable(true);
        statusLabel.setText("Loading latest survey responses...");

        CompletableFuture
                .supplyAsync(() -> {
                    try {
                        return service.fetchQuestions();
                    } catch (IOException exception) {
                        throw new CompletionException(exception);
                    } catch (InterruptedException exception) {
                        Thread.currentThread().interrupt();
                        throw new CompletionException(exception);
                    }
                })
                .whenComplete((counts, failure) -> Platform.runLater(() -> {
                    refreshButton.setDisable(false);
                    if (failure != null) {
                        Throwable cause = failure;
                        while (cause instanceof CompletionException && cause.getCause() != null) {
                            cause = cause.getCause();
                        }
                        statusLabel.setText("Could not load survey responses: " + cause.getMessage());
                        System.err.println("Could not load survey responses: " + cause);
                        return;
                    }

                    for (int index = 0; index < questionRows.size(); index++) {
                        FastMoneyQuestion question = counts.get(index);
                        questionRows.get(index).showQuestion(question.getQuestion());
                        questionRows.get(index).setResponses(question.getResponses());
                    }
                    statusLabel.setText("Survey responses updated.");
                    updateScores();
                }));
    }

    @FXML
    private void switchToLanding() throws IOException {
        App.setRoot("landing");
    }

    @FXML
    private void showSurveyQrCode() {
        var imageResource = FastMoney.class.getResource("/csa/sti/fastmoney-form-qr.png");
        if (imageResource == null) {
            statusLabel.setText("The survey QR image is missing from the application resources.");
            System.err.println("Missing application resource: /csa/sti/fastmoney-form-qr.png");
            return;
        }

        Image image = new Image(imageResource.toExternalForm(), 600, 600, true, false);
        if (image.isError()) {
            Exception imageFailure = image.getException();
            String message = imageFailure == null ? "Unknown image loading error" : imageFailure.getMessage();
            statusLabel.setText("Could not load the survey QR image: " + message);
            System.err.println("Could not load the survey QR image: " + imageFailure);
            return;
        }

        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(false);

        Label instructions = new Label("Scan this code to open the survey.");
        VBox content = new VBox(12, imageView, instructions);
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-padding: 16; -fx-background-color: white;");

        Stage qrWindow = new Stage();
        qrWindow.setTitle("Survey QR Code");
        qrWindow.initOwner(responseGrid.getScene().getWindow());
        qrWindow.initModality(Modality.WINDOW_MODAL);
        qrWindow.setResizable(false);
        qrWindow.setScene(new Scene(content));
        qrWindow.show();
    }

    private void updateScores() {
        int firstTotal = 0;
        int secondTotal = 0;

        for (QuestionRow row : questionRows) {
            int firstPoints = row.pointsFor(row.contestant1Answer.getText());
            int secondPoints = row.pointsFor(row.contestant2Answer.getText());
            row.contestant1Points.setText(Integer.toString(firstPoints));
            row.contestant2Points.setText(Integer.toString(secondPoints));
            firstTotal = Math.addExact(firstTotal, firstPoints);
            secondTotal = Math.addExact(secondTotal, secondPoints);
        }

        contestant1Total.setText("Contestant 1 total: " + firstTotal);
        contestant2Total.setText("Contestant 2 total: " + secondTotal);
        winnerLabel.setText(firstTotal == secondTotal
                ? "Winner: Tie"
                : "Winner: Contestant " + (firstTotal > secondTotal ? "1" : "2"));
    }

    private <T extends Node> T cell(int column, int row, Class<T> type) {
        return responseGrid.getChildren().stream()
                .filter(node -> Integer.valueOf(column).equals(GridPane.getColumnIndex(node)))
                .filter(node -> Integer.valueOf(row).equals(GridPane.getRowIndex(node)))
                .filter(type::isInstance)
                .map(type::cast)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Missing " + type.getSimpleName() + " at grid column " + column + ", row " + row));
    }

    private static final class QuestionRow {
        private final Label question;
        private final TextField contestant1Answer;
        private final Label contestant1Points;
        private final TextField contestant2Answer;
        private final Label contestant2Points;
        private List<FastMoneyResponse> responses = List.of();

        private QuestionRow(
                Label question,
                TextField contestant1Answer,
                Label contestant1Points,
                TextField contestant2Answer,
                Label contestant2Points) {
            this.question = question;
            this.contestant1Answer = contestant1Answer;
            this.contestant1Points = contestant1Points;
            this.contestant2Answer = contestant2Answer;
            this.contestant2Points = contestant2Points;
        }

        private void setResponses(List<FastMoneyResponse> answerCounts) {
            responses = answerCounts;
        }

        private void showQuestion(String text) {
            question.setText(text);
            question.setWrapText(true);
            question.setPrefWidth(180);
        }

        private int pointsFor(String answer) {
            return responses.stream()
                    .filter(response -> response.matches(answer))
                    .mapToInt(FastMoneyResponse::getCount)
                    .findFirst()
                    .orElse(0);
        }
    }
}
