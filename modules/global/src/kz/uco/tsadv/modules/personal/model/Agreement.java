package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import kz.uco.tsadv.modules.personal.dictionary.DicAgreementStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicContractsType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.Listeners;

@Listeners("tsadv_AgreementEntityListener")
@NamePattern("%s|agreementNumber")
@Table(name = "TSADV_AGREEMENT")
@Entity(name = "tsadv$Agreement")
public class Agreement extends AbstractParentEntity {
    private static final long serialVersionUID = 8825260090757218682L;

    @Column(name = "AGREEMENT_NUMBER", nullable = false)
    protected String agreementNumber;

    @Temporal(TemporalType.DATE)
    @Column(name = "SUSPENSION_DATE_FROM")
    protected Date suspensionDateFrom;

    @Temporal(TemporalType.DATE)
    @Column(name = "SUSPENSION_DATE_TO")
    protected Date suspensionDateTo;


    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "agreement")
    protected List<AgreementDocument> files;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "AGREEMENT_TYPE_ID")
    protected DicContractsType agreementType;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_FROM", nullable = false)
    protected Date dateFrom;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_TO", nullable = false)
    protected Date dateTo;

    @Lookup(type = LookupType.DROPDOWN)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "STATUS_ID")
    protected DicAgreementStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    protected List<FileDescriptor> files;*/


    public void setFiles(List<AgreementDocument> files) {
        this.files = files;
    }

    public List<AgreementDocument> getFiles() {
        return files;
    }


    public void setSuspensionDateFrom(Date suspensionDateFrom) {
        this.suspensionDateFrom = suspensionDateFrom;
    }

    public Date getSuspensionDateFrom() {
        return suspensionDateFrom;
    }

    public void setSuspensionDateTo(Date suspensionDateTo) {
        this.suspensionDateTo = suspensionDateTo;
    }

    public Date getSuspensionDateTo() {
        return suspensionDateTo;
    }





    public void setAgreementType(DicContractsType agreementType) {
        this.agreementType = agreementType;
    }

    public DicContractsType getAgreementType() {
        return agreementType;
    }


    public DicAgreementStatus getStatus() {
        return status;
    }

    public void setStatus(DicAgreementStatus status) {
        this.status = status;
    }


    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }


    public void setAgreementNumber(String agreementNumber) {
        this.agreementNumber = agreementNumber;
    }

    public String getAgreementNumber() {
        return agreementNumber;
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


    /*public void setFiles(List<FileDescriptor> files) {
        this.files = files;
    }

    public List getFiles() {
        return files;
    }
*/
}