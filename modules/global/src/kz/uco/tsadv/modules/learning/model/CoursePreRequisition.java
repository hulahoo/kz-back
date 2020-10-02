package kz.uco.tsadv.modules.learning.model;

import kz.uco.tsadv.modules.learning.model.*;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;

@Table(name = "TSADV_COURSE_PRE_REQUISITION")
@Entity(name = "tsadv$CoursePreRequisition")
public class CoursePreRequisition extends AbstractParentEntity {
    private static final long serialVersionUID = 1065529823143490073L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COURSE_ID")
    protected kz.uco.tsadv.modules.learning.model.Course course;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "REQUISITION_COURSE_ID")
    protected kz.uco.tsadv.modules.learning.model.Course requisitionCourse;

    public void setRequisitionCourse(kz.uco.tsadv.modules.learning.model.Course requisitionCourse) {
        this.requisitionCourse = requisitionCourse;
    }

    public kz.uco.tsadv.modules.learning.model.Course getRequisitionCourse() {
        return requisitionCourse;
    }


    public void setCourse(kz.uco.tsadv.modules.learning.model.Course course) {
        this.course = course;
    }

    public Course getCourse() {
        return course;
    }


}