package csa.sti;

import java.util.Objects;

public class ScoringService {

    public static void awardPoints(Player team, int points) {
        requireTeamAndPoints(team, points);
        team.setScore(Math.addExact(team.getScore(), points));
        team.recordCorrect();
    }

    public static void deductPoints(Player team, int points) {
        requireTeamAndPoints(team, points);
        team.setScore(Math.subtractExact(team.getScore(), points));
        team.recordIncorrect();
    }

    public static void stealPoints(Player stealingTeam, Player otherTeam, int points) {
        requireTeamAndPoints(stealingTeam, points);
        Objects.requireNonNull(otherTeam, "Other team must not be null");
        if (stealingTeam == otherTeam) {
            throw new IllegalArgumentException("A team cannot steal points from itself");
        }

        int stealingTeamScore = Math.addExact(stealingTeam.getScore(), points);
        int otherTeamScore = Math.subtractExact(otherTeam.getScore(), points);

        stealingTeam.setScore(stealingTeamScore);
        otherTeam.setScore(otherTeamScore);
        stealingTeam.recordCorrect();
        otherTeam.recordIncorrect();
    }

    private static void requireTeamAndPoints(Player team, int points) {
        Objects.requireNonNull(team, "Team must not be null");
        if (points <= 0) {
            throw new IllegalArgumentException("Points must be greater than zero");
        }
    }
}
