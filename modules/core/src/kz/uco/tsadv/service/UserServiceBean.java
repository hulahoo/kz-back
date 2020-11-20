package kz.uco.tsadv.service;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.security.entity.Role;
import com.haulmont.cuba.security.entity.User;
import kz.uco.base.entity.shared.PersonGroup;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
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


    @Override
    public boolean isEmployee() {
        PersonGroup userPersonGroup = getPersonGroup(getCurrentUser());
        if (userPersonGroup != null)
            return true;
        return false;
    }

    @Override
    public PersonGroupExt getPersonGroup(User user) {
        return getPersonGroup(user, "_local");
    }

    @Override
    public PersonGroupExt getPersonGroup(User user, String view) {
        LoadContext<PersonGroupExt> loadContext = LoadContext.create(PersonGroupExt.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select user.personGroup " +
                        "from tsadv$UserExt user " +
                        "where user.id = :userId ");
        query.setParameter("userId", user.getId());

        loadContext.setQuery(query);
        loadContext.setView(view);
        return dataManager.load(loadContext);
    }

    protected User getCurrentUser() {
        return userSessionSource.getUserSession().getUser();
    }


}