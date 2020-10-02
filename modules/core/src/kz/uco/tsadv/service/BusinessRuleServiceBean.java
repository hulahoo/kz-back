package kz.uco.tsadv.service;

import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import kz.uco.tsadv.modules.administration.BusinessRule;
import kz.uco.tsadv.modules.administration.enums.RuleStatus;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;


@Service(BusinessRuleService.NAME)
public class BusinessRuleServiceBean implements BusinessRuleService {
    @Inject
    protected DataManager dataManager;
    @Inject
    private Metadata metadata;

    @Override
    public RuleStatus getRuleStatus(String ruleCode) {
        LoadContext<BusinessRule> loadContext = LoadContext.create(BusinessRule.class);

        loadContext.setQuery(LoadContext.createQuery(
                "select e from " + metadata.getClass(BusinessRule.class).getName() + " e where e.ruleCode = :ruleCode")
                .setParameter("ruleCode", ruleCode));
        BusinessRule rule = dataManager.load(loadContext);
        RuleStatus result;
        if (rule!=null&&rule.getRuleStatus()!=null)
            result=rule.getRuleStatus();
        else result=RuleStatus.DISABLE;
        return result;
    }

    @Override
    public String getBusinessRuleMessage(String ruleCode) {
        LoadContext<BusinessRule> loadContext = LoadContext.create(BusinessRule.class);

        loadContext.setQuery(LoadContext.createQuery(
                "select e from " + metadata.getClass(BusinessRule.class).getName() + " e where e.ruleCode = :ruleCode")
                .setParameter("ruleCode", ruleCode));
        BusinessRule rule = dataManager.load(loadContext);
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String langOrder = AppContext.getProperty("base.abstractDictionary.langOrder");
        if (rule==null){
            return "";
        }
        if (langOrder != null) {
            List<String> langs = Arrays.asList(langOrder.split(";"));
            if (rule.getRuleStatus().equals(RuleStatus.ERROR)) {
                switch (langs.indexOf(language)) {
                    case 0: {
                        return rule.getErrorTextLang1();
                    }
                    case 1: {
                        return rule.getErrorTextLang2();
                    }
                    case 2: {
                        return rule.getErrorTextLang3();
                    }
                    case 3: {
                        return rule.getErrorTextLang4();
                    }
                    case 4: {
                        return rule.getErrorTextLang5();
                    }
                    default:
                        break;
                }
            }
            if (rule.getRuleStatus().equals(RuleStatus.WARNING)) {

                switch (langs.indexOf(language)) {
                    case 0: {
                        return rule.getWarningTextLang1();
                    }
                    case 1: {
                        return rule.getWarningTextLang2();
                    }
                    case 2: {
                        return rule.getWarningTextLang3();
                    }
                    case 3: {
                        return rule.getWarningTextLang4();
                    }
                    case 4: {
                        return rule.getWarningTextLang5();
                    }
                    default:
                        break;
                }
            }

        }
        return null;
    }


}