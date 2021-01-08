package kz.uco.tsadv.listeners;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.reports.ReportingApi;
import com.haulmont.reports.entity.Report;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.personal.dictionary.DicReceivingType;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.model.CertificateRequest;
import kz.uco.tsadv.service.EmployeeService;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.inject.Inject;
import java.util.UUID;

@Component("tsadv_CertificateRequestChangedListener")
public class CertificateRequestChangedListener {
    @Inject
    protected DataManager dataManager;
    @Inject
    protected CommonService commonService;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected ReportingApi reportingApi;
    @Inject
    protected Logger log;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit(EntityChangedEvent<CertificateRequest, UUID> event) {
        CertificateRequest request = dataManager.load(CertificateRequest.class)
                .id(event.getEntityId().getValue())
                .view("certificateRequest-view").one();
        DicReceivingType receivingType = request.getReceivingType();

        try {
            approveScanVersion(request, receivingType);
        } catch (Exception e) {
            log.error("Error on approving Scan version of Certificate", e);
        }


    }

    private void approveScanVersion(CertificateRequest request, DicReceivingType receivingType) {
        if ("SCAN_VERSION".equals(receivingType.getCode())) {
            request.setStatus(commonService.getEntity(DicRequestStatus.class, "APPROVED"));
            Report report = getReportByCertificateRequest(request);
            FileDescriptor file = reportingApi.createAndSaveReport(report, ParamsMap.of("", null), report.getName());
            request.setFile(file);
            dataManager.commit(request);
        }
    }


    public Report getReportByCertificateRequest(CertificateRequest request) {
        Report report = dataManager.load(Report.class)
                .query("select e.report from tsadv_CertificateTemplate e where e.language=:langId and e.receivingType=:recTyId and e.showSalary = :salary and e.certificateType = :certType")
                .parameter("langId", request.getLanguage())
                .parameter("recTyId", request.getReceivingType())
                .parameter("certType", request.getCretificateType())
                .parameter("salary", request.getShowSalary())
                .view("report.edit")
                .optional().orElse(null);

        return report;
    }


}