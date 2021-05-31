package kz.uco.tsadv.modules.bpm;

import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicHrRole;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "TSADV_BPM_ROLES_LINK")
@Entity(name = "tsadv$BpmRolesLink")
public class BpmRolesLink extends StandardEntity {
    private static final long serialVersionUID = 2556837329709520964L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BPM_ROLES_DEFINER_ID")
    protected BpmRolesDefiner bpmRolesDefiner;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "HR_ROLE_ID")
    protected DicHrRole hrRole;

    @Column(name = "BPROC_USER_TASK_CODE")
    protected String bprocUserTaskCode;

    @Column(name = "ORDER_", nullable = false)
    @NotNull
    private Integer order;

    @NotNull
    @Column(name = "REQUIRED", nullable = false)
    protected Boolean required = false;

    @NotNull
    @Column(name = "IS_ADDABLE_APPROVER", nullable = false)
    private Boolean isAddableApprover = false;

    @Column(name = "PRIORITY")
    private Integer priority;

    @NotNull
    @Column(name = "FIND_BY_COUNTER", nullable = false)
    protected Boolean findByCounter = false;

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Boolean getIsAddableApprover() {
        return isAddableApprover;
    }

    public void setIsAddableApprover(Boolean isAddableApprover) {
        this.isAddableApprover = isAddableApprover;
    }

    public String getBprocUserTaskCode() {
        return bprocUserTaskCode;
    }

    public void setBprocUserTaskCode(String bprocUserTaskCode) {
        this.bprocUserTaskCode = bprocUserTaskCode;
    }

    public void setFindByCounter(Boolean findByCounter) {
        this.findByCounter = findByCounter;
    }

    public Boolean getFindByCounter() {
        return findByCounter;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setBpmRolesDefiner(BpmRolesDefiner bpmRolesDefiner) {
        this.bpmRolesDefiner = bpmRolesDefiner;
    }

    public BpmRolesDefiner getBpmRolesDefiner() {
        return bpmRolesDefiner;
    }

    public void setHrRole(DicHrRole hrRole) {
        this.hrRole = hrRole;
    }

    public DicHrRole getHrRole() {
        return hrRole;
    }

}