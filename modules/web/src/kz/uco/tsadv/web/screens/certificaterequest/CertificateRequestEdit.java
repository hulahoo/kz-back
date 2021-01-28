package kz.uco.tsadv.web.screens.certificaterequest;

import com.haulmont.addon.bproc.web.processform.Outcome;
import com.haulmont.addon.bproc.web.processform.ProcessForm;
import com.haulmont.cuba.gui.components.Form;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.modules.personal.model.CertificateRequest;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.web.abstraction.bproc.AbstractBprocEditor;

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
@ProcessForm(
        outcomes = {
                @Outcome(id = AbstractBprocRequest.OUTCOME_REVISION),
                @Outcome(id = AbstractBprocRequest.OUTCOME_APPROVE),
                @Outcome(id = AbstractBprocRequest.OUTCOME_REJECT)
        }
)
public class CertificateRequestEdit extends AbstractBprocEditor<CertificateRequest> {

    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected UserSession userSession;
    @Inject
    protected Form form;

    @Subscribe
    protected void onInitEntity(InitEntityEvent<CertificateRequest> event) {
        CertificateRequest certificateRequest = event.getEntity();
        certificateRequest.setPersonGroup(employeeService.getPersonGroupByUserIdExtendedView(userSession.getUser().getId()));
    }

    @Override
    protected void initEditableFields() {
        super.initEditableFields();
        form.setEditable(isDraft());
    }
}