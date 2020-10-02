package kz.uco.tsadv.modules.recruitment.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.recruitment.enums.RequisitionAccessLevel;

import javax.persistence.*;

@NamePattern("%s|id")
@Table(name = "TSADV_REQUISITION_MEMBER")
@Entity(name = "tsadv$RequisitionMember")
public class RequisitionMember extends AbstractParentEntity {
    private static final long serialVersionUID = 7877618782271854167L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "REQUISITION_ID")
    protected kz.uco.tsadv.modules.recruitment.model.Requisition requisition;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @Column(name = "ACCESS_LEVEL", nullable = false)
    protected Integer accessLevel;

    public RequisitionAccessLevel getAccessLevel() {
        return accessLevel == null ? null : RequisitionAccessLevel.fromId(accessLevel);
    }

    public void setAccessLevel(RequisitionAccessLevel accessLevel) {
        this.accessLevel = accessLevel == null ? null : accessLevel.getId();
    }

    public void setRequisition(kz.uco.tsadv.modules.recruitment.model.Requisition requisition) {
        this.requisition = requisition;
    }

    public Requisition getRequisition() {
        return requisition;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }


}