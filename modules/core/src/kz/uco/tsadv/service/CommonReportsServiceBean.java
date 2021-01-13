package kz.uco.tsadv.service;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.reports.ReportingApi;
import com.haulmont.reports.entity.Report;
import kz.uco.tsadv.modules.personal.model.CertificateRequest;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(CommonReportsService.NAME)
public class CommonReportsServiceBean implements CommonReportsService {

    @Inject
    protected ReportingApi reportingApi;
    @Inject
    protected DataManager dataManager;


    public FileDescriptor getReportByCertificateRequest(CertificateRequest request) {
        if (request == null) {
            throw new NullPointerException("report is null");
        }

        Report report = dataManager.load(Report.class)
                .query("select e.report from tsadv_CertificateTemplate e where e.language=:langId and e.receivingType=:recTyId and e.showSalary = :salary and e.certificateType = :certType")
                .parameter("langId", request.getLanguage())
                .parameter("recTyId", request.getReceivingType())
                .parameter("certType", request.getCretificateType())
                .parameter("salary", request.getShowSalary())
                .view("report.edit")
                .optional().orElse(null);

        if (report == null) {
            return null;
        }

        FileDescriptor file = reportingApi.createAndSaveReport(report, ParamsMap.of("req_id", report), report.getName());
        return file;
    }


}