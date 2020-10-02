package kz.uco.tsadv.web.bpm.form;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.bpm.entity.ProcActor;
import com.haulmont.bpm.entity.ProcDefinition;
import com.haulmont.bpm.entity.ProcRole;
import com.haulmont.bpm.gui.procactor.ProcActorsFrame;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.entity.extend.UserExt;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.exceptions.ItemNotFoundException;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.global.entity.UserExtPersonGroup;
import kz.uco.tsadv.modules.personal.dictionary.DicHrRole;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.OrganizationHrUser;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.service.BpmProcActorsService;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.service.OrganizationHrUserService;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class UcoBpmProcActorsFrame extends ProcActorsFrame {

    @Inject
    protected BpmProcActorsService bpmProcActorsService;
    @Inject
    protected CollectionDatasource<User, UUID> usersDs;
    @Inject
    protected OrganizationHrUserService organizationHrUserService;
    @Inject
    protected UserSession userSession;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected CollectionDatasource.Ordered<ProcActor, UUID> procActorsDs;
    @Named("procActorsTable.remove")
    protected RemoveAction procActorsTableRemove;
    @Inject
    protected Table<ProcActor> procActorsTable;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected CommonService commonService;

    protected PersonGroupExt personGroupExt;
    protected PositionGroupExt positionGroupExt;
    protected OrganizationGroupExt organizationGroupExt;

    protected HashMap<DicHrRole, OrganizationHrUser> currentHrUsers = new HashMap<>();        //for change counter

    protected Set<UserExt> notEditableUsers = new HashSet<>();
    protected Set<String> notFoundUserInRoleRequired = new HashSet<>();
    protected Map<String, String> procRoleName = new HashMap<>();
    protected Map<String, Object> initParams;

    @Override
    public void init(Map<String, Object> params) {
        initParams = params;
        if (procInstance == null)
            return;
        procRolesDs.getItems().forEach(procRole -> procRoleName.put(procRole.getCode().toUpperCase(), procRole.getLocName()));

        removeAllProcActors();
        StringBuilder strNotify = new StringBuilder();
        super.init(params);
        addingListener();

        ProcDefinition procDefinition = procInstance.getProcDefinition();
        procDefinition = dataManager.reload(procDefinition, "procDefinition-with-model");
        procInstance.setProcDefinition(procDefinition);

        personGroupExt = employeeService.getPersonGroupByUserId(userSession.getUser().getId());
        if (personGroupExt == null) {
            throw new ItemNotFoundException(messages.getMessage(UcoBpmProcActorsFrame.class, "personGroupNotFound"));
        }
        positionGroupExt = employeeService.getPositionGroupByPersonGroupId(personGroupExt.getId(), "positionGroupExt.admin.approve");
        if (positionGroupExt == null) {
            strNotify.append(messages.getMessage(UcoBpmProcActorsFrame.class, "no.position")).append('\n');
        }

        organizationGroupExt = positionGroupExt != null ?
                employeeService.getOrganizationGroupExtByPositionGroup(positionGroupExt, View.MINIMAL) :
                getOrgByAssignmentGroupId(userSession.getAttribute(StaticVariable.ASSIGNMENT_GROUP_ID));

        if (organizationGroupExt == null) {
            strNotify.append(messages.getMessage(UcoBpmProcActorsFrame.class, "orgNotFound")).append('\n');
        }

        Map<String, Object> paramGenerator = bpmProcActorsService.generateProcActors(procInstance, personGroupExt, positionGroupExt,
                organizationGroupExt, new ArrayList<>(procRolesDs.getItems()), params);

        /*List<BpmRolesLink> bpmRolesLinks = bpmService.getBpmRolesLinks(positionGroupExt, procDefinition.getModel());

        if (bpmRolesLinks == null || bpmRolesLinks.isEmpty()) {
            throw new ItemNotFoundException(messages.getMessage(UcoBpmProcActorsFrame.class, "no.links.error"));
        }

        bpmRolesLinks = filterLinks(addingAdminApprove(bpmRolesLinks));

        bpmRolesLinks.forEach(b -> {
            boolean isNeedCounter = b.getBpmRole().getCode().toLowerCase().matches("admin_approve|hr_specialist");

            if (params.get(b.getHrRole().getCode().toUpperCase()) != null &&
                    (boolean) params.get(b.getHrRole().getCode().toUpperCase())) {
                procRolesDs.removeItem(b.getBpmRole());
            } else {
                preUsers.computeIfAbsent(b.getBpmRole(), k -> new ArrayList<>());

                if (b.getHrRole().getCode().toUpperCase().matches("DIRECTOR|MANAGER")) {
                    addDirectorOrManger(b, isNeedCounter, preUsers, noUser, notEditableUsers);
                } else if (organizationGroupExt != null) {
                    addHrOrganizationUsers(b, isNeedCounter, preUsers, noEmployee);
                }

                if (b.getRequired() && (preUsers.get(b.getBpmRole()).size() == 0)) {
                    notFoundUserInRoleRequired.add(b.getBpmRole().getCode().toUpperCase());
                }
                if (isNeedCounter) {
                    procRolesDs.removeItem(b.getBpmRole());
                }
            }
        });


        addPreActors(preUsers);

        deleteSameUsers();*/

        setParams(paramGenerator);

        notify((Map<DicHrRole, UUID>) paramGenerator.get("noUser"), (Map<DicHrRole, UserExt>) paramGenerator.get("noEmployee"), strNotify);
        notFoundUserInRoleRequired.forEach(s -> {
            for (ProcRole item : procRolesDs.getItems()) {
                if (item.getCode().equalsIgnoreCase(s)) {
                    addProcActor(item);
                    break;
                }
            }
        });

    }

    private void setParams(Map<String, Object> paramGenerator) {
        notEditableUsers = (Set<UserExt>) paramGenerator.get("notEditableUsers");
        notFoundUserInRoleRequired = (Set<String>) paramGenerator.get("notFoundUserInRoleRequired");
        currentHrUsers = (HashMap<DicHrRole, OrganizationHrUser>) paramGenerator.get("currentHrUsers");
        Collection<ProcActor> procActors = (Collection<ProcActor>) paramGenerator.get("procActorsDs");
        for (ProcActor procActor : procActors) {
            procActorsDs.addItem(procActor);
        }

        Collection<ProcRole> procRoles = (Collection<ProcRole>) paramGenerator.get("procRoles");
        List<ProcRole> list = new ArrayList<>(procRolesDs.getItems());
        for (ProcRole procRole : list) {
            if (!procRoles.contains(procRole)) {
                procRolesDs.removeItem(procRole);
            }
        }
    }

    protected void removeAllProcActors() {
        List<ProcActor> list = new ArrayList<>(procActorsDs.getItems());
        list.forEach(procActor -> procActorsDs.removeItem(procActor));
    }

    protected OrganizationGroupExt getOrgByAssignmentGroupId(UUID assignmentGroupID) {
        if (assignmentGroupID == null) {
            return null;
        }
        List list = commonService.getEntities(OrganizationGroupExt.class,
                "select e.organizationGroup from base$AssignmentExt e where e.group.id = :id " +
                        "  and e.primaryFlag = 'TRUE'" +
                        "  and :date between e.startDate and e.endDate ",
                ParamsMap.of("id", assignmentGroupID, "date", CommonUtils.getSystemDate()), View.MINIMAL);
        return (OrganizationGroupExt) (list.isEmpty() ? null : list.get(0));
    }

   /* private List<BpmRolesLink> filterLinks(List<BpmRolesLink> bpmRolesLinks) {
        String dbName = metadata.getTools().getDatabaseTable(metadata.getClass(procInstance.getEntityName()));

        if (dbName != null && dbName.equalsIgnoreCase("TSADV_POSITION_CHANGE_REQUEST")
                && isRole(bpmUtils.getCreatedBy(procInstance.getId(), "userExtPersonGroup.edit"), "HR_BUSINESS_PARTNER")) {
            List<BpmRolesLink> newList = new ArrayList<>();
            for (BpmRolesLink bpmRolesLink : bpmRolesLinks) {
                if (bpmRolesLink.getHrRole().getCode().toUpperCase().matches("^(HR_VP|.*SPECIALIST.*)$")) {
                    newList.add(bpmRolesLink);
                }
            }
            bpmRolesLinks = newList;
        }

        return bpmRolesLinks;
    }

    protected boolean isRole(UserExtPersonGroup userExtPersonGroup, String role) {
        Map<Integer, Object> param = new HashMap<>();
        param.put(1, role);
        param.put(2, userExtPersonGroup.getUserExt().getId());
        return commonService.getCount(" select count(*) from tsadv_hr_user_role hu " +
                "join tsadv_dic_hr_role r " +
                " on r.id = hu.role_id " +
                " and r.delete_ts is null " +
                " and r.code = ?1 " +
                "where hu.delete_ts is null " +
                " and hu.user_id = ?2 " +
                " and current_date between hu.date_from and hu.date_to ", param) > 0;
    }

    protected void deleteSameUsers() {
        // delete procActor with same users
        List<ProcActor> list = new ArrayList<>(procActorsDs.getItems());
//        System.out.println(list.get(0).getProcRole().getLocName() + '\t' + list.get(0).getUser().getFirstName() + '\t' + list.get(0).getProcRole().getOrder());
        for (int i = 1; i < list.size(); i++) {
//            System.out.println(list.get(i).getProcRole().getLocName() + '\t' + list.get(i).getUser().getFirstName() + '\t' + list.get(i).getProcRole().getOrder());
            User nexUser = list.get(i).getUser();
            User prevUser = list.get(i - 1).getUser();
            if (nexUser != null && prevUser != null && nexUser.getId().equals(prevUser.getId())) {
                procActorsDs.removeItem(list.get(i));
            }
        }
    }

    protected void addDirectorOrManger(BpmRolesLink b, boolean isNeedCounter, Map<ProcRole, List<UserExt>> preUsers, Map<DicHrRole, UUID> noUser, Set<UserExt> notEditableUsers) {
        boolean isDirector = b.getHrRole().getCode().equalsIgnoreCase("DIRECTOR");
        String dbName = metadata.getTools().getDatabaseTable(metadata.getClass(procInstance.getEntityName()));
        UUID id = isDirector ?
                getDirectorId()
                : (positionGroupExt != null
                && positionGroupExt.getPosition() != null
                && positionGroupExt.getPosition().getManagerFlag()
                && dbName != null && !dbName.equals("TSADV_ABSENCE_REQUEST"))
                ? null
                : employeeService.getImmediateSupervisorByPersonGroup(personGroupExt.getId()); //todo

        if (id != null) {
            UserExt directorUser = employeeService.getUserExtByPersonGroupId(id);
            if (directorUser != null) {
                addUser(b.getBpmRole(), directorUser, preUsers);
                notEditableUsers.add(directorUser);
            } else {
                noUser.put(b.getHrRole(), id);
            }
        } else if (isNeedCounter) {
            throw new ItemNotFoundException(b.getHrRole().getLangValue() + " не найдено!");
        }
    }

    protected void addHrOrganizationUsers(BpmRolesLink b, boolean isNeedCounter, Map<ProcRole, List<UserExt>> preUsers, Map<DicHrRole, UserExt> noEmployee) {
        List<OrganizationHrUser> hrUsers = new ArrayList<>();
        boolean configActive = b.getHrRole().getCode().equalsIgnoreCase("hr_specialist")
                && procInstance.getEntityName().equals(Objects.requireNonNull(metadata.getClass(PositionChangeRequest.class)).getName())
                && !StringUtils.isEmpty(bpmRequestConfig.getPositionHrSpecialist());

        if (configActive) {
            UserExt userExt = employeeService.getUserByLogin(bpmRequestConfig.getPositionHrSpecialist());
            if (userExt != null) {
                OrganizationHrUser hrUser = metadata.create(OrganizationHrUser.class);
                hrUser.setUser(userExt);
                hrUsers.add(hrUser);
            }
        } else {
            hrUsers = organizationHrUserService.getHrUsersWithCounter(
                    "HR_BUSINESS_PARTNER".equals(b.getHrRole().getCode()) && initParams.containsKey("ORGANIZATION_BUSINESS_PARTNER")
                            ? (UUID) initParams.get("ORGANIZATION_BUSINESS_PARTNER")
                            : organizationGroupExt.getId(),
                    b.getHrRole().getCode(),
                    isNeedCounter ? 1 : null);
        }
        if (hrUsers.size() == 1) {
            if (employeeService.getPersonGroupByUserId(hrUsers.get(0).getUser().getId()) != null) {
                addUser(b.getBpmRole(), hrUsers.get(0).getUser(), preUsers);
                notEditableUsers.add(hrUsers.get(0).getUser());

                if (isNeedCounter && !configActive) {
                    currentHrUsers.put(b.getHrRole(), hrUsers.get(0));
                }

            } else {
                if (isNeedCounter) {
                    throw new ItemNotFoundException(b.getHrRole().getLangValue() + " " +
                            messages.getMessage(UcoBpmProcActorsFrame.class, "doesNotHaveUser"));
                }
                noEmployee.put(b.getHrRole(), hrUsers.get(0).getUser());
            }
        } else if (isNeedCounter && hrUsers.isEmpty()) {
            throw new ItemNotFoundException(b.getHrRole().getLangValue() + ' ' +
                    messages.getMessage(UcoBpmProcActorsFrame.class, "nF"));
        } else if (isNeedCounter) {
            throw new ItemNotFoundException(messages.getMessage(UcoBpmProcActorsFrame.class, "moreThanOne") + ' ' +
                    b.getHrRole().getLangValue() + ' ' +
                    messages.getMessage(UcoBpmProcActorsFrame.class, "f"));
        }
    }

    protected List<BpmRolesLink> addingAdminApprove(List<BpmRolesLink> links) {
        if (positionGroupExt == null) return links;

        ProcRole bpmRole = procRolesDs.getItems().stream().filter(procRole -> procRole.getCode().equalsIgnoreCase("admin_approve")).findFirst().orElse(null);
        DicHrRole dicHrRole = positionGroupExt.getAdminApprove();
        if (bpmRole != null && dicHrRole != null) {
            BpmRolesLink link = metadata.create(BpmRolesLink.class);
            link.setBpmRole(bpmRole);
            link.setHrRole(dicHrRole);
//            link.setBpmRolesDefiner(bpmRolesDefiner);
            link.setRequired(true);
            links.add(link);
        }
        return links;
    }

    protected UUID getDirectorId() {
        UUID posId = employeeService.getDirectorPositionByPersonGroup(personGroupExt.getId());
        if (posId != null) {
            List<PersonGroupExt> list = commonService.getEntities(PersonGroupExt.class,
                    "select e.personGroup from base$AssignmentExt e " +
                            "where current_date between e.startDate and e.endDate " +
                            " and e.primaryFlag = TRUE " +
                            " and e.positionGroup.id = :pos", ParamsMap.of("pos", posId), View.MINIMAL);

            if (list != null && list.size() == 1) {
                return list.get(0).getId();
            }
        }
        return null;
    }*/

    protected void notify(Map<DicHrRole, UUID> noUser, Map<DicHrRole, UserExt> noEmployee, StringBuilder strNotify) {
        noUser.forEach((hrRole, id) -> {
            PersonExt personExt = employeeService.getPersonByPersonGroup(id, CommonUtils.getSystemDate(), "person-edit");
            if (personExt != null) {
                strNotify.append(messages.getMessage(UcoBpmProcActorsFrame.class, hrRole.getCode().toUpperCase()))
                        .append(" (").append(personExt.getFioWithEmployeeNumber()).append(") ")
                        .append(messages.getMessage(UcoBpmProcActorsFrame.class, "doesNotHaveUser"))
                        .append('\n');
            }
        });

        noEmployee.forEach((hrRole, userExt) ->
                strNotify.append(messages.getMessage(UcoBpmProcActorsFrame.class, hrRole.getCode().toUpperCase()))
                        .append(" (").append(userExt.getFullName()).append(") ")
                        .append(messages.getMessage(UcoBpmProcActorsFrame.class, "doesNotHaveUser"))
                        .append('\n'));

        if (!strNotify.toString().isEmpty()) {
            showNotification(strNotify.toString(), NotificationType.HUMANIZED);
        }
    }

    public Component generatePicker(ProcActor procActor) {
        PickerField pickerField = componentsFactory.createComponent(PickerField.class);
        pickerField.setMetaClass(metadata.getClass(UserExtPersonGroup.class));
        pickerField.setWidth("100%");
        pickerField.setCaptionMode(CaptionMode.PROPERTY);
        pickerField.setCaptionProperty("fullName");
        pickerField.setRequired(true);
        PickerField.LookupAction lookupAction = pickerField.addLookupAction();
        lookupAction.setLookupScreen("tsadv$UserExtPersonGroup.browse");
        lookupAction.setLookupScreenParamsSupplier(() -> ParamsMap.of("EMPLOYEE", true));
        lookupAction.setAfterLookupSelectionHandler(items -> {
            if (!CollectionUtils.isEmpty(items)) {
                UserExtPersonGroup userExtPersonGroup = (UserExtPersonGroup) items.iterator().next();
                if (!isContainUser(userExtPersonGroup.getUserExt())) {
                    procActor.setUser(userExtPersonGroup.getUserExt());
                } else {
                    pickerField.setValue(null);
                    showNotification(messages.getMessage(UcoBpmProcActorsFrame.class, "alreadyAddedUser"));
                }
            }
        });
        if (procActor.getUser() != null) {
            pickerField.setValue(commonService.getEntity(UserExtPersonGroup.class,
                    "select e from tsadv$UserExtPersonGroup e where e.userExt.id = :userId",
                    ParamsMap.of("userId", procActor.getUser().getId()), "userExtPersonGroup.edit"));
            if (notEditableUsers.contains(procActor.getUser())) {
                pickerField.setEditable(false);
            }
        }
        return pickerField;
    }

    protected boolean isContainUser(UserExt userExt) {
        for (ProcActor item : procActorsDs.getItems()) {
            if (item.getUser() != null && item.getUser().getId().equals(userExt.getId())) {
                return true;
            }
        }
        return false;
    }

    protected void addingListener() {
        procActorsDs.addItemChangeListener(e -> {
            if (e.getItem() != null) {
                Set<String> haveNecessaryUser = new HashSet<>(notFoundUserInRoleRequired);
                procActorsDs.getItems().forEach(procActor -> {
                    if (procActor.getProcRole() != null && e.getItem() != null && !procActor.equals(e.getItem()))
                        haveNecessaryUser.remove(procActor.getProcRole().getCode().toUpperCase());
                });
                if (e.getItem() != null &&
                        (e.getItem().getUser() != null && notEditableUsers.contains(e.getItem().getUser())
                                || haveNecessaryUser.contains(e.getItem().getProcRole().getCode().toUpperCase()))) {
                    procActorsTableRemove.setEnabled(false);
                    procActorsTable.setEditable(false);
                } else {
                    procActorsTableRemove.setEnabled(true);
                    procActorsTable.setEditable(true);
                }
            }
        });

        procActorsDs.addCollectionChangeListener(e -> {
            if (CollectionDatasource.Operation.ADD.equals(e.getOperation())) {
                procActorsDs.getItems().forEach(procActor -> {
                    if (procActor.getUser() != null) {
                        usersDs.removeItem(procActor.getUser());
                    }
                });
            } else if (CollectionDatasource.Operation.REMOVE.equals(e.getOperation()) ||
                    CollectionDatasource.Operation.CLEAR.equals(e.getOperation())) {
                e.getItems().forEach(procActor -> {
                    if (procActor.getUser() != null) {
                        usersDs.addItem(procActor.getUser());
                    }
                });
            }
            if (CollectionDatasource.Operation.ADD.equals(e.getOperation()) && !isSorted(procActorsDs.getItems())) {
                List<ProcActor> actors = new ArrayList<ProcActor>(procActorsDs.getItems());

                actors.sort(Comparator.comparing(procActor -> procActor.getProcRole().getOrder()));

                procActorsDs.clear();
                actors.forEach(procActor -> procActorsDs.addItem(procActor));
            }
        });
    }

    protected boolean isSorted(Collection<ProcActor> actors) {
        if (actors == null || actors.isEmpty()) {
            return true;
        }
        int maxOrder = 0;
        for (ProcActor procActor : actors) {
            if (maxOrder > procActor.getProcRole().getOrder()) {
                return false;
            }
            maxOrder = procActor.getProcRole().getOrder();
        }
        return true;
    }

   /* protected void addUser(ProcRole role, UserExt
            userExt, Map<ProcRole, List<UserExt>> preUsers) {
        if (userExt != null) {
            preUsers.computeIfAbsent(role, k -> new ArrayList<>()).add(userExt);
        }
    }

    protected void addPreActors(Map<ProcRole, List<UserExt>> users) {
        users = sortMap(users);

        for (Map.Entry<ProcRole, List<UserExt>> entry : users.entrySet()) {
            entry.getValue().forEach(userExt -> {
                if (userExt != null) {
                    ProcActor procActor = metadata.create(ProcActor.class);
                    procActor.setUser(userExt);
                    ProcRole procRole = entry.getKey();
                    procActor.setProcRole(procRole);
                    procActor.setProcInstance(procInstance);
                    procActor.setOrder(getLastOrder(procRole) + 1);
                    procActorsDs.addItem(procActor);
                    usersDs.removeItem(userExt);
                }
            });
        }
    }*/

   /* protected Map<ProcRole, List<UserExt>> sortMap(Map<ProcRole, List<UserExt>> map) {
        Map<ProcRole, List<UserExt>> treeMap = new TreeMap<>(Comparator.comparingInt(ProcRole::getOrder));
        treeMap.putAll(map);
        return treeMap;
    }*/

    public void checkUsers(HashMap<String, Boolean> params) {

        if (!isActorHaveUser()) {
            throw new ItemNotFoundException(messages.getMessage(UcoBpmProcActorsFrame.class, "notUsers"));
        }

        Set<String> haveNecessaryUser = new HashSet<>(notFoundUserInRoleRequired);

        params.forEach((s, aBoolean) -> {
            if (aBoolean) {
                haveNecessaryUser.remove(s.toUpperCase());
            }
        });

        procActorsDs.getItems().forEach(procActor -> {
            if (procActor.getUser() != null) {
                haveNecessaryUser.remove(procActor.getProcRole().getCode().toUpperCase());
            }
        });

        if (!haveNecessaryUser.isEmpty()) {
            StringBuilder message = new StringBuilder();
            message.append(messages.getMessage(UcoBpmProcActorsFrame.class, "notFound"));
            message.append('\n');
            message.append(messages.getMessage(UcoBpmProcActorsFrame.class, "requiredRoles"));
            haveNecessaryUser.forEach(s -> message.append(procRoleName.get(s.toUpperCase())).append(", "));
            message.deleteCharAt(message.lastIndexOf(","));
            throw new ItemNotFoundException(message.toString());
        }
    }

    protected boolean isActorHaveUser() {
        for (ProcActor procActor : procActorsDs.getItems()) {
            if (procActor.getUser() != null)
                return true;
        }
        return false;
    }

    public void postValidate(ValidationErrors errors) {

        for (ProcActor item : procActorsDs.getItems()) {
            if (item.getUser() == null) {
                errors.add(messages.getMessage(UcoBpmProcActorsFrame.class, "requiredUser"));
                break;
            }
        }
    }

    @Override
    public void commit() {
        super.commit();
        currentHrUsers.forEach((hrRole, organizationHrUser) -> organizationHrUserService.setNextCounter(organizationHrUser, hrRole.getCode()));
    }
}
