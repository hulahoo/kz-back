package kz.uco.tsadv.modules.recruitment.config;

import com.haulmont.cuba.core.config.type.TypeFactory;

public class OrganizationGenerationTypeFactory extends TypeFactory {
    @Override
    public Object build(String string) {
        for (OrganizationGenerationType generationType: OrganizationGenerationType.values()){
            if (generationType.name().equals(string)){
                return generationType;
            }
        }
        return null;
    }
}
