package kz.uco.tsadv.web.screens.organizationincentivemonthresult;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.OrganizationIncentiveMonthResult;

@UiController("tsadv_OrganizationIncentiveMonthResult.browse")
@UiDescriptor("organization-incentive-month-result-browse.xml")
@LookupComponent("organizationIncentiveMonthResultsTable")
@LoadDataBeforeShow
public class OrganizationIncentiveMonthResultBrowse extends StandardLookup<OrganizationIncentiveMonthResult> {
}