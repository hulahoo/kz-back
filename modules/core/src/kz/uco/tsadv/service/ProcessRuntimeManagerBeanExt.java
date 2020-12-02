package kz.uco.tsadv.service;

import com.google.common.base.Strings;
/*import com.haulmont.bpm.core.ProcTaskResult;
import com.haulmont.bpm.core.ProcessRuntimeManagerBean;
import com.haulmont.bpm.entity.ProcActor;
import com.haulmont.bpm.entity.ProcInstance;
import com.haulmont.bpm.entity.ProcTask;
import com.haulmont.bpm.exception.BpmException;*/
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.core.global.View;
import kz.uco.tsadv.exceptions.ItemNotFoundException;
import kz.uco.tsadv.modules.administration.UserExt;
/*import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.runtime.ProcessInstance;*/
import org.apache.commons.lang3.BooleanUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;

public class ProcessRuntimeManagerBeanExt /*extends ProcessRuntimeManagerBean */{

//    @Inject
//    protected BpmUserSubstitutionService bpmUserSubstitutionService;
//    @Inject
//    protected Metadata metadata;
//    @Inject
//    protected TimeSource timeSource;
//
//    @Override
//    public ProcInstance startProcess(ProcInstance procInstance, String comment, @Nullable Map<String, Object> variables) {
//        if (PersistenceHelper.isNew(procInstance)) {
//            throw new IllegalArgumentException("procInstance entity should be persisted");
//        }
//
//        Transaction tx = persistence.createTransaction();
//        try {
//            EntityManager em = persistence.getEntityManager();
//            procInstance = em.reload(procInstance, "procInstance-start");
//            if (procInstance == null) {
//                throw new BpmException("Cannot start process. ProcInstance not found in database.");
//            }
//            if (procInstance.getProcDefinition() == null) {
//                throw new BpmException("Cannot start process. ProcDefinition property is null.");
//            }
//            if (BooleanUtils.isTrue(procInstance.getActive())) {
//                throw new BpmException("Cannot start process. ProcessInstance is already active.");
//            }
//            if (!procInstance.getProcDefinition().getActive()) {
//                throw new BpmException("Cannot start process. Process definition is not active.");
//            }
//
//            if (variables == null)
//                variables = new HashMap<>();
//            variables.put("bpmProcInstanceId", procInstance.getId());
//            if (!Strings.isNullOrEmpty(procInstance.getEntityName())) {
//                variables.put("entityName", procInstance.getEntityName());
//            }
//            if (procInstance.getObjectEntityId() != null) {
//                variables.put("entityId", procInstance.getObjectEntityId());
//            }
//
//            procInstance.setStartComment(comment);
//            procInstance.setStartDate(timeSource.currentTimestamp());
//            procInstance.setStartedBy(userSessionSource.getUserSession().getCurrentOrSubstitutedUser());
//            procInstance = em.merge(procInstance);
//
//            ProcessInstance activitiProcessInstance = runtimeService.startProcessInstanceById(procInstance.getProcDefinition().getActId(), variables);
//
//            procInstance.setActive(true);
//            procInstance.setActProcessInstanceId(activitiProcessInstance.getProcessInstanceId());
//            procInstance = em.merge(procInstance);
//
//            tx.commit();
//            return procInstance;
//        } finally {
//            tx.end();
//        }
//    }
//
//    @Override
//    public ProcTask createProcTask(TaskEntity actTask) {
//        String assignee = actTask.getAssignee();
//        if (Strings.isNullOrEmpty(assignee))
//            throw new BpmException("No assignee defined for task " + actTask.getTaskDefinitionKey() + " with id = " + actTask.getId());
//
//        UUID bpmProcInstanceId = (UUID) actTask.getVariable("bpmProcInstanceId");
//        if (bpmProcInstanceId == null)
//            throw new BpmException("No 'bpmProcInstanceId' process variable defined for activiti process " + actTask.getProcessInstanceId());
//        EntityManager em = persistence.getEntityManager();
//
//        ProcInstance procInstance = em.find(ProcInstance.class, bpmProcInstanceId);
//        if (procInstance == null)
//            throw new BpmException("Process instance with id " + bpmProcInstanceId + " not found");
//
//        String roleCode = extensionElementsManager.getTaskProcRole(actTask.getProcessDefinitionId(), actTask.getTaskDefinitionKey());
//        if (Strings.isNullOrEmpty(roleCode))
//            throw new BpmException("ProcRole code for task " + actTask.getTaskDefinitionKey() + " not defined");
//        ProcActor procActor = findProcActor(procInstance, roleCode, UUID.fromString(assignee), actTask);
//        if (procActor == null)
//            throw new BpmException("ProcActor " + roleCode + " not defined");
//
//        ProcTask procTask = createNewProcTask(procActor, procInstance, actTask);
//        em.merge(procTask);
//        //reset variable '<taskName>_result'
//        String variableName = createResultVariableName(actTask.getTaskDefinitionKey());
//        ProcTaskResult taskResult = new ProcTaskResult();
//        runtimeService.setVariable(procTask.getActExecutionId(), variableName, taskResult);
//
//        return procTask;
//    }
//
//    @Nullable
//    protected ProcActor findProcActor(ProcInstance procInstance, String procRoleCode, UUID userId, TaskEntity actTask) {
//        Integer nrOfCompletedInstances = (Integer) runtimeService.getVariableLocal(actTask.getExecutionId(), "nrOfCompletedInstances");
//
//        ProcActor procActor;
//        try (Transaction tx = persistence.getTransaction()) {
//            EntityManager em = persistence.getEntityManager();
//            TypedQuery<ProcActor> query = em.createQuery("select pa from bpm$ProcActor pa " +
//                    "where pa.procInstance.id = :procInstanceId " +
//                    "and pa.procRole.code = :procRoleCode " +
//                    (nrOfCompletedInstances != null ? " and pa.order >= :order " : "") +
//                    "and pa.user.id = :userId", ProcActor.class);
//            query.setViewName("procActor-procTaskCreation");
//            query.setParameter("procInstanceId", procInstance.getId());
//            query.setParameter("procRoleCode", procRoleCode);
//            query.setParameter("userId", userId);
//            if (nrOfCompletedInstances != null) {
//                query.setParameter("order", nrOfCompletedInstances);
//            }
//            procActor = query.getFirstResult();
//
//            tx.commit();
//        }
//
//        if (procActor != null && procActor.getUser() != null) {
//            procActor = autoRedirect((UserExt) procActor.getUser(), procActor, procInstance, actTask);
//        }
//        return procActor;
//    }
//
//    public ProcActor autoRedirect(UserExt userExt, ProcActor procActor, ProcInstance procInstance, TaskEntity actTask) {
//        String idBpmUserSubstitutionPath = bpmUserSubstitutionService.getCurrentBpmUserSubstitution(userExt, true);
//        if (idBpmUserSubstitutionPath != null) {
//            try (Transaction tx = persistence.getTransaction()) {
//
//                EntityManager em = persistence.getEntityManager();
//
//                HashSet<String> stringSet = new HashSet<>();
//                String[] idBpmUserSubstitution = idBpmUserSubstitutionPath.split("\\*");
//                List<ProcActor> procActorList = getProcActorListAfter(procActor);
//                int maxOrder = procActor.getOrder();
//
//                for (int i = 0; i < idBpmUserSubstitution.length - 1; i++) {
//                    if (stringSet.contains(idBpmUserSubstitution[i])) {
//                        throw new ItemNotFoundException("Impossible define user substitution!");
//                    }
//                    stringSet.add(idBpmUserSubstitution[i]);
//                    UserExt user = em.find(UserExt.class, UUID.fromString(idBpmUserSubstitution[i]), View.MINIMAL);
//
//                    ProcActor actor = metadata.getTools().deepCopy(procActor);
//                    actor.setId(UUID.randomUUID());
//                    actor.setUser(user);
//                    actor.setOrder(procActor.getOrder() + i);
//                    maxOrder = Math.max(maxOrder, procActor.getOrder() + i);
//                    em.persist(actor);
//
//                    ProcTask task = createNewProcTask(actor, procInstance, actTask);
//                    task.setOutcome("autoRedirect");
//                    task.setActExecutionId("");
//                    task.setEndDate(timeSource.currentTimestamp());
//                    em.persist(task);
//                }
//                procActor.setUser(em.find(UserExt.class, UUID.fromString(idBpmUserSubstitution[idBpmUserSubstitution.length - 1]), View.MINIMAL));
//                maxOrder = Math.max(maxOrder, procActor.getOrder() + idBpmUserSubstitution.length - 1);
//                procActor.setOrder(procActor.getOrder() + idBpmUserSubstitution.length - 1);
//                procActor = em.merge(procActor);
//
//                for (ProcActor actor : procActorList) {
//                    maxOrder = Math.max(maxOrder, actor.getOrder() + idBpmUserSubstitution.length - 1);
//                    actor.setOrder(actor.getOrder() + idBpmUserSubstitution.length - 1);
//                    em.merge(actor);
//                }
//
//                runtimeService.setVariableLocal(actTask.getExecutionId(), "nrOfInstances", maxOrder);
//                runtimeService.setVariableLocal(actTask.getExecutionId(), "loopCounter", procActor.getOrder() - 1);
//                runtimeService.setVariableLocal(actTask.getExecutionId(), "nrOfCompletedInstances", procActor.getOrder() - 1);
//                runtimeService.setVariableLocal(actTask.getExecutionId(), "assignee", procActor.getUser().getId().toString());
//                runtimeService.setVariableLocal(actTask.getExecutionId(), "outcome", "autoRedirect");
//
//                tx.commit();
//            }
//        }
//        return procActor;
//    }
//
//    protected List<ProcActor> getProcActorListAfter(ProcActor procActor) {
//        List<ProcActor> actors;
//        try (Transaction tx = persistence.getTransaction()) {
//            EntityManager em = persistence.getEntityManager();
//            actors = em.createQuery("select e from bpm$ProcActor e " +
//                    " where e.procInstance.id = :procInstanceId " +
//                    " and e.procRole.id = :procRoleId " +
//                    " and e.order > :order ", ProcActor.class)
//                    .setParameter("procInstanceId", procActor.getProcInstance().getId())
//                    .setParameter("procRoleId", procActor.getProcRole().getId())
//                    .setParameter("order", procActor.getOrder())
//                    .getResultList();
//            tx.commit();
//        }
//        return actors;
//    }
//
//    public ProcTask createNewProcTask(ProcActor procActor, ProcInstance procInstance, TaskEntity actTask) {
//        ProcTask procTask = metadata.create(ProcTask.class);
//        procTask.setProcActor(procActor);
//        procTask.setProcInstance(procInstance);
//        procTask.setActExecutionId(actTask.getExecutionId());
//        procTask.setName(actTask.getName());
//        procTask.setActTaskDefinitionKey(actTask.getTaskDefinitionKey());
//        procTask.setActTaskId(actTask.getId());
//        procTask.setStartDate(timeSource.currentTimestamp());
//        procTask.setActProcessDefinitionId(actTask.getProcessDefinitionId());
//        return procTask;
//    }
}
