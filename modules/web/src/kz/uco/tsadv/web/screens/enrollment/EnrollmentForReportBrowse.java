package kz.uco.tsadv.web.screens.enrollment;

import com.haulmont.cuba.gui.components.CheckBox;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.learning.model.Enrollment;

import javax.inject.Inject;

@UiController("tsadv$EnrollmentForReport.browse")
@UiDescriptor("enrollment-for-report-browse.xml")
@LookupComponent("enrollmentsTable")
@LoadDataBeforeShow
public class EnrollmentForReportBrowse extends StandardLookup<Enrollment> {

    @Inject
    protected ComponentsFactory componentsFactory;

    public Component checkIsCertification(Enrollment enrollment) {
        CheckBox checkBox = componentsFactory.createComponent(CheckBox.class);
        checkBox.setValue(enrollment.getCertificationEnrollment() != null && enrollment.getCertificationEnrollment().getId() != null);
        checkBox.setEditable(false);
        return checkBox;
    }
}