package kz.uco.tsadv.web.businessrule;

import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.administration.BusinessRule;
import kz.uco.tsadv.modules.administration.enums.RuleStatus;

import javax.inject.Inject;
import javax.inject.Named;

public class BusinessRuleEdit extends AbstractEditor<BusinessRule> {
    @Named("fieldGroup.ruleStatus")
    protected LookupField<Object> ruleStatusField;
    @Inject
    protected Datasource<BusinessRule> businessRuleDs;
    @Named("fieldGroup.ruleCode")
    protected TextField ruleCodeField;
    @Named("fieldGroupError.errorTextLang1")
    protected ResizableTextArea errorTextLang1Field;
    @Named("fieldGroupWarning.warningTextLang1")
    protected ResizableTextArea warningTextLang1Field;
    @Inject
    protected FieldGroup fieldGroupWarning;
    @Inject
    protected FieldGroup fieldGroupError;
    @Inject
    protected CommonService commonService;
    @Named("fieldGroup.ruleName")
    protected TextField ruleNameField;

    @Override
    public void ready() {
        super.ready();

        if (!PersistenceHelper.isNew(businessRuleDs.getItem())) {
            ruleCodeField.setEditable(false);
            this.setFocusComponent("fieldGroup");
        }

        checkRequiredFields(getItem().getRuleStatus());

        ruleStatusField.addValueChangeListener(e -> {
            checkRequiredFields((RuleStatus) e.getValue());
        });
    }

    @Override
    protected void initNewItem(BusinessRule item) {
        super.initNewItem(item);

        item.setRuleStatus(RuleStatus.DISABLE);
    }

    private void checkRequiredFields(RuleStatus ruleStatus) {
        boolean isError = ruleStatus.equals(RuleStatus.ERROR),
                isDisabled = ruleStatus.equals(RuleStatus.DISABLE);
        warningTextLang1Field.setRequired(!isDisabled && !isError);
        errorTextLang1Field.setRequired(!isDisabled && isError);

        fieldGroupError.setVisible(errorTextLang1Field.isRequired());
        fieldGroupWarning.setVisible(warningTextLang1Field.isRequired());
    }

}