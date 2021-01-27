package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsencePurpose;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsenceType;

import javax.persistence.*;

@Table(name = "TSADV_ABS_PURPOSE_SETTING", uniqueConstraints = {
        @UniqueConstraint(name = "IDX_TSADV_ABS_PURPOSE_SETTING_UNQ", columnNames = {"ABSENCE_TYPE_ID", "ABSENCE_PURPOSE_ID"})
})
@Entity(name = "tsadv_AbsPurposeSetting")
@NamePattern("%s %s|absenceType,absencePurpose")
public class AbsPurposeSetting extends AbstractParentEntity {
    private static final long serialVersionUID = -7613769236731154687L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ABSENCE_TYPE_ID")
    protected DicAbsenceType absenceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ABSENCE_PURPOSE_ID")
    protected DicAbsencePurpose absencePurpose;

    @Column(name = "ORDER_NUMBER")
    protected Integer orderNumber;

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public DicAbsencePurpose getAbsencePurpose() {
        return absencePurpose;
    }

    public void setAbsencePurpose(DicAbsencePurpose absencePurpose) {
        this.absencePurpose = absencePurpose;
    }

    public DicAbsenceType getAbsenceType() {
        return absenceType;
    }

    public void setAbsenceType(DicAbsenceType absenceType) {
        this.absenceType = absenceType;
    }
}