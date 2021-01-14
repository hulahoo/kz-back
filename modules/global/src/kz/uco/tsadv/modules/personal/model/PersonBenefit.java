package kz.uco.tsadv.modules.personal.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicReasonBenifit;
import kz.uco.tsadv.modules.personal.enums.YesNoEnum;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import java.util.Date;

@Table(name = "TSADV_PERSON_BENEFIT")
@Entity(name = "tsadv_PersonBenefit")
public class PersonBenefit extends AbstractParentEntity {
    private static final long serialVersionUID = -2620294066304735292L;

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

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE_HISTORY")
    private Date startDateHistory;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE_HISTORY")
    private Date endDateHistory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    private PersonGroupExt personGroup;

    public void setReason(DicReasonBenifit reason) {
        this.reason = reason;
    }

    public DicReasonBenifit getReason() {
        return reason;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

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