package kz.uco.tsadv.global.entity;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

/**
 * @author veronika.buksha
 */
@MetaClass(name = "tsadv$SalaryChartEntity")
public class SalaryChartEntity extends BaseUuidEntity {

    @MetaProperty
    protected Double salary;

    @MetaProperty
    protected String salaryDescription;

    @MetaProperty
    protected String color;

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public String getSalaryDescription() {
        return salaryDescription;
    }

    public void setSalaryDescription(String salaryDescription) {
        this.salaryDescription = salaryDescription;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
