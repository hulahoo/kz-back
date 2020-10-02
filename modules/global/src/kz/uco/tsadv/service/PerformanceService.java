package kz.uco.tsadv.service;


import kz.uco.tsadv.modules.performance.model.NotPersistEntity;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;

import java.util.List;

public interface PerformanceService {
    String NAME = "tsadv_PerformanceService";

    List<NotPersistEntity> getNotPersistEntityList(boolean myTeam, String language);
    List<NotPersistEntity> getNotPersistEntityList(boolean myTeam, OrganizationGroupExt organizationGroupExt, String fio, String language);
}