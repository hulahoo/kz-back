package kz.uco.tsadv.web.screens.organizationincentivemonthresult;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.OrganizationIncentiveMonthResult;

@UiController("tsadv_OrganizationIncentiveMonthResult.edit")
@UiDescriptor("organization-incentive-month-result-edit.xml")
@EditedEntityContainer("organizationIncentiveMonthResultDc")
@LoadDataBeforeShow
public class OrganizationIncentiveMonthResultEdit extends StandardEditor<OrganizationIncentiveMonthResult> {
}