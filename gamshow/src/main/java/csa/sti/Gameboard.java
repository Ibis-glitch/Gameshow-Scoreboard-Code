package csa.sti;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;


public class Gameboard {

    //the five categories, in the order they go across the board
    private String[] categories = {
        "General Sort", "Code Logic", "Spot the Mistake", "Unscramble Code", "Insertion Mayhem"
    };

    //the board grid from gameboard.fxml
    @FXML private GridPane board;

    //all 25 question buttons, stored as buttons[column][row] (row 0 = 100 points, row 4 = 500 points)
    private Button[][] buttons = new Button[5][5];

    //builds the category names and the 25 question buttons when the gameboard opens
    @FXML
    private void initialize() {
        for (int column = 0; column < 5; column++) {
            //category name goes in the top row of each column
            board.add(new Label(categories[column]), column, 0);

            for (int row = 0; row < 5; row++) {
                int points = (row + 1) * 100;
                String category = categories[column];

                Button questionButton = new Button("" + points);
                questionButton.setPrefSize(110, 50);
                questionButton.setOnAction(event -> {
                    System.out.println("Picked " + category + " for " + points);
                    questionButton.setDisable(true);
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
}
