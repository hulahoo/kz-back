package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.PublishEntityChangedEvents;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.base.entity.dictionary.DicRegion;
import kz.uco.base.entity.dictionary.DicSex;
import kz.uco.tsadv.modules.personal.dictionary.DicAddressType;
import kz.uco.tsadv.modules.personal.dictionary.DicDocumentType;
import kz.uco.tsadv.modules.personal.dictionary.DicMICAttachmentStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicRelationshipType;
import kz.uco.tsadv.modules.personal.enums.RelativeType;
import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@PublishEntityChangedEvents
@Listeners("tsadv_InsuredPersonChangedListener")
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
    private DicMICAttachmentStatus statusRequest;

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

    @Column(name = "JOB_MEMBER")
    private String jobMember;

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DOCUMENT_TYPE_ID")
    @NotNull
    private DicDocumentType documentType;

    @Column(name = "DOCUMENT_NUMBER", nullable = false)
    @NotNull
    private String documentNumber;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "REGION_ID")
    private DicRegion region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADDRESS_TYPE_ID")
    private DicAddressType addressType;

    @Column(name = "ADDRESS")
    private String address;

    @NotNull
    @Column(name = "INSURANCE_PROGRAM", nullable = false, length = 500)
    private String insuranceProgram;

    @OnDelete(DeletePolicy.CASCADE)
    @ManyToMany
    @JoinTable(name = "TSADV_INSURED_PERSON_FILE_DESCRIPTOR_LINK",
            joinColumns = @JoinColumn(name = "INSURED_PERSON_ID"),
            inverseJoinColumns = @JoinColumn(name = "FILE_DESCRIPTOR_ID"))
    private List<FileDescriptor> file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATEMENT_FILE_ID")
    private FileDescriptor statementFile;

    @Column(name = "TYPE", nullable = false)
    @NotNull
    private String type;

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

    public void setAddressType(DicAddressType addressType) {
        this.addressType = addressType;
    }

    public DicAddressType getAddressType() {
        return addressType;
    }

    public FileDescriptor getStatementFile() {
        return statementFile;
    }

    public void setStatementFile(FileDescriptor statementFile) {
        this.statementFile = statementFile;
    }

    public String getJobMember() {
        return jobMember;
    }

    public void setJobMember(String jobMember) {
        this.jobMember = jobMember;
    }

    public void setFile(List<FileDescriptor> file) {
        this.file = file;
    }

    public List<FileDescriptor> getFile() {
        return file;
    }

    public void setType(RelativeType type) {
        this.type = type == null ? null : type.getId();
    }

    public RelativeType getType() {
        return type == null ? null : RelativeType.fromId(type);
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setJob(JobGroup job) {
        this.job = job;
    }

    public JobGroup getJob() {
        return job;
    }

    public void setDocumentType(DicDocumentType documentType) {
        this.documentType = documentType;
    }

    public DicDocumentType getDocumentType() {
        return documentType;
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

    public String getInsuranceProgram() {
        return insuranceProgram;
    }

    public void setInsuranceProgram(String insuranceProgram) {
        this.insuranceProgram = insuranceProgram;
    }

    public DicRegion getRegion() {
        return region;
    }

    public void setRegion(DicRegion region) {
        this.region = region;
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

    public DicMICAttachmentStatus getStatusRequest() {
        return statusRequest;
    }

    public void setStatusRequest(DicMICAttachmentStatus statusRequest) {
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