package kz.uco.tsadv.service;

import com.haulmont.cuba.core.global.DataManager;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kz.uco.tsadv.api.BaseResult;
import kz.uco.tsadv.config.IntegrationConfig;
import kz.uco.tsadv.entity.VacationScheduleRequest;
import kz.uco.tsadv.global.enums.SendingToOracleStatus;
import kz.uco.tsadv.modules.integration.jsonobject.AbsenceRequestDataJson;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service(IntegrationScheduledService.NAME)
public class IntegrationScheduledServiceBean implements IntegrationScheduledService {

    @Inject
    protected DataManager dataManager;
    @Inject
    protected IntegrationRestService integrationRestService;

    protected SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Inject
    protected IntegrationConfig integrationConfig;

    @Override
    public void sendVacationScheduleRequestToOracle() {
        List<VacationScheduleRequest> sendingToOracle = dataManager.load(VacationScheduleRequest.class)
                .query("select e from tsadv_VacationScheduleRequest e " +
                        " where e.sentToOracle = :sentToOracle")
                .parameter("sentToOracle", SendingToOracleStatus.SENDING_TO_ORACLE)
                .view("vacationScheduleRequest-for-integration").list();
        BaseResult baseResult = null;
        String methodName = "tsadv_IntegrationScheduledService.sendVacationScheduleRequestToOracle";

        for (VacationScheduleRequest vacationScheduleRequest : sendingToOracle) {
            try {
                AbsenceRequestDataJson absenceJson = null;
                String responseBody = null;
                baseResult = new BaseResult();
                absenceJson = getAbsenceRequestDataJson(vacationScheduleRequest);
                try {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Accept", "*/*");
                    headers.put("Accept-Encoding", "gzip, deflate, br");
                    HttpResponse<String> response = Unirest
                            .post(integrationConfig.getAbsenceRequestUrl())
                            .basicAuth(integrationConfig.getBasicAuthLogin(), integrationConfig.getBasicAuthPassword())
                            .headers(headers)
                            .body(absenceJson)
                            .asString();
                    responseBody = response.getBody();
                    if (responseBody.contains("\"success\":\"true\"")) {
                        vacationScheduleRequest.setSentToOracle(SendingToOracleStatus.SENT_TO_ORACLE);
                        dataManager.commit(vacationScheduleRequest);
                        integrationRestService.prepareSuccess(baseResult,
                                methodName,
                                absenceJson);
                    } else {
                        integrationRestService.prepareError(baseResult,
                                methodName,
                                absenceJson,
                                responseBody);
                    }
                } catch (Exception e) {
                    integrationRestService.prepareError(baseResult,
                            methodName,
                            absenceJson,
                            responseBody + e.getMessage());
                }
            } catch (Exception exception) {
                String stackTrace = Arrays.stream(exception.getStackTrace()).map(stackTraceElement ->
                        stackTraceElement.toString()).collect(Collectors.joining());
                integrationRestService.prepareError(new BaseResult(),
                        methodName,
                        vacationScheduleRequest,
                        stackTrace);
            }
        }
    }

    protected AbsenceRequestDataJson getAbsenceRequestDataJson(VacationScheduleRequest vacationScheduleRequest) {
        AbsenceRequestDataJson absenceJson = new AbsenceRequestDataJson();
        String personId = (vacationScheduleRequest.getPersonGroup() != null
                && vacationScheduleRequest.getPersonGroup().getLegacyId() != null)
                ? vacationScheduleRequest.getPersonGroup().getLegacyId() : "";
        absenceJson.setPersonId(personId);
        String requestNumber = (vacationScheduleRequest.getRequestNumber() != null)
                ? vacationScheduleRequest.getRequestNumber().toString() : "";
        absenceJson.setRequestNumber(requestNumber);
        absenceJson.setAbsenceTypeId("0");
        String startDate = getFormattedDateString(vacationScheduleRequest.getStartDate());
        absenceJson.setStartDate(startDate);
        String endDate = getFormattedDateString(vacationScheduleRequest.getEndDate());
        absenceJson.setEndDate(endDate);
        String absenceDuration = (vacationScheduleRequest.getAbsenceDays() != null)
                ? vacationScheduleRequest.getAbsenceDays().toString() : "";
        absenceJson.setAbsenceDuration(absenceDuration);
        String purpose = (vacationScheduleRequest.getComment() != null) ? vacationScheduleRequest.getComment() : "";
        absenceJson.setPurpose(purpose);
        absenceJson.setIsOnRotation(false);
        absenceJson.setRequestDate(vacationScheduleRequest.getRequestDate() != null
                ? getFormattedDateString(vacationScheduleRequest.getRequestDate()) : "");
        absenceJson.setCompanyCode(vacationScheduleRequest.getPersonGroup() != null
                && vacationScheduleRequest.getPersonGroup().getCompany() != null
                && vacationScheduleRequest.getPersonGroup().getCompany().getLegacyId() != null ?
                vacationScheduleRequest.getPersonGroup().getCompany().getLegacyId() : "");
        return absenceJson;
    }

    protected String getFormattedDateString(Date date) {
        return date != null ? formatter.format(date) : "";
    }

}