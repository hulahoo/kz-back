package kz.uco.tsadv.web.modules.selfservice;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.entity.KeyValueEntity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.entity.dictionary.DicCurrency;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.components.MyTeamComponent;
import kz.uco.tsadv.config.PositionStructureConfig;
import kz.uco.tsadv.entity.AssignmentRequest;
import kz.uco.tsadv.entity.AssignmentSalaryRequest;
import kz.uco.tsadv.entity.MyTeamNew;
import kz.uco.tsadv.entity.tb.TemporaryTranslationRequest;
import kz.uco.tsadv.exceptions.ItemNotFoundException;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.enums.GrossNet;
import kz.uco.tsadv.modules.personal.enums.SalaryType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.personal.model.SalaryRequest;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.service.HierarchyService;
import kz.uco.tsadv.service.MyTeamService;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.*;

public class AssignmentSalaryChange extends AbstractLookup {

    @Inject
    protected HierarchicalDatasource<MyTeamNew, UUID> teamDs;
    @Inject
    protected CommonService commonService;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    protected UserSession userSession;
    @Inject
    private Metadata metadata;
    @Inject
    private TabSheet tabSheet;
    @Inject
    private CollectionDatasource<KeyValueEntity, Object> requestDs;
    @Inject
    private DataManager dataManager;
    @Inject
    private TreeTable<MyTeamNew> assignmentsTable;
    @Inject
    private PositionStructureConfig positionStructureConfig;
    @Inject
    private UserSessionSource userSessionSource;
    @Inject
    private HierarchyService hierarchyService;
    @Inject
    private TextField<String> searchTextField;
    @Inject
    private MyTeamService myTeamService;
    @Inject
    protected MyTeamComponent myTeamComponent;

    protected String sessionLanguage;
    protected UUID positionStructureId;
    protected UUID idPositionGroup;
    protected String searchText;
    protected String pathNumber;
    protected UUID findPersonGroupId;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (userSession.getAttribute(StaticVariable.POSITION_GROUP_ID) == null ||
                hierarchyService.isParent(userSession.getAttribute(StaticVariable.POSITION_GROUP_ID), null)) {
            throw new ItemNotFoundException(messages.getMainMessage("noSubordinateEmployees"));
        }

        sessionLanguage = userSessionSource.getLocale().getLanguage();
        positionStructureId = positionStructureConfig.getPositionStructureId();

        idPositionGroup = userSession.getAttribute(StaticVariable.POSITION_GROUP_ID);
        if (idPositionGroup == null) return;

