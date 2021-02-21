package kz.uco.tsadv.service;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.EntityStates;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.config.AbsenceConfig;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.timesheet.model.Calendar;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

import static kz.uco.base.common.Null.checkNull;

/**
 * Работа с HRMS календарями
 *
 * @author Felix Kamalov
 */
@Service(CalendarService.NAME)
public class CalendarServiceBean implements CalendarService {

    // Inject не работает. не может выбрать из 2-х бинов base & tsadv. задаю явно через сеттер в spring.xml
    protected OrganizationService organizationService;
    @Inject
    protected AbsenceConfig absenceConfig;
    @Inject
    protected CommonService commonService;
    @Inject
    protected EntityStates entityStates;
    @Inject
    protected DataManager dataManager;

    /**
     * Возвращает календарь для заданного назначения
     * Или со Штатной единицы, если нет на назначении,
     * Или с Подразделения, или его родителя в иерархии (и так до конца)
     * Или календарь по умолчанию
     * Или null
     *
     * @param assignmentGroup Назначение работника
     */
    @Override
    public Calendar getCalendar(AssignmentGroupExt assignmentGroup) {
        checkNull(assignmentGroup, "assignmentGroup is undefined in AbsenceServiceBean.getCalendar(assignmentGroup)");

        Calendar calendar = getCalendarForAssignment(assignmentGroup);
        if (calendar != null) {
            return calendar;
        }

        AssignmentExt assignment = assignmentGroup.getAssignment();
        if (assignment != null) {
            assignment = dataManager.reload(assignment, "assignmentBrowse.view");
            if (assignment.getPositionGroup() != null) {

                calendar = getCalendarForPosition(assignment.getPositionGroup());
                if (calendar != null) {
                    return calendar;
                }
            }
            if (assignment.getOrganizationGroup() != null) {

                calendar = getCalendar(assignment.getOrganizationGroup());
                if (calendar != null) {
                    return calendar;
                }

            }
        }

        return getDefaultCalendar();
    }

    /**
     * Возвращает календарь для заданного назначения (и только для него)
     */
    @Override
    public Calendar getCalendarForAssignment(AssignmentGroupExt assignmentGroup) {
        checkNull(assignmentGroup, "assignmentGroup is undefined in AbsenceServiceBean.getCalendarForAssignment(assignmentGroup)");

        if (!entityStates.isLoaded(assignmentGroup, "analytics")) {
            assignmentGroup = dataManager.reload(assignmentGroup, "assignment.analytic.update");
        }

        if (assignmentGroup.getAnalytics() != null) {
            return assignmentGroup.getAnalytics().getCalendar();
        }

        return null;
    }

    /**
     * Возвращает календарь для заданной Штатной единицы (и только для неё)
     */
    @Override
    public Calendar getCalendarForPosition(PositionGroupExt positionGroup) {
        checkNull(positionGroup, "positionGroup is undefined in AbsenceServiceBean.getCalendarForPosition(positionGroup)");

        if (!entityStates.isLoaded(positionGroup, "analytics")) {
            positionGroup = dataManager.reload(positionGroup, "position.analytic.update");
        }

        if (positionGroup.getAnalytics() != null) {
            return positionGroup.getAnalytics().getCalendar();
        }

        return null;
    }

    /**
     * Возвращает календарь для заданного Подразделения (и только для него)
     */
    @Override
    public Calendar getCalendarForOrganization(OrganizationGroupExt organizationGroup) {
        checkNull(organizationGroup, "organizationGroup is undefined in AbsenceServiceBean.getCalendarForOrganization(organizationGroup)");

        if (!entityStates.isLoaded(organizationGroup, "analytics")) {
            organizationGroup = dataManager.reload(organizationGroup, "organization.analytic.update");
        }

        if (organizationGroup.getAnalytics() != null) {
            return organizationGroup.getAnalytics().getCalendar();
        }

        return null;
    }

    /**
     * Возвращает календарь для заданного Подразделения,
     * Или его родителя в основной иерархии (и так до конца) если на самом подразделении не задан календарь
     */
    @Override
    public Calendar getCalendar(OrganizationGroupExt organizationGroup) {
        checkNull(organizationGroup, "organizationGroup is undefined in AbsenceServiceBean.getCalendar(organizationGroup)");

        Calendar calendar = getCalendarForOrganization(organizationGroup);
        if (calendar != null) {
            return calendar;
        }

        OrganizationGroupExt parent = organizationService.getParent(organizationGroup);
        if (parent != null) {
            return getCalendar(parent);
        }

        return null;
    }

    /**
     * Возвращает календарь по умолчанию
     * Или null
     */
    @Override
    public Calendar getDefaultCalendar() {
        if (absenceConfig.getDefaultCalendar() != null) {
            return commonService.getEntity(Calendar.class, absenceConfig.getDefaultCalendar());
        }

        return null;
    }


    public void setOrganizationService(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }
}