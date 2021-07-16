package kz.uco.tsadv.service;


import com.haulmont.cuba.core.global.View;
import kz.uco.tsadv.global.entity.OrganizationTree;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.personal.dictionary.DicPayroll;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

//todo need codeReview
public interface OrganizationService extends kz.uco.base.service.OrganizationService {
    String NAME = "tsadv_OrganizationService";

    OrganizationGroupExt getOrganizationGroup(@Nullable View view);

    OrganizationGroupExt getOrganizationGroup(UUID userId, @Nullable View view);

    /**
     * Возвращает родительское подразделение для заданного подразделения в основной организационной иерархии
     */
    OrganizationGroupExt getParent(OrganizationGroupExt organizationGroup);

    OrganizationGroupExt getOrganizationGroupByPositionGroupId(UUID positionGroupId, String viewName);

    List<OrganizationTree> loadOrganizationTree(UUID parentId, UUID hierarchyId, String searchText);

    List<UUID> getAllNestedOrganizationIds(UUID organizationGroupId, UUID hierarchyId);

    DicPayroll getPayroll();

    void sendToResponsibleForIndicators();

    String getOrganizationPathToHint(UUID organizationGroupId, Date date);

    @Nonnull
    Set<OrganizationGroupExt> getOrganizationsWhereUserIsHr(TsadvUser userExt);

    Map<UUID, String> getOrganizationPathToHintForList(List<UUID> organizationGroupIds, Date date);
}