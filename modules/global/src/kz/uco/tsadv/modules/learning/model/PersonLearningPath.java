package kz.uco.tsadv.modules.learning.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;

@Table(name = "TSADV_PERSON_LEARNING_PATH")
@Entity(name = "tsadv$PersonLearningPath")
public class PersonLearningPath extends AbstractParentEntity {
    private static final long serialVersionUID = -6722109859267533906L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "LEARNING_PATH_ID")
    protected LearningPath learningPath;

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }


    public void setLearningPath(LearningPath learningPath) {
        this.learningPath = learningPath;
    }

    public LearningPath getLearningPath() {
        return learningPath;
    }


}