package kz.uco.tsadv.web.screens.insuredperson;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.base.entity.dictionary.DicRegion;
import kz.uco.base.entity.dictionary.DicSex;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.VacationScheduleRequest;
import kz.uco.tsadv.modules.personal.dictionary.*;
import kz.uco.tsadv.modules.personal.enums.RelativeType;
import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.*;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Date;

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
    private PickerField<DicDocumentType> documentTypeField;
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
    private DateField<Date> attachDateField;
    @Inject
    private InstanceContainer<InsuredPerson> insuredPersonDc;
    @Inject
    private LookupField<Address> addressTypeField;
    @Inject
    private TextArea<String> addressField;
    @Inject
    private CollectionLoader<Address> addressDl;
    @Inject
    private CollectionContainer<Address> addressDc;
    @Inject
    private CollectionContainer<DicDocumentType> documentTypeDc;
    @Inject
    private CollectionLoader<DicDocumentType> documentTypeDl;
    @Inject
    private TextArea<String> insuranceProgramField;
    @Inject
    private LookupField<DicRegion> regionField;
    @Inject
    private TextField<BigDecimal> totalAmountField;
    @Inject
    private CollectionContainer<InsuredPerson> insuredPersonsDc;
    @Inject
    private CollectionLoader<InsuredPerson> insuredPersonsDl;
    @Inject
    GroupBoxLayout familyMemberInformationGroup;
    @Inject
    GroupBoxLayout informationOnVHI;
    @Inject
    private ScreenBuilders screenBuilders;
    @Inject
    private GroupTable<InsuredPerson> insuredPersonTable;
    @Inject
    private LookupField<DicVHIAttachmentStatus> statusRequestField;
    @Inject
    private CommonService commonService;

    @Subscribe
    public void onAfterShow(AfterShowEvent event) {


//        insuranceContractDl.setParameter("companyId", null);

//        PersonGroupExt item = insuredPersonDc.getItem().getEmployee();
//        insuranceContractDl.setParameter("value",
//                item.getCurrentAssignment().getOrganizationGroup().getOrganization().getCompany().getId());
    }



    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        if (insuredPersonDc.getItemOrNull() != null && insuredPersonDc.getItem().getEmployee() != null){
            insuredPersonsDl.setParameter("employeeId", insuredPersonDc.getItem().getEmployee().getId());
        }else if (employeeField.getValue() != null){
            insuredPersonsDl.setParameter("employeeId", employeeField.getValue().getId());
        }
        if (typeField.getValue() != null && RelativeType.EMPLOYEE == typeField.getValue()) {

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
//            checkRelativeType(RelativeType.EMPLOYEE);
        } else if (typeField.getValue() != null && RelativeType.MEMBER == typeField.getValue()) {
            familyMemberInformationGroup.setVisible(true);
            addressTypeField.setEditable(false);
            addressField.setEditable(false);
            documentNumberField.setEditable(false);
            documentTypeField.setEditable(false);
            insuranceContractField.setEditable(false);
            regionField.setEditable(false);
            totalAmountField.setVisible(false);
            relativeField.setVisible(false);
            employeeField.setEditable(false);
//            checkRelativeType(RelativeType.MEMBER);
        }else if (typeField.getValue() == null){
            familyMemberInformationGroup.setVisible(false);
            insuranceContractField.setEditable(true);
        }
