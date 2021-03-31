package kz.uco.tsadv.web.screens.coursereview;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.model.CourseReview;

@UiController("tsadv$CourseReview.browse")
@UiDescriptor("course-review-browse.xml")
@LookupComponent("courseReviewsTable")
@LoadDataBeforeShow
public class CourseReviewBrowse extends StandardLookup<CourseReview> {
}