package kz.uco.tsadv.modules.learning.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "TSADV_LEARNING_PATH_REVIEW")
@Entity(name = "tsadv$LearningPathReview")
public class LearningPathReview extends AbstractParentEntity {
    private static final long serialVersionUID = -4887638570363262708L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "LEARNING_PATH_ID")
    protected LearningPath learningPath;

    @Column(name = "RATE")
    protected Double rate;

    @NotNull
    @Column(name = "TEXT", nullable = false, length = 500)
    protected String text;

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

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Double getRate() {
        return rate;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }


}