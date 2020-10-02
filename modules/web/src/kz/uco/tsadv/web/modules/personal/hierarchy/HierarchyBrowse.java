package kz.uco.tsadv.web.modules.personal.hierarchy;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.base.entity.shared.Hierarchy;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class HierarchyBrowse extends AbstractLookup {

    @Named("hierarchiesTable.remove")
    private RemoveAction hierarchiesTableRemove;

    @Inject
    private GroupDatasource<Hierarchy, UUID> hierarchiesDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        hierarchiesDs.addItemChangeListener(e -> {
            hierarchiesTableRemove.setEnabled(hierarchiesDs.getItem() != null && !hierarchiesDs.getItem().getPrimaryFlag());
        });
    }
}