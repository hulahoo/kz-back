package kz.uco.tsadv.service;

import kz.uco.tsadv.modules.personal.dictionary.DicHrRole;
import kz.uco.tsadv.modules.personal.model.OrganizationHrUser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public interface OrganizationHrUserService {

    String NAME = "tsadv_OrganizationHrUserService";

    /**
     * @see EmployeeService#getHrUsers(UUID, String)
     */
    List<OrganizationHrUser> getHrUsers(@Nonnull UUID organizationGroupId, @Nonnull String roleCode, @Nullable Integer counter);

    List<OrganizationHrUser> getHrUsersWithCounter(UUID organizationGroupId, String roleCode, Integer counter);

    void setNextCounter(OrganizationHrUser currentHrUser, String roleCode);

    List<DicHrRole> getDicHrRoles(UUID userId);
}
