package kz.uco.tsadv.web.dialogs;

import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import kz.uco.tsadv.datasource.OrganizationTreeDatasource;
import kz.uco.tsadv.global.entity.OrganizationTree;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * How to use this dialog window:
 * <p>
 * PickerField pickerField = (PickerField) fg.getFieldNN("organizationGroup").getComponent();
 * pickerField.removeAction(PickerField.LookupAction.NAME);
 * PickerField.LookupAction lookupAction = new PickerField.LookupAction(pickerField) {
 * public OrganizationGroupExt transformValueFromLookupWindow(Entity valueFromLookupWindow) {
 * return ((OrganizationTree) valueFromLookupWindow).getOrganizationGroupExt();
 * }
 * };
 * pickerField.addAction(lookupAction);
 * lookupAction.setLookupScreen("organization-tree");
 * lookupAction.setLookupScreenOpenType(WindowManager.OpenType.DIALOG);
 *
 * @author adilbekov.yernar
 */
public class OrganizationTreeLookup extends AbstractLookup {

    @Inject
    private Tree<OrganizationTree> organizationTree;

    @Inject
    private TextField<String> searchField;
    @Inject
    private LinkButton removeSearchText;
    @Inject
    private OrganizationTreeDatasource organizationTreeDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        organizationTreeDs.refresh(params);

        searchField.addEnterPressListener(e -> search(searchField.getValue()));
        removeSearchText.setAction(new BaseAction("remove-st") {
            @Override
            public void actionPerform(Component component) {
                searchField.setValue(null);
                restoreTree();
            }
        });
    }

    private void search(String searchText) {
        if (StringUtils.isBlank(searchText)) {
            restoreTree();
        } else {
            organizationTree.setStyleProvider(new Tree.StyleProvider<OrganizationTree>() {
                @Override
                public String getStyleName(OrganizationTree entity) {
                    return entity.getOrganizationName().toLowerCase().contains(searchText.toLowerCase()) ? "org-tree-search-fi" : null;
                }
            });

            organizationTreeDs.clear();

            Map<String, Object> params = new HashMap<>();
            params.putAll(organizationTreeDs.getLastRefreshParameters());
            params.put(OrganizationTreeDatasource.SEARCH_TEXT, searchText);

            organizationTreeDs.refresh(params);

            organizationTreeDs.detachListener(organizationTreeDs.getItems());

            organizationTree.expandTree();

            organizationTreeDs.attachListener();
        }
    }

    private void restoreTree() {
        organizationTree.setStyleProvider(null);

        organizationTree.collapseTree();
        organizationTreeDs.clear();

        Map<String, Object> params = new HashMap<>();
        params.put(OrganizationTreeDatasource.HIERARCHY_ID, organizationTreeDs.getLastRefreshParameters().get(OrganizationTreeDatasource.HIERARCHY_ID));
        params.put(OrganizationTreeDatasource.HIERARCHY_ELEMENT_PARENT_ID, null);
        organizationTreeDs.refresh(params);
    }
}