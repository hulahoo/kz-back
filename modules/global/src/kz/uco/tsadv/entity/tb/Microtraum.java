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
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

import java.util.List;
import javax.persistence.OneToMany;

@Table(name = "TSADV_MICROTRAUM")
@Entity(name = "tsadv$Microtraum")
public class Microtraum extends AbstractParentEntity {
    private static final long serialVersionUID = 5837164118011189017L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MANAGER_PERSON_ID")
    protected PersonExt managerPerson;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "microtraum")
    protected List<MicrotraumaInjured> injured;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "microtraum")
    protected List<Attachment> attachment;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "microtraum")
    protected List<MicrotraumaWitnesses> witnesses;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPORTED_PERSON_ID")
    protected PersonExt reportedPerson;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ORGANIZATION_ID")
    protected OrganizationGroupExt organization;

    @Temporal(TemporalType.DATE)
    @Column(name = "MICROTRAUMA_DATE", nullable = false)
    protected Date microtraumaDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "MICROTRAUMA_TIME")
    protected Date microtraumaTime;

    @Column(name = "MICROTRAUMA_REASON", nullable = false)
    protected String microtraumaReason;

    @Column(name = "MICRATRAUMA_CONDITIONS")
    protected String micratraumaConditions;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "SHIFT_START")
    protected Date shiftStart;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "SHIFT_END")
    protected Date shiftEnd;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WORK_PLACE_ID")
    protected WorkPlace workPlace;

    public void setInjured(List<MicrotraumaInjured> injured) {
        this.injured = injured;
    }

    public List<MicrotraumaInjured> getInjured() {
        return injured;
    }


    public void setAttachment(List<Attachment> attachment) {
        this.attachment = attachment;
    }

    public List<Attachment> getAttachment() {
        return attachment;
    }


    public void setWitnesses(List<MicrotraumaWitnesses> witnesses) {
        this.witnesses = witnesses;
    }

    public List<MicrotraumaWitnesses> getWitnesses() {
        return witnesses;
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

    public void setReportedPerson(PersonExt reportedPerson) {
        this.reportedPerson = reportedPerson;
    }

    public PersonExt getReportedPerson() {
        return reportedPerson;
    }



    public void setMicrotraumaDate(Date microtraumaDate) {
        this.microtraumaDate = microtraumaDate;
    }

    public Date getMicrotraumaDate() {
        return microtraumaDate;
    }

    public void setMicrotraumaTime(Date microtraumaTime) {
        this.microtraumaTime = microtraumaTime;
    }

    public Date getMicrotraumaTime() {
        return microtraumaTime;
    }

    public void setMicrotraumaReason(String microtraumaReason) {
        this.microtraumaReason = microtraumaReason;
    }

    public String getMicrotraumaReason() {
        return microtraumaReason;
    }

    public void setMicratraumaConditions(String micratraumaConditions) {
        this.micratraumaConditions = micratraumaConditions;
    }

    public String getMicratraumaConditions() {
        return micratraumaConditions;
    }

    public void setShiftStart(Date shiftStart) {
        this.shiftStart = shiftStart;
    }

    public Date getShiftStart() {
        return shiftStart;
    }

    public void setShiftEnd(Date shiftEnd) {
        this.shiftEnd = shiftEnd;
    }

    public Date getShiftEnd() {
        return shiftEnd;
    }


}