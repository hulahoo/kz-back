package kz.uco.tsadv.modules.learning.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.modules.learning.dictionary.DicCourseFormat;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.modules.learning.model.CourseSectionAttempt;
import kz.uco.tsadv.modules.learning.model.CourseSectionObject;
import kz.uco.tsadv.modules.learning.model.CourseSectionSession;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|sectionName")
@Table(name = "TSADV_COURSE_SECTION")
@Entity(name = "tsadv$CourseSection")
public class CourseSection extends AbstractParentEntity {
    private static final long serialVersionUID = -9016243636592128455L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COURSE_ID")
    protected Course course;

    @NotNull
    @Column(name = "MANDATORY", nullable = false)
    protected Boolean mandatory = false;

    @Column(name = "SECTION_NAME", nullable = false)
    protected String sectionName;

    @Column(name = "ORDER_", nullable = false)
    protected Integer order;

    @OnDelete(DeletePolicy.CASCADE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SECTION_OBJECT_ID")
    protected CourseSectionObject sectionObject;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FORMAT_ID")
    protected DicCourseFormat format;

    @Lob
    @Column(name = "DESCRIPTION")
    protected String description;


    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "courseSection", cascade = CascadeType.PERSIST)
    protected List<CourseSectionSession> session;

    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "courseSection")
    protected List<CourseSectionAttempt> courseSectionAttempts;

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    public Boolean getMandatory() {
        return mandatory;
    }


    public void setCourseSectionAttempts(List<CourseSectionAttempt> courseSectionAttempts) {
        this.courseSectionAttempts = courseSectionAttempts;
    }

    public List<CourseSectionAttempt> getCourseSectionAttempts() {
        return courseSectionAttempts;
    }


    public void setSession(List<CourseSectionSession> session) {
        this.session = session;
    }

    public List<CourseSectionSession> getSession() {
        return session;
    }


    public void setSectionObject(CourseSectionObject sectionObject) {
        this.sectionObject = sectionObject;
    }

    public CourseSectionObject getSectionObject() {
        return sectionObject;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


    public DicCourseFormat getFormat() {
        return format;
    }

    public void setFormat(DicCourseFormat format) {
        this.format = format;
    }


    public void setCourse(Course course) {
        this.course = course;
    }

    public Course getCourse() {
        return course;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getOrder() {
        return order;
    }


}