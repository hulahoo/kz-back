package kz.uco.tsadv.web.screens.insurancecontractadministrator;

import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.InsuranceContractAdministrator;

import javax.inject.Inject;

@UiController("tsadv$InsuranceContractAdministrator.edit")
@UiDescriptor("insurance-contract-administrator-edit.xml")
@EditedEntityContainer("insuranceContractAdministratorDc")
@LoadDataBeforeShow
public class InsuranceContractAdministratorEdit extends StandardEditor<InsuranceContractAdministrator> {
    @Inject
    protected InstanceContainer<InsuranceContractAdministrator> insuranceContractAdministratorDc;

    @Subscribe
    protected void onAfterShow(AfterShowEvent event) {
        if (PersistenceHelper.isNew(insuranceContractAdministratorDc.getItem())) {
            insuranceContractAdministratorDc.getItem().setNotifyAboutNewAttachments(true);
        }
    }
}