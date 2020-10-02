package kz.uco.tsadv.modules.recruitment.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|requirement")
@Table(name = "TSADV_REQUISITION_REQUIREMENT")
@Entity(name = "tsadv$RequisitionRequirement")
public class RequisitionRequirement extends StandardEntity {
    private static final long serialVersionUID = 8371393578195828577L;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "clear"})
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "REQUIREMENT_ID")
    protected RcQuestion requirement;

    @NotNull
    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "REQUISITION_ID")
    protected Requisition requisition;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "clear"})
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "REQUIREMENT_LEVEL_ID")
    protected RcAnswer requirementLevel;

    @NotNull
    @Column(name = "CRITICAL", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    protected Boolean critical = false;

    public void setRequirement(RcQuestion requirement) {
        this.requirement = requirement;
    }

    public RcQuestion getRequirement() {
        return requirement;
    }

    public void setRequisition(Requisition requisition) {
        this.requisition = requisition;
    }

    public Requisition getRequisition() {
        return requisition;
    }

    public void setRequirementLevel(RcAnswer requirementLevel) {
        this.requirementLevel = requirementLevel;
    }

    public RcAnswer getRequirementLevel() {
        return requirementLevel;
    }

    public void setCritical(Boolean critical) {
        this.critical = critical;
    }

    public Boolean getCritical() {
        return critical;
    }


}