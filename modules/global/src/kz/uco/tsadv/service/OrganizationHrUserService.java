package kz.uco.tsadv.service;

import com.haulmont.cuba.security.entity.User;
import kz.uco.tsadv.modules.personal.dictionary.DicHrRole;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.model.OrganizationHrUser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public interface OrganizationHrUserService {

    String NAME = "tsadv_OrganizationHrUserService";

    String HR_ROLE_MANAGER = "MANAGER";
    String HR_ROLE_SUP_MANAGER = "SUP_MANAGER";
    String HR_ROLE_EMPLOYEE = "EMPLOYEE";
    String HR_ROLE_MANAGER_ASSISTANT = "MANAGER_ASSISTANT";
    String HR_SPECIALIST = "HR";

    List<OrganizationHrUser> getHrUsers(@Nonnull UUID organizationGroupId, @Nonnull String roleCode, @Nullable Integer counter);

    List<OrganizationHrUser> getHrUsersWithCounter(UUID organizationGroupId, String roleCode, Integer counter);

    void setNextCounter(OrganizationHrUser currentHrUser, String roleCode);

    List<DicHrRole> getDicHrRoles(UUID userId);

    List<? extends User> getHrUsersForPerson(@Nonnull UUID personGroupId, @Nonnull String roleCode);

    /**
     * @return true if user is manager or sup_manager of employee else false
     */
    boolean isManagerOrSupManager(@Nonnull UUID userId, @Nonnull UUID employeePersonGroupId);

    List<OrganizationGroupExt> getOrganizationList(@Nonnull UUID userId, @Nonnull String roleCode);
}
