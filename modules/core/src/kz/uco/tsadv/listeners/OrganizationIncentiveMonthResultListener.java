package kz.uco.tsadv.listeners;

import com.haulmont.cuba.core.TransactionalDataManager;
import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import com.haulmont.cuba.core.global.DataManager;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kz.uco.tsadv.api.BaseResult;
import kz.uco.tsadv.api.Null;
import kz.uco.tsadv.config.IntegrationConfig;
import kz.uco.tsadv.modules.integration.jsonobject.OrganizationIncentiveMonthResultJson;
import kz.uco.tsadv.modules.personal.model.OrganizationIncentiveMonthResult;
import kz.uco.tsadv.modules.personal.model.OrganizationIncentiveResult;
import kz.uco.tsadv.service.IntegrationRestService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

@Component("tsadv_OrganizationIncentiveMonthResultListener")
public class OrganizationIncentiveMonthResultListener {

    protected SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Inject
    private TransactionalDataManager transactionalDataManager;
    @Inject
    private DataManager dataManager;
    @Inject
    private IntegrationConfig integrationConfig;
    @Inject
    private IntegrationRestService integrationRestService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void beforeCommit(EntityChangedEvent<OrganizationIncentiveMonthResult, UUID> event) {
        if (event.getType() == EntityChangedEvent.Type.CREATED || event.getType() == EntityChangedEvent.Type.UPDATED) {

            String methodName = "tsadv_OrganizationIncentiveResultListener.OnBefore" + event.getType();
            OrganizationIncentiveMonthResult incentiveMonthResult = loadEventEntity(event);
            if (isEntityApproved(incentiveMonthResult)) {

                incentiveMonthResult = dataManager.reload(incentiveMonthResult, "organizationIncentiveMonthResult.integration.full");

                List<OrganizationIncentiveMonthResultJson> incentiveMonthResultJsons = new ArrayList<>();

                List<OrganizationIncentiveResult> incentiveResults = incentiveMonthResult.getIncentiveResults();

                incentiveMonthResult.getIncentiveResults()
                        .stream()
                        .map(this::parseToOrganizationIncentiveMonthResultJson)
                        .peek(resultJson -> resultJson.setResult(
                                String.valueOf(incentiveResults.stream()
                                        .filter(result -> Objects.equals(result.getIndicator().getCode(), resultJson.getIncentiveType()))
                                        .mapToDouble(OrganizationIncentiveResult::getScore)
                                        .sum())
                        ))
                        .forEach(incentiveMonthResultJsons::add);

                sendRequest(incentiveMonthResultJsons, methodName);
            }

        }
    }

    protected OrganizationIncentiveMonthResultJson parseToOrganizationIncentiveMonthResultJson(OrganizationIncentiveResult organizationIncentiveResult) {
        OrganizationIncentiveMonthResultJson organizationIncentiveMonthResultJson = new OrganizationIncentiveMonthResultJson();
        String organizationId = Null.nullReplace(organizationIncentiveResult.getOrganizationGroup().getLegacyId(), "");
        organizationIncentiveMonthResultJson.setOrganizationId(organizationId);
        String period = formatter.format(organizationIncentiveResult.getPeriodDate());
        organizationIncentiveMonthResultJson.setPeriod(period);
        String incentiveType = Null.nullReplace(organizationIncentiveResult.getIndicator().getType().getCode(), "");
        organizationIncentiveMonthResultJson.setIncentiveType(incentiveType);
        String companyCode = Null.nullReplace(organizationIncentiveResult.getOrganizationGroup().getCompany().getLegacyId(), "");
        organizationIncentiveMonthResultJson.setCompanyCode(companyCode);
        return organizationIncentiveMonthResultJson;
    }

    protected OrganizationIncentiveMonthResult loadEventEntity(EntityChangedEvent<OrganizationIncentiveMonthResult, UUID> event) {
        return transactionalDataManager
                .load(event.getEntityId())
                .view("organizationIncentiveMonthResult.integration")
                .one();
    }

    protected boolean isEntityApproved(OrganizationIncentiveMonthResult incentiveMonthResult) {
        return incentiveMonthResult.getStatus() != null && "APPROVED".equals(incentiveMonthResult.getStatus().getCode());
    }

    protected void sendRequest(List<OrganizationIncentiveMonthResultJson> organizationIncentiveMonthResultJsons, String methodName) {

        for (OrganizationIncentiveMonthResultJson incentiveMonthResultJson : organizationIncentiveMonthResultJsons) {
            BaseResult baseResult = new BaseResult();
            String responseBody = null;
            try {
                HttpResponse<String> response = doPost(incentiveMonthResultJson);
                responseBody = response.getBody();
                if (responseBody.contains("\"success\":\"true\"")) {
                    integrationRestService.prepareSuccess(
                            baseResult,
                            methodName,
                            incentiveMonthResultJson
                    );
                } else {
                    integrationRestService.prepareError(
                            baseResult,
                            methodName,
                            incentiveMonthResultJson,
                            responseBody
                    );
                }
            } catch (Exception e) {
                String exceptionMessage = "Failed " + methodName + "\n " + "Detail message: \n" + e.getMessage() + "\n StackTrace: \n" + Arrays.toString(e.getStackTrace());
                String logErrorMessage = exceptionMessage + "\nresponceBody: \n" + responseBody;
                integrationRestService.prepareError(baseResult,
                        methodName,
                        incentiveMonthResultJson,
                        logErrorMessage);
                //todo switch to UnirestIntegrationException
                throw new RuntimeException(exceptionMessage, e);
            }
        }
    }

    protected HttpResponse<String> doPost(OrganizationIncentiveMonthResultJson organizationIncentiveMonthResultJson) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "*/*");
        headers.put("Accept-Encoding", "gzip, deflate, br");

        return Unirest
                .post(integrationConfig.getOrganizationIncentiveResultIntegrationUrl())
                .basicAuth(integrationConfig.getBasicAuthLogin(), integrationConfig.getBasicAuthPassword())
                .headers(headers)
                .body(organizationIncentiveMonthResultJson)
                .asString();
    }
}