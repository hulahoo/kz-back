package kz.uco.tsadv.modules.personal.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.enums.YesNoEnum;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import java.util.Date;

@Table(name = "TSADV_PERSON_CLINIC_DISPANCER")
@Entity(name = "tsadv_PersonClinicDispancer")
public class PersonClinicDispancer extends AbstractParentEntity {
    private static final long serialVersionUID = -9117635314117167301L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    private PersonGroupExt personGroup;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE_HISTORY")
    private Date startDateHistory;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE_HISTORY")
    private Date endDateHistory;

    @Column(name = "HAVE_CLINIC_DISPANCER")
    private String haveClinicDispancer;

    @Column(name = "PERIOD_FROM", length = 2000)
    private String periodFrom;

    public String getPeriodFrom() {
        return periodFrom;
    }

    public void setPeriodFrom(String periodFrom) {
        this.periodFrom = periodFrom;
    }

    public YesNoEnum getHaveClinicDispancer() {
        return haveClinicDispancer == null ? null : YesNoEnum.fromId(haveClinicDispancer);
    }

    public void setHaveClinicDispancer(YesNoEnum haveClinicDispancer) {
        this.haveClinicDispancer = haveClinicDispancer == null ? null : haveClinicDispancer.getId();
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