package kz.uco.tsadv.web.screens.dicprogrammcode;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.dictionary.DicProgrammCode;

@UiController("tsadv_DicProgrammCode.edit")
@UiDescriptor("dic-programm-code-edit.xml")
@EditedEntityContainer("dicProgrammCodeDc")
@LoadDataBeforeShow
public class DicProgrammCodeEdit extends StandardEditor<DicProgrammCode> {
}