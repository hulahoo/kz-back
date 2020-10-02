package kz.uco.tsadv.modules.recruitment.model;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import kz.uco.tsadv.modules.personal.model.PersonExt;

@MetaClass(name = "tsadv$RequisitionSearchCandidateResult")
public class RequisitionSearchCandidateResult extends BaseUuidEntity {
    private static final long serialVersionUID = 5257693847604255211L;

    @MetaProperty
    protected PersonExt person;

    @MetaProperty
    protected Long match;

    @MetaProperty
    protected Long clickCount;

    @MetaProperty
    protected Integer personTypeOrder;

    @MetaProperty
    protected String positionName;

    @MetaProperty
    protected String organizationName;

    @MetaProperty
    protected Boolean isReserved;

    public void setIsReserved(Boolean isReserved) {
        this.isReserved = isReserved;
    }

    public Boolean getIsReserved() {
        return isReserved;
    }


    public Integer getPersonTypeOrder() {
        return personTypeOrder;
    }

    public void setPersonTypeOrder(Integer personTypeOrder) {
        this.personTypeOrder = personTypeOrder;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Long getClickCount() {
        return clickCount;
    }

    public void setClickCount(Long clickCount) {
        this.clickCount = clickCount;
    }

    public void setPerson(PersonExt person) {
        this.person = person;
    }

    public PersonExt getPerson() {
        return person;
    }

    public Long getMatch() {
        return match;
    }

    public void setMatch(Long match) {
        this.match = match;
    }
}