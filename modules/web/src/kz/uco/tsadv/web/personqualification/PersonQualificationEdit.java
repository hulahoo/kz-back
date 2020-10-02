package kz.uco.tsadv.web.personqualification;

import com.haulmont.cuba.gui.components.AbstractEditor;
import kz.uco.tsadv.entity.tb.PersonQualification;
import kz.uco.tsadv.global.common.CommonUtils;

public class PersonQualificationEdit extends AbstractEditor<PersonQualification> {
    @Override
    protected void initNewItem(PersonQualification item) {
        super.initNewItem(item);
        item.setStartDate(CommonUtils.getSystemDate());
        item.setEndDate(CommonUtils.getEndOfTime());
    }
}