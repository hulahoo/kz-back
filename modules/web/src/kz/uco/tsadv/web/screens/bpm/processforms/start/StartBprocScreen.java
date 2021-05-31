package kz.uco.tsadv.web.screens.bpm.processforms.start;

import com.haulmont.addon.bproc.web.processform.*;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.actions.list.RemoveAction;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.data.table.ContainerTableItems;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionPropertyContainer;
import com.haulmont.cuba.gui.model.DataComponents;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.tsadv.entity.bproc.StartBprocParams;
import kz.uco.tsadv.exceptions.ItemNotFoundException;
import kz.uco.tsadv.exceptions.PortalException;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.bpm.BpmRolesDefiner;
import kz.uco.tsadv.modules.bpm.BpmRolesLink;
import kz.uco.tsadv.modules.bpm.NotPersisitBprocActors;
import kz.uco.tsadv.modules.personal.dictionary.DicHrRole;
import kz.uco.tsadv.service.BprocService;
import kz.uco.tsadv.service.OrganizationHrUserService;
import kz.uco.tsadv.service.portal.StartBprocService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * For initiator :
 * <ul>
 *     <li>rejectNotificationTemplateCode - send after reject</li>
 *     <li>approveNotificationTemplateCode - send after approve</li>
 *     <li>initiatorNotificationTemplateCode - send after complete task</li>
 * </ul>
 * <p>
 * For approver : approverNotificationTemplateCode - send after create task
 *
 * @author Alibek Berdaulet
 */
@UiController("tsadv_StartBprocScreen")
@UiDescriptor("start-bproc-screen.xml")
@ProcessForm(params = {
        @Param(name = "approverNotificationTemplateCode"),
        @Param(name = "initiatorNotificationTemplateCode"),
        @Param(name = "rejectNotificationTemplateCode"),
        @Param(name = "approveNotificationTemplateCode"),
})
public class StartBprocScreen extends Screen {

    @Inject
    protected InstanceContainer<BpmRolesDefiner> bpmRolesDefinerDc;
    @Inject
    protected CollectionContainer<DicHrRole> hrRolesDc;
    @Inject
    protected CollectionPropertyContainer<BpmRolesLink> linksDc;
    @Inject
    protected CollectionContainer<NotPersisitBprocActors> notPersisitBprocActorsDc;

    @Inject
    protected ProcessFormContext processFormContext;
    @Inject
    protected Metadata metadata;
    @Inject
    protected UiComponents uiComponents;
    @Inject
    protected UserSession userSession;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected OrganizationHrUserService organizationHrUserService;
    @Inject
    protected DataComponents dataComponents;
    @Inject
    protected BprocService bprocService;
    @Inject
    protected Notifications notifications;
    @Inject
    protected StartBprocService startBprocService;
    @ProcessFormParam
    @SuppressWarnings("unused")
    private String approverNotificationTemplateCode;
    @ProcessFormParam
    @SuppressWarnings("unused")
    private String initiatorNotificationTemplateCode;
    @ProcessFormParam
    @SuppressWarnings("unused")
    private String rejectNotificationTemplateCode;
    @ProcessFormParam
    @SuppressWarnings("unused")
    private String approveNotificationTemplateCode;
    @Inject
    protected MessageBundle messageBundle;

    @Inject
    protected HBoxLayout addHrRoleHbox;
    @Named("bprocActorsTable.remove")
    protected RemoveAction<NotPersisitBprocActors> bprocActorsTableRemove;
    @Inject
    protected LookupField<DicHrRole> addHrRoleLookup;

    protected StartBprocParams startBprocParams;

    public void setStartBprocParams(StartBprocParams startBprocParams) {
        this.startBprocParams = startBprocParams;
    }

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        Assert.notNull(startBprocParams, "startBprocParams is null");
        Assert.notNull(startBprocParams.getRequest(), "entity is null");
        Assert.notNull(startBprocParams.getInitiatorPersonGroupId(), "personGroupId is null");

