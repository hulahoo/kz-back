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
@Table(name = "TSADV_DIC_TALENT_PROGRAM_STEP")
@Entity(name = "tsadv$DicTalentProgramStep")
public class DicTalentProgramStep extends AbstractDictionary {
    private static final long serialVersionUID = -4471357834141666271L;
}