package kz.uco.tsadv.web.modules.timesheet.standardschedule;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.timesheet.enums.ScheduleTypeEnum;
import kz.uco.tsadv.modules.timesheet.model.StandardOffset;
import kz.uco.tsadv.modules.timesheet.model.StandardSchedule;
import kz.uco.tsadv.modules.timesheet.model.StandardShift;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class StandardScheduleEdit extends AbstractEditor<StandardSchedule> {
    @Inject
    private FieldGroup fieldGroup;
    @Named("fieldGroup.period")
    private TextField<Integer> periodField;
    @Inject
    private CollectionDatasource<StandardShift, UUID> standardShiftsDs;
    @Inject
    private CollectionDatasource<StandardOffset, UUID> standardOffsetsDs;
    @Inject
    private Datasource<StandardSchedule> scheduleDs;
    @Inject
    private Metadata metadata;
    @Named("fieldGroup.startDate")
    private DateField<Date> startDateField;
    @Named("fieldGroup.endDate")
    private DateField<Date> endDateField;
    @Named("standardOffsetsTable.create")
    private CreateAction standardOffsetsTableCreate;
    @Named("standardOffsetsTable.edit")
    private EditAction standardOffsetsTableEdit;
    @Inject
    private Table<StandardShift> standardShiftsTable;
    @Named("fieldGroup.scheduleType")
    private LookupField<Object> scheduleTypeField;
    @Named("fieldGroup.isHolidayWorkDay")
    private CheckBox isHolidayWorkDayField;
    private boolean isAfterPostInit;

    @Override
    protected void initNewItem(StandardSchedule item) {
        super.initNewItem(item);

        item.setStartDate(CommonUtils.getSystemDate());
        /*item.setEndDate(CommonUtils.getEndOfTime());*/
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        scheduleDs.addItemPropertyChangeListener(event -> {
            if (event.getProperty().equals("period")) {
                handlePeriodFieldData();
            }
            initStandardOffsetParams();
        });

        FieldGroup.FieldConfig startDateConfig = fieldGroup.getField("startDate");
        startDateField.addValueChangeListener(e ->{
            for (StandardOffset offset : standardOffsetsDs.getItems()) {
                Date date = (Date) ((HasValue.ValueChangeEvent) e).getValue();
                offset.setStartDate(date);
                standardOffsetsDs.modifyItem(offset);
            }
        });
//        ((HasValue) startDateConfig.getComponent()).addValueChangeListener(e -> {
//            for (StandardOffset offset : standardOffsetsDs.getItems()) {
//                offset.setStartDate((Date) e);
//                standardOffsetsDs.modifyItem(offset);
//            }
//        });

        FieldGroup.FieldConfig endDateConfig = fieldGroup.getField("endDate");
        endDateField.addValueChangeListener(e -> {
            if (e != null) {
                Date value = (Date) ((HasValue.ValueChangeEvent) e).getValue();
                for (StandardOffset offset : standardOffsetsDs.getItems()) {
                    if (offset.getEndDate() == null || offset.getEndDate().after(value)) {
                        offset.setEndDate(value);
                        standardOffsetsDs.modifyItem(offset);
                    }
                }
            }
            initStandardOffsetParams();
        });
//        ((HasValue) endDateConfig.getComponent()).addValueChangeListener(e -> {
//            if (e != null) {
//                Date value = (Date) e;
//                for (StandardOffset offset : standardOffsetsDs.getItems()) {
//                    if (offset.getEndDate() == null || offset.getEndDate().after(value)) {
//                        offset.setEndDate(value);
//                        standardOffsetsDs.modifyItem(offset);
//                    }
//                }
//            }
//            initStandardOffsetParams();
//        });

        standardOffsetsTableCreate.setInitialValuesSupplier(() ->
                ParamsMap.of("startDate", getItem().getStartDate(),
                        "endDate", getItem().getEndDate())
        );
        scheduleTypeField.addValueChangeListener(e -> {
            ScheduleTypeEnum scheduleType = (ScheduleTypeEnum) e.getValue();
            if (PersistenceHelper.isNew(getItem())) {
                if (scheduleType != null && (scheduleType.equals(ScheduleTypeEnum.SHIFT_WORK) || scheduleType.equals(ScheduleTypeEnum.WATCH))) {
                    isHolidayWorkDayField.setValue(true);
                } else {
                    isHolidayWorkDayField.setValue(false);
                }
            }
        });
    }

    private void handlePeriodFieldData() {
        if (periodField.getValue() == null) {
            new ArrayList<>(standardShiftsDs.getItems()).forEach(i -> standardShiftsDs.removeItem(i));
        } else if (isAfterPostInit) {
            Integer value = periodField.getValue();
            if (value < standardShiftsDs.getItems().size()) {
                new ArrayList<>(standardShiftsDs.getItems()).stream().filter(i -> i.getNumberInShift() > value).forEach(i -> standardShiftsDs.removeItem(i));
            } else if (value > standardShiftsDs.getItems().size()) {
                int startNumber = standardShiftsDs.getItems().size() + 1;
                for (int i = startNumber; i <= value; i++) {
                    StandardShift standardShift = metadata.create(StandardShift.class);
                    standardShift.setStandardSchedule(getItem());
                    standardShift.setNumberInShift(i);
                    standardShift.setShiftDisplayDay(1);
                    standardShiftsDs.addItem(standardShift);
                }
            }

            for (StandardOffset item : standardOffsetsDs.getItems()) {
                if (item.getOffsetDisplayDays() > value) {
                    item.setOffsetDisplayDays(value);
                    standardOffsetsDs.modifyItem(item);
                }
            }
        }
    }

    @Override
    protected void postInit() {
        if (PersistenceHelper.isNew(getItem())) {
            StandardOffset zeroOffset = metadata.create(StandardOffset.class);
            zeroOffset.setStandardSchedule(getItem());
            zeroOffset.setOffsetDisplayDays(0);
            zeroOffset.setOffsetDisplay("I");
            zeroOffset.setStartDate(getItem().getStartDate());

            standardOffsetsDs.addItem(zeroOffset);
        }
        isAfterPostInit = true;
        super.postInit();
    }

    private void initStandardOffsetParams() {
        Map<String, Object> standardOffsetParams = new HashMap<>();
        standardOffsetParams.put("maxEndDate", getItem().getEndDate());
        standardOffsetParams.put("maxOffsetDisplayDays", getItem().getPeriod());
        standardOffsetsTableCreate.setWindowParams(standardOffsetParams);
        standardOffsetsTableEdit.setWindowParams(standardOffsetParams);
    }

    @Override
    protected void postValidate(ValidationErrors errors) {

        /*if (standardShiftsDs.getItems().stream().filter(i -> i.getShift() == null).count() > 0)
            errors.add(messages.getMainMessage("StandardScheduleEdit.standardShifts.error"));*/

        super.postValidate(errors);
    }
}