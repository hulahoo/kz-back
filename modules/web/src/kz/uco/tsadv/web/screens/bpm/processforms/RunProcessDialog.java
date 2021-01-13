package kz.uco.tsadv.web.screens.bpm.processforms;

import com.haulmont.addon.bproc.entity.ProcessInstanceData;
import com.haulmont.addon.bproc.service.BprocRuntimeService;
import com.haulmont.addon.bproc.web.processform.ProcessForm;
import com.haulmont.addon.bproc.web.processform.ProcessFormContext;
import com.haulmont.addon.bproc.web.processform.ProcessVariable;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.data.options.ContainerOptions;
import com.haulmont.cuba.gui.model.CollectionChangeType;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.bpm.BpmRolesLink;
import kz.uco.tsadv.modules.bpm.BprocInstanceRolesLink;
import kz.uco.tsadv.modules.personal.dictionary.DicHrRole;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@UiController("tsadv_RunProcessDialog")
@UiDescriptor("run-process-dialog.xml")
@ProcessForm
public class RunProcessDialog extends Screen {
    @Inject
    protected BprocRuntimeService bprocRuntimeService;
    @Inject
    protected TextArea<String> comment;
    @Inject
    protected CollectionLoader<BprocInstanceRolesLink> bprocInstanceRolesLinksDl;
    @Inject
    protected CollectionContainer<BprocInstanceRolesLink> bprocInstanceRolesLinksDc;
    @Inject
    protected Metadata metadata;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected ProcessFormContext processFormContext;
    @Inject
    private UiComponents uiComponents;
    @Inject
    private CollectionContainer<DicHrRole> dicHrRolesDc;
    @Inject
    private CollectionLoader<DicHrRole> dicHrRolesDl;

    protected String processDefinitionKey;
    protected Map<BprocInstanceRolesLink, LookupField<UserExt>> userListsMap = new HashMap<>();
    @Inject
    private UserSession userSession;

    @Subscribe
    public void onInit(InitEvent event) {
        dicHrRolesDl.load();
        bprocInstanceRolesLinksDc.addCollectionChangeListener(collectionChangeEvent -> {
            List<DicHrRole> changedList = collectionChangeEvent.getChanges().stream()
                    .map(BprocInstanceRolesLink::getHrRole)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            if (collectionChangeEvent.getChangeType().equals(CollectionChangeType.REMOVE_ITEMS)) {
                dicHrRolesDc.getMutableItems().addAll(changedList);
            } else if (collectionChangeEvent.getChangeType().equals(CollectionChangeType.ADD_ITEMS)) {
                dicHrRolesDc.getMutableItems().removeAll(changedList);
            }
        });
        bprocInstanceRolesLinksDc.addItemPropertyChangeListener(itemPropertyChangeEvent -> {
            if ("hrRole".equals(itemPropertyChangeEvent.getProperty())) {
                if (itemPropertyChangeEvent.getPrevValue() != null) {
                    dicHrRolesDc.getMutableItems().add(((DicHrRole) itemPropertyChangeEvent.getPrevValue()));
                }
                if (itemPropertyChangeEvent.getValue() != null) {
                    dicHrRolesDc.getMutableItems().remove(itemPropertyChangeEvent.getValue());
                }
                userListsMap.get(itemPropertyChangeEvent.getItem()).setOptionsList(getUsersByRole(itemPropertyChangeEvent.getItem().getHrRole()));
            }
        });
        bprocInstanceRolesLinksDl.setParameter("processInstanceId", processFormContext.getFormData().getBusinessKey());
        processDefinitionKey = processFormContext.getProcessDefinitionData().getKey();

        initRoute(processDefinitionKey);
    }

    protected void initRoute(String processDefinitionKey) {
        List<BpmRolesLink> bpmRolesLinks = dataManager.load(BpmRolesLink.class)
                .query("select e from tsadv$BpmRolesLink e" +
                        " where e.bpmRolesDefiner.processDefinitionKey = :processDefinitionKey")
                .parameter("processDefinitionKey", processDefinitionKey)
                .view("bpmRolesLink-view")
                .list();
        for (BpmRolesLink bpmRolesLink : bpmRolesLinks) {
            if (bpmRolesLink.getRequired()) {
                BprocInstanceRolesLink bprocInstanceRolesLink = metadata.create(BprocInstanceRolesLink.class);
                bprocInstanceRolesLink.setHrRole(bpmRolesLink.getHrRole());
                bprocInstanceRolesLink.setRequired(true);
                BprocInstanceRolesLink trackedBprocInstanceRolesLink = getScreenData().getDataContext().merge(bprocInstanceRolesLink);
                bprocInstanceRolesLinksDc.getMutableItems().add(trackedBprocInstanceRolesLink);
            }
        }
    }

