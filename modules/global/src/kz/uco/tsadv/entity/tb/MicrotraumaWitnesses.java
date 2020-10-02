package kz.uco.tsadv.entity.tb;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import kz.uco.tsadv.modules.personal.model.PersonExt;

@Table(name = "TSADV_MICROTRAUMA_WITNESSES")
@Entity(name = "tsadv$MicrotraumaWitnesses")
public class MicrotraumaWitnesses extends AbstractParentEntity {
    private static final long serialVersionUID = -1223896553783586653L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_ID")
    protected PersonExt person;





    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MICROTRAUM_ID")
    protected Microtraum microtraum;

    public void setMicrotraum(Microtraum microtraum) {
        this.microtraum = microtraum;
    }

    public Microtraum getMicrotraum() {
        return microtraum;
    }


    public void setPerson(PersonExt person) {
        this.person = person;
    }

    public PersonExt getPerson() {
        return person;
    }


}