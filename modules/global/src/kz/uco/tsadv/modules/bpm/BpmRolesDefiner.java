package kz.uco.tsadv.modules.bpm;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.dictionary.DicCompany;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Table(name = "TSADV_BPM_ROLES_DEFINER")
@Entity(name = "tsadv$BpmRolesDefiner")
@NamePattern("%s|processDefinitionKey")
public class BpmRolesDefiner extends StandardEntity {
    private static final long serialVersionUID = 2556837329709520964L;

    @Column(name = "PROCESS_DEFINITION_KEY")
    protected String processDefinitionKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    private DicCompany company;

    @Composition
    @OrderBy("order")
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "bpmRolesDefiner")
    protected List<BpmRolesLink> links;

    @NotNull
    @Column(name = "MANAGER_LAUNCHES", nullable = false)
    private Boolean managerLaunches = false;

    @NotNull
    @Column(name = "ACTIVE_SUP_MANAGER_EXCLUSION", nullable = false)
    private Boolean activeSupManagerExclusion = false;

    public Boolean getActiveSupManagerExclusion() {
        return activeSupManagerExclusion;
    }

    public void setActiveSupManagerExclusion(Boolean activeSupManagerExclusion) {
        this.activeSupManagerExclusion = activeSupManagerExclusion;
    }

    public Boolean getManagerLaunches() {
        return managerLaunches;
    }

    public void setManagerLaunches(Boolean managerLaunches) {
        this.managerLaunches = managerLaunches;
    }

    public DicCompany getCompany() {
        return company;
    }

    public void setCompany(DicCompany company) {
        this.company = company;
    }

    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    public void setLinks(List<BpmRolesLink> links) {
        this.links = links;
    }

    public List<BpmRolesLink> getLinks() {
        return links;
    }


}