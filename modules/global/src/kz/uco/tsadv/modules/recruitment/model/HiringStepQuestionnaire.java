package kz.uco.tsadv.modules.recruitment.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.modules.recruitment.model.*;
import kz.uco.tsadv.modules.recruitment.model.RcQuestionnaire;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;

@NamePattern("%s|id")
@Table(name = "TSADV_HIRING_STEP_QUESTIONNAIRE")
@Entity(name = "tsadv$HiringStepQuestionnaire")
public class HiringStepQuestionnaire extends AbstractParentEntity {
    private static final long serialVersionUID = -7599998659980141241L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "HIRING_STEP_ID")
    protected HiringStep hiringStep;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "QUESTIONNAIRE_ID")
    protected kz.uco.tsadv.modules.recruitment.model.RcQuestionnaire questionnaire;

    public void setHiringStep(HiringStep hiringStep) {
        this.hiringStep = hiringStep;
    }

    public HiringStep getHiringStep() {
        return hiringStep;
    }

    public void setQuestionnaire(kz.uco.tsadv.modules.recruitment.model.RcQuestionnaire questionnaire) {
        this.questionnaire = questionnaire;
    }

    public RcQuestionnaire getQuestionnaire() {
        return questionnaire;
    }


}