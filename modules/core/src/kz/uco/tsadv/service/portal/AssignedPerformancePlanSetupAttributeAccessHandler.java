package kz.uco.tsadv.service.portal;

import com.haulmont.cuba.core.app.SetupAttributeAccessHandler;
import com.haulmont.cuba.core.app.events.SetupAttributeAccessEvent;
import com.haulmont.cuba.core.global.Security;
import com.haulmont.cuba.core.global.UserSessionSource;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.modules.performance.enums.CardStatusEnum;
import kz.uco.tsadv.modules.performance.model.AssignedPerformancePlan;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.UUID;

@Component(AssignedPerformancePlanSetupAttributeAccessHandler.NAME)
public class AssignedPerformancePlanSetupAttributeAccessHandler implements SetupAttributeAccessHandler<AssignedPerformancePlan> {
    public static final String NAME = "tsadv_AssignedPerformancePlanSetupAttributeAccessHandler";

    @Inject
    private UserSessionSource userSessionSource;

    @Inject
    private Security security;

    @Override
    public boolean supports(Class clazz) {
        return AssignedPerformancePlan.class.isAssignableFrom(clazz);
    }

    @Override
    public void setupAccess(SetupAttributeAccessEvent<AssignedPerformancePlan> event) {
        UUID userPersonGroupId = userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP_ID);
        boolean isUserAssignedPersonPlan = event.getEntity().getAssignedPerson().getId().equals(userPersonGroupId);
        if (!isUserAssignedPersonPlan || !event.getEntity().getStatus().equals(CardStatusEnum.DRAFT)) {
            event.addHidden("performancePlan");
        }
    }
}