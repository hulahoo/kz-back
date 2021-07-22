package kz.uco.tsadv.web.screens.organizationincentiveresult;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.OrganizationIncentiveResult;

@UiController("tsadv_OrganizationIncentiveResult.edit")
@UiDescriptor("organization-incentive-result-edit.xml")
@EditedEntityContainer("organizationIncentiveResultDc")
@LoadDataBeforeShow
public class OrganizationIncentiveResultEdit extends StandardEditor<OrganizationIncentiveResult> {
}