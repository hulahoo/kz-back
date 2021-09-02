package kz.uco.tsadv.service;

import kz.uco.tsadv.pojo.pagination.EntitiesPaginationResult;
import kz.uco.tsadv.pojo.pagination.PaginationPojo;

import java.util.UUID;

public interface IncentiveService {
    String NAME = "tsadv_IncentiveService";

    EntitiesPaginationResult getIncentiveList(PaginationPojo paginationPojo);

    void saveMonthResult(String status, String comment, UUID incentiveMonthResultId);
}