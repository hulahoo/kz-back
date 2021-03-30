package kz.uco.tsadv.web.screens.scormquestionmapping;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.model.ScormQuestionMapping;

@UiController("tsadv_ScormQuestionMapping.browse")
@UiDescriptor("scorm-question-mapping-browse.xml")
@LookupComponent("scormQuestionMappingsTable")
@LoadDataBeforeShow
public class ScormQuestionMappingBrowse extends StandardLookup<ScormQuestionMapping> {
}