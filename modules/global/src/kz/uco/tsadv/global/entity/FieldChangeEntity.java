package kz.uco.tsadv.global.entity;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

@MetaClass(name = "tsadv$FieldChangeEntity")
public class FieldChangeEntity extends BaseUuidEntity {
    private static final long serialVersionUID = 3765458908729074709L;

    @MetaProperty
    protected String field;

    @MetaProperty
    protected String oldValue;

    @MetaProperty
    protected String newValue;

    public void setField(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getNewValue() {
        return newValue;
    }


}