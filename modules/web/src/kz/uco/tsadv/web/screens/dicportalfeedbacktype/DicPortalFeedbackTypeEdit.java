package kz.uco.tsadv.web.screens.dicportalfeedbacktype;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.model.DicPortalFeedbackType;


/**
 * @author Alibek Berdaulet
 */
@UiController("tsadv_DicPortalFeedbackType.edit")
@UiDescriptor("dic-portal-feedback-type-edit.xml")
@EditedEntityContainer("dicPortalFeedbackTypeDc")
@LoadDataBeforeShow
public class DicPortalFeedbackTypeEdit extends StandardEditor<DicPortalFeedbackType> {
}