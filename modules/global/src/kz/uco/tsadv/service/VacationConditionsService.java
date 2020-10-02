package kz.uco.tsadv.service;

import kz.uco.tsadv.modules.personal.model.VacationConditions;

import javax.annotation.Nonnull;

public interface VacationConditionsService {

    String NAME = "tsadv_VacationConditionsService";

    boolean isIntersectVacationConditions(@Nonnull VacationConditions vacationConditions);

}
