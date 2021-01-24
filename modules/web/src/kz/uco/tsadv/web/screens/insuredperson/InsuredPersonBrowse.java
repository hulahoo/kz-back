package kz.uco.tsadv.web.screens.insuredperson;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.BulkEditors;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.actions.list.BulkEditAction;
import com.haulmont.cuba.gui.app.core.bulk.ColumnsMode;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import groovy.lang.MetaProperty;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.personal.dictionary.DicVHIAttachmentStatus;
import kz.uco.tsadv.modules.personal.model.InsuranceContract;
import kz.uco.tsadv.modules.personal.model.InsuredPerson;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

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
    @Named("insuredPersonsTable.bulkEdit")
    private BulkEditAction insuredPersonBulkEdit;
    @Inject
    private BulkEditors bulkEditors;


    @Subscribe
    public void onInit(InitEvent event) {
        insuredPersonBulkEdit.setOpenMode(OpenMode.DIALOG);
//        insuredPersonBulkEdit.setIncludeProperties(Arrays.asList("attachDate", "statusRequest", "exclusionDate", "comment"));
        insuredPersonBulkEdit.setColumnsMode(ColumnsMode.TWO_COLUMNS);
    }

//    @Install(to = "insuredPersonsTable.bulkEdit", subject = "fieldSorter")
//    private Map<MetaProperty, Integer> insuredPersonsTableBulkEditFieldSorter(List<MetaProperty> properties){
//        Map<MetaProperty, Integer> result = new HashMap<>();
//        for (MetaProperty property : properties){
//            switch (property.getName()) {
//                case "attachDate":
//                    result.put(property, 0);
//                    break;
//                case "statusRequest":
//                    result.put(property, 1);
//                    break;
//                case "exclusionDate":
//                    result.put(property, 2);
//                    break;
//                case "comment":
//                    result.put(property, 3);
//                    break;
//            }
//        }
//        return result;
//    }



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