package kz.uco.tsadv.web.modules.timesheet.standardschedule;

import com.haulmont.cuba.gui.components.AbstractWindow;
import kz.uco.tsadv.modules.timesheet.model.ScheduleSummary;

import java.util.Map;

public class StandardScheduleDayDetail extends AbstractWindow {
    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.containsKey("selectedScheduleSummary")) {
            setCaption(((ScheduleSummary) params.get("selectedScheduleSummary")).getShiftName());
        }
    }
}