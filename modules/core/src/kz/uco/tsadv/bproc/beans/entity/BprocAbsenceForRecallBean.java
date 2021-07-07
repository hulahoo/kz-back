package kz.uco.tsadv.bproc.beans.entity;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.View;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsenceType;
import kz.uco.tsadv.modules.personal.model.AbsenceForRecall;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Map;

@Component(BprocAbsenceForRecallBean.NAME)
public class BprocAbsenceForRecallBean<T extends AbsenceForRecall> extends AbstractBprocEntityBean<T> {
    public static final String NAME = "tsadv_BprocAbsenceForRecallBean";

    @Override
    public Map<String, Object> getNotificationParams(String templateCode, T entity) {

        /*case "Application.for.withdrawal.from.labor.leave.requires":
        case "Application.for.withdrawal.from.labor.leave.rejected":
        case "Application.for.withdrawal.from.labor.leave.approved":*/

        Map<String, Object> params = super.getNotificationParams(templateCode, entity);
        AbsenceForRecall absenceForRecall = transactionalDataManager.load(AbsenceForRecall.class)
                .id(entity.getId()).view("absenceForRecall.edit").optional().orElse(null);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        PersonExt person = commonService.getEntity(PersonExt.class,
                "select e from base$PersonExt e " +
                        " where e.group.id = :groupId " +
                        "   and current_date between e.startDate and e.endDate ",
                ParamsMap.of("groupId", absenceForRecall.getEmployee().getId()),
                View.LOCAL);

        DicAbsenceType type = absenceForRecall.getVacation().getType();
        params.put("fullNameRu", person.getFullNameLatin("ru"));
        params.put("fullNameEn", person.getFullNameLatin("en"));
        params.put("absenceTypeRu", type.getLangValue1());
        params.put("absenceTypeEn", type.getLangValue3());
        params.put("dateFrom", absenceForRecall.getRecallDateFrom() != null ?
                dateFormat.format(absenceForRecall.getRecallDateFrom()) : "");
        params.put("dateTo", absenceForRecall.getRecallDateTo() != null ?
                dateFormat.format(absenceForRecall.getRecallDateTo()) : "");
        params.putIfAbsent("requestStatusRu", absenceForRecall.getStatus().getLangValue1());
        params.putIfAbsent("requestStatusEn", absenceForRecall.getStatus().getLangValue3());
        if (absenceForRecall.getPurpose() != null && absenceForRecall.getPurpose().getCode() != null) {
            if (absenceForRecall.getPurpose().getCode().equals("OTHER")) {
                params.putIfAbsent("purposeRu", absenceForRecall.getPurposeText() != null ?
                        absenceForRecall.getPurposeText() : " ");
                params.putIfAbsent("purposeEn", absenceForRecall.getPurposeText() != null ?
                        absenceForRecall.getPurposeText() : " ");
            } else {
                params.putIfAbsent("purposeRu", absenceForRecall.getPurpose().getLangValue1() != null ?
                        absenceForRecall.getPurpose().getLangValue1() : " ");
                params.putIfAbsent("purposeEn", absenceForRecall.getPurpose().getLangValue3() != null ?
                        absenceForRecall.getPurpose().getLangValue3() : " ");
            }
        } else {
            params.putIfAbsent("purposeRu", " ");
            params.putIfAbsent("purposeEn", " ");
        }
        return params;
    }
}