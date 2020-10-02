package kz.uco.tsadv.service;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.View;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.personal.model.VacationConditions;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;


@Service(VacationConditionsService.NAME)
public class VacationConditionsServiceBean implements VacationConditionsService {

    @Inject
    protected CommonService commonService;

    @Override
    public boolean isIntersectVacationConditions(@Nonnull VacationConditions vacationConditions) {

        if (vacationConditions.getJobGroup() == null && vacationConditions.getPositionGroup() == null) return true;

        String property;
        UUID propertyId;

        if (vacationConditions.getPositionGroup() != null) {
            property = "positionGroup.id";
            propertyId = vacationConditions.getPositionGroup().getId();
        } else {
            property = "jobGroup.id";
            propertyId = vacationConditions.getJobGroup().getId();
        }

        String queryString = "select e from tsadv$VacationConditions e " +
                " where e.id <> :id " +
                "   and e." + property + " = :value " +
                "   and ( e.startDate > :endDate or e.endDate < :startDate ) = false ";

        Map<String, Object> params = ParamsMap.of("id", vacationConditions.getId(),
                "value", propertyId,
                "startDate", vacationConditions.getStartDate(),
                "endDate", vacationConditions.getEndDate());

        return !commonService.getEntities(VacationConditions.class, queryString, params, View.MINIMAL)
                .isEmpty();
    }
}
