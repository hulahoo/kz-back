package kz.uco.tsadv.web.modules.personal.person.frames;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.ButtonsPanel;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.performance.dictionary.DicParticipantType;
import kz.uco.tsadv.modules.performance.model.Assessment;
import kz.uco.tsadv.modules.performance.model.AssessmentParticipant;
import kz.uco.tsadv.modules.personal.group.CompetenceGroup;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.CompetenceElement;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.web.modules.performance.assessment.AssessmentCreate;
import kz.uco.tsadv.web.modules.performance.assessment.AssessmentEdit;

import javax.inject.Inject;
import java.util.*;

@SuppressWarnings("all")
public class PcfAssessment extends EditableFrame {

    @Inject
    private Table assessmentTable;

    @Inject
    private UserSessionSource userSessionSource;

    @Inject
    private Metadata metadata;

    @Inject
    private DataManager dataManager;

    @Inject
    private ButtonsPanel buttonsPanel;

    public CollectionDatasource<Assessment, UUID> assessmentsDs;
    public Datasource<PersonExt> personDs;
    @Inject
    private CommonService commonService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        assessmentTable.addAction(new CreateAction(assessmentTable) {
            @Override
            public void actionPerform(Component component) {
                createAssessment();
            }

            @Override
            public String getCaption() {
                return "";
            }

            @Override
            public boolean isEnabled() {
                return isManager();
            }

            @Override
            public String getIcon() {
                return "icons/plus-btn.png";
            }
        });

        assessmentTable.addAction(new EditAction(assessmentTable) {
            @Override
            public void actionPerform(Component component) {
                editAssessment();
            }

            @Override
            public boolean isEnabled() {
                return super.isEnabled() && isManager();
            }

            @Override
            public String getCaption() {
                return "";
            }
        });

        BaseAction assessAction = new BaseAction("assess") {
            @Override
            public void actionPerform(Component component) {
                assess();
            }
        };
        assessAction.setEnabled(false);

        assessmentTable.addAction(assessAction);

        assessmentsDs.addItemChangeListener(e -> {
            assessAction.setEnabled(e.getItem() != null);
        });
    }

    private PersonGroupExt getFirstManager(PersonGroupExt group) {
        return (PersonGroupExt) userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP); //TODO right select
    }

    private void editAssessment() {
        AssessmentCreate editor = (AssessmentCreate) openEditor("tsadv$Assessment.create", assessmentsDs.getItem(), WindowManager.OpenType.DIALOG);
        editor.addCloseListener(actionId -> {
            assessmentsDs.refresh();
        });
    }

    private boolean isManager() {
        LoadContext<AssignmentExt> loadContext = LoadContext.create(AssignmentExt.class);
        loadContext.setQuery(LoadContext.createQuery("select e from base$AssignmentExt e " +
                "where e.personGroup.id = :personGroupId")
                .setParameter("personGroupId", (userSessionSource.getUserSession().getAttribute("userPersonGroupId"))))
                .setView("assignment.isManager");
        List<AssignmentExt> assignment = dataManager.loadList(loadContext);
        return assignment == null || assignment.isEmpty() ? false : assignment.get(0).getPositionGroup().getPosition().getManagerFlag();
    }

    private void createAssessment() {
        PersonGroupExt firstManager = getFirstManager(personDs.getItem().getGroup());
        Assessment assessment = metadata.create(Assessment.class);
        assessment.setEmployeePersonGroup(personDs.getItem().getGroup());
        assessment.setPerformance(0.0);
        assessment.setPotential(0.0);
        assessment.setRiskOfLoss(0.0);
        assessment.setImpactOfLoss(0.0);
        assessment.setOveralRating(0.0);
        assessment.setCompetenceRating(0.0);
        assessment.setGoalRating(0.0);
        if (assessment.getAssessmentParticipant() == null) {
            assessment.setAssessmentParticipant(new ArrayList<>());
        }
        assessment.getAssessmentParticipant().add(getNewAssessmentParticipant("worker", personDs.getItem().getGroup(), assessment));
        assessment.getAssessmentParticipant().add(getNewAssessmentParticipant("manager", firstManager, assessment));
        if (!((PersonGroupExt) userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP)).equals(firstManager)) {
            assessment.getAssessmentParticipant().add(getNewAssessmentParticipant("participant",
                    ((PersonExt) userSessionSource.getUserSession().getAttribute("userPerson")).getGroup(), assessment));
        }

        AssessmentCreate creator = (AssessmentCreate) openEditor("tsadv$Assessment.create", assessment, WindowManager.OpenType.DIALOG);
        creator.addCloseListener(actionId -> {
            assessmentsDs.refresh();
        });
    }

    private List<CompetenceGroup> getPositionCompetenceGroup(Assessment assessment) {
        List<CompetenceGroup> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("personGroupId", assessment.getEmployeePersonGroup().getId());
        map.put("systemDate", CommonUtils.getSystemDate());
        PositionGroupExt positionGroup = commonService.getEntity(PositionGroupExt.class,
                "select e.positionGroup from base$AssignmentExt e " +
                        " where e.personGroup.id =  :personGroupId" +
                        "   and :systemDate between e.startDate and e.endDate", map,
                "positionGroup.forAssess");
        if (positionGroup != null) {
            for (CompetenceElement ce : positionGroup.getCompetenceElements()) {
                list.add(ce.getCompetenceGroup());
            }
        }
        return list;
    }

    private AssessmentParticipant getNewAssessmentParticipant(String type, PersonGroupExt participant, Assessment assessment) {
        AssessmentParticipant ap = metadata.create(AssessmentParticipant.class);
        ap.setParticipantType(getParticipantType(type));
        ap.setParticipantPersonGroup(participant);
        ap.setAssessment(assessment);
        ap.setAssessmentCompetence(new ArrayList<>());
        ap.setAssessmentGoal(new ArrayList<>());
        setDefaultRating(ap);
        return ap;
    }


    private void setDefaultRating(AssessmentParticipant ap) {
        ap.setCompetenceRating(0.0);
        ap.setGoalRating(0.0);
        ap.setOverallRating(0.0);
    }

    private DicParticipantType getParticipantType(String type) {
        LoadContext<DicParticipantType> loadContext = LoadContext.create(DicParticipantType.class);
        loadContext.setQuery(LoadContext.createQuery("select e from tsadv$DicParticipantType e where e.code = :type")
                .setParameter("type", type));
        return dataManager.load(loadContext);
    }

    private void assess() {
        AssessmentEdit editor = (AssessmentEdit) openEditor("tsadv$Assessment.edit", assessmentsDs.getItem(), WindowManager.OpenType.THIS_TAB);
        editor.addCloseListener(actionId -> {
            assessmentsDs.refresh();
        });
    }

    @Override
    public void editable(boolean editable) {
        buttonsPanel.setVisible(editable);
    }

    @Override
    public void initDatasource() {
        assessmentsDs = (CollectionDatasource<Assessment, UUID>) getDsContext().get("assessmentsDs");
        personDs = getDsContext().get("personDs");
    }
}