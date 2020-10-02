package kz.uco.tsadv.web.accidents;

import com.haulmont.bali.util.ParamsMap;
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
import kz.uco.tsadv.entity.tb.Accidents;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class AccidentsBrowse extends AbstractLookup {

    @Inject
    private GroupDatasource<Accidents, UUID> accidentsesDs;
    @Inject
    private ComponentsFactory componentsFactory;
    @Named("accidentsesTable.create")
    private CreateAction createAction;
    @Named("accidentsesTable.edit")
    private EditAction editAction;

    public Component generateNameCell(Accidents entity) {
        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        linkButton.setCaption(entity.getAccidentActNumber());
        linkButton.setAction(new BaseAction("actionId") {
            @Override
            public void actionPerform(Component component) {
                AbstractEditor<Accidents> accidentsAbstractEditor = openEditor("tsadv$Accidents.edit",
                        entity,
                        WindowManager.OpenType.THIS_TAB,
                        ParamsMap.of("readOnly", "readOnly"));
                accidentsAbstractEditor.addCloseListener(actionId -> accidentsesDs.refresh());
            }
        });
        return linkButton;
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        createAction.setAfterCommitHandler(entity -> accidentsesDs.refresh());
        editAction.setAfterCommitHandler(entity -> accidentsesDs.refresh());
    }
}