package kz.uco.tsadv.bproc.beans.entity;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.View;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.modules.personal.model.ChangeAbsenceDaysRequest;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Map;

@Component(BprocChangeAbsenceDaysRequestBean.NAME)
public class BprocChangeAbsenceDaysRequestBean extends AbstractBprocEntityBean<ChangeAbsenceDaysRequest> {
    public static final String NAME = "tsadv_BprocChangeAbsenceDaysRequestBean";

    @Override
    public Map<String, Object> getNotificationParams(String templateCode, ChangeAbsenceDaysRequest entity) {
        Map<String, Object> params = super.getNotificationParams(templateCode, entity);

        /*case "changeAbsenceDaysRequest.start":
        case "changeAbsenceDaysRequest.approved":
        case "changeAbsenceDaysRequest.reject":
        case "changeAbsenceDaysRequest.revision":*/

        ChangeAbsenceDaysRequest changeAbsenceDaysRequest = transactionalDataManager.load(ChangeAbsenceDaysRequest.class)
                .id(entity.getId()).view("changeAbsenceDaysRequest.edit").optional().orElse(null);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        if (changeAbsenceDaysRequest != null) {

            PersonExt person = commonService.getEntity(PersonExt.class,
                    "select e from base$PersonExt e " +
                            " where e.group.id = :groupId " +
                            "   and current_date between e.startDate and e.endDate ",
                    ParamsMap.of("groupId", changeAbsenceDaysRequest.getEmployee().getId()),
                    View.LOCAL);

            params.put("fullNameRu", person.getFullNameLatin("ru"));
            params.put("fullNameEn", person.getFullNameLatin("en"));
            params.put("status", changeAbsenceDaysRequest.getStatus() != null
                    ? changeAbsenceDaysRequest.getStatus().getLangValue1()
                    : null);
            params.put("type", changeAbsenceDaysRequest.getRequestType() != null
                    ? changeAbsenceDaysRequest.getRequestType().getLangValue1()
                    : "");
            params.put("dateFrom", changeAbsenceDaysRequest.getNewStartDate() != null
                    ? dateFormat.format(changeAbsenceDaysRequest.getNewStartDate())
                    : null);
            params.put("dateTo", changeAbsenceDaysRequest.getNewEndDate() != null
                    ? dateFormat.format(changeAbsenceDaysRequest.getNewEndDate())
                    : null);
        }

        return params;
    }

    @Override
    public boolean instanceOf(Class<? extends AbstractBprocRequest> tClass) {
        return ChangeAbsenceDaysRequest.class.isAssignableFrom(tClass);
    }
}