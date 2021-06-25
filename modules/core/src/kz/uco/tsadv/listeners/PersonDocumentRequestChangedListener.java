package kz.uco.tsadv.listeners;

import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.View;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kz.uco.tsadv.api.BaseResult;
import kz.uco.tsadv.modules.integration.jsonobject.PersonDocumentRequestDataJson;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.model.PersonDocumentRequest;
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

@Component("tsadv_PersonDocumentRequestChangedListener")
public class PersonDocumentRequestChangedListener {
    @Inject
    protected DataManager dataManager;
    protected String APPROVED_STATUS = "APPROVED";
    protected SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    @Inject
    protected IntegrationRestService integrationRestService;
    protected String personinfoRequestUrl = "http://10.2.200.101:8290/api/ahruco/document/request";

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit(EntityChangedEvent<PersonDocumentRequest, UUID> event) {
        if (event.getChanges().getAttributes().stream().anyMatch("status"::equals)) {
            String methodName = "PersonalDataRequestChangedListener.afterCommit";
            BaseResult baseResult = new BaseResult();
            PersonDocumentRequestDataJson personDocumentRequestDataJson = new PersonDocumentRequestDataJson();
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
                PersonDocumentRequest personDocumentRequest = dataManager.load(event.getEntityId())
                        .view("personDocumentRequest-for-integration").one();
                DicRequestStatus requestStatus = personDocumentRequest.getStatus();
                if (APPROVED_STATUS.equals(requestStatus.getCode()) && !APPROVED_STATUS.equals(oldStatus != null
                        ? oldStatus.getCode() : "")) {
                    personDocumentRequestDataJson.setPersonId(personDocumentRequest.getPersonGroup().getLegacyId());
                    personDocumentRequestDataJson.setRequestNumber(personDocumentRequest.getRequestNumber().toString());
                    personDocumentRequestDataJson.setLegacyId(personDocumentRequest.getEditedPersonDocument() != null
                            ? personDocumentRequest.getEditedPersonDocument().getLegacyId() : "");
                    personDocumentRequestDataJson.setDocumentTypeId(personDocumentRequest.getDocumentType() != null
                            ? personDocumentRequest.getDocumentType().getLegacyId() : "");
                    personDocumentRequestDataJson.setIssuedBy(personDocumentRequest.getIssuedBy());
                    personDocumentRequestDataJson.setDocumentNumber(personDocumentRequest.getDocumentNumber());
                    personDocumentRequestDataJson.setIssueDate(getFormattedDateString(personDocumentRequest.getIssueDate()));
                    personDocumentRequestDataJson.setExpiredDate(getFormattedDateString(personDocumentRequest.getExpiredDate()));
                    personDocumentRequestDataJson.setIssueAuthorityId(personDocumentRequest.getIssuingAuthority() != null
                            ? personDocumentRequest.getIssuingAuthority().getLegacyId()
                            : personDocumentRequest.getIssuedBy());
                    personDocumentRequestDataJson.setCompanyCode(personDocumentRequest.getPersonGroup() != null
                            && personDocumentRequest.getPersonGroup().getCompany() != null
                            ? personDocumentRequest.getPersonGroup().getCompany().getLegacyId()
                            : "");
                    setupUnirest();
                    HttpResponse<String> response = Unirest
                            .post(personinfoRequestUrl)
                            .body(personDocumentRequestDataJson)
                            .asString();

                    String responseBody = response.getBody();
                    if (responseBody.contains("\"success\":\"true\"")) {
                        integrationRestService.prepareSuccess(baseResult,
                                methodName,
                                personDocumentRequestDataJson);
                    } else {
                        integrationRestService.prepareError(baseResult,
                                methodName,
                                personDocumentRequestDataJson,
                                responseBody);
                    }
                }
            } catch (Exception e) {
                String stackTrace = Arrays.stream(e.getStackTrace()).map(stackTraceElement ->
                        stackTraceElement.toString()).collect(Collectors.joining());
                e.printStackTrace();
                integrationRestService.prepareError(baseResult,
                        methodName,
                        personDocumentRequestDataJson,
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

    protected String getFormattedDateString(Date date) {
        return date != null ? formatter.format(date) : "";
    }
}