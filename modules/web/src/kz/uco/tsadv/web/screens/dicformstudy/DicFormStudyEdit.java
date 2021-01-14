package kz.uco.tsadv.web.screens.dicformstudy;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicFormStudy;

@UiController("tsadv_DicFormStudy.edit")
@UiDescriptor("dic-form-study-edit.xml")
@EditedEntityContainer("dicFormStudyDc")
@LoadDataBeforeShow
public class DicFormStudyEdit extends StandardEditor<DicFormStudy> {
}