package kz.uco.tsadv.service;


import kz.uco.tsadv.modules.learning.model.BudgetRequest;
import kz.uco.tsadv.modules.learning.model.dto.BudgetRequestItemsRowDto;
import kz.uco.tsadv.modules.personal.dictionary.DicCostType;

import java.util.Collection;

public interface BudgetService {
    String NAME = "tsadv_BudgetService";

    Collection<BudgetRequestItemsRowDto> getItems(BudgetRequest budgetRequest, Collection<DicCostType> dicCostTypes);
}