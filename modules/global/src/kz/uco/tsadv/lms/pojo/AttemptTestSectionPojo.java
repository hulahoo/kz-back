package kz.uco.tsadv.lms.pojo;

import java.util.List;

public class AttemptTestSectionPojo {

    protected String testSectionId;

    protected List<AttemptQuestionPojo> questionsAndAnswers;

    public String getTestSectionId() {
        return testSectionId;
    }

    public void setTestSectionId(String testSectionId) {
        this.testSectionId = testSectionId;
    }

    public List<AttemptQuestionPojo> getQuestionsAndAnswers() {
        return questionsAndAnswers;
    }

    public void setQuestionsAndAnswers(List<AttemptQuestionPojo> questionsAndAnswers) {
        this.questionsAndAnswers = questionsAndAnswers;
    }
}
