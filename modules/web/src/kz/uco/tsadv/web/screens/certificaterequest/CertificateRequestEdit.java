package kz.uco.tsadv.web.screens.certificaterequest;

import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.tsadv.modules.personal.model.CertificateRequest;
import kz.uco.tsadv.service.EmployeeService;

import javax.inject.Inject;


/**
 * User: maiha
 * Date: 25.12.2020
 * Time: 11:37
 */

@UiController("tsadv_CertificateRequest.edit")
@UiDescriptor("certificate-request-edit.xml")
@EditedEntityContainer("certificateRequestDc")
@LoadDataBeforeShow
public class CertificateRequestEdit extends StandardEditor<CertificateRequest> {

    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected UserSession userSession;

    @Subscribe
    protected void onInitEntity(InitEntityEvent<CertificateRequest> event) {
        CertificateRequest certificateRequest = event.getEntity();
        certificateRequest.setPersonGroup(employeeService.getPersonGroupByUserIdExtendedView(userSession.getUser().getId()));
    }
}