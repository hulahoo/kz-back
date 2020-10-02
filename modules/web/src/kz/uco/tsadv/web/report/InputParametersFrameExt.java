package kz.uco.tsadv.web.report;

import com.haulmont.cuba.gui.components.CaptionMode;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.cuba.gui.components.TokenList;
import com.haulmont.reports.entity.ReportInputParameter;
import com.haulmont.reports.gui.report.run.InputParametersFrame;
import kz.uco.tsadv.ReportInputParameterExt;
import org.apache.commons.lang3.StringUtils;

public class InputParametersFrameExt extends InputParametersFrame {

    @Override
    protected void createComponent(ReportInputParameter parameter, int currentGridRow, boolean visible) {
        super.createComponent(parameter, currentGridRow, true);

        ReportInputParameterExt parameterExt = (ReportInputParameterExt) parameter;
        String captionProperty = parameterExt.getCaptionProperty();
        if (StringUtils.isNotBlank(captionProperty)) {
            Component component = parametersGrid.getComponent(1, currentGridRow);
            if (component != null) {
                if (component instanceof PickerField) {
                    ((PickerField) component).setCaptionMode(CaptionMode.PROPERTY);
                    ((PickerField) component).setCaptionProperty(captionProperty);
                } else if (component instanceof TokenList) {
                    ((TokenList) component).setCaptionMode(CaptionMode.PROPERTY);
                    ((TokenList) component).setCaptionProperty(captionProperty);
                }
            }
        }
    }
}