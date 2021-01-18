package kz.uco.tsadv.web.screens.correctioncoefficient;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.performance.model.CorrectionCoefficient;

@UiController("tsadv_CorrectionCoefficient.edit")
@UiDescriptor("correction-coefficient-edit.xml")
@EditedEntityContainer("correctionCoefficientDc")
@LoadDataBeforeShow
public class CorrectionCoefficientEdit extends StandardEditor<CorrectionCoefficient> {
}