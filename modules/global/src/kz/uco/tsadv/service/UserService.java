package kz.uco.tsadv.service;


import com.haulmont.cuba.security.entity.Role;

import java.util.List;

public interface UserService {
    String NAME = "tsadv_UserService";

    boolean hasRole(String roleName);

    boolean hasRoles(String... roleNames);

    boolean hasAnyRole(String... roleNames);

    boolean isEmployee();

    List<Role> loadUserRoles();

    List<Role> loadUserRoles(String... roleNames);
}