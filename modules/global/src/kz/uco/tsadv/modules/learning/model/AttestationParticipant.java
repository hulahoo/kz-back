package kz.uco.tsadv.modules.learning.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.modules.learning.dictionary.DicAttestationEvent;
import kz.uco.tsadv.modules.learning.dictionary.DicAttestationResult;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.modules.learning.enums.Language;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.modules.learning.dictionary.DicAttestationInterviewResult;
import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.tsadv.modules.personal.dictionary.DicPersonType;
import com.haulmont.cuba.core.entity.FileDescriptor;

@NamePattern("%s|attestation")
@Table(name = "TSADV_ATTESTATION_PARTICIPANT")
@Entity(name = "tsadv$AttestationParticipant")
public class AttestationParticipant extends StandardEntity {
    private static final long serialVersionUID = 2545118744196361828L;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "clear"})
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ATTESTATION_ID")
    protected Attestation attestation;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "clear"})
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "ATTESTATION_DATE", nullable = false)
    protected Date attestationDate;

    @NotNull
    @Column(name = "PASSING_LANGUAGE", nullable = false)
    protected String passingLanguage;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "clear"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESULT_ID")
    protected DicAttestationResult result;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "clear"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EVENT_ID")
    protected DicAttestationEvent event;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "clear"})
    @OnDeleteInverse(DeletePolicy.UNLINK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INTERVIEW_RESULT_ID")
    protected DicAttestationInterviewResult interviewResult;

    @Column(name = "COMMISSION_RECOMENDATION")
    protected String commissionRecomendation;

    @Column(name = "NOT_APPEARED", columnDefinition = "BOOLEAN DEFAULT FALSE")
    protected Boolean notAppeared;

    @Column(name = "NOT_APPEARED_REASON")
    protected String notAppearedReason;


    @Column(name = "PROTOCOL")
    protected String protocol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTACHMENT_ID")
    protected FileDescriptor attachment;

    public void setAttachment(FileDescriptor attachment) {
        this.attachment = attachment;
    }

    public FileDescriptor getAttachment() {
        return attachment;
    }


    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getProtocol() {
        return protocol;
    }


    public void setInterviewResult(DicAttestationInterviewResult interviewResult) {
        this.interviewResult = interviewResult;
    }

    public DicAttestationInterviewResult getInterviewResult() {
        return interviewResult;
    }

    public void setCommissionRecomendation(String commissionRecomendation) {
        this.commissionRecomendation = commissionRecomendation;
    }

    public String getCommissionRecomendation() {
        return commissionRecomendation;
    }

    public void setNotAppeared(Boolean notAppeared) {
        this.notAppeared = notAppeared;
    }

    public Boolean getNotAppeared() {
        return notAppeared;
    }

    public void setNotAppearedReason(String notAppearedReason) {
        this.notAppearedReason = notAppearedReason;
    }

    public String getNotAppearedReason() {
        return notAppearedReason;
    }


    public void setPassingLanguage(Language passingLanguage) {
        this.passingLanguage = passingLanguage == null ? null : passingLanguage.getId();
    }

    public Language getPassingLanguage() {
        return passingLanguage == null ? null : Language.fromId(passingLanguage);
    }


    public void setAttestation(Attestation attestation) {
        this.attestation = attestation;
    }

    public Attestation getAttestation() {
        return attestation;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setAttestationDate(Date attestationDate) {
        this.attestationDate = attestationDate;
    }

    public Date getAttestationDate() {
        return attestationDate;
    }

    public void setResult(DicAttestationResult result) {
        this.result = result;
    }

    public DicAttestationResult getResult() {
        return result;
    }

    public void setEvent(DicAttestationEvent event) {
        this.event = event;
    }

    public DicAttestationEvent getEvent() {
        return event;
    }


}