package kz.uco.tsadv.web.modules.learning.learningfeedbacktemplate;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.CheckBox;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.tsadv.config.LearningConfig;
import kz.uco.tsadv.modules.learning.enums.feedback.LearningFeedbackUsageType;
import kz.uco.tsadv.modules.learning.model.feedback.LearningFeedbackTemplate;
import kz.uco.tsadv.modules.learning.model.feedback.LearningFeedbackTemplateQuestion;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class LearningFeedbackTemplateEdit extends AbstractEditor<LearningFeedbackTemplate> {

    @Inject
    protected CollectionDatasource<LearningFeedbackTemplateQuestion, UUID> templateQuestionsDs;
    @Named("fieldGroup.active")
    protected CheckBox activeField;
    @Inject
    protected Table<LearningFeedbackTemplateQuestion> templateQuestionsTable;
    @Named("templateQuestionsTable.create")
    protected CreateAction templateQuestionsTableCreate;
    @Named("templateQuestionsTable.edit")
    protected EditAction templateQuestionsTableEdit;
    @Inject
    protected LearningConfig learningConfig;

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
                return learningFeedbackTemplateQuestion.getOrder().equals(templateQuestionsTable.getSingleSelected().getOrder()) &&
                        learningFeedbackTemplateQuestion.getFeedbackQuestion().getId().equals(templateQuestionsTable.getSingleSelected().getFeedbackQuestion().getId());
            });
            templateQuestionsTableEdit.setWindowParams(ParamsMap.of("questionList", questionsListForEdit));
            return true;
        });

        templateQuestionsDs.addCollectionChangeListener(this::templateQuestionCollectionChanged);
    }

    protected void templateQuestionCollectionChanged(CollectionDatasource.CollectionChangeEvent<LearningFeedbackTemplateQuestion, UUID> event) {
        Collection<LearningFeedbackTemplateQuestion> items = event.getDs().getItems();
        Integer numberOfAnswers = learningConfig.getNumberOfAnswers();
        boolean isHasAllAnswers = items.stream()
                .map(LearningFeedbackTemplateQuestion::getFeedbackQuestion)
                .map(question -> question != null && question.getAnswers() != null ? question.getAnswers().size() : 0)
                .allMatch(countAnswer -> countAnswer.equals(numberOfAnswers));
        activeField.setEditable(isHasAllAnswers);
        if (Boolean.TRUE.equals(getEditedEntity().getActive()) && !isHasAllAnswers) getEditedEntity().setActive(false);
    }

    @Override
    protected void initNewItem(LearningFeedbackTemplate item) {
        super.initNewItem(item);
        item.setEmployee(true);
        item.setUsageType(LearningFeedbackUsageType.COURSE);
        item.setActive(true);
    }
}