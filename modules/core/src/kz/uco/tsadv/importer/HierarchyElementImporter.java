package kz.uco.tsadv.importer;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.entity.BaseGenericIdEntity;
import com.haulmont.cuba.core.global.FileStorageException;
import kz.uco.base.entity.shared.ElementType;
import kz.uco.base.entity.shared.Hierarchy;
import kz.uco.base.importer.exception.ImportFileEofEvaluationException;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.importer.utils.XlsHelper;
import kz.uco.tsadv.importer.utils.XlsImporter;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.HierarchyElementExt;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;

/**
 * @author veronika.buksha
 */
@Component(HierarchyElementImporter.NAME)
@Scope("prototype")
public class HierarchyElementImporter extends XlsImporter {
    private static final Logger log = LoggerFactory.getLogger(HierarchyElementImporter.class);

    public static final String NAME = "tsadv_HierarchyElementImporter";

    public static final String HIERARCHY_ID = "hierarchyId";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String ELEMENT_TYPE = "elementType";
    public static final String LEGACY_ID = "legacyId";
    public static final String PARENT_LEGACY_ID = "parentLegacyId";

    @Inject
    private CommonService commonService;

    private Map<String, Integer> parentElementType = new HashMap<>();

    private Map<String, UUID> organizationLegacyMap = new HashMap<>();

    private Map<String, UUID> positionLegacyMap = new HashMap<>();

    @Override
    protected Map<String, Integer> createAttributesToColumns() {
        Map<String, Integer> columns = new HashMap<>();

        int i = 0;
        columns.put(HIERARCHY_ID, i++);
        columns.put(START_DATE, i++);
        columns.put(END_DATE, i++);
        columns.put(ELEMENT_TYPE, i++);
        columns.put(LEGACY_ID, i++);
        columns.put(PARENT_LEGACY_ID, i);

        return columns;
    }

