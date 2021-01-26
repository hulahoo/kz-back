package kz.uco.tsadv.web.personaldatarequest;

import com.haulmont.bali.util.ParamsMap;
//import com.haulmont.bpm.entity.ProcInstance;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.service.DatesService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PersonalDataRequestShow extends AbstractWindow {

    /*@Inject
    protected Datasource<PersonExt> personDs;
    @Inject
    protected DatesService datesService;
    @Inject
    protected CollectionDatasource<PersonContact, UUID> personContactsDs;
    @Inject
    protected CollectionDatasource<AddressRequest, UUID> addressRequestsDs;
    @Inject
    protected CommonService commonService;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    protected CollectionDatasource<ProcInstance, UUID> procInstancesDs;
    @Inject
    protected CollectionDatasource<Beneficiary, UUID> beneficiariesDs;
    @Inject
    protected CollectionDatasource<Address, UUID> addressesDs;
    @Inject
    protected Metadata metadata;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected Button editBtn;
    @Named("userContactFace.edit")
    protected EditAction userContactFaceEdit;
    @Inject
    protected Table<Address> userAddress;
    @Named("userContact.create")
    protected CreateAction userContactCreate;
    @Named("userContact.remove")
    protected RemoveAction userContactRemove;
    @Named("userContactFace.create")
    protected CreateAction userContactFaceCreate;
    @Named("userContactFace.remove")
    protected RemoveAction userContactFaceRemove;
    @Named("userContact.edit")
    protected EditAction userContactEdit;
    @Inject
    protected TabSheet mainTab;
    @Inject
    protected Notifications notifications;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        initPerson();
        userContactFaceEdit.setWindowId("tsadv$BeneficiaryView");

        userContactCreate.setInitialValuesSupplier(() ->
                personDs.getItem().getGroup() != null
                        ? ParamsMap.of("personGroupId", personDs.getItem().getGroup())
                        : null);

        userContactCreate.setWindowParamsSupplier(() -> ParamsMap.of("noEmail", true));
        userContactEdit.setWindowParamsSupplier(() -> ParamsMap.of("noEmail", true));

        userContactFaceCreate.setWindowParamsSupplier(() -> ParamsMap.of("fromSS", true));
        userContactFaceEdit.setWindowParamsSupplier(() -> ParamsMap.of("fromSS", true));

        userContactFaceCreate.setInitialValuesSupplier(() ->
                personDs.getItem().getGroup() != null
                        ? ParamsMap.of("personGroupParent", personDs.getItem().getGroup())
                        : null);
        userContactFaceRemove.setBeforeActionPerformedHandler(() -> {
                    removeLinkedBeneficiary(beneficiariesDs.getItem());
                    return true;
                }
        );

        mainTab.addSelectedTabChangeListener(event -> {
            if ("myRequests".equals(event.getSelectedTab().getName())) {
                procInstancesDs.refresh();
            } else {
                setEnabledEditBtn();
            }
        });

        personContactsDs.addItemChangeListener(e -> {
            PersonContact contact = e.getItem();
            boolean editAndRemoveEnable = !(contact != null && contact.getType() != null && contact.getType().getCode().equalsIgnoreCase("email"));
            userContactEdit.setEnabled(editAndRemoveEnable);
            userContactRemove.setEnabled(editAndRemoveEnable);
        });

        setEnabledEditBtn();

    }

    protected void initPerson() {
        personDs.setItem(getPerson());
    }

    protected PersonExt getPerson() {
        List<PersonExt> list = commonService.getEntities(PersonExt.class,
                "select e from base$PersonExt e " +
                        " where e.group.id = :groupId " +
                        "     and :date between e.startDate and e.endDate ",
                ParamsMap.of("groupId", userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP_ID),
                        "date", CommonUtils.getSystemDate()),
                "person-view_1");
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public void ready() {
        super.ready();
        addressesDs.addItemChangeListener(e -> getComponentNN("editAddressBtn").setEnabled(e.getItem() != null));
    }

    public void onEditBtnClick() {
        PersonalDataRequest personalDataRequest = getActiveRequest("DRAFT");
        if (personalDataRequest == null) {
            personalDataRequest = metadata.create(PersonalDataRequest.class);
            personalDataRequest.setDateOfBirth(personDs.getItem().getDateOfBirth());
            personalDataRequest.setFirstName(personDs.getItem().getFirstName());
            personalDataRequest.setFirstNameLatin(personDs.getItem().getFirstNameLatin());
            personalDataRequest.setLastName(personDs.getItem().getLastName());
            personalDataRequest.setLastNameLatin(personDs.getItem().getLastNameLatin());
            personalDataRequest.setMiddleName(personDs.getItem().getMiddleName());
            personalDataRequest.setMiddleNameLatin(personDs.getItem().getMiddleNameLatin());
            personalDataRequest.setMaritalStatus(personDs.getItem().getMaritalStatus());
            personalDataRequest.setPersonGroup(personDs.getItem().getGroup());
            personalDataRequest.setAttachment(null);
        }
        AbstractEditor abstractEditor = openEditor("tsadv$PersonalDataRequest.edit", personalDataRequest, WindowManager.OpenType.DIALOG);
        abstractEditor.addCloseListener(actionId -> {
            procInstancesDs.refresh();
            setEnabledEditBtn();
        });
    }

    public Component generatePhoneNumberCell(Beneficiary entity) {
        return getContactComponent(entity, "mobile");
    }

    public Component generateEmailCell(Beneficiary entity) {
        return getContactComponent(entity, "email");
    }

    protected Component getContactComponent(Beneficiary entity, String type) {
        Label label = componentsFactory.createComponent(Label.class);
        PersonGroupExt personGroupExt = entity.getPersonGroupChild();
        List<PersonContact> personContact = personGroupExt != null ?
                commonService.getEntities(PersonContact.class,
                        "select e from tsadv$PersonContact e " +
                                " where e.personGroup.id = :id and :currentDate between e.startDate and e.endDate " +
                                "       and e.type.code = :code",
                        ParamsMap.of("id", personGroupExt.getId(),
                                "currentDate", CommonUtils.getSystemDate(),
                                "code", type),
                        "personContact.card") :
                null;
        StringBuilder res = new StringBuilder();
        if (personContact != null) {
            for (int i = 0; i < personContact.size(); i++) {
                res.append(i != 0 ? ", " : "").append(personContact.get(i).getContactValue());
            }
        }

        label.setValue(res.toString());
        return label;
    }

    public Component generateStatusCell(ProcInstance entity) {
        Label label = componentsFactory.createComponent(Label.class);
        DicRequestStatus status = null;
        Object o = getEntity(entity);
        if (entity.getEntityName().equals("tsadv$AddressRequest"))
            status = ((AddressRequest) o).getStatus();
        else if (entity.getEntityName().equals("tsadv$PersonalDataRequest")) {
            status = ((PersonalDataRequest) o).getStatus();
        }
        label.setValue(status);
        return label;
    }

    public Component generateEntityNameCell(ProcInstance entity) {
        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        linkButton.setCaption(getMessage(entity.getEntityName()));
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
        String editor = null;
        switch (entity.getEntityName()) {
            case "tsadv$PersonalDataRequest":
                editor = "tsadv$PersonalDataRequest.bpm";
                break;
            case "tsadv$AddressRequest":
                editor = "tsadv$AddressRequest.edit";
                break;
        }
        AbstractWindow abstractWindow = openEditor(editor, getEntity(entity), WindowManager.OpenType.THIS_TAB);
        abstractWindow.addCloseListener(actionId -> {
            procInstancesDs.refresh();
            setEnabledEditBtn();
        });
    }

    protected void setEnabledEditBtn() {
        editBtn.setEnabled(getActiveRequest("APPROVING") == null);
        initPerson();
    }

    protected PersonalDataRequest getActiveRequest(String statusCode) {
        List<PersonalDataRequest> list = commonService.getEntities(PersonalDataRequest.class,
                "select e from tsadv$PersonalDataRequest e " +
                        " where e.personGroup.id = :groupId " +
                        "   and e.status.code = :code "
                , ParamsMap.of("groupId", userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP_ID),
                        "code", statusCode),
                "personalDataRequest-view");
        return list.isEmpty() ? null : list.get(0);
    }

    protected Entity getEntity(ProcInstance entity) {
        String entityName = entity.getEntityName();
        String query = " select e from " + entityName + " e where e.id = :id ";
        switch (entityName) {
            case "tsadv$AddressRequest":
                return commonService.getEntity(AddressRequest.class, query,
                        ParamsMap.of("id", entity.getEntity().getEntityId()), "addressRequest-view");
            case "tsadv$PersonalDataRequest":
                return commonService.getEntity(PersonalDataRequest.class, query,
                        ParamsMap.of("id", entity.getEntity().getEntityId()), "personalDataRequest-view");
            default:
                return null;
        }
    }

    public void onAddressCreateBtn() {
        PersonGroupExt personGroupExt = userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP);

        if (personGroupExt == null) {
            showNotification(getMessage("noPerson"));
            return;
        }

        List<AddressRequest> list = commonService.getEntities(AddressRequest.class,
                " select e from tsadv$AddressRequest e " +
                        " where e.personGroup.id = :personGroupId " +
                        "       and e.status.code = 'DRAFT' " +
                        "       and e.baseAddress is null ",
                ParamsMap.of("personGroupId", personGroupExt.getId()), "addressRequest-view");
        AddressRequest addressRequest = list.isEmpty() ? null : list.get(0);
        if (addressRequest == null) {
            addressRequest = metadata.create(AddressRequest.class);
            addressRequest.setPersonGroup(personGroupExt);
        }
        AbstractWindow abstractWindow = openEditor(addressRequest, WindowManager.OpenType.DIALOG);
        abstractWindow.addCloseListener(actionId -> {
            procInstancesDs.refresh();
            setEnabledEditBtn();
        });
    }

    protected void removeLinkedBeneficiary(Beneficiary beneficiary) {
        String queryString = "SELECT e FROM tsadv$Beneficiary e " +
                "WHERE e.personGroupChild.id = :personGroupChild " +
                "AND e.personGroupParent.id = :personGroupParent";
        Map<String, Object> params = new HashMap<>();
        params.put("personGroupChild", beneficiary.getPersonGroupParent().getId());
        params.put("personGroupParent", beneficiary.getPersonGroupChild().getId());
        List<Beneficiary> linkedBeneficiaries = commonService.getEntities(Beneficiary.class, queryString, params, null);
        if (!linkedBeneficiaries.isEmpty()) {
            for (Beneficiary beneficiaryToDelete : linkedBeneficiaries) {
                dataManager.remove(beneficiaryToDelete);
            }
        }
    }

    public void onAddressEditBtn() {
        PersonGroupExt personGroupExt = userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP);

        if (personGroupExt == null) {
            showNotification(getMessage("noPerson"));
            return;
        }
        Address selectedAddress = addressesDs.getItem();

        List<AddressRequest> list = commonService.getEntities(AddressRequest.class,
                " select e from tsadv$AddressRequest e " +
                        " where e.personGroup.id = :personGroupId " +
                        "       and e.status.code = 'DRAFT' " +
                        "       and e.baseAddress.id = :addressId ",
                ParamsMap.of("personGroupId", personGroupExt.getId()
                        , "addressId", selectedAddress.getId()), "addressRequest-view");
        AddressRequest addressRequest = list.isEmpty() ? null : list.get(0);
        if (addressRequest == null) {
            addressRequest = metadata.create(AddressRequest.class);
            addressRequest.setPersonGroup(personGroupExt);
            addressRequest.setAddress(selectedAddress.getAddress());
            addressRequest.setAddressType(selectedAddress.getAddressType());
            addressRequest.setBaseAddress(selectedAddress);
            addressRequest.setCity(selectedAddress.getCity());
            addressRequest.setPostalCode(selectedAddress.getPostalCode());
            addressRequest.setCountry(selectedAddress.getCountry());
            addressRequest.setStartDate(selectedAddress.getStartDate());
        }

        AbstractWindow abstractWindow = openEditor(addressRequest, WindowManager.OpenType.DIALOG);
        abstractWindow.addCloseListener(actionId -> {
            procInstancesDs.refresh();
            setEnabledEditBtn();
        });
    }*/
}