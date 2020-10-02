package kz.uco.tsadv.web.modules.performance.matrix;

import com.google.gson.Gson;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.LookupPickerField;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.performance.dto.BoardChangedItem;
import kz.uco.tsadv.modules.performance.dto.BoardUpdateType;
import kz.uco.tsadv.modules.performance.enums.MatrixType;
import kz.uco.tsadv.modules.performance.model.CalibrationSession;
import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.web.gui.components.WebMatrix;
import kz.uco.tsadv.web.toolkit.ui.matrixcomponent.MatrixComponent;
import kz.uco.tsadv.service.EmployeeService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MatrixBrowse extends AbstractWindow {

    private static Gson gson = new Gson();

    @Inject
    private FieldGroup fieldGroup;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private EmployeeService employeeService;

    @Inject
    private LookupPickerField jobGroupLookupId;

    @Inject
    private LookupPickerField organizationGroupLookupId;

    @Inject
    private LookupPickerField positionGroupLookupId;

    @Inject
    private CollectionDatasource<JobGroup, UUID> jobGroupsDs;

    @Inject
    private CollectionDatasource<OrganizationGroupExt, UUID> organizationGroupsDs;

    @Inject
    private CollectionDatasource<PositionGroupExt, UUID> positionGroupsDs;

    @Inject
    private UserSessionSource userSessionSource;

    private List<String> changes = new ArrayList<>();

    private MatrixType matrixType;
    private CalibrationSession calibrationSession;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        removeOpenAction();

        if (params.containsKey(StaticVariable.CALIBRATION_SESSION)) {
            calibrationSession = (CalibrationSession) params.get(StaticVariable.CALIBRATION_SESSION);

            if (params.containsKey(StaticVariable.MATRIX_TYPE)) {
                matrixType = (MatrixType) params.get(StaticVariable.MATRIX_TYPE);

                setCaption(getMessage("MatrixBrowse." + matrixType.name()));

                WebMatrix webMatrix = componentsFactory.createComponent(WebMatrix.class);
                MatrixComponent matrixComponent = (MatrixComponent) webMatrix.getComponent();
                matrixComponent.setRequestUrl(generateRequestUrl());
                matrixComponent.setAuthorizationToken(CommonUtils.getAuthorizationToken());

                matrixComponent.setListener(changedItems -> {
                    changes.add(changedItems);
                });

                addChangeListener(matrixComponent);

                fieldGroup.getFieldNN("matrix").setComponent(webMatrix);
            }
        }
    }

    private void removeOpenAction() {
        organizationGroupLookupId.removeAction("open");
        positionGroupLookupId.removeAction("open");
        jobGroupLookupId.removeAction("open");
    }

    private void addChangeListener(MatrixComponent matrixComponent) {
        organizationGroupLookupId.addValueChangeListener(e -> {
            reloadScrumBoard(matrixComponent);
        });

        positionGroupLookupId.addValueChangeListener(e -> {
            reloadScrumBoard(matrixComponent);
        });

        jobGroupLookupId.addValueChangeListener(e -> {
            reloadScrumBoard(matrixComponent);
        });
    }

    private void reloadScrumBoard(MatrixComponent matrixComponent) {
        matrixComponent.setRequestUrl(generateRequestUrl());
        matrixComponent.setReload();
        matrixComponent.setAuthorizationToken(CommonUtils.getAuthorizationToken());
    }

    private String generateRequestUrl() {
        String url = "/" + AppContext.getProperty("cuba.webContextName") + "/rest/v2/services/tsadv_EmployeeService/getMatrix?type=%s&calibrationSessionId=%s&orgId=%s&positionId=%s&jobId=%s&lang=%s";

        return String.format(url,
                matrixType.name(),
                calibrationSession.getId().toString(),
                checkObject(organizationGroupsDs.getItem()),
                checkObject(positionGroupsDs.getItem()),
                checkObject(jobGroupsDs.getItem()),
                userSessionSource.getLocale().getLanguage());
    }

    private String checkObject(AbstractParentEntity entity) {
        if (entity != null) {
            return entity.getId().toString();
        }
        return "0";
    }

    public void save() {
        if (!changes.isEmpty() && calibrationSession != null && matrixType != null) {
            BoardUpdateType boardUpdateType = matrixType.equals(MatrixType.PERFORMANCE) ? BoardUpdateType.MATRIX_PERFORMANCE : BoardUpdateType.MATRIX_RISK;
            for (String change : changes) {
                BoardChangedItem item = gson.fromJson(change, BoardChangedItem.class);
                employeeService.updatePerformanceMatrix(item, boardUpdateType, calibrationSession);
            }
        }

        getWindowManager().close(this);
    }

    public void cancel() {
        getWindowManager().close(this);
    }
}