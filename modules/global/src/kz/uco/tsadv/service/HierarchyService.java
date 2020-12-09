package kz.uco.tsadv.service;


import com.haulmont.cuba.core.global.View;
import kz.uco.base.entity.shared.Hierarchy;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.HierarchyElementExt;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface HierarchyService {
    String NAME = "tsadv_HierarchyService";

    List<HierarchyElementExt> loadPositionHierarchyElement(UUID hierarchyId, UUID parentHierarchyId, View view);

    Long hierarchyElementChildrenCount(UUID parentHierarchyElementId, UUID hierarchyId);

    @Nullable
    UUID getHierarchyElementId(UUID positionGroupId, UUID hierarchyId);

    @Nullable
    PositionGroupExt getParentPosition(PositionGroupExt positionGroupExt, String view);

    boolean isParent(UUID positionGroupId, UUID hierarchyId);

    List<UUID> getOrganizationGroupIdChild(UUID organizationGroupId);

    List<UUID> getPositionGroupIdChild(UUID positionGroupId, Date date);

    /**
     * Возвращает список пользователей руководителей для заданной штатной единицы в заданной иерархии
     */
    List<UserExt> findManagerUsers(UUID positionGroupId, UUID hierarchyId);

    List<UUID> getHierarchyException();

    List<HierarchyElementExt> getChildHierarchyElement(@Nonnull Hierarchy hierarchy, @Nullable HierarchyElementExt parent);

    /**
     * @return all found HierarchyElement with all parents and children
     */
    List<HierarchyElementExt> search(@Nonnull Hierarchy hierarchy, @Nonnull String searchText);
}