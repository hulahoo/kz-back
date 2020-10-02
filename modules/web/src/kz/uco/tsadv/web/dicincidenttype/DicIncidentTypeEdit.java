package kz.uco.tsadv.web.dicincidenttype;

import com.haulmont.cuba.gui.components.AbstractEditor;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personprotection.dictionary.DicIncidentType;

public class DicIncidentTypeEdit extends AbstractEditor<DicIncidentType> {
    @Override
    protected void initNewItem(DicIncidentType item) {
        super.initNewItem(item);
        item.setStartDate(CommonUtils.getSystemDate());
        item.setEndDate(CommonUtils.getEndOfTime());
    }
}