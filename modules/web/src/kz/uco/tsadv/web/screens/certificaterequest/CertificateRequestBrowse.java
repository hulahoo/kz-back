package kz.uco.tsadv.web.screens.certificaterequest;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.CertificateRequest;


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
}