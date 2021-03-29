package kz.uco.tsadv.web.screens.diclmssliderposition;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.dictionary.DicLmsSliderPosition;

@UiController("tsadv_DicLmsSliderPosition.browse")
@UiDescriptor("dic-lms-slider-position-browse.xml")
@LookupComponent("dicLmsSliderPositionsTable")
@LoadDataBeforeShow
public class DicLmsSliderPositionBrowse extends StandardLookup<DicLmsSliderPosition> {
}