package csa.sti;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;


public class Gameboard {

    //the five categories, in the order they go across the board
    private String[] categories = {
        "General Sort", "Code Logic", "Spot the Mistake", "Unscramble Code", "Insertion Mayhem"
    };

    //the board grid from gameboard.fxml
    @FXML private GridPane board;
    @FXML private ComboBox<Player> teamPicker;
    @FXML private Label scoreLabel;
    @FXML private Label questionStatusLabel;
    @FXML private Button correctButton;
    @FXML private Button incorrectButton;

    //all 25 question buttons, stored as buttons[column][row] (row 0 = 100 points, row 4 = 500 points)
    private Button[][] buttons = new Button[5][5];
    private Button selectedQuestionButton;
    private Player selectedTeam;
    private int selectedPointValue;

    //builds the category names and the 25 question buttons when the gameboard opens
    @FXML
    private void initialize() {
        teamPicker.setItems(GameState.getTeams());
        teamPicker.valueProperty().addListener(
            (obs, oldTeam, newTeam) -> updateScoreLabel(newTeam)
        );
        correctButton.setDisable(true);
        incorrectButton.setDisable(true);
        updateScoreLabel(null);

        if (!GameState.getTeams().isEmpty()) {
            teamPicker.getSelectionModel().selectFirst();
        }
        for (int column = 0; column < 5; column++) {
            //category name goes in the top row of each column
            board.add(new Label(categories[column]), column, 0);

            for (int row = 0; row < 5; row++) {
                int points = (row + 1) * 100;
                String category = categories[column];

                Button questionButton = new Button("" + points);
                questionButton.setPrefSize(110, 50);
                questionButton.setOnAction(event -> {
                    if (selectedQuestionButton != null) {
                        return;
                    }
                    Player team = teamPicker.getValue();
                    if (team == null) {
                        new Alert(Alert.AlertType.INFORMATION, "Add or select a team first.").showAndWait();
                        return;
                    }
                    selectedQuestionButton = questionButton;
                    selectedTeam = team;
                    selectedPointValue = points;
                    teamPicker.setDisable(true);
                    correctButton.setDisable(false);
                    incorrectButton.setDisable(false);
                    questionStatusLabel.setText(category + " - " + points + " points");
                    System.out.println("Picked " + category + " for " + points);
                });

                buttons[column][row] = questionButton;
                board.add(questionButton, column, row + 1);
            }
        }
    }

    //point to the leaderboard navigation
    @FXML
    private void switchToLeaderboard() throws IOException {
        App.setRoot("leaderboard");
    }

    @FXML
    private void switchToLanding() throws IOException {
        App.setRoot("landing");
    }

    public void awardPoints(Player team, int selectedPointValue) {
        ScoringService.awardPoints(team, selectedPointValue);
        updateScoreLabel(team);
    }

    public void deductPoints(Player team, int selectedPointValue) {
        ScoringService.deductPoints(team, selectedPointValue);
        updateScoreLabel(team);
    }

    public void stealPoints(Player stealingTeam, Player otherTeam, int selectedPointValue) {
        ScoringService.stealPoints(stealingTeam, otherTeam, selectedPointValue);
        updateScoreLabel(stealingTeam);
    }

    @FXML
    private void markCorrect() {
        if (selectedTeam != null && selectedQuestionButton != null) {
            awardPoints(selectedTeam, selectedPointValue);
            finishQuestion();
        }
    }

    @FXML
    private void markIncorrect() {
        if (selectedTeam != null && selectedQuestionButton != null) {
            deductPoints(selectedTeam, selectedPointValue);
            finishQuestion();
        }
    }

    private void updateScoreLabel(Player team) {
        scoreLabel.setText(team == null ? "Score: -" : "Score: " + team.getScore());
    }

    private void finishQuestion() {
        selectedQuestionButton.setDisable(true);
        selectedQuestionButton = null;
        selectedTeam = null;
        selectedPointValue = 0;
        teamPicker.setDisable(false);
        correctButton.setDisable(true);
        incorrectButton.setDisable(true);
        questionStatusLabel.setText("Select a team and a question.");
    }
}
