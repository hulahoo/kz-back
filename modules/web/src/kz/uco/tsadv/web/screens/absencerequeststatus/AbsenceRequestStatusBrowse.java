package kz.uco.tsadv.web.screens.absencerequeststatus;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.entity.AbsenceRequestStatus;


/**
 * User: maiha
 * Date: 23.12.2020
 * Time: 16:58
 */

@UiController("tsadv_AbsenceRequestStatus.browse")
@UiDescriptor("absence-request-status-browse.xml")
@LookupComponent("absenceRequestStatusesTable")
@LoadDataBeforeShow
public class AbsenceRequestStatusBrowse extends StandardLookup<AbsenceRequestStatus> {
}