package kz.uco.tsadv.modules.recognition;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import kz.uco.base.entity.shared.Person;
import kz.uco.tsadv.modules.recognition.dictionary.DicActionLikeType;
import com.haulmont.cuba.core.entity.StandardEntity;

@Table(name = "TSADV_PERSON_ACTION_LIKE")
@Entity(name = "tsadv$PersonActionLike")
public class PersonActionLike extends StandardEntity {
    private static final long serialVersionUID = 5822787881940050620L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ACTION_ID")
    protected PersonAction action;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "LIKE_TYPE_ID")
    protected DicActionLikeType likeType;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected Person personGroup;

    public void setAction(PersonAction action) {
        this.action = action;
    }

    public PersonAction getAction() {
        return action;
    }

    public void setLikeType(DicActionLikeType likeType) {
        this.likeType = likeType;
    }

    public DicActionLikeType getLikeType() {
        return likeType;
    }

    public void setPersonGroup(Person personGroup) {
        this.personGroup = personGroup;
    }

    public Person getPersonGroup() {
        return personGroup;
    }


}