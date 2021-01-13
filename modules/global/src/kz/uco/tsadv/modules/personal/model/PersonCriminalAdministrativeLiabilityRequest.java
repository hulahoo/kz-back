package kz.uco.tsadv.modules.personal.model;

import com.haulmont.cuba.core.entity.FileDescriptor;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicCriminalLiabilityType;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.enums.YesNoEnum;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;

@Table(name = "TSADV_PERSON_CRIMINAL_ADMINISTRATIVE_LIABILITY_REQUEST")
@Entity(name = "tsadv_PersonCriminalAdministrativeLiabilityRequest")
public class PersonCriminalAdministrativeLiabilityRequest extends AbstractParentEntity {
    private static final long serialVersionUID = 1974033631533410307L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    private PersonGroupExt personGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TYPE_ID")
    private DicCriminalLiabilityType type;

    @Column(name = "HAVE_LIABILITY")
    private String haveLiability;

    @Column(name = "REASON_PERIOD", length = 2000)
    private String reasonPeriod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_STATUS_ID")
    private DicRequestStatus requestStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    private FileDescriptor file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LIABILITY_ID")
    private PersonCriminalAdministrativeLiability liability;

    public DicCriminalLiabilityType getType() {
        return type;
    }

    public void setType(DicCriminalLiabilityType type) {
        this.type = type;
    }

    public PersonCriminalAdministrativeLiability getLiability() {
        return liability;
    }

    public void setLiability(PersonCriminalAdministrativeLiability liability) {
        this.liability = liability;
    }

    public FileDescriptor getFile() {
        return file;
    }

    public void setFile(FileDescriptor file) {
        this.file = file;
    }

    public DicRequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(DicRequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getReasonPeriod() {
        return reasonPeriod;
    }

    public void setReasonPeriod(String reasonPeriod) {
        this.reasonPeriod = reasonPeriod;
    }

    public YesNoEnum getHaveLiability() {
        return haveLiability == null ? null : YesNoEnum.fromId(haveLiability);
    }

    public void setHaveLiability(YesNoEnum haveLiability) {
        this.haveLiability = haveLiability == null ? null : haveLiability.getId();
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }
}