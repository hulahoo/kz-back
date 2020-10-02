package kz.uco.tsadv.api;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import kz.uco.tsadv.api.AbstractEntityInt;
import kz.uco.tsadv.api.InterviewQuestionInt;

import java.util.List;
import java.util.UUID;
import kz.uco.tsadv.modules.recruitment.enums.InterviewStatus;

@MetaClass(name = "tsadv$InterviewQuestionnaireInt")
public class InterviewQuestionnaireInt extends AbstractEntityInt {
    private static final long serialVersionUID = -5104739396709513823L;

    @MetaProperty
    protected List<InterviewQuestionInt> questions;

    @MetaProperty
    protected UUID questionnaireId;

    @MetaProperty
    protected UUID requisition;

    @MetaProperty
    protected String interviewStatus;

    public String getInterviewStatus() {
        return interviewStatus;
    }

    public void setInterviewStatus(String interviewStatus) {
        this.interviewStatus = interviewStatus;
    }





    public UUID getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(UUID questionnaireId) {
        this.questionnaireId = questionnaireId;
    }



    public void setRequisition(UUID requisition) {
        this.requisition = requisition;
    }

    public UUID getRequisition() {
        return requisition;
    }


    public void setQuestions(List<InterviewQuestionInt> questions) {
        this.questions = questions;
    }

    public List<InterviewQuestionInt> getQuestions() {
        return questions;
    }


}