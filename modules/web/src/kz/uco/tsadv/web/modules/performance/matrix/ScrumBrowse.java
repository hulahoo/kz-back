package kz.uco.tsadv.web.modules.performance.matrix;

import com.google.gson.Gson;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.LookupPickerField;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.performance.dto.BoardChangedItem;
import kz.uco.tsadv.modules.performance.dto.BoardUpdateType;
import kz.uco.tsadv.modules.performance.model.CalibrationSession;
import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.web.gui.components.WebScrumBoard;
import kz.uco.tsadv.web.toolkit.ui.scrumboardcomponent.ScrumBoardComponent;
import kz.uco.tsadv.service.EmployeeService;

import javax.inject.Inject;
import java.util.*;

public class ScrumBrowse extends AbstractWindow {

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

    @Inject
    private DataManager dataManager;

    private List<String> changes = new ArrayList<>();

    private ScrumType scrumType;
    private CalibrationSession calibrationSession;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        removeOpenAction();

        if (params.containsKey(StaticVariable.CALIBRATION_SESSION)) {
            calibrationSession = (CalibrationSession) params.get(StaticVariable.CALIBRATION_SESSION);

            if (params.containsKey(StaticVariable.SCRUM_TYPE)) {
                scrumType = (ScrumType) params.get(StaticVariable.SCRUM_TYPE);

                setCaption(getMessage("ScrumBrowse." + scrumType.name()));

                WebScrumBoard webScrumBoard = componentsFactory.createComponent(WebScrumBoard.class);

                ScrumBoardComponent scrumBoardComponent = (ScrumBoardComponent) webScrumBoard.getComponent();
                scrumBoardComponent.setRequestUrl(generateRequestUrl());
                scrumBoardComponent.setListener(changedItems -> {
                    changes.add(changedItems);
                });
                scrumBoardComponent.setAuthorizationToken(CommonUtils.getAuthorizationToken());

                addChangeListener(scrumBoardComponent);

                fieldGroup.getFieldNN("scrumBoard").setComponent(webScrumBoard);
            }
        }
    }

    private void removeOpenAction() {
        organizationGroupLookupId.removeAction("open");
        positionGroupLookupId.removeAction("open");
        jobGroupLookupId.removeAction("open");
    }

    private void addChangeListener(ScrumBoardComponent scrumBoardComponent) {
        organizationGroupLookupId.addValueChangeListener(e -> {
            reloadScrumBoard(scrumBoardComponent);
        });

        positionGroupLookupId.addValueChangeListener(e -> {
            reloadScrumBoard(scrumBoardComponent);
        });

        jobGroupLookupId.addValueChangeListener(e -> {
            reloadScrumBoard(scrumBoardComponent);
        });

        scrumBoardComponent.setActionClickListener((personGroupId, actionType) -> {
            if (actionType.equalsIgnoreCase("beautyTree")) {
                openWindow(actionType, WindowManager.OpenType.THIS_TAB, new HashMap<String, Object>() {{
                    put("personGroupId", personGroupId);
                }});
            } else {
                LoadContext<PersonGroupExt> loadContext = LoadContext.create(PersonGroupExt.class);
                loadContext.setQuery(
                        LoadContext.createQuery("select p from base$PersonGroupExt p where p.id = :id")
                                .setParameter("id", UUID.fromString(personGroupId)))
                        .setView("personGroup.scrum.competence");

                PersonGroupExt personGroup = dataManager.load(loadContext);

                if (personGroup != null && personGroup.getPerson() != null) {
                    WindowConfig windowConfig = AppBeans.get(WindowConfig.NAME);
                    WindowInfo windowInfo = windowConfig.getWindowInfo(actionType);
                    getWindowManager().openEditor(windowInfo, personGroup.getPerson(), WindowManager.OpenType.THIS_TAB);
                }
            }
        });
    }

    private void reloadScrumBoard(ScrumBoardComponent scrumBoardComponent) {
        scrumBoardComponent.setRequestUrl(generateRequestUrl());
        scrumBoardComponent.setReload();
        scrumBoardComponent.setAuthorizationToken(CommonUtils.getAuthorizationToken());
    }

    private String generateRequestUrl() {
        /*String url = "/tal/rest/v2/services/tsadv_EmployeeService/getPersonForScrum?byType=%s&orgId=%s&positionId=%s&jobId=%s&calibrationSessionId=%s&lang=%s";*/
        String url = "/" + AppContext.getProperty("cuba.webContextName") + "/rest/v2/services/tsadv_EmployeeService/getPersonForScrum?byType=%s&orgId=%s&positionId=%s&jobId=%s&calibrationSessionId=%s&lang=%s";

        return String.format(url,
                scrumType.name(),
                checkObject(organizationGroupsDs.getItem()),
                checkObject(positionGroupsDs.getItem()),
                checkObject(jobGroupsDs.getItem()),
                calibrationSession.getId().toString(),
                userSessionSource.getLocale().getLanguage());
    }

    private String checkObject(AbstractParentEntity entity) {
        if (entity != null) {
            return entity.getId().toString();
        }
        return "0";
    }

    public void save() {
        if (!changes.isEmpty() && calibrationSession != null && scrumType != null) {
            BoardUpdateType boardUpdateType = scrumType.equals(ScrumType.COMPETENCE) ? BoardUpdateType.SCRUM_COMPETENCE : BoardUpdateType.SCRUM_GOAL;
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

    public enum ScrumType {
        COMPETENCE,
        GOAL
    }

}