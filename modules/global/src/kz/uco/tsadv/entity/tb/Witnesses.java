package kz.uco.tsadv.entity.tb;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.entity.tb.dictionary.AccidentPersonType;

@Table(name = "TSADV_WITNESSES")
@Entity(name = "tsadv$Witnesses")
public class Witnesses extends AbstractParentEntity {
    private static final long serialVersionUID = 6345891345908748498L;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_ID")
    protected PersonExt person;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TYPE_ID")
    protected AccidentPersonType type;





    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCIDENTS_ID")
    protected Accidents accidents;

    public void setAccidents(Accidents accidents) {
        this.accidents = accidents;
    }

    public Accidents getAccidents() {
        return accidents;
    }


    public AccidentPersonType getType() {
        return type;
    }

    public void setType(AccidentPersonType type) {
        this.type = type;
    }





    public void setPerson(PersonExt person) {
        this.person = person;
    }

    public PersonExt getPerson() {
        return person;
    }





}