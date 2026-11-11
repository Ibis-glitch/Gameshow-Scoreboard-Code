package csa.sti;

import java.io.IOException;

import javafx.fxml.FXML;

/** Handles navigation from the game's landing page. */
public class LandingController {

    @FXML
    private void openMainGame() throws IOException {
        // Change the shared scene to the main question board.
        App.setRoot("gameboard");
    }

    @FXML
    private void openFastMoney() throws IOException {
        // Open the survey-driven Fast Money round.
        App.setRoot("fastmoney");
    }

    @FXML
    private void openLeaderboard() throws IOException {
        // Open the shared team and score table.
        App.setRoot("leaderboard");
    }
}
