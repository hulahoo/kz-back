package kz.uco.tsadv.web.modules.timesheet.shift;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.timesheet.model.Shift;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class ShiftBrowse extends AbstractLookup {

    @Inject
    private Metadata metadata;

    @Inject
    private GroupDatasource<Shift, UUID> shiftsDs;

    @Named("detailsTable.create")
    private CreateAction detailsTableCreate;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        detailsTableCreate.setInitialValuesSupplier(() -> Collections.singletonMap("shift", shiftsDs.getItem()));
        detailsTableCreate.setEnabled(false);
        shiftsDs.addItemChangeListener(e -> {
            detailsTableCreate.setEnabled(e.getItem() != null);
        });
    }
}