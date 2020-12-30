package kz.uco.tsadv.web.modules.personal.hierarchyelement;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.LookupComponent;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.base.entity.shared.ElementType;
import kz.uco.base.entity.shared.Hierarchy;
import kz.uco.tsadv.modules.personal.model.HierarchyElementExt;
import kz.uco.tsadv.modules.personal.model.OrganizationExt;
import kz.uco.tsadv.modules.personal.model.PositionExt;
import kz.uco.tsadv.service.HierarchyService;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * @author Alibek Berdaulet
 */
@UiController("base$HierarchyElement.browse")
@UiDescriptor("hierarchy-element-browse.xml")
@LookupComponent("tree")
public class HierarchyElementBrowse extends StandardLookup<HierarchyElementExt> {

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
    protected LookupField<Hierarchy> hierarchyLookup;
    @Inject
    protected Tree<HierarchyElementExt> tree;
    @Inject
    protected TextField<String> searchField;
    @Inject
    protected VBoxLayout cssLayout;

    protected boolean isSearch;

    @Subscribe
    protected void onInit(InitEvent event) {
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
        hierarchiesDl.load();

        hierarchyLookup.setValue(hierarchiesDc.getItems().get(0));
    }

    @Subscribe("hierarchyLookup")
    protected void onHierarchyLookupValueChange(HasValue.ValueChangeEvent<Hierarchy> event) {
        refresh();
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
        cssLayout.getComponentNN("organizationFragment").setVisible(false);
        cssLayout.getComponentNN("positionFragment").setVisible(false);

        HierarchyElementExt hierarchyElement = event.getItem();
        if (hierarchyElement == null) return;

        if (ElementType.POSITION.equals(hierarchyElement.getElementType())) {
            PositionExt position = hierarchyElement.getPositionGroup().getPosition();
            if (positionDc.getView() != null && !PersistenceHelper.isLoadedWithView(position, positionDc.getView().getName()))
                position = dataManager.reload(position, positionDc.getView());
            positionDc.setItem(position);
            cssLayout.getComponentNN("positionFragment").setVisible(true);
        } else if (ElementType.ORGANIZATION.equals(hierarchyElement.getElementType())) {
            OrganizationExt organization = hierarchyElement.getOrganizationGroup().getOrganization();
            if (organizationDc.getView() != null && !PersistenceHelper.isLoadedWithView(organization, organizationDc.getView().getName()))
                organization = dataManager.reload(organization, organizationDc.getView());
            organizationDc.setItem(organization);
            cssLayout.getComponentNN("organizationFragment").setVisible(true);
        }
    }

}