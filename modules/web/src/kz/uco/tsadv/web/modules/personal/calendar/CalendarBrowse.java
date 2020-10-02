package kz.uco.tsadv.web.modules.personal.calendar;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.timesheet.model.Calendar;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class CalendarBrowse extends AbstractLookup {

    @Inject
    private Metadata metadata;
    @Inject
    private GroupDatasource<Calendar, UUID> calendarsDs;

    @Named("calendarHolidaysTable.create")
    private CreateAction calendarHolidaysTableCreate;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        calendarHolidaysTableCreate.setEnabled(false);
        calendarHolidaysTableCreate.setInitialValuesSupplier(() -> Collections.singletonMap("calendar", calendarsDs.getItem()));

        calendarsDs.addItemChangeListener(e -> {
            calendarHolidaysTableCreate.setEnabled(e.getItem() != null);
        });
    }
}