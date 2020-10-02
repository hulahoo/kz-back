package kz.uco.tsadv.web.modules.timesheet.scheduleheader;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.DatePicker;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.cuba.gui.components.ValidationErrors;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import kz.uco.tsadv.modules.timesheet.model.StandardSchedule;
import kz.uco.tsadv.service.TimesheetService;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Date;
import java.util.Map;


public class FormSchedule extends AbstractWindow {
    private static final Logger log = LoggerFactory.getLogger(FormSchedule.class);

    @Inject
    private DatePicker<Date> month;
    @Inject
    private PickerField<Entity> standardSchedule;

    @Inject
    private TimesheetService timesheetService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        addAction(new BaseAction("windowClose")
                .withCaption(messages.getMainMessage("actions.Cancel"))
                .withIcon("icons/cancel.png")
                .withHandler((e) -> close(CLOSE_ACTION_ID)));
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        if (!((StandardSchedule) standardSchedule.getValue()).getStartDate().before(DateUtils.addMonths(month.getValue(), 1)))
            errors.add(messages.getMainMessage("FormSchedule.monthStartError"));

        if (((StandardSchedule) standardSchedule.getValue()).getEndDate() != null
                && ((StandardSchedule) standardSchedule.getValue()).getEndDate().before(month.getValue()))
            errors.add(messages.getMainMessage("FormSchedule.monthEndError"));

        super.postValidate(errors);
    }

    public void form() {
        if (validateAll()) {
            try {
                timesheetService.formSchedule((StandardSchedule) standardSchedule.getValue(), month.getValue());
                showNotification(messages.getMainMessage("FormSchedule.success"), NotificationType.TRAY);
            } catch (Exception e) {
                if (e.getMessage() != null && e.getMessage().equals("datesIssue")) {
                    showNotification(getMessage("scheduleDatesIssue"), NotificationType.HUMANIZED);
                } else if (e.getMessage() != null && e.getMessage().equals("shiftDetailsAutoGeneration.breaksIntersectsWorkHours")) {
                    showNotification(getMessage("shiftDetailsAutoGeneration.breaksIntersectsWorkHours"), NotificationType.HUMANIZED);
                }
                else {
                    e.printStackTrace();
                }
            }
            close(COMMIT_ACTION_ID);
        }
    }
}