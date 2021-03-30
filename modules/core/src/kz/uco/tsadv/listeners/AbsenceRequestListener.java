package kz.uco.tsadv.listeners;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kz.uco.tsadv.api.BaseResult;
import kz.uco.tsadv.modules.integration.jsonobject.AbsenceRequestDataJson;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.model.AbsenceRequest;
import kz.uco.tsadv.service.IntegrationRestService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component("tsadv_AbsenceRequestListener")
public class AbsenceRequestListener implements BeforeUpdateEntityListener<AbsenceRequest>, BeforeInsertEntityListener<AbsenceRequest> {

    private final String APPROVED_STATUS = "APPROVED";

    @Inject
    protected IntegrationRestService integrationRestService;

    @Override
    public void onBeforeUpdate(AbsenceRequest entity, EntityManager entityManager) {
        if (isApproved(entity, entityManager)) {
            AbsenceRequestDataJson absenceJson = getAbsenceRequestDataJson(entity);

            Unirest.config().setDefaultBasicAuth("ahruco", "ahruco");
            Unirest.config().addDefaultHeader("Content-Type", "application/json");
            Unirest.config().addDefaultHeader("Accept", "*/*");
            Unirest.config().addDefaultHeader("Accept-Encoding", "gzip, deflate, br");

            HttpResponse<String> response = Unirest.post("http://10.2.200.101:8290/api/ahruco/absence/request").body(absenceJson).asString();

            BaseResult baseResult = new BaseResult();
            String methodName = "AbsenceRequestListener.onBeforeUpdate";
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
        if (isApproved(entity, entityManager)) {
            AbsenceRequestDataJson absenceJson = getAbsenceRequestDataJson(entity);

            Unirest.config().setDefaultBasicAuth("ahruco", "ahruco");
            Unirest.config().addDefaultHeader("Content-Type", "application/json");
            Unirest.config().addDefaultHeader("Accept", "*/*");
            Unirest.config().addDefaultHeader("Accept-Encoding", "gzip, deflate, br");

            HttpResponse<String> response = Unirest.post("http://10.2.200.101:8290/api/ahruco/absence/request").body(absenceJson).asString();

            BaseResult baseResult = new BaseResult();
            String methodName = "AbsenceRequestListener.onBeforeInsert";
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

    protected AbsenceRequestDataJson getAbsenceRequestDataJson(AbsenceRequest entity) {
        AbsenceRequestDataJson absenceJson = new AbsenceRequestDataJson();
        String personId = (entity.getPersonGroup() != null && entity.getPersonGroup().getLegacyId() != null) ? entity.getPersonGroup().getLegacyId() : "";
        absenceJson.setPersonId(personId);
        absenceJson.setRequestNumber(entity.getRequestNumber().toString());
        absenceJson.setAbsenceTypeId(entity.getType().getLegacyId());
        absenceJson.setStartDate(entity.getDateFrom().toString());
        absenceJson.setEndDate(entity.getDateTo().toString());
        absenceJson.setAbsenceDuration(entity.getAbsenceDays());
        String purpose = (entity.getReason() != null) ? entity.getReason() : "";
        absenceJson.setPurpose(purpose);
        boolean isProvideSheetOfTemporary = (entity.getOriginalSheet() != null ? entity.getOriginalSheet() : false);
        absenceJson.setIsProvideSheetOfTemporary(isProvideSheetOfTemporary);
        absenceJson.setIsOnRotation(false);
        String companyCode = (entity.getPersonGroup() != null
                && entity.getPersonGroup().getCompany() != null
                && entity.getPersonGroup().getCompany().getLegacyId() != null) ? entity.getPersonGroup().getCompany().getLegacyId() : "";
        absenceJson.setCompanyCode(companyCode);
        return absenceJson;
    }
}