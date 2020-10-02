package kz.uco.tsadv.web.modules.personal.calendarholiday;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.HasValue;
import com.haulmont.cuba.gui.components.ValidationErrors;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.timesheet.enums.DayType;
import kz.uco.tsadv.modules.timesheet.model.CalendarHoliday;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class CalendarHolidayEdit extends AbstractEditor<CalendarHoliday> {

    protected static final Date END_OF_TIME = CommonUtils.getEndOfTime();

    @Inject
    private FieldGroup fieldGroup;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        fieldGroup.getField("transferStartDate").setEditable(false);
        fieldGroup.getField("transferEndDate").setEditable(false);

        ((HasValue) fieldGroup.getField("dayType").getComponent()).addValueChangeListener(e -> {
            fieldGroup.getField("transferStartDate").setEditable(e == DayType.TRANSFER);
            fieldGroup.getField("transferEndDate").setEditable(e == DayType.TRANSFER);
        });
    }

    @Override
    protected void initNewItem(CalendarHoliday item) {
        super.initNewItem(item);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
        cal.set(Calendar.DAY_OF_YEAR, 1);
        item.setActionDateFrom(cal.getTime());
        item.setActionDateTo(END_OF_TIME);
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        if (getItem().getStartDate() != null
                && getItem().getEndDate() != null
                && getItem().getEndDate().before(getItem().getStartDate()))
            errors.add(messages.getMainMessage("CalendarHolidayEdit.date.error"));
        super.postValidate(errors);
    }
}