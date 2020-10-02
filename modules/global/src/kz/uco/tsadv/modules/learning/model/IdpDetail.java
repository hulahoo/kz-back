package kz.uco.tsadv.modules.learning.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.modules.learning.dictionary.DicEducationType;
import kz.uco.tsadv.modules.personal.group.CompetenceGroup;
import kz.uco.tsadv.modules.personal.model.ScaleLevel;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

@Table(name = "TSADV_IDP_DETAIL")
@Entity(name = "tsadv$IdpDetail")
public class IdpDetail extends AbstractParentEntity {
    private static final long serialVersionUID = -8950500689835536617L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDP_ID")
    protected IndividualDevelopmentPlan idp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPETENCE_ID")
    protected CompetenceGroup competence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SCALE_LEVEL_ID")
    protected ScaleLevel scaleLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COURSE_ID")
    protected Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EDUCATION_TYPE_ID")
    protected DicEducationType educationType;

    @Lob
    @Column(name = "DESCRIPTION")
    protected String description;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "TARGET_DATE", nullable = false)
    protected Date targetDate;

    @NotNull
    @Column(name = "DONE", nullable = false)
    protected Boolean done = false;

    @Lob
    @Column(name = "COMMENT_")
    protected String comment;

    @Column(name = "REASON")
    protected String reason;

    public void setIdp(IndividualDevelopmentPlan idp) {
        this.idp = idp;
    }

    public IndividualDevelopmentPlan getIdp() {
        return idp;
    }

    public void setCompetence(CompetenceGroup competence) {
        this.competence = competence;
    }

    public CompetenceGroup getCompetence() {
        return competence;
    }

    public void setScaleLevel(ScaleLevel scaleLevel) {
        this.scaleLevel = scaleLevel;
    }

    public ScaleLevel getScaleLevel() {
        return scaleLevel;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Course getCourse() {
        return course;
    }

    public void setEducationType(DicEducationType educationType) {
        this.educationType = educationType;
    }

    public DicEducationType getEducationType() {
        return educationType;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setTargetDate(Date targetDate) {
        this.targetDate = targetDate;
    }

    public Date getTargetDate() {
        return targetDate;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public Boolean getDone() {
        return done;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }


}