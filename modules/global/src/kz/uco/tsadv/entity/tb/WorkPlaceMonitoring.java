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

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.entity.tb.dictionary.ControlStage;
import kz.uco.tsadv.modules.personal.model.PersonExt;

@Table(name = "TSADV_WORK_PLACE_MONITORING")
@Entity(name = "tsadv$WorkPlaceMonitoring")
public class WorkPlaceMonitoring extends AbstractParentEntity {
    private static final long serialVersionUID = 4965867074115590437L;

    @Temporal(TemporalType.DATE)
    @Column(name = "MONITORING_DATE", nullable = false)
    protected Date monitoringDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "INSPECTOR_FULL_NAME_ID")
    protected PersonExt inspectorFullName;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CONTROL_STAGE_ID")
    protected ControlStage controlStage;




    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WORK_PLACE_ID")
    protected WorkPlace workPlace;

    public void setWorkPlace(WorkPlace workPlace) {
        this.workPlace = workPlace;
    }

    public WorkPlace getWorkPlace() {
        return workPlace;
    }


    public void setInspectorFullName(PersonExt inspectorFullName) {
        this.inspectorFullName = inspectorFullName;
    }

    public PersonExt getInspectorFullName() {
        return inspectorFullName;
    }


    public void setControlStage(ControlStage controlStage) {
        this.controlStage = controlStage;
    }

    public ControlStage getControlStage() {
        return controlStage;
    }


    public void setMonitoringDate(Date monitoringDate) {
        this.monitoringDate = monitoringDate;
    }

    public Date getMonitoringDate() {
        return monitoringDate;
    }



}