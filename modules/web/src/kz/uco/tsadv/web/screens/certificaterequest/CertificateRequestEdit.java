package kz.uco.tsadv.web.screens.certificaterequest;

import com.haulmont.addon.bproc.web.processform.Outcome;
import com.haulmont.addon.bproc.web.processform.ProcessForm;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.Form;
import com.haulmont.cuba.gui.components.HasValue;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicReceivingType;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.CertificateRequest;
import kz.uco.tsadv.service.CommonReportsService;
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
    protected InstanceContainer<AssignmentExt> assignmentDc;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected UserSession userSession;
    @Inject
    protected Form form;
    @Inject
    protected Button getReferenceBtn;
    @Inject
    protected CommonReportsService commonReportsService;

    @Override
    protected void initVariables() {
        super.initVariables();
        initAssignment();
    }

    @Override
    protected void initVisibleFields() {
        super.initVisibleFields();
        btnSettings(getEditedEntity().getReceivingType());
    }

    protected void initAssignment() {
        AssignmentExt assignmentExt = employeeService
                .getAssignmentExt(getEditedEntity().getPersonGroup().getId(), CommonUtils.getSystemDate(), "assignment.view");
        assignmentDc.setItem(assignmentExt);
    }

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

    @Subscribe("receivingTypeField")
    protected void onReceivingTypeFieldValueChange(HasValue.ValueChangeEvent<DicReceivingType> event) {
        btnSettings(event.getValue());
    }

    protected void btnSettings(DicReceivingType receivingType) {
        boolean visible = !isDraft() || receivingType != null && "ON_HAND".equals(receivingType.getCode());
        getReferenceBtn.setVisible(!visible);
        procActionButtonHBox.setVisible(visible);
    }

    @Subscribe("getReferenceBtn")
    protected void onGetReferenceBtnClick(Button.ClickEvent event) {
        closeWithCommit();
    }
}