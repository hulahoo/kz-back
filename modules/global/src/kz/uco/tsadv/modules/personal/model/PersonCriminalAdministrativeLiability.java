package kz.uco.tsadv.modules.personal.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.enums.YesNoEnum;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import java.util.Date;

@Table(name = "TSADV_PERSON_CRIMINAL_ADMINISTRATIVE_LIABILITY")
@Entity(name = "tsadv_PersonCriminalAdministrativeLiability")
public class PersonCriminalAdministrativeLiability extends AbstractParentEntity {
    private static final long serialVersionUID = -8729334990072965109L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    private PersonGroupExt personGroup;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE_HISTORY")
    private Date startDateHistory;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE_HISTORY")
    private Date endDateHistory;

    @Column(name = "HAVE_LIABILITY")
    private String haveLiability;

    @Column(name = "REASON_PERIOD", length = 2000)
    private String reasonPeriod;

    public String getReasonPeriod() {
        return reasonPeriod;
    }

    public void setReasonPeriod(String reasonPeriod) {
        this.reasonPeriod = reasonPeriod;
    }

    public YesNoEnum getHaveLiability() {
        return haveLiability == null ? null : YesNoEnum.fromId(haveLiability);
    }

    public void setHaveLiability(YesNoEnum haveLiability) {
        this.haveLiability = haveLiability == null ? null : haveLiability.getId();
    }

    public Date getEndDateHistory() {
        return endDateHistory;
    }

    public void setEndDateHistory(Date endDateHistory) {
        this.endDateHistory = endDateHistory;
    }

    public Date getStartDateHistory() {
        return startDateHistory;
    }

    public void setStartDateHistory(Date startDateHistory) {
        this.startDateHistory = startDateHistory;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }
}