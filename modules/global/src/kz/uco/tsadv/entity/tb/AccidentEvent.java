package kz.uco.tsadv.entity.tb;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.FileDescriptor;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.model.PersonExt;

@Table(name = "TSADV_ACCIDENT_EVENT")
@Entity(name = "tsadv$AccidentEvent")
public class AccidentEvent extends AbstractParentEntity {
    private static final long serialVersionUID = 6703344080748167784L;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_ID")
    protected PersonExt person;

    @Column(name = "VIOLATIONS", nullable = false)
    protected String violations;

    @Column(name = "ACCIDENT_REASON_REMOVAL", nullable = false)
    protected String accidentReasonRemoval;

    @Temporal(TemporalType.DATE)
    @Column(name = "EXECUTION_PERIOD", nullable = false)
    protected Date executionPeriod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTACHMENT_ID")
    protected FileDescriptor attachment;




    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCIDENTS_ID")
    protected Accidents accidents;

    public void setAccidents(Accidents accidents) {
        this.accidents = accidents;
    }

    public Accidents getAccidents() {
        return accidents;
    }


    public void setPerson(PersonExt person) {
        this.person = person;
    }

    public PersonExt getPerson() {
        return person;
    }


    public void setViolations(String violations) {
        this.violations = violations;
    }

    public String getViolations() {
        return violations;
    }

    public void setAccidentReasonRemoval(String accidentReasonRemoval) {
        this.accidentReasonRemoval = accidentReasonRemoval;
    }

    public String getAccidentReasonRemoval() {
        return accidentReasonRemoval;
    }

    public void setExecutionPeriod(Date executionPeriod) {
        this.executionPeriod = executionPeriod;
    }

    public Date getExecutionPeriod() {
        return executionPeriod;
    }

    public void setAttachment(FileDescriptor attachment) {
        this.attachment = attachment;
    }

    public FileDescriptor getAttachment() {
        return attachment;
    }


}