package kz.uco.tsadv.modules.personal.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Table(name = "TSADV_LEARNING_RESULT")
@Entity(name = "tsadv_LearningResult")
public class LearningResult extends AbstractParentEntity {
    private static final long serialVersionUID = -9032775256708090170L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "GRANTEES_AGREEMENT_ID")
    protected GranteesAgreement granteesAgreement;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @NotNull
    @Column(name = "STUDY_YEAR", nullable = false)
    protected Integer studyYear;

    @NotNull
    @Column(name = "SEMESTER", nullable = false)
    protected String semester;

    @Column(name = "AVERAGE_SCORE")
    protected Double averageScore;

    @NotNull
    @Column(name = "SCHOLARSHIP", nullable = false)
    protected BigDecimal scholarship;

    public BigDecimal getScholarship() {
        return scholarship;
    }

    public void setScholarship(BigDecimal scholarship) {
        this.scholarship = scholarship;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public Integer getStudyYear() {
        return studyYear;
    }

    public void setStudyYear(Integer studyYear) {
        this.studyYear = studyYear;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public GranteesAgreement getGranteesAgreement() {
        return granteesAgreement;
    }

    public void setGranteesAgreement(GranteesAgreement granteesAgreement) {
        this.granteesAgreement = granteesAgreement;
    }
}