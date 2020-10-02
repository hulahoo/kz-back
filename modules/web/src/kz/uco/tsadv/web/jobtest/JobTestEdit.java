package kz.uco.tsadv.web.jobtest;

import com.haulmont.cuba.gui.components.AbstractEditor;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.learning.model.JobTest;

public class JobTestEdit extends AbstractEditor<JobTest> {
    @Override
    protected void initNewItem(JobTest item) {
        super.initNewItem(item);
        item.setStartDate(CommonUtils.getSystemDate());
        item.setEndDate(CommonUtils.getEndOfTime());
    }
}