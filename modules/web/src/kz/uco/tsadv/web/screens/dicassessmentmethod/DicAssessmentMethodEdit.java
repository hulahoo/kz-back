package kz.uco.tsadv.web.screens.dicassessmentmethod;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.dictionary.DicAssessmentMethod;

@UiController("tsadv_DicAssessmentMethod.edit")
@UiDescriptor("dic-assessment-method-edit.xml")
@EditedEntityContainer("dicAssessmentMethodDc")
@LoadDataBeforeShow
public class DicAssessmentMethodEdit extends StandardEditor<DicAssessmentMethod> {
}