package kz.uco.tsadv.modules.personal.lengthofservice;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import kz.uco.tsadv.modules.personal.dictionary.DicLengthOfServiceRange;
import kz.uco.tsadv.modules.personal.dictionary.DicLengthOfServiceType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s %s|personGroup,range")
@Table(name = "TSADV_PERSON_LENGTH_OF_SERVICE")
@Entity(name = "tsadv$PersonLengthOfService")
public class PersonLengthOfService extends AbstractParentEntity {
    private static final long serialVersionUID = -9157161733423389450L;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "clear"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "clear"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LENGTH_OF_SERVICE_TYPE_ID")
    protected DicLengthOfServiceType lengthOfServiceType;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "clear"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RANGE_ID")
    protected DicLengthOfServiceRange range;

    @Temporal(TemporalType.DATE)
    @Column(name = "EFFECTIVE_DATE")
    protected Date effectiveDate;

    @Column(name = "VALUE_")
    protected Double value;

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getValue() {
        return value;
    }


    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setLengthOfServiceType(DicLengthOfServiceType lengthOfServiceType) {
        this.lengthOfServiceType = lengthOfServiceType;
    }

    public DicLengthOfServiceType getLengthOfServiceType() {
        return lengthOfServiceType;
    }

    public void setRange(DicLengthOfServiceRange range) {
        this.range = range;
    }

    public DicLengthOfServiceRange getRange() {
        return range;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }


}