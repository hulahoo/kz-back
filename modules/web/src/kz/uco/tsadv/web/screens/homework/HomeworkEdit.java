package kz.uco.tsadv.web.screens.homework;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.model.Homework;

@UiController("tsadv_Homework.edit")
@UiDescriptor("homework-edit.xml")
@EditedEntityContainer("homeworkDc")
@LoadDataBeforeShow
public class HomeworkEdit extends StandardEditor<Homework> {
}