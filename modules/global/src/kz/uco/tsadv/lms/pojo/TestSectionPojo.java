package kz.uco.tsadv.lms.pojo;

import java.io.Serializable;
import java.util.List;

public class TestSectionPojo implements Serializable {
    protected String id;
    protected String name;
    protected List<QuestionPojo> questionsAndAnswers;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<QuestionPojo> getQuestionsAndAnswers() {
        return questionsAndAnswers;
    }

    public void setQuestionsAndAnswers(List<QuestionPojo> questionsAndAnswers) {
        this.questionsAndAnswers = questionsAndAnswers;
    }
}
