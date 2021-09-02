package kz.uco.tsadv.web.screens.tsadvreport;

import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.administration.TsadvReport;

@UiController("tsadv_TsadvReportScreenshot.edit")
@UiDescriptor("tsadv-report-screenshot-edit.xml")
@EditedEntityContainer("tsadvReportDc")
@LoadDataBeforeShow
public class TsadvReportScreenshotEdit extends StandardEditor<TsadvReport> {
}