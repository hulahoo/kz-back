package kz.uco.tsadv.datasource;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.impl.CustomCollectionDatasource;
import kz.uco.tsadv.modules.learning.model.BudgetRequest;
import kz.uco.tsadv.modules.learning.model.dto.BudgetRequestItemsRowDto;
import kz.uco.tsadv.modules.personal.dictionary.DicCostType;
import kz.uco.tsadv.service.BudgetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;


public class BudgetDatasource extends CustomCollectionDatasource<BudgetRequestItemsRowDto, UUID> {
    private static final Logger log = LoggerFactory.getLogger(BudgetDatasource.class);

    protected BudgetService budgetService = AppBeans.get(BudgetService.class);


    @Override
    protected Collection<BudgetRequestItemsRowDto> getEntities(Map<String, Object> params) {

        BudgetRequest budgetRequest = (BudgetRequest) params.get("budgetRequest");

        CollectionDatasource<DicCostType, UUID> dicCostTypeDs = (CollectionDatasource<DicCostType, UUID>) params.get("dicCostTypeDs");

        return budgetService.getItems(budgetRequest, dicCostTypeDs != null ? dicCostTypeDs.getItems() : null);

    }
}
