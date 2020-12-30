package kz.uco.tsadv.web.modules.personal.reserve;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.ComponentVisitor;
import com.haulmont.cuba.gui.ComponentsHelper;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.haulmont.cuba.web.gui.components.WebLookupField;
import com.haulmont.cuba.web.gui.components.WebLookupPickerField;
import com.haulmont.cuba.web.widgets.CubaHorizontalSplitPanel;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.AbstractSplitPanel;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.entity.dictionary.DicLocation;
import kz.uco.base.entity.shared.ElementType;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.web.modules.personal.common.Utils;
import kz.uco.tsadv.web.modules.personal.hierarchyelement.old.v68.PersonPercentageDatasource;
import kz.uco.tsadv.web.modules.personal.hierarchyelement.old.v68.PositionPercentageDatasource;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Calendar;
import java.util.*;

/**
 * @author Adilbekov Yernar
 */
@SuppressWarnings("all")
public class PositionPersonCompetence extends AbstractWindow {

    @Inject
    private HierarchicalDatasource<HierarchyElementExt, UUID> hierarchyElementsDs;

    @Inject
    private EmployeeService employeeService;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private PersonPercentageDatasource personPercentagesDs;

    @Inject
    private PositionPercentageDatasource positionPercentagesDs;

    @Inject
    private Table<PersonPercentage> personTable;

    @Inject
    private DataManager dataManager;

    @Inject
    private SplitPanel split;

    @Inject
    private SplitPanel splitForPerson;

    @Inject
    private HBoxLayout searchLayout;

    @Inject
    private LookupPickerField jobGroupLookupId;

    @Inject
    private LookupPickerField organizationGroupLookupId;

    @Inject
    private LookupPickerField positionGroupLookupId;

    @Inject
    private LookupPickerField locationLookupId;

    @Inject
    private LookupPickerField positionJobLookup;

    @Inject
    private LookupPickerField positionLocLookup;

    @Inject
    private LookupPickerField positionOrgLookup;

    @Inject
    private LookupPickerField positionPosLookup;

    @Inject
    private CollectionDatasource<DicLocation, UUID> dicLocationDs;

    @Inject
    private CollectionDatasource<JobGroup, UUID> jobGroupsDs;

    @Inject
    private CollectionDatasource<OrganizationGroupExt, UUID> organizationGroupsDs;

    @Inject
    private CollectionDatasource<PositionGroupExt, UUID> positionGroupsDs;

    @Inject
    private CollectionDatasource<AssignmentExt, UUID> assignmentsDs;

    @Inject
    private Tree<HierarchyElementExt> tree;

    @Inject
    private VBoxLayout splitRight;

    @Inject
    private GridLayout gridFilter;

    @Inject
    private GroupBoxLayout groupBoxFilter;

    @Inject
    private Label organizationName;

    @Inject
    private LookupField matrixLookup;

    @Inject
    private TabSheet tabSheet;

    @Inject
    private Table<AssignmentExt> personList;

    @Inject
    private Metadata metadata;

    @Inject
    private Datasource<SuccessionPlanning> successionPlanningDs;

    @Inject
    private Button addToReserve;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        tree.addStyleName("b-tree");
        tree.setIconProvider(new ListComponent.IconProvider<HierarchyElementExt>() {
            @Nullable
            @Override
            public String getItemIcon(HierarchyElementExt entity) {
                switch (entity.getElementType()) {
                    case ORGANIZATION:
                        return "font-icon:BANK";
                    case POSITION:
                        return "font-icon:BRIEFCASE";
                    default:
                        return "font-icon:USER";
                }
            }
        });

        initVisibleComponent(hierarchyElementsDs.getItem());

//        tabSheet.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
//            @Override
//            public void selectedTabChanged(TabSheet.SelectedTabChangeEvent event) {
//                resetFilter();
//            }
//        });

        hierarchyElementsDs.addItemChangeListener(e -> {
            HierarchyElementExt hierarchyElement = e.getItem();
            if (hierarchyElement != null) {
                initVisibleComponent(hierarchyElement);
                if (hierarchyElement.getElementType().equals(ElementType.POSITION)) {
                    refreshPercentageDs(true);
                } else {
                    tree.expand(hierarchyElement.getId());
                }
            }
        });

