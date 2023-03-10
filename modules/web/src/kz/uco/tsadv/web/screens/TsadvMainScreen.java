package kz.uco.tsadv.web.screens;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.CssLayout;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.base.NotificationMessagePojo;
import kz.uco.base.service.common.CommonService;
import kz.uco.base.web.root.BaseMainScreen;
import kz.uco.tsadv.components.GroupsComponent;
import kz.uco.tsadv.web.screens.activity.ActivityForHandbellBrowse;
import kz.uco.uactivity.entity.Activity;
import kz.uco.uactivity.entity.StatusEnum;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@UiController("tsadvMainScreen")
@UiDescriptor("tsadv-main-screen.xml")
public class TsadvMainScreen extends BaseMainScreen {
    @Inject
    private CommonService commonService;
    @Inject
    private UiComponents uiComponents;
    @Inject
    private ScreenBuilders screenBuilders;
    @Inject
    private GroupsComponent groupsComponent;

    @Override
    protected List loadTasks() {
        List<Activity> activities = loadActivities(null);
        for (Activity activity : activities) {
            listTasks.add(createTask(activity));
        }
        return activities;
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
        CssLayout cssLayout = uiComponents.create(CssLayout.class);
        cssLayout.setStyleName("note-content-item");
        cssLayout.addLayoutClickListener(event -> clickListener(activity));
        return cssLayout;
    }

    protected Label createLabel(String value) {
        Label label = uiComponents.create(Label.class);
        label.setWidth("100%");
        label.setHeight("100%");
        label.setStyleName("b-white-space-n");
        label.setHtmlEnabled(true);
        label.setValue(value);
        return label;
    }

    protected void clickListener(Activity activity) {
        if ("NOTIFICATION".equals(activity.getType().getCode())) {
            notificationClickListener(activity);
        } else {
            taskClickListener(activity);
        }
    }

    protected void notificationClickListener(Activity activity) {
        Map<String, Object> windowParams = new HashMap<>();
        NotificationMessagePojo notificationMessagePojo = new NotificationMessagePojo();
        notificationMessagePojo.setNotificationBody(activity.getNotificationBody());
        notificationMessagePojo.setNotificationHeader(activity.getNotificationHeader());
        windowParams.put("notificationMessagePojo", notificationMessagePojo);
        windowParams.put("activity", activity);
        Screen screen = screenBuilders.screen(this)
                .withScreenClass(ActivityForHandbellBrowse.class)
                .withOpenMode(OpenMode.THIS_TAB)
                .withOptions(new MapScreenOptions(windowParams))
                .build().show();
        screen.addAfterCloseListener(afterCloseEvent -> {
            if (((StandardCloseAction) afterCloseEvent.getCloseAction()).getActionId().equalsIgnoreCase("ok")) {
                refreshNotifications();
            }
        });
    }

    protected void taskClickListener(Activity activity) {
        if (activity.getReferenceId() != null) {
            Map<String, Object> windowParams = new HashMap<>();
            windowParams.put("fromActivity", null);
            if ("REQUISITION_APPROVE".equals(activity.getType().getCode()))
                windowParams.put("isRecruiter", true);
            if ("INTERVIEW_APPROVE".equals(activity.getType().getCode())) {
                windowParams.put("readOnly", true);
            }
            windowParams.put("activity", activity.getId().toString());

            MetaClass metaClass = metadata.getSession().getClassNN(activity.getType().getWindowProperty().getEntityName());
            Entity<UUID> entity = dataManager.load(
                    Id.of(activity.getReferenceId(), metaClass.getJavaClass()))
                    .one();

            if (activity.getType().getCode().equals("KPI_APPROVE")) {
                screenBuilders.screen(this)
                        .withScreenId(activity.getType().getWindowProperty().getScreenName())
                        .withOpenMode(OpenMode.NEW_TAB)
                        .withOptions(new MapScreenOptions(ParamsMap.of(
                                "fromActivity", new Object(),
                                "performancePlanHeader", entity,
                                "activity", activity)))
                        .build().show();
            } else if (activity.getType().getCode().equalsIgnoreCase("PERSON_COMPETENCE_ASSESSMENT_APPROVE")) {
                screenBuilders.screen(this)
                        .withScreenId(activity.getType().getWindowProperty().getScreenName())
                        .withOpenMode(OpenMode.NEW_TAB)
                        .withOptions(new MapScreenOptions(ParamsMap.of(
                                "personCompetenceAssessmentId", activity.getReferenceId(),
                                "activity", activity)))
                        .build().show();
            } else if (activity.getType().getCode().equalsIgnoreCase("PERSON_COMPETENCE_ASSESSMENT_LEARN")) {
                screenBuilders.screen(this)
                        .withScreenId(activity.getType().getWindowProperty().getScreenName())
                        .withOpenMode(OpenMode.NEW_TAB)
                        .withOptions(new MapScreenOptions(ParamsMap.of(
                                "personCompetenceAssessment", entity,
                                "activity", activity)))
                        .build().show();
            } else {
                Screen screen = screenBuilders.editor(
                        metaClass.getJavaClass(), this)
                        .withScreenId(activity.getType().getWindowProperty().getScreenName())
                        .editEntity(entity)
                        .withOpenMode(OpenMode.NEW_TAB)
                        .withOptions(new MapScreenOptions(windowParams))
                        .build().show();
                if (activity.getNotificationHeader() != null || activity.getNotificationBody() != null) {
                    groupsComponent.addActivityHeaderToScreen(screen, activity);
                }
            }
        }
    }

    protected void refreshNotifications() {
        refreshCount(true, false, true);
    }

    @Override
    protected Action toNotificationsAction() {
        return new BaseAction("user-notification") {
            @Override
            public void actionPerform(Component component) {
                screenBuilders.screen(mainMenuFrameOwner)
                        .withScreenId("my-tasks")
                        .withOptions(new MapScreenOptions(ParamsMap.of("notification", true)))
                        .build()
                        .show();
            }
        };
    }

    @Override
    protected Action toTasksAction() {
        return new BaseAction("user-notification") {
            @Override
            public void actionPerform(Component component) {
                screenBuilders.screen(mainMenuFrameOwner)
                        .withScreenId("my-tasks")
                        .build()
                        .show();
            }
        };
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

    @Override
    protected long getNotificationCount() {
        return loadActivities("NOTIFICATION").size();
    }
}