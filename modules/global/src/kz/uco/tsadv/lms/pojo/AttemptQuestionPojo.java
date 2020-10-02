package kz.uco.tsadv.lms.pojo;

import java.io.Serializable;
import java.util.List;

public class AttemptQuestionPojo implements Serializable {
    protected String questionId;
    protected List<String> answer;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public List<String> getAnswer() {
        return answer;
    }

    public void setAnswer(List<String> answer) {
        this.answer = answer;
    }
}
