package kz.uco.tsadv.web.screens.dicportalfeedbacktype;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.model.DicPortalFeedbackType;


/**
 * @author Alibek Berdaulet
 */
@UiController("tsadv_DicPortalFeedbackType.browse")
@UiDescriptor("dic-portal-feedback-type-browse.xml")
@LookupComponent("dicPortalFeedbackTypesTable")
@LoadDataBeforeShow
public class DicPortalFeedbackTypeBrowse extends StandardLookup<DicPortalFeedbackType> {
}