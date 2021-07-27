package kz.uco.tsadv.web.screens.dicsocstatus;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicSocStatus;

@UiController("tsadv_DicSocStatus.browse")
@UiDescriptor("dic-soc-status-browse.xml")
@LookupComponent("dicSocStatusesTable")
@LoadDataBeforeShow
public class DicSocStatusBrowse extends StandardLookup<DicSocStatus> {
}