    @Subscribe("startProcessBtn")
    public void onStartProcessBtnClick(Button.ClickEvent event) {
        saveRoute();
        ProcessInstanceData processInstanceData = bprocRuntimeService.startProcessInstanceByKey(
                processDefinitionKey,
                processFormContext.getFormData().getBusinessKey(),
                ParamsMap.of("startComment", comment.getValue(),
                        "entityId", processFormContext.getFormData().getBusinessKey(),
                        "initiator", userSession.getUser().getId().toString(),
                        "entityName", processFormContext.getFormData().getFormParams().stream()
                                .filter(formParam -> formParam.getName().equals("entityName"))
                                .map(formParam -> formParam.getValue())
                                .findAny().orElse(null),
                        "processDefinitionKey", processDefinitionKey));
        close(new StandardCloseAction(Window.COMMIT_ACTION_ID));
    }

    protected void saveRoute() {
        for (BprocInstanceRolesLink mutableItem : bprocInstanceRolesLinksDc.getMutableItems()) {
            mutableItem.setProcessInstanceId(processFormContext.getFormData().getBusinessKey());
            BprocInstanceRolesLink trackedBprocInstanceRolesLink = getScreenData().getDataContext().merge(mutableItem);
        }
        getScreenData().getDataContext().commit();
    }

    @Subscribe("bprocInstanceRolesLinksTable.create")
    public void onBprocInstanceRolesLinksTableCreate(Action.ActionPerformedEvent event) {
        BprocInstanceRolesLink bprocInstanceRolesLink = metadata.create(BprocInstanceRolesLink.class);
        bprocInstanceRolesLink.setProcessInstanceId(processFormContext.getFormData().getBusinessKey());
        bprocInstanceRolesLinksDc.getMutableItems().add(bprocInstanceRolesLink);
    }

    public Component hrRolesGenerate(BprocInstanceRolesLink bprocInstanceRolesLink) {
            LookupField<DicHrRole> lookupField = uiComponents.create(LookupField.of(DicHrRole.class));
            lookupField.setValue(bprocInstanceRolesLink.getHrRole());
            lookupField.setEditable(!Optional.ofNullable(bprocInstanceRolesLink.isRequired()).orElse(true));
            lookupField.addValueChangeListener(dicHrRoleValueChangeEvent -> {
                bprocInstanceRolesLink.setHrRole(dicHrRoleValueChangeEvent.getValue());
                bprocInstanceRolesLinksDc.setItem(getScreenData().getDataContext().merge(bprocInstanceRolesLink));
            });
            lookupField.setOptions(new ContainerOptions(dicHrRolesDc));
            lookupField.setRequired(true);
            lookupField.setNullOptionVisible(false);
            lookupField.setWidth("200px");
            return lookupField;
    }

    public Component userGenerate(BprocInstanceRolesLink bprocInstanceRolesLink) {
        if (bprocInstanceRolesLink.isRequired()) {
            Label<String> label = uiComponents.create(Label.class);
            String users = getUsersByRole(bprocInstanceRolesLink.getHrRole()).stream()
                    .map(User::getLogin)
                    .collect(Collectors.joining(", "));
            label.setValue(users);
            PopupView popupView = uiComponents.create(PopupView.class);
            popupView.setHideOnMouseOut(false);
            popupView.setMinimizedValue("Users");
            popupView.setPopupContent(label);
            return popupView;
        } else {
            LookupField<UserExt> lookupField = uiComponents.create(LookupField.of(UserExt.class));
            lookupField.setRequired(true);
            lookupField.setWidth("200px");
            List<UserExt> userExtList = getUsersByRole(bprocInstanceRolesLink.getHrRole());
            lookupField.setOptionsList(userExtList);
            lookupField.setValue(bprocInstanceRolesLink.getUser());
            lookupField.setEditable(!bprocInstanceRolesLink.isRequired());
            lookupField.addValueChangeListener(userExtValueChangeEvent -> {
                bprocInstanceRolesLink.setUser(userExtValueChangeEvent.getValue());
                bprocInstanceRolesLinksDc.setItem(getScreenData().getDataContext().merge(bprocInstanceRolesLink));
            });
            userListsMap.put(bprocInstanceRolesLink, lookupField);
            return lookupField;
        }
    }

    protected List<UserExt> getUsersByRole(DicHrRole hrRole) {
        if (hrRole == null) {
            return new ArrayList<>();
        }
        List<UserExt> userList = dataManager.load(UserExt.class)
                .query("select e.user from tsadv$HrUserRole e" +
                        " where e.role.id = :hrRoleId")
                .parameter("hrRoleId", hrRole.getId())
                .view(View.LOCAL)
                .list();
        return userList;
    }

    public Component deleteGenerate(Entity entity) {
        Button button = uiComponents.create(Button.class);
        button.setIcon("font-icon:CLOSE");
        button.setAction(new BaseAction("remove") {
            @Override
            public void actionPerform(Component component) {
                bprocInstanceRolesLinksDc.getMutableItems().remove(entity);
                getScreenData().getDataContext().remove(entity);
            }
        });
        return button;
    }
}