package kz.uco.tsadv.formatter;


import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.gui.components.Formatter;
import kz.uco.tsadv.modules.timesheet.config.TimecardConfig;
import kz.uco.tsadv.service.DatesService;

public class TimecardRepresentationFormatter implements Formatter<Double> {


    @Override
    public String format(Double hours) {
        if (hours == null) {
            return null;
        }
        Configuration configuration = AppBeans.get(Configuration.class);
        DatesService datesService = AppBeans.get(DatesService.class);
        TimecardConfig timecardConfig = configuration.getConfig(TimecardConfig.class);

        String value;
        if (timecardConfig.getDisplayHoursWithMinutes()) {
            value = datesService.getHoursWithMinutes(hours);
        } else {
            value = datesService.getHoursWithTwoDigitsAfterComma(hours);
        }
        return value;
    }
}
