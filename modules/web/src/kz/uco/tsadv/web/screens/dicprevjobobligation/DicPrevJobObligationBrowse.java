package kz.uco.tsadv.web.screens.dicprevjobobligation;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicPrevJobObligation;

@UiController("tsadv_DicPrevJobObligation.browse")
@UiDescriptor("dic-prev-job-obligation-browse.xml")
@LookupComponent("dicPrevJobObligationsTable")
@LoadDataBeforeShow
public class DicPrevJobObligationBrowse extends StandardLookup<DicPrevJobObligation> {
}