package kz.uco.tsadv.web.activity;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.events.NotificationRefreshEvent;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.components.GroupsComponent;
import kz.uco.tsadv.entity.dbview.ActivityTask;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.uactivity.entity.Activity;
import kz.uco.uactivity.entity.StatusEnum;
import org.springframework.context.event.EventListener;

import javax.inject.Inject;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * @author daniil.ivantsov
 */

public class ActivityBrowseNew extends AbstractLookup {

    protected List<Activity> notifications = new ArrayList<>();
    protected CloseListener closeListener;
    protected AbstractEditor openedEditor;


    @WindowParam(name = "openTab")
    protected String defaultTabFromLink;

    @Inject
    protected UserSession userSession;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    private CommonService commonService;
    @Inject
    private Metadata metadata;
    @Inject
    private UserSessionSource userSessionSource;
    @Inject
    protected TabSheet tabSheet;
    @Inject
    protected Table<Activity> notificationsTable;
    @Inject
    protected Button markAsReadButton;
    @Inject
    protected Button markAsUnreadButton;
    @Inject
    protected GroupDatasource<Activity, UUID> notificationsDs;
    @Inject
    private GroupDatasource<ActivityTask, UUID> activityTasksDs;
    @Inject
    protected GroupsComponent groupsComponent;

    @Override
    public void ready() {
        super.ready();

        changeTab();
        setupChangeScreenCaption();
    }

    protected void setupChangeScreenCaption() {
        tabSheet.addSelectedTabChangeListener(event -> changeScreenCaption());
    }

    protected void changeTab() {
        if (isNotEmpty(defaultTabFromLink)) {
            tabSheet.setSelectedTab(defaultTabFromLink);
            changeScreenCaption();
        }
    }

    protected void changeScreenCaption() {
        setCaption(getMessage("browse.caption") + ": " +
                getMessage(tabSheet.getSelectedTab().getName()) + "                              ");
    }

    /**
     * Генерирует колонку с галочками для таблицы на вкладке "Уведомления"
     *
     * @param entity - объект {@link Activity} (Уведомление), полями которого заполняется строка таблицы
     * @return объект CheckBox
     */
    public Component generateCheckBoxColumn(Activity entity) {
        CheckBox checkBox = componentsFactory.createComponent(CheckBox.class);
        if (notifications.contains(entity)) {
            checkBox.setValue(true);
        }
        checkBox.addValueChangeListener(e -> {
            if (e.getValue() != null && e.getValue() && !notifications.contains(entity)) {
                notifications.add(entity);
                notifications.forEach(notification -> {
                    if (StatusEnum.active.equals(notification.getStatus())) {
                        markAsReadButton.setEnabled(true);
                    } else {
                        markAsUnreadButton.setEnabled(true);
                    }
                });


            }
            if (e.getValue() != null && !(Boolean) e.getValue() && notifications.contains(entity)) {
                notifications.remove(entity);
                markAsReadButton.setEnabled(false);
                markAsUnreadButton.setEnabled(false);
                notifications.forEach(notification -> {
                    if (StatusEnum.active.equals(notification.getStatus())) {
                        markAsReadButton.setEnabled(true);
                    } else {
                        markAsUnreadButton.setEnabled(true);
                    }
                });
            }
        });
        return checkBox;
    }

    public Component generateReadStatusColumn(Activity entity) {
        Label label = componentsFactory.createComponent(Label.class);
        if (StatusEnum.active.equals(entity.getStatus())) {
            label.setValue(getMessage("unread"));
        } else {
            label.setValue(getMessage("read"));
        }
        return label;
    }

    public Component generateTaskStatusColumn(ActivityTask entity) {
        Label label = componentsFactory.createComponent(Label.class);
        if (entity.getStatus().equals("20")) {
            label.setValue(getMessage("closed"));
        }
        if (entity.getStatus().equals("10")) {
            label.setValue(getMessage("inProgress"));
        }
        return label;
    }

    public Component generateProcessNameColumn(ActivityTask entity) {
        Label label = componentsFactory.createComponent(Label.class);
        String language = userSessionSource.getLocale().getLanguage();
        if (language.equals("ru")) {
            label.setValue(entity.getProcessRu());
        }
        if (language.equals("kz")) {
            label.setValue(entity.getProcessRu());
        }
        if (language.equals("en")) {
            label.setValue(entity.getProcessEn());
        }
        return label;
    }

    /**
     * На вкладке "Уведомления" помечает все отмеченные уведомления как прочитанные
     */
    public void markAsRead() {
        if (!notifications.isEmpty()) {
            notifications.forEach(notification -> {
                if (notificationsDs.containsItem(notification.getId())) {
                    notificationsDs.getItemNN(notification.getId()).setStatus(StatusEnum.done);
                    notificationsDs.getItemNN(notification.getId()).setEndDateTime(CommonUtils.getSystemDate());
                }
            });
            notificationsDs.commit();
            notifications.clear();
            notificationsTable.repaint();
            markAsReadButton.setEnabled(false);
            markAsUnreadButton.setEnabled(false);
        }
    }

