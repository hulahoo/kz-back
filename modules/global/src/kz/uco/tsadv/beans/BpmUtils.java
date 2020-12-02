package kz.uco.tsadv.beans;

import com.haulmont.bali.util.ParamsMap;
//import com.haulmont.bpm.entity.*;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.bpm.BpmRolesDefiner;
import kz.uco.tsadv.modules.bpm.BpmRolesLink;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The component contain only simple methods as getter
 * <p/>
 * Complex methods written in {@link kz.uco.tsadv.service.BpmService}
 *
 * @author Alibek Berdaulet
 * @see kz.uco.tsadv.service.BpmService
 */
@Component("tsadv_BpmUtils")
public class BpmUtils {

    @Inject
    protected CommonService commonService;
    @Inject
    protected Metadata metadata;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected ReferenceToEntitySupport referenceToEntitySupport;

    /**
     * !!!! Do not change {@link CommonService#getEntity(Class, String, Map, String)} to {@link CommonService#emQueryFirstResult(Class, String, Map, String)} !!!!
     */
    @Nullable
    public UserExt getActiveTaskUser(@Nonnull UUID bpmProcInstanceId, @Nullable String viewName) {
        return commonService.getEntity(UserExt.class, "select u " +
                        " from bpm$ProcTask e " +
                        " join tsadv$UserExt u" +
                        " on u.id = e.procActor.user.id" +
                        " and e.procInstance.id = :id " +
                        "  where e.endDate is null  ",
                ParamsMap.of("id", bpmProcInstanceId), viewName != null ? viewName : View.LOCAL);
    }

    /*@Nullable
    public ProcTask getActiveProcTask(@Nonnull UUID bpmProcInstanceId, @Nullable String viewName) {
        return commonService.emQueryFirstResult(ProcTask.class, "select e " +
                        " from bpm$ProcTask e " +
                        "where e.procInstance.id = :procInstanceId " +
                        "  and e.endDate is null ",
                ParamsMap.of("procInstanceId", bpmProcInstanceId), viewName != null ? viewName : View.LOCAL);
    }*/

    @Nullable
    public UserExt getCreatedBy(@Nonnull UUID bpmProcInstanceId, @Nullable String viewName) {
        List<UserExt> list = commonService.getEntities(UserExt.class,
                " select e from tsadv$UserExt e join bpm$ProcInstance a " +
                        " on a.createdBy = e.login where a.id = :id ",
                ParamsMap.of("id", bpmProcInstanceId), viewName != null ? viewName : View.LOCAL);
        return list.isEmpty() ? null : list.get(0);
    }

    /*public List<ProcActor> getProcActors(@Nonnull ProcInstance procInstance, @Nullable String viewName) {
        return commonService.getEntities(ProcActor.class,
                " select e from bpm$ProcActor e where e.procInstance.id = :procInstance",
                ParamsMap.of("procInstance", procInstance.getId()), viewName != null ? viewName : View.LOCAL);
    }*/

    /*public List<ProcTask> getProcTaskList(@Nonnull UUID bpmProcInstanceId, @Nullable String viewName) {
        return commonService.emQueryResultList(ProcTask.class,
                "select e from bpm$ProcTask e " +
                        " where e.procInstance.id = :id " +
                        "   order by e.startDate ",
                ParamsMap.of("id", bpmProcInstanceId), viewName);
    }*/

    /*@Nonnull
    public ProcInstance getOrCreateProcInstance(@Nonnull ProcDefinition procDefinition, @Nonnull Entity entity) {
        ProcInstance procInstance = findProcInstance(procDefinition, entity);
        if (procInstance == null) {
            procInstance = metadata.create(ProcInstance.class);
            procInstance.setProcDefinition(procDefinition);
            procInstance.setObjectEntityId(entity.getId());
            procInstance.setEntityName(entity.getMetaClass().getName());
        }
        return procInstance;
    }*/

