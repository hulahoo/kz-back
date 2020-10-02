package kz.uco.tsadv.web.modules.personal.timecard.allowablescheduleforposition;

import com.haulmont.cuba.gui.components.AbstractEditor;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.timesheet.model.AllowableScheduleForPosition;

public class AllowableScheduleForPositionEdit extends AbstractEditor<AllowableScheduleForPosition> {

    @Override
    protected void initNewItem(AllowableScheduleForPosition item) {
        super.initNewItem(item);
        item.setEndDate(CommonUtils.getEndOfTime());
    }
}