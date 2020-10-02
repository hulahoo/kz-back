package kz.uco.tsadv.web.screens;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.mainwindow.SideMenu;
import kz.uco.base.NotificationMessagePojo;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.entity.core.notification.SendingNotification;
import kz.uco.base.service.common.CommonService;
import kz.uco.base.web.template.BaseMainWindowOld;
import kz.uco.tsadv.components.GroupsComponent;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.uactivity.entity.Activity;
import kz.uco.uactivity.entity.StatusEnum;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtAppMainWindow extends BaseMainWindowOld {

    protected static final SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    protected AssignmentExt assignment;

    @Inject
    protected GroupsComponent groupsComponent;
    @Inject
    protected CommonService commonService;

    @Override
    public void init(Map<String, Object> params) {
        this.assignment = userSession.getAttribute("assignment");

        super.init(params);

        SideMenu.MenuItem recognitionWindow = sideMenu.getMenuItem("recognition-window");
        if (recognitionWindow != null) {
            recognitionWindow.setCommand(menuItem -> {
                Map<String, Object> recognitionParams = new HashMap<>();
                recognitionParams.put("defaultWindowId", "main");
                showWebPage("./open?screen=recognition-window", recognitionParams);
            });
        }

        SideMenu.MenuItem recognitionFeedbackWindow = sideMenu.getMenuItem("recognition-window-feedback");
        if (recognitionFeedbackWindow != null) {
            recognitionFeedbackWindow.setCommand(menuItem -> {
                Map<String, Object> recognitionParams = new HashMap<>();
                recognitionParams.put("defaultWindowId", "feedback");
                showWebPage("./open?screen=recognition-window-feedback", recognitionParams);
            });
        }
    }

    @Override
    protected String getUserFullName() {
        PersonExt person = userSession.getAttribute(StaticVariable.USER_PERSON);
        if (person != null) {
            return person.getLastName() + " " + person.getFirstName();
        }
        return super.getUserFullName();
    }

    @Override
    protected Action toHomeAction() {
        return new BaseAction("person-card") {
            @Override
            public void actionPerform(Component component) {
                if (assignment != null) {
                    openEditor("person-card", assignment, WindowManager.OpenType.NEW_TAB);
                } else {
                    showNotification("Person doesn't have assignment!");
                }
            }
        };
    }

    @Override
    protected Action toSettingsAction() {
        return new BaseAction("person-settings") {
            @Override
            public void actionPerform(Component component) {
                PersonGroupExt personGroup = userSession.getAttribute(StaticVariable.USER_PERSON_GROUP);
                if (personGroup != null) {
                    openWindow("person-settings",
                            WindowManager.OpenType.NEW_TAB,
                            ParamsMap.of("personGroup", personGroup));
                } else {
                    showNotification("PersonGroup is null!");
                }
            }
        };
    }

    @Override
    protected Action toNotificationsAction() {
        return new BaseAction("person-notification") {
            @Override
            public void actionPerform(Component component) {
                openWindow(
                        "my-tasks",
                        WindowManager.OpenType.NEW_TAB,
                        ParamsMap.of("openTab", "notificationsTab"));
            }
        };
    }

    @Override
    protected Action toTasksAction() {
        return new BaseAction("to-tasks") {
            @Override
            public void actionPerform(Component component) {
                openWindow(
                        "my-tasks",
                        WindowManager.OpenType.NEW_TAB,
                        ParamsMap.of("openTab", "tasksTab"));
            }
        };
    }

    @Override
    protected FileDescriptor getUserImage() {
        PersonExt personExt = userSession.getAttribute(StaticVariable.USER_PERSON);
        if (personExt != null) {
            return personExt.getImage();
        }
        return super.getUserImage();
    }

    @Override
    protected long fillNotifications() {
        List<Activity> activities = loadActivities("NOTIFICATION");

        listNotifications.removeAll();
        for (Activity activity : activities) {
            listNotifications.add(createNotification(activity));
        }

        return activities.size();
    }

    protected Component createNotification(Activity activity) {
        CssLayout cssLayout = getCssLayout(activity);
        Label label = createLabel(String.format("<div class=\"note-wrapper\">" +
                        "<div class=\"note-body\">" +
                        "<div class=\"note-template-name\">%s</div>" +
                        "<div class=\"note-time\">%s</div></div></div>",
                activity.getName(),
                activity.getCreateTs() == null ? "" : simpleDateTimeFormat.format(activity.getCreateTs())));

        cssLayout.add(label);
        return cssLayout;
    }

    protected Label createLabel(String value) {
        Label label = componentsFactory.createComponent(Label.class);
        label.setWidth("100%");
        label.setHeight("100%");
        label.setStyleName("b-white-space-n");
        label.setHtmlEnabled(true);
        label.setValue(value);
        return label;
    }

    protected void notificationClickListener(Activity activity) {
        Map<String, Object> windowParams = new HashMap<>();
        NotificationMessagePojo notificationMessagePojo = new NotificationMessagePojo();
        notificationMessagePojo.setNotificationBody(activity.getNotificationBody());
        notificationMessagePojo.setNotificationHeader(activity.getNotificationHeader());
        windowParams.put("notificationMessagePojo", notificationMessagePojo);
        windowParams.put("activity", activity);
        AbstractWindow abstractWindow = openWindow("ext-user-notification-view",
                WindowManager.OpenType.DIALOG,
                windowParams);
        abstractWindow.addCloseListener(actionId -> {
            if (actionId.equalsIgnoreCase("ok")) {
                refreshNotifications();
            }
        });
    }

    protected void refreshNotifications() {
        refreshCount(true, false, true);
    }

    protected SendingNotification getNotification(Activity activity) { //TODO реализовать метод
        return null;
    }

    protected List<Activity> loadActivities(String typeCode) {
        String query = "select e from uactivity$Activity e " +
                "where e.assignedUser.id = :currentUserId " +
                "  and e.status = :activeStatusId " +
                ("NOTIFICATION".equals(typeCode) ? "and e.type.code = :notificationCode " : "and e.type.code <> :notificationCode ") +
                "order by e.createTs desc";
        Map<String, Object> params = new HashMap<>();
        params.put("currentUserId", userSession.getUser().getId());
        params.put("activeStatusId", StatusEnum.active);
        params.put("notificationCode", "NOTIFICATION");
        return commonService.getEntities(Activity.class, query, params, "activity.view.tsadv");
    }

    @Override
    protected List loadTasks() {
        List<Activity> activities = loadActivities(null);
        for (Activity activity : activities) {
            listTasks.add(createTask(activity));
        }
        return activities;
    }

    protected CssLayout createTask(Activity activity) {
        CssLayout cssLayout = getCssLayout(activity);
        Label label = createLabel(String.format("<div class=\"note-wrapper\">" +
                        "<div class=\"note-body\">" +
                        "<div class=\"note-process-name\">%s</div>" +
                        "<div class=\"note-sender\">" +
                        "<span class=\"note-author\">%s</span> - <span class=\"note-time\">%s</span>\n" +
                        "</div></div></div>",
                activity.getName(),
                activity.getAssignedBy().getLogin(),
                simpleDateTimeFormat.format(activity.getStartDateTime())));

        cssLayout.add(label);
        return cssLayout;
    }

    protected CssLayout getCssLayout(Activity activity) {
        CssLayout cssLayout = componentsFactory.createComponent(CssLayout.class);
        cssLayout.setStyleName("note-content-item");
        cssLayout.addLayoutClickListener(event -> clickListener(activity));
        return cssLayout;
    }

    protected void clickListener(Activity activity) {
        if ("NOTIFICATION".equals(activity.getType().getCode())) {
            notificationClickListener(activity);
        } else {
            taskClickListener(activity);
        }
    }

    protected void taskClickListener(Activity activity) {
        if (activity.getReferenceId() != null) {
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("entityId", activity.getReferenceId());
            Map<String, Object> windowParams = new HashMap<>();
            windowParams.put("fromActivity", null);
            if ("REQUISITION_APPROVE".equals(activity.getType().getCode()))
                windowParams.put("isRecruiter", true);
            if ("INTERVIEW_APPROVE".equals(activity.getType().getCode())) {
                windowParams.put("readOnly", true);
            }
            windowParams.put("activity", activity.getId().toString());

            Entity entity = commonService.getEntity(metadata.getSession().getClassNN(activity.getType().getWindowProperty().getEntityName()).getJavaClass(),
                    "select e from " + activity.getType().getWindowProperty().getEntityName() + " e " +
                            "where e.id = :entityId", queryParams, activity.getType().getWindowProperty().getViewName()/*activity.getType().getWindowProperty().getViewName()*/);
            if (activity.getType().getCode().equals("KPI_APPROVE")) {
                openWindow(activity.getType().getWindowProperty().getScreenName(),
                        WindowManager.OpenType.NEW_TAB, ParamsMap.of("fromActivity", new Object(),
                                "performancePlanHeader", entity, "activity", activity));
            } else if (activity.getType().getCode().equalsIgnoreCase("PERSON_COMPETENCE_ASSESSMENT_APPROVE")) {
                openWindow(activity.getType().getWindowProperty().getScreenName(),
                        WindowManager.OpenType.NEW_TAB,
                        ParamsMap.of("personCompetenceAssessmentId", activity.getReferenceId(),
                                "activity", activity));
            } else if (activity.getType().getCode().equalsIgnoreCase("PERSON_COMPETENCE_ASSESSMENT_LEARN")) {
                openWindow(activity.getType().getWindowProperty().getScreenName(),
                        WindowManager.OpenType.NEW_TAB,
                        ParamsMap.of("personCompetenceAssessment", entity,
                                "activity", activity));
            } else {
                AbstractEditor abstractEditor = openEditor(activity.getType().getWindowProperty().getScreenName(),
                        entity, WindowManager.OpenType.NEW_TAB, windowParams);
                if (activity.getNotificationHeader() != null || activity.getNotificationBody() != null) {
                    groupsComponent.addActivityBodyToWindow(abstractEditor, activity);
                }
            }
        }
    }

    @Override
    protected long getTaskCount() {
        return loadActivities(null).size();
    }

    @Override
    protected long getNotificationCount() {
        return loadActivities("NOTIFICATION").size();
    }
}