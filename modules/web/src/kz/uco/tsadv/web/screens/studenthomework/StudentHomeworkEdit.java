package kz.uco.tsadv.web.screens.studenthomework;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.model.StudentHomework;

@UiController("tsadv_StudentHomework.edit")
@UiDescriptor("student-homework-edit.xml")
@EditedEntityContainer("studentHomeworkDc")
@LoadDataBeforeShow
public class StudentHomeworkEdit extends StandardEditor<StudentHomework> {
}