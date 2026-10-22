package csa.sti;

import java.io.IOException;
import java.util.Comparator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

public class Leaderboard {
    //Creating the table components
    @FXML private TableView<Player> leaderboardTable;
    @FXML private TableColumn<Player, Number> rankColumn;
    @FXML private TableColumn<Player, String> nameColumn;
    @FXML private TableColumn<Player, Integer> scoreColumn;

    //point to the gameboard navigation
    @FXML
    private void switchToGameboard() throws IOException {
        App.setRoot("gameboard");
    }

    @FXML
    private void leaderboardTable() throws IOException {

        //Ranking Columns
        rankColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(leaderboardTable.getItems().indexOf(cellData.getValue()) + 1));
        
        //Name Column
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        //Score Column
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));

        //Sample Data & Sorting
        ObservableList<Player> playerList = FXCollections.observableArrayList(
            new Player("Team 1", 100),
            new Player("Team 2", 200),
            new Player("Team 3", 150)
        );
        
        //Sort by descending score
        playerList.sort(Comparator.comparingInt(Player::getScore).reversed());
        leaderboardTable.setItems(playerList);
    }
}