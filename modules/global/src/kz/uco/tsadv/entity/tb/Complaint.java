package kz.uco.tsadv.entity.tb;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import java.util.UUID;

@Table(name = "TSADV_COMPLAINT")
@Entity(name = "tsadv$Complaint")
public class Complaint extends AbstractParentEntity {
    private static final long serialVersionUID = -8966564847280097548L;


    @Column(name = "COMPLAINTS", nullable = false)
    protected String complaints;

    @Column(name = "INDEX_")
    protected String index;




    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HEALTH_DETERIORATION_ID")
    protected HealthDeterioration healthDeterioration;

    public void setHealthDeterioration(HealthDeterioration healthDeterioration) {
        this.healthDeterioration = healthDeterioration;
    }

    public HealthDeterioration getHealthDeterioration() {
        return healthDeterioration;
    }


    public void setComplaints(String complaints) {
        this.complaints = complaints;
    }

    public String getComplaints() {
        return complaints;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getIndex() {
        return index;
    }


}