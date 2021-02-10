package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractTimeBasedEntity;
import kz.uco.base.entity.abstraction.IGroupedEntity;
import kz.uco.tsadv.modules.personal.group.GradeGroup;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NamePattern("%s |gradeName,endDate,startDate")
@Table(name = "TSADV_GRADE")
@Entity(name = "tsadv$Grade")
public class Grade extends AbstractTimeBasedEntity implements IGroupedEntity<GradeGroup> {
    private static final long serialVersionUID = -8521029929932814000L;

    public final static String NAME = "GRADE";

    @Column(name = "GRADE_NAME", nullable = false, length = 500)
    protected String gradeName;

    @NotNull
    @Column(name = "RECOGNITION_NOMINATE", nullable = false)
    protected Boolean recognitionNominate = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID")
    protected GradeGroup group;

    @Column(name = "BONUS_PERCENT")
    protected Double bonusPercent;

    public Double getBonusPercent() {
        return bonusPercent;
    }

    public void setBonusPercent(Double bonusPercent) {
        this.bonusPercent = bonusPercent;
    }

    public void setRecognitionNominate(Boolean recognitionNominate) {
        this.recognitionNominate = recognitionNominate;
    }

    public Boolean getRecognitionNominate() {
        return recognitionNominate;
    }

    public void setGroup(GradeGroup group) {
        this.group = group;
    }

    public GradeGroup getGroup() {
        return group;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public String getGradeName() {
        return gradeName;
    }
}