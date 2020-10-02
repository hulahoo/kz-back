package kz.uco.tsadv.web.modules.personal.assignment;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.components.TreeTable;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.gui.ReportGuiManager;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.components.MyTeamComponent;
import kz.uco.tsadv.config.PositionStructureConfig;
import kz.uco.tsadv.entity.MyTeamNew;
import kz.uco.tsadv.exceptions.ItemNotFoundException;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicLanguage;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.service.HierarchyService;
import kz.uco.tsadv.service.MyTeamService;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.*;

public class AssignmentMyTeamBrowse extends AbstractLookup {

    //Visual components
    @Inject
    protected TreeTable<MyTeamNew> assignmentsTable;

    //Datasources
    @Inject
    protected HierarchicalDatasource<MyTeamNew, UUID> teamDs;

    //Service objects
    @Inject
    protected HierarchyService hierarchyService;
    @Inject
    protected CommonService commonService;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected PositionStructureConfig positionStructureConfig;
    @Inject
    protected UserSession userSession;
    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    private TextField<String> searchTextField;
    @Inject
    private MyTeamService myTeamService;
    @Inject
    private MyTeamComponent myTeamComponent;

    protected String searchText;
    protected String pathNumber;
    protected UUID findPersonGroupId;

    protected AssignmentExt currentAssignment;
    protected PersonGroupExt currentPersonGroup;
    protected Date currentDate;
    protected String sessionLanguage;
    protected UUID positionStructureId;
    protected PositionGroupExt currentPersonPositionGroup;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (userSession.getAttribute(StaticVariable.POSITION_GROUP_ID) == null ||
                hierarchyService.isParent(userSession.getAttribute(StaticVariable.POSITION_GROUP_ID), null)) {
            throw new ItemNotFoundException(messages.getMainMessage("noSubordinateEmployees"));
        }

        teamDs.setAllowCommit(false);
        currentDate = CommonUtils.getSystemDate();
        sessionLanguage = userSessionSource.getLocale().getLanguage();
        currentPersonGroup = employeeService.getPersonGroupByUserId(userSession.getUser().getId());
        positionStructureId = positionStructureConfig.getPositionStructureId();

        if (currentPersonGroup != null) {
            String queryString = "SELECT e.positionGroup from base$AssignmentExt e\n" +
                    "             WHERE :currentDate between e.startDate and e.endDate\n" +
                    "             AND e.primaryFlag = true\n" +
                    "             AND e.personGroup.id = :currentPersonGroupId";
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("currentPersonGroupId", currentPersonGroup.getId());
            queryParams.put("currentDate", currentDate);
            currentPersonPositionGroup = commonService.getEntity(PositionGroupExt.class, queryString, queryParams, "positionGroup.browse");
        }

