package kz.uco.tsadv.modules.personal.model;

import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.tsadv.modules.personal.dictionary.DicIncentiveResultStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Table(name = "TSADV_ORGANIZATION_INCENTIVE_MONTH_RESULT")
@Entity(name = "tsadv_OrganizationIncentiveMonthResult")
public class OrganizationIncentiveMonthResult extends StandardEntity {
    private static final long serialVersionUID = -6327941622311600685L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COMPANY_ID")
    private DicCompany company;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "PERIOD_", nullable = false)
    private Date period;

    @OneToMany(mappedBy = "organizationIncentiveMonthResult")
    private List<OrganizationIncentiveResult> incentiveResults;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATUS_ID")
    private DicIncentiveResultStatus status;

    @Column(name = "COMMENT_", length = 2500)
    private String comment;

    public DicCompany getCompany() {
        return company;
    }

    public void setCompany(DicCompany company) {
        this.company = company;
    }

    public List<OrganizationIncentiveResult> getIncentiveResults() {
        return incentiveResults;
    }

    public void setIncentiveResults(List<OrganizationIncentiveResult> incentiveResults) {
        this.incentiveResults = incentiveResults;
    }

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

    public Date getPeriod() {
        return period;
    }

    public void setPeriod(Date period) {
        this.period = period;
    }

}