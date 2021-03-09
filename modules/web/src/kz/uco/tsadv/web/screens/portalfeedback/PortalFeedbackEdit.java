package kz.uco.tsadv.web.screens.portalfeedback;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.model.PortalFeedback;

@UiController("tsadv_PortalFeedback.edit")
@UiDescriptor("portal-feedback-edit.xml")
@EditedEntityContainer("portalFeedbackDc")
@LoadDataBeforeShow
public class PortalFeedbackEdit extends StandardEditor<PortalFeedback> {
}