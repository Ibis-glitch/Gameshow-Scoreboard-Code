package csa.sti;

import java.io.IOException;

import javafx.fxml.FXML;

/** Handles navigation from the game's landing page. */
public class LandingController {

    @FXML
    private void openMainGame() throws IOException {
        App.setRoot("gameboard");
    }

    @FXML
    private void openFastMoney() throws IOException {
        App.setRoot("fastmoney");
    }

    @FXML
    private void openLeaderboard() throws IOException {
        App.setRoot("leaderboard");
    }
}
