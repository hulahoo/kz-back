package kz.uco.tsadv.modules.personal.model;

import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "TSADV_INSURANCE_CONTRACT_ADMINISTRATOR")
@Entity(name = "tsadv$InsuranceContractAdministrator")
public class InsuranceContractAdministrator extends StandardEntity {
    private static final long serialVersionUID = 5323731111863476380L;

    @Column(name = "NOTIFY_ABOUT_NEW_ATTACHMENTS")
    private Boolean notifyAboutNewAttachments;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EMPLOYEE_ID")
    private PersonGroupExt employee;

    public PersonGroupExt getEmployee() {
        return employee;
    }

    public void setEmployee(PersonGroupExt employee) {
        this.employee = employee;
    }

    public Boolean getNotifyAboutNewAttachments() {
        return notifyAboutNewAttachments;
    }

    public void setNotifyAboutNewAttachments(Boolean notifyAboutNewAttachments) {
        this.notifyAboutNewAttachments = notifyAboutNewAttachments;
    }
}