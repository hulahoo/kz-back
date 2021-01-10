package kz.uco.tsadv.modules.personal.model;

import com.haulmont.cuba.core.entity.FileDescriptor;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicRetirementType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import java.util.Date;

@Table(name = "TSADV_RETIREMENT_REQUEST")
@Entity(name = "tsadv_RetirementRequest")
public class RetirementRequest extends AbstractParentEntity {
    private static final long serialVersionUID = 490985702335959565L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RETIREMENT_TYPE_ID")
    protected DicRetirementType retirementType;

    @Temporal(TemporalType.DATE)
    @Column(name = "ISSEU_DOC_DATE")
    private Date isseuDocDate;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RETIREMENT_ID")
    private Retirement retirement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    private FileDescriptor file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_STATUS_ID")
    private DicRequestStatus requestStatus;

    public FileDescriptor getFile() {
        return file;
    }

    public void setFile(FileDescriptor file) {
        this.file = file;
    }

    public Retirement getRetirement() {
        return retirement;
    }

    public void setRetirement(Retirement retirement) {
        this.retirement = retirement;
    }

    public DicRequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(DicRequestStatus requestStatus) {
        this.requestStatus = requestStatus;
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