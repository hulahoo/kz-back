package kz.uco.tsadv.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.entity.tb.dictionary.DicTalentProgramSkill;

import javax.persistence.*;

@NamePattern("%s|talentProgramStep")
@Table(name = "TSADV_TALENT_PROGRAM_STEP_SKILL")
@Entity(name = "tsadv$TalentProgramStepSkill")
public class TalentProgramStepSkill extends StandardEntity {
    private static final long serialVersionUID = 2906410218412010104L;

    @Column(name = "ORDER_NUMBER")
    protected Integer orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TALENT_PROGRAM_STEP_ID")
    protected TalentProgramStep talentProgramStep;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SKILL_ID")
    protected DicTalentProgramSkill skill;

    public void setTalentProgramStep(TalentProgramStep talentProgramStep) {
        this.talentProgramStep = talentProgramStep;
    }

    public TalentProgramStep getTalentProgramStep() {
        return talentProgramStep;
    }

    public void setSkill(DicTalentProgramSkill skill) {
        this.skill = skill;
    }

    public DicTalentProgramSkill getSkill() {
        return skill;
    }


    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }


}
