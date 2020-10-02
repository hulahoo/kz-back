package kz.uco.tsadv.web.modules.personal.globalvaluegroup;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.personal.group.GlobalValueGroup;
import kz.uco.tsadv.modules.personal.model.GlobalValue;
import kz.uco.tsadv.web.modules.personal.globalvalue.GlobalValueEdit;
import kz.uco.tsadv.web.modules.personal.common.Utils;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class GlobalValueGroupBrowse extends AbstractLookup {
    @Inject
    private GroupDatasource<GlobalValueGroup, UUID> globalValueGroupsDs;
    @Inject
    private CollectionDatasource<GlobalValue, UUID> globalValueDs;
    @Inject
    private Metadata metadata;

    public void openGlobalValue() {
        opentGlobalValueEditor(metadata.create(GlobalValue.class), null);
    }

    public void edit() {
        GlobalValueGroup globalValueGroup = globalValueGroupsDs.getItem();
        GlobalValue globalValue = globalValueGroup.getGlobalValue();
        opentGlobalValueEditor(globalValue, null);
    }

    private void opentGlobalValueEditor(GlobalValue globalValue, Map<String, Object> params) {
        GlobalValueEdit globalValueEdit = (GlobalValueEdit) openEditor("tsadv$GlobalValue.edit", globalValue, WindowManager.OpenType.THIS_TAB, params);
        globalValueEdit.addCloseListener(actionId -> globalValueGroupsDs.refresh());
    }

//    public void editHistory() {
//        Utils.editHistory(globalValueDs.getItem(), globalValueGroupsDs.getItem().getList(), this, globalValueGroupsDs);
//    }
//
//    public void removeHistory() {
//        Utils.removeHistory(globalValueDs, this, globalValueGroupsDs);
//    }

    public void removeHistories() {

    }
}