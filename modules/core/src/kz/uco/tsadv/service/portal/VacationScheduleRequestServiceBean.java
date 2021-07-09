package kz.uco.tsadv.service.portal;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.app.serialization.EntitySerializationAPI;
import com.haulmont.cuba.core.app.serialization.EntitySerializationOption;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.core.sys.AppContext;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.entity.VacationScheduleRequest;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.PositionExt;
import kz.uco.tsadv.pojo.GanttChartVacationScheduleData;
import kz.uco.tsadv.pojo.pagination.EntitiesPaginationResult;
import kz.uco.tsadv.pojo.pagination.PaginationPojo;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.service.HierarchyService;
import kz.uco.tsadv.service.OrganizationHrUserService;
import kz.uco.tsadv.service.PositionService;
import org.apache.commons.lang3.ArrayUtils;
import org.postgresql.util.PGobject;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@Service(VacationScheduleRequestService.NAME)
public class VacationScheduleRequestServiceBean implements VacationScheduleRequestService {

    private static final Gson gson = new Gson();

    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected Persistence persistence;
    @Inject
    protected OrganizationHrUserService organizationHrUserService;
    @Inject
    protected PositionService positionService;
    @Inject
    protected EntitySerializationAPI entitySerializationAPI;
    @Inject
    protected ViewRepository viewRepository;
    @Inject
    protected HierarchyService hierarchyService;
    @Inject
    protected DataManager dataManager;

    @Override
    public EntitiesPaginationResult getChildVacationSchedule(PaginationPojo paginationPojo) {

        PositionExt position = positionService.getPosition(
                new View(PositionExt.class).addProperty("organizationGroupExt").addProperty("group"));

        if (position == null) throw new NullPointerException("Position is null!");

        List<OrganizationGroupExt> hrOrganizationList =
                organizationHrUserService
                        .getOrganizationList(userSessionSource.getUserSession().getUser().getId(),
                                OrganizationHrUserService.HR_SPECIALIST);

        boolean isHr = !hrOrganizationList.isEmpty();

        View view = new View(VacationScheduleRequest.class)
                .addProperty("personGroup", viewRepository.getView(PersonGroupExt.class, View.MINIMAL))
                .addProperty("requestNumber")
                .addProperty("startDate")
                .addProperty("endDate")
                .addProperty("absenceDays")
                .addProperty("sentToOracle");

        LoadContext<VacationScheduleRequest> loadContext = LoadContext.create(VacationScheduleRequest.class);

        if (isHr) {

            List<UUID> parentIdList = hrOrganizationList.stream().map(BaseUuidEntity::getId).collect(Collectors.toList());
            List<UUID> allOrgIdList = hierarchyService.getOrganizationGroupIdChild(parentIdList);
            allOrgIdList.addAll(parentIdList);

            loadContext
                    .setQuery(LoadContext.createQuery("select r from tsadv_VacationScheduleRequest r " +
                            "   join r.personGroup.assignments s " +
                            "   where s.organizationGroup.id in :allOrgIdList " +
                            "   and s.primaryFlag = 'true'" +
                            "   and current_date between s.startDate and s.endDate" +
                            "   order by r.startDate desc ")
                            .setParameter("allOrgIdList", allOrgIdList));
        } else
            loadContext
                    .setQuery(LoadContext.createQuery("select r from base$HierarchyElementExt e " +
                            "   join e.parentGroup.list l " +
                            "   join base$AssignmentExt a on a.positionGroup = e.positionGroup " +
                            "   join tsadv_VacationScheduleRequest r on r.personGroup = a.personGroup " +
                            "   where current_date between l.startDate and l.endDate" +
                            "       and l.positionGroup.id = :positionGroupId " +
                            "       and current_date between a.startDate and a.endDate " +
                            "       and a.primaryFlag = 'TRUE' " +
                            "       and a.assignmentStatus.code <> 'TERMINATED'" +
                            "   order by r.startDate desc")
                            .setParameter("positionGroupId", position.getGroup().getId()));

        long count = dataManager.getCount(loadContext);

        loadContext.getQuery()
                .setFirstResult(paginationPojo.getOffset())
                .setMaxResults(paginationPojo.getLimit());

        List<VacationScheduleRequest> entities = dataManager.loadList(loadContext.setView(view));

        List<EntitySerializationOption> serializationOptions = new ArrayList<>();
        serializationOptions.add(EntitySerializationOption.SERIALIZE_INSTANCE_NAME);

        String json = entitySerializationAPI.toJson(entities, view, serializationOptions.toArray(new EntitySerializationOption[0]));

        return new EntitiesPaginationResult(json, count);
    }

    @Override
    public List<GanttChartVacationScheduleData> ganttChart(Date startDate, Date endDate) {
        final UUID personGroupId = userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP_ID);
        if (personGroupId == null) throw new NullPointerException("Session personGroupId is null!");

        return ganttChart(personGroupId, startDate, endDate);
    }

    @Override
    public List<GanttChartVacationScheduleData> ganttChart(UUID personGroupId, Date startDate, Date endDate) {

        String langOrder = AppContext.getProperty("base.abstractDictionary.langOrder");

        if (langOrder == null) throw new NullPointerException("base.abstractDictionary.langOrder is null");

        int langIndex = ArrayUtils.indexOf(langOrder.split(";"), userSessionSource.getLocale().getLanguage());

        Assert.isTrue(langIndex != -1, userSessionSource.getLocale().getLanguage() + " not found in base.abstractDictionary.langOrder");

        String queryStr = "SELECT td.* FROM gantt_chart_vacation_schedule(#personGroupId, #startDate::date, #endDate::date, #langIndex) td";

        String jsonData = persistence.callInTransaction(em ->
                Optional.ofNullable(
                        em.createNativeQuery(queryStr)
                                .setParameter("personGroupId", personGroupId)
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