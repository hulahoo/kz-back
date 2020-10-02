package kz.uco.tsadv.modules.personal.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.sys.AppContext;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.modules.personal.model.OrderMasterEntity;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import com.haulmont.chile.core.annotations.NamePattern;

import java.util.Arrays;
import java.util.List;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@NamePattern("%s|name")
@Table(name = "TSADV_ORDER_MASTER_ENTITY_PROPERTY")
@Entity(name = "tsadv$OrderMasterEntityProperty")
public class OrderMasterEntityProperty extends AbstractParentEntity {
    private static final long serialVersionUID = -8103559138627688996L;

    @NotNull
    @Column(name = "NAME", nullable = false)
    protected String name;

    @NotNull
    @Column(name = "LANG_NAME1", nullable = false)
    protected String langName1;

    @Column(name = "LANG_NAME2")
    protected String langName2;

    @Column(name = "LANG_NAME3")
    protected String langName3;

    @Column(name = "LANG_NAME4")
    protected String langName4;

    @Column(name = "LANG_NAME5")
    protected String langName5;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_MASTER_ENTITY_ID")
    protected kz.uco.tsadv.modules.personal.model.OrderMasterEntity orderMasterEntity;

    public void setOrderMasterEntity(kz.uco.tsadv.modules.personal.model.OrderMasterEntity orderMasterEntity) {
        this.orderMasterEntity = orderMasterEntity;
    }

    public OrderMasterEntity getOrderMasterEntity() {
        return orderMasterEntity;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
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

    public void setLangName5(String langName5) {
        this.langName5 = langName5;
    }

    public String getLangName5() {
        return langName5;
    }

    @MetaProperty(related = "langName1,langName2,langName3,langName4,langName5")
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


}