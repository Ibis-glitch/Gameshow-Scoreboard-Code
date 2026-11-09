//Developed by Gargi, Sophie, Jimmy
//Gameboard Class
//Period 6- CSA-Commander Schenk
//5 November 2026

package csa.sti;
//Imports->
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

    //all 25 question panels, stored as buttons[column][row] (row 0 = 100 points, row 4 = 500 points)
    private Button[][] buttons = new Button[5][5];
    private Button selectedQuestionButton;
    private int selectedQuestionColumn;
    private int selectedQuestionRow;
    private Player selectedTeam;
    private int selectedPointValue;
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
                Button questionButton = new Button("" + points);
                questionButton.setPrefSize(110, 50);
                questionButton.setDisable(GameState.isQuestionUsed(column, row));
                questionButton.setOnAction(event -> {
                    //if a question is already selected, nothing happens
                    if (selectedQuestionButton != null) {
                        return;
                    }
                    //Getting the value from the teamPicker
                    Player team = teamPicker.getValue();
                    if (team == null) {
                        new Alert(Alert.AlertType.INFORMATION, "Add or select a team first.").showAndWait();
                        return;
                    }
                    selectedQuestionButton = questionButton;
                    selectedQuestionColumn = questionColumn;
                    selectedQuestionRow = questionRow;
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

    @FXML
    private void clearBoard() {
        GameState.clearUsedQuestions();
        for (Button[] column : buttons) {
            for (Button questionButton : column) {
                questionButton.setDisable(false);
            }
        }
    }

    private void updateScoreLabel(Player team) {
        scoreLabel.setText(team == null ? "Score: -" : "Score: " + team.getScore());
    }

    private void finishQuestion() {
        GameState.markQuestionUsed(selectedQuestionColumn, selectedQuestionRow);
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
