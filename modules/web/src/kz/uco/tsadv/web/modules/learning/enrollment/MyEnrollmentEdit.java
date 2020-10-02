package kz.uco.tsadv.web.modules.learning.enrollment;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.AbstractEditor;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;
import kz.uco.tsadv.modules.learning.model.Enrollment;

import javax.inject.Inject;

public class MyEnrollmentEdit extends AbstractEditor<Enrollment> {

    @Inject
    private DataManager dataManager;

    @Override
    protected boolean preCommit() {
        getItem().setStatus(EnrollmentStatus.CANCELLED);
        return super.preCommit();
    }
}