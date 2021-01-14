package kz.uco.tsadv.modules.personal.model;

import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Table(name = "TSADV_CONTRACT_ADMINISTRATOR")
@Entity(name = "tsad$ContractAdministrator")
public class InsuranceContractAdministrator extends StandardEntity {
    private static final long serialVersionUID = 4372972668186211278L;

    @Column(name = "NOTIFY_ABOUT_NEW_ATTACHMENTS")
    private Boolean notifyAboutNewAttachments;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EMPLOYEE_ID")
    private PersonGroupExt employee;
    @OneToMany(mappedBy = "insuranceContractAdministrator")
    private List<InsuranceContract> insuranceContract;

    public List<InsuranceContract> getInsuranceContract() {
        return insuranceContract;
    }

    public void setInsuranceContract(List<InsuranceContract> insuranceContract) {
        this.insuranceContract = insuranceContract;
    }

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