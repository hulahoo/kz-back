package kz.uco.tsadv.bproc.beans.entity;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.View;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsenceType;
import kz.uco.tsadv.modules.personal.model.AbsenceRequest;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Map;

@Component(BprocAbsenceRequestBean.NAME)
public class BprocAbsenceRequestBean extends AbstractBprocEntityBean<AbsenceRequest> {
    public static final String NAME = "tsadv_BprocAbsenceRequestBean";

    @Override
    public Map<String, Object> getNotificationParams(String templateCode, AbsenceRequest entity) {
        Map<String, Object> params = super.getNotificationParams(templateCode, entity);

        /*case "bpm.absenceRequest.approver.notification": {
                AbsenceRequest absenceRequest = (AbsenceRequest) dataManager.reload(entity, "absenceRequest.view");

                params.putIfAbsent("item", absenceRequest);
                params.putIfAbsent("tableRu", createTableAbsence(absenceRequest, "Ru"));
                params.putIfAbsent("tableEn", createTableAbsence(absenceRequest, "En"));
                break;
            }*/

        /*case "bpm.absenceRequest.initiator.notification":
        case "bpm.absenceRequest.revision.notification":
        case "bpm.absenceRequest.forInitiator.notification":
        case "bpm.absenceRequest.reject.notification":
        case "bpm.absenceRequest.approved.notification":
        case "bpm.absenceRequest.toapprove.notification":*/

        AbsenceRequest absenceRequest = transactionalDataManager.load(AbsenceRequest.class)
                .id(entity.getId()).view("absenceRequest.view").optional().orElse(null);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        PersonExt person = commonService.getEntity(PersonExt.class,
                "select p from base$AssignmentExt e " +
                        "   join e.personGroup.list p " +
                        " where e.group.id = :groupId" +
                        "   and current_date between e.startDate and e.endDate" +
                        "   and current_date between p.startDate and p.endDate" +
                        "   and e.primaryFlag = 'TRUE'  ",
                ParamsMap.of("groupId", absenceRequest.getAssignmentGroup().getId()),
                View.LOCAL);

        DicAbsenceType type = absenceRequest.getType();
        params.put("fullNameRu", person.getFullNameLatin("ru"));
        params.put("fullNameEn", person.getFullNameLatin("en"));
        params.put("absenceTypeRu", type.getLangValue1());
        params.put("absenceTypeEn", type.getLangValue3());
        params.put("dateFrom", dateFormat.format(absenceRequest.getDateFrom()));
        params.put("dateTo", dateFormat.format(absenceRequest.getDateTo()));
        params.put("days", absenceRequest.getAbsenceDays());
        params.putIfAbsent("requestStatusRu", absenceRequest.getStatus().getLangValue1());
        params.putIfAbsent("requestStatusEn", absenceRequest.getStatus().getLangValue3());
        if (absenceRequest.getPurpose() != null && absenceRequest.getPurpose().getCode() != null) {
            if (absenceRequest.getPurpose().getCode().equals("OTHER")) {
                params.putIfAbsent("purposeRu", absenceRequest.getPurposeText() != null ?
                        absenceRequest.getPurposeText() : " ");
                params.putIfAbsent("purposeEn", absenceRequest.getPurposeText() != null ?
                        absenceRequest.getPurposeText() : " ");
            } else {
                params.putIfAbsent("purposeRu", absenceRequest.getPurpose().getLangValue1() != null ?
                        absenceRequest.getPurpose().getLangValue1() : " ");
                params.putIfAbsent("purposeEn", absenceRequest.getPurpose().getLangValue3() != null ?
                        absenceRequest.getPurpose().getLangValue3() : " ");
            }
        } else {
            params.putIfAbsent("purposeRu", " ");
            params.putIfAbsent("purposeEn", " ");
        }

        return params;
    }

    /*protected String createTableAbsence(AbsenceRequest absenceRequest, String lang) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        boolean isRussian = !lang.equals("En");

        PersonExt person = commonService.getEntity(PersonExt.class,
                "select p from base$AssignmentExt e " +
                        "   join e.personGroup.list p " +
                        " where e.group.id = :groupId" +
                        "   and current_date between e.startDate and e.endDate" +
                        "   and current_date between p.startDate and p.endDate" +
                        "   and e.primaryFlag = 'TRUE'  ",
                ParamsMap.of("groupId", absenceRequest.getAssignmentGroup().getId()),
                View.LOCAL);

        DicAbsenceType type = absenceRequest.getType();

        Map<String, Object> params = new HashMap<>();
        params.put("employeeFullName", person.getFullNameLatin(lang.toLowerCase()));
//        params.put("absenceType", (isRussian ? type.getLangValue1() : type.getLangValue3()));
        params.put("dateFrom", dateFormat.format(absenceRequest.getDateFrom()));
        params.put("dateTo", dateFormat.format(absenceRequest.getDateTo()));
        params.put("days", absenceRequest.getAbsenceDays());
        params.put("comment", absenceRequest.getComment());

        String templateContents = resources.getResourceAsString(String.format(templateFolder + "absenceRequest/AbsenceRequestTable%s.html", lang));
        Assert.notNull(templateContents, "templateContents not found!");
        return TemplateHelper.processTemplate(templateContents, params);
    }*/

    @Override
    public boolean instanceOf(Class<? extends AbstractBprocRequest> tClass) {
        return AbsenceRequest.class.isAssignableFrom(tClass);
    }
}