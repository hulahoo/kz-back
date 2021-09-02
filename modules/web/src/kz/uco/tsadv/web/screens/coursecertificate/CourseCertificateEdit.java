package kz.uco.tsadv.web.screens.coursecertificate;

import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.administration.TsadvReport;
import kz.uco.tsadv.modules.learning.model.CourseCertificate;

import javax.inject.Inject;

@UiController("tsadv_CourseCertificate.edit")
@UiDescriptor("course-certificate-edit.xml")
@EditedEntityContainer("courseCertificateDc")
@DialogMode(forceDialog = true)
@LoadDataBeforeShow
public class CourseCertificateEdit extends StandardEditor<CourseCertificate> {
    @Inject
    protected ScreenBuilders screenBuilders;

    @Subscribe("certificate.lookup")
    protected void onCertificateLookup(Action.ActionPerformedEvent event) {
        screenBuilders.lookup(TsadvReport.class, this).withScreenId("tsadv_TsadvReport.forScreenshot")
                .withOpenMode(OpenMode.THIS_TAB)
                .build().show();
    }
}