package kz.uco.tsadv.web.modules.timesheet.standardshift;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.timesheet.model.StandardShift;
import kz.uco.tsadv.web.modules.personal.common.Utils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

public class StandardShiftEdit extends AbstractEditor<StandardShift> {

    @Inject
    private FieldGroup fieldGroup;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private Datasource<StandardShift> standardShiftDs;

    @Named("fieldGroup.shift")
    private LookupPickerField shiftField;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        shiftField.addLookupAction();
        shiftField.addClearAction();
        Utils.customizeLookupPickerField(shiftField, null, WindowManager.OpenType.DIALOG, null);
        FieldGroup.FieldConfig shiftDisplayDayConfig = fieldGroup.getField("shiftDisplayDay");
        LookupField shiftDisplayDayLookup = componentsFactory.createComponent(LookupField.class);
        Map<String, Integer> optionsMap = new HashMap<>();
        optionsMap.put(getMessage("split"), 0);
        optionsMap.put("1", 1);
        optionsMap.put("2", 2);
        shiftDisplayDayLookup.setOptionsMap(optionsMap);
        shiftDisplayDayLookup.setDatasource(standardShiftDs, "shiftDisplayDay");
        shiftDisplayDayConfig.setComponent(shiftDisplayDayLookup);
    }
}