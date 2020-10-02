package kz.uco.tsadv.modules.learning.model;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.chile.core.annotations.MetaProperty;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

@MetaClass(name = "tsadv$ForPivotTable")
public class ForPivotTable extends BaseUuidEntity {
    private static final long serialVersionUID = -4426048121559404640L;

    @MetaProperty
    protected PersonGroupExt personGroup;

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }


}