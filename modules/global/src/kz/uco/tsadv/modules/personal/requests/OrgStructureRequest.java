package kz.uco.tsadv.modules.personal.requests;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Table(name = "TSADV_ORG_STRUCTURE_REQUEST")
@Entity(name = "tsadv_OrgStructureRequest")
public class OrgStructureRequest extends AbstractBprocRequest {
    private static final long serialVersionUID = -587617498631162165L;

    public static final String PROCESS_DEFINITION_KEY = "orgStructureRequest";

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "orgStructureRequest")
    protected List<OrgStructureRequestDetail> orgStructureDetail;

    @Temporal(TemporalType.DATE)
    @Column(name = "MODIFY_DATE")
    private Date modifyDate;

    @JoinTable(name = "TSADV_ORG_STRUCTURE_REQUEST_FILE_DESCRIPTOR_LINK",
            joinColumns = @JoinColumn(name = "ORG_STRUCTURE_REQUEST_ID"),
            inverseJoinColumns = @JoinColumn(name = "FILE_DESCRIPTOR_ID"))
    @ManyToMany
    @OnDelete(DeletePolicy.CASCADE)
    protected List<FileDescriptor> file;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COMPANY_ID")
    private DicCompany company;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @JoinColumn(name = "DEPARTMENT_ID")
    protected OrganizationGroupExt department;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @JoinColumn(name = "AUTHOR_ID")
    protected PersonGroupExt author;

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public void setFile(List<FileDescriptor> file) {
        this.file = file;
    }

    public List<FileDescriptor> getFile() {
        return file;
    }

    public DicCompany getCompany() {
        return company;
    }

    public void setCompany(DicCompany company) {
        this.company = company;
    }

    public List<OrgStructureRequestDetail> getOrgStructureDetail() {
        return orgStructureDetail;
    }

    public void setOrgStructureDetail(List<OrgStructureRequestDetail> orgStructureDetail) {
        this.orgStructureDetail = orgStructureDetail;
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

    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }
}