package kz.uco.tsadv.modules.recognition;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import com.haulmont.cuba.core.entity.StandardEntity;

@Table(name = "TSADV_PERSON_MEDAL")
@Entity(name = "tsadv$PersonMedal")
public class PersonMedal extends StandardEntity {
    private static final long serialVersionUID = 7343235564685000959L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MEDAL_ID")
    protected Medal medal;

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setMedal(Medal medal) {
        this.medal = medal;
    }

    public Medal getMedal() {
        return medal;
    }


}