package kz.uco.tsadv.modules.learning.model;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.learning.dictionary.DicLearningCenter;

import javax.persistence.*;
import java.util.Date;

@NamePattern("%s|name")
@Table(name = "TSADV_COURSE_SCHEDULE")
@Entity(name = "tsadv_CourseSchedule")
public class CourseSchedule extends AbstractParentEntity {
    private static final long serialVersionUID = -2148209599765916627L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COURSE_ID")
    protected Course course;

    @Column(name = "NAME")
    protected String name;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "START_DATE")
    protected Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_DATE")
    protected Date endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LEARNING_CENTER_ID")
    protected DicLearningCenter learningCenter;

    @Column(name = "ADDRESS")
    protected String address;

    @Column(name = "MAX_NUMBER_OF_PEOPLE")
    protected Integer maxNumberOfPeople;

    public Integer getMaxNumberOfPeople() {
        return maxNumberOfPeople;
    }

    public void setMaxNumberOfPeople(Integer maxNumberOfPeople) {
        this.maxNumberOfPeople = maxNumberOfPeople;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public DicLearningCenter getLearningCenter() {
        return learningCenter;
    }

    public void setLearningCenter(DicLearningCenter learningCenter) {
        this.learningCenter = learningCenter;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}