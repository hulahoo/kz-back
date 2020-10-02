package kz.uco.tsadv.modules.learning.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

@Table(name = "TSADV_ATTESTATION_ORGANIZATION")
@Entity(name = "tsadv$AttestationOrganization")
public class AttestationOrganization extends AbstractParentEntity {
    private static final long serialVersionUID = 4224471627400837470L;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "clear"})
    @NotNull
    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ATTESTATION_ID")
    protected Attestation attestation;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "clear"})
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ORGANIZATION_GROUP_ID")
    protected OrganizationGroupExt organizationGroup;

    @NotNull
    @Column(name = "INCLUDE_CHILD", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    protected Boolean includeChild = false;

    public void setAttestation(Attestation attestation) {
        this.attestation = attestation;
    }

    public Attestation getAttestation() {
        return attestation;
    }

    public void setOrganizationGroup(OrganizationGroupExt organizationGroup) {
        this.organizationGroup = organizationGroup;
    }

    public OrganizationGroupExt getOrganizationGroup() {
        return organizationGroup;
    }

    public void setIncludeChild(Boolean includeChild) {
        this.includeChild = includeChild;
    }

    public Boolean getIncludeChild() {
        return includeChild;
    }


}