package kz.uco.tsadv.web.modules.performance.assessment;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.tsadv.modules.learning.model.Answer;
import kz.uco.tsadv.modules.learning.model.Question;
import kz.uco.tsadv.modules.learning.model.QuestionInSection;
import kz.uco.tsadv.modules.learning.model.TestSection;
import kz.uco.tsadv.modules.performance.dictionary.DicNineBoxLevel;
import kz.uco.tsadv.modules.performance.dictionary.DicOverallRating;
import kz.uco.tsadv.modules.performance.dictionary.DicParticipantType;
import kz.uco.tsadv.modules.performance.model.*;
import kz.uco.tsadv.modules.personal.group.CompetenceGroup;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.personal.model.PositionExt;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.entity.abstraction.AbstractDictionary;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.web.modules.personal.common.Utils;
import kz.uco.tsadv.web.gui.components.WebFontRateStars;
import kz.uco.tsadv.web.gui.components.WebRateStars;
import kz.uco.tsadv.web.toolkit.ui.fontratestarscomponent.FontRateStarsComponent;
import kz.uco.tsadv.web.toolkit.ui.ratestarscomponent.RateStarsComponent;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.stream.Collectors;

public class AssessmentEdit extends AbstractEditor<Assessment> {
    @Inject
    private DataManager dataManager;

    @Inject
    private UserSession userSession;

    @Inject
    private Metadata metadata;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private Datasource<Assessment> assessmentDs;

    @Inject
    private VBoxLayout ratingVbox;

    @Inject
    private VBoxLayout ratingVbox_2;

    @Inject
    private VBoxLayout vbox;

    @Inject
    private Embedded photoEmbedded;

    @Inject
    private Label fioAssesmentTemplateLablel;

    @Inject
    private VBoxLayout vbox_3;

    @Inject
    private VBoxLayout ratingVbox_3;

    @Inject
    private FieldGroup fieldGroupImpact;

    @Inject
    private FieldGroup fieldGroupPerformance;

    @Inject
    private FieldGroup fieldGroupPotential;

    @Inject
    private FieldGroup fieldGroupRisk;

    @Inject
    private RichTextArea textAreaManager;

    @Inject
    private RichTextArea textAreaWorker;

    @Inject
    private Button showAllComments;

    @Inject
    private VBoxLayout vbox_1;

    private PersonExt person;
    private PersonExt currentPerson;
    private AssessmentTemplate assessmentTemplate;
    private Assessment assessment;
    private DicParticipantType currentParticipantType;
    private List<SimpleModel<AssessmentCompetence>> competenceModelList = new ArrayList<>();
    private List<SimpleModel<AssessmentGoal>> goalModelList = new ArrayList<>();
    private Map<AssessmentCompetence, RichTextArea> competenceCommentMap = new HashMap<>();
    private Map<AssessmentGoal, RichTextArea> goalCommentMap = new HashMap<>();
    private Map<String, RateStarsComponent> commonRateMap = new HashMap<>();
    private Map<String, FontRateStarsComponent> fontRateStarsMap = new HashMap<>();
    @Named("tabSheet.tab_2")
    private OrderedContainer tab_2;
    @Inject
    private CollectionDatasource<AssessmentPersonAnswer, UUID> assessmentPersonAnswersDs;
    @Inject
    private CommonService commonService;
    @Inject
    private TabSheet tabSheet;
    @Inject
    private Button button;
    @Inject
    private CollectionDatasource<AssessmentCompetence, UUID> assessmentCompetenceDs;
    @Inject
    private CollectionDatasource<AssessmentGoal, UUID> assessmentGoalDs;
    @Inject
    private CollectionDatasource<AssessmentParticipant, UUID> assessmentParticipantDs;

    private class SimpleModel<T extends Entity> {
        private T entity;
        private String participantType;
        private RateStarsComponent rateStars;

        public SimpleModel(T entity, String participantType, RateStarsComponent rateStars) {
            this.entity = entity;
            this.participantType = participantType;
            this.rateStars = rateStars;
        }
    }

    @Override
    public void ready() {
        super.ready();
        fillGlobalFields();

        initHeader();

        initCommonCompetencies();
        initCompetencies();

        initCommonGoals();
        initGoals();

        initTest();

        initOverallSummary();
    }

