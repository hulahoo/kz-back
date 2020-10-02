package kz.uco.tsadv.web.modules.learning.budgetrequest;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.web.modules.personal.common.Utils;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.learning.dictionary.DicBudgetStatus;
import kz.uco.tsadv.modules.learning.model.Budget;
import kz.uco.tsadv.modules.learning.model.BudgetRequest;

import javax.inject.Inject;
import java.util.*;

public class BudgetRequestCopy extends AbstractWindow {

    @Inject
    private PickerField<Budget> budget;

    @Inject
    private CollectionDatasource<BudgetRequest, UUID> budgetRequestsDs;

    @Inject
    private CommonService commonService;

    @Inject
    private Metadata metadata;

    @Inject
    private UserSessionSource userSessionSource;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        Map<String, Object> budgetParams = new HashMap<>();
        budgetParams.put("filterByRequestDate", Boolean.TRUE);
        Utils.customizeLookup(budget, "tsadv$Budget.browse", WindowManager.OpenType.DIALOG, budgetParams);

        addAction(new BaseAction("windowCommit")
                .withHandler(event -> {
                    budgetRequestsDs.getDsContext().commit();
                    close(COMMIT_ACTION_ID);
                })
                .withCaption(getMessage("actions.Ok")));


        addAction(new BaseAction("windowClose")
                .withHandler(event -> {
                    close(CLOSE_ACTION_ID);
                })
                .withCaption(getMessage("actions.Cancel")));
    }


    public void copy() {
        if (budget.getValue() == null)
            showNotification(getMessage("BudgetRequest.selectBudget"));
        else {
            Budget b = budget.getValue();
            if (b.getPreviousBudget() == null)
                showNotification(getMessage("BudgetRequestCopy.nullPreviousBudget"));
            else {
                Map<String, Object> params = new HashMap<>();
                params.put("previousBudgetId", ((Budget) budget.getValue()).getPreviousBudget().getId());
                params.put("userPersonGroupId", userSessionSource.getUserSession().getAttribute("userPersonGroupId"));

                List<BudgetRequest> previousRequests = commonService.getEntities(BudgetRequest.class,
                        "select e from tsadv$BudgetRequest e " +
                                " where e.budget.id = :previousBudgetId" +
                                " and e.initiatorPersonGroup.id = :userPersonGroupId ",
                        params,
                        "budgetRequest.view");
                DicBudgetStatus draft = commonService.getEntity(DicBudgetStatus.class, "DRAFT");
                Calendar c = Calendar.getInstance();
                c.setTime(CommonUtils.getSystemDate());
                c.set(Calendar.DAY_OF_MONTH, 1);

                for (BudgetRequest previousRequest : previousRequests) {
                    BudgetRequest copy = metadata.create(BudgetRequest.class);
                    copy.setBudget(budget.getValue());
                    copy.setStatus(draft);
                    copy.setInitiatorPersonGroup(previousRequest.getInitiatorPersonGroup());
                    copy.setCourse(previousRequest.getCourse());
                    copy.setCourseName(previousRequest.getCourseName());
                    copy.setComment(previousRequest.getComment());
                    copy.setEmployeesCount(previousRequest.getEmployeesCount());
                    copy.setLearningCosts(previousRequest.getLearningCosts());
                    copy.setTripCosts(previousRequest.getTripCosts());
                    copy.setLearningType(previousRequest.getLearningType());
                    copy.setProviderCompany(previousRequest.getProviderCompany());
                    copy.setMonth(c.getTime());
                    budgetRequestsDs.addItem(copy);
                }
            }
        }
    }


}