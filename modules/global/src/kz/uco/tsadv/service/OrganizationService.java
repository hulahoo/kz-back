package kz.uco.tsadv.service;


import kz.uco.tsadv.global.entity.OrganizationTree;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.personal.dictionary.DicPayroll;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;

import javax.annotation.Nonnull;
import java.util.*;

public interface OrganizationService extends kz.uco.base.service.OrganizationService {
    String NAME = "tsadv_OrganizationService";

    /**
     * Возвращает родительское подразделение для заданного подразделения в основной организационной иерархии
     */
    OrganizationGroupExt getParent(OrganizationGroupExt organizationGroup);

    OrganizationGroupExt getOrganizationGroupByPositionGroupId(UUID positionGroupId, String viewName);

    List<OrganizationTree> loadOrganizationTree(UUID parentId, UUID hierarchyId, String searchText);

    List<UUID> getAllNestedOrganizationIds(UUID organizationGroupId, UUID hierarchyId);

    DicPayroll getPayroll();

    String getOrganizationPathToHint(UUID organizationGroupId, Date date);

    @Nonnull
    Set<OrganizationGroupExt> getOrganizationsWhereUserIsHr(UserExt userExt);

    Map<UUID,String> getOrganizationPathToHintForList(List<UUID> organizationGroupIds,Date date);
}