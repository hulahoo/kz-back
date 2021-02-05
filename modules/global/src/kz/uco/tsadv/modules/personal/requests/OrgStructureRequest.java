package kz.uco.tsadv.modules.personal.requests;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicCompany;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Table(name = "TSADV_ORG_STRUCTURE_REQUEST")
@Entity(name = "tsadv_OrgStructureRequest")
public class OrgStructureRequest extends StandardEntity {
    private static final long serialVersionUID = -587617498631162165L;

    @NotNull
    @Column(name = "REQUEST_NUMBER", nullable = false)
    protected Long requestNumber;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "REQUEST_DATE", nullable = false)
    protected Date requestDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @JoinColumn(name = "REQUEST_STATUS_ID")
    protected DicRequestStatus requestStatus;

    @JoinTable(name = "TSADV_ORG_STRUCTURE_REQUEST_FILE_DESCRIPTOR_LINK",
            joinColumns = @JoinColumn(name = "ORG_STRUCTURE_REQUEST_ID"),
            inverseJoinColumns = @JoinColumn(name = "FILE_DESCRIPTOR_ID"))
    @ManyToMany
    protected List<FileDescriptor> file;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @JoinColumn(name = "COMPANY_ID")
    protected DicCompany company;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @JoinColumn(name = "DEPARTMENT_ID")
    protected OrganizationGroupExt department;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @JoinColumn(name = "AUTHOR_ID")
    protected PersonGroupExt author;

    public void setFile(List<FileDescriptor> file) {
        this.file = file;
    }

    public List<FileDescriptor> getFile() {
        return file;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public PersonGroupExt getAuthor() {
        return author;
    }

    public void setAuthor(PersonGroupExt author) {
        this.author = author;
    }

    public OrganizationGroupExt getDepartment() {
        return department;
    }

    public void setDepartment(OrganizationGroupExt department) {
        this.department = department;
    }

    public DicCompany getCompany() {
        return company;
    }

    public void setCompany(DicCompany company) {
        this.company = company;
    }

    public DicRequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(DicRequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public Long getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(Long requestNumber) {
        this.requestNumber = requestNumber;
    }
}