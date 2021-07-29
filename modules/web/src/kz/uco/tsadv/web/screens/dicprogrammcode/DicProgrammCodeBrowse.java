package kz.uco.tsadv.web.screens.dicprogrammcode;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.dictionary.DicProgrammCode;

@UiController("tsadv_DicProgrammCode.browse")
@UiDescriptor("dic-programm-code-browse.xml")
@LookupComponent("dicProgrammCodesTable")
@LoadDataBeforeShow
public class DicProgrammCodeBrowse extends StandardLookup<DicProgrammCode> {
}