    private void initTest() {
        List<QuestionInSection> questionList;
        if ("manager".equals(currentParticipantType == null ? null : currentParticipantType.getCode()))
            questionList = assessmentDs.getItem().getTemplate().getManagerTest() != null ?
                    assessmentDs.getItem().getTemplate().getManagerTest().getSections()
                            .stream().findFirst().orElse(new TestSection()).getQuestions() : null;
        else if ("worker".equals(currentParticipantType == null ? null : currentParticipantType.getCode()))
            questionList = assessmentDs.getItem().getTemplate().getWorkerTest() != null ?
                    assessmentDs.getItem().getTemplate().getWorkerTest().getSections()
                            .stream().findFirst().orElse(new TestSection()).getQuestions() : null;
        else
            questionList = assessmentDs.getItem().getTemplate().getParticipantTest() != null ?
                    assessmentDs.getItem().getTemplate().getParticipantTest().getSections()
                            .stream().findFirst().orElse(new TestSection()).getQuestions() : null;
        if (questionList != null && questionList.size() > 0) {
            for (int i = 0; i < questionList.size(); i++) {
                Map<String, Object> params = new HashMap<>();
                params.put("person.id", currentPerson.getGroup().getId()); //TODO check right
                params.put("assessment.id", assessmentDs.getItem().getId());
                params.put("question.id", questionList.get(i).getQuestion().getId());
                AssessmentPersonAnswer personAnswer = commonService.getEntity(AssessmentPersonAnswer.class, params);
                if (personAnswer == null) {
                    personAnswer = metadata.create(AssessmentPersonAnswer.class);
                    personAnswer.setPerson(currentPerson.getGroup());
                    personAnswer.setAssessment(assessmentDs.getItem());
                    personAnswer.setQuestion(questionList.get(i).getQuestion());
                }
                tab_2.add(createQuestion(questionList.get(i).getQuestion(), personAnswer, i + 1));
                assessmentPersonAnswersDs.addItem(personAnswer);
            }
        } else {
            tabSheet.removeTab("tab_2");
        }
    }

    private GroupBoxLayout createQuestion(Question question, AssessmentPersonAnswer personAnswer, int num) {
        GroupBoxLayout block = componentsFactory.createComponent(GroupBoxLayout.class);
        block.addStyleName("start-course-test-question");

        block.setCaption("Вопрос №" + num + ". " + question.getText());
        block.setSpacing(true);

        switch (question.getType()) {
            case TEXT: {
                TextArea<String> textArea = componentsFactory.createComponent(TextArea.class);
                textArea.addValueChangeListener(e -> personAnswer.setAnswer(textArea.getValue()));
                textArea.setValue(personAnswer.getAnswer());
                block.add(textArea);
                break;
            }
            case ONE: {
                OptionsGroup optionsGroup = componentsFactory.createComponent(OptionsGroup.class);
                optionsGroup.setOrientation(OptionsGroup.Orientation.VERTICAL);
                optionsGroup.setOptionsMap(parseToMap(question.getAnswers()));
                if (personAnswer.getAnswer() != null)
                    optionsGroup.setValue(personAnswer.getAnswer().replaceAll("[\\]\\[]", ""));
                optionsGroup.addValueChangeListener(e -> personAnswer.setAnswer(Arrays.toString(optionsGroup.getLookupSelectedItems().toArray())));
                block.add(optionsGroup);
                break;
            }
            case MANY: {
                OptionsGroup optionsGroup = componentsFactory.createComponent(OptionsGroup.class);
                optionsGroup.setMultiSelect(true);
                optionsGroup.setOrientation(OptionsGroup.Orientation.VERTICAL);
                optionsGroup.setOptionsMap(parseToMap(question.getAnswers()));
                if (personAnswer.getAnswer() != null)
                    optionsGroup.setValue(Arrays.asList(personAnswer.getAnswer().replaceAll("[\\]\\[]", "").split(", ")));
                optionsGroup.addValueChangeListener(e -> personAnswer.setAnswer(Arrays.toString(optionsGroup.getLookupSelectedItems().toArray())));
                block.add(optionsGroup);
                break;
            }
            case NUM: {
                TextField<String> textField = componentsFactory.createComponent(TextField.class);
                textField.setValue(personAnswer.getAnswer());
                textField.addValueChangeListener(e -> personAnswer.setAnswer(textField.getValue()));
                block.add(textField);
                break;
            }
        }
        return block;
    }

    private Map<String, String> parseToMap(List<Answer> list) {
        return list.stream().collect(
                Collectors.toMap(Answer::getAnswer, x -> x.getId().toString()));
    }

    private void fillGlobalFields() {
        assessment = assessmentDs.getItem();
        person = assessmentDs.getItem().getEmployeePersonGroup().getPerson();
        assessmentTemplate = assessmentDs.getItem().getTemplate();
        currentPerson = userSession.getAttribute(StaticVariable.USER_PERSON);
        currentParticipantType = getCurrentParticipantType();
    }

    private void initHeader() {
        if (getCurrentAssessmentParticipant() == null) button.setEnabled(false);
        else button.setEnabled(true);
        Utils.getPersonImageEmbedded(person, "100px", photoEmbedded);
        fioAssesmentTemplateLablel.setValue(person.getFioWithEmployeeNumber() +
                ": " + assessmentTemplate.getAssessmentTemplateName());
    }

    private void initCommonCompetencies() {
        Label label;
        WebRateStars webRateStars;
        HBoxLayout hBoxLayout;
        for (DicParticipantType pt : getParticipantTypeList()) {
            if (!hasParticipantOfType(pt)) continue;
            //create needed elements
            label = componentsFactory.createComponent(Label.class);
            webRateStars = componentsFactory.createComponent(WebRateStars.class);
            RateStarsComponent rateStars = (RateStarsComponent) webRateStars.getComponent();
            rateStars.setReadOnly(true);
            rateStars.setListener(newValue -> {
                rateStars.setValue(newValue);
                commonRateMap.get("overall-" + pt.getCode()).setValue(
                        newValue * assessmentDs.getItem().getTemplate().getCompetenceWeight() / 100 +
                                commonRateMap.get("goal-" + pt.getCode()).getValue() *
                                        assessmentDs.getItem().getTemplate().getGoalWeight() / 100);
            });
            rateStars.setValue(getCommonCompetenceRating(pt));
            commonRateMap.put("competence-" + pt.getCode(), rateStars);
            hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
            //set properties
            label.setValue(pt.getLangValue());
            label.setAlignment(Alignment.TOP_RIGHT);
            hBoxLayout.setSpacing(true);
            hBoxLayout.setWidth("100%");
            //add to layouts
            hBoxLayout.add(label);
            hBoxLayout.add(webRateStars);
            ratingVbox.add(hBoxLayout);
        }
    }

