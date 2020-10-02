package kz.uco.tsadv.web.enrollmentfortrainingrequest;

import com.haulmont.cuba.gui.components.AbstractEditor;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;
import kz.uco.tsadv.modules.learning.model.EnrollmentForTrainingRequest;

public class EnrollmentForTrainingRequestEdit extends AbstractEditor<EnrollmentForTrainingRequest> {
    @Override
    protected void initNewItem(EnrollmentForTrainingRequest item) {
        super.initNewItem(item);
        item.setDate(CommonUtils.getSystemDate());
        item.setStatus(EnrollmentStatus.REQUIRED_SETTING);
    }
}