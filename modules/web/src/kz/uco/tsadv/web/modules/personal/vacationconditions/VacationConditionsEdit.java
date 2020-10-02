package kz.uco.tsadv.web.modules.personal.vacationconditions;

import com.haulmont.cuba.gui.components.ValidationErrors;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.gui.components.AbstractHrEditor;
import kz.uco.tsadv.modules.personal.model.VacationConditions;
import kz.uco.tsadv.service.VacationConditionsService;

import javax.inject.Inject;

public class VacationConditionsEdit extends AbstractHrEditor<VacationConditions> {

    @Inject
    protected VacationConditionsService vacationConditionsService;

    @Override
    protected void initNewItem(VacationConditions item) {
        super.initNewItem(item);
        if (item.getAdditionalDays() == null) item.setAdditionalDays(0);
        if (item.getStartDate() == null) item.setStartDate(CommonUtils.getSystemDate());
        if (item.getEndDate() == null) item.setEndDate(CommonUtils.getEndOfTime());
//        if (item.getVacationDurationType() == null) item.setVacationDurationType(VacationDurationType.CALENDAR);
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        if (errors.isEmpty()) {
            if (vacationConditionsService.isIntersectVacationConditions(getItem())) {
                errors.add(getMessage("intersect.vacationCondition"));
            }
        }
    }
}