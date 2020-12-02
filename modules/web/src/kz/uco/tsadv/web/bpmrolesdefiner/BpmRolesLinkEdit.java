package kz.uco.tsadv.web.bpmrolesdefiner;

//import com.haulmont.bpm.entity.ProcRole;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.LookupPickerField;
import com.haulmont.cuba.gui.components.ValidationErrors;
import kz.uco.tsadv.modules.bpm.BpmRolesLink;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

public class BpmRolesLinkEdit extends AbstractEditor<BpmRolesLink> {
    @Named("fieldGroup.bpmRole")
    private LookupPickerField<Entity> bpmRoleField;
    @Inject
    private FieldGroup fieldGroup;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.containsKey("requiredVisible")) {
            fieldGroup.getFieldNN("required").setVisible((boolean) params.get("requiredVisible"));
        }
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        if (!(getItem().getBpmRolesDefiner() != null || getItem().getPositionBpmRole() != null)) {
            errors.add("Need to choose BpmRolesDefiner or PositionBpmRole!");
        }
    }

    @Override
    public void ready() {
        super.ready();
        /*bpmRoleField.addValueChangeListener(e -> {
            if (e.getValue() != null && ((ProcRole) e.getValue()).getCode().equalsIgnoreCase("admin_approve")) {
                showNotification(messages.getMessage(this.getClass(), "admin.approve"), NotificationType.TRAY);
                bpmRoleField.setValue(null);
            }
        });*/
    }
}