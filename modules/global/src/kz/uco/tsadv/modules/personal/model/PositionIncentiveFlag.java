package kz.uco.tsadv.modules.personal.model;

import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Table(name = "TSADV_POSITION_INCENTIVE_FLAG")
@Entity(name = "tsadv_PositionIncentiveFlag")
public class PositionIncentiveFlag extends StandardEntity {
    private static final long serialVersionUID = 1701170390873204520L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "POSITION_GROUP_ID")
    private PositionGroupExt positionGroup;

    @Column(name = "LEGACY_ID")
    private String legacyId;

    @NotNull
    @Column(name = "IS_INCENTIVE", nullable = false)
    private Boolean isIncentive = true;

    @Column(name = "DATE_FROM")
    @Temporal(TemporalType.DATE)
    private Date dateFrom;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_TO")
    private Date dateTo;

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Boolean getIsIncentive() {
        return isIncentive;
    }

    public void setIsIncentive(Boolean isIncentive) {
        this.isIncentive = isIncentive;
    }

    public String getLegacyId() {
        return legacyId;
    }

    public void setLegacyId(String legacyId) {
        this.legacyId = legacyId;
    }

    public PositionGroupExt getPositionGroup() {
        return positionGroup;
    }

    public void setPositionGroup(PositionGroupExt positionGroup) {
        this.positionGroup = positionGroup;
    }
}