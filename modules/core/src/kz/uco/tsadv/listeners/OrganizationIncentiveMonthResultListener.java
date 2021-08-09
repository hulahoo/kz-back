package kz.uco.tsadv.listeners;

import com.haulmont.cuba.core.TransactionalDataManager;
import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import com.haulmont.cuba.core.global.DataManager;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kz.uco.tsadv.api.BaseResult;
import kz.uco.tsadv.api.Null;
import kz.uco.tsadv.config.IntegrationConfig;
import kz.uco.tsadv.entity.dbview.OrganizationIncentiveMonthResultShortView;
import kz.uco.tsadv.modules.integration.jsonobject.OrganizationIncentiveMonthResultJson;
import kz.uco.tsadv.modules.personal.model.OrganizationIncentiveMonthResult;
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
        if(event.getType() == EntityChangedEvent.Type.CREATED || event.getType() == EntityChangedEvent.Type.UPDATED){

            String methodName = "tsadv_OrganizationIncentiveResultListener.OnBefore" + event.getType();
            OrganizationIncentiveMonthResult incentiveMonthResult = loadEventEntity(event);
            if(isEntityApproved(incentiveMonthResult)){
                List<OrganizationIncentiveMonthResultShortView> incentiveMonthResultShortViews = transactionalDataManager
                        .load(OrganizationIncentiveMonthResultShortView.class)
                        .query("select e from tsadv_OrganizationIncentiveMonthResultShortView e where e.incentiveMonthResult = :incentiveMonthResul")
                        .parameter("incentiveMonthResul",incentiveMonthResult)
                        .list();


                List<OrganizationIncentiveMonthResultJson> incentiveMonthResultJsons = new ArrayList<>();
                for(OrganizationIncentiveMonthResultShortView incentiveMonthResultShortView : incentiveMonthResultShortViews){
                    OrganizationIncentiveMonthResultJson incentiveMonthResultJson = getOrganizationIncentiveMonthResultJson(incentiveMonthResultShortView);
                    incentiveMonthResultJsons.add(incentiveMonthResultJson);
                }

                sendRequest(incentiveMonthResultJsons,methodName);
            }

        }

    }

    protected OrganizationIncentiveMonthResult loadEventEntity(EntityChangedEvent<OrganizationIncentiveMonthResult, UUID> event){
        return transactionalDataManager
                .load(event.getEntityId())
                .view("organizationIncentiveMonthResult.integration")
                .one();
    }

    protected boolean isEntityApproved(OrganizationIncentiveMonthResult incentiveMonthResult){
        return incentiveMonthResult.getStatus() != null && "APPROVED".equals(incentiveMonthResult.getStatus().getCode());
    }

    protected OrganizationIncentiveMonthResultJson getOrganizationIncentiveMonthResultJson(OrganizationIncentiveMonthResultShortView incentiveMonthResult){
        OrganizationIncentiveMonthResultJson organizationIncentiveMonthResultJson = new OrganizationIncentiveMonthResultJson();
        incentiveMonthResult = dataManager.reload(incentiveMonthResult,"organizationIncentiveMonthResultShortView.integration");
        String organizationId = Null.nullReplace(incentiveMonthResult.getDepartment().getLegacyId(),"");
        organizationIncentiveMonthResultJson.setOrganizationId(organizationId);
        String period = formatter.format(incentiveMonthResult.getPeriod());
        organizationIncentiveMonthResultJson.setPeriod(period);
        String incentiveType = Null.nullReplace(incentiveMonthResult.getIndicator().getType().getCode(),"");
        organizationIncentiveMonthResultJson.setIncentiveType(incentiveType);
        String result = String.valueOf(incentiveMonthResult.getResult());
        organizationIncentiveMonthResultJson.setResult(result);
        String companyCode = Null.nullReplace(incentiveMonthResult.getCompany().getLegacyId(),"");
        organizationIncentiveMonthResultJson.setCompanyCode(companyCode);

        return organizationIncentiveMonthResultJson;
    }

    protected void sendRequest(List<OrganizationIncentiveMonthResultJson> organizationIncentiveMonthResultJsons,String methodName){

        for(OrganizationIncentiveMonthResultJson incentiveMonthResultJson : organizationIncentiveMonthResultJsons) {
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
                throw new RuntimeException(exceptionMessage);
            }
        }
    }

    protected HttpResponse<String> doPost(OrganizationIncentiveMonthResultJson organizationIncentiveMonthResultJson){
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "*/*");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        HttpResponse<String> response = Unirest
                .post(integrationConfig.getOrganizationIncentiveResultIntegrationUrl())
                .basicAuth(integrationConfig.getBasicAuthLogin(),integrationConfig.getBasicAuthPassword())
                .headers(headers)
                .body(organizationIncentiveMonthResultJson)
                .asString();

        return response;
    }
}