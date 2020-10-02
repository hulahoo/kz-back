package kz.uco.tsadv.modules.recruitment.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.modules.recruitment.model.*;
import kz.uco.tsadv.modules.recruitment.model.HiringStep;
import kz.uco.tsadv.modules.recruitment.model.Requisition;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NamePattern("%s|id")
@Table(name = "TSADV_REQUISITION_HIRING_STEP")
@Entity(name = "tsadv$RequisitionHiringStep")
public class RequisitionHiringStep extends AbstractParentEntity {
    private static final long serialVersionUID = 8158974448057208133L;

    @NotNull
    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "REQUISITION_ID")
    protected kz.uco.tsadv.modules.recruitment.model.Requisition requisition;

    @Column(name = "REQUIRED", nullable = false)
    protected Boolean required = false;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "HIRING_STEP_ID")
    protected HiringStep hiringStep;

    @Column(name = "ORDER_", nullable = false)
    protected Integer order;

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Boolean getRequired() {
        return required;
    }


    public void setRequisition(kz.uco.tsadv.modules.recruitment.model.Requisition requisition) {
        this.requisition = requisition;
    }

    public Requisition getRequisition() {
        return requisition;
    }

    public void setHiringStep(HiringStep hiringStep) {
        this.hiringStep = hiringStep;
    }

    public HiringStep getHiringStep() {
        return hiringStep;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getOrder() {
        return order;
    }


}