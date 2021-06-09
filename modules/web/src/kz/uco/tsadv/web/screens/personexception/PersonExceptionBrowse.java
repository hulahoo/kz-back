package kz.uco.tsadv.web.screens.personexception;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.performance.model.PersonException;

@UiController("tsadv_PersonException.browse")
@UiDescriptor("person-exception-browse.xml")
@LookupComponent("personExceptionsTable")
@LoadDataBeforeShow
public class PersonExceptionBrowse extends StandardLookup<PersonException> {
}