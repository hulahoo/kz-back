package kz.uco.tsadv.web.modules.personal.transferrequest;

//import com.haulmont.bpm.gui.procactions.ProcActionsFrame;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.gui.components.WebPickerField;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.PositionStructure;
import kz.uco.tsadv.modules.personal.model.TransferRequest;
import kz.uco.base.service.common.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;

public class TransferRequestEdit extends AbstractEditor<TransferRequest> {

    private static final String PROCESS_CODE = "transferRequestApproval";
    private static final Logger log = LoggerFactory.getLogger(TransferRequestEdit.class);

    @Inject
    private FieldGroup fieldGroup;

    @Inject
    private Datasource<TransferRequest> transferRequestDs;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private DataManager dataManager;

//    @Inject
//    private ProcActionsFrame procActionsFrame;

    @Inject
    private CommonService commonService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        customizeLookups();
    }

    @Override
    public void postInit() {
        initProcActionsFrame();

        /*UserExt u = commonService.getEntity(UserExt.class, String.format("select u from tsadv$UserExt u where u.%s = :keyValue", "personGroup.id"), Collections.singletonMap("keyValue", getItem().getAssignmentGroup().getAssignment().getPersonGroup().getId()), "user.edit");
        log.info(u.getLogin() + "");*/
    }

    @Override
    protected void initNewItem(TransferRequest item) {
        super.initNewItem(item);
        item.setRequestStatus(getStatusByCode("NEW"));
    }

    private DicRequestStatus getStatusByCode(String code) {
        DicRequestStatus status = null;

        LoadContext<DicRequestStatus> loadContext = LoadContext.create(DicRequestStatus.class)
                .setQuery(
                        LoadContext.createQuery("select e from tsadv$DicRequestStatus e where e.code = :code")
                                .setParameter("code", code)
                );

        status = dataManager.load(loadContext);

        return status;
    }

    private void customizeLookups() {
        FieldGroup.FieldConfig assignmentGroupConfig = fieldGroup.getField("assignmentGroup");
        PickerField assignmentGroupPickerField = componentsFactory.createComponent(PickerField.class);
        assignmentGroupPickerField.setDatasource(transferRequestDs, assignmentGroupConfig.getProperty());
        assignmentGroupPickerField.addAction(new AbstractAction("customLookup") {
            @Override
            public void actionPerform(Component component) {
                openAssignmentBrowse();
            }

            @Override
            public String getIcon() {
                return "font-icon:ELLIPSIS_H";
            }
        });
        assignmentGroupPickerField.setWidth("100%");
        assignmentGroupPickerField.setCaptionMode(CaptionMode.PROPERTY);
        assignmentGroupPickerField.setCaptionProperty("assignmentPersonFioWithEmployeeNumber");
        assignmentGroupConfig.setComponent(assignmentGroupPickerField);

        FieldGroup.FieldConfig newPositionGroupConfig = fieldGroup.getField("newPositionGroup");
        PickerField newPositionGroupPickerField = componentsFactory.createComponent(PickerField.class);
        newPositionGroupPickerField.setDatasource(transferRequestDs, newPositionGroupConfig.getProperty());
        newPositionGroupPickerField.addAction(new AbstractAction("customLookup") {
            @Override
            public void actionPerform(Component component) {
                openPositionStructureBrowse();
            }

            @Override
            public String getIcon() {
                return "font-icon:ELLIPSIS_H";
            }
        });
        newPositionGroupPickerField.setWidth("100%");
        newPositionGroupPickerField.setCaptionMode(CaptionMode.PROPERTY);
        newPositionGroupPickerField.setCaptionProperty("position");
        newPositionGroupConfig.setComponent(newPositionGroupPickerField);
    }

    private void openAssignmentBrowse() {
        AbstractLookup assignmentBrowse = openLookup("base$Assignment.browse", new Lookup.Handler() {
            @Override
            public void handleLookup(Collection items) {
                for (Object o : items) {
                    AssignmentExt a = (AssignmentExt) o;
                    getItem().setAssignmentGroup(dataManager.reload(a.getGroup(), "assignmentGroup.view"));
                    WebPickerField positionTextFiled = (WebPickerField) fieldGroup.getField("assignmentGroup.assignment.positionGroup.position").getComponent();
                    positionTextFiled.setValue(getItem().getAssignmentGroup().getAssignment().getPositionGroup().getPosition());
                    WebPickerField organizationTextFiled = (WebPickerField) fieldGroup.getField("assignmentGroup.assignment.organizationGroup.organization").getComponent();
                    organizationTextFiled.setValue(getItem().getAssignmentGroup().getAssignment().getOrganizationGroup().getOrganization());
                }
            }
        }, WindowManager.OpenType.DIALOG);
    }


    private void openPositionStructureBrowse() {
        AbstractLookup hierarchyElementBrowse = openLookup("tsadv$PositionStructure.browse", new Lookup.Handler() {
            @Override
            public void handleLookup(Collection items) {
                for (Object o : items) {
                    PositionStructure ps = (PositionStructure) o;
                    getItem().setNewPositionGroup(ps.getPositionGroup());
                    getItem().setNewOrganizationGroup(ps.getOrganizationGroup());
                }
            }
        }, WindowManager.OpenType.DIALOG);
    }

    private void initProcActionsFrame() {
//        procActionsFrame.initializer()
//                .setBeforeStartProcessPredicate(this::commit)
//                .setAfterStartProcessListener(() -> {
//                    showNotification(getMessage("transferRequestApproval.processStarted"), NotificationType.HUMANIZED);
//                    close(COMMIT_ACTION_ID);
//
//                })
//                .setAfterClaimTaskListener(() -> {
//                    this.commit();
//                    initProcActionsFrame();
//                })
//                .setBeforeCompleteTaskPredicate(this::commit)
//                .setAfterCompleteTaskListener(() -> {
//                    showNotification(getMessage("transferRequestApproval.taskCompleted"), NotificationType.HUMANIZED);
//                    close(COMMIT_ACTION_ID);
//                })
//                .init(PROCESS_CODE, getItem());
    }

}