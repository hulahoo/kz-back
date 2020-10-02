package kz.uco.tsadv.web.modules.personal.hierarchyelementtable;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.personal.model.HierarchyElementExt;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class HierarchyElementTableBrowse extends AbstractLookup {

    @Inject
    private HierarchicalDatasource<HierarchyElementExt, UUID> hierarchyElementsDs;

    @Named("hierarchyElementsTable.close")
    private Action hierarchyElementsTableClose;

    @Named("hierarchyElementsTable.create")
    private CreateAction hierarchyElementsTableCreate;

    @Named("hierarchyElementsTable.edit")
    private EditAction hierarchyElementsTableEdit;

    @Named("hierarchyElementsTable.reassignElement")
    private Action hierarchyElementsTableReassignElement;

    @Inject
    private ComponentsFactory componentsFactory;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        hierarchyElementsTableClose.setEnabled(false);
        hierarchyElementsTableReassignElement.setEnabled(false);
        hierarchyElementsDs.addItemChangeListener(e -> {
            hierarchyElementsTableClose.setEnabled(hierarchyElementsDs.getItem() != null);
            hierarchyElementsTableReassignElement.setEnabled(hierarchyElementsDs.getItem() != null);
        });

        hierarchyElementsTableCreate.setInitialValuesSupplier(() -> ParamsMap.of("createFromHierarchyElementsTable", null));

        hierarchyElementsTableEdit.setWindowParams(ParamsMap.of("openedForEdit", ""));
    }

    public void close() {
        AbstractEditor<HierarchyElementExt> hierarchyElementAbstractEditor = openEditor("base$HierarchyElement.edit", hierarchyElementsDs.getItem(), WindowManager.OpenType.DIALOG, Collections.singletonMap("close", Boolean.TRUE));
        hierarchyElementAbstractEditor.addCloseWithCommitListener(() -> hierarchyElementsDs.refresh());
    }

    public void reassignElement() {
        openEditor("base$HierarchyElement.edit", hierarchyElementsDs.getItem(), WindowManager.OpenType.DIALOG, ParamsMap.of("reassignElement", ""));
    }

    public Component generateParentName(HierarchyElementExt entity) {
        Label label = componentsFactory.createComponent(Label.class);
        if (entity.getParent() != null && entity.getParent().getName() != null) {
            label.setValue(entity.getParent().getName());
        }
        return label;
    }
}