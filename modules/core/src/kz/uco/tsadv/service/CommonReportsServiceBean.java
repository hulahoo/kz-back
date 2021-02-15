package kz.uco.tsadv.service;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.reports.ReportingApi;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.libintegration.CubaReporting;
import kz.uco.tsadv.modules.personal.model.CertificateRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.inject.Inject;

@Service(CommonReportsService.NAME)
public class CommonReportsServiceBean implements CommonReportsService {

    @Inject
    protected ReportingApi reportingApi;
    @Inject
    protected DataManager dataManager;

    public FileDescriptor getReportByCertificateRequest(@Nonnull CertificateRequest request) {
        Report report = dataManager.load(Report.class)
                .query("select e.report from tsadv_CertificateTemplate e where e.language=:langId and e.receivingType=:recTyId and e.showSalary = :salary and e.certificateType = :certType")
                .parameter("langId", request.getLanguage())
                .parameter("recTyId", request.getReceivingType())
                .parameter("certType", request.getCertificateType())
                .parameter("salary", request.getShowSalary())
                .view("report.edit")
                .optional().orElse(null);

        if (report == null) {
            return null;
        }

        return reportingApi.createAndSaveReport(report,
                ParamsMap.of("req_id", request, CubaReporting.REPORT_FILE_NAME_KEY, report.getName()),
                report.getName());
    }


}