package kz.uco.tsadv.web.modules.performance.calibrationmember;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.modules.performance.enums.MatrixType;
import kz.uco.tsadv.modules.performance.model.CalibrationMember;
import kz.uco.tsadv.modules.performance.model.CalibrationSession;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.web.modules.performance.matrix.ScrumBrowse;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CalibrationMemberBrowse extends AbstractLookup {

    @Inject
    private CollectionDatasource<CalibrationMember, UUID> calibrationMembersDs;

    @Inject
    private Datasource<CalibrationSession> calibrationSessionDs;

    @Override
    public void init(Map<String, Object> params) {
        if (params.containsKey(StaticVariable.CALIBRATION_SESSION)) {
            CalibrationSession calibrationSession = (CalibrationSession) params.get(StaticVariable.CALIBRATION_SESSION);

            calibrationSessionDs.setItem(calibrationSession);

            calibrationMembersDs.setQuery(String.format(
                    "select e from tsadv$CalibrationMember e where e.session.id = '%s'",
                    calibrationSession.getId()));

            addAction(new BaseAction("fillMembers") {
                @Override
                public void actionPerform(Component component) {
                    WaitFillMembers waitFillMembers = (WaitFillMembers) openWindow("wait-fill-members", WindowManager.OpenType.DIALOG, new HashMap<String, Object>() {{
                        put(StaticVariable.CALIBRATION_SESSION, calibrationSession);
                    }});

                    waitFillMembers.setSessionMembersDs(calibrationMembersDs);
                }
            });
        } else {
            addAction(new BaseAction("fillMembers") {
                @Override
                public void actionPerform(Component component) {
                    showNotification("Пустая калибровочная сессия!");
                }
            });
        }

        super.init(params);
    }

    public void showMatrixPerformance() {
        openMatrix(MatrixType.PERFORMANCE);
    }

    public void showMatrixRisk() {
        openMatrix(MatrixType.RISK);
    }

    private void openMatrix(MatrixType matrixType) {
        openWindow("matrix-browse", WindowManager.OpenType.THIS_TAB, new HashMap<String, Object>() {{
            put(StaticVariable.CALIBRATION_SESSION, calibrationSessionDs.getItem());
            put(StaticVariable.MATRIX_TYPE, matrixType);
        }});
    }

    public void showScrumCompetence() {
        openScrum(ScrumBrowse.ScrumType.COMPETENCE);
    }

    public void showScrumGoal() {
        openScrum(ScrumBrowse.ScrumType.GOAL);
    }

    private void openScrum(ScrumBrowse.ScrumType scrumType) {
        openWindow("scrum-browse", WindowManager.OpenType.THIS_TAB, new HashMap<String, Object>() {{
            put(StaticVariable.CALIBRATION_SESSION, calibrationSessionDs.getItem());
            put(StaticVariable.SCRUM_TYPE, scrumType);
        }});
    }
}