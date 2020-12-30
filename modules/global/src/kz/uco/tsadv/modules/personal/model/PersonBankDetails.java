package kz.uco.tsadv.modules.personal.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.base.entity.shared.Bank;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import java.util.Date;

@Table(name = "TSADV_PERSON_BANK_DETAILS")
@Entity(name = "tsadv_PersonBankDetails")
public class PersonBankDetails extends AbstractParentEntity {
    private static final long serialVersionUID = -2240000130310370769L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BANK_ID")
    private Bank bank;

    @Column(name = "FULL_NAME_BANK_CARD", length = 2000)
    private String fullNameBankCard;

    @Column(name = "IBAN")
    private String iban;

    @Column(name = "BIC_BANK")
    private String bicBank;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    private PersonGroupExt personGroup;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE_HISTORY")
    private Date startDateHistory;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE_HISTORY")
    private Date endDateHistory;

    public Date getEndDateHistory() {
        return endDateHistory;
    }

    public void setEndDateHistory(Date endDateHistory) {
        this.endDateHistory = endDateHistory;
    }

    public Date getStartDateHistory() {
        return startDateHistory;
    }

    public void setStartDateHistory(Date startDateHistory) {
        this.startDateHistory = startDateHistory;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public String getBicBank() {
        return bicBank;
    }

    public void setBicBank(String bicBank) {
        this.bicBank = bicBank;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getFullNameBankCard() {
        return fullNameBankCard;
    }

    public void setFullNameBankCard(String fullNameBankCard) {
        this.fullNameBankCard = fullNameBankCard;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }
}