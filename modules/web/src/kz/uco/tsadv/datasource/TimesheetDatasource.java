package kz.uco.tsadv.datasource;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.data.impl.CustomCollectionDatasource;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.timesheet.model.Timesheet;
import kz.uco.tsadv.service.TimesheetService;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;


public class TimesheetDatasource extends CustomCollectionDatasource<Timesheet, UUID> {

    protected CommonService commonService = AppBeans.get(CommonService.class);
    protected TimesheetService timesheetService = AppBeans.get(TimesheetService.class);

    @Override
    protected Collection<Timesheet> getEntities(Map<String, Object> params) {
        Date startDate = (Date) params.get("startDate");
        Date endDate = (Date) params.get("endDate");
        boolean loadFullData = (boolean) params.get("loadFullData");
        OrganizationGroupExt organizationGroup = (OrganizationGroupExt) params.get("organizationGroup");
        PositionGroupExt positionGroup = (PositionGroupExt) params.get("positionGroup");
        AssignmentExt assignmentExt = (AssignmentExt) params.get("assignmentExt");
        PersonGroupExt personGroup = (PersonGroupExt) params.get("personGroup");
        Boolean enableInclusions = (Boolean) params.get("enableInclusions");
        firstResult = params.get("firstResult") != null ? ((Integer) params.get("firstResult")) : firstResult;
        savedParameters.remove("firstResult");//when not search
        return timesheetService.getTimesheets(organizationGroup, positionGroup, personGroup, startDate, endDate,
                firstResult, maxResults, loadFullData, assignmentExt, enableInclusions);
    }

    @Override
    public int getCount() {
        Date startDate = (Date) savedParameters.get("startDate");
        Date endDate = (Date) savedParameters.get("endDate");
        OrganizationGroupExt organizationGroup = (OrganizationGroupExt) savedParameters.get("organizationGroup");
        PositionGroupExt positionGroup = (PositionGroupExt) savedParameters.get("positionGroup");
        PersonGroupExt personGroup = (PersonGroupExt) savedParameters.get("personGroup");
        Boolean enableInclusions = (Boolean) savedParameters.get("enableInclusions");
        return timesheetService.getTimesheetsCount(organizationGroup, positionGroup, personGroup, startDate, endDate, enableInclusions);
    }
}
