package kz.uco.tsadv.web.screens.personexception;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.performance.model.PersonException;

@UiController("tsadv_PersonException.edit")
@UiDescriptor("person-exception-edit.xml")
@EditedEntityContainer("personExceptionDc")
@LoadDataBeforeShow
public class PersonExceptionEdit extends StandardEditor<PersonException> {
}