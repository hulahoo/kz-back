package kz.uco.tsadv.modules.timesheet.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.PersistenceHelper;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NamePattern("%s|id")
@Table(name = "TSADV_SCHEDULE_HEADER")
@Entity(name = "tsadv$ScheduleHeader")
public class ScheduleHeader extends AbstractParentEntity {
    private static final long serialVersionUID = -8745580573054224420L;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SCHEDULE_ID")
    protected kz.uco.tsadv.modules.timesheet.model.StandardSchedule schedule;

    @Temporal(TemporalType.DATE)
    @Column(name = "MONTH_")
    protected Date month;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OFFSET_ID")
    protected StandardOffset offset;

    @Column(name = "IS_LOCKED", nullable = false)
    protected Boolean isLocked = false;

    @Column(name = "BASE_DAYS")
    protected Integer baseDays;

    @Column(name = "BASE_HOURS")
    protected Double baseHours;

    @Column(name = "PLAN_DAYS")
    protected Integer planDays;

    @Column(name = "PLAN_HOURS")
    protected Double planHours;

    @Column(name = "PLAN_HOURS_PART")
    protected Double planHoursPart;

    @Column(name = "PLAN_HOURS_MONTH")
    protected Double planHoursMonth;

    @Column(name = "NIGHT_HOURS")
    protected Double nightHours;

    @Column(name = "WEEKEND_DAYS")
    protected Integer weekendDays;

    @Column(name = "HOLIDAY_DAYS")
    protected Integer holidayDays;

    @Column(name = "HOLIDAY_WORK_DAYS")
    protected Integer holidayWorkDays;

    @Column(name = "HOLIDAY_WORK_HOURS")
    protected Double holidayWorkHours;

    @OrderBy("day")
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "header")
    protected List<ScheduleSummary> summaries;

    @Transient
    protected Map<Integer, ScheduleSummary> summariesMap;

    public void setHolidayDays(Integer holidayDays) {
        this.holidayDays = holidayDays;
    }

    public Integer getHolidayDays() {
        return holidayDays;
    }


    public Integer getBaseDays() {
        return baseDays;
    }

    public void setBaseDays(Integer baseDays) {
        this.baseDays = baseDays;
    }


    public Integer getPlanDays() {
        return planDays;
    }

    public void setPlanDays(Integer planDays) {
        this.planDays = planDays;
    }


    public Integer getWeekendDays() {
        return weekendDays;
    }

    public void setWeekendDays(Integer weekendDays) {
        this.weekendDays = weekendDays;
    }


    public Integer getHolidayWorkDays() {
        return holidayWorkDays;
    }

    public void setHolidayWorkDays(Integer holidayWorkDays) {
        this.holidayWorkDays = holidayWorkDays;
    }


    public void setSummaries(List<ScheduleSummary> summaries) {
        this.summaries = summaries;
    }

    public List<ScheduleSummary> getSummaries() {
        return summaries;
    }


    public void setIsLocked(Boolean isLocked) {
        this.isLocked = isLocked;
    }

    public Boolean getIsLocked() {
        return isLocked;
    }

    public void setBaseHours(Double baseHours) {
        this.baseHours = baseHours;
    }

    public Double getBaseHours() {
        return baseHours;
    }

    public void setPlanHours(Double planHours) {
        this.planHours = planHours;
    }

    public Double getPlanHours() {
        return planHours;
    }

    public void setPlanHoursPart(Double planHoursPart) {
        this.planHoursPart = planHoursPart;
    }

    public Double getPlanHoursPart() {
        return planHoursPart;
    }

    public void setPlanHoursMonth(Double planHoursMonth) {
        this.planHoursMonth = planHoursMonth;
    }

    public Double getPlanHoursMonth() {
        return planHoursMonth;
    }

    public void setNightHours(Double nightHours) {
        this.nightHours = nightHours;
    }

    public Double getNightHours() {
        return nightHours;
    }

    public void setHolidayWorkHours(Double holidayWorkHours) {
        this.holidayWorkHours = holidayWorkHours;
    }

    public Double getHolidayWorkHours() {
        return holidayWorkHours;
    }


    public void setSchedule(kz.uco.tsadv.modules.timesheet.model.StandardSchedule schedule) {
        this.schedule = schedule;
    }

    public StandardSchedule getSchedule() {
        return schedule;
    }

    public void setMonth(Date month) {
        this.month = month;
    }

    public Date getMonth() {
        return month;
    }

    public void setOffset(StandardOffset offset) {
        this.offset = offset;
    }

    public StandardOffset getOffset() {
        return offset;
    }

    public Map<Integer, ScheduleSummary> getSummariesMap() {
        if (this.summariesMap == null) {
            if (this.summaries != null && PersistenceHelper.isLoaded(this, "summaries")) {
                this.summariesMap = this.summaries.stream().collect(Collectors.toMap(s -> s.getDay(), s -> s));
            }
        }
        return this.summariesMap;
    }

    public void setSummariesMap(Map<Integer, ScheduleSummary> summariesMap) {
        this.summariesMap = summariesMap;
    }
}