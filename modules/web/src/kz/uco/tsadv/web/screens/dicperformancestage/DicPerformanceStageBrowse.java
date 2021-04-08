package kz.uco.tsadv.web.screens.dicperformancestage;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.performance.dictionary.DicPerformanceStage;

@UiController("tsadv_DicPerformanceStage.browse")
@UiDescriptor("dic-performance-stage-browse.xml")
@LookupComponent("dicPerformanceStagesTable")
@LoadDataBeforeShow
public class DicPerformanceStageBrowse extends StandardLookup<DicPerformanceStage> {
}