    private void initCompetencies() {
        for (AssessmentCompetence ac : getCurrentAssessmentParticipant().getAssessmentCompetence()) {
            buildCompetence(ac);
        }
    }

    public void buildCompetence(AssessmentCompetence ac) {
        //Создание визуальных объектов для одной компетенции
        GroupBoxLayout groupBoxLayout = componentsFactory.createComponent(GroupBoxLayout.class);
        HBoxLayout hBoxLayout1 = componentsFactory.createComponent(HBoxLayout.class);
        GroupBoxLayout groupBoxLayout1 = componentsFactory.createComponent(GroupBoxLayout.class);
        Label competenceNameLabel = componentsFactory.createComponent(Label.class);
        VBoxLayout vBoxLayout = componentsFactory.createComponent(VBoxLayout.class);
        Label weightLabel = componentsFactory.createComponent(Label.class);
        RichTextArea textArea = componentsFactory.createComponent(RichTextArea.class);

        //Задание параметров
        groupBoxLayout.setWidth("100%");
        hBoxLayout1.setWidth("100%");
        groupBoxLayout1.setCollapsable(true);
        groupBoxLayout1.setCaption("Comment");
        groupBoxLayout1.setExpanded(false);
        competenceNameLabel.setAlignment(Alignment.TOP_LEFT);
        competenceNameLabel.setIcon("font-icon:INFO_CIRCLE");
        competenceNameLabel.setValue(ac.getCompetenceGroup().getCompetence().getCompetenceName());
        vBoxLayout.setSpacing(true);
        for (DicParticipantType pt : getParticipantTypeList()) {
            if (!hasParticipantOfType(pt)) continue;
            Label participantTypeLabel = componentsFactory.createComponent(Label.class);
            WebRateStars webRateStars = componentsFactory.createComponent(WebRateStars.class);
            RateStarsComponent rateStars = (RateStarsComponent) webRateStars.getComponent();
            HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);

            participantTypeLabel.setValue(pt.getLangValue());
            participantTypeLabel.setAlignment(Alignment.TOP_RIGHT);
            rateStars.setFullStar(true);
            rateStars.setListener((double newValue) -> {
                rateStars.setValue(newValue);
                commonRateMap.get("competence-" + pt.getCode()).setValue(calculateCommonRate(pt.getCode()));
            });
            if (!pt.equals(currentParticipantType)) {
                rateStars.setReadOnly(true);
            }
            rateStars.setValue(getCompetenceRating(ac.getCompetenceGroup(), pt));
            hBoxLayout.setSpacing(true);
            hBoxLayout.setWidth("100%");

            hBoxLayout.add(participantTypeLabel);
            hBoxLayout.add(webRateStars);
            vBoxLayout.add(hBoxLayout);

            competenceModelList.add(new SimpleModel(ac, pt.getCode(), rateStars));
        }
        weightLabel.setAlignment(Alignment.MIDDLE_CENTER);
        weightLabel.setValue(ac.getWeight().toString());
        textArea.setWidth("100%");
        textArea.setValue(getCompetenceComment(ac.getCompetenceGroup(), getCurrentAssessmentParticipant()));
        if (textArea.getValue() == null || textArea.getValue().isEmpty()) {
            groupBoxLayout1.setIcon("font-icon:COMMENT_O");
        } else {
            groupBoxLayout1.setIcon("font-icon:COMMENTING_O");
        }

        hBoxLayout1.add(competenceNameLabel);
        hBoxLayout1.add(vBoxLayout);
        hBoxLayout1.add(weightLabel);
        groupBoxLayout1.add(textArea);
        groupBoxLayout.add(hBoxLayout1);
        groupBoxLayout.add(groupBoxLayout1);
        vbox.add(groupBoxLayout);

        textArea.addValueChangeListener(e -> {
            if (e.getValue() == null || e.getValue().equals("")) {
                ((GroupBoxLayout) e.getComponent().getParent()).setIcon("font-icon:COMMENT_O");
            } else {
                ((GroupBoxLayout) e.getComponent().getParent()).setIcon("font-icon:COMMENTING_O");
            }
        });

