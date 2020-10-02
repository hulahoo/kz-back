package kz.uco.tsadv.modules.learning.model;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@NamePattern("%s|langValue")
@Table(name = "TSADV_FAQ")
@Entity(name = "tsadv$Faq")
public class Faq extends AbstractParentEntity {
    private static final long serialVersionUID = -3560137413501147316L;

    @NotNull
    @Column(name = "LANG_VALUE1", nullable = false, length = 2000)
    protected String langValue1;

    @Column(name = "ORDER_")
    protected Integer order;

    @Column(name = "LANG_VALUE2", length = 2000)
    protected String langValue2;

    @Column(name = "LANG_VALUE3", length = 2000)
    protected String langValue3;

    @Column(name = "LANG_VALUE4", length = 2000)
    protected String langValue4;

    @Column(name = "LANG_VALUE5", length = 2000)
    protected String langValue5;

    @Lob
    @Column(name = "CONTENT_LANG_VALUE1")
    protected String contentLangValue1;

    @Lob
    @Column(name = "CONTENT_LANG_VALUE2")
    protected String contentLangValue2;

    @Lob
    @Column(name = "CONTENT_LANG_VALUE3")
    protected String contentLangValue3;

    @Lob
    @Column(name = "CONTENT_LANG_VALUE4")
    protected String contentLangValue4;

    @Lob
    @Column(name = "CONTENT_LANG_VALUE5")
    protected String contentLangValue5;

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getOrder() {
        return order;
    }


    public void setContentLangValue1(String contentLangValue1) {
        this.contentLangValue1 = contentLangValue1;
    }

    public String getContentLangValue1() {
        return contentLangValue1;
    }

    public void setContentLangValue2(String contentLangValue2) {
        this.contentLangValue2 = contentLangValue2;
    }

    public String getContentLangValue2() {
        return contentLangValue2;
    }

    public void setContentLangValue3(String contentLangValue3) {
        this.contentLangValue3 = contentLangValue3;
    }

    public String getContentLangValue3() {
        return contentLangValue3;
    }

    public void setContentLangValue4(String contentLangValue4) {
        this.contentLangValue4 = contentLangValue4;
    }

    public String getContentLangValue4() {
        return contentLangValue4;
    }

    public void setContentLangValue5(String contentLangValue5) {
        this.contentLangValue5 = contentLangValue5;
    }

    public String getContentLangValue5() {
        return contentLangValue5;
    }


    @MetaProperty(related = "langValue1,langValue2,langValue3,langValue4,langValue5")
    public String getLangValue() {
        return langValue1;
    }

    @MetaProperty(related = "contentLangValue1,contentLangValue2,contentLangValue3,contentLangValue4,contentLangValue5")
    public String getContentLangValue() {
        return contentLangValue1;
    }

    public void setLangValue1(String langValue1) {
        this.langValue1 = langValue1;
    }

    public String getLangValue1() {
        return langValue1;
    }

    public void setLangValue2(String langValue2) {
        this.langValue2 = langValue2;
    }

    public String getLangValue2() {
        return langValue2;
    }

    public void setLangValue3(String langValue3) {
        this.langValue3 = langValue3;
    }

    public String getLangValue3() {
        return langValue3;
    }

    public void setLangValue4(String langValue4) {
        this.langValue4 = langValue4;
    }

    public String getLangValue4() {
        return langValue4;
    }

    public void setLangValue5(String langValue5) {
        this.langValue5 = langValue5;
    }

    public String getLangValue5() {
        return langValue5;
    }


}