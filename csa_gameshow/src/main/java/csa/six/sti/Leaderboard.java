package csa.six.sti;

public class Leaderboard {
    private Scoreboard[] scoreboards; //Array of Scoreboard objects to hold the scores ig
    private int numTeams; //Number of teams on leaderboard
    //probably ranking should go here, but whatever

    //Default Constructor
    public Leaderboard() {
        this.scoreboards = new Scoreboard[10]; //max of 10 teams
        this.numTeams = 0;
    }

    //Getters 
    public Scoreboard[] getScoreboards() {
        return this.scoreboards;
    }

    public int getNumTeams() {
        return this.numTeams;
    }

    //Setters
    public void setScoreboards(Scoreboard[] scoreboards) {
        //User validation goes here 
        this.scoreboards = scoreboards;
    }

    public void setNumTeams(int numTeams) {
        //User validation goes here 
        this.numTeams = numTeams; 
    }
}
