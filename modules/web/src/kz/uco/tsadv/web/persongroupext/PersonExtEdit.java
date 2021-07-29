package kz.uco.tsadv.web.persongroupext;

import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.service.NationalIdentifierService;

import javax.inject.Inject;

@UiController("base$PersonExt.edit")
@UiDescriptor("person-ext-edit.xml")
@EditedEntityContainer("personExtDc")
@LoadDataBeforeShow
public class PersonExtEdit extends StandardEditor<PersonExt> {
    @Inject
    private NationalIdentifierService nationalIdentifierService;
    @Inject
    private InstanceContainer<PersonExt> personExtDc;
    @Inject
    private Notifications notifications;
    @Inject
    private MessageBundle messageBundle;

    @Subscribe
    public void onBeforeCommitChanges(BeforeCommitChangesEvent event) {
        PersonExt item = personExtDc.getItem();
        if (!nationalIdentifierService.checkNationalIdentifier(item.getNationalIdentifier(), item.getDateOfBirth(),
                item.getSex())) {
            event.preventCommit();
            notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                    .withCaption(messageBundle.getMessage("iinNotCorrect")).show();
        }
    }
}