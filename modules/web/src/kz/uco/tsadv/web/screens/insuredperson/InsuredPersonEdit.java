package kz.uco.tsadv.web.screens.insuredperson;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.export.ExportFormat;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.base.entity.dictionary.DicRegion;
import kz.uco.base.entity.dictionary.DicSex;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.tb.Attachment;

import kz.uco.tsadv.modules.personal.dictionary.DicDocumentType;
import kz.uco.tsadv.modules.personal.dictionary.DicMICAttachmentStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicRelationshipType;
import kz.uco.tsadv.modules.personal.enums.RelativeType;
import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.web.screens.insurancecontract.InsuranceContractEdit;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@UiController("tsadv$InsuredPerson.edit")
@UiDescriptor("insured-person-edit.xml")
@EditedEntityContainer("insuredPersonDc")
@LoadDataBeforeShow
public class InsuredPersonEdit extends StandardEditor<InsuredPerson> {

    @Inject
    private CollectionLoader<InsuranceContract> insuranceContractDl;
    @Inject
    private LookupField<InsuranceContract> insuranceContractField;
    @Inject
    private TextField<String> firstNameField;
    @Inject
    private TextField<String> middleNameField;
    @Inject
    private TextField<String> secondNameField;
    @Inject
    private LookupField<JobGroup> jobField;
    @Inject
    private LookupField<DicSex> sexField;
    @Inject
    private DateField<Date> birthdateField;
    @Inject
    private TextField<String> iinField;
    @Inject
    private Field<String> documentNumberField;
    @Inject
    private LookupField<DicDocumentType> documentTypeField;
    @Inject
    private PickerField<PersonGroupExt> employeeField;
    @Inject
    private DataManager dataManager;
    @Inject
    private LookupField<DicRelationshipType> relativeField;
    @Inject
    private LookupField<DicCompany> companyField;
    @Inject
    private DateField<Date> assignDateField;
    @Inject
    private Field<RelativeType> typeField;
    @Inject
    private DateField<Date> startDateField;
    @Inject
    private DateField<Date> endDateField;
    @Inject
    private InstanceContainer<InsuredPerson> insuredPersonDc;
    @Inject
    private LookupField<Address> addressTypeField;
    @Inject
    private TextArea<String> addressField;
    @Inject
    private CollectionContainer<Address> addressDc;
    @Inject
    private TextArea<String> insuranceProgramField;
    @Inject
    private LookupField<DicRegion> regionField;
    @Inject
    private TextField<BigDecimal> totalAmountField;
    @Inject
    private CollectionContainer<InsuredPerson> insuredPersonMemberDc;
    @Inject
    private CollectionLoader<InsuredPerson> insuredPersonMemberDl;
    @Inject
    GroupBoxLayout familyMemberInformationGroup;
    @Inject
    GroupBoxLayout informationOnMIC;
    @Inject
    private ScreenBuilders screenBuilders;
    @Inject
    private CommonService commonService;
    @Inject
    private TimeSource timeSource;
    @Inject
    private DateField<Date> attachDateField;
    private String whichButton;
    @Inject
    private TextField<String> commentField;
    @Inject
    private DateField<Date> exclusionDateField;
    @Inject
    private UserSession userSession;
    @Inject
    private GroupTable<InsuredPerson> insuredPersonTable;
    @Inject
    private Form form2;
    @Inject
    private UiComponents uiComponents;
    @Inject
    private ExportDisplay exportDisplay;
    @Inject
    private Button createButton;
    @Inject
    private Button editButton;
    @Inject
    private Button removeButton;
    @Inject
    private ComponentsFactory componentsFactory;


    public void setParameter(String whichButton) {
        this.whichButton = whichButton;
    }


