package kz.uco.tsadv.modules.personal.model;

import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicIncentiveResultStatus;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Table(name = "TSADV_ORGANIZATION_INCENTIVE_MONTH_RESULT")
@Entity(name = "tsadv_OrganizationIncentiveMonthResult")
public class OrganizationIncentiveMonthResult extends StandardEntity {
    private static final long serialVersionUID = -6327941622311600685L;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "PERIOD_", nullable = false)
    private Date period;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DEPARTMENT_ID")
    @NotNull
    private OrganizationGroupExt department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATUS_ID")
    private DicIncentiveResultStatus status;

    @Column(name = "COMMENT_", length = 2500)
    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public DicIncentiveResultStatus getStatus() {
        return status;
    }

    public void setStatus(DicIncentiveResultStatus status) {
        this.status = status;
    }

    public OrganizationGroupExt getDepartment() {
        return department;
    }

    public void setDepartment(OrganizationGroupExt department) {
        this.department = department;
    }

    public Date getPeriod() {
        return period;
    }

    public void setPeriod(Date period) {
        this.period = period;
    }

}