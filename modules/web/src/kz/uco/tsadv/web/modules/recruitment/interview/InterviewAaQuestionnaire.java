package kz.uco.tsadv.web.modules.recruitment.interview;

import com.google.common.collect.Iterators;
import com.haulmont.chile.core.datatypes.Datatype;
import com.haulmont.chile.core.datatypes.Datatypes;
import com.haulmont.chile.core.datatypes.impl.DoubleDatatype;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.validators.DoubleValidator;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.DatasourceImpl;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.recruitment.enums.RcAnswerType;
import kz.uco.tsadv.modules.recruitment.model.Interview;
import kz.uco.tsadv.modules.recruitment.model.InterviewAnswer;
import kz.uco.tsadv.modules.recruitment.model.InterviewQuestion;
import kz.uco.tsadv.modules.recruitment.model.InterviewQuestionnaire;
import kz.uco.tsadv.web.modules.personal.common.Utils;
import org.apache.commons.lang3.BooleanUtils;

import javax.inject.Inject;
import java.text.DecimalFormat;
import java.util.*;

public class InterviewAaQuestionnaire extends AbstractWindow {

    @WindowParam
    private Interview interview;
    @WindowParam
    private PersonExt person;
    @Inject
    private Embedded userImage;
    @Inject
    private VBoxLayout vBox;
    @Inject
    private Table<InterviewQuestionnaire> questionnaireResult;
    @Inject
    private VBoxLayout questionBlock;
    @Inject
    private ScrollBoxLayout scrollBox;
    @Inject
    private Label questionnaireName;
    @Inject
    private LinkButton userFullName;

    @Inject
    private HBoxLayout controlButtons;
    @Inject
    private Datasource<PersonExt> personDs;
    @Inject
    private CollectionDatasource<InterviewQuestionnaire, UUID> questionnairesDs;
    @Inject
    private CollectionDatasource<InterviewQuestion, UUID> questionsDs;
    @Inject
    private CollectionDatasource<InterviewAnswer, UUID> answersDs;
    @Inject
    private VBoxLayout questionBuilder;
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private HBoxLayout startInterviewBlock;
    @Inject
    private Button nextBtn;
    @Inject
    private Button previousBtn;
    @Inject
    private UserSessionSource userSessionSource;

    private int currentPage = 0;
    private boolean isFinishQuestionnaire;
    private boolean isStartedInterview;

    @Override
    public void init(Map<String, Object> params) {
        isFinishQuestionnaire = false;

        personDs.setItem(person);
        if (person != null) {
            userFullName.setCaption(person.getFullName());
            Utils.getPersonImageEmbedded(person, "50px", userImage);
        }

        questionnairesDs.setQuery(String.format(
                "select e from tsadv$InterviewQuestionnaire e " +
                        "where e.interview.id = '%s'", interview.getId()));

        questionnairesDs.addCollectionChangeListener(new CollectionDatasource.CollectionChangeListener<InterviewQuestionnaire, UUID>() {
            @Override
            public void collectionChanged(CollectionDatasource.CollectionChangeEvent<InterviewQuestionnaire, UUID> e) {
                if (e.getOperation().equals(CollectionDatasource.Operation.REFRESH)) {
                    for (InterviewQuestionnaire interviewQuestionnaire : questionnairesDs.getItems()) {
                        calculateTotalScore(interviewQuestionnaire);
                    }
                    ((DatasourceImpl) questionnairesDs).setModified(false);
                }
            }
        });

        questionnairesDs.refresh();

        checkQuestionnaireState();

        super.init(params);
    }

    public void startInterview() {
        if (questionnairesDs.size() > 0) {
            isStartedInterview = true;
            checkQuestionnaireState();
            printQuestion();
        } else {
            showNotification(getMessage("Interview.not.questionnaire"), NotificationType.TRAY);
        }
    }

    private void checkQuestionnaireState() {
        controlButtons.setVisible(!isFinishQuestionnaire && isStartedInterview);
        startInterviewBlock.setVisible(!isStartedInterview && !isFinishQuestionnaire);
        questionnaireResult.setVisible(!isStartedInterview);
        questionBlock.setVisible(!isFinishQuestionnaire && isStartedInterview);
        questionBuilder.removeAll();
        vBox.expand((isFinishQuestionnaire || !isStartedInterview) ? questionnaireResult : scrollBox);
    }

