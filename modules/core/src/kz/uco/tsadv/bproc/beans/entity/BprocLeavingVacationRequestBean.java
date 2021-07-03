package kz.uco.tsadv.bproc.beans.entity;

import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.modules.personal.model.LeavingVacationRequest;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Map;

@Component(BprocLeavingVacationRequestBean.NAME)
public class BprocLeavingVacationRequestBean extends AbstractBprocEntityBean<LeavingVacationRequest> {
    public static final String NAME = "tsadv_BprocLeavingVacationRequestBean";

    @Override
    public Map<String, Object> getNotificationParams(String templateCode, LeavingVacationRequest entity) {
        Map<String, Object> params = super.getNotificationParams(templateCode, entity);

        /*case "application.for.absence.requires.approval":
        case "absence.request.rejected":
        case "absence.application.approved":
        case "end.of.absence":*/

        LeavingVacationRequest leavingVacationRequest = transactionalDataManager.load(LeavingVacationRequest.class)
                .id(entity.getId()).view("leavingVacationRequest-editView").optional().orElse(null);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        if (leavingVacationRequest != null) {
            params.put("status", leavingVacationRequest.getStatus() != null
                    ? leavingVacationRequest.getStatus().getLangValue1()
                    : null);
            params.put("dateFrom", leavingVacationRequest.getStartDate() != null
                    ? dateFormat.format(leavingVacationRequest.getStartDate())
                    : null);
            params.put("dateTo", leavingVacationRequest.getEndDate() != null
                    ? dateFormat.format(leavingVacationRequest.getEndDate())
                    : null);
        }

        return params;
    }

    @Override
    public boolean instanceOf(Class<? extends AbstractBprocRequest> tClass) {
        return LeavingVacationRequest.class.isAssignableFrom(tClass);
    }
}