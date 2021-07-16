package kz.uco.tsadv.web.screens.organizationincentiveindicators;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.OrganizationIncentiveIndicators;

@UiController("tsadv_OrganizationIncentiveIndicators.edit")
@UiDescriptor("organization-incentive-indicators-edit.xml")
@EditedEntityContainer("organizationIncentiveIndicatorsDc")
@LoadDataBeforeShow
public class OrganizationIncentiveIndicatorsEdit extends StandardEditor<OrganizationIncentiveIndicators> {
}