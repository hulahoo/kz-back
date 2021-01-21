package kz.uco.tsadv.web.screens.bpm.processforms;

import com.haulmont.addon.bproc.entity.ProcessInstanceData;
import com.haulmont.addon.bproc.service.BprocRuntimeService;
import com.haulmont.addon.bproc.web.processform.ProcessForm;
import com.haulmont.addon.bproc.web.processform.ProcessFormContext;
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
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.entity.shared.Person;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.bpm.BpmRolesLink;
import kz.uco.tsadv.modules.bpm.BprocInstanceRolesLink;
import kz.uco.tsadv.modules.personal.dictionary.DicHrRole;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;

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
    protected UiComponents uiComponents;
    @Inject
    protected CollectionContainer<DicHrRole> dicHrRolesDc;
    @Inject
    protected CollectionLoader<DicHrRole> dicHrRolesDl;
    @Inject
    protected UserSession userSession;

    protected String processDefinitionKey;
    protected Map<BprocInstanceRolesLink, LookupField<PersonExt>> userListsMap = new HashMap<>();

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
                userListsMap.get(itemPropertyChangeEvent.getItem()).setOptionsList(getPersonsByRole(itemPropertyChangeEvent.getItem().getHrRole()));
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

    public Component personGenerate(BprocInstanceRolesLink bprocInstanceRolesLink) {
        if (bprocInstanceRolesLink.isRequired()) {
            Label<String> label = uiComponents.create(Label.class);
            List<PersonExt> personList = getPersonsByRole(bprocInstanceRolesLink.getHrRole());

            Label<String> fistPersonLabel = uiComponents.create(Label.class);
            PersonExt firstPerson = personList.stream().findFirst().orElse(null);
            if (firstPerson == null) return null;
            fistPersonLabel.setValue(firstPerson.getFioWithEmployeeNumber());
            if (personList.size() > 1) {
                HBoxLayout hBoxLayout = uiComponents.create(HBoxLayout.class);
                personList.remove(firstPerson);
                String persons = personList.stream()
                        .map(PersonExt::getFioWithEmployeeNumber)
                        .collect(Collectors.joining(", "));
                label.setValue(persons);
                PopupView popupView = uiComponents.create(PopupView.class);
                popupView.setHideOnMouseOut(false);
                popupView.setMinimizedValue(", ...");
                popupView.setPopupContent(label);
                hBoxLayout.add(fistPersonLabel);
                hBoxLayout.add(popupView);
                return hBoxLayout;
            }
            return fistPersonLabel;
        } else {
            LookupField<PersonExt> lookupField = uiComponents.create(LookupField.of(PersonExt.class));
            lookupField.setOptionCaptionProvider(Person::getFioWithEmployeeNumber);
            lookupField.setRequired(true);
            lookupField.setWidth("200px");
            List<PersonExt> personExtList = getPersonsByRole(bprocInstanceRolesLink.getHrRole());
            lookupField.setOptionsList(personExtList);
            if (bprocInstanceRolesLink.getUser() != null
                    && bprocInstanceRolesLink.getUser().getPersonGroup() != null
                    && bprocInstanceRolesLink.getUser().getPersonGroup().getPerson() != null) {
                lookupField.setValue(bprocInstanceRolesLink.getUser().getPersonGroup().getPerson());
            }
            lookupField.setEditable(!bprocInstanceRolesLink.isRequired());
            lookupField.addValueChangeListener(userExtValueChangeEvent -> {
                bprocInstanceRolesLink.setUser(getUserByPersonGroup(userExtValueChangeEvent.getValue().getGroup()));
                bprocInstanceRolesLinksDc.setItem(getScreenData().getDataContext().merge(bprocInstanceRolesLink));
            });
            userListsMap.put(bprocInstanceRolesLink, lookupField);
            return lookupField;
        }
    }

    protected UserExt getUserByPersonGroup(PersonGroupExt personGroup) {
        View view = new View(UserExt.class, View.LOCAL)
                .addProperty("personGroup", new View(PersonGroupExt.class)
                        .addProperty("list", new View(PersonExt.class)
                        .addProperty("lastName")
                        .addProperty("firstName")
                        .addProperty("middleName")
                        .addProperty("firstNameLatin")
                        .addProperty("lastNameLatin")
                        .addProperty("employeeNumber")));
        return dataManager.load(UserExt.class)
                .query("select e from tsadv$UserExt e" +
                        " where e.personGroup.id = :personGroupId")
                .parameter("personGroupId", personGroup.getId())
                .view(view)
                .optional().orElse(null);
    }

    protected List<PersonExt> getPersonsByRole(DicHrRole hrRole) {
        if (hrRole == null) return new ArrayList<>();
        View view = new View(PersonExt.class);
        view.addProperty("lastName");
        view.addProperty("firstName");
        view.addProperty("middleName");
        view.addProperty("firstNameLatin");
        view.addProperty("lastNameLatin");
        view.addProperty("employeeNumber");
        view.addProperty("group");
        List<PersonExt> personList = dataManager.load(PersonExt.class)
                .query("select e from base$PersonExt e" +
                        " join tsadv$HrUserRole r on r.user.personGroup.id = e.group.id" +
                        " where r.role.id = :hrRoleId")
                .parameter("hrRoleId", hrRole.getId())
                .view(view)
                .list();
        return personList;
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