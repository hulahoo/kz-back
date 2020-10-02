package kz.uco.tsadv.web.modules.recruitment.rcquestionnaire;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParams;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.modules.recruitment.dictionary.DicRcQuestionnaireCategory;
import kz.uco.tsadv.modules.recruitment.enums.RcAnswerType;
import kz.uco.tsadv.modules.recruitment.model.*;
import kz.uco.tsadv.web.modules.personal.common.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.stream.Collectors;

public class RcQuestionnaireEdit extends AbstractEditor<RcQuestionnaire> {
    private static final Logger log = LoggerFactory.getLogger(RcQuestionnaireEdit.class);

    private static final String TEST_RESULTS = "TEST_RESULTS";
    private static final String RESULT = "RESULT";

    @Inject
    private Metadata metadata;
    @Inject
    private CollectionDatasource<RcQuestionnaireQuestion, UUID> questionsDs;
    @Inject
    private CollectionDatasource<RcQuestionnaireAnswer, UUID> answersDs;
    @Inject
    private Datasource<RcQuestionnaire> rcQuestionnaireDs;
    @Inject
    private FieldGroup fieldGroup;
    @Named("fieldGroup.category")
    private PickerField categoryField;
    @Named("fieldGroup.passingScore")
    private TextField<Double> passingScoreField;
    @Inject
    private Table<RcQuestionnaireQuestion> questionsTable;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        Utils.customizeLookup(fieldGroup.getField("status").getComponent(), null, WindowManager.OpenType.DIALOG, null);
        Utils.customizeLookup(fieldGroup.getField("category").getComponent(), null, WindowManager.OpenType.DIALOG, null);
    }

    @Override
    public void ready() {
        super.ready();

//        categoryField.addValueChangeListener(new ValueChangeListener() {
//            @Override
//            public void valueChanged(ValueChangeEvent e) {
//                Object value = e.getValue();
//                if (value != null) {
//                    initPassingScore();
//                }
//            }
//        });

        initPassingScore();
    }

    private void initPassingScore() {
        DicRcQuestionnaireCategory category = (DicRcQuestionnaireCategory) categoryField.getValue();
        boolean visible = false;
        if (category != null) {
            if (category.getCode() != null && category.getCode().equalsIgnoreCase("PRE_SCREEN_TEST")) {
                visible = true;
            }
        }

        passingScoreField.setVisible(visible);

        if (!visible) {
            passingScoreField.setValue(null);
        }
    }

    @Override
    public boolean validateAll() {
        passingScoreField.setRequired(false);

        if (super.validateAll()) {
            Collection<RcQuestionnaireQuestion> questions = questionsDs.getItems();
            if (questions != null && !questions.isEmpty()) {
                boolean success = true;
                double sumWeight = 0;
                questionsLoop:
                for (RcQuestionnaireQuestion question : questions) {
                    RcAnswerType rcAnswerType = question.getQuestion().getAnswerType();
                    if (rcAnswerType.equals(RcAnswerType.MULTI) || rcAnswerType.equals(RcAnswerType.SINGLE)) {
                        List<RcQuestionnaireAnswer> answers = question.getAnswers();

                        for (RcQuestionnaireAnswer answer : answers) {
                            if (answer.getWeight() == null) {
                                success = false;
                                break questionsLoop;
                            } else {
                                sumWeight += answer.getWeight();
                            }
                        }
                    }
                }

                String errorMessage = "";
                if (success) {
                    if (sumWeight != 0) {
                        passingScoreField.setRequired(true);
                        Double passingScore = passingScoreField.getValue();

                        if (passingScore == null) {
                            try {
                                passingScoreField.validate();
                            } catch (ValidationException e) {
                                success = false;
                                errorMessage = e.getDetailsMessage();
                            }
                        } else {
                            if (sumWeight < passingScore) {
                                success = false;
                                errorMessage = getMessage("rc.questionnaire.answer.weight.rather.sum");
                            }
                        }
                    } else {
                        passingScoreField.setRequired(false);
                    }
                } else {
                    success = false;
                    errorMessage = getMessage("rc.questionnaire.answer.weight.r");
                }

                if (!success) {
                    showNotification(
                            getMessage("Attention"),
                            errorMessage,
                            NotificationType.TRAY);
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public void addQuestions() {
        Map<String, Object> params = new HashMap<>();
        params.put(WindowParams.MULTI_SELECT.toString(), Boolean.TRUE);
        params.put("excludeRcQuestionIds", questionsDs.getItems() != null ? questionsDs.getItems().stream().map(qq -> qq.getQuestion().getId()).collect(Collectors.toList()) : null);
        if (rcQuestionnaireDs.getItem().getCategory().getCode().equals(TEST_RESULTS)) {
            params.put("questionType", RESULT);
        }
        openLookup("tsadv$RcQuestion.lookup", items -> {
            for (Object item : items) {
                RcQuestion question = (RcQuestion) item;
                RcQuestionnaireQuestion questionnaireQuestion = metadata.create(RcQuestionnaireQuestion.class);
                questionnaireQuestion.setQuestionnaire(getItem());
                questionnaireQuestion.setQuestion(question);
                questionsDs.addItem(questionnaireQuestion);
                questionsDs.setItem(questionnaireQuestion);
                for (RcAnswer answer : question.getAnswers()) {
                    if (answer.getDeleteTs() == null) {
                        RcQuestionnaireAnswer questionnaireAnswer = metadata.create(RcQuestionnaireAnswer.class);
                        questionnaireAnswer.setQuestionnaireQuestion(questionnaireQuestion);
                        questionnaireAnswer.setAnswer(answer);
                        questionnaireAnswer.setOrder(answer.getOrder());
                        questionnaireAnswer.setWeight(1d);
                        answersDs.addItem(questionnaireAnswer);
                    }
                }
                questionsDs.setItem(null);
            }
        }, WindowManager.OpenType.DIALOG, params);
    }

    public void removeQuestions() {

        showOptionDialog(getMessage("removeDialog.confirm.title"), getMessage("removeDialog.confirm.text"),
                MessageType.CONFIRMATION,
                new Action[]{
                        new DialogAction(DialogAction.Type.YES) {
                            @Override
                            public void actionPerform(Component component) {
                                Set<RcQuestionnaireQuestion> selected = questionsTable.getSelected();
                                for (RcQuestionnaireQuestion item : selected) {
                                    questionsDs.setItem(item);
                                    for (RcQuestionnaireAnswer answer : new ArrayList<>(answersDs.getItems()))
                                        answersDs.removeItem(answer);

                                    questionsDs.removeItem(item);
                                }
                            }
                        },
                        new DialogAction(DialogAction.Type.NO)
                });

    }


}