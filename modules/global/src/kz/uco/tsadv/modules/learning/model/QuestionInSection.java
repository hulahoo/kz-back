package kz.uco.tsadv.modules.learning.model;

import kz.uco.tsadv.modules.learning.model.Question;
import kz.uco.tsadv.modules.learning.model.TestSection;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;

@Table(name = "TSADV_QUESTION_IN_SECTION")
@Entity(name = "tsadv$QuestionInSection")
public class QuestionInSection extends AbstractParentEntity {
    private static final long serialVersionUID = 6545869666482799477L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TEST_SECTION_ID")
    protected TestSection testSection;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "QUESTION_ID")
    protected Question question;

    public void setTestSection(TestSection testSection) {
        this.testSection = testSection;
    }

    public TestSection getTestSection() {
        return testSection;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Question getQuestion() {
        return question;
    }


}