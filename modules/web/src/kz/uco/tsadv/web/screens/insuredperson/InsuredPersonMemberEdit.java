package kz.uco.tsadv.web.screens.insuredperson;

import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicRelationshipType;
import kz.uco.tsadv.modules.personal.enums.RelativeType;
import kz.uco.tsadv.modules.personal.model.ContractConditions;
import kz.uco.tsadv.modules.personal.model.InsuranceContract;
import kz.uco.tsadv.modules.personal.model.InsuredPerson;
import kz.uco.tsadv.service.EmployeeService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;

@UiController("tsadv$InsuredPerson.MemberEdit")
@UiDescriptor("insured-person-member-edit.xml")
@EditedEntityContainer("insuredPersonDc")
@LoadDataBeforeShow
public class InsuredPersonMemberEdit extends StandardEditor<InsuredPerson> {

    @Inject
    private CollectionLoader<DicRelationshipType> relativeDl;
    private boolean isFree;
    @Inject
    private DateField<Date> birthdateField;
    @Inject
    private LookupField<DicRelationshipType> relativeField;
    @Inject
    private PickerField<InsuranceContract> insuranceContractField;
    @Inject
    private TextField<BigDecimal> amountField;
    @Inject
    private TimeSource timeSource;
    @Inject
    protected EmployeeService employeeService;


    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        relativeDl.setParameter("employee", "PRIMARY");
    }


    public void setParameter(boolean isFree){
        this.isFree = isFree;
    }


    @Subscribe("birthdateField")
    public void onBirthdateFieldValueChange(HasValue.ValueChangeEvent<Date> event) {
        if (event.getValue() != null && relativeField.getValue() != null && insuranceContractField.getValue() != null){
            amountField.setValue(BigDecimal.valueOf(0));
            calculatedAmount(isFree);
        }
    }


    @Subscribe("relativeField")
    public void onRelativeFieldValueChange(HasValue.ValueChangeEvent<DicRelationshipType> event) {
        if (event.getValue() != null && birthdateField.getValue() != null && insuranceContractField.getValue() != null){
            amountField.setValue(BigDecimal.valueOf(0));
            calculatedAmount(isFree);
        }
    }


    protected void calculatedAmount(boolean isFree){

        int age = employeeService.calculateAge(birthdateField.getValue());

        if (!isFree && insuranceContractField.getValue().getProgramConditions().size() >=1){
            for (ContractConditions condition : insuranceContractField.getValue().getProgramConditions()){
                if (condition.getRelationshipType().getId() == relativeField.getValue().getId()){
                    if (condition.getAgeMin() <= age && condition.getAgeMax() >= age){
                        amountField.setValue(condition.getCostInKzt());
                        break;
                    }
                }
            }
        }

    }


}