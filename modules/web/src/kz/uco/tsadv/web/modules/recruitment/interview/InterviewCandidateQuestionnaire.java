package kz.uco.tsadv.web.modules.recruitment.interview;

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

public class InterviewCandidateQuestionnaire extends AbstractWindow {

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
    private Button nextQuestionnaire;
    @Inject
    private Button previousQuestionnaire;
    @Inject
    private UserSessionSource userSessionSource;

    private List<InterviewQuestionQuestionnaire> questionsList = new LinkedList<>();
    private Map<Integer, InterviewQuestionnaireOrderPage> questionnairePageMap = new LinkedHashMap<>();

    private int currentPage = 0;
    private int currentQuestionnairePage;

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

        questionnairesDs.refresh();

        questionsList.clear();

        int questionnaireOrder = 1;
        int questionCount = 0;

        for (InterviewQuestionnaire questionnaire : questionnairesDs.getItems()) {
            questionnairePageMap.put(questionnaireOrder, new InterviewQuestionnaireOrderPage(questionnaire, questionCount));

            for (InterviewQuestion question : questionnaire.getQuestions()) {
                questionsList.add(new InterviewQuestionQuestionnaire(question, questionnaire, questionnaireOrder));
                questionCount++;
            }

            questionnaireOrder++;
        }

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
        if (questionsList.size() > 0) {
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
        vBox.expand((isFinishQuestionnaire || !isStartedInterview) ? questionnaireResult : questionBlock);
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
                maxScore += interviewQuestion.getAnswers().stream().mapToDouble(a -> (a != null && a.getWeight() != null) ? a.getWeight() : 0).max().orElseGet(() -> 0d);
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
        currentQuestionnairePage = 0;
        checkQuestionnaireState();
    }

    private void printQuestion() {
        questionBuilder.removeAll();

        int questionnaireSize = questionnairePageMap.size();
        int questionsSize = questionsList.size();

        nextBtn.setEnabled(false);
        previousBtn.setEnabled(false);
        nextQuestionnaire.setEnabled(false);
        previousQuestionnaire.setEnabled(false);

        if (currentPage > -1 && currentPage <= questionsSize) {
            InterviewQuestionQuestionnaire questionQuestionnaire = questionsList.get(currentPage);
            InterviewQuestion interviewQuestion = questionQuestionnaire.getQuestion();

            if (interviewQuestion != null) {
                if (currentQuestionnairePage != questionQuestionnaire.getQuestionnaireOrder()) {
                    questionnairesDs.setItem(questionQuestionnaire.getQuestionnaire());
                }

                currentQuestionnairePage = questionQuestionnaire.getQuestionnaireOrder();

                nextBtn.setEnabled(currentPage < questionsSize - 1);
                previousBtn.setEnabled(currentPage > 0);

                nextQuestionnaire.setEnabled(currentQuestionnairePage < questionnaireSize);
                previousQuestionnaire.setEnabled(currentQuestionnairePage > 1);

                HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
                hBoxLayout.setStyleName("interview-q-item");
                hBoxLayout.setWidthFull();

                Label questionLabel = componentsFactory.createComponent(Label.class);
                questionLabel.setWidthFull();
                questionLabel.setValue(interviewQuestion.getQuestion().getQuestionText());
                questionLabel.setStyleName("interview-q-question");
                hBoxLayout.add(questionLabel);

                Label questionNum = componentsFactory.createComponent(Label.class);
                questionNum.setValue(currentPage + 1);
                questionNum.setStyleName("interview-q-num");
                hBoxLayout.add(questionNum);

                hBoxLayout.expand(questionLabel);

                questionnaireName.setValue(questionQuestionnaire.getQuestionnaire().getQuestionnaire().getName());

                questionBuilder.add(hBoxLayout);

                questionBuilder.add(generateAnswers(interviewQuestion, currentPage));
            }
        }
    }

    public void previousQuestion() {
        currentPage--;
        printQuestion();
    }

    public void nextQuestion() {
        currentPage++;
        printQuestion();
    }

    public void previousQuestionnaire() {

        currentQuestionnairePage--;
        currentPage = questionnairePageMap.get(currentQuestionnairePage).startPage;
        printQuestion();
    }

    public void nextQuestionnaire() {
        currentQuestionnairePage++;
        currentPage = questionnairePageMap.get(currentQuestionnairePage).startPage;
        printQuestion();
    }