    /*@Nullable
    public ProcInstance findProcInstance(@Nonnull ProcDefinition procDefinition, @Nonnull Entity entity) {
        String referenceIdPropertyName = referenceToEntitySupport.getReferenceIdPropertyName(entity.getMetaClass());
        LoadContext<ProcInstance> ctx = LoadContext.create(ProcInstance.class).setView("procInstance-start");
        ctx.setQueryString("select pi from bpm$ProcInstance pi where pi.procDefinition.id = :procDefinition and " +
                "pi.entity." + referenceIdPropertyName + " = :entityId order by pi.deploymentDate desc")
                .setParameter("procDefinition", procDefinition)
                .setParameter("entityId", referenceToEntitySupport.getReferenceId(entity));
        List<ProcInstance> list = dataManager.loadList(ctx);
        return list.isEmpty() ? null : list.get(0);
    }*/

    /*@Nullable
    public List<BpmRolesLink> getBpmRolesLinks(@Nonnull PositionGroupExt positionGroupExt, @Nonnull ProcModel model, @Nullable String viewName) {
        List<BpmRolesLink> res = commonService.getEntities(BpmRolesLink.class,
                " select e from tsadv$BpmRolesLink e " +
                        "               join e.positionBpmRole pr" +
                        "               where pr.positionGroup.id = :positionGroupId " +
                        "                     and pr.procModel.id = :id ",
                ParamsMap.of("positionGroupId", positionGroupExt.getId(), "id", model.getId()),
                viewName != null ? viewName : View.LOCAL);
        BpmRolesDefiner definer = res.isEmpty() ? this.getBpmRolesDefiner(model, "bpmRolesDefiner-view") : null;
        return res.isEmpty() && definer != null ? definer.getLinks() : res;
    }*/

    /*@Nullable
    public BpmRolesDefiner getBpmRolesDefiner(@Nonnull ProcModel procModel, @Nullable String viewName) {
        List<BpmRolesDefiner> list = commonService.getEntities(BpmRolesDefiner.class, "select e from tsadv$BpmRolesDefiner e where e.procModel.id = :procModelId",
                ParamsMap.of("procModelId", procModel.getId()), viewName != null ? viewName : View.LOCAL);
        return list.isEmpty() ? null : list.get(0);
    }*/

    /*@Nullable
    public ProcDefinition getProcDefinition(@Nonnull Entity entity, @Nonnull String processName) {
        return getProcDefinition(entity, processName, null);
    }*/

    /*@Nullable
    public ProcDefinition getProcDefinition(@Nonnull Entity entity, @Nonnull String processName, @Nullable String viewName) {
        String referenceIdPropertyName = referenceToEntitySupport.getReferenceIdPropertyName(entity.getMetaClass());
        List<ProcDefinition> list = commonService.getEntities(ProcDefinition.class,
                "select pd from bpm$ProcInstance pi " +
                        " join pi.procDefinition pd" +
                        " where pi.entityName = :entityName " +
                        " and pd.model.name = :name " +
                        " and pi.entity." + referenceIdPropertyName + " = :entityId " +
                        " order by pi.createTs desc",
                ParamsMap.of("name", processName,
                        "entityId", referenceToEntitySupport.getReferenceId(entity),
                        "entityName", entity.getMetaClass().getName()), viewName != null ? viewName : View.LOCAL);

        return list.isEmpty() ? getActiveProcDefinition(processName, viewName) : list.get(0);
    }*/

    /*@Nullable
    public ProcDefinition getActiveProcDefinition(@Nonnull String processName, @Nullable String viewName) {
        List<ProcDefinition> list = commonService.getEntities(ProcDefinition.class,
                "select pd from bpm$ProcDefinition pd " +
                        "where pd.active = 'TRUE' and pd.model.name = :name order by pd.deploymentDate desc",
                ParamsMap.of("name", processName), viewName != null ? viewName : View.LOCAL);
        return list.isEmpty() ? null : list.get(0);
    }*/

    /*public ProcTask createInitiatorTask(ProcInstance procInstance) {
        ProcTask initiatorTask = metadata.create(ProcTask.class);
        ProcActor procActor = metadata.create(ProcActor.class);
        procActor.setUser(procInstance.getStartedBy());
        initiatorTask.setProcActor(procActor);
        initiatorTask.setStartDate(procInstance.getStartDate());
        initiatorTask.setEndDate(procInstance.getStartDate());
        initiatorTask.setComment(procInstance.getStartComment());
        initiatorTask.setOutcome("launch.process");
        initiatorTask.setName("initiator");
        return initiatorTask;
    }*/
}
