package kz.uco.tsadv.modules.recognition;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.UserSessionSource;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

@NamePattern("%s|title")
@Table(name = "TSADV_RCG_FAQ")
@Entity(name = "tsadv$RcgFaq")
public class RcgFaq extends StandardEntity {
    private static final long serialVersionUID = -7839282703519069861L;

    @Transient
    @MetaProperty
    protected String title;

    @Column(name = "ORDER_")
    protected Integer order;

    @NotNull
    @Column(name = "TITLE_LANG1", nullable = false, length = 1000)
    protected String titleLang1;

    @Column(name = "TITLE_LANG2", length = 1000)
    protected String titleLang2;

    @Column(name = "TITLE_LANG3", length = 1000)
    protected String titleLang3;

    @Column(name = "TITLE_LANG4", length = 1000)
    protected String titleLang4;

    @Column(name = "TITLE_LANG5", length = 1000)
    protected String titleLang5;

    @Transient
    @MetaProperty
    protected String content;

    @Lob
    @Column(name = "CONTENT_LANG1")
    protected String contentLang1;

    @Lob
    @Column(name = "CONTENT_LANG2")
    protected String contentLang2;

    @Lob
    @Column(name = "CONTENT_LANG3")
    protected String contentLang3;

    @Lob
    @Column(name = "CONTENT_LANG4")
    protected String contentLang4;

    @Lob
    @Column(name = "CONTENT_LANG5")
    protected String contentLang5;

    @Column(name = "CODE", length = 20)
    protected String code;

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getOrder() {
        return order;
    }


    public void setTitleLang1(String titleLang1) {
        this.titleLang1 = titleLang1;
    }

    public String getTitleLang1() {
        return titleLang1;
    }

    public void setTitleLang2(String titleLang2) {
        this.titleLang2 = titleLang2;
    }

    public String getTitleLang2() {
        return titleLang2;
    }

    public void setTitleLang3(String titleLang3) {
        this.titleLang3 = titleLang3;
    }

    public String getTitleLang3() {
        return titleLang3;
    }

    public void setTitleLang4(String titleLang4) {
        this.titleLang4 = titleLang4;
    }

    public String getTitleLang4() {
        return titleLang4;
    }

    public void setTitleLang5(String titleLang5) {
        this.titleLang5 = titleLang5;
    }

    public String getTitleLang5() {
        return titleLang5;
    }

    public void setContentLang1(String contentLang1) {
        this.contentLang1 = contentLang1;
    }

    public String getContentLang1() {
        return contentLang1;
    }

    public void setContentLang2(String contentLang2) {
        this.contentLang2 = contentLang2;
    }

    public String getContentLang2() {
        return contentLang2;
    }

    public void setContentLang3(String contentLang3) {
        this.contentLang3 = contentLang3;
    }

    public String getContentLang3() {
        return contentLang3;
    }

    public void setContentLang4(String contentLang4) {
        this.contentLang4 = contentLang4;
    }

    public String getContentLang4() {
        return contentLang4;
    }

    public void setContentLang5(String contentLang5) {
        this.contentLang5 = contentLang5;
    }

    public String getContentLang5() {
        return contentLang5;
    }


    public String getTitle() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String langOrder = com.haulmont.cuba.core.sys.AppContext.getProperty("base.abstractDictionary.langOrder");
        String language = userSessionSource.getLocale().getLanguage();
        title = titleLang1;
        if (langOrder != null) {
            List<String> langs = Arrays.asList(langOrder.split(";"));
            switch (langs.indexOf(language)) {
                case 0: {
                    title = titleLang1;
                    break;
                }
                case 1: {
                    title = titleLang2;
                    break;
                }
                case 2: {
                    title = titleLang3;
                    break;
                }
                case 3: {
                    title = titleLang4;
                    break;
                }
                case 4: {
                    title = titleLang5;
                    break;
                }
            }
        }
        return title;
    }

    public String getContent() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String langOrder = com.haulmont.cuba.core.sys.AppContext.getProperty("base.abstractDictionary.langOrder");
        String language = userSessionSource.getLocale().getLanguage();
        content = contentLang1;
        if (langOrder != null) {
            List<String> langs = Arrays.asList(langOrder.split(";"));
            switch (langs.indexOf(language)) {
                case 0: {
                    content = contentLang1;
                    break;
                }
                case 1: {
                    content = contentLang2;
                    break;
                }
                case 2: {
                    content = contentLang3;
                    break;
                }
                case 3: {
                    content = contentLang4;
                    break;
                }
                case 4: {
                    content = contentLang5;
                    break;
                }
            }
        }
        return content;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }


}