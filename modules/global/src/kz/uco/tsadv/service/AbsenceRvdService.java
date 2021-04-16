package kz.uco.tsadv.service;

import kz.uco.tsadv.modules.personal.dictionary.DicAbsenceType;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;

import java.util.Date;
import java.util.UUID;

public interface AbsenceRvdService {
    String NAME = "tsadv_AbsenceRvdService";

    int countTotalHours(Date dateFrom, Date dateTo, DicAbsenceType absenceType, AssignmentGroupExt assignmentGroup);

}
