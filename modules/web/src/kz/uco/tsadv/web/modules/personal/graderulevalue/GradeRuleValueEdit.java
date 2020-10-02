package kz.uco.tsadv.web.modules.personal.graderulevalue;

import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.components.*;
import kz.uco.tsadv.modules.personal.model.GradeRuleValue;
import kz.uco.tsadv.gui.components.AbstractHrEditor;
import kz.uco.base.service.common.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradeRuleValueEdit extends AbstractHrEditor<GradeRuleValue> {
    private static final Logger log = LoggerFactory.getLogger(GradeRuleValueEdit.class);

    @Inject
    private FieldGroup fieldGroup;

    @Inject
    private OptionsGroup<String, String> mode;

    @Inject
    @Named("fieldGroup.gradeGroup")
    private PickerField gradeGroup;

    @Inject
    @Named("fieldGroup.value")
    private TextField value;
    @Inject
    @Named("fieldGroup.min")
    private TextField min;
    @Inject
    @Named("fieldGroup.mid")
    private TextField mid;
    @Inject
    @Named("fieldGroup.max")
    private TextField max;

    @Inject
    private CommonService commonService;

    @Override
    protected FieldGroup getStartEndDateFieldGroup() {
        return fieldGroup;
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        Map<String, String> optionsMap = new HashMap<>();
        optionsMap.put(getMessage("GradeRuleValue.mode.value"), "value");
        optionsMap.put(getMessage("GradeRuleValue.mode.plug"), "plug");
        mode.setOptionsMap(optionsMap);

        mode.addValueChangeListener((e) -> {
            if (e.getValue() != null) {
                value.setEditable("value".equals(e.getValue()));
                min.setEditable("plug".equals(e.getValue()));
                mid.setEditable("plug".equals(e.getValue()));
                max.setEditable("plug".equals(e.getValue()));

                value.setRequired("value".equals(e.getValue()));
                min.setRequired("plug".equals(e.getValue()));
                mid.setRequired("plug".equals(e.getValue()));
                max.setRequired("plug".equals(e.getValue()));

                if ("value".equals(e.getValue())) {
                    min.setValue(null);
                    mid.setValue(null);
                    max.setValue(null);
                } else if ("plug".equals(e.getValue())) {
                    value.setValue(null);
                }
            }
        });

        gradeGroup.addValidator(value -> {
            if (PersistenceHelper.isNew(getItem()) && value != null && gradeRuleValueExists())
                throw new ValidationException(getMessage("GradeRuleValueEdit.gradeGroup.validatorMsg"));
        });
    }

    @Override
    public void ready() {
        super.ready();

        if (getItem().getGradeGroup() != null)
            gradeGroup.setEditable(false);

        if (getItem().getValue() != null)
            mode.setValue("value");
        else if (getItem().getMin() != null || getItem().getMid() != null || getItem().getMax() != null)
            mode.setValue("plug");
        else
            mode.setValue("value");
    }

    private boolean gradeRuleValueExists() {
        boolean exists = false;

        Map<String, Object> params = new HashMap<>();
        params.put("gradeGroupId", getItem().getGradeGroup().getId());
        params.put("gradeRuleId", getItem().getGradeRule().getId());
        List<GradeRuleValue> gradeRuleValueList = commonService.getEntities(GradeRuleValue.class,
                "select e " +
                        " from tsadv$GradeRuleValue e " +
                        " where e.gradeGroup.id = :gradeGroupId " +
                        " and e.gradeRule.id = :gradeRuleId",
                params,
                View.LOCAL);

        exists = !gradeRuleValueList.isEmpty();

        return exists;
    }
}