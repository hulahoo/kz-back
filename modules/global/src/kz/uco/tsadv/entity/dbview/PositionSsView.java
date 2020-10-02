package kz.uco.tsadv.entity.dbview;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DesignSupport;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.haulmont.cuba.core.global.UserSessionSource;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import com.haulmont.chile.core.annotations.MetaProperty;

import javax.persistence.Transient;

import kz.uco.tsadv.modules.personal.group.GradeGroup;

@DesignSupport("{'dbView':true,'generateDdl':false}")
@Table(name = "aa_position_vw")
@Entity(name = "tsadv$PositionSsView")
public class PositionSsView extends StandardEntity {
    private static final long serialVersionUID = -4990146275530408001L;

    @Column(name = "POSITION_NAME_RU")
    protected String positionNameRu;

    @Column(name = "POSITION_NAME_KZ")
    protected String positionNameKz;

    @Column(name = "POSITION_NAME_EN")
    protected String positionNameEn;

    @Transient
    @MetaProperty(related = {"positionNameRu", "positionNameKz", "positionNameEn"})
    protected String positionName;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE")
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE")
    protected Date endDate;

    @Column(name = "MAX_START_DATE")
    protected String maxStartDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POSITION_GROUP_ID")
    protected PositionGroupExt positionGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_GROUP_ID")
    protected OrganizationGroupExt organizationGroup;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GRADE_GROUP_ID")
    protected GradeGroup gradeGroup;

    @Column(name = "FTE")
    protected Double fte;

    @Column(name = "COST_CENTER")
    protected String costCenter;

    public void setGradeGroup(GradeGroup gradeGroup) {
        this.gradeGroup = gradeGroup;
    }

    public GradeGroup getGradeGroup() {
        return gradeGroup;
    }


    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getPositionName() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String positionOrder = com.haulmont.cuba.core.sys.AppContext.getProperty("base.abstractDictionary.langOrder");
        if (positionOrder != null) {
            List<String> langs = Arrays.asList(positionOrder.split(";"));
            switch (langs.indexOf(language)) {
                case 0:
                    return positionNameRu;
                case 1:
                    return positionNameKz;
                case 2:
                    return positionNameEn;
            }
        }
        return positionName;
    }


    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }

    public String getCostCenter() {
        return costCenter;
    }


    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }


    public void setPositionNameRu(String positionNameRu) {
        this.positionNameRu = positionNameRu;
    }

    public String getPositionNameRu() {
        return positionNameRu;
    }

    public void setPositionNameKz(String positionNameKz) {
        this.positionNameKz = positionNameKz;
    }

    public String getPositionNameKz() {
        return positionNameKz;
    }

    public void setPositionNameEn(String positionNameEn) {
        this.positionNameEn = positionNameEn;
    }

    public String getPositionNameEn() {
        return positionNameEn;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setMaxStartDate(String maxStartDate) {
        this.maxStartDate = maxStartDate;
    }

    public String getMaxStartDate() {
        return maxStartDate;
    }

    public void setPositionGroup(PositionGroupExt positionGroup) {
        this.positionGroup = positionGroup;
    }

    public PositionGroupExt getPositionGroup() {
        return positionGroup;
    }

    public void setOrganizationGroup(OrganizationGroupExt organizationGroup) {
        this.organizationGroup = organizationGroup;
    }

    public OrganizationGroupExt getOrganizationGroup() {
        return organizationGroup;
    }


    public void setFte(Double fte) {
        this.fte = fte;
    }

    public Double getFte() {
        return fte;
    }


}