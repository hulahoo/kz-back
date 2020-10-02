package kz.uco.tsadv.modules.learning.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Учащийся
 */
@NamePattern("%s - %s|group,personGroup")
@Table(name = "TSADV_LEARNER", uniqueConstraints = {
        @UniqueConstraint(name = "IDX_TSADV_LEARNER_UNQ", columnNames = {"GROUP_ID", "PERSON_GROUP_ID"})
})
@Entity(name = "tsadv$Learner")
public class Learner extends StandardEntity {
    private static final long serialVersionUID = -8936601918303021232L;

    // todo перенести ВСЮ портянку по заявкам на обучение (LearningRequest) с проекта KNU и добавить сюда ссылку на заявку

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "GROUP_ID")
    protected LearnerGroup group;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    public void setGroup(LearnerGroup group) {
        this.group = group;
    }

    public LearnerGroup getGroup() {
        return group;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

}