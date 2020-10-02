package kz.uco.tsadv.web.modules.personal.globalvalue;

import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.TextField;
import kz.uco.tsadv.modules.personal.model.GlobalValue;
import kz.uco.tsadv.gui.components.AbstractHrEditor;

import javax.inject.Inject;
import javax.inject.Named;

public class GlobalValueEdit extends AbstractHrEditor<GlobalValue> {
    @Inject
    private FieldGroup fieldGroup;
    @Named("fieldGroup.code")
    private TextField codeField;

    @Override
    protected void postInit() {
        super.postInit();
        if (getItem().getCode() != null) {
            codeField.setEditable(false);
        }
    }

    @Override
    protected FieldGroup getStartEndDateFieldGroup() {
        return fieldGroup;
    }
}