package kz.uco.tsadv.modules.personal.model;

import com.haulmont.cuba.core.entity.annotation.Listeners;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicRetirementType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import java.util.Date;

@Listeners("tsadv_RetirementListener")
@Table(name = "TSADV_RETIREMENT")
@Entity(name = "tsadv$Retirement")
public class Retirement extends AbstractParentEntity {
    private static final long serialVersionUID = -7106120359716838609L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RETIREMENT_TYPE_ID")
    protected DicRetirementType retirementType;

    @Temporal(TemporalType.DATE)
    @Column(name = "ISSEU_DOC_DATE")
    private Date isseuDocDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE_HISTORY")
    private Date startDateHistory;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE_HISTORY")
    private Date endDateHistory;

    @Column(name = "DOCUMENT_NUMBER")
    protected String documentNumber;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_FROM")
    protected Date dateFrom;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_TO")
    protected Date dateTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_EXT_ID")
    protected PersonGroupExt personGroupExt;

    public Date getEndDateHistory() {
        return endDateHistory;
    }

    public void setEndDateHistory(Date endDateHistory) {
        this.endDateHistory = endDateHistory;
    }

    public Date getStartDateHistory() {
        return startDateHistory;
    }

    public void setStartDateHistory(Date startDateHistory) {
        this.startDateHistory = startDateHistory;
    }

    public Date getIsseuDocDate() {
        return isseuDocDate;
    }

    public void setIsseuDocDate(Date isseuDocDate) {
        this.isseuDocDate = isseuDocDate;
    }

    public void setPersonGroupExt(PersonGroupExt personGroupExt) {
        this.personGroupExt = personGroupExt;
    }

    public PersonGroupExt getPersonGroupExt() {
        return personGroupExt;
    }

    public void setRetirementType(DicRetirementType retirementType) {
        this.retirementType = retirementType;
    }

    public DicRetirementType getRetirementType() {
        return retirementType;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Date getDateTo() {
        return dateTo;
    }
}