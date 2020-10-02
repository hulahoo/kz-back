package kz.uco.tsadv.modules.recruitment.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.modules.personal.group.CompetenceGroup;
import kz.uco.tsadv.modules.personal.model.ScaleLevel;
import kz.uco.tsadv.modules.recruitment.enums.CompetenceCriticalness;
import kz.uco.tsadv.modules.recruitment.model.*;
import kz.uco.tsadv.modules.recruitment.model.Requisition;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NamePattern("%s|id")
@Table(name = "TSADV_REQUISITION_COMPETENCE")
@Entity(name = "tsadv$RequisitionCompetence")
public class RequisitionCompetence extends AbstractParentEntity {
    private static final long serialVersionUID = 8915959719189337075L;

    @NotNull
    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "REQUISITION_ID")
    protected kz.uco.tsadv.modules.recruitment.model.Requisition requisition;

    @NotNull
    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COMPETENCE_GROUP_ID")
    protected CompetenceGroup competenceGroup;

    @NotNull
    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SCALE_LEVEL_ID")
    protected ScaleLevel scaleLevel;

    @Column(name = "CRITICALNESS", nullable = false)
    protected Integer criticalness;

    public void setCriticalness(CompetenceCriticalness criticalness) {
        this.criticalness = criticalness == null ? null : criticalness.getId();
    }

    public CompetenceCriticalness getCriticalness() {
        return criticalness == null ? null : CompetenceCriticalness.fromId(criticalness);
    }


    public void setRequisition(kz.uco.tsadv.modules.recruitment.model.Requisition requisition) {
        this.requisition = requisition;
    }

    public Requisition getRequisition() {
        return requisition;
    }

    public void setCompetenceGroup(CompetenceGroup competenceGroup) {
        this.competenceGroup = competenceGroup;
    }

    public CompetenceGroup getCompetenceGroup() {
        return competenceGroup;
    }

    public void setScaleLevel(ScaleLevel scaleLevel) {
        this.scaleLevel = scaleLevel;
    }

    public ScaleLevel getScaleLevel() {
        return scaleLevel;
    }


}