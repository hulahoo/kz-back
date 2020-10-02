package kz.uco.tsadv.service;

import kz.uco.tsadv.modules.personal.model.Salary;

import java.util.List;

public interface SalariesPeriodChangerService {
    String NAME = "tsadv_SalariesPeriodChangerService";

    List<Salary> getExistingSalaries(Salary newSalary);

    void changeExistingSalary(Salary existingSalary, Salary newSalary);
}
