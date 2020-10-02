package kz.uco.tsadv.web.uom;

import com.haulmont.cuba.gui.components.AbstractEditor;
import kz.uco.tsadv.entity.tb.dictionary.UOM;
import kz.uco.tsadv.global.common.CommonUtils;

public class UOMEdit extends AbstractEditor<UOM> {

    @Override
    protected void initNewItem(UOM item) {
        super.initNewItem(item);
        item.setStartDate(CommonUtils.getSystemDate());
        item.setEndDate(CommonUtils.getEndOfTime());
    }
}