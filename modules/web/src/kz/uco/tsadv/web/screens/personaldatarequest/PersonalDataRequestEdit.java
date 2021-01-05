package kz.uco.tsadv.web.screens.personaldatarequest;

import com.haulmont.cuba.gui.components.NotificationFacet;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.PersonalDataRequest;

import javax.inject.Inject;

@UiController("tsadv$PersonalDataRequest.edit")
@UiDescriptor("personal-data-request-edit.xml")
@EditedEntityContainer("personalDataRequestDc")
@LoadDataBeforeShow
public class PersonalDataRequestEdit extends StandardEditor<PersonalDataRequest> {
    @Inject
    protected InstanceContainer<PersonalDataRequest> personalDataRequestDc;
    @Inject
    protected TextField<String> nationalIdentifierField;
    @Inject
    protected NotificationFacet notificationTray;


    @Subscribe
    protected void onAfterShow(AfterShowEvent event) {
        String nationalIdentifier = personalDataRequestDc.getItem().getNationalIdentifier();
        if (nationalIdentifier != null && !nationalIdentifier.isEmpty()) {
            nationalIdentifierField.setEditable(false);
        }
        personalDataRequestDc.addItemPropertyChangeListener(personalDataRequestItemPropertyChangeEvent -> {
            if ("citizenship".equals(personalDataRequestItemPropertyChangeEvent.getProperty())) {
                notificationTray.show();
                nationalIdentifierField.setEditable(true);
            }
        });
        if (personalDataRequestDc.getItem().getNationalIdentifier() != null &&
                !personalDataRequestDc.getItem().getNationalIdentifier().isEmpty()) {
            nationalIdentifierField.setEditable(false);
        }
    }

}