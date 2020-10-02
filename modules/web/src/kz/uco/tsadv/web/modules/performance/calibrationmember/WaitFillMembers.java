package kz.uco.tsadv.web.modules.performance.calibrationmember;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.modules.performance.model.Assessment;
import kz.uco.tsadv.modules.performance.model.CalibrationMember;
import kz.uco.tsadv.modules.performance.model.CalibrationSession;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

public class WaitFillMembers extends AbstractWindow {

    @Inject
    private DataManager dataManager;

    @Inject
    private Metadata metadata;

    @Inject
    private Label confirmText;

    @Inject
    private Label waitLoading;

    @Inject
    private HBoxLayout buttonsBlock;

    @Inject
    private Button windowClose;

    @Inject
    private Button windowCommit;

    private CollectionDatasource sessionMembersDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        waitLoading.setValue("<i class=\"fa fa-spinner fa-pulse fa-lg fa-fw\"></i> " + getMessage("wait.fill.member.load"));
        waitLoading.setVisible(false);

        if (params.containsKey(StaticVariable.CALIBRATION_SESSION)) {
            CalibrationSession calibrationSession = (CalibrationSession) params.get(StaticVariable.CALIBRATION_SESSION);

            addAction(new BaseAction("yes") {
                @Override
                public void actionPerform(Component component) {
                    waitVisible(true);
                    startFillMembers(calibrationSession);
                    waitVisible(false);
                    close(this.id);
                }
            });
            addAction(new BaseAction("no") {
                @Override
                public void actionPerform(Component component) {
                    close(this.id);
                }
            });
        }
    }

    public void setSessionMembersDs(CollectionDatasource sessionMembersDs) {
        this.sessionMembersDs = sessionMembersDs;
    }

    @Override
    protected boolean preClose(String actionId) {
        if (actionId.equalsIgnoreCase("yes")) {
            if (sessionMembersDs != null) sessionMembersDs.refresh();
        }
        return super.preClose(actionId);
    }

    private void waitVisible(boolean started) {
        confirmText.setVisible(false);
        waitLoading.setVisible(false);

        for (Component component : buttonsBlock.getComponents()) {
            component.setEnabled(!started);
        }
    }

    private void startFillMembers(CalibrationSession calibrationSession) {
        LoadContext<Assessment> loadContext = LoadContext.create(Assessment.class);
        loadContext.setQuery(LoadContext.createQuery("select e from tsadv$Assessment e where e.template.id = :templateId")
                .setParameter("templateId", calibrationSession.getTemplate().getId()))
                .setView("assessment.matrix");

        List<Assessment> assessmentList = dataManager.loadList(loadContext);
        for (Assessment assessment : assessmentList) {
            insertMembers(assessment, calibrationSession);
        }
    }

    private void insertMembers(Assessment assessment, CalibrationSession calibrationSession) {
        CalibrationMember calibrationMember = metadata.create(CalibrationMember.class);
        calibrationMember.setPerson(assessment.getEmployeePersonGroup());
        calibrationMember.setSession(calibrationSession);

        calibrationMember.setCompetenceOverall(getPosition(assessment.getCompetenceRating()));
        calibrationMember.setGoalOverall(getPosition(assessment.getGoalRating()));

        calibrationMember.setImpactOfLoss(roundDouble(assessment.getImpactOfLoss()));
        calibrationMember.setRiskOfLoss(roundDouble(assessment.getRiskOfLoss()));
        calibrationMember.setPotencial(roundDouble(assessment.getPotential()));
        calibrationMember.setPerformance(roundDouble(assessment.getPerformance()));
        dataManager.commit(calibrationMember);
    }

    private int getPosition(Double value) {
        if (value == null || value == 0) return 1;

        double first = 2.3, second = 3.6, third = 5.0;

        int pos = 1;
        if (value <= first) {
            pos = 1;
        } else if (value > first && value <= second) {
            pos = 2;
        } else if (value > second && value <= third) {
            pos = 3;
        }
        return pos;
    }

    private int roundDouble(Double value) {
        if (value == null || value == 0 || value < 1) value = 1.0;
        return (int) Math.round(value);
    }
}