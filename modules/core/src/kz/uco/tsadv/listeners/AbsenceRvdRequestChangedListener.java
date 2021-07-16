package kz.uco.tsadv.listeners;

import com.haulmont.cuba.core.TransactionalDataManager;
import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.View;
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
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Component("tsadv_AbsenceRvdRequestChangedListener")
public class AbsenceRvdRequestChangedListener {

    @Inject
    protected TransactionalDataManager transactionalDataManager;
    @Inject
    IntegrationRestService integrationRestService;

    protected SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    protected SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
    protected String APPROVED_STATUS = "APPROVED";
    @Inject
    private DataManager dataManager;

//    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
//    public void beforeCommit(EntityChangedEvent<AbsenceRvdRequest, UUID> event) {
//        AbsenceRvdRequest absenceRvdRequest;
//        if (event.getType().equals(EntityChangedEvent.Type.UPDATED)) {
//            absenceRvdRequest = transactionalDataManager.load(event.getEntityId()).view("absenceRvdRequest.edit").one();
//            if ("APPROVED".equals(absenceRvdRequest.getStatus().getCode())) {
//                if (absenceRvdRequest.getType().getWorkOnWeekend()) {
//                    AbsenceRvdRequestDataJson absenceRvdRequestJson = getAbsenceRvdRequestDataJson(absenceRvdRequest, true);
//
//                    setupUnirest();
//                    HttpResponse<String> response = Unirest
//                            .post("http://10.2.200.101:8290/api/ahruco/work/holiday/request")
//                            .body(absenceRvdRequestJson)
//                            .asString();
//
//                    BaseResult baseResult = new BaseResult();
//                    String methodName = "tsadv_AbsenceRvdRequestChangedListener.updated";
//                    String responseBody = response.getBody();
//                    if (responseBody.contains("\"success\":\"true\"")) {
//                        integrationRestService.prepareSuccess(baseResult,
//                                methodName,
//                                absenceRvdRequestJson);
//                    } else {
//                        integrationRestService.prepareError(baseResult,
//                                methodName,
//                                absenceRvdRequestJson,
//                                responseBody);
//                    }
//                } else if (absenceRvdRequest.getType().getTemporaryTransfer()) {
//                    AbsenceRvdRequestDataJson absenceRvdRequestJson = getAbsenceRvdRequestDataJson(absenceRvdRequest, false);
//
//                    setupUnirest();
//                    HttpResponse<String> response = Unirest
//                            .post("http://10.2.200.101:8290/api/ahruco/work/night/request")
//                            .body(absenceRvdRequestJson)
//                            .asString();
//
//                    BaseResult baseResult = new BaseResult();
//                    String methodName = "tsadv_AbsenceRvdRequestChangedListener.updated";
//                    String responseBody = response.getBody();
//                    if (responseBody.contains("\"success\":\"true\"")) {
//                        integrationRestService.prepareSuccess(baseResult,
//                                methodName,
//                                absenceRvdRequestJson);
//                    } else {
//                        integrationRestService.prepareError(baseResult,
//                                methodName,
//                                absenceRvdRequestJson,
//                                responseBody);
//                    }
//                }
//            }
//        } else if (event.getType().equals(EntityChangedEvent.Type.CREATED)) {
//            absenceRvdRequest = transactionalDataManager.load(event.getEntityId()).view("absenceRvdRequest.edit").one();
//            if ("APPROVED".equals(absenceRvdRequest.getStatus().getCode())) {
//                if (absenceRvdRequest.getType().getWorkOnWeekend()) {
//                    AbsenceRvdRequestDataJson absenceRvdRequestJson = getAbsenceRvdRequestDataJson(absenceRvdRequest, true);
//
//                    setupUnirest();
//                    HttpResponse<String> response = Unirest
//                            .post("http://10.2.200.101:8290/api/ahruco/work/holiday/request")
//                            .body(absenceRvdRequestJson)
//                            .asString();
//
//                    BaseResult baseResult = new BaseResult();
//                    String methodName = "tsadv_AbsenceRvdRequestChangedListener.created";
//                    String responseBody = response.getBody();
//                    if (responseBody.contains("\"success\":\"true\"")) {
//                        integrationRestService.prepareSuccess(baseResult,
//                                methodName,
//                                absenceRvdRequestJson);
//                    } else {
//                        integrationRestService.prepareError(baseResult,
//                                methodName,
//                                absenceRvdRequestJson,
//                                responseBody);
//                    }
//                } else if (absenceRvdRequest.getType().getTemporaryTransfer()) {
//                    AbsenceRvdRequestDataJson absenceRvdRequestJson = getAbsenceRvdRequestDataJson(absenceRvdRequest, false);
//
//                    setupUnirest();
//                    HttpResponse<String> response = Unirest
//                            .post("http://10.2.200.101:8290/api/ahruco/work/night/request")
//                            .body(absenceRvdRequestJson)
//                            .asString();
//
//                    BaseResult baseResult = new BaseResult();
//                    String methodName = "tsadv_AbsenceRvdRequestChangedListener.created";
//                    String responseBody = response.getBody();
//                    if (responseBody.contains("\"success\":\"true\"")) {
//                        integrationRestService.prepareSuccess(baseResult,
//                                methodName,
//                                absenceRvdRequestJson);
//                    } else {
//                        integrationRestService.prepareError(baseResult,
//                                methodName,
//                                absenceRvdRequestJson,
//                                responseBody);
//                    }
//                }
//            }
//        }
//    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit(EntityChangedEvent<AbsenceRvdRequest, UUID> event) {
        if (event.getChanges().getAttributes().stream().anyMatch("status"::equals)) {
            String methodName = "absenceRvdRequestChangedListener.afterCommit";
            BaseResult baseResult = new BaseResult();
            AbsenceRvdRequestDataJson absenceRvdRequestJson = new AbsenceRvdRequestDataJson();
            try {
                DicRequestStatus oldStatus = null;
                if (event.getChanges().getOldValue("status") != null
                        && ((Id<DicRequestStatus, UUID>) event.getChanges()
                        .getOldValue("status")).getValue() != null) {
                    oldStatus = dataManager.load(DicRequestStatus.class)
                            .query("select e from tsadv$DicRequestStatus e where e.id = :id")
                            .parameter("id", ((Id<DicRequestStatus, UUID>) event.getChanges()
                                    .getOldValue("status")).getValue())
                            .view(View.LOCAL).list().stream().findFirst().orElse(null);
                }
                AbsenceRvdRequest absenceRvdRequest = dataManager.load(event.getEntityId())
                        .view("absenceRvdRequest.edit").one();
                DicRequestStatus requestStatus = absenceRvdRequest.getStatus();
                if (APPROVED_STATUS.equals(requestStatus.getCode()) && !APPROVED_STATUS.equals(oldStatus != null
                        ? oldStatus.getCode() : "")) {
                    if (absenceRvdRequest.getType().getWorkOnWeekend()) {
                        absenceRvdRequestJson = getAbsenceRvdRequestDataJson(absenceRvdRequest, true);

                        setupUnirest();
                        HttpResponse<String> response = Unirest
                                .post("http://10.2.200.101:8290/api/ahruco/work/holiday/request")
                                .body(absenceRvdRequestJson)
                                .asString();

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
                    } else if (absenceRvdRequest.getType().getTemporaryTransfer()) {
                        absenceRvdRequestJson = getAbsenceRvdRequestDataJson(absenceRvdRequest, false);

                        setupUnirest();
                        HttpResponse<String> response = Unirest
                                .post("http://10.2.200.101:8290/api/ahruco/work/night/request")
                                .body(absenceRvdRequestJson)
                                .asString();

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
            } catch (Exception e) {
                String stackTrace = Arrays.stream(e.getStackTrace()).map(stackTraceElement ->
                        stackTraceElement.toString()).collect(Collectors.joining());
                e.printStackTrace();
                integrationRestService.prepareError(baseResult,
                        methodName,
                        absenceRvdRequestJson,
                        stackTrace);
            }
        }
    }

    protected void setupUnirest() {
        Unirest.config().setDefaultBasicAuth("ahruco", "ahruco");
        Unirest.config().addDefaultHeader("Content-Type", "application/json");
        Unirest.config().addDefaultHeader("Accept", "*/*");
        Unirest.config().addDefaultHeader("Accept-Encoding", "gzip, deflate, br");
    }

    protected String getFormattedDateString(Date date, SimpleDateFormat formatter) {
        return date != null ? formatter.format(date) : "";
    }

    protected AbsenceRvdRequestDataJson getAbsenceRvdRequestDataJson(AbsenceRvdRequest entity, Boolean isRvd) {
        AbsenceRvdRequestDataJson absenceRvdRequestDataJson = new AbsenceRvdRequestDataJson();
        String personId = (entity.getPersonGroup() != null && entity.getPersonGroup().getLegacyId() != null) ? entity.getPersonGroup().getLegacyId() : "";
        absenceRvdRequestDataJson.setPersonId(personId);
        String requestNumber = (entity.getRequestNumber() != null) ? entity.getRequestNumber().toString() : "";
        absenceRvdRequestDataJson.setRequestNumber(requestNumber);
        String requestDate = getFormattedDateString(entity.getRequestDate(), dateFormatter);
        absenceRvdRequestDataJson.setRequestDate(requestDate);
        String purpose = (entity.getPurpose() != null && entity.getPurpose().getLangValue() != null) ? entity.getPurpose().getLangValue() : entity.getPurposeText();
        purpose = Null.nullReplace(purpose, "");
        absenceRvdRequestDataJson.setPurpose(purpose);
        String startDate = getFormattedDateString(entity.getTimeOfStarting(), dateFormatter);
        absenceRvdRequestDataJson.setStartDate(startDate);
        String endDate = getFormattedDateString(entity.getTimeOfFinishing(), dateFormatter);
        absenceRvdRequestDataJson.setEndDate(endDate);
        String hours = (entity.getTotalHours() != null) ? entity.getTotalHours().toString() : "";
        absenceRvdRequestDataJson.setHours(hours);
        if (isRvd) {
            boolean compensation = Null.nullReplace(entity.getCompensation(), false);
            String isVacationProvidedOrCompensationProvided = (compensation) ? "Compensation" : "Vacation";
            absenceRvdRequestDataJson.setIsVacationProvidedOrCompensationProvided(isVacationProvidedOrCompensationProvided);
        }
        boolean isEmployeeAgree = Null.nullReplace(entity.getAgree(), false);
        absenceRvdRequestDataJson.setEmployeeAgree(isEmployeeAgree);
        boolean isEmployeeInformed = Null.nullReplace(entity.getAcquainted(), false);
        absenceRvdRequestDataJson.setEmployeeInformed(isEmployeeInformed);
        absenceRvdRequestDataJson.setShiftCode(entity.getShiftCode());
        String startTime = getFormattedDateString(entity.getTimeOfStarting(), timeFormatter);
        absenceRvdRequestDataJson.setStartTime(startTime);
        String endTime = getFormattedDateString(entity.getTimeOfFinishing(), timeFormatter);
        absenceRvdRequestDataJson.setEndTime(endTime);
        absenceRvdRequestDataJson.setShift(entity.getShift() != null ? entity.getShift().getCode() : "");
        absenceRvdRequestDataJson.setShift(entity.getOverrideAllHoursByDay() != null
                ? entity.getOverrideAllHoursByDay().getId() : "");
        String companyCode = "";
        if (entity.getPersonGroup() != null && entity.getPersonGroup().getCompany() != null) {
            DicCompany company = entity.getPersonGroup().getCompany();
            companyCode = company != null && company.getCode() != null
                    ? company.getCode()
                    : "";
        }
        absenceRvdRequestDataJson.setCompanyCode(companyCode);

        return absenceRvdRequestDataJson;
    }
}