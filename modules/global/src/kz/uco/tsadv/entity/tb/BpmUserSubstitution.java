package kz.uco.tsadv.entity.tb;

import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.modules.administration.UserExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

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