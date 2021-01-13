package kz.uco.tsadv.web.screens.dicformstudy;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicFormStudy;

@UiController("tsadv_DicFormStudy.browse")
@UiDescriptor("dic-form-study-browse.xml")
@LookupComponent("dicFormStudiesTable")
@LoadDataBeforeShow
public class DicFormStudyBrowse extends StandardLookup<DicFormStudy> {
}