package kz.uco.tsadv.modules.recognition;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import com.haulmont.cuba.core.entity.StandardEntity;

@Table(name = "TSADV_PERSON_POINT")
@Entity(name = "tsadv$PersonPoint")
public class PersonPoint extends StandardEntity {
    private static final long serialVersionUID = 4974542936677032989L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID", unique = true)
    protected PersonGroupExt personGroup;

    @NotNull
    @Column(name = "POINTS", nullable = false)
    protected Long points;

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public Long getPoints() {
        return points;
    }


}