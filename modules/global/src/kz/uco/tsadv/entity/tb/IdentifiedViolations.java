package kz.uco.tsadv.entity.tb;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.entity.tb.dictionary.DisconformityType;
import kz.uco.tsadv.entity.tb.dictionary.RisksViolations;
import kz.uco.tsadv.entity.tb.dictionary.Subsections;

@Table(name = "TSADV_IDENTIFIED_VIOLATIONS")
@Entity(name = "tsadv$IdentifiedViolations")
public class IdentifiedViolations extends AbstractParentEntity {
    private static final long serialVersionUID = -6774534562980273684L;

    @Column(name = "ENTITY_TYPE", nullable = false)
    protected String entityType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RISKS_VIOLATIONS_ID")
    protected RisksViolations risksViolations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUBSECTIONS_ID")
    protected Subsections subsections;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TYPE_ID")
    protected DisconformityType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESPONSIBLE_ID")
    protected PersonExt responsible;

    @Lob
    @Column(name = "DESCRIPTION")
    protected String description;

    @Temporal(TemporalType.DATE)
    @Column(name = "VIOLATION_DATE")
    protected Date violationDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "ELIMINATION_PLAN_DATE")
    protected Date eliminationPlanDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "ELIMINATION_FACT_DATE")
    protected Date eliminationFactDate;

    @Column(name = "IDENTIFIE_BY_WORKERS")
    protected Boolean identifieByWorkers;

    public void setRisksViolations(RisksViolations risksViolations) {
        this.risksViolations = risksViolations;
    }

    public RisksViolations getRisksViolations() {
        return risksViolations;
    }

    public void setSubsections(Subsections subsections) {
        this.subsections = subsections;
    }

    public Subsections getSubsections() {
        return subsections;
    }


    public void setType(DisconformityType type) {
        this.type = type;
    }

    public DisconformityType getType() {
        return type;
    }


    public void setResponsible(PersonExt responsible) {
        this.responsible = responsible;
    }

    public PersonExt getResponsible() {
        return responsible;
    }


    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setViolationDate(Date violationDate) {
        this.violationDate = violationDate;
    }

    public Date getViolationDate() {
        return violationDate;
    }

    public void setEliminationPlanDate(Date eliminationPlanDate) {
        this.eliminationPlanDate = eliminationPlanDate;
    }

    public Date getEliminationPlanDate() {
        return eliminationPlanDate;
    }

    public void setEliminationFactDate(Date eliminationFactDate) {
        this.eliminationFactDate = eliminationFactDate;
    }

    public Date getEliminationFactDate() {
        return eliminationFactDate;
    }

    public void setIdentifieByWorkers(Boolean identifieByWorkers) {
        this.identifieByWorkers = identifieByWorkers;
    }

    public Boolean getIdentifieByWorkers() {
        return identifieByWorkers;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


}