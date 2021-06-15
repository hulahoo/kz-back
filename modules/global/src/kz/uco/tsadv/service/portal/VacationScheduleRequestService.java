package kz.uco.tsadv.service.portal;

import kz.uco.tsadv.entity.VacationScheduleRequest;
import kz.uco.tsadv.pojo.GanttChartVacationScheduleData;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface VacationScheduleRequestService {
    String NAME = "tsadv_VacationScheduleRequestService";

    List<VacationScheduleRequest> getChildVacationSchedule();

    List<GanttChartVacationScheduleData> ganttChart(Date startDate, Date endDate);

    List<GanttChartVacationScheduleData> ganttChart(UUID organizationGroupId, Date startDate, Date endDate);

}