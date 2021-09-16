package kz.uco.tsadv.web.screens.tsadvreport;

import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.administration.TsadvReport;

import javax.inject.Inject;

@UiController("tsadv_TsadvReport.forScreenshot")
@UiDescriptor("tsadv-report-for-screenshot.xml")
@LookupComponent("tsadvReportsTable")
@LoadDataBeforeShow
public class TsadvReportForScreenshot extends StandardLookup<TsadvReport> {

    @Inject
    protected GroupTable<TsadvReport> tsadvReportsTable;
    @Inject
    protected Notifications notifications;
    @Inject
    protected MessageBundle messageBundle;
    @Inject
    protected ScreenBuilders screenBuilders;

    @Subscribe("tsadvReportsTable.screenshot")
    protected void onTsadvReportsTableScreenshot(Action.ActionPerformedEvent event) {
        if (tsadvReportsTable.getSingleSelected() != null
                && tsadvReportsTable.getSingleSelected().getScreenshot() != null) {
            screenBuilders.editor(tsadvReportsTable)
                    .withScreenId("tsadv_TsadvReportScreenshot.edit")
                    .withOpenMode(OpenMode.THIS_TAB)
                    .build().show();
        } else {
            notifications.create().withPosition(Notifications.Position.BOTTOM_RIGHT)
                    .withCaption(messageBundle.getMessage("noScreenshot")).show();
        }
    }
}