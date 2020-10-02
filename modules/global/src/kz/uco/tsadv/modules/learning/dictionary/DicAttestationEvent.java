package kz.uco.tsadv.modules.learning.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_ATTESTATION_EVENT")
@Entity(name = "tsadv$DicAttestationEvent")
public class DicAttestationEvent extends AbstractDictionary {
    private static final long serialVersionUID = -7496670850326559785L;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DIC_ATTESTATION_RESULT_ID")
    protected DicAttestationResult dicAttestationResult;

    public void setDicAttestationResult(DicAttestationResult dicAttestationResult) {
        this.dicAttestationResult = dicAttestationResult;
    }

    public DicAttestationResult getDicAttestationResult() {
        return dicAttestationResult;
    }



}