package kz.uco.tsadv.web.screens.certificaterequest;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.reports.entity.Report;
import kz.uco.tsadv.modules.personal.dictionary.DicReceivingType;
import kz.uco.tsadv.modules.personal.model.CertificateRequest;
import kz.uco.tsadv.service.CommonReportsService;

import javax.inject.Inject;


/**
 * User: maiha
 * Date: 08.01.2021
 * Time: 10:02
 */

@UiController("tsadv_CertificateRequestHR.browse")
@UiDescriptor("certificate-request-hr-browse.xml")
@LookupComponent("certificateRequestsTable")
@LoadDataBeforeShow
public class CertificateRequestHRBrowse extends StandardLookup<CertificateRequest> {
    @Inject
    protected CollectionContainer<CertificateRequest> certificateRequestsDc;
    @Inject
    protected GroupTable<CertificateRequest> certificateRequestsTable;
    @Inject
    protected ExportDisplay exportDisplay;
    @Inject
    protected Notifications notifications;
    @Inject
    protected MessageBundle messageBundle;
    @Inject
    protected CommonReportsService commonReportsService;
    @Inject
    private Button printBtn;

    @Subscribe
    public void onInit(InitEvent event) {
        certificateRequestsTable.addSelectionListener(e -> {
            CertificateRequest certificateRequest = e.getSelected().stream().findFirst().orElse(null);
            if (certificateRequest == null) {
                printBtn.setEnabled(true);
                return;
            }
            DicReceivingType receivingType = certificateRequest.getReceivingType();
            if (receivingType == null) {
                printBtn.setEnabled(true);
                return;
            }
            String code = receivingType.getCode();
            printBtn.setEnabled("SCAN_VERSION".equals(code));
        });
    }


    public void print() {
        CertificateRequest request = certificateRequestsTable.getSingleSelected();
        if (request == null) {
            notifications.create(Notifications.NotificationType.ERROR).withCaption(messageBundle.getMessage("select_before_printing")).show();
            return;
        }

        FileDescriptor report = commonReportsService.getReportByCertificateRequest(request);

        if (report == null) {
            notifications.create(Notifications.NotificationType.ERROR).withCaption(messageBundle.getMessage("no_report")).show();
            return;
        }

        exportDisplay.show(report);
    }

    public void printReport(CertificateRequest item, String columnId) {
        exportDisplay.show(item.getFile());
    }


}