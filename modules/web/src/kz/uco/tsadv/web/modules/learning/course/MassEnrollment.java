package kz.uco.tsadv.web.modules.learning.course;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.CheckBox;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.modules.learning.model.ForMassEnrollment;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

public class MassEnrollment extends AbstractEditor<ForMassEnrollment> {

    @Named("fieldGroup.check")
    protected CheckBox checkField;
    @Named("fieldGroup.position")
    protected PickerField positionField;
    @Named("fieldGroup.organization")
    protected PickerField organizationField;
    @Named("fieldGroup.job")
    protected PickerField jobField;
    @Named("fieldGroup.course")
    protected PickerField courseField;
    @Inject
    protected Datasource<ForMassEnrollment> forMassEnrollmentDs;

    protected Map<String, Object> param;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        param = params;
        addBeforeCloseWithCloseButtonListener(event -> {
            close("cancel", true);
        });
        organizationFieldRequestFocus();
    }

    @Override
    public void ready() {
        super.ready();

        setCourseFieldDescription();
    }

    protected void organizationFieldRequestFocus() {
        if (!courseField.isEditable()) {
            organizationField.requestFocus();
        }
    }

    protected void setCourseFieldDescription() {
        if (getItem().getCourse() != null) {
            courseField.setDescription(getItem().getCourse().getName());
        }
    }

    public void onCancelBtnClick() {
        close("cancel", true);
    }

    public void onOkBtnClick() {
        Map<String, Object> map = new HashMap<>();
        if (organizationField.getValue() != null) {
            map.put("organizationGroup", organizationField.getValue());
            map.put("inOrganizaiton", checkField.getValue());
        }
        if (positionField.getValue() != null) {
            map.put("positionGroup", positionField.getValue());
        }
        if (jobField.getValue() != null) {
            map.put("jobGroup", jobField.getValue());
        }
        map.put("courseId", param.containsKey("courseId") ? param.get("courseId") : null);
        AbstractWindow window = openWindow("person-for-mass-enrollment",
                WindowManager.OpenType.THIS_TAB, map);
        window.addCloseListener(actionId -> {
            if ("commit".equals(actionId)) {
                close(actionId, true);
            }
        });
    }
}