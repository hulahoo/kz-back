package kz.uco.tsadv.entity.tb;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;

@Table(name = "TSADV_OCCUPATIONAL_MEDICINE")
@Entity(name = "tsadv$OccupationalMedicine")
public class OccupationalMedicine extends AbstractParentEntity {
    private static final long serialVersionUID = -5765307976993311001L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ORGANIZATION_ID")
    protected OrganizationGroupExt organization;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "occupationalMedicine")
    protected List<SanitaryHygieneEvent> event;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "occupationalMedicine")
    protected List<SanitaryRegulationsControl> control;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "occupationalMedicine")
    protected List<Attachment> attachment;

    @Temporal(TemporalType.DATE)
    @Column(name = "ENTRY_DATE", nullable = false)
    protected Date entryDate;

    public void setEvent(List<SanitaryHygieneEvent> event) {
        this.event = event;
    }

    public List<SanitaryHygieneEvent> getEvent() {
        return event;
    }


    public void setControl(List<SanitaryRegulationsControl> control) {
        this.control = control;
    }

    public List<SanitaryRegulationsControl> getControl() {
        return control;
    }

    public void setAttachment(List<Attachment> attachment) {
        this.attachment = attachment;
    }

    public List<Attachment> getAttachment() {
        return attachment;
    }


    public void setOrganization(OrganizationGroupExt organization) {
        this.organization = organization;
    }

    public OrganizationGroupExt getOrganization() {
        return organization;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public Date getEntryDate() {
        return entryDate;
    }


}