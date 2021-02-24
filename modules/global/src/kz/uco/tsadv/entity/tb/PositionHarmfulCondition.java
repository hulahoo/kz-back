package kz.uco.tsadv.entity.tb;

import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.base.entity.shared.PositionGroup;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Table(name = "TSADV_POSITION_HARMFUL_CONDITION")
@Entity(name = "tsadv_PositionHarmfulCondition")
public class PositionHarmfulCondition extends StandardEntity {
    private static final long serialVersionUID = -4271285474033997233L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "POSITION_GROUP_ID")
    protected PositionGroupExt positionGroup;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "END_DATE", nullable = false)
    protected Date endDate;

    @NotNull
    @Column(name = "DAYS", nullable = false)
    protected Integer days;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "START_DATE", nullable = false)
    protected Date startDate;

    public void setPositionGroup(PositionGroupExt positionGroup) {
        this.positionGroup = positionGroup;
    }

    public PositionGroupExt getPositionGroup() {
        return positionGroup;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

}