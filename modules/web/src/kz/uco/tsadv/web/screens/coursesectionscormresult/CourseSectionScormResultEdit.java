package kz.uco.tsadv.web.screens.coursesectionscormresult;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.model.CourseSectionScormResult;

@UiController("tsadv_CourseSectionScormResult.edit")
@UiDescriptor("course-section-scorm-result-edit.xml")
@EditedEntityContainer("courseSectionScormResultDc")
@LoadDataBeforeShow
public class CourseSectionScormResultEdit extends StandardEditor<CourseSectionScormResult> {
}