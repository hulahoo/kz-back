package kz.uco.tsadv.service.portal;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.*;
import kz.uco.tsadv.exceptions.PortalException;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.bpm.BpmRolesDefiner;
import kz.uco.tsadv.modules.bpm.BpmRolesLink;
import kz.uco.tsadv.modules.bpm.BprocActors;
import kz.uco.tsadv.modules.bpm.NotPersisitBprocActors;
import kz.uco.tsadv.modules.personal.dictionary.DicHrRole;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.PositionExt;
import kz.uco.tsadv.service.BpmRolesDefinerService;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.service.OrganizationHrUserService;
import kz.uco.tsadv.service.PositionService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service(StartBprocService.NAME)
public class StartBprocServiceBean implements StartBprocService {

    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected OrganizationHrUserService organizationHrUserService;
    @Inject
    protected Messages messages;
    @Inject
    protected Metadata metadata;
    @Inject
    protected PositionService positionService;
    @Inject
    protected BpmRolesDefinerService definerService;

    @Override
    public BpmRolesDefiner getBpmRolesDefiner(String processDefinitionKey, UUID employeePersonGroupId, boolean isAssistant) {
        BpmRolesDefiner bpmRolesDefiner = definerService.getBpmRolesDefiner(processDefinitionKey, employeePersonGroupId);

        bpmRolesDefiner = excludeSupManager(employeePersonGroupId, bpmRolesDefiner);
        bpmRolesDefiner = filterAssistant(bpmRolesDefiner, isAssistant);

        return bpmRolesDefiner;
    }

    protected BpmRolesDefiner filterAssistant(BpmRolesDefiner bpmRolesDefiner, boolean isAssistant) {
        List<BpmRolesLink> links = bpmRolesDefiner.getLinks()
                .stream()
                .filter(bpmRolesLink -> bpmRolesLink.isActive(isAssistant))
                .collect(Collectors.toList());
        bpmRolesDefiner.setLinks(links);
        return bpmRolesDefiner;
    }

    protected BpmRolesDefiner excludeSupManager(UUID employeePersonGroupId, BpmRolesDefiner bpmRolesDefiner) {
        if (bpmRolesDefiner.getActiveSupManagerExclusion() && !CollectionUtils.isEmpty(bpmRolesDefiner.getLinks())) {
            boolean isSupManagerExclusion = false;
            String supManagerCode = OrganizationHrUserService.HR_ROLE_SUP_MANAGER;

            PositionGroupExt positionGroup = employeeService.getPositionGroupByPersonGroupId(employeePersonGroupId, View.MINIMAL);
            PositionGroupExt supManager = positionService.getManager(positionGroup.getId());
            if (supManager != null) supManager = positionService.getManager(supManager.getId());

            if (supManager != null) {
                supManager = dataManager.reload(supManager, new View(PositionGroupExt.class)
                        .addProperty("list", new View(PositionExt.class)
                                .addProperty("startDate")
                                .addProperty("endDate")
                                .addProperty("supManagerExclusion")));
                isSupManagerExclusion = Optional.ofNullable(supManager.getPosition()).map(PositionExt::getSupManagerExclusion).orElse(false);
            }

            if (isSupManagerExclusion) {
                List<BpmRolesLink> links = bpmRolesDefiner.getLinks().stream()
                        .filter(link -> !supManagerCode.equals(link.getHrRole().getCode())).collect(Collectors.toList());
                bpmRolesDefiner.setLinks(links);
            }
        }
        return bpmRolesDefiner;
    }

