package csa.sti;

import java.io.IOException;
import java.util.Comparator;
import java.util.Optional;

import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ButtonType;

public class Leaderboard {

    //Creating the variables
    @FXML private TableView<Player> leaderboardTable;
    @FXML private TableColumn<Player, Number> rankColumn;
    @FXML private TableColumn<Player, String> nameColumn;
    @FXML private TableColumn<Player, Integer> scoreColumn;
    @FXML private TableColumn<Player, Number> streakColumn;
    @FXML private TextField nameField;
    @FXML private TextField scoreField;

    //point to the gameboard navigation
    @FXML
    private void switchToGameboard() throws IOException {
        App.setRoot("gameboard");
    }

    //this is to ensure that rankings will be the same if two
    //teams have the same score
    //(i.e. Team 1: 300 pts. Team 2: 300 pts. Team 3: 400 pts.)
    //(#1 Team 3, #2 Team 2, #2, Team 1)
    @FXML
    private int rankOf(int score) {
        int higher = 0;
        for (Player p : GameState.getTeams()) {
            if (p.getScore() > score) higher ++;
        }
        return higher + 1;
    }

    //Creating the leaderboard table
    @FXML
    private void initialize() throws IOException {
        //Streak Column
        streakColumn.setCellValueFactory(cd -> cd.getValue().streakProperty());
        streakColumn.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Number streak, boolean empty) {
                super.updateItem(streak, empty);
                //if no streak, then set no streak
                //if streak more than 3, give a fire emoji and the streak score
                //if streak more than 0, give the streak score
                //set base value of streak to nothing
                if (empty || streak == null) {
                    setText(null);
                } else if (streak.intValue() >= 3) {
                    setText("🔥 " + streak.intValue());
                } else if (streak.intValue() > 0) {
                    setText(String.valueOf(streak.intValue()));
                } else {
                    setText("-");
                }
            }
        });
        
        //Ranking Column
        rankColumn.setCellValueFactory(cd -> cd.getValue().scoreProperty());

        rankColumn.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Number score, boolean empty) {
                super.updateItem(score, empty);
                //if no score, then set no score
                //else give the score value
                if (empty || score == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(rankOf(score.intValue())));
                }
            }
        });

        //Name Column
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        //Score Column
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));

        //Sample Data & Sorting
        //Make the ranking listed in descending score order
        SortedList<Player> sorted = new SortedList<> (
            GameState.getTeams(),
            Comparator.comparingInt(Player::getScore).reversed()
                      .thenComparing(Player::getName, String.CASE_INSENSITIVE_ORDER));
        leaderboardTable.setItems(sorted);
        
        //Listener is update the table
        sorted.addListener((javafx.collections.ListChangeListener<Player>) c->
            leaderboardTable.refresh());

    }

    //Adding teams function
    @FXML 
    private void addTeam() throws IOException {

        //Creating the popup for adding teams
        //Setting up the structure for the popup dialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Team");
        dialog.setHeaderText("New team");
        dialog.setContentText("Team name:");

        //The empty textbox input
        //This is where the user will input new team names
        Optional<String> result = dialog.showAndWait();

        //Checking if there is an input 
        result.ifPresent(input -> {
            String name = input.trim();
            
            //First checking if the input is empty or not
            //If it is empty, send an alert that you must input a name
            if (name.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Input a team name").showAndWait();
                return;
            }

            //This is under the condition that there is an input
            //exists will be used to check if name already exists
            boolean exists = GameState.getTeams().stream()
                .anyMatch(t -> t.getName().equalsIgnoreCase(name));
            
            //If the team name already exists, send back a message saying team already exists
            if (exists) {
                new Alert(Alert.AlertType.WARNING, "Team already exists ;p").showAndWait();
                return;
            }

            //Actually adding the team to the table after passing all conditionals
            //Only input the team name, rankings should be automatic
            GameState.getTeams().add(new Player(name, 0, 0)); //score automatically set to zero
        });
    }

    //Removing teams from the tables
    @FXML 
    private void removeTeam() throws IOException {
        
        //First checking if there are any teams present in the table
        //If the text input is empty, then send back a text message 
        //saying there are no existing teams
        if (GameState.getTeams().isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "There are no teams present").showAndWait();
            return;
        }

        //Open up a popoup for removing teams
        //Open up a choice dialog in which there is a dropdown for 
        //which team to remove
        //Setting up the structure of the popup
        ChoiceDialog<Player> dialog = new ChoiceDialog<>(
            GameState.getTeams().get(0), GameState.getTeams());
        dialog.setTitle("Remove Team");
        dialog.setHeaderText("Choose which team to remove");
        dialog.setContentText("Team:");
        dialog.setContentText("New Name:");
        
        //Saving the option the player chooses to pick
        Optional<Player> result = dialog.showAndWait();

        //Checking if the team exists
        //And then sending an alert saying if the user would like to confirm choice
        result.ifPresent(team -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Remove " + team.getName() + " (score: " + team.getScore() + ")?");
            confirm.showAndWait()
                .filter(button -> button == ButtonType.OK)
                .ifPresent(button -> GameState.getTeams().remove(team));
        });
    }

    // Editing the team name
    // Used for the edit team button
    @FXML
    private void editTeam() throws IOException {
        //\Checking if there are any teams on the table
        if (GameState.getTeams().isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "There are no teams present").showAndWait();
            return;
        }

        //Open up a popoup for editing teams
        ChoiceDialog<Player> dialog = new ChoiceDialog<>(
            GameState.getTeams().get(0), GameState.getTeams());
        dialog.setTitle("Edit Team");                           // Title of popup
        dialog.setHeaderText("Choose which team to rename");    // Text of popup
        dialog.setContentText("Team:");                         // Label for the dropdown 

        // Saving the name for the team that needs to be editted
        Optional<Player> result = dialog.showAndWait();
        
        // Returning if the player clicked cancel
        if (result.isEmpty()) {
            return;
        }
        
        // New pop up for a text input (user types something)
        TextInputDialog editDialog = new TextInputDialog();       // Text input popup
        editDialog.setContentText("New name:");                  // Label for the text input

        // Saving the new name for the team
        Optional<String> editResult = editDialog.showAndWait();

        // Returning if the user clicked cancel on the text input dialog
        if (editResult.isEmpty()) {
            return;
        }

        //Checking if there is an input 
        editResult.ifPresent(input -> {
            String name = input.trim();
            
            //First checking if the input is empty or not
            //If it is empty, send an alert that you must input a name
            if (name.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Input a team name").showAndWait();
                return;
            }

            //This is under the condition that there is an input
            //exists will be used to check if name already exists
            boolean exists = GameState.getTeams().stream()
                .anyMatch(t -> t != result.get()
                    && t.getName().equalsIgnoreCase(name));
            
            //If the team name already exists, send back a message saying team already exists
            if (exists) {
                new Alert(Alert.AlertType.WARNING, "Team already exists ;p").showAndWait();
                return;
            }
        });

        // Asking for confirmation for changing the team name
        result.ifPresent(team -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Edit " + team.getName() + " to " + editResult.get() + "?");
                
            // Showing confirmation dialog and changing the team name if user confirms
            confirm.showAndWait()
                .filter(button -> button == ButtonType.OK)
                .ifPresent(button -> team.setName(editResult.get()));
        });
    }
}
