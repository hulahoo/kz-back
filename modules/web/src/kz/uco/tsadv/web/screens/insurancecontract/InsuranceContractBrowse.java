package kz.uco.tsadv.web.screens.insurancecontract;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.GlobalConfig;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.LookupComponent;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.base.service.NotificationService;
import kz.uco.tsadv.config.FrontConfig;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.personal.model.InsuranceContract;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UiController("tsadv$InsuranceContract.browse")
@UiDescriptor("insurance-contract-browse.xml")
@LookupComponent("insuranceContractsTable")
@LoadDataBeforeShow
public class InsuranceContractBrowse extends StandardLookup<InsuranceContract> {
    @Inject
    protected GlobalConfig globalConfig;
    @Inject
    protected FrontConfig frontConfig;
    @Inject
    private ScreenBuilders screenBuilders;
    @Inject
    private DataGrid<InsuranceContract> insuranceContractsTable;
    @Inject
    private Button removeBtn;
    @Inject
    private UiComponents uiComponents;
    @Inject
    private Metadata metadata;
    @Inject
    private DataManager dataManager;
    @Inject
    protected NotificationService notificationService;

    @Subscribe(id = "insuranceContractsDc", target = Target.DATA_CONTAINER)
    protected void onInsuranceContractsDcItemChange(InstanceContainer.ItemChangeEvent<InsuranceContract> event) {
        if (event.getItem() != null) {
            InsuranceContract insuranceContract = dataManager.load(InsuranceContract.class)
                    .query("select e from tsadv$InsuranceContract e " +
                            "   join tsadv$InsuredPerson p on p.insuranceContract = e " +
                            "where e.id = :contractId")
                    .parameter("contractId", event.getItem().getId())
                    .view(View.MINIMAL)
                    .list().stream().findFirst().orElse(null);

            removeBtn.setEnabled(insuranceContract == null);
        }
    }

    @Subscribe("insuranceContractsTable.create")
    public void onInsuranceContractsTableCreate(Action.ActionPerformedEvent event) {
        InsuranceContract contract = metadata.create(InsuranceContract.class);
        InsuranceContractEdit contractEdit = screenBuilders.editor(insuranceContractsTable)
                .withScreenClass(InsuranceContractEdit.class)
                .editEntity(contract)
                .build();

        contractEdit.setParameter(true);
        contractEdit.show();
    }

    @Subscribe("insuranceContractsTable.sendNotification")
    protected void onInsuranceContractsTableSendNotification(Action.ActionPerformedEvent event) {
        List<TsadvUser> tsadvUsers = dataManager.load(TsadvUser.class)
                .query("select e from tsadv$UserExt e " +
                        " where e.personGroup.company = :company")
                .parameter("company", insuranceContractsTable.getSingleSelected().getCompany())
                .view("tsadvUserExt-view")
                .list();
        if (!tsadvUsers.isEmpty()) {
            tsadvUsers.forEach(tsadvUser -> {
                Map<String, Object> params = new HashMap<>();
                params.put("fio", tsadvUser.getPersonGroup().getFullName());
                params.put("linkRu", "<a href=\"" + frontConfig.getFrontAppUrl() + "\" target=\"_blank\">????????????</a>");
                notificationService.sendParametrizedNotification("insurance.contract.dms",
                        tsadvUser, params);
            });
        }
    }

    @Install(to = "insuranceContractsTable.contractField", subject = "columnGenerator")
    protected Component insuranceContractsTableContractFieldColumnGenerator(DataGrid.ColumnGeneratorEvent<InsuranceContract> event) {
        LinkButton linkButton = uiComponents.create(LinkButton.class);
        linkButton.setCaption(event.getItem().getContract());
        linkButton.setAction(new BaseAction("contractField").withHandler(e -> {
            screenBuilders.editor(insuranceContractsTable)
                    .withScreenClass(InsuranceContractEdit.class)
                    .editEntity(event.getItem())
                    .build().show();
        }));
        return linkButton;
    }

//
//    public void showInsuredPersons() {
//        InsuranceContract contract = insuranceContractsTable.getSingleSelected();
//
//        InsuredPersonBrowse insuredPersonBrowse = screenBuilders.screen(this)
//                .withScreenClass(InsuredPersonBrowse.class)
//                .build();
//
//        if (contract != null && contract.getCompany() != null){
//            insuredPersonBrowse.setParameter(contract);
//            insuredPersonBrowse.show();
//        }
//    }
}