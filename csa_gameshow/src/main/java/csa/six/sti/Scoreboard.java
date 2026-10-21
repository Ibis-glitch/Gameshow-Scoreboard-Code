package csa.six.sti;

public class Scoreboard {
    private int score;              //score team has concurrently
    private String teamName;        //name of the team
    private int rank;               //ranking the team has on leaderboard (make a leaderboard too later on)
    private int correctAnswer;      //number of questions answered correctly    
    private int incorrectAnswer;    //number of questions answered incorrectly
    private float doubleOnWager;    //number of points wagered on Double O(n) questions

    //Default Constructor (idk if we make the other two)
    public Scoreboard() {
        this.score = 0;
        this.teamName = "";
        this.rank = 0;
        this.correctAnswer = 0;
        this.incorrectAnswer = 0;
        this.doubleOnWager = 0.0f;
    }

    //Getters
    public String getTeamName()  {
        return this.teamName;
    } 
    
    public int getScore() {
        return this.score;
    }

    public int getRank() {
        return this.rank;
    }

    public int getCorrectAnswer() {
        return this.correctAnswer;
    }

    public int getIncorrectAnswer() {
        return this.incorrectAnswer;
    }

    public double getDoubleOnWager() {
        return this.doubleOnWager;
    }

    //Setters
    public void setTeamName(String teamName) {
        //User validation goes here 
        this.teamName = teamName;
    }

    public void setScore(int score) {
        //User validation goes here 
        this.score = score;
    }

    public void setRank(int rank) {
        //User validation goes here 
        this.rank = rank;
    }

    public void setCorrectAnswer(int correctAnswer) {
        //User validation goes here 
        this.correctAnswer = correctAnswer;
    }

    public void setIncorrectAnswer(int incorrectAnswer) {
        //User validation goes here 
        this.incorrectAnswer = incorrectAnswer;
    }

    public void setDoubleOnWager(float doubleOnWager) {
        //User validation goes here 
        this.doubleOnWager = doubleOnWager;
    }
}

//Someone read the prompt man. I have no clue what we are to do.
