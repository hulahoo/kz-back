package kz.uco.tsadv.modules.recognition.dictionary;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.sys.AppContext;
import kz.uco.base.entity.abstraction.AbstractDictionary;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.List;

@Table(name = "TSADV_DIC_QUALITY")
@Entity(name = "tsadv$DicQuality")
public class DicQuality extends AbstractDictionary {
    private static final long serialVersionUID = -2328619383775840154L;

    @Column(name = "FULL_LANG_VALUE1", length = 2000)
    protected String fullLangValue1;

    @Column(name = "FULL_LANG_VALUE2", length = 2000)
    protected String fullLangValue2;

    @Column(name = "FULL_LANG_VALUE3", length = 2000)
    protected String fullLangValue3;

    @Column(name = "FULL_LANG_VALUE4", length = 2000)
    protected String fullLangValue4;

    @Column(name = "FULL_LANG_VALUE5", length = 2000)
    protected String fullLangValue5;

    public void setFullLangValue1(String fullLangValue1) {
        this.fullLangValue1 = fullLangValue1;
    }

    public String getFullLangValue1() {
        return fullLangValue1;
    }

    public void setFullLangValue2(String fullLangValue2) {
        this.fullLangValue2 = fullLangValue2;
    }

    public String getFullLangValue2() {
        return fullLangValue2;
    }

    public void setFullLangValue3(String fullLangValue3) {
        this.fullLangValue3 = fullLangValue3;
    }

    public String getFullLangValue3() {
        return fullLangValue3;
    }

    public void setFullLangValue4(String fullLangValue4) {
        this.fullLangValue4 = fullLangValue4;
    }

    public String getFullLangValue4() {
        return fullLangValue4;
    }

    public void setFullLangValue5(String fullLangValue5) {
        this.fullLangValue5 = fullLangValue5;
    }

    public String getFullLangValue5() {
        return fullLangValue5;
    }

    @MetaProperty(related = {"fullLangValue1", "fullLangValue2", "fullLangValue3", "fullLangValue4", "fullLangValue5"})
    public String getFullLangValue() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String descriptionOrder = AppContext.getProperty("base.abstractDictionary.langOrder");

        if (descriptionOrder != null) {
            List<String> langs = Arrays.asList(descriptionOrder.split(";"));
            switch (langs.indexOf(language)) {
                case 0: {
                    return fullLangValue1;
                }
                case 1: {
                    return fullLangValue2;
                }
                case 2: {
                    return fullLangValue3;
                }
                case 3: {
                    return fullLangValue4;
                }
                case 4: {
                    return fullLangValue5;
                }
                default:
                    return fullLangValue1;
            }
        }
        return fullLangValue1;
    }
}