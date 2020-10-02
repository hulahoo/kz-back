package kz.uco.tsadv.modules.recruitment.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.reports.entity.Report;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;

import javax.persistence.*;

@NamePattern("%s|reportTemplate")
@Table(name = "TSADV_OFFER_TEMPLATE")
@Entity(name = "tsadv$OfferTemplate")
public class OfferTemplate extends AbstractParentEntity {
    private static final long serialVersionUID = 5990545020934320813L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPORT_TEMPLATE_ID")
    protected Report reportTemplate;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "clear"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_ID")
    protected OrganizationGroupExt organization;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "clear"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POSITION_ID")
    protected PositionGroupExt position;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "clear"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_ID")
    protected JobGroup job;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "clear"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RC_JOB_GROUP_ID")
    protected kz.uco.tsadv.modules.recruitment.model.RcJobGroup rcJobGroup;

    public void setRcJobGroup(kz.uco.tsadv.modules.recruitment.model.RcJobGroup rcJobGroup) {
        this.rcJobGroup = rcJobGroup;
    }

    public RcJobGroup getRcJobGroup() {
        return rcJobGroup;
    }


    public void setReportTemplate(Report reportTemplate) {
        this.reportTemplate = reportTemplate;
    }

    public Report getReportTemplate() {
        return reportTemplate;
    }

    public void setOrganization(OrganizationGroupExt organization) {
        this.organization = organization;
    }

    public OrganizationGroupExt getOrganization() {
        return organization;
    }

    public void setPosition(PositionGroupExt position) {
        this.position = position;
    }

    public PositionGroupExt getPosition() {
        return position;
    }

    public void setJob(JobGroup job) {
        this.job = job;
    }

    public JobGroup getJob() {
        return job;
    }


}