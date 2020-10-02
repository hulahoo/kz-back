package kz.uco.tsadv.modules.performance.model;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import kz.uco.base.entity.dictionary.DicCurrency;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.personal.model.PositionExt;

import java.util.UUID;

@MetaClass(name = "tsadv$NotPersistEntity")
public class NotPersistEntity extends BaseUuidEntity {
    private static final long serialVersionUID = -7320350054652939060L;

    @MetaProperty
    protected UUID assignmentId;

    @MetaProperty(related = "personId")
    protected PersonExt person;

    @MetaProperty
    protected PositionExt position;

    @MetaProperty
    protected Assessment assessment;

    @MetaProperty
    protected Long direct;

    @MetaProperty
    protected Long total;

    @MetaProperty
    protected Integer amount;

    @MetaProperty
    protected DicCurrency currency;

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setCurrency(DicCurrency currency) {
        this.currency = currency;
    }

    public DicCurrency getCurrency() {
        return currency;
    }


    public void setAssignmentId(UUID assignmentId) {
        this.assignmentId = assignmentId;
    }

    public UUID getAssignmentId() {
        return assignmentId;
    }


    public Long getDirect() {
        return direct;
    }

    public void setDirect(Long direct) {
        this.direct = direct;
    }


    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }


    public void setPosition(PositionExt position) {
        this.position = position;
    }

    public PositionExt getPosition() {
        return position;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }

    public Assessment getAssessment() {
        return assessment;
    }


    public void setPerson(PersonExt person) {
        this.person = person;
    }

    public PersonExt getPerson() {
        return person;
    }


}