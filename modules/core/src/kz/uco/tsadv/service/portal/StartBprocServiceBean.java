package kz.uco.tsadv.service.portal;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.security.entity.User;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.tsadv.exceptions.PortalException;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.bpm.BpmRolesDefiner;
import kz.uco.tsadv.modules.bpm.BpmRolesLink;
import kz.uco.tsadv.modules.bpm.BprocActors;
import kz.uco.tsadv.modules.bpm.NotPersisitBprocActors;
import kz.uco.tsadv.modules.personal.dictionary.DicHrRole;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.service.OrganizationHrUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    @Override
    public BpmRolesDefiner getBpmRolesDefiner(String processDefinitionKey, UUID initiatorPersonGroupId) {
        DicCompany dicCompany = employeeService.getCompanyByPersonGroupId(initiatorPersonGroupId);
        UUID company = dicCompany != null ? dicCompany.getId() : null;
        List<BpmRolesDefiner> bpmRolesDefiners = dataManager.load(BpmRolesDefiner.class)
                .query("select e from tsadv$BpmRolesDefiner e where e.company.id = :companyId and e.processDefinitionKey = :processDefinitionKey ")
                .parameter("companyId", company)
                .parameter("processDefinitionKey", processDefinitionKey)
                .view("bpmRolesDefiner-view")
                .list();
        if (!bpmRolesDefiners.isEmpty()) return bpmRolesDefiners.get(0);

        bpmRolesDefiners = dataManager.load(BpmRolesDefiner.class)
                .query("select e from tsadv$BpmRolesDefiner e where e.processDefinitionKey = :processDefinitionKey")
                .parameter("processDefinitionKey", processDefinitionKey)
                .view("bpmRolesDefiner-view")
                .list()
                .stream()
                .filter(bpmRolesDefiner -> bpmRolesDefiner.getCompany() == null)
                .collect(Collectors.toList());

        if (!bpmRolesDefiners.isEmpty()) return bpmRolesDefiners.get(0);
        throw new RuntimeException("bpmRolesDefiner not found!");
    }

    @Override
    public List<NotPersisitBprocActors> getNotPersisitBprocActors(@Nullable TsadvUser employee, UUID initiatorPersonGroupId, BpmRolesDefiner bpmRolesDefiner) {
        List<NotPersisitBprocActors> actors = new ArrayList<>();
        List<BpmRolesLink> links = bpmRolesDefiner.getLinks();

        for (BpmRolesLink link : links) {
            DicHrRole hrRole = link.getHrRole();
            String roleCode = hrRole.getCode();
            List<? extends User> hrUsersForPerson = new ArrayList<>();

            if (roleCode.equals("EMPLOYEE")) {
                if (employee != null)
                    hrUsersForPerson = Collections.singletonList(dataManager.reload(employee, "user-fioWithLogin"));
            } else
                hrUsersForPerson = dataManager.load(TsadvUser.class)
                        .query("select e from tsadv$UserExt e where e in :users")
                        .setParameters(ParamsMap.of("users", organizationHrUserService.getHrUsersForPerson(initiatorPersonGroupId, roleCode)))
                        .view("user-fioWithLogin")
                        .list();

            if (hrUsersForPerson.isEmpty()) {
                if (!link.getIsAddableApprover())
                    throw new PortalException(String.format(messages.getMainMessage("hr.user.not.found"), hrRole.getLangValue()));

                if (link.getRequired()) {
                    NotPersisitBprocActors bprocActors = createNotPersisitBprocActors(link, hrRole);
                    bprocActors.setIsEditable(true);
                    actors.add(bprocActors);
                }
            } else {
                NotPersisitBprocActors bprocActors = createNotPersisitBprocActors(link, hrRole);
                bprocActors.setIsEditable(false);
                //noinspection unchecked
                bprocActors.setUsers((List<TsadvUser>) hrUsersForPerson);
                actors.add(bprocActors);
            }
        }
        return actors;
    }

    @Override
    public void saveBprocActors(UUID entityId, List<NotPersisitBprocActors> notPersisitBprocActors) {

        CommitContext commitContext = new CommitContext();

        dataManager.load(BprocActors.class)
                .query("select e from tsadv_BprocActors e where e.entityId = :entityId")
                .setParameters(ParamsMap.of("entityId", entityId))
                .list()
                .forEach(commitContext::addInstanceToRemove);

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
        return bprocActors;
    }
}