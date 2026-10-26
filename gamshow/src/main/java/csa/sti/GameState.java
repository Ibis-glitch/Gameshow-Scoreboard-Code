package csa.sti;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

//Holds the shared game date (aka the lists of teams) 
//Also allows for saving and loading team files
//Every screen should read from here so that they can access all the same teams
public class GameState {

    //Saving the team to a file so that it can be accessed again
    //This is to ensure already created teams aren't deleted once broswer is closed and opened again
    //This is where the file is saved
    private static final Path SAVE_FILE =
        Paths.get(System.getProperty("user.home"), "gameshow-teams.text");

    //Shared list of teams
    private static final ObservableList<Player> teams = 
        FXCollections.observableArrayList(p -> new Observable[] {p.scoreProperty(), p.streakProperty() });
    
    //Runs once, only when the first time GameState is used
    static {
        //Loading saved teams
        load();
        //After loading, save every change to the file
        //(i.e. team added, deleted, etc...)
        //Listener keeps track of those things
        teams.addListener((ListChangeListener<Player>) c -> save());
    }

    //Lets other classes have access to the list of teams
    public static ObservableList<Player> getTeams() {
        return teams;
    }

    //Saving the list
    //Writes every team to a line
    private static void save() {
        List<String> lines = new ArrayList<>();
        //the format for saving teams
        //Done with a tab so that teams with comma don't get separated
        for (Player p : teams) {
            lines.add(p.getName() + "\t" + p.getScore() + "\t" + p.getStreak());
        }
        try {
            //overwrites the file with current list
            Files.write(SAVE_FILE, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            //catches errors
            //if unable to save file, print that could not save team
            System.err.println("Could not save teams: " + e.getMessage());
        }
    }

    //For loading the saved teams
    //Reads the save file to recreate the teams
    private static void load() {
        //No save file exists yet, therefore nothing to load
        if (!Files.exists(SAVE_FILE)) return;
        try {
            for (String line : Files.readAllLines(SAVE_FILE, StandardCharsets.UTF_8)) {
                //Split the line back into the full name, score, and streak
                String[] parts = line.split("\t");
                //No teams loaded, Accepts only 2 fields
                if (parts.length < 2) continue;
                try {
                    int score = Integer.parseInt(parts[1].trim());
                    //Old save files have no streak, so default to 0
                    int streak = parts.length >= 3 ? Integer.parseInt(parts[2].trim()) : 0;
                    teams.add(new Player(parts[0], score, streak));
                } catch (NumberFormatException ignored) { }
                //Skips lines where score or streak isn't a valid number
            }
        } catch (IOException e) {
            //if loading fails, print problem
            System.err.println("Could not load teams: " + e.getMessage());
        }
    }
}
