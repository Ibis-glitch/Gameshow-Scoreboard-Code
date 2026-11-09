package csa.sti;

import java.util.List;

public class FastMoneyQuestion {
    private final String question;
    private final List<FastMoneyResponse> responses;

    public FastMoneyQuestion(String question, List<FastMoneyResponse> responses) {
        this.question = question;
        this.responses = List.copyOf(responses);
    }

    public String getQuestion() {
        return question;
    }

    public List<FastMoneyResponse> getResponses() {
        return responses;
    }
}
