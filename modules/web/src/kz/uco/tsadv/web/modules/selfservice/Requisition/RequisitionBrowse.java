package kz.uco.tsadv.web.modules.selfservice.Requisition;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.LinkButton;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.recruitment.model.Requisition;

import javax.inject.Inject;

public class RequisitionBrowse extends AbstractLookup {
    @Inject
    protected ComponentsFactory componentsFactory;

    public Component generateJobGroupCell(Requisition entity) {
        LinkButton linkButton=componentsFactory.createComponent(LinkButton.class);
        linkButton.setCaption(entity.getJobGroup().getJobName());
        linkButton.setAction(new BaseAction("requisitionDetail"){
            @Override
            public void actionPerform(Component component) {
                super.actionPerform(component);

                openEditor("requisition-selfservice-view",entity, WindowManager.OpenType.NEW_WINDOW);

            }
        });
        return linkButton;
    }
}