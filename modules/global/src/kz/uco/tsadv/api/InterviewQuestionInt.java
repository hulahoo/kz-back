package kz.uco.tsadv.api;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import kz.uco.tsadv.api.AbstractEntityInt;
import kz.uco.tsadv.api.InterviewAnswerInt;

import java.util.List;
import java.util.UUID;

@MetaClass(name = "tsadv$InterviewQuestionInt")
public class InterviewQuestionInt extends AbstractEntityInt {
    private static final long serialVersionUID = -2981723504396352387L;

    @MetaProperty
    protected List<InterviewAnswerInt> answers;

    @MetaProperty
    protected UUID questionId;

    @MetaProperty
    protected String anotherAnswer;

    @MetaProperty
    protected String type;

    public UUID getQuestionId() {
        return questionId;
    }

    public void setQuestionId(UUID questionId) {
        this.questionId = questionId;
    }



    public void setAnotherAnswer(String anotherAnswer) {
        this.anotherAnswer = anotherAnswer;
    }

    public String getAnotherAnswer() {
        return anotherAnswer;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }


    public void setAnswers(List<InterviewAnswerInt> answers) {
        this.answers = answers;
    }

    public List<InterviewAnswerInt> getAnswers() {
        return answers;
    }


}