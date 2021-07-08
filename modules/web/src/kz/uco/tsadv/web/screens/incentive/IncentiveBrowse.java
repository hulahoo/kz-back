package kz.uco.tsadv.web.screens.incentive;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.actions.list.AddAction;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.base.entity.shared.Hierarchy;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.service.HierarchyService;
import kz.uco.tsadv.web.screens.organizationincentiveindicators.OrganizationIncentiveIndicatorsEdit;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@UiController("tsadv_IncentiveBrowse")
@UiDescriptor("incentive-browse.xml")
public class IncentiveBrowse extends Screen {

    @Inject
    protected CollectionContainer<HierarchyElementExt> hierarchyElementDc;
    @Inject
    protected CollectionLoader<Hierarchy> hierarchiesDl;
    @Inject
    protected CollectionContainer<Hierarchy> hierarchiesDc;
    @Inject
    protected InstanceContainer<PositionExt> positionDc;
    @Inject
    protected InstanceContainer<OrganizationExt> organizationDc;

    @Inject
    protected HierarchyService hierarchyService;
    @Inject
    protected Metadata metadata;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected Notifications notifications;
    @Inject
    protected Messages messages;
    @Inject
    protected Tree<HierarchyElementExt> tree;
    @Inject
    protected TextField<String> searchField;
    @Inject
    protected VBoxLayout cssLayout;
    @Named("organizationIncentiveFlagsTable.add")
    private AddAction<OrganizationIncentiveFlag> organizationIncentiveFlagsTableAdd;
    @Inject
    private ScreenBuilders screenBuilders;
    @Inject
    private CollectionLoader<OrganizationIncentiveFlag> organizationIncentiveFlagsDl;
    @Inject
    private CollectionContainer<OrganizationIncentiveFlag> organizationIncentiveFlagsDc;
    @Inject
    private Table<OrganizationIncentiveFlag> organizationIncentiveFlagsTable;

    protected boolean isSearch;

    protected String ELEMENT_TYPE_SCREEN_PARAM = "ELEMENT_TYPE";
    protected String ELEMENT_TYPE_SCREEN_VALUE = "";
    @Named("organizationIncentiveIndicatorsTable.add")
    private AddAction<OrganizationIncentiveIndicators> organizationIncentiveIndicatorsTableAdd;
    @Inject
    private Button organizationIncentiveFlagsTableAddBtn;
    @Inject
    private Button organizationIncentiveIndicatorsTableAddBtn;
    @Inject
    private CollectionLoader<OrganizationIncentiveIndicators> organizationIncentiveIndicatorsDl;
    @Inject
    private Table<OrganizationIncentiveIndicators> organizationIncentiveIndicatorsTable;
    @Inject
    private CollectionContainer<OrganizationIncentiveIndicators> organizationIncentiveIndicatorsDc;

