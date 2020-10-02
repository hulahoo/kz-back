package kz.uco.tsadv.modules.recruitment.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.modules.recruitment.model.*;
import kz.uco.tsadv.modules.recruitment.model.Requisition;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;

@NamePattern("%s|id")
@Table(name = "TSADV_REQUISITION_QUESTIONNAIRE")
@Entity(name = "tsadv$RequisitionQuestionnaire")
public class RequisitionQuestionnaire extends AbstractParentEntity {
    private static final long serialVersionUID = 4535278014283873045L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "REQUISITION_ID")
    protected kz.uco.tsadv.modules.recruitment.model.Requisition requisition;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "QUESTIONNAIRE_ID")
    protected RcQuestionnaire questionnaire;

    @Column(name = "WEIGHT")
    protected Double weight;

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getWeight() {
        return weight;
    }


    public void setRequisition(kz.uco.tsadv.modules.recruitment.model.Requisition requisition) {
        this.requisition = requisition;
    }

    public Requisition getRequisition() {
        return requisition;
    }

    public void setQuestionnaire(RcQuestionnaire questionnaire) {
        this.questionnaire = questionnaire;
    }

    public RcQuestionnaire getQuestionnaire() {
        return questionnaire;
    }


}