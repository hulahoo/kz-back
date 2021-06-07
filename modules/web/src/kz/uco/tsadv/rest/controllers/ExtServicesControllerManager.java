package kz.uco.tsadv.rest.controllers;

import com.haulmont.addon.restapi.api.config.RestServicesConfiguration;
import com.haulmont.addon.restapi.api.exception.RestAPIException;
import com.haulmont.addon.restapi.api.service.ServicesControllerManager;
import com.haulmont.addon.restapi.api.transform.JsonTransformationDirection;
import com.haulmont.chile.core.datatypes.Datatype;
import com.haulmont.chile.core.datatypes.Datatypes;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.app.serialization.EntitySerializationOption;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Alibek Berdaulet
 */
public class ExtServicesControllerManager extends ServicesControllerManager {

    private static final Logger log = LoggerFactory.getLogger(ExtServicesControllerManager.class);

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Nullable
    @Override
    protected ServiceCallResult _invokeServiceMethod(String serviceName,
                                                     String methodName,
                                                     List<String> paramNames,
                                                     List<String> paramValuesStr,
                                                     String modelVersion) throws Throwable {
        Object service = AppBeans.get(serviceName);
        RestServicesConfiguration.RestMethodInfo restMethodInfo = restServicesConfiguration.getRestMethodInfo(serviceName, methodName, paramNames);
        if (restMethodInfo == null) {
            throw new RestAPIException("Service method not found",
                    serviceName + "." + methodName + "(" + String.join(",", paramNames) + ")",
                    HttpStatus.NOT_FOUND);
        }
        Method serviceMethod = restMethodInfo.getMethod();
        List<Object> paramValues = new ArrayList<>();
        Type[] types = restMethodInfo.getMethod().getGenericParameterTypes();
        for (int i = 0; i < types.length; i++) {
            int idx = i;
            try {
                idx = paramNames.indexOf(restMethodInfo.getParams().get(i).getName());
                paramValues.add(restParseUtils.toObject(types[i], paramValuesStr.get(idx), modelVersion));
            } catch (Exception e) {
                log.error("Error on parsing service param value", e);
                throw new RestAPIException("Invalid parameter value",
                        "Invalid parameter value for " + paramNames.get(idx),
                        HttpStatus.BAD_REQUEST,
                        e);
            }
        }

        Object methodResult;
        try {
            methodResult = serviceMethod.invoke(service, paramValues.toArray());
        } catch (InvocationTargetException | IllegalAccessException ex) {
            throw ex.getCause();
        }

        if (methodResult == null) {
            return null;
        }

        Class<?> methodReturnType = serviceMethod.getReturnType();
        if (Entity.class.isAssignableFrom(methodReturnType)) {
            Entity entity = (Entity) methodResult;
            restControllerUtils.applyAttributesSecurity(entity);
            String entityJson = entitySerializationAPI.toJson(entity,
                    null,
                    EntitySerializationOption.SERIALIZE_INSTANCE_NAME);
            entityJson = restControllerUtils.transformJsonIfRequired(entity.getMetaClass().getName(),
                    modelVersion, JsonTransformationDirection.TO_VERSION, entityJson);
            return new ServiceCallResult(entityJson, true);
        } else if (Collection.class.isAssignableFrom(methodReturnType)) {
            Type returnTypeArgument = getMethodReturnTypeArgument(serviceMethod);
            if ((returnTypeArgument instanceof Class && Entity.class.isAssignableFrom((Class) returnTypeArgument))
                    || isEntitiesCollection((Collection) methodResult)) {
                Collection<? extends Entity> entities = (Collection<? extends Entity>) methodResult;
                entities.forEach(entity -> restControllerUtils.applyAttributesSecurity(entity));
                String entitiesJson = entitySerializationAPI.toJson(entities,
                        null,
                        EntitySerializationOption.SERIALIZE_INSTANCE_NAME);
                if (returnTypeArgument != null) {
                    MetaClass metaClass = CollectionUtils.isEmpty(entities) ? null : entities.iterator().next().getMetaClass();
                    if (metaClass != null) {
                        entitiesJson = restControllerUtils.transformJsonIfRequired(metaClass.getName(), modelVersion,
                                JsonTransformationDirection.TO_VERSION, entitiesJson);
                    } else {
                        log.error("MetaClass for service collection parameter type {} not found", returnTypeArgument);
                    }
                }
                return new ServiceCallResult(entitiesJson, true);
            } else {
                return new ServiceCallResult(restParseUtils.serialize(methodResult), true);
            }
        } else {
            Datatype<?> datatype = Datatypes.get(methodReturnType);
            if (datatype != null) {
                return new ServiceCallResult(datatype.format(methodResult), false);
            } else {
                return new ServiceCallResult(restParseUtils.serialize(methodResult), true);
            }
        }
    }
}