        competenceCommentMap.put(ac, textArea);
    }

    private void initCommonGoals() {
        Label label;
        WebRateStars webRateStars;
        HBoxLayout hBoxLayout;
        for (DicParticipantType pt : getParticipantTypeList()) {
            if (!hasParticipantOfType(pt)) continue;
            //create needed elements
            label = componentsFactory.createComponent(Label.class);
            webRateStars = componentsFactory.createComponent(WebRateStars.class);
            RateStarsComponent rateStars = (RateStarsComponent) webRateStars.getComponent();
            rateStars.setReadOnly(true);
            rateStars.setListener(newValue -> {
                rateStars.setValue(newValue);
                commonRateMap.get("overall-" + pt.getCode()).setValue(
                        newValue * assessmentDs.getItem().getTemplate().getGoalWeight() / 100 +
                                commonRateMap.get("competence-" + pt.getCode()).getValue() *
                                        assessmentDs.getItem().getTemplate().getCompetenceWeight() / 100);
            });
            rateStars.setValue(getCommonGoalRating(pt));
            commonRateMap.put("goal-" + pt.getCode(), rateStars);
            hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
            //set properties
            label.setValue(pt.getLangValue());
            label.setAlignment(Alignment.TOP_RIGHT);
            hBoxLayout.setSpacing(true);
            hBoxLayout.setWidth("100%");
            //add to layouts
            hBoxLayout.add(label);
            hBoxLayout.add(webRateStars);
            ratingVbox_2.add(hBoxLayout);
        }
    }

    private void initGoals() {
        for (AssessmentGoal ag : getCurrentAssessmentParticipant().getAssessmentGoal()) {
            GroupBoxLayout groupBoxLayout = componentsFactory.createComponent(GroupBoxLayout.class);
            HBoxLayout hBoxLayout1 = componentsFactory.createComponent(HBoxLayout.class);
            GroupBoxLayout groupBoxLayout1 = componentsFactory.createComponent(GroupBoxLayout.class);
            Label goalNameLabel = componentsFactory.createComponent(Label.class);
            VBoxLayout vBoxLayout = componentsFactory.createComponent(VBoxLayout.class);
            Label weightLabel = componentsFactory.createComponent(Label.class);
            RichTextArea textArea = componentsFactory.createComponent(RichTextArea.class);

            groupBoxLayout.setWidth("100%");
            hBoxLayout1.setWidth("100%");
            groupBoxLayout1.setCollapsable(true);
            groupBoxLayout1.setCaption("Comment");
            groupBoxLayout1.setExpanded(false);
            goalNameLabel.setAlignment(Alignment.MIDDLE_LEFT);
            goalNameLabel.setIcon("font-icon:INFO_CIRCLE");
            goalNameLabel.setValue(ag.getGoal().getGoalName());
            vBoxLayout.setSpacing(true);
            for (DicParticipantType pt : getParticipantTypeList()) {
                if (!hasParticipantOfType(pt)) continue;
                Label participantTypeLabel = componentsFactory.createComponent(Label.class);
                WebRateStars webRateStars = componentsFactory.createComponent(WebRateStars.class);
                RateStarsComponent rateStars = (RateStarsComponent) webRateStars.getComponent();
                HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);

                participantTypeLabel.setValue(pt.getLangValue());
                participantTypeLabel.setAlignment(Alignment.TOP_RIGHT);
                rateStars.setFullStar(true);
                rateStars.setListener((double newValue) -> {
                    rateStars.setValue(newValue);
                    commonRateMap.get("goal-" + pt.getCode()).setValue(calculateCommonGoalRate(pt.getCode()));
                });
                if (!pt.equals(currentParticipantType)) {
                    rateStars.setReadOnly(true);
                }
                rateStars.setValue(getGoalRating(pt, ag.getGoal()));
                hBoxLayout.setSpacing(true);
                hBoxLayout.setWidth("100%");

                hBoxLayout.add(participantTypeLabel);
                hBoxLayout.add(webRateStars);
                vBoxLayout.add(hBoxLayout);

                goalModelList.add(new SimpleModel(ag, pt.getCode(), rateStars));
            }
            weightLabel.setAlignment(Alignment.MIDDLE_CENTER);
            weightLabel.setValue(ag.getWeight());
            textArea.setWidth("100%");
            textArea.setValue(ag.getComment());

            hBoxLayout1.add(goalNameLabel);
            hBoxLayout1.add(vBoxLayout);
            hBoxLayout1.add(weightLabel);
            groupBoxLayout1.add(textArea);
            groupBoxLayout.add(hBoxLayout1);
            groupBoxLayout.add(groupBoxLayout1);
            vbox_1.add(groupBoxLayout);

            goalCommentMap.put(ag, textArea);
        }
    }

    private String getGoalComment(AssignedGoal ag, AssessmentParticipant currentAssessmentParticipant) {
        if (currentAssessmentParticipant == null) return null;
        LoadContext<AssessmentGoal> loadContext = LoadContext.create(AssessmentGoal.class);
        loadContext.setQuery(LoadContext.createQuery("select e from tsadv$AssessmentGoal e where " +
                "e.goal.id = :goal and e.participantAssessment.id = :ap")
                .setParameter("goal", ag.getGoal().getId())
                .setParameter("ap", currentAssessmentParticipant.getId()));
        AssessmentGoal assessmentGoal = dataManager.load(loadContext);
        if (assessmentGoal == null)
            return null;
        else
            return assessmentGoal.getComment();
    }

    private void initOverallSummary() {
        Label label;
        WebRateStars webRateStars;
        HBoxLayout hBoxLayout;
        for (DicParticipantType pt : getParticipantTypeList()) {
            //create needed elements
            label = componentsFactory.createComponent(Label.class);
            webRateStars = componentsFactory.createComponent(WebRateStars.class);
            RateStarsComponent rateStars = (RateStarsComponent) webRateStars.getComponent();
            rateStars.setReadOnly(true);
            rateStars.setListener(rateStars::setValue);
            rateStars.setValue(getCommonOverallRating(pt));
            commonRateMap.put("overall-" + pt.getCode(), rateStars);
            hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
            //set properties
            label.setValue(pt.getLangValue());
            label.setAlignment(Alignment.TOP_RIGHT);
            hBoxLayout.setSpacing(true);
            hBoxLayout.setWidth("100%");
            //add to layouts
            hBoxLayout.add(label);
            hBoxLayout.add(webRateStars);
            ratingVbox_3.add(hBoxLayout);
        }

        textAreaManager.setEditable(isManager());
        if (!isManager() && (textAreaManager.getValue() == null || textAreaManager.getValue().isEmpty())) {
            textAreaManager.setHeight("30px");
        }
        textAreaManager.setValue(getAssessmentParticipantByType(getParticipantTypeByCode("manager")).getOverallComment());
        if (isManager() && (textAreaWorker.getValue() == null || textAreaWorker.getValue().isEmpty())) {
            textAreaWorker.setHeight("30px");
        }
        textAreaWorker.setEditable(!isManager());
        textAreaWorker.setValue(getAssessmentParticipantByType(getParticipantTypeByCode("worker")).getOverallComment());

        //performance
        addFontRateStars("performance", "fontRatePerformance", fieldGroupPerformance)
                .setValue(getCurrentAssessmentParticipant() == null ? 0 : (getCurrentAssessmentParticipant().getPerformance() == null
                        ? 0 : Integer.valueOf(getCurrentAssessmentParticipant().getPerformance().getCode())));
        //potential
        addFontRateStars("potential", "fontRatePotential", fieldGroupPotential)
                .setValue(getCurrentAssessmentParticipant() == null ? 0 : (getCurrentAssessmentParticipant().getPotential() == null
                        ? 0 : Integer.valueOf(getCurrentAssessmentParticipant().getPotential().getCode())));
        //risk
        addFontRateStars("risk", "fontRateRisk", fieldGroupRisk)
                .setValue(getCurrentAssessmentParticipant() == null ? 0 : (getCurrentAssessmentParticipant().getRiskOfLoss() == null
                        ? 0 : Integer.valueOf(getCurrentAssessmentParticipant().getRiskOfLoss().getCode())));
        //impact
        addFontRateStars("impact", "fontRateImpact", fieldGroupImpact)
                .setValue(getCurrentAssessmentParticipant() == null ? 0 : (getCurrentAssessmentParticipant().getImpactOfLoss() == null
                        ? 0 : Integer.valueOf(getCurrentAssessmentParticipant().getImpactOfLoss().getCode())));
    }

    private String getCompetenceComment(CompetenceGroup cg, AssessmentParticipant ap) {
        if (ap == null) return null;
        LoadContext<AssessmentCompetence> loadContext = LoadContext.create(AssessmentCompetence.class);
        loadContext.setQuery(LoadContext.createQuery("select e from tsadv$AssessmentCompetence e where " +
                "e.competenceGroup.id = :cg and e.participantAssessment.id = :ap")
                .setParameter("cg", cg.getId())
                .setParameter("ap", ap == null ? null : ap.getId()));
        AssessmentCompetence ac = dataManager.load(loadContext);
        if (ac == null)
            return null;
        else
            return ac.getComment();
    }

    private DicParticipantType getParticipantTypeByCode(String code) {
        LoadContext<DicParticipantType> loadContext = LoadContext.create(DicParticipantType.class);
        loadContext.setQuery(LoadContext.createQuery("select e from tsadv$DicParticipantType e where e.code = :code")
                .setParameter("code", code));
        return dataManager.load(loadContext);
    }

    private boolean isManager() {
        boolean result = false;
        LoadContext<PositionExt> loadContext = LoadContext.create(PositionExt.class);
        loadContext.setQuery(LoadContext.createQuery("select e from base$PositionExt e " +
                "where e.group.id = :userPositionGroupId")
                .setParameter("userPositionGroupId", userSession.getAttribute("userPositionGroupId")));
        PositionExt p = dataManager.load(loadContext);
        if (p != null)
            result = p.getManagerFlag();
        return result;
    }

    private FontRateStarsComponent addFontRateStars(String fieldName, String fieldId, FieldGroup fieldGroup) {
        String[] messages = getNineBoxValues();
        WebFontRateStars webFontRateStars = componentsFactory.createComponent(WebFontRateStars.class);
        FontRateStarsComponent fontRateStarsComponent = (FontRateStarsComponent) webFontRateStars.getComponent();
        fontRateStarsComponent.setMessages(messages);
        fontRateStarsComponent.setListener(fontRateStarsComponent::setValue);
        FieldGroup.FieldConfig fieldConfig = fieldGroup.getFieldNN(fieldId);
        fieldConfig.setWidth("350px");
        fieldConfig.setComponent(webFontRateStars);
        fontRateStarsMap.put(fieldName, fontRateStarsComponent);
        return fontRateStarsComponent;
    }

    public <T extends AbstractDictionary> List<T> getDicEntityList(Class<T> entityClass) {
        LoadContext<T> loadContext = LoadContext.create(entityClass);
        loadContext.setQuery(LoadContext.createQuery("select e from tsadv$" + entityClass.getSimpleName() + " e"));
        return dataManager.loadList(loadContext);
    }

    private String[] getNineBoxValues() {
        List<DicNineBoxLevel> list = getDicEntityList(DicNineBoxLevel.class);
        String[] result = new String[list.size() + 1];
        result[0] = getMessage("Font.Rate.empty");
        list.forEach(dnbl -> result[Integer.valueOf(dnbl.getCode())] = dnbl.getLangValue());
        return result;
    }

    //save
    public void onButtonClick() {
        assessment = assessmentDs.getItem();
        assessment.setCompetenceRating(getOverallCompetenceRating());
        assessment.setGoalRating(getOverallGoalRating());
        assessment.setOveralRating(getOverallRating());
        assessment.setPerformance(getCommonEfficiency("performance"));
        assessment.setPotential(getCommonEfficiency("potential"));
        assessment.setRiskOfLoss(getCommonEfficiency("risk"));
        assessment.setImpactOfLoss(getCommonEfficiency("impact"));

        AssessmentParticipant ap = getCurrentAssessmentParticipant();
        assessmentParticipantDs.setItem(ap);
        //Заполнение оценок
        ap.setGoalRating(commonRateMap.get("goal-" + currentParticipantType.getCode()).getValue());
        ap.setCompetenceRating(commonRateMap.get("competence-" + currentParticipantType.getCode()).getValue());
        ap.setOverallRating(commonRateMap.get("overall-" + currentParticipantType.getCode()).getValue());
        //Заполнение эффективности
        ap.setPerformance(getDicNineBoxValue("performance"));
        ap.setPotential(getDicNineBoxValue("potential"));
        ap.setRiskOfLoss(getDicNineBoxValue("risk"));
        ap.setImpactOfLoss(getDicNineBoxValue("impact"));
        //Заполнение комментария
        ap.setOverallComment(isManager() ? textAreaManager.getValue() : textAreaWorker.getValue());

        for (AssessmentCompetence ac : ap.getAssessmentCompetence()) {
            ac.setComment(competenceCommentMap.get(ac).getValue());
            ac.setOverallRating(getOverallRatingByCode(String.valueOf(
                    Math.round(getRateByTypeAndCompetenceFromList(ap.getParticipantType().getCode(), ac.getCompetenceGroup())))));
            assessmentCompetenceDs.modifyItem(ac);
        }

        for (AssessmentGoal ag : ap.getAssessmentGoal()) {
            ag.setComment(goalCommentMap.get(ag).getValue());
            ag.setComment(goalCommentMap.get(ag).getValue());
            ag.setOverallRating(getOverallRatingByCode(String.valueOf(
                    Math.round(getRateByTypeAndGoalFromList(ap.getParticipantType().getCode(), ag.getGoal())))));
            assessmentGoalDs.modifyItem(ag);
        }

        commitAndClose();
    }

    private Double getCommonEfficiency(String efficiencyType) {
        Double result = 0.0;
        for (AssessmentParticipant assessmentParticipant : assessmentDs.getItem().getAssessmentParticipant()) {
            if (!assessmentParticipant.equals(getCurrentAssessmentParticipant())) {
                if (assessmentParticipant.getPerformance() != null) {
                    result += Integer.valueOf(assessmentParticipant.getPerformance().getCode());
                }
            }
        }
        result += fontRateStarsMap.get(efficiencyType).getValue();
        result = result / assessment.getAssessmentParticipant().size();
        return result;
    }

    private <T extends AbstractDictionary> T getDicEntityByCode(Class<T> entityClass, String code) {
        LoadContext<T> loadContext = LoadContext.create(entityClass);
        loadContext.setQuery(LoadContext
                .createQuery("select e from tsadv$" + entityClass.getSimpleName() + " e where e.code = :code")
                .setParameter("code", code));
        return dataManager.load(loadContext);

    }

    private DicNineBoxLevel getDicNineBoxValue(String fontRateStarsMapKey) {
        return getDicEntityByCode(DicNineBoxLevel.class,
                String.valueOf(fontRateStarsMap.get(fontRateStarsMapKey).getValue()));
    }

    //utils
    private Double getCommonCompetenceRating(DicParticipantType pt) {
        if (pt.getCode().equals("participant")) {
            if (currentParticipantType.equals(pt)) {
                return getCurrentAssessmentParticipant().getCompetenceRating();
            }
            List<AssessmentParticipant> apList = new ArrayList<>();
            for (AssessmentParticipant ap : assessmentDs.getItem().getAssessmentParticipant()) {
                if (ap.getParticipantType().equals(pt)) {
                    apList.add(ap);
                }
            }
            Double result = 0.0;
            for (AssessmentParticipant ap : apList) {
                result += ap.getCompetenceRating();
            }
            return result / apList.size();
        } else {
            return getAssessmentParticipantByType(pt).getCompetenceRating();
        }
    }

    private Double getCompetenceRating(CompetenceGroup competenceGroup, DicParticipantType pt) {
        if ("participant".equals(pt.getCode())) {
            if (currentParticipantType.equals(pt)) {
                return getRateByCompetenceAndAP(competenceGroup, getCurrentAssessmentParticipant());
            } else {
                Double result = 0.0;
                List<AssessmentParticipant> apList = new ArrayList<>();
                for (AssessmentParticipant ap : assessmentDs.getItem().getAssessmentParticipant()) {
                    if (ap.getParticipantType().equals(pt)) {
                        apList.add(ap);
                    }
                }
                for (AssessmentParticipant ap : apList) {
                    result += getRateByCompetenceAndAP(competenceGroup, ap);
                }
                return result / apList.size();
            }
        } else {
            return getRateByCompetenceAndAP(competenceGroup, getAssessmentParticipantByType(pt));
        }
    }

    private Double getRateByCompetenceAndAP(CompetenceGroup competenceGroup, AssessmentParticipant assessmentParticipant) {
        for (AssessmentCompetence ac : assessmentParticipant.getAssessmentCompetence()) {
            if (ac.getCompetenceGroup().equals(competenceGroup)) {
                return Double.valueOf(ac.getOverallRating().getCode());
            }
        }
        return 0.0;
    }

    private Double getCommonGoalRating(DicParticipantType pt) {
        if ("participant".equals(pt.getCode())) {
            if (currentParticipantType.equals(pt)) {
                return getCurrentAssessmentParticipant().getGoalRating();
            }
            List<AssessmentParticipant> apList = new ArrayList<>();
            for (AssessmentParticipant ap : assessmentDs.getItem().getAssessmentParticipant()) {
                if (ap.getParticipantType().equals(pt)) {
                    apList.add(ap);
                }
            }
            Double result = 0.0;
            for (AssessmentParticipant ap : apList) {
                result += ap.getGoalRating();
            }
            return result / apList.size();
        } else {
            return getAssessmentParticipantByType(pt).getGoalRating();
        }
    }

    private Double getGoalRating(DicParticipantType pt, Goal goal) {
        if ("participant".equals(pt.getCode())) {
            if (currentParticipantType.equals(pt)) {
                return getRateByGoalAndAP(goal, getCurrentAssessmentParticipant());
            } else {
                Double result = 0.0;
                List<AssessmentParticipant> apList = new ArrayList<>();
                for (AssessmentParticipant ap : assessmentDs.getItem().getAssessmentParticipant()) {
                    if (ap.getParticipantType().equals(pt)) {
                        apList.add(ap);
                    }
                }
                for (AssessmentParticipant ap : apList) {
                    result += getRateByGoalAndAP(goal, ap);
                }
                return result / apList.size();
            }
        } else {
            return getRateByGoalAndAP(goal, getAssessmentParticipantByType(pt));
        }
    }

    private Double getRateByGoalAndAP(Goal goal, AssessmentParticipant ap) {
        for (AssessmentGoal ag : ap.getAssessmentGoal()) {
            if (ag.getGoal().equals(goal)) {
                return Double.valueOf(ag.getOverallRating().getCode());
            }
        }
        return 0.0;
    }

    private Double getCommonOverallRating(DicParticipantType pt) {
        if ("participant".equals(pt.getCode())) {
            if (pt.equals(currentParticipantType)) {
                return getCurrentAssessmentParticipant().getOverallRating();
            }
            List<AssessmentParticipant> apList = new ArrayList<>();
            for (AssessmentParticipant ap : assessmentDs.getItem().getAssessmentParticipant()) {
                if (ap.getParticipantType().equals(pt)) {
                    apList.add(ap);
                }
            }
            Double result = 0.0;
            for (AssessmentParticipant ap : apList) {
                result += ap.getOverallRating();
            }
            return result / apList.size();
        } else {
            return getAssessmentParticipantByType(pt).getOverallRating();
        }
    }

    private List<DicParticipantType> getParticipantTypeList() {
        LoadContext<DicParticipantType> loadContext = LoadContext.create(DicParticipantType.class);
        loadContext.setQuery(LoadContext.createQuery("select e from tsadv$DicParticipantType e"));
        return dataManager.loadList(loadContext);
    }

    private boolean hasParticipantOfType(DicParticipantType pt) {
        for (AssessmentParticipant ap : assessmentDs.getItem().getAssessmentParticipant()) {
            if (ap.getParticipantType().equals(pt)) {
                return true;
            }
        }
        return false;
    }

    private Double calculateCommonGoalRate(String participantType) {
        Double result = 0.0;
        for (SimpleModel<AssessmentGoal> gm : goalModelList) {
            if (gm.participantType.equals(participantType)) {
                result += gm.entity.getWeight() / 100 * gm.rateStars.getValue();
            }
        }
        return result;
    }

    private List<AssignedGoal> getAssignedGoals() {
        LoadContext<AssignedGoal> loadContext = LoadContext.create(AssignedGoal.class);
        loadContext.setQuery(LoadContext.createQuery("select e from tsadv$AssignedGoal e " +
                "where e.personGroup.id = :personId")
                .setParameter("personId", person.getGroup().getId()))
                .setView("assignedGoalForCard");
        return dataManager.loadList(loadContext);
    }

    private Double calculateCommonRate(String participantType) {
        Double result = 0.0;
        for (SimpleModel<AssessmentCompetence> cm : competenceModelList) {
            if (cm.participantType.equals(participantType)) {
                result += cm.entity.getWeight() / 100 * cm.rateStars.getValue();
            }
        }
        return result;
    }

    private AssessmentParticipant getAssessmentParticipantByType(DicParticipantType participantType) {
        for (AssessmentParticipant ap : assessmentDs.getItem().getAssessmentParticipant()) {
            if (ap.getParticipantType().equals(participantType))
                return ap;
        }
        return null;
    }

    private AssessmentParticipant getCurrentAssessmentParticipant() {
        for (AssessmentParticipant ap : assessmentDs.getItem().getAssessmentParticipant()) {
            if (ap.getParticipantPersonGroup().equals(currentPerson.getGroup()))
                return ap;
        }
        return null;
    }

    @Nullable
    @SuppressWarnings("all")
    private Embedded generateImage(PersonExt person, String height, Embedded displayComponent) {
        if (displayComponent == null) {
            displayComponent = (Embedded) componentsFactory.createComponent(Embedded.NAME);
        } else {
            displayComponent.resetSource();
        }

        /*if (person == null || person.getPhoto() == null) {
            displayComponent.setSource("theme://images/no-avatar.png");
            return displayComponent;
        }

        displayComponent.setSource("userImage-" + person.getId(), new ByteArrayInputStream(person.getPhoto()));TODO*/
        displayComponent.setType(Embedded.Type.IMAGE);

        if (height != null)
            displayComponent.setHeight(height);

        return displayComponent;
    }

    private Double getOverallCompetenceRating() {
        Double result = 0.0;
        for (AssessmentParticipant assessmentParticipant : assessmentDs.getItem().getAssessmentParticipant()) {
            if (!assessmentParticipant.equals(getCurrentAssessmentParticipant())) {
                result += assessmentParticipant.getCompetenceRating();
            }
        }
        result += commonRateMap.get("competence-" + currentParticipantType.getCode()).getValue();
        result = result / assessment.getAssessmentParticipant().size();
        return result;
    }

    private Double getOverallGoalRating() {
        Double result = 0.0;
        for (AssessmentParticipant assessmentParticipant : assessmentDs.getItem().getAssessmentParticipant()) {
            if (!assessmentParticipant.equals(getCurrentAssessmentParticipant())) {
                result += assessmentParticipant.getGoalRating();
            }
        }
        result += commonRateMap.get("goal-" + currentParticipantType.getCode()).getValue();
        result = result / assessment.getAssessmentParticipant().size();
        return result;
    }

    private Double getOverallRating() {
        Double result = 0.0;
        for (AssessmentParticipant assessmentParticipant : assessmentDs.getItem().getAssessmentParticipant()) {
            if (!assessmentParticipant.equals(getCurrentAssessmentParticipant())) {
                result += assessmentParticipant.getOverallRating();
            }
        }
        result += commonRateMap.get("overall-" + currentParticipantType.getCode()).getValue();
        result = result / assessment.getAssessmentParticipant().size();
        return result;
    }

    private Double getRateByTypeAndCompetenceFromList(String type, CompetenceGroup competenceGroup) {
        for (SimpleModel<AssessmentCompetence> cm : competenceModelList) {
            if (cm.participantType.equals(type) && cm.entity.getCompetenceGroup().equals(competenceGroup)) {
                return cm.rateStars.getValue();
            }
        }
        return 0.0;
    }

    private Double getRateByTypeAndGoalFromList(String type, Goal goal) {
        for (SimpleModel<AssessmentGoal> gm : goalModelList) {
            if (gm.participantType.equals(type) && gm.entity.getGoal().equals(goal)) {
                return gm.rateStars.getValue();
            }
        }
        return 0.0;
    }

    private AssessmentGoal getCurrentAssessmentGoal(AssessmentParticipant assessmentParticipant, AssignedGoal ag) {
        if (assessmentParticipant.getAssessmentGoal() != null) {
            for (AssessmentGoal assessmentGoal : assessmentParticipant.getAssessmentGoal()) {
                if (assessmentGoal.getGoal().equals(ag.getGoal())) {
                    return assessmentGoal;
                }
            }
        } else {
            assessmentParticipant.setAssessmentGoal(new ArrayList<>());
        }
        return null;
    }

    private DicOverallRating getOverallRatingByCode(String code) {
        LoadContext<DicOverallRating> loadContext = LoadContext.create(DicOverallRating.class);
        loadContext.setQuery(LoadContext
                .createQuery("select e from tsadv$DicOverallRating e where e.code = '" + code + "'"));
        return dataManager.load(loadContext);
    }

    private DicParticipantType getCurrentParticipantType() {
        AssessmentParticipant ap = assessmentDs.getItem().getAssessmentParticipant().stream()
                .filter(nextAp -> Objects.equals(currentPerson.getGroup(), nextAp.getParticipantPersonGroup()))
                .findFirst().orElse(null);
        return ap == null ? null : ap.getParticipantType();
    }

    public void onButton_1Click() {
        close("");
    }
}