package kz.uco.tsadv.listeners;

import com.google.gson.Gson;
import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.View;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.tsadv.api.BaseResult;
import kz.uco.tsadv.config.IntegrationConfig;
import kz.uco.tsadv.modules.integration.jsonobject.PersonalDataRequestDataJson;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.enums.YesNoEnum;
import kz.uco.tsadv.modules.personal.model.PersonalDataRequest;
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

@Component("tsadv_PersonalDataRequestChangedListener")
public class PersonalDataRequestChangedListener {

    @Inject
    protected DataManager dataManager;
    protected String APPROVED_STATUS = "APPROVED";
    protected SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Inject
    protected IntegrationRestService integrationRestService;
    @Inject
    protected IntegrationConfig integrationConfig;


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit(EntityChangedEvent<PersonalDataRequest, UUID> event) {
        if (event.getChanges().getAttributes().stream().anyMatch(s -> "status".equals(s))) {
            String methodName = "PersonalDataRequestChangedListener.afterCommit";
            BaseResult baseResult = new BaseResult();
            PersonalDataRequestDataJson personalDataRequestDataJson = new PersonalDataRequestDataJson();
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
                PersonalDataRequest personalDataRequest = dataManager.load(event.getEntityId())
                        .view("personalDataRequest-for-integration").one();
                DicRequestStatus requestStatus = personalDataRequest.getStatus();
                if (APPROVED_STATUS.equals(requestStatus.getCode()) && !APPROVED_STATUS.equals(oldStatus != null
                        ? oldStatus.getCode() : "") && !integrationConfig.getPersonalDataRequestOff()) {
                    personalDataRequestDataJson.setPersonId(personalDataRequest.getPersonGroup().getLegacyId());
                    personalDataRequestDataJson.setRequestNumber(personalDataRequest.getRequestNumber().toString());
                    personalDataRequestDataJson.setLastName(personalDataRequest.getLastName());
                    personalDataRequestDataJson.setFirstName(personalDataRequest.getFirstName());
                    personalDataRequestDataJson.setMiddleName(personalDataRequest.getMiddleName());
                    personalDataRequestDataJson.setLastNameLatin(personalDataRequest.getLastNameLatin());
                    personalDataRequestDataJson.setFirstNameLatin(personalDataRequest.getFirstNameLatin());
                    personalDataRequestDataJson.setMaritalStatus(personalDataRequest.getMaritalStatus() != null
                            ? personalDataRequest.getMaritalStatus().getLegacyId() : "");
                    personalDataRequestDataJson.setHasLoanFromPrevEmployer(personalDataRequest.getPersonGroup()
                            .getPerson().getCommitmentsLoan() != null ? personalDataRequest.getPersonGroup()
                            .getPerson().getCommitmentsLoan() : false);
                    personalDataRequestDataJson.setHasCreditFromPrevEmployer(personalDataRequest.getPersonGroup()
                            .getPerson().getCommitmentsCredit() != null ? personalDataRequest.getPersonGroup()
                            .getPerson().getCommitmentsCredit() : false);
                    personalDataRequestDataJson.setHasWealthFromPrevEmployer(personalDataRequest.getPersonGroup()
                            .getPerson().getCommitmentsNotSurMatValues() != null
                            ? personalDataRequest.getPersonGroup().getPerson().getCommitmentsNotSurMatValues() : false);
                    personalDataRequestDataJson.setHasNdaFromPrevEmployer(personalDataRequest.getPersonGroup()
                            .getPerson().getHaveNDA() != null && YesNoEnum.YES.equals(personalDataRequest.getPersonGroup()
                            .getPerson().getHaveNDA()) ? true : false);
                    personalDataRequestDataJson.setHasCriminalRecord(personalDataRequest.getPersonGroup()
                            .getPerson().getHaveConviction() != null && YesNoEnum.YES.equals(personalDataRequest.getPersonGroup()
                            .getPerson().getHaveConviction()) ? true : false);
                    personalDataRequestDataJson.setEffectiveDate(getFormattedDateString(BaseCommonUtils.getSystemDate()));
                    personalDataRequestDataJson.setCompanyCode(personalDataRequest.getPersonGroup() != null
                            && personalDataRequest.getPersonGroup().getCompany() != null
                            ? personalDataRequest.getPersonGroup().getCompany().getLegacyId()
                            : "");

                    setupUnirest();

                    Gson gson = new Gson();
                    HttpResponse<String> response = Unirest
                            .post(getApiUrl())
                            .body(String.format("[%s]", gson.toJson(personalDataRequestDataJson)))
                            .asString();

                    String responseBody = response.getBody();
                    if (responseBody.contains("\"success\":\"true\"")) {
                        integrationRestService.prepareSuccess(baseResult,
                                methodName,
                                personalDataRequestDataJson);
                    } else {
                        integrationRestService.prepareError(baseResult,
                                methodName,
                                personalDataRequestDataJson,
                                responseBody);
                    }
                }
            } catch (Exception e) {
                String stackTrace = Arrays.stream(e.getStackTrace()).map(stackTraceElement ->
                        stackTraceElement.toString()).collect(Collectors.joining());
                e.printStackTrace();
                integrationRestService.prepareError(baseResult,
                        methodName,
                        personalDataRequestDataJson,
                        stackTrace);
            }

        }
    }

    protected String getApiUrl() {
        return integrationConfig.getPersonalDataRequestUrl();
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