package kz.uco.tsadv.service;


import kz.uco.tsadv.modules.personal.dictionary.DicAbsenceType;
import kz.uco.tsadv.modules.personal.enums.VacationDurationType;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.Absence;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface AbsenceService {
    String NAME = "tsadv_AbsenceService";

    boolean isLongAbsence(Absence absence);

    List<Absence> getAllAbsencesForPerson(PersonGroupExt personGroup);

    void fillNextTaskIdRestForAA();

    int countBusinessDays(Date dateFrom, Date dateTo, DicAbsenceType absenceType, AssignmentGroupExt assignmentGroup);

    int countDays(Date dateFrom, Date dateTo, UUID absenceTypeId, UUID personGroupId);

    int countDaysWithoutHolidays(Date dateFrom, Date dateTo, UUID personGroupId);

    int countWeekendDays(Date dateFrom, Date dateTo);

    int countAbsenceDays(Date dateFrom, Date dateTo, DicAbsenceType absenceType, AssignmentGroupExt assignmentGroup);

    String intersectionExists(String personGroupId, Date startDate, Date endDate,
                              String language, @Nullable UUID absenceId, String checkingType);

    boolean bpmRequiredForAbsence(UUID entityId);

    boolean isAbsenceTypeLong(UUID entityId);

    List<DicAbsenceType> getAbsenceTypeLong(@Nullable String viewName);

    void createAbsenceFromRequest(String entityId);

    VacationDurationType getVacationDurationType(UUID personGroupId, UUID absenceTypeId, Date date);
}