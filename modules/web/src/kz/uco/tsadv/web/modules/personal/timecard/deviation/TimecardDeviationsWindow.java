package kz.uco.tsadv.web.modules.personal.timecard.deviation;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.timesheet.enums.DeviationChangingTypeEnum;
import kz.uco.tsadv.modules.timesheet.enums.TimecardHeaderTypeEnum;
import kz.uco.tsadv.modules.timesheet.model.Timecard;
import kz.uco.tsadv.modules.timesheet.model.TimecardDeviation;
import kz.uco.tsadv.service.TimecardService;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

public class TimecardDeviationsWindow extends AbstractWindow {

    @Inject
    private TimecardService timecardService;
    @Inject
    private DateField<Date> dateFromField;
    @Inject
    private DateField<Date> dateToField;
    @Inject
    private TextField<Double> hoursField;
    @Inject
    private OptionsGroup<Object, Object> typesField;
    @Inject
    private CheckBox changeWeekends;
    @Inject
    private Button delete;
    @Inject
    private GroupDatasource<TimecardDeviation, UUID> timecardDeviationsDs;
    private Set<Timecard> selectedTimecards;
    private boolean fact = true;
    private boolean plan;
    private boolean changeHoursFromBegin;
    @Inject
    private OptionsGroup<Object, Object> changeFromWhichEndField;
    private Date startDate;
    private Date endDate;
    @Inject
    private CommonService commonService;
    @Inject
    private Metadata metadata;
    @Inject
    private DataManager dataManager;


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        delete.setEnabled(false);
        timecardDeviationsDs.addItemChangeListener(e -> {
            if (timecardDeviationsDs.getItem() != null) {
                delete.setEnabled(true);
            } else {
                delete.setEnabled(false);
            }
        });

        if (params.containsKey("selectedTimecards")) {
            selectedTimecards = (Set<Timecard>) params.get("selectedTimecards");
            startDate = (Date) params.get("startDate");
            endDate = (Date) params.get("endDate");
        }
        typesField.setOptionsList(Arrays.asList(TimecardHeaderTypeEnum.FACT, TimecardHeaderTypeEnum.PLAN));
        typesField.setValue(Arrays.asList(TimecardHeaderTypeEnum.FACT));
        typesField.addValueChangeListener(e -> {
            //Какой тип принимают typeField
            Collection<TimecardHeaderTypeEnum> list = (Collection<TimecardHeaderTypeEnum>) e.getValue();
            fact = list.contains(TimecardHeaderTypeEnum.FACT);
            plan = list.contains(TimecardHeaderTypeEnum.PLAN);
        });
        changeWeekends.setValue(true);
        changeFromWhichEndField.setOptionsList(Arrays.asList(DeviationChangingTypeEnum.FROM_BEGIN, DeviationChangingTypeEnum.FROM_END));
        changeFromWhichEndField.setValue(DeviationChangingTypeEnum.FROM_END);
        changeFromWhichEndField.addValueChangeListener((e -> changeHoursFromBegin = e.getValue().equals(DeviationChangingTypeEnum.FROM_BEGIN)));

        hoursField.addValueChangeListener(e -> {
            if ((double) e.getValue() > 0) {
                changeFromWhichEndField.setValue(DeviationChangingTypeEnum.FROM_END);
                changeFromWhichEndField.setEnabled(false);
            } else {
                changeFromWhichEndField.setEnabled(true);
            }
        });
        dateFromField.setRequiredMessage(getMessage("fillField"));
        dateToField.setRequiredMessage(getMessage("fillField"));
        hoursField.setRequiredMessage(getMessage("fillField"));
        List<UUID> assignmentGroupIds = selectedTimecards.stream()
                .map(Timecard::getAssignmentGroupId)
                .collect(Collectors.toList());

        List<AssignmentGroupExt> assignmentGroups = getAssignmentGroupsByIds(assignmentGroupIds);

        Map<String, Object> customMap = new HashMap<>();
        customMap.put("assignmentGroups", assignmentGroups);
        customMap.put("startDate", startDate);
        customMap.put("endDate", endDate);
        timecardDeviationsDs.refresh(customMap);
    }

    private List<AssignmentGroupExt> getAssignmentGroupsByIds(List<UUID> assignmentGroupIds) {
        List<AssignmentGroupExt> assignmentGroupExts = new ArrayList<>();
        for (UUID id : assignmentGroupIds) {
            AssignmentGroupExt assignmentGroupExt = metadata.create(AssignmentGroupExt.class);
            assignmentGroupExt.setId(id);
            assignmentGroupExts.add(assignmentGroupExt);
        }
        return assignmentGroupExts;
    }

    @Override
    public boolean validateAll() {
        return super.validateAll();
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        if (hoursField.getValue() != null
                && Math.abs((Double)hoursField.getValue()) > 35791394) {
            errors.add(getMessage("error.value.interval.exceeded"));
        }
        super.postValidate(errors);
    }

    public void save() {
        if (validateAll()) {
            if (!fact && !plan) {
                showNotification(getMessage("chooseAtLeastOneType"), NotificationType.TRAY);
            } else {
                timecardService.insertDeviations(selectedTimecards, hoursField.getValue(), dateFromField.getValue(),
                        dateToField.getValue(), fact, plan, changeHoursFromBegin, changeWeekends.getValue());
                close(COMMIT_ACTION_ID);
            }

        }
    }

    public void closeWindow() {
        close(CLOSE_ACTION_ID);
    }

    public void delete() {
        TimecardDeviation deviation = timecardDeviationsDs.getItem();
        dataManager.remove(deviation);
        timecardDeviationsDs.refresh();
    }
}