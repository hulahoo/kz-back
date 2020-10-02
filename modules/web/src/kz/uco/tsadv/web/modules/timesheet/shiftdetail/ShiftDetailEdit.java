package kz.uco.tsadv.web.modules.timesheet.shiftdetail;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.timesheet.dictionary.DicScheduleElementType;
import kz.uco.tsadv.modules.timesheet.model.ShiftDetail;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

public class ShiftDetailEdit extends AbstractEditor<ShiftDetail> {

    @Inject
    private FieldGroup fieldGroup;
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private Datasource<ShiftDetail> shiftDetailDs;
    @Named("fieldGroup.elementType")
    private LookupPickerField<Entity> elementTypeField;

    @Override
    protected void initNewItem(ShiftDetail item) {
        super.initNewItem(item);
        item.setDayFrom(1);
        item.setDayTo(1);
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        FieldGroup.FieldConfig dayFrom = fieldGroup.getField("dayFrom");
        FieldGroup.FieldConfig dayTo = fieldGroup.getField("dayTo");
        LookupField<Integer> dayFromLookup = componentsFactory.createComponent(LookupField.class);
        LookupField<Integer> dayToLookup = componentsFactory.createComponent(LookupField.class);
        Map<String, Integer> optionsMap = new HashMap<>();
        optionsMap.put("1", 1);
        optionsMap.put("2", 2);
        dayFromLookup.setOptionsMap(optionsMap);
        dayToLookup.setOptionsMap(optionsMap);
        dayFromLookup.setDatasource(shiftDetailDs, "dayFrom");
        dayToLookup.setDatasource(shiftDetailDs, "dayTo");
        dayFrom.setComponent(dayFromLookup);
        dayTo.setComponent(dayToLookup);
        dayFrom.setRequired(true);
        dayTo.setRequired(true);
        dayFromLookup.addValueChangeListener(e -> {
            if (e.getValue() != null)
                if ((Integer) e.getValue() == 2)
                    getItem().setDayTo(2);
        });
        dayToLookup.addValueChangeListener(e -> {
            if (e.getValue() != null)
                if ((Integer) e.getValue() == 1)
                    getItem().setDayFrom(1);
        });

        elementTypeField.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                DicScheduleElementType elementType = (DicScheduleElementType) e.getValue();
                getItem().setName(elementType.getLangValue());
            }
        });
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        if (getItem().getTimeFrom() != null
                && getItem().getTimeTo() != null
                && getItem().getDayFrom() != null
                && getItem().getDayTo() != null) {
            if (getItem().getDayTo().equals(getItem().getDayFrom()) && getItem().getTimeTo().before(getItem().getTimeFrom()))
                errors.add(messages.getMainMessage("ShiftDetail.time.error"));
        }

        super.postValidate(errors);
    }
}