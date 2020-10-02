package kz.uco.tsadv.service;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.timesheet.model.AssignmentSchedule;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

@Service(AssignmentScheduleDividerService.NAME)
public class AssignmentScheduleDividerServiceBean implements AssignmentScheduleDividerService {

    @Inject
    private DataManager dataManager;
    @Inject
    private Metadata metadata;
    @Inject
    private CommonService commonService;
    @Inject
    private Persistence persistence;

    @Override
    public List<AssignmentSchedule> loadOldAssignmentSchedules(AssignmentSchedule assignmentSchedule) {
        return commonService.emQueryResultList(AssignmentSchedule.class,
                "select o " +
                        "    from tsadv$AssignmentSchedule o " +
                        "   where o.assignmentGroup.id = :assignmentGroupId " +
                        "     and ((o.startDate <= :endDate and o.startDate >= :startDate) " +
                        "     or (o.endDate <= :endDate and o.endDate >= :startDate) " +
                        "     or (o.startDate >= :startDate and o.endDate <= :endDate) " +
                        "     or (o.startDate <= :endDate and o.endDate >= :endDate)) ",
                ParamsMap.of("assignmentGroupId", assignmentSchedule.getAssignmentGroup().getId(),
                        "startDate", assignmentSchedule.getStartDate(),
                        "endDate", assignmentSchedule.getEndDate()),
                "assignmentSchedule.view");
    }


    @Override
    public AssignmentSchedule getAheadAssignmentSchedule(AssignmentSchedule assignmentSchedule) {
        return commonService.emQueryFirstResult(AssignmentSchedule.class,
                "select o from tsadv$AssignmentSchedule o " +
                        "   where o.assignmentGroup.id = :assignmentGroupId " +
                        "     and o.endDate = :endDate",
                ParamsMap.of("assignmentGroupId", assignmentSchedule.getAssignmentGroup().getId(),
                        "endDate", DateUtils.addDays(assignmentSchedule.getStartDate(), -1)),
                "assignmentSchedule.view");
    }

    @Override
    public AssignmentSchedule getBehindAssignmentSchedule(AssignmentSchedule assignmentSchedule) {
        return commonService.emQueryFirstResult(AssignmentSchedule.class,
                "select o from tsadv$AssignmentSchedule o " +
                        "   where o.assignmentGroup.id = :assignmentGroupId " +
                        "     and o.startDate = :startDate",
                ParamsMap.of("assignmentGroupId", assignmentSchedule.getAssignmentGroup().getId(),
                        "startDate", DateUtils.addDays(assignmentSchedule.getEndDate(), 1)),
                "assignmentSchedule.view");
    }


    @Override
    public void changeAssignmentSchedule(AssignmentSchedule assignmentScheduleToChange, AssignmentSchedule assignmentScheduleNotToBeTouched) {

        boolean isOldStartDateEntersInNewSchedule =
                !assignmentScheduleToChange.getStartDate().before(assignmentScheduleNotToBeTouched.getStartDate()) &&
                        !assignmentScheduleToChange.getStartDate().after(assignmentScheduleNotToBeTouched.getEndDate());
        boolean isOldEndDateEntersInNewSchedule =
                !assignmentScheduleToChange.getEndDate().before(assignmentScheduleNotToBeTouched.getStartDate()) &&
                        !assignmentScheduleToChange.getEndDate().after(assignmentScheduleNotToBeTouched.getEndDate());

        boolean isAllOldScheduleEntersInNewSchedule = !isOldStartDateEntersInNewSchedule && !isOldEndDateEntersInNewSchedule;
        if (isAllOldScheduleEntersInNewSchedule) {
            mergeAssignmentSchedule(assignmentScheduleToChange, assignmentScheduleNotToBeTouched);
        } else {
            if (isOldStartDateEntersInNewSchedule && isOldEndDateEntersInNewSchedule) {
                assignmentScheduleToChange.setName("avoid_delete_listener");
                try (Transaction transaction = persistence.getTransaction()) {
                    persistence.getEntityManager().remove(assignmentScheduleToChange);
                    transaction.commit();
                }
                return;
            }

            if (isOldStartDateEntersInNewSchedule) {
                assignmentScheduleToChange = changeStartDate(assignmentScheduleToChange, assignmentScheduleNotToBeTouched);
            }

            if (isOldEndDateEntersInNewSchedule) {
                assignmentScheduleToChange = changeEndDate(assignmentScheduleToChange, assignmentScheduleNotToBeTouched);
            }

            saveEntity(assignmentScheduleToChange);
        }

    }

    private AssignmentSchedule changeStartDate(AssignmentSchedule assignmentScheduleToChange, AssignmentSchedule assignmentScheduleNotToBeTouched) {
        Date newStartDate = DateUtils.addDays(assignmentScheduleNotToBeTouched.getEndDate(), 1);
        assignmentScheduleToChange.setStartDate(newStartDate);
        return assignmentScheduleToChange;
    }

    private AssignmentSchedule changeEndDate(AssignmentSchedule assignmentScheduleToChange, AssignmentSchedule assignmentScheduleNotToBeTouched) {
        Date newEndDate = DateUtils.addDays(assignmentScheduleNotToBeTouched.getStartDate(), -1);
        assignmentScheduleToChange.setEndDate(newEndDate);
        return assignmentScheduleToChange;
    }

    private void mergeAssignmentSchedule(AssignmentSchedule assignmentScheduleToChange, AssignmentSchedule assignmentScheduleNotToBeTouched) {
        Date oldEndDate = assignmentScheduleToChange.getEndDate();

        Date newFirstEndDate = DateUtils.addDays(assignmentScheduleNotToBeTouched.getStartDate(), -1);
        assignmentScheduleToChange.setEndDate(newFirstEndDate);

        saveEntity(assignmentScheduleToChange);

        Date newSecondStartDate = DateUtils.addDays(assignmentScheduleNotToBeTouched.getEndDate(), 1);

        AssignmentSchedule secondAssignmentScheduleToChange = metadata.create(AssignmentSchedule.class);
        secondAssignmentScheduleToChange.setStartDate(newSecondStartDate);
        secondAssignmentScheduleToChange.setEndDate(oldEndDate);
        secondAssignmentScheduleToChange.setSchedule(assignmentScheduleToChange.getSchedule());
        secondAssignmentScheduleToChange.setAssignmentGroup(assignmentScheduleToChange.getAssignmentGroup());
        secondAssignmentScheduleToChange.setOffset(assignmentScheduleToChange.getOffset());
        secondAssignmentScheduleToChange.setColorsSet(assignmentScheduleToChange.getColorsSet());

        saveEntity(secondAssignmentScheduleToChange);
    }

    private void saveEntity(AssignmentSchedule entity) {
        try (Transaction transaction = persistence.getTransaction()) {
            persistence.getEntityManager().merge(entity);
            transaction.commit();
        }
    }

}