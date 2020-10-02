package kz.uco.tsadv.service;


import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.timesheet.dictionary.DicScheduleElementType;
import kz.uco.tsadv.modules.timesheet.model.Timecard;
import kz.uco.tsadv.modules.timesheet.model.TimecardHeader;
import kz.uco.tsadv.modules.timesheet.model.WorkedHoursDetailed;

import java.util.*;

public interface TimecardService {
    String NAME = "tsadv_TimecardService";

    void formTimecard(Date monthStart, Collection<Timecard> timecards) throws Exception;

    void checkAndCreateSpecialHours(WorkedHoursDetailed workedHoursDetailed);

    void calculateStatistic(TimecardHeader timecardHeader);

    void insertDeviations(Set<Timecard> selectedTimecards, double hours, Date dateFrom, Date dateTo, boolean fact, boolean plan, boolean changeHoursFromBack, Boolean changesWeekends);

    List<Timecard> getTimecards(OrganizationGroupExt organizationGroup,
                                PositionGroupExt positionGroup,
                                PersonGroupExt personGroup,
                                Date startDate,
                                Date endDate,
                                int firstResult,
                                int maxResults,
                                boolean loadFullData,
                                AssignmentExt assignmentExt,
                                Boolean enableInclusions);

    List<Timecard> getTimecards(OrganizationGroupExt organizationGroup,
                                PersonGroupExt personGroup,
                                Date startDate,
                                Date endDate,
                                int firstResult,
                                int maxResults,
                                boolean loadFullData,
                                AssignmentExt assignmentExt,
                                Boolean enableInclusions);

    int getTimecardsCount(OrganizationGroupExt organizationGroup, PositionGroupExt positionGroup, PersonGroupExt personGroup, Date startDate, Date endDate, Boolean enableInclusions);

    void copyTimecard(Timecard timecard, List<PersonGroupExt> selectedPersonGroups, Date monthFromCopy, Date monthForCopy, Boolean copyDeviationsToo) throws Exception;

    boolean isWeekendOrHoliday(DicScheduleElementType scheduleElementType);

    void setCorrective(Boolean corrective, Collection<Timecard> timecards, Date startDate, Date endDate);

    String getTimecardRepresentation(Double hours);
}