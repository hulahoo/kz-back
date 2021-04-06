package kz.uco.tsadv.modules.performance.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.performance.dictionary.DicUOM;
import kz.uco.tsadv.modules.personal.dictionary.DicMeasureType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@NamePattern("%s|goalName")
@Table(name = "TSADV_GOAL")
@Entity(name = "tsadv$Goal")
public class Goal extends AbstractParentEntity {
    private static final long serialVersionUID = -7336911560887095981L;

    @NotNull
    @Column(name = "GOAL_NAME", nullable = false, length = 1000)
    protected String goalName;

    @Column(name = "SUCCESS_CRITERIA", length = 100)
    protected String successCriteria;

    @Lookup(type = LookupType.SCREEN, actions = "lookup")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "LIBRARY_ID")
    protected GoalLibrary library;

    @Lookup(type = LookupType.DROPDOWN, actions = {})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEASURE_TYPE_ID")
    protected DicMeasureType measureType;

    @Lookup(type = LookupType.DROPDOWN, actions = {})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UOM_ID")

    // todo - заменить DicUOM на общий справочник kz.uco.tsadv.global.dictionary.DicUnitOfMeasure
    protected DicUOM uom;


    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE")
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE")
    protected Date endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_GOAL_ID")
    protected Goal parentGoal;

    @Column(name = "GOAL_NAME_LANG2")
    protected String goalNameLang2;

    @Column(name = "GOAL_NAME_LANG3")
    protected String goalNameLang3;

    @Column(name = "SUCCESS_CRITERIA_LANG2")
    protected String successCriteriaLang2;

    @Column(name = "SUCCESS_CRITERIA_LANG3")
    protected String successCriteriaLang3;

    public String getSuccessCriteriaLang3() {
        return successCriteriaLang3;
    }

    public void setSuccessCriteriaLang3(String successCriteriaLang3) {
        this.successCriteriaLang3 = successCriteriaLang3;
    }

    public String getSuccessCriteriaLang2() {
        return successCriteriaLang2;
    }

    public void setSuccessCriteriaLang2(String successCriteriaLang2) {
        this.successCriteriaLang2 = successCriteriaLang2;
    }

    public String getGoalNameLang3() {
        return goalNameLang3;
    }

    public void setGoalNameLang3(String goalNameLang3) {
        this.goalNameLang3 = goalNameLang3;
    }

    public String getGoalNameLang2() {
        return goalNameLang2;
    }

    public void setGoalNameLang2(String goalNameLang2) {
        this.goalNameLang2 = goalNameLang2;
    }

    public Goal getParentGoal() {
        return parentGoal;
    }

    public void setParentGoal(Goal parentGoal) {
        this.parentGoal = parentGoal;
    }

    public GoalLibrary getLibrary() {
        return library;
    }

    public void setLibrary(GoalLibrary library) {
        this.library = library;
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





    public void setUom(DicUOM uom) {
        this.uom = uom;
    }

    public DicUOM getUom() {
        return uom;
    }


    public void setMeasureType(DicMeasureType measureType) {
        this.measureType = measureType;
    }

    public DicMeasureType getMeasureType() {
        return measureType;
    }


    public void setSuccessCriteria(String successCriteria) {
        this.successCriteria = successCriteria;
    }

    public String getSuccessCriteria() {
        return successCriteria;
    }


    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public String getGoalName() {
        return goalName;
    }


}