    public Component generateTotalScore(InterviewQuestionnaire interviewQuestionnaire) {
        Label label = componentsFactory.createComponent(Label.class);
        label.setValue(formattingScore(interviewQuestionnaire));
        return label;
    }

    public void calculateTotalScore(InterviewQuestionnaire interviewQuestionnaire) {
        Collection<InterviewQuestion> propertyValues = interviewQuestionnaire.getQuestions();
        Double maxScore = 0d;
        Double score = 0d;

        for (InterviewQuestion interviewQuestion : propertyValues) {
            if (interviewQuestion.getQuestion().getAnswerType() == RcAnswerType.MULTI) {
                maxScore += interviewQuestion.getAnswers().stream().mapToDouble(a -> (a != null && a.getWeight() != null) ? a.getWeight() : 0).sum();
                score += interviewQuestion.getAnswers().stream().filter(answer -> answer != null && BooleanUtils.isTrue(answer.getBooleanAnswer())).mapToDouble(a -> a.getWeight() != null ? a.getWeight() : 0).sum();
            }
            if (interviewQuestion.getQuestion().getAnswerType() == RcAnswerType.SINGLE) {
                maxScore += interviewQuestion.getAnswers().stream().mapToDouble(a -> (a != null && a.getWeight() != null) ? a.getWeight() : 0).max().orElse(0d);
                score += interviewQuestion.getAnswers().stream().filter(answer -> answer != null && BooleanUtils.isTrue(answer.getBooleanAnswer())).mapToDouble(a -> a.getWeight() != null ? a.getWeight() : 0).sum();
            }
        }

        interviewQuestionnaire.setTotalScore(score);
        interviewQuestionnaire.setTotalMaxScore(maxScore);
    }

    private String formattingScore(InterviewQuestionnaire interviewQuestionnaire) {
        Double totalScore = interviewQuestionnaire.getTotalScore();
        Double totalMaxScore = interviewQuestionnaire.getTotalMaxScore();
        StringBuilder result = new StringBuilder();

        if (totalScore != null && totalMaxScore != null) {
            DecimalFormat df = new DecimalFormat("###.##");

            if (totalMaxScore != 0) {
                result.append(totalScore);
                result.append(" (");
                result.append(df.format(totalScore / totalMaxScore * 100));
                result.append("%)");
            }
        }

        return result.toString();
    }

    public void finishInterview() {
        if (getDsContext().isModified()) {
            getDsContext().commit();
        }

        questionnairesDs.refresh();
        isFinishQuestionnaire = true;
        isStartedInterview = false;
        currentPage = 0;
        checkQuestionnaireState();
    }

    private void printQuestion() {
        questionBuilder.removeAll();

        int questionsSize = questionnairesDs.size();

        nextBtn.setEnabled(currentPage < questionsSize - 1);
        previousBtn.setEnabled(currentPage > 0);

        if (currentPage > -1 && currentPage <= questionsSize) {
            InterviewQuestionnaire interviewQuestionnaire = Iterators.get(questionnairesDs.getItems().iterator(), currentPage);

            if (interviewQuestionnaire != null) {
                questionnairesDs.setItem(interviewQuestionnaire);
                printQuestionnaireBlock(interviewQuestionnaire);
            }
        }
    }

    private void printQuestionnaireBlock(InterviewQuestionnaire interviewQuestionnaire) {
        questionnaireName.setValue(interviewQuestionnaire.getQuestionnaire().getName());

        VBoxLayout wrapper = componentsFactory.createComponent(VBoxLayout.class);
        wrapper.setSpacing(true);

        List<InterviewQuestion> interviewQuestions = interviewQuestionnaire.getQuestions();

        if (interviewQuestions != null && !interviewQuestions.isEmpty()) {
            int index = 1;
            for (InterviewQuestion interviewQuestion : interviewQuestions) {
                wrapper.add(generateQuestionBlock(interviewQuestion, index));
                index++;
            }
        }
        questionBuilder.add(wrapper);
    }

