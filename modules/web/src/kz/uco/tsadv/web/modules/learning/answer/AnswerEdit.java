package kz.uco.tsadv.web.modules.learning.answer;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.components.validators.DoubleValidator;
import kz.uco.tsadv.modules.learning.enums.QuestionType;
import kz.uco.tsadv.modules.learning.model.Answer;
import kz.uco.base.common.StaticVariable;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

public class AnswerEdit extends AbstractEditor<Answer> {

    @Named("fieldGroup.answer")
    private TextField answerField;

    @Inject
    private FieldGroup fieldGroup;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (params.containsKey(StaticVariable.QUESTION_TYPE)) {
            QuestionType questionType = (QuestionType) params.get(StaticVariable.QUESTION_TYPE);
            boolean isNum = questionType.equals(QuestionType.NUM);
            if (isNum) {
                answerField.addValidator(new DoubleValidator());
            }

            fieldGroup.getFieldNN("answerTextArea").setVisible(!isNum);
            fieldGroup.getFieldNN("answerTextArea").setRequired(!isNum);

            answerField.setVisible(isNum);
            answerField.setRequired(isNum);
        }
    }
}