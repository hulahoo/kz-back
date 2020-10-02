package kz.uco.tsadv.modules.performance.model;

import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import kz.uco.tsadv.modules.performance.dictionary.DicOverallRating;
import kz.uco.tsadv.modules.performance.model.*;
import kz.uco.tsadv.modules.performance.model.AssessmentParticipant;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;

@Table(name = "TSADV_ASSESSMENT_GOAL")
@Entity(name = "tsadv$AssessmentGoal")
public class AssessmentGoal extends AbstractParentEntity {
    private static final long serialVersionUID = 6181161195073475461L;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup"})
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PARTICIPANT_ASSESSMENT_ID")
    protected kz.uco.tsadv.modules.performance.model.AssessmentParticipant participantAssessment;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup"})
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "GOAL_ID")
    protected Goal goal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "OVERALL_RATING_ID")
    protected DicOverallRating overallRating;
    @Column(name = "COMMENT_", length = 2000)
    protected String comment;

    @Column(name = "WEIGHT")
    protected Double weight;

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getWeight() {
        return weight;
    }


    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }


    public DicOverallRating getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(DicOverallRating overallRating) {
        this.overallRating = overallRating;
    }


    public void setParticipantAssessment(kz.uco.tsadv.modules.performance.model.AssessmentParticipant participantAssessment) {
        this.participantAssessment = participantAssessment;
    }

    public AssessmentParticipant getParticipantAssessment() {
        return participantAssessment;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public Goal getGoal() {
        return goal;
    }


}