package kz.uco.tsadv.web.screens.personlanguage;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.PersonLanguage;

@UiController("tsadv_PersonLanguage.edit")
@UiDescriptor("person-language-edit.xml")
@EditedEntityContainer("personLanguageDc")
@LoadDataBeforeShow
public class PersonLanguageEdit extends StandardEditor<PersonLanguage> {
}