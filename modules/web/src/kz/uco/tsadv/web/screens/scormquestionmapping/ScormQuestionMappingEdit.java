package kz.uco.tsadv.web.screens.scormquestionmapping;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.model.ScormQuestionMapping;

@UiController("tsadv_ScormQuestionMapping.edit")
@UiDescriptor("scorm-question-mapping-edit.xml")
@EditedEntityContainer("scormQuestionMappingDc")
@LoadDataBeforeShow
public class ScormQuestionMappingEdit extends StandardEditor<ScormQuestionMapping> {
}