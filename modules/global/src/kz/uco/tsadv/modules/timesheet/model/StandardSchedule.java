package kz.uco.tsadv.modules.timesheet.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.timesheet.enums.ScheduleTypeEnum;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import com.haulmont.cuba.core.entity.annotation.Listeners;

@Listeners("tsadv_StandardScheduleListener")
@NamePattern("%s|id,legacyId")
@Table(name = "TSADV_STANDARD_SCHEDULE")
@Entity(name = "tsadv$StandardSchedule")
public class StandardSchedule extends AbstractParentEntity {
    private static final long serialVersionUID = 2046791105932639559L;

    @Column(name = "SCHEDULE_NAME", nullable = false)
    protected String scheduleName;

    @Column(name = "DESCRIPTION", length = 500)
    protected String description;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE", nullable = false)
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE")
    protected Date endDate;

    @NotNull
    @Column(name = "PERIOD", nullable = false)
    protected Integer period;


    @NotNull
    @Column(name = "SCHEDULE_TYPE", nullable = false)
    protected String scheduleType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BASE_STANDARD_SCHEDULE_ID")
    protected StandardSchedule baseStandardSchedule;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CALENDAR_ID")
    protected Calendar calendar;

    @Column(name = "IS_HOLIDAY_WORK_DAY", nullable = false)
    protected Boolean isHolidayWorkDay = false;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "standardSchedule")
    protected List<StandardOffset> standardOffsets;

    @OrderBy("numberInShift")
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "standardSchedule")
    protected List<StandardShift> standardShifts;
    public StandardSchedule getBaseStandardSchedule() {
        return baseStandardSchedule;
    }

    public void setBaseStandardSchedule(StandardSchedule baseStandardSchedule) {
        this.baseStandardSchedule = baseStandardSchedule;
    }




    public void setScheduleType(ScheduleTypeEnum scheduleType) {
        this.scheduleType = scheduleType == null ? null : scheduleType.getId();
    }

    public ScheduleTypeEnum getScheduleType() {
        return scheduleType == null ? null : ScheduleTypeEnum.fromId(scheduleType);
    }

    public void setIsHolidayWorkDay(Boolean isHolidayWorkDay) {
        this.isHolidayWorkDay = isHolidayWorkDay;
    }

    public Boolean getIsHolidayWorkDay() {
        return isHolidayWorkDay;
    }




    public void setStandardOffsets(List<StandardOffset> standardOffsets) {
        this.standardOffsets = standardOffsets;
    }

    public List<StandardOffset> getStandardOffsets() {
        return standardOffsets;
    }


    public void setStandardShifts(List<kz.uco.tsadv.modules.timesheet.model.StandardShift> standardShifts) {
        this.standardShifts = standardShifts;
    }

    public List<StandardShift> getStandardShifts() {
        return standardShifts;
    }


    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public Calendar getCalendar() {
        return calendar;
    }


    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    public String getScheduleName() {
        return scheduleName;
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

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public Integer getPeriod() {
        return period;
    }


}