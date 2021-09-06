package kz.uco.tsadv.web.modules.learning.learningfeedbackanswer;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.components.ValidationException;
import kz.uco.tsadv.modules.learning.model.feedback.LearningFeedbackAnswer;

import javax.inject.Named;

public class LearningFeedbackAnswerEdit extends AbstractEditor<LearningFeedbackAnswer> {
    @Named("fieldGroup.score")
    protected TextField<Integer> scoreField;

    @Override
    public void ready() {
        super.ready();
        scoreField.addValidator(integer -> {
            if (integer < 1 || integer > 5) {
                throw new ValidationException(getMessage("validateScore"));
            }
        });
    }
}