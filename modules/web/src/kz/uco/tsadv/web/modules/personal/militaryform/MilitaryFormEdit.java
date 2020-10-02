package kz.uco.tsadv.web.modules.personal.militaryform;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.ScrollBoxLayout;
import kz.uco.tsadv.modules.personal.model.MilitaryForm;

import javax.inject.Inject;
import java.util.Map;

public class MilitaryFormEdit extends AbstractEditor<MilitaryForm> {
    @Inject
    private FieldGroup fieldGroup;
    @Inject
    private ScrollBoxLayout scrollBox;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
//        scrollBox.setWidth(String.valueOf(fieldGroup.getWidth()));
//        scrollBox.setWidthFull();
//        scrollBox.setHeight(String.valueOf(fieldGroup.getWidth()));
    }
}