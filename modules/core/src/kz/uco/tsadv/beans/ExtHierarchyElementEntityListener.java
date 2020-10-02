package kz.uco.tsadv.beans;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.PersistenceTools;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.MetadataTools;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.base.entity.shared.HierarchyElement;
import kz.uco.base.listener.abstraction.HierarchyElementEntityListener;
import kz.uco.tsadv.exceptions.ItemNotFoundException;
import kz.uco.tsadv.modules.personal.model.HierarchyElementExt;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @author Alibek Berdaulet
 */
public class ExtHierarchyElementEntityListener extends HierarchyElementEntityListener {

    @Inject
    private Persistence persistence;

    @Inject
    private Metadata metadata;

    @Override
    public void onBeforeUpdate(HierarchyElement entity, EntityManager entityManager) {
        PersistenceTools persistenceTools = persistence.getTools();
        MetadataTools metadataTools = metadata.getTools();
        Set<String> dirtyFields = persistenceTools.getDirtyFields(entity);

        if (!Boolean.TRUE.equals(entity.getDoNotCopy()) && !dirtyFields.contains("endDate")) {
            Date systemDate = BaseCommonUtils.getSystemDate();
            Date startDate = dirtyFields.contains("startDate") ? entity.getStartDate() : systemDate;

            if ((!dirtyFields.contains("startDate") && systemDate.before(entity.getStartDate()))
                    || (dirtyFields.contains("startDate")
                    && entity.getStartDate().before((Date) persistenceTools.getOldValue(entity, "startDate")))) {
                throw new ItemNotFoundException("impossible.create.history");
            }

            HierarchyElement copy = metadataTools.copy(entity);
            for (String dirtyField : dirtyFields) {
                try {
                    copy.setValue(dirtyField, persistenceTools.getOldValue(entity, dirtyField));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            copy.setId(UUID.randomUUID());
            Date endDate = DateUtils.addDays(startDate, -1);
            copy.setEndDate(endDate);
            entity.setStartDate(startDate);

            Assert.isTrue(!copy.getStartDate().after(copy.getEndDate()), "Impossible to create a history!");

            entityManager.persist(copy);

            //копируем все дочерние элементы
            List<HierarchyElementExt> childList = getChildList(entityManager, entity, startDate);
            for (HierarchyElementExt child : childList) {
                HierarchyElementExt copyChild = metadataTools.copy(child);
                copyChild.setEndDate(endDate);
                copyChild.setParent((HierarchyElementExt) copy);
                copyChild.setDoNotCopy(true);
                copyChild.setId(UUID.randomUUID());

                Assert.isTrue(!copyChild.getStartDate().after(copyChild.getEndDate()), "Impossible to create a history!");
                entityManager.persist(copyChild);

                child.setStartDate(startDate);
                child.setDoNotCopy(true);
                entityManager.merge(child);
            }

        }
        entity.setDoNotCopy(null);
    }

    private List<HierarchyElementExt> getChildList(EntityManager em, HierarchyElement entity, Date date) {
        return em.createQuery("select e from base$HierarchyElementExt e where e.parent.id = :parentId and :date between e.startDate and e.endDate", HierarchyElementExt.class)
                .setParameter("parentId", entity.getId())
                .setParameter("date", date)
                .setViewName("hierarchyElement.full")
                .getResultList();
    }
}
