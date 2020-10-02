package kz.uco.tsadv.entity.tb;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;
import javax.persistence.Column;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import kz.uco.tsadv.entity.tb.dictionary.ControlType;

@Table(name = "TSADV_SANITARY_REGULATIONS_CONTROL")
@Entity(name = "tsadv$SanitaryRegulationsControl")
public class SanitaryRegulationsControl extends AbstractParentEntity {
    private static final long serialVersionUID = -8207548801978362695L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CONTROL_TYPE_ID")
    protected ControlType controlType;

    @Column(name = "TOTAL_CONTROL")
    protected Long totalControl;

    @Column(name = "IDENTIFIED_INCONSISTENCIES")
    protected Long identifiedInconsistencies;

    @Column(name = "ELIMINATED_INCONSISTENCIES")
    protected Long eliminatedInconsistencies;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OCCUPATIONAL_MEDICINE_ID")
    protected OccupationalMedicine occupationalMedicine;

    public void setOccupationalMedicine(OccupationalMedicine occupationalMedicine) {
        this.occupationalMedicine = occupationalMedicine;
    }

    public OccupationalMedicine getOccupationalMedicine() {
        return occupationalMedicine;
    }


    public ControlType getControlType() {
        return controlType;
    }

    public void setControlType(ControlType controlType) {
        this.controlType = controlType;
    }


    public void setTotalControl(Long totalControl) {
        this.totalControl = totalControl;
    }

    public Long getTotalControl() {
        return totalControl;
    }

    public void setIdentifiedInconsistencies(Long identifiedInconsistencies) {
        this.identifiedInconsistencies = identifiedInconsistencies;
    }

    public Long getIdentifiedInconsistencies() {
        return identifiedInconsistencies;
    }

    public void setEliminatedInconsistencies(Long eliminatedInconsistencies) {
        this.eliminatedInconsistencies = eliminatedInconsistencies;
    }

    public Long getEliminatedInconsistencies() {
        return eliminatedInconsistencies;
    }


}