    /**
     * На вкладке "Уведомления" помечает все отмеченные уведомления как непрочитанные.
     */
    public void markAsUnread() {
        if (!notifications.isEmpty()) {
            notifications.forEach(notification -> {
                if (notificationsDs.containsItem(notification.getId())) {
                    notificationsDs.getItemNN(notification.getId()).setStatus(StatusEnum.active);
                    notificationsDs.getItemNN(notification.getId()).setEndDateTime(null);
                }
            });
            notificationsDs.commit();
            notifications.clear();
            notificationsTable.repaint();
            markAsReadButton.setEnabled(false);
            markAsUnreadButton.setEnabled(false);
        }
    }

    public void redirectToTask(ActivityTask activityTask, String name) {
        if (activityTask.getActivity() != null) {
            openActivityTask(activityTask);
        }
        /*
         Activity activity = activityTask.getActivity();
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
            Entity entity = commonService.getEntity(metadata.getSession().getClassNN(activity.getType().getWindowProperty().getEntityName()).getJavaClass(),
                    "select e from " + activity.getType().getWindowProperty().getEntityName() + " e " +
                            "where e.id = :entityId", queryParams, activity.getType().getWindowProperty().getViewName());
        if (activity.getType().getCode().equals("KPI_APPROVE")) {
            AbstractWindow abstractWindow = openWindow(activity.getType().getWindowProperty().getScreenName(),
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
                insertNotificationMessage(abstractEditor, activity);
            }
        }
    }
         */
    }

    protected AbstractWindow openActivityTask(ActivityTask activityTask) {
        AbstractWindow abstractWindow = null;
        Activity activity = activityTask.getActivity();
        if (activity != null && activity.getReferenceId() != null) {
            Entity entity = commonService.getEntity(metadata.getClass(activity.getType().getWindowProperty().getEntityName()).getJavaClass(),
                    activity.getReferenceId(), activity.getType().getWindowProperty().getViewName());

            Map<String, Object> windowParam = new HashMap<>(getWindowParam(activity, entity));
            windowParam.put("activityId", activityTask);
            if (isOpenWindow(activity.getType().getCode().toUpperCase())) {
                abstractWindow = openWindow(activity.getType().getWindowProperty().getScreenName(),
                        WindowManager.OpenType.NEW_TAB,
                        windowParam);
            } else {
                abstractWindow = openEditor(activity.getType().getWindowProperty().getScreenName(),
                        entity, WindowManager.OpenType.NEW_TAB, windowParam);
            }

//            if (activity.getNotificationHeader() != null || activity.getNotificationBody() != null) {
//                groupsComponent.addActivityBodyToWindow(abstractWindow, activity);
//            }

            abstractWindow.addCloseListener(closeId -> activityTasksDs.refresh());
        }
        return abstractWindow;
    }

    protected boolean isOpenWindow(String code) {
        return code.matches("KPI_APPROVE|PERSON_COMPETENCE_ASSESSMENT_APPROVE|PERSON_COMPETENCE_ASSESSMENT_LEARN");
    }

    protected Map<String, Object> getWindowParam(Activity activity, Entity entity) {
        switch (activity.getType().getCode().toUpperCase()) {
            case "KPI_APPROVE":
                return ParamsMap.of("fromActivity", new Object(),
                        "performancePlanHeader", entity, "activity", activity);
            case "PERSON_COMPETENCE_ASSESSMENT_APPROVE":
                return ParamsMap.of("personCompetenceAssessmentId", activity.getReferenceId(),
                        "activity", activity);
            case "PERSON_COMPETENCE_ASSESSMENT_LEARN":
                return ParamsMap.of("personCompetenceAssessment", entity,
                        "activity", activity);
            case "INTERVIEW_APPROVE":
                return ParamsMap.of("fromActivity", null, "readOnly", true);
            case "REQUISITION_APPROVE":
                return ParamsMap.of("fromActivity", null, "isRecruiter", true);
            default:
                return ParamsMap.empty();
        }
    }

    public void onOpenAllUnApprovedRequests() {
        ActivityTask activeTask = getFirstActivityTask(activityTasksDs.getItems());
        if (activeTask != null) {
            initCloseListener();
            openedEditor = (AbstractEditor) openActivityTask(activeTask);
            openedEditor.addCloseListener(closeListener);
        }
    }

    private void initCloseListener() {
        closeListener = getCloseListener();
    }

    protected CloseListener getCloseListener() {
        return actionId -> {
            activityTasksDs.refresh();
            ActivityTask activeTask = getFirstActivityTask(activityTasksDs.getItems());
            // можно реализовать через actionId
            if (activeTask != null
                    && activeTask.getActivity() != null
                    && !openedEditor.getItem().getId().equals(activeTask.getActivity().getReferenceId())) {
                openedEditor = (AbstractEditor) openActivityTask(activeTask);
                openedEditor.addCloseListener(closeListener);
            }
        };
    }

    protected ActivityTask getFirstActivityTask(Collection<ActivityTask> activityTasks) {
        for (ActivityTask activityTask : activityTasks) {
            if (activityTask.getActivity() != null && StatusEnum.active.equals(activityTask.getActivity().getStatus())) {
                return activityTask;
            }
        }
        return null;
    }

    @EventListener
    private void handleNotificationChange(NotificationRefreshEvent event) {
        if (isSessionUser((UUID) event.getSource())) {
            activityTasksDs.refresh();
            notificationsDs.refresh();
        }
    }

    protected boolean isSessionUser(UUID userId) {
        return userSession.getUser().getId().equals(userId);
    }
}
