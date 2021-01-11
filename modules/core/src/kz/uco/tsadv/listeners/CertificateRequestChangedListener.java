package kz.uco.tsadv.listeners;

import com.haulmont.cuba.core.TransactionalDataManager;
import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.personal.dictionary.DicReceivingType;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.model.CertificateRequest;
import kz.uco.tsadv.service.CommonReportsService;
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
    protected TransactionalDataManager dataManager;
    @Inject
    protected CommonService commonService;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected Logger log;
    @Inject
    protected CommonReportsService commonReportsService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void beforeCommit(EntityChangedEvent<CertificateRequest, UUID> event) {
        CertificateRequest request = dataManager.load(CertificateRequest.class)
                .id(event.getEntityId().getValue())
                .view("certificateRequest-view").one();
        DicReceivingType receivingType = request.getReceivingType();

        try {
            if ("SCAN_VERSION".equals(receivingType.getCode()) && "CERTIFICATE_OF_EMPLOYMENT".equals(request.getCretificateType().getCode())) {
                approveScanVersion(request);
            }
        } catch (Exception e) {
            log.error("Error on approving Scan version of Certificate", e);
        }


    }

    private void approveScanVersion(CertificateRequest request) {
        request.setStatus(commonService.getEntity(DicRequestStatus.class, "APPROVED"));
        request.setFile(commonReportsService.getReportByCertificateRequest(request));
        dataManager.save(request);
    }


}