package kz.uco.tsadv.web.modules.learning.learningfeedbackquestion;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.tsadv.modules.learning.enums.feedback.LearningFeedbackQuestionType;
import kz.uco.tsadv.modules.learning.model.feedback.LearningFeedbackAnswer;
import kz.uco.tsadv.modules.learning.model.feedback.LearningFeedbackQuestion;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class LearningFeedbackQuestionEdit extends AbstractEditor<LearningFeedbackQuestion> {

    @Named("answersTable.create")
    private CreateAction answersTableCreate;
    @Inject
    private CollectionDatasource<LearningFeedbackAnswer, UUID> answersDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        answersTableCreate.setInitialValuesSupplier(() -> Collections.singletonMap("order", answersDs.size() + 1));
    }

    @Override
    protected void initNewItem(LearningFeedbackQuestion item) {
        super.initNewItem(item);
        item.setQuestionType(LearningFeedbackQuestionType.ONE);
    }
}