package kz.uco.tsadv.web.modules.timesheet.scheduleheader;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.timesheet.model.ScheduleHeader;

import javax.inject.Inject;
import java.util.UUID;

public class ScheduleHeaderBrowse extends AbstractLookup {

    @Inject
    private GroupTable<ScheduleHeader> scheduleHeadersTable;
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private GroupDatasource<ScheduleHeader, UUID> scheduleHeadersDs;

    @Override
    public void ready() {
        super.ready();

        for (int i = 1; i <= 31; i++) {
            final int ii = i;
            scheduleHeadersTable.addGeneratedColumn("s" + ii, entity -> {
                if (entity.getSummariesMap().containsKey(ii)) {
                    Label v = componentsFactory.createComponent(Label.class);
                    v.setValue(entity.getSummariesMap().get(ii).getShiftName());
                    v.setHtmlEnabled(false);
                    return v;
                }
                return null;
            });

            scheduleHeadersTable.getColumn("s" + ii).setCaption("" + ii);
            scheduleHeadersTable.getColumn("s" + ii).setWidth(50);
        }

    }

    public void formSchedule() {
        AbstractWindow formSchedule = openWindow("form-schedule", WindowManager.OpenType.THIS_TAB);
        formSchedule.addCloseWithCommitListener(() -> scheduleHeadersDs.refresh());
    }
}