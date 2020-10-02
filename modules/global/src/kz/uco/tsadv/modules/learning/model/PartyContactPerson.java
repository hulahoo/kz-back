package kz.uco.tsadv.modules.learning.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import kz.uco.tsadv.modules.learning.dictionary.DicContactPersonType;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|fullName")
@Table(name = "TSADV_PARTY_CONTACT_PERSON")
@Entity(name = "tsadv$PartyContactPerson")
public class PartyContactPerson extends StandardEntity {
    private static final long serialVersionUID = -6042734666310911551L;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "clear"})
    @OnDeleteInverse(DeletePolicy.UNLINK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARTY_EXT_ID")
    protected PartyExt partyExt;

    @Column(name = "FULL_NAME")
    protected String fullName;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "clear"})
    @OnDeleteInverse(DeletePolicy.UNLINK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTACT_PERSON_TYPE_ID")
    protected DicContactPersonType contactPersonType;

    public void setPartyExt(PartyExt partyExt) {
        this.partyExt = partyExt;
    }

    public PartyExt getPartyExt() {
        return partyExt;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setContactPersonType(DicContactPersonType contactPersonType) {
        this.contactPersonType = contactPersonType;
    }

    public DicContactPersonType getContactPersonType() {
        return contactPersonType;
    }


}