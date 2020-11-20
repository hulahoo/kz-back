package kz.uco.tsadv.web.modules.personal.assignmentschedule;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.widgets.CubaFlowLayout;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.Label;
import kz.uco.base.entity.shared.Hierarchy;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.datasource.TimecardHierarchyDatasource;
import kz.uco.tsadv.datasource.TimesheetDatasource;
import kz.uco.tsadv.entity.TimecardHierarchy;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.timesheet.enums.MaterialDesignColorsEnum;
import kz.uco.tsadv.modules.timesheet.model.AssignmentSchedule;
import kz.uco.tsadv.modules.timesheet.model.StandardOffset;
import kz.uco.tsadv.modules.timesheet.model.Timesheet;
import kz.uco.tsadv.service.DatesService;
import kz.uco.tsadv.service.OrganizationService;
import kz.uco.tsadv.service.TimecardHierarchyService;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class AssignmentScheduleBrowse extends AbstractLookup {

    @Inject
    private Metadata metadata;
    @Inject
    private TimecardHierarchyService timecardHierarchyService;
    @Inject
    private DataManager dataManager;
    @Inject
    private CommonService commonService;
    @Inject
    private DatesService datesService;
    @Inject
    private OrganizationService organizationService;
    @Inject
    private UserSession userSession;


    @Inject
    private TimecardHierarchyDatasource hierarchyElementsDs;
    @Inject
    private Tree<TimecardHierarchy> tree;
    @Inject
    private TimesheetDatasource timesheetDs;
    @Inject
    private DateField<Date> month;
    @Inject
    private VBoxLayout cssLayout;
    @Inject
    private CssLayout trashLayout;
    @Inject
    private TextField searchByNumber;
    @Inject
    private FlowBoxLayout offsetsBox;
    @Inject
    private LinkButton cleanSearch;
    @Inject
    private DateField<Date> dateField;

    Set<String> allowedTimecardHierarchies;

    private Date monthEnd;
    private Date monthStart;
    Set<OrganizationGroupExt> organizationsWhereUserIsHr;

    private Map<StandardOffset, MaterialDesignColorsEnum> presentedOffsetsAndColors = new HashMap<>();

    protected Hierarchy hierarchy;

    private boolean enableInclusions;

    private String styleHtmlWithFormattedString = "<div style=\"" +
            " background: %s;" +
            " padding: 3px;" +
            " border-radius: 5px;\n" +
            " border: 1px solid %s;" +
            " color:%s;" +
            " margin: 0 !important; \">%s</div>";


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        hierarchyElementsDs.addItemChangeListener(e -> openFrame(e.getItem(), enableInclusions));

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

        timesheetDs.addCollectionChangeListener(e -> updateOffsetsBoxLayout());

        month.addValueChangeListener(e -> {
                    monthStart = datesService.getMonthBegin(month.getValue());
                    monthEnd = datesService.getMonthEnd(month.getValue());
                    openFrame(hierarchyElementsDs.getItem(), enableInclusions);
                    setDateToSearch();
                }
        );
        month.setValue(CommonUtils.getSystemDate());
        setDateToSearch();
        com.vaadin.ui.CssLayout unwrappedTrashLayout = trashLayout.unwrap(com.vaadin.ui.CssLayout.class);
        com.vaadin.ui.CssLayout trash = new com.vaadin.ui.CssLayout();
        trash.setHeight("40px");
        trash.setWidth("40px");
        trash.setStyleName("timesheet-trash-block");
        DragAndDropWrapper trashDropWrapper = new DragAndDropWrapper(trash);
        trashDropWrapper.addStyleName("no-vertical-drag-hints");
        trashDropWrapper.addStyleName("no-horizontal-drag-hints");
        unwrappedTrashLayout.addComponent(trashDropWrapper);
        hierarchy = getHierarchy();

        searchByNumber.addEnterPressListener(e -> searchPersonByCode());

        allowedTimecardHierarchies = getAllowedTimecardHierarchies();

        if (allowedTimecardHierarchies != null && !allowedTimecardHierarchies.isEmpty()) {
            hierarchyElementsDs.refresh(ParamsMap.of("allowedTimecardHierarchies", allowedTimecardHierarchies,
                    TimecardHierarchyService.SEARCH_TEXT, "blablabla", "date", month.getValue()));

            com.vaadin.v7.ui.Tree vaadinTree = tree.unwrap(com.vaadin.v7.ui.Tree.class);
            vaadinTree.addExpandListener(event -> {
                UUID itemId = (UUID) event.getItemId();
                TimecardHierarchy timecardHierarchy = getTimecardHierarchy(itemId);
                if (timecardHierarchy.getPositionGroup() != null) {
                    hierarchyElementsDs.refresh(ParamsMap.of(TimecardHierarchyService.HIERARCHY_ELEMENT_PARENT_ID, itemId,
                            TimecardHierarchyService.SEARCH_TEXT, "sdfsl",
                            "positionHierarchy", timecardHierarchy, "date", month.getValue()));
                }
            });
        }
    }

    private Set<String> getAllowedTimecardHierarchies() {
        if (allowedTimecardHierarchies == null) {
            organizationsWhereUserIsHr = organizationService.getOrganizationsWhereUserIsHr(((UserExt) userSession.getUser()));
            if (organizationsWhereUserIsHr != null && !organizationsWhereUserIsHr.isEmpty()) {
                Set<String> list = new HashSet<>();
                for (OrganizationGroupExt organizationGroupExt : organizationsWhereUserIsHr) {
                    TimecardHierarchy parent = timecardHierarchyService.getHierarchyElement(organizationGroupExt,
                            null,
                            month.getValue());
                    list.add(parent.getId().toString());
                }
                allowedTimecardHierarchies = list;
                return list;
            }
        }
        return allowedTimecardHierarchies;
    }

    private TimecardHierarchy getTimecardHierarchy(UUID id) {
        TimecardHierarchy timecardHierarchy = metadata.create(TimecardHierarchy.class);
        timecardHierarchy.setId(id);
        timecardHierarchy = dataManager.reload(timecardHierarchy, "timecardHierarchy-full-view");
        return timecardHierarchy;
    }

    private void setDateToSearch() {
        int currentMonthNumber = datesService.getMonthNumber(CommonUtils.getSystemDate());
        Date systemDate = CommonUtils.getSystemDate();
        systemDate = datesService.getDayBegin(systemDate);
        int monthNumber;
        if (month.getValue() != null) {
            monthNumber = datesService.getMonthNumber(month.getValue());
        } else {
            monthNumber = datesService.getMonthNumber(CommonUtils.getSystemDate());
        }
        if (monthNumber != currentMonthNumber) {
            dateField.setValue(month.getValue());
        } else {
            dateField.setValue(systemDate);
        }
    }

    public void searchPersonByCode() {
        if (searchByNumber.getValue() != null) {
            PersonExt personExt = getPersonByEmployeeCode(searchByNumber.getValue().toString(), dateField.getValue());
            if (personExt != null) {
                openTreeFor(personExt);
            } else {
                showNotification(getMessage("personNotFound"), NotificationType.HUMANIZED);
            }
        }
    }

    private PersonExt getPersonByEmployeeCode(String employeeNumber, Date date) {
        return commonService.getEntity(PersonExt.class, "select e\n" +
                "  from base$PersonExt e\n" +
                "    left join e.group.assignments a\n" +
                "       where e.employeeNumber = :employeeNumber" +
                "           and :date between e.startDate and e.endDate\n" +
                "           and :date between a.startDate and a.endDate\n" +
                "           and (a.assignmentStatus.code = 'ACTIVE' or  a.assignmentStatus.code = 'SUSPENDED')\n" +
                "           and a.primaryFlag = true", ParamsMap.of("employeeNumber", employeeNumber,
                "date", date), "person-for-search");
    }

    protected void openFrame(TimecardHierarchy element, boolean enableInclusions) {
        if (element != null) {
            HashMap<String, Object> params = new HashMap<>();
            params.put("firstResult", 0); // because it's search - we set table to 1st page
            refreshDs(params);
            Consumer consumer = o -> {
                this.enableInclusions = ((boolean) o);
                openFrame(hierarchyElementsDs.getItem(), ((boolean) o));
            };
            openFrame(cssLayout, "assignment-schedule",
                    ParamsMap.of("presentedOffsetsAndColors", presentedOffsetsAndColors,
                            "consumer", consumer, "enableInclusionsValue", enableInclusions));
        } else {
            cssLayout.removeAll();
        }
    }

    private void refreshDs(HashMap<String, Object> params) {
        params.put("organizationGroup", hierarchyElementsDs.getItem().getOrganizationGroup());
        params.put("positionGroup", hierarchyElementsDs.getItem().getPositionGroup());
        params.put("personGroup", hierarchyElementsDs.getItem().getPersonGroup());
        params.put("startDate", monthStart);
        params.put("endDate", monthEnd);
        params.put("loadFullData", false);
        params.put("enableInclusions", enableInclusions);
        timesheetDs.refresh(params);
    }

    public void openPersonLookup() {
        openLookup("tsadv$EmployeeListForTimecard.browse", items -> {
            for (Object item : items) {
                openTreeFor((PersonExt) item);
            }
        }, WindowManager.OpenType.DIALOG, ParamsMap.of("date", month.getValue()));
    }

    protected AssignmentExt getAssignment(List<AssignmentExt> assignments) {
        return assignments.stream().filter(assignmentExt ->
                assignmentExt.getPrimaryFlag()
                        && !assignmentExt.getStartDate().after(month.getValue())
                        && !assignmentExt.getEndDate().before(month.getValue())).findFirst().orElse(null);
    }

    private void openTreeFor(PersonExt person) {
        PersonGroupExt personGroup = person.getGroup();
        AssignmentExt currentAssignment = getAssignment(personGroup.getAssignments());
        if (currentAssignment == null) {
            showNotification("non.assignment");
            return;
        }
        if ("TERMINATED".equals(currentAssignment.getAssignmentStatus().getCode())) {
            showNotification(getMessage("assignment.terminated"));
            return;
        }

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
        Map<String, Object> params = new HashMap<>();
        params.putAll(hierarchyElementsDs.getLastRefreshParameters());
        params.put(TimecardHierarchyDatasource.HIERARCHY_ELEMENT_PARENT_ID, hierarchyElement.getId());
        params.put("allowedTimecardHierarchies", null);
        params.put(TimecardHierarchyDatasource.SEARCH_TEXT, "blablabla");
        params.put("date", month.getValue());

        hierarchyElementsDs.refresh(params);
        TimecardHierarchy hierarchyElementExt;
        if (positionGroup != null) {
            hierarchyElementExt = hierarchyElementsDs.getItems().stream()
                    .filter(e -> e.getName().equals(personGroup.getFioWithEmployeeNumber())).findFirst().orElse(null);
        } else {
            OrganizationGroupExt finalOrganizationGroup = organizationGroup;
            hierarchyElementExt = hierarchyElementsDs.getItems().stream()
                    .filter(e -> e.getOrganizationGroup().getId().equals(finalOrganizationGroup.getId()) && e.getPositionGroup() == null).findFirst().orElse(null);
        }
        if (hierarchyElementExt != null) {
            tree.setSelected(hierarchyElementExt);
            tree.expand(hierarchyElementExt.getId());
        }
    }

    private TimecardHierarchy getHierarchyElement(PositionGroupExt positionGroup, Hierarchy hierarchy, Date date) {
        TimecardHierarchy timecardHierarchy = timecardHierarchyService.getHierarchyElement(positionGroup, hierarchy, date);
        return setParentTimecardHierarchy(timecardHierarchy);
    }

    private TimecardHierarchy getHierarchyElement(OrganizationGroupExt organizationGroupExt, Hierarchy hierarchy, Date date) {
        TimecardHierarchy timecardHierarchy = timecardHierarchyService.getHierarchyElement(organizationGroupExt, hierarchy, date);
        return setParentTimecardHierarchy(timecardHierarchy);
    }

    private TimecardHierarchy setParentTimecardHierarchy(TimecardHierarchy timecardHierarchy) {
        TimecardHierarchy parent = metadata.create(TimecardHierarchy.class);
        parent.setId(timecardHierarchy.getParent().getId());
        timecardHierarchy.setParent(parent);
        return timecardHierarchy;
    }

    private void restoreTree() {
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


    private Hierarchy getHierarchy() {
        return commonService.getEntity(Hierarchy.class, "select e from base$Hierarchy e where e.primaryFlag = TRUE", ParamsMap.empty(), View.LOCAL);
    }

    private void updateOffsetsBoxLayout() {
        CubaFlowLayout offsetsBoxLayout = offsetsBox.unwrap(CubaFlowLayout.class);
        offsetsBoxLayout.removeAllComponents();
        presentedOffsetsAndColors.clear();
        CollectionDatasource datasource = timesheetDs;

        Stream<Timesheet> timesheetStream = datasource.getItems().stream().filter(e -> ((Timesheet) e).getAssignmentSchedules() != null && !((Timesheet) e).getAssignmentSchedules().isEmpty());
        Stream<AssignmentSchedule> assignmentScheduleStream = timesheetStream.map(e -> e.getAssignmentSchedules()).flatMap(assignmentSchedules -> assignmentSchedules.stream());
        assignmentScheduleStream.forEach(e -> presentedOffsetsAndColors.put(e.getOffset(), e.getColorsSet()));

        presentedOffsetsAndColors.keySet()
                .forEach(offset -> {
                    offset = getFullOffset(offset);
                    String value = offset.getOffsetScheduleName();
                    MaterialDesignColorsEnum colorsEnum = presentedOffsetsAndColors.get(offset);
                    String color = colorsEnum.getColorHex();
                    String fontColor = colorsEnum.getFontColor();

                    Label label = new Label();
                    label.setContentMode(ContentMode.HTML);
                    label.setData(offset);
                    label.setValue(String.format(styleHtmlWithFormattedString, color, color, fontColor, value));
                    DragAndDropWrapper labelDragWrapper = new DragAndDropWrapper(label);
                    labelDragWrapper.setSizeUndefined();
                    labelDragWrapper.setDragStartMode(DragAndDropWrapper.DragStartMode.COMPONENT);
                    offsetsBoxLayout.addComponent(labelDragWrapper);
                });
    }

    private StandardOffset getFullOffset(StandardOffset offset) {
        return dataManager.reload(offset, "standardOffset.view");
    }

}