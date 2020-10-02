package kz.uco.tsadv.entity.tb;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;
import javax.persistence.Column;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;

@Table(name = "TSADV_SANITARY_HYGIENE_EVENT")
@Entity(name = "tsadv$SanitaryHygieneEvent")
public class SanitaryHygieneEvent extends AbstractParentEntity {
    private static final long serialVersionUID = -8755128439210313649L;

    @Column(name = "DEVELOPED_EVENTS", nullable = false)
    protected Long developedEvents;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "sanitaryHygieneEvent")
    protected List<Attachment> attachment;

    @Column(name = "DONE_EVENTS")
    protected Long doneEvents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OCCUPATIONAL_MEDICINE_ID")
    protected OccupationalMedicine occupationalMedicine;

    public void setAttachment(List<Attachment> attachment) {
        this.attachment = attachment;
    }

    public List<Attachment> getAttachment() {
        return attachment;
    }


    public void setOccupationalMedicine(OccupationalMedicine occupationalMedicine) {
        this.occupationalMedicine = occupationalMedicine;
    }

    public OccupationalMedicine getOccupationalMedicine() {
        return occupationalMedicine;
    }


    public void setDevelopedEvents(Long developedEvents) {
        this.developedEvents = developedEvents;
    }

    public Long getDevelopedEvents() {
        return developedEvents;
    }

    public void setDoneEvents(Long doneEvents) {
        this.doneEvents = doneEvents;
    }

    public Long getDoneEvents() {
        return doneEvents;
    }


}