package kz.uco.tsadv.web.screens.diccertificatetype;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.dictionary.DicCertificateType;


/**
 * User: maiha
 * Date: 25.12.2020
 * Time: 12:53
 */

@UiController("tsadv_DicCertificateType.browse")
@UiDescriptor("dic-certificate-type-browse.xml")
@LookupComponent("dicCertificateTypesTable")
@LoadDataBeforeShow
public class DicCertificateTypeBrowse extends StandardLookup<DicCertificateType> {
}