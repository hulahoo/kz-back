package kz.uco.tsadv.modules.administration;

import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.modules.administration.enums.RuleStatus;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|ruleName")
@Table(name = "TSADV_BUSINESS_RULE")
@Entity(name = "tsadv$BusinessRule")
public class BusinessRule extends StandardEntity {
    private static final long serialVersionUID = 1848853353459443599L;

    @NotNull
    @Column(name = "RULE_CODE", nullable = false, unique = true)
    protected String ruleCode;

    @Column(name = "RULE_NAME")
    protected String ruleName;

    @Lob
    @Column(name = "DESCRIPTION")
    protected String description;

    @NotNull
    @Column(name = "RULE_STATUS", nullable = false)
    protected Integer ruleStatus;

    @Column(name = "ERROR_TEXT_LANG1", length = 1000)
    protected String errorTextLang1;

    @Column(name = "ERROR_TEXT_LANG2", length = 1000)
    protected String errorTextLang2;

    @Column(name = "ERROR_TEXT_LANG3", length = 1000)
    protected String errorTextLang3;

    @Column(name = "ERROR_TEXT_LANG4", length = 1000)
    protected String errorTextLang4;

    @Column(name = "ERROR_TEXT_LANG5", length = 1000)
    protected String errorTextLang5;

    @Column(name = "WARNING_TEXT_LANG1", length = 1000)
    protected String warningTextLang1;

    @Column(name = "WARNING_TEXT_LANG2", length = 1000)
    protected String warningTextLang2;

    @Column(name = "WARNING_TEXT_LANG3", length = 1000)
    protected String warningTextLang3;

    @Column(name = "WARNING_TEXT_LANG4", length = 1000)
    protected String warningTextLang4;

    @Column(name = "WARNING_TEXT_LANG5", length = 1000)
    protected String warningTextLang5;

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }


    public void setRuleStatus(RuleStatus ruleStatus) {
        this.ruleStatus = ruleStatus == null ? null : ruleStatus.getId();
    }

    public RuleStatus getRuleStatus() {
        return ruleStatus == null ? null : RuleStatus.fromId(ruleStatus);
    }

    public void setErrorTextLang1(String errorTextLang1) {
        this.errorTextLang1 = errorTextLang1;
    }

    public String getErrorTextLang1() {
        return errorTextLang1;
    }

    public void setErrorTextLang2(String errorTextLang2) {
        this.errorTextLang2 = errorTextLang2;
    }

    public String getErrorTextLang2() {
        return errorTextLang2;
    }

    public void setErrorTextLang3(String errorTextLang3) {
        this.errorTextLang3 = errorTextLang3;
    }

    public String getErrorTextLang3() {
        return errorTextLang3;
    }

    public void setErrorTextLang4(String errorTextLang4) {
        this.errorTextLang4 = errorTextLang4;
    }

    public String getErrorTextLang4() {
        return errorTextLang4;
    }

    public void setErrorTextLang5(String errorTextLang5) {
        this.errorTextLang5 = errorTextLang5;
    }

    public String getErrorTextLang5() {
        return errorTextLang5;
    }

    public void setWarningTextLang1(String warningTextLang1) {
        this.warningTextLang1 = warningTextLang1;
    }

    public String getWarningTextLang1() {
        return warningTextLang1;
    }

    public void setWarningTextLang2(String warningTextLang2) {
        this.warningTextLang2 = warningTextLang2;
    }

    public String getWarningTextLang2() {
        return warningTextLang2;
    }

    public void setWarningTextLang3(String warningTextLang3) {
        this.warningTextLang3 = warningTextLang3;
    }

    public String getWarningTextLang3() {
        return warningTextLang3;
    }

    public void setWarningTextLang4(String warningTextLang4) {
        this.warningTextLang4 = warningTextLang4;
    }

    public String getWarningTextLang4() {
        return warningTextLang4;
    }

    public void setWarningTextLang5(String warningTextLang5) {
        this.warningTextLang5 = warningTextLang5;
    }

    public String getWarningTextLang5() {
        return warningTextLang5;
    }


    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleName() {
        return ruleName;
    }


}