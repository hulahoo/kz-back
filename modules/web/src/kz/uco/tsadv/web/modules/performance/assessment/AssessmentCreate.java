package kz.uco.tsadv.web.modules.performance.assessment;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.performance.dictionary.DicOverallRating;
import kz.uco.tsadv.modules.performance.model.*;
import kz.uco.tsadv.modules.personal.group.CompetenceGroup;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.CompetenceElement;
import kz.uco.base.service.common.CommonService;

import javax.inject.Inject;
import java.util.*;

public class AssessmentCreate extends AbstractEditor<Assessment> {
    @Inject
    private Datasource<Assessment> assessmentDs;
    @Inject
    private CollectionDatasource<AssessmentParticipant, UUID> assessmentParticipantDs;
    @Inject
    private CollectionDatasource<AssessmentCompetence, UUID> assessmentCompetenceDs;
    @Inject
    private CommonService commonService;
    @Inject
    private Metadata metadata;
    @Inject
    private CollectionDatasource<AssessmentGoal, UUID> assessmentGoalDs;
    @Inject
    private DataManager dataManager;

    @Override
    protected boolean preCommit() {
        DicOverallRating defaultOverallRating = commonService.getEntity(DicOverallRating.class, "0");
        for (AssessmentParticipant ap : assessmentParticipantDs.getItems()) {
            assessmentParticipantDs.setItem(ap);

            clearCompetence(ap);
            clearGoal(ap);

            fillCompetenceFromPosition(ap, defaultOverallRating);
            fillCompetenceFromTemplate(ap, defaultOverallRating);

            fillGoalFromTemplate(ap, defaultOverallRating);
        }
        return super.preCommit();
    }

    private void fillGoalFromTemplate(AssessmentParticipant ap, DicOverallRating defaultOverallRating) {
        for (AssignedGoal ag : getAssignedGoals()) {
            AssessmentGoal assessmentGoal = metadata.create(AssessmentGoal.class);
            assessmentGoal.setParticipantAssessment(ap);
            assessmentGoal.setGoal(ag.getParentGoal());
            assessmentGoal.setWeight(ag.getWeight().doubleValue());
            assessmentGoal.setOverallRating(defaultOverallRating);
            assessmentGoalDs.addItem(assessmentGoal);
        }
    }

    private void fillCompetenceFromTemplate(AssessmentParticipant ap, DicOverallRating defaultOverallRating) {
        Double freedWeight = 0.0;
        List<AssessmentCompetence> addedCompetence = new ArrayList<>();
        for (CompetenceTemplateDetail ctd : assessmentDs.getItem().getTemplate().getCompetenceTemplate().getCompetenceTemplateDetail()) {
            if (assessmentCompetenceDs.getItems().stream().filter(ac -> ac.getCompetenceGroup().equals(ctd.getCompetenceGroup()) && ap.equals(ac.getParticipantAssessment())).findFirst().orElse(null) == null) {
                AssessmentCompetence ac = metadata.create(AssessmentCompetence.class);
                ac.setCompetenceGroup(ctd.getCompetenceGroup());
                ac.setWeight(ctd.getWeight().doubleValue());
                ac.setParticipantAssessment(ap);
                ac.setOverallRating(defaultOverallRating);
                addedCompetence.add(ac);
            } else {
                freedWeight += ctd.getWeight();
            }
        }
        Double averageWeight = 0.0;
        if (addedCompetence.size() != 0) {
            averageWeight = ((Long) Math.round(freedWeight * 100 / addedCompetence.size())).doubleValue() / 100;
        }
        for (AssessmentCompetence ac : addedCompetence) {
            if (addedCompetence.get(addedCompetence.size() - 1).equals(ac)) {
                ac.setWeight(ac.getWeight() + freedWeight - (addedCompetence.size() - 1) * averageWeight);
            } else {
                ac.setWeight(ac.getWeight() + averageWeight);
            }
            assessmentCompetenceDs.addItem(ac);
        }
    }

    private void fillCompetenceFromPosition(AssessmentParticipant ap, DicOverallRating defOverallRating) {
        List<CompetenceGroup> competenceGroupList = getPositionCompetenceGroup();
        for (CompetenceGroup cg : competenceGroupList) {
            AssessmentCompetence ac = metadata.create(AssessmentCompetence.class);
            ac.setParticipantAssessment(ap);
            ac.setCompetenceGroup(cg);
            Double averageWeight =
                    ((Long) Math.round(assessmentDs.getItem()
                            .getTemplate()
                            .getCompetenceTemplate()
                            .getPositionCompetenceWeight()
                            .doubleValue() * 100
                            / competenceGroupList.size())).doubleValue() / 100;
            if (competenceGroupList.get(competenceGroupList.size() - 1).equals(cg)) {
                ac.setWeight(assessmentDs.getItem()
                        .getTemplate()
                        .getCompetenceTemplate()
                        .getPositionCompetenceWeight().doubleValue() - averageWeight * (competenceGroupList.size() - 1));
            } else {
                ac.setWeight(averageWeight);
            }
            ac.setOverallRating(defOverallRating);
            assessmentCompetenceDs.addItem(ac);
        }
    }

    private void clearGoal(AssessmentParticipant ap) {
        for (UUID id : assessmentGoalDs.getItemIds()) {
            if (ap.equals(assessmentGoalDs.getItem(id).getParticipantAssessment())) {
                assessmentGoalDs.removeItem(assessmentGoalDs.getItem(id));
            }
        }
    }

    private void clearCompetence(AssessmentParticipant ap) {
        for (UUID id : assessmentCompetenceDs.getItemIds()) {
            if (ap.equals(assessmentCompetenceDs.getItem(id).getParticipantAssessment())) {
                assessmentCompetenceDs.removeItem(assessmentCompetenceDs.getItem(id));
            }
        }
    }

    private List<CompetenceGroup> getPositionCompetenceGroup() {
        List<CompetenceGroup> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("personGroupId", assessmentDs.getItem().getEmployeePersonGroup().getId());
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

    private List<AssignedGoal> getAssignedGoals() {
        LoadContext<AssignedGoal> loadContext = LoadContext.create(AssignedGoal.class);
        loadContext.setQuery(LoadContext.createQuery("select e from tsadv$AssignedGoal e " +
                "where e.personGroup.id = :personGroupId" +
                "  and e.performancePlan.id = :performancePlanId" +
                "  and e.goal.deleteTs is null")
                .setParameter("personGroupId", assessmentDs.getItem().getEmployeePersonGroup().getId())
                .setParameter("performancePlanId", assessmentDs.getItem().getTemplate().getPerformancePlan().getId()))
                .setView("assignedGoal.assess");
        return dataManager.loadList(loadContext);
    }
}