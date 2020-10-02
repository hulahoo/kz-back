package kz.uco.tsadv.entity.dbview;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.global.DesignSupport;
import kz.uco.tsadv.global.enums.LearningHistoryType;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import java.util.Date;

/**
 * Вся история обучения лица
 */
@NamePattern("%s - %tF|courseName,startDate")
@DesignSupport("{'dbView':true,'generateDdl':false}")
@Table(name = "TSADV_PERSON_ALL_LEARNING_HISTORY_V")
@Entity(name = "tsadv$PersonAllLearningHistory")
public class PersonAllLearningHistory extends StandardEntity {
    private static final long serialVersionUID = -9115520662421676995L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COURSE_ID")
    protected Course course;

    @Column(name = "COURSE_NAME")
    protected String courseName;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE")
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE")
    protected Date endDate;

    @Column(name = "TYPE_")
    protected String type;


    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
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

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setType(LearningHistoryType type) {
        this.type = type == null ? null : type.getId();
    }

    public LearningHistoryType getType() {
        return type == null ? null : LearningHistoryType.fromId(type);
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Course getCourse() {
        return course;
    }

}