package kz.uco.tsadv.web.learningfeedbackanswer;

import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.learning.model.feedback.LearningFeedbackAnswer;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class LearningFeedbackAnswerBrowse extends AbstractLookup {

    @WindowParam
    private UUID questionId;

    @Inject
    private GroupDatasource<LearningFeedbackAnswer, UUID> learningFeedbackAnswersDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (questionId != null) {
            learningFeedbackAnswersDs.setQuery(String.format(
                    "select e from tsadv$LearningFeedbackAnswer e " +
                            "where e.feedbackQuestion.id = '%s'",
                    questionId.toString()));
        }
    }
}