package kz.uco.tsadv.web.absence;

import com.haulmont.bali.util.ParamsMap;
//import com.haulmont.bpm.entity.ProcInstance;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.model.Absence;
import kz.uco.tsadv.modules.personal.model.AbsenceRequest;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.service.EmployeeService;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AbsenceLookup extends AbstractLookup {

    /*@Inject
    protected EmployeeService employeeService;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected CommonService commonService;
    *//*@Inject
    protected CollectionDatasource<ProcInstance, UUID> procInstancesDs;*//*
    @Inject
    protected UserSession userSession;
    @Inject
    protected Metadata metadata;
    @Inject
    protected TabSheet tabSheet;
    @Inject
    protected GroupDatasource<Absence, UUID> absencesDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        tabSheet.addSelectedTabChangeListener(event -> {
            if (event.getSelectedTab().getName().equals("currectInfo")) {
                absencesDs.refresh();
            } else {
                procInstancesDs.refresh();
            }
        });
    }

    public void addAbsence() {
        AssignmentExt assignmentExt = employeeService.getAssignment(userSession.getAttribute(StaticVariable.USER_PERSON_GROUP_ID), "assignment.view");

        if (assignmentExt == null) {
            showNotification(getMessage("noAssignment"));
            return;
        }

        AbsenceRequest absenceRequest = null;
        List<AbsenceRequest> list = commonService.getEntities(AbsenceRequest.class,
                " select distinct e from tsadv$AbsenceRequest e " +
                        " where e.assignmentGroup.id = :group " +
                        "       and e.status.code = 'DRAFT' ",
                ParamsMap.of("group", assignmentExt.getGroup().getId()), "absenceRequest.view");
        if (list != null && !list.isEmpty()) {
            absenceRequest = list.get(0);
        }
        if (absenceRequest == null) {
            absenceRequest = metadata.create(AbsenceRequest.class);
            absenceRequest.setId(UUID.randomUUID());
            absenceRequest.setAssignmentGroup(assignmentExt.getGroup());
            absenceRequest.setStatus(commonService.getEntity(DicRequestStatus.class, "DRAFT"));
        }
        AbstractWindow abstractWindow = openEditor("tsadv$AbsenceRequest.edit", absenceRequest, WindowManager.OpenType.THIS_TAB);
        abstractWindow.addCloseListener(actionId -> procInstancesDs.refresh());
    }

    *//*public Component generateStatusCell(ProcInstance entity) {
        Label label = componentsFactory.createComponent(Label.class);
        AbsenceRequest absenceRequest = (AbsenceRequest) getEntity(entity);
        if(absenceRequest !=null ) {
            label.setValue(absenceRequest.getStatus());
        }
        return label;
    }*//*

    public Component generateEntityNameCell(ProcInstance entity) {
        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        linkButton.setCaption(getMessage("tsadv$AbsenceRequest"));
        linkButton.setAction(new BaseAction("entityAction") {
            @Override
            public void actionPerform(Component component) {
                super.actionPerform(component);
                openEntity(entity);
            }
        });
        return linkButton;
    }

    protected void openEntity(ProcInstance entity) {
        openEditor("tsadv$AbsenceRequest.edit", getEntity(entity), WindowManager.OpenType.THIS_TAB);
    }

    protected Entity getEntity(ProcInstance entity) {
        return commonService.getEntity(AbsenceRequest.class,
                " select e from tsadv$AbsenceRequest e where e.id = :id ",
                ParamsMap.of("id", entity.getEntity().getEntityId()), "absenceRequest.view");
    }

    public void onBalanceBtnClick() {
        openWindow("tsadv$myAbsenceBalance", WindowManager.OpenType.THIS_TAB);
    }*/

}