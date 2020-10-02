package kz.uco.tsadv.web.interviewquestionnaire;

import com.haulmont.chile.core.datatypes.Datatype;
import com.haulmont.chile.core.datatypes.Datatypes;
import com.haulmont.chile.core.datatypes.impl.DoubleDatatype;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.recruitment.enums.RcAnswerType;
import kz.uco.tsadv.modules.recruitment.model.*;
import org.apache.commons.lang3.BooleanUtils;

import javax.inject.Inject;
import java.util.*;

public class InterviewQuestionnaireEdit extends AbstractEditor<InterviewQuestionnaire> {

    @Inject
    private DataManager dataManager;

    @Inject
    private CollectionDatasource<InterviewAnswer, UUID> answersDs;
    @Inject
    private Datasource<InterviewQuestionnaire> interviewQuestionnaireDs;
    @Inject
    private CollectionDatasource<InterviewQuestion, UUID> questionsDs;
    @Inject
    private ScrollBoxLayout scrollBox;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private UserSessionSource userSessionSource;

    @Inject
    private Metadata metadata;

    @Inject
    private Label testResultValue;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    protected void postInit() {
        super.postInit();
        fillQuestionnaire();
        renderInterviewResult();
        calculateTestResult();
    }

    private void calculateTestResult() {
        Double passingScore = null;
        RcQuestionnaire rcQuestionnaire = getItem().getQuestionnaire();
        if (rcQuestionnaire != null) {
            passingScore = rcQuestionnaire.getPassingScore();
        }

        Double maxScore = 0d;
        Double totalScore = 0d;

        if (getItem().getQuestions() != null) {
            Collection<InterviewQuestion> propertyValues = getItem().getQuestions();

            for (InterviewQuestion interviewQuestion : propertyValues) {
                RcQuestion rcQuestion = interviewQuestion.getQuestion();
                if (rcQuestion != null) {
                    RcAnswerType rcAnswerType = rcQuestion.getAnswerType();
                    if (rcAnswerType == RcAnswerType.MULTI) {
                        maxScore += interviewQuestion.getAnswers().stream().mapToDouble(a -> (a != null && a.getWeight() != null) ? a.getWeight() : 0).sum();
                        totalScore += interviewQuestion.getAnswers().stream().filter(answer -> answer != null && BooleanUtils.isTrue(answer.getBooleanAnswer())).mapToDouble(a -> a.getWeight() != null ? a.getWeight() : 0).sum();
                    }
                    if (rcAnswerType == RcAnswerType.SINGLE) {
                        maxScore += interviewQuestion.getAnswers().stream().mapToDouble(a -> (a != null && a.getWeight() != null) ? a.getWeight() : 0).max().orElseGet(() -> 0d);
                        totalScore += interviewQuestion.getAnswers().stream().filter(answer -> answer != null && BooleanUtils.isTrue(answer.getBooleanAnswer())).mapToDouble(a -> a.getWeight() != null ? a.getWeight() : 0).sum();
                    }
                }
            }
        }

        testResultValue.setValue(String.format("%s / %s", totalScore, maxScore));

        if (passingScore != null) {
            String cssClass = (totalScore >= passingScore) ? "pre-screen-passed" : "pre-screen-fail";
            testResultValue.setStyleName(cssClass);
        }

    }

    private void fillQuestionnaire() {
        if (questionsDs.getItems() == null || questionsDs.getItems().isEmpty()) {
            RcQuestionnaire rcQuestionnaire = dataManager.reload(getItem().getQuestionnaire(), "rcQuestionnaire.view");
            for (RcQuestionnaireQuestion questionnaireQuestion : rcQuestionnaire.getQuestions()) {
                InterviewQuestion interviewQuestion = metadata.create(InterviewQuestion.class);
                interviewQuestion.setInterviewQuestionnaire(interviewQuestionnaireDs.getItem());
                interviewQuestion.setQuestion(questionnaireQuestion.getQuestion());
                interviewQuestion.setOrder(questionnaireQuestion.getOrder());
                questionsDs.addItem(interviewQuestion);
                questionsDs.setItem(interviewQuestion);
                for (RcQuestionnaireAnswer questionnaireAnswer : questionnaireQuestion.getAnswers()) {
                    InterviewAnswer interviewAnswer = metadata.create(InterviewAnswer.class);
                    interviewAnswer.setInterviewQuestion(interviewQuestion);
                    interviewAnswer.setAnswer(questionnaireAnswer.getAnswer());
                    interviewAnswer.setWeight(questionnaireAnswer.getWeight());
                    interviewAnswer.setOrder(questionnaireAnswer.getAnswer().getOrder());
                    answersDs.addItem(interviewAnswer);
                }

                switch (interviewQuestion.getQuestion().getAnswerType()) {
                    case DATE:
                    case TEXT:
                    case NUMBER:
                        InterviewAnswer interviewAnswer = metadata.create(InterviewAnswer.class);
                        interviewAnswer.setInterviewQuestion(interviewQuestion);
                        answersDs.addItem(interviewAnswer);
                        break;
                }
            }
        }
    }

