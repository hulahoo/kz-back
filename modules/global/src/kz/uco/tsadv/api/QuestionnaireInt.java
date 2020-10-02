package kz.uco.tsadv.api;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import kz.uco.tsadv.api.AbstractEntityInt;
import kz.uco.tsadv.api.QuestionInt;

import java.util.List;

@MetaClass(name = "tsadv$QuestionnaireInt")
public class QuestionnaireInt extends AbstractEntityInt {
    private static final long serialVersionUID = -5110762936314603744L;

    @MetaProperty
    protected String name;

    @MetaProperty
    protected List<QuestionInt> questions;

    @MetaProperty
    protected String instruction;

    public void setQuestions(List<QuestionInt> questions) {
        this.questions = questions;
    }

    public List<QuestionInt> getQuestions() {
        return questions;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getInstruction() {
        return instruction;
    }


}