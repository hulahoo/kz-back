package kz.uco.tsadv.global.common;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.chile.core.model.MetaPropertyPath;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.security.entity.EntityOp;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author adilbekov.yernar
 */
@Component
public class MetadataMessageTools {

    @Inject
    protected Metadata metadata;

    @Inject
    protected ExtendedEntities extendedEntities;

    @Inject
    protected MessageTools messageTools;

    @Inject
    protected Messages messages;

    public Map<String, Object> getEntitiesLookupFieldOptions() {
        Map<String, Object> options = new TreeMap<>();

        for (MetaClass metaClass : metadata.getTools().getAllPersistentMetaClasses()) {
            if (readPermitted(metaClass)) {
                Class javaClass = metaClass.getJavaClass();
                if (Entity.class.isAssignableFrom(javaClass)) {
                    options.put(messageTools.getEntityCaption(metaClass) + " (" + metaClass.getName() + ")", metaClass);
                }
            }
        }

        return options;
    }

    public Map<String, Object> getEntitiesLookupFieldOptions(List<String> excludeEntities) {
        if (excludeEntities == null) return getEntitiesLookupFieldOptions();

        Map<String, Object> options = new TreeMap<>();

        for (MetaClass metaClass : metadata.getTools().getAllPersistentMetaClasses()) {
            if (!excludeEntities.contains(metaClass.getName())) {
                if (readPermitted(metaClass)) {
                    Class javaClass = metaClass.getJavaClass();
                    if (Entity.class.isAssignableFrom(javaClass)) {
                        options.put(messageTools.getEntityCaption(metaClass) + " (" + metaClass.getName() + ")", metaClass);
                    }
                }
            }
        }

        return options;
    }

    public boolean readPermitted(MetaClass metaClass) {
        return entityOpPermitted(metaClass, EntityOp.READ);
    }

    public boolean entityOpPermitted(MetaClass metaClass, EntityOp entityOp) {
        Security security = AppBeans.get(Security.NAME);
        return security.isEntityOpPermitted(metaClass, entityOp);
    }

    public String getPropertyCaption(MetaClass metaClass, MetaProperty metaProperty, Locale locale) {
        return getPropertyCaption(metaClass, metaProperty.getName(), locale);
    }

    public String getPropertyCaption(MetaClass metaClass, String propertyName, Locale locale) {
        Class originalClass = extendedEntities.getOriginalClass(metaClass);
        Class<?> ownClass = originalClass != null ? originalClass : metaClass.getJavaClass();
        String className = ownClass.getSimpleName();

        String key = className + "." + propertyName;
        String message = messages.getMessage(ownClass, key, locale);
        if (!message.equals(key)) {
            return message;
        }

        MetaPropertyPath propertyPath = metaClass.getPropertyPath(propertyName);
        if (propertyPath != null) {
            return getPropertyCaption(propertyPath.getMetaProperty(), locale);
        } else {
            return message;
        }
    }

    public String getPropertyCaption(MetaProperty property, Locale locale) {
        Class<?> declaringClass = property.getDeclaringClass();
        if (declaringClass == null)
            return property.getName();

        String className = declaringClass.getSimpleName();
        return messages.getMessage(declaringClass, className + "." + property.getName(), locale);
    }

    public String getEntityCaption(MetaClass metaClass, Locale locale) {
        return messageTools.getEntityCaption(metaClass, locale);
    }
}