package kz.uco.tsadv.modules.timesheet.model;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import com.haulmont.cuba.core.entity.annotation.Listeners;

@Listeners("tsadv_StandardOffsetListener")
@NamePattern("%s|id")
@Table(name = "TSADV_STANDARD_OFFSET")
@Entity(name = "tsadv$StandardOffset")
public class StandardOffset extends AbstractParentEntity {
    private static final long serialVersionUID = -4329989150329969870L;

    @NotNull
    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "STANDARD_SCHEDULE_ID")
    protected StandardSchedule standardSchedule;

    @NotNull
    @Column(name = "OFFSET_DISPLAY", nullable = false)
    protected String offsetDisplay;


    @NotNull
    @Column(name = "OFFSET_DISPLAY_DAYS", nullable = false)
    protected Integer offsetDisplayDays;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "START_DATE", nullable = false)
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE")
    protected Date endDate;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "offset")
    protected kz.uco.tsadv.modules.timesheet.model.OrgAnalytics orgAnalytics;

    public void setOrgAnalytics(kz.uco.tsadv.modules.timesheet.model.OrgAnalytics orgAnalytics) {
        this.orgAnalytics = orgAnalytics;
    }

    public OrgAnalytics getOrgAnalytics() {
        return orgAnalytics;
    }


    public void setOffsetDisplayDays(Integer offsetDisplayDays) {
        this.offsetDisplayDays = offsetDisplayDays;
    }

    public Integer getOffsetDisplayDays() {
        return offsetDisplayDays;
    }


    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }


    public void setStandardSchedule(StandardSchedule standardSchedule) {
        this.standardSchedule = standardSchedule;
    }

    public StandardSchedule getStandardSchedule() {
        return standardSchedule;
    }


    public void setOffsetDisplay(String offsetDisplay) {
        this.offsetDisplay = offsetDisplay;
    }

    public String getOffsetDisplay() {
        return offsetDisplay;
    }

    @MetaProperty
    public String getOffsetScheduleName() {
        return getStandardSchedule().getScheduleName().concat(" - ").concat(getOffsetDisplay());
    }


}