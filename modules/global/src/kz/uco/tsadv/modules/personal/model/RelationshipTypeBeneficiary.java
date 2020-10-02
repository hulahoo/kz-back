package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicRelationshipType;

import javax.persistence.*;

@NamePattern("%s %s|parent,child")
@Table(name = "TSADV_RELATIONSHIP_TYPE_BENEFICIARY")
@Entity(name = "tsadv$RelationshipTypeBeneficiary")
public class RelationshipTypeBeneficiary extends StandardEntity {
    private static final long serialVersionUID = 522632941083375935L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PARENT_ID")
    protected DicRelationshipType parent;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CHILD_ID")
    protected DicRelationshipType child;

    public void setParent(DicRelationshipType parent) {
        this.parent = parent;
    }

    public DicRelationshipType getParent() {
        return parent;
    }

    public void setChild(DicRelationshipType child) {
        this.child = child;
    }

    public DicRelationshipType getChild() {
        return child;
    }


}