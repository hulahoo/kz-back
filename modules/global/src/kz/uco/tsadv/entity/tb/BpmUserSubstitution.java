package kz.uco.tsadv.entity.tb;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import kz.uco.base.entity.extend.UserExt;
import com.haulmont.cuba.core.entity.StandardEntity;

@Table(name = "TSADV_BPM_USER_SUBSTITUTION")
@Entity(name = "tsadv$BpmUserSubstitution")
public class BpmUserSubstitution extends StandardEntity {
    private static final long serialVersionUID = -6746651332303539978L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SUBSTITUTED_USER_ID")
    protected UserExt substitutedUser;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID")
    protected UserExt user;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "START_DATE", nullable = false)
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "END_DATE", nullable = false)
    protected Date endDate;

    public void setSubstitutedUser(UserExt substitutedUser) {
        this.substitutedUser = substitutedUser;
    }

    public UserExt getSubstitutedUser() {
        return substitutedUser;
    }

    public void setUser(UserExt user) {
        this.user = user;
    }

    public UserExt getUser() {
        return user;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }


}