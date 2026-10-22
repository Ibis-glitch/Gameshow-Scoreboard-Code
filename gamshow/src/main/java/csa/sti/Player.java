package csa.sti;

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

    //Setters
    public void setName(String name) {
        //User validation goes here
        this.name = name;
    }

    public void setScore(int score) {
        //User validation goes here
        this.score = score;
    }
}
