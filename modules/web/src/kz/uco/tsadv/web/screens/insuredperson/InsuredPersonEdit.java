package kz.uco.tsadv.web.screens.insuredperson;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.base.entity.dictionary.DicSex;
import kz.uco.tsadv.modules.personal.dictionary.DicCompany;
import kz.uco.tsadv.modules.personal.dictionary.DicDocumentType;
import kz.uco.tsadv.modules.personal.dictionary.DicRelationshipType;
import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.InsuranceContract;
import kz.uco.tsadv.modules.personal.model.InsuredPerson;
import kz.uco.tsadv.modules.personal.model.PersonExt;

import javax.inject.Inject;
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
    private Field<Integer> documentNumberType;
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
    private Field<Integer> typeField;
    @Inject
    private DateField<Date> startDateField;
    @Inject
    private DateField<Date> endDateField;
    @Inject
    private DateField<Date> attachDateField;

    @Subscribe("relativeField")
    public void onRelativeFieldValueChange(HasValue.ValueChangeEvent<DicRelationshipType> event) {
        if (event.getValue() != null && !"PRIMARY".equals(event.getValue().getCode())) {
            checkRelationType(true);
            iinField.setValue(null);
            birthdateField.setValue(null);
            sexField.setValue(null);
            jobField.setValue(null);
            typeField.setValue(2);
            PersonGroupExt personGroupExt = employeeField.getValue();
            if (personGroupExt == null){
                iinField.setValue(null);
                birthdateField.setValue(null);
                sexField.setValue(null);
                jobField.setValue(null);
            }
        } else if (event.getValue() != null && "PRIMARY".equals(event.getValue().getCode())) {
            checkRelationType(false);
            PersonGroupExt personGroupExt = employeeField.getValue();
            if (personGroupExt != null){
                PersonExt person = personGroupExt.getPerson();
                AssignmentExt assignment = personGroupExt.getCurrentAssignment();

                iinField.setValue(person.getNationalIdentifier());
                birthdateField.setValue(person.getDateOfBirth());
                sexField.setValue(person.getSex());
                jobField.setValue(assignment.getJobGroup());
                typeField.setValue(1);
            }else {
                iinField.setValue(null);
                birthdateField.setValue(null);
                sexField.setValue(null);
                jobField.setValue(null);
            }
        }
    }

    private void checkRelationType(boolean b){
        firstNameField.setVisible(b);
        middleNameField.setVisible(b);
        secondNameField.setVisible(b);
    }

    @Subscribe("typeField")
    public void onTypeFieldValueChange(HasValue.ValueChangeEvent<Integer> event) {
        if (null != event.getValue() && 1 == event.getValue()){
            insuranceContractField.setEditable(true);
            firstNameField.setVisible(false);
            middleNameField.setVisible(false);
            secondNameField.setVisible(false);
            jobField.setEditable(false);
            sexField.setEditable(false);
            birthdateField.setEditable(false);
            iinField.setEditable(false);
            companyField.setEditable(false);
            assignDateField.setEditable(false);
        } else if (event.getValue() != null && !(1 == event.getValue())){

        }
    }

    @Subscribe("employeeField")
    public void onEmployeeFieldValueChange(HasValue.ValueChangeEvent<PersonGroupExt> event) {
        if (event.getValue() != null){
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


            PersonExt person = event.getValue().getPerson();
            assignDateField.setValue(person.getHireDate());
            companyField.setValue(company);
            if (relativeField.getValue() != null && relativeField.getValue().getCode().equals("PRIMARY")){
                AssignmentExt assignment = event.getValue().getCurrentAssignment();

                iinField.setValue(person.getNationalIdentifier());
                birthdateField.setValue(person.getDateOfBirth());
                sexField.setValue(person.getSex());
                jobField.setValue(assignment.getJobGroup());
                typeField.setValue(1);
            }
        }else {
                iinField.setValue(null);
                birthdateField.setValue(null);
                sexField.setValue(null);
                jobField.setValue(null);

        }
    }

    @Subscribe("insuranceContractField")
    public void onInsuranceContractFieldValueChange(HasValue.ValueChangeEvent<InsuranceContract> event) {
        startDateField.setValue(event.getValue().getAvailabilityPeriodFrom());
        endDateField.setValue(event.getValue().getAvailabilityPeriodTo());
    }

}