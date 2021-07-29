package kz.uco.tsadv.web.screens.granteesagreement;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.GranteesAgreement;

@UiController("tsadv_GranteesAgreement.browse")
@UiDescriptor("grantees-agreement-browse.xml")
@LookupComponent("granteesAgreementsTable")
@LoadDataBeforeShow
public class GranteesAgreementBrowse extends StandardLookup<GranteesAgreement> {
}