package kz.uco.tsadv.web.workplace;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import kz.uco.tsadv.entity.tb.WorkPlace;

import javax.inject.Inject;
import java.util.Map;

public class WorkPlaceEdit extends AbstractEditor<WorkPlace> {

    @Inject
    private FieldGroup fieldGroup;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.get("readOnly") != null) {
            readOnlyMode();
        }
    }

    private void readOnlyMode() {
        fieldGroup.setEditable(false);
    }
}