    @Override
    protected Map<String, Object> createDefaultValues() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put(START_DATE, CommonUtils.getSystemDate());
        defaults.put(END_DATE, CommonUtils.getEndOfTime());
        return defaults;
    }

    @Override
    protected boolean eof(Row row) throws ImportFileEofEvaluationException {
        return eofByColumnNullValue(row, HIERARCHY_ID);
    }

    @Override
    protected List<BaseGenericIdEntity> getEntitiesToPersist(Map<String, Object> values, Map<String, Object> params) throws Exception {
        List<BaseGenericIdEntity> result = new ArrayList<>();
        Persistence persistence = getPersistence();

        try (Transaction trx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            Date startDate = XlsHelper.getParameterValue(values, START_DATE);
            Date endDate = XlsHelper.getParameterValue(values, END_DATE);

            UUID hierarchyId = XlsHelper.getParameterUUIDValue(values, HIERARCHY_ID);
            String legacyId = XlsHelper.getParameterStringValue(values, LEGACY_ID);
            String parentLegacyId = XlsHelper.getParameterStringValue(values, PARENT_LEGACY_ID);
            ElementType elementType = ElementType.fromId(XlsHelper.getParameterIntegerValue(values, ELEMENT_TYPE));

            if (elementType == null) {
                throw new IllegalArgumentException(String.format("ElementType by legacyId: [%s] not found!", legacyId));
            }

            parentElementType.put(legacyId, elementType.getId());

            HierarchyElementExt hierarchyElement = findHierarchyElement(em, elementType, hierarchyId, legacyId, startDate, endDate);
            if (hierarchyElement == null) {
                hierarchyElement = metadata.create(HierarchyElementExt.class);
            } else {
                hierarchyElement.setWriteHistory(true);
            }

            hierarchyElement.setHierarchy(commonService.getEntity(Hierarchy.class, hierarchyId));
            hierarchyElement.setStartDate(startDate);
            hierarchyElement.setEndDate(endDate);
            hierarchyElement.setElementType(elementType);

            switch (elementType) {
                case ORGANIZATION: {
                    try {
                        UUID organizationGroupId = organizationLegacyMap.get(legacyId);
                        if (organizationGroupId == null) {
                            throw new NullPointerException();
                        }

                        hierarchyElement.setOrganizationGroup(em.getReference(OrganizationGroupExt.class, organizationGroupId));
                    } catch (Exception ex) {
                        log.warn(String.format("OrganizationGroup by legacyId: [%s] not found!", legacyId));
                    }
                    break;
                }
                case POSITION:
                    try {
                        UUID positionGroupId = positionLegacyMap.get(legacyId);
                        if (positionGroupId == null) {
                            throw new NullPointerException();
                        }

                        hierarchyElement.setPositionGroup(em.getReference(PositionGroupExt.class, positionGroupId));
                    } catch (Exception ex) {
                        log.warn(String.format("PositionGroup by legacyId: [%s] not found!", legacyId));
                    }
                    break;
            }

            if (parentLegacyId != null) {
                hierarchyElement.setParent(findHierarchyElement(em, null, hierarchyId, parentLegacyId, startDate, endDate));
            }

            result.add(hierarchyElement);

            return result;
        }
    }

    @Override
    protected void afterEntitiesPersisted(List<BaseGenericIdEntity> persistedEntities) {
        super.afterEntitiesPersisted(persistedEntities);
        for (BaseGenericIdEntity e : persistedEntities) {
            if (e instanceof HierarchyElementExt)
                logHelper.info("Created hierarchy element: ", "base$HierarchyElementExt", "base$HierarchyElement.edit", (UUID) e.getId());
        }
    }

    private HierarchyElementExt findHierarchyElement(EntityManager em, ElementType elementType, UUID hierarchyId, String legacyId, Date startDate, Date endDate) {
        if (elementType == null) {
            Integer elementTypeId = parentElementType.get(legacyId);
            if (elementTypeId != null) {
                elementType = ElementType.fromId(elementTypeId);
            }

            if (elementType == null) {
                log.warn(String.format("Parent ElementType by legacyId: [%s] not found!", legacyId));
                return null;
            }
        }

        if (elementType.equals(ElementType.ORGANIZATION)) {
            UUID organizationGroupId = getOrganizationGroupId(em, legacyId);
            if (organizationGroupId != null) {
                return commonService.getEntity(HierarchyElementExt.class,
                        "  select e " +
                                "      from base$HierarchyElementExt e" +
                                " left join e.organizationGroup og " +
                                "     where e.hierarchy.id = :hierarchyId " +
                                "       and e.startDate = :startDate " +
                                "       and e.endDate = :endDate " +
                                "       and e.elementType = 1 and og.id = :orgGroupId " +
                                "       and e.deleteTs is null ",
                        ParamsMap.of("hierarchyId", hierarchyId,
                                "startDate", startDate,
                                "endDate", endDate,
                                "orgGroupId", organizationGroupId),
                        "hierarchyElement.full");
            }

        } else {
            UUID positionGroupId = getPositionGroupId(em, legacyId);
            if (positionGroupId != null) {
                return commonService.getEntity(HierarchyElementExt.class,
                        "  select e " +
                                "      from base$HierarchyElementExt e" +
                                " left join e.positionGroup pg " +
                                "     where e.hierarchy.id = :hierarchyId " +
                                "       and e.startDate = :startDate " +
                                "       and e.endDate = :endDate " +
                                "       and e.elementType = 2 and pg.id = :posGroupId" +
                                "       and e.deleteTs is null ",
                        ParamsMap.of("hierarchyId", hierarchyId,
                                "startDate", startDate,
                                "endDate", endDate,
                                "posGroupId", positionGroupId),
                        "hierarchyElement.full");
            }

        }
        return null;
    }

    private UUID getOrganizationGroupId(EntityManager em, String legacyId) {
        if (organizationLegacyMap.containsKey(legacyId)) {
            return organizationLegacyMap.get(legacyId);
        }

        Query query = em.createNativeQuery(
                "select e.group_id from BASE_ORGANIZATION e where e.legacy_id = ?1");
        query.setParameter(1, legacyId);

        Object organizationGroupIdObject = query.getFirstResult();
        if (organizationGroupIdObject != null) {
            UUID organizationGroupId = (UUID) organizationGroupIdObject;
            organizationLegacyMap.put(legacyId, organizationGroupId);
            return organizationGroupId;
        }
        return null;
    }

    private UUID getPositionGroupId(EntityManager em, String legacyId) {
        if (positionLegacyMap.containsKey(legacyId)) {
            return positionLegacyMap.get(legacyId);
        }

        Query query = em.createNativeQuery(
                "select p.group_id from BASE_POSITION p where p.legacy_id = ?1");
        query.setParameter(1, legacyId);

        Object positionGroupIdObject = query.getFirstResult();
        if (positionGroupIdObject != null) {
            UUID positionGroupId = (UUID) positionGroupIdObject;
            positionLegacyMap.put(legacyId, positionGroupId);
            return positionGroupId;
        }
        return null;
    }
}
