package kz.uco.tsadv.web.screens.instructionskpi;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.performance.model.InstructionsKpi;

@UiController("tsadv_InstructionsKpi.edit")
@UiDescriptor("instructions-kpi-edit.xml")
@EditedEntityContainer("instructionsKpiDc")
@LoadDataBeforeShow
public class InstructionsKpiEdit extends StandardEditor<InstructionsKpi> {
}