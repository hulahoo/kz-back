package kz.uco.tsadv.modules.learning.model;

import kz.uco.tsadv.modules.learning.model.*;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.modules.personal.group.CompetenceGroup;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.model.ScaleLevel;

import javax.persistence.*;

@Table(name = "TSADV_COURSE_COMPETENCE")
@Entity(name = "tsadv$CourseCompetence")
public class CourseCompetence extends AbstractParentEntity {
    private static final long serialVersionUID = -8040473834155543341L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COURSE_ID")
    protected kz.uco.tsadv.modules.learning.model.Course course;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COMPETENCE_GROUP_ID")
    protected CompetenceGroup competenceGroup;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SCALE_LEVEL_ID")
    protected ScaleLevel scaleLevel;

    public void setScaleLevel(ScaleLevel scaleLevel) {
        this.scaleLevel = scaleLevel;
    }

    public ScaleLevel getScaleLevel() {
        return scaleLevel;
    }


    public CompetenceGroup getCompetenceGroup() {
        return competenceGroup;
    }

    public void setCompetenceGroup(CompetenceGroup competenceGroup) {
        this.competenceGroup = competenceGroup;
    }


    public void setCourse(kz.uco.tsadv.modules.learning.model.Course course) {
        this.course = course;
    }

    public Course getCourse() {
        return course;
    }


}