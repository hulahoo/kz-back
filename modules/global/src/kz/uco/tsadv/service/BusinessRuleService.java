package kz.uco.tsadv.service;


import kz.uco.tsadv.modules.administration.enums.RuleStatus;

public interface BusinessRuleService {
    String NAME = "tsadv_BusinessRuleService";
    RuleStatus getRuleStatus(String ruleCode);
    String getBusinessRuleMessage(String ruleCode);
}