package kz.uco.tsadv.web.screens.portalfeedback;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.model.PortalFeedback;

@UiController("tsadv_PortalFeedback.browse")
@UiDescriptor("portal-feedback-browse.xml")
@LookupComponent("portalFeedbacksTable")
@LoadDataBeforeShow
public class PortalFeedbackBrowse extends StandardLookup<PortalFeedback> {
}