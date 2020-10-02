package kz.uco.tsadv.modules.performance.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;

@Table(name = "TSADV_CALIBRATION_COMISSION")
@Entity(name = "tsadv$CalibrationComission")
public class CalibrationComission extends AbstractParentEntity {
    private static final long serialVersionUID = 3921493674348409675L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_ID")
    protected PersonGroupExt person;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CALIBRATION_SESSION_ID")
    protected CalibrationSession calibrationSession;

    public void setCalibrationSession(CalibrationSession calibrationSession) {
        this.calibrationSession = calibrationSession;
    }

    public CalibrationSession getCalibrationSession() {
        return calibrationSession;
    }


    public void setPerson(PersonGroupExt person) {
        this.person = person;
    }

    public PersonGroupExt getPerson() {
        return person;
    }


}