package kz.uco.tsadv.web.modules.personal.hierarchyelement.old.v68;

import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.components.Tree;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.base.entity.shared.Hierarchy;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.personal.model.HierarchyElementExt;
import kz.uco.tsadv.service.HierarchyService;

import javax.inject.Inject;
import java.util.List;

@UiController("base$HierarchyElement.lookup")
@UiDescriptor("hierarchy-element-lookup.xml")
@LookupComponent("tree")
public class HierarchyElementLookup extends StandardLookup<HierarchyElementExt> {

    @Inject
    protected CommonService commonService;

    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    protected TextField<String> searchField;
    @Inject
    protected LookupField<Hierarchy> hierarchyLookup;
    @Inject
    protected Tree<HierarchyElementExt> tree;
    @Inject
    protected CollectionContainer<Hierarchy> hierarchiesDc;
    @Inject
    protected CollectionContainer<HierarchyElementExt> hierarchyElementDc;
    @Inject
    protected HierarchyService hierarchyService;
    @Inject
    protected Messages messages;
    @Inject
    protected Notifications notifications;
    @Inject
    protected Metadata metadata;

    protected boolean isSearch;
    @Inject
    protected CollectionLoader<Hierarchy> hierarchiesDl;

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        hierarchiesDl.load();
        refresh();
    }

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

        MapScreenOptions options = (MapScreenOptions) event.getOptions();
        if (options.getParams().containsKey("hierarchy")) {
            hierarchyLookup.setValue((Hierarchy) options.getParams().get("hierarchy"));
            hierarchyLookup.setEnabled(false);
        }

        hierarchyLookup.addValueChangeListener(e -> refresh());
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
}