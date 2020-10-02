package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.sys.AppContext;
import kz.uco.tsadv.modules.personal.model.OrderMaster;
import kz.uco.tsadv.modules.personal.model.OrderMasterEntityProperty;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.chile.core.annotations.Composition;

@Table(name = "TSADV_ORDER_MASTER_ENTITY")
@Entity(name = "tsadv$OrderMasterEntity")
public class OrderMasterEntity extends AbstractParentEntity {
    private static final long serialVersionUID = -7874047779680570923L;

    @NotNull
    @Column(name = "ENTITY_NAME", nullable = false)
    protected String entityName;

    @NotNull
    @Column(name = "DEFAULT_ENTITY", nullable = false)
    protected Boolean defaultEntity = false;

    @NotNull
    @Column(name = "ENTITY_NAME_LANG1", nullable = false)
    protected String entityNameLang1;

    @Column(name = "ENTITY_NAME_LANG2")
    protected String entityNameLang2;

    @Column(name = "ENTITY_NAME_LANG3")
    protected String entityNameLang3;

    @Column(name = "ENTITY_NAME_LANG4")
    protected String entityNameLang4;

    @Column(name = "ENTITY_NAME_LANG5")
    protected String entityNameLang5;

    @NotNull
    @Column(name = "ORDER_", nullable = false)
    protected Integer order;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "orderMasterEntity")
    protected List<OrderMasterEntityProperty> properties;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_MASTER_ID")
    protected OrderMaster orderMaster;

    public void setOrderMaster(OrderMaster orderMaster) {
        this.orderMaster = orderMaster;
    }

    public OrderMaster getOrderMaster() {
        return orderMaster;
    }


    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setDefaultEntity(Boolean defaultEntity) {
        this.defaultEntity = defaultEntity;
    }

    public Boolean getDefaultEntity() {
        return defaultEntity;
    }

    public void setEntityNameLang1(String entityNameLang1) {
        this.entityNameLang1 = entityNameLang1;
    }

    public String getEntityNameLang1() {
        return entityNameLang1;
    }

    public void setEntityNameLang2(String entityNameLang2) {
        this.entityNameLang2 = entityNameLang2;
    }

    public String getEntityNameLang2() {
        return entityNameLang2;
    }

    public void setEntityNameLang3(String entityNameLang3) {
        this.entityNameLang3 = entityNameLang3;
    }

    public String getEntityNameLang3() {
        return entityNameLang3;
    }

    public void setEntityNameLang4(String entityNameLang4) {
        this.entityNameLang4 = entityNameLang4;
    }

    public String getEntityNameLang4() {
        return entityNameLang4;
    }

    public void setEntityNameLang5(String entityNameLang5) {
        this.entityNameLang5 = entityNameLang5;
    }

    public String getEntityNameLang5() {
        return entityNameLang5;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getOrder() {
        return order;
    }

    public void setProperties(List<OrderMasterEntityProperty> properties) {
        this.properties = properties;
    }

    public List<OrderMasterEntityProperty> getProperties() {
        return properties;
    }

    @MetaProperty(related = "entityNameLang1,entityNameLang2,entityNameLang3,entityNameLang4,entityNameLang5")
    public String getEntityLangName() {
        UserSessionSource userSessionSource = AppBeans.get("cuba_UserSessionSource");
        String language = userSessionSource.getLocale().getLanguage();
        String langOrder = AppContext.getProperty("base.abstractDictionary.langOrder");

        if (langOrder != null) {
            List<String> langs = Arrays.asList(langOrder.split(";"));
            switch (langs.indexOf(language)) {
                case 0: {
                    return entityNameLang1;
                }
                case 1: {
                    return entityNameLang2;
                }
                case 2: {
                    return entityNameLang3;
                }
                case 3: {
                    return entityNameLang4;
                }
                case 4: {
                    return entityNameLang5;
                }
                default:
                    return entityNameLang1;
            }
        }

        return entityNameLang1;
    }


}