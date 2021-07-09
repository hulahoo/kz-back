package kz.uco.tsadv.web.modules.learning;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.actions.list.RemoveAction;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.base.cuba.actions.CreateActionExt;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.modules.learning.model.PositionCourse;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;

import javax.inject.Inject;
import javax.inject.Named;

@UiController("tsadv_CourseMatrixBrowse")
@UiDescriptor("course-matrix-browse.xml")
public class CourseMatrixBrowse extends Screen {
    @Inject
    protected InstanceContainer<PositionGroupExt> positionGroupExtDc;
    @Inject
    protected DataManager dataManager;
    @Named("positionCoursesTable.create")
    protected CreateActionExt positionCoursesTableCreate;
    @Inject
    protected CollectionLoader<PositionCourse> positionCoursesDl;
    @Inject
    protected ScreenBuilders screenBuilders;
    @Named("positionCoursesTable.remove")
    protected RemoveAction<PositionCourse> positionCoursesTableRemove;

    @Subscribe
    protected void onInit(InitEvent event) {
        MapScreenOptions options = (MapScreenOptions) event.getOptions();
        if (options != null && options.getParams().containsKey("positionGroup")) {
            positionGroupExtDc.setItem(dataManager.reload(((PositionGroupExt) options.getParams().get("positionGroup")),
                    "positionGroupExt-for-cousre-matrix-browse"));
            getWindow().setCaption(positionGroupExtDc.getItem().getPositionName());
            positionCoursesDl.setParameter("positionGroupId", positionGroupExtDc.getItem().getId());
            positionCoursesDl.load();
        }
    }

    @Subscribe
    protected void onAfterShow(AfterShowEvent event) {
        positionCoursesTableCreate.setInitializer(o -> {
            PositionCourse positionCourse = (PositionCourse) o;
            positionCourse.setPositionGroup(positionGroupExtDc.getItem());
            positionCourse.setStartDate(CommonUtils.getSystemDate());
            positionCourse.setEndDate(CommonUtils.getEndOfTime());
        });
        positionCoursesTableCreate.setAfterCloseHandler(afterCloseEvent -> positionCoursesDl.load());
        positionCoursesTableRemove.setConfirmation(true);
    }

    public void openCourseEditor(Entity item, String columnId) {
        Screen courseEditor = screenBuilders.editor(Course.class, this)
                .editEntity(((PositionCourse) item).getCourse()).build().show();
        courseEditor.addAfterCloseListener(afterCloseEvent -> positionCoursesDl.load());
    }
}