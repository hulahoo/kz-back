package kz.uco.tsadv.modules.timesheet.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.modules.timesheet.model.*;
import kz.uco.tsadv.modules.timesheet.model.CalendarHoliday;
import kz.uco.tsadv.modules.timesheet.model.OrgAnalytics;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import com.haulmont.cuba.core.entity.annotation.Listeners;

@Listeners("tsadv_CalendarListener")
@NamePattern("%s|id,description")
@Table(name = "TSADV_CALENDAR")
@Entity(name = "tsadv$Calendar")
public class Calendar extends AbstractParentEntity {
    private static final long serialVersionUID = 7377875829885212345L;

    @Column(name = "CALENDAR", nullable = false)
    protected String calendar;

    @Column(name = "DESCRIPTION", length = 500)
    protected String description;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE", nullable = false)
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE", nullable = false)
    protected Date endDate;


    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "calendar")
    protected List<CalendarHoliday> calendarHolidays;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "calendar")
    protected kz.uco.tsadv.modules.timesheet.model.OrgAnalytics orgAnalytics;

    public void setOrgAnalytics(kz.uco.tsadv.modules.timesheet.model.OrgAnalytics orgAnalytics) {
        this.orgAnalytics = orgAnalytics;
    }

    public OrgAnalytics getOrgAnalytics() {
        return orgAnalytics;
    }


    public void setCalendarHolidays(List<CalendarHoliday> calendarHolidays) {
        this.calendarHolidays = calendarHolidays;
    }

    public List<CalendarHoliday> getCalendarHolidays() {
        return calendarHolidays;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
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


    public void setCalendar(String calendar) {
        this.calendar = calendar;
    }

    public String getCalendar() {
        return calendar;
    }


}