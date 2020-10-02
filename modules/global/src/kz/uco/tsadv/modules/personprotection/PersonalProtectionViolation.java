package kz.uco.tsadv.modules.personprotection;

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
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import javax.persistence.Lob;

@Table(name = "TSADV_PERSONAL_PROTECTION_VIOLATION")
@Entity(name = "tsadv$PersonalProtectionViolation")
public class PersonalProtectionViolation extends AbstractParentEntity {
    private static final long serialVersionUID = 3467533701977168489L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSONAL_PROTECTION_ID")
    protected PersonalProtection personalProtection;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSONAL_PROTECTION_INSPECTOR_ID")
    protected PersonalProtectionInspector personalProtectionInspector;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "VIOLATION_DATE", nullable = false)
    protected Date violationDate;

    @Lob
    @Column(name = "VIOLATION_INFO")
    protected String violationInfo;

    public void setPersonalProtection(PersonalProtection personalProtection) {
        this.personalProtection = personalProtection;
    }

    public PersonalProtection getPersonalProtection() {
        return personalProtection;
    }

    public void setPersonalProtectionInspector(PersonalProtectionInspector personalProtectionInspector) {
        this.personalProtectionInspector = personalProtectionInspector;
    }

    public PersonalProtectionInspector getPersonalProtectionInspector() {
        return personalProtectionInspector;
    }

    public void setViolationDate(Date violationDate) {
        this.violationDate = violationDate;
    }

    public Date getViolationDate() {
        return violationDate;
    }

    public void setViolationInfo(String violationInfo) {
        this.violationInfo = violationInfo;
    }

    public String getViolationInfo() {
        return violationInfo;
    }


}