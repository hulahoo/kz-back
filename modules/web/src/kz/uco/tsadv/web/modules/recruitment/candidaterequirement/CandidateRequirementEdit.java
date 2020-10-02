package kz.uco.tsadv.web.modules.recruitment.candidaterequirement;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.PickerField;
import kz.uco.tsadv.modules.recruitment.model.CandidateRequirement;

import javax.inject.Named;
import java.util.Map;

public class CandidateRequirementEdit extends AbstractEditor<CandidateRequirement> {
    @Named("fieldGroup.personGroup")
    protected PickerField personGroupField;
    @Named("fieldGroup.requirement")
    protected PickerField requirementField;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.containsKey("fromJobRequest")){
            personGroupField.setEditable(false);
            requirementField.setEditable(false);
        }
    }
}