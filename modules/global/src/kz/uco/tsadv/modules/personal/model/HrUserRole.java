package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.personal.dictionary.DicHrRole;

import javax.persistence.*;
import java.util.Date;

@NamePattern("%s|id")
@Table(name = "TSADV_HR_USER_ROLE")
@Entity(name = "tsadv$HrUserRole")
public class HrUserRole extends AbstractParentEntity {
    private static final long serialVersionUID = -485948825432285552L;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ROLE_ID")
    protected DicHrRole role;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID")
    protected UserExt user;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_FROM", nullable = false)
    protected Date dateFrom;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_TO", nullable = false)
    protected Date dateTo;

    public void setUser(UserExt user) {
        this.user = user;
    }

    public UserExt getUser() {
        return user;
    }


    public DicHrRole getRole() {
        return role;
    }

    public void setRole(DicHrRole role) {
        this.role = role;
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