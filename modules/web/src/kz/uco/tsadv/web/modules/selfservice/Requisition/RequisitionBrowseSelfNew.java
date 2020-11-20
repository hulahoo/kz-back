package kz.uco.tsadv.web.modules.selfservice.Requisition;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.gui.components.renderers.WebComponentRenderer;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.recruitment.model.Requisition;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RequisitionBrowseSelfNew extends AbstractLookup {
    @Inject
    protected CollectionDatasource<Requisition, UUID> requisitionsDs;
    @Inject
    protected DataGrid<Requisition> requisitionDataGrid;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Named("requisitionDataGrid.create")
    protected CreateAction requisitionDataGridCreate;
    @Named("requisitionDataGrid.edit")
    protected EditAction requisitionDataGridEdit;
    @Inject
    protected UserSession userSession;
    @Inject
    protected CommonService commonService;
    protected PersonGroupExt currentPersonGroup;
    @Inject
    protected DataManager dataManager;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        currentPersonGroup = commonService.getEntity(PersonGroupExt.class,
                "select e.personGroup from tsadv$UserExt e where e.id = :userId",
                ParamsMap.of("userId", userSession.getUser().getId()), View.MINIMAL);
        setCreateActionSettings();
        setEditActionSettings();
        nameForSiteColumnGenerate();
        jobRequestCountColumnGenerate();
    }

    protected void setEditActionSettings() {
        requisitionDataGridEdit.setWindowId("tsadv$Requisition.self.new.edit");
        requisitionDataGridEdit.setBeforeActionPerformedHandler(() -> {
            requisitionsDs.updateItem(dataManager.reload(requisitionsDs.getItem(), "requisition.for.self.new.edit"));
            return true;
        });
        requisitionDataGridEdit.setAfterCommitHandler(entity -> {
            requisitionsDs.updateItem(dataManager.reload(((Requisition) entity), "requisition.for.self.new"));
        });

    }

    protected void setCreateActionSettings() {
        requisitionDataGridCreate.setEnabled(currentPersonGroup != null);
        requisitionDataGridCreate.setWindowId("tsadv$Requisition.self.new.edit");
        if (currentPersonGroup != null) {
            Map<String, Object> initialValues = requisitionDataGridCreate.getInitialValues();
            if (initialValues != null) {
                initialValues.put("managerPersonGroup", currentPersonGroup);
            } else {
                initialValues = new HashMap<>();
                initialValues.put("managerPersonGroup", currentPersonGroup);
                requisitionDataGridCreate.setInitialValues(initialValues);
            }
        }
    }

    protected void jobRequestCountColumnGenerate() {
        DataGrid.Column jobRequestCount = requisitionDataGrid.addGeneratedColumn("jobRequestCount",
                new DataGrid.ColumnGenerator<Requisition, Component>() {
                    @Override
                    public Component getValue(DataGrid.ColumnGeneratorEvent<Requisition> event) {
                        Label label = componentsFactory.createComponent(Label.class);
                        label.setValue(event.getItem().getJobRequests().size());
                        return label;
                    }

                    @Override
                    public Class<Component> getType() {
                        return Component.class;
                    }
                });
        jobRequestCount.setRenderer(new WebComponentRenderer());
    }

    protected void nameForSiteColumnGenerate() {
        DataGrid.Column nameForSiteLang = requisitionDataGrid.addGeneratedColumn("nameForSiteLang",
                new DataGrid.ColumnGenerator<Requisition, Component>() {
                    @Override
                    public Component getValue(DataGrid.ColumnGeneratorEvent<Requisition> event) {
                        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
                        linkButton.setAction(new BaseAction("linkBtn:" + event.getItem().getId().toString()) {
                            @Override
                            public void actionPerform(Component component) {
                                super.actionPerform(component);
                                openEditor(event.getItem());
                            }
                        });
                        linkButton.setCaption(event.getItem().getNameForSiteLang());
                        return linkButton;
                    }

                    @Override
                    public Class<Component> getType() {
                        return Component.class;
                    }
                });
        nameForSiteLang.setRenderer(new WebComponentRenderer());
    }

    protected void openEditor(Requisition requisition) {
        AbstractEditor abstractEditor = openEditor("tsadv$Requisition.self.new.edit",
                requisition, WindowManager.OpenType.THIS_TAB);
        abstractEditor.addCloseWithCommitListener(() -> {
            requisitionsDs.refresh();
        });
    }

}
