package kz.uco.tsadv.modules.recognition;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.base.entity.shared.Person;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.recognition.dictionary.DicPersonPreferenceType;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

@Table(name = "TSADV_PERSON_PREFERENCE")
@Entity(name = "tsadv$PersonPreference")
public class PersonPreference extends AbstractParentEntity {
    private static final long serialVersionUID = -769043250705332083L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;



    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PREFERENCE_TYPE_ID")
    protected DicPersonPreferenceType preferenceType;

    @Column(name = "DESCRIPTION", length = 2000)
    protected String description;

    @Column(name = "DESCRIPTION_EN", length = 2000)
    protected String descriptionEn;

    @Column(name = "DESCRIPTION_RU", length = 2000)
    protected String descriptionRu;

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionRu(String descriptionRu) {
        this.descriptionRu = descriptionRu;
    }

    public String getDescriptionRu() {
        return descriptionRu;
    }


    public void setPreferenceType(DicPersonPreferenceType preferenceType) {
        this.preferenceType = preferenceType;
    }

    public DicPersonPreferenceType getPreferenceType() {
        return preferenceType;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }




}