package kz.uco.tsadv.entity.tb;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

import java.util.List;
import javax.persistence.OneToMany;

@Table(name = "TSADV_MICROTRAUMA_INJURED")
@Entity(name = "tsadv$MicrotraumaInjured")
public class MicrotraumaInjured extends AbstractParentEntity {
    private static final long serialVersionUID = 7083561460634753731L;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_ID")
    protected PersonExt person;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "microtraumaInjured")
    protected List<InjuredOrgan> organ;

    @Column(name = "CORRECTIVE_ACTIONS")
    protected String correctiveActions;

    @Column(name = "NOTE_MICROTRAUMA_TO_ACCIDENT")
    protected String noteMicrotraumaToAccident;




    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MICROTRAUM_ID")
    protected Microtraum microtraum;

    public void setOrgan(List<InjuredOrgan> organ) {
        this.organ = organ;
    }

    public List<InjuredOrgan> getOrgan() {
        return organ;
    }


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


    public void setCorrectiveActions(String correctiveActions) {
        this.correctiveActions = correctiveActions;
    }

    public String getCorrectiveActions() {
        return correctiveActions;
    }

    public void setNoteMicrotraumaToAccident(String noteMicrotraumaToAccident) {
        this.noteMicrotraumaToAccident = noteMicrotraumaToAccident;
    }

    public String getNoteMicrotraumaToAccident() {
        return noteMicrotraumaToAccident;
    }


}