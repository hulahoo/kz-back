package kz.uco.tsadv.beans;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.chile.core.model.Instance;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.core.global.DevelopmentException;
import com.haulmont.cuba.core.global.MetadataTools;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Pattern;

import static com.haulmont.bali.util.Preconditions.checkNotNullArgument;

public class CustomMetadataTools extends MetadataTools {
    private static final Pattern INSTANCE_NAME_SPLIT_PATTERN = Pattern.compile("[,;]");

    @Override
    public String getInstanceName(Instance instance) {
        checkNotNullArgument(instance, "instance is null");

        MetaClass metaClass = instance.getMetaClass();

        NamePatternRec rec = parseNamePattern(metaClass);
        if (rec == null) {
            return instance.toString();
        }

        if (rec.methodName != null) {
            try {
                Method method = instance.getClass().getMethod(rec.methodName);
                Object result;
                if (rec.format.equalsIgnoreCase("$")) {
                    result = method.invoke(instance, userSessionSource.getLocale().getLanguage());
                } else {
                    result = method.invoke(instance);
                }
                return (String) result;
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException("Error getting instance name", e);
            }
        }

        Object[] values = new Object[rec.fields.length];
        for (int i = 0; i < rec.fields.length; i++) {
            String fieldName = rec.fields[i];
            MetaProperty property = metaClass.getPropertyNN(fieldName);

            Object value = instance.getValue(fieldName);
            values[i] = format(value, property);
        }

        return String.format(rec.format, values);
    }

    @Nullable
    @Override
    public NamePatternRec parseNamePattern(MetaClass metaClass) {
        Map attributes = (Map) metaClass.getAnnotations().get(NamePattern.class.getName());
        if (attributes == null) {
            return null;
        }
        String pattern = (String) attributes.get("value");
        if (StringUtils.isBlank(pattern)) {
            return null;
        }

        int pos = pattern.indexOf("|");
        if (pos < 0) {
            throw new DevelopmentException("Invalid name pattern: " + pattern);
        }

        String format = StringUtils.substring(pattern, 0, pos);
        String trimmedFormat = format.trim();
        String methodName = trimmedFormat.startsWith("#") || trimmedFormat.startsWith("$") ? trimmedFormat.substring(1) : null;
        String fieldsStr = StringUtils.substring(pattern, pos + 1);
        String[] fields = INSTANCE_NAME_SPLIT_PATTERN.split(fieldsStr);
        return new NamePatternRec(format, methodName, fields);
    }
}