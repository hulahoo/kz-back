package kz.uco.tsadv.bproc.beans.entity;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.View;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsenceType;
import kz.uco.tsadv.modules.personal.model.AbsenceRvdRequest;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Map;

@Component(BprocAbsenceRvdRequestBean.NAME)
public class BprocAbsenceRvdRequestBean extends AbstractBprocEntityBean<AbsenceRvdRequest> {
    public static final String NAME = "tsadv_BprocAbsenceRvdRequestBean";

    @Override
    public Map<String, Object> getNotificationParams(String templateCode, AbsenceRvdRequest entity) {

        /*case "Application.for.withdrawal.from.labor.leave.requires":
        case "Application.for.withdrawal.from.labor.leave.rejected":
        case "Application.for.withdrawal.from.labor.leave.approved":*/

        Map<String, Object> params = super.getNotificationParams(templateCode, entity);
        AbsenceRvdRequest absenceRvdRequest = transactionalDataManager.load(AbsenceRvdRequest.class)
                .id(entity.getId()).view("absenceRvdRequest.edit").optional().orElse(null);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        PersonExt person = commonService.getEntity(PersonExt.class,
                "select e from base$PersonExt e " +
                        " where e.group.id = :groupId " +
                        "   and current_date between e.startDate and e.endDate ",
                ParamsMap.of("groupId", absenceRvdRequest.getPersonGroup().getId()),
                View.LOCAL);

        DicAbsenceType type = absenceRvdRequest.getType();
        params.put("fullNameRu", person.getFullNameLatin("ru"));
        params.put("fullNameEn", person.getFullNameLatin("en"));
        params.put("absenceTypeRu", type.getLangValue1());
        params.put("absenceTypeEn", type.getLangValue3());
        params.put("dateFrom", dateFormat.format(absenceRvdRequest.getTimeOfStarting()));
        params.put("dateTo", dateFormat.format(absenceRvdRequest.getTimeOfFinishing()));
        params.putIfAbsent("requestStatusRu", absenceRvdRequest.getStatus().getLangValue1());
        params.putIfAbsent("requestStatusEn", absenceRvdRequest.getStatus().getLangValue3());
        if (absenceRvdRequest.getPurpose() != null && absenceRvdRequest.getPurpose().getCode() != null) {
            if (absenceRvdRequest.getPurpose().getCode().equals("OTHER")) {
                params.putIfAbsent("purposeRu", absenceRvdRequest.getPurposeText() != null ?
                        absenceRvdRequest.getPurposeText() : " ");
                params.putIfAbsent("purposeEn", absenceRvdRequest.getPurposeText() != null ?
                        absenceRvdRequest.getPurposeText() : " ");
            } else {
                params.putIfAbsent("purposeRu", absenceRvdRequest.getPurpose().getLangValue1() != null ?
                        absenceRvdRequest.getPurpose().getLangValue1() : " ");
                params.putIfAbsent("purposeEn", absenceRvdRequest.getPurpose().getLangValue3() != null ?
                        absenceRvdRequest.getPurpose().getLangValue3() : " ");
            }
        } else {
            params.putIfAbsent("purposeRu", " ");
            params.putIfAbsent("purposeEn", " ");
        }

        return params;
    }

    @Override
    public boolean instanceOf(Class<? extends AbstractBprocRequest> tClass) {
        return AbsenceRvdRequest.class.isAssignableFrom(tClass);
    }
}