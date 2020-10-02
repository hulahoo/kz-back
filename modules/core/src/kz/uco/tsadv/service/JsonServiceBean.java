package kz.uco.tsadv.service;

import com.haulmont.cuba.core.entity.BaseGenericIdEntity;
import com.haulmont.cuba.core.entity.Entity;
import kz.uco.tsadv.converter.EntityGsonSerializationSupport;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(JsonService.NAME)
public class JsonServiceBean implements JsonService {

    @Override
    public String convertToString(Entity entity) {
        return new EntityGsonSerializationSupport().convertToString(entity);
    }

    @Override
    public String convertToString(List<BaseGenericIdEntity> entities) {
        if (entities == null || entities.isEmpty()) return "";
        StringBuilder stringBuilder = new StringBuilder();
        EntityGsonSerializationSupport support = new EntityGsonSerializationSupport();
        for (Entity e : entities) {
            stringBuilder.append(support.convertToString(e));
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}