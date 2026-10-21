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


//This is what we have to do (pulled from proposal)

//_______________________________________________________________
//The code will demonstrate a form where the point values 
//are also stored in the array and make it easy for the teams to 
//calculate points. When you click on a point value and select 
//the team, the points will automatically be added to that team, 
//as well as blurring out the point value for the specific category, 
//as the value won't be available anymore. It will have a clean UI so 
//that anyone, not just the creators, could use it and have added 
//features like light and dark mode, selection of doubling the points 
//if they hit a daily double, and also subtracting the points in case 
//they get the question. Another feature is that there is a steal option, 
//so we don't run into the issue of not being able to select the point 
//anymore in case one team gets minus the certain amount of points 
//and the other gets the points. This will make it super simple and 
//easy to calculate the points and make sure the game runs very smoothly.

//_______________________________________________________________________
//A separate feature will also be collecting the responses from a form 
//that the audience will fill out, so that for Fast Money, it will be 
//easy to calculate the point values for each question. It will also 
//calculate the total amount of points for Fast Money to make it easier 
//to determine the winner!

