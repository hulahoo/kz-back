package kz.uco.tsadv.web.screens.personcontact;

import com.haulmont.cuba.gui.components.HasValue;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicPhoneType;
import kz.uco.tsadv.modules.personal.model.PersonContact;

import javax.inject.Inject;

@UiController("tsadv$PersonContactPersonData.edit")
@UiDescriptor("person-contact-person-data-edit.xml")
@EditedEntityContainer("personContactDc")
@LoadDataBeforeShow
public class PersonContactPersonDataEdit extends StandardEditor<PersonContact> {

    @Inject
    protected TextField<String> contactEmailValueField;
    @Inject
    protected TextField<String> contactPhoneValueField;
    @Inject
    protected TextField<String> contactValueField;
    @Inject
    protected InstanceContainer<PersonContact> personContactDc;
    protected boolean started = false;

    @Subscribe
    protected void onInitEntity(InitEntityEvent<PersonContact> event) {
        event.getEntity().setStartDate(CommonUtils.getSystemDate());
        event.getEntity().setEndDate(CommonUtils.getEndOfTime());
    }

    @Subscribe
    protected void onAfterShow(AfterShowEvent event) {
        started = true;
        if (personContactDc.getItem().getType() != null
                && personContactDc.getItem().getType().getCode() != null) {
            if (personContactDc.getItem().getType().getCode().toLowerCase().contains("email")) {
                contactEmailValueField.setVisible(true);
                contactPhoneValueField.setVisible(false);
                contactValueField.setVisible(false);
            } else if (personContactDc.getItem().getType().getCode().toLowerCase().contains("mobile")) {
                contactEmailValueField.setVisible(false);
                contactPhoneValueField.setVisible(true);
                contactValueField.setVisible(false);
            } else {
                contactEmailValueField.setVisible(false);
                contactPhoneValueField.setVisible(false);
                contactValueField.setVisible(true);
            }
        } else {
            contactEmailValueField.setVisible(false);
            contactPhoneValueField.setVisible(false);
            contactValueField.setVisible(true);
        }
    }


    @Subscribe("typeField")
    protected void onTypeFieldValueChange(HasValue.ValueChangeEvent<DicPhoneType> event) {
        if (!started) {
            return;
        }
        if (event.getValue() != null && event.getValue().getCode() != null) {
            if (event.getValue().getCode().toLowerCase().contains("email")) {
                if (event.getPrevValue() == null || event.getPrevValue().getCode() == null
                        || !event.getPrevValue().getCode().toLowerCase().contains("email")) {
                    personContactDc.getItem().setContactValue(null);
                }
                contactEmailValueField.setVisible(true);
                contactPhoneValueField.setVisible(false);
                contactValueField.setVisible(false);
            } else if (event.getValue().getCode().toLowerCase().contains("mobile")) {
                if (event.getPrevValue() == null || event.getPrevValue().getCode() == null
                        || !event.getPrevValue().getCode().toLowerCase().contains("mobile")) {
                    personContactDc.getItem().setContactValue(null);
                }
                contactEmailValueField.setVisible(false);
                contactPhoneValueField.setVisible(true);
                contactValueField.setVisible(false);
            } else {
                contactEmailValueField.setVisible(false);
                contactPhoneValueField.setVisible(false);
                contactValueField.setVisible(true);
            }
        } else {
            contactEmailValueField.setVisible(false);
            contactPhoneValueField.setVisible(false);
            contactValueField.setVisible(true);
        }
    }

}