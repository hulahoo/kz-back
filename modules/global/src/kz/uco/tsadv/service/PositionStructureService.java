package kz.uco.tsadv.service;

import kz.uco.tsadv.entity.models.PositionHierarchy;

import java.util.List;
import java.util.UUID;

public interface PositionStructureService {
    String NAME = "tsadv_PositionStructureService";

    List<PositionHierarchy> getChildren(UUID parentId);

    List<PositionHierarchy> getStartData();


}