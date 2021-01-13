package kz.uco.tsadv.modules.personal.model;

import com.haulmont.cuba.core.entity.FileDescriptor;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicReasonBenifit;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.enums.YesNoEnum;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;

@Table(name = "TSADV_PERSON_BENEFIT_REQUEST")
@Entity(name = "tsadv_PersonBenefitRequest")
public class PersonBenefitRequest extends AbstractParentEntity {
    private static final long serialVersionUID = 8492530444301143962L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REASON_ID")
    private DicReasonBenifit reason;

    @Column(name = "COMBATANT")
    private String combatant;

    @Column(name = "CERTIFICATE_FROM_DATE")
    private String certificateFromDate;

    @Column(name = "DOCUMENT_NUMBER")
    private String documentNumber;

    @Column(name = "RADIATION_RISK_ZONE")
    private String radiationRiskZone;

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
    @JoinColumn(name = "PERSON_BENEFIT_ID")
    private PersonBenefit personBenefit;

    public void setReason(DicReasonBenifit reason) {
        this.reason = reason;
    }

    public DicReasonBenifit getReason() {
        return reason;
    }

    public PersonBenefit getPersonBenefit() {
        return personBenefit;
    }

    public void setPersonBenefit(PersonBenefit personBenefit) {
        this.personBenefit = personBenefit;
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

    public YesNoEnum getRadiationRiskZone() {
        return radiationRiskZone == null ? null : YesNoEnum.fromId(radiationRiskZone);
    }

    public void setRadiationRiskZone(YesNoEnum radiationRiskZone) {
        this.radiationRiskZone = radiationRiskZone == null ? null : radiationRiskZone.getId();
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getCertificateFromDate() {
        return certificateFromDate;
    }

    public void setCertificateFromDate(String certificateFromDate) {
        this.certificateFromDate = certificateFromDate;
    }

    public YesNoEnum getCombatant() {
        return combatant == null ? null : YesNoEnum.fromId(combatant);
    }

    public void setCombatant(YesNoEnum combatant) {
        this.combatant = combatant == null ? null : combatant.getId();
    }

}