package kz.uco.tsadv.entity.dbview;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.global.DesignSupport;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import kz.uco.tsadv.modules.learning.model.Attestation;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

@DesignSupport("{'dbView':true,'generateDdl':false}")
@Table(name = "TSADV_PERSON_GROUP_ATTESTATION_V")
@Entity(name = "tsadv$PersonGroupAttestation")
public class PersonGroupAttestation extends BaseUuidEntity {
    private static final long serialVersionUID = 384539140847811764L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_group_id")
    protected PersonGroupExt personGroupExt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attestation_id")
    protected Attestation attestation;

    public void setPersonGroupExt(PersonGroupExt personGroupExt) {
        this.personGroupExt = personGroupExt;
    }

    public PersonGroupExt getPersonGroupExt() {
        return personGroupExt;
    }

    public void setAttestation(Attestation attestation) {
        this.attestation = attestation;
    }

    public Attestation getAttestation() {
        return attestation;
    }


}