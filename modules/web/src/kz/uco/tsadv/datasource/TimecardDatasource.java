package kz.uco.tsadv.datasource;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.gui.data.impl.CustomCollectionDatasource;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.timesheet.model.Timecard;
import kz.uco.tsadv.service.TimecardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;


public class TimecardDatasource extends CustomCollectionDatasource<Timecard, UUID> {
    private static final Logger log = LoggerFactory.getLogger(TimecardDatasource.class);

    protected CommonService commonService = AppBeans.get(CommonService.class);
    protected Configuration configuration = AppBeans.get(Configuration.NAME);
    protected TimecardService timecardService = AppBeans.get(TimecardService.class);

    @Override
    protected Collection<Timecard> getEntities(Map<String, Object> params) {
        Date startDate = (Date) params.get("startDate");
        Date endDate = (Date) params.get("endDate");
        boolean loadFullData = (boolean) params.get("loadFullData");
        OrganizationGroupExt organizationGroup = (OrganizationGroupExt) params.get("organizationGroup");
        PositionGroupExt positionGroup = (PositionGroupExt) params.get("positionGroup");
        PersonGroupExt personGroup = (PersonGroupExt) params.get("personGroup");
        AssignmentExt assignmentExt = (AssignmentExt) params.get("assignmentExt");
        Boolean enableInclusions = (Boolean) params.get("enableInclusions");
        firstResult = params.get("firstResult") != null ? ((Integer) params.get("firstResult")) : firstResult;
        savedParameters.remove("firstResult");//when not search
        Boolean isIntern = params.get("isIntern") != null && params.get("isIntern").equals(true);
        if (isIntern)
            return timecardService.getTimecards(organizationGroup, personGroup, startDate, endDate,
                    firstResult, maxResults, loadFullData, assignmentExt, enableInclusions);
        else
            return timecardService.getTimecards(organizationGroup, positionGroup, personGroup, startDate, endDate,
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
        return timecardService.getTimecardsCount(organizationGroup, positionGroup, personGroup, startDate, endDate, enableInclusions);
    }

}
