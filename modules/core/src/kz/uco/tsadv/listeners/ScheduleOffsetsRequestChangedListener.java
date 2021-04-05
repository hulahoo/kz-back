package kz.uco.tsadv.listeners;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.tsadv.api.BaseResult;
import kz.uco.tsadv.api.Null;
import kz.uco.tsadv.modules.integration.jsonobject.ScheduleOffsetsRequestDataJson;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.model.AbsenceRvdRequest;
import kz.uco.tsadv.modules.personal.model.ScheduleOffsetsRequest;
import kz.uco.tsadv.service.IntegrationRestService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Component("tsadv_ScheduleOffsetsRequestChangedListener")
public class ScheduleOffsetsRequestChangedListener implements BeforeUpdateEntityListener<ScheduleOffsetsRequest>, BeforeInsertEntityListener<ScheduleOffsetsRequest> {

    @Inject
    IntegrationRestService integrationRestService;

    protected String APPROVED_STATUS = "APPROVED";
    protected SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void onBeforeInsert(ScheduleOffsetsRequest entity, EntityManager entityManager) {
        if(isApproved(entity,entityManager)){
            ScheduleOffsetsRequestDataJson scheduleOffsetsRequestJson = getScheduleOffsetsRequestDataJson(entity,entityManager);

            setupUnirest();
            HttpResponse<String> response = Unirest
                    .post("http://10.2.200.101:8290/api/ahruco/schedule/change/request")
                    .body(scheduleOffsetsRequestJson)
                    .asString();

            BaseResult baseResult = new BaseResult();
            String methodName = "tsadv_ScheduleOffsetsRequestChangedListener.onBeforeInsert";
            String responseBody = response.getBody();
            if (responseBody.contains("\"success\":\"true\"")) {
                integrationRestService.prepareSuccess(baseResult,
                        methodName,
                        scheduleOffsetsRequestJson);
            } else {
                integrationRestService.prepareError(baseResult,
                        methodName,
                        scheduleOffsetsRequestJson,
                        responseBody);
            }
        }
    }

    @Override
    public void onBeforeUpdate(ScheduleOffsetsRequest entity, EntityManager entityManager) {
        if(isApproved(entity,entityManager)){
            ScheduleOffsetsRequestDataJson scheduleOffsetsRequestJson = getScheduleOffsetsRequestDataJson(entity,entityManager);

            setupUnirest();
            HttpResponse<String> response = Unirest
                    .post("http://10.2.200.101:8290/api/ahruco/schedule/change/request")
                    .body(scheduleOffsetsRequestJson)
                    .asString();

            BaseResult baseResult = new BaseResult();
            String methodName = "tsadv_ScheduleOffsetsRequestChangedListener.onBeforeUpdate";
            String responseBody = response.getBody();
            if (responseBody.contains("\"success\":\"true\"")) {
                integrationRestService.prepareSuccess(baseResult,
                        methodName,
                        scheduleOffsetsRequestJson);
            } else {
                integrationRestService.prepareError(baseResult,
                        methodName,
                        scheduleOffsetsRequestJson,
                        responseBody);
            }
        }

    }

    protected ScheduleOffsetsRequestDataJson getScheduleOffsetsRequestDataJson(ScheduleOffsetsRequest entity, EntityManager entityManager){
        ScheduleOffsetsRequestDataJson scheduleOffsetsRequestJson = new ScheduleOffsetsRequestDataJson();
        String personId = (entity.getPersonGroup() != null && entity.getPersonGroup().getLegacyId() != null) ? entity.getPersonGroup().getLegacyId() : "";
        scheduleOffsetsRequestJson.setPersonId(personId);
        String requestNumber = (entity.getRequestNumber() != null) ? entity.getRequestNumber().toString() : "";
        scheduleOffsetsRequestJson.setRequestNumber(requestNumber);
        String requestDate = getFormattedDateString(entity.getRequestDate(),dateFormatter);
        scheduleOffsetsRequestJson.setRequestDate(requestDate);
        String purpose = (entity.getPurpose() != null && entity.getPurpose().getLangValue() != null) ? entity.getPurpose().getLangValue() : entity.getPurposeText();
        purpose = Null.nullReplace(purpose,"");
        scheduleOffsetsRequestJson.setPurpose(purpose);
        String currentScheduleId = (entity.getCurrentSchedule() != null && entity.getCurrentSchedule().getLegacyId() != null) ? entity.getCurrentSchedule().getLegacyId() : "";
        scheduleOffsetsRequestJson.setCurrentScheduleId(currentScheduleId);
        String newScheduleId = (entity.getNewSchedule() != null && entity.getNewSchedule().getLegacyId() != null) ? entity.getNewSchedule().getLegacyId() : "";
        scheduleOffsetsRequestJson.setNewScheduleId(newScheduleId);
        String startDate = getFormattedDateString(entity.getDateOfStartNewSchedule(),dateFormatter);
        scheduleOffsetsRequestJson.setStartDate(startDate);
        String endDate = getFormattedDateString(entity.getDateOfNewSchedule(),dateFormatter);
        scheduleOffsetsRequestJson.setEndDate(endDate);
        String details = Null.nullReplace(entity.getDetailsOfActualWork(),"");
        scheduleOffsetsRequestJson.setDetails(details);
        boolean isEmployeeAgree = Null.nullReplace(entity.getAgree(),false);
        scheduleOffsetsRequestJson.setEmployeeAgree(isEmployeeAgree);
        boolean isEmployeeInformed = Null.nullReplace(entity.getAcquainted(),false);
        scheduleOffsetsRequestJson.setEmployeeInformed(isEmployeeInformed);
        scheduleOffsetsRequestJson.setEarningPolicy("");
        String companyCode = "";
        if(entity.getPersonGroup() != null && entity.getPersonGroup().getCompany() != null){
            DicCompany company = entity.getPersonGroup().getCompany();
            companyCode = entityManager.reloadNN(company,View.LOCAL).getLegacyId();
            companyCode = Null.nullReplace(companyCode,"");
        }
        scheduleOffsetsRequestJson.setCompanyCode(companyCode);

        return scheduleOffsetsRequestJson;
    }

    protected void setupUnirest(){
        Unirest.config().setDefaultBasicAuth("ahruco", "ahruco");
        Unirest.config().addDefaultHeader("Content-Type", "application/json");
        Unirest.config().addDefaultHeader("Accept", "*/*");
        Unirest.config().addDefaultHeader("Accept-Encoding", "gzip, deflate, br");
    }

    protected boolean isApproved(ScheduleOffsetsRequest entity, EntityManager entityManager) {
        DicRequestStatus status = entity.getStatus();
        if (status == null) return false;
        return APPROVED_STATUS.equals(entityManager.reloadNN(status, View.LOCAL).getCode());
    }

    protected String getFormattedDateString(Date date, SimpleDateFormat formatter){
        return date != null ? formatter.format(date) : "" ;
    }
}