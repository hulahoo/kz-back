package kz.uco.tsadv.modules.administration;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

@Table(name = "TSADV_LOOKUP_VALUE")
@Entity(name = "tsadv$LookupValue")
public class LookupValue extends AbstractParentEntity {
    private static final long serialVersionUID = -8178679506455056313L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "LOOKUP_TYPE_ID")
    protected LookupType lookupType;

    @NotNull
    @Column(name = "LOOKUP_TYPE_CODE", nullable = false)
    protected String lookupTypeCode;

    @NotNull
    @Column(name = "MEANING_LANG1", nullable = false)
    protected String meaningLang1;

    @Column(name = "MEANING_LANG2")
    protected String meaningLang2;

    @Column(name = "MEANING_LANG3")
    protected String meaningLang3;

    @Column(name = "DESCRIPTION_LANG1")
    protected String descriptionLang1;

    @Column(name = "DESCRIPTION_LANG2")
    protected String descriptionLang2;

    @Column(name = "DESCRIPTION_LANG3")
    protected String descriptionLang3;

    @Column(name = "ENABLED_FLAG")
    protected Boolean enabledFlag;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE")
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE")
    protected Date endDate;

    @Column(name = "TAG")
    protected String tag;

    public void setLookupType(LookupType lookupType) {
        this.lookupType = lookupType;
    }

    public LookupType getLookupType() {
        return lookupType;
    }

    public void setLookupTypeCode(String lookupTypeCode) {
        this.lookupTypeCode = lookupTypeCode;
    }

    public String getLookupTypeCode() {
        return lookupTypeCode;
    }

    public void setMeaningLang1(String meaningLang1) {
        this.meaningLang1 = meaningLang1;
    }

    public String getMeaningLang1() {
        return meaningLang1;
    }

    public void setMeaningLang2(String meaningLang2) {
        this.meaningLang2 = meaningLang2;
    }

    public String getMeaningLang2() {
        return meaningLang2;
    }

    public void setMeaningLang3(String meaningLang3) {
        this.meaningLang3 = meaningLang3;
    }

    public String getMeaningLang3() {
        return meaningLang3;
    }

    public void setDescriptionLang1(String descriptionLang1) {
        this.descriptionLang1 = descriptionLang1;
    }

    public String getDescriptionLang1() {
        return descriptionLang1;
    }

    public void setDescriptionLang2(String descriptionLang2) {
        this.descriptionLang2 = descriptionLang2;
    }

    public String getDescriptionLang2() {
        return descriptionLang2;
    }

    public void setDescriptionLang3(String descriptionLang3) {
        this.descriptionLang3 = descriptionLang3;
    }

    public String getDescriptionLang3() {
        return descriptionLang3;
    }

    public void setEnabledFlag(Boolean enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public Boolean getEnabledFlag() {
        return enabledFlag;
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

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }


}