package kz.uco.tsadv.web.modules.timesheet.standardoffset;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.TimecardHierarchy;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.timesheet.config.TimecardConfig;
import kz.uco.tsadv.modules.timesheet.model.AllowableScheduleForPosition;
import kz.uco.tsadv.modules.timesheet.model.StandardOffset;
import kz.uco.tsadv.modules.timesheet.model.StandardSchedule;
import kz.uco.tsadv.service.TimecardHierarchyService;

import javax.inject.Inject;
import java.util.*;

public class StandardOffsetBrowse extends AbstractLookup {
    @Inject
    private CommonService commonService;
    @Inject
    private DataManager dataManager;
    @Inject
    private TimecardHierarchyService timecardHierarchyService;
    @Inject
    private TimecardConfig timecardConfig;
    @Inject
    private GroupDatasource<StandardOffset, UUID> standardOffsetsDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        Object startDateValue = params.get("startDateValue");
        Object endDateValue = params.get("endDateValue");
        if (startDateValue != null && endDateValue != null) {
            standardOffsetsDs.setQuery("select e from tsadv$StandardOffset e " +
                    "  where (:param$startDateValue >= e.startDate) " +
//                    " and (:param$endDateValue <= e.endDate) " +
                    " order by e.standardSchedule.scheduleName");
        }
        if (timecardConfig.getAllowableSchedulesForPositionOn()) {
            constrainByAllowedSchedules(params);
        }
    }

    protected void constrainByAllowedSchedules(Map<String, Object> params) {
        OrganizationGroupExt organizationGroup = (OrganizationGroupExt) params.get("organizationGroup");
        PositionGroupExt positionGroup = (PositionGroupExt) params.get("positionGroup");
        PersonGroupExt personGroup = (PersonGroupExt) params.get("personGroup");
        Object startDateValue = params.get("startDateValue");
        Object endDateValue = params.get("endDateValue");
        Date startDate = CommonUtils.getSystemDate();
        Date endDate = CommonUtils.getSystemDate();
        if (startDateValue != null && endDateValue != null) {
            startDate = ((Date) startDateValue);
            endDate = ((Date) endDateValue);
        }
            Set<PositionGroupExt> positionGroups = new HashSet<>();

        if (organizationGroup != null && positionGroup == null) {
            TimecardHierarchy timecardHierarchy = commonService.getEntity(TimecardHierarchy.class,
                    "select e from tsadv$TimecardHierarchy e where e.organizationGroup.id = :organizationGroup and e.positionGroup is null",
                    ParamsMap.of("organizationGroup", organizationGroup), "timecardHierarchy-full-view");

            if (timecardHierarchy != null) {
                Collection<TimecardHierarchy> innerHierarchies = timecardHierarchyService.getAllNestedElements(timecardHierarchy.getId(), startDate, endDate);
                innerHierarchies.forEach(h -> {
                    if (h.getPositionGroup() != null) {
                        positionGroups.add(h.getPositionGroup());
                    }
                });
            }
        }

        if (positionGroup != null) {
            positionGroups.add(positionGroup);
        }

        if (personGroup != null) {
            personGroup = dataManager.reload(personGroup, "personGroup.with.positionGroup");
            PositionGroupExt positionGroupForPerson = personGroup.getPrimaryAssignment().getPositionGroup();
            positionGroups.add(positionGroupForPerson);

        }

        List<AllowableScheduleForPosition> allowableSchedulesForPositions = commonService.getEntities(AllowableScheduleForPosition.class,
                "select e from tsadv$AllowableScheduleForPosition e where e.positionGroup in :positionGroups and current_date between e.startDate and e.endDate",
                ParamsMap.of("positionGroups", new ArrayList<>(positionGroups)), "allowableScheduleForPosition-view");

        List<StandardSchedule> allowableSchedules = new ArrayList<>();
        allowableSchedulesForPositions.forEach(a -> allowableSchedules.add(a.getSchedule()));

        if (startDateValue != null && endDateValue != null) {
            standardOffsetsDs.setQuery("select e from tsadv$StandardOffset e " +
                    "  where (:param$startDateValue >= e.startDate) " +
//                    " and (:param$endDateValue <= e.endDate) " +
                    " and e.standardSchedule in :custom$allowableSchedules " +
                    " order by e.standardSchedule.scheduleName");
            standardOffsetsDs.refresh(ParamsMap.of("allowableSchedules", allowableSchedules));
        }
    }
}