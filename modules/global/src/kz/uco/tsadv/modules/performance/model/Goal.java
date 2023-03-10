package kz.uco.tsadv.modules.performance.model;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.sys.AppContext;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.performance.dictionary.DicUOM;
import kz.uco.tsadv.modules.personal.dictionary.DicMeasureType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static kz.uco.base.common.Null.nullReplace;

@NamePattern("%s|goalLang")
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


    @Transient
    @MetaProperty(related = {"goalName", "goalNameLang2", "goalNameLang3"})
    public String getGoalLang() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String langOrder = AppContext.getProperty("base.abstractDictionary.langOrder");

        if (!isBaseLangsFullLoaded()) {
            return null;
        }

        if (langOrder != null) {
            List<String> langs = Arrays.asList(langOrder.split(";"));
            switch (langs.indexOf(language)) {
                case 0: {
                    return goalName;
                }
                case 1: {
                    return nullReplace(goalNameLang2, goalName);
                }
                case 2: {
                    return nullReplace(goalNameLang3, goalName);
                }
                default:
                    return goalName;
            }
        }

        return goalName;
    }

    private boolean isBaseLangsFullLoaded() {
        return PersistenceHelper.isLoaded(this, "goalName")
                && PersistenceHelper.isLoaded(this, "goalNameLang2")
                && PersistenceHelper.isLoaded(this, "goalNameLang3");
    }

    @Transient
    @MetaProperty(related = {"successCriteria", "successCriteriaLang2", "successCriteriaLang3"})
    public String getSuccessCriteriaLang() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String langOrder = AppContext.getProperty("base.abstractDictionary.langOrder");

        if (!isBaseSuccessCriteriaLangsFullLoaded()) {
            return null;
        }

        if (langOrder != null) {
            List<String> langs = Arrays.asList(langOrder.split(";"));
            switch (langs.indexOf(language)) {
                case 0: {
                    return successCriteria;
                }
                case 1: {
                    return nullReplace(successCriteriaLang2, successCriteria);
                }
                case 2: {
                    return nullReplace(successCriteriaLang3, successCriteria);
                }
                default:
                    return successCriteria;
            }
        }

        return successCriteria;
    }

    private boolean isBaseSuccessCriteriaLangsFullLoaded() {
        return PersistenceHelper.isLoaded(this, "successCriteria")
                && PersistenceHelper.isLoaded(this, "successCriteriaLang2")
                && PersistenceHelper.isLoaded(this, "successCriteriaLang3");
    }
}