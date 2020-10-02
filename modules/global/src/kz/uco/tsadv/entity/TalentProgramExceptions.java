package kz.uco.tsadv.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;

import javax.persistence.*;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

@NamePattern("%s|talentProgram")
@Table(name = "TSADV_TALENT_PROGRAM_EXCEPTIONS")
@Entity(name = "tsadv$TalentProgramExceptions")
public class TalentProgramExceptions extends StandardEntity {
    private static final long serialVersionUID = -8146848670712441305L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TALENT_PROGRAM_ID")
    protected TalentProgram talentProgram;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setTalentProgram(TalentProgram talentProgram) {
        this.talentProgram = talentProgram;
    }

    public TalentProgram getTalentProgram() {
        return talentProgram;
    }
}