package csa.six.sti;

public class Main {
    public static void main(String[] args) {
        System.out.println("This is the scoreboard");
        Scoreboard scoreboard = new Scoreboard(); //initializing scoreboard object
        Leaderboard leaderboard = new Leaderboard(); //initializing leaderboard object

        //Just spit out the values in the scoreboard constructor for now
        System.out.println("Team: " + scoreboard.getTeamName());
        System.out.println("Score: " + scoreboard.getScore());
        System.out.println("Rank: " + scoreboard.getRank());
        System.out.println("# of correctly answered questions: " + scoreboard.getCorrectAnswer());
        System.out.println("# of incorrectly answered questions: " + scoreboard.getIncorrectAnswer());
        System.out.println("Amount wager on Doubly O(n): " + scoreboard.getDoubleOnWager());
        
        //Spit out values from Leaderboard too
        System.out.println("Number of teams on leaderboard: " + leaderboard.getNumTeams());
        System.out.println("Scoreboard for team: " + leaderboard.getScoreboards()[0].getTeamName());
    }
}
