package kz.uco.tsadv.web.screens.diclmssliderposition;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.dictionary.DicLmsSliderPosition;

@UiController("tsadv_DicLmsSliderPosition.edit")
@UiDescriptor("dic-lms-slider-position-edit.xml")
@EditedEntityContainer("dicLmsSliderPositionDc")
@LoadDataBeforeShow
public class DicLmsSliderPositionEdit extends StandardEditor<DicLmsSliderPosition> {
}