package kz.uco.tsadv.web.lookupvalue;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.PickerField;
import kz.uco.tsadv.modules.administration.LookupValue;

import javax.inject.Named;
import java.util.Map;

public class LookupValueEdit extends AbstractEditor<LookupValue> {

    @Named("fieldGroup.lookupType")
    protected PickerField lookupTypeField;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.containsKey("lookupType")) {
            lookupTypeField.setValue(params.get("lookupType"));
        }
    }
}