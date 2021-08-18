package kz.uco.tsadv.web.modules.learning.coursefeedbacktemplate;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.PickerField;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.learning.model.feedback.CourseFeedbackTemplate;
import kz.uco.tsadv.modules.learning.model.feedback.LearningFeedbackTemplate;

import javax.inject.Named;
import java.util.Date;

public class CourseFeedbackTemplateEdit extends AbstractEditor<CourseFeedbackTemplate> {

    @Named("fieldGroup.feedbackTemplate")
    protected PickerField<LearningFeedbackTemplate> feedbackTemplateField;

    @Override
    public void ready() {
        feedbackTemplateField.getLookupAction().setLookupScreenParamsSupplier(() -> ParamsMap.of("active", true));
    }

    @Override
    protected void initNewItem(CourseFeedbackTemplate item) {
        super.initNewItem(item);
        item.setStartDate(new Date());
        item.setEndDate(CommonUtils.getEndOfTime());
    }
}