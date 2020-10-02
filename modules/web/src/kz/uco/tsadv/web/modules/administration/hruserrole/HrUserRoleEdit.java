package kz.uco.tsadv.web.modules.administration.hruserrole;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.ValidationErrors;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.model.HrUserRole;

import javax.inject.Inject;
import java.util.Objects;

public class HrUserRoleEdit extends AbstractEditor<HrUserRole> {
    @Inject
    protected CommonService commonService;

    @Override
    protected void initNewItem(HrUserRole item) {
        super.initNewItem(item);
        item.setDateFrom(CommonUtils.getSystemDate());
        item.setDateTo(CommonUtils.getEndOfTime());
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        HrUserRole hrUserRole = getItem();
        if (Objects.isNull(hrUserRole.getUser())
                || Objects.isNull(hrUserRole.getRole())
                || Objects.isNull(hrUserRole.getDateFrom())
                || Objects.isNull(hrUserRole.getDateTo())) {
            return;
        }
        if (isHaveSameRole(hrUserRole)) {
            errors.add(getMessage("already.have.user.role"));
        }
    }

    protected boolean isHaveSameRole(HrUserRole hrUserRole) {
        return !commonService.getEntities(HrUserRole.class,
                "select e from tsadv$HrUserRole e " +
                        " where e.user.id = :userId " +
                        "   and e.role.id = :roleId " +
                        "   and e.dateFrom <= :dateFrom " +
                        "   and e.dateTo >= :dateTo " +
                        "   and e.id <> :id",
                ParamsMap.of("userId", hrUserRole.getUser().getId(),
                        "roleId", hrUserRole.getRole().getId(),
                        "dateFrom", hrUserRole.getDateFrom(),
                        "dateTo", hrUserRole.getDateTo(),
                        "id", hrUserRole.getId()),
                View.MINIMAL).isEmpty();
    }
}