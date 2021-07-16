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
import kz.uco.tsadv.modules.integration.jsonobject.ChangeAbsenceDaysRequestDataJson;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.model.ChangeAbsenceDaysRequest;
import kz.uco.tsadv.service.IntegrationRestService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;


@Component("tsadv_ChangeAbsenceDaysRequestListener")
public class ChangeAbsenceDaysRequestListener implements BeforeUpdateEntityListener<ChangeAbsenceDaysRequest>, BeforeInsertEntityListener<ChangeAbsenceDaysRequest> {

    @Inject
    private IntegrationConfig integrationConfig;
    @Inject
    IntegrationRestService integrationRestService;
    protected final String APPROVED_STATUS = "APPROVED";
    protected SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    protected String CHANGE_ABSENCE_DAYS_REQUEST_API_URL = integrationConfig.getChangeAbsenceDaysRequestUrl();

    @Override
    public void onBeforeUpdate(ChangeAbsenceDaysRequest entity, EntityManager entityManager) {
        if (isApproved(entity, entityManager) && !integrationConfig.getChangeAbsenceDaysRequestOff()) {
            ChangeAbsenceDaysRequestDataJson changeAbsenceDaysRequestJson = getChangeAbsenceDaysRequestDataJson(entity, entityManager);

            setupUnirest();
            HttpResponse<String> response = Unirest
                    .post(CHANGE_ABSENCE_DAYS_REQUEST_API_URL)
                    .body(changeAbsenceDaysRequestJson)
                    .asString();

            BaseResult baseResult = new BaseResult();
            String methodName = "tsadv_ChangeAbsenceDaysRequestListener.onBeforeUpdate";
            String responseBody = response.getBody();
            if (responseBody.contains("\"success\":\"true\"")) {
                integrationRestService.prepareSuccess(baseResult,
                        methodName,
                        changeAbsenceDaysRequestJson);
            } else {
                integrationRestService.prepareError(baseResult,
                        methodName,
                        changeAbsenceDaysRequestJson,
                        responseBody);
            }
        }

    }

    @Override
    public void onBeforeInsert(ChangeAbsenceDaysRequest entity, EntityManager entityManager) {
        if (isApproved(entity, entityManager) && !integrationConfig.getChangeAbsenceDaysRequestOff()) {
            ChangeAbsenceDaysRequestDataJson changeAbsenceDaysRequestJson = getChangeAbsenceDaysRequestDataJson(entity, entityManager);

            setupUnirest();
            HttpResponse<String> response = Unirest
                    .post(CHANGE_ABSENCE_DAYS_REQUEST_API_URL)
                    .body(changeAbsenceDaysRequestJson)
                    .asString();

            BaseResult baseResult = new BaseResult();
            String methodName = "tsadv_ChangeAbsenceDaysRequestListener.onBeforeInsert";
            String responseBody = response.getBody();
            if (responseBody.contains("\"success\":\"true\"")) {
                integrationRestService.prepareSuccess(baseResult,
                        methodName,
                        changeAbsenceDaysRequestJson);
            } else {
                integrationRestService.prepareError(baseResult,
                        methodName,
                        changeAbsenceDaysRequestJson,
                        responseBody);
            }
        }
    }

    protected boolean isApproved(ChangeAbsenceDaysRequest entity, EntityManager entityManager) {
        DicRequestStatus status = entity.getStatus();
        if (status == null) return false;
        return APPROVED_STATUS.equals(entityManager.reloadNN(status, View.LOCAL).getCode());
    }

    protected ChangeAbsenceDaysRequestDataJson getChangeAbsenceDaysRequestDataJson(ChangeAbsenceDaysRequest entity, EntityManager entityManager) {
        ChangeAbsenceDaysRequestDataJson changeAbsenceDaysRequestJson = new ChangeAbsenceDaysRequestDataJson();
        String personId = (entity.getEmployee() != null && entity.getEmployee().getLegacyId() != null) ? entity.getEmployee().getLegacyId() : "";
        changeAbsenceDaysRequestJson.setPersonId(personId);
        String requestNumber = entity.getRequestNumber() != null ? entity.getRequestNumber().toString() : "";
        changeAbsenceDaysRequestJson.setRequestNumber(requestNumber);
        String requestDate = getFormattedDateString(entity.getRequestDate());
        changeAbsenceDaysRequestJson.setRequestDate(requestDate);
        String parentAbsenceLegacyId = (entity.getVacation() != null && entity.getVacation().getLegacyId() != null) ? entity.getVacation().getLegacyId() : "";
        changeAbsenceDaysRequestJson.setParentAbsenceLegacyId(parentAbsenceLegacyId);
        String startDate = getFormattedDateString(entity.getNewStartDate());
        changeAbsenceDaysRequestJson.setStartDate(startDate);
        String endDate = getFormattedDateString(entity.getNewEndDate());
        changeAbsenceDaysRequestJson.setEndDate(endDate);
        String watchStartDate = getFormattedDateString(entity.getScheduleStartDate());
        changeAbsenceDaysRequestJson.setWatchStartDate(watchStartDate);
        String watchEndDate = getFormattedDateString(entity.getScheduleEndDate());
        changeAbsenceDaysRequestJson.setWatchEndDate(watchEndDate);
        String purpose = (entity.getPurpose() != null && entity.getPurpose().getLangValue() != null) ? entity.getPurpose().getLangValue() : entity.getPurposeText();
        purpose = Null.nullReplace(purpose, "");
        changeAbsenceDaysRequestJson.setPurpose(purpose);
        boolean isEmployeeAgree = Null.nullReplace(entity.getAgree(), false);
        changeAbsenceDaysRequestJson.setEmployeeAgree(isEmployeeAgree);
        boolean isEmployeeInformed = Null.nullReplace(entity.getFamiliarization(), false);
        changeAbsenceDaysRequestJson.setEmployeeInformed(isEmployeeInformed);
        String companyCode = "";
        if (entity.getEmployee() != null && entity.getEmployee().getCompany() != null) {
            DicCompany company = entity.getEmployee().getCompany();
            companyCode = entityManager.reloadNN(company, View.LOCAL).getLegacyId();
            companyCode = Null.nullReplace(companyCode, "");
        }
        changeAbsenceDaysRequestJson.setCompanyCode(companyCode);

        return changeAbsenceDaysRequestJson;
    }

    protected void setupUnirest() {
        Unirest.config().setDefaultBasicAuth(integrationConfig.getBasicAuthLogin(), integrationConfig.getBasicAuthPassword());
        Unirest.config().addDefaultHeader("Content-Type", "application/json");
        Unirest.config().addDefaultHeader("Accept", "*/*");
        Unirest.config().addDefaultHeader("Accept-Encoding", "gzip, deflate, br");
    }

    protected String getFormattedDateString(Date date) {
        return date != null ? formatter.format(date) : "";
    }
}