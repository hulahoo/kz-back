package kz.uco.tsadv.service.portal;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.core.sys.AppContext;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.entity.VacationScheduleRequest;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.pojo.GanttChartVacationScheduleData;
import kz.uco.tsadv.service.EmployeeService;
import org.apache.commons.lang3.ArrayUtils;
import org.postgresql.util.PGobject;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.util.*;

@Service(VacationScheduleRequestService.NAME)
public class VacationScheduleRequestServiceBean implements VacationScheduleRequestService {

    private static final Gson gson = new Gson();

    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected Persistence persistence;

    @Override
    public List<VacationScheduleRequest> getChildVacationSchedule() {
        final UUID positionGroupId = userSessionSource.getUserSession().getAttribute(StaticVariable.POSITION_GROUP_ID);

        if (positionGroupId == null) throw new NullPointerException("Position is null!");

        return persistence.callInTransaction(em ->
                em.createQuery("select r from base$HierarchyElementExt e " +
                        "   join e.parentGroup.list l " +
                        "   join base$AssignmentExt a on a.positionGroup = e.positionGroup " +
                        "   join tsadv_VacationScheduleRequest r on r.personGroup = a.personGroup " +
                        "   where current_date between l.startDate and l.endDate" +
                        "       and l.positionGroup.id = :positionGroupId " +
                        "       and current_date between a.startDate and a.endDate " +
                        "       and a.primaryFlag = 'TRUE' " +
                        "       and a.assignmentStatus.code <> 'TERMINATED'", VacationScheduleRequest.class)
                        .setParameter("positionGroupId", positionGroupId)
                        .setViewName(View.MINIMAL)
                        .getResultList());
    }

    @Override
    public List<GanttChartVacationScheduleData> ganttChart(Date startDate, Date endDate) {
        final UUID personGroupId = userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP_ID);
        if (personGroupId == null) throw new NullPointerException("Session personGroupId is null!");
        OrganizationGroupExt organizationGroup = employeeService.getOrganizationGroupByPersonGroupId(personGroupId, View.MINIMAL);
        if (organizationGroup == null) throw new NullPointerException("Session user organization is null!");

        return ganttChart(organizationGroup.getId(), startDate, endDate);
    }

    @Override
    public List<GanttChartVacationScheduleData> ganttChart(UUID organizationGroupId, Date startDate, Date endDate) {

        String langOrder = AppContext.getProperty("base.abstractDictionary.langOrder");

        if (langOrder == null) throw new NullPointerException("base.abstractDictionary.langOrder is null");

        int langIndex = ArrayUtils.indexOf(langOrder.split(";"), userSessionSource.getLocale().getLanguage());

        Assert.isTrue(langIndex != -1, userSessionSource.getLocale().getLanguage() + " not found in base.abstractDictionary.langOrder");

        String queryStr = "SELECT td.* FROM gantt_chart_vacation_schedule(#organizationGroupId, #startDate::date, #endDate::date, #langIndex) td";

        String jsonData = persistence.callInTransaction(em ->
                Optional.ofNullable(
                        em.createNativeQuery(queryStr)
                                .setParameter("organizationGroupId", organizationGroupId)
                                .setParameter("startDate", startDate)
                                .setParameter("endDate", endDate)
                                .setParameter("langIndex", String.valueOf(langIndex))
                                .getFirstResult()
                ).map(o -> (PGobject) o)
                        .map(PGobject::getValue)
                        .orElse("[]")
        );

        List<GanttChartVacationScheduleData> ganttChartData = gson.fromJson(jsonData, new TypeToken<List<GanttChartVacationScheduleData>>() {
        }.getType());

        List<UUID> personIdList = new ArrayList<>();

        ganttChartData
                .forEach(ganttChart -> {
                    int colorIndex = personIdList.indexOf(ganttChart.getPersonGroupId());
                    if (colorIndex == -1) {
                        colorIndex = personIdList.size();
                        personIdList.add(ganttChart.getPersonGroupId());
                    }
                    ganttChart.setBrighten(Math.random());
                    ganttChart.setColorIndex(colorIndex);
                });

        return ganttChartData;

    }
}