package kz.uco.tsadv.modules.performance.model;

import kz.uco.tsadv.modules.learning.model.Question;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;

@Table(name = "TSADV_ASSESSMENT_PERSON_ANSWER")
@Entity(name = "tsadv$AssessmentPersonAnswer")
public class AssessmentPersonAnswer extends AbstractParentEntity {
    private static final long serialVersionUID = 882055981953019909L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_ID")
    protected PersonGroupExt person;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "QUESTION_ID")
    protected Question question;

    @Lob
    @Column(name = "ANSWER")
    protected String answer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ASSESSMENT_ID")
    protected Assessment assessment;

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }

    public Assessment getAssessment() {
        return assessment;
    }


    public void setPerson(PersonGroupExt person) {
        this.person = person;
    }

    public PersonGroupExt getPerson() {
        return person;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Question getQuestion() {
        return question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }


}