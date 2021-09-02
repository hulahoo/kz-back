package kz.uco.tsadv.web.screens.dicincentiveresultstatus;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicIncentiveResultStatus;

@UiController("tsadv_DicIncentiveResultStatus.edit")
@UiDescriptor("dic-incentive-result-status-edit.xml")
@EditedEntityContainer("dicIncentiveResultStatusDc")
@LoadDataBeforeShow
public class DicIncentiveResultStatusEdit extends StandardEditor<DicIncentiveResultStatus> {
}