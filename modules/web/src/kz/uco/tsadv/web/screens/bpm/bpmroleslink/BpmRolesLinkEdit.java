package kz.uco.tsadv.web.screens.bpm.bpmroleslink;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.bpm.BpmRolesLink;

@UiController("tsadv$BpmRolesLink.edit")
@UiDescriptor("bpm-roles-link-edit.xml")
@EditedEntityContainer("bpmRolesLinkDc")
@LoadDataBeforeShow
public class BpmRolesLinkEdit extends StandardEditor<BpmRolesLink> {
}