package kz.uco.tsadv.web.screens.insurancecontract;


import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.app.FileStorageService;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.RemoveOperation;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.export.ExportFormat;
import com.haulmont.cuba.gui.model.*;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.reports.app.service.ReportService;
import com.haulmont.reports.entity.Report;
import kz.uco.base.service.NotificationSenderAPIService;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.tb.Attachment;
import kz.uco.tsadv.modules.personal.dictionary.DicMICAttachmentStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicRelationshipType;
import kz.uco.tsadv.modules.personal.model.ContractConditions;
import kz.uco.tsadv.modules.personal.model.InsuranceContract;
import kz.uco.tsadv.modules.personal.model.InsuranceContractAdministrator;
import kz.uco.tsadv.modules.personal.model.InsuredPerson;
import kz.uco.tsadv.web.screens.insuredperson.InsuredPersonBulkEdit;
import kz.uco.tsadv.web.screens.insuredperson.InsuredPersonEdit;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

@UiController("tsadv$InsuranceContract.edit")
@UiDescriptor("insurance-contract-edit.xml")
@EditedEntityContainer("insuranceContractDc")
@LoadDataBeforeShow
public class InsuranceContractEdit extends StandardEditor<InsuranceContract> {
    protected static final Logger log = org.slf4j.LoggerFactory.getLogger(InsuranceContractEdit.class);
    @Inject
    protected InstanceLoader<InsuranceContract> insuranceContractDl;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected ReportService reportService;
    @Inject
    protected CollectionPropertyContainer<Attachment> attachmentsDc;
    @Inject
    protected DataGrid<ContractConditions> programConditionsDataGrid;
    @Inject
    protected DataGrid<InsuranceContractAdministrator> contractAdministratorDataGrid;
    @Inject
    protected Notifications notifications;
    @Inject
    protected Metadata metadata;
    @Inject
    protected CollectionPropertyContainer<InsuranceContractAdministrator> contractAdministratorDc;
    @Inject
    protected DateField<Date> expirationDateField;
    @Inject
    protected DateField<Date> availabilityPeriodToField;
    @Inject
    protected CollectionPropertyContainer<ContractConditions> conditionDc;
    @Inject
    protected CollectionLoader<InsuredPerson> insuredPersonsDl;
    @Inject
    protected InstanceContainer<InsuranceContract> insuranceContractDc;
    @Inject
    protected DataGrid<InsuredPerson> insuredPersonsTable;
    @Inject
    protected UiComponents uiComponents;
    @Inject
    protected ScreenBuilders screenBuilders;
    @Inject
    protected TimeSource timeSource;
    @Inject
    protected CommonService commonService;
    @Inject
    protected Button createBtnPerson;
    @Inject
    protected CollectionContainer<DicRelationshipType> relationTypeDc;
    @Inject
    protected ExportDisplay exportDisplay;
    @Inject
    protected FileStorageService fileStorageService;
    @Inject
    protected NotificationSenderAPIService notificationSenderAPIService;

    protected SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    protected boolean isCreateContract;

