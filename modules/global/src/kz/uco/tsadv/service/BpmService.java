package kz.uco.tsadv.service;

//import com.haulmont.bpm.entity.ProcActor;
//import com.haulmont.bpm.entity.ProcInstance;
//import com.haulmont.bpm.entity.ProcTask;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.security.entity.User;
import kz.uco.tsadv.entity.dbview.ActivityTask;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.AbsenceRequest;
import kz.uco.tsadv.modules.personal.model.BpmRequestMessage;
import kz.uco.uactivity.entity.Activity;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

/**
 * The service contain main logic of bpm process
 * <p/>
 * Simple methods written in {@link kz.uco.tsadv.beans.BpmUtils}
 *
 * @see kz.uco.tsadv.beans.BpmUtils
 */
public interface BpmService {
    String NAME = "tsadv_BpmService";
    /**//*
        /**
         * @param entity         have to save in DB
         * @param processName    bpm model name
         * @param personGroupExt employee that start process
         /
    void startBpmProcess(@Nonnull Entity entity, @Nonnull String processName, @Nonnull PersonGroupExt personGroupExt,
                         PositionGroupExt positionGroupExt, OrganizationGroupExt organizationGroupExt);*//*

    void start(UUID entityId, String entityName, UUID bpmProcInstanceId);

    void reject(UUID entityId, String entityName, UUID bpmProcInstanceId);

    void approve(UUID entityId, String entityName, UUID bpmProcInstanceId);

    *//*@Deprecated
    void approve(UUID entityId, String entityName, UUID bpmProcInstanceId, String emailTemplateCode, String notificationTemplateCode);*//*

    @Deprecated
    void reject(UUID entityId, String entityName, UUID bpmProcInstanceId, String emailTemplateCode, String notificationTemplateCode);

//    void notifyAssignee(UUID bpmProcInstanceId, UUID bpmProcTaskId, String emailTemplateCode, String notificationTemplateCode);

    @Deprecated
    void notify(UUID entityId, String entityName, UUID bpmProcInstanceId, String role, String emailTemplateCode);

    *//*@Deprecated
    void notify(UUID entityId, String entityName, UUID bpmProcInstanceId, String role, String emailTemplateCode, String notificationTemplateCode);*//*

//    void sendUserNotification(User assignedUser, User assignedBy, UUID entityId, UUID bpmProcInstanceId, String activityCode, String emailTemplateCode, String notificationTemplateCode);

    void sendNotifyToInitiator(UUID entityId, UUID bpmProcInstanceId, String emailTemplateCode, String template);

//    boolean hasActor(UUID bpmProcInstanceId, String role);

//    UserExt getNextApprover(ProcActor procActor);

    @Deprecated
    void doneActivity(UUID entityId, String activityCode);

//    void doneActivity(UUID bpmProcInstanceId);

    *//**
     * Create procActor with order = currentProcActor.getOrder() + 1
     *
     * @param currentProcTask  Current ProcTask
     * @param currentProcActor Current ProcActor
     * @param items            Collection of procActors (all list ProcActors where current ProcRole(in currentProcActor) equals list.get(i).procRole )
     * @param userExt          Reassign user
     *//*
//    void commitReassignProcActor(ProcTask currentProcTask, ProcActor currentProcActor, Collection<ProcActor> items, UserExt userExt);

    *//**
     * Called in bpm model
     *
     * @param absenceRequestId {@link AbsenceRequest#getId()}
     * @return true if {@link AbsenceRequest#getType()} is long else false
     *//*
    boolean isAbsenceTypeLong(UUID absenceRequestId);

    *//**
     * @param date   date
     * @param status status
     * @return date - 1 if  {@link DicRequestStatus#getCode()} equals one of the APPROVED|CANCELLED|CANCELED else date
     *//*
    Date getDate(Date date, DicRequestStatus status);

    boolean isExpiredTask(ActivityTask activityTask);

    void schedulerExpiredTask();

    void notifyManagersAboutAccessInf(Date date,
                                      PersonGroupExt personGroup,
                                      OrganizationGroupExt newOrganizationGroup,
                                      OrganizationGroupExt oldOrganizationGroup,
                                      PositionGroupExt newPositionGroup,
                                      PositionGroupExt oldPositionGroup, boolean isScheduler);

    void schedulerNotifyManagersAboutAccessInf();

//    String getRequestUrl(Activity activity, ProcInstance procInstance);

    @Nonnull
    Activity bpmRequestAskAnswerNotification(BpmRequestMessage bpmRequestMessage);*/
}