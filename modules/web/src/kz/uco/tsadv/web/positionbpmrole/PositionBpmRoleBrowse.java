package kz.uco.tsadv.web.positionbpmrole;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.entity.tb.PositionBpmRole;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.UUID;

public class PositionBpmRoleBrowse extends AbstractLookup {
    @Named("bpmRolesLinkTable.create")
    private CreateAction bpmRolesLinkTableCreate;
    @Inject
    private GroupDatasource<PositionBpmRole, UUID> positionBpmRolesDs;

    @Override
    public void ready() {
        super.ready();

        bpmRolesLinkTableCreate.setInitialValuesSupplier(() -> ParamsMap.of("positionBpmRole", positionBpmRolesDs.getItem()));

        positionBpmRolesDs.addItemChangeListener(e -> {
            bpmRolesLinkTableCreate.setEnabled(e.getItem() != null);
        });
    }
}