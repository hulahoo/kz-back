package kz.uco.tsadv.modules.performance.model;

import kz.uco.tsadv.modules.performance.dictionary.DicOverallRating;
import kz.uco.tsadv.modules.performance.model.*;
import kz.uco.tsadv.modules.performance.model.AssessmentParticipant;
import kz.uco.tsadv.modules.personal.group.CompetenceGroup;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;

@Table(name = "TSADV_ASSESSMENT_COMPETENCE")
@Entity(name = "tsadv$AssessmentCompetence")
public class AssessmentCompetence extends AbstractParentEntity {
    private static final long serialVersionUID = -1487165126882883996L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PARTICIPANT_ASSESSMENT_ID")
    protected kz.uco.tsadv.modules.performance.model.AssessmentParticipant participantAssessment;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COMPETENCE_GROUP_ID")
    protected CompetenceGroup competenceGroup;

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


    public void setCompetenceGroup(CompetenceGroup competenceGroup) {
        this.competenceGroup = competenceGroup;
    }

    public CompetenceGroup getCompetenceGroup() {
        return competenceGroup;
    }


    public void setParticipantAssessment(kz.uco.tsadv.modules.performance.model.AssessmentParticipant participantAssessment) {
        this.participantAssessment = participantAssessment;
    }

    public AssessmentParticipant getParticipantAssessment() {
        return participantAssessment;
    }


}