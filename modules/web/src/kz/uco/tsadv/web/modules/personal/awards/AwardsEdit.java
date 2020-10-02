package kz.uco.tsadv.web.modules.personal.awards;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicAwardType;
import kz.uco.tsadv.modules.personal.model.Awards;
import kz.uco.tsadv.modules.personal.model.Salary;
import kz.uco.tsadv.modules.personal.model.SurChargePeriod;
import kz.uco.tsadv.modules.personal.model.SurChargeType;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AwardsEdit extends AbstractEditor<Awards> {
    @Inject
    private Datasource<Awards> awardsDs;
    @Inject
    private DataManager dataManager;
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private FieldGroup fieldGroup;
    @Named("fieldGroup.value")
    private TextField valueField;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        OptionsGroup typeEnum = componentsFactory.createComponent(OptionsGroup.class);
        typeEnum.setOptionsEnum(SurChargeType.class);
        typeEnum.setOrientation(OptionsGroup.Orientation.HORIZONTAL);
        typeEnum.setDatasource(awardsDs, "surChargeType");
//        typeEnum.addValueChangeListener(new ValueChangeListener() {
//            @Override
//            public void valueChanged(ValueChangeEvent e) {
//                SurChargeType surChargeType = (SurChargeType) e.getValue();
//                if (surChargeType != null) {
//                    FieldGroup.FieldConfig fieldConfig = fieldGroup.getFieldNN("calculated");
//                    if (surChargeType.getId() == 2) {
//                        fieldConfig.setVisible(true);
//                        calculate(getItem().getValue() == null ? 0 : getItem().getValue());
//
//                        valueField.addValueChangeListener(e1 -> {
//                            try {
//                                int value = Integer.parseInt(String.valueOf(e1.getValue()));
//                                calculate(value);
//                            } catch (Exception ex) {
//                                getItem().setCalculated("0");
//                            }
//                        });
//                    } else {
//                        fieldConfig.setVisible(false);
//                        getItem().setCalculated("");
//                    }
//                }
//            }
//        });
        fieldGroup.getFieldNN("surChargeType").setComponent(typeEnum);

        FieldGroup.FieldConfig promotionTypeConfig = fieldGroup.getField("promotionType");

        FieldGroup.FieldConfig awardConfig = fieldGroup.getField("awardType");
        PickerField awardPickerField = componentsFactory.createComponent(PickerField.class);
        awardPickerField.setDatasource(awardsDs, awardConfig.getProperty());

        awardPickerField.addAction(new BaseAction("openAward"){
            @Override
            public void actionPerform(Component component) {
                super.actionPerform(component);
                Map<String, Object> map = new HashMap<>();
                if (awardsDs.getItem().getPromotionType() != null) {
                    map.put("promotionTypeId", awardsDs.getItem().getPromotionType() != null ? awardsDs.getItem().getPromotionType().getId() : null);
                    openLookup("tsadv$DicAwardType.browse",
                            (items -> {
                            for (Object o : items){
                                getItem().setAwardType((DicAwardType) o);
                            }
                    }), WindowManager.OpenType.THIS_TAB, map);
                }else {
                    showNotification(getMessage("promotionTypeRequiredMessage"), NotificationType.TRAY);
                }
            }

            @Override
            public String getIcon() {
                return "font-icon:ELLIPSIS_H";
            }
        });
        awardPickerField.addClearAction();
        awardConfig.setComponent(awardPickerField);
    }

    @Override
    protected void initNewItem(Awards item) {
        super.initNewItem(item);
    }

    private void calculate(int value) {
        Salary salary = getCurrentSalary();

        if (salary != null) {
            double calculatedValue = salary.getAmount() * value / 100;
            getItem().setCalculated(calculatedValue + "");
        } else {
            getItem().setCalculated("0");
        }
    }

    private Salary getCurrentSalary() {
        UUID assignmentGroupId = null;
        if (awardsDs.getItem().getAssignmentGroup()!=null)
            assignmentGroupId  = awardsDs.getItem().getAssignmentGroup().getId();
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
    protected boolean postCommit(boolean committed, boolean close) {
        if (committed && close){
            showNotification(getMessage("person.card.commit.title"), getMessage("person.card.commit.msg"), Frame.NotificationType.TRAY);
        }
        return super.postCommit(committed, close);
    }
}