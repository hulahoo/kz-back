package kz.uco.tsadv.modules.recruitment.model;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

@MetaClass(name = "tsadv$RequisitionCount")
public class RequisitionCount extends BaseUuidEntity {
    private static final long serialVersionUID = 9157955725449706556L;

    @MetaProperty
    protected PersonGroupExt personGroup;

    @MetaProperty
    protected Long countRequisition;

    @MetaProperty
    protected Long countJobRequest;

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setCountRequisition(Long countRequisition) {
        this.countRequisition = countRequisition;
    }

    public Long getCountRequisition() {
        return countRequisition;
    }

    public void setCountJobRequest(Long countJobRequest) {
        this.countJobRequest = countJobRequest;
    }

    public Long getCountJobRequest() {
        return countJobRequest;
    }


}