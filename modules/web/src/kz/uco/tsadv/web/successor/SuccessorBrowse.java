package kz.uco.tsadv.web.successor;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.personal.model.Successor;

import javax.inject.Inject;
import java.util.UUID;

public class SuccessorBrowse extends AbstractLookup {
    @Inject
    protected GroupDatasource<Successor, UUID> successorsDs;
    @Inject
    protected ComponentsFactory componentsFactory;

    public void openHistory() {
        Window window = openWindow("tsadv$SuccessorHistory.browse",
                WindowManager.OpenType.THIS_TAB, ParamsMap.of("successionId",
                        successorsDs.getItem().getSuccession().getId(),
                        "personGroupId", successorsDs.getItem().getPersonGroup().getId()));
        window.addCloseListener(actionId -> successorsDs.refresh());
    }

    public void move() {
        AbstractEditor abstractEditor = openEditor("tsadv$Successor.edit", successorsDs.getItem(),
                WindowManager.OpenType.THIS_TAB,
                ParamsMap.of("fromPromote", true));
        abstractEditor.addCloseListener(actionId -> successorsDs.refresh());
    }

    public void ipr() {
        Window window = openWindow("tsadv$IdpDetail.browse", WindowManager.OpenType.THIS_TAB,
                ParamsMap.of("personGroupId", successorsDs.getItem().getPersonGroup().getId()));
        Button closeButton = componentsFactory.createComponent(Button.class);
        closeButton.setAction(new BaseAction("close") {
            @Override
            public void actionPerform(Component component) {
                super.actionPerform(component);
                window.close("cansel");
            }
        });
        closeButton.setCaption(getMessage("go.back"));
        closeButton.setIcon("font-icon:ARROW_LEFT");
        window.add(closeButton);
    }
}