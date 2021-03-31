package kz.uco.tsadv.modules.learning.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.learning.dictionary.DicTestType;
import kz.uco.tsadv.modules.learning.enums.TestSectionOrder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Тест (тестирование)
 */
@NamePattern("%s|name")
@Table(name = "TSADV_TEST")
@Entity(name = "tsadv$Test")
public class Test extends AbstractParentEntity {
    private static final long serialVersionUID = -2921606785960139564L;

    @Column(name = "NAME", nullable = false, length = 120)
    protected String name;

    @Column(name = "DESCRIPTION", length = 2000)
    protected String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TYPE_ID")
    @NotNull
    protected DicTestType type;

    @Column(name = "ACTIVE", nullable = false)
    protected Boolean active = false;

    @Column(name = "MAX_ATTEMPT", nullable = false)
    protected Integer maxAttempt;

    @Column(name = "DAYS_BETWEEN_ATTEMPTS", nullable = false)
    protected Integer daysBetweenAttempts;

    @Column(name = "TIMER", nullable = false)
    protected Integer timer;

    @Column(name = "SECTION_ORDER", nullable = false)
    protected Integer sectionOrder;

    @Column(name = "INSTRUCTION", length = 4000)
    protected String instruction;

    @Column(name = "TARGET_SCORE")
    protected Integer targetScore;

    @Column(name = "SHOW_RESULTS", nullable = false)
    protected Boolean showResults = false;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "test")
    protected List<TestSection> sections;

    @Column(name = "SHOW_SECTION_NEW_PAGE")
    protected Boolean showSectionNewPage;

    @Column(name = "QUESTION_PER_PAGE")
    protected Integer questionPerPage;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "test")
    protected List<JobTest> jobTest;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "test")
    protected List<PositionTest> positionTest;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "clear"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COURSE_ID")
    protected Course course;

    @OneToMany(mappedBy = "test")
    private List<CourseSectionObject> courseSectionObjects;

    public List<CourseSectionObject> getCourseSectionObjects() {
        return courseSectionObjects;
    }

    public void setCourseSectionObjects(List<CourseSectionObject> courseSectionObjects) {
        this.courseSectionObjects = courseSectionObjects;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Course getCourse() {
        return course;
    }

    public void setJobTest(List<JobTest> jobTest) {
        this.jobTest = jobTest;
    }

    public List<JobTest> getJobTest() {
        return jobTest;
    }

    public void setPositionTest(List<PositionTest> positionTest) {
        this.positionTest = positionTest;
    }

    public List<PositionTest> getPositionTest() {
        return positionTest;
    }

    public List<TestSection> getSections() {
        return sections;
    }

    public void setSections(List<TestSection> sections) {
        this.sections = sections;
    }

    public void setQuestionPerPage(Integer questionPerPage) {
        this.questionPerPage = questionPerPage;
    }

    public Integer getQuestionPerPage() {
        return questionPerPage;
    }

    public void setShowSectionNewPage(Boolean showSectionNewPage) {
        this.showSectionNewPage = showSectionNewPage;
    }

    public Boolean getShowSectionNewPage() {
        return showSectionNewPage;
    }


    public TestSectionOrder getSectionOrder() {
        return sectionOrder == null ? null : TestSectionOrder.fromId(sectionOrder);
    }

    public void setSectionOrder(TestSectionOrder sectionOrder) {
        this.sectionOrder = sectionOrder == null ? null : sectionOrder.getId();
    }

    public void setTimer(Integer timer) {
        this.timer = timer;
    }

    public Integer getTimer() {
        return timer;
    }

    public DicTestType getType() {
        return type;
    }

    public void setType(DicTestType type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getActive() {
        return active;
    }

    public void setMaxAttempt(Integer maxAttempt) {
        this.maxAttempt = maxAttempt;
    }

    public Integer getMaxAttempt() {
        return maxAttempt;
    }

    public void setDaysBetweenAttempts(Integer daysBetweenAttempts) {
        this.daysBetweenAttempts = daysBetweenAttempts;
    }

    public Integer getDaysBetweenAttempts() {
        return daysBetweenAttempts;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setTargetScore(Integer targetScore) {
        this.targetScore = targetScore;
    }

    public Integer getTargetScore() {
        return targetScore;
    }

    public void setShowResults(Boolean showResults) {
        this.showResults = showResults;
    }

    public Boolean getShowResults() {
        return showResults;
    }

}