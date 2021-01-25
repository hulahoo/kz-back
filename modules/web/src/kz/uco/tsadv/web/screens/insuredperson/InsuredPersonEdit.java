package kz.uco.tsadv.web.screens.insuredperson;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.TimeSource;
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
import kz.uco.tsadv.entity.tb.Attachment;
import kz.uco.tsadv.modules.personal.dictionary.*;
import kz.uco.tsadv.modules.personal.enums.RelativeType;
import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.*;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    private PickerField<Attachment> fileField;
    @Inject
    private TextField<String> commentField;
    @Inject
    private DateField<Date> exclusionDateField;


//    @Subscribe
//    public void onAfterShow(AfterShowEvent event) {
//
//        if (typeField.getValue() != null && typeField.getValue() == RelativeType.EMPLOYEE && employeeField.getValue() != null){
//            familyMemberInformationGroup.setVisible(false);
//        }else if (typeField.getValue() != null && typeField.getValue() == RelativeType.MEMBER && employeeField.getValue() != null){
//            familyMemberInformationGroup.setVisible(true);
//        }else if (typeField.getValue() != null && typeField.getValue() == RelativeType.EMPLOYEE && employeeField.getValue() == null){
//            familyMemberInformationGroup.setVisible(false);
//        } else if (typeField.getValue() == null && employeeField.getValue() == null){
//            familyMemberInformationGroup.setVisible(false);
//        }
//    }

    public void setParameter(String whichButton){
        this.whichButton = whichButton;
    }

    @Subscribe
    public void onAfterShow(AfterShowEvent event) {
        LocalDateTime ldt = LocalDateTime.ofInstant(timeSource.currentTimestamp().toInstant(), ZoneId.systemDefault());
        Date outRequestDate = Date.from(ldt.minusDays(1).atZone(ZoneId.systemDefault()).toInstant());
        attachDateField.setRangeStart(outRequestDate);
        Date today = timeSource.currentTimestamp();
        attachDateField.setValue(today);

        if ("joinMember".equals(whichButton)){
            familyMemberInformationGroup.setVisible(true);
            employeeField.setEditable(false);
            relativeField.setVisible(false);
            documentTypeField.setEditable(false);
            documentNumberField.setEditable(false);
            addressTypeField.setEditable(false);
            addressField.setEditable(false);
            regionField.setEditable(false);
            fileField.setEditable(false);
            exclusionDateField.setVisible(false);
            commentField.setVisible(false);
        }else if ("joinEmployee".equals(whichButton)){

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
        }else if ("joinHr".equals(whichButton)){
            attachDateField.setEditable(true);
            familyMemberInformationGroup.setVisible(false);
        }else if ("editHr".equals(whichButton)){
            attachDateField.setEditable(true);
            if (addressTypeField.getValue() == null){
                addressTypeField.setVisible(false);
                addressField.setCaption("Домашний адрес");
            }
            if (insuredPersonDc.getItemOrNull().getType() == RelativeType.MEMBER){
                familyMemberInformationGroup.setVisible(true);
            }else if (insuredPersonDc.getItemOrNull().getType() == RelativeType.EMPLOYEE){
                familyMemberInformationGroup.setVisible(false);
            }
        }else {
            familyMemberInformationGroup.setVisible(true);
            employeeField.setEditable(false);
            relativeField.setVisible(false);
            documentTypeField.setEditable(false);
            documentNumberField.setEditable(false);
            addressTypeField.setEditable(false);
            addressField.setEditable(false);
            regionField.setEditable(false);
            fileField.setEditable(false);
            exclusionDateField.setVisible(false);
            commentField.setVisible(false);
            insuranceContractField.setEditable(false);
            if (addressTypeField.getValue() == null){
                addressTypeField.setVisible(false);
                addressField.setCaption("Домашний адрес");
            }
        }

        DicRelationshipType relationshipType = commonService.getEntity(DicRelationshipType.class, "PRIMARY");
        if (insuredPersonDc.getItemOrNull() != null && insuredPersonDc.getItem().getEmployee() != null){
            insuredPersonMemberDl.setParameter("employeeId", insuredPersonDc.getItem().getEmployee().getId());
            insuredPersonMemberDl.setParameter("relativeType", RelativeType.MEMBER);
            insuredPersonMemberDl.setParameter("employeeContractId", insuredPersonDc.getItem().getInsuranceContract().getId());
            insuredPersonMemberDl.load();
        }else if (employeeField.getValue() != null && insuranceContractField.getValue() != null){
            insuredPersonMemberDl.setParameter("employeeId", employeeField.getValue().getId());
            insuredPersonMemberDl.setParameter("relativeId", relationshipType.getId());
            insuredPersonMemberDl.setParameter("employeeContractId", insuranceContractField.getValue().getId());
            insuredPersonMemberDl.load();
        }
    }

    @Subscribe(id = "insuredPersonMemberDl", target = Target.DATA_LOADER)
    public void onInsuredPersonMemberDlPostLoad(CollectionLoader.PostLoadEvent<InsuredPerson> event) {
        if (insuranceContractField.getValue() != null && insuranceContractField.getValue().getCountOfFreeMembers() != null
                && insuredPersonMemberDc.getItems().size() > insuranceContractField.getValue().getCountOfFreeMembers()){
            totalAmountField.setValue(BigDecimal.valueOf(insuredPersonMemberDc.getItems().size() - insuranceContractField.getValue().getCountOfFreeMembers()));
        }
    }




    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {

        if (typeField.getValue() != null && RelativeType.EMPLOYEE == typeField.getValue()) {
//            checkRelativeType(RelativeType.EMPLOYEE);
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
//            checkRelativeType(RelativeType.MEMBER);
        }else if (typeField.getValue() == null){
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
                .withScreenClass(InsuredPersonMemberEdit.class)
                .editEntity(newInsuredPersonMember(item))
//                .withLaunchMode(OpenMode.DIALOG)
                .build()
                .show()
                .addAfterCloseListener(afterCloseEvent ->{
                    insuredPersonMemberDl.load();
                });
    }


    protected InsuredPerson newInsuredPersonMember(InsuredPerson item){
        DicMICAttachmentStatus status = commonService.getEntity(DicMICAttachmentStatus.class, "DRAFT");
        item.setEmployee(employeeField.getValue());
        item.setCompany(companyField.getValue());
        Date today = timeSource.currentTimestamp();
        item.setAttachDate(today);
        item.setInsuranceContract(insuranceContractField.getValue());
        item.setInsuranceProgram(insuranceProgramField.getValue());
        item.setTotalAmount(new BigDecimal(0));
        item.setStatusRequest(status);
        item.setType(RelativeType.MEMBER);
        return item;
    }


    @Subscribe("relativeField")
    public void onRelativeFieldValueChange(HasValue.ValueChangeEvent<DicRelationshipType> event) {
        if (event.getValue() != null && !"PRIMARY".equals(event.getValue().getCode()) && !(whichButton.equals("joinHr") || whichButton.equals("editHr"))){
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
            if (employeeField == null){

            }
        }
        else if (event.getValue() != null && "PRIMARY".equals(event.getValue().getCode()) && !(whichButton.equals("joinHr") || whichButton.equals("editHr"))){
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
                if (employeeField.getValue().getAddresses().size() == 0){
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
//                addressTypeField.setEditable(false);
//                addressField.setEditable(false);
//                documentNumberField.setEditable(false);
//                documentTypeField.setEditable(false);
            }
            if (typeField.getValue() != null && RelativeType.EMPLOYEE == typeField.getValue()) {
//                familyMemberInformationGroup.setVisible(false);
            }
        }
        else if (event.getValue() != null && (whichButton.equals("joinHr") || whichButton.equals("editHr"))){
            if ("PRIMARY".equals(relativeField.getValue().getCode())){
                typeField.setValue(RelativeType.EMPLOYEE);
            }else {
                typeField.setValue(RelativeType.MEMBER);
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

            if (employeeField.getValue().getAddresses().size() == 0){
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
            if (employeeField.getValue().getAddresses().size() == 0){
                addressTypeField.setVisible(false);
                addressField.setCaption("Домашний адрес");
                addressField.setRequired(true);
            }
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

//    public void relativeTypeIsEmployee(boolean check) {
//        insuranceContractField.setEditable(check);
//        firstNameField.setVisible(!check);
//        middleNameField.setVisible(!check);
//        secondNameField.setVisible(!check);
//        jobField.setEditable(false);
//        sexField.setEditable(false);
//        birthdateField.setEditable(false);
//        iinField.setEditable(false);
//        companyField.setEditable(false);
//        employeeField.setEditable(false);
//        relativeField.setEditable(false);
//    }

//    private void checkRelativeType (RelativeType type){
//        boolean isEmployee = type == RelativeType.EMPLOYEE;
//        addressTypeField.setEditable(!isEmployee);
//        addressField.setEditable(!isEmployee);
//        documentNumberField.setEditable(!isEmployee);
//        documentTypeField.setEditable(!isEmployee);
//        insuranceContractField.setEditable(!isEmployee);
//        regionField.setEditable(!isEmployee);
//        totalAmountField.setVisible(!isEmployee);
//    }

//    public void isEmployeeRelativeAndEmployeeNotNull (RelativeType type, PersonGroupExt employee){
//        PersonGroupExt item = type == RelativeType.EMPLOYEE ? employee : null;
//        boolean isNull = item == null;
//        boolean isEmployeeRelativeType = type == RelativeType.EMPLOYEE;
//        if (employee != null && type != null){
////            relativeTypeIsEmployee(!isEmployeeRelativeType);
//            assignDateField.setValue(isNull ? null : item.getPerson().getHireDate());
//            firstNameField.setValue(isNull ? null : item.getPerson().getFirstName());
//            secondNameField.setValue(isNull ? null : item.getPerson().getLastName());
//            middleNameField.setValue(isNull ? null : item.getPerson().getMiddleName());
//            iinField.setValue(isNull ? null : item.getPerson().getNationalIdentifier());
//            birthdateField.setValue(isNull ? null : item.getPerson().getDateOfBirth());
//            sexField.setValue(isNull ? null : item.getPerson().getSex());
//            jobField.setValue(isNull ? null : item.getCurrentAssignment().getJobGroup());
//        } else  if (employee == null && type != null){
////            relativeTypeIsEmployee(!isEmployeeRelativeType);
//            if (isEmployeeRelativeType){
//                jobField.setRequired(true);
//                typeField.setValue(RelativeType.EMPLOYEE);
//            }else {
//                iinField.setValue(null);
//                birthdateField.setValue(null);
//                sexField.setValue(null);
//                jobField.setValue(null);
//                typeField.setValue(RelativeType.MEMBER);
//                iinField.setValue(null);
//                birthdateField.setValue(null);
//                sexField.setValue(null);
//                jobField.setValue(null);
//            }
//        } else  if (employee != null && type == null){
//
//        }
//
//    }

}