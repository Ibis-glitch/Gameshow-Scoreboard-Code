//Developed by Gargi, Sophie, Jimmy
//Gameboard Class
//Period 6- CSA-Commander Schenk
//5 November 2026

package csa.sti;
//Imports->
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ChoiceDialog;
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
    @FXML private Button stealButton;
    @FXML private Button doublePointsButton;

    //all 25 question panels, stored as buttons[column][row] (row 0 = 100 points, row 4 = 500 points)
    private Button[][] buttons = new Button[5][5];
    private Button selectedQuestionButton;
    private int selectedQuestionColumn;
    private int selectedQuestionRow;
    private Player selectedTeam;
    private int selectedPointValue;
    private boolean doublePointsApplied;
    //Buttons created and UI styled by Gargi, Panels created by Jimmy, Functions for buttons defined by Sophie
    //builds the category names and the 25 question buttons when the gameboard opens
    @FXML
    private void initialize() {
        //populate the team picker with the teams from GameState and set up a listener to update the score label when a team is selected
        teamPicker.setItems(GameState.getTeams());
        teamPicker.valueProperty().addListener(
            (obs, oldTeam, newTeam) -> updateScoreLabel(newTeam)
        );
        correctButton.setDisable(true);
        incorrectButton.setDisable(true);
        stealButton.setDisable(true);
        doublePointsButton.setDisable(true);
        updateScoreLabel(null);

        //select the first team in the picker if there are any teams
        //Checks if it's empty, if not, selects the first team in the picker
        if (!GameState.getTeams().isEmpty()) {
            teamPicker.getSelectionModel().selectFirst();
        }
        for (int column = 0; column < 5; column++) {
            //category name goes in the top row of each column
            board.add(new Label(categories[column]), column, 0);
            //For loop to create the 5 question buttons for each column, with point values increasing by 100 for each row
            //Initial function created by Jimmy
            //Fixed by Gargi to integrate with other pages
            for (int row = 0; row < 5; row++) {
                int points = (row + 1) * 100;
                String category = categories[column];
                int questionColumn = column;
                int questionRow = row;
                Button questionButton = new Button(column == 4 ? "" : Integer.toString(points));
                questionButton.setPrefSize(110, 50);
                questionButton.setDisable(GameState.isQuestionUsed(column, row));
                questionButton.setOnAction(event -> {
                    if (selectedQuestionButton == questionButton) {
                        clearQuestionSelection("Select a team and a question to track points");
                        return;
                    }

                    Player team = selectedTeam == null ? teamPicker.getValue() : selectedTeam;
                    if (team == null) {
                        new Alert(Alert.AlertType.INFORMATION, "Add or select a team first.").showAndWait();
                        return;
                    }
                    if (selectedQuestionButton != null) {
                        selectedQuestionButton.getStyleClass().remove("selected-question");
                    }
                    selectedQuestionButton = questionButton;
                    selectedQuestionButton.getStyleClass().add("selected-question");
                    selectedQuestionColumn = questionColumn;
                    selectedQuestionRow = questionRow;
                    selectedTeam = team;
                    doublePointsApplied = false;
                    selectedPointValue = questionColumn == 4
                        ? ThreadLocalRandom.current().nextInt(100, 501)
                        : points;
                    teamPicker.setDisable(true);
                    correctButton.setDisable(false);
                    incorrectButton.setDisable(false);
                    stealButton.setDisable(false);
                    doublePointsButton.setDisable(!isDoublePointsQuestion(
                        selectedQuestionColumn, selectedQuestionRow));
                    questionStatusLabel.setText(category + " - " + selectedPointValue + " points");
                });

                buttons[column][row] = questionButton;
                board.add(questionButton, column, row + 1);
            }
        }
    }

    //point to the leaderboard navigation
    //Functions defined by Sophie
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
    //Gargi almost forgot to add this function, but she remembered in time... (Documented by Gargi haha)
    public void stealPoints(Player stealingTeam, Player otherTeam, int selectedPointValue) {
        ScoringService.stealPoints(stealingTeam, otherTeam, selectedPointValue);
        updateScoreLabel(teamPicker.getValue());
    }

    @FXML
    private void markCorrect() {
        if (selectedTeam != null && selectedQuestionButton != null) {
            awardPoints(selectedTeam, pointsForSelectedQuestion());
            finishQuestion();
        }
    }

    @FXML
    private void markIncorrect() {
        if (selectedTeam != null && selectedQuestionButton != null) {
            deductPoints(selectedTeam, pointsForSelectedQuestion());
            finishQuestion();
        }
    }

    @FXML
    private void doublePoints() {
        if (selectedQuestionButton == null
                || !isDoublePointsQuestion(selectedQuestionColumn, selectedQuestionRow)
                || doublePointsButton.isDisabled()) {
            return;
        }

        doublePointsApplied = true;
        doublePointsButton.setDisable(true);
        questionStatusLabel.setText(categories[selectedQuestionColumn] + " - "
            + pointsForSelectedQuestion() + " points (doubled)");
    }

    @FXML
    private void handleStealPoints() {
        if (selectedTeam == null || selectedQuestionButton == null) {
            return;
        }

        List<Player> eligibleTeams = GameState.getTeams().stream()
            .filter(team -> team != selectedTeam)
            .toList();
        if (eligibleTeams.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "Add another team before stealing points.").showAndWait();
            return;
        }

        ChoiceDialog<Player> dialog = new ChoiceDialog<>(eligibleTeams.get(0), eligibleTeams);
        dialog.setTitle("Steal Points");
        dialog.setHeaderText("Which team is stealing " + selectedTeam.getName()
            + "'s " + pointsForSelectedQuestion() + " points?");
        dialog.setContentText("Stealing team:");
        dialog.showAndWait().ifPresent(stealingTeam -> {
            stealPoints(stealingTeam, selectedTeam, pointsForSelectedQuestion());
            finishQuestion();
        });
    }

    @FXML
    private void clearBoard() {
        GameState.clearUsedQuestions();
        for (Player team : GameState.getTeams()) {
            team.setScore(0);
        }
        clearQuestionSelection("Select a team and a question to track points");
        setQuestionButtonsDisabled(false);
        updateScoreLabel(teamPicker.getValue());
    }

    private void updateScoreLabel(Player team) {
        scoreLabel.setText(team == null ? "Score: -" : "Score: " + team.getScore());
    }

    private void finishQuestion() {
        GameState.markQuestionUsed(selectedQuestionColumn, selectedQuestionRow);
        clearQuestionSelection("Select a team and a question.");
        setQuestionButtonsDisabled(false);
    }

    private void clearQuestionSelection(String status) {
        if (selectedQuestionButton != null) {
            selectedQuestionButton.getStyleClass().remove("selected-question");
        }
        selectedQuestionButton = null;
        selectedTeam = null;
        selectedPointValue = 0;
        doublePointsApplied = false;
        teamPicker.setDisable(false);
        correctButton.setDisable(true);
        incorrectButton.setDisable(true);
        stealButton.setDisable(true);
        doublePointsButton.setDisable(true);
        questionStatusLabel.setText(status);
    }

    private boolean isDoublePointsQuestion(int column, int row) {
        return (column == 2 && row == 2) || (column == 4 && row == 4);
    }

    private int pointsForSelectedQuestion() {
        return doublePointsApplied ? Math.multiplyExact(selectedPointValue, 2) : selectedPointValue;
    }

    private void setQuestionButtonsDisabled(boolean disabled) {
        for (int column = 0; column < buttons.length; column++) {
            for (int row = 0; row < buttons[column].length; row++) {
                buttons[column][row].setDisable(
                    disabled || GameState.isQuestionUsed(column, row));
            }
        }
    }
}
