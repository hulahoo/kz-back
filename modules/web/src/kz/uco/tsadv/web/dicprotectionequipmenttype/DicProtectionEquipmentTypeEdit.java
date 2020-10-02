package kz.uco.tsadv.web.dicprotectionequipmenttype;

import com.haulmont.cuba.gui.components.AbstractEditor;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personprotection.dictionary.DicProtectionEquipmentType;
import org.apache.commons.lang3.time.DateUtils;

public class DicProtectionEquipmentTypeEdit extends AbstractEditor<DicProtectionEquipmentType> {

    @Override
    protected void initNewItem(DicProtectionEquipmentType item) {
        super.initNewItem(item);
        item.setStartDate(CommonUtils.getSystemDate());
        item.setEndDate(CommonUtils.getEndOfTime());
    }
}