package kz.uco.tsadv.web.screens.dicperformancestage;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.performance.dictionary.DicPerformanceStage;

@UiController("tsadv_DicPerformanceStage.edit")
@UiDescriptor("dic-performance-stage-edit.xml")
@EditedEntityContainer("dicPerformanceStageDc")
@LoadDataBeforeShow
public class DicPerformanceStageEdit extends StandardEditor<DicPerformanceStage> {
}