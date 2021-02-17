package kz.uco.tsadv.service;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.app.FileStorageAPI;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.reports.app.service.ReportService;
import com.haulmont.reports.entity.Report;
import com.haulmont.yarg.reporting.ReportOutputDocument;
import kz.uco.tsadv.modules.personal.model.CertificateRequest;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.inject.Inject;

@Service(CommonReportsService.NAME)
public class CommonReportsServiceBean implements CommonReportsService {

    protected static final Logger log = org.slf4j.LoggerFactory.getLogger(CommonReportsServiceBean.class);
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
    protected Persistence persistence;

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
}