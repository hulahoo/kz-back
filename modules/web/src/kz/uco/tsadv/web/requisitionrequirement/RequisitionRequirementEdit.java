package kz.uco.tsadv.web.requisitionrequirement;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.components.PickerField;
import kz.uco.tsadv.modules.recruitment.model.RcQuestion;
import kz.uco.tsadv.modules.recruitment.model.RequisitionRequirement;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

public class RequisitionRequirementEdit extends AbstractEditor<RequisitionRequirement> {
    @Named("fieldGroup.requisition")
    protected PickerField requisitionField;
    @Named("fieldGroup.requirement")
    protected PickerField<Entity> requirementField;
    @Inject
    protected LookupField requirementLevelLookupField;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.containsKey("excludeRcQuestionIds")) {
            requirementField.getLookupAction().setLookupScreenParams(params);
        }
    }

    @Override
    public void ready() {
        super.ready();
        requisitionField.setEditable(!(getItem() != null && getItem().getRequisition() != null));
        if (requirementField.getValue()!=null){
            requirementLevelLookupField.setOptionsList(((RcQuestion) requirementField.getValue()).getAnswers());
        }
        requirementField.addValueChangeListener(e -> {
            requirementLevelLookupField.setOptionsList(((RcQuestion) e.getValue()).getAnswers());
        });
    }
}