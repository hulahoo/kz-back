package kz.uco.tsadv.web.screens.insurancecontractadministrator;

import com.haulmont.cuba.gui.components.CheckBox;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.InsuranceContractAdministrator;

import javax.inject.Inject;

@UiController("tsadv$InsuranceContractAdministrator.edit")
@UiDescriptor("insurance-contract-administrator-edit.xml")
@EditedEntityContainer("insuranceContractAdministratorDc")
@LoadDataBeforeShow
public class InsuranceContractAdministratorEdit extends StandardEditor<InsuranceContractAdministrator> {

    @Inject
    private CheckBox notifyAboutNewAttachmentsField;

    @Subscribe
    public void onAfterCommitChanges(AfterCommitChangesEvent event) {
        notifyAboutNewAttachmentsField.setValue(true);
    }

}