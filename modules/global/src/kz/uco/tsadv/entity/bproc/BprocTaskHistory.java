package kz.uco.tsadv.entity.bproc;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.personal.dictionary.DicHrRole;

import java.util.Date;

@MetaClass(name = "tsadv_BprocTaskHistory")
public class BprocTaskHistory extends BaseUuidEntity {
    private static final long serialVersionUID = 3030955067418474614L;

    @MetaProperty
    protected String procInstanseId;

    @MetaProperty
    protected DicHrRole role;

    @MetaProperty
    protected UserExt user;

    @MetaProperty
    protected String outcome;

    @MetaProperty
    protected Date startDate;

    @MetaProperty
    protected Date endDate;

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public UserExt getUser() {
        return user;
    }

    public void setUser(UserExt user) {
        this.user = user;
    }

    public DicHrRole getRole() {
        return role;
    }

    public void setRole(DicHrRole role) {
        this.role = role;
    }

    public String getProcInstanseId() {
        return procInstanseId;
    }

    public void setProcInstanseId(String procInstanseId) {
        this.procInstanseId = procInstanseId;
    }
}