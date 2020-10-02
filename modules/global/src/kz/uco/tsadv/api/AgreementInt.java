package kz.uco.tsadv.api;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;

@MetaClass(name = "tsadv$AgreementInt")
public class AgreementInt extends AbstractEntityInt {
    private static final long serialVersionUID = 2808227813185572808L;

    @MetaProperty
    protected String agreementNumber;

    @MetaProperty
    protected String agreementType;

    @MetaProperty
    protected String dateFrom;

    @MetaProperty
    protected String dateTo;

    @MetaProperty
    protected String status;

    @MetaProperty
    protected String personGroup;

    public void setAgreementNumber(String agreementNumber) {
        this.agreementNumber = agreementNumber;
    }

    public String getAgreementNumber() {
        return agreementNumber;
    }

    public void setAgreementType(String agreementType) {
        this.agreementType = agreementType;
    }

    public String getAgreementType() {
        return agreementType;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setPersonGroup(String personGroup) {
        this.personGroup = personGroup;
    }

    public String getPersonGroup() {
        return personGroup;
    }


}