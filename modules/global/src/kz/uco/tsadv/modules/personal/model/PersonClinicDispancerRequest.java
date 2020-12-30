package kz.uco.tsadv.modules.personal.model;

import com.haulmont.cuba.core.entity.FileDescriptor;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.enums.YesNoEnum;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;

@Table(name = "TSADV_PERSON_CLINIC_DISPANCER_REQUEST")
@Entity(name = "tsadv_PersonClinicDispancerRequest")
public class PersonClinicDispancerRequest extends AbstractParentEntity {
    private static final long serialVersionUID = 6996467803352619570L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    private PersonGroupExt personGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_STATUS_ID")
    private DicRequestStatus requestStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    private FileDescriptor file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_CLINIC_DISPANCER_ID")
    private PersonClinicDispancer personClinicDispancer;

    @Column(name = "HAVE_CLINIC_DISPANCER")
    private String haveClinicDispancer;

    @Column(name = "PERIOD_FROM", length = 2000)
    private String periodFrom;

    public void setRequestStatus(DicRequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public DicRequestStatus getRequestStatus() {
        return requestStatus;
    }

    public PersonClinicDispancer getPersonClinicDispancer() {
        return personClinicDispancer;
    }

    public void setPersonClinicDispancer(PersonClinicDispancer personClinicDispancer) {
        this.personClinicDispancer = personClinicDispancer;
    }

    public FileDescriptor getFile() {
        return file;
    }

    public void setFile(FileDescriptor file) {
        this.file = file;
    }

    public String getPeriodFrom() {
        return periodFrom;
    }

    public void setPeriodFrom(String periodFrom) {
        this.periodFrom = periodFrom;
    }

    public YesNoEnum getHaveClinicDispancer() {
        return haveClinicDispancer == null ? null : YesNoEnum.fromId(haveClinicDispancer);
    }

    public void setHaveClinicDispancer(YesNoEnum haveClinicDispancer) {
        this.haveClinicDispancer = haveClinicDispancer == null ? null : haveClinicDispancer.getId();
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }
}