package kz.uco.tsadv.web.screens.dicsocstatus;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicSocStatus;

@UiController("tsadv_DicSocStatus.edit")
@UiDescriptor("dic-soc-status-edit.xml")
@EditedEntityContainer("dicSocStatusDc")
@LoadDataBeforeShow
public class DicSocStatusEdit extends StandardEditor<DicSocStatus> {
}