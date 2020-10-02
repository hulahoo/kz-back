package kz.uco.tsadv.web.modules.learning.budget;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.base.web.components.CustomFilter;
import kz.uco.tsadv.modules.learning.model.Budget;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class BudgetBrowse extends AbstractLookup {

    @Inject
    private GroupDatasource<Budget, UUID> budgetsDs;
    @Inject
    private GroupTable<Budget> budgetsTable;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (!getContext().equals(WindowManager.OpenType.NEW_TAB)) {
            budgetsTable.getButtonsPanel().setVisible(false);
            budgetsTable.getActions().forEach(a -> a.setEnabled(false));

            if (params.containsKey("filterByRequestDate")) {
                String entityAlias = CustomFilter.getEntityAlias(budgetsDs.getQuery());
                String queryFilter = String.format(" and (:session$systemDate between %s.requestStartDate and %s.requestEndDate )" +
                                " and (%s.status.code = 'APPROVED') ",
                        entityAlias,
                        entityAlias,
                        entityAlias);

                budgetsDs.setQuery(CustomFilter.getFilteredQuery(budgetsDs.getQuery(), queryFilter));
                budgetsDs.refresh();
            }
        }

    }
}