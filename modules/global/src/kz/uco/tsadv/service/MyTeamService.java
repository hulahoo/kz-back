package kz.uco.tsadv.service;

import kz.uco.tsadv.entity.MyTeamNew;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public interface MyTeamService {
    String NAME = "tsadv_MyTeamService";

    List<MyTeamNew> searchMyTeam(UUID parentPositionGroupId, String searchFio);

    List<Object[]> searchMyTeam(UUID hierarchyId, UUID parentPositionGroupId, String searchFio,
                                @Nullable String lastFindPathNumber, @Nullable UUID lastFindPersonGroupId,
                                boolean onlyFirstElement);

    List<MyTeamNew> getChildren(UUID parentPositionGroupId);

    List<MyTeamNew> getChildren(UUID parentPositionGroupId, MyTeamNew parent);

    List<MyTeamNew> getChildren(UUID parentPositionGroupId, UUID positionStructureId, MyTeamNew parent);

    List<MyTeamNew> getMyTeamInPosition(UUID positionGroupId, UUID positionStructureId);

    MyTeamNew parseMyTeamNewObject(Object[] entity);

    MyTeamNew parseMyTeamNewObject(Object[] entity, String vacantPosition);

    MyTeamNew createFakeChild(@Nullable MyTeamNew parent);

    @Nullable
    List<UUID> getChildPositionIdList(@Nullable UUID parentPositionGroupId, @Nullable UUID positionStructureId);
}