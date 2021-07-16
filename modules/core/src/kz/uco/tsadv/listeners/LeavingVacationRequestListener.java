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
import kz.uco.tsadv.modules.integration.jsonobject.LeavingVacationRequestDataJson;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.Absence;
import kz.uco.tsadv.modules.personal.model.LeavingVacationRequest;
import kz.uco.tsadv.service.IntegrationRestService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component("tsadv_LeavingVacationRequestListener")
public class LeavingVacationRequestListener implements BeforeUpdateEntityListener<LeavingVacationRequest>, BeforeInsertEntityListener<LeavingVacationRequest> {


    @Inject
    IntegrationRestService integrationRestService;

    protected String APPROVED_STATUS = "APPROVED";
    protected SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Inject
    protected IntegrationConfig integrationConfig;
    protected String MATERNITY_RECALL_API_URL = integrationConfig.getLeavingVacationRequestUrl();

    @Override
    public void onBeforeInsert(LeavingVacationRequest entity, EntityManager entityManager) {
        if (isApproved(entity, entityManager) && !integrationConfig.getLeavingVacationRequestOff()) {
            LeavingVacationRequestDataJson leavingVacationRequestDataJson = getLeavingVacationRequestDataJson(entity, entityManager);

            setupUnirest();
            HttpResponse<String> response = Unirest
                    .post(getApiUrl())
                    .body(leavingVacationRequestDataJson)
                    .asString();

            BaseResult baseResult = new BaseResult();
            String methodName = "tsadv_LeavingVacationRequestListener.onBeforeInsert";
            String responseBody = response.getBody();
            if (responseBody.contains("\"success\":\"true\"")) {
                integrationRestService.prepareSuccess(baseResult,
                        methodName,
                        leavingVacationRequestDataJson);
            } else {
                integrationRestService.prepareError(baseResult,
                        methodName,
                        leavingVacationRequestDataJson,
                        responseBody);
            }
        }
    }

    @Override
    public void onBeforeUpdate(LeavingVacationRequest entity, EntityManager entityManager) {
        if (isApproved(entity, entityManager) && !integrationConfig.getLeavingVacationRequestOff()) {
            LeavingVacationRequestDataJson leavingVacationRequestDataJson = getLeavingVacationRequestDataJson(entity, entityManager);

            setupUnirest();
            HttpResponse<String> response = Unirest
                    .post(getApiUrl())
                    .body(leavingVacationRequestDataJson)
                    .asString();

            BaseResult baseResult = new BaseResult();
            String methodName = "tsadv_LeavingVacationRequestListener.onBeforeUpdate";
            String responseBody = response.getBody();
            if (responseBody.contains("\"success\":\"true\"")) {
                integrationRestService.prepareSuccess(baseResult,
                        methodName,
                        leavingVacationRequestDataJson);
            } else {
                integrationRestService.prepareError(baseResult,
                        methodName,
                        leavingVacationRequestDataJson,
                        responseBody);
            }
        }
    }

    protected LeavingVacationRequestDataJson getLeavingVacationRequestDataJson(LeavingVacationRequest entity, EntityManager entityManager) {
        LeavingVacationRequestDataJson leavingVacationRequestDataJson = new LeavingVacationRequestDataJson();
        Absence vacation = entity.getVacation();
        PersonGroupExt personGroup = (vacation != null && vacation.getPersonGroup() != null) ? vacation.getPersonGroup() : null;
        if (personGroup != null) {
            personGroup = entityManager.reloadNN(personGroup, View.LOCAL);
        }
        String personId = (personGroup != null && personGroup.getLegacyId() != null) ? personGroup.getLegacyId() : "";
        leavingVacationRequestDataJson.setPersonId(personId);
        String requestNumber = (entity.getRequestNumber() != null) ? entity.getRequestNumber().toString() : "";
        leavingVacationRequestDataJson.setRequestNumber(requestNumber);
        leavingVacationRequestDataJson.setRequestDate(getFormattedDateString(entity.getRequestDate()));
        String parentAbsenceLegacyId = (vacation != null && vacation.getLegacyId() != null) ? entity.getVacation().getLegacyId() : "";
        leavingVacationRequestDataJson.setParentAbsenceLegacyId(parentAbsenceLegacyId);
        leavingVacationRequestDataJson.setStartDate(getFormattedDateString(entity.getPlannedStartDate()));
        leavingVacationRequestDataJson.setEndDate(getFormattedDateString(entity.getEndDate()));
        leavingVacationRequestDataJson.setHasAbsenceAtAnotherPeriod(false);
        leavingVacationRequestDataJson.setHasCompensation(false);
        leavingVacationRequestDataJson.setNewStartDate(getFormattedDateString(null));
        leavingVacationRequestDataJson.setNewEndDate(getFormattedDateString(null));
        String purpose = Null.nullReplace(entity.getComment(), "");
        leavingVacationRequestDataJson.setPurpose(purpose);
        leavingVacationRequestDataJson.setEmployeeAgree(false);
        leavingVacationRequestDataJson.setEmployeeInformed(false);
        String companyCode = "";
        if (personGroup != null && personGroup.getCompany() != null) {
            DicCompany company = personGroup.getCompany();
            companyCode = entityManager.reloadNN(company, View.LOCAL).getLegacyId();
            companyCode = Null.nullReplace(companyCode, "");
        }
        leavingVacationRequestDataJson.setCompanyCode(companyCode);

        return leavingVacationRequestDataJson;
    }

    protected boolean isApproved(LeavingVacationRequest entity, EntityManager entityManager) {
        DicRequestStatus status = entity.getStatus();
        if (status == null) return false;
        return APPROVED_STATUS.equals(entityManager.reloadNN(status, View.LOCAL).getCode());
    }

    protected String getApiUrl() {
        return MATERNITY_RECALL_API_URL;
    }

    protected String getFormattedDateString(Date date) {
        return date != null ? formatter.format(date) : "";
    }

    protected void setupUnirest() {
        Unirest.config().setDefaultBasicAuth(integrationConfig.getBasicAuthLogin(), integrationConfig.getBasicAuthPassword());
        Unirest.config().addDefaultHeader("Content-Type", "application/json");
        Unirest.config().addDefaultHeader("Accept", "*/*");
        Unirest.config().addDefaultHeader("Accept-Encoding", "gzip, deflate, br");
    }
}