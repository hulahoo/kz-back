package kz.uco.tsadv.web.screens.coursereview;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.model.CourseReview;

@UiController("tsadv$CourseReview.edit")
@UiDescriptor("course-review-edit.xml")
@EditedEntityContainer("courseReviewDc")
@LoadDataBeforeShow
public class CourseReviewEdit extends StandardEditor<CourseReview> {
}