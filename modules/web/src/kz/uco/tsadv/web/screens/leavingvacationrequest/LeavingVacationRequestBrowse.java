package kz.uco.tsadv.web.screens.leavingvacationrequest;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.LeavingVacationRequest;

@UiController("tsadv$LeavingVacationRequest.browse")
@UiDescriptor("leaving-vacation-request-browse.xml")
@LookupComponent("leavingVacationRequestsTable")
@LoadDataBeforeShow
public class LeavingVacationRequestBrowse extends StandardLookup<LeavingVacationRequest> {
}