package kz.uco.tsadv.service;


import com.haulmont.cuba.core.entity.BaseGenericIdEntity;
import com.haulmont.cuba.core.entity.Entity;

import java.util.List;

public interface JsonService {
    String NAME = "tsadv_JsonService";

    String convertToString(Entity entity);

    String convertToString(List<BaseGenericIdEntity> entities);
}