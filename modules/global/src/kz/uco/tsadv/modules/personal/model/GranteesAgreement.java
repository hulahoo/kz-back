package kz.uco.tsadv.modules.personal.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicAgreementStatus;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Table(name = "TSADV_GRANTEES_AGREEMENT")
@Entity(name = "tsadv_GranteesAgreement")
public class GranteesAgreement extends AbstractParentEntity {
    private static final long serialVersionUID = -5206186761753771978L;

    @NotNull
    @Column(name = "CONTRACT_NUMBER", nullable = false)
    protected String contractNumber;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "CONTRACT_DATE", nullable = false)
    protected Date contractDate;

    @NotNull
    @Column(name = "UNIVERSITY", nullable = false)
    protected String university;

    @NotNull
    @Column(name = "AGREEMENT_NUMBER", nullable = false)
    protected String agreementNumber;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "AGREEMENT_DATE", nullable = false)
    protected Date agreementDate;

    @NotNull
    @Column(name = "START_YEAR", nullable = false)
    protected Integer startYear;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "STATUS_ID")
    protected DicAgreementStatus status;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public DicAgreementStatus getStatus() {
        return status;
    }

    public void setStatus(DicAgreementStatus status) {
        this.status = status;
    }

    public Integer getStartYear() {
        return startYear;
    }

    public void setStartYear(Integer startYear) {
        this.startYear = startYear;
    }

    public Date getAgreementDate() {
        return agreementDate;
    }

    public void setAgreementDate(Date agreementDate) {
        this.agreementDate = agreementDate;
    }

    public String getAgreementNumber() {
        return agreementNumber;
    }

    public void setAgreementNumber(String agreementNumber) {
        this.agreementNumber = agreementNumber;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public Date getContractDate() {
        return contractDate;
    }

    public void setContractDate(Date contractDate) {
        this.contractDate = contractDate;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }
}