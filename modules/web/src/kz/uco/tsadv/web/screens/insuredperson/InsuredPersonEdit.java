package kz.uco.tsadv.web.screens.insuredperson;

import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.base.entity.dictionary.DicSex;
import kz.uco.tsadv.modules.personal.dictionary.DicDocumentType;
import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.InsuranceContract;
import kz.uco.tsadv.modules.personal.model.InsuredPerson;

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
    private InstanceContainer<InsuredPerson> insuredPersonDc;
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
    private LookupField<PersonGroupExt> employeeField;

    @Subscribe
    public void onInit(InitEvent event) {
        insuranceContractDl.setParameter("company",null);
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
        }
    }



    @Subscribe("employeeField")
    public void onEmployeeFieldValueChange(HasValue.ValueChangeEvent<PersonGroupExt> event) {
        if (event.getValue() != null && event.getValue().getCurrentAssignment() !=null){
            insuranceContractDl.setParameter("company",
                    event.getValue().getCurrentAssignment().getOrganizationGroup().getOrganization().getCompany());
            insuranceContractDl.load();

        }

    }




}