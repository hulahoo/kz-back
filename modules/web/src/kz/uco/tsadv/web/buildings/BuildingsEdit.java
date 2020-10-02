package kz.uco.tsadv.web.buildings;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import kz.uco.tsadv.entity.tb.Buildings;

import javax.inject.Inject;
import java.util.Map;

public class BuildingsEdit extends AbstractEditor<Buildings> {

    @Inject
    private FieldGroup fieldGroup;
    @Inject
    private FieldGroup fieldGroup1;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.get("readOnly") != null) {
            readOnlyMode();
        }
    }

    private void readOnlyMode() {
        fieldGroup.setEditable(false);
        fieldGroup1.setEditable(false);
    }
}