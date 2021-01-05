package kz.uco.tsadv.web.screens.personrelativeshaveproperty;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.PersonRelativesHaveProperty;

@UiController("tsadv_PersonRelativesHaveProperty.edit")
@UiDescriptor("person-relatives-have-property-edit.xml")
@EditedEntityContainer("personRelativesHavePropertyDc")
@LoadDataBeforeShow
public class PersonRelativesHavePropertyEdit extends StandardEditor<PersonRelativesHaveProperty> {
}