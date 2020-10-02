package kz.uco.tsadv.modules.personal.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.modules.performance.model.Questionnaire;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.enums.CompetenceAssessmentStatus;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

@Table(name = "TSADV_PERSON_QUESTIONNAIRE")
@Entity(name = "tsadv$PersonQuestionnaire")
public class PersonQuestionnaire extends AbstractParentEntity {
    private static final long serialVersionUID = 446903610956315077L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "APPRAISE_ID")
    protected PersonGroupExt appraise;

    @OrderBy("question")
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "personQuestionnaire")
    protected List<PersonQuestionnaireAnswer> personQuestionnaireAnswer;

    @NotNull
    @Column(name = "STATUS", nullable = false)
    protected String status;

    @Column(name = "OVERALL_SCORE")
    protected Integer overallScore;

    @Column(name = "AVERAGE_SCORE")
    protected Double averageScore;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "APPRAISER_ID")
    protected PersonGroupExt appraiser;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "QUESTIONNAIRE_ID")
    protected Questionnaire questionnaire;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "APPRAISAL_DATE", nullable = false)
    protected Date appraisalDate;

    public void setPersonQuestionnaireAnswer(List<PersonQuestionnaireAnswer> personQuestionnaireAnswer) {
        this.personQuestionnaireAnswer = personQuestionnaireAnswer;
    }

    public List<PersonQuestionnaireAnswer> getPersonQuestionnaireAnswer() {
        return personQuestionnaireAnswer;
    }


    public void setStatus(CompetenceAssessmentStatus status) {
        this.status = status == null ? null : status.getId();
    }

    public CompetenceAssessmentStatus getStatus() {
        return status == null ? null : CompetenceAssessmentStatus.fromId(status);
    }

    public void setOverallScore(Integer overallScore) {
        this.overallScore = overallScore;
    }

    public Integer getOverallScore() {
        return overallScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }

    public Double getAverageScore() {
        return averageScore;
    }


    public void setAppraise(PersonGroupExt appraise) {
        this.appraise = appraise;
    }

    public PersonGroupExt getAppraise() {
        return appraise;
    }

    public void setAppraiser(PersonGroupExt appraiser) {
        this.appraiser = appraiser;
    }

    public PersonGroupExt getAppraiser() {
        return appraiser;
    }

    public void setQuestionnaire(Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
    }

    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }

    public void setAppraisalDate(Date appraisalDate) {
        this.appraisalDate = appraisalDate;
    }

    public Date getAppraisalDate() {
        return appraisalDate;
    }


}