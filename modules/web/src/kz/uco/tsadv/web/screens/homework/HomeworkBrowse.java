package kz.uco.tsadv.web.screens.homework;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.model.Homework;

@UiController("tsadv_Homework.browse")
@UiDescriptor("homework-browse.xml")
@LookupComponent("homeworkTable")
@LoadDataBeforeShow
public class HomeworkBrowse extends StandardLookup<Homework> {
}