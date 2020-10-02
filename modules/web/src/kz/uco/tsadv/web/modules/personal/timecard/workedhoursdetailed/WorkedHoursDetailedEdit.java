package kz.uco.tsadv.web.modules.personal.timecard.workedhoursdetailed;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.DateField;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.components.ValidationErrors;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.timesheet.dictionary.DicScheduleElementType;
import kz.uco.tsadv.modules.timesheet.model.WorkedHoursDetailed;
import kz.uco.tsadv.modules.timesheet.model.WorkedHoursSummary;
import kz.uco.tsadv.service.DatesService;
import kz.uco.tsadv.service.TimecardService;
import org.apache.commons.lang3.time.DateUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

//WorkedHoursDetailedEdit has listener
public class WorkedHoursDetailedEdit extends AbstractEditor<WorkedHoursDetailed> {

    @Inject
    private CommonService commonService;
    @Inject
    private DatesService datesService;
    @Inject
    private DataManager dataManager;
    @Inject
    private TimecardService timecardService;

    @Named("fieldGroup.hours")
    private TextField hoursField;
    @Named("fieldGroup.actualTimeOut")
    private DateField<Date> actualTimeOutField;
    @Named("fieldGroup.actualTimeIn")
    private DateField<Date> actualTimeInField;
    private CollectionDatasource<WorkedHoursDetailed, UUID> workedHoursDetailsDs;
    public Date workedDate;
    public Date endRange;
    protected double oldHours;
    protected Consumer consumer;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        consumer = (Consumer) params.get("consumer");
        actualTimeInField.addValueChangeListener(e -> updateHours());
        actualTimeOutField.addValueChangeListener(e -> updateHours());
        if (params.containsKey("workedHoursDetailsDs")) {
            workedHoursDetailsDs = (CollectionDatasource<WorkedHoursDetailed, UUID>) params.get("workedHoursDetailsDs");
        }
        if (params.containsKey("workedDate")) {
            workedDate = (Date) params.get("workedDate");
        }

    }

    @Override
    public void ready() {
        super.ready();
        oldHours = getItem().getHours();
    }

    @Override
    protected void initNewItem(WorkedHoursDetailed item) {
        super.initNewItem(item);
        item.setIsNeedToCheckAndCreateAdditionalHours(true);
        item.setPlanHours(0d);
        DicScheduleElementType workHoursType = commonService.getEntity(DicScheduleElementType.class, "WORK_HOURS");
        item.setScheduleElementType(workHoursType);

        item.setActualTimeIn(workedDate);
        item.setActualTimeOut(workedDate);
    }

    @Override
    protected void postInit() {
        super.postInit();
        endRange = DateUtils.addDays(workedDate, 1);
        endRange = DateUtils.addMilliseconds(endRange, -1);

        actualTimeInField.setRangeStart(workedDate);
        actualTimeInField.setRangeEnd(endRange);

        actualTimeOutField.setRangeStart(workedDate);
        actualTimeOutField.setRangeEnd(DateUtils.addDays(endRange, 1));
    }

    private void updateHours() {
        Date startDate = actualTimeInField.getValue();
        Date endDate = actualTimeOutField.getValue();
        if (startDate != null && endDate != null) {
            double differenceInHours = datesService.calculateDifferenceInHours(endDate, startDate);
            getItem().setHours(differenceInHours);
        }
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        if (actualTimeInField.getValue().after(actualTimeOutField.getValue())) {
            errors.add(messages.formatMainMessage(getMessage("startDate.validatorMsg")));
        }
        if (alreadyExistToTime(getItem())) {
            errors.add(messages.formatMainMessage(getMessage("on.dates.already.exist.validatorMsg")));
        }
    }

    private boolean alreadyExistToTime(WorkedHoursDetailed item) {
        Date startDate = actualTimeInField.getValue();
        Date endDate = actualTimeOutField.getValue();
        if (workedHoursDetailsDs.getItems() != null && workedHoursDetailsDs.getItems().size() > 0) {
            for (WorkedHoursDetailed workedHoursDetailed : workedHoursDetailsDs.getItems()) {
                if (!item.equals(workedHoursDetailed) &&
                        datesService.overlapPeriods(workedHoursDetailed.getActualTimeIn(), workedHoursDetailed.getActualTimeOut(), startDate, endDate)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (committed && oldHours != 0d) {
            WorkedHoursDetailed oldDetail = getOldDetail(getItem());
            if (oldDetail != null) {
                WorkedHoursSummary summary = getItem().getWorkedHoursSummary();
                summary.setHours(summary.getHours() - oldHours + getItem().getHours());
                dataManager.commit(summary);
            }
        }
        if (committed) {
            consumer.accept(null);
        }
        return super.postCommit(committed, close);
    }

    private WorkedHoursDetailed getOldDetail(WorkedHoursDetailed workedHoursDetailed) {
        LoadContext<WorkedHoursDetailed> loadContext = LoadContext.create(WorkedHoursDetailed.class);
        loadContext.setQuery(LoadContext.createQuery(" select e from tsadv$WorkedHoursDetailed e" +
                " where e.id = :entityId and e.scheduleElementType.code=:code")
                .setParameter("entityId", workedHoursDetailed.getId())
                .setParameter("code", "WORK_HOURS"))
                .setView("workedHoursDetailed-view");
        return dataManager.load(loadContext);
    }
}