package kz.uco.tsadv.modules.personal.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.base.entity.dictionary.DicLanguage;
import kz.uco.tsadv.modules.personal.dictionary.DicLanguageLevel;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;

@Table(name = "TSADV_PERSON_LANGUAGE")
@Entity(name = "tsadv_PersonLanguage")
public class PersonLanguage extends AbstractParentEntity {
    private static final long serialVersionUID = 7855702406283809675L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    private PersonGroupExt personGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LANGUAGE_ID")
    private DicLanguage language;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LANGUAGE_LEVEL_ID")
    private DicLanguageLevel languageLevel;

    public DicLanguageLevel getLanguageLevel() {
        return languageLevel;
    }

    public void setLanguageLevel(DicLanguageLevel languageLevel) {
        this.languageLevel = languageLevel;
    }

    public DicLanguage getLanguage() {
        return language;
    }

    public void setLanguage(DicLanguage language) {
        this.language = language;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }
}