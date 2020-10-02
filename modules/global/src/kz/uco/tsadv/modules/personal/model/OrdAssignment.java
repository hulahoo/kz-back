package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.PersistenceHelper;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;

import javax.persistence.*;
import java.util.List;

@NamePattern("%s|assignmentGroup")
@Table(name = "TSADV_ORD_ASSIGNMENT")
@Entity(name = "tsadv$OrdAssignment")
public class OrdAssignment extends AbstractParentEntity {
    private static final long serialVersionUID = 4921661317623473504L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ASSIGNMENT_GROUP_ID")
    protected AssignmentGroupExt assignmentGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID")
    protected Order order;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "ordAssignment")
    protected List<Absence> absences;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "ordAssignment")
    protected List<Salary> salary;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "ordAssignment")
    protected List<BusinessTrip> businessTrip;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "ordAssignment")
    protected List<Dismissal> dismissal;

    @Transient
    @MetaProperty
    protected AssignmentExt assignmentByOrderDate;

    @Transient
    @MetaProperty(related = "order")
    protected PersonExt assignmentPersonByOrderDate;

    @Transient
    @MetaProperty(related = "order")
    protected PositionExt assignmentPositionByOrderDate;

    public AssignmentExt getAssignmentByOrderDate() {
        return assignmentByOrderDate;
    }

    public void setDismissal(List<Dismissal> dismissal) {
        this.dismissal = dismissal;
    }

    public List<Dismissal> getDismissal() {
        return dismissal;
    }

    public void setBusinessTrip(List<BusinessTrip> businessTrip) {
        this.businessTrip = businessTrip;
    }

    public List<BusinessTrip> getBusinessTrip() {
        return businessTrip;
    }

    public void setSalary(List<Salary> salary) {
        this.salary = salary;
    }

    public List<Salary> getSalary() {
        return salary;
    }

    public void setAssignmentGroup(AssignmentGroupExt assignmentGroup) {
        this.assignmentGroup = assignmentGroup;
    }

    public AssignmentGroupExt getAssignmentGroup() {
        return assignmentGroup;
    }

    public void setAbsences(List<Absence> absences) {
        this.absences = absences;
    }

    public List<Absence> getAbsences() {
        return absences;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

    public PersonExt getAssignmentPersonByOrderDate() {
        if (getAssignmentByOrderDate() != null
                && getAssignmentByOrderDate().getPersonGroup() != null
                && PersistenceHelper.isLoaded(getAssignmentByOrderDate().getPersonGroup(), "list")
                && PersistenceHelper.isLoaded(this, "order"))
            getAssignmentByOrderDate().getPersonGroup().getList().stream().filter(p -> !order.getOrderDate().before(p.getStartDate())
                    && !order.getOrderDate().after(p.getEndDate())).findFirst().ifPresent(p -> assignmentPersonByOrderDate = p);
        return assignmentPersonByOrderDate;
    }

    public PositionExt getAssignmentPositionByOrderDate() {
        if (getAssignmentByOrderDate() != null
                && getAssignmentByOrderDate().getPositionGroup() != null
                && PersistenceHelper.isLoaded(getAssignmentByOrderDate().getPositionGroup(), "list")
                && PersistenceHelper.isLoaded(this, "order"))
            getAssignmentByOrderDate().getPositionGroup().getList().stream().filter(p -> !order.getOrderDate().before(p.getStartDate())
                    && !order.getOrderDate().after(p.getEndDate())).findFirst().ifPresent(p -> assignmentPositionByOrderDate = p);
        return assignmentPositionByOrderDate;
    }
}