package kz.uco.tsadv.service;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.app.FileStorageAPI;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.*;
import com.haulmont.reports.app.service.ReportService;
import com.haulmont.reports.entity.Report;
import com.haulmont.yarg.reporting.ReportOutputDocument;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.personal.model.CertificateRequest;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service(CommonReportsService.NAME)
public class CommonReportsServiceBean implements CommonReportsService {

    protected static final Logger log = LoggerFactory.getLogger(CommonReportsServiceBean.class);
    @Inject
    protected DataManager dataManager;
    @Inject
    protected ReportService reportService;
    @Inject
    protected Metadata metadata;
    @Inject
    protected TimeSource timeSource;
    @Inject
    protected FileStorageAPI fileStorageAPI;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    private CommonService commonService;

    public FileDescriptor getReportByCertificateRequest(@Nonnull CertificateRequest request) {

        DicCompany company = employeeService.getCompanyByPersonGroupId(request.getPersonGroup().getId());

        FluentLoader.ByQuery<Report, UUID> parameter = dataManager.load(Report.class)
                .query("select e.report from tsadv_CertificateTemplate e " +
                        (company != null ? "   join e.organization.company c " : "") +
                        "   where e.language=:langId " +
                        "       and e.receivingType=:recTyId " +
                        "       and e.showSalary = :salary " +
                        "       and e.certificateType = :certType" +
                        (company != null ? "       and c.id = :companyId " : " and e.organization is null"))
                .parameter("langId", request.getLanguage())
                .parameter("recTyId", request.getReceivingType())
                .parameter("certType", request.getCertificateType())
                .parameter("salary", request.getShowSalary());
        if (company != null) parameter.parameter("companyId", company.getId());
        Report report = parameter
                .view("report.edit")
                .optional().orElse(null);

        if (report == null) {
            return null;
        }

        ReportOutputDocument document = reportService.createReport(report, ParamsMap.of("req_id", request));

        return saveDocument(report.getName(), document);
    }

    protected FileDescriptor saveDocument(String name, ReportOutputDocument document) {
        String ext = FilenameUtils.getExtension(document.getDocumentName());
        FileDescriptor file = metadata.create(FileDescriptor.class);
        file.setCreateDate(timeSource.currentTimestamp());
        file.setName(name);
        file.setExtension(ext);
        file.setSize((long) document.getContent().length);

        try {
            fileStorageAPI.saveFile(file, document.getContent());
        } catch (FileStorageException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        file = dataManager.commit(file);
        return file;
    }

    @Override
    public Map<String, Object> downloadReportByCode(@NotNull String reportCode, @NotNull Map<String, Object> params) {
        Report report = commonService.getEntity(Report.class, reportCode);
        ReportOutputDocument document = reportService.createReport(report, params);

        Map<String, Object> response = new HashMap<>();
        response.put("extension", document.getReportOutputType().getId());
        response.put("content", new String(Base64.getEncoder().encode(document.getContent()), StandardCharsets.UTF_8));
        return response;
    }

    @Override
    public Map<String, Object> downloadReportByCode(String reportCode, String entityParamName, UUID entityId) {
        return this.downloadReportByCode(reportCode, ParamsMap.of(entityParamName, entityId));
    }
}