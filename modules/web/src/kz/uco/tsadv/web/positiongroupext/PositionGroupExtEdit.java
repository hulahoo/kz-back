package kz.uco.tsadv.web.positiongroupext;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.entity.dbview.PositionSsView;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;

import javax.inject.Inject;
import java.util.Map;

public class PositionGroupExtEdit extends AbstractEditor<PositionGroupExt> {
    @Inject
    private Datasource<PositionGroupExt> positionGroupExtDs;
    @Inject
    private PickerField<Entity> positionPicker;
    @Inject
    private DataManager dataManager;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        ((Label) getComponentNN("warning")).setValue(getMessage("position.closed"));

        PickerField.LookupAction lookupAction = new PickerField.LookupAction(positionPicker) {
            @Override
            public Entity transformValueFromLookupWindow(Entity valueFromLookupWindow) {
                return dataManager.reload(((PositionSsView) valueFromLookupWindow).getPositionGroup(), "positionGroup.change.request");
            }
        };
        lookupAction.setLookupScreen("tsadv$PositionSsView.browse");
        positionPicker.addAction(lookupAction);
    }

    @Override
    public void ready() {
        super.ready();
        positionGroupExtDs.setItem(null);

        positionPicker.addValueChangeListener(e -> positionGroupExtDs.setItem((PositionGroupExt) e.getValue()));
    }

    public void okBtn() {
        if (positionGroupExtDs.getItem() == null) {
            showNotification(getMessage("Attention"), getMessage("position.choose"), NotificationType.TRAY);
            return;
        }
        close(CLOSE_ACTION_ID);
    }

    public void cancelBtn() {
        positionGroupExtDs.setItem(null);
        close(CLOSE_ACTION_ID);
    }
}