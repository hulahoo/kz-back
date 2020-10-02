package kz.uco.tsadv.service;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.bpm.entity.ProcActor;
import com.haulmont.bpm.entity.ProcDefinition;
import com.haulmont.bpm.entity.ProcInstance;
import com.haulmont.bpm.entity.ProcRole;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import kz.uco.base.entity.extend.UserExt;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.beans.BpmUtils;
import kz.uco.tsadv.config.BpmRequestConfig;
import kz.uco.tsadv.exceptions.ItemNotFoundException;
import kz.uco.tsadv.global.entity.UserExtPersonGroup;
import kz.uco.tsadv.modules.bpm.BpmRolesDefiner;
import kz.uco.tsadv.modules.bpm.BpmRolesLink;
import kz.uco.tsadv.modules.personal.dictionary.DicHrRole;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.OrganizationHrUser;
import kz.uco.tsadv.modules.personal.model.PositionChangeRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;

@Service(BpmProcActorsService.NAME)
public class BpmProcActorsServiceBean implements BpmProcActorsService {

    @Inject
    protected BpmUtils bpmUtils;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected BpmRequestConfig bpmRequestConfig;
    @Inject
    protected Messages messages;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected CommonService commonService;
    @Inject
    protected Metadata metadata;
    @Inject
    protected OrganizationHrUserService organizationHrUserService;

    @Override
    public Map<String, Object> generateProcActors(ProcInstance procInstance, PersonGroupExt personGroupExt,
                                                  PositionGroupExt positionGroupExt, OrganizationGroupExt organizationGroupExt,
                                                  Collection<ProcRole> procRoles, Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        Set<UserExt> notEditableUsers = new HashSet<>();
        Map<DicHrRole, UUID> noUser = new HashMap<>();
        Map<DicHrRole, UserExt> noEmployee = new HashMap<>();
        Map<ProcRole, List<UserExt>> preUsers = new HashMap<>();
        Set<String> notFoundUserInRoleRequired = new HashSet<>();
        HashMap<DicHrRole, OrganizationHrUser> currentHrUsers = new HashMap<>();

        ProcDefinition procDefinition = procInstance.getProcDefinition();

        List<BpmRolesLink> bpmRolesLinks;
        if (positionGroupExt != null) {
            bpmRolesLinks = bpmUtils.getBpmRolesLinks(positionGroupExt, procDefinition.getModel(), "bpmRolesLink-view");
        } else {
            BpmRolesDefiner definer = bpmUtils.getBpmRolesDefiner(procDefinition.getModel(), "bpmRolesDefiner-view");
            bpmRolesLinks = definer != null ? definer.getLinks() : null;
        }

        throwItemNotFoundException(bpmRolesLinks == null || bpmRolesLinks.isEmpty(), "bpm.roles.links.no.error");

        bpmRolesLinks = filterLinks(addingAdminApprove(bpmRolesLinks, positionGroupExt, procRoles), procInstance);

        bpmRolesLinks.forEach(b -> {
            String code = b.getHrRole().getCode().toUpperCase();

            if ((boolean) params.getOrDefault(code, false)) {
                procRoles.remove(b.getBpmRole());
            } else {
                preUsers.computeIfAbsent(b.getBpmRole(), k -> new ArrayList<>());
                boolean isNeedCounter = b.getFindByCounter(); //isNeedCounter(code);

                if (code.matches("DIRECTOR|MANAGER")) {
                    boolean isAddedRequired = addDirectorOrManger(b, preUsers, noUser, notEditableUsers, procInstance, personGroupExt, positionGroupExt);
                    throwItemNotFoundException(!isAddedRequired && isNeedCounter, b.getHrRole().getLangValue() + "/not.found");
                } else {
                    UUID organizationGroupId = "HR_BUSINESS_PARTNER".equals(b.getHrRole().getCode()) && params.containsKey("ORGANIZATION_BUSINESS_PARTNER")
                            ? (UUID) params.get("ORGANIZATION_BUSINESS_PARTNER")
                            : organizationGroupExt.getId();
                    throwItemNotFoundException(organizationGroupId == null, "organization.group.not.found");
                    addHrOrganizationUsers(b, procInstance, currentHrUsers, isNeedCounter, preUsers, noEmployee, notEditableUsers, organizationGroupId);
                }

                if (b.getRequired() && (preUsers.get(b.getBpmRole()).size() == 0)) {
                    notFoundUserInRoleRequired.add(b.getBpmRole().getCode().toUpperCase());
                }
                if (isNeedCounter) {
                    procRoles.remove(b.getBpmRole());
                }
            }
        });


        Collection<ProcActor> procActorsDs = createProcActorsDs(preUsers, procInstance);

        deleteSameUsers(procActorsDs);

        result.put("notEditableUsers", notEditableUsers);
        result.put("noUser", noUser);
        result.put("noEmployee", noEmployee);
        result.put("notFoundUserInRoleRequired", notFoundUserInRoleRequired);
        result.put("currentHrUsers", currentHrUsers);
        result.put("procRoles", procRoles);
        result.put("procActorsDs", procActorsDs); // list of procActors

        return result;
    }

