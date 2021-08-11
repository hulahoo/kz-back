package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicIncentiveIndicators;
import kz.uco.tsadv.modules.personal.enums.OrganizationIncentiveIndicatorType;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Table(name = "TSADV_ORGANIZATION_INCENTIVE_INDICATORS")
@Entity(name = "tsadv_OrganizationIncentiveIndicators")
@NamePattern("%s|responsiblePosition")
public class OrganizationIncentiveIndicators extends StandardEntity {
    private static final long serialVersionUID = -5141736109346347548L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ORGANIZATION_GROUP_ID")
    protected OrganizationGroupExt organizationGroup;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "DATE_FROM", nullable = false)
    protected Date dateFrom;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "DATE_TO", nullable = false)
    protected Date dateTo;

    @NotNull
    @Column(name = "INDICATOR_TYPE", nullable = false)
    protected String indicatorType;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "INDICATOR_ID")
    protected DicIncentiveIndicators indicator;

    @NotNull
    @Column(name = "WEIGHT", nullable = false)
    protected Double weight;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RESPONSIBLE_POSITION_ID")
    @NotNull
    protected PositionGroupExt responsiblePosition;

    public void setResponsiblePosition(PositionGroupExt responsiblePosition) {
        this.responsiblePosition = responsiblePosition;
    }

    public PositionGroupExt getResponsiblePosition() {
        return responsiblePosition;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public DicIncentiveIndicators getIndicator() {
        return indicator;
    }

    public void setIndicator(DicIncentiveIndicators indicator) {
        this.indicator = indicator;
    }

    public OrganizationIncentiveIndicatorType getIndicatorType() {
        return indicatorType == null ? null : OrganizationIncentiveIndicatorType.fromId(indicatorType);
    }

    public void setIndicatorType(OrganizationIncentiveIndicatorType indicatorType) {
        this.indicatorType = indicatorType == null ? null : indicatorType.getId();
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public OrganizationGroupExt getOrganizationGroup() {
        return organizationGroup;
    }

    public void setOrganizationGroup(OrganizationGroupExt organizationGroup) {
        this.organizationGroup = organizationGroup;
    }
}