    @Subscribe
    public void onAfterShow(AfterShowEvent event) {

        LocalDateTime ldt = LocalDateTime.ofInstant(timeSource.currentTimestamp().toInstant(), ZoneId.systemDefault());
        Date outRequestDate = Date.from(ldt.minusDays(1).atZone(ZoneId.systemDefault()).toInstant());
        attachDateField.setRangeStart(outRequestDate);
        Date today = timeSource.currentTimestamp();
        attachDateField.setValue(today);

        if ("joinMember".equals(whichButton)) {
            familyMemberInformationGroup.setVisible(true);
            employeeField.setEditable(false);
            relativeField.setVisible(false);
            documentTypeField.setEditable(false);
            documentNumberField.setEditable(false);
            addressTypeField.setEditable(false);
            addressField.setEditable(false);
            regionField.setEditable(false);

            exclusionDateField.setVisible(false);
            commentField.setVisible(false);
        } else if ("openEmployee".equals(whichButton)) {
            editButton.setVisible(false);
            removeButton.setVisible(false);
            createButton.setVisible(false);
            relativeField.setVisible(false);
            if (addressTypeField.getValue() == null) {
                addressTypeField.setVisible(false);
                addressField.setCaption("Домашний адрес");
            }
            if (insuredPersonMemberDc.getItems().size() != 0) {
                familyMemberInformationGroup.setVisible(true);
            } else {
                familyMemberInformationGroup.setVisible(false);
            }
        } else if ("joinEmployee".equals(whichButton)) {
            familyMemberInformationGroup.setVisible(false);
            employeeField.setEditable(false);
            relativeField.setVisible(false);
            addressTypeField.setEditable(true);
            regionField.setEditable(true);
            addressField.setEditable(true);
            documentNumberField.setEditable(true);
            documentTypeField.setEditable(true);
            totalAmountField.setVisible(false);
            insuranceContractField.setEditable(true);
            documentNumberField.setRequired(true);

        } else if ("joinHr".equals(whichButton)) {
            attachDateField.setEditable(true);
            familyMemberInformationGroup.setVisible(false);
            commentField.setVisible(false);
        } else if ("editHr".equals(whichButton)) {
            attachDateField.setEditable(true);
            exclusionDateField.setVisible(true);
            commentField.setVisible(true);
            if (addressTypeField.getValue() == null) {
                addressTypeField.setVisible(false);
                addressField.setCaption("Домашний адрес");
            }
            if (insuredPersonDc.getItemOrNull().getRelative().getCode().equals("PRIMARY")) {
                familyMemberInformationGroup.setVisible(true);
            } else if (!insuredPersonDc.getItemOrNull().getRelative().getCode().equals("PRIMARY")) {
                familyMemberInformationGroup.setVisible(false);
            }
        } else {
            familyMemberInformationGroup.setVisible(true);

            employeeField.setEditable(false);
            relativeField.setVisible(false);
            documentTypeField.setEditable(false);
            documentNumberField.setEditable(false);
            addressTypeField.setEditable(false);
            addressField.setEditable(false);
            regionField.setEditable(false);

            exclusionDateField.setVisible(false);
            commentField.setVisible(false);
            insuranceContractField.setEditable(false);
            if (addressTypeField.getValue() == null) {
                addressTypeField.setVisible(false);
                addressField.setCaption("Домашний адрес");
            }
        }

        if (insuredPersonDc.getItemOrNull() != null && insuredPersonDc.getItem().getEmployee() != null) {
            insuredPersonMemberDl.setParameter("employeeId", insuredPersonDc.getItem().getEmployee().getId());
            insuredPersonMemberDl.setParameter("relativeType", RelativeType.MEMBER);
            insuredPersonMemberDl.setParameter("employeeContractId", insuredPersonDc.getItem().getInsuranceContract().getId());
            insuredPersonMemberDl.load();
        } else if (employeeField.getValue() != null && insuranceContractField.getValue() != null) {
            insuredPersonMemberDl.setParameter("employeeId", employeeField.getValue().getId());
            insuredPersonMemberDl.setParameter("relativeType", RelativeType.MEMBER);
            insuredPersonMemberDl.setParameter("employeeContractId", insuranceContractField.getValue().getId());
            insuredPersonMemberDl.load();
        }
    }

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {

        if (typeField.getValue() != null && RelativeType.EMPLOYEE == typeField.getValue()) {
        } else if (typeField.getValue() != null && RelativeType.MEMBER == typeField.getValue()) {
            addressTypeField.setEditable(false);
            addressField.setEditable(false);
            documentNumberField.setEditable(false);
            documentTypeField.setEditable(false);
            insuranceContractField.setEditable(false);
            regionField.setEditable(false);
            totalAmountField.setVisible(false);
            relativeField.setVisible(false);
            employeeField.setEditable(false);
        } else if (typeField.getValue() == null) {
            insuranceContractField.setEditable(true);
        }
//        documentTypeDl.setParameter("personGroupId", insuredPersonDc.getItem().getEmployee().getPersonDocuments());
        if (employeeField.getValue() != null &&
                relativeField.getValue() != null && relativeField.getValue().getCode().equals("PRIMARY")) {
            if (addressDc.getItems().size() == 0) {
                addressTypeField.setRequired(false);
                addressTypeField.setVisible(false);
                addressField.setRequired(true);
                addressField.setCaption("Домашний адрес");
            }
            if (employeeField.getValue().getPersonDocuments().size() == 0) {
//                documentNumberField.setEditable(true);
//                documentTypeDl.setParameter("personGroupId", null);
            }
        }
    }

