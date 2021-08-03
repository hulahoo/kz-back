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
import kz.uco.tsadv.config.IntegrationConfig;
import kz.uco.tsadv.modules.integration.jsonobject.AbsenceRequestDataJson;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.model.AbsenceRequest;
import kz.uco.tsadv.service.IntegrationRestService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component("tsadv_AbsenceRequestListener")
public class AbsenceRequestListener implements BeforeUpdateEntityListener<AbsenceRequest>, BeforeInsertEntityListener<AbsenceRequest> {

    protected String APPROVED_STATUS = "APPROVED";
    protected SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    protected SimpleDateFormat formatterNoTime = new SimpleDateFormat("yyyy-MM-dd");

    @Inject
    protected IntegrationRestService integrationRestService;
    @Inject
    private IntegrationConfig integrationConfig;

    @Override
    public void onBeforeUpdate(AbsenceRequest entity, EntityManager entityManager) {
        if (isApproved(entity, entityManager) && !integrationConfig.getAbsenceRequestOff()) {
            AbsenceRequestDataJson absenceJson = getAbsenceRequestDataJson(entity, entityManager);

            setupUnirest();
            HttpResponse<String> response = Unirest
                    .post(getApiUrl())
                    .body(absenceJson)
                    .asString();

            BaseResult baseResult = new BaseResult();
            String methodName = "tsadv_AbsenceRequestListener.onBeforeUpdate";
            String responseBody = response.getBody();
            if (responseBody.contains("\"success\":\"true\"")) {
                integrationRestService.prepareSuccess(baseResult,
                        methodName,
                        absenceJson);
            } else {
                integrationRestService.prepareError(baseResult,
                        methodName,
                        absenceJson,
                        responseBody);
            }
        }
    }

    @Override
    public void onBeforeInsert(AbsenceRequest entity, EntityManager entityManager) {
        if (isApproved(entity, entityManager) && !integrationConfig.getAbsenceRequestOff()) {
            AbsenceRequestDataJson absenceJson = getAbsenceRequestDataJson(entity, entityManager);

            setupUnirest();
            HttpResponse<String> response = Unirest
                    .post(getApiUrl())
                    .body(absenceJson)
                    .asString();

            BaseResult baseResult = new BaseResult();
            String methodName = "tsadv_AbsenceRequestListener.onBeforeInsert";
            String responseBody = response.getBody();
            if (responseBody.contains("\"success\":\"true\"")) {
                integrationRestService.prepareSuccess(baseResult,
                        methodName,
                        absenceJson);
            } else {
                integrationRestService.prepareError(baseResult,
                        methodName,
                        absenceJson,
                        responseBody);
            }
        }
    }

    protected boolean isApproved(AbsenceRequest entity, EntityManager entityManager) {
        DicRequestStatus status = entity.getStatus();
        if (status == null) return false;
        return APPROVED_STATUS.equals(entityManager.reloadNN(status, View.LOCAL).getCode());
    }

    protected AbsenceRequestDataJson getAbsenceRequestDataJson(AbsenceRequest entity, EntityManager entityManager) {
        AbsenceRequestDataJson absenceJson = new AbsenceRequestDataJson();
        String personId = (entity.getPersonGroup() != null && entity.getPersonGroup().getLegacyId() != null) ? entity.getPersonGroup().getLegacyId() : "";
        absenceJson.setPersonId(personId);
        String requestNumber = (entity.getRequestNumber() != null) ? entity.getRequestNumber().toString() : "";
        absenceJson.setRequestNumber(requestNumber);
        String requestDate = entity.getRequestDate() != null ? formatterNoTime.format(entity.getRequestDate()) : "";
        absenceJson.setRequestDate(requestDate);
        String absenceTypeId = (entity.getType() != null && entity.getType().getLegacyId() != null) ? entity.getType().getLegacyId() : "";
        absenceJson.setAbsenceTypeId(absenceTypeId);
        String startDate = getFormattedDateString(entity.getDateFrom());
        absenceJson.setStartDate(startDate);
        String endDate = getFormattedDateString(entity.getDateTo());
        absenceJson.setEndDate(endDate);
        String absenceDuration = (entity.getAbsenceDays() != null) ? entity.getAbsenceDays().toString() : "";
        absenceJson.setAbsenceDuration(absenceDuration);
        String purpose = (entity.getReason() != null) ? entity.getReason() : "";
        absenceJson.setPurpose(purpose);
        boolean isProvideSheetOfTemporary = Null.nullReplace(entity.getOriginalSheet(), false);
        absenceJson.setIsProvideSheetOfTemporary(isProvideSheetOfTemporary);
        absenceJson.setIsOnRotation(false);
        String companyCode = "";
        if (entity.getPersonGroup() != null && entity.getPersonGroup().getCompany() != null) {
            DicCompany company = entity.getPersonGroup().getCompany();
            companyCode = entityManager.reloadNN(company, View.LOCAL).getLegacyId();
            companyCode = Null.nullReplace(companyCode, "");
        }
        absenceJson.setCompanyCode(companyCode);
        return absenceJson;
    }

    protected String getFormattedDateString(Date date) {
        return date != null ? formatter.format(date) : "";
    }

    protected String getApiUrl() {
        return integrationConfig.getAbsenceRequestRestUrl();
    }

    protected void setupUnirest() {
        Unirest.config().setDefaultBasicAuth("ahruco", "ahruco");
        Unirest.config().addDefaultHeader("Content-Type", "application/json");
        Unirest.config().addDefaultHeader("Accept", "*/*");
        Unirest.config().addDefaultHeader("Accept-Encoding", "gzip, deflate, br");
    }
}