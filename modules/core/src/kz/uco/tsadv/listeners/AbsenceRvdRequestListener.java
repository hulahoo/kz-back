package kz.uco.tsadv.listeners;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.tsadv.api.BaseResult;
import kz.uco.tsadv.api.Null;
import kz.uco.tsadv.modules.integration.jsonobject.AbsenceRvdRequestDataJson;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.model.AbsenceRvdRequest;
import kz.uco.tsadv.service.IntegrationRestService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component("tsadv_AbsenceRvdRequestListener")
public class AbsenceRvdRequestListener implements BeforeUpdateEntityListener<AbsenceRvdRequest>, BeforeInsertEntityListener<AbsenceRvdRequest> {

    @Inject
    IntegrationRestService integrationRestService;

    protected String APPROVED_STATUS = "APPROVED";
    protected SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    protected SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");

    @Override
    public void onBeforeInsert(AbsenceRvdRequest entity, EntityManager entityManager) {
        if(isApproved(entity,entityManager)){
            AbsenceRvdRequestDataJson absenceRvdRequestJson = getAbsenceRvdRequestDataJson(entity,entityManager);

            setupUnirest();
            HttpResponse<String> response = Unirest
                    .post("http://10.2.200.101:8290/api/ahruco/work/holiday/request")
                    .body(absenceRvdRequestJson)
                    .asString();

            BaseResult baseResult = new BaseResult();
            String methodName = "tsadv_AbsenceRvdRequestListener.onBeforeInsert";
            String responseBody = response.getBody();
            if (responseBody.contains("\"success\":\"true\"")) {
                integrationRestService.prepareSuccess(baseResult,
                        methodName,
                        absenceRvdRequestJson);
            } else {
                integrationRestService.prepareError(baseResult,
                        methodName,
                        absenceRvdRequestJson,
                        responseBody);
            }
        }
    }

    @Override
    public void onBeforeUpdate(AbsenceRvdRequest entity, EntityManager entityManager) {
        if(isApproved(entity,entityManager)){
            AbsenceRvdRequestDataJson absenceRvdRequestJson = getAbsenceRvdRequestDataJson(entity,entityManager);

            setupUnirest();
            HttpResponse<String> response = Unirest
                    .post("http://10.2.200.101:8290/api/ahruco/work/holiday/request")
                    .body(absenceRvdRequestJson)
                    .asString();

            BaseResult baseResult = new BaseResult();
            String methodName = "tsadv_AbsenceRvdRequestListener.onBeforeUpdate";
            String responseBody = response.getBody();
            if (responseBody.contains("\"success\":\"true\"")) {
                integrationRestService.prepareSuccess(baseResult,
                        methodName,
                        absenceRvdRequestJson);
            } else {
                integrationRestService.prepareError(baseResult,
                        methodName,
                        absenceRvdRequestJson,
                        responseBody);
            }
        }
    }

    protected void setupUnirest(){
        Unirest.config().setDefaultBasicAuth("ahruco", "ahruco");
        Unirest.config().addDefaultHeader("Content-Type", "application/json");
        Unirest.config().addDefaultHeader("Accept", "*/*");
        Unirest.config().addDefaultHeader("Accept-Encoding", "gzip, deflate, br");
    }

    protected boolean isApproved(AbsenceRvdRequest entity, EntityManager entityManager) {
        DicRequestStatus status = entity.getStatus();
        if (status == null) return false;
        return APPROVED_STATUS.equals(entityManager.reloadNN(status, View.LOCAL).getCode());
    }

    protected String getFormattedDateString(Date date,SimpleDateFormat formatter){
        return date != null ? formatter.format(date) : "" ;
    }

    protected AbsenceRvdRequestDataJson getAbsenceRvdRequestDataJson(AbsenceRvdRequest entity, EntityManager entityManager){
        AbsenceRvdRequestDataJson absenceRvdRequestDataJson = new AbsenceRvdRequestDataJson();
        String personId = (entity.getPersonGroup() != null && entity.getPersonGroup().getLegacyId() != null) ? entity.getPersonGroup().getLegacyId() : "";
        absenceRvdRequestDataJson.setPersonId(personId);
        String requestNumber = (entity.getRequestNumber() != null) ? entity.getRequestNumber().toString() : "";
        absenceRvdRequestDataJson.setRequestNumber(requestNumber);
        String requestDate = getFormattedDateString(entity.getRequestDate(),dateFormatter);
        absenceRvdRequestDataJson.setRequestDate(requestDate);
        String purpose = (entity.getPurpose() != null && entity.getPurpose().getLangValue() != null) ? entity.getPurpose().getLangValue() : entity.getPurposeText();
        purpose = Null.nullReplace(purpose,"");
        absenceRvdRequestDataJson.setPurpose(purpose);
        String startDate = getFormattedDateString(entity.getTimeOfStarting(),dateFormatter);
        absenceRvdRequestDataJson.setStartDate(startDate);
        String endDate = getFormattedDateString(entity.getTimeOfFinishing(),dateFormatter);
        absenceRvdRequestDataJson.setEndDate(endDate);
        String hours = (entity.getTotalHours() != null) ? entity.getTotalHours().toString() : "";
        absenceRvdRequestDataJson.setHours(hours);
        boolean compensation = Null.nullReplace(entity.getCompencation(),false);
        String isVacationProvidedOrCompensationProvided = (compensation) ? "Compensation" : "Vacation";
        absenceRvdRequestDataJson.setIsVacationProvidedOrCompensationProvided(isVacationProvidedOrCompensationProvided);
        boolean isEmployeeAgree = Null.nullReplace(entity.getAgree(),false);
        absenceRvdRequestDataJson.setEmployeeAgree(isEmployeeAgree);
        boolean isEmployeeInformed = Null.nullReplace(entity.getAcquainted(),false);
        absenceRvdRequestDataJson.setEmployeeInformed(isEmployeeInformed);
        absenceRvdRequestDataJson.setShiftCode("");
        String startTime = getFormattedDateString(entity.getTimeOfStarting(),timeFormatter);
        absenceRvdRequestDataJson.setStartTime(startTime);
        String endTime = getFormattedDateString(entity.getTimeOfFinishing(),timeFormatter);
        absenceRvdRequestDataJson.setEndTime(endTime);
        String companyCode = "";
        if(entity.getPersonGroup() != null && entity.getPersonGroup().getCompany() != null){
            DicCompany company = entity.getPersonGroup().getCompany();
            companyCode = entityManager.reloadNN(company,View.LOCAL).getLegacyId();
            companyCode = Null.nullReplace(companyCode,"");
        }
        absenceRvdRequestDataJson.setCompanyCode(companyCode);

        return absenceRvdRequestDataJson;
    }


}