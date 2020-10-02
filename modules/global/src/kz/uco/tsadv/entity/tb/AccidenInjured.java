package kz.uco.tsadv.entity.tb;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.entity.tb.dictionary.DisabilityGroup;
import kz.uco.tsadv.entity.tb.dictionary.TraumaLevel;
import kz.uco.tsadv.entity.tb.dictionary.ReasonNoProductionConnection;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;
import kz.uco.tsadv.entity.tb.dictionary.ReabilitationType;

@Table(name = "TSADV_ACCIDEN_INJURED")
@Entity(name = "tsadv$AccidenInjured")
public class AccidenInjured extends AbstractParentEntity {
    private static final long serialVersionUID = -7303609764648588159L;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_ID")
    protected PersonExt person;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "accidenInjured")
    protected List<Attachment> attachment;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "accidenInjured")
    protected List<InvestigationResult> result;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DISABILITY_GROUP_ID")
    protected DisabilityGroup disabilityGroup;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TRAUMA_LEVEL_ID")
    protected TraumaLevel traumaLevel;

    @Temporal(TemporalType.DATE)
    @Column(name = "INDUCTION_DATE")
    protected Date inductionDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "RE_INTRODUCTORY_DATE")
    protected Date reIntroductoryDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "KNOWLEDGE_TEST_DATE")
    protected Date knowledgeTestDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "MEDICAL_EXAMINATION_DATE")
    protected Date medicalExaminationDate;

    @Column(name = "WORKING_HOURS", nullable = false)
    protected Long workingHours;

    @Column(name = "PHYSICAL_CONDITION", nullable = false)
    protected String physicalCondition;

    @Column(name = "DIAGNOSIS")
    protected String diagnosis;

    @Column(name = "PRODUCTION_CONNECTION")
    protected Boolean productionConnection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REASON_NO_PRODUCTION_CONNECTION_ID")
    protected ReasonNoProductionConnection reasonNoProductionConnection;

    @Column(name = "SPECIAL_OPINION")
    protected Boolean specialOpinion;

    @Column(name = "EMPLOYEE_GUILT")
    protected Long employeeGuilt;

    @Column(name = "EMPLOYER_GUILT")
    protected Long employerGuilt;

    @Column(name = "DISABILITY_PERCENT")
    protected Long disabilityPercent;

    @Temporal(TemporalType.DATE)
    @Column(name = "SICKNESS_START_DATE")
    protected Date sicknessStartDate;




    @Temporal(TemporalType.DATE)
    @Column(name = "SICKNESS_END_DATE")
    protected Date sicknessEndDate;

    @Column(name = "RETRAINING_PROFESSION")
    protected String retrainingProfession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REABILITATION_ID")
    protected ReabilitationType reabilitation;

    @Column(name = "SICKNESS_DAYS")
    protected Long sicknessDays;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_WORK_DATE")
    protected Date startWorkDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCIDENTS_ID")
    protected Accidents accidents;

    public void setReabilitation(ReabilitationType reabilitation) {
        this.reabilitation = reabilitation;
    }

    public ReabilitationType getReabilitation() {
        return reabilitation;
    }


    public void setRetrainingProfession(String retrainingProfession) {
        this.retrainingProfession = retrainingProfession;
    }

    public String getRetrainingProfession() {
        return retrainingProfession;
    }


    public void setAttachment(List<Attachment> attachment) {
        this.attachment = attachment;
    }

    public List<Attachment> getAttachment() {
        return attachment;
    }

    public void setResult(List<InvestigationResult> result) {
        this.result = result;
    }

    public List<InvestigationResult> getResult() {
        return result;
    }


    public void setAccidents(Accidents accidents) {
        this.accidents = accidents;
    }

    public Accidents getAccidents() {
        return accidents;
    }


    public void setReasonNoProductionConnection(ReasonNoProductionConnection reasonNoProductionConnection) {
        this.reasonNoProductionConnection = reasonNoProductionConnection;
    }

    public ReasonNoProductionConnection getReasonNoProductionConnection() {
        return reasonNoProductionConnection;
    }

    public void setSicknessEndDate(Date sicknessEndDate) {
        this.sicknessEndDate = sicknessEndDate;
    }

    public Date getSicknessEndDate() {
        return sicknessEndDate;
    }

    public void setSicknessDays(Long sicknessDays) {
        this.sicknessDays = sicknessDays;
    }

    public Long getSicknessDays() {
        return sicknessDays;
    }

    public void setStartWorkDate(Date startWorkDate) {
        this.startWorkDate = startWorkDate;
    }

    public Date getStartWorkDate() {
        return startWorkDate;
    }


    public DisabilityGroup getDisabilityGroup() {
        return disabilityGroup;
    }

    public void setDisabilityGroup(DisabilityGroup disabilityGroup) {
        this.disabilityGroup = disabilityGroup;
    }


    public TraumaLevel getTraumaLevel() {
        return traumaLevel;
    }

    public void setTraumaLevel(TraumaLevel traumaLevel) {
        this.traumaLevel = traumaLevel;
    }


    public void setPerson(PersonExt person) {
        this.person = person;
    }

    public PersonExt getPerson() {
        return person;
    }






    public void setEmployeeGuilt(Long employeeGuilt) {
        this.employeeGuilt = employeeGuilt;
    }

    public Long getEmployeeGuilt() {
        return employeeGuilt;
    }

    public void setEmployerGuilt(Long employerGuilt) {
        this.employerGuilt = employerGuilt;
    }

    public Long getEmployerGuilt() {
        return employerGuilt;
    }

    public void setDisabilityPercent(Long disabilityPercent) {
        this.disabilityPercent = disabilityPercent;
    }

    public Long getDisabilityPercent() {
        return disabilityPercent;
    }

    public void setSicknessStartDate(Date sicknessStartDate) {
        this.sicknessStartDate = sicknessStartDate;
    }

    public Date getSicknessStartDate() {
        return sicknessStartDate;
    }


    public void setInductionDate(Date inductionDate) {
        this.inductionDate = inductionDate;
    }

    public Date getInductionDate() {
        return inductionDate;
    }

    public void setReIntroductoryDate(Date reIntroductoryDate) {
        this.reIntroductoryDate = reIntroductoryDate;
    }

    public Date getReIntroductoryDate() {
        return reIntroductoryDate;
    }

    public void setKnowledgeTestDate(Date knowledgeTestDate) {
        this.knowledgeTestDate = knowledgeTestDate;
    }

    public Date getKnowledgeTestDate() {
        return knowledgeTestDate;
    }

    public void setMedicalExaminationDate(Date medicalExaminationDate) {
        this.medicalExaminationDate = medicalExaminationDate;
    }

    public Date getMedicalExaminationDate() {
        return medicalExaminationDate;
    }

    public void setWorkingHours(Long workingHours) {
        this.workingHours = workingHours;
    }

    public Long getWorkingHours() {
        return workingHours;
    }

    public void setPhysicalCondition(String physicalCondition) {
        this.physicalCondition = physicalCondition;
    }

    public String getPhysicalCondition() {
        return physicalCondition;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setProductionConnection(Boolean productionConnection) {
        this.productionConnection = productionConnection;
    }

    public Boolean getProductionConnection() {
        return productionConnection;
    }

    public void setSpecialOpinion(Boolean specialOpinion) {
        this.specialOpinion = specialOpinion;
    }

    public Boolean getSpecialOpinion() {
        return specialOpinion;
    }


}