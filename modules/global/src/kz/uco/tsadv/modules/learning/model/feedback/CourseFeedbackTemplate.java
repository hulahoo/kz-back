package kz.uco.tsadv.modules.learning.model.feedback;

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
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

@Table(name = "TSADV_COURSE_FEEDBACK_TEMPLATE")
@Entity(name = "tsadv$CourseFeedbackTemplate")
public class CourseFeedbackTemplate extends AbstractParentEntity {
    private static final long serialVersionUID = -6038901842476226195L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FEEDBACK_TEMPLATE_ID")
    protected LearningFeedbackTemplate feedbackTemplate;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COURSE_ID")
    protected Course course;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "START_DATE", nullable = false)
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "END_DATE", nullable = false)
    protected Date endDate;

    public void setFeedbackTemplate(LearningFeedbackTemplate feedbackTemplate) {
        this.feedbackTemplate = feedbackTemplate;
    }

    public LearningFeedbackTemplate getFeedbackTemplate() {
        return feedbackTemplate;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Course getCourse() {
        return course;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }


}