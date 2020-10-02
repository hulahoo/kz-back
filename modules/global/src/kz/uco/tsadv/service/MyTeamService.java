package kz.uco.tsadv.service;

import kz.uco.tsadv.entity.MyTeamNew;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public interface MyTeamService {
    String NAME = "tsadv_MyTeamService";

    List<Object[]> searchMyTeam(UUID hierarchyId, UUID parentPositionGroupId, String searchFio,
                                @Nullable String lastFindPathNumber, @Nullable UUID lastFindPersonGroupId,
                                boolean onlyFirstElement);

    List<Object[]> getChildren(UUID parentPositionGroupId, UUID positionStructureId);

    List<Object[]> getMyTeamInPosition(UUID positionGroupId, UUID positionStructureId);

    MyTeamNew parseMyTeamNewObject(Object[] entity, String vacantPosition);

    MyTeamNew createFakeChild(@Nullable MyTeamNew parent);

    @Nullable
    List<UUID> getChildPositionIdList(@Nullable UUID parentPositionGroupId, @Nullable UUID positionStructureId);
}