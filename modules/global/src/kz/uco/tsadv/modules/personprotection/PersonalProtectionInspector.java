package kz.uco.tsadv.modules.personprotection;

import com.haulmont.chile.core.annotations.MetaProperty;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Table(name = "TSADV_PERSONAL_PROTECTION_INSPECTOR")
@Entity(name = "tsadv$PersonalProtectionInspector")
public class PersonalProtectionInspector extends AbstractParentEntity {
    private static final long serialVersionUID = -2014206698424752162L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EMPLOYEE_ID")
    protected PersonGroupExt employee;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ORGANIZATION_GROUP_ID")
    protected OrganizationGroupExt organizationGroup;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "ASSIGNMENT_DATE", nullable = false)
    protected Date assignmentDate;

    @Column(name = "ASSIGNMENT_ORDER")
    protected String assignmentOrder;

    public void setEmployee(PersonGroupExt employee) {
        this.employee = employee;
    }

    public PersonGroupExt getEmployee() {
        return employee;
    }

    public void setOrganizationGroup(OrganizationGroupExt organizationGroup) {
        this.organizationGroup = organizationGroup;
    }

    public OrganizationGroupExt getOrganizationGroup() {
        return organizationGroup;
    }

    public void setAssignmentDate(Date assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    public Date getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentOrder(String assignmentOrder) {
        this.assignmentOrder = assignmentOrder;
    }

    public String getAssignmentOrder() {
        return assignmentOrder;
    }

    @MetaProperty
    public String getEmployeeFullName() {
        if (employee != null) {
            return employee.getFullName();
        }
        return null;
    }


}