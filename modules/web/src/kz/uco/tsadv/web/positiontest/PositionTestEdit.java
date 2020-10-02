package kz.uco.tsadv.web.positiontest;

import com.haulmont.cuba.gui.components.AbstractEditor;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.learning.model.PositionTest;

public class PositionTestEdit extends AbstractEditor<PositionTest> {
    @Override
    protected void initNewItem(PositionTest item) {
        super.initNewItem(item);
        item.setStartDate(CommonUtils.getSystemDate());
        item.setEndDate(CommonUtils.getEndOfTime());
    }
}