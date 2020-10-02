package kz.uco.tsadv.web.modules.personal.timecard.copy;

import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.DatePicker;
import com.haulmont.cuba.gui.components.OptionsGroup;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.timesheet.enums.TimecardHeaderTypeEnum;
import kz.uco.tsadv.modules.timesheet.model.Timecard;
import kz.uco.tsadv.service.BusinessRuleService;
import kz.uco.tsadv.service.TimecardService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TimecardCopyWindow extends AbstractWindow {

    @Inject
    private TimecardService timecardService;

    protected Timecard selectedTimecard;
    protected Date monthToCopy;
    @Inject
    private OptionsGroup hoursOptions;
    @Inject
    private BusinessRuleService businessRuleService;
    @Inject
    private DatePicker<Date> month;
    private List<PersonGroupExt> selectedPersonGroups;

    @Override
    public void init(Map<String, Object> params) {

        super.init(params);
        if (params.containsKey("selectedTimecard")) {
            selectedTimecard = (Timecard) params.get("selectedTimecard");
        }
        if (params.containsKey("personGroups")) {
            selectedPersonGroups = (List<PersonGroupExt>) params.get("personGroups");
        }

        if (params.containsKey("monthToCopy")) {
            monthToCopy = (Date) params.get("monthToCopy");
        }

        List<TimecardHeaderTypeEnum> list = new ArrayList<>();
        list.add(TimecardHeaderTypeEnum.PLAN);
        list.add(TimecardHeaderTypeEnum.FACT);
        hoursOptions.setOptionsList(list);
        hoursOptions.setValue(TimecardHeaderTypeEnum.PLAN);
    }

    public void save() {
        boolean copyDeviationsToo = hoursOptions.getValue().equals(TimecardHeaderTypeEnum.FACT);
        try {
            timecardService.copyTimecard(selectedTimecard, selectedPersonGroups, monthToCopy, month.getValue(), copyDeviationsToo);
        } catch (Exception e) {
            if (e.getMessage().contains("scheduleNotFormed")) {
                String offsetName = e.getMessage().replace("scheduleNotFormed", "");
                showNotification(businessRuleService.getBusinessRuleMessage("scheduleNotFormed") + " " +  offsetName, NotificationType.HUMANIZED);
            }
        }
        close(COMMIT_ACTION_ID);
    }

    public void closeWindow() {
        close(CLOSE_ACTION_ID);
    }

}