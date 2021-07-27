package kz.uco.tsadv.web.screens.learningresult;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.LearningResult;

@UiController("tsadv_LearningResult.edit")
@UiDescriptor("learning-result-edit.xml")
@EditedEntityContainer("learningResultDc")
@LoadDataBeforeShow
public class LearningResultEdit extends StandardEditor<LearningResult> {
}