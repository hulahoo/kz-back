package kz.uco.tsadv.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import java.util.Date;
import kz.uco.tsadv.entity.tb.dictionary.DicTalentProgramStep;

@NamePattern("%s|talentProgram")
@Table(name = "TSADV_TALENT_PROGRAM_REQUEST")
@Entity(name = "tsadv$TalentProgramRequest")
public class TalentProgramRequest extends StandardEntity {
    private static final long serialVersionUID = 3581412187696882763L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TALENT_PROGRAM_ID")
    protected TalentProgram talentProgram;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATUS_ID")
    protected DicTalentProgramRequestStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @Temporal(TemporalType.DATE)
    @Column(name = "REQUEST_DATE")
    protected Date requestDate;

    @Lob
    @Column(name = "ESSAY")
    protected String essay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CURRENT_STEP_ID")
    protected DicTalentProgramStep currentStep;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CURRENT_STEP_STATUS_ID")
    protected DicTalentProgramRequestStatus currentStepStatus;

    public DicTalentProgramStep getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(DicTalentProgramStep currentStep) {
        this.currentStep = currentStep;
    }

    public void setCurrentStepStatus(DicTalentProgramRequestStatus currentStepStatus) {
        this.currentStepStatus = currentStepStatus;
    }

    public DicTalentProgramRequestStatus getCurrentStepStatus() {
        return currentStepStatus;
    }

    public DicTalentProgramRequestStatus getStatus() {
        return status;
    }

    public void setStatus(DicTalentProgramRequestStatus status) {
        this.status = status;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public void setTalentProgram(TalentProgram talentProgram) {
        this.talentProgram = talentProgram;
    }

    public TalentProgram getTalentProgram() {
        return talentProgram;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setEssay(String essay) {
        this.essay = essay;
    }

    public String getEssay() {
        return essay;
    }

}