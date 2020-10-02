package kz.uco.tsadv.service;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.security.entity.Role;
import com.haulmont.cuba.security.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

@Service(UserService.NAME)
public class UserServiceBean implements UserService {

    @Inject
    private DataManager dataManager;

    @Inject
    private UserSessionSource userSessionSource;

    @Override
    public boolean hasRole(String roleName) {
        return StringUtils.isNotBlank(roleName) && hasAnyRole(new String[]{roleName});
    }

    @Override
    public boolean hasRoles(String[] roleNames) {
        long roleNamesCount = roleNames != null ? roleNames.length : 0;
        long userRolesCount = userRolesCount(roleNames);
        return roleNamesCount == userRolesCount;
    }

    @Override
    public boolean hasAnyRole(String[] roleNames) {
        return userRolesCount(roleNames) > 0;
    }

    private Long userRolesCount(String[] roleNames) {
        boolean checkRoleNames = roleNames != null && roleNames.length > 0;

        LoadContext<Role> loadContext = LoadContext.create(Role.class);
        LoadContext.Query query = LoadContext.createQuery(String.format(
                "select ur.role from sec$UserRole ur " +
                        "join ur.user u " +
                        "join ur.role r " +
                        "where u.id = :userId %s",
                checkRoleNames ? "and r.name in :roleNames" : ""));
        query.setParameter("userId", getCurrentUser().getId());

        if (checkRoleNames) {
            query.setParameter("roleNames", Arrays.asList(roleNames));
        }

        loadContext.setQuery(query);
        loadContext.setView(View.LOCAL);
        return dataManager.getCount(loadContext);
    }

    @Override
    public List<Role> loadUserRoles() {
        return loadUserRoles(new String[0]);
    }

    @Override
    public List<Role> loadUserRoles(String... roleNames) {
        boolean checkRoleNames = roleNames != null && roleNames.length > 0;

        LoadContext<Role> loadContext = LoadContext.create(Role.class);
        LoadContext.Query query = LoadContext.createQuery(String.format(
                "select ur.role from sec$UserRole ur " +
                        "join ur.user u " +
                        "join ur.role r " +
                        "where u.id = :userId %s",
                checkRoleNames ? "and r.name in :roleNames" : ""));
        query.setParameter("userId", getCurrentUser().getId());

        if (checkRoleNames) {
            query.setParameter("roleNames", Arrays.asList(roleNames));
        }

        loadContext.setQuery(query);
        loadContext.setView(View.LOCAL);
        return dataManager.loadList(loadContext);
    }

    private User getCurrentUser() {
        return userSessionSource.getUserSession().getUser();
    }
}