    private void renderInterviewResult() {
        InterviewQuestionnaire questionnaire = getItem();
        int count = 1;
        if (questionnaire.getQuestions() != null) {
            for (InterviewQuestion interviewQuestion : questionnaire.getQuestions()) {
                String questionText = interviewQuestion.getQuestion().getQuestionText();

                VBoxLayout wrapper = componentsFactory.createComponent(VBoxLayout.class);
                wrapper.setStyleName("int-pre-screen-wrap");
                scrollBox.add(wrapper);

                HBoxLayout header = componentsFactory.createComponent(HBoxLayout.class);
                header.setWidthFull();
                header.setStyleName("int-pre-screen-h");
                wrapper.add(header);
                Label question = label(questionText, "int-pre-screen-h-q");
                question.setAlignment(Alignment.MIDDLE_LEFT);
                header.add(question);
                header.add(label(count + "", "int-pre-screen-h-p"));
                header.expand(question);

                wrapper.add(generateAnswers(interviewQuestion));
                count++;
            }
        }
    }

    private Component generateAnswers(InterviewQuestion interviewQuestion) {
        CssLayout cssLayout = componentsFactory.createComponent(CssLayout.class);
        cssLayout.setStyleName("int-pre-screen-b");
        cssLayout.setWidthFull();

        List<InterviewAnswer> answers = interviewQuestion.getAnswers();

        switch (interviewQuestion.getQuestion().getAnswerType()) {
            case DATE:
                DateField<Date> dateField = componentsFactory.createComponent(DateField.class);
                dateField.setResolution(DateField.Resolution.DAY);

                if (answers != null && !answers.isEmpty()) {
                    dateField.setValue(answers.get(0).getDateAnswer());
                }

                dateField.addValueChangeListener(e -> {
                    interviewQuestion.getAnswers().forEach(interviewAnswer -> interviewAnswer.setDateAnswer(e.getValue() == null ? null : (Date) e.getValue()));
                    questionsDs.setItem(interviewQuestion);
                    answersDs.getItems().forEach(interviewAnswer -> {
                        interviewAnswer.setDateAnswer(e.getValue() == null ? null : (Date) e.getValue());
                        answersDs.modifyItem(interviewAnswer);
                    });
                });

                cssLayout.add(dateField);
                break;
            case NUMBER:
                TextField<Double> numberField = componentsFactory.createComponent(TextField.class);
                numberField.setDatatype(Datatypes.get(DoubleDatatype.NAME));

                if (answers != null && !answers.isEmpty()) {
                    numberField.setValue(answers.get(0).getNumberAnswer());
                }

                numberField.addValueChangeListener(e -> {
                    try {
                        numberField.validate();
                        Datatype<Double> datatype = Datatypes.get(DoubleDatatype.NAME);
                        Double num = datatype.parse(String.valueOf(e.getValue()), userSessionSource.getLocale());

                        interviewQuestion.getAnswers().forEach(interviewAnswer -> interviewAnswer.setNumberAnswer(num));
                        questionsDs.setItem(interviewQuestion);
                        answersDs.getItems().forEach(interviewAnswer -> {
                            interviewAnswer.setNumberAnswer(num);
                            answersDs.modifyItem(interviewAnswer);
                        });
                    } catch (Exception ex) {
                        showNotification(ex.getMessage(), NotificationType.TRAY);
                    }
                });

                cssLayout.add(numberField);
                break;
            case TEXT:
                TextArea<String> textArea = componentsFactory.createComponent(TextArea.class);
                textArea.setRows(4);
                textArea.setWidthFull();

                if (answers != null && !answers.isEmpty()) {
                    textArea.setValue(answers.get(0).getTextAnswer());
                }

                textArea.addValueChangeListener(e -> {
                    interviewQuestion.getAnswers().forEach(interviewAnswer -> interviewAnswer.setTextAnswer(e.getValue() == null ? null : (String) e.getValue()));
                    questionsDs.setItem(interviewQuestion);
                    answersDs.getItems().forEach(interviewAnswer -> {
                        interviewAnswer.setTextAnswer(e.getValue() == null ? null : (String) e.getValue());
                        answersDs.modifyItem(interviewAnswer);
                    });

                });

                cssLayout.add(textArea);
                break;
            case MULTI:
                OptionsGroup<List<InterviewAnswer>, String> optionsGroupMulti = componentsFactory.createComponent(OptionsGroup.class);
                optionsGroupMulti.setMultiSelect(true);
//                Map<String, InterviewAnswer> multiOptionsMap = new LinkedHashMap<>();
//                if (answers != null)
//                    answers
//                            .stream()
//                            .sorted(Comparator.comparingInt(InterviewAnswer::getOrder))
//                            .forEach(interviewAnswer -> {
//                                multiOptionsMap.put(interviewAnswer.getAnswer().getAnswerText() + " (" + interviewAnswer.getWeight() + ")", interviewAnswer);
//                            });
//
//                optionsGroupMulti.setOptionsMap(multiOptionsMap);

                List<InterviewAnswer> multiValues = new ArrayList<>();
                if (answers != null && !answers.isEmpty())
                    answers.forEach(interviewAnswer -> {
                                if (interviewAnswer != null && interviewAnswer.getBooleanAnswer() != null && interviewAnswer.getBooleanAnswer())
                                    multiValues.add(interviewAnswer);
                            }
                    );
                optionsGroupMulti.setValue(multiValues);

                optionsGroupMulti.addValueChangeListener(e -> {
                    Collection<InterviewAnswer> selectedAnswers = (Collection<InterviewAnswer>) e.getValue();
                    questionsDs.setItem(interviewQuestion);
                    interviewQuestion.getAnswers().forEach(
                            interviewAnswer -> {
                                if (selectedAnswers.stream().anyMatch(selectedAnswer -> selectedAnswer.getId().equals(interviewAnswer.getId())))
                                    interviewAnswer.setBooleanAnswer(true);
                                else
                                    interviewAnswer.setBooleanAnswer(false);
                            }
                    );

                    answersDs.getItems().forEach(
                            answerItem -> {
                                if (selectedAnswers.stream().anyMatch(selectedAnswer -> selectedAnswer.getId().equals(answerItem.getId())))
                                    answerItem.setBooleanAnswer(true);
                                else
                                    answerItem.setBooleanAnswer(false);

                                answersDs.modifyItem(answerItem);
                            }
                    );

                    questionsDs.refresh();
                });

                cssLayout.add(optionsGroupMulti);
                break;
            case SINGLE:
                OptionsGroup optionsGroupSingle = componentsFactory.createComponent(OptionsGroup.class);
                optionsGroupSingle.setMultiSelect(false);

                Map<String, InterviewAnswer> singleOptionsMap = new LinkedHashMap<>();
                if (answers != null)
                    answers
                            .stream()
                            .sorted(Comparator.comparingInt(InterviewAnswer::getOrder))
                            .forEach(interviewAnswer -> {
                                singleOptionsMap.put(interviewAnswer.getAnswer().getAnswerText() + " (" + interviewAnswer.getWeight() + ")", interviewAnswer);
                            });

                optionsGroupSingle.setOptionsMap(singleOptionsMap);

                InterviewAnswer singleValue = null;
                if (answers != null && !answers.isEmpty())
                    for (InterviewAnswer interviewAnswer : interviewQuestion.getAnswers()) {
                        if (interviewAnswer != null && interviewAnswer.getBooleanAnswer() != null && interviewAnswer.getBooleanAnswer())
                            singleValue = interviewAnswer;
                    }

                optionsGroupSingle.setValue(singleValue);

                optionsGroupSingle.addValueChangeListener(e -> {
                    InterviewAnswer selectedAnswer = (InterviewAnswer) e;
                    questionsDs.setItem(interviewQuestion);
                    interviewQuestion.getAnswers().forEach(
                            interviewAnswer -> {
                                if (selectedAnswer.getId().equals(interviewAnswer.getId()))
                                    interviewAnswer.setBooleanAnswer(true);
                                else
                                    interviewAnswer.setBooleanAnswer(false);
                            }
                    );

                    answersDs.getItems().forEach(
                            answerItem -> {
                                if (selectedAnswer.getId().equals(answerItem.getId()))
                                    answerItem.setBooleanAnswer(true);
                                else
                                    answerItem.setBooleanAnswer(false);
                                answersDs.modifyItem(answerItem);
                            }
                    );
                });

                cssLayout.add(optionsGroupSingle);
                break;
        }

        return cssLayout;
    }

    private Label label(String value) {
        Label label = componentsFactory.createComponent(Label.class);
        label.setValue(value);
        return label;
    }

    private Label label(String value, String cssClass) {
        Label label = label(value);
        label.setStyleName(cssClass);
        return label;
    }
}