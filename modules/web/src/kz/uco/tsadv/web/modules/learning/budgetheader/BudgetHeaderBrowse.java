package kz.uco.tsadv.web.modules.learning.budgetheader;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import kz.uco.tsadv.modules.learning.config.BudgetConfig;
import kz.uco.tsadv.modules.learning.model.BudgetHeader;
import kz.uco.tsadv.modules.learning.model.BudgetRequest;
import kz.uco.tsadv.modules.learning.model.BudgetRequestItem;
import kz.uco.tsadv.modules.performance.enums.BudgetHeaderStatus;
import kz.uco.tsadv.modules.personal.model.HierarchyElementExt;
import kz.uco.tsadv.service.OrganizationService;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class BudgetHeaderBrowse extends AbstractLookup {
    @Inject
    protected CollectionDatasource<BudgetHeader, UUID> budgetHeadersDs;
    @Inject
    protected BudgetConfig budgetConfig;
    @Inject
    protected OrganizationService organizationService;

    @Named("budgetHeadersTable.details")
    protected Action budgetHeadersTableDetails;
    @Named("budgetHeadersTable.history")
    protected Action budgetHeadersTableHistory;
    @Named("budgetHeadersTable.copy")
    protected Action budgetHeadersTableCopy;
    @Inject
    protected Filter filter;
    @Named("budgetHeadersTable.create")
    protected CreateAction budgetHeadersTableCreate;
    @Named("budgetHeadersTable.remove")
    protected RemoveAction budgetHeadersTableRemove;
    @Inject
    protected Metadata metadata;
    @Inject
    protected DataManager dataManager;

    protected String hierarchyId;

    @Inject
    protected Tree<HierarchyElementExt> organizationTree;
    @Inject
    protected TextField<String> searchField;
    @Inject
    protected LinkButton removeSearchText;
    @Inject
    protected HierarchicalDatasource<HierarchyElementExt, UUID> organizationTreeDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        hierarchyId = budgetConfig.getBudgetOrgStructureId();
        params.put("hId", hierarchyId);

        organizationTreeDs.addItemChangeListener(e -> {
            if (e.getItem() != null) {
                List<UUID> organizationsUUID = organizationService.getAllNestedOrganizationIds(e.getItem().getOrganizationGroup().getId(),
                        hierarchyId != null ? UUID.fromString(hierarchyId) : null);
                filter.apply(false);
                refreshTableDs(organizationsUUID);
                budgetHeadersTableCreate.setInitialValues(
                        ParamsMap.of("organizationGroup", e.getItem().getOrganizationGroup())
                );
            } else {
                budgetHeadersDs.setQuery("select e from tsadv$BudgetHeader e");
                budgetHeadersDs.refresh();
            }
        });

        searchField.addEnterPressListener(e -> search(searchField.getValue()));
        removeSearchText.setAction(new BaseAction("remove-st") {
            @Override
            public void actionPerform(Component component) {
                searchField.setValue(null);
                restoreTree();
            }
        });

        budgetHeadersTableDetails.setEnabled(false);
        budgetHeadersTableHistory.setEnabled(false);
        budgetHeadersTableCopy.setEnabled(false);
        budgetHeadersDs.addItemChangeListener(e -> {
            if (e.getItem() != null) {
                budgetHeadersTableDetails.setEnabled(true);
                budgetHeadersTableHistory.setEnabled(true);
                budgetHeadersTableCopy.setEnabled(true);
                budgetHeadersTableRemove.setEnabled(e.getItem().getStatus().equals(BudgetHeaderStatus.DRAFT));
            } else {
                budgetHeadersTableDetails.setEnabled(false);
                budgetHeadersTableHistory.setEnabled(false);
                budgetHeadersTableCopy.setEnabled(false);
            }
        });

        budgetHeadersTableCreate.setAfterWindowClosedHandler((window, closeActionId) -> {
            budgetHeadersDs.refresh();
        });

        organizationTreeDs.setAllowCommit(false);
    }

    private void refreshTableDs(List<UUID> organizationsId) {
        budgetHeadersDs.setQuery(String.format("select e from tsadv$BudgetHeader e" +
                " where e.organizationGroup.id in :custom$organizationsId"));
        Map<String, Object> params = new HashMap<>();
        params.put("organizationsId", organizationsId);
        budgetHeadersDs.refresh(params);
    }

    public void details() {
        openWindow("tsadv$BudgetRequest.browse",
                WindowManager.OpenType.THIS_TAB, ParamsMap.of("budgetHeaderId", budgetHeadersDs.getItem().getId(),
                        "budgetHeader", budgetHeadersDs.getItem()));
    }

    public void history() {
        openWindow("tsadv$BudgetHeaderHistory.browse", WindowManager.OpenType.THIS_TAB, ParamsMap.of("budgetHeaderId", budgetHeadersDs.getItem()));
    }

    public void copy() {
        BudgetHeader toCopy = budgetHeadersDs.getItem();

        BudgetHeader copy = metadata.getTools().deepCopy(toCopy);
        copy.setHeaderName(copy.getHeaderName() + " " + getMessage(getMessage("plusCopy")));
        copy.setId(UUID.randomUUID());
        copy.setStatus(BudgetHeaderStatus.DRAFT);

        toCopy = dataManager.reload(toCopy, "budgetHeader-with-request");
        List<BudgetRequest> budgetRequestsCopy = new ArrayList<>();
        List<BudgetRequestItem> budgetRequestItemsCopy = new ArrayList<>();

        toCopy.getBudgetRequests().forEach(budgetRequest -> {
            BudgetRequest copyBR = metadata.getTools().deepCopy(budgetRequest);
            copyBR.setId(UUID.randomUUID());
            copyBR.setBudgetHeader(copy);
            copyBR.setInitiatorPersonGroup(copy.getResponsiblePerson());
            copyBR.setBudget(copy.getBudget());
            budgetRequestsCopy.add(copyBR);

            budgetRequest = dataManager.reload(budgetRequest, "budgetRequest-with-items");

            List<BudgetRequestItem> budgetRequestItems = budgetRequest.getBudgetRequestItems();

            budgetRequestItems.forEach(budgetRequestItem -> {
                BudgetRequestItem budgetRequestItemCopy = metadata.getTools().deepCopy(budgetRequestItem);
                budgetRequestItemCopy.setId(UUID.randomUUID());
                budgetRequestItemCopy.setName(budgetRequestItem.getName());
                budgetRequestItemCopy.setBudgetRequest(copyBR);
                budgetRequestItemsCopy.add(budgetRequestItemCopy);
            });

        });
        copy.setBudgetRequests(budgetRequestsCopy);


        CommitContext commitContext = new CommitContext();
        //level1
        commitContext.addInstanceToCommit(copy);
        budgetRequestsCopy.forEach(commitContext::addInstanceToCommit);
        budgetRequestItemsCopy.forEach(commitContext::addInstanceToCommit);
        copy.getBudgetRequests().forEach(commitContext::addInstanceToCommit);

        dataManager.commit(commitContext);
        budgetHeadersDs.refresh();
    }

    private void search(String searchText) {
        if (StringUtils.isBlank(searchText)) {
            restoreTree();
        } else {
            organizationTree.setStyleProvider(entity -> entity.getName().toLowerCase().contains(searchText.toLowerCase()) ? "org-tree-search-fi" : "non-bold");

            organizationTreeDs.clear();
            organizationTreeDs.refresh();

            HashMap<HierarchyElementExt, Boolean> map = new HashMap<>();

            for (HierarchyElementExt item : organizationTreeDs.getItems()) {
                if (item != null && map.get(item) == null) {
                    map.put(item, isContainSearchText(item, searchText, map));
                }
            }

            map.forEach((hierarchyElementExt, b) -> {
                if (!b) {
                    organizationTreeDs.removeItem(hierarchyElementExt);
                }
            });

            organizationTree.expandTree();
        }
    }

    private boolean isContainSearchText(HierarchyElementExt item, String searchText, HashMap<HierarchyElementExt, Boolean> map) {
        if (item == null) {
            return false;
        }
        if (map.get(item) != null) {
            return map.get(item);
        }
        if (item.getName() != null && item.getName().toLowerCase().contains(searchText.toLowerCase())) {
            return true;
        }
        if (item.getParent() != null) {
            boolean res = isContainSearchText(organizationTreeDs.getItem(item.getParent().getId()), searchText, map);
            map.put(item, res);
            return res;
        }
        return false;
    }

    private void restoreTree() {
        organizationTree.setStyleProvider(null);
        organizationTree.collapseTree();
        organizationTreeDs.clear();
        organizationTreeDs.refresh();
    }
}