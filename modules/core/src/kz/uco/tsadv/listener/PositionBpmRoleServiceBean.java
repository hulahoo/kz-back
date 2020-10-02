package kz.uco.tsadv.listener;

import com.haulmont.cuba.core.Persistence;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.UUID;

@Service(PositionBpmRoleService.NAME)
public class PositionBpmRoleServiceBean implements PositionBpmRoleService {
    @Inject
    private Persistence persistence;

    public boolean isConstrain(UUID positionGroupId, String modelName, UUID entityId) {
        return persistence.callInTransaction(em ->
                em.createQuery("select count(e) from tsadv$PositionBpmRole e " +
                        " where e.positionGroup.id = :positionGroupId " +
                        "      and e.procModel.name = :name " +
                        "      and e.id <> :id ", Long.class)
                        .setParameter("positionGroupId", positionGroupId)
                        .setParameter("name", modelName)
                        .setParameter("id", entityId)
                        .getSingleResult()) > 0;
    }
}