    public Component generateAnswers(InterviewQuestion interviewQuestion, int index) {
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
                TextField<Double> numberField = componentsFactory.createComponent(TextField.class);
                numberField.setDatatype(Datatypes.get(Double.class));
                numberField.addValidator(new DoubleValidator());

                if (interviewQuestion.getAnswers() != null && !interviewQuestion.getAnswers().isEmpty())
                    numberField.setValue(interviewQuestion.getAnswers().get(0).getNumberAnswer());

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
                if (interviewQuestion.getAnswers() != null && !interviewQuestion.getAnswers().isEmpty())
                    textArea.setValue(interviewQuestion.getAnswers().get(0).getTextAnswer());
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
                OptionsGroup<String, InterviewAnswer> optionsGroupMulti = componentsFactory.createComponent(OptionsGroup.class);
                optionsGroupMulti.setMultiSelect(true);
                Map<String, InterviewAnswer> multiOptionsMap = new LinkedHashMap<>();
                if (interviewQuestion.getAnswers() != null)
                    interviewQuestion.getAnswers()
                            .stream()
                            .sorted(Comparator.comparingInt(InterviewAnswer::getOrder))
                            .forEach(interviewAnswer -> {
                                multiOptionsMap.put(interviewAnswer.getAnswer().getAnswerText() + " (" + interviewAnswer.getWeight() + ")", interviewAnswer);
                            });

                optionsGroupMulti.setOptionsMap(multiOptionsMap);

                List<InterviewAnswer> multiValues = new ArrayList<>();
                if (interviewQuestion.getAnswers() != null && !interviewQuestion.getAnswers().isEmpty())
                    interviewQuestion.getAnswers().forEach(interviewAnswer -> {
                                if (interviewAnswer != null && interviewAnswer.getBooleanAnswer() != null && interviewAnswer.getBooleanAnswer())
                                    multiValues.add(interviewAnswer);
                            }
                    );
                optionsGroupMulti.setValue(multiValues.get(0).getTextAnswer());

                optionsGroupMulti.addValueChangeListener(e -> {
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

                cssLayout.add(optionsGroupMulti);
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
                                singleOptionsMap.put(interviewAnswer.getAnswer().getAnswerText() + " (" + interviewAnswer.getWeight() + ")", interviewAnswer);
                            });

                optionsGroupSingle.setOptionsMap(singleOptionsMap);

                InterviewAnswer singleValue = null;
                if (interviewQuestion.getAnswers() != null && !interviewQuestion.getAnswers().isEmpty())
                    for (InterviewAnswer interviewAnswer : interviewQuestion.getAnswers()) {
                        if (interviewAnswer != null && interviewAnswer.getBooleanAnswer() != null && interviewAnswer.getBooleanAnswer())
                            singleValue = interviewAnswer;
                    }

                optionsGroupSingle.setValue(singleValue.getTextAnswer());

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

    class InterviewQuestionQuestionnaire {
        private InterviewQuestion question;
        private InterviewQuestionnaire questionnaire;
        private int questionnaireOrder;

        public InterviewQuestionQuestionnaire(InterviewQuestion question, InterviewQuestionnaire questionnaire, int questionnaireOrder) {
            this.question = question;
            this.questionnaire = questionnaire;
            this.questionnaireOrder = questionnaireOrder;
        }

        public int getQuestionnaireOrder() {
            return questionnaireOrder;
        }

        public void setQuestionnaireOrder(int questionnaireOrder) {
            this.questionnaireOrder = questionnaireOrder;
        }

        public InterviewQuestion getQuestion() {
            return question;
        }

        public void setQuestion(InterviewQuestion question) {
            this.question = question;
        }

        public InterviewQuestionnaire getQuestionnaire() {
            return questionnaire;
        }

        public void setQuestionnaire(InterviewQuestionnaire questionnaire) {
            this.questionnaire = questionnaire;
        }
    }

    class InterviewQuestionnaireOrderPage {
        private InterviewQuestionnaire interviewQuestionnaire;
        private int startPage;

        public InterviewQuestionnaireOrderPage(InterviewQuestionnaire interviewQuestionnaire, int startPage) {
            this.interviewQuestionnaire = interviewQuestionnaire;
            this.startPage = startPage;
        }

        public int getStartPage() {
            return startPage;
        }

        public void setStartPage(int startPage) {
            this.startPage = startPage;
        }

        public InterviewQuestionnaire getInterviewQuestionnaire() {
            return interviewQuestionnaire;
        }

        public void setInterviewQuestionnaire(InterviewQuestionnaire interviewQuestionnaire) {
            this.interviewQuestionnaire = interviewQuestionnaire;
        }
    }
}