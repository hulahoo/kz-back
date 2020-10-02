package kz.uco.tsadv.entity.tb;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.haulmont.bpm.entity.ProcModel;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import com.haulmont.cuba.core.entity.StandardEntity;

import java.util.List;
import javax.persistence.OneToMany;

import kz.uco.tsadv.modules.bpm.BpmRolesLink;

@Table(name = "TSADV_POSITION_BPM_ROLE")
@Entity(name = "tsadv$PositionBpmRole")
public class PositionBpmRole extends StandardEntity {
    private static final long serialVersionUID = -5059157546766013776L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POSITION_GROUP_ID")
    protected PositionGroupExt positionGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROC_MODEL_ID")
    protected ProcModel procModel;

    @OneToMany(mappedBy = "positionBpmRole")
    protected List<BpmRolesLink> bpmRolesLink;

    public void setPositionGroup(PositionGroupExt positionGroup) {
        this.positionGroup = positionGroup;
    }

    public PositionGroupExt getPositionGroup() {
        return positionGroup;
    }


    public void setBpmRolesLink(List<BpmRolesLink> bpmRolesLink) {
        this.bpmRolesLink = bpmRolesLink;
    }

    public List<BpmRolesLink> getBpmRolesLink() {
        return bpmRolesLink;
    }


    public void setProcModel(ProcModel procModel) {
        this.procModel = procModel;
    }

    public ProcModel getProcModel() {
        return procModel;
    }


}