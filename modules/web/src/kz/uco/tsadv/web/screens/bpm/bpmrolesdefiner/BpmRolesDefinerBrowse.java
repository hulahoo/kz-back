package kz.uco.tsadv.web.screens.bpm.bpmrolesdefiner;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.bpm.BpmRolesDefiner;

@UiController("tsadv$BpmRolesDefiner.browse")
@UiDescriptor("bpm-roles-definer-browse.xml")
@LookupComponent("bpmRolesDefinersTable")
@LoadDataBeforeShow
public class BpmRolesDefinerBrowse extends StandardLookup<BpmRolesDefiner> {
}