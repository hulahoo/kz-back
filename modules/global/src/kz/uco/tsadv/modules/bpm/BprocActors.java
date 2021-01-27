package kz.uco.tsadv.modules.bpm;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.base.entity.extend.UserExt;
import kz.uco.tsadv.modules.personal.dictionary.DicHrRole;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Table(name = "TSADV_BPROC_ACTORS")
@Entity(name = "tsadv_BprocActors")
@NamePattern("%s - %s|hrRole,user")
public class BprocActors extends StandardEntity {
    private static final long serialVersionUID = -8643062596339094659L;

    @NotNull
    @Column(name = "ENTITY_ID", nullable = false)
    private UUID entityId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "HR_ROLE_ID")
    private DicHrRole hrRole;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID")
    private UserExt user;

    @NotNull
    @Column(name = "BPROC_USER_TASK_CODE", nullable = false)
    private String bprocUserTaskCode;

    public String getBprocUserTaskCode() {
        return bprocUserTaskCode;
    }

    public void setBprocUserTaskCode(String bprocUserTaskCode) {
        this.bprocUserTaskCode = bprocUserTaskCode;
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

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }
}