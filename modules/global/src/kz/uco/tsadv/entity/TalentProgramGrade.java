package kz.uco.tsadv.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;
import javax.persistence.OneToMany;
import kz.uco.tsadv.modules.personal.group.GradeGroup;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.ManyToOne;

@NamePattern("%s|talentProgram")
@Table(name = "TSADV_TALENT_PROGRAM_GRADE")
@Entity(name = "tsadv$TalentProgramGrade")
public class TalentProgramGrade extends StandardEntity {
    private static final long serialVersionUID = -8573322187499718867L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TALENT_PROGRAM_ID")
    protected TalentProgram talentProgram;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(DeletePolicy.CASCADE)
    @JoinColumn(name = "GRADE_GROUP_ID")
    protected GradeGroup gradeGroup;

    public TalentProgram getTalentProgram() {
        return talentProgram;
    }

    public void setTalentProgram(TalentProgram talentProgram) {
        this.talentProgram = talentProgram;
    }

    public GradeGroup getGradeGroup() {
        return gradeGroup;
    }

    public void setGradeGroup(GradeGroup gradeGroup) {
        this.gradeGroup = gradeGroup;
    }








}