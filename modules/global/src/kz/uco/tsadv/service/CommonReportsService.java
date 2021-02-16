package kz.uco.tsadv.service;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.yarg.reporting.ReportOutputDocument;
import kz.uco.tsadv.modules.personal.model.CertificateRequest;

import javax.annotation.Nonnull;

public interface CommonReportsService {
    String NAME = "tsadv_CommonReportsService";

    FileDescriptor getReportByCertificateRequest(@Nonnull CertificateRequest request);
}