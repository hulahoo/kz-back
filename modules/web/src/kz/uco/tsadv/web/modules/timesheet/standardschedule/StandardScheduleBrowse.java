package kz.uco.tsadv.web.modules.timesheet.standardschedule;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.timesheet.model.ScheduleHeader;
import kz.uco.tsadv.modules.timesheet.model.StandardSchedule;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.service.TimesheetService;
import org.apache.commons.lang3.time.DateUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Calendar;
import java.util.*;
import java.util.stream.Collectors;

public class StandardScheduleBrowse extends AbstractLookup {

    @Inject
    private GroupTable<StandardSchedule> schedulesTable;
    @Inject
    private GroupTable<ScheduleHeader> scheduleHeadersTable;
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private DateField<Date> monthFrom;
    @Inject
    private DateField<Date> monthTo;
    @Named("schedulesTable.formSchedule")
    private Action schedulesTableFormSchedule;
    @Named("schedulesTable.showSchedule")
    private Action schedulesTableShowSchedule;
    @Inject
    private GroupDatasource<StandardSchedule, UUID> schedulesDs;
    @Inject
    private TimesheetService timesheetService;
    @Inject
    private CommonService commonService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        schedulesTableFormSchedule.setEnabled(false);
        schedulesTableShowSchedule.setEnabled(false);
        schedulesDs.addItemChangeListener(e -> {
            schedulesTableFormSchedule.setEnabled(!schedulesTable.getSelected().isEmpty());
            schedulesTableShowSchedule.setEnabled(!schedulesTable.getSelected().isEmpty());
        });

        Date currMonth = DateUtils.truncate(new Date(), Calendar.MONTH);

        monthFrom.setValue(currMonth);
        monthTo.setValue(currMonth);
    }

    public void formSchedule() {
        if (monthTo.getValue().before(monthFrom.getValue())) {
            showNotification(messages.getMainMessage("FormSchedule.month.period.error"), NotificationType.TRAY);
            return;
        }
        if (!isDatesOk(schedulesDs.getItem())) {
            showNotification(messages.getMainMessage("AssignmentSchedule.offset.dates.fields.error"), NotificationType.TRAY);
            return;
        }
        List<ScheduleHeader> lockedSchedules = commonService.getEntities(ScheduleHeader.class,
                "select e " +
                        "    from tsadv$ScheduleHeader e " +
                        "   where e.schedule.id in :scheduleIds " +
                        "     and e.month between :monthFrom and :monthTo " +
                        "     and e.isLocked = TRUE " +
                        "     and e.deleteTs is null",
                ParamsMap.of("scheduleIds", schedulesTable.getSelected().stream().map(i -> i.getId()).collect(Collectors.toList()),
                        "monthFrom", monthFrom.getValue(),
                        "monthTo", monthTo.getValue()),
                "scheduleHeader.view");

        if (lockedSchedules != null && !lockedSchedules.isEmpty()) {
            showNotification(getMessage("FormSchedule.locked.schedule.error"), NotificationType.TRAY);
            return;
        }

        for (Date month = monthFrom.getValue(); !month.after(monthTo.getValue()); month = DateUtils.addMonths(month, 1)) {
            for (StandardSchedule standardSchedule : schedulesTable.getSelected()) {
                try {
                    timesheetService.formSchedule(standardSchedule, month);
                } catch (Exception e) {
                    if (e.getMessage() != null && e.getMessage().equals("datesIssue")) {
                        showNotification(getMessage("scheduleDatesIssue"), NotificationType.HUMANIZED);
                    } else if (e.getMessage() != null && e.getMessage().equals("shiftDetailsAutoGeneration.breaksIntersectsWorkHours")) {
                        showNotification(getMessage("shiftDetailsAutoGeneration.breaksIntersectsWorkHours"), NotificationType.HUMANIZED);
                    }
                    else {
                        e.printStackTrace();
                    }
                    return;
                }
            }
        }
        showNotification(getMessage("FormSchedule.success"), NotificationType.TRAY);
    }

    public void showSchedule() {
        if (monthTo.getValue().before(monthFrom.getValue())) {
            showNotification(messages.getMainMessage("FormSchedule.month.period.error"), NotificationType.TRAY);
            return;
        }
        if (isDatesOk(schedulesDs.getItem())) {
            openWindow("tsadv$StandardScheduleDetail",
                    WindowManager.OpenType.NEW_WINDOW,
                    new HashMap<String, Object>() {{
                        put("selectedSchedule", schedulesDs.getItem());
                        put("monthFrom", monthFrom.getValue());
                        put("monthTo", monthTo.getValue());
                    }});
        } else {
            showNotification(messages.getMainMessage("AssignmentSchedule.offset.dates.fields.error"), NotificationType.TRAY);
        }
    }

    private boolean isDatesOk(StandardSchedule schedule) {
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();
        Calendar scheduleStartCalendar = Calendar.getInstance();
        startCalendar.setTime(monthFrom.getValue());
        endCalendar.setTime(monthTo.getValue());
        scheduleStartCalendar.setTime(schedule.getStartDate());

        if (startCalendar.get(Calendar.YEAR) < scheduleStartCalendar.get(Calendar.YEAR) &&
                startCalendar.get(Calendar.MONTH) < scheduleStartCalendar.get(Calendar.MONTH)) {
            return false;
        }

        Date endDate = schedule.getEndDate();
        if (endDate != null) {
            Calendar scheduleEndCalendar = Calendar.getInstance();
            scheduleEndCalendar.setTime(schedule.getEndDate());
            if (endCalendar.get(Calendar.YEAR) > scheduleEndCalendar.get(Calendar.YEAR) &&
                    endCalendar.get(Calendar.MONTH) > scheduleEndCalendar.get(Calendar.MONTH)) {
                return false;
            }
        }
        return true;
    }

}