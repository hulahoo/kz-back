package kz.uco.tsadv.web.modules.recruitment.rcquestion;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.tsadv.modules.recruitment.enums.RcAnswerType;
import kz.uco.tsadv.modules.recruitment.enums.RcQuestionType;
import kz.uco.tsadv.modules.recruitment.model.RcAnswer;
import kz.uco.tsadv.modules.recruitment.model.RcQuestion;
import kz.uco.tsadv.web.modules.personal.common.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RcQuestionEdit extends AbstractEditor<RcQuestion> {
    private static final Logger log = LoggerFactory.getLogger(RcQuestionEdit.class);

    @Inject
    private FieldGroup fieldGroup;

    @Inject
    private Table<RcAnswer> answersTable;

    @Inject
    private CollectionDatasource<RcAnswer, UUID> answersDs;

    @Inject
    @Named("fieldGroup.questionType")
    private LookupField<Object> questionType;

    @Inject
    @Named("fieldGroup.answerType")
    private LookupField answerType;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        Utils.customizeLookup(fieldGroup.getField("questionCategory").getComponent(), null, WindowManager.OpenType.DIALOG, null);
        Utils.customizeLookup(fieldGroup.getField("questionAccessibility").getComponent(), null, WindowManager.OpenType.DIALOG, null);

        answerType.addValueChangeListener(e -> {
            checkAnswersAccess();
        });

        questionType.addValueChangeListener(e -> {
            checkAnswerType();

            if (e.getPrevValue() != null && e.getValue() != null) {
                clearAnswers();
            }
        });
    }

    @Override
    protected void postInit() {
        super.postInit();

        checkAnswersAccess();
        checkAnswerType();
    }

    private void checkAnswersAccess() {
        if (getItem() == null
                || getItem().getAnswerType() == null
                || !RcAnswerType.SINGLE.equals(getItem().getAnswerType())
                && !RcAnswerType.MULTI.equals(getItem().getAnswerType())) {
            answersTable.getActions().forEach(a -> a.setEnabled(false));

            clearAnswers();
        } else {
            answersTable.getActions().forEach(a -> a.setEnabled(true));
        }
    }

    private void checkAnswerType() {
        if (getItem().getQuestionType() != null && RcQuestionType.SELECTION.equals(getItem().getQuestionType())) {
            answerType.setValue(RcAnswerType.SINGLE);
            answerType.setEditable(false);
        } else {
            answerType.setEditable(true);
        }

        /*if (getItem() == null
                || getItem().getQuestionType() == null
                || RcQuestionType.ASSESSMENT.equals(getItem().getQuestionType())) {
            answerType.setEditable(true);
        } else if (RcQuestionType.SELECTION.equals(getItem().getQuestionType())) {
            answerType.setValue(RcAnswerType.SINGLE);
            answerType.setEditable(false);
        }*/
    }

    private void clearAnswers() {
        List<RcAnswer> answers = new ArrayList<>(answersDs.getItems());
        for (RcAnswer item : answers)
            answersDs.removeItem(item);
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        if ((RcAnswerType.SINGLE.equals(getItem().getAnswerType())
                || RcAnswerType.MULTI.equals(getItem().getAnswerType()))
                && answersDs.getItems().isEmpty())
            errors.add(getMessage("RcQuestionEdit.answers.validatorMsg"));

        super.postValidate(errors);
    }
}