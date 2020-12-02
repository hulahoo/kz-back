package kz.uco.tsadv.web.procinstancev;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.LinkButton;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.entity.dbview.ProcInstanceV;
import kz.uco.tsadv.modules.personal.enums.PositionChangeRequestType;
import kz.uco.tsadv.modules.personal.model.PositionChangeRequest;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class ProcInstanceVBrowse extends AbstractLookup {

    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected GroupDatasource<ProcInstanceV, UUID> procInstanceVsDs;
    @Named("procInstanceVsTable.showProcessParticipants")
    protected Action procInstanceVsTableShowProcessParticipants;

    protected WindowConfig windowConfig;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        windowConfig = AppBeans.get(WindowConfig.class);
        procInstanceVsDs.addItemChangeListener(this::procInstanceChangeListener);
    }

    protected void procInstanceChangeListener(Datasource.ItemChangeEvent<ProcInstanceV> procInstanceVItemChangeEvent) {
        procInstanceVsTableShowProcessParticipants.setEnabled(procInstanceVItemChangeEvent.getItem() != null);
    }

    public Component generateLinkToEntity(ProcInstanceV entity) {
        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        linkButton.setCaption(entity.getRequestNumber());
        linkButton.setAction(new BaseAction("openScreen") {
            @Override
            public void actionPerform(Component component) {
                super.actionPerform(component);

                BaseUuidEntity reload = dataManager.reload(entity.getEntity(), View.LOCAL);

                openEditor(getEditorScreenId(reload), reload, WindowManager.OpenType.THIS_TAB, ParamsMap.of("fromProcInstance", true));
            }
        });
        return linkButton;
    }

    protected String getEditorScreenId(BaseUuidEntity entity) {
        if (entity instanceof PositionChangeRequest) {
            PositionChangeRequest positionChangeRequest = (PositionChangeRequest) entity;
            if (PositionChangeRequestType.CHANGE.equals(positionChangeRequest.getRequestType())) {
                return "tsadv$PositionChangeRequestTypeChange.edit";
            }
        }
        return windowConfig.getEditorScreenId(entity.getMetaClass());
    }

    public void showProcessParticipants() {
//        openWindow("bpm$ProcActor.browse", WindowManager.OpenType.DIALOG, ParamsMap.of("procInstance", procInstanceVsDs.getItem().getProcInstance()));
    }
}