    @Subscribe
    protected void onInit(InitEvent event) {
        MapScreenOptions screenOptions = (MapScreenOptions) event.getOptions();

        if(screenOptions == null || !screenOptions.getParams().containsKey(ELEMENT_TYPE_SCREEN_PARAM)){
            notifications.create().withCaption("No screen params").show();
            closeWithDefaultAction();
            return;
        }

        ELEMENT_TYPE_SCREEN_VALUE = (String) screenOptions.getParams().get(ELEMENT_TYPE_SCREEN_PARAM);

        initTableDoubleClickActions();

        tree.setIconProvider(hierarchyElement -> {
            if (hierarchyElement.getElementType() == null) return null;
            switch (hierarchyElement.getElementType()) {
                case ORGANIZATION:
                    return "font-icon:BANK";
                case POSITION:
                    return "font-icon:BRIEFCASE";
                default:
                    return "font-icon:USER";
            }
        });

        searchField.addEnterPressListener(enterPressEvent -> search());
    }

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        initHierarchiesDc();
    }

    @Subscribe(id = "hierarchiesDc", target = Target.DATA_CONTAINER)
    public void onHierarchiesDcItemChange(InstanceContainer.ItemChangeEvent<Hierarchy> event) {
        refresh();
    }

    protected void initHierarchiesDc(){
        Hierarchy hierarchy = loadHierarchy();
        hierarchiesDc.getMutableItems().add(hierarchy);
        hierarchiesDc.setItem(hierarchy);
    }

    protected Hierarchy loadHierarchy(){
        String hierarchyCode = ELEMENT_TYPE_SCREEN_VALUE.equals("ORGANIZATION") ? "1" : "2";
        Hierarchy hierarchy = dataManager.load(Hierarchy.class)
                .query("select e from base$Hierarchy e where e.type.code = :code")
                .parameter("code",hierarchyCode)
                .one();

        return hierarchy;
    }

    protected void refresh() {
        Hierarchy hierarchy = hierarchiesDc.getItemOrNull();
        if (hierarchy != null) {
            tree.collapseTree();
            hierarchyElementDc.getMutableItems().clear();
            addChildren(null);
        }
    }

    protected void addChildren(HierarchyElementExt parent) {
        List<HierarchyElementExt> childHierarchyElement = hierarchyService.getChildHierarchyElement(hierarchiesDc.getItem(), parent);
        for (HierarchyElementExt HierarchyElementExt : childHierarchyElement) {
            hierarchyElementDc.getMutableItems().add(HierarchyElementExt);
            if (!isSearch && HierarchyElementExt.getHasChild()) {
                hierarchyElementDc.getMutableItems().add(createFakeChild(HierarchyElementExt));
            }
        }
    }

    protected HierarchyElementExt createFakeChild(HierarchyElementExt HierarchyElementExt) {
        HierarchyElementExt fakeChild = metadata.create(HierarchyElementExt.class);
        fakeChild.setParent(HierarchyElementExt);
        fakeChild.setParentFromGroup(HierarchyElementExt);
        fakeChild.setParentName("fake");
        return fakeChild;
    }

    public void close() {
        getWindow().openEditor(hierarchyElementDc.getItem(), WindowManager.OpenType.DIALOG, ParamsMap.of("close", Boolean.TRUE))
                .addCloseWithCommitListener(this::refresh);
    }

    public void search() {
        String searchText = searchField.getValue();
        if (searchText == null) {
            refresh();
            return;
        } else if (searchText.length() <= 3) {
            notifications.create(Notifications.NotificationType.HUMANIZED)
                    .withDescription(messages.getMainMessage("search.text.small"))
                    .show();
            return;
        }

        List<HierarchyElementExt> hierarchyElementList = hierarchyService.search(hierarchiesDc.getItem(), searchText);

        if (hierarchyElementList.isEmpty()) {
            refresh();
        } else {
            isSearch = true;

            List<HierarchyElementExt> hierarchyElementDcMutableItems = hierarchyElementDc.getMutableItems();
            hierarchyElementDcMutableItems.clear();
            hierarchyElementDcMutableItems.addAll(hierarchyElementList);
            tree.expandTree();

            isSearch = false;
        }
    }

    @Subscribe("tree")
    protected void onTreeExpand(Tree.ExpandEvent<HierarchyElementExt> event) {
        if (isSearch) return;
        HierarchyElementExt expandItem = event.getExpandedItem();

        Collection<HierarchyElementExt> children = hierarchyElementDc.getItems().stream()
                .filter(hierarchyElement -> Objects.equals(hierarchyElement.getParentFromGroup(), expandItem))
                .collect(Collectors.toList());
        if (children.size() == 1) {
            HierarchyElementExt child = children.iterator().next();
            if ("fake".equals(child.getParentName())) {
                hierarchyElementDc.getMutableItems().remove(child);
                addChildren(expandItem);
            }
        }
    }

    @Subscribe(id = "hierarchyElementDc", target = Target.DATA_CONTAINER)
    protected void onHierarchyElementDcItemChange(InstanceContainer.ItemChangeEvent<HierarchyElementExt> event) {
        HierarchyElementExt hierarchyElement = event.getItem();
        enableAddActions(event.getItem() != null);
        if (hierarchyElement == null){
            loadEmptyIncentives();
            return;
        }

        loadIncentives();
    }


    protected void enableAddActions(boolean enable){
        organizationIncentiveFlagsTableAddBtn.setEnabled(enable);
        organizationIncentiveIndicatorsTableAddBtn.setEnabled(enable);
    }

    protected void initTableDoubleClickActions(){
        organizationIncentiveFlagsTable.setItemClickAction(new BaseAction("doubleClickAction")
                .withHandler(e -> editOrganizationIncentiveFlag()));
        organizationIncentiveIndicatorsTable.setItemClickAction(new BaseAction("doubleClickAction")
                .withHandler(e -> editOrganizationIncentiveIndicator()));
    }

    protected void loadIncentives(){
        loadOrganizationIncentiveFlags();
        loadOrganizationIncentiveIndicators();
    }

    protected void loadEmptyIncentives(){
        loadEmptyOrganizationIncentiveFlags();
        loadEmptyOrganizationIncentiveIndicators();
    }

    public void addOrganizationIncentiveFlag() {
        Screen editScreen = screenBuilders.editor(OrganizationIncentiveFlag.class,this)
                        .withInitializer(i -> {i.setOrganizationGroup(hierarchyElementDc.getItem().getOrganizationGroup());})
                        .build();
        editScreen.addAfterCloseListener((l) -> loadOrganizationIncentiveFlags());
        editScreen.show();

    }

    protected void editOrganizationIncentiveFlag(){
        Screen editScreen = screenBuilders.editor(OrganizationIncentiveFlag.class,this)
                .editEntity(organizationIncentiveFlagsDc.getItem())
                .build();
        editScreen.addAfterCloseListener((l) -> loadOrganizationIncentiveFlags());
        editScreen.show();
    }

    protected void loadOrganizationIncentiveFlags(){
        organizationIncentiveFlagsDl.setQuery("select e from tsadv_OrganizationIncentiveFlag e where e.organizationGroup = :organizationGroup");
        organizationIncentiveFlagsDl.setParameter("organizationGroup",hierarchyElementDc.getItem().getOrganizationGroup());
        organizationIncentiveFlagsDl.load();
    }

    protected void loadEmptyOrganizationIncentiveFlags(){
        organizationIncentiveFlagsDl.setQuery("select e from tsadv_OrganizationIncentiveFlag e where 1<>1");
        organizationIncentiveFlagsDl.setParameters(new HashMap<>());
        organizationIncentiveFlagsDl.load();
    }

    public void addOrganizationIncentiveIndicator() {
        Screen editScreen = screenBuilders.editor(OrganizationIncentiveIndicators.class,this)
                .withInitializer(i -> i.setOrganizationGroup(hierarchyElementDc.getItem().getOrganizationGroup()))
                .build();
        editScreen.addAfterCloseListener((l) -> loadOrganizationIncentiveIndicators());
        editScreen.show();
    }

    protected void editOrganizationIncentiveIndicator(){
        Screen editScreen = screenBuilders.editor(OrganizationIncentiveIndicators.class,this)
                .editEntity(organizationIncentiveIndicatorsDc.getItem())
                .build();
        editScreen.addAfterCloseListener((l) -> loadOrganizationIncentiveIndicators());
        editScreen.show();
    }

    protected void loadOrganizationIncentiveIndicators(){
        organizationIncentiveIndicatorsDl.setQuery("select e from tsadv_OrganizationIncentiveIndicators e where e.organizationGroup = :organizationGroup");
        organizationIncentiveIndicatorsDl.setParameter("organizationGroup",hierarchyElementDc.getItem().getOrganizationGroup());
        organizationIncentiveIndicatorsDl.load();
    }

    protected void loadEmptyOrganizationIncentiveIndicators(){
        organizationIncentiveIndicatorsDl.setQuery("select e from tsadv_OrganizationIncentiveIndicators e where 1<>1");
        organizationIncentiveIndicatorsDl.setParameters(new HashMap<>());
        organizationIncentiveIndicatorsDl.load();
    }

}