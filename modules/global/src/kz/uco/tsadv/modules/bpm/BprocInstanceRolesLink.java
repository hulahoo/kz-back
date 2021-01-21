package kz.uco.tsadv.modules.bpm;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.personal.dictionary.DicHrRole;

import javax.persistence.*;

@Table(name = "TSADV_BPROC_INSTANCE_ROLES_LINK")
@Entity(name = "tsadv_BprocInstanceRolesLink")
public class BprocInstanceRolesLink extends StandardEntity {
    private static final long serialVersionUID = 7192788609266291235L;

    @Column(name = "PROCESS_INSTANCE_ID")
    protected String processInstanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HR_ROLE_ID")
    protected DicHrRole hrRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    protected UserExt user;

    @MetaProperty
    @Transient
    protected Boolean required;

    public Boolean isRequired() {
        return required == null ? false : required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public UserExt getUser() {
        return user;
    }

    public void setUser(UserExt user) {
        this.user = user;
    }

    public DicHrRole getHrRole() {
        return hrRole;
    }

    public void setHrRole(DicHrRole hrRole) {
        this.hrRole = hrRole;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }
}