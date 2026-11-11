package csa.sti;

import java.util.List;

/** Holds one Fast Money prompt and the survey answers that can score for it. */
public class FastMoneyQuestion {
    private final String question;
    private final List<FastMoneyResponse> responses;

    public FastMoneyQuestion(String question, List<FastMoneyResponse> responses) {
        this.question = question;
        // Prevent callers from changing the response list after this question is created.
        this.responses = List.copyOf(responses);
    }

    public String getQuestion() {
        return question;
    }

    public List<FastMoneyResponse> getResponses() {
        return responses;
    }
}
