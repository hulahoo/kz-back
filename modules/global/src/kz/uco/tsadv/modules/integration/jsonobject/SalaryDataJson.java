package kz.uco.tsadv.modules.integration.jsonobject;

import java.io.Serializable;
import java.util.ArrayList;

public class SalaryDataJson implements Serializable {

    protected ArrayList<SalaryJson> salary;

    public ArrayList<SalaryJson> getSalary() {
        return salary;
    }

    public void setSalary(ArrayList<SalaryJson> salary) {
        this.salary = salary;
    }
}
