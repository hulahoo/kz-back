package kz.uco.tsadv.global.entity;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

@MetaClass(name = "tsadv$CompetenceChartEntity")
public class CompetenceChartEntity extends BaseUuidEntity {
    private static final long serialVersionUID = -4281852406590769095L;

    @MetaProperty
    protected String competenceName;

    @MetaProperty
    protected String scaleName;

    @MetaProperty
    protected Long assignmentScaleLevel;

    @MetaProperty
    protected Long positionScaleLevel;

    @MetaProperty
    protected String assignmentScaleLevelDesc;

    @MetaProperty
    protected String positionScaleLevelDesc;

    @MetaProperty
    protected String assignmentScaleLevelName;

    @MetaProperty
    protected String positionScaleLevelName;

    public void setCompetenceName(String competenceName) {
        this.competenceName = competenceName;
    }

    public String getCompetenceName() {
        return competenceName;
    }

    public void setScaleName(String scaleName) {
        this.scaleName = scaleName;
    }

    public String getScaleName() {
        return scaleName;
    }

    public void setAssignmentScaleLevel(Long assignmentScaleLevel) {
        this.assignmentScaleLevel = assignmentScaleLevel;
    }

    public Long getAssignmentScaleLevel() {
        return assignmentScaleLevel;
    }

    public void setPositionScaleLevel(Long positionScaleLevel) {
        this.positionScaleLevel = positionScaleLevel;
    }

    public Long getPositionScaleLevel() {
        return positionScaleLevel;
    }

    public String getAssignmentScaleLevelDesc() {
        return assignmentScaleLevelDesc;
    }

    public void setAssignmentScaleLevelDesc(String assignmentScaleLevelDesc) {
        this.assignmentScaleLevelDesc = assignmentScaleLevelDesc;
    }

    public String getPositionScaleLevelDesc() {
        return positionScaleLevelDesc;
    }

    public void setPositionScaleLevelDesc(String positionScaleLevelDesc) {
        this.positionScaleLevelDesc = positionScaleLevelDesc;
    }

    public String getAssignmentScaleLevelName() {
        return assignmentScaleLevelName;
    }

    public void setAssignmentScaleLevelName(String assignmentScaleLevelName) {
        this.assignmentScaleLevelName = assignmentScaleLevelName;
    }

    public String getPositionScaleLevelName() {
        return positionScaleLevelName;
    }

    public void setPositionScaleLevelName(String positionScaleLevelName) {
        this.positionScaleLevelName = positionScaleLevelName;
    }
}