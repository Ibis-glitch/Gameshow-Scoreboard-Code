package csa.sti;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.IntegerProperty;

//Tracks who's in the game (players participating)
public class Player {
    //variables
    private final IntegerProperty streak = new SimpleIntegerProperty(0);    //Streak variable
    private final StringProperty name = new SimpleStringProperty();         //players name
    private final IntegerProperty score = new SimpleIntegerProperty();      //players score

    //full constructor
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

    //Property acessors (so that table can see changes)
    public StringProperty nameProperty() {
        return name;
    }

    public IntegerProperty scoreProperty() {
        return score;
    }

    public IntegerProperty streakProperty() {
        return streak;
    }

    //call these in the gameboard later on!!!
    //If player correctly answers a question
    //give one streak, this will add on later on
    public void recordCorrect() { 
        streak.set(streak.get() + 1);
    }

    //If player incorrectly answers a question
    //Set streak back to 0
    public void recordIncorrect() {
        streak.set(0);
    }

    //Dump
    @Override 
    public String toString() {
        return getName();
    }
}
