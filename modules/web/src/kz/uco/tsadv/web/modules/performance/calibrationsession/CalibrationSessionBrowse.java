package kz.uco.tsadv.web.modules.performance.calibrationsession;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.modules.performance.model.CalibrationSession;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CalibrationSessionBrowse extends AbstractLookup {

    @Inject
    private Table<CalibrationSession> calibrationSessionsTable;

    @Inject
    private CollectionDatasource<CalibrationSession, UUID> calibrationSessionsDs;

    @Inject
    private Button membersBtn;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        enableDisableButtons(calibrationSessionsTable.getSingleSelected() != null);

        calibrationSessionsDs.addItemChangeListener(new Datasource.ItemChangeListener<CalibrationSession>() {
            @Override
            public void itemChanged(Datasource.ItemChangeEvent<CalibrationSession> e) {
                enableDisableButtons(e.getItem() != null);
            }
        });
    }

    private void enableDisableButtons(boolean enable) {
        membersBtn.setEnabled(enable);
    }

    public void showMembers() {
        openWindow("tsadv$CalibrationMember.browse", WindowManager.OpenType.THIS_TAB, new HashMap<String, Object>() {{
            put(StaticVariable.CALIBRATION_SESSION, calibrationSessionsDs.getItem());
        }});
    }
}