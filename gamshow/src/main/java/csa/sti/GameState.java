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

/** Stores shared teams and question-use state, and persists team changes locally. */
public class GameState {

    // Board coordinates are indexed as [column][row], matching the game-board buttons.
    private static final boolean[][] usedQuestions = new boolean[5][5];

    // Save outside the application directory so team data survives app updates and restarts.
    private static final Path SAVE_FILE =
        Paths.get(System.getProperty("user.home"), "gameshow-teams.text");

    // The extractor turns Player property changes into observable list update events.
    private static final ObservableList<Player> teams = 
        FXCollections.observableArrayList(p -> new Observable[] 
            {   
                p.nameProperty(),
                p.scoreProperty(), 
                p.streakProperty() });
    
    // Load before attaching the listener so initialization does not immediately rewrite the file.
    static {
        load();
        // Persist additions, removals, and property updates made through the shared list.
        teams.addListener((ListChangeListener<Player>) c -> save());
    }

    /** Returns the observable team list shared by all screens. */
    public static ObservableList<Player> getTeams() {
        return teams;
    }

    /** Reports whether the board cell at the given column and row has been played. */
    public static boolean isQuestionUsed(int column, int row) {
        return usedQuestions[column][row];
    }

    /** Marks a board cell as played so it cannot be selected again this round. */
    public static void markQuestionUsed(int column, int row) {
        usedQuestions[column][row] = true;
    }

    /** Makes every board cell available for a new round. */
    public static void clearUsedQuestions() {
        for (int column = 0; column < usedQuestions.length; column++) {
            for (int row = 0; row < usedQuestions[column].length; row++) {
                usedQuestions[column][row] = false;
            }
        }
    }

    // Rewrite the snapshot so removals and edits are reflected as well as additions.
    private static void save() {
        List<String> lines = new ArrayList<>();
        // Tab separators allow commas in team names without affecting the saved fields.
        for (Player p : teams) {
            lines.add(p.getName() + "\t" + p.getScore() + "\t" + p.getStreak());
        }
        try {
            Files.write(SAVE_FILE, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            // Report persistence failures without interrupting gameplay.
            System.err.println("Could not save teams: " + e.getMessage());
        }
    }

    // Restore the saved team snapshot; missing or malformed individual entries are skipped.
    private static void load() {
        if (!Files.exists(SAVE_FILE)) return;
        try {
            for (String line : Files.readAllLines(SAVE_FILE, StandardCharsets.UTF_8)) {
                String[] parts = line.split("\t");
                // Two-field records are supported for compatibility with saves made before streaks.
                if (parts.length < 2) continue;
                try {
                    int score = Integer.parseInt(parts[1].trim());
                    int streak = parts.length >= 3 ? Integer.parseInt(parts[2].trim()) : 0;
                    teams.add(new Player(parts[0], score, streak));
                } catch (NumberFormatException ignored) {
                    // Skip only the invalid record so other saved teams can still be restored.
                }
            }
        } catch (IOException e) {
            // Report read failures while allowing the application to start with an empty list.
            System.err.println("Could not load teams: " + e.getMessage());
        }
    }
}
