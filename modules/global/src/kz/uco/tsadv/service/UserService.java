package kz.uco.tsadv.service;


import com.haulmont.cuba.security.entity.Role;
import com.haulmont.cuba.security.entity.User;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import java.util.List;

public interface UserService {
    String NAME = "tsadv_UserService";

    boolean hasRole(String roleName);

    boolean hasRoles(String... roleNames);

    boolean hasAnyRole(String... roleNames);

    boolean isEmployee();

    List<Role> loadUserRoles();

    List<Role> loadUserRoles(String... roleNames);

    PersonGroupExt getPersonGroup(User user);

    PersonGroupExt getPersonGroup(User user, String view);
}