package kz.uco.tsadv.components;

import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.vaadin.event.ExpandEvent;
import kz.uco.tsadv.config.PositionStructureConfig;
import kz.uco.tsadv.entity.MyTeamNew;
import kz.uco.tsadv.service.MyTeamService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * @author Alibek Berdaulet
 */
@Component
public class MyTeamComponent {

    @Inject
    protected MyTeamService myTeamService;
    @Inject
    protected PositionStructureConfig positionStructureConfig;
    @Inject
    protected Messages messages;

    public void addChildren(HierarchicalDatasource<MyTeamNew, UUID> teamDs, UUID parentPositionGroupId, MyTeamNew parent) {
        UUID positionStructureId = positionStructureConfig.getPositionStructureId();
        if (positionStructureId == null) return;
        List<Object[]> list = myTeamService.getChildren(parentPositionGroupId, positionStructureId);
        String vacantPosition = messages.getMessage("kz.uco.tsadv.web.modules.personal.assignment", "vacantPosition");
        for (Object[] entity : list) {
            MyTeamNew item = myTeamService.parseMyTeamNewObject(entity, vacantPosition);
            item.setParent(parent);
            teamDs.addItem(item);
            if (item.getHasChild())
                teamDs.addItem(myTeamService.createFakeChild(item));
        }
    }

    public void onExpand(ExpandEvent<MyTeamNew> expandEvent, HierarchicalDatasource<MyTeamNew, UUID> teamDs) {
        UUID itemId = expandEvent.getExpandedItem().getId();
        Collection<UUID> children = teamDs.getChildren(itemId);
        boolean firstTime = false;
        if (children.size() == 1) {
            for (UUID elem : children) {
                MyTeamNew fakeItem = teamDs.getItemNN(elem);
                if (fakeItem.getFullName().equals("FORDELETE!!")) {
                    teamDs.removeItem(fakeItem);
                    firstTime = true;
                }
            }
        }
        if (children.size() > 0 && firstTime) {

            MyTeamNew eventItem = teamDs.getItemNN(itemId);
            this.addChildren(teamDs, eventItem.getPositionGroupId(), eventItem);
        }
    }
}
