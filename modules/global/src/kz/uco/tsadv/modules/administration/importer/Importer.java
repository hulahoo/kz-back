package kz.uco.tsadv.modules.administration.importer;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

/**
 * @author veronika.buksha
 */

@NamePattern("%s|beanName,description")
@MetaClass(name = "tsadv$Importer")
public class Importer extends BaseUuidEntity {
    private static final long serialVersionUID = -5094342418060950482L;

    @MetaProperty(mandatory = true)
    protected String beanName;

    @MetaProperty
    protected String description;

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


}