    protected Collection<ProcActor> createProcActorsDs(Map<ProcRole, List<UserExt>> users, ProcInstance procInstance) {
        users = sortMap(users);

        Collection<ProcActor> procActorsDs = new ArrayList<>();
        for (Map.Entry<ProcRole, List<UserExt>> entry : users.entrySet()) {
            entry.getValue().forEach(userExt -> {
                if (userExt != null) {
                    ProcActor procActor = metadata.create(ProcActor.class);
                    procActor.setUser(userExt);
                    ProcRole procRole = entry.getKey();
                    procActor.setProcRole(procRole);
                    procActor.setProcInstance(procInstance);
                    procActor.setOrder(getLastOrder(procRole, procActorsDs) + 1);
                    procActorsDs.add(procActor);
                }
            });
        }
        return procActorsDs;
    }

    protected int getLastOrder(ProcRole procRole, Collection<ProcActor> procActorsDs) {
        int lastOrder = 0;
        for (ProcActor procActor : procActorsDs) {
            if (procRole.equals(procActor.getProcRole()) && procActor.getOrder() > lastOrder) {
                lastOrder = procActor.getOrder();
            }
        }
        return lastOrder;
    }

    protected Map<ProcRole, List<UserExt>> sortMap(Map<ProcRole, List<UserExt>> map) {
        Map<ProcRole, List<UserExt>> treeMap = new TreeMap<>(Comparator.comparingInt(ProcRole::getOrder));
        treeMap.putAll(map);
        return treeMap;
    }

    private void deleteSameUsers(Collection<ProcActor> procActorsDs) {
        List<ProcActor> list = new ArrayList<>(procActorsDs);
        for (int i = 1; i < list.size(); i++) {
            if (Objects.equals(list.get(i).getUser(), list.get(i - 1).getUser())) {
                procActorsDs.remove(list.get(i));
            }
        }
    }

    /*@Deprecated
    protected boolean isNeedCounter(@Nonnull String code) {
        return code.toLowerCase().matches("admin_approve|hr_specialist|hr_specialist_front");
    }*/

    protected void throwItemNotFoundException(boolean isException, @Nonnull String message) {
        if (isException) throw new ItemNotFoundException(message);
    }

