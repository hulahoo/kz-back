package kz.uco.tsadv.listener;

import java.util.UUID;

public interface PositionBpmRoleService {
    String NAME = "tsadv_PositionBpmRoleService";

    boolean isConstrain(UUID positionGroupId, String modelName, UUID entityId);
}