package kz.uco.tsadv.bproc.beans.entity;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.View;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.personal.model.ScheduleOffsetsRequest;
import kz.uco.tsadv.modules.timesheet.model.StandardSchedule;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Map;

@Component(BprocScheduleOffsetsRequestBean.NAME)
public class BprocScheduleOffsetsRequestBean extends AbstractBprocEntityBean<ScheduleOffsetsRequest> {
    public static final String NAME = "tsadv_BprocScheduleOffsetsRequestBean";

    @Override
    public Map<String, Object> getNotificationParams(String templateCode, ScheduleOffsetsRequest entity) {
        Map<String, Object> params = super.getNotificationParams(templateCode, entity);

        /*case "bpm.scheduleOffsetsRequest.approved.notification":
            case "bpm.scheduleOffsetsRequest.reject.notification":
            case "bpm.scheduleOffsetsRequest.initiator.notification":
            case "bpm.scheduleOffsetsRequest.revision.notification":
            case "bpm.scheduleOffsetsRequest.toapprove.notification":*/

        ScheduleOffsetsRequest scheduleOffsetsRequest = transactionalDataManager.load(ScheduleOffsetsRequest.class)
                .id(entity.getId()).view("scheduleOffsetsRequest-for-my-team").optional().orElse(null);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        PersonExt person = commonService.getEntity(PersonExt.class,
                "select e from base$PersonExt e " +
                        " where e.group.id = :groupId " +
                        "   and current_date between e.startDate and e.endDate ",
                ParamsMap.of("groupId", scheduleOffsetsRequest.getPersonGroup().getId()),
                View.LOCAL);

        StandardSchedule newSchedule = scheduleOffsetsRequest.getNewSchedule();
        params.put("fullNameRu", person.getFullNameLatin("ru"));
        params.put("fullNameEn", person.getFullNameLatin("en"));
        params.put("absenceTypeRu", newSchedule != null ? newSchedule.getScheduleName() : "");
        params.put("absenceTypeEn", newSchedule != null ? newSchedule.getScheduleName() : "");
        params.put("dateFrom", dateFormat.format(scheduleOffsetsRequest.getRequestDate()));
        params.put("dateTo", dateFormat.format(scheduleOffsetsRequest.getDateOfStartNewSchedule()));
        params.putIfAbsent("requestStatusRu", scheduleOffsetsRequest.getStatus().getLangValue1());
        params.putIfAbsent("requestStatusEn", scheduleOffsetsRequest.getStatus().getLangValue3());
        if (scheduleOffsetsRequest.getPurpose() != null && scheduleOffsetsRequest.getPurpose().getCode() != null) {
            if (scheduleOffsetsRequest.getPurpose().getCode().equals("OTHER")) {
                params.putIfAbsent("purposeRu", scheduleOffsetsRequest.getPurposeText() != null ?
                        scheduleOffsetsRequest.getPurposeText() : " ");
                params.putIfAbsent("purposeEn", scheduleOffsetsRequest.getPurposeText() != null ?
                        scheduleOffsetsRequest.getPurposeText() : " ");
            } else {
                params.putIfAbsent("purposeRu", scheduleOffsetsRequest.getPurpose().getLangValue1() != null ?
                        scheduleOffsetsRequest.getPurpose().getLangValue1() : " ");
                params.putIfAbsent("purposeEn", scheduleOffsetsRequest.getPurpose().getLangValue3() != null ?
                        scheduleOffsetsRequest.getPurpose().getLangValue3() : " ");
            }
        } else {
            params.putIfAbsent("purposeRu", " ");
            params.putIfAbsent("purposeEn", " ");
        }
        return params;
    }

    @Override
    public boolean instanceOf(Class<? extends AbstractBprocRequest> tClass) {
        return ScheduleOffsetsRequest.class.isAssignableFrom(tClass);
    }
}