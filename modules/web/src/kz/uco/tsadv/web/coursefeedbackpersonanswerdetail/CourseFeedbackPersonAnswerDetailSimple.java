package kz.uco.tsadv.web.coursefeedbackpersonanswerdetail;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.learning.enums.feedback.LearningFeedbackQuestionType;
import kz.uco.tsadv.modules.learning.model.feedback.CourseFeedbackPersonAnswerDetail;
import kz.uco.tsadv.modules.learning.model.feedback.LearningFeedbackQuestion;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class CourseFeedbackPersonAnswerDetailSimple extends AbstractLookup {

    public static final String COURSE_FEEDBACK_PERSON_ANSWER_ID = "COURSE_FEEDBACK_PERSON_ANSWER_ID";

    @Inject
    private GroupDatasource<CourseFeedbackPersonAnswerDetail, UUID> courseFeedbackPersonAnswerDetailsDs;

    @Inject
    private ComponentsFactory componentsFactory;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.containsKey(COURSE_FEEDBACK_PERSON_ANSWER_ID)) {
            courseFeedbackPersonAnswerDetailsDs.setQuery(String.format(
                    "select e from tsadv$CourseFeedbackPersonAnswerDetail e " +
                            "where e.courseFeedbackPersonAnswer.id = '%s' order by e.questionOrder asc",
                    params.get(COURSE_FEEDBACK_PERSON_ANSWER_ID).toString()));
        }
    }

    public Component generateAnswer(CourseFeedbackPersonAnswerDetail detail) {
        LearningFeedbackQuestion question = detail.getQuestion();

        Label label = componentsFactory.createComponent(Label.class);

        if (question.getQuestionType().equals(LearningFeedbackQuestionType.TEXT)
                || question.getQuestionType().equals(LearningFeedbackQuestionType.NUM)
                || question.getQuestionType().equals(LearningFeedbackQuestionType.MANY))
        {
            label.setValue(detail.getTextAnswer());
        } else {
            label.setValue(detail.getAnswer() != null ?
                    detail.getAnswer().getAnswerLangValue() :
                    null);
        }
        return label;
    }
}