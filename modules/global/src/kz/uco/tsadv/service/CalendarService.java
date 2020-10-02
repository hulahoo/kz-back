package kz.uco.tsadv.service;

import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.timesheet.model.Calendar;

/**
 * Работа с HRMS календарями
 *
 * @author Felix Kamalov
 */
public interface CalendarService {
    String NAME = "tsadv_CalendarService";

    /**
     * Возвращает календарь для заданного назначения
     * Или со Штатной единицы, если нет на назначении,
     * Или с Подразделения, или его родителя в иерархии (и так до конца)
     * Или календарь по умолчанию
     * Или null
     *
     * @param assignmentGroup Назначение работника
     */
    Calendar getCalendar(AssignmentGroupExt assignmentGroup);

    /**
     * Возвращает календарь для заданного назначения (и только для него)
     */
    Calendar getCalendarForAssignment(AssignmentGroupExt assignmentGroup);

    /**
     * Возвращает календарь для заданной Штатной единицы (и только для неё)
     */
    Calendar getCalendarForPosition(PositionGroupExt positionGroup);

    /**
     * Возвращает календарь для заданного Подразделения,
     * Или его родителя в основной иерархии (и так до конца) если на самом подразделении не задан календарь
     */
    Calendar getCalendar(OrganizationGroupExt organizationGroup);

    /**
     * Возвращает календарь для заданного Подразделения (и только для него)
     */
    Calendar getCalendarForOrganization(OrganizationGroupExt organizationGroup);

    /**
     * Возвращает календарь по умолчанию
     * Или null
     */
    Calendar getDefaultCalendar();
}