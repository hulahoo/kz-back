package kz.uco.tsadv.web.screens.homework;

import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.model.InstanceLoader;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.model.Homework;
import kz.uco.tsadv.modules.learning.model.StudentHomework;

import javax.inject.Inject;

@UiController("tsadv_Homework.edit")
@UiDescriptor("homework-edit.xml")
@EditedEntityContainer("homeworkDc")
public class HomeworkEdit extends StandardEditor<Homework> {
    @Inject
    protected InstanceLoader<Homework> homeworkDl;
    @Inject
    protected CollectionLoader<StudentHomework> studentHomeworkDl;
    @Inject
    protected InstanceContainer<Homework> homeworkDc;
    @Inject
    protected ScreenBuilders screenBuilders;
    @Inject
    protected Table<StudentHomework> studentTable;

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        homeworkDl.load();
        studentHomeworkDl.setParameter("homework", homeworkDc.getItem());
        studentHomeworkDl.load();
    }

    @Subscribe("studentTable.create")
    protected void onStudentTableCreate(Action.ActionPerformedEvent event) {
        screenBuilders.editor(studentTable)
                .newEntity()
                .withInitializer(studentHomework -> studentHomework.setHomework(homeworkDc.getItem())).build().show()
                .addAfterCloseListener(afterCloseEvent -> studentHomeworkDl.load());
    }
}