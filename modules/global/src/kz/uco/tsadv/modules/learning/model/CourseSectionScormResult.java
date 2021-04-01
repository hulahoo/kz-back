package kz.uco.tsadv.modules.learning.model;

import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "TSADV_COURSE_SECTION_SCORM_RESULT")
@Entity(name = "tsadv_CourseSectionScormResult")
public class CourseSectionScormResult extends StandardEntity {
    private static final long serialVersionUID = -3851458331292547011L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COURSE_SECTION_ATTEMPT_ID")
    protected CourseSectionAttempt courseSectionAttempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUESTION_ID")
    protected ScormQuestionMapping question;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ANSWER_TIME_STAMP")
    protected Date answerTimeStamp;

    @Lob
    @Column(name = "ANSWER")
    protected String answer;

    @Column(name = "IS_CORRECT")
    protected Boolean isCorrect = false;

    @Column(name = "SCORE")
    protected BigDecimal score;

    @Column(name = "MAX_SCORE")
    protected BigDecimal maxScore;

    @Column(name = "MIN_SCORE")
    protected BigDecimal minScore;

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setMaxScore(BigDecimal maxScore) {
        this.maxScore = maxScore;
    }

    public BigDecimal getMaxScore() {
        return maxScore;
    }

    public void setMinScore(BigDecimal minScore) {
        this.minScore = minScore;
    }

    public BigDecimal getMinScore() {
        return minScore;
    }

    public ScormQuestionMapping getQuestion() {
        return question;
    }

    public void setQuestion(ScormQuestionMapping question) {
        this.question = question;
    }

    public Date getAnswerTimeStamp() {
        return answerTimeStamp;
    }

    public void setAnswerTimeStamp(Date answerTimeStamp) {
        this.answerTimeStamp = answerTimeStamp;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public CourseSectionAttempt getCourseSectionAttempt() {
        return courseSectionAttempt;
    }

    public void setCourseSectionAttempt(CourseSectionAttempt courseSectionAttempt) {
        this.courseSectionAttempt = courseSectionAttempt;
    }
}