    @Subscribe(id = "insuredPersonMemberDc", target = Target.DATA_CONTAINER)
    public void onInsuredPersonMemberDcItemChange(InstanceContainer.ItemChangeEvent<InsuredPerson> event) {
        if ("openEmployee".equals(whichButton)) {
            editButton.setEnabled(false);
            removeButton.setEnabled(false);
        }
    }


    @Subscribe(id = "insuredPersonMemberDl", target = Target.DATA_LOADER)
    public void onInsuredPersonMemberDlPostLoad(CollectionLoader.PostLoadEvent<InsuredPerson> event) {
        if (insuranceContractField.getValue() != null) {
            totalAmountField.setValue(BigDecimal.valueOf(0));
            BigDecimal totalAmount = totalAmountField.getValue();
            for (InsuredPerson person : insuredPersonMemberDc.getItems()) {
                if (person.getAmount() != null) {
                    totalAmount = totalAmount.add(person.getAmount());
                }
            }
            totalAmountField.setValue(totalAmount);
        }
    }


    @Subscribe("insuredPersonTable.create")
    public void onInsuredPersonTableCreate(Action.ActionPerformedEvent event) {
        insuredPersonDc.getItem();
        if (insuredPersonDc.getItem().getCompany() != null) {
            insuranceContractDl.setParameter("companyId", insuredPersonDc.getItem().getCompany());
        }
        InsuredPersonMemberEdit insuredPersonMemberEdit = screenBuilders.editor(InsuredPerson.class, this)
                .withScreenClass(InsuredPersonMemberEdit.class)
                .editEntity(newInsuredPersonMember())
                .withAfterCloseListener(e -> {
                    insuredPersonMemberDl.load();
                })
                .build();
        setParameterAndShow(insuredPersonMemberEdit);

    }


    @Subscribe("insuredPersonTable.edit")
    public void onInsuredPersonTableEdit(Action.ActionPerformedEvent event) {
        InsuredPerson insuredPerson = insuredPersonTable.getSingleSelected();
        assert insuredPerson != null;
        InsuredPersonMemberEdit insuredPersonMemberEdit = screenBuilders.editor(InsuredPerson.class, this)
                .withScreenClass(InsuredPersonMemberEdit.class)
                .editEntity(insuredPerson)
                .withAfterCloseListener(e -> {
                    insuredPersonMemberDl.load();
                })
                .build();
        if (insuredPerson.getAmount() != BigDecimal.valueOf(0)) {
            insuredPersonMemberEdit.setParameter(false);
        } else {
            insuredPersonMemberEdit.setParameter(true);
        }
        insuredPersonMemberEdit.show();

    }


    protected void setParameterAndShow(InsuredPersonMemberEdit insuredPersonMemberEdit) {
        if (insuranceContractField.getValue() != null && insuranceContractField.getValue().getCountOfFreeMembers() != null &&
                insuranceContractField.getValue().getCountOfFreeMembers() > insuredPersonMemberDc.getItems().size()) {
            insuredPersonMemberEdit.setParameter(true);
            insuredPersonMemberEdit.show();
        } else if (insuranceContractField.getValue() != null && insuranceContractField.getValue().getCountOfFreeMembers() != null &&
                insuranceContractField.getValue().getCountOfFreeMembers() <= insuredPersonMemberDc.getItems().size()) {
            insuredPersonMemberEdit.setParameter(false);
            insuredPersonMemberEdit.show();
        }
    }


