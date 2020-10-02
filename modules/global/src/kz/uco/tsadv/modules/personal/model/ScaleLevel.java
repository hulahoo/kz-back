package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.sys.AppContext;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.modules.personal.model.Scale;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;
import com.haulmont.chile.core.annotations.MetaProperty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

@NamePattern("%s - %s|levelNumber,levelName")
@Table(name = "TSADV_SCALE_LEVEL")
@Entity(name = "tsadv$ScaleLevel")
public class ScaleLevel extends AbstractParentEntity {
    private static final long serialVersionUID = 1384759822527976718L;

    @Column(name = "LEVEL_NUMBER", nullable = false)
    protected Integer levelNumber;

    @Transient
    @MetaProperty(related = {"levelNameLang1", "levelNameLang2", "levelNameLang3", "levelNameLang4", "levelNameLang5"})
    protected String levelName;


    @NotNull
    @Column(name = "LEVEL_NAME_LANG1", nullable = false, length = 1000)
    protected String levelNameLang1;

    @Column(name = "LEVEL_NAME_LANG2", length = 1000)
    protected String levelNameLang2;

    @Column(name = "LEVEL_NAME_LANG3", length = 1000)
    protected String levelNameLang3;

    @Column(name = "LEVEL_NAME_LANG4", length = 1000)
    protected String levelNameLang4;

    @Column(name = "LEVEL_NAME_LANG5", length = 1000)
    protected String levelNameLang5;


    @Column(name = "LEVEL_DESCRIPTION_LANG1", length = 4000)
    protected String levelDescriptionLang1;

    @Column(name = "LEVEL_DESCRIPTION_LANG2", length = 4000)
    protected String levelDescriptionLang2;

    @Column(name = "LEVEL_DESCRIPTION_LANG3", length = 4000)
    protected String levelDescriptionLang3;

    @Column(name = "LEVEL_DESCRIPTION_LANG4", length = 4000)
    protected String levelDescriptionLang4;

    @Column(name = "LEVEL_DESCRIPTION_LANG5", length = 4000)
    protected String levelDescriptionLang5;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "open", "clear"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SCALE_ID")
    protected Scale scale;
    public void setLevelDescriptionLang1(String levelDescriptionLang1) {
        this.levelDescriptionLang1 = levelDescriptionLang1;
    }

    public String getLevelDescriptionLang1() {
        return levelDescriptionLang1;
    }

    public void setLevelDescriptionLang2(String levelDescriptionLang2) {
        this.levelDescriptionLang2 = levelDescriptionLang2;
    }

    public String getLevelDescriptionLang2() {
        return levelDescriptionLang2;
    }

    public void setLevelDescriptionLang3(String levelDescriptionLang3) {
        this.levelDescriptionLang3 = levelDescriptionLang3;
    }

    public String getLevelDescriptionLang3() {
        return levelDescriptionLang3;
    }

    public void setLevelDescriptionLang4(String levelDescriptionLang4) {
        this.levelDescriptionLang4 = levelDescriptionLang4;
    }

    public String getLevelDescriptionLang4() {
        return levelDescriptionLang4;
    }

    public void setLevelDescriptionLang5(String levelDescriptionLang5) {
        this.levelDescriptionLang5 = levelDescriptionLang5;
    }

    public String getLevelDescriptionLang5() {
        return levelDescriptionLang5;
    }


    public void setLevelNameLang1(String levelNameLang1) {
        this.levelNameLang1 = levelNameLang1;
    }

    public String getLevelNameLang1() {
        return levelNameLang1;
    }

    public void setLevelNameLang2(String levelNameLang2) {
        this.levelNameLang2 = levelNameLang2;
    }

    public String getLevelNameLang2() {
        return levelNameLang2;
    }

    public void setLevelNameLang3(String levelNameLang3) {
        this.levelNameLang3 = levelNameLang3;
    }

    public String getLevelNameLang3() {
        return levelNameLang3;
    }

    public void setLevelNameLang4(String levelNameLang4) {
        this.levelNameLang4 = levelNameLang4;
    }

    public String getLevelNameLang4() {
        return levelNameLang4;
    }

    public void setLevelNameLang5(String levelNameLang5) {
        this.levelNameLang5 = levelNameLang5;
    }

    public String getLevelNameLang5() {
        return levelNameLang5;
    }


    public Scale getScale() {
        return scale;
    }

    public void setScale(Scale scale) {
        this.scale = scale;
    }









    public void setLevelNumber(Integer levelNumber) {
        this.levelNumber = levelNumber;
    }

    public Integer getLevelNumber() {
        return levelNumber;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getLevelName() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String langOrder = AppContext.getProperty("base.abstractDictionary.langOrder");
        if (langOrder != null) {
            List<String> langs = Arrays.asList(langOrder.split(";"));
            switch (langs.indexOf(language)) {
                case 0:
                    return levelNameLang1;
                case 1:
                    return levelNameLang2;
                case 2:
                    return levelNameLang3;
                case 3:
                    return levelNameLang4;
                case 4:
                    return levelNameLang5;
                default:
                    return levelNameLang1;
            }
        }
        return levelNameLang1;
    }
}