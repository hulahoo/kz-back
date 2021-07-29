package kz.uco.tsadv.modules.personal.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "TSADV_PAYMENTS")
@Entity(name = "tsadv_Payments")
public class Payments extends AbstractParentEntity {
    private static final long serialVersionUID = 960412828906902093L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "GRANTEES_AGREEMENT_ID")
    protected GranteesAgreement granteesAgreement;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @NotNull
    @Column(name = "ACCOUNT", nullable = false)
    protected String account;

    @NotNull
    @Column(name = "ACCOUNT_NAME", nullable = false)
    protected String accountName;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "PAYMENT_DATE", nullable = false)
    protected Date paymentDate;

    @NotNull
    @Column(name = "AMOUNT", nullable = false)
    protected BigDecimal amount;

    @Column(name = "NOTE")
    protected String note;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public GranteesAgreement getGranteesAgreement() {
        return granteesAgreement;
    }

    public void setGranteesAgreement(GranteesAgreement granteesAgreement) {
        this.granteesAgreement = granteesAgreement;
    }
}