        try {
            bpmRolesDefinerDc.setItem(startBprocService.getBpmRolesDefiner(
                    startBprocParams.getRequest().getProcessDefinitionKey(),
                    startBprocParams.getInitiatorPersonGroupId()));
            initNotPersisitBprocActors();
            initHrRolesDcItems();
        } catch (PortalException e) {
            throw new ItemNotFoundException(e.getMessage());
        }
    }

    protected void initHrRolesDcItems() {
        hrRolesDc.setItems(linksDc.getItems().stream()
                .filter(BpmRolesLink::getIsAddableApprover)
                .map(BpmRolesLink::getHrRole)
                .collect(Collectors.toList()));
        addHrRoleHbox.setVisible(!hrRolesDc.getItems().isEmpty());
    }

    protected void initNotPersisitBprocActors() {
        List<NotPersisitBprocActors> notPersisitBprocActors = startBprocService.getNotPersisitBprocActors(
                startBprocParams.getEmployee(),
                startBprocParams.getInitiatorPersonGroupId(),
                bpmRolesDefinerDc.getItem());
        notPersisitBprocActorsDc.setItems(notPersisitBprocActors);
    }

    @Subscribe("ok")
    protected void onOkClick(Button.ClickEvent event) {

        if (!validate()) return;

        startBprocService.saveBprocActors(startBprocParams.getRequest().getId(), notPersisitBprocActorsDc.getItems());

        ProcessStarting processStarting = processFormContext.processStarting()
                .withBusinessKey(startBprocParams.getRequest().getId().toString())
                .addProcessVariable("entity", startBprocParams.getRequest())
                .addProcessVariable("initiator", userSession.getUser())
                .addProcessVariable("rolesLinks", linksDc.getItems())
                .addProcessVariable("approverNotificationTemplateCode", approverNotificationTemplateCode)
                .addProcessVariable("rejectNotificationTemplateCode", rejectNotificationTemplateCode)
                .addProcessVariable("approveNotificationTemplateCode", approveNotificationTemplateCode)
                .addProcessVariable("initiatorNotificationTemplateCode", initiatorNotificationTemplateCode);

        Map<String, Object> params = startBprocParams.getParams();
        if (params != null && !params.isEmpty()) params.forEach(processStarting::addProcessVariable);

        processStarting.start();

        close(WINDOW_COMMIT_AND_CLOSE_ACTION);
    }

    protected boolean validate() {
        for (NotPersisitBprocActors item : notPersisitBprocActorsDc.getItems()) {
            List<TsadvUser> users = item.getUsers();
            if (users == null || users.isEmpty()) {
                notifications.create(Notifications.NotificationType.TRAY)
                        .withDescription(messageBundle.getMessage("fill.user"))
                        .show();
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings({"UnstableApiUsage", "unchecked"})
    public Component usersGenerator(NotPersisitBprocActors entity) {
        List<TsadvUser> users = entity.getUsers();
        if (users == null || users.size() == 1) {
            PickerField<TsadvUser> pickerField = uiComponents.create(PickerField.NAME);
            pickerField.setEditable(entity.getIsEditable());
            pickerField.setRequired(true);
            pickerField.setMetaClass(metadata.getClassNN(TsadvUser.class));
            pickerField.addAction(PickerField.LookupAction.create(pickerField));
            pickerField.addAction(PickerField.ClearAction.create(pickerField));
            pickerField.setOptionCaptionProvider(userExt -> StringUtils.defaultString(userExt != null ? userExt.getFullNameWithLogin() : null, ""));
            if (users != null && !users.isEmpty()) pickerField.setValue(users.get(0));
            else pickerField.setValue(null);
            pickerField.addValueChangeListener(event -> {
                TsadvUser value = event.getValue();
                if (value != null) {
                    entity.setUsers(Collections.singletonList(dataManager.reload(value, "user-fioWithLogin")));
                } else entity.setUsers(null);
            });
            return pickerField;
        } else {
            PopupView popupView = uiComponents.create(PopupView.class);
            popupView.setMinimizedValue(users.get(0).getFullNameWithLogin() + " +" + (users.size() - 1));

            CollectionContainer<TsadvUser> container = dataComponents.createCollectionContainer(TsadvUser.class);

            container.setItems(users);

            Table<TsadvUser> table = uiComponents.create(Table.class);
            table.addGeneratedColumn("fullNameWithLogin", user -> {
                Label<String> lbl = uiComponents.create(Label.TYPE_STRING);
                lbl.setValue(user.getFullNameWithLogin());
                return lbl;
            });
            table.setItems(new ContainerTableItems<>(container));
            table.setColumnHeaderVisible(false);
            table.setShowSelection(false);
            table.setWidthAuto();

            popupView.setPopupContent(table);
            return popupView;
        }
    }

    @Subscribe("cancel")
    protected void onCancelClick(Button.ClickEvent event) {
        closeWithDefaultAction();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Subscribe("addHrRoleLookup")
    protected void onAddHrRoleLookupValueChange(HasValue.ValueChangeEvent<DicHrRole> event) {
        DicHrRole role = event.getValue();
        if (role != null) {
            BpmRolesLink rolesLink = linksDc.getItems().stream()
                    .filter(bpmRolesLink -> bpmRolesLink.getHrRole().equals(role))
                    .findAny().get();
            Integer order = rolesLink.getOrder();

            NotPersisitBprocActors bprocActors = metadata.create(NotPersisitBprocActors.class);
            bprocActors.setHrRole(role);
            bprocActors.setBprocUserTaskCode(rolesLink.getBprocUserTaskCode());
            bprocActors.setOrder(rolesLink.getOrder());
            bprocActors.setRolesLink(rolesLink);

            List<NotPersisitBprocActors> mutableItems = notPersisitBprocActorsDc.getMutableItems();
            boolean isAdded = false;
            for (int i = mutableItems.size() - 1; i >= 0; i--) {
                NotPersisitBprocActors actor = mutableItems.get(i);
                if (Objects.equals(order, actor.getOrder())
                        || order >= actor.getOrder()) {
                    mutableItems.add(i + 1, bprocActors);
                    isAdded = true;
                    break;
                }
            }
            if (!isAdded) mutableItems.add(bprocActors);
            addHrRoleLookup.setValue(null);
        }
    }

    @Subscribe(id = "notPersisitBprocActorsDc", target = Target.DATA_CONTAINER)
    protected void onNotPersisitBprocActorsDcItemChange(InstanceContainer.ItemChangeEvent<NotPersisitBprocActors> event) {
        NotPersisitBprocActors item = event.getItem();
        if (item == null) return;

        bprocActorsTableRemove.setEnabled(!item.getIsSystemRecord());
    }

}