        if (currentPersonPositionGroup != null) {
            myTeamComponent.addChildren(teamDs, currentPersonPositionGroup.getId(), null);
        }
    }

    @Override
    public void ready() {
        super.ready();

        assignmentsTable.unwrap(com.vaadin.v7.ui.TreeTable.class).addExpandListener(event -> myTeamComponent.onExpand(event, teamDs));
        searchTextField.addEnterPressListener(e -> searchBtn());
        searchTextField.addTextChangeListener(e -> ((Button) getComponentNN("searchBtn"))
                .setIcon(StringUtils.isNotBlank(e.getText()) && e.getText().equals(searchText)
                        ? "font-icon:ANGLE_DOUBLE_RIGHT" : "font-icon:SEARCH"));

    }

    public void openTeamMemberView(MyTeamNew argument, String name) {
        String queryString = "SELECT e from base$PersonExt e\n" +
                "             WHERE e.group.id = :personGroupId" +
                "             AND :currentDate between e.startDate and e.endDate";
        Map<String, Object> params = new HashMap<>();
        params.put("personGroupId", argument.getPersonGroupId());
        params.put("currentDate", CommonUtils.getSystemDate());
        PersonExt person = commonService.getEntity(PersonExt.class, queryString, params, "person-view");

        if (person != null) {
            if (argument.getLinkEnabled()) {
                openEditor("base$TeamMember", person, WindowManager.OpenType.THIS_TAB);
            }
        }
    }

    public void createReport() {
        String langCode = sessionLanguage.toUpperCase();
        report(getTemplateCode(langCode), langCode);
    }

    private String getTemplateCode(String langCode) {
        return (langCode.equals("EN") ? langCode : "RU");
    }

    public void createReportWithoutSalary() {
        String langCode = sessionLanguage.toUpperCase();
        report(getTemplateCode(langCode) + "_NS", langCode);
    }

    protected void report(String templateCode, String langCode) {
        Report report = commonService.getEntity(Report.class,
                "select r from report$Report r where r.code = :code",
                ParamsMap.of("code", "KCHR-629"),
                "report.edit");

        if (report != null && currentPersonPositionGroup != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("pDate", CommonUtils.getSystemDate());
            params.put("pPos", currentPersonPositionGroup);
            params.put("pLanguage", commonService.getEntity(DicLanguage.class, langCode));

            AppBeans.get(ReportGuiManager.class).printReport(report, params, templateCode, null);
        } else {
            showNotification(getMessage("unableToCreateReport"), NotificationType.TRAY);
        }
    }


    public void searchBtn() {
        MyTeamNew scrollTo = search();
        if (scrollTo != null) {
            ((Button) getComponentNN("searchBtn")).setIcon("font-icon:ANGLE_DOUBLE_RIGHT");
            assignmentsTable.scrollTo(scrollTo);
            assignmentsTable.setSelected(scrollTo);
        } else if (StringUtils.isNotBlank(searchTextField.getValue())) {
            showNotification(String.format(messages.getMainMessage("search.not.found"), searchText));
        }
    }

    protected MyTeamNew search() {
        if (StringUtils.isBlank(searchTextField.getValue())) return null;
        if (!searchTextField.getValue().equals(searchText)) {
            findPersonGroupId = null;
            pathNumber = null;
        }

        searchText = searchTextField.getValue();

        List<Object[]> objectList = myTeamService.searchMyTeam(positionStructureId, currentPersonPositionGroup.getId(), searchText, pathNumber, findPersonGroupId, true);

        if (objectList.isEmpty()) {
            if (pathNumber != null) {
                pathNumber = null;
                findPersonGroupId = null;
                showNotification(String.format(messages.getMainMessage("end.of.list"), searchText));
                return search();
            }
            return null;
        }
        Object[] objects = objectList.get(0);

        MyTeamNew team;
        pathNumber = (String) objects[0];
        String[] path = ((String) objects[1]).split("\\*");
        UUID foundPosGroupId = (UUID) objects[2];
        UUID foundPersonGroupId = (UUID) objects[3];
        findPersonGroupId = foundPersonGroupId;
        if (foundPosGroupId == null) return null;

        assignmentsTable.collapseAll();

        for (int i = 1; i < path.length - 1; i++) {
            team = findMyTeamByPosId(UUID.fromString(path[i]));
            assignmentsTable.expand(team.getId());
        }

        return foundPersonGroupId == null ? findMyTeamByPosId(foundPosGroupId) : findMyTeamByPosPersonId(foundPosGroupId, foundPersonGroupId);
    }

    protected MyTeamNew findMyTeamByPosPersonId(UUID positionGroupId, UUID personGroupId) {
        return teamDs.getItems().stream().filter(myTeamNew -> positionGroupId.equals(myTeamNew.getPositionGroupId())
                && personGroupId.equals(myTeamNew.getPersonGroupId()))
                .findFirst().orElse(null);
    }

    protected MyTeamNew findMyTeamByPosId(UUID positionGroupId) {
        return teamDs.getItems().stream().filter(myTeamNew -> positionGroupId.equals(myTeamNew.getPositionGroupId())).findFirst().orElse(null);
    }

    public void reportStaffing() {
        AppBeans.get(ReportGuiManager.class).printReport(commonService.getEntity(Report.class, "KCHR-1152"),
                ParamsMap.of("pPersonGroup", userSession.getAttribute(StaticVariable.USER_PERSON_GROUP_ID)));
    }
}