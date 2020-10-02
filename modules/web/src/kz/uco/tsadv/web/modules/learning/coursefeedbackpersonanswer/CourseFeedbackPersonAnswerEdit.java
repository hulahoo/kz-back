package kz.uco.tsadv.web.modules.learning.coursefeedbackpersonanswer;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.DsBuilder;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.administration.enums.RuleStatus;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.modules.learning.model.CourseSectionSession;
import kz.uco.tsadv.modules.learning.model.feedback.*;
import kz.uco.tsadv.service.BusinessRuleService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class CourseFeedbackPersonAnswerEdit extends AbstractEditor<CourseFeedbackPersonAnswer> {

    @Named("fieldGroup.feedbackTemplate")
    protected PickerField feedbackTemplateField;
    @Inject
    protected ComponentsFactory componentsFactory;

    @Named("detailsTable.create")
    protected CreateAction detailsTableCreate;
    @Named("detailsTable.edit")
    protected EditAction detailsTableEdit;
    @Named("detailsTable.remove")
    protected RemoveAction detailsTableRemove;
    @Named("fieldGroup.courseSectionSession")
    protected LookupPickerField courseSectionSessionField;
    @Named("fieldGroup.course")
    protected PickerField courseField;
    @Inject
    protected CollectionDatasource<CourseFeedbackPersonAnswerDetail, UUID> detailsDs;
    @Inject
    protected BusinessRuleService businessRuleService;
    @Inject
    protected Metadata metadata;
    @Inject
    protected Datasource<CourseFeedbackPersonAnswer> courseFeedbackPersonAnswerDs;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected Table<CourseFeedbackPersonAnswerDetail> detailsTable;
    @Inject
    protected CollectionDatasource<CourseSectionSession, UUID> courseSectionSessionsDs;
    @Named("fieldGroup.avgScore")
    protected TextField avgScoreField;
    @Named("fieldGroup.sumScore")
    protected TextField sumScoreField;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        detailsTable.sort("questionOrder", Table.SortDirection.DESCENDING);
        courseField.addValueChangeListener(e -> courseSectionSessionsDsRefresh());
        courseSectionSessionField.addLookupAction();
        courseSectionSessionField.getLookupAction().setLookupScreenParamsSupplier(() -> getCourseSectionSessionLookupParams());

        feedbackTemplateField.removeAction("lookup");
        feedbackTemplateField.addAction(new PickerField.LookupAction(feedbackTemplateField) {
            @Override
            public void actionPerform(Component component1) {
                if (!detailsDs.getItems().isEmpty()) {
                    RuleStatus ruleStatus = businessRuleService.getRuleStatus("loadFromTemplate");
                    String message;
                    if (ruleStatus != null && ruleStatus.equals(RuleStatus.WARNING)) {
                        message = businessRuleService.getBusinessRuleMessage("loadFromTemplate");
                    } else {
                        message = getMessage("message");
                    }
                    showOptionDialog(getMessage("title"), message, MessageType.CONFIRMATION,
                            new Action[]{
                                    new DialogAction(DialogAction.Type.YES, Status.PRIMARY) {
                                        public void actionPerform(Component component) {
                                            doSuperActionPerform(component1);
                                        }
                                    },
                                    new DialogAction(DialogAction.Type.NO, Status.NORMAL)
                            });
                } else {
                    super.actionPerform(component1);
                }
            }

            public void doSuperActionPerform(Component component2) {
                super.actionPerform(component2);
            }
        });

        feedbackTemplateField.removeAction("clear");
        feedbackTemplateField.addAction(new PickerField.ClearAction(feedbackTemplateField) {
            @Override
            public void actionPerform(Component component1) {
                if (!detailsDs.getItems().isEmpty()) {
                    RuleStatus ruleStatus = businessRuleService.getRuleStatus("loadFromTemplate");
                    String message;
                    if (ruleStatus != null && ruleStatus.equals(RuleStatus.WARNING)) {
                        message = businessRuleService.getBusinessRuleMessage("loadFromTemplate");
                    } else {
                        message = getMessage("message");
                    }
                    showOptionDialog(getMessage("title"), message, MessageType.CONFIRMATION,
                            new Action[]{
                                    new DialogAction(DialogAction.Type.YES, Status.PRIMARY) {
                                        public void actionPerform(Component component) {
                                            doSuperActionPerform(component1);
                                        }
                                    },
                                    new DialogAction(DialogAction.Type.NO, Status.NORMAL)
                            });
                } else {
                    super.actionPerform(component1);
                }
            }

            public void doSuperActionPerform(Component component2) {
                super.actionPerform(component2);
            }
        });


    }

    protected void feedbackTemplateFieldValueChangeListener(Object e) {
        while (!detailsDs.getItems().isEmpty()) {
            detailsDs.removeItem(new ArrayList<>(detailsDs.getItems()).get(0));
        }
        LearningFeedbackTemplate feedbackTemplate = (LearningFeedbackTemplate) e;
        if (feedbackTemplate != null) {
            feedbackTemplate = dataManager.reload(feedbackTemplate, "learning-feedback-template");
            List<LearningFeedbackTemplateQuestion> templateQuestions = feedbackTemplate.getTemplateQuestions();
            CourseFeedbackPersonAnswer item = courseFeedbackPersonAnswerDs.getItem();

            templateQuestions.sort(Comparator.comparing(LearningFeedbackTemplateQuestion::getOrder));

            for (LearningFeedbackTemplateQuestion templateQuestion : templateQuestions) {
                CourseFeedbackPersonAnswerDetail courseFeedbackPersonAnswerDetail = metadata.create(CourseFeedbackPersonAnswerDetail.class);
                courseFeedbackPersonAnswerDetail.setFeedbackTemplate((LearningFeedbackTemplate) e);
                courseFeedbackPersonAnswerDetail.setCourse(item.getCourse());
                courseFeedbackPersonAnswerDetail.setPersonGroup(item.getPersonGroup());
                courseFeedbackPersonAnswerDetail.setCourseSectionSession(item.getCourseSectionSession());
                courseFeedbackPersonAnswerDetail.setQuestion(templateQuestion.getFeedbackQuestion());
                courseFeedbackPersonAnswerDetail.setCourseFeedbackPersonAnswer(courseFeedbackPersonAnswerDs.getItem());
                courseFeedbackPersonAnswerDetail.setAnswer(templateQuestion.getFeedbackQuestion() != null
                        ? templateQuestion.getFeedbackQuestion().getAnswers() != null
                        ? !templateQuestion.getFeedbackQuestion().getAnswers().isEmpty()
                        ? templateQuestion.getFeedbackQuestion().getAnswers().get(0) : null : null : null);
                courseFeedbackPersonAnswerDetail.setScore(templateQuestion.getFeedbackQuestion() != null
                        ? templateQuestion.getFeedbackQuestion().getAnswers() != null
                        ? !templateQuestion.getFeedbackQuestion().getAnswers().isEmpty()
                        ? templateQuestion.getFeedbackQuestion().getAnswers().get(0) != null
                        ? templateQuestion.getFeedbackQuestion().getAnswers().get(0).getScore() : null : null : null : null);
                courseFeedbackPersonAnswerDetail.setQuestionOrder(templateQuestion.getOrder());
                detailsDs.addItem(courseFeedbackPersonAnswerDetail);
            }
        }
    }

    @Override
    public void ready() {
        super.ready();
        feedbackTemplateField.addValueChangeListener(this::feedbackTemplateFieldValueChangeListener);
    }

    @Override
    protected boolean preCommit() {
        Collection<CourseFeedbackPersonAnswerDetail> items = detailsDs.getItems();
        CourseFeedbackPersonAnswer courseFeedbackPersonAnswer = courseFeedbackPersonAnswerDs.getItem();
        long sumScore = 0;
        int questionCount = 0;
        for (CourseFeedbackPersonAnswerDetail item : items) {
            if (item.getFeedbackTemplate() == null) {
                item.setFeedbackTemplate(courseFeedbackPersonAnswer.getFeedbackTemplate());
            }
            if (item.getCourse() == null) {
                item.setCourse(courseFeedbackPersonAnswer.getCourse());
            }
            if (item.getPersonGroup() == null) {
                item.setPersonGroup(courseFeedbackPersonAnswer.getPersonGroup());
            }
            if (item.getCourseSectionSession() == null) {
                item.setCourseSectionSession(courseFeedbackPersonAnswer.getCourseSectionSession());
            }
            if (item.getCourseFeedbackPersonAnswer() == null) {
                item.setCourseFeedbackPersonAnswer(courseFeedbackPersonAnswer);
            }
            if (item.getScore() == null) {
                item.setScore(0);
            } else {
                sumScore += item.getScore();
                questionCount++;
            }

        }
        if (sumScore > 0) {
            sumScoreField.setValue(sumScore);
            avgScoreField.setValue(round((double) sumScore / questionCount, 1));
        }
        return super.preCommit();
    }

    protected Map<String, Object> getCourseSectionSessionLookupParams() {
        Course course = getItem().getCourse();
        if (course != null) {
            return Collections.singletonMap("courseId", course.getId());
        }
        return new HashMap<>();
    }

    protected void courseSectionSessionsDsRefresh() {
        courseSectionSessionsDs.refresh();
    }

    public Component answerField(CourseFeedbackPersonAnswerDetail entity) {
        LookupField lookupField = componentsFactory.createComponent(LookupField.class);
        List<LearningFeedbackAnswer> answers = entity.getQuestion().getAnswers();
        CollectionDatasource optionDS = new DsBuilder(getDsContext())
                .setJavaClass(LearningFeedbackAnswer.class)
                .setViewName(View.LOCAL)
                .setAllowCommit(false)
                .buildCollectionDatasource();
        optionDS.setQuery("select e from tsadv$LearningFeedbackAnswer e where 1<>1");
        answers.forEach(learningFeedbackAnswer -> {
            optionDS.addItem(learningFeedbackAnswer);
        });
        lookupField.setOptionsDatasource(optionDS);
        lookupField.setCaptionMode(CaptionMode.PROPERTY);
        lookupField.setCaptionProperty("answerLangValue");
        lookupField.setValue(entity.getAnswer());
        lookupField.addValueChangeListener(e -> {
            entity.setAnswer(((LearningFeedbackAnswer) e));
            entity.setScore(((LearningFeedbackAnswer) e).getScore());
        });
        lookupField.setRequired(true);
        return lookupField;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}