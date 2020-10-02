package kz.uco.tsadv.service;

import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AssignmentSalaryService {

    String NAME = "tsadv_AssignmentSalaryService";

    double calculatePercentage(Double oldValue, Double newValue);

    boolean isLastAssignment(UUID personGroupId, Date endDate);

    boolean isLastSalary(UUID assignmentGroupId, Date dateFrom);

    boolean isHaveRequest(List<String> types, Map<Integer, Object> params);

    PersonGroupExt getPersonGroupByRequestId(UUID id, String view, Class entityClass);
}
