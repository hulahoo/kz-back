package kz.uco.tsadv.service;

import kz.uco.tsadv.pojo.pagination.EntitiesPaginationResult;
import kz.uco.tsadv.pojo.pagination.PaginationPojo;

public interface IncentiveService {
    String NAME = "tsadv_IncentiveService";

    EntitiesPaginationResult getIncentiveList(PaginationPojo paginationPojo);
}