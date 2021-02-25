package kz.uco.tsadv.web.screens;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import com.vaadin.event.ExpandEvent;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.components.MyTeamComponent;
import kz.uco.tsadv.config.PositionStructureConfig;
import kz.uco.tsadv.entity.MyTeamNew;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsenceType;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.modules.timesheet.model.StandardSchedule;
import kz.uco.tsadv.service.EmployeeNumberService;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.service.MyTeamService;
import kz.uco.tsadv.web.screens.absenceforrecall.AbsenceForRecallEdit;
import kz.uco.tsadv.web.screens.absencerequest.AbsenceRvdRequestEdit;
import kz.uco.tsadv.web.screens.changeabsencedaysrequest.ChangeAbsenceDaysRequestEdit;
import kz.uco.tsadv.web.screens.scheduleoffsetsrequest.ScheduleOffsetsRequestSsMyTeamEdit;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.stream.Collectors;

public class SsStructurePerson extends AbstractWindow {
    @Inject
    protected HierarchicalDatasource<MyTeamNew, UUID> teamDs;
    @Inject
    protected Tree<MyTeamNew> tree;
    @Inject
    protected MyTeamComponent myTeamComponent;
    @Inject
    protected Button searchButton;
    @Inject
    protected TextField<String> searchField;
    @Inject
    protected PositionStructureConfig positionStructureConfig;
    @Inject
    protected Datasource<PersonExt> personExtDs;
    @Inject
    protected CommonService commonService;
    @Inject
    protected MyTeamService myTeamService;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected LinkButton firstNameLink;
    @Inject
    protected LinkButton middleNameLink;
    @Inject
    protected LinkButton lastNameLink;

    protected boolean isEng;
    protected String pathParent;
    protected String vacantPosition;

    protected UUID positionStructureId;
    protected PersonGroupExt currentPersonGroup;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected UserSession userSession;
    protected PositionGroupExt currentPersonPositionGroup;
    protected Date currentDate;
    @Inject
    protected CollectionDatasource<Absence, UUID> absencesDs;
    @Inject
    protected CollectionDatasource<AbsenceRvdRequest, UUID> absenceRvdRequestsDs;
    @Inject
    protected Metadata metadata;
    @Inject
    protected EmployeeNumberService employeeNumberService;
    @Inject
    protected ScreenBuilders screenBuilders;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected CollectionDatasource<ScheduleOffsetsRequest, UUID> scheduleOffsetsRequestsDs;
    @Inject
    protected CollectionDatasource<Absence, UUID> absenceDs;
    @Inject
    protected Table<Absence> absenceTable1;
    @Named("absenceTable1.recallCreate")
    protected Action absenceTable1RecallCreate;
    @Named("absenceTable1.changeAbsenceDaysCreate")
    protected Action absenceTable1ChangeAbsenceDaysCreate;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        currentDate = CommonUtils.getSystemDate();
        positionStructureId = positionStructureConfig.getPositionStructureId();
        isEng = AppBeans.get(UserSession.class).getLocale().getLanguage().equals("en");
        vacantPosition = messages.getMessage("kz.uco.tsadv.web.modules.personal.assignment", "vacantPosition");

        personExtDs.addItemChangeListener(e -> {
            PersonExt personExt = e.getItem();
            firstNameLink.setCaption(getValue(personExt, "firstName", isEng));
            middleNameLink.setCaption(getValue(personExt, "middleName", isEng));
            lastNameLink.setCaption(getValue(personExt, "lastName", isEng));
        });

