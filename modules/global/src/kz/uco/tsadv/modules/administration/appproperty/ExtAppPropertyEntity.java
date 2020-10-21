package kz.uco.tsadv.modules.administration.appproperty;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.config.AppPropertyEntity;

@MetaClass(name = "tsadv_ExtAppPropertyEntity")
public class ExtAppPropertyEntity extends AppPropertyEntity {
    private static final long serialVersionUID = -8795233440860154266L;

    @MetaProperty
    private ExtAppPropertyEntity extParent;

    @MetaProperty
    private AppPropertyEntityDescription description;

    public ExtAppPropertyEntity getExtParent() {
        return extParent;
    }

    public void setExtParent(ExtAppPropertyEntity extParent) {
        this.extParent = extParent;
    }

    public AppPropertyEntityDescription getDescription() {
        return description;
    }

    public void setDescription(AppPropertyEntityDescription description) {
        this.description = description;
    }
}