package kz.uco.tsadv.web.modules.performance.assignedgoal;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.gui.ReportGuiManager;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.common.WebCommonUtils;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.performance.dictionary.DicPriority;
import kz.uco.tsadv.modules.performance.model.AssignedGoal;
import kz.uco.tsadv.modules.performance.model.Goal;
import kz.uco.tsadv.modules.performance.model.PerformancePlan;
import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.web.modules.personal.assignment.Filterframe;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AssignedGoalBrowse extends AbstractLookup {
    private static final String IMAGE_CELL_HEIGHT = "90px";
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private DataManager dataManager;
    @Inject
    private PopupButton popupButton;
    @Inject
    private Button reportButton;
    @Inject
    private CollectionDatasource<PerformancePlan, UUID> performancePlansDs;
    @Inject
    private GroupDatasource<AssignmentExt, UUID> assignmentsDs;
    @Inject
    private GroupTable<AssignmentExt> assignmentsTable;
    private long maxTotal;
    @Inject
    private Filterframe filterFrame;
    @Inject
    private CommonService commonService;
    @Inject
    private CollectionDatasource<AssignedGoal, UUID> assignedGoalsDs;
    @Inject
    private Metadata metadata;
    @Inject
    private UserSession userSession;
    @Inject
    private Button assignGoalsButton;
    @Inject
    private Label planOrganizationGroupIdStr;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        initFilterFrame();
        reportButton.setAction(new BaseAction("report") {
            @Override
            public void actionPerform(Component component) {
                super.actionPerform(component);
                Report report = commonService.emQuerySingleRelult(Report.class, "select e from report$Report e where e.code = 'GOAL_REPORT'", null);
                ReportGuiManager reportGuiManager = AppBeans.get(ReportGuiManager.class);
                Map<String, Object> map = new HashMap<>();
                map.put("plan", performancePlansDs.getItem());
                map.put("sysDate", CommonUtils.getSystemDate());
                map.put("personGroupId", userSession.getAttribute(StaticVariable.USER_PERSON_GROUP_ID));
                map.put("positionGroupId", userSession.getAttribute(StaticVariable.POSITION_GROUP_ID));
                map.put("myTeam", ((OptionsGroup) filterFrame.getComponent("myTeamOptionGroupId")).getValue());
                reportGuiManager.printReport(report, map, null, report.getLocName());
            }
        });
        performancePlansDs.addItemChangeListener(e -> {
            if ((e.getItem() != null &&
                    e.getItem().getAccessibilityStartDate() != null &&
                    e.getItem().getAccessibilityEndDate() != null &&
                    (CommonUtils.getSystemDate().after(e.getItem().getAccessibilityStartDate()) || CommonUtils.getSystemDate().equals(e.getItem().getAccessibilityStartDate())) &&
                    (CommonUtils.getSystemDate().before(e.getItem().getAccessibilityEndDate()) || CommonUtils.getSystemDate().equals(e.getItem().getAccessibilityEndDate()))) ||
                    (e.getItem() != null &&
                            (e.getItem().getAccessibilityStartDate() == null ||
                                    e.getItem().getAccessibilityEndDate() == null)))
                assignGoalsButton.setEnabled(true);
            else assignGoalsButton.setEnabled(false);
        });
        performancePlansDs.refresh();
        if (performancePlansDs.getItems().size() != 0) {
            PerformancePlan performancePlan = performancePlansDs.getItems().stream().findFirst().orElse(null);
            performancePlansDs.setItem(performancePlan);
        }
        for (PerformancePlan pp : performancePlansDs.getItems()) {
            popupButton.addAction(new BaseAction(pp.getPerformancePlanName()) {
                @Override
                public void actionPerform(Component component) {
                    performancePlansDs.setItem(pp);
                    planOrganizationGroupIdStr.setValue(pp.getOrganizations());
                    calculateMaxTotal();
                    assignmentsTable.repaint();
                }
            });
        }
        assignmentsDs.refresh();
        calculateMaxTotal();
    }

    private void initFilterFrame() {
        ((OptionsGroup) filterFrame.getComponent("myTeamOptionGroupId"))
                .addValueChangeListener((e) -> {
                    assignmentsDs.refresh();
                    calculateMaxTotal();
                    assignmentsTable.repaint();
                });
    }

    private void calculateMaxTotal() {
        maxTotal = 0;
        long i;
        for (AssignmentExt a : assignmentsDs.getItems()) {
            i = getGoalsCount("total", a);
            if (maxTotal < i) {
                maxTotal = i;
            }
        }
    }

    @Override
    public void ready() {
        super.ready();
    }

    public Component generatePersonImageCell(AssignmentExt entity) {
        return WebCommonUtils.setImage(entity.getPersonGroup().getPerson().getImage(), null, "90px");
    }

    @Nullable
    private String getPositionStructureInfo(AssignmentExt entity) {
        String positionStructureInfo = null;
        int direct = 0;
        int total = 0;

        LoadContext<AssignmentExt> positionStructureLoadContext = LoadContext.create(AssignmentExt.class)
                .setQuery(
                        LoadContext.createQuery("select e from base$AssignmentExt e " +
                                " where :systemDate between e.startDate and e.endDate " +
                                " and e.positionGroup.id in (select ps.positionGroup.id " +
                                " from tsadv$PositionStructure ps " +
                                " where ps.parentPositionGroup.id = :positionGroupId " +
                                " and :systemDate between ps.startDate and ps.endDate " +
                                " and :systemDate between ps.posStartDate and ps.posEndDate)")
                                .setParameter("positionGroupId", entity.getPositionGroup().getId())
                                .setParameter("systemDate", CommonUtils.getSystemDate())
                );

        direct = dataManager.loadList(positionStructureLoadContext).size();

        positionStructureLoadContext = LoadContext.create(AssignmentExt.class)
                .setQuery(
                        LoadContext.createQuery("select e from base$AssignmentExt e " +
                                " where :systemDate between e.startDate and e.endDate" +
                                " and e.positionGroup.id in (select ps.positionGroup.id " +
                                " from tsadv$PositionStructure ps where ps.positionGroupPath like concat('%',concat(:positionGroupId, '%')) " +
                                " and :systemDate between ps.startDate and ps.endDate" +
                                " and :systemDate between ps.posStartDate and ps.posEndDate)")
                                .setParameter("positionGroupId", entity.getPositionGroup().getId().toString())
                                .setParameter("systemDate", CommonUtils.getSystemDate())
                );
        total = dataManager.loadList(positionStructureLoadContext).size() - 1;

        positionStructureInfo = String.format(getMessage("AssignmentMyTeamBrowse.positionStructureInfo"), direct, total);

        return positionStructureInfo;
    }

    public Component generateAssignmentG1Cell(AssignmentExt entity) {
        CssLayout wrapper = componentsFactory.createComponent(CssLayout.class);
        VBoxLayout vBoxLayout1 = componentsFactory.createComponent(VBoxLayout.class);

        LinkButton personName = componentsFactory.createComponent(LinkButton.class);
        personName.setCaption(entity.getPersonGroup().getPerson().getFioWithEmployeeNumber());
        personName.setStyleName("ss-div-blue");
        personName.setAction(new BaseAction("redirect-card") {
            @Override
            public void actionPerform(Component component) {
                openEditor("person-card", entity.getPersonGroup(), WindowManager.OpenType.THIS_TAB);
            }
        });

        vBoxLayout1.add(personName);

        Label position = componentsFactory.createComponent(Label.class);
        position.setValue(entity.getPositionGroup().getPosition().getPositionName());
        position.setStyleName("ss-div-dimgrey");

        vBoxLayout1.add(position);

        if (entity.getPositionGroup().getPosition().getManagerFlag()) {
            Label positionStructureInfoLabel = componentsFactory.createComponent(Label.class);
            positionStructureInfoLabel.setValue(getPositionStructureInfo(entity));
            positionStructureInfoLabel.setStyleName("ss-icon ss-div-dimgrey");
            positionStructureInfoLabel.setIcon("font-icon:USERS");

            vBoxLayout1.add(positionStructureInfoLabel);
        }

        wrapper.add(vBoxLayout1);
        return wrapper;
    }

    public Component generateAssignmentG2Cell(AssignmentExt entity) {
        VBoxLayout vBoxLayout = componentsFactory.createComponent(VBoxLayout.class);
        HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
        VBoxLayout vBoxLayout1 = componentsFactory.createComponent(VBoxLayout.class);
        VBoxLayout vBoxLayout2 = componentsFactory.createComponent(VBoxLayout.class);
        VBoxLayout vBoxLayout3 = componentsFactory.createComponent(VBoxLayout.class);
        GridLayout gridLayout = componentsFactory.createComponent(GridLayout.class);
        LinkButton totalValue = componentsFactory.createComponent(LinkButton.class);
        Label label1 = componentsFactory.createComponent(Label.class);
        Label label2 = componentsFactory.createComponent(Label.class);
        Label label3 = componentsFactory.createComponent(Label.class);
        Label label4 = componentsFactory.createComponent(Label.class);

        long notStartedCnt = getGoalsCount("notStarted", entity);
        long inProgressCnt = getGoalsCount("inProgress", entity);
        long completedCnt = getGoalsCount("completed", entity);

        vBoxLayout.setSpacing(true);
        hBoxLayout.setSpacing(true);
        vBoxLayout1.addStyleName("background-orange");
        vBoxLayout2.addStyleName("background-blue");
        vBoxLayout3.addStyleName("background-green");
        gridLayout.setRows(1);
        gridLayout.setColumns(3);
        if (maxTotal != 0)
            gridLayout.setWidth(String.valueOf(Double.valueOf(getGoalsCount("total", entity) * 100 / maxTotal).intValue()) + "%");
        else
            gridLayout.setWidth("0%");
        gridLayout.setColumnExpandRatio(0, notStartedCnt);
        gridLayout.setColumnExpandRatio(1, inProgressCnt);
        gridLayout.setColumnExpandRatio(2, completedCnt);
        totalValue.addStyleName("h2");
//        label.setAlignment();
        label2.addStyleName("font-color-white");
        label3.addStyleName("font-color-white");
        label4.addStyleName("font-color-white");
        label2.setAlignment(Alignment.MIDDLE_CENTER);
        label3.setAlignment(Alignment.MIDDLE_CENTER);
        label4.setAlignment(Alignment.MIDDLE_CENTER);

        totalValue.setCaption(String.valueOf(getGoalsCount("total", entity)));
        totalValue.setAction(new BaseAction("my-assigned-goal") {
            @Override
            public void actionPerform(Component component) {
                Map<String, Object> map = new HashMap<>();
                map.put("selectedPersonGroup", entity.getPersonGroup());
                map.put("selectedPerformacePlan", performancePlansDs.getItem());
                AbstractWindow window = openWindow("tsadv$MyAssignedGoal.browse", WindowManager.OpenType.THIS_TAB, map);
                window.addCloseListener(actionId -> {
                    calculateMaxTotal();
                    assignmentsTable.repaint();
                });
            }
        });

        label1.setValue(getMessage("AssignedGoal.browse.total"));
        label2.setValue(notStartedCnt == 0 ? "" : notStartedCnt);
        label3.setValue(inProgressCnt == 0 ? "" : inProgressCnt);
        label4.setValue(completedCnt == 0 ? "" : completedCnt);

        vBoxLayout.add(hBoxLayout);
        vBoxLayout.add(gridLayout);
        hBoxLayout.add(totalValue);
        hBoxLayout.add(label1);
        vBoxLayout1.add(label2);
        vBoxLayout2.add(label3);
        vBoxLayout3.add(label4);
        gridLayout.add(vBoxLayout1, 0, 0);
        gridLayout.add(vBoxLayout2, 1, 0);
        gridLayout.add(vBoxLayout3, 2, 0);
        return vBoxLayout;
    }

    private long getGoalsCount(String calculateMode, AssignmentExt assignment) {
        LoadContext<AssignedGoal> loadContext = LoadContext.create(AssignedGoal.class);
        switch (calculateMode) {
            case "total":
                loadContext.setQuery(LoadContext.createQuery("select e from tsadv$AssignedGoal e " +
                        "where e.personGroupId.id = :currentPersonId" +
                        "  and e.performancePlan.id = :currentPerformancePlanId")
                        .setParameter("currentPersonId", assignment.getPersonGroup().getId())
                        .setParameter("currentPerformancePlanId", performancePlansDs.getItem()));
                break;
            case "notStarted":
                loadContext.setQuery(LoadContext.createQuery("select e from tsadv$AssignedGoal e " +
                        "where e.personGroupId.id = :currentPersonId" +
                        "  and e.performancePlan.id = :currentPerformancePlanId" +
                        "  and e.actualValue = 0")
                        .setParameter("currentPersonId", assignment.getPersonGroup().getId())
                        .setParameter("currentPerformancePlanId", performancePlansDs.getItem()));
                break;
            case "inProgress":
                loadContext.setQuery(LoadContext.createQuery("select e from tsadv$AssignedGoal e " +
                        "where e.personGroupId.id = :currentPersonId" +
                        "  and e.performancePlan.id = :currentPerformancePlanId" +
                        "  and e.actualValue > 0" +
                        "  and e.actualValue < e.targetValue")
                        .setParameter("currentPersonId", assignment.getPersonGroup().getId())
                        .setParameter("currentPerformancePlanId", performancePlansDs.getItem()));
                break;
            case "completed":
                loadContext.setQuery(LoadContext.createQuery("select e from tsadv$AssignedGoal e " +
                        "where e.personGroupId.id = :currentPersonId" +
                        "  and e.performancePlan.id = :currentPerformancePlanId" +
                        "  and e.actualValue = e.targetValue" +
                        "  and e.targetValue <> 0")
                        .setParameter("currentPersonId", assignment.getPersonGroup().getId())
                        .setParameter("currentPerformancePlanId", performancePlansDs.getItem()));
        }
        return dataManager.getCount(loadContext);
    }

    public void onAssignGoalsButtonClick() {
        assignedGoalsDs.refresh();
        Map<Integer, Object> params = new HashMap<>();
        Map<String, Object> params2 = new HashMap<>();
        assignmentsDs.getItems().forEach(assignment -> {
            params.clear();
            params.put(1, CommonUtils.getSystemDate());
            params.put(2, assignment.getPositionGroup().getId());
            List<Object[]> parentGoals = commonService.emNativeQueryResultList("WITH org AS (SELECT " +
                    "               ps4.organization_group_id, " +
                    "               ps4.position_group_path, " +
                    "               ps4.position_group_id, " +
                    "               ps4.lvl " +
                    "             FROM tsadv_position_structure ps4 " +
                    "             WHERE ps4.element_type = 2 " +
                    "                   AND  ?1 BETWEEN ps4.start_date AND ps4.end_date " +
                    "                   AND  ?1 BETWEEN ps4.pos_start_date AND ps4.pos_end_date " +
                    "                   AND ps4.delete_ts IS NULL " +
                    "                   AND ps4.position_group_id = " +
                    "                        ?2) " +
                    "select d.element_type, d.element_id, d.goal_id, d.weight " +
                    "FROM (SELECT foo.goal_id, max(foo.lvl) lvl " +
                    "      FROM (SELECT " +
                    "               CASE WHEN ps.element_type = 1 " +
                    "                 THEN og.id " +
                    "               ELSE pg.id END            goal_id, " +
                    "               ps.lvl " +
                    "             FROM tsadv_position_structure ps " +
                    "               LEFT JOIN BASE_ORGANIZATION o " +
                    "                 ON o.group_id = ps.organization_group_id " +
                    "                    AND ps.element_type = 1 " +
                    "                    AND  ?1 BETWEEN o.start_date AND o.end_date " +
                    "                    AND o.delete_ts IS NULL " +
                    "               LEFT JOIN tsadv_organization_group_goal_link ogl " +
                    "                 ON ogl.organization_group_id = o.group_id " +
                    "                    AND ogl.delete_ts IS NULL " +
                    "               LEFT JOIN tsadv_goal og " +
                    "                 ON og.id = ogl.goal_id " +
                    "                    AND og.delete_ts IS NULL " +
                    "               LEFT JOIN BASE_POSITION p " +
                    "                 ON p.group_id = ps.position_group_id " +
                    "                    AND  ?1 BETWEEN p.start_date AND p.end_date " +
                    "                    AND p.delete_ts IS NULL " +
                    "               LEFT JOIN tsadv_position_group_goal_link pgl " +
                    "                 ON pgl.position_group_id = p.group_id " +
                    "                    AND pgl.delete_ts IS NULL " +
                    "               LEFT JOIN tsadv_goal pg " +
                    "                 ON pg.id = pgl.goal_id " +
                    "                    AND pg.delete_ts IS NULL " +
                    "               JOIN org " +
                    "                 ON 1 = 1 " +
                    "             WHERE ((ps.element_type = 1 AND " +
                    "                      ?1 BETWEEN ps.start_date AND ps.end_date AND " +
                    "                     (SELECT ps1.organization_group_path " +
                    "                      FROM tsadv_position_structure ps1 " +
                    "                      WHERE ps1.element_type = 1 " +
                    "                            AND  ?1 BETWEEN ps1.start_date AND ps1.end_date " +
                    "                            AND ps1.delete_ts IS NULL " +
                    "                            AND ps1.organization_group_id = org.organization_group_id) LIKE " +
                    "                     '%' || ps.organization_group_id || '%' " +
                    "                    ) OR " +
                    "                    (ps.element_type = 2 AND " +
                    "                      ?1 BETWEEN ps.start_date AND ps.end_date AND " +
                    "                      ?1 BETWEEN ps.pos_start_date AND ps.pos_end_date AND " +
                    "                     org.position_group_path LIKE '%' || ps.position_group_id || '%' AND " +
                    "                     pg.id IS NOT NULL AND " +
                    "                     ps.organization_group_id = org.organization_group_id)) AND " +
                    "                   ps.delete_ts IS NULL " +
                    "             UNION ALL " +
                    "             SELECT " +
                    "               jg.id            goal_id, " +
                    "               org.lvl - 0.5           lvl " +
                    "             FROM tsadv_job j " +
                    "               LEFT JOIN tsadv_job_group_goal_link jgl " +
                    "                 ON jgl.job_group_id = j.group_id " +
                    "                    AND jgl.delete_ts IS NULL " +
                    "               LEFT JOIN tsadv_goal jg " +
                    "                 ON jg.id = jgl.goal_id " +
                    "                    AND jg.delete_ts IS NULL " +
                    "               JOIN org " +
                    "                 ON 1 = 1 " +
                    "             WHERE  ?1 BETWEEN j.start_date AND j.end_date " +
                    "                   AND j.delete_ts IS NULL " +
                    "                   AND j.group_id = (SELECT p1.job_group_id " +
                    "                                     FROM BASE_POSITION p1 " +
                    "                                     WHERE p1.group_id = org.position_group_id " +
                    "                                           AND  ?1 BETWEEN p1.start_date AND p1.end_date " +
                    "                                           AND p1.delete_ts IS NULL) " +
                    "           ) foo " +
                    "      GROUP BY foo.goal_id) m " +
                    "JOIN ( " +
                    "             SELECT " +
                    "               ps.element_type                  element_type, " +
                    "               CASE WHEN ps.element_type = 1 " +
                    "                 THEN o.group_id " +
                    "               ELSE p.group_id END         element_id, " +
                    "               CASE WHEN ps.element_type = 1 " +
                    "                 THEN og.goal_name " +
                    "               ELSE pg.goal_name END            goal_name, " +
                    "               CASE WHEN ps.element_type = 1 " +
                    "                 THEN og.id " +
                    "               ELSE pg.id END            goal_id, " +
                    "               coalesce(CASE WHEN ps.element_type = 1 " +
                    "                 THEN ogl.weight " +
                    "                        ELSE pgl.weight END, 0) weight, " +
                    "               ps.lvl " +
                    "             FROM tsadv_position_structure ps " +
                    "               LEFT JOIN BASE_ORGANIZATION o " +
                    "                 ON o.group_id = ps.organization_group_id " +
                    "                    AND ps.element_type = 1 " +
                    "                    AND  ?1 BETWEEN o.start_date AND o.end_date " +
                    "                    AND o.delete_ts IS NULL " +
                    "               LEFT JOIN tsadv_organization_group_goal_link ogl " +
                    "                 ON ogl.organization_group_id = o.group_id " +
                    "                    AND ogl.delete_ts IS NULL " +
                    "               LEFT JOIN tsadv_goal og " +
                    "                 ON og.id = ogl.goal_id " +
                    "                    AND og.delete_ts IS NULL " +
                    "               LEFT JOIN BASE_POSITION p " +
                    "                 ON p.group_id = ps.position_group_id " +
                    "                    AND  ?1 BETWEEN p.start_date AND p.end_date " +
                    "                    AND p.delete_ts IS NULL " +
                    "               LEFT JOIN tsadv_position_group_goal_link pgl " +
                    "                 ON pgl.position_group_id = p.group_id " +
                    "                    AND pgl.delete_ts IS NULL " +
                    "               LEFT JOIN tsadv_goal pg " +
                    "                 ON pg.id = pgl.goal_id " +
                    "                    AND pg.delete_ts IS NULL " +
                    "               JOIN org " +
                    "                 ON 1 = 1 " +
                    "             WHERE ((ps.element_type = 1 AND " +
                    "                      ?1 BETWEEN ps.start_date AND ps.end_date AND " +
                    "                     (SELECT ps1.organization_group_path " +
                    "                      FROM tsadv_position_structure ps1 " +
                    "                      WHERE ps1.element_type = 1 " +
                    "                            AND  ?1 BETWEEN ps1.start_date AND ps1.end_date " +
                    "                            AND ps1.delete_ts IS NULL " +
                    "                            AND ps1.organization_group_id = org.organization_group_id) LIKE " +
                    "                     '%' || ps.organization_group_id || '%' " +
                    "                    ) OR " +
                    "                    (ps.element_type = 2 AND " +
                    "                      ?1 BETWEEN ps.start_date AND ps.end_date AND " +
                    "                      ?1 BETWEEN ps.pos_start_date AND ps.pos_end_date AND " +
                    "                     org.position_group_path LIKE '%' || ps.position_group_id || '%' AND " +
                    "                     pg.id IS NOT NULL AND " +
                    "                     ps.organization_group_id = org.organization_group_id)) AND " +
                    "                   ps.delete_ts IS NULL " +
                    "             UNION ALL " +
                    "             SELECT " +
                    "               3, " +
                    "               j.group_id              element_id, " +
                    "               jg.goal_name            goal_name, " +
                    "               jg.id                   goal_id, " +
                    "               coalesce(jgl.weight, 0) weight, " +
                    "               org.lvl - 0.5           lvl " +
                    "             FROM tsadv_job j " +
                    "               LEFT JOIN tsadv_job_group_goal_link jgl " +
                    "                 ON jgl.job_group_id = j.group_id " +
                    "                    AND jgl.delete_ts IS NULL " +
                    "               LEFT JOIN tsadv_goal jg " +
                    "                 ON jg.id = jgl.goal_id " +
                    "                    AND jg.delete_ts IS NULL " +
                    "               JOIN org " +
                    "                 ON 1 = 1 " +
                    "             WHERE  ?1 BETWEEN j.start_date AND j.end_date " +
                    "                   AND j.delete_ts IS NULL " +
                    "                   AND j.group_id = (SELECT p1.job_group_id " +
                    "                                     FROM BASE_POSITION p1 " +
                    "                                     WHERE p1.group_id = org.position_group_id " +
                    "                                           AND  ?1 BETWEEN p1.start_date AND p1.end_date " +
                    "                                           AND p1.delete_ts IS NULL) " +
                    " " +
                    "      ) d " +
                    "on m.goal_id = d.goal_id " +
                    "AND m.lvl = d.lvl " +
                    "ORDER BY m.lvl", params);
            for (Object[] o : parentGoals) {
                /*params2.clear(); //search by clearQuery
                params2.put("goalId", (UUID) o[2]);
                params2.put("personGroupId", assignment.getPersonGroup().getId());
                params2.put("performancePlanId", performancePlansDs.getItem().getId());
                params2.put("organizationGroupId", (Integer) o[0] == 1 ? o[1] : null);
                params2.put("positionGroupId", (Integer) o[0] == 2 ? o[1] : null);
                params2.put("jobGroupId", (Integer) o[0] == 3 ? o[1] : null);*/
                AssignedGoal agToDelete = assignedGoalsDs.getItems().stream().filter(assignedGoal ->  //search from ds list
                        assignedGoal.getGoal().getId().equals((UUID) o[2]) &&
                                assignedGoal.getPersonGroup().getId().equals(assignment.getPersonGroup().getId()) &&
//                                assignedGoal.getPerformancePlan().equals(performancePlansDs.getItem()) &&
                                (((Integer) o[0] == 1 &&
                                        assignedGoal.getOrganizationGroup() != null &&
                                        assignedGoal.getOrganizationGroup().getId().equals((UUID) o[1])) ||
                                        ((Integer) o[0] == 2 &&
                                                assignedGoal.getPositionGroup() != null &&
                                                assignedGoal.getPositionGroup().getId().equals((UUID) o[1])) ||
                                        ((Integer) o[0] == 3 &&
                                                assignedGoal.getJobGroup() != null &&
                                                assignedGoal.getJobGroup().getId().equals((UUID) o[1])))).findFirst().orElse(null);

                /*commonService.getEntity(AssignedGoal.class, "select e from tsadv$AssignedGoal e " + //search by clearQuery
                        "where e.goal.id = :goalId " +
                        "and e.personGroupId.id = :personGroupId " +
                        "and e.performancePlan.id = :performancePlanId " +
                        "and e.organizationGroup.id = :organizationGroupId " +
                        "and e.positionGroup.id = :positionGroupId " +
                        "and e.jobGroup.id = :jobGroupId", params2, null)*/

                if (agToDelete != null)
                    assignedGoalsDs.removeItem(agToDelete);
                params2.clear();
                params2.put("goalId", (UUID) o[2]);
                params2.put("personGroupId", assignment.getPersonGroup().getId());
                params2.put("performancePlanId", performancePlansDs.getItem().getId());
                /*commonService.getEntity(AssignedGoal.class, "select e from tsadv$AssignedGoal e " + //search by clearQuery
                        "where e.goal.id = :goalId " +
                        "and e.personGroupId.id = :personGroupId " +
                        "and e.performancePlan.id = :performancePlanId", params2, null)*/
                if (assignedGoalsDs.getItems().stream().filter(assignedGoal ->  //search from ds list
                        assignedGoal.getGoal().getId().equals((UUID) o[2]) &&
                                assignedGoal.getPersonGroup().getId().equals(assignment.getPersonGroup().getId())
//                                && assignedGoal.getPerformancePlan().equals(performancePlansDs.getItem())
                )
                        .findFirst().orElse(null) == null) {
                    AssignedGoal ag = metadata.create(AssignedGoal.class);
                    Goal g = commonService.getEntity(Goal.class, (UUID) o[2]);
                    ag.setGoal(g);
                    ag.setPersonGroup(assignment.getPersonGroup());
                    ag.setOrganizationGroup((Integer) o[0] == 1 ? commonService.getEntity(OrganizationGroupExt.class, (UUID) o[1]) : null);
                    ag.setPositionGroup((Integer) o[0] == 2 ? commonService.getEntity(PositionGroupExt.class, (UUID) o[1]) : null);
                    ag.setJobGroup((Integer) o[0] == 3 ? commonService.getEntity(JobGroup.class, (UUID) o[1]) : null);
                    ag.setTargetValue(0);
                    ag.setActualValue(0);
                    ag.setSuccessCritetia(g.getSuccessCriteria());
                    ag.setAssignedByPersonGroup(userSession.getAttribute(StaticVariable.USER_PERSON_GROUP));
                    ag.setStartDate(performancePlansDs.getItem().getStartDate());
                    ag.setEndDate(performancePlansDs.getItem().getEndDate());
                    ag.setWeight((Integer) o[3]);
                    ag.setPriority(commonService.getEntity(DicPriority.class, "HIGH"));
//                    ag.setPerformancePlan(performancePlansDs.getItem());
                    assignedGoalsDs.addItem(ag);
                }
            }
        });
        assignedGoalsDs.commit();
        calculateMaxTotal();
        assignmentsTable.repaint();
        showNotification(getMessage("assignParentGoalsSuccessful"), NotificationType.TRAY);
    }
}