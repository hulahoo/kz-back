package kz.uco.tsadv.entity.tb;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import kz.uco.tsadv.modules.personal.model.PersonExt;

@Table(name = "TSADV_HEALTH_DETERIORATION_WITNESSES")
@Entity(name = "tsadv$HealthDeteriorationWitnesses")
public class HealthDeteriorationWitnesses extends StandardEntity {
    private static final long serialVersionUID = 5946858533117253290L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_ID")
    protected PersonExt person;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HEALTH_DETERIORATION_ID")
    protected HealthDeterioration healthDeterioration;

    public void setHealthDeterioration(HealthDeterioration healthDeterioration) {
        this.healthDeterioration = healthDeterioration;
    }

    public HealthDeterioration getHealthDeterioration() {
        return healthDeterioration;
    }


    public void setPerson(PersonExt person) {
        this.person = person;
    }

    public PersonExt getPerson() {
        return person;
    }


}