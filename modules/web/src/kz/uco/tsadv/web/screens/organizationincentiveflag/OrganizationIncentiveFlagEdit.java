package kz.uco.tsadv.web.screens.organizationincentiveflag;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.OrganizationIncentiveFlag;

@UiController("tsadv_OrganizationIncentiveFlag.edit")
@UiDescriptor("organization-incentive-flag-edit.xml")
@EditedEntityContainer("organizationIncentiveFlagDc")
@LoadDataBeforeShow
public class OrganizationIncentiveFlagEdit extends StandardEditor<OrganizationIncentiveFlag> {
}