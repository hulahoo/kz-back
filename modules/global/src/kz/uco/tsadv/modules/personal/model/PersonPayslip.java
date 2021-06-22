package kz.uco.tsadv.modules.personal.model;

import com.haulmont.cuba.core.entity.FileDescriptor;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import java.util.Date;

@Table(name = "TSADV_PERSON_PAYSLIP")
@Entity(name = "tsadv_PersonPayslip")
public class PersonPayslip extends AbstractParentEntity {
    private static final long serialVersionUID = 373648419138095145L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    private PersonGroupExt personGroup;

    @Temporal(TemporalType.DATE)
    @Column(name = "PERIOD")
    private Date period;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    private FileDescriptor file;

    public FileDescriptor getFile() {
        return file;
    }

    public void setFile(FileDescriptor file) {
        this.file = file;
    }

    public Date getPeriod() {
        return period;
    }

    public void setPeriod(Date period) {
        this.period = period;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }
}