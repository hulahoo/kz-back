package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.entity.tb.Attachment;
import kz.uco.tsadv.modules.personal.dictionary.DicCompany;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Table(name = "TSADV_INSURANCE_CONTRACT")
@Entity(name = "tsadv$InsuranceContract")
public class InsuranceContract extends StandardEntity {
    private static final long serialVersionUID = 599161704312110269L;

    @NotNull
    @Column(name = "POLICY_NAME", nullable = false, length = 25)
    private String policyName;

    @NotNull
    @Column(name = "CONTRACT", nullable = false, length = 10)
    private String contract;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "SIGN_DATE", nullable = false)
    private Date signDate;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COMPANY_ID")
    private DicCompany company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESPONSIBLE_ID")
    private PersonGroupExt responsible;

    @Column(name = "INSURER")
    private String insurer;

    @Column(name = "YEAR")
    private Integer year;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "START_DATE", nullable = false)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "EXPIRATION_DATE", nullable = false)
    private Date expirationDate;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "AVAILABILITY_PERIOD_FROM", nullable = false)
    private Date availabilityPeriodFrom;

    @NotNull
    @Column(name = "INSURANCE_PROGRAM", nullable = false, length = 500)
    private String insuranceProgram;

    @Column(name = "INSURER_CONTACTS", length = 100)
    private String insurerContacts;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "NOTIFICATION_DATE", nullable = false)
    private Date notificationDate;

    @NotNull
    @Column(name = "ATTACHING_AN_EMPLOYEE", nullable = false)
    private Integer attachingAnEmployee;

    @NotNull
    @Column(name = "ATTACHING_FAMILY", nullable = false)
    private Integer attachingFamily;

    @Column(name = "COUNT_OF_FREE_MEMBERS")
    private Integer countOfFreeMembers;

    @Composition
    @OneToMany(mappedBy = "insuranceContract")
    private List<ContractConditions> programConditions;

    @Composition
    @OneToMany(mappedBy = "insuranceContract")
    private List<InsuranceContractAdministrator> contractAdministrator;

    @Composition
    @OneToMany(mappedBy = "insuranceContract")
    private List<Attachment> attachments;

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public List<InsuranceContractAdministrator> getContractAdministrator() {
        return contractAdministrator;
    }

    public void setContractAdministrator(List<InsuranceContractAdministrator> contractAdministrator) {
        this.contractAdministrator = contractAdministrator;
    }

    public List<ContractConditions> getProgramConditions() {
        return programConditions;
    }

    public void setProgramConditions(List<ContractConditions> programConditions) {
        this.programConditions = programConditions;
    }

    public Integer getCountOfFreeMembers() {
        return countOfFreeMembers;
    }

    public void setCountOfFreeMembers(Integer countOfFreeMembers) {
        this.countOfFreeMembers = countOfFreeMembers;
    }

    public Integer getAttachingFamily() {
        return attachingFamily;
    }

    public void setAttachingFamily(Integer attachingFamily) {
        this.attachingFamily = attachingFamily;
    }

    public Integer getAttachingAnEmployee() {
        return attachingAnEmployee;
    }

    public void setAttachingAnEmployee(Integer attachingAnEmployee) {
        this.attachingAnEmployee = attachingAnEmployee;
    }

    public Date getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(Date notificationDate) {
        this.notificationDate = notificationDate;
    }

    public String getInsurerContacts() {
        return insurerContacts;
    }

    public void setInsurerContacts(String insurerContacts) {
        this.insurerContacts = insurerContacts;
    }

    public String getInsuranceProgram() {
        return insuranceProgram;
    }

    public void setInsuranceProgram(String insuranceProgram) {
        this.insuranceProgram = insuranceProgram;
    }

    public Date getAvailabilityPeriodFrom() {
        return availabilityPeriodFrom;
    }

    public void setAvailabilityPeriodFrom(Date availabilityPeriodFrom) {
        this.availabilityPeriodFrom = availabilityPeriodFrom;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getInsurer() {
        return insurer;
    }

    public void setInsurer(String insurer) {
        this.insurer = insurer;
    }

    public PersonGroupExt getResponsible() {
        return responsible;
    }

    public void setResponsible(PersonGroupExt responsible) {
        this.responsible = responsible;
    }

    public DicCompany getCompany() {
        return company;
    }

    public void setCompany(DicCompany company) {
        this.company = company;
    }

    public Date getSignDate() {
        return signDate;
    }

    public void setSignDate(Date signDate) {
        this.signDate = signDate;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }
}