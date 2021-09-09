package kz.uco.tsadv.web.modules.learning.question.v68;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.modules.learning.enums.QuestionType;
import kz.uco.tsadv.modules.learning.model.Answer;
import kz.uco.tsadv.modules.learning.model.Question;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Deprecated
public class QuestionEditV68 extends AbstractEditor<Question> {

    @Inject
    protected CollectionDatasource<Answer, UUID> answersDs;

    @Inject
    protected Datasource<Question> questionDs;

    @Named("fieldGroup.type")
    protected LookupField<Object> typeField;

    @Named("answersTable.create")
    protected CreateAction answersTableCreate;

    @Named("answersTable.edit")
    protected EditAction answersTableEdit;

    protected void initAnswerParams() {
        Map<String, Object> map = ParamsMap.of(StaticVariable.QUESTION_TYPE, typeField.getValue());
        answersTableCreate.setWindowParams(map);
        answersTableEdit.setWindowParams(map);
    }

    @Override
    protected void postInit() {
        super.postInit();

        typeField.addValueChangeListener(e -> changeQuestionType(e.getValue()));
    }

    @Override
    public boolean validateAll() {
        if (super.validateAll()) {
            QuestionType questionType = getItem().getType();
            switch (questionType) {
                case ONE:
                case MANY:
                case NUM: {
                    if (!hasSingleRightAnswer(questionType)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    protected boolean hasSingleRightAnswer(QuestionType questionType) {
        int answerSize = answersDs.size();
        if (answerSize == 0) {
            notify("add.question.answer.empty");
            return false;
        }

        if (questionType.equals(QuestionType.NUM) && answerSize > 1) {
            notify("add.question.answer.single.right");
            return false;
        }

        if ((questionType.equals(QuestionType.ONE) || questionType.equals(QuestionType.MANY)) && answerSize == 1) {
            /**
             * Required minimum two answer
             * */
            notify("add.question.answer.two.answer");
            return false;
        }

        int rightAnswerCount = 0;
        for (Answer answer : answersDs.getItems()) {
            if (answer.getCorrect() != null && answer.getCorrect()) {
                rightAnswerCount++;
            }
        }

        if (rightAnswerCount == 0) {
            notify(questionType.equals(QuestionType.MANY) ?
                    "add.question.answer.min.not.right.2" : "add.question.answer.min.not.right");
            return false;
        } else if (rightAnswerCount > 1) {
            if (questionType.equals(QuestionType.ONE)) {
                /**
                 * Required single correct answer
                 * */
                notify("add.question.answer.not.right");
                return false;
            }
        }

        return true;
    }

    protected void notify(String messageKey) {
        String warningTitle = getMessage("msg.warning.title");
        showNotification(warningTitle, getMessage(messageKey), NotificationType.TRAY);
    }

    protected void changeQuestionType(Object questionTypeObject) {
        answersTableCreate.setEnabled(questionTypeObject != null);

        if (questionTypeObject != null) {
            initAnswerParams();

            switch ((QuestionType) questionTypeObject) {
                case ONE:
                case MANY: {
                    break;
                }
                case NUM:
                case TEXT: {
                    List<Answer> removeAnswers = new ArrayList<>();
                    removeAnswers.addAll(answersDs.getItems());
                    if (!removeAnswers.isEmpty()) {
                        removeAnswers.forEach(answer -> answersDs.removeItem(answer));
                    }
                    break;
                }
            }
        }
    }
}