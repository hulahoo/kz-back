package kz.uco.tsadv.web.modules.personal.timecard.workedhoursdetailed;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.tsadv.modules.timesheet.model.WorkedHoursDetailed;
import kz.uco.tsadv.modules.timesheet.model.WorkedHoursSummary;

import javax.inject.Inject;
import javax.inject.Named;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class WorkedHoursDetailedBrowse extends AbstractWindow {
    @Named("scheduleDetailsTable.create")
    private CreateAction scheduleDetailsTableCreate;
    @Named("scheduleDetailsTable.edit")
    private EditAction scheduleDetailsTableEdit;
    @Inject
    private CollectionDatasource<WorkedHoursDetailed, UUID> workedHoursDetailsDs;
    private WorkedHoursSummary workedHoursSummary;
    protected boolean isChanged;
    protected Consumer consumer;
    @Inject
    private Table<WorkedHoursDetailed> scheduleDetailsTable;

    @Override
    public void init(Map<String, Object> params) {
        consumer = o -> isChanged = true;
        if (params.containsKey("selectedWorkedHoursSummary")) {
            workedHoursSummary = (WorkedHoursSummary) params.get("selectedWorkedHoursSummary");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            setCaption(dateFormat.format(workedHoursSummary.getWorkedDate()));
        }
        scheduleDetailsTableCreate.setInitialValuesSupplier(() -> ParamsMap.of(
                "workedHoursSummary", workedHoursSummary,
                "workedDate", workedHoursSummary.getWorkedDate(),
                "timecardHeader", workedHoursSummary.getTimecardHeader())
        );
        scheduleDetailsTableCreate.setWindowParamsSupplier(() -> ParamsMap.of(
                "workedHoursDetailsDs", workedHoursDetailsDs,
                "timecardHeader", workedHoursSummary.getTimecardHeader(),
                "consumer", consumer,
                "workedDate", workedHoursSummary.getWorkedDate())
        );
        scheduleDetailsTableEdit.setWindowParamsSupplier(() -> ParamsMap.of(
                "workedHoursDetailsDs", workedHoursDetailsDs,
                "consumer", consumer,
                "workedDate", workedHoursSummary.getWorkedDate())
        );


        scheduleDetailsTableCreate.setEditorCloseListener(e -> workedHoursDetailsDs.refresh());
        scheduleDetailsTableEdit.setEditorCloseListener(e -> workedHoursDetailsDs.refresh());
    }

    @Override
    public void ready() {
        super.ready();
        workedHoursDetailsDs.addCollectionChangeListener(e -> isChanged = true);
    }

    @Override
    public boolean close(String actionId) {
        if (isChanged) {
            close(COMMIT_ACTION_ID, true);
        }
        return super.close(actionId);
    }
}