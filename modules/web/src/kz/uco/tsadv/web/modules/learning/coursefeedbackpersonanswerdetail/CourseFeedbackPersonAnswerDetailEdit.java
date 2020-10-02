package kz.uco.tsadv.web.modules.learning.coursefeedbackpersonanswerdetail;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.cuba.gui.components.ResizableTextArea;
import com.haulmont.cuba.gui.components.TextField;
import kz.uco.tsadv.modules.learning.enums.feedback.LearningFeedbackQuestionType;
import kz.uco.tsadv.modules.learning.model.feedback.CourseFeedbackPersonAnswer;
import kz.uco.tsadv.modules.learning.model.feedback.CourseFeedbackPersonAnswerDetail;
import kz.uco.tsadv.modules.learning.model.feedback.LearningFeedbackAnswer;
import kz.uco.tsadv.modules.learning.model.feedback.LearningFeedbackQuestion;

import javax.inject.Named;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CourseFeedbackPersonAnswerDetailEdit extends AbstractEditor<CourseFeedbackPersonAnswerDetail> {

    @Named("fieldGroup.question")
    private PickerField questionField;
    @Named("fieldGroup.answer")
    private PickerField<Entity> answerField;
    @Named("fieldGroup.textAnswer")
    private ResizableTextArea textAnswerField;
    @Named("fieldGroup.score")
    private TextField scoreField;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        answerField.getLookupAction().setLookupScreenParamsSupplier(new Supplier<Map<String, Object>>() {
            @Override
            public Map<String, Object> get() {
                LearningFeedbackQuestion question = getItem().getQuestion();
                if (question != null) {
                    return Collections.singletonMap("questionId", question.getId());
                }
                return new HashMap<>();
            }
        });

        answerField.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                LearningFeedbackAnswer learningFeedbackAnswer = (LearningFeedbackAnswer) e.getValue();
                if (learningFeedbackAnswer != null) {
                    scoreField.setValue(learningFeedbackAnswer.getScore());
                    scoreField.setEditable(false);
                }
            }

        });
    }

    @Override
    protected void postInit() {
        super.postInit();

        initVisibleAnswers();

//        questionField.addValueChangeListener(new ValueChangeListener() {
//            @Override
//            public void valueChanged(ValueChangeEvent e) {
//                initVisibleAnswers();
//            }
//        });
    }

    private void initVisibleAnswers() {
        LearningFeedbackQuestion question = getItem().getQuestion();
        if (question != null) {
            boolean isText = question.getQuestionType().equals(LearningFeedbackQuestionType.TEXT);
            answerField.setVisible(!isText);
            answerField.setRequired(!isText);

            textAnswerField.setVisible(isText);
            textAnswerField.setRequired(isText);
        } else {
            answerField.setVisible(false);
            answerField.setRequired(false);
            answerField.setValue(null);

            textAnswerField.setVisible(false);
            textAnswerField.setRequired(false);
            textAnswerField.setValue(null);
        }
    }

    @Override
    protected void initNewItem(CourseFeedbackPersonAnswerDetail item) {
        super.initNewItem(item);
        item.setScore(0);

        CourseFeedbackPersonAnswer personAnswer = item.getCourseFeedbackPersonAnswer();
        if (personAnswer != null) {
            item.setFeedbackTemplate(personAnswer.getFeedbackTemplate());
            item.setPersonGroup(personAnswer.getPersonGroup());
            item.setCourse(personAnswer.getCourse());
            item.setCourseSectionSession(personAnswer.getCourseSectionSession());
        }
    }
}