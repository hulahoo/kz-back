package kz.uco.tsadv.modules.personal.model;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import kz.uco.tsadv.entity.AbsenceRequestStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicCertificateType;
import kz.uco.tsadv.modules.personal.dictionary.DicLanguage;
import kz.uco.tsadv.modules.personal.dictionary.DicReceivingType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Table(name = "TSADV_CERTIFICATE_REQUEST")
@Entity(name = "tsadv_CertificateRequest")
public class CertificateRequest extends StandardEntity {
    private static final long serialVersionUID = -2710520566356850633L;

    @NotNull
    @Column(name = "REQUEST_NUMBER", nullable = false)
    private Long requestNumber;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "REQUEST_DATE", nullable = false)
    private Date requestDate;

    @Lookup(type = LookupType.SCREEN, actions = "lookup")
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    private PersonGroupExt personGroup;

    @Lookup(type = LookupType.SCREEN, actions = "lookup")
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CRETIFICATE_TYPE_ID")
    private DicCertificateType cretificateType;

    @Lookup(type = LookupType.SCREEN, actions = "lookup")
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RECEIVING_TYPE_ID")
    private DicReceivingType receivingType;

    @Lookup(type = LookupType.SCREEN, actions = "lookup")
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "LANGUAGE_ID")
    private DicLanguage language;

    @NotNull
    @Column(name = "SHOW_SALARY", nullable = false)
    private Boolean showSalary = false;

    @NotNull
    @Column(name = "NUMBER_OF_COPY", nullable = false)
    private Integer numberOfCopy;

    @Lookup(type = LookupType.SCREEN, actions = "lookup")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    private FileDescriptor file;

    @Lookup(type = LookupType.DROPDOWN, actions = "lookup")
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "STATUS_ID")
    private AbsenceRequestStatus status;

    public AbsenceRequestStatus getStatus() {
        return status;
    }

    public void setStatus(AbsenceRequestStatus status) {
        this.status = status;
    }

    public FileDescriptor getFile() {
        return file;
    }

    public void setFile(FileDescriptor file) {
        this.file = file;
    }

    public Integer getNumberOfCopy() {
        return numberOfCopy;
    }

    public void setNumberOfCopy(Integer numberOfCopy) {
        this.numberOfCopy = numberOfCopy;
    }

    public Boolean getShowSalary() {
        return showSalary;
    }

    public void setShowSalary(Boolean showSalary) {
        this.showSalary = showSalary;
    }

    public DicLanguage getLanguage() {
        return language;
    }

    public void setLanguage(DicLanguage language) {
        this.language = language;
    }

    public DicReceivingType getReceivingType() {
        return receivingType;
    }

    public void setReceivingType(DicReceivingType receivingType) {
        this.receivingType = receivingType;
    }

    public DicCertificateType getCretificateType() {
        return cretificateType;
    }

    public void setCretificateType(DicCertificateType cretificateType) {
        this.cretificateType = cretificateType;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Long getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(Long requestNumber) {
        this.requestNumber = requestNumber;
    }
}