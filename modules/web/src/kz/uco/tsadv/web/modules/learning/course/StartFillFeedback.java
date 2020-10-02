package kz.uco.tsadv.web.modules.learning.course;

import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.MetadataTools;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.validators.DoubleValidator;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.modules.learning.enums.feedback.FeedbackResponsibleRole;
import kz.uco.tsadv.modules.learning.enums.feedback.LearningFeedbackQuestionType;
import kz.uco.tsadv.modules.learning.model.Answer;
import kz.uco.tsadv.modules.learning.model.Enrollment;
import kz.uco.tsadv.modules.learning.model.feedback.*;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import org.apache.commons.lang3.BooleanUtils;

import javax.inject.Inject;
import java.util.*;

public class StartFillFeedback extends AbstractWindow {

    public static final String FEEDBACK_TEMPLATE = "SFF_FEEDBACK_TEMPLATE";
    public static final String ENROLLMENT = "SFF_ENROLLMENT";

    @Inject
    protected Button saveBtn, cancelBtn;
    @Inject
    protected ScrollBoxLayout mainScrollBox;
    @Inject
    protected Label templateName;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected Metadata metadata;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected UserSession userSession;
    protected boolean changed = false;
    protected Map<LearningFeedbackTemplateQuestion, HasValue> templateQuestionMap = new HashMap<>();
    protected Enrollment enrollment;
    protected PersonGroupExt personGroupExt;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        personGroupExt = userSession.getAttribute(StaticVariable.USER_PERSON_GROUP);

        if (personGroupExt != null && params.containsKey(FEEDBACK_TEMPLATE) && params.containsKey(ENROLLMENT)) {
            Enrollment enrollment = (Enrollment) params.get(ENROLLMENT);
            LearningFeedbackTemplate feedbackTemplate = (LearningFeedbackTemplate) params.get(FEEDBACK_TEMPLATE);

            loadPage(feedbackTemplate);

            saveBtn.setAction(new BaseAction("save-feedback") {
                @Override
                public void actionPerform(Component component) {
                    try {
                        saveFeedback(enrollment, feedbackTemplate);
                    } catch (Exception ex) {
                        showNotification(ex.getMessage(), NotificationType.TRAY);
                    }
                }
            });

            saveBtn.setVisible(true);
        }

