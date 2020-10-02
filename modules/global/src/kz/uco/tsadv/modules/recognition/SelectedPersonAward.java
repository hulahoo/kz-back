package kz.uco.tsadv.modules.recognition;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.persistence.Lob;

@Table(name = "TSADV_SELECTED_PERSON_AWARD")
@Entity(name = "tsadv$SelectedPersonAward")
public class SelectedPersonAward extends StandardEntity {
    private static final long serialVersionUID = 7198838227033220740L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "AWARD_PROGRAM_ID")
    protected AwardProgram awardProgram;

    @Lob
    @Column(name = "DESCRIPTION_LANG_VALUE1")
    protected String descriptionLangValue1;

    @Lob
    @Column(name = "DESCRIPTION_LANG_VALUE2")
    protected String descriptionLangValue2;

    @Lob
    @Column(name = "DESCRIPTION_LANG_VALUE3")
    protected String descriptionLangValue3;

    @Lob
    @Column(name = "DESCRIPTION_LANG_VALUE4")
    protected String descriptionLangValue4;

    @Lob
    @Column(name = "DESCRIPTION_LANG_VALUE5")
    protected String descriptionLangValue5;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @NotNull
    @Column(name = "AWARDED", nullable = false)
    protected Boolean awarded = false;


    public void setDescriptionLangValue2(String descriptionLangValue2) {
        this.descriptionLangValue2 = descriptionLangValue2;
    }

    public String getDescriptionLangValue2() {
        return descriptionLangValue2;
    }


    public void setDescriptionLangValue1(String descriptionLangValue1) {
        this.descriptionLangValue1 = descriptionLangValue1;
    }

    public String getDescriptionLangValue1() {
        return descriptionLangValue1;
    }

    public void setDescriptionLangValue3(String descriptionLangValue3) {
        this.descriptionLangValue3 = descriptionLangValue3;
    }

    public String getDescriptionLangValue3() {
        return descriptionLangValue3;
    }

    public void setDescriptionLangValue4(String descriptionLangValue4) {
        this.descriptionLangValue4 = descriptionLangValue4;
    }

    public String getDescriptionLangValue4() {
        return descriptionLangValue4;
    }

    public void setDescriptionLangValue5(String descriptionLangValue5) {
        this.descriptionLangValue5 = descriptionLangValue5;
    }

    public String getDescriptionLangValue5() {
        return descriptionLangValue5;
    }



    public void setAwardProgram(AwardProgram awardProgram) {
        this.awardProgram = awardProgram;
    }

    public AwardProgram getAwardProgram() {
        return awardProgram;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setAwarded(Boolean awarded) {
        this.awarded = awarded;
    }

    public Boolean getAwarded() {
        return awarded;
    }


}