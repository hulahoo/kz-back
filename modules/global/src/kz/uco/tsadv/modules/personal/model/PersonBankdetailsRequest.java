package kz.uco.tsadv.modules.personal.model;

import com.haulmont.cuba.core.entity.FileDescriptor;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.base.entity.shared.Bank;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;

@Table(name = "TSADV_PERSON_BANKDETAILS_REQUEST")
@Entity(name = "tsadv_PersonBankdetailsRequest")
public class PersonBankdetailsRequest extends AbstractParentEntity {
    private static final long serialVersionUID = 7647973767098565351L;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_STATUS_ID")
    private DicRequestStatus requestStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    private FileDescriptor file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BANK_DETAILS_ID")
    private PersonBankDetails bankDetails;

    public PersonBankDetails getBankDetails() {
        return bankDetails;
    }

    public void setBankDetails(PersonBankDetails bankDetails) {
        this.bankDetails = bankDetails;
    }

    public FileDescriptor getFile() {
        return file;
    }

    public void setFile(FileDescriptor file) {
        this.file = file;
    }

    public DicRequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(DicRequestStatus requestStatus) {
        this.requestStatus = requestStatus;
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