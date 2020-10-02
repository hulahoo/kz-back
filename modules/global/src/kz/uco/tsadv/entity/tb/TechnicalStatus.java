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
import kz.uco.tsadv.entity.tb.dictionary.TechnicalStatusDictionary;
import java.util.UUID;

@Table(name = "TSADV_TECHNICAL_STATUS")
@Entity(name = "tsadv$TechnicalStatus")
public class TechnicalStatus extends AbstractParentEntity {
    private static final long serialVersionUID = -1212215261271111097L;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BUILD_TECHNICAL_STATUS_ID")
    protected TechnicalStatusDictionary buildTechnicalStatus;

    @Temporal(TemporalType.DATE)
    @Column(name = "TECHNICAL_STATUS_DATE", nullable = false)
    protected Date technicalStatusDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TECHNICAL_STATUS_BUILDSTRUCTURES_ID")
    protected TechnicalStatusDictionary technicalStatusBuildstructures;




    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUILDINGS_ID")
    protected Buildings buildings;

    public void setBuildings(Buildings buildings) {
        this.buildings = buildings;
    }

    public Buildings getBuildings() {
        return buildings;
    }


    public TechnicalStatusDictionary getBuildTechnicalStatus() {
        return buildTechnicalStatus;
    }

    public void setBuildTechnicalStatus(TechnicalStatusDictionary buildTechnicalStatus) {
        this.buildTechnicalStatus = buildTechnicalStatus;
    }


    public TechnicalStatusDictionary getTechnicalStatusBuildstructures() {
        return technicalStatusBuildstructures;
    }

    public void setTechnicalStatusBuildstructures(TechnicalStatusDictionary technicalStatusBuildstructures) {
        this.technicalStatusBuildstructures = technicalStatusBuildstructures;
    }



    public void setTechnicalStatusDate(Date technicalStatusDate) {
        this.technicalStatusDate = technicalStatusDate;
    }

    public Date getTechnicalStatusDate() {
        return technicalStatusDate;
    }


}