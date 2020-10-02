package kz.uco.tsadv.modules.recruitment.config;

import com.haulmont.cuba.core.config.type.TypeStringify;

public class OrganizationGenerationTypeStringify extends TypeStringify {
    @Override
    public String stringify(Object value) {
        for (OrganizationGenerationType generationType:OrganizationGenerationType.values()){
            if(generationType.equals(value)){
                return generationType.name();
            }
        }
        return null;
    }
}
