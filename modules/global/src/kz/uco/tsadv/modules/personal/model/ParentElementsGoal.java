package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import kz.uco.tsadv.modules.personal.enums.ElementTypeForGoals;

@MetaClass(name = "tsadv$ParentElementsGoal")
public class ParentElementsGoal extends BaseUuidEntity {
    private static final long serialVersionUID = 9071514882870507941L;

    @MetaProperty
    protected Integer elementType;

    @MetaProperty
    protected String elementName;

    @MetaProperty
    protected String goalName;

    @MetaProperty
    protected Integer goalWeight;

    public ElementTypeForGoals getElementType() {
        return elementType == null ? null : ElementTypeForGoals.fromId(elementType);
    }

    public void setElementType(ElementTypeForGoals elementType) {
        this.elementType = elementType == null ? null : elementType.getId();
    }


    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public String getElementName() {
        return elementName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public String getGoalName() {
        return goalName;
    }

    public void setGoalWeight(Integer goalWeight) {
        this.goalWeight = goalWeight;
    }

    public Integer getGoalWeight() {
        return goalWeight;
    }


}