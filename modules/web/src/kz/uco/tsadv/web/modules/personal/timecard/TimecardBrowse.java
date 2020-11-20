package kz.uco.tsadv.web.modules.personal.timecard;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.widgets.CubaHorizontalSplitPanel;
import com.vaadin.ui.AbstractSplitPanel;
import kz.uco.base.entity.shared.Hierarchy;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.datasource.TimecardDatasource;
import kz.uco.tsadv.datasource.TimecardHierarchyDatasource;
import kz.uco.tsadv.entity.TimecardHierarchy;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.Absence;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.timesheet.model.Timecard;
import kz.uco.tsadv.service.DatesService;
import kz.uco.tsadv.service.OrganizationService;
import kz.uco.tsadv.service.TimecardHierarchyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Calendar;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TimecardBrowse extends AbstractLookup {

    private static final Logger log = LoggerFactory.getLogger(TimecardBrowse.class);

    @Inject
    protected DataManager dataManager;
    @Inject
    protected Metadata metadata;
    @Inject
    protected UserSession userSession;
    @Inject
    protected CommonService commonService;
    @Inject
    protected DatesService datesService;
    @Inject
    protected TimecardHierarchyService timecardHierarchyService;
    @Inject
    protected OrganizationService organizationService;

    @Inject
    protected TimecardHierarchyDatasource hierarchyElementsDs;
    @Inject
    protected TimecardDatasource timecardsDs;

    @Inject
    protected SplitPanel split;
    @Inject
    protected DateField<Date> month;
    @Inject
    protected Tree<TimecardHierarchy> tree;
    @Inject
    protected VBoxLayout cssLayout;
    @Inject
    protected LinkButton cleanSearch;

    protected Date monthEnd;
    protected Date monthStart;
    protected Hierarchy hierarchy;
    protected Set<String> allowedTimecardHierarchies;
    protected Set<OrganizationGroupExt> organizationsWhereUserIsHr;
    protected boolean enableInclusions;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        Calendar c = Calendar.getInstance();
        month.setValue(c.getTime());
        monthStart = datesService.getMonthBegin(month.getValue());
        monthEnd = datesService.getMonthEnd(month.getValue());

        CubaHorizontalSplitPanel cubaHorizontalSplitPanel = split.unwrap(CubaHorizontalSplitPanel.class);
        cubaHorizontalSplitPanel.addSplitterClickListener((AbstractSplitPanel.SplitterClickListener) event -> {
            if (split.getSplitPosition() > 0) {
                split.setSplitPosition(0);
            } else {
                split.setSplitPosition(250);
            }
        });
        hierarchyElementsDs.addItemChangeListener(e -> openFrame(e.getItem()));

        tree.addStyleName("timecard-tree");
        tree.setIconProvider(new ListComponent.IconProvider<TimecardHierarchy>() {
            @Nullable
            @Override
            public String getItemIcon(TimecardHierarchy entity) {
                switch (entity.getElementType()) {
                    case ORGANIZATION:
                        return "font-icon:BANK";
                    case POSITION:
                        return "font-icon:BRIEFCASE";
                    case PERSON:
                        return "font-icon:USER";
                    default:
                        return "";
                }
            }
        });

        month.addValueChangeListener(e -> {
                    monthStart = datesService.getMonthBegin(month.getValue());
                    monthEnd = datesService.getMonthEnd(month.getValue());
                    openFrameWindow(hierarchyElementsDs.getItem(), enableInclusions);
                    cleanSearch();
                }
        );

        allowedTimecardHierarchies = getAllowedTimecardHierarchies();

        if (allowedTimecardHierarchies != null && !allowedTimecardHierarchies.isEmpty()) {
            hierarchyElementsDs.refresh(ParamsMap.of("allowedTimecardHierarchies", allowedTimecardHierarchies,
                    TimecardHierarchyService.SEARCH_TEXT, "blablabla", "date", month.getValue()));

            com.vaadin.ui.Tree vaadinTree = tree.unwrap(com.vaadin.ui.Tree.class);
            vaadinTree.addExpandListener(event -> {
                UUID itemId = ((TimecardHierarchy) event.getExpandedItem()).getId();
                TimecardHierarchy timecardHierarchy = getTimecardHierarchy(itemId);
                hierarchyElementsDs.refresh(ParamsMap.of(TimecardHierarchyService.HIERARCHY_ELEMENT_PARENT_ID, itemId,
                        TimecardHierarchyService.SEARCH_TEXT, "blablabla",
                        "positionHierarchy", timecardHierarchy, "date", month.getValue()));
            });
        }

        if (params.containsKey("personId")) {
            PersonExt personExt = new PersonExt();
            personExt.setId(UUID.fromString((String) params.get("personId")));
            personExt = dataManager.reload(personExt, "person.browse");
            openTreeFor(getHierarchyElement(personExt), personExt);
        }
    }

    protected TimecardHierarchy getHierarchyElement(PersonExt personExt) {
        PersonGroupExt personGroup = personExt.getGroup();
        AssignmentExt currentAssignment = getAssignment(personGroup.getAssignments());

        if (!validateAssignment(currentAssignment)) return null;

        PositionGroupExt positionGroup = currentAssignment.getPositionGroup();
        OrganizationGroupExt organizationGroup = null;
        TimecardHierarchy hierarchyElement = null;
        if (positionGroup == null) {
            organizationGroup = currentAssignment.getOrganizationGroup();
            if (organizationGroup != null) {
                hierarchyElement = getHierarchyElement(organizationGroup, hierarchy, month.getValue());
            }
        } else {
            hierarchyElement = getHierarchyElement(positionGroup, hierarchy, month.getValue());
        }
        return hierarchyElement;
    }

    protected boolean validateAssignment(AssignmentExt currentAssignment) {
        if (currentAssignment == null) {
            showNotification("non.assignment");
            return false;
        }
        if ("TERMINATED".equals(currentAssignment.getAssignmentStatus().getCode())) {
            showNotification(getMessage("assignment.terminated"));
            return false;
        }
        return true;
    }

    protected Set<String> getAllowedTimecardHierarchies() {
        if (allowedTimecardHierarchies == null) {
            organizationsWhereUserIsHr = organizationService.getOrganizationsWhereUserIsHr(((UserExt) userSession.getUser()));
            Set<String> list = new HashSet<>();
            for (OrganizationGroupExt organizationGroupExt : organizationsWhereUserIsHr) {
                TimecardHierarchy parent =
                        getTimecardHierarchyByOrganization(organizationGroupExt.getId(), month.getValue());
                if (parent != null)
                    list.add(parent.getId().toString());
            }
            allowedTimecardHierarchies = list.isEmpty() ? null : list;
            return list;
        }
        return allowedTimecardHierarchies;
    }

    protected TimecardHierarchy getTimecardHierarchy(UUID id) {
        TimecardHierarchy timecardHierarchy = metadata.create(TimecardHierarchy.class);
        timecardHierarchy.setId(id);
        timecardHierarchy = dataManager.reload(timecardHierarchy, "timecardHierarchy-full-view");
        return timecardHierarchy;
    }

    @Nullable
    public TimecardHierarchy getTimecardHierarchyByOrganization(UUID organizationGroup, Date date) {
        return commonService.getEntities(TimecardHierarchy.class,
                "select e from tsadv$TimecardHierarchy e where e.organizationGroup.id = :organizationGroup and e.positionGroup is null" +
                        "  and  e.startDate <= :date and e.endDate >= :date  " +
                        "  ORDER BY e.endDate DESC",
                ParamsMap.of("organizationGroup", organizationGroup, "date", date), View.MINIMAL)
                .stream().findFirst().orElse(null);
    }


    protected void openFrameWindow(TimecardHierarchy element, boolean enableInclusions) {
        this.enableInclusions = enableInclusions;
        if (element != null) {
            refreshDs(new HashMap<>());
            Consumer consumer = createConsumer();
            openFrame(cssLayout, "timecard",
                    ParamsMap.of("consumer", consumer,
                            "enableInclusionsValue", enableInclusions,
                            "organizationGroup", element.getOrganizationGroup()));
        } else {
            cssLayout.removeAll();
        }
    }

    protected Consumer createConsumer() {
        return o -> {
            if (o == null) {
                refreshDs(new HashMap<>());
            } else {
                if (o instanceof Boolean) {
                    this.enableInclusions = ((boolean) o);
                    openFrameWindow(hierarchyElementsDs.getItem(), ((boolean) o));
                } else {
                    Map<String, Object> params = new HashMap<>();
                    params.put("organizationGroup", hierarchyElementsDs.getItem().getOrganizationGroup());
                    params.put("positionGroup", hierarchyElementsDs.getItem().getPositionGroup());
                    params.put("personGroup", ((PersonExt) o).getGroup());
                    params.put("startDate", monthStart);
                    params.put("endDate", monthEnd);
                    params.put("loadFullData", false);
                    params.put("enableInclusions", enableInclusions);
                    timecardsDs.refresh(params);
                }
            }
        };
    }

    protected void checkSuspendedAssignments(Date monthStart, Date monthEnd) {
        Set<Timecard> suspendedTimecards = new HashSet<>();
        List<UUID> assignmentGroupIds = timecardsDs.getItems().stream().filter(t -> t.getAssignmentGroupId() != null).map(Timecard::getAssignmentGroupId).collect(Collectors.toList());
        List<AssignmentExt> suspendedAssignments = commonService.getEntities(AssignmentExt.class, "select e from base$AssignmentExt e" +
                        " where e.group.id in :assignmentGroupIds " +
                        " and e.assignmentStatus.code = 'SUSPENDED' " +
                        "and :monthStart <= e.endDate " +
                        "       and :monthEnd >= e.startDate",
                ParamsMap.of("assignmentGroupIds", assignmentGroupIds, "monthStart", monthStart, "monthEnd", monthEnd), "assignment-with-groups");

        for (AssignmentExt assignment : suspendedAssignments) {
            Date startDate = assignment.getStartDate().after(monthStart) ? assignment.getStartDate() : monthStart;
            Date endDate = assignment.getEndDate().before(monthEnd) ? assignment.getEndDate() : monthEnd;
            List<Absence> absences = commonService.getEntities(Absence.class, "select e from tsadv$Absence e  " +
                            "    where   e.personGroup.id = :personGroupId" +
                            "       and  :startDate <= e.dateTo" +
                            "       and  :endDate >= e.dateFrom",
                    ParamsMap.of("personGroupId", assignment.getPersonGroup().getId(), "startDate", startDate, "endDate", endDate), View.MINIMAL);
            if (absences.isEmpty()) {
                Timecard timecardWithoutAbsence = timecardsDs.getItems().stream().filter(t -> t.getAssignmentGroupId() != null)
                        .filter(timecard -> timecard.getAssignmentGroupId().equals(assignment.getGroup().getId())).findAny().orElse(null);
                suspendedTimecards.add(timecardWithoutAbsence);
            }
        }
        String persons = suspendedTimecards.stream().map(t -> "\n" + t.getName()).collect(Collectors.joining());
        if (!suspendedTimecards.isEmpty()) {
            showNotification(getMessage("no.absence.in.suspended") + persons, NotificationType.HUMANIZED);
        }
    }

    protected void refreshDs(Map<String, Object> params) {
        params.put("organizationGroup", hierarchyElementsDs.getItem().getOrganizationGroup());
        params.put("positionGroup", hierarchyElementsDs.getItem().getPositionGroup());
        params.put("personGroup", hierarchyElementsDs.getItem().getPersonGroup());
        params.put("startDate", monthStart);
        params.put("endDate", monthEnd);
        params.put("loadFullData", false);
        params.put("enableInclusions", enableInclusions);
        timecardsDs.refresh(params);
    }

    public void openPersonLookup() {
        openLookup("tsadv$EmployeeListForTimecard.browse", items -> {
            for (Object item : items) {
                openTreeFor(getHierarchyElement((PersonExt) item), (PersonExt) item);
            }
        }, WindowManager.OpenType.DIALOG, ParamsMap.of("date", month.getValue()));
    }

    protected void openTreeFor(TimecardHierarchy hierarchyElement, PersonExt personExt) {
        if (hierarchyElement == null) return;
        boolean accessAllowed = true;
        if (getAllowedTimecardHierarchies() != null && !allowedTimecardHierarchies.isEmpty()) {
            accessAllowed = false;
            for (String allowedTimecardHierarchy : allowedTimecardHierarchies) {
                if (hierarchyElement.getIdPath().contains(allowedTimecardHierarchy)) {
                    accessAllowed = true;
                }
            }
        }
        if (!accessAllowed) {
            showNotification(getMessage("forbiddenForTimekeeper"), NotificationType.HUMANIZED);
            return;
        }
        Map<String, Object> params = new HashMap<>(hierarchyElementsDs.getLastRefreshParameters());
        params.put(TimecardHierarchyDatasource.HIERARCHY_ELEMENT_PARENT_ID, hierarchyElement.getId());
        params.put("allowedTimecardHierarchies", null);
        params.put(TimecardHierarchyDatasource.SEARCH_TEXT, "blablabla");
        params.put("date", month.getValue());

        hierarchyElementsDs.refresh(params);
        OrganizationGroupExt organizationGroupExt = hierarchyElement.getOrganizationGroup();
        PositionGroupExt positionGroup = hierarchyElement.getPositionGroup();
        TimecardHierarchy hierarchyElementExt;
        hierarchyElementExt = hierarchyElementsDs.getItems().stream()
                .filter(e -> e.getName().equals(personExt.getFioWithEmployeeNumber())).findFirst().orElse(null);
        if (hierarchyElement == null) {
            OrganizationGroupExt finalOrganizationGroup = organizationGroupExt;
            hierarchyElementExt = hierarchyElementsDs.getItems().stream()
                    .filter(e -> e.getOrganizationGroup().getId().equals(finalOrganizationGroup.getId()) && e.getPositionGroup() == null).findFirst().orElse(null);
        }
        if (hierarchyElementExt != null) {
            tree.setSelected(hierarchyElementExt);
            tree.expand(hierarchyElementExt.getId());
        }
    }

    protected AssignmentExt getAssignment(List<AssignmentExt> assignments) {
        return assignments.stream().filter(assignmentExt ->
                assignmentExt.getPrimaryFlag()
                        && !assignmentExt.getStartDate().after(month.getValue())
                        && !assignmentExt.getEndDate().before(month.getValue())).findFirst().orElse(null);
    }

    protected TimecardHierarchy getHierarchyElement(PositionGroupExt positionGroup, Hierarchy hierarchy, Date date) {
        return timecardHierarchyService.getHierarchyElement(positionGroup, hierarchy, date);
    }

    protected TimecardHierarchy getHierarchyElement(OrganizationGroupExt organizationGroupExt, Hierarchy hierarchy, Date date) {
        return timecardHierarchyService.getHierarchyElement(organizationGroupExt, hierarchy, date);
    }

    protected void openFrame(TimecardHierarchy element) {
        if (element != null) {
            switch (element.getElementType()) {
                default: {
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("firstResult", 0); // because it's search - we set table to 1st page
                    params.put("startDate", month.getValue());
                    params.put("endDate", monthEnd);
                    refreshDs(params);
                    Consumer consumer = createConsumer();
                    openFrame(cssLayout, "timecard",
                            ParamsMap.of("consumer", consumer,
                                    "enableInclusionsValue", enableInclusions,
                                    "organizationGroup", element.getOrganizationGroup()));
                    checkSuspendedAssignments(monthStart, monthEnd);
                    break;
                }
            }
        } else {
            cssLayout.removeAll();
        }
    }

    /*protected void search(String searchText) {
        if (StringUtils.isBlank(searchText)) {
            restoreTree();
        } else {
            tree.setStyleProvider(new Tree.StyleProvider<TimecardHierarchy>() {
                @Override
                public String getStyleName(TimecardHierarchy entity) {
                    return entity.getName().toLowerCase().contains(searchText.toLowerCase()) ? "org-tree-search-fi" : null;
                }
            });

            hierarchyElementsDs.clear();

            Map<String, Object> params = new HashMap<>();
            params.putAll(hierarchyElementsDs.getLastRefreshParameters());
            params.put(OrganizationTreeDatasource.SEARCH_TEXT, searchText);
            params.put("startDate", month.getValue());
            params.put("endDate", monthEnd);

            hierarchyElementsDs.refresh(params);

            hierarchyElementsDs.detachListener(hierarchyElementsDs.getItems());

            tree.expandTree();

            hierarchyElementsDs.attachListener();
        }
    }*/


    protected void restoreTree() {
//        tree.collapseTree(); - this in very dangerous! it's cause recursion!

        hierarchyElementsDs.getItems().forEach(timecardHierarchy -> {
            timecardHierarchy.setHasChild(false);
            tree.collapse(timecardHierarchy.getId());
        });

        hierarchyElementsDs.clear();
        hierarchyElementsDs.refresh(ParamsMap.of("date", month.getValue()));
        tree.repaint();

    }

    public void cleanSearch() {
        if (getAllowedTimecardHierarchies() != null && !allowedTimecardHierarchies.isEmpty()) {
            cleanSearch.setEnabled(false);
            return;
        }
        restoreTree();
    }
}
