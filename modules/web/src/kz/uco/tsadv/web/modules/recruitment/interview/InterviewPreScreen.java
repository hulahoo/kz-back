package kz.uco.tsadv.web.modules.recruitment.interview;

import com.haulmont.chile.core.datatypes.Datatype;
import com.haulmont.chile.core.datatypes.Datatypes;
import com.haulmont.chile.core.datatypes.impl.DoubleDatatype;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.modules.recruitment.model.InterviewAnswer;
import kz.uco.tsadv.modules.recruitment.model.InterviewQuestion;
import kz.uco.tsadv.modules.recruitment.model.InterviewQuestionnaire;

import javax.inject.Inject;
import java.util.*;

public class InterviewPreScreen extends AbstractWindow {

    @Inject
    private DataManager dataManager;

    @Inject
    private ScrollBoxLayout scrollBox;

    @Inject
    private ComponentsFactory componentsFactory;

    private InterviewQuestionnaire interviewQuestionnaire = null;

    private boolean readOnly = true;

    private List<Entity> instanceToCommit = new ArrayList<>();
    @Inject
    private UserSessionSource userSessionSource;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (params.containsKey(StaticVariable.INTERVIEW_ID)) {
            UUID interviewId = (UUID) params.get(StaticVariable.INTERVIEW_ID);
            renderInterviewResult(interviewId);
        } else if (params.containsKey("INTERVIEW_QUESTIONNAIRE_ID")) {
            readOnly = false;
            interviewQuestionnaire = (InterviewQuestionnaire) params.get("INTERVIEW_QUESTIONNAIRE_ID");
            renderInterviewResult(interviewQuestionnaire);
        } else if (params.containsKey("CARD_INTERVIEW_QUESTIONNAIRE_ID")) {
            interviewQuestionnaire = (InterviewQuestionnaire) params.get("CARD_INTERVIEW_QUESTIONNAIRE_ID");
            interviewQuestionnaire = dataManager.reload(interviewQuestionnaire, "interviewQuestionnaire.view");
            renderInterviewResult(interviewQuestionnaire);
        }
    }

    private void renderInterviewResult(UUID interviewId) {
        InterviewQuestionnaire questionnaire = loadQuestionnaire(interviewId);
        interviewQuestionnaire = questionnaire;
        renderInterviewResult(questionnaire);
    }

    private void renderInterviewResult(InterviewQuestionnaire questionnaire) {
        if (questionnaire != null) {
            int count = 1;
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
                dateField.setEditable(!readOnly);

                if (answers != null && !answers.isEmpty()) {
                    dateField.setValue(answers.get(0).getDateAnswer());
                }

                dateField.addValueChangeListener(e -> {
                    interviewQuestion.getAnswers().forEach(interviewAnswer -> {
                        interviewAnswer.setDateAnswer(e.getValue() == null ? null : (Date) e.getValue());
                        instanceToCommit.remove(interviewAnswer);
                        instanceToCommit.add(interviewAnswer);
                    });
                });

                cssLayout.add(dateField);
                break;
            case NUMBER:
                TextField<Double> numberField = componentsFactory.createComponent(TextField.class);
                numberField.setDatatype(Datatypes.get(Double.class));
                numberField.setEditable(!readOnly);

                if (answers != null && !answers.isEmpty()) {
                    numberField.setValue(answers.get(0).getNumberAnswer());
                }

                numberField.addValueChangeListener(e -> {
                    try {
                        numberField.validate();
                        Datatype<Double> datatype = Datatypes.get(DoubleDatatype.NAME);
                        Double num = datatype.parse(String.valueOf(e.getValue()), userSessionSource.getLocale());

                        interviewQuestion.getAnswers().forEach(interviewAnswer -> {
                            interviewAnswer.setNumberAnswer(num);
                            instanceToCommit.remove(interviewAnswer);
                            instanceToCommit.add(interviewAnswer);
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
                textArea.setEditable(!readOnly);

                if (answers != null && !answers.isEmpty()) {
                    textArea.setValue(answers.get(0).getTextAnswer());
                }

                textArea.addValueChangeListener(e -> {
                    interviewQuestion.getAnswers().forEach(interviewAnswer -> {
                        interviewAnswer.setTextAnswer(e.getValue() == null ? null : (String) e.getValue());
                        instanceToCommit.remove(interviewAnswer);
                        instanceToCommit.add(interviewAnswer);
                    });

                });

                cssLayout.add(textArea);
                break;
            case MULTI:
                OptionsGroup<String, InterviewAnswer> optionsGroupMulti = componentsFactory.createComponent(OptionsGroup.class);
                optionsGroupMulti.setMultiSelect(true);
                Map<String, InterviewAnswer> multiOptionsMap = new LinkedHashMap<>();
                if (answers != null)
                    answers
                            .stream()
                            .sorted(Comparator.comparingInt(InterviewAnswer::getOrder))
                            .forEach(interviewAnswer -> {
                                multiOptionsMap.put(interviewAnswer.getAnswer().getAnswerText() + " (" + interviewAnswer.getWeight() + ")", interviewAnswer);
                            });

                optionsGroupMulti.setOptionsMap(multiOptionsMap);

                List<InterviewAnswer> multiValues = new ArrayList<>();
                if (answers != null && !answers.isEmpty())
                    answers.forEach(interviewAnswer -> {
                                if (interviewAnswer != null && interviewAnswer.getBooleanAnswer() != null && interviewAnswer.getBooleanAnswer())
                                    multiValues.add(interviewAnswer);
                            }
                    );
                optionsGroupMulti.setValue(multiValues.get(0).getTextAnswer());

                optionsGroupMulti.addValueChangeListener(e -> {
                    Collection<InterviewAnswer> selectedAnswers = (Collection<InterviewAnswer>) e.getComponent();
                    interviewQuestion.getAnswers().forEach(
                            interviewAnswer -> {
                                if (selectedAnswers.stream().anyMatch(selectedAnswer -> selectedAnswer.getId().equals(interviewAnswer.getId())))
                                    interviewAnswer.setBooleanAnswer(true);
                                else
                                    interviewAnswer.setBooleanAnswer(false);
                                instanceToCommit.remove(interviewAnswer);
                                instanceToCommit.add(interviewAnswer);
                            }
                    );
                });

                optionsGroupMulti.setEditable(!readOnly);
                cssLayout.add(optionsGroupMulti);
                break;
            case SINGLE:
                OptionsGroup<String, InterviewAnswer> optionsGroupSingle = componentsFactory.createComponent(OptionsGroup.class);
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

                optionsGroupSingle.setValue(singleValue.getTextAnswer());

                optionsGroupSingle.addValueChangeListener(e -> {
                    InterviewAnswer selectedAnswer = (InterviewAnswer) e.getComponent();
                    interviewQuestion.getAnswers().forEach(
                            interviewAnswer -> {
                                if (selectedAnswer.getId().equals(interviewAnswer.getId()))
                                    interviewAnswer.setBooleanAnswer(true);
                                else
                                    interviewAnswer.setBooleanAnswer(false);
                                instanceToCommit.remove(interviewAnswer);
                                instanceToCommit.add(interviewAnswer);
                            }
                    );
                });

                optionsGroupSingle.setEditable(!readOnly);
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

    private InterviewQuestionnaire loadQuestionnaire(UUID interviewId) {
        LoadContext<InterviewQuestionnaire> loadContext = LoadContext.create(InterviewQuestionnaire.class);
        LoadContext.Query loadContextQuery = LoadContext.createQuery(
                "select e from tsadv$InterviewQuestionnaire e " +
                        "join e.questionnaire rq " +
                        "join rq.category c " +
                        "where c.code = 'PRE_SCREEN_TEST' and e.interview.id = :intId");
        loadContextQuery.setParameter("intId", interviewId);
        loadContext.setQuery(loadContextQuery);
        loadContext.setView("interviewQuestionnaire.answers");
        return dataManager.load(loadContext);
    }

    public void saveAnswers() {
//        list.add();
//        interviewQuestionnaire.getQuestions().get(0).setAnswers();
    }
}