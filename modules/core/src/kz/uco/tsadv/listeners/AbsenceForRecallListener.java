package kz.uco.tsadv.listeners;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kz.uco.tsadv.api.BaseResult;
import kz.uco.tsadv.modules.integration.jsonobject.AbsenceForRecallDataJson;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.model.AbsenceForRecall;
import kz.uco.tsadv.service.DatesService;
import kz.uco.tsadv.service.IntegrationRestService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component("tsadv_AbsenceForRecallListener")
public class AbsenceForRecallListener implements BeforeUpdateEntityListener<AbsenceForRecall>, BeforeInsertEntityListener<AbsenceForRecall> {

    private final String APPROVED_STATUS = "APPROVED";
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Inject
    IntegrationRestService integrationRestService;

    @Inject
    protected DatesService datesService;


    @Override
    public void onBeforeInsert(AbsenceForRecall entity, EntityManager entityManager) {
        if(isApproved(entity,entityManager)){
            AbsenceForRecallDataJson absenceForRecallJson = getAbsenceForRecallDataJson(entity);

            setupUnirest();
            HttpResponse<String> response = Unirest
                    .post("http://10.2.200.101:8290/api/ahruco/absence/recall/request")
                    .body(absenceForRecallJson)
                    .asString();

            BaseResult baseResult = new BaseResult();
            String methodName = "tsadv_AbsenceForRecallListener.onBeforeInsert";
            String responseBody = response.getBody();
            if (responseBody.contains("\"success\":\"true\"")) {
                integrationRestService.prepareSuccess(baseResult,
                        methodName,
                        absenceForRecallJson);
            } else {
                integrationRestService.prepareError(baseResult,
                        methodName,
                        absenceForRecallJson,
                        responseBody);
            }
        }
    }

    @Override
    public void onBeforeUpdate(AbsenceForRecall entity, EntityManager entityManager) {
        if(isApproved(entity,entityManager)){
            AbsenceForRecallDataJson absenceForRecallJson = getAbsenceForRecallDataJson(entity);

            setupUnirest();
            HttpResponse<String> response = Unirest
                    .post("http://10.2.200.101:8290/api/ahruco/absence/recall/request")
                    .body(absenceForRecallJson)
                    .asString();

            BaseResult baseResult = new BaseResult();
            String methodName = "tsadv_AbsenceForRecallListener.onBeforeUpdate";
            String responseBody = response.getBody();
            if (responseBody.contains("\"success\":\"true\"")) {
                integrationRestService.prepareSuccess(baseResult,
                        methodName,
                        absenceForRecallJson);
            } else {
                integrationRestService.prepareError(baseResult,
                        methodName,
                        absenceForRecallJson,
                        responseBody);
            }
        }
    }

    protected boolean isApproved(AbsenceForRecall entity, EntityManager entityManager) {
        DicRequestStatus status = entity.getStatus();
        if (status == null) return false;
        return APPROVED_STATUS.equals(entityManager.reloadNN(status, View.LOCAL).getCode());
    }

    protected AbsenceForRecallDataJson getAbsenceForRecallDataJson(AbsenceForRecall entity) {
            AbsenceForRecallDataJson absenceForRecallJson = new AbsenceForRecallDataJson();
            String personId = (entity.getEmployee() != null && entity.getEmployee().getLegacyId() != null) ? entity.getEmployee().getLegacyId() : "";
            absenceForRecallJson.setPersonId(personId);
            absenceForRecallJson.setRequestNumber(entity.getRequestNumber().toString());
            absenceForRecallJson.setRequestDate(getFormattedDateString(entity.getRequestDate()));
            String parentAbsenceLegacyId = (entity.getVacation() != null && entity.getVacation().getLegacyId() != null) ? entity.getVacation().getLegacyId() : "";
            absenceForRecallJson.setParentAbsenceLegacyId(parentAbsenceLegacyId);
            absenceForRecallJson.setStartDate(getFormattedDateString(entity.getRecallDateFrom()));
            absenceForRecallJson.setEndDate(getFormattedDateString(entity.getRecallDateTo()));
            absenceForRecallJson.setHasAbsenceAtAnotherPeriod(wrapBoolean(entity.getLeaveOtherTime()));
            absenceForRecallJson.setHasCompensation(wrapBoolean(entity.getCompensationPayment()));
            absenceForRecallJson.setNewStartDate(getFormattedDateString(entity.getDateFrom()));
            absenceForRecallJson.setNewEndDate(getFormattedDateString(entity.getDateTo()));
            String purpose = (entity.getPurpose() != null && entity.getPurpose().getLangValue() != null) ? entity.getPurpose().getLangValue() : entity.getPurposeText();
            purpose = (purpose != null) ? purpose : "";
            absenceForRecallJson.setPurpose(purpose);
            absenceForRecallJson.setEmployeeAgree(wrapBoolean(entity.getIsAgree()));
            absenceForRecallJson.setEmployeeInformed(wrapBoolean(entity.getIsFamiliarization()));
            if(entity.getRecallDateFrom() != null && entity.getRecallDateTo() != null) {
                int recallDaysMain = datesService.getFullDaysCount(entity.getRecallDateFrom(), entity.getRecallDateTo());
                absenceForRecallJson.setRecallDaysMain(recallDaysMain);
            }
            String companyCode = (entity.getEmployee() != null
                    && entity.getEmployee().getCompany() != null
                    && entity.getEmployee().getCompany().getLegacyId() != null) ? entity.getEmployee().getCompany().getLegacyId() : "";
            absenceForRecallJson.setCompanyCode(companyCode);

            return absenceForRecallJson;
    }

    protected String getFormattedDateString(Date date){
        return date != null ? formatter.format(date) : "" ;
    }

    protected void setupUnirest(){
        Unirest.config().setDefaultBasicAuth("ahruco", "ahruco");
        Unirest.config().addDefaultHeader("Content-Type", "application/json");
        Unirest.config().addDefaultHeader("Accept", "*/*");
        Unirest.config().addDefaultHeader("Accept-Encoding", "gzip, deflate, br");
    }

    protected boolean wrapBoolean(Boolean bool){
        if(bool == null) return false;
        return bool;
    }

}