package kz.uco.tsadv.listeners;

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
import kz.uco.tsadv.modules.integration.jsonobject.PersonalDataRequestJson;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.enums.YesNoEnum;
import kz.uco.tsadv.modules.personal.model.PersonalDataRequest;
import kz.uco.tsadv.service.IntegrationRestService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
            ArrayList<PersonalDataRequestJson> personalDataRequestJsons = new ArrayList<>();
            PersonalDataRequestJson personalDataRequestJson = new PersonalDataRequestJson();
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
                    personalDataRequestJson.setPersonId(personalDataRequest.getPersonGroup().getLegacyId());
                    personalDataRequestJson.setRequestNumber(personalDataRequest.getRequestNumber().toString());
                    personalDataRequestJson.setLastName(personalDataRequest.getLastName());
                    personalDataRequestJson.setFirstName(personalDataRequest.getFirstName());
                    personalDataRequestJson.setMiddleName(personalDataRequest.getMiddleName());
                    personalDataRequestJson.setLastNameLatin(personalDataRequest.getLastNameLatin());
                    personalDataRequestJson.setFirstNameLatin(personalDataRequest.getFirstNameLatin());
                    personalDataRequestJson.setMaritalStatus(personalDataRequest.getMaritalStatus() != null
                            ? personalDataRequest.getMaritalStatus().getLegacyId() : "");
                    personalDataRequestJson.setHasLoanFromPrevEmployer(personalDataRequest.getPersonGroup()
                            .getPerson().getCommitmentsLoan() != null ? personalDataRequest.getPersonGroup()
                            .getPerson().getCommitmentsLoan() : false);
                    personalDataRequestJson.setHasCreditFromPrevEmployer(personalDataRequest.getPersonGroup()
                            .getPerson().getCommitmentsCredit() != null ? personalDataRequest.getPersonGroup()
                            .getPerson().getCommitmentsCredit() : false);
                    personalDataRequestJson.setHasWealthFromPrevEmployer(personalDataRequest.getPersonGroup()
                            .getPerson().getCommitmentsNotSurMatValues() != null
                            ? personalDataRequest.getPersonGroup().getPerson().getCommitmentsNotSurMatValues() : false);
                    personalDataRequestJson.setHasNdaFromPrevEmployer(personalDataRequest.getPersonGroup()
                            .getPerson().getHaveNDA() != null && YesNoEnum.YES.equals(personalDataRequest.getPersonGroup()
                            .getPerson().getHaveNDA()) ? true : false);
                    personalDataRequestJson.setHasCriminalRecord(personalDataRequest.getPersonGroup()
                            .getPerson().getHaveConviction() != null && YesNoEnum.YES.equals(personalDataRequest.getPersonGroup()
                            .getPerson().getHaveConviction()) ? true : false);
                    personalDataRequestJson.setEffectiveDate(getFormattedDateString(BaseCommonUtils.getSystemDate()));
                    personalDataRequestJson.setCompanyCode(personalDataRequest.getPersonGroup() != null
                            && personalDataRequest.getPersonGroup().getCompany() != null
                            ? personalDataRequest.getPersonGroup().getCompany().getLegacyId()
                            : "");
                    personalDataRequestJsons.add(personalDataRequestJson);
                    personalDataRequestDataJson.setPersonalDataRequestJsons(personalDataRequestJsons);
                    setupUnirest();
                    HttpResponse<String> response = Unirest
                            .post(getApiUrl())
                            .body(personalDataRequestDataJson)
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