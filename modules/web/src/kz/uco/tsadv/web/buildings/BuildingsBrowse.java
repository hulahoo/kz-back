package kz.uco.tsadv.web.buildings;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.LinkButton;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.entity.tb.Buildings;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BuildingsBrowse extends AbstractLookup {

    @Inject
    private GroupDatasource<Buildings, UUID> buildingsesDs;
    @Inject
    private ComponentsFactory componentsFactory;
    @Named("buildingsesTable.create")
    private CreateAction createAction;
    @Named("buildingsesTable.edit")
    private EditAction editAction;

    public Component generateNameCell(Buildings entity) {
        Map<String, Object> map = new HashMap<>();
        map.put("readOnly", "readOnly");
        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        linkButton.setCaption(entity.getName());
        linkButton.setAction(new BaseAction("actionId") {
            @Override
            public void actionPerform(Component component) {
                AbstractEditor<Buildings> buildingsAbstractEditor = openEditor("tsadv$Buildings.edit", entity, WindowManager.OpenType.THIS_TAB, map);
                buildingsAbstractEditor.addCloseListener(actionId -> buildingsesDs.refresh());
            }
        });
        return linkButton;
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        createAction.setAfterCommitHandler(entity -> buildingsesDs.refresh());
        editAction.setAfterCommitHandler(entity -> buildingsesDs.refresh());
    }
}