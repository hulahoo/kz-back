package kz.uco.tsadv.web.modules.personal.person.frames;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.components.ButtonsPanel;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.Salary;
import kz.uco.tsadv.modules.personal.model.SurCharge;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class PcfSurCharge extends EditableFrame {

    @Inject
    private ButtonsPanel buttonsPanel;

    public Datasource<AssignmentExt> assignmentDs;
    public Datasource<SurCharge> surChargeDs;

    @Named("surChargeTable.create")
    protected CreateAction surChargeTableCreate;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        surChargeTableCreate.setInitialValuesSupplier(() -> getInitialValueMap());
    }

    protected Map<String,Object> getInitialValueMap() {
        return getDsContext().get("assignmentGroupDs") != null
                ? ParamsMap.of("assignmentGroup", getDsContext().get("assignmentGroupDs").getItem())
                : null;
    }

    public Component calculatePercent(SurCharge entity) {
        initDatasource();
        Label label = componentsFactory.createComponent(Label.class);
        label.setValue(entity.getValue());

        if (entity.getType().getId() == 2) {
            Salary salary = getCurrentSalary();
            if (salary != null) {
                double calculatedValue = salary.getAmount() * entity.getValue() / 100;
                label.setValue(calculatedValue);
            } else {
                label.setValue("0");
            }
        }
        return label;
    }

    public Component calculateValue(SurCharge entity) {
        Label label = componentsFactory.createComponent(Label.class);
        // label.setValue(entity.getValue());
        label.setValue(entity.getType().getId() == 2 ? entity.getValue() + "%" : "");
        return label;
    }

    private Salary getCurrentSalary() {
        UUID assignmentGroupId = assignmentDs.getItem().getGroup().getId();
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
            salary = dataManager.load(loadContext);
        } else {
            showNotification("AssignmentGroupID is null!");
        }
        return salary;
    }

    @Override
    public void editable(boolean editable) {
        buttonsPanel.setVisible(editable);
    }

    @Override
    public void initDatasource() {
        assignmentDs = (Datasource<AssignmentExt>) getDsContext().get("assignmentDs");
        surChargeDs = (Datasource<SurCharge>) getDsContext().get("surChargeDs");
    }
}