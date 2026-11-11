package csa.sti;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.IntegerProperty;

/** A team's name, score, and answer streak, exposed as JavaFX properties for live UI updates. */
public class Player {
    // JavaFX properties let bound controls refresh when a value changes.
    private final IntegerProperty streak = new SimpleIntegerProperty(0);    //Streak variable
    private final StringProperty name = new SimpleStringProperty();         //players name
    private final IntegerProperty score = new SimpleIntegerProperty();      //players score

    /** Creates a team with the supplied initial name, score, and streak. */
    public Player(String name, int score, int streak) {
        this.name.set(name);
        this.score.set(score);
        this.streak.set(streak);
    }

    //Getters
    public String getName() {
        return name.get();
    }

    public int getScore() {
        return score.get();
    }

    public int getStreak() {
        return streak.get();
    }

    //Setters
    public void setName(String name) {
        //User validation goes here
        this.name.set(name);
    }

    public void setScore(int score) {
        //User validation goes here
        this.score.set(score);
    }

    public void setStreak(int streak) {
        //User validation goes here 
        this.streak.set(streak);
    }

    // Property accessors are used by JavaFX tables and observable collections.
    public StringProperty nameProperty() {
        return name;
    }

    public IntegerProperty scoreProperty() {
        return score;
    }

    public IntegerProperty streakProperty() {
        return streak;
    }

    /** Records a correct answer by increasing the current streak by one. */
    public void recordCorrect() { 
        streak.set(streak.get() + 1);
    }

    /** Records an incorrect answer by resetting the current streak. */
    public void recordIncorrect() {
        streak.set(0);
    }

    @Override 
    public String toString() {
        // Provides a readable label when a Player is shown in a JavaFX choice control.
        return getName();
    }
}