    private Component generateQuestionBlock(InterviewQuestion interviewQuestion, int index) {
        VBoxLayout wrapper = componentsFactory.createComponent(VBoxLayout.class);
        wrapper.setStyleName("interview-q-wrap");

        HBoxLayout questionWrapper = componentsFactory.createComponent(HBoxLayout.class);
        questionWrapper.setStyleName("interview-q-item");
        questionWrapper.setWidthFull();

        Label questionLabel = componentsFactory.createComponent(Label.class);
        questionLabel.setWidthFull();
        questionLabel.setValue(interviewQuestion.getQuestion().getQuestionText());
        questionLabel.setStyleName("interview-q-question");

        Label questionNum = componentsFactory.createComponent(Label.class);
        questionNum.setValue(index);
        questionNum.setStyleName("interview-q-num");

        questionWrapper.add(questionLabel);
        questionWrapper.add(questionNum);
        questionWrapper.expand(questionLabel);

        wrapper.add(questionWrapper);
        wrapper.add(generateAnswers(interviewQuestion));
        return wrapper;
    }

    public void previousQuestion() {
        currentPage--;
        printQuestion();
    }

    public void nextQuestion() {
        currentPage++;
        printQuestion();
    }

    private Component generateAnswers(InterviewQuestion interviewQuestion) {
        CssLayout cssLayout = componentsFactory.createComponent(CssLayout.class);
        cssLayout.setStyleName("interview-q-answer");
        cssLayout.setWidthFull();
        switch (interviewQuestion.getQuestion().getAnswerType()) {
            case DATE:
                DateField<Date> dateField = componentsFactory.createComponent(DateField.class);
                dateField.setResolution(DateField.Resolution.DAY);
                if (interviewQuestion.getAnswers() != null && !interviewQuestion.getAnswers().isEmpty())
                    dateField.setValue(interviewQuestion.getAnswers().get(0).getDateAnswer());
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
                TextField<Object> numberField = componentsFactory.createComponent(TextField.class);
                numberField.setDatatype(Datatypes.get(String.valueOf(Datatype.class)));
                numberField.addValidator(new DoubleValidator());

                if (interviewQuestion.getAnswers() != null && !interviewQuestion.getAnswers().isEmpty())
                    numberField.setValue(interviewQuestion.getAnswers().get(0).getNumberAnswer());

                numberField.addValueChangeListener(e -> {
                    try {
                        numberField.validate();
                        Datatype<Double> datatype = Datatypes.get(DoubleDatatype.NAME);
                        Double num = datatype.parse((String) e.getValue(), userSessionSource.getLocale());

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
                RichTextArea richTextArea = componentsFactory.createComponent(RichTextArea.class);
                richTextArea.setWidthFull();
                if (interviewQuestion.getAnswers() != null && !interviewQuestion.getAnswers().isEmpty()) {
                    richTextArea.setValue(interviewQuestion.getAnswers().get(0).getTextAnswer());
                }
                richTextArea.addValueChangeListener(e -> {
                    interviewQuestion.getAnswers().forEach(interviewAnswer -> interviewAnswer.setTextAnswer(e.getValue() == null ? null : (String) e.getValue()));
                    questionsDs.setItem(interviewQuestion);
                    answersDs.getItems().forEach(interviewAnswer -> {
                        interviewAnswer.setTextAnswer(e.getValue() == null ? null : (String) e.getValue());
                        answersDs.modifyItem(interviewAnswer);
                    });
                });

                cssLayout.add(richTextArea);
                break;
            case MULTI:
                HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
                hBoxLayout.setWidthFull();

                Map<String, InterviewAnswer> positiveOptionsMap = new LinkedHashMap<>();
                Map<String, InterviewAnswer> negativeOptionsMap = new LinkedHashMap<>();

                List<InterviewAnswer> values = new ArrayList<>();

                if (interviewQuestion.getAnswers() != null) {
                    interviewQuestion.getAnswers()
                            .stream()
                            .sorted(Comparator.comparingInt(InterviewAnswer::getOrder))
                            .forEach(interviewAnswer -> {
                                Map<String, InterviewAnswer> optionsMap;
                                if (BooleanUtils.isTrue(interviewAnswer.getAnswer().getPositive())) {
                                    optionsMap = positiveOptionsMap;
                                } else {
                                    optionsMap = negativeOptionsMap;
                                }

                                String text = interviewAnswer.getAnswer().getAnswerText();
                                if (interviewAnswer.getWeight() != null) {
                                    text += " (" + interviewAnswer.getWeight() + ")";
                                }

                                optionsMap.put(text, interviewAnswer);

                                if (BooleanUtils.isTrue(interviewAnswer.getBooleanAnswer())) {
                                    values.add(interviewAnswer);
                                }
                            });
                }

                if (!positiveOptionsMap.isEmpty()) {
                    VBoxLayout vBoxLayout = componentsFactory.createComponent(VBoxLayout.class);
                    Label label = componentsFactory.createComponent(Label.class);
                    label.setValue("Позитивные");
                    vBoxLayout.add(label);

                    vBoxLayout.add(createOptionsGroup(interviewQuestion, positiveOptionsMap, values));
                    hBoxLayout.add(vBoxLayout);
                }

                if (!negativeOptionsMap.isEmpty()) {
                    VBoxLayout vBoxLayout = componentsFactory.createComponent(VBoxLayout.class);
                    Label label = componentsFactory.createComponent(Label.class);
                    label.setValue("Негативные");
                    vBoxLayout.add(label);

                    vBoxLayout.add(createOptionsGroup(interviewQuestion, negativeOptionsMap, values));
                    hBoxLayout.add(vBoxLayout);
                }

                cssLayout.add(hBoxLayout);
                break;
            case SINGLE:
                OptionsGroup<String, InterviewAnswer> optionsGroupSingle = componentsFactory.createComponent(OptionsGroup.class);
                optionsGroupSingle.setMultiSelect(false);

                Map<String, InterviewAnswer> singleOptionsMap = new LinkedHashMap<>();
                if (interviewQuestion.getAnswers() != null)
                    interviewQuestion.getAnswers()
                            .stream()
                            .sorted(Comparator.comparingInt(InterviewAnswer::getOrder))
                            .forEach(interviewAnswer -> {
                                String text = interviewAnswer.getAnswer().getAnswerText();
                                if (interviewAnswer.getWeight() != null) {
                                    text += " (" + interviewAnswer.getWeight() + ")";
                                }

                                singleOptionsMap.put(text, interviewAnswer);
                            });

                optionsGroupSingle.setOptionsMap(singleOptionsMap);

                InterviewAnswer singleValue = null;
                if (interviewQuestion.getAnswers() != null && !interviewQuestion.getAnswers().isEmpty())
                    for (InterviewAnswer interviewAnswer : interviewQuestion.getAnswers()) {
                        if (interviewAnswer != null && interviewAnswer.getBooleanAnswer() != null && interviewAnswer.getBooleanAnswer())
                            singleValue = interviewAnswer;
                    }

                optionsGroupSingle.setValue(singleValue.getAnswer().getAnswerText());

                optionsGroupSingle.addValueChangeListener(e -> {
                    InterviewAnswer selectedAnswer = (InterviewAnswer) e.getComponent();
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

                    questionsDs.refresh();
                });

                cssLayout.add(optionsGroupSingle);
                break;
        }

        return cssLayout;
    }

    private Component createOptionsGroup(InterviewQuestion interviewQuestion, Map<String, InterviewAnswer> optionsMap, List<InterviewAnswer> values) {
        OptionsGroup<String, InterviewAnswer> optionsGroup = componentsFactory.createComponent(OptionsGroup.class);
        optionsGroup.setMultiSelect(true);
        optionsGroup.setOptionsMap(optionsMap);
        optionsGroup.setValue(values.get(0).getTextAnswer());

        optionsGroup.addValueChangeListener(e -> {
            Collection<InterviewAnswer> selectedAnswers = (Collection<InterviewAnswer>) e.getComponent();
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
        return optionsGroup;
    }
}