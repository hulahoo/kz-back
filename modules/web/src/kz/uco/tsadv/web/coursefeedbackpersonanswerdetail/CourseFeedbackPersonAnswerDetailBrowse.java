package kz.uco.tsadv.web.coursefeedbackpersonanswerdetail;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.learning.enums.feedback.LearningFeedbackQuestionType;
import kz.uco.tsadv.modules.learning.model.feedback.CourseFeedbackPersonAnswerDetail;
import kz.uco.tsadv.modules.learning.model.feedback.LearningFeedbackQuestion;

import javax.inject.Inject;

public class CourseFeedbackPersonAnswerDetailBrowse extends AbstractLookup {

    @Inject
    private ComponentsFactory componentsFactory;

    public Component generateAnswer(CourseFeedbackPersonAnswerDetail detail) {
        LearningFeedbackQuestion question = detail.getQuestion();

        Label label = componentsFactory.createComponent(Label.class);

        if (question.getQuestionType().equals(LearningFeedbackQuestionType.TEXT)) {
            label.setValue(detail.getTextAnswer());
        } else {
            label.setValue(detail.getAnswer().getAnswerLangValue());
        }
        return label;
    }
}