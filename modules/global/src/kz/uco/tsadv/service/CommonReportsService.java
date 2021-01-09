package kz.uco.tsadv.service;

import com.haulmont.cuba.core.entity.FileDescriptor;
import kz.uco.tsadv.modules.personal.model.CertificateRequest;

public interface CommonReportsService {
    String NAME = "tsadv_CommonReportsService";

    FileDescriptor getReportByCertificateRequest(CertificateRequest request);
}