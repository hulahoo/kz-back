package kz.uco.tsadv.web.dicprotectionequipmentcondition;

import com.haulmont.cuba.gui.components.AbstractEditor;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personprotection.dictionary.DicProtectionEquipmentCondition;

public class DicProtectionEquipmentConditionEdit extends AbstractEditor<DicProtectionEquipmentCondition> {

    @Override
    protected void initNewItem(DicProtectionEquipmentCondition item) {
        super.initNewItem(item);
        item.setStartDate(CommonUtils.getSystemDate());
        item.setEndDate(CommonUtils.getEndOfTime());
    }
}