package kz.uco.tsadv.web.screens.certificaterequest;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.CertificateRequest;


/**
 * User: maiha
 * Date: 08.01.2021
 * Time: 10:02
 */

@UiController("tsadv_CertificateRequestHR.browse")
@UiDescriptor("certificate-request-hr-browse.xml")
@LookupComponent("certificateRequestsTable")
@LoadDataBeforeShow
public class CertificateRequestHRBrowse extends StandardLookup<CertificateRequest> {
    public void print() {
    }
}