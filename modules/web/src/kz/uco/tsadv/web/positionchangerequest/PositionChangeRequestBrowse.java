package kz.uco.tsadv.web.positionchangerequest;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.personal.enums.PositionChangeRequestType;
import kz.uco.tsadv.modules.personal.model.PositionChangeRequest;
import kz.uco.tsadv.service.EmployeeNumberService;

import javax.inject.Inject;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class PositionChangeRequestBrowse extends AbstractLookup {

    @Inject
    private Button createPosition, editPosition, removePosition;


    @Inject
    private Metadata metadata;

    @Inject
    private EmployeeNumberService employeeNumberService;

    @Inject
    private GroupDatasource<PositionChangeRequest, UUID> positionChangeRequestsDs;
    @Inject
    private DataManager dataManager;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        initButtonActions();
    }

    private void initButtonActions() {
        createPosition.setAction(new BaseAction("create") {
            @Override
            public void actionPerform(Component component) {
                openEditor(PositionChangeRequestType.NEW);
            }
        });

        editPosition.setAction(new BaseAction("edit") {
            @Override
            public void actionPerform(Component component) {
                openEditor(PositionChangeRequestType.CHANGE);
            }
        });

        removePosition.setAction(new BaseAction("remove") {
            @Override
            public void actionPerform(Component component) {
                openEditor(PositionChangeRequestType.CLOSE);
            }
        });
    }

    private void openEditor(PositionChangeRequestType requestType) {
        try {
            PositionChangeRequest positionChangeRequest = metadata.create(PositionChangeRequest.class);
            positionChangeRequest.setRequestNumber(employeeNumberService.generateNextRequestNumber());
            positionChangeRequest.setRequestType(requestType);
            positionChangeRequest.setRequestDate(new Date());
            AbstractEditor editor = openEditor(getWindowAlias(requestType), positionChangeRequest, WindowManager.OpenType.THIS_TAB);
            editor.addCloseListener(actionId -> positionChangeRequestsDs.refresh());
        } catch (Exception ex) {
            showNotification("Error", ex.getMessage(), NotificationType.TRAY);
        }
    }

    public void openChangeRequest(PositionChangeRequest positionChangeRequest, String target) {
        if (positionChangeRequest.getPositionGroup() != null) {
            positionChangeRequest.setPositionGroup(dataManager.reload(positionChangeRequest.getPositionGroup(), "positionGroup.change.request"));
        }
        AbstractWindow abstractWindow = openEditor(getWindowAlias(positionChangeRequest.getRequestType()),
                positionChangeRequest,
                WindowManager.OpenType.THIS_TAB);
        abstractWindow.addCloseListener((s) -> positionChangeRequestsDs.refresh());
    }

    protected String getWindowAlias(PositionChangeRequestType requestType) {
        return requestType.equals(PositionChangeRequestType.CHANGE) ?
                "tsadv$PositionChangeRequestTypeChange.edit" : "tsadv$PositionChangeRequest.edit";
    }
}