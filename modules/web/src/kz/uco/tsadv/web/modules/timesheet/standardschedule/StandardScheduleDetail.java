package kz.uco.tsadv.web.modules.timesheet.standardschedule;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.timesheet.model.ScheduleHeader;
import kz.uco.tsadv.modules.timesheet.model.ScheduleSummary;
import kz.uco.tsadv.service.TimecardService;
import kz.uco.tsadv.service.TimesheetService;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class StandardScheduleDetail extends AbstractLookup {

    @Inject
    private GroupTable<ScheduleHeader> scheduleHeadersTable;
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private TimesheetService timesheetService;
    @Inject
    private CommonService commonService;
    @Inject
    private TimecardService timecardService;

    @Override
    public void ready() {
        super.ready();

        for (int i = 1; i <= 31; i++) {
            final int ii = i;
            scheduleHeadersTable.addGeneratedColumn("s" + ii, entity -> {
                if (entity.getSummariesMap().containsKey(ii)) {

                    ScheduleSummary scheduleSummary = entity.getSummariesMap().get(ii);
                    String code = scheduleSummary.getElementType().getCode();
                    if (code.equals("WORK_HOURS")) {
                        Map<String, Object> params = new HashMap<>();
                        if (entity.getSummariesMap() != null) {
                            params.put("selectedScheduleSummary", scheduleSummary);
                        }
                        Double hours = scheduleSummary.getHours();
                        String hoursValue = timecardService.getTimecardRepresentation(hours);
                        LinkButton lb = componentsFactory.createComponent(LinkButton.class);
                        lb.setCaption(hoursValue);
                        if (scheduleSummary.getDisplayValue() != null) {
                            lb.setCaption(scheduleSummary.getDisplayValue());
                        }
                        lb.setAction(new BaseAction("showDetailedDay2") {
                            @Override
                            public void actionPerform(Component component) {
                                openWindow("tsadv$StandardScheduleDayDetail", WindowManager.OpenType.DIALOG,
                                        params
                                );
                            }
                        });
                        return lb;
                    }

                    Label v = componentsFactory.createComponent(Label.class);
                    v.setValue(scheduleSummary.getShiftName());
                    if (code.equals("WEEKEND")) {
                        v.setStyleName("green-day");
                    }
                    if (code.equals("HOLIDAY")) {
                        v.setStyleName("red-day");
                    }
                    v.setHtmlEnabled(false);
                    return v;
                }
                return null;
            });

            scheduleHeadersTable.getColumn("s" + ii).setCaption("" + ii);
            scheduleHeadersTable.getColumn("s" + ii).setWidth(45);
        }

    }

    public Component mothWithYearGenerator(ScheduleHeader entity) {
        DateFormat dateFormat = new SimpleDateFormat("MM.yyyy");
        Label label = componentsFactory.createComponent(Label.class);
        label.setValue(dateFormat.format(entity.getMonth()));
        return label;
    }

}