    @Subscribe
    public void onInit(InitEvent event) {
        insuredPersonsDl.setParameter("insuranceContractId", UUID.randomUUID());
    }

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        if (isCreateContract) {
            createBtnPerson.setEnabled(false);
        }
    }

    public void setParameter(boolean isCreateContract) {
        this.isCreateContract = isCreateContract;
    }

    @Subscribe("insuredPersonsTable.edit")
    public void onInsuredPersonsTableEdit(Action.ActionPerformedEvent event) {
        InsuredPerson selectItem = insuredPersonsTable.getSingleSelected();
        if (selectItem != null) {
            InsuredPersonEdit editorBuilder = (InsuredPersonEdit) screenBuilders.editor(insuredPersonsTable)
                    .editEntity(selectItem)
                    .build();
            editorBuilder.setParameter("editHr");
            editorBuilder.show();
        }
    }

    @Subscribe("insuredPersonsTable.bulk")
    public void onInsuredPersonsTableBulk(Action.ActionPerformedEvent event) {
        Set<InsuredPerson> bulks = insuredPersonsTable.getSelected();

        InsuredPersonBulkEdit bulkEdit = screenBuilders.editor(InsuredPerson.class, this)
                .withScreenClass(InsuredPersonBulkEdit.class)
                .withAfterCloseListener(e -> {
                    int bulkItemSize = bulks.size();
                    notifications.create().withCaption("Изменено " + bulkItemSize + " запись");
                    insuredPersonsDl.load();
                })
                .build();
        if (!bulks.isEmpty()) {
            bulkEdit.setParameter(bulks);
            bulkEdit.show();
        }
    }

    @Subscribe
    public void onBeforeCommitChanges(BeforeCommitChangesEvent event) {
        DataContext dataContext = event.getDataContext();
        contractAdministratorDc.getItems().forEach(dataContext::merge);
        conditionDc.getItems().forEach(dataContext::merge);
        attachmentsDc.getItems().forEach(dataContext::merge);
    }


    @Subscribe("insuredPersonsTable.create")
    public void onInsuredPersonsTableCreate(Action.ActionPerformedEvent event) {
        InsuredPerson insuredPerson = metadata.create(InsuredPerson.class);
        insuredPerson.setInsuranceContract(insuranceContractDc.getItem());
        insuredPerson.setDocumentType(insuranceContractDc.getItem().getDefaultDocumentType());
        insuredPerson.setAttachDate(timeSource.currentTimestamp());
        insuredPerson.setCompany(insuranceContractDc.getItem().getCompany());
        insuredPerson.setInsuranceProgram(insuranceContractDc.getItem().getInsuranceProgram());
        insuredPerson.setStatusRequest(commonService.getEntity(DicMICAttachmentStatus.class, "DRAFT"));

        InsuredPersonEdit editorBuilder = screenBuilders.editor(InsuredPerson.class, this)
                .withScreenClass(InsuredPersonEdit.class)
                .newEntity(insuredPerson)
                .build();

        editorBuilder.setParameter("joinHr");
        editorBuilder.show();

    }

    @Subscribe("startDateField")
    public void onStartDateFieldValueChange(HasValue.ValueChangeEvent<Date> event) {
        if (event.getValue() != null) {
            Instant i = Instant.ofEpochMilli(event.getValue().getTime());
            Date outRequestDate = Date.from(i);
            expirationDateField.setRangeStart(outRequestDate);
        }
    }


    @Subscribe("availabilityPeriodFromField")
    public void onAvailabilityPeriodFromFieldValueChange(HasValue.ValueChangeEvent<Date> event) {
        if (event.getValue() != null) {
            Instant i = Instant.ofEpochMilli(event.getValue().getTime());
            Date outRequestDate = Date.from(i);
            availabilityPeriodToField.setRangeStart(outRequestDate);
        }
    }

    @Subscribe("commitBtn")
    public void onCommitBtnClick(Button.ClickEvent event) {
        createBtnPerson.setEnabled(true);
    }


    @Install(to = "programConditionsDataGrid.remove", subject = "afterActionPerformedHandler")
    protected void programConditionsDataGridRemoveAfterActionPerformedHandler(RemoveOperation.AfterActionPerformedEvent<ContractConditions> afterActionPerformedEvent) {
        insuredPersonsDl.load();
    }

    @Subscribe("insuredPersonsTable.sendNotification")
    protected void onInsuredPersonsTableSendNotification(Action.ActionPerformedEvent event) {
        LoadContext<Report> reportLoadContext = LoadContext.create(Report.class)
                .setQuery(LoadContext.createQuery("select e from report$Report e where e.code = :code")
                        .setParameter("code", "DMS"))
                .setView("report.edit");
        Report report = dataManager.load(reportLoadContext);
        if (report != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("contractNumber", insuranceContractDc.getItem().getContract());
            params.put("date", insuranceContractDc.getItem().getSignDate() != null
                    ? dateFormat.format(insuranceContractDc.getItem().getSignDate())
                    : "");
            FileDescriptor fileDescriptor = reportService.createAndSaveReport(report,
                    ParamsMap.of("isp", insuredPersonsTable.getSelected()), "Список исключенных");
            if (fileDescriptor != null) {
                EmailAttachment[] emailAttachments = new EmailAttachment[0];
                emailAttachments = getEmailAttachments(fileDescriptor, emailAttachments);
                notificationSenderAPIService.sendParametrizedNotification("insurance.person.excluded.dms",
                        insuranceContractDc.getItem().getInsurerContacts(), params, emailAttachments);
            }
        }
    }

    protected EmailAttachment[] getEmailAttachments(FileDescriptor fileDescriptor, EmailAttachment[] emailAttachments) {
        try {
            EmailAttachment emailAttachment = new EmailAttachment(fileStorageService.loadFile(fileDescriptor), "Список исключенных");
            emailAttachments = ArrayUtils.add(emailAttachments, emailAttachment);
        } catch (FileStorageException e) {
            log.error("Error", e);
        }
        return emailAttachments;
    }

    @Install(to = "insuredPersonsTable.contractFieldPerson", subject = "columnGenerator")
    protected Component insuredPersonsTableContractFieldPersonColumnGenerator(DataGrid.ColumnGeneratorEvent<InsuredPerson> event) {
        LinkButton linkButton = uiComponents.create(LinkButton.class);
        linkButton.setCaption(event.getItem().getInsuranceContract().getContract());
        linkButton.setAction(new BaseAction("contractFieldPerson").withHandler(e -> {
            InsuredPersonEdit editorBuilder = (InsuredPersonEdit) screenBuilders.editor(insuredPersonsTable)
                    .editEntity(event.getItem())
                    .build();
            editorBuilder.setParameter("editHr");
            editorBuilder.show();
        }));
        return linkButton;
    }

    @Install(to = "insuredPersonsTable.statementFileField", subject = "columnGenerator")
    protected Component insuredPersonsTableStatementFileFieldColumnGenerator(DataGrid.ColumnGeneratorEvent<InsuredPerson> event) {
        CssLayout cssLayout = uiComponents.create(CssLayout.class);

        cssLayout.setHeight("300px");
        cssLayout.setWidthFull();
        FlowBoxLayout hBoxLayout = uiComponents.create(FlowBoxLayout.class);
        cssLayout.add(hBoxLayout);
        List<FileDescriptor> fileDescriptorList = event.getItem().getFile();

        if (event.getItem().getRelative().getCode().equals("PRIMARY") && event.getItem().getStatementFile() != null) {
            LinkButton linkButton = uiComponents.create(LinkButton.class);
            linkButton.setCaption(event.getItem().getStatementFile().getName());
            linkButton.setWidthAuto();
            linkButton.setHeightAuto();
            linkButton.setAction(new BaseAction("statementFileField")
                    .withHandler(actionPerformedEvent -> exportDisplay.show(event.getItem().getStatementFile(), ExportFormat.OCTET_STREAM)));
            hBoxLayout.add(linkButton);
        } else {
            if (fileDescriptorList != null && !fileDescriptorList.isEmpty())
                fileDescriptorList.forEach(e -> {
                    LinkButton button = uiComponents.create(LinkButton.class);
                    button.setWidthAuto();
                    button.setHeightAuto();
                    button.setCaption(e.getName());
                    button.setAction(new BaseAction("statementFileField")
                            .withHandler(actionPerformedEvent -> exportDisplay.show(e, ExportFormat.OCTET_STREAM)));
                    hBoxLayout.add(button);
                });
        }

        return cssLayout;
    }

    @Subscribe(id = "insuranceContractDl", target = Target.DATA_LOADER)
    protected void onInsuranceContractDlPostLoad(InstanceLoader.PostLoadEvent<InsuranceContract> event) {
        insuredPersonsDl.setParameter("insuranceContractId", event.getLoadedEntity().getId());
        insuredPersonsDl.load();
    }
}