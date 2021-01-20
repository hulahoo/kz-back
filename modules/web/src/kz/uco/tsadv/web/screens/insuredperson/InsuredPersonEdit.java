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
        if (event.getValue() != null && !"PRIMARY".equals(event.getValue().getCode())){
            firstNameField.setVisible(true);
            middleNameField.setVisible(true);
            secondNameField.setVisible(true);
        }
    }

    @Subscribe("typeField")
    public void onTypeFieldValueChange(HasValue.ValueChangeEvent<Integer> event) {
        if (null != event.getValue() && 1 == event.getValue()){
            employeeField.setEditable(false);
            insuranceContractField.setEditable(true);
            firstNameField.setVisible(false);
            middleNameField.setVisible(false);
            secondNameField.setVisible(false);
            jobField.setEditable(false);
            sexField.setEditable(false);
            birthdateField.setEditable(false);
            iinField.setEditable(false);
            documentNumberType.setEditable(false);
            documentTypeField.setEditable(false);
            companyField.setEditable(false);
            assignDateField.setEditable(false);
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

            PersonGroupExt personGroupExt = event.getValue();
            PersonExt person = personGroupExt.getPerson();
            AssignmentExt assignment = personGroupExt.getCurrentAssignment();

            iinField.setValue(person.getNationalIdentifier());
            assignDateField.setValue(person.getHireDate());
            birthdateField.setValue(person.getDateOfBirth());
            sexField.setValue(person.getSex());
            jobField.setValue(assignment.getJobGroup());
            companyField.setValue(company);
            typeField.setValue(1);
        }
    }

    @Subscribe("insuranceContractField")
    public void onInsuranceContractFieldValueChange(HasValue.ValueChangeEvent<InsuranceContract> event) {
        startDateField.setValue(event.getValue().getAvailabilityPeriodFrom());
        endDateField.setValue(event.getValue().getAvailabilityPeriodTo());
    }

}