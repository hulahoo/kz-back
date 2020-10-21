package kz.uco.tsadv.modules.administration.appproperty;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Table(name = "TSADV_APP_PROPERTY_ENTITY_DESCRIPTION")
@Entity(name = "tsadv_AppPropertyEntityDescription")
@NamePattern("%s|value")
public class AppPropertyEntityDescription extends StandardEntity {
    private static final long serialVersionUID = 8524418265465528745L;

    @Column(name = "DESCRIPTION", length = 512)
    private String value;

    @Column(name = "APP_PROPERTY_NAME", nullable = false, unique = true, length = 512)
    @NotNull
    private String appPropertyName;

    public String getAppPropertyName() {
        return appPropertyName;
    }

    public void setAppPropertyName(String appPropertyName) {
        this.appPropertyName = appPropertyName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}