        personPercentagesDs.addItemChangeListener(new Datasource.ItemChangeListener<PersonPercentage>() {
            @Override
            public void itemChanged(Datasource.ItemChangeEvent<PersonPercentage> e) {
                addToReserve.setEnabled(e.getItem() != null);
            }
        });

        assignmentsDs.addItemChangeListener(new Datasource.ItemChangeListener<AssignmentExt>() {
            @Override
            public void itemChanged(Datasource.ItemChangeEvent<AssignmentExt> e) {
                if (e.getItem() != null) refreshPercentageDs(false);
            }
        });

        setDockableSplit();
        removeOpenAction();
        addFilterListener();
        createSearchBlock();

        Map<String, MatrixFilter> map = new HashMap<>();
        for (int i = 1; i <= 9; i++) {
            map.put(getMessage("Matrix.filter." + i), new MatrixFilter(i, "images/matrix/" + i + ".png"));
        }

        matrixLookup.setOptionsMap(map);
        matrixLookup.setOptionIconProvider(item -> ((MatrixFilter) item).getImage());
    }

    public void addToReserve() {
        HierarchyElementExt hierarchyElement = hierarchyElementsDs.getItem();
        if (hierarchyElement != null && hierarchyElement.getElementType().equals(ElementType.POSITION)) {
            PositionGroupExt positionGroup = hierarchyElement.getPositionGroup();
            if (positionGroup != null) {
                SuccessionPlanning successionPlanning = getSuccessPlanning(positionGroup);

                List<Successor> newSuccessors = new ArrayList<>();

                Map<String, Object> map = new HashMap<>();

                List<String> errors = new ArrayList<>();

                if (successionPlanning != null) {
                    for (PersonPercentage personPercentage : personTable.getSelected()) {
                        PersonExt person = personPercentage.getAssignment().getPersonGroup().getPerson();
                        UUID personGroupId = person.getGroup().getId();

                        if (!hasSuccessor(successionPlanning.getId(), personGroupId)) {
                            successionPlanning.setSuccessors(newSuccessors);

                            createSuccessor(successionPlanning, personPercentage);
                        } else {
                            errors.add(String.format(getMessage("PositionPersonCompetence.add.to.reserve.error"), person.getFullName()));
                        }
                    }

                    if (!newSuccessors.isEmpty()) map.put(StaticVariable.NEW_SUCCESSORS, newSuccessors);

                } else {
                    successionPlanning = metadata.create(SuccessionPlanning.class);
                    successionPlanning.setPositionGroup(positionGroup);
                    successionPlanning.setSuccessors(new ArrayList<>());
                    successionPlanning.setStartDate(new Date());

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(9999, 11, 31);
                    successionPlanning.setEndDate(calendar.getTime());

                    for (PersonPercentage personPercentage : personTable.getSelected()) {
                        createSuccessor(successionPlanning, personPercentage);
                    }
                }

                if (!errors.isEmpty()) {
                    StringBuilder sbError = new StringBuilder("<ul>");
                    for (String message : errors) {
                        sbError.append("<li>").append(message).append("</li>");
                    }
                    sbError.append("</ul>");
                    showMessageDialog(getMessage("PositionPersonCompetence.add.to.reserve.error.title"), sbError.toString(), MessageType.WARNING_HTML);
                } else {
                    openEditor(successionPlanning, WindowManager.OpenType.THIS_TAB, map);
                }
            }
        }
    }

    private void createSuccessor(SuccessionPlanning successionPlanning, PersonPercentage personPercentage) {
        Successor successor = metadata.create(Successor.class);
        successor.setStartDate(successionPlanning.getStartDate());
        successor.setEndDate(successionPlanning.getEndDate());
        successor.setSuccession(successionPlanning);

        PersonGroupExt personGroup = loadPersonGroup(personPercentage.getAssignment().getPersonGroup().getId());
        successor.setPersonGroup(personGroup);

        successionPlanning.getSuccessors().add(successor);
    }

    private PersonGroupExt loadPersonGroup(UUID personGroupId) {
        LoadContext<PersonGroupExt> loadContext = LoadContext.create(PersonGroupExt.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e from base$PersonGroupExt e where e.id = :personGroupId")
                .setParameter("personGroupId", personGroupId))
                .setView("personGroup.add.reserve");
        return dataManager.load(loadContext);
    }

    private SuccessionPlanning getSuccessPlanning(PositionGroupExt positionGroup) {
        LoadContext<SuccessionPlanning> loadContext = LoadContext.create(SuccessionPlanning.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e from tsadv$SuccessionPlanning e where e.positionGroup.id = :positionGroupId")
                .setParameter("positionGroupId", positionGroup.getId()))
                .setView("successionPlanning.browse");
        return dataManager.load(loadContext);
    }

    private boolean hasSuccessor(UUID sPlannigId, UUID personGroupId) {
        LoadContext<Successor> loadContext = LoadContext.create(Successor.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e from tsadv$Successor e where e.succession.id = :sPlannigId and e.personGroup.id = :personGroupId")
                .setParameter("sPlannigId", sPlannigId)
                .setParameter("personGroupId", personGroupId))
                .setView("successor.browse");
        return dataManager.getCount(loadContext) > 0;
    }

    public void linkInvoke(PersonPercentage personPercentage, String name) {
        AssignmentExt assignment = getAssignment(personPercentage.getAssignment().getPersonGroup().getId());

        if (name.equalsIgnoreCase("managerAssignment.personGroup.person.fullName")) {
            assignment = personPercentage.getManagerAssignment();
        }

        if (assignment != null) {
            openEditor("person-card", assignment.getPersonGroup(), WindowManager.OpenType.THIS_TAB);
        } else {
            showNotification("Assignment is NULL!");
        }
    }

    public void linkInvokePosition(AssignmentExt assignment, String name) {
        redirectToCard(assignment.getPersonGroup());
    }

    private void redirectToCard(PersonGroupExt personGroup) {
        if (personGroup != null) {
            AssignmentExt assignment = getAssignment(personGroup.getId());
            if (assignment != null) {
                openEditor("person-card", personGroup, WindowManager.OpenType.THIS_TAB);
            } else {
                showNotification("Assignment is NULL!");
            }
        }
    }

    private void createSearchBlock() {
        final TextField<String> searchField = componentsFactory.createComponent(TextField.class);
        searchField.setWidth("100%");
//        searchField.addEnterPressListener(new TextInputField.EnterPressListener() {
//            @Override
//            public void enterPressed(TextInputField.EnterPressEvent e) {
//                search(searchField.getValue());
//            }
//        });
        searchField.requestFocus();
        searchLayout.add(searchField);
        searchLayout.expand(searchField);

        Button searchButton = componentsFactory.createComponent(Button.class);
        searchButton.setCaption(getMessage("table.btn.search"));
        searchButton.setIcon("icons/search.png");
        searchButton.setAction(new BaseAction("search-click") {
            @Override
            public void actionPerform(Component component) {
                search(searchField.getValue());
            }
        });

        searchLayout.add(searchButton);
    }

    private void search(String searchRequest) {
        assignmentsDs.setQuery("select e from base$AssignmentExt e");

        if (searchRequest != null && searchRequest.trim().length() != 0) {
            searchRequest = "%" + searchRequest.toLowerCase() + "%";
            String resolvedString = String.format("select e from base$AssignmentExt e, base$PersonExt p " +
                    "where e.personGroup.id = p.group.id " +
                    "and (lower(p.firstName) like '%s' " +
                    "   or lower(p.lastName) like '%s' " +
                    "   or lower(p.middleName) like '%s'" +
                    "   or lower(p.employeeNumber) like '%s'" +
                    ")", searchRequest, searchRequest, searchRequest, searchRequest);

            assignmentsDs.setQuery(resolvedString);
        }

        assignmentsDs.refresh();
    }

    private void resetFilter() {
        ComponentsHelper.walkComponents(tabSheet, new ComponentVisitor() {
            @Override
            public void visit(Component component, String name) {
                String simpleName = component.getClass().getSimpleName();

                if (simpleName.equalsIgnoreCase("WebLookupPickerField")) {
                    ((WebLookupPickerField) component).setValue(null);
                } else if (simpleName.equalsIgnoreCase("WebLookupField")) {
                    ((WebLookupField) component).setValue(null);
                }
            }
        });

        organizationGroupsDs.setItem(null);
        positionGroupsDs.setItem(null);
        jobGroupsDs.setItem(null);
        dicLocationDs.setItem(null);
    }

    private void initVisibleComponent(HierarchyElementExt hierarchyElement) {
        if (hierarchyElement == null) {
            splitRight.setVisible(false);
        } else {
            splitRight.setVisible(true);
            boolean isPosition = hierarchyElement.getElementType().equals(ElementType.POSITION);
            groupBoxFilter.setVisible(isPosition);
            personTable.setVisible(isPosition);
            organizationName.setVisible(!isPosition);
            if (!isPosition) {
                organizationName.setValue(hierarchyElement.getOrganizationGroup().getOrganization().getOrganizationName());
            }
        }
    }

    private void addFilterListener() {
        /**
         * for tab 'By Position'
         * */
        organizationGroupLookupId.addValueChangeListener(e -> refreshPercentageDs(true));
        positionGroupLookupId.addValueChangeListener(e -> refreshPercentageDs(true));
        jobGroupLookupId.addValueChangeListener(e -> refreshPercentageDs(true));
        locationLookupId.addValueChangeListener(e -> refreshPercentageDs(true));
        matrixLookup.addValueChangeListener(e -> refreshPercentageDs(true));

        /**
         * for tab 'By Person'
         * */
        positionOrgLookup.addValueChangeListener(e -> refreshPercentageDs(false));
        positionPosLookup.addValueChangeListener(e -> refreshPercentageDs(false));
        positionJobLookup.addValueChangeListener(e -> refreshPercentageDs(false));
        positionLocLookup.addValueChangeListener(e -> refreshPercentageDs(false));
    }

    private void refreshPercentageDs(boolean isByPosition) {
        Map<String, Object> params = new HashMap<>();

        if (organizationGroupsDs.getItem() != null) {
            params.put(StaticVariable.FILTER_ORGANIZATION_GROUP_ID, organizationGroupsDs.getItem().getId());
        }

        if (positionGroupsDs.getItem() != null) {
            params.put(StaticVariable.FILTER_POSITION_GROUP_ID, positionGroupsDs.getItem().getId());
        }
        if (jobGroupsDs.getItem() != null) {
            params.put(StaticVariable.FILTER_JOB_GROUP_ID, jobGroupsDs.getItem().getId());
        }
        if (dicLocationDs.getItem() != null) {
            params.put(StaticVariable.FILTER_LOCATION_GROUP_ID, dicLocationDs.getItem().getId());
        }

        if (isByPosition) {
            params.put(StaticVariable.POSITION_GROUP_ID, hierarchyElementsDs.getItem().getPositionGroup().getId());

            if (matrixLookup.getValue() != null) {
                params.put(StaticVariable.FILTER_MATRIX_ID, ((MatrixFilter) matrixLookup.getValue()).getId());
            }

            personPercentagesDs.clear();
            personPercentagesDs.refresh(params);
        } else {
            params.put(StaticVariable.USER_PERSON_GROUP_ID, assignmentsDs.getItem().getPersonGroup().getId());

            positionPercentagesDs.clear();
            positionPercentagesDs.refresh(params);
        }
    }

    private void removeOpenAction() {
        ComponentsHelper.walkComponents(tabSheet, new ComponentVisitor() {
            @Override
            public void visit(Component component, String name) {
                String simpleName = component.getClass().getSimpleName();
                if (simpleName.equalsIgnoreCase("WebLookupPickerField")) {
                    ((WebLookupPickerField) component).removeAction("open");
                }
            }
        });
    }

    @Override
    public void ready() {
        super.ready();
        personTable.getColumn("personImage").setWidth(70);
        personTable.getColumn("matrix").setWidth(55);
        personList.getColumn("personImage").setWidth(70);
    }

    private void setDockableSplit() {
        CubaHorizontalSplitPanel firstTabSplit = (CubaHorizontalSplitPanel) WebComponentsHelper.unwrap(split);
        firstTabSplit.setMaxSplitPosition(400, Sizeable.Unit.PIXELS);
        firstTabSplit.setDockable(true);
        firstTabSplit.addSplitterClickListener((AbstractSplitPanel.SplitterClickListener) event -> firstTabSplit.setSplitPosition(firstTabSplit.getSplitPosition() > 0 ? 0 : 300));

        CubaHorizontalSplitPanel secondTabSplit = (CubaHorizontalSplitPanel) WebComponentsHelper.unwrap(splitForPerson);
        secondTabSplit.setMaxSplitPosition(500, Sizeable.Unit.PIXELS);
        secondTabSplit.setDockable(true);
        secondTabSplit.addSplitterClickListener((AbstractSplitPanel.SplitterClickListener) event -> secondTabSplit.setSplitPosition(secondTabSplit.getSplitPosition() > 0 ? 0 : 400));
    }

    public Component matrix(PersonPercentage personPercentage) {
        if (personPercentage.getMatrix() == null || personPercentage.getMatrix() == 0) {
            return null;
        } else {
            Embedded embedded = componentsFactory.createComponent(Embedded.class);
            embedded.setAlignment(Alignment.MIDDLE_CENTER);
            embedded.setWidth("40px");
            embedded.setHeight("30px");
            embedded.setSource("theme://images/matrix/" + personPercentage.getMatrix() + ".png");
            embedded.setType(Embedded.Type.IMAGE);
            return embedded;
        }
    }

    private AssignmentExt getAssignment(UUID personGroupId) {
        LoadContext<AssignmentExt> loadContext = LoadContext.create(AssignmentExt.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e from base$AssignmentExt e " +
                        "where :sysDate between e.startDate and e.endDate " +
                        "  and e.primaryFlag = true " +
                        "and e.personGroup.id = :personGroupId")
                .setParameter("personGroupId", personGroupId)
                .setParameter("sysDate", CommonUtils.getSystemDate()))
                .setView("assignment.card");
        return dataManager.load(loadContext);
    }

    public Component personImage(PersonPercentage personPercentage) {
        return Utils.getPersonImageEmbedded(personPercentage.getAssignment().getPersonGroup().getPerson(), "50px", null);
    }

    public Component personImageForPosition(AssignmentExt assignment) {
        return Utils.getPersonImageEmbedded(assignment.getPersonGroup().getPerson(), "50px", null);
    }

    private int row = 0;

    public Component hierarchyInfo(PersonPercentage personPercentage) {
        GridLayout gridLayout = componentsFactory.createComponent(GridLayout.class);
        gridLayout.setStyleName("person-percentage-grid");
        gridLayout.setColumns(2);

        AssignmentExt assignment = personPercentage.getAssignment();

        if (assignment != null) {
            OrganizationGroupExt organizationGroup = assignment.getOrganizationGroup();
            PositionGroupExt positionGroup = assignment.getPositionGroup();
            PositionExt position = positionGroup != null ? positionGroup.getPosition() : null;
            JobGroup jobGroup = positionGroup != null ? positionGroup.getPosition().getJobGroup() : null;
            OrganizationExt organization = organizationGroup != null ? organizationGroup.getOrganization() : null;
            if (organization != null) {
                addRow(gridLayout, "PersonPercentage.organization.name", organization.getOrganizationName());
            }

            if (position != null) {
                addRow(gridLayout, "PersonPercentage.position.name", position.getPositionName());

                if (jobGroup != null) {
                    Job job = jobGroup.getJob();
                    if (job != null) {
                        addRow(gridLayout, "PersonPercentage.job.name", job.getJobName());
                    }
                }
            }

            if (organization != null && position != null) {
                addRow(gridLayout, "PersonPercentage.location", calculateLocation(assignment));
            }
        }

        return gridLayout;
    }

    private String calculateLocation(AssignmentExt assignment) {
        DicLocation location = assignment.getLocation();
        if (location == null) {
            location = assignment.getOrganizationGroup().getOrganization().getLocation();

            if (location == null) {
                location = assignment.getPositionGroup().getPosition().getLocation();
            }
        }
        return location != null ? location.getLangValue() : "";
    }

    public Component matchPercent(PersonPercentage personPercentage) {
        return createLabel(personPercentage.getMatch() + "%", "");
    }

    public Component matchPosition(PositionPercentage positionPercentage) {
        return createLabel(positionPercentage.getMatch() + "%", "");
    }

    private void addRow(GridLayout gridLayout, String labelKey, String value) {
        gridLayout.setRows(row + 1);
        gridLayout.add(createLabel(getMessage(labelKey)), 0, row);
        gridLayout.add(createLabel(value), 1, row);
        row++;
    }

    private Label createLabel(String value) {
        Label label = componentsFactory.createComponent(Label.class);
        label.setValue(value);
        return label;
    }

    private Label createLabel(String value, String cssClass) {
        Label label = createLabel(value);
        label.setStyleName(cssClass);
        return label;
    }

    public class MatrixFilter {
        private int id;
        private String image;

        MatrixFilter(int id, String image) {
            this.id = id;
            this.image = image;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}