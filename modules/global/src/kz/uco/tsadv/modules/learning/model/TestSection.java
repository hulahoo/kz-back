package kz.uco.tsadv.modules.learning.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.modules.learning.enums.TestSectionOrder;
import kz.uco.tsadv.modules.learning.model.*;
import kz.uco.tsadv.modules.learning.model.QuestionBank;
import kz.uco.tsadv.modules.learning.model.QuestionInSection;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;
import java.util.List;
import javax.validation.constraints.NotNull;

@Table(name = "TSADV_TEST_SECTION")
@Entity(name = "tsadv$TestSection")
public class TestSection extends AbstractParentEntity {
    private static final long serialVersionUID = 5219670311966457983L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TEST_ID")
    protected Test test;

    @NotNull
    @Column(name = "DYNAMIC_LOAD", nullable = false)
    protected Boolean dynamicLoad = false;

    @Column(name = "GENERATE_COUNT")
    protected Integer generateCount;

    @Column(name = "SECTION_NAME", nullable = false)
    protected String sectionName;

    @Column(name = "QUESTION_ORDER", nullable = false)
    protected Integer questionOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "QUESTION_BANK_ID")
    protected QuestionBank questionBank;

    @Column(name = "QUESTION_PER_PAGE", nullable = false)
    protected Integer questionPerPage;

    @Column(name = "ANSWER_ORDER", nullable = false)
    protected Integer answerOrder;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "testSection")
    protected List<QuestionInSection> questions;

    @Column(name = "POINTS_PER_QUESTION")
    protected Integer pointsPerQuestion;

    public void setPointsPerQuestion(Integer pointsPerQuestion) {
        this.pointsPerQuestion = pointsPerQuestion;
    }

    public Integer getPointsPerQuestion() {
        return pointsPerQuestion;
    }


    public void setDynamicLoad(Boolean dynamicLoad) {
        this.dynamicLoad = dynamicLoad;
    }

    public Boolean getDynamicLoad() {
        return dynamicLoad;
    }

    public void setGenerateCount(Integer generateCount) {
        this.generateCount = generateCount;
    }

    public Integer getGenerateCount() {
        return generateCount;
    }


    public List<QuestionInSection> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionInSection> questions) {
        this.questions = questions;
    }


    public TestSectionOrder getAnswerOrder() {
        return answerOrder == null ? null : TestSectionOrder.fromId(answerOrder);
    }

    public void setAnswerOrder(TestSectionOrder answerOrder) {
        this.answerOrder = answerOrder == null ? null : answerOrder.getId();
    }


    public TestSectionOrder getQuestionOrder() {
        return questionOrder == null ? null : TestSectionOrder.fromId(questionOrder);
    }

    public void setQuestionOrder(TestSectionOrder questionOrder) {
        this.questionOrder = questionOrder == null ? null : questionOrder.getId();
    }



    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getSectionName() {
        return sectionName;
    }


    public void setTest(Test test) {
        this.test = test;
    }

    public Test getTest() {
        return test;
    }

    public void setQuestionBank(QuestionBank questionBank) {
        this.questionBank = questionBank;
    }

    public QuestionBank getQuestionBank() {
        return questionBank;
    }

    public void setQuestionPerPage(Integer questionPerPage) {
        this.questionPerPage = questionPerPage;
    }

    public Integer getQuestionPerPage() {
        return questionPerPage;
    }


}