package kz.uco.tsadv.modules.learning.model;

import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.modules.learning.model.LearningPath;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;

@Table(name = "TSADV_LEARNING_PATH_COURSE")
@Entity(name = "tsadv$LearningPathCourse")
public class LearningPathCourse extends AbstractParentEntity {
    private static final long serialVersionUID = 8198059660507588993L;

    @Column(name = "ORDER_NUMBER", nullable = false)
    protected Integer orderNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COURSE_ID")
    protected Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LEARNING_PATH_ID")
    protected LearningPath learningPath;

    public void setLearningPath(LearningPath learningPath) {
        this.learningPath = learningPath;
    }

    public LearningPath getLearningPath() {
        return learningPath;
    }


    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Course getCourse() {
        return course;
    }


}