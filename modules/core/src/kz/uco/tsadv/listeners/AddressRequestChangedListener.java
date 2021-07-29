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
import kz.uco.tsadv.modules.integration.jsonobject.AddressRequestJson;
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
import java.util.ArrayList;
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
            ArrayList<AddressRequestJson> addressRequestJsons = new ArrayList<>();
            AddressRequestJson addressRequestJson = new AddressRequestJson();
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
                    addressRequestJson.setPersonId(addressRequest.getPersonGroup().getLegacyId());
                    addressRequestJson.setRequestNumber(addressRequest.getRequestNumber().toString());
                    addressRequestJson.setLegacyId(addressRequest.getBaseAddress() != null
                            ? addressRequest.getBaseAddress().getLegacyId() : "");
                    addressRequestJson.setAddressType(addressRequest.getAddressType() != null
                            ? addressRequest.getAddressType().getCode() : "");
                    addressRequestJson.setPostal(addressRequest.getPostalCode());
                    addressRequestJson.setCountryId(addressRequest.getCountry() != null
                            ? addressRequest.getCountry().getLegacyId() : "");
                    addressRequestJson.setAddressKATOCode(addressRequest.getKato() != null
                            ? addressRequest.getKato().getCode() : "");
                    addressRequestJson.setStreetTypeId(addressRequest.getStreetType() != null
                            ? addressRequest.getStreetType().getLegacyId() : "");
                    addressRequestJson.setStreetName(addressRequest.getStreetName());
                    addressRequestJson.setBuilding(addressRequest.getBuilding());
                    addressRequestJson.setBlock(addressRequest.getBlock());
                    addressRequestJson.setFlat(addressRequest.getFlat());
                    addressRequestJson.setAddressForExpats(addressRequest.getAddressForExpats());
                    addressRequestJson.setAddressKazakh(addressRequest.getAddressKazakh());
                    addressRequestJson.setAddressEnglish(addressRequest.getAddressEnglish());
                    addressRequestJson.setStartDate(getFormattedDateString(addressRequest.getStartDate()));
                    addressRequestJson.setEndDate(getFormattedDateString(addressRequest.getEndDate()));
                    addressRequestJson.setCompanyCode(addressRequest.getPersonGroup() != null
                            && addressRequest.getPersonGroup().getCompany() != null
                            ? addressRequest.getPersonGroup().getCompany().getLegacyId()
                            : "");
                    addressRequestJsons.add(addressRequestJson);
                    addressRequestDataJson.setAddressRequestJsons(addressRequestJsons);
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