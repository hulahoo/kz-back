package kz.uco.tsadv.modules.learning.model;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@NamePattern("%s - %s|positionGroup,course")
@Table(name = "TSADV_POSITION_COURSE")
@Entity(name = "tsadv_PositionCourse")
public class PositionCourse extends AbstractParentEntity {
    private static final long serialVersionUID = 386284251495410510L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "POSITION_GROUP_ID")
    protected PositionGroupExt positionGroup;

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

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public PositionGroupExt getPositionGroup() {
        return positionGroup;
    }

    public void setPositionGroup(PositionGroupExt positionGroup) {
        this.positionGroup = positionGroup;
    }
}