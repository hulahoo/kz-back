package kz.uco.tsadv.service;

import com.haulmont.cuba.core.entity.FileDescriptor;
import kz.uco.tsadv.modules.personal.model.CertificateRequest;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;

public interface CommonReportsService {
    String NAME = "tsadv_CommonReportsService";

    FileDescriptor getReportByCertificateRequest(@Nonnull CertificateRequest request);

    Map<String, Object> downloadReportByCode(@NotNull String reportCode, @NotNull Map<String, Object> params);

    Map<String, Object> downloadReportByCode(String reportCode, String entityParamName, UUID entityId);
}