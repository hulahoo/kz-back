package kz.uco.tsadv.web.screens.guardian;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.Guardian;

@UiController("tsadv_Guardian.browse")
@UiDescriptor("guardian-browse.xml")
@LookupComponent("guardiansTable")
@LoadDataBeforeShow
public class GuardianBrowse extends StandardLookup<Guardian> {
}