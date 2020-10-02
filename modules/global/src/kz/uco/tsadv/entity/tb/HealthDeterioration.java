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
import kz.uco.tsadv.entity.tb.dictionary.Result;

import javax.validation.constraints.NotNull;

import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

import java.util.List;
import javax.persistence.OneToMany;

@Table(name = "TSADV_HEALTH_DETERIORATION")
@Entity(name = "tsadv$HealthDeterioration")
public class HealthDeterioration extends AbstractParentEntity {
    private static final long serialVersionUID = 284835014726679181L;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_", nullable = false)
    protected Date date;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "healthDeterioration")
    protected List<Complaint> complaint;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "healthDeterioration")
    protected List<HealthDeteriorationWitnesses> witnesses;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "healthDeterioration")
    protected List<Attachment> attachment;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SICK_PERSON_ID")
    protected PersonExt sickPerson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MANAGER_PERSON_ID")
    protected PersonExt managerPerson;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ORGANIZATION_ID")
    protected OrganizationGroupExt organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESULT_ID")
    protected Result result;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TIME_")
    protected Date time;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "START_SHIFT")
    protected Date startShift;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_SHIFT")
    protected Date endShift;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WORK_PLACE_ID")
    protected WorkPlace workPlace;

    @Column(name = "PRELIMINARY_DIAGNOSIS")
    protected String preliminaryDiagnosis;

    @Column(name = "DESCRIPTION")
    protected String description;

    @Column(name = "REASON")
    protected String reason;

    @Temporal(TemporalType.DATE)
    @Column(name = "STICKNESS_START_DATE")
    protected Date sticknessStartDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "STICKNESS_END_DATE")
    protected Date sticknessEndDate;

    @Column(name = "FINAL_DIAGNOSIS")
    protected String finalDiagnosis;

    @Temporal(TemporalType.DATE)
    @Column(name = "RESULT_DATE")
    protected Date resultDate;

    public OrganizationGroupExt getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationGroupExt organization) {
        this.organization = organization;
    }

    public void setComplaint(List<Complaint> complaint) {
        this.complaint = complaint;
    }

    public List<Complaint> getComplaint() {
        return complaint;
    }

    public void setWitnesses(List<HealthDeteriorationWitnesses> witnesses) {
        this.witnesses = witnesses;
    }

    public List<HealthDeteriorationWitnesses> getWitnesses() {
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






    public void setSickPerson(PersonExt sickPerson) {
        this.sickPerson = sickPerson;
    }

    public PersonExt getSickPerson() {
        return sickPerson;
    }

    public void setManagerPerson(PersonExt managerPerson) {
        this.managerPerson = managerPerson;
    }

    public PersonExt getManagerPerson() {
        return managerPerson;
    }





    public void setResult(Result result) {
        this.result = result;
    }

    public Result getResult() {
        return result;
    }


    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Date getTime() {
        return time;
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

    public void setPreliminaryDiagnosis(String preliminaryDiagnosis) {
        this.preliminaryDiagnosis = preliminaryDiagnosis;
    }

    public String getPreliminaryDiagnosis() {
        return preliminaryDiagnosis;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setSticknessStartDate(Date sticknessStartDate) {
        this.sticknessStartDate = sticknessStartDate;
    }

    public Date getSticknessStartDate() {
        return sticknessStartDate;
    }

    public void setSticknessEndDate(Date sticknessEndDate) {
        this.sticknessEndDate = sticknessEndDate;
    }

    public Date getSticknessEndDate() {
        return sticknessEndDate;
    }

    public void setFinalDiagnosis(String finalDiagnosis) {
        this.finalDiagnosis = finalDiagnosis;
    }

    public String getFinalDiagnosis() {
        return finalDiagnosis;
    }

    public void setResultDate(Date resultDate) {
        this.resultDate = resultDate;
    }

    public Date getResultDate() {
        return resultDate;
    }


}