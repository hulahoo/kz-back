package kz.uco.tsadv.web.modules.learning.learningfeedbacktemplate;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.tsadv.modules.learning.enums.feedback.LearningFeedbackUsageType;
import kz.uco.tsadv.modules.learning.model.feedback.LearningFeedbackTemplate;
import kz.uco.tsadv.modules.learning.model.feedback.LearningFeedbackTemplateQuestion;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class LearningFeedbackTemplateEdit extends AbstractEditor<LearningFeedbackTemplate> {

    @Inject
    protected CollectionDatasource<LearningFeedbackTemplateQuestion, UUID> templateQuestionsDs;
    @Inject
    protected Table<LearningFeedbackTemplateQuestion> templateQuestionsTable;
    @Named("templateQuestionsTable.create")
    protected CreateAction templateQuestionsTableCreate;
    @Named("templateQuestionsTable.edit")
    protected EditAction templateQuestionsTableEdit;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        templateQuestionsTableCreate.setInitialValuesSupplier(() -> Collections.singletonMap("order", templateQuestionsDs.size() + 1));
        templateQuestionsTableCreate.setBeforeActionPerformedHandler(() -> {
            ArrayList<LearningFeedbackTemplateQuestion> questionsListForCreate = new ArrayList<>(templateQuestionsDs.getItems());
            templateQuestionsTableCreate.setWindowParams(ParamsMap.of("questionList", questionsListForCreate));
            return true;
        });
        templateQuestionsTableEdit.setBeforeActionPerformedHandler(() -> {
            ArrayList<LearningFeedbackTemplateQuestion> questionsListForEdit = new ArrayList<>(templateQuestionsDs.getItems());
            questionsListForEdit.removeIf(learningFeedbackTemplateQuestion -> {
                if (learningFeedbackTemplateQuestion.getOrder().equals(templateQuestionsTable.getSingleSelected().getOrder())  &&
                        learningFeedbackTemplateQuestion.getFeedbackQuestion().getId().equals(templateQuestionsTable.getSingleSelected().getFeedbackQuestion().getId())) {
                    return true;
                }
                return false;
            });
            templateQuestionsTableEdit.setWindowParams(ParamsMap.of("questionList", questionsListForEdit));
            return true;
        });

    }

    @Override
    protected void initNewItem(LearningFeedbackTemplate item) {
        super.initNewItem(item);
        item.setEmployee(true);
        item.setUsageType(LearningFeedbackUsageType.COURSE);
        item.setActive(true);
    }
}