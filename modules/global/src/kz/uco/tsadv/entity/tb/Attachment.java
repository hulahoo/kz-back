package kz.uco.tsadv.entity.tb;

import com.haulmont.cuba.core.entity.FileDescriptor;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.entity.tb.dictionary.AttachmentType;
import kz.uco.tsadv.modules.personal.model.InsuranceContract;

import javax.persistence.*;
import java.util.List;

@Table(name = "TSADV_ATTACHMENT")
@Entity(name = "tsadv$Attachment")
public class Attachment extends AbstractParentEntity {
    private static final long serialVersionUID = 2362528513885900971L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTACHMENT_ID")
    protected FileDescriptor attachment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTACHMENT_TYPE_ID")
    protected AttachmentType attachmentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUILDINGS_ID")
    protected Buildings buildings;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCIDENTS_ID")
    protected Accidents accidents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCIDEN_INJURED_ID")
    protected AccidenInjured accidenInjured;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MICROTRAUM_ID")
    protected Microtraum microtraum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INCIDENT_ID")
    protected Incident incident;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HEALTH_DETERIORATION_ID")
    protected HealthDeterioration healthDeterioration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OCCUPATIONAL_MEDICINE_ID")
    protected OccupationalMedicine occupationalMedicine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SANITARY_HYGIENE_EVENT_ID")
    protected SanitaryHygieneEvent sanitaryHygieneEvent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HARMFULL_FACTORS_ID")
    protected HarmfullFactors harmfullFactors;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NOT_ALLOWED_PERSON_ID")
    protected NotAllowedPerson notAllowedPerson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEDICAL_INSPECTION_ID")
    protected MedicalInspection medicalInspection;
    @OneToMany(mappedBy = "attachment")
    private List<InsuranceContract> insuranceContract;

    public List<InsuranceContract> getInsuranceContract() {
        return insuranceContract;
    }

    public void setInsuranceContract(List<InsuranceContract> insuranceContract) {
        this.insuranceContract = insuranceContract;
    }

    public void setMedicalInspection(MedicalInspection medicalInspection) {
        this.medicalInspection = medicalInspection;
    }

    public MedicalInspection getMedicalInspection() {
        return medicalInspection;
    }


    public void setNotAllowedPerson(NotAllowedPerson notAllowedPerson) {
        this.notAllowedPerson = notAllowedPerson;
    }

    public NotAllowedPerson getNotAllowedPerson() {
        return notAllowedPerson;
    }


    public void setHarmfullFactors(HarmfullFactors harmfullFactors) {
        this.harmfullFactors = harmfullFactors;
    }

    public HarmfullFactors getHarmfullFactors() {
        return harmfullFactors;
    }


    public void setSanitaryHygieneEvent(SanitaryHygieneEvent sanitaryHygieneEvent) {
        this.sanitaryHygieneEvent = sanitaryHygieneEvent;
    }

    public SanitaryHygieneEvent getSanitaryHygieneEvent() {
        return sanitaryHygieneEvent;
    }


    public void setOccupationalMedicine(OccupationalMedicine occupationalMedicine) {
        this.occupationalMedicine = occupationalMedicine;
    }

    public OccupationalMedicine getOccupationalMedicine() {
        return occupationalMedicine;
    }


    public void setHealthDeterioration(HealthDeterioration healthDeterioration) {
        this.healthDeterioration = healthDeterioration;
    }

    public HealthDeterioration getHealthDeterioration() {
        return healthDeterioration;
    }


    public void setIncident(Incident incident) {
        this.incident = incident;
    }

    public Incident getIncident() {
        return incident;
    }


    public void setMicrotraum(Microtraum microtraum) {
        this.microtraum = microtraum;
    }

    public Microtraum getMicrotraum() {
        return microtraum;
    }


    public void setAccidenInjured(AccidenInjured accidenInjured) {
        this.accidenInjured = accidenInjured;
    }

    public AccidenInjured getAccidenInjured() {
        return accidenInjured;
    }


    public void setAccidents(Accidents accidents) {
        this.accidents = accidents;
    }

    public Accidents getAccidents() {
        return accidents;
    }


    public void setBuildings(Buildings buildings) {
        this.buildings = buildings;
    }

    public Buildings getBuildings() {
        return buildings;
    }



    public void setAttachment(FileDescriptor attachment) {
        this.attachment = attachment;
    }

    public FileDescriptor getAttachment() {
        return attachment;
    }


    public void setAttachmentType(AttachmentType attachmentType) {
        this.attachmentType = attachmentType;
    }

    public AttachmentType getAttachmentType() {
        return attachmentType;
    }



}