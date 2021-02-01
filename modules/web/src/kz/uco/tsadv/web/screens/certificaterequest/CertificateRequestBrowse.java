package kz.uco.tsadv.web.screens.certificaterequest;

import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.CertificateRequest;
import kz.uco.tsadv.service.EmployeeService;

import javax.inject.Inject;


/**
 * User: maiha
 * Date: 25.12.2020
 * Time: 11:37
 */

@UiController("tsadv_CertificateRequest.browse")
@UiDescriptor("certificate-request-browse.xml")
@LookupComponent("certificateRequestsTable")
@LoadDataBeforeShow
public class CertificateRequestBrowse extends StandardLookup<CertificateRequest> {

    @Inject
    protected CollectionContainer<CertificateRequest> certificateRequestsDc;
    @Inject
    protected CollectionLoader<CertificateRequest> certificateRequestsDl;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected UserSession userSession;
    @Inject
    protected ExportDisplay exportDisplay;
    @Inject
    protected ScreenBuilders screenBuilders;
    @Inject
    protected GroupTable<CertificateRequest> certificateRequestsTable;

    @Subscribe
    protected void onInit(InitEvent event) {
        User user = userSession.getUser();
        PersonGroupExt personGroupExt = employeeService.getPersonGroupByUserId(user.getId());
        certificateRequestsDl.setParameter("session$userPersonGroupId", personGroupExt.getId());
    }


    public void printReport(CertificateRequest item, String columnId) {
        exportDisplay.show(item.getFile());
    }

    public void openRequest(CertificateRequest certificateRequest, String columnId) {
        screenBuilders.editor(certificateRequestsTable)
                .editEntity(certificateRequest)
                .build()
                .show();
    }
}