package kz.uco.tsadv.service;


import kz.uco.tsadv.modules.timesheet.model.AssignmentSchedule;

import java.util.List;

public interface AssignmentScheduleDividerService {
    String NAME = "tsadv_AssignmentScheduleDividerService";

    List<AssignmentSchedule> loadOldAssignmentSchedules(AssignmentSchedule assignmentSchedule);

    AssignmentSchedule getAheadAssignmentSchedule(AssignmentSchedule assignmentSchedule);

    AssignmentSchedule getBehindAssignmentSchedule(AssignmentSchedule assignmentSchedule);

    void changeAssignmentSchedule(AssignmentSchedule assignmentScheduleToChange, AssignmentSchedule assignmentScheduleNotToChange);
}