    @Override
    public List<NotPersisitBprocActors> getNotPersisitBprocActors(UUID employeePersonGroupId,
                                                                  BpmRolesDefiner bpmRolesDefiner,
                                                                  boolean isAssistant) {
        List<NotPersisitBprocActors> actors = new ArrayList<>();
        List<BpmRolesLink> links = bpmRolesDefiner.getLinks();

        for (BpmRolesLink link : links) {
            if (!link.isActive(isAssistant)) continue;

            DicHrRole hrRole = link.getHrRole();
            String roleCode = hrRole.getCode();
            List<TsadvUser> hrUsersForPerson;

            if (roleCode.equals(OrganizationHrUserService.HR_ROLE_EMPLOYEE)) {
                hrUsersForPerson = dataManager.load(TsadvUser.class)
                        .query("select e from tsadv$UserExt e where e.personGroup.id = :employeePersonGroupId and e.active = 'TRUE'")
                        .parameter("employeePersonGroupId", employeePersonGroupId)
                        .view("user-fioWithLogin")
                        .list();
            } else
                hrUsersForPerson = dataManager.load(TsadvUser.class)
                        .query("select e from tsadv$UserExt e where e in :users")
                        .setParameters(ParamsMap.of("users", organizationHrUserService.getHrUsersForPerson(employeePersonGroupId, roleCode)))
                        .view("user-fioWithLogin")
                        .list();

            if (hrUsersForPerson.isEmpty()) {
                if (!link.getIsAddableApprover() && link.getRequired())
                    throw new PortalException(String.format(messages.getMainMessage("hr.user.not.found"), hrRole.getLangValue()));

                if (link.getRequired()) {
                    NotPersisitBprocActors bprocActors = createNotPersisitBprocActors(link, hrRole);
                    bprocActors.setIsEditable(true);
                    actors.add(bprocActors);
                }
            } else {
                NotPersisitBprocActors bprocActors = createNotPersisitBprocActors(link, hrRole);
                bprocActors.setIsEditable(false);
                bprocActors.setUsers(hrUsersForPerson);
                actors.add(bprocActors);
            }
        }

        return actors;
    }

    protected List<NotPersisitBprocActors> excludeDuplicate(List<NotPersisitBprocActors> actors) {
        List<NotPersisitBprocActors> newActors = new ArrayList<>();

        boolean hasDuplicate = false;
        boolean removeNext = false;
        for (int i = 0; i < actors.size() - 1; i++) {
            boolean removeCurrentActor = removeNext;
            removeNext = false;
            NotPersisitBprocActors actor = actors.get(i);
            NotPersisitBprocActors nextActor = actors.get(i + 1);

            List<TsadvUser> users = actor.getUsers();
            List<TsadvUser> nextUsers = nextActor.getUsers();

            if (nextUsers.size() == 1
                    && users.size() == 1
                    && nextUsers.get(0).equals(users.get(0))
                    && actor.getRolesLink() != null
                    && nextActor.getRolesLink() != null
            ) {
                Integer left = actor.getRolesLink().getPriority();
                Integer right = nextActor.getRolesLink().getPriority();

                if (left == null || right != null && left < right) removeCurrentActor = true;
                else if (right == null || left > right) removeNext = true;

                hasDuplicate = hasDuplicate || removeCurrentActor || removeNext;
            }

            if (!removeCurrentActor) newActors.add(actor);
        }

        if (!removeNext) newActors.add(actors.get(actors.size() - 1));

        return hasDuplicate ? excludeDuplicate(newActors) : newActors;
    }

    @Override
    public void saveBprocActors(UUID entityId, List<NotPersisitBprocActors> notPersisitBprocActors) {

        CommitContext commitContext = new CommitContext();

        dataManager.load(BprocActors.class)
                .query("select e from tsadv_BprocActors e where e.entityId = :entityId")
                .setParameters(ParamsMap.of("entityId", entityId))
                .list()
                .forEach(commitContext::addInstanceToRemove);

        notPersisitBprocActors = excludeDuplicate(notPersisitBprocActors);

        for (NotPersisitBprocActors notPersisitBprocActor : notPersisitBprocActors) {
            BprocActors bprocActors = metadata.create(BprocActors.class);
            bprocActors.setEntityId(entityId);
            bprocActors.setBprocUserTaskCode(notPersisitBprocActor.getBprocUserTaskCode());
            bprocActors.setHrRole(notPersisitBprocActor.getHrRole());

            for (TsadvUser user : notPersisitBprocActor.getUsers()) {
                BprocActors copy = metadata.getTools().copy(bprocActors);
                copy.setId(UUID.randomUUID());
                copy.setUser(user);
                commitContext.addInstanceToCommit(copy);
            }
        }

        dataManager.commit(commitContext);
    }

    protected NotPersisitBprocActors createNotPersisitBprocActors(BpmRolesLink link, DicHrRole hrRole) {
        NotPersisitBprocActors bprocActors = metadata.create(NotPersisitBprocActors.class);
        bprocActors.setHrRole(hrRole);
        bprocActors.setIsSystemRecord(true);
        bprocActors.setBprocUserTaskCode(link.getBprocUserTaskCode());
        bprocActors.setOrder(link.getOrder());
        bprocActors.setRolesLink(link);
        return bprocActors;
    }
}