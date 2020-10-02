package kz.uco.tsadv.listener;

import com.haulmont.cuba.core.listener.AfterDeleteEntityListener;
import com.haulmont.cuba.core.listener.AfterInsertEntityListener;
import com.haulmont.cuba.core.listener.AfterUpdateEntityListener;
import kz.uco.tsadv.IntegrationException;
import kz.uco.tsadv.config.IntegrationConfig;
import kz.uco.tsadv.modules.timesheet.model.CalendarHoliday;
import kz.uco.tsadv.service.IntegrationService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Connection;

@Component("tsadv_CalendarHolidayListener")
public class CalendarHolidayListener implements AfterDeleteEntityListener<CalendarHoliday>, AfterInsertEntityListener<CalendarHoliday>, AfterUpdateEntityListener<CalendarHoliday> {

    @Inject
    private IntegrationConfig integrationConfig;

    @Inject
    private IntegrationService integrationService;

    @Override
    public void onAfterDelete(CalendarHoliday entity, Connection connection) {
        if (!this.integrationConfig.getIsIntegrationOff()) {
            try {
                this.integrationService.deleteCalendarHoliday(entity, connection);
            } catch (Exception e) {
                if (e.getCause() != null) {
                    throw new IntegrationException(e.getCause().getMessage());
                } else {
                    throw new IntegrationException(e.getMessage());
                }
            }
        }
    }


    @Override
    public void onAfterInsert(CalendarHoliday entity, Connection connection) {
        if (!this.integrationConfig.getIsIntegrationOff()) {
            try {
                this.integrationService.createCalendarHoliday(entity, connection);
            } catch (Exception e) {
                if (e.getCause() != null) {
                    throw new IntegrationException(e.getCause().getMessage());
                } else {
                    throw new IntegrationException(e.getMessage());
                }
            }
        }
    }


    @Override
    public void onAfterUpdate(CalendarHoliday entity, Connection connection) {
        if (!this.integrationConfig.getIsIntegrationOff()) {
            try {
                this.integrationService.updateCalendarHoliday(entity, connection);
            } catch (Exception e) {
                if (e.getCause() != null) {
                    throw new IntegrationException(e.getCause().getMessage());
                } else {
                    throw new IntegrationException(e.getMessage());
                }
            }
        }
    }


}