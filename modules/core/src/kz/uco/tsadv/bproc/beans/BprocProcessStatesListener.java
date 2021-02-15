package kz.uco.tsadv.bproc.beans;

import com.haulmont.addon.bproc.data.OutcomesContainer;
import com.haulmont.addon.bproc.entity.TaskData;
import com.haulmont.addon.bproc.events.UserTaskCompletedEvent;
import com.haulmont.addon.bproc.service.BprocRuntimeService;
import com.haulmont.addon.bproc.service.BprocTaskService;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.TransactionalDataManager;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.security.entity.User;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.bproc.beans.helper.AbstractBprocHelper;
import kz.uco.tsadv.bproc.events.ExtProcessStartedEvent;
import kz.uco.tsadv.bproc.events.ExtUserTaskCreatedEvent;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.service.BprocService;
import kz.uco.uactivity.entity.Activity;
import kz.uco.uactivity.entity.ActivityType;
import kz.uco.uactivity.entity.StatusEnum;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.identitylink.api.IdentityLinkInfo;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component(BprocProcessStatesListener.NAME)
public class BprocProcessStatesListener extends AbstractBprocHelper {
    public static final String NAME = "tsadv_BprocProcessStatesListener";

    @Inject
    private BprocRuntimeService bprocRuntimeService;
    @Inject
    protected CommonService commonService;
    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    protected Metadata metadata;
    @Inject
    protected Persistence persistence;
    @Inject
    protected BprocService bprocService;
    @Inject
    protected BprocTaskService bprocTaskService;
    @Inject
    protected TransactionalDataManager transactionalDataManager;

    @EventListener
    @SuppressWarnings("unchecked")
    protected <T extends AbstractBprocRequest> void onProcessStarted(ExtProcessStartedEvent event) {
        @SuppressWarnings("unchecked") T bprocRequest = (T) bprocRuntimeService.getVariable(event.getExecutionId(), "entity");

        bprocRequest = (T) transactionalDataManager.load(bprocRequest.getClass())
                .id(bprocRequest.getId())
                .view(new View(bprocRequest.getClass()).addProperty("status")).one();
        bprocRequest.setStatus(commonService.getEntity(DicRequestStatus.class, "APPROVING"));
        transactionalDataManager.save(bprocRequest);
    }

    @EventListener
    protected void onTaskCreated(ExtUserTaskCreatedEvent event) {
        if (!event.getTaskData().getTaskDefinitionKey().equals("initiator_task")) {
            notifyApprovers(event);
            notifyInitiator(event);
        }
    }

    protected <T extends AbstractBprocRequest> void notifyInitiator(ExtUserTaskCreatedEvent event) {
        TaskData taskData = event.getTaskData();
        String executionId = taskData.getExecutionId();
        @SuppressWarnings("unchecked") T bprocRequest = (T) bprocRuntimeService.getVariable(executionId, "entity");
        bprocService.sendNotificationToInitiator(bprocRequest);
    }

    protected <T extends AbstractBprocRequest> void notifyApprovers(ExtUserTaskCreatedEvent event) {
        TaskData taskData = event.getTaskData();

        String executionId = taskData.getExecutionId();
        @SuppressWarnings("unchecked") T bprocRequest = (T) bprocRuntimeService.getVariable(executionId, "entity");

        String notificationTemplateCode = (String) bprocRuntimeService.getVariable(executionId, "approverNotificationTemplateCode");

        List<User> userList = new ArrayList<>();

        List<UUID> uuidList = new ArrayList<>();

        Set<IdentityLink> candidates = event.getCandidates();
        if (!CollectionUtils.isEmpty(candidates)) {
            uuidList = candidates.stream().map(IdentityLinkInfo::getUserId).map(UUID::fromString).collect(Collectors.toList());
        }
        if (event.getUser() != null) uuidList.add(event.getUser().getId());

        if (!uuidList.isEmpty())
            userList.addAll(dataManager.load(User.class)
                    .query("select e from sec$User e where e.id in :idList ")
                    .setParameters(ParamsMap.of("idList", uuidList))
                    .view("user-fioWithLogin")
                    .list());

        ActivityType activityType = getActivityFromEntity(bprocRequest);

        if (userList.size() == 1) {
            User user = userList.get(0);
            bprocTaskService.claim(taskData.getId(), user);
            taskData.setAssignee(user.getId().toString());
        }

        userList.forEach(user -> bprocService.sendNotificationAndActivity(bprocRequest, user, activityType, notificationTemplateCode));
    }

    @SuppressWarnings("ConstantConditions")
    @EventListener
    @Transactional
    protected <T extends AbstractBprocRequest> void onTaskCompleted(UserTaskCompletedEvent event) {
        TaskData taskData = event.getTaskData();

        T bprocRequest = bprocService.getProcessVariable(taskData.getProcessInstanceId(), "entity");

        List<Activity> activityList = getActivityList(bprocRequest.getId(), getActivityCodeFromTableName(bprocRequest));
        EntityManager entityManager = persistence.getEntityManager();
        for (Activity activity : activityList) {
            activity.setStatus(StatusEnum.done);
            entityManager.merge(activity);
        }

        OutcomesContainer outcomesContainer = bprocService.getProcessVariable(taskData.getProcessInstanceId(), taskData.getTaskDefinitionKey() + "_result");

        boolean isOutcomeReject = outcomesContainer.getOutcomes().stream()
                .anyMatch(outcome -> outcome.getOutcomeId().equals(AbstractBprocRequest.OUTCOME_REJECT));
        if (isOutcomeReject) bprocService.reject(bprocRequest);
    }

    protected List<Activity> getActivityList(UUID referenceId, String taskCode) {
        LoadContext<Activity> loadContext = LoadContext.create(Activity.class)
                .setQuery(LoadContext.createQuery(
                        "select e from uactivity$Activity e " +
                                " where e.referenceId = :referenceId " +
                                "  and e.type.code = :taskCode ")
                        .setParameter("referenceId", referenceId)
                        .setParameter("taskCode", taskCode)
                ).setView(View.LOCAL);
        return dataManager.loadList(loadContext);
    }
}