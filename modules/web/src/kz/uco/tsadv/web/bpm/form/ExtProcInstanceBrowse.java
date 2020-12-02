package kz.uco.tsadv.web.bpm.form;

import com.haulmont.bali.util.ParamsMap;
//import com.haulmont.bpm.gui.procinstance.ProcInstanceBrowse;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractEditor;
import kz.uco.tsadv.modules.personal.enums.PositionChangeRequestType;
import kz.uco.tsadv.modules.personal.model.PositionChangeRequest;

public class ExtProcInstanceBrowse /*extends ProcInstanceBrowse */{

    /*@Override
    public AbstractEditor openEditor(String windowAlias, Entity entity, WindowManager.OpenType thisTab) {
        if (windowAlias.equals("tsadv$PositionChangeRequest.edit") && entity instanceof PositionChangeRequest) {
            PositionChangeRequest positionChangeRequest = (PositionChangeRequest) entity;
            if (positionChangeRequest.getRequestType().equals(PositionChangeRequestType.CHANGE)) {
                windowAlias = "tsadv$PositionChangeRequestTypeChange.edit";
            }
        }
        return super.openEditor(windowAlias, entity, thisTab, ParamsMap.of("fromProcInstance", true));
    }*/
}