package kz.uco.tsadv.api;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;

@MetaClass(name = "tsadv$AnswerInt")
public class AnswerInt extends AbstractEntityInt {
    private static final long serialVersionUID = -2981245969182444808L;

    @MetaProperty
    protected String answerText;

    @MetaProperty
    protected String weight;

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }



    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public String getAnswerText() {
        return answerText;
    }


}