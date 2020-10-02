package kz.uco.tsadv.web.modules.timesheet.standardoffset;

import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import kz.uco.tsadv.modules.timesheet.model.StandardOffset;

import javax.inject.Inject;
import java.util.Date;
import java.util.Map;

public class StandardOffsetEdit extends AbstractEditor<StandardOffset> {

    @WindowParam
    private Date maxEndDate;

    @WindowParam
    private Integer maxOffsetDisplayDays;

    @Inject
    private FieldGroup fieldGroup;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (maxEndDate != null) {
            ((DateField) fieldGroup.getField("endDate").getComponent()).setRangeEnd(maxEndDate);
        }
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        if (maxOffsetDisplayDays != null && getItem().getOffsetDisplayDays() > maxOffsetDisplayDays)
            errors.add(String.format(messages.getMainMessage("StandardOffsetEdit.maxOffsetDisplayDays.error"), maxOffsetDisplayDays));

        super.postValidate(errors);
    }
}