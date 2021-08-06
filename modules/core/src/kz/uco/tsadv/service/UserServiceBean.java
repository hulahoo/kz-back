package kz.uco.tsadv.service;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.security.entity.Role;
import com.haulmont.cuba.security.entity.User;
import kz.uco.base.entity.shared.PersonGroup;
import kz.uco.base.service.NotificationSenderAPIService;
import kz.uco.tsadv.config.FrontConfig;
import kz.uco.tsadv.entity.dbview.ActivityTask;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

@Service(UserService.NAME)
public class UserServiceBean implements UserService {

    @Inject
    private DataManager dataManager;

    @Inject
    private UserSessionSource userSessionSource;
    @Inject
    private NotificationSenderAPIService notificationSenderAPIService;
    @Inject
    private FrontConfig frontConfig;

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

    @Override
    public void sendNotifyByNotClosedTasks() {
        List<TsadvUser> tsadvUserList = dataManager.load(TsadvUser.class)
                .query("select distinct a.assignedUser from tsadv$ActivityTask e " +
                        " join e.activity a " +
                        " where a.status <> '20' " +
                        " and a.type.code <> 'NOTIFICATION'")
                .view("user.edit")
                .list();
        for (TsadvUser tsadvUser : tsadvUserList) {
            List<ActivityTask> activityTasks = dataManager.load(ActivityTask.class)
                    .query("select distinct e from tsadv$ActivityTask e " +
                            " join e.activity a " +
                            " where a.status <> '20' " +
                            " and a.type.code <> 'NOTIFICATION' " +
                            " and a.assignedUser.id = :tsadvUser")
                    .parameter("tsadvUser", tsadvUser.getId())
                    .view("activityTask.view")
                    .list();
            String tableRu = "<table border=\"1\" cellpadding=\"1\" cellspacing=\"1\" style=\"width:500px\">\n" +
                    "    <tbody>\n" +
                    "    <tr>\n" +
                    "        <td>Код заявки</td>\n" +
                    "        <td>Процесс</td>\n" +
                    "        <td>Дата заявки</td>\n" +
                    "    </tr>\n";
            String tableEn = "<table border=\"1\" cellpadding=\"1\" cellspacing=\"1\" style=\"width:500px\">\n" +
                    "    <tbody>\n" +
                    "    <tr>\n" +
                    "        <td>Request number</td>\n" +
                    "        <td>Process</td>\n" +
                    "        <td>Request date</td>\n" +
                    "    </tr>\n";
            StringBuilder stringBuilderRu = new StringBuilder();
            StringBuilder stringBuilderEn = new StringBuilder();
            String endTable = "    </tbody>\n" +
                    "</table>";
            activityTasks.forEach(activityTask -> {
                stringBuilderRu.append("    <tr>\n" +
                        "        <td>" +
                        activityTask.getOrderCode() +
                        "        </td>\n" +
                        "        <td>" +
                        activityTask.getProcessRu() +
                        "        </td>\n" +
                        "        <td>" +
                        getRequestDate(activityTask.getOrderDate()) +
                        "         </td>\n" +
                        "    </tr>\n");
                stringBuilderEn.append("    <tr>\n" +
                        "        <td>" +
                        activityTask.getOrderCode() +
                        "        </td>\n" +
                        "        <td>" +
                        activityTask.getProcessEn() +
                        "        </td>\n" +
                        "        <td>" +
                        getRequestDate(activityTask.getOrderDate()) +
                        "         </td>\n" +
                        "    </tr>\n");
            });
            Map<String, Object> maps = new HashMap<>();
            String link = "<a href=\"" + frontConfig.getFrontAppUrl()
                    + "\" target=\"_blank\">%s " + "</a>";
            maps.put("tableRu", tableRu + stringBuilderRu + endTable);
            maps.put("tableEn", tableEn + stringBuilderEn + endTable);
            maps.put("linkRu", String.format(link, "ссылке"));
            maps.put("linkEn", String.format(link, "link"));
            notificationSenderAPIService.sendParametrizedNotification("notClosedTasks", tsadvUser, maps);
        }
    }

    protected String getRequestDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        return date != null ? formatter.format(date) : "";
    }
}