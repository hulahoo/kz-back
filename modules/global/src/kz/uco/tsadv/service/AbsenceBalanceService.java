package kz.uco.tsadv.service;


import kz.uco.tsadv.entity.dbview.AbsenceBalanceV;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.Absence;
import kz.uco.tsadv.modules.personal.model.AbsenceBalance;
import kz.uco.tsadv.modules.personal.model.VacationConditions;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface AbsenceBalanceService {
    String NAME = "tsadv_AbsenceBalanceService";

    List<AbsenceBalanceV> getAbsenceBalance(UUID personGroupId);

    List<AbsenceBalanceV> getAbsenceBalance(UUID personGroupId, Date date);

    int getBalanceDays(PersonGroupExt personGroup, PositionGroupExt positionGroup);

    int getAdditionalBalanceDays(PersonGroupExt personGroupExt);

    int getAdditionalBalanceDays(PositionGroupExt positionGroup);

    List<VacationConditions> getAllVacationConditionsList(@Nullable UUID positionGroupId);

    int getAnnualDaysSpent(PersonGroupExt personGroup, Absence absence);

//    int getAdditionalDaysSpent(PersonGroupExt personGroup);

//    int getAdditionalDaysSpentForAbsence(Absence absence);

    AbsenceBalance createNewAbsenceBalance(Absence absence, PersonGroupExt personGroup, PositionGroupExt positionGroup, Date lastEndDate);

    AbsenceBalance createNextAbsenceBalances(AbsenceBalance lastAbsenceBalance, Absence absence, int excessDaysFromPrevious);

    AbsenceBalance createNextAbsenceBalancesWithoutPersistence(AbsenceBalance lastAbsenceBalance, Absence absence, int excessDaysFromPrevious);

    Date getActualDateTo(Date dateFrom);

    Date getActualDateFrom(AbsenceBalance lastAbsenceBalance);

    AbsenceBalance createNewAbsenceBalance(PersonGroupExt personGroup, PositionGroupExt positionGroup);

    int getDaysDifference(PersonGroupExt personGroup, Absence originalAbsence, Absence changeAbsence);

    int getCurrentAbsenceDays(PersonGroupExt personGroup);

    void recountBalanceDays(Absence absence, List<AbsenceBalance> absenceBalanceList, PositionGroupExt positionGroup);

    void recountBalanceDaysOnDelete(Absence absence, PositionGroupExt positionGroup);

    void recountBalanceDaysOnChangingLongAbsence(Absence absence, List<AbsenceBalance> absenceBalanceList, PositionGroupExt positionGroup);

    List<AbsenceBalance> getAbsenceBalanceList(Absence absence, PositionGroupExt positionGroup);

    void sendNotificationBeforeEndLongAbsence();

    boolean absenceIntersectsWithPeriod(Absence absence, AbsenceBalance absenceBalance);

    List<AbsenceBalance> getExistingAbsenceBalances(Absence absence);

    void recountBalanceDays(PersonGroupExt personGroupExt, Absence absence, Absence excludedAbsence, AbsenceBalance absenceBalance, Integer previousAbsenceDays);

    void createMissingAbsenceBalances();

    double getAbsenceBalance(UUID personGroupId, Date absenceDate, UUID dicAbsenceTypeId);
}