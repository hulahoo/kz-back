package kz.uco.tsadv.modules.bpm;

import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.entity.tb.PositionBpmRole;
import kz.uco.tsadv.modules.personal.dictionary.DicHrRole;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "TSADV_BPM_ROLES_LINK")
@Entity(name = "tsadv$BpmRolesLink")
public class BpmRolesLink extends StandardEntity {
    private static final long serialVersionUID = 2556837329709520964L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BPM_ROLES_DEFINER_ID")
    protected BpmRolesDefiner bpmRolesDefiner;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "HR_ROLE_ID")
    protected DicHrRole hrRole;


    @NotNull
    @Column(name = "REQUIRED", nullable = false)
    protected Boolean required = false;

    @NotNull
    @Column(name = "FIND_BY_COUNTER", nullable = false)
    protected Boolean findByCounter = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POSITION_BPM_ROLE_ID")
    protected PositionBpmRole positionBpmRole;

    public void setFindByCounter(Boolean findByCounter) {
        this.findByCounter = findByCounter;
    }

    public Boolean getFindByCounter() {
        return findByCounter;
    }


    public void setPositionBpmRole(PositionBpmRole positionBpmRole) {
        this.positionBpmRole = positionBpmRole;
    }

    public PositionBpmRole getPositionBpmRole() {
        return positionBpmRole;
    }


    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Boolean getRequired() {
        return required;
    }


    public void setBpmRolesDefiner(BpmRolesDefiner bpmRolesDefiner) {
        this.bpmRolesDefiner = bpmRolesDefiner;
    }

    public BpmRolesDefiner getBpmRolesDefiner() {
        return bpmRolesDefiner;
    }

    public void setHrRole(DicHrRole hrRole) {
        this.hrRole = hrRole;
    }

    public DicHrRole getHrRole() {
        return hrRole;
    }



}