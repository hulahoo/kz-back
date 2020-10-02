package kz.uco.tsadv.web.workplace;

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
import kz.uco.tsadv.entity.tb.WorkPlace;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WorkPlaceBrowse extends AbstractLookup {

    @Inject
    private GroupDatasource<WorkPlace, UUID> workPlacesDs;
    @Named("workPlacesTable.create")
    private CreateAction createAction;
    @Named("workPlacesTable.edit")
    private EditAction editAction;
    @Inject
    private ComponentsFactory componentsFactory;

    public Component generateNameCell(WorkPlace entity) {
        Map<String, Object> map = new HashMap<>();
        map.put("readOnly", "readOnly");
        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        linkButton.setCaption(entity.getName());
        linkButton.setAction(new BaseAction("actionId") {
            @Override
            public void actionPerform(Component component) {
                AbstractEditor<WorkPlace> workPlaceAbstractEditor = openEditor("tsadv$WorkPlace.edit", entity, WindowManager.OpenType.THIS_TAB, map);
                workPlaceAbstractEditor.addCloseListener(actionId -> workPlacesDs.refresh());
            }
        });
        return linkButton;
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        createAction.setAfterCommitHandler(entity -> workPlacesDs.refresh());
        editAction.setAfterCommitHandler(entity -> workPlacesDs.refresh());
    }
}