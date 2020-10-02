package kz.uco.tsadv.modules.performance.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.performance.dictionary.DicNineBoxLevel;
import kz.uco.tsadv.modules.performance.dictionary.DicParticipantType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import java.util.List;

@Table(name = "TSADV_ASSESSMENT_PARTICIPANT")
@Entity(name = "tsadv$AssessmentParticipant")
public class AssessmentParticipant extends AbstractParentEntity {
    private static final long serialVersionUID = 1177986690563263605L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ASSESSMENT_ID")
    protected Assessment assessment;

    @Lookup(type = LookupType.DROPDOWN, actions = {"lookup"})
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PARTICIPANT_TYPE_ID")
    protected DicParticipantType participantType;


    @Lookup(type = LookupType.SCREEN, actions = {"lookup"})
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PARTICIPANT_PERSON_GROUP_ID")
    protected PersonGroupExt participantPersonGroup;

    @Column(name = "GOAL_RATING", nullable = false)
    protected Double goalRating;

    @Column(name = "COMPETENCE_RATING", nullable = false)
    protected Double competenceRating;

    @Column(name = "OVERALL_RATING", nullable = false)
    protected Double overallRating;

    @Lookup(type = LookupType.DROPDOWN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERFORMANCE_ID")
    protected DicNineBoxLevel performance;

    @Lookup(type = LookupType.DROPDOWN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POTENTIAL_ID")
    protected DicNineBoxLevel potential;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RISK_OF_LOSS_ID")
    protected DicNineBoxLevel riskOfLoss;

    @Lookup(type = LookupType.DROPDOWN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IMPACT_OF_LOSS_ID")
    protected DicNineBoxLevel impactOfLoss;

    @OrderBy("competenceGroup")
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "participantAssessment")
    protected List<AssessmentCompetence> assessmentCompetence;
    @OrderBy("goal")
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "participantAssessment")
    protected List<AssessmentGoal> assessmentGoal;

    @Column(name = "OVERALL_COMMENT", length = 2000)
    protected String overallComment;

    public void setOverallComment(String overallComment) {
        this.overallComment = overallComment;
    }

    public String getOverallComment() {
        return overallComment;
    }


    public DicParticipantType getParticipantType() {
        return participantType;
    }

    public void setParticipantType(DicParticipantType participantType) {
        this.participantType = participantType;
    }


    public DicNineBoxLevel getPerformance() {
        return performance;
    }

    public void setPerformance(DicNineBoxLevel performance) {
        this.performance = performance;
    }


    public DicNineBoxLevel getPotential() {
        return potential;
    }

    public void setPotential(DicNineBoxLevel potential) {
        this.potential = potential;
    }


    public DicNineBoxLevel getRiskOfLoss() {
        return riskOfLoss;
    }

    public void setRiskOfLoss(DicNineBoxLevel riskOfLoss) {
        this.riskOfLoss = riskOfLoss;
    }


    public DicNineBoxLevel getImpactOfLoss() {
        return impactOfLoss;
    }

    public void setImpactOfLoss(DicNineBoxLevel impactOfLoss) {
        this.impactOfLoss = impactOfLoss;
    }

    public Double getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(Double overallRating) {
        this.overallRating = overallRating;
    }


    public void setParticipantPersonGroup(PersonGroupExt participantPersonGroup) {
        this.participantPersonGroup = participantPersonGroup;
    }

    public PersonGroupExt getParticipantPersonGroup() {
        return participantPersonGroup;
    }


    public void setAssessmentGoal(List<AssessmentGoal> assessmentGoal) {
        this.assessmentGoal = assessmentGoal;
    }

    public List<AssessmentGoal> getAssessmentGoal() {
        return assessmentGoal;
    }


    public Double getGoalRating() {
        return goalRating;
    }

    public void setGoalRating(Double goalRating) {
        this.goalRating = goalRating;
    }


    public Double getCompetenceRating() {
        return competenceRating;
    }

    public void setCompetenceRating(Double competenceRating) {
        this.competenceRating = competenceRating;
    }


    public void setAssessmentCompetence(List<AssessmentCompetence> assessmentCompetence) {
        this.assessmentCompetence = assessmentCompetence;
    }

    public List<AssessmentCompetence> getAssessmentCompetence() {
        return assessmentCompetence;
    }


    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }

    public Assessment getAssessment() {
        return assessment;
    }


}