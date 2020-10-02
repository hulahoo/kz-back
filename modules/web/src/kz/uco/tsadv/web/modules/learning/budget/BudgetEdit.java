package kz.uco.tsadv.web.modules.learning.budget;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.ValidationException;
import kz.uco.tsadv.web.modules.personal.common.Utils;
import kz.uco.tsadv.modules.learning.model.Budget;

import javax.inject.Inject;
import java.util.Date;
import java.util.Map;

public class BudgetEdit extends AbstractEditor<Budget> {

    @Inject
    private FieldGroup fieldGroup;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        Utils.customizeLookup(fieldGroup.getField("previousBudget").getComponent(), "tsadv$Budget.browse", WindowManager.OpenType.DIALOG, null);

        fieldGroup.getField("budgetStartDate").addValidator(value -> {
            if (value != null && getItem().getBudgetEndDate() != null) {
                Date startDate = (Date) value;
                if (startDate.after(getItem().getBudgetEndDate()))
                    throw new ValidationException(getMessage("AbstractHrEditor.startDate.validatorMsg"));
            }
        });

        fieldGroup.getField("budgetEndDate").addValidator(value -> {
            if (value != null && getItem().getBudgetStartDate() != null) {
                Date endDate = (Date) value;
                if (endDate.before(getItem().getBudgetStartDate()))
                    throw new ValidationException(getMessage("AbstractHrEditor.endDate.validatorMsg"));
            }
        });

        fieldGroup.getField("requestStartDate").addValidator(value -> {
            if (value != null && getItem().getRequestEndDate() != null) {
                Date startDate = (Date) value;
                if (startDate.after(getItem().getRequestEndDate()))
                    throw new ValidationException(getMessage("BudgetEdit.requestStartDate.validatorMsg"));
            }
        });

        fieldGroup.getField("requestEndDate").addValidator(value -> {
            if (value != null && getItem().getRequestStartDate() != null) {
                Date endDate = (Date) value;
                if (endDate.before(getItem().getRequestStartDate()))
                    throw new ValidationException(getMessage("BudgetEdit.requestEndDate.validatorMsg"));
            }
        });

        fieldGroup.getField("previousBudget").addValidator(value -> {
            if (value != null && getItem().getId() != null) {
                Budget previousBudget = (Budget) value;
                if (previousBudget.getId().equals(getItem().getId()))
                    throw new ValidationException(getMessage("BudgetEdit.previousBudget.validatorMsg"));
            }
        });

    }
}