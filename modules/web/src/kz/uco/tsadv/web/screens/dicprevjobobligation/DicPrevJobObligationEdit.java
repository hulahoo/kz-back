package kz.uco.tsadv.web.screens.dicprevjobobligation;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicPrevJobObligation;

@UiController("tsadv_DicPrevJobObligation.edit")
@UiDescriptor("dic-prev-job-obligation-edit.xml")
@EditedEntityContainer("dicPrevJobObligationDc")
@LoadDataBeforeShow
public class DicPrevJobObligationEdit extends StandardEditor<DicPrevJobObligation> {
}