    protected InsuredPerson newInsuredPersonMember() {
        InsuredPerson item = dataManager.create(InsuredPerson.class);
        DicMICAttachmentStatus status = commonService.getEntity(DicMICAttachmentStatus.class, "DRAFT");
        item.setEmployee(employeeField.getValue());
        item.setCompany(companyField.getValue());
        Date today = timeSource.currentTimestamp();
        item.setAttachDate(today);
        item.setInsuranceContract(insuranceContractField.getValue());
        item.setInsuranceProgram(insuranceProgramField.getValue());
        item.setStatusRequest(status);
        item.setType(RelativeType.MEMBER);
        item.setAmount(BigDecimal.valueOf(0));
        item.setTotalAmount(BigDecimal.valueOf(0));
        return item;
    }


    @Subscribe("relativeField")
    public void onRelativeFieldValueChange(HasValue.ValueChangeEvent<DicRelationshipType> event) {

        if (event.getValue() != null && !"PRIMARY".equals(event.getValue().getCode()) && !(whichButton.equals("joinHr") || whichButton.equals("editHr"))) {
            totalAmountField.setValue(new BigDecimal(0));
            addressTypeField.setEditable(true);
            addressField.setEditable(true);
            documentTypeField.setEditable(true);
            insuranceContractField.setEditable(true);
            totalAmountField.setVisible(false);
            firstNameField.setVisible(true);
            middleNameField.setVisible(true);
            secondNameField.setVisible(true);

            firstNameField.setValue(null);
            secondNameField.setValue(null);
            middleNameField.setValue(null);
            iinField.setValue(null);
            birthdateField.setValue(null);
            sexField.setValue(null);
            jobField.setValue(null);
            documentTypeField.setValue(insuranceContractField.getValue() == null ?
                    null : insuranceContractField.getValue().getDefaultDocumentType());
            documentNumberField.setValue(null);
            addressTypeField.setVisible(false);
            addressField.setValue(null);
            addressField.setRequired(true);
            addressField.setCaption("Домашний адрес");
            iinField.setEditable(true);
            sexField.setEditable(true);
            birthdateField.setEditable(true);
            jobField.setEditable(true);
            if (employeeField == null) {

            }
        } else if (event.getValue() != null
                && "PRIMARY".equals(event.getValue().getCode())) {
            totalAmountField.setValue(new BigDecimal(0));
            firstNameField.setVisible(false);
            middleNameField.setVisible(false);
            secondNameField.setVisible(false);
            documentTypeField.setValue(insuranceContractField.getValue() == null ?
                    null : insuranceContractField.getValue().getDefaultDocumentType());
            addressTypeField.setVisible(true);
            addressField.setCaption("");
            if (employeeField.getValue() != null) {
                if (employeeField.getValue().getAddresses().size() == 0) {
                    addressTypeField.setVisible(false);
                    addressField.setCaption("Домашний адрес");
                    addressField.setRequired(true);
                }
                typeField.setValue(RelativeType.EMPLOYEE);
                PersonExt personExt = employeeField.getValue().getPerson();
                AssignmentExt assignmentExt = employeeField.getValue().getCurrentAssignment();
                assignDateField.setValue(personExt.getHireDate());
                firstNameField.setValue(personExt.getFirstName());
                secondNameField.setValue(personExt.getLastName());
                middleNameField.setValue(personExt.getMiddleName());
                iinField.setValue(personExt.getNationalIdentifier());
                birthdateField.setValue(personExt.getDateOfBirth());
                sexField.setValue(personExt.getSex());
                jobField.setValue(assignmentExt.getJobGroup());
                jobField.setEditable(false);
                sexField.setEditable(false);
                birthdateField.setEditable(false);
                iinField.setEditable(false);
                companyField.setEditable(false);
            }
            if (typeField.getValue() != null && RelativeType.EMPLOYEE == typeField.getValue()) {

            }
        } else if (event.getValue() != null && (whichButton.equals("joinHr") || whichButton.equals("editHr"))) {
            if ("PRIMARY".equals(relativeField.getValue().getCode())) {
                totalAmountField.setValue(new BigDecimal(0));
                firstNameField.setVisible(false);
                middleNameField.setVisible(false);
                secondNameField.setVisible(false);
                documentTypeField.setValue(insuranceContractField.getValue() == null ?
                        null : insuranceContractField.getValue().getDefaultDocumentType());
                addressTypeField.setVisible(true);
                addressField.setCaption("");
                if (employeeField.getValue() != null) {
                    if (employeeField.getValue().getAddresses().size() == 0) {
                        addressTypeField.setVisible(false);
                        addressField.setCaption("Домашний адрес");
                        addressField.setRequired(true);
                    }
                    PersonExt personExt = employeeField.getValue().getPerson();
                    AssignmentExt assignmentExt = employeeField.getValue().getCurrentAssignment();
                    assignDateField.setValue(personExt.getHireDate());
                    firstNameField.setValue(personExt.getFirstName());
                    secondNameField.setValue(personExt.getLastName());
                    middleNameField.setValue(personExt.getMiddleName());
                    iinField.setValue(personExt.getNationalIdentifier());
                    birthdateField.setValue(personExt.getDateOfBirth());
                    sexField.setValue(personExt.getSex());
                    jobField.setValue(assignmentExt.getJobGroup());
                    jobField.setEditable(false);
                    sexField.setEditable(false);
                    birthdateField.setEditable(false);
                    iinField.setEditable(false);
                    companyField.setEditable(false);
                }
                typeField.setValue(RelativeType.EMPLOYEE);
            } else {
                typeField.setValue(RelativeType.MEMBER);

                totalAmountField.setValue(new BigDecimal(0));
                addressTypeField.setEditable(true);
                addressField.setEditable(true);
//            documentNumberField.setEditable(true);
                documentTypeField.setEditable(true);
                insuranceContractField.setEditable(true);
//            regionField.setEditable(true);
                totalAmountField.setVisible(false);
                firstNameField.setVisible(true);
                middleNameField.setVisible(true);
                secondNameField.setVisible(true);
//            if (employeeField == null){
                firstNameField.setValue(null);
                secondNameField.setValue(null);
                middleNameField.setValue(null);
                iinField.setValue(null);
                birthdateField.setValue(null);
                sexField.setValue(null);
                jobField.setValue(null);
                documentTypeField.setValue(insuranceContractField.getValue() == null ?
                        null : insuranceContractField.getValue().getDefaultDocumentType());
                documentNumberField.setValue(null);
                addressTypeField.setVisible(false);
                addressField.setValue(null);
                addressField.setRequired(true);
                addressField.setCaption("Домашний адрес");
                iinField.setEditable(true);
                sexField.setEditable(true);
                birthdateField.setEditable(true);
                jobField.setEditable(true);

            }
        } else if (event.getValue() != null && !"PRIMARY".equals(event.getValue().getCode())) {

        }
    }


