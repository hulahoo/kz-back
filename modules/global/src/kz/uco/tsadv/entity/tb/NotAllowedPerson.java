package kz.uco.tsadv.entity.tb;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;
import kz.uco.tsadv.entity.tb.dictionary.IntoxicationType;
import javax.validation.constraints.NotNull;

@Table(name = "TSADV_NOT_ALLOWED_PERSON")
@Entity(name = "tsadv$NotAllowedPerson")
public class NotAllowedPerson extends AbstractParentEntity {
    private static final long serialVersionUID = 8798345589686986464L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "NOT_ALLOWED_ID")
    protected PersonExt notAllowed;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "notAllowedPerson")
    protected List<Attachment> attachment;

    @Column(name = "DISPENSARY_CONFIRMATION")
    protected Boolean dispensaryConfirmation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INTOXICATION_TYPE_ID")
    protected IntoxicationType intoxicationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HARMFULL_FACTORS_ID")
    protected HarmfullFactors harmfullFactors;

    public void setHarmfullFactors(HarmfullFactors harmfullFactors) {
        this.harmfullFactors = harmfullFactors;
    }

    public HarmfullFactors getHarmfullFactors() {
        return harmfullFactors;
    }


    public IntoxicationType getIntoxicationType() {
        return intoxicationType;
    }

    public void setIntoxicationType(IntoxicationType intoxicationType) {
        this.intoxicationType = intoxicationType;
    }


    public void setAttachment(List<Attachment> attachment) {
        this.attachment = attachment;
    }

    public List<Attachment> getAttachment() {
        return attachment;
    }



    public void setNotAllowed(PersonExt notAllowed) {
        this.notAllowed = notAllowed;
    }

    public PersonExt getNotAllowed() {
        return notAllowed;
    }

    public void setDispensaryConfirmation(Boolean dispensaryConfirmation) {
        this.dispensaryConfirmation = dispensaryConfirmation;
    }

    public Boolean getDispensaryConfirmation() {
        return dispensaryConfirmation;
    }


}