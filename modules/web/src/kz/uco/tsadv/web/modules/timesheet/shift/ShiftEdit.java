package kz.uco.tsadv.web.modules.timesheet.shift;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.DateField;
import com.haulmont.cuba.gui.components.FieldGroup;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.timesheet.model.Shift;

import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ShiftEdit extends AbstractEditor<Shift> {
    @Inject
    private FieldGroup fieldGroup;

    @Override
    public void postInit() {
        DateField startDate = (DateField) fieldGroup.getField("dateFrom").getComponent();
        if (startDate.getValue() == null)
            startDate.setValue(CommonUtils.getSystemDate());

        DateField endDate = (DateField) fieldGroup.getField("dateTo").getComponent();
        try {
            if (endDate.getValue() == null)
                endDate.setValue(new SimpleDateFormat("dd.MM.yyyy").parse("31.12.9999"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}