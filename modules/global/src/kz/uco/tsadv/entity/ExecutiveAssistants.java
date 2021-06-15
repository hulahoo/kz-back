package kz.uco.tsadv.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Table(name = "TSADV_EXECUTIVE_ASSISTANTS")
@Entity(name = "tsadv_ExecutiveAssistants")
@NamePattern("%s - %s|startDate,endDate,managerPositionGroup")
public class ExecutiveAssistants extends StandardEntity {
    private static final long serialVersionUID = -3149567331708196270L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MANAGER_POSITION_GROUP_ID")
    private PositionGroupExt managerPositionGroup;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ASSISTANCE_POSITION_GROUP_ID")
    private PositionGroupExt assistancePositionGroup;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "START_DATE", nullable = false)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "END_DATE", nullable = false)
    private Date endDate;

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

    public PositionGroupExt getAssistancePositionGroup() {
        return assistancePositionGroup;
    }

    public void setAssistancePositionGroup(PositionGroupExt assistancePositionGroup) {
        this.assistancePositionGroup = assistancePositionGroup;
    }

    public PositionGroupExt getManagerPositionGroup() {
        return managerPositionGroup;
    }

    public void setManagerPositionGroup(PositionGroupExt managerPositionGroup) {
        this.managerPositionGroup = managerPositionGroup;
    }

    @PostConstruct
    public void postConstruct() {
        this.startDate = CommonUtils.getSystemDate();
        this.endDate = CommonUtils.getEndOfTime();
    }
}