package kz.uco.tsadv.modules.timesheet.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import javax.persistence.ManyToOne;
import kz.uco.tsadv.modules.personal.dictionary.DicWorkingCondition;

@NamePattern("%s|id")
@Table(name = "TSADV_ORG_ANALYTICS")
@Entity(name = "tsadv$OrgAnalytics")
public class OrgAnalytics extends StandardEntity {
    private static final long serialVersionUID = 3241683771722600218L;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CALENDAR_ID")
    protected Calendar calendar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WORKING_CONDITION_ID")
    protected DicWorkingCondition workingCondition;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OFFSET_ID")
    protected StandardOffset offset;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "analytics")
    protected OrganizationGroupExt organizationGroup;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "analytics")
    protected PositionGroupExt positionGroup;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "analytics")
    protected AssignmentGroupExt assignmentGroup;

    public void setWorkingCondition(DicWorkingCondition workingCondition) {
        this.workingCondition = workingCondition;
    }

    public DicWorkingCondition getWorkingCondition() {
        return workingCondition;
    }


    public void setAssignmentGroup(AssignmentGroupExt assignmentGroup) {
        this.assignmentGroup = assignmentGroup;
    }

    public AssignmentGroupExt getAssignmentGroup() {
        return assignmentGroup;
    }


    public void setPositionGroup(PositionGroupExt positionGroup) {
        this.positionGroup = positionGroup;
    }

    public PositionGroupExt getPositionGroup() {
        return positionGroup;
    }


    public void setOrganizationGroup(OrganizationGroupExt organizationGroup) {
        this.organizationGroup = organizationGroup;
    }

    public OrganizationGroupExt getOrganizationGroup() {
        return organizationGroup;
    }


    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setOffset(StandardOffset offset) {
        this.offset = offset;
    }

    public StandardOffset getOffset() {
        return offset;
    }


}