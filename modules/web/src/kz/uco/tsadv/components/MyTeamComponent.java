package kz.uco.tsadv.components;

import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.vaadin.v7.ui.Tree;
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
    protected Messages messages;

    public void addChildren(HierarchicalDatasource<MyTeamNew, UUID> teamDs, UUID parentPositionGroupId, MyTeamNew parent) {
        List<MyTeamNew> list = myTeamService.getChildren(parentPositionGroupId, parent);
        for (MyTeamNew item : list) {
            teamDs.addItem(item);
            if (item.getHasChild())
                teamDs.addItem(myTeamService.createFakeChild(item));
        }
    }

    public void onExpand(Tree.ExpandEvent expandEvent, HierarchicalDatasource<MyTeamNew, UUID> teamDs) {
        UUID itemId = (UUID) expandEvent.getItemId();
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