        tree.setIconProvider(entity -> "font-icon:USER");
        tree.setStyleName("b-tree");
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
            initTeamDs();
        }

        tree.collapseTree();

        absenceTable1.addSelectionListener(absenceSelectionEvent -> {
            Set<Absence> absence = absenceSelectionEvent.getSelected();
            if (absence.size() > 0 && absence.iterator().next().getType() != null
                    && absence.iterator().next().getType().getCode().equals("ANNUAL")) {
                absenceTable1RecallCreate.setEnabled(true);
                absenceTable1ChangeAbsenceDaysCreate.setEnabled(true);
            } else {
                absenceTable1RecallCreate.setEnabled(false);
                absenceTable1ChangeAbsenceDaysCreate.setEnabled(false);
            }
        });
    }

    protected String getValue(PersonExt personExt, String property, boolean isEng) {
        String value = personExt != null ?
                personExt.getValue(property + (isEng ? "Latin" : ""))
                : null;
        if (isEng && personExt != null && StringUtils.isBlank(value))
            return getValue(personExt, property, false);
        return value;
    }

    @Override
    public void ready() {
        super.ready();
        tree.unwrap(com.vaadin.ui.Tree.class).addExpandListener(this::itemExpand);
//        tree.expandTree();
        searchField.addEnterPressListener(e -> searchBtn());
        teamDs.addItemChangeListener(this::itemChangeListener);
    }

    public void itemExpand(ExpandEvent<MyTeamNew> event) {
        if (pathParent != null) return;
        com.vaadin.v7.ui.Tree.ExpandEvent expandEvent = new com.vaadin.v7.ui.Tree.ExpandEvent(event.getComponent(), event.getExpandedItem().getId());
        myTeamComponent.onExpand(expandEvent, teamDs);
    }

    protected void itemChangeListener(Datasource.ItemChangeEvent<MyTeamNew> event) {
        PersonExt person = getPerson(event.getItem() != null ? event.getItem().getPersonGroupId() : null);
        personExtDs.setItem(person);
        refreshDss(person);

    }

    private void refreshDss(PersonExt person) {
        Map<String, Object> params = new HashMap<>();
        if (person != null && person.getGroup() != null) {
            params.put("personGroupId", person.getGroup().getId());
            absencesDs.refresh(params);
            absenceRvdRequestsDs.refresh(params);
            scheduleOffsetsRequestsDs.refresh(params);
        } else {
            absencesDs.clear();
            absenceRvdRequestsDs.clear();
            scheduleOffsetsRequestsDs.clear();
        }
    }

    protected PersonExt getPerson(UUID personGroupId) {
        if (personGroupId == null) return null;
        return commonService.getEntity(PersonExt.class,
                "select e from base$PersonExt e " +
                        " where e.group.id = :personGroupId and :sysDate between e.startDate and e.endDate",
                ParamsMap.of("personGroupId", personGroupId,
                        "sysDate", CommonUtils.getSystemDate()), "person-edit");
    }

    public void searchBtn() {
        teamDs.clear();
        if (StringUtils.isBlank(searchField.getValue())) initTeamDs();
        else search();
    }

    protected void initTeamDs() {
        pathParent = null;
        myTeamComponent.addChildren(teamDs, currentPersonPositionGroup.getId(), null);
//        myTeamComponent.addChildren(teamDs, null, null);
    }

    protected void search() {
        String searchFieldValue = searchField.getValue();
        List<Object[]> objectList = myTeamService.searchMyTeam(positionStructureId, currentPersonPositionGroup.getId(),
                searchFieldValue, null, null, false);

        if (objectList.isEmpty()) {
            initTeamDs();
            showNotification(String.format(messages.getMainMessage("search.not.found"), searchFieldValue));
            return;
        }

        for (Object[] objects : objectList) {
            pathParent = (String) objects[1];
            String[] path = (pathParent).split("\\*");
            UUID foundPosGroupId = (UUID) objects[2];
            UUID foundPersonGroupId = (UUID) objects[3];

            for (int i = 0; i < path.length; i++) {
                UUID posId = UUID.fromString(path[i]);
                if (!contain(posId)) {
                    UUID parentId = i == 0 ? null : UUID.fromString(path[i - 1]);
                    UUID personGroupId = posId.equals(foundPosGroupId) ? foundPersonGroupId : null;
                    addItems(posId, parentId, personGroupId);
                }
            }
            List<UUID> childIdList = myTeamService.getChildPositionIdList(foundPosGroupId, positionStructureId);
            if (!CollectionUtils.isEmpty(childIdList)) {
                childIdList.forEach(child -> addItems(child, foundPosGroupId, null));
            }
        }
        tree.expandTree();
        pathParent = null;
    }

    protected void addItems(@Nonnull UUID posId, @Nullable UUID parentPosId, @Nullable UUID personGroupId) {
        List<MyTeamNew> parentList = teamDs.getItems().stream()
                .filter(myTeamNew -> Objects.equals(myTeamNew.getPositionGroupId(), parentPosId))
                .collect(Collectors.toList());

        List<MyTeamNew> list = myTeamService.getMyTeamInPosition(posId, positionStructureId)
                .stream().map(objects -> myTeamService.parseMyTeamNewObject(objects, vacantPosition))
                .collect(Collectors.toList());

        for (MyTeamNew myTeamNew : list) {
            if (personGroupId == null
                    || personGroupId.equals(myTeamNew.getPersonGroupId())) {
                if (parentList.isEmpty()) {
                    myTeamNew.setParent(null);
                    teamDs.addItem(myTeamNew);
                } else {
                    for (MyTeamNew teamNew : parentList) {
                        MyTeamNew copy = copy(myTeamNew);
                        copy.setParent(teamNew);
                        teamDs.addItem(copy);
                        /*if (personGroupId != null && Boolean.TRUE.equals(copy.getHasChild())) {
                            teamDs.addItem(myTeamService.createFakeChild(copy));
                        }*/
                    }
                }
            }
        }
    }

    protected MyTeamNew copy(MyTeamNew myTeamNew) {
        MyTeamNew copy = new MyTeamNew();
        copy.setFullName(myTeamNew.getFullName());
        copy.setPositionGroupId(myTeamNew.getPositionGroupId());
        copy.setPersonGroupId(myTeamNew.getPersonGroupId());
        copy.setHasChild(myTeamNew.getHasChild());
        return copy;
    }

    protected boolean contain(UUID posId) {
        for (MyTeamNew item : teamDs.getItems()) {
            if (posId.equals(item.getPositionGroupId())) return true;
        }
        return false;
    }

    public void openPersonCard() {
        openEditor("person-card", personExtDs.getItem().getGroup(), WindowManager.OpenType.THIS_TAB);
    }

    protected AssignmentExt getAssignment(UUID personGroupId) {
        return commonService.getEntity(AssignmentExt.class,
                "select e from base$AssignmentExt e " +
                        "where :sysDate between e.startDate and e.endDate " +
                        "  and e.primaryFlag = true " +
                        "and e.personGroup.id = :personGroupId",
                ParamsMap.of("personGroupId", personGroupId,
                        "sysDate", CommonUtils.getSystemDate()),
                "assignment.card");
    }


    public void createAbsenceRequest() {
        if (personExtDs.getItem() != null) {
            AbsenceRvdRequest absenceRvdRequest = metadata.create(AbsenceRvdRequest.class);
            PersonGroupExt personGroupExt = dataManager.reload(personExtDs.getItem().getGroup(), "personGroup.person.info");
            absenceRvdRequest.setPersonGroup(personGroupExt);
            DicAbsenceType workOnWeekend = commonService.getEntity(DicAbsenceType.class, "WORK_ON_WEEKEND");
            DicRequestStatus status = commonService.getEntity(DicRequestStatus.class, "DRAFT");
            absenceRvdRequest.setType(workOnWeekend);
            absenceRvdRequest.setStatus(status);
            absenceRvdRequest.setRequestDate(CommonUtils.getSystemDate());
            absenceRvdRequest.setRequestNumber(employeeNumberService.generateNextRequestNumber());
            absenceRvdRequest.setCompencation(true);
            absenceRvdRequest.setVacationDay(false);
            screenBuilders.editor(AbsenceRvdRequest.class, this)
                    .withScreenClass(AbsenceRvdRequestEdit.class).newEntity(absenceRvdRequest)
                    .withAfterCloseListener(absenceRvdRequestEditAfterScreenCloseEvent -> {
                        refreshDss(personExtDs.getItem());
                    }).build().show();
        }
    }

    public void createScheduleOffsetRequest() {
        if (personExtDs.getItem() != null) {
            ScheduleOffsetsRequest request = metadata.create(ScheduleOffsetsRequest.class);
            PersonGroupExt personGroupExt = dataManager.reload(personExtDs.getItem().getGroup(), "personGroup.person.info");
            request.setPersonGroup(personGroupExt);
            DicRequestStatus status = commonService.getEntity(DicRequestStatus.class, "DRAFT");
            request.setStatus(status);
            StandardSchedule standardSchedule = commonService.getEntities(StandardSchedule.class,
                    "select e.schedule from tsadv$AssignmentSchedule e " +
                            " where current_date between e.startDate and e.endDate and e.assignmentGroup.id in " +
                            "(select a.group.id from base$AssignmentExt a where current_date between a.startDate and a.endDate " +
                            "and a.personGroup.id = :personGroupId) ",
                    ParamsMap.of("personGroupId", personGroupExt.getId()),
                    "standardSchedule-for-my-team").stream().findFirst().orElse(null);
            request.setCurrentSchedule(standardSchedule);
            request.setRequestDate(CommonUtils.getSystemDate());
            request.setRequestNumber(employeeNumberService.generateNextRequestNumber());
            screenBuilders.editor(ScheduleOffsetsRequest.class, this)
                    .withScreenClass(ScheduleOffsetsRequestSsMyTeamEdit.class).newEntity(request)
                    .withAfterCloseListener(offsetsRequestSsMyTeamEditAfterScreenCloseEvent -> {
                        refreshDss(personExtDs.getItem());
                    }).build().show();
        }
    }

    public void createRequestForRecall() {
        AbsenceForRecall absenceForRecall = metadata.create(AbsenceForRecall.class);
        absenceForRecall.setEmployee(personExtDs.getItem().getGroup());
        absenceForRecall.setLeaveOtherTime(true);
        absenceForRecall.setVacation(absenceTable1.getSingleSelected());
        absenceForRecall.setAbsenceType(absenceTable1.getSingleSelected().getType());
        screenBuilders.editor(AbsenceForRecall.class, this)
                .withScreenClass(AbsenceForRecallEdit.class)
                .newEntity(absenceForRecall)
                .build()
                .show();
    }

    public void changeAbsenceDaysCreate() {
        ChangeAbsenceDaysRequest changeAbsenceDaysRequest = metadata.create(ChangeAbsenceDaysRequest.class);
        changeAbsenceDaysRequest.setEmployee(personExtDs.getItem().getGroup());
        changeAbsenceDaysRequest.setVacation(absenceTable1.getSingleSelected());
        changeAbsenceDaysRequest.setScheduleStartDate(absenceTable1.getSingleSelected().getDateFrom());
        changeAbsenceDaysRequest.setScheduleEndDate(absenceTable1.getSingleSelected().getDateTo());
        screenBuilders.editor(ChangeAbsenceDaysRequest.class, this)
                .withScreenClass(ChangeAbsenceDaysRequestEdit.class)
                .newEntity(changeAbsenceDaysRequest)
                .build()
                .show();
    }
}