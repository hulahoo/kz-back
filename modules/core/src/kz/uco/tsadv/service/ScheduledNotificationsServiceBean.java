package kz.uco.tsadv.service;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.uactivity.entity.ActivityType;
import kz.uco.uactivity.entity.StatusEnum;
import kz.uco.uactivity.service.ActivityService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

@Service(ScheduledNotificationsService.NAME)
public class ScheduledNotificationsServiceBean implements ScheduledNotificationsService {

    @Inject
    protected ActivityService activityService;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected CommonService commonService;

    String REMIND_VACATION_LEAVE_CODE = "reminder.schedule.the.leave";

    @Override
    public void automaticReminderApplyingVacationSchedule() {

        List<TsadvUser> userExtList = dataManager.load(TsadvUser.class)
                .query("select e from base$UserExt e join e.userRoles r where  r.role.name = :roleId")
                .parameter("roleId", "EMPLOYEE_SELF_SERVICE")
                .view("userExt.edit")
                .list();


        userExtList.forEach(userExt -> {
            activityService.createActivity(
                    userExt,
                    employeeService.getSystemUser(),
                    commonService.getEntity(ActivityType.class, "NOTIFICATION"),
                    StatusEnum.active,
                    "description",
                    null,
                    new Date(),
                    null,
                    null,
                    null,
                    REMIND_VACATION_LEAVE_CODE,
                    ParamsMap.of("personFullName", employeeService.getPersonGroupByUserId(userExt.getId()).getFullName()));
        });


    }


}