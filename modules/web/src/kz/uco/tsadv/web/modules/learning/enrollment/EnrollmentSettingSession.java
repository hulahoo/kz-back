package kz.uco.tsadv.web.modules.learning.enrollment;

import com.haulmont.cuba.gui.components.GroupBoxLayout;
import kz.uco.tsadv.modules.learning.model.Enrollment;

import javax.inject.Inject;
import java.util.Map;

public class EnrollmentSettingSession<T extends Enrollment> extends AbstractEnrollmentEditor<T> {

    @Inject
    private GroupBoxLayout sessionGroupBox;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    protected void postInit() {
        super.postInit();

        initVisibleComponent(getItem().getCourse());
    }

    @Override
    public GroupBoxLayout getSectionsGroupBox() {
        return sessionGroupBox;
    }

    @Override
    public void editable(boolean editable) {

    }

}