package kz.uco.tsadv.web.screens.insuredperson;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.personal.dictionary.DicVHIAttachmentStatus;
import kz.uco.tsadv.modules.personal.model.InsuranceContract;
import kz.uco.tsadv.modules.personal.model.InsuredPerson;

import javax.inject.Inject;

@UiController("tsadv$InsuredPerson.browse")
@UiDescriptor("insured-person-browse.xml")
@LookupComponent("insuredPersonsTable")
@LoadDataBeforeShow
public class InsuredPersonBrowse extends StandardLookup<InsuredPerson> {
    @Inject
    private CollectionLoader<InsuredPerson> insuredPersonsDl;
    @Inject
    private ScreenBuilders screenBuilders;
    @Inject
    private Metadata metadata;
    @Inject
    private GroupTable<InsuredPerson> insuredPersonsTable;
    @Inject
    private CommonService commonService;
    protected InsuranceContract insuranceContract;
    @Inject
    private TimeSource timeSource;

    @Subscribe
    public void onInit(InitEvent event) {
        insuredPersonsDl.setParameter("myCompany",null);
    }

    public void setParameter(InsuranceContract contract) {
        insuranceContract = contract;
        insuredPersonsDl.setParameter("myCompany", contract.getCompany());
        insuredPersonsDl.load();
    }

    @Subscribe("insuredPersonsTable.create")
    public void onInsuredPersonsTableCreate(Action.ActionPerformedEvent event) {
        InsuredPerson insuredPerson = metadata.create(InsuredPerson.class);
        insuredPerson.setInsuranceContract(insuranceContract);
//        insuredPerson.setAddressType(insuranceContract.getDefaultAddress());
        insuredPerson.setDocumentType(insuranceContract.getDefaultDocumentType());
        insuredPerson.setAttachDate(timeSource.currentTimestamp());
        insuredPerson.setCompany(insuranceContract.getCompany());
        insuredPerson.setInsuranceProgram(insuranceContract.getInsuranceProgram());
        insuredPerson.setStatusRequest(commonService.getEntity(DicVHIAttachmentStatus.class, "DRAFT"));
        screenBuilders.editor(insuredPersonsTable)
                .newEntity(insuredPerson)
                .build()
                .show();
    }
}