    @Subscribe("employeeField")
    public void onEmployeeFieldValueChange(HasValue.ValueChangeEvent<PersonGroupExt> event) {
        PersonGroupExt personGroupExt = dataManager.load(PersonGroupExt.class).query("select e.personGroup " +
                "from tsadv$UserExt e " +
                "where e.id = :uId").parameter("uId", userSession.getUser().getId())
                .view("personGroupExt-view")
                .list().stream().findFirst().orElse(null);

        if (event.getValue() != null && relativeField.getValue() != null
                && relativeField.getValue().getCode().equals("PRIMARY")) {
            DicCompany company = personGroupExt.getCurrentAssignment().getOrganizationGroup().getCompany();
            companyField.setValue(company);

            if (personGroupExt.getAddresses().size() == 0) {
                addressTypeField.setVisible(false);
                addressField.setCaption("Домашний адрес");
                addressField.setRequired(true);
            }
            PersonExt personExt = event.getValue().getPerson();
            AssignmentExt assignmentExt = event.getValue().getCurrentAssignment();
            insuranceContractField.setEditable(true);
            firstNameField.setVisible(false);
            middleNameField.setVisible(false);
            secondNameField.setVisible(false);
            jobField.setEditable(false);
            sexField.setEditable(false);
            birthdateField.setEditable(false);
            iinField.setEditable(false);
            companyField.setEditable(false);
            assignDateField.setValue(personExt.getHireDate());
            firstNameField.setValue(personExt.getFirstName());
            secondNameField.setValue(personExt.getLastName());
            middleNameField.setValue(personExt.getMiddleName());
            iinField.setValue(personExt.getNationalIdentifier());
            birthdateField.setValue(personExt.getDateOfBirth());
            sexField.setValue(personExt.getSex());
            jobField.setValue(assignmentExt.getJobGroup());
        } else if (event.getValue() != null && relativeField.getValue() != null
                && !relativeField.getValue().getCode().equals("PRIMARY")) {
            firstNameField.setValue(null);
            secondNameField.setValue(null);
            middleNameField.setValue(null);
            iinField.setValue(null);
            birthdateField.setValue(null);
            sexField.setValue(null);
            jobField.setValue(null);
            assignDateField.setValue(event.getValue().getPerson().getHireDate());
        } else if (event.getValue() != null && relativeField.getValue() == null) {
//            assignDateField.setValue(event.getValue().getPerson().getHireDate());
//            if (personGroupExt.getAddresses().size() == 0){
//                addressTypeField.setVisible(false);
//                addressField.setCaption("Домашний адрес");
//                addressField.setRequired(true);
//            }
        } else if (event.getValue() == null) {
            assignDateField.setValue(null);
            iinField.setValue(null);
            iinField.setEditable(true);
            sexField.setValue(null);
            sexField.setEditable(true);
            birthdateField.setValue(null);
            birthdateField.setEditable(true);
            jobField.setValue(null);
            jobField.setEditable(true);
        }
    }


