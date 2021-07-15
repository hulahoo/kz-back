package kz.uco.tsadv.service;

import java.util.Date;
import java.util.UUID;


public interface AbsenceRvdService {
    String NAME = "tsadv_AbsenceRvdService";

    int countTotalHours(Date dateFrom, Date dateTo, UUID absenceTypeId, UUID personGroupId);

}