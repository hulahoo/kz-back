package kz.uco.tsadv.web.screens.bpm.processforms.start;

import com.haulmont.addon.bproc.web.processform.*;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.CommitContext;
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
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.exceptions.ItemNotFoundException;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.bpm.BpmRolesDefiner;
import kz.uco.tsadv.modules.bpm.BpmRolesLink;
import kz.uco.tsadv.modules.bpm.BprocActors;
import kz.uco.tsadv.modules.bpm.NotPersisitBprocActors;
import kz.uco.tsadv.modules.personal.dictionary.DicHrRole;
import kz.uco.tsadv.service.BprocService;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.service.OrganizationHrUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Alibek Berdaulet
 */
@UiController("tsadv_StartBprocScreen")
@UiDescriptor("start-bproc-screen.xml")
@ProcessForm(params = {
        @Param(name = "approverNotificationTemplateCode"),
        @Param(name = "initiatorNotificationTemplateCode")
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
    @ProcessFormParam
    @SuppressWarnings("unused")
    private String approverNotificationTemplateCode;
    @ProcessFormParam
    @SuppressWarnings("unused")
    private String initiatorNotificationTemplateCode;
    @Inject
    protected MessageBundle messageBundle;

    @Inject
    protected HBoxLayout addHrRoleHbox;
    @Named("bprocActorsTable.remove")
    protected RemoveAction<NotPersisitBprocActors> bprocActorsTableRemove;
    @Inject
    protected LookupField<DicHrRole> addHrRoleLookup;

    protected AbstractBprocRequest entity;
    protected UUID personGroupId;
    protected UserExt employee;
    protected Supplier<Map<String, Object>> processVariableSupplier;

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        Assert.notNull(entity, "entity is null");
        Assert.notNull(personGroupId, "personGroupId is null");

        bpmRolesDefinerDc.setItem(getBpmRolesDefiner());
        initNotPersisitBprocActors();
        initHrRolesDcItems();
    }

    protected void initHrRolesDcItems() {
        hrRolesDc.setItems(linksDc.getItems().stream()
                .filter(BpmRolesLink::getIsAddableApprover)
                .map(BpmRolesLink::getHrRole)
                .collect(Collectors.toList()));
        addHrRoleHbox.setVisible(!hrRolesDc.getItems().isEmpty());
    }

    @SuppressWarnings("unchecked")
    protected void initNotPersisitBprocActors() {
        List<NotPersisitBprocActors> actors = new ArrayList<>();
        BpmRolesDefiner definer = bpmRolesDefinerDc.getItem();
        List<BpmRolesLink> links = definer.getLinks();

        for (BpmRolesLink link : links) {
            DicHrRole hrRole = link.getHrRole();
            String roleCode = hrRole.getCode();
            List<? extends User> hrUsersForPerson = new ArrayList<>();

            if (roleCode.equals("EMPLOYEE"))
                if (employee != null)
                    hrUsersForPerson = Collections.singletonList(dataManager.reload(employee, "user-fioWithLogin"));
                else
                    hrUsersForPerson = dataManager.load(UserExt.class)
                            .query("select e from tsadv$UserExt e where e in :users")
                            .setParameters(ParamsMap.of("users", organizationHrUserService.getHrUsersForPerson(personGroupId, roleCode)))
                            .view("user-fioWithLogin")
                            .list();

            if (hrUsersForPerson.isEmpty()) {
                if (!link.getIsAddableApprover())
                    throw new ItemNotFoundException(String.format(messageBundle.getMessage("hr.user.not.found"), hrRole.getLangValue()));

                if (link.getRequired()) {
                    NotPersisitBprocActors bprocActors = createNotPersisitBprocActors(link, hrRole);
                    bprocActors.setIsEditable(true);
                    actors.add(bprocActors);
                }
            } else {
                NotPersisitBprocActors bprocActors = createNotPersisitBprocActors(link, hrRole);
                bprocActors.setIsEditable(false);
                bprocActors.setUsers((List<UserExt>) hrUsersForPerson);
                actors.add(bprocActors);
            }
        }
        notPersisitBprocActorsDc.setItems(actors);
    }

    protected NotPersisitBprocActors createNotPersisitBprocActors(BpmRolesLink link, DicHrRole hrRole) {
        NotPersisitBprocActors bprocActors = metadata.create(NotPersisitBprocActors.class);
        bprocActors.setHrRole(hrRole);
        bprocActors.setIsSystemRecord(true);
        bprocActors.setBprocUserTaskCode(link.getBprocUserTaskCode());
        bprocActors.setOrder(link.getOrder());
        return bprocActors;
    }

    protected BpmRolesDefiner getBpmRolesDefiner() {
        UUID company = getCompany();
        List<BpmRolesDefiner> bpmRolesDefiners = dataManager.load(BpmRolesDefiner.class)
                .query("select e from tsadv$BpmRolesDefiner e where e.company.id = :companyId and e.processDefinitionKey = :processDefinitionKey ")
                .parameter("companyId", company)
                .parameter("processDefinitionKey", entity.getProcessDefinitionKey())
                .view("bpmRolesDefiner-view")
                .list();
        if (!bpmRolesDefiners.isEmpty()) return bpmRolesDefiners.get(0);

        bpmRolesDefiners = dataManager.load(BpmRolesDefiner.class)
                .query("select e from tsadv$BpmRolesDefiner e where e.processDefinitionKey = :processDefinitionKey")
                .parameter("processDefinitionKey", entity.getProcessDefinitionKey())
                .view("bpmRolesDefiner-view")
                .list()
                .stream()
                .filter(bpmRolesDefiner -> bpmRolesDefiner.getCompany() == null)
                .collect(Collectors.toList());

        if (!bpmRolesDefiners.isEmpty()) return bpmRolesDefiners.get(0);
        throw new RuntimeException("bpmRolesDefiner not found!");
    }

    protected UUID getCompany() {
        DicCompany dicCompany = getBeanLocator().get(EmployeeService.class).getCompanyByPersonGroupId(personGroupId);
        return dicCompany != null ? dicCompany.getId() : null;
    }

    @Subscribe("ok")
    protected void onOkClick(Button.ClickEvent event) {

        if (!validate()) return;

        CommitContext commitContext = new CommitContext();

        UUID entityId = entity.getId();
        dataManager.load(BprocActors.class)
                .query("select e from tsadv_BprocActors e where e.entityId = :entityId")
                .setParameters(ParamsMap.of("entityId", entityId))
                .list()
                .forEach(commitContext::addInstanceToRemove);

        for (NotPersisitBprocActors notPersisitBprocActors : notPersisitBprocActorsDc.getItems()) {
            BprocActors bprocActors = metadata.create(BprocActors.class);
            bprocActors.setEntityId(entityId);
            bprocActors.setBprocUserTaskCode(notPersisitBprocActors.getBprocUserTaskCode());
            bprocActors.setHrRole(notPersisitBprocActors.getHrRole());

            for (UserExt user : notPersisitBprocActors.getUsers()) {
                BprocActors copy = metadata.getTools().copy(bprocActors);
                copy.setId(UUID.randomUUID());
                copy.setUser(user);
                commitContext.addInstanceToCommit(copy);
            }
        }

        dataManager.commit(commitContext);

        ProcessStarting processStarting = processFormContext.processStarting()
                .withBusinessKey(entityId.toString())
                .addProcessVariable("entity", entity)
                .addProcessVariable("initiator", userSession.getUser())
                .addProcessVariable("rolesLinks", linksDc.getItems())
                .addProcessVariable("approverNotificationTemplateCode", approverNotificationTemplateCode)
                .addProcessVariable("initiatorNotificationTemplateCode", initiatorNotificationTemplateCode);

        Map<String, Object> params = processVariableSupplier.get();
        if (params != null && !params.isEmpty()) params.forEach(processStarting::addProcessVariable);

        processStarting.start();

        close(WINDOW_COMMIT_AND_CLOSE_ACTION);
    }

    protected boolean validate() {
        for (NotPersisitBprocActors item : notPersisitBprocActorsDc.getItems()) {
            List<UserExt> users = item.getUsers();
            if (users == null || users.isEmpty()) {
                notifications.create(Notifications.NotificationType.TRAY)
                        .withDescription(messageBundle.getMessage("fill.user"))
                        .show();
                return false;
            }
        }
        return true;
    }

    public void setEntity(AbstractBprocRequest entity) {
        this.entity = entity;
    }

    public void setEmployee(UserExt employee) {
        this.employee = employee;
    }

    //todo personGroupId is session user person group id
    public void setPersonGroupId(UUID personGroupId) {
        this.personGroupId = personGroupId;
    }

    public void setProcessVariableSupplier(Supplier<Map<String, Object>> processVariableSupplier) {
        this.processVariableSupplier = processVariableSupplier;
    }

    @SuppressWarnings({"UnstableApiUsage", "unchecked"})
    public Component usersGenerator(NotPersisitBprocActors entity) {
        List<UserExt> users = entity.getUsers();
        if (users == null || users.size() == 1) {
            PickerField<UserExt> pickerField = uiComponents.create(PickerField.NAME);
            pickerField.setEditable(entity.getIsEditable());
            pickerField.setRequired(true);
            pickerField.setMetaClass(metadata.getClassNN(UserExt.class));
            pickerField.addAction(PickerField.LookupAction.create(pickerField));
            pickerField.addAction(PickerField.ClearAction.create(pickerField));
            pickerField.setOptionCaptionProvider(userExt -> StringUtils.defaultString(userExt != null ? userExt.getFullNameWithLogin() : null, ""));
            if (users != null && !users.isEmpty()) pickerField.setValue(users.get(0));
            else pickerField.setValue(null);
            pickerField.addValueChangeListener(event -> {
                UserExt value = event.getValue();
                if (value != null) {
                    entity.setUsers(Collections.singletonList(dataManager.reload(value, "user-fioWithLogin")));
                } else entity.setUsers(null);
            });
            return pickerField;
        } else {
            PopupView popupView = uiComponents.create(PopupView.class);
            popupView.setMinimizedValue(users.get(0).getFullNameWithLogin() + " +" + (users.size() - 1));

            CollectionContainer<UserExt> container = dataComponents.createCollectionContainer(UserExt.class);

            container.setItems(users);

            Table<UserExt> table = uiComponents.create(Table.class);
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
            Integer order = linksDc.getItems().stream()
                    .filter(bpmRolesLink -> bpmRolesLink.getHrRole().equals(role))
                    .findAny().get().getOrder();
            NotPersisitBprocActors bprocActors = metadata.create(NotPersisitBprocActors.class);
            bprocActors.setHrRole(role);
            bprocActors.setBprocUserTaskCode(getBprocUserTaskCode(role));
            bprocActors.setOrder(order);
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

    protected String getBprocUserTaskCode(DicHrRole role) {
        return linksDc.getItems().stream().filter(bpmRolesLink -> bpmRolesLink.getHrRole().equals(role))
                .map(BpmRolesLink::getBprocUserTaskCode)
                .findAny().orElseThrow(() -> new RuntimeException(role.getLangValue() + " : BprocUserTaskCode not found!"));
    }

    @Subscribe(id = "notPersisitBprocActorsDc", target = Target.DATA_CONTAINER)
    protected void onNotPersisitBprocActorsDcItemChange(InstanceContainer.ItemChangeEvent<NotPersisitBprocActors> event) {
        NotPersisitBprocActors item = event.getItem();
        if (item == null) return;

        bprocActorsTableRemove.setEnabled(!item.getIsSystemRecord());
    }

}