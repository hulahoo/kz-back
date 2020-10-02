package kz.uco.tsadv.beans;

import com.haulmont.cuba.core.EntityManager;
import kz.uco.base.common.BaseCommonUtils;

import static kz.uco.tsadv.modules.personal.group.PersonGroupExtDescriptorCustomizer.TIME_MACHINE_MOMENT_PROPERTY_NAME;

/**
 * Переопределёние cuba реализации Persistence
 * для установки в EntityManager дополнительных свойств используемых в движке EclipseLink
 * В частности для дополнительной фильтрации по дате машины времени
 * при выборке и сущностей с историей изменений
 * @author Василий Сафронов, Felix Kamalov
 */
public class PersistenceImpl extends com.haulmont.cuba.core.sys.PersistenceImpl {

    @Override
    public EntityManager getEntityManager(String store) {

        EntityManager entityManager = super.getEntityManager(store);

        entityManager.getDelegate().setProperty(
                TIME_MACHINE_MOMENT_PROPERTY_NAME,
                BaseCommonUtils.getSystemDate());

        return entityManager;
    }
    
}
