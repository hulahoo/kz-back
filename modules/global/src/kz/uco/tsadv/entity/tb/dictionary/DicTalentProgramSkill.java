package kz.uco.tsadv.entity.tb.dictionary;

import javax.persistence.Entity;
import javax.persistence.Table;
import kz.uco.base.entity.abstraction.AbstractDictionary;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import kz.uco.tsadv.entity.TalentProgramStepSkill;

@NamePattern("%s|langValue")
@Table(name = "TSADV_DIC_TALENT_PROGRAM_SKILL")
@Entity(name = "tsadv$DicTalentProgramSkill")
public class DicTalentProgramSkill extends AbstractDictionary {
    private static final long serialVersionUID = 7383716132252842398L;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TALENT_PROGRAM_STEP_SKILL_ID")
    protected TalentProgramStepSkill talentProgramStepSkill;

    public void setTalentProgramStepSkill(TalentProgramStepSkill talentProgramStepSkill) {
        this.talentProgramStepSkill = talentProgramStepSkill;
    }

    public TalentProgramStepSkill getTalentProgramStepSkill() {
        return talentProgramStepSkill;
    }







}