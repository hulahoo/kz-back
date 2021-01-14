package kz.uco.tsadv.web.screens.certificatetemplate;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.CertificateTemplate;


/**
 * User: maiha
 * Date: 05.01.2021
 * Time: 16:19
 */

@UiController("tsadv_CertificateTemplate.browse")
@UiDescriptor("certificate-template-browse.xml")
@LookupComponent("certificateTemplatesTable")
@LoadDataBeforeShow
public class CertificateTemplateBrowse extends StandardLookup<CertificateTemplate> {
}