    protected void addHrOrganizationUsers(BpmRolesLink b, ProcInstance procInstance, HashMap<DicHrRole, OrganizationHrUser> currentHrUsers, boolean isNeedCounter,
                                          Map<ProcRole, List<UserExt>> preUsers, Map<DicHrRole, UserExt> noEmployee,
                                          Set<UserExt> notEditableUsers, UUID organizationGroupId) {
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
            hrUsers = organizationHrUserService.getHrUsersWithCounter(organizationGroupId,
                    b.getHrRole().getCode(),
                    isNeedCounter ? 1 : null);
        }
        if (!hrUsers.isEmpty()) {
            if (employeeService.getPersonGroupByUserId(hrUsers.get(0).getUser().getId()) != null) {
                addUser(b.getBpmRole(), hrUsers.get(0).getUser(), preUsers);
                notEditableUsers.add(hrUsers.get(0).getUser());

                if (isNeedCounter && !configActive) {
                    currentHrUsers.put(b.getHrRole(), hrUsers.get(0));
                }

            } else {
                throwItemNotFoundException(isNeedCounter, b.getHrRole().getLangValue() + "/has.no.user");
                noEmployee.put(b.getHrRole(), hrUsers.get(0).getUser());
            }
        }
        throwItemNotFoundException(isNeedCounter && hrUsers.isEmpty(), b.getHrRole().getLangValue() + "/not.found");
    }

    @Nullable
    protected UUID getDirectorId(@Nonnull PersonGroupExt personGroupExt) {
        UUID posId = employeeService.getDirectorPositionByPersonGroup(personGroupExt.getId());
        if (posId != null) {
            List<PersonGroupExt> list = commonService.getEntities(PersonGroupExt.class,
                    "select e.personGroup from base$AssignmentExt e " +
                            "where current_date between e.startDate and e.endDate " +
                            " and e.primaryFlag = TRUE " +
                            " and e.positionGroup.id = :pos" +
                            " and e.assignmentStatus.code = 'ACTIVE' ",
                    ParamsMap.of("pos", posId), View.MINIMAL);

            return list.size() == 1 ? list.get(0).getId() : null;
        }
        return null;
    }

    protected void addUser(ProcRole role, UserExt
            userExt, Map<ProcRole, List<UserExt>> preUsers) {
        if (userExt != null) {
            preUsers.computeIfAbsent(role, k -> new ArrayList<>()).add(userExt);
        }
    }

    protected boolean addDirectorOrManger(BpmRolesLink b, Map<ProcRole, List<UserExt>> preUsers, Map<DicHrRole,
            UUID> noUser, Set<UserExt> notEditableUsers, ProcInstance procInstance, PersonGroupExt personGroupExt,
                                          PositionGroupExt positionGroupExt) {
        boolean isDirector = b.getHrRole().getCode().equalsIgnoreCase("DIRECTOR");
        String dbName = metadata.getTools().getDatabaseTable(metadata.getClass(procInstance.getEntityName()));
        UUID id = isDirector ? getDirectorId(personGroupExt)
                : (positionGroupExt != null
                && positionGroupExt.getPosition() != null
                && positionGroupExt.getPosition().getManagerFlag()
                && dbName != null && !dbName.equals("TSADV_ABSENCE_REQUEST"))
                ? null
                : employeeService.getImmediateSupervisorByPersonGroup(personGroupExt.getId());

        if (id != null) {
            UserExt directorUser = employeeService.getUserExtByPersonGroupId(id);
            if (directorUser != null) {
                addUser(b.getBpmRole(), directorUser, preUsers);
                notEditableUsers.add(directorUser);
            } else {
                noUser.put(b.getHrRole(), id);
            }
            return true;
        }
        return false;
    }

    protected List<BpmRolesLink> addingAdminApprove(List<BpmRolesLink> links, PositionGroupExt positionGroupExt, Collection<ProcRole> procRoles) {
        if (positionGroupExt == null) return links;

        ProcRole bpmRole = procRoles.stream().filter(procRole -> procRole.getCode().equalsIgnoreCase("admin_approve")).findFirst().orElse(null);
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

    private List<BpmRolesLink> filterLinks(List<BpmRolesLink> bpmRolesLinks, ProcInstance procInstance) {
        String dbName = metadata.getTools().getDatabaseTable(metadata.getClass(procInstance.getEntityName()));

        if (dbName != null && dbName.equalsIgnoreCase("TSADV_POSITION_CHANGE_REQUEST")) {
            UserExtPersonGroup userExtPersonGroup = bpmUtils.getCreatedBy(procInstance.getId(), "userExtPersonGroup.edit");
            if (userExtPersonGroup != null && isRole(userExtPersonGroup, "HR_BUSINESS_PARTNER")) {
                List<BpmRolesLink> newList = new ArrayList<>();
                for (BpmRolesLink bpmRolesLink : bpmRolesLinks) {
                    if (bpmRolesLink.getHrRole().getCode().toUpperCase().matches("^(HR_VP|.*SPECIALIST.*)$")) {
                        newList.add(bpmRolesLink);
                    }
                }
                bpmRolesLinks = newList;
            }
        }

        return bpmRolesLinks;
    }

    protected boolean isRole(@Nonnull UserExtPersonGroup userExtPersonGroup, String role) {
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
}