//        documentTypeDl.setParameter("personGroupId", insuredPersonDc.getItem().getEmployee().getPersonDocuments());
        if (employeeField.getValue() != null &&
                relativeField.getValue() != null && relativeField.getValue().getCode().equals("PRIMARY")){
            if (addressDc.getItems().size() == 0){
                addressTypeField.setRequired(false);
                addressTypeField.setVisible(false);
                addressField.setRequired(true);
                addressField.setCaption("Домашний адрес");
            }
            if (employeeField.getValue().getPersonDocuments().size() == 0){
//                documentNumberField.setEditable(true);
//                documentTypeDl.setParameter("personGroupId", null);
            }
        }
    }

    @Subscribe("insuredPersonTable.create")
    public void onInsuredPersonTableCreate(Action.ActionPerformedEvent event) {
        insuredPersonDc.getItem();
        if (insuredPersonDc.getItem().getCompany() != null){
            insuranceContractDl.setParameter("companyId", insuredPersonDc.getItem().getCompany());
        }
        InsuredPerson item = dataManager.create(InsuredPerson.class);

        screenBuilders.editor(InsuredPerson.class, this)
                .editEntity(newInsuredPersonMember(item))
                .withLaunchMode(OpenMode.DIALOG)
                .build()
                .show()
                .addAfterCloseListener(afterCloseEvent -> {
                    employeeField.setVisible(true);
                    iinField.setVisible(true);
                    sexField.setVisible(true);
                    documentTypeField.setVisible(true);
                    companyField.setVisible(true);
                    attachDateField.setVisible(true);
                    firstNameField.setVisible(false);
                    secondNameField.setVisible(false);
                    middleNameField.setVisible(false);
                    addressTypeField.setVisible(false);
                    informationOnVHI.setVisible(true);
                });
    }

    protected InsuredPerson newInsuredPersonMember(InsuredPerson insuredPerson){
        DicVHIAttachmentStatus status = commonService.getEntity(DicVHIAttachmentStatus.class, "PRIMARY");
        insuredPerson.setEmployee(employeeField.getValue());
        insuredPerson.setCompany(companyField.getValue());
        employeeField.setVisible(false);
        firstNameField.setVisible(true);
        secondNameField.setVisible(true);
        middleNameField.setVisible(true);
        addressTypeField.setVisible(false);
        addressField.setRequired(true);
        assignDateField.setVisible(false);
        insuranceContractField.setValue(insuranceContractField.getValue());
        insuranceContractField.setVisible(false);
        endDateField.setValue(insuranceContractField.getValue().getAvailabilityPeriodFrom());
        startDateField.setValue(insuranceContractField.getValue().getAvailabilityPeriodTo());
        endDateField.setVisible(false);
        sexField.setVisible(false);
        statusRequestField.setValue(status);
        statusRequestField.setVisible(false);
        addressField.setCaption("Домашний адрес");
        companyField.setVisible(false);
        typeField.setValue(RelativeType.MEMBER);
        informationOnVHI.setVisible(false);
        return insuredPerson;
    }

    @Subscribe("relativeField")
    public void onRelativeFieldValueChange(HasValue.ValueChangeEvent<DicRelationshipType> event) {
        if (event.getValue() != null && !"PRIMARY".equals(event.getValue().getCode())){
            totalAmountField.setValue(new BigDecimal(0));
//            typeField.setValue(RelativeType.MEMBER);
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
            addressField.setCaption("Домашний адрес");
            iinField.setEditable(true);
            sexField.setEditable(true);
            birthdateField.setEditable(true);
            jobField.setEditable(true);
//            } else {
//                firstNameField.setValue(null);
//                secondNameField.setValue(null);
//                middleNameField.setValue(null);
//            }


        }else if (event.getValue() != null && "PRIMARY".equals(event.getValue().getCode())){
            totalAmountField.setValue(new BigDecimal(0));
//            typeField.setValue(RelativeType.EMPLOYEE);
            firstNameField.setVisible(false);
            middleNameField.setVisible(false);
            secondNameField.setVisible(false);
            documentTypeField.setValue(insuranceContractField.getValue() == null ?
                    null : insuranceContractField.getValue().getDefaultDocumentType());
            addressTypeField.setVisible(true);
            addressField.setCaption("");
            if (employeeField.getValue() != null){
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
//                addressTypeField.setEditable(false);
//                addressField.setEditable(false);
//                documentNumberField.setEditable(false);
//                documentTypeField.setEditable(false);
            }
            if (typeField.getValue() != null && RelativeType.EMPLOYEE == typeField.getValue()) {
                familyMemberInformationGroup.setVisible(false);
            }
        }
//        boolean isEmployee = type == RelativeType.EMPLOYEE;
//        addressTypeField.setEditable(!isEmployee);
//        addressField.setEditable(!isEmployee);
//        documentNumberField.setEditable(!isEmployee);
//        documentTypeField.setEditable(!isEmployee);
//        insuranceContractField.setEditable(!isEmployee);
//        regionField.setEditable(!isEmployee);
//        totalAmountField.setVisible(!isEmployee);

//        if (event.getValue() != null && !"PRIMARY".equals(event.getValue().getCode())) {
//            isEmployeeRelativeAndEmployeeNotNull(RelativeType.EMPLOYEE, null);
//        } else if (event.getValue() != null && "PRIMARY".equals(event.getValue().getCode())) {
//            isEmployeeRelativeAndEmployeeNotNull(RelativeType.MEMBER, null);
//        }
    }

    @Subscribe("documentTypeField")
    public void onDocumentTypeFieldValueChange(HasValue.ValueChangeEvent<DicDocumentType> event) {
        if (event.getValue() != null){
            PersonDocument document = dataManager.load(PersonDocument.class)
                    .query("select e from tsadv$PersonDocument e " +
                            " where e.documentType.id = :documentTypeId")
                    .parameter("documentTypeId", event.getValue().getId())
                    .view("_local")
                    .list().stream().findFirst().orElse(null);
            if (employeeField.getValue() != null &&
                    document != null &&
                    document.getDocumentType().getId().equals(event.getValue().getId())){
                documentNumberField.setValue(document.getDocumentNumber());
            }
        }
    }

    @Subscribe("employeeField")
    public void onEmployeeFieldValueChange(HasValue.ValueChangeEvent<PersonGroupExt> event) {
        if (event.getValue() != null && relativeField.getValue() != null
                && relativeField.getValue().getCode().equals("PRIMARY")){
            DicCompany company = dataManager.load(DicCompany.class)
                    .query("select o.company " +
                            "   from base$AssignmentExt a" +
                            " join a.assignmentStatus s " +
                            " join a.organizationGroup.list o " +
                            " where a.personGroup.id = :pg " +
                            "and current_date between a.startDate and a.endDate "+
                            "and a.primaryFlag = 'TRUE' " +
                            "and s.code in ('ACTIVE','SUSPENDED') " +
                            " and current_date between o.startDate and o.endDate")
                    .parameter("pg", event.getValue().getId()).view(View.LOCAL)
                    .list().stream().findFirst().orElse(null);
            companyField.setValue(company);

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
//            employeeField.setEditable(false);
//            relativeField.setEditable(false);
//            documentNumberField.setEditable(false);
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
//            isEmployeeRelativeAndEmployeeNotNull(RelativeType.MEMBER, event.getValue());
        } else if (event.getValue() != null && relativeField.getValue() == null){
            assignDateField.setValue(event.getValue().getPerson().getHireDate());

//            isEmployeeRelativeAndEmployeeNotNull(RelativeType.EMPLOYEE, null);
        } else if (event.getValue() == null){
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

        if (insuranceContractField.getValue() != null){
            insuranceContractDl.setParameter("companyId", insuranceContractField.getValue().getCompany().getId());
        } else if (insuredPersonDc.getItem().getCompany()!=null){
            insuranceContractDl.setParameter("companyId", insuredPersonDc.getItem().getCompany().getId());
        }else {
            insuranceContractDl.setParameter("companyId", null);
        }
//        if (event.getValue() != null){
//            insuranceContractDl.setParameter("companyId", event.getValue().getId());
//        } else {
//            insuranceContractDl.setParameter("companyId", null);
//        }
    }


    @Subscribe("insuranceContractField")
    public void onInsuranceContractFieldValueChange(HasValue.ValueChangeEvent<InsuranceContract> event) {
        if (event.getValue() != null){
            insuranceProgramField.setValue(event.getValue().getInsuranceProgram());
            startDateField.setValue(event.getValue().getAvailabilityPeriodFrom());
            endDateField.setValue(event.getValue().getAvailabilityPeriodTo());
        }

    }

    public void relativeTypeIsEmployee(boolean check) {
        insuranceContractField.setEditable(check);
        firstNameField.setVisible(!check);
        middleNameField.setVisible(!check);
        secondNameField.setVisible(!check);
        jobField.setEditable(false);
        sexField.setEditable(false);
        birthdateField.setEditable(false);
        iinField.setEditable(false);
        companyField.setEditable(false);
        employeeField.setEditable(false);
        relativeField.setEditable(false);
    }

    private void checkRelativeType (RelativeType type){
        boolean isEmployee = type == RelativeType.EMPLOYEE;
        addressTypeField.setEditable(!isEmployee);
        addressField.setEditable(!isEmployee);
        documentNumberField.setEditable(!isEmployee);
        documentTypeField.setEditable(!isEmployee);
        insuranceContractField.setEditable(!isEmployee);
        regionField.setEditable(!isEmployee);
        totalAmountField.setVisible(!isEmployee);
    }

    public void isEmployeeRelativeAndEmployeeNotNull (RelativeType type, PersonGroupExt employee){
        PersonGroupExt item = type == RelativeType.EMPLOYEE ? employee : null;
        boolean isNull = item == null;
        boolean isEmployeeRelativeType = type == RelativeType.EMPLOYEE;
        if (employee != null && type != null){
            relativeTypeIsEmployee(!isEmployeeRelativeType);
            assignDateField.setValue(isNull ? null : item.getPerson().getHireDate());
            firstNameField.setValue(isNull ? null : item.getPerson().getFirstName());
            secondNameField.setValue(isNull ? null : item.getPerson().getLastName());
            middleNameField.setValue(isNull ? null : item.getPerson().getMiddleName());
            iinField.setValue(isNull ? null : item.getPerson().getNationalIdentifier());
            birthdateField.setValue(isNull ? null : item.getPerson().getDateOfBirth());
            sexField.setValue(isNull ? null : item.getPerson().getSex());
            jobField.setValue(isNull ? null : item.getCurrentAssignment().getJobGroup());
        } else  if (employee == null && type != null){
            relativeTypeIsEmployee(!isEmployeeRelativeType);
            if (isEmployeeRelativeType){
                jobField.setRequired(true);
                typeField.setValue(RelativeType.EMPLOYEE);
            }else {
                iinField.setValue(null);
                birthdateField.setValue(null);
                sexField.setValue(null);
                jobField.setValue(null);
                typeField.setValue(RelativeType.MEMBER);
                iinField.setValue(null);
                birthdateField.setValue(null);
                sexField.setValue(null);
                jobField.setValue(null);
            }
        } else  if (employee != null && type == null){

        }

    }

}