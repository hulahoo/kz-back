package kz.uco.tsadv.service;

import java.util.List;
import java.util.UUID;

public interface BprocUtilService {
    String NAME = "tsadv_BprocUtilService";

    List<UUID> getAssignee(String hrRoleCode);

    boolean hasAssignee(String hrRoleCode);

    void reject(String processId);

    void approve(String processId);
}