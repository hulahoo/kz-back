package kz.uco.tsadv.service;


import kz.uco.tsadv.modules.personal.model.AssignmentExt;

import java.util.Date;

public interface PersonDataService {
    String NAME = "tsadv_PersonDataService";

    /**
     * Проверяет ошибочный статус штатной единицы текущего назначения на момент даты приема сотрудника.
     *
     * @param hireDate - дата приема сотрудника
     * @param assignmentExt - объект назначения
     * @return true, если в указанную дату штатная единица отсутствует, или статус штатной единицы неактивен.
     */
    boolean checkPositionForErrors(Date hireDate, AssignmentExt assignmentExt);
}