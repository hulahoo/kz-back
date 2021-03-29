package kz.uco.tsadv.web.screens.coursecertificate;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.model.CourseCertificate;

@UiController("tsadv_CourseCertificate.edit")
@UiDescriptor("course-certificate-edit.xml")
@EditedEntityContainer("courseCertificateDc")
@DialogMode(forceDialog = true)
@LoadDataBeforeShow
public class CourseCertificateEdit extends StandardEditor<CourseCertificate> {
}