        cancelBtn.setAction(new BaseAction("cancel") {
            @Override
            public void actionPerform(Component component) {
                if (changed) {
                    showOptionDialog(
                            getMessage("start.fill.feedback.confirm.t"),
                            getMessage("start.fill.feedback.close.confirm.b"),
                            MessageType.CONFIRMATION,
                            new Action[]{
                                    new DialogAction(DialogAction.Type.YES) {
                                        @Override
                                        public void actionPerform(Component component) {
                                            close("cancel", true);
                                        }
                                    },
                                    new DialogAction(DialogAction.Type.NO)
                            }
                    );
                } else {
                    close("cancel", true);
                }
            }
        });
    }

    protected void saveFeedback(Enrollment enrollment, LearningFeedbackTemplate feedbackTemplate) {
        if (validateAll() && !templateQuestionMap.isEmpty()) {
            CommitContext commitContext = new CommitContext();

            CourseFeedbackPersonAnswer personAnswer = metadata.create(CourseFeedbackPersonAnswer.class);
            personAnswer.setPersonGroup(personGroupExt);
            personAnswer.setCompleteDate(new Date());
            personAnswer.setCourse(enrollment.getCourse());
            personAnswer.setFeedbackTemplate(feedbackTemplate);
            personAnswer.setResponsibleRole(FeedbackResponsibleRole.LEARNER);
            personAnswer.setSumScore(0L);
            personAnswer.setAvgScore(0D);

            List<CourseFeedbackPersonAnswerDetail> details = new ArrayList<>();

            long totalSumScore = 0, questionCount = 0;

            for (Map.Entry<LearningFeedbackTemplateQuestion, HasValue> entry : templateQuestionMap.entrySet()) {
                CourseFeedbackPersonAnswerDetail detail = metadata.create(CourseFeedbackPersonAnswerDetail.class);
                detail.setCourse(personAnswer.getCourse());
                detail.setCourseFeedbackPersonAnswer(personAnswer);
                detail.setFeedbackTemplate(personAnswer.getFeedbackTemplate());
                detail.setPersonGroup(personGroupExt);
                detail.setQuestion(entry.getKey().getFeedbackQuestion());
                detail.setQuestionOrder(entry.getKey().getOrder());
                detail.setScore(0);

                HasValue component = entry.getValue();
                if (component != null) {
                    questionCount++;
                    if (component instanceof OptionsGroup) {
                        if(component.getValue() instanceof Collection){
                            Collection<LearningFeedbackAnswer> answers = (Collection<LearningFeedbackAnswer>) component.getValue();
                            String personAnswers = "";
                            boolean check = true;
                            int answerScore = 0;
                            for (LearningFeedbackAnswer answer : answers){
                                answerScore += answer.getScore();
                                personAnswers += answer.getAnswerLangValue() + "\n";
                                if (answer.getScore() == 0){
                                    check = false;
                                }
                            }
                            if (!check){
                                answerScore = 0;
                            }
                            detail.setScore(answerScore);
                            totalSumScore += answerScore;
                            detail.setTextAnswer(personAnswers);
                        } else {
                            LearningFeedbackAnswer feedbackAnswer = (LearningFeedbackAnswer) component.getValue();
                            int answerScore = feedbackAnswer.getScore();
                            detail.setScore(answerScore);
                            detail.setAnswer(feedbackAnswer);
                            totalSumScore += feedbackAnswer.getScore();
                        }
                    } else {
                        LearningFeedbackQuestion question = entry.getKey().getFeedbackQuestion();
                        if (question.getQuestionType().equals(LearningFeedbackQuestionType.NUM)){
                            int numAnswer = Integer.parseInt((String) component.getValue());
                            int answerScore = 0;
                            List<LearningFeedbackAnswer> answerList = question.getAnswers();
                            for (LearningFeedbackAnswer a : answerList) {
                                int check = Integer.parseInt(a.getAnswerLangValue());
                                if (check == numAnswer){
                                    answerScore += a.getScore();
                                    totalSumScore += a.getScore();
                                    break;
                                }
                            }
                            detail.setScore(answerScore);
                            detail.setTextAnswer((String) component.getValue());
                        }else if (question.getQuestionType().equals(LearningFeedbackQuestionType.TEXT)){
                            //Check QuestionType TEXT answer
                            detail.setTextAnswer((String) component.getValue());
                        }
                    }
                }
                details.add(detail);
            }
            personAnswer.setSumScore(totalSumScore);
            if (questionCount != 0) {
                double avgScore = (double) totalSumScore / (double) questionCount;
                personAnswer.setAvgScore(avgScore);
            }
            commitContext.addInstanceToCommit(personAnswer);
            details.forEach(commitContext::addInstanceToCommit);

            dataManager.commit(commitContext);
            close("save", true);
        }
    }

    protected void loadPage(LearningFeedbackTemplate feedbackTemplate) {
        feedbackTemplate = dataManager.reload(feedbackTemplate, "learningFeedbackTemplate.for.course");

        templateQuestionMap.clear();

        templateName.setValue(feedbackTemplate.getName());

        List<LearningFeedbackTemplateQuestion> templateQuestions = feedbackTemplate.getTemplateQuestions();
        if (templateQuestions != null && !templateQuestions.isEmpty()) {
            templateQuestions.sort(Comparator.comparing(LearningFeedbackTemplateQuestion::getOrder));

            for (LearningFeedbackTemplateQuestion templateQuestion : templateQuestions) {
                mainScrollBox.add(createQuestion(templateQuestion));
            }
        } else {
            showNotification("Questions is empty!");
        }
    }

    protected Component createQuestion(LearningFeedbackTemplateQuestion templateQuestion) {
        LearningFeedbackQuestion question = templateQuestion.getFeedbackQuestion();
        int questionOrder = templateQuestion.getOrder();

        VBoxLayout wrapper = componentsFactory.createComponent(VBoxLayout.class);
        wrapper.setWidthFull();
        wrapper.setStyleName("start-fill-feedback-item");

        HBoxLayout questionWrapper = componentsFactory.createComponent(HBoxLayout.class);
        questionWrapper.setAlignment(Alignment.MIDDLE_LEFT);
        questionWrapper.setSpacing(true);
        questionWrapper.setWidthFull();
        questionWrapper.setStyleName("start-fill-feedback-qw");
        questionWrapper.add(label(String.valueOf(questionOrder), "start-fill-feedback-qo"));

        Label questionTextLabel = label(question.getQuestionLangValue(), "start-fill-feedback-qt");
        questionTextLabel.setAlignment(Alignment.MIDDLE_LEFT);
        questionWrapper.add(questionTextLabel);
        questionWrapper.expand(questionTextLabel);
        wrapper.add(questionWrapper);

        CssLayout answerWrapper = componentsFactory.createComponent(CssLayout.class);
        answerWrapper.setStyleName("start-fill-feedback-aw");
        answerWrapper.setWidthFull();
        String requiredMessage = String.format(getMessage("start.fill.feedback.required.answer"), questionOrder);

        if (question.getQuestionType().equals(LearningFeedbackQuestionType.ONE)) {
            List<LearningFeedbackAnswer> answers = question.getAnswers();
            if (answers != null && !answers.isEmpty()) {
                OptionsGroup optionsGroup = componentsFactory.createComponent(OptionsGroup.class);
                optionsGroup.setRequired(true);
                optionsGroup.setRequiredMessage(requiredMessage);

                answers.sort(Comparator.comparing(LearningFeedbackAnswer::getOrder));
                optionsGroup.setOptionsMap(answers
                        .stream()
                        .collect(
                                LinkedHashMap::new,             // Supplier
                                (map, item) -> map.put(item.getAnswerLangValue(), item),     // Accumulator
                                Map::putAll                     // Combiner
                        ));
                optionsGroup.setMultiSelect(false);
                optionsGroup.addValueChangeListener(e -> changed = true);
                answerWrapper.add(optionsGroup);
                templateQuestionMap.put(templateQuestion, optionsGroup);
            }

        }else if(question.getQuestionType().equals(LearningFeedbackQuestionType.MANY)) {
            List<LearningFeedbackAnswer> answers = question.getAnswers();
            if (answers != null && !answers.isEmpty()) {
                OptionsGroup optionsGroup = componentsFactory.createComponent(OptionsGroup.class);
                optionsGroup.setRequired(true);
                optionsGroup.setRequiredMessage(requiredMessage);

                answers.sort(Comparator.comparing(LearningFeedbackAnswer::getOrder));

                optionsGroup.setOptionsMap(answers
                        .stream()
                        .collect(
                                LinkedHashMap::new,             // Supplier
                                (map, item) -> map.put(item.getAnswerLangValue(), item),     // Accumulator
                                Map::putAll                     // Combiner
                        ));
                optionsGroup.setMultiSelect(true);
                optionsGroup.addValueChangeListener(e -> changed = true);
                answerWrapper.add(optionsGroup);
                templateQuestionMap.put(templateQuestion, optionsGroup);
            }
        }else if(question.getQuestionType().equals(LearningFeedbackQuestionType.NUM)){
            TextField textField = componentsFactory.createComponent(TextField.class);
            textField.addValidator(new DoubleValidator());
            textField.setRequired(true);
            textField.setRequiredMessage(requiredMessage);

            textField.addValidator(e -> changed = true);
            answerWrapper.add(textField);
            templateQuestionMap.put(templateQuestion, textField);
        }else {
            TextArea textArea = componentsFactory.createComponent(TextArea.class);
            textArea.setRequired(true);
            textArea.setRequiredMessage(requiredMessage);
            textArea.setSizeFull();
            textArea.addValueChangeListener(e -> changed = true);
            answerWrapper.add(textArea);
            templateQuestionMap.put(templateQuestion, textArea);
        }
        wrapper.add(answerWrapper);

        return wrapper;
    }

    protected TextArea getTextArea(String requiredMessage) {
        TextArea textArea = componentsFactory.createComponent(TextArea.class);
        textArea.setRequired(true);
        textArea.setRequiredMessage(requiredMessage);
        textArea.setSizeFull();
        textArea.addValueChangeListener(e -> changed = true);
        return textArea;
    }

    protected Label label(String value) {
        Label label = componentsFactory.createComponent(Label.class);
        label.setValue(value);
        return label;
    }

    protected Label label(String value, String cssClass) {
        Label label = label(value);
        label.addStyleName(cssClass);
        return label;
    }
}