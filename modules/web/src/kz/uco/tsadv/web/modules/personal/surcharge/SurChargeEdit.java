package kz.uco.tsadv.web.modules.personal.surcharge;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.model.Salary;
import kz.uco.tsadv.modules.personal.model.SurCharge;
import kz.uco.tsadv.modules.personal.model.SurChargePeriod;
import kz.uco.tsadv.modules.personal.model.SurChargeType;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SurChargeEdit extends AbstractEditor<SurCharge> {

    @Inject
    private FieldGroup fieldGroup;
    @Named("fieldGroup.dateFrom")
    protected DateField dateFromField;
    @Named("fieldGroup.dateTo")
    protected DateField dateToField;
    @Named("fieldGroup.name")
    protected PickerField nameField;
    @Named("fieldGroup.value")
    protected TextField valueField;

    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected Datasource<SurCharge> surChargeDs;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected UserSession userSession;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        OptionsGroup periodEnum = componentsFactory.createComponent(OptionsGroup.class);
        periodEnum.setOptionsEnum(SurChargePeriod.class);
        periodEnum.setOrientation(OptionsGroup.Orientation.HORIZONTAL);
        periodEnum.setDatasource(surChargeDs, "period");

        fieldGroup.getFieldNN("period").setComponent(periodEnum);

        OptionsGroup typeEnum = componentsFactory.createComponent(OptionsGroup.class);
        typeEnum.setOptionsEnum(SurChargeType.class);
        typeEnum.setOrientation(OptionsGroup.Orientation.HORIZONTAL);
        typeEnum.setDatasource(surChargeDs, "type");
        typeEnum.addValueChangeListener(valueChangeEvent -> {
            SurChargeType surChargeType = (SurChargeType) valueChangeEvent;
            if (surChargeType != null) {
                FieldGroup.FieldConfig fieldConfig = fieldGroup.getFieldNN("calculate");

                if (surChargeType.getId() == 2) {
                    fieldConfig.setVisible(true);
                    calculate(getItem().getValue() == null ? 0 : getItem().getValue());

                    valueField.addValueChangeListener(valueChangeEvent1 -> {
                        try {
                            Double value = Double.parseDouble(String.valueOf(valueChangeEvent1));
                            calculate(value);
                        } catch (Exception ex) {
                            getItem().setCalculate("0");
                        }
                    });
                } else {
                    fieldConfig.setVisible(false);
                    getItem().setCalculate("");
                }
                if (surChargeType.getId() == 1) {
                    fieldGroup.getFieldNN("grossNet").setRequired(true);
                }
            }
        });
        fieldGroup.getFieldNN("type").setComponent(typeEnum);

        OptionsGroup grossNet = componentsFactory.createComponent(OptionsGroup.class);
        grossNet.setOptionsEnum(SurChargeType.class);
        grossNet.setOrientation(OptionsGroup.Orientation.HORIZONTAL);
        grossNet.setDatasource(surChargeDs, "grossNet");
        fieldGroup.getFieldNN("grossNet").setComponent(grossNet);

    }

    protected void calculate(Double value) {
        Salary salary = getCurrentSalary();

        if (salary != null) {
            double calculatedValue = salary.getAmount() * value / 100;
            getItem().setCalculate(calculatedValue + "");
        } else {
            getItem().setCalculate("0");
        }
    }


    protected Salary getCurrentSalary() {
        UUID assignmentGroupId = null;
        if (surChargeDs.getItem().getAssignmentGroup() != null)
            assignmentGroupId = surChargeDs.getItem().getAssignmentGroup().getId();

        Salary salary = null;
        if (assignmentGroupId != null) {
            LoadContext<Salary> loadContext = LoadContext.create(Salary.class);
            loadContext.setQuery(LoadContext.createQuery(
                    "select e from tsadv$Salary e " +
                            "where e.assignmentGroup.id = :aId " +
                            "and :sysDate between e.startDate and e.endDate")
                    .setParameter("aId", assignmentGroupId)
                    .setParameter("sysDate", CommonUtils.getSystemDate()))
                    .setView("salary.browse");
            List<Salary> salaries = dataManager.loadList(loadContext);
            salary = salaries.isEmpty() ? null : salaries.get(0);
        } else {
            showNotification("AssignmentGroupID is null!");
        }
        return salary;
    }
}