    @Subscribe("companyField")
    public void onCompanyFieldValueChange(HasValue.ValueChangeEvent<DicCompany> event) {

        if (insuranceContractField.getValue() != null) {
            insuranceContractDl.setParameter("companyId", insuranceContractField.getValue().getCompany().getId());
        } else if (insuredPersonDc.getItem().getCompany() != null) {
            insuranceContractDl.setParameter("companyId", insuredPersonDc.getItem().getCompany().getId());
        } else {
            insuranceContractDl.setParameter("companyId", null);
        }
    }


    @Subscribe("insuranceContractField")
    public void onInsuranceContractFieldValueChange(HasValue.ValueChangeEvent<InsuranceContract> event) {
        if (event.getValue() != null) {
            insuranceProgramField.setValue(event.getValue().getInsuranceProgram());
            startDateField.setValue(event.getValue().getAvailabilityPeriodFrom());
            endDateField.setValue(event.getValue().getAvailabilityPeriodTo());
            if (event.getValue().getAttachments().size() != 0) {
                for (Attachment attachment : event.getValue().getAttachments()) {
                    LinkButton linkButton = uiComponents.create(LinkButton.class);
                    linkButton.setCaption(attachment.getAttachment().getName());
                    linkButton.setIcon("");
                    linkButton.setAction(new BaseAction("contractField") {
                        @Override
                        public void actionPerform(Component component) {
                            super.actionPerform(component);
                            exportDisplay.show(attachment.getAttachment(), ExportFormat.OCTET_STREAM);
                        }
                    });
                    form2.add(linkButton);


                }
            }
        }
    }


    @Subscribe("removeButton")
    public void onRemoveButtonClick(Button.ClickEvent event) {
        insuredPersonMemberDl.load();
    }



    public Component generatorName(InsuredPerson entity) {
        HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
        List<FileDescriptor> fileDescriptorList = entity.getFile();
        if (fileDescriptorList != null && !fileDescriptorList.isEmpty())
            fileDescriptorList.forEach(e -> {
                LinkButton button = componentsFactory.createComponent(LinkButton.class);
                Label dot = componentsFactory.createComponent(Label.class);
                button.setCaption(e.getName());
                button.setAction(new BaseAction("file") {
                    @Override
                    public void actionPerform(Component component) {
                        super.actionPerform(component);
                        exportDisplay.show(e, ExportFormat.OCTET_STREAM);
                    }
                });
                hBoxLayout.add(button);

                if (fileDescriptorList.indexOf(e) == fileDescriptorList.size() - 1) dot.setValue("");
                else dot.setValue(",  ");
                hBoxLayout.add(dot);

            });

        return hBoxLayout;
    }

}