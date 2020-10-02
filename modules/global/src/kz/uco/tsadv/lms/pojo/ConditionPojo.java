package kz.uco.tsadv.lms.pojo;

import java.io.Serializable;

public class ConditionPojo implements Serializable {
    protected String property;
    protected Object value;
    protected String operator;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
