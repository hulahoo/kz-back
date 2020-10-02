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
import kz.uco.tsadv.entity.tb.dictionary.DirectReason;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.entity.tb.dictionary.AccidentType;
import kz.uco.tsadv.entity.tb.dictionary.Excavation;
import kz.uco.tsadv.entity.tb.dictionary.InvestigationConducted;

import javax.validation.constraints.NotNull;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;

@Table(name = "TSADV_ACCIDENTS")
@Entity(name = "tsadv$Accidents")
public class Accidents extends AbstractParentEntity {
    private static final long serialVersionUID = -4739356069873408241L;

    @Temporal(TemporalType.TIME)
    @Column(name = "ACCIDENT_TIME", nullable = false)
    protected Date accidentTime;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "accidents")
    protected List<AccidentEvent> event;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "accidents")
    protected List<AccidenInjured> injured;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "accidents")
    protected List<Witnesses> witnesses;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "accidents")
    protected List<Attachment> attachment;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ORGANIZATION_ID")
    protected OrganizationGroupExt organization;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "accidents")
    protected List<Punishment> punishment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "INVESTIGATION_CONDUCTED_ID")
    protected InvestigationConducted investigationConducted;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DIRECT_REASON_ID")
    protected DirectReason directReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EXCAVATION_ID")
    protected Excavation excavation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ACCIDENT_TYPE_ID")
    protected AccidentType accidentType;

    @Column(name = "ACCIDENT_ACT_NUMBER")
    protected String accidentActNumber;

    @Temporal(TemporalType.DATE)
    @Column(name = "ACCIDENT_ACT_DATE")
    protected Date accidentActDate;

    @Column(name = "ACCIDENT_CONDITIONS", nullable = false)
    protected String accidentConditions;

    @Temporal(TemporalType.DATE)
    @Column(name = "ACCIDENT_DATE", nullable = false)
    protected Date accidentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WORK_PLACE_ID")
    protected WorkPlace workPlace;
    public void setEvent(List<AccidentEvent> event) {
        this.event = event;
    }

    public List<AccidentEvent> getEvent() {
        return event;
    }


    public void setInjured(List<AccidenInjured> injured) {
        this.injured = injured;
    }

    public List<AccidenInjured> getInjured() {
        return injured;
    }


    public void setWitnesses(List<Witnesses> witnesses) {
        this.witnesses = witnesses;
    }

    public List<Witnesses> getWitnesses() {
        return witnesses;
    }


    public void setAttachment(List<Attachment> attachment) {
        this.attachment = attachment;
    }

    public List<Attachment> getAttachment() {
        return attachment;
    }

    public void setPunishment(List<Punishment> punishment) {
        this.punishment = punishment;
    }

    public List<Punishment> getPunishment() {
        return punishment;
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







    public InvestigationConducted getInvestigationConducted() {
        return investigationConducted;
    }

    public void setInvestigationConducted(InvestigationConducted investigationConducted) {
        this.investigationConducted = investigationConducted;
    }


    public Excavation getExcavation() {
        return excavation;
    }

    public void setExcavation(Excavation excavation) {
        this.excavation = excavation;
    }


    public AccidentType getAccidentType() {
        return accidentType;
    }

    public void setAccidentType(AccidentType accidentType) {
        this.accidentType = accidentType;
    }





    public DirectReason getDirectReason() {
        return directReason;
    }

    public void setDirectReason(DirectReason directReason) {
        this.directReason = directReason;
    }






    public void setAccidentActNumber(String accidentActNumber) {
        this.accidentActNumber = accidentActNumber;
    }

    public String getAccidentActNumber() {
        return accidentActNumber;
    }

    public void setAccidentActDate(Date accidentActDate) {
        this.accidentActDate = accidentActDate;
    }

    public Date getAccidentActDate() {
        return accidentActDate;
    }

    public void setAccidentConditions(String accidentConditions) {
        this.accidentConditions = accidentConditions;
    }

    public String getAccidentConditions() {
        return accidentConditions;
    }



    public void setAccidentTime(Date accidentTime) {
        this.accidentTime = accidentTime;
    }

    public Date getAccidentTime() {
        return accidentTime;
    }

    public void setAccidentDate(Date accidentDate) {
        this.accidentDate = accidentDate;
    }

    public Date getAccidentDate() {
        return accidentDate;
    }


}