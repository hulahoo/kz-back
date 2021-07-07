package kz.uco.tsadv.web.screens.dicassessmentmethod;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.dictionary.DicAssessmentMethod;

@UiController("tsadv_DicAssessmentMethod.browse")
@UiDescriptor("dic-assessment-method-browse.xml")
@LookupComponent("dicAssessmentMethodsTable")
@LoadDataBeforeShow
public class DicAssessmentMethodBrowse extends StandardLookup<DicAssessmentMethod> {
}