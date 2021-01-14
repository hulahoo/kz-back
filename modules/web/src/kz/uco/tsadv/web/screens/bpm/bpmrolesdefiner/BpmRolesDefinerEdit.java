package kz.uco.tsadv.web.screens.bpm.bpmrolesdefiner;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.bpm.BpmRolesDefiner;

@UiController("tsadv$BpmRolesDefiner.edit")
@UiDescriptor("bpm-roles-definer-edit.xml")
@EditedEntityContainer("bpmRolesDefinerDc")
@LoadDataBeforeShow
public class BpmRolesDefinerEdit extends StandardEditor<BpmRolesDefiner> {
}