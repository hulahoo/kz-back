package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicRelationshipType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import java.util.Date;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import com.haulmont.cuba.core.entity.annotation.Listeners;

@Listeners("tsadv_BeneficiaryListener")
@NamePattern("%s %s|personGroupChild,personGroupParent")
@Table(name = "TSADV_BENEFICIARY")
@Entity(name = "tsadv$Beneficiary")
public class Beneficiary extends AbstractParentEntity {
    private static final long serialVersionUID = 4701244820156979239L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_PARENT_ID")
    protected PersonGroupExt personGroupParent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_CHILD_ID")
    protected PersonGroupExt personGroupChild;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_FROM")
    protected Date dateFrom;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_TO")
    protected Date dateTo;

    @Column(name = "GET_ALIMONY")
    protected Boolean getAlimony;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RELATIONSHIP_TYPE_ID")
    protected DicRelationshipType relationshipType;

    public DicRelationshipType getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(DicRelationshipType relationshipType) {
        this.relationshipType = relationshipType;
    }


    public PersonGroupExt getPersonGroupChild() {
        return personGroupChild;
    }

    public void setPersonGroupChild(PersonGroupExt personGroupChild) {
        this.personGroupChild = personGroupChild;
    }


    public void setPersonGroupParent(PersonGroupExt personGroupParent) {
        this.personGroupParent = personGroupParent;
    }

    public PersonGroupExt getPersonGroupParent() {
        return personGroupParent;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setGetAlimony(Boolean getAlimony) {
        this.getAlimony = getAlimony;
    }

    public Boolean getGetAlimony() {
        return getAlimony;
    }


}