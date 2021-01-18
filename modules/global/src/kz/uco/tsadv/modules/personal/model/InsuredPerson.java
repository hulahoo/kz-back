package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.dictionary.DicEducationType;
import kz.uco.base.entity.dictionary.DicRegion;
import kz.uco.base.entity.dictionary.DicSex;
import kz.uco.base.entity.shared.Address;
import kz.uco.tsadv.entity.tb.Attachment;
import kz.uco.tsadv.modules.personal.dictionary.DicCompany;
import kz.uco.tsadv.modules.personal.dictionary.DicRelationshipType;
import kz.uco.tsadv.modules.personal.dictionary.DicVHIAttachmentStatus;
import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "TSADV_INSURED_PERSON")
@Entity(name = "tsadv$InsuredPerson")
@NamePattern("%s|firstName")
public class InsuredPerson extends StandardEntity {
    private static final long serialVersionUID = 7292298488106113517L;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "ATTACH_DATE", nullable = false)
    private Date attachDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "STATUS_REQUEST_ID")
    @NotNull
    private DicVHIAttachmentStatus statusRequest;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "INSURANCE_CONTRACT_ID")
    private InsuranceContract insuranceContract;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COMPANY_ID")
    private DicCompany company;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EMPLOYEE_ID")
    @NotNull
    private PersonGroupExt employee;

    @Column(name = "ASSIGN_DATE", nullable = false)
    @NotNull
    @Temporal(TemporalType.DATE)
    private Date assignDate;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RELATIVE_ID")
    private DicRelationshipType relative;

    @NotNull
    @Column(name = "FIRST_NAME", nullable = false, length = 50)
    private String firstName;

    @NotNull
    @Column(name = "SECOND_NAME", nullable = false, length = 50)
    private String secondName;

    @Column(name = "MIDDLE_NAME", length = 50)
    private String middleName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_ID")
    private JobGroup job;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SEX_ID")
    @NotNull
    private DicSex sex;

    @NotNull
    @Column(name = "IIN", nullable = false)
    private String iin;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "BIRTHDATE", nullable = false)
    private Date birthdate;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DOCUMENT_TYPE_ID")
    private DicEducationType documentType;

    @Column(name = "DOCUMENT_NUMBER", nullable = false)
    @NotNull
    private Integer documentNumber;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "REGION_ID")
    private DicRegion region;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ADDRESS_ID")
    private Address address;

    @NotNull
    @Column(name = "INSURANCE_PROGRAM", nullable = false, length = 500)
    private String insuranceProgram;

    @OnDelete(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    private Attachment file;

    @NotNull
    @Column(name = "TYPE", nullable = false)
    private Integer type;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    @Column(name = "TOTAL_AMOUNT", nullable = false)
    @NotNull
    private BigDecimal totalAmount;

    @Temporal(TemporalType.DATE)
    @Column(name = "EXCLUSION_DATE")
    private Date exclusionDate;

    @Column(name = "COMMENT", length = 500)
    private String comment;

    public void setDocumentNumber(Integer documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Integer getDocumentNumber() {
        return documentNumber;
    }

    public void setAssignDate(Date assignDate) {
        this.assignDate = assignDate;
    }

    public Date getAssignDate() {
        return assignDate;
    }

    public void setSex(DicSex sex) {
        this.sex = sex;
    }

    public DicSex getSex() {
        return sex;
    }

    public void setEmployee(PersonGroupExt employee) {
        this.employee = employee;
    }

    public PersonGroupExt getEmployee() {
        return employee;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getExclusionDate() {
        return exclusionDate;
    }

    public void setExclusionDate(Date exclusionDate) {
        this.exclusionDate = exclusionDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Attachment getFile() {
        return file;
    }

    public void setFile(Attachment file) {
        this.file = file;
    }

    public String getInsuranceProgram() {
        return insuranceProgram;
    }

    public void setInsuranceProgram(String insuranceProgram) {
        this.insuranceProgram = insuranceProgram;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public DicRegion getRegion() {
        return region;
    }

    public void setRegion(DicRegion region) {
        this.region = region;
    }

    public DicEducationType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DicEducationType documentType) {
        this.documentType = documentType;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getIin() {
        return iin;
    }

    public void setIin(String iin) {
        this.iin = iin;
    }

    public JobGroup getJob() {
        return job;
    }

    public void setJob(JobGroup job) {
        this.job = job;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public DicRelationshipType getRelative() {
        return relative;
    }

    public void setRelative(DicRelationshipType relative) {
        this.relative = relative;
    }

    public DicVHIAttachmentStatus getStatusRequest() {
        return statusRequest;
    }

    public void setStatusRequest(DicVHIAttachmentStatus statusRequest) {
        this.statusRequest = statusRequest;
    }

    public DicCompany getCompany() {
        return company;
    }

    public void setCompany(DicCompany company) {
        this.company = company;
    }

    public InsuranceContract getInsuranceContract() {
        return insuranceContract;
    }

    public void setInsuranceContract(InsuranceContract insuranceContract) {
        this.insuranceContract = insuranceContract;
    }

    public Date getAttachDate() {
        return attachDate;
    }

    public void setAttachDate(Date attachDate) {
        this.attachDate = attachDate;
    }
}