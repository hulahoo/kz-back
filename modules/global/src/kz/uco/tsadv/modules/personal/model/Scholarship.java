package kz.uco.tsadv.modules.personal.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "TSADV_SCHOLARSHIP")
@Entity(name = "tsadv_Scholarship")
public class Scholarship extends AbstractParentEntity {
    private static final long serialVersionUID = -7831679694443896181L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "GRANTEES_AGREEMENT_ID")
    protected GranteesAgreement granteesAgreement;

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

    public GranteesAgreement getGranteesAgreement() {
        return granteesAgreement;
    }

    public void setGranteesAgreement(GranteesAgreement granteesAgreement) {
        this.granteesAgreement = granteesAgreement;
    }
}