package kz.uco.tsadv.web.screens.dicincentiveresultstatus;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicIncentiveResultStatus;

@UiController("tsadv_DicIncentiveResultStatus.browse")
@UiDescriptor("dic-incentive-result-status-browse.xml")
@LookupComponent("dicIncentiveResultStatusesTable")
@LoadDataBeforeShow
public class DicIncentiveResultStatusBrowse extends StandardLookup<DicIncentiveResultStatus> {
}