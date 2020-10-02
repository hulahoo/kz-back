package kz.uco.tsadv.lms.pojo;

import java.io.Serializable;
import java.util.List;

public class AnsweredTest implements Serializable {
    protected String attemptId;
    protected List<AttemptQuestionPojo> questionsAndAnswers;


    public String getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(String attemptId) {
        this.attemptId = attemptId;
    }

    public List<AttemptQuestionPojo> getQuestionsAndAnswers() {
        return questionsAndAnswers;
    }

    public void setQuestionsAndAnswers(List<AttemptQuestionPojo> questionsAndAnswers) {
        this.questionsAndAnswers = questionsAndAnswers;
    }
}
