package kz.uco.tsadv.entity.tb;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

import java.util.List;
import javax.persistence.OneToMany;

@NamePattern("%s|description")
@Table(name = "TSADV_SAFETY_PLAN_EVENT")
@Entity(name = "tsadv$SafetyPlanEvent")
public class SafetyPlanEvent extends StandardEntity {
    private static final long serialVersionUID = -3850635564513214192L;

    @Column(name = "PLAN_NAME", nullable = false)
    protected String planName;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "safetyPlanEvent")
    protected List<AssignedEvent> assignedEvent;

    @Lob
    @Column(name = "DESCRIPTION")
    protected String description;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_FROM", nullable = false)
    protected Date dateFrom;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_TO", nullable = false)
    protected Date dateTo;

    @Column(name = "ACTIVE", nullable = false)
    protected Boolean active = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_ID")
    protected OrganizationGroupExt organization;

    public void setAssignedEvent(List<AssignedEvent> assignedEvent) {
        this.assignedEvent = assignedEvent;
    }

    public List<AssignedEvent> getAssignedEvent() {
        return assignedEvent;
    }


    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getPlanName() {
        return planName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getActive() {
        return active;
    }

    public void setOrganization(OrganizationGroupExt organization) {
        this.organization = organization;
    }

    public OrganizationGroupExt getOrganization() {
        return organization;
    }


}