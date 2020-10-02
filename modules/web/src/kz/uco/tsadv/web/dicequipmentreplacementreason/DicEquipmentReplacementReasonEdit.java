package kz.uco.tsadv.web.dicequipmentreplacementreason;

import com.haulmont.cuba.gui.components.AbstractEditor;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personprotection.dictionary.DicEquipmentReplacementReason;

public class DicEquipmentReplacementReasonEdit extends AbstractEditor<DicEquipmentReplacementReason> {

    @Override
    protected void initNewItem(DicEquipmentReplacementReason item) {
        super.initNewItem(item);
        item.setStartDate(CommonUtils.getSystemDate());
        item.setEndDate(CommonUtils.getEndOfTime());
    }
}