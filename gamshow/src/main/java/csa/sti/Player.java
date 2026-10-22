package csa.sti;

//Tracks who's in the game (players participating)
public class Player {
    //variables
    private String name;    //players name
    private int score;      //players score

    //full constructor
    public Player(String name, int score) {
        this.name = name;
        this.score = score;
    }

    //Getters
    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    //Setteres
    public void setName(String name) {
        this.name = name;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
