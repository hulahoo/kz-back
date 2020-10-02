package kz.uco.tsadv.modules.learning.model;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

@NamePattern("%s|langValue")
@Table(name = "TSADV_FAQ_CONTENT")
@Entity(name = "tsadv$FaqContent")
public class FaqContent extends AbstractParentEntity {
    private static final long serialVersionUID = -6027837281620390264L;

    @Lob
    @Column(name = "LANG_VALUE1", nullable = false)
    protected String langValue1;

    @Lob
    @Column(name = "LANG_VALUE2")
    protected String langValue2;

    @Lob
    @Column(name = "LANG_VALUE3")
    protected String langValue3;

    @Lob
    @Column(name = "LANG_VALUE4")
    protected String langValue4;

    @Lob
    @Column(name = "LANG_VALUE5")
    protected String langValue5;

    @MetaProperty(related = "langValue1,langValue2,langValue3,langValue4,langValue5")
    public String getLangValue() {
        return langValue1;
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