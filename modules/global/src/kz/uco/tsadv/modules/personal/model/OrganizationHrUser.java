package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.base.entity.extend.UserExt;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;

import javax.persistence.*;
import java.util.Date;

@NamePattern("%s|id")
@Table(name = "TSADV_ORGANIZATION_HR_USER")
@Entity(name = "tsadv$OrganizationHrUser")
public class OrganizationHrUser extends AbstractParentEntity {
    private static final long serialVersionUID = -457894593197304338L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ORGANIZATION_GROUP_ID")
    protected OrganizationGroupExt organizationGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    protected UserExt user;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_FROM", nullable = false)
    protected Date dateFrom;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_TO", nullable = false)
    protected Date dateTo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "REQUESTED_TS")
    protected Date requestedTs;

    @Column(name = "COUNTER")
    protected Integer counter;

    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    public Integer getCounter() {
        return counter;
    }


    public void setUser(UserExt user) {
        this.user = user;
    }

    public UserExt getUser() {
        return user;
    }


    public void setRequestedTs(Date requestedTs) {
        this.requestedTs = requestedTs;
    }

    public Date getRequestedTs() {
        return requestedTs;
    }


    public void setOrganizationGroup(OrganizationGroupExt organizationGroup) {
        this.organizationGroup = organizationGroup;
    }

    public OrganizationGroupExt getOrganizationGroup() {
        return organizationGroup;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Date getDateTo() {
        return dateTo;
    }


}