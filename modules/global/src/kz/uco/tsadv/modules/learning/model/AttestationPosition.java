package kz.uco.tsadv.modules.learning.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

@Table(name = "TSADV_ATTESTATION_POSITION")
@Entity(name = "tsadv$AttestationPosition")
public class AttestationPosition extends AbstractParentEntity {
    private static final long serialVersionUID = 941051709574076803L;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "clear"})
    @NotNull
    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ATTESTATION_ID")
    protected Attestation attestation;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "clear"})
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "POSITION_GROUP_ID")
    protected PositionGroupExt positionGroup;

    public void setAttestation(Attestation attestation) {
        this.attestation = attestation;
    }

    public Attestation getAttestation() {
        return attestation;
    }

    public void setPositionGroup(PositionGroupExt positionGroup) {
        this.positionGroup = positionGroup;
    }

    public PositionGroupExt getPositionGroup() {
        return positionGroup;
    }


}