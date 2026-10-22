package csa.sti;

import java.io.IOException;

import javafx.fxml.FXML;


public class Gameboard {

    //point to the leaderboard navigation
    @FXML
    private void switchToLeaderboard() throws IOException {
        App.setRoot("leaderboard");
    }
}
