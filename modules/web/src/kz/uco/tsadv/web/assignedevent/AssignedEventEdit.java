package kz.uco.tsadv.web.assignedevent;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.GlobalConfig;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.entity.tb.AssignedEvent;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.uactivity.service.ActivityService;

import javax.inject.Inject;

public class AssignedEventEdit extends AbstractEditor<AssignedEvent> {

    @Inject
    private UserSession userSession;
    @Inject
    private ActivityService activityService;
    @Inject
    private EmployeeService employeeService;
    @Inject
    private CommonService commonService;
    @Inject
    private GlobalConfig globalConfig;
    @Inject
    private DataManager dataManager;

    boolean isNew;

    @Override
    protected void initNewItem(AssignedEvent item) {
        isNew = true;
        PersonGroupExt personGroup = userSession.getAttribute(StaticVariable.USER_PERSON_GROUP);
        if (personGroup != null) {
            item.setAssigned(personGroup);
        }
    }

    //    @Inject
//    private UserSession userSession;
//
//    @Override
//    protected void initNewItem(AssignedEvent item){
//        super.initNewItem(item);
//
//        PersonGroup personGroupId userSession.getAttribute(StaticVariable.USER_PERSON_GROUP);
//        if (personGroupId != null){
//            item.setAssigned(personGroupId);
//        }
//    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (committed) {
           /*
            TODO: Murat Issenzhulov
            if (isNew) {
                UserExt assignedBy = employeeService.getUserExtByPersonGroupId(getItem().getAssigned() == null ? null : getItem().getAssigned().getId());
                activityService.createActivity(
                        getItem().getSafetyEvent().getName(),
                        employeeService.getUserExtByPersonGroupId(getItem().getAssignment() == null ? null : getItem().getAssignment().getId()),
                        assignedBy != null ? assignedBy : employeeService.getSystemUser(),
                        commonService.getEntity(ActivityType.class, "tsadv$AssignedEvent"),
                        StatusEnum.active,
                        null,
                        getItem().getDeadline(),
                        getItem().getDeadline(),
                        commonService.getEntity(Priority.class, "MEDIUM"),
                        globalConfig.getWebAppUrl() + "/open?screen=tsadv$AssignedEvent.edit&item=tsadv$AssignedEvent-" + getItem().getId(),
                        getItem().getId(),
                        null
                );
                AppBeans.get(Events.class).publish(new NotificationRefreshEvent(""));
            } else {
                Map<String, Object> map = new HashMap<>();
                map.put("assignedEventId", getItem().getId());
                Activity activity = commonService.getEntity(Activity.class,
                        "select e from uactivity$Activity e where e.referenceId = :assignedEventId",
                        map,
                        "activity-view");
                activity.setName(getItem().getSafetyEvent().getName());
                activity.setAssignedUser(employeeService.getUserExtByPersonGroupId(getItem().getAssignment() == null ? null : getItem().getAssignment().getId()));
                activity.setAssignedBy(employeeService.getUserExtByPersonGroupId(getItem().getAssigned() == null ? null : getItem().getAssigned().getId()));
                activity.setStartDateTime(getItem().getDeadline());
                activity.setEndDateTime(getItem().getDeadline());
                dataManager.commit(activity);
            }*/
        }
        return super.postCommit(committed, close);
    }
}