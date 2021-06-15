package kz.uco.tsadv.web.screens.executiveassistants;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.entity.ExecutiveAssistants;


/**
 * @author Alibek Berdaulet
 */
@UiController("tsadv_ExecutiveAssistants.browse")
@UiDescriptor("executive-assistants-browse.xml")
@LookupComponent("executiveAssistantsesTable")
@LoadDataBeforeShow
public class ExecutiveAssistantsBrowse extends StandardLookup<ExecutiveAssistants> {
}