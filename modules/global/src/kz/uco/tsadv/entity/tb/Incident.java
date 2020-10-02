package kz.uco.tsadv.entity.tb;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.entity.tb.dictionary.IncidentType;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

import java.util.List;
import javax.persistence.OneToMany;

@Table(name = "TSADV_INCIDENT")
@Entity(name = "tsadv$Incident")
public class Incident extends AbstractParentEntity {
    private static final long serialVersionUID = 3452577214578599211L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MANAGER_PERSON_ID")
    protected PersonExt managerPerson;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "incident")
    protected List<IncidentWitnesses> witnesses;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "incident")
    protected List<Attachment> attachment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ORGANIZATION_ID")
    protected OrganizationGroupExt organization;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "INCIDENT_TYPE_ID")
    protected IncidentType incidentType;

    @Temporal(TemporalType.DATE)
    @Column(name = "INCIDENT_DATE", nullable = false)
    protected Date incidentDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "INCIDENT_TIME")
    protected Date incidentTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "START_SHIFT")
    protected Date startShift;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_SHIFT")
    protected Date endShift;

    @Column(name = "INCIDENT_CONDITIONS", nullable = false)
    protected String incidentConditions;

    @Column(name = "INCIDENT_REASON")
    protected String incidentReason;

    @Column(name = "DAMAGE")
    protected String damage;

    @Column(name = "CORRECTIVE_ACTIONS")
    protected String correctiveActions;

    @Column(name = "STOPPED_PROCESS")
    protected Boolean stoppedProcess;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WORK_PLACE_ID")
    protected WorkPlace workPlace;

    public void setWitnesses(List<IncidentWitnesses> witnesses) {
        this.witnesses = witnesses;
    }

    public List<IncidentWitnesses> getWitnesses() {
        return witnesses;
    }

    public void setAttachment(List<Attachment> attachment) {
        this.attachment = attachment;
    }

    public List<Attachment> getAttachment() {
        return attachment;
    }


    public WorkPlace getWorkPlace() {
        return workPlace;
    }

    public void setWorkPlace(WorkPlace workPlace) {
        this.workPlace = workPlace;
    }






    public OrganizationGroupExt getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationGroupExt organization) {
        this.organization = organization;
    }


    public void setManagerPerson(PersonExt managerPerson) {
        this.managerPerson = managerPerson;
    }

    public PersonExt getManagerPerson() {
        return managerPerson;
    }



    public void setIncidentType(IncidentType incidentType) {
        this.incidentType = incidentType;
    }

    public IncidentType getIncidentType() {
        return incidentType;
    }


    public void setIncidentDate(Date incidentDate) {
        this.incidentDate = incidentDate;
    }

    public Date getIncidentDate() {
        return incidentDate;
    }

    public void setIncidentTime(Date incidentTime) {
        this.incidentTime = incidentTime;
    }

    public Date getIncidentTime() {
        return incidentTime;
    }

    public void setStartShift(Date startShift) {
        this.startShift = startShift;
    }

    public Date getStartShift() {
        return startShift;
    }

    public void setEndShift(Date endShift) {
        this.endShift = endShift;
    }

    public Date getEndShift() {
        return endShift;
    }

    public void setIncidentConditions(String incidentConditions) {
        this.incidentConditions = incidentConditions;
    }

    public String getIncidentConditions() {
        return incidentConditions;
    }

    public void setIncidentReason(String incidentReason) {
        this.incidentReason = incidentReason;
    }

    public String getIncidentReason() {
        return incidentReason;
    }

    public void setDamage(String damage) {
        this.damage = damage;
    }

    public String getDamage() {
        return damage;
    }

    public void setCorrectiveActions(String correctiveActions) {
        this.correctiveActions = correctiveActions;
    }

    public String getCorrectiveActions() {
        return correctiveActions;
    }

    public void setStoppedProcess(Boolean stoppedProcess) {
        this.stoppedProcess = stoppedProcess;
    }

    public Boolean getStoppedProcess() {
        return stoppedProcess;
    }


}