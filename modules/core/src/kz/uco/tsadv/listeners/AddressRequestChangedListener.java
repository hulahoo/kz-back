package kz.uco.tsadv.listeners;

import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.View;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kz.uco.tsadv.api.BaseResult;
import kz.uco.tsadv.config.IntegrationConfig;
import kz.uco.tsadv.modules.integration.jsonobject.AddressRequestDataJson;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.model.AddressRequest;
import kz.uco.tsadv.service.IntegrationRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Component("tsadv_AddressRequestChangedListener")
public class AddressRequestChangedListener {

    protected static final Logger log = LoggerFactory.getLogger(AddressRequestChangedListener.class);
    @Inject
    protected DataManager dataManager;
    @Inject
    protected IntegrationRestService integrationRestService;
    protected String APPROVED_STATUS = "APPROVED";
    protected SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    @Inject
    protected IntegrationConfig integrationConfig;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit(EntityChangedEvent<AddressRequest, UUID> event) {

        if (event.getChanges().getAttributes().stream().anyMatch("status"::equals)) {
            String methodName = "AddressRequestChangedListener.afterCommit";
            BaseResult baseResult = new BaseResult();
            AddressRequestDataJson addressRequestDataJson = new AddressRequestDataJson();
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
                AddressRequest addressRequest = dataManager.load(event.getEntityId())
                        .view("addressRequest-view").one();
                DicRequestStatus requestStatus = addressRequest.getStatus();
                if (APPROVED_STATUS.equals(requestStatus.getCode()) && !APPROVED_STATUS.equals(oldStatus != null
                        ? oldStatus.getCode() : "") && !integrationConfig.getAddressRequestOff()) {
                    addressRequestDataJson.setPersonId(addressRequest.getPersonGroup().getLegacyId());
                    addressRequestDataJson.setRequestNumber(addressRequest.getRequestNumber().toString());
                    addressRequestDataJson.setLegacyId(addressRequest.getBaseAddress() != null
                            ? addressRequest.getBaseAddress().getLegacyId() : "");
                    addressRequestDataJson.setAddressType(addressRequest.getAddressType() != null
                            ? addressRequest.getAddressType().getCode() : "");
                    addressRequestDataJson.setPostal(addressRequest.getPostalCode());
                    addressRequestDataJson.setCountryId(addressRequest.getCountry() != null
                            ? addressRequest.getCountry().getLegacyId() : "");
                    addressRequestDataJson.setAddressKATOCode(addressRequest.getKato().getCode());
                    addressRequestDataJson.setStreetTypeId(addressRequest.getStreetType() != null
                            ? addressRequest.getStreetType().getLegacyId() : "");
                    addressRequestDataJson.setStreetName(addressRequest.getStreetName());
                    addressRequestDataJson.setBuilding(addressRequest.getBuilding());
                    addressRequestDataJson.setBlock(addressRequest.getBlock());
                    addressRequestDataJson.setFlat(addressRequest.getFlat());
                    addressRequestDataJson.setAddressForExpats(addressRequest.getAddressForExpats());
                    addressRequestDataJson.setAddressKazakh(addressRequest.getAddressKazakh());
                    addressRequestDataJson.setAddressEnglish(addressRequest.getAddressEnglish());
                    addressRequestDataJson.setStartDate(getFormattedDateString(addressRequest.getStartDate()));
                    addressRequestDataJson.setEndDate(getFormattedDateString(addressRequest.getEndDate()));
                    addressRequestDataJson.setCompanyCode(addressRequest.getPersonGroup() != null
                            && addressRequest.getPersonGroup().getCompany() != null
                            ? addressRequest.getPersonGroup().getCompany().getLegacyId()
                            : "");
                    setupUnirest();
                    HttpResponse<String> response = Unirest
                            .post(getApiUrl())
                            .body(addressRequestDataJson)
                            .asString();

                    String responseBody = response.getBody();
                    if (responseBody.contains("\"success\":\"true\"")) {
                        integrationRestService.prepareSuccess(baseResult,
                                methodName,
                                addressRequestDataJson);
                    } else {
                        integrationRestService.prepareError(baseResult,
                                methodName,
                                addressRequestDataJson,
                                responseBody);
                    }
                }
            } catch (Exception e) {
                String stackTrace = Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.joining());
                log.error("Error", e);
                integrationRestService.prepareError(baseResult,
                        methodName,
                        addressRequestDataJson,
                        stackTrace);
            }

        }
    }

    protected String getApiUrl() {
        return integrationConfig.getAddressRequestUrl();
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