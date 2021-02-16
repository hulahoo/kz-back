package kz.uco.tsadv.service.portal;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.app.AttributeAccessService;
import com.haulmont.cuba.core.entity.*;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.Security;
import com.haulmont.cuba.security.entity.EntityOp;
import kz.uco.tsadv.exceptions.PortalException;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

@Service(PortalAccessEntityAttributesService.NAME)
public class PortalAccessEntityAttributesServiceBean implements PortalAccessEntityAttributesService {

    @Inject
    private Metadata metadata;

    @Inject
    private Security security;

    @Inject
    private DataManager dataManager;

    @Inject
    private AttributeAccessService attributeAccessService;

    @Override
    public SecurityState entityAttributesSecurityState(String entityName, String entityId) {
        MetaClass metaClass = metadata.getClassNN(entityName);
        checkCanReadEntity(metaClass);

        LoadContext<Entity> ctx = new LoadContext<>(metaClass);
        Object id = getIdFromString(entityId, metaClass);
        ctx.setId(id);

        Entity entity = dataManager.load(ctx);
        checkEntityIsNotNull(entityName, entityId, entity);

        SecurityState securityState = attributeAccessService.computeSecurityState(entity);

        try {
            Field securityTokenField = SecurityState.class.getDeclaredField("securityToken");
            securityTokenField.setAccessible(true);

            ReflectionUtils.setField(securityTokenField, securityState, null);
            return securityState;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private Object getIdFromString(String entityId, MetaClass metaClass) {
        try {
            if (BaseDbGeneratedIdEntity.class.isAssignableFrom(metaClass.getJavaClass())) {
                if (BaseIdentityIdEntity.class.isAssignableFrom(metaClass.getJavaClass())) {
                    return IdProxy.of(Long.valueOf(entityId));
                } else if (BaseIntIdentityIdEntity.class.isAssignableFrom(metaClass.getJavaClass())) {
                    return IdProxy.of(Integer.valueOf(entityId));
                } else {
                    Class<?> clazz = metaClass.getJavaClass();
                    while (clazz != null) {
                        Method[] methods = clazz.getDeclaredMethods();
                        for (Method method : methods) {
                            if (method.getName().equals("getDbGeneratedId")) {
                                Class<?> idClass = method.getReturnType();
                                if (Long.class.isAssignableFrom(idClass)) {
                                    return Long.valueOf(entityId);
                                } else if (Integer.class.isAssignableFrom(idClass)) {
                                    return Integer.valueOf(entityId);
                                } else if (Short.class.isAssignableFrom(idClass)) {
                                    return Long.valueOf(entityId);
                                } else if (UUID.class.isAssignableFrom(idClass)) {
                                    return UUID.fromString(entityId);
                                }
                            }
                        }
                        clazz = clazz.getSuperclass();
                    }
                }
                throw new UnsupportedOperationException("Unsupported ID type in entity " + metaClass.getName());
            } else {
                //noinspection unchecked
                Method getIdMethod = metaClass.getJavaClass().getMethod("getId");
                Class<?> idClass = getIdMethod.getReturnType();
                if (UUID.class.isAssignableFrom(idClass)) {
                    return UUID.fromString(entityId);
                } else if (Integer.class.isAssignableFrom(idClass)) {
                    return Integer.valueOf(entityId);
                } else if (Long.class.isAssignableFrom(idClass)) {
                    return Long.valueOf(entityId);
                } else {
                    return entityId;
                }
            }
        } catch (Exception e) {
            throw new PortalException(
                    String.format("Cannot convert %s into valid entity ID", entityId));
        }
    }

    protected void checkCanReadEntity(MetaClass metaClass) {
        if (!security.isEntityOpPermitted(metaClass, EntityOp.READ)) {
            throw new PortalException(String.format("Reading of the %s is forbidden", metaClass.getName()));
        }
    }

    protected void checkEntityIsNotNull(String entityName, String entityId, Entity entity) {
        if (entity == null) {
            throw new PortalException(String.format("Entity %s with id %s not found", entityName, entityId));
        }
    }
}