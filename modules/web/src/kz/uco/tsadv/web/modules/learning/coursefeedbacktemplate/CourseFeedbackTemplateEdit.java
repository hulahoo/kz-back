package kz.uco.tsadv.web.modules.learning.coursefeedbacktemplate;

import com.haulmont.cuba.gui.components.AbstractEditor;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.learning.model.feedback.CourseFeedbackTemplate;

import java.util.Date;

public class CourseFeedbackTemplateEdit extends AbstractEditor<CourseFeedbackTemplate> {

    @Override
    protected void initNewItem(CourseFeedbackTemplate item) {
        super.initNewItem(item);
        item.setStartDate(new Date());
        item.setEndDate(CommonUtils.getEndOfTime());
    }
}