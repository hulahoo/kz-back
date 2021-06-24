package kz.uco.tsadv.web.modules.learning.positiongroupextcoursematrix;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.web.modules.learning.CourseMatrixBrowse;

import javax.inject.Inject;

@UiController("base$PositionGroupExtCourseMatrix.browse")
@UiDescriptor("position-group-ext-course-matrix-browse.xml")
@LookupComponent("positionGroupExtsTable")
@LoadDataBeforeShow
public class PositionGroupExtCourseMatrixBrowse extends StandardLookup<PositionGroupExt> {

    @Inject
    protected ScreenBuilders screenBuilders;

    public void openCourseMatrixScreen(Entity item, String columnId) {
        screenBuilders.screen(this).withOpenMode(OpenMode.THIS_TAB)
                .withScreenClass(CourseMatrixBrowse.class)
                .withOptions(new MapScreenOptions(ParamsMap.of("positionGroup", ((PositionGroupExt) item))))
                .build().show();
    }
}