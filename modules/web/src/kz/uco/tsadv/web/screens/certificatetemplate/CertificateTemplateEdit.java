package kz.uco.tsadv.web.screens.certificatetemplate;

import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.tsadv.modules.personal.model.CertificateTemplate;
import kz.uco.tsadv.service.EmployeeService;

import javax.inject.Inject;


/**
 * User: maiha
 * Date: 05.01.2021
 * Time: 16:19
 */

@UiController("tsadv_CertificateTemplate.edit")
@UiDescriptor("certificate-template-edit.xml")
@EditedEntityContainer("certificateTemplateDc")
@LoadDataBeforeShow
public class CertificateTemplateEdit extends StandardEditor<CertificateTemplate> {
    @Inject
    protected UserSession userSession;
    @Inject
    protected EmployeeService employeeService;

    @Subscribe
    protected void onAfterShow(AfterShowEvent event) {
        CertificateTemplate editedEntity = getEditedEntity();
        if (PersistenceHelper.isNew(editedEntity)) {
            editedEntity.setSigner(employeeService.getPersonGroupByUserId(userSession.getUser().getId()));
        }
    }


}