        myTeamComponent.addChildren(teamDs, idPositionGroup, null);
    }

    @Override
    public void ready() {
        super.ready();

        tabSheet.addSelectedTabChangeListener(event -> {
            if ("request".equalsIgnoreCase(event.getSelectedTab().getName())) {
                requestDs.refresh();
            }
        });

        searchTextField.addTextChangeListener(e -> ((Button) getComponentNN("searchBtn"))
                .setIcon(StringUtils.isNotBlank(e.getText()) && e.getText().equals(searchText)
                        ? "font-icon:ANGLE_DOUBLE_RIGHT" : "font-icon:SEARCH"));

        searchTextField.addEnterPressListener(e -> searchBtn());

        assignmentsTable.unwrap(com.vaadin.ui.TreeGrid.class).addExpandListener(event -> myTeamComponent.onExpand(event, teamDs));
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

    public void onSalaryRequestCreate() {
        SalaryRequest salaryRequest = metadata.create(SalaryRequest.class);
        PersonGroupExt personGroupExt = getCurrentPersonGroup();
        if (personGroupExt != null) {
            salaryRequest.setAssignmentGroup(dataManager.reload(employeeService.getAssignmentGroupByPersonGroup(personGroupExt), "assignmentGroup.view"));
        }
        openEditor(salaryRequest, WindowManager.OpenType.THIS_TAB);
    }

    public void onAssignmentRequestCreate(Component source) {
        AssignmentRequest assignmentRequest = metadata.create(AssignmentRequest.class);
        assignmentRequest.setDateFrom(CommonUtils.getSystemDate());
        assignmentRequest.setPersonGroup(getCurrentPersonGroup());
        DicRequestStatus status = commonService.getEntity(DicRequestStatus.class, "DRAFT");
        if (status == null) {
            showNotification("Request status with code 'DRAFT' not found!");
            return;
        }
        assignmentRequest.setStatus(status);
        openEditor("tsadv$AssignmentRequest.edit", assignmentRequest, WindowManager.OpenType.THIS_TAB);
    }

    public void openRequest(Entity item, String columnId) {
        if (item.getValue("name") == null) {
            return;
        }
        switch ((String) Objects.requireNonNull(item.getValue("name"))) {
            case "assignment": {
                AssignmentRequest assignmentRequest = commonService.getEntity(AssignmentRequest.class,
                        "select e from tsadv$AssignmentRequest e where e.requestNumber = :num"
                        , ParamsMap.of("num", item.getValue("requestNumber")), "assignmentRequest-view");
                openEditor(assignmentRequest, WindowManager.OpenType.THIS_TAB);
                break;
            }
            case "assignmentSalary": {
                AssignmentSalaryRequest assignmentSalaryRequest = commonService.getEntity(AssignmentSalaryRequest.class,
                        "select e from tsadv$AssignmentSalaryRequest e where e.requestNumber = :num "
                        , ParamsMap.of("num", item.getValue("requestNumber")), "assignmentSalaryRequest-view");
                openEditor(assignmentSalaryRequest, WindowManager.OpenType.THIS_TAB);
                break;
            }
            case "salaryRequest": {
                SalaryRequest salaryRequest = commonService.getEntity(SalaryRequest.class,
                        "select e from tsadv$SalaryRequest e where e.requestNumber = :num "
                        , ParamsMap.of("num", item.getValue("requestNumber")), "salary-request.view");
                openEditor(salaryRequest, WindowManager.OpenType.THIS_TAB);
                break;
            }
            case "temporaryTranslation": {
                TemporaryTranslationRequest translationRequest = commonService.getEntity(TemporaryTranslationRequest.class,
                        "select e from tsadv$TemporaryTranslationRequest e where e.requestNumber = :num "
                        , ParamsMap.of("num", item.getValue("requestNumber")), "temporaryTranslationRequest-view");
                openEditor(translationRequest, WindowManager.OpenType.THIS_TAB);
            }
        }
    }

    public Component getFullName(Entity entity) {
        UUID personGroupId = entity.getValue("personGroupId");
        Date date = entity.getValue("dateFrom");
        PersonExt personExt = commonService.getEntity(PersonExt.class, " select e from base$PersonExt e " +
                        "   where e.group.id = :groupId and :date between e.startDate and e.endDate ",
                ParamsMap.of("groupId", personGroupId, "date", date), View.LOCAL);
        Label label = componentsFactory.createComponent(Label.class);
        label.setValue(personExt != null ? personExt.getFullName() : "");
        return label;
    }

    public Component getName(Entity entity) {
        Label label = componentsFactory.createComponent(Label.class);
        String name = entity.getValue("name");
        if (name != null) {
            label.setValue(getMessage(name));
        }
        return label;
    }

    public void onAssignmentSalaryRequestCreate() {

        AssignmentSalaryRequest assignmentSalaryRequest = metadata.create(AssignmentSalaryRequest.class);
        assignmentSalaryRequest.setDateFrom(CommonUtils.getSystemDate());
        assignmentSalaryRequest.setPersonGroup(getCurrentPersonGroup());
        assignmentSalaryRequest.setCurrency(commonService.getEntity(DicCurrency.class, ("KZT")));
        assignmentSalaryRequest.setNetGross(GrossNet.GROSS);
        assignmentSalaryRequest.setType(SalaryType.monthlyRate);

        DicRequestStatus status = commonService.getEntity(DicRequestStatus.class, "DRAFT");
        if (status == null) {
            showNotification("Request status with code 'DRAFT' not found!");
            return;
        }
        assignmentSalaryRequest.setStatus(status);
        openEditor(assignmentSalaryRequest, WindowManager.OpenType.THIS_TAB);
    }

    protected PersonGroupExt getCurrentPersonGroup() {
        MyTeamNew myTeam = teamDs.getItem();
        if (myTeam != null && myTeam.getPersonGroupId() != null) {
            PersonGroupExt personGroupExt = metadata.create(PersonGroupExt.class);
            personGroupExt.setId(myTeam.getPersonGroupId());
            return dataManager.reload(personGroupExt, "personGroup.browse");
        }
        return null;
    }

    public void onTemporaryTranslationRequestCreate() {
        TemporaryTranslationRequest translationRequest = metadata.create(TemporaryTranslationRequest.class);
        translationRequest.setStartDate(CommonUtils.getSystemDate());
        translationRequest.setPersonGroup(getCurrentPersonGroup());
        DicRequestStatus status = commonService.getEntity(DicRequestStatus.class, "DRAFT");
        if (status == null) {
            showNotification("Request status with code 'DRAFT' not found!");
            return;
        }
        translationRequest.setStatus(status);
        openEditor(translationRequest, WindowManager.OpenType.THIS_TAB);
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
            pathNumber = null;
            findPersonGroupId = null;
        }

        searchText = searchTextField.getValue();

        List<Object[]> objectList = myTeamService.searchMyTeam(positionStructureId, idPositionGroup, searchText, pathNumber, findPersonGroupId, true);

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

        return foundPersonGroupId != null ? findMyTeamByPosPersonId(foundPosGroupId, foundPersonGroupId) : findMyTeamByPosId(foundPosGroupId);
    }

    protected MyTeamNew findMyTeamByPosPersonId(UUID positionGroupId, UUID personGroupId) {
        return teamDs.getItems().stream().filter(myTeamNew -> positionGroupId.equals(myTeamNew.getPositionGroupId())
                && personGroupId.equals(myTeamNew.getPersonGroupId()))
                .findFirst().orElse(null);
    }

    protected MyTeamNew findMyTeamByPosId(UUID positionGroupId) {
        return teamDs.getItems().stream().filter(myTeamNew -> positionGroupId.equals(myTeamNew.getPositionGroupId())).findFirst().orElse(null);
    }
}