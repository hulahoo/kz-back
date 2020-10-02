package kz.uco.tsadv.modules.recognition;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.reports.entity.Report;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|langName1")
@Table(name = "TSADV_MEDAL")
@Entity(name = "tsadv$Medal")
public class Medal extends StandardEntity {
    private static final long serialVersionUID = 5462255273222145204L;

    @NotNull
    @Column(name = "LANG_NAME1", nullable = false)
    protected String langName1;

    @NotNull
    @Column(name = "LANG_NAME2", nullable = false)
    protected String langName2;

    @Column(name = "LANG_NAME5")
    protected String langName5;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ICON_ID")
    protected FileDescriptor icon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEMPLATE_ID")
    protected Report template;

    @Column(name = "LANG_NAME3")
    protected String langName3;

    @Column(name = "LANG_NAME4")
    protected String langName4;

    @OneToMany(mappedBy = "childMedal")
    protected List<MedalCondition> childMedalConditions;

    @OneToMany(mappedBy = "medal")
    protected List<MedalCondition> medalConditions;

    @Column(name = "SORT")
    protected Integer sort;

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getSort() {
        return sort;
    }


    public void setMedalConditions(List<MedalCondition> medalConditions) {
        this.medalConditions = medalConditions;
    }

    public List<MedalCondition> getMedalConditions() {
        return medalConditions;
    }

    public void setChildMedalConditions(List<MedalCondition> childMedalConditions) {
        this.childMedalConditions = childMedalConditions;
    }

    public List<MedalCondition> getChildMedalConditions() {
        return childMedalConditions;
    }


    @MetaProperty(related = {"langName1", "langName2", "langName3", "langName4", "langName5"})
    public String getLangName() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String langOrder = AppContext.getProperty("base.abstractDictionary.langOrder");

        if (langOrder != null) {
            List<String> langs = Arrays.asList(langOrder.split(";"));
            switch (langs.indexOf(language)) {
                case 0: {
                    return langName1;
                }
                case 1: {
                    return langName2;
                }
                case 2: {
                    return langName3;
                }
                case 3: {
                    return langName4;
                }
                case 4: {
                    return langName5;
                }
                default:
                    return langName1;
            }
        }

        return langName1;
    }

    public void setLangName1(String langName1) {
        this.langName1 = langName1;
    }

    public String getLangName1() {
        return langName1;
    }

    public void setLangName2(String langName2) {
        this.langName2 = langName2;
    }

    public String getLangName2() {
        return langName2;
    }

    public void setLangName5(String langName5) {
        this.langName5 = langName5;
    }

    public String getLangName5() {
        return langName5;
    }

    public void setIcon(FileDescriptor icon) {
        this.icon = icon;
    }

    public FileDescriptor getIcon() {
        return icon;
    }

    public void setTemplate(Report template) {
        this.template = template;
    }

    public Report getTemplate() {
        return template;
    }

    public void setLangName3(String langName3) {
        this.langName3 = langName3;
    }

    public String getLangName3() {
        return langName3;
    }

    public void setLangName4(String langName4) {
        this.langName4 = langName4;
    }

    public String getLangName4() {
        return langName4;
    }


}