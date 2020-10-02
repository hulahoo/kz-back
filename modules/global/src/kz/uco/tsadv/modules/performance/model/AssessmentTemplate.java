package kz.uco.tsadv.modules.performance.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.modules.performance.model.*;
import kz.uco.tsadv.modules.performance.model.CompetenceTemplate;
import kz.uco.tsadv.modules.performance.model.PerformancePlan;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.learning.model.Test;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Table(name = "TSADV_ASSESSMENT_TEMPLATE")
@Entity(name = "tsadv$AssessmentTemplate")
@NamePattern("%s|assessmentTemplateName")
public class AssessmentTemplate extends AbstractParentEntity {
    private static final long serialVersionUID = -1138111754985737967L;

    @NotNull
    @Column(name = "ASSESSMENT_TEMPLATE_NAME", nullable = false)
    protected String assessmentTemplateName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERFORMANCE_PLAN_ID")
    protected PerformancePlan performancePlan;


    @Column(name = "SELF_ASSESSMENT", nullable = false)
    protected Boolean selfAssessment = false;

    @Column(name = "MANAGER_ASSESSMENT", nullable = false)
    protected Boolean managerAssessment = false;

    @Column(name = "PARTICIPANT_ASSESSMENT", nullable = false)
    protected Boolean participantAssessment = false;


    @Lookup(type = LookupType.SCREEN, actions = {"lookup"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPETENCE_TEMPLATE_ID")
    protected CompetenceTemplate competenceTemplate;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "template")
    protected List<Assessment> assessment;

    @Column(name = "GOAL_WEIGHT")
    protected Double goalWeight;

    @Column(name = "COMPETENCE_WEIGHT")
    protected Double competenceWeight;

    @Lookup(type = LookupType.SCREEN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MANAGER_TEST_ID")
    protected Test managerTest;

    @Lookup(type = LookupType.SCREEN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WORKER_TEST_ID")
    protected Test workerTest;

    @Lookup(type = LookupType.SCREEN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARTICIPANT_TEST_ID")
    protected Test participantTest;
    public CompetenceTemplate getCompetenceTemplate() {
        return competenceTemplate;
    }

    public void setCompetenceTemplate(CompetenceTemplate competenceTemplate) {
        this.competenceTemplate = competenceTemplate;
    }



    public void setManagerTest(Test managerTest) {
        this.managerTest = managerTest;
    }

    public Test getManagerTest() {
        return managerTest;
    }

    public void setWorkerTest(Test workerTest) {
        this.workerTest = workerTest;
    }

    public Test getWorkerTest() {
        return workerTest;
    }

    public void setParticipantTest(Test participantTest) {
        this.participantTest = participantTest;
    }

    public Test getParticipantTest() {
        return participantTest;
    }


    public void setGoalWeight(Double goalWeight) {
        this.goalWeight = goalWeight;
    }

    public Double getGoalWeight() {
        return goalWeight;
    }

    public void setCompetenceWeight(Double competenceWeight) {
        this.competenceWeight = competenceWeight;
    }

    public Double getCompetenceWeight() {
        return competenceWeight;
    }


    public void setAssessmentTemplateName(String assessmentTemplateName) {
        this.assessmentTemplateName = assessmentTemplateName;
    }

    public String getAssessmentTemplateName() {
        return assessmentTemplateName;
    }


    public void setAssessment(List<Assessment> assessment) {
        this.assessment = assessment;
    }

    public List<Assessment> getAssessment() {
        return assessment;
    }





    public void setPerformancePlan(PerformancePlan performancePlan) {
        this.performancePlan = performancePlan;
    }

    public PerformancePlan getPerformancePlan() {
        return performancePlan;
    }


    public void setSelfAssessment(Boolean selfAssessment) {
        this.selfAssessment = selfAssessment;
    }

    public Boolean getSelfAssessment() {
        return selfAssessment;
    }

    public void setManagerAssessment(Boolean managerAssessment) {
        this.managerAssessment = managerAssessment;
    }

    public Boolean getManagerAssessment() {
        return managerAssessment;
    }

    public void setParticipantAssessment(Boolean participantAssessment) {
        this.participantAssessment = participantAssessment;
    }

    public Boolean getParticipantAssessment() {
        return participantAssessment;
    }


}