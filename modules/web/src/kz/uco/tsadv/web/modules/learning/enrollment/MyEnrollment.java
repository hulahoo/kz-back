package kz.uco.tsadv.web.modules.learning.enrollment;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.CheckBox;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;
import kz.uco.tsadv.modules.learning.model.Enrollment;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

/**
 * 1-Request, 2-Waitlist, 3-Approved, 4-Cancelled, 5-Completed
 */

public class MyEnrollment extends AbstractLookup {

    @Inject
    private ComponentsFactory componentsFactory;

    @Named("enrollmentsTable.edit")
    private EditAction enrollmentsTableEdit;

    @Inject
    private GroupDatasource<Enrollment, UUID> enrollmentsDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        enrollmentsDs.addItemChangeListener(new Datasource.ItemChangeListener<Enrollment>() {
            @Override
            public void itemChanged(Datasource.ItemChangeEvent<Enrollment> e) {
                initEditAccess(e.getItem());
            }
        });

        initEditAccess(enrollmentsDs.getItem());
        enrollmentsTableEdit.setWindowId("tsadv$MyEnrollment.edit");
    }

    public Component checkIsCertification(Enrollment enrollment) {
        CheckBox checkBox = componentsFactory.createComponent(CheckBox.class);
        checkBox.setValue(enrollment.getCertificationEnrollment() != null && enrollment.getCertificationEnrollment().getId() != null);
        checkBox.setEditable(false);
        return checkBox;
    }

    private void initEditAccess(Enrollment enrollment) {
        if (enrollment != null) {
            EnrollmentStatus status = enrollment.getStatus();
            boolean access = true;
            if (enrollment.getCertificationEnrollment() != null && enrollment.getCertificationEnrollment().getId() != null) {
                access = false;
            } else {
                if (!status.equals(EnrollmentStatus.REQUEST) && !status.equals(EnrollmentStatus.WAITLIST)) {
                    access = false;
                }
            }

            enrollmentsTableEdit.setEnabled(access);
        }
    }
}