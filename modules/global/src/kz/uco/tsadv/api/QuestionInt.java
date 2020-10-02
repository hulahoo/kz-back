package kz.uco.tsadv.api;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import kz.uco.tsadv.api.AbstractEntityInt;
import kz.uco.tsadv.api.AnswerInt;

import java.util.List;

@MetaClass(name = "tsadv$QuestionInt")
public class QuestionInt extends AbstractEntityInt {
    private static final long serialVersionUID = -913005640947176258L;

    @MetaProperty
    protected String questionText;

    @MetaProperty
    protected String answerType;

    @MetaProperty
    protected List<AnswerInt> answers;

    public void setAnswers(List<AnswerInt> answers) {
        this.answers = answers;
    }

    public List<AnswerInt> getAnswers() {
        return answers;
    }


    public void setAnswerType(String answerType) {
        this.answerType = answerType;
    }

    public String getAnswerType() {
        return answerType;
    }


    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionText() {
        return questionText;
    }


}