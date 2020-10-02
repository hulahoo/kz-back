package kz.uco.tsadv.lms.pojo;

import kz.uco.tsadv.modules.learning.enums.QuestionType;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class QuestionPojo implements Serializable {
    protected UUID id;
    protected String text;
    protected QuestionType type;
    protected List<AnswerPojo> answers;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<AnswerPojo> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerPojo> answers) {
        this.answers = answers;
    }

    public QuestionType getType() {
        return type;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }
}
