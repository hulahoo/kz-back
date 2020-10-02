package kz.uco.tsadv.modules.learning.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.modules.learning.enums.KmsTestPurpose;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

@Table(name = "TSADV_POSITION_TEST")
@Entity(name = "tsadv$PositionTest")
public class PositionTest extends AbstractParentEntity {
    private static final long serialVersionUID = 4452250738334313373L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "POSITION_GROUP_ID")
    protected PositionGroupExt positionGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEST_ID")
    protected Test test;

    @NotNull
    @Column(name = "PURPOSE", nullable = false)
    protected String purpose;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "START_DATE", nullable = false)
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "END_DATE", nullable = false)
    protected Date endDate;

    public void setPositionGroup(PositionGroupExt positionGroup) {
        this.positionGroup = positionGroup;
    }

    public PositionGroupExt getPositionGroup() {
        return positionGroup;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public Test getTest() {
        return test;
    }

    public void setPurpose(KmsTestPurpose purpose) {
        this.purpose = purpose == null ? null : purpose.getId();
    }

    public KmsTestPurpose getPurpose() {
        return purpose == null ? null : KmsTestPurpose.fromId(purpose);
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }


}