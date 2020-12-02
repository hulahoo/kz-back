package kz.uco.tsadv.app;

//import com.haulmont.bpm.core.ProcessRuntimeManager;
//import com.haulmont.bpm.entity.ProcActor;
//import com.haulmont.bpm.entity.ProcInstance;
//import com.haulmont.bpm.entity.ProcRole;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.*;
import com.haulmont.cuba.core.app.EmailService;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.common.exceptions.SmsException;
import kz.uco.base.entity.abstraction.AbstractDictionary;
import kz.uco.base.entity.core.notification.SendingNotification;
import kz.uco.base.notification.NotificationSenderAPI;
import kz.uco.base.service.SmsService;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.personal.dictionary.DicApprovalStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicDismissalStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicOrderStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.modules.recruitment.dictionary.DicInterviewReason;
import kz.uco.tsadv.modules.recruitment.enums.InterviewStatus;
import kz.uco.tsadv.modules.recruitment.enums.OfferStatus;
import kz.uco.tsadv.modules.recruitment.model.Interview;
import kz.uco.tsadv.modules.recruitment.model.Offer;
import kz.uco.tsadv.modules.recruitment.model.OfferHistory;
import kz.uco.tsadv.modules.recruitment.model.Requisition;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.uactivity.entity.ActivityType;
import kz.uco.uactivity.entity.StatusEnum;
import kz.uco.uactivity.service.ActivityService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author veronika.buksha
 */
@Component("tal_bpmUtil")
public class BPMUtil {

    private Logger log = LoggerFactory.getLogger(BPMUtil.class);

    @Inject
    private Persistence persistence;

//    @Inject
//    private ProcessRuntimeManager processRuntimeManager;

    @Inject
    protected EmailService emailService;

    @Inject
    private SmsService smsService;

    @Inject
    protected Metadata metadata;

    @Inject
    protected DataManager dataManager;

    @Inject
    private NotificationSenderAPI notificationSender;

    @Inject
    private CommonService commonService;

    @Inject
    private EmployeeService employeeService;

    @Inject
    private ActivityService activityService;

    /*public boolean hasPerson(UUID bpmProcInstanceId, String role, String isCorrect) {
        return !processRuntimeManager.getTaskAssigneeList(bpmProcInstanceId, role).isEmpty() == "true".equals(isCorrect);
    }*/

    /*public void interviewApproval(UUID entityId, UUID bpmProcInstanceId, String action) {
        EntityManager em = persistence.getEntityManager();
        Interview interview = em.find(Interview.class, entityId, "interview.full");
        ProcInstance procInstance = em.find(ProcInstance.class, bpmProcInstanceId);
        Map<String, Object> templateParams = new HashMap<>();

        switch (action) {
            case "assignActors":
                for (ProcActor procActor : procInstance.getProcActors())
                    dataManager.remove(procActor);

                Map<String, ProcActor> procActors = new HashMap<>();

                UserExt mainInterviewerUser = getUserByKey("personGroup.id", interview.getMainInterviewerPersonGroup().getId());
                if (mainInterviewerUser != null) {
                    ProcActor mainInterviewerUserActor = metadata.create(ProcActor.class);
                    mainInterviewerUserActor.setUser(mainInterviewerUser);
                    mainInterviewerUserActor.setProcInstance(procInstance);
                    mainInterviewerUserActor.setOrder(0);
                    procActors.put("mainInterviewerUser", mainInterviewerUserActor);
                }

                if (procInstance.getProcDefinition() != null)
                    for (ProcRole pr : procInstance.getProcDefinition().getProcRoles()) {
                        if (procActors.containsKey(pr.getCode()))
                            procActors.get(pr.getCode()).setProcRole(pr);
                    }

                procInstance.setProcActors(new HashSet<>(procActors.values()));

                List<Entity> commitInstances = new ArrayList<>(procActors.values());
                dataManager.commit(new CommitContext(commitInstances));
                break;
            case "notifyMainInterviewerUser":
                templateParams.put("interview", interview);
                processRuntimeManager.getTaskAssigneeList(bpmProcInstanceId, "mainInterviewerUser").stream().forEach(a -> {
                    sendNotification("interview.mainInterviewer.notification", a, templateParams);
                });
                break;
            case "plan":
                updateInterviewStatus(entityId, InterviewStatus.PLANNED);
                break;
            case "cancel":
                updateInterviewStatus(entityId, InterviewStatus.CANCELLED);
                break;
        }
    }*/

    private void updateInterviewStatus(UUID entityId, InterviewStatus status) {
        try (Transaction tx = persistence.getTransaction()) {
            Interview entity = persistence.getEntityManager().find(Interview.class, entityId, "interview.full");
            if (entity != null) {
                entity.setInterviewStatus(status);
                if (status == InterviewStatus.CANCELLED)
                    try {
                        entity.setInterviewReason((DicInterviewReason) getDictionaryValueByCode("tsadv$DicInterviewReason", "MAIN_INTERVIEWER"));
                    } catch (Exception e) {
                        log.error("No dictionary value for code MAIN_INTERVIEWER");
                    }
            }
            tx.commit();
        }
    }

    /*public void orderApproval(UUID entityId, UUID bpmProcInstanceId, String action) {
        EntityManager em = persistence.getEntityManager();
        Order order = em.find(Order.class, entityId, "order-view");
        ProcInstance procInstance = em.find(ProcInstance.class, bpmProcInstanceId);

        switch (action) {
            case "assignActor":
                for (ProcActor procActor : procInstance.getProcActors())
                    dataManager.remove(procActor);

                updateOrderStatus(order.getId(), "APPROVING");

                UserExt approverUser = getUserByKey("personGroup.id", order.getApproverPersonGroup().getId());

                ProcActor approverActor = metadata.create(ProcActor.class);
                approverActor.setUser(approverUser);
                approverActor.setProcInstance(procInstance);
                approverActor.setOrder(0);

                if (procInstance.getProcDefinition() != null)
                    for (ProcRole pr : procInstance.getProcDefinition().getProcRoles()) {
                        if ("approver".equals(pr.getCode()))
                            approverActor.setProcRole(pr);
                    }
                Set<ProcActor> procActorSet = new HashSet<>();
                procActorSet.add(approverActor);
                procInstance.setProcActors(procActorSet);

                List<Entity> commitInstances = new ArrayList<>();
                commitInstances.add(approverActor);
                dataManager.commit(new CommitContext(commitInstances));
                break;
            case "notifyActor":
                Map<String, Object> templateParams = new HashMap<>();
                templateParams.put("order", order);

                processRuntimeManager.getTaskAssigneeList(bpmProcInstanceId, "approver").stream().forEach(a -> {
                    sendNotification("order.approver.notification", a, templateParams);
                });
                break;
            case "cancel":
                updateOrderStatus(order.getId(), "CANCELLED");
                break;
            case "approve":
                updateOrderStatus(order.getId(), "APPROVED");

                if ("DISMISSAL".equals(order.getOrderType().getCode())) {
                    dismissEmployee(order.getOrdAssignment());
                }
                break;
            default:
                break;
        }

    }*/

    /*public void transferRequestApproval(UUID entityId, UUID bpmProcInstanceId, String action) {
        EntityManager em = persistence.getEntityManager();
        TransferRequest transferRequest = em.find(TransferRequest.class, entityId, "transferRequest.edit");
        ProcInstance procInstance = em.find(ProcInstance.class, bpmProcInstanceId);
        Map<String, Object> templateParams = new HashMap<>();

        switch (action) {
            case "assignActors":
                for (ProcActor procActor : procInstance.getProcActors())
                    dataManager.remove(procActor);

                Map<String, List<ProcActor>> procActors = new HashMap<>();

                UserExt transferUser = getUserByKey("personGroup.id", transferRequest.getAssignmentGroup().getAssignment().getPersonGroup().getId());
                UserExt newManagerUser = findManagerUserByPosition(transferRequest.getNewPositionGroup().getId());
                if (transferUser != null) {
                    ProcActor transferUserActor = metadata.create(ProcActor.class);
                    transferUserActor.setUser(transferUser);
                    transferUserActor.setProcInstance(procInstance);
                    transferUserActor.setOrder(0);
                    procActors.put("transferUser", Collections.singletonList(transferUserActor));
                }
                if (newManagerUser != null && !newManagerUser.getLogin().equals(transferRequest.getCreatedBy())) {
                    ProcActor newManagerUserActor = metadata.create(ProcActor.class);
                    newManagerUserActor.setUser(newManagerUser);
                    newManagerUserActor.setProcInstance(procInstance);
                    newManagerUserActor.setOrder(1);
                    procActors.put("newManagerUser", Collections.singletonList(newManagerUserActor));
                }
                List<OrganizationHrUser> hrUsers = employeeService.getHrUsers(transferRequest.getNewOrganizationGroup().getId(), "HR_SPECIALIST");

                if (hrUsers != null && !hrUsers.isEmpty()) {
                    procActors.put("newHrSpecialistUser", new ArrayList<>());
                    for (OrganizationHrUser organizationHrUser : hrUsers) {
                        ProcActor hewHrSpecialistUserActor = metadata.create(ProcActor.class);
                        hewHrSpecialistUserActor.setUser(organizationHrUser.getUser());
                        hewHrSpecialistUserActor.setProcInstance(procInstance);
                        hewHrSpecialistUserActor.setOrder(2);
                        procActors.get("newHrSpecialistUser").add(hewHrSpecialistUserActor);
                    }
                }
                if (!procActors.containsKey("newHrSpecialistUser")) {
                    throw new RuntimeException("No HR specialist to approve your entity!");
                }

                if (!procActors.containsKey("transferUser")) {
                    throw new RuntimeException("No transfer user to approve your entity!");
                }
                if (procInstance.getProcDefinition() != null)
                    for (ProcRole pr : procInstance.getProcDefinition().getProcRoles()) {
                        if (procActors.containsKey(pr.getCode()))
                            procActors.get(pr.getCode()).forEach(pa -> pa.setProcRole(pr));
                    }
                procInstance.setProcActors(new HashSet<>(procActors.values().stream().flatMap(List::stream).collect(Collectors.toList())));
                List<Entity> commitInstances = new ArrayList<>(procActors.values().stream().flatMap(List::stream).collect(Collectors.toList()));

                dataManager.commit(new CommitContext(commitInstances));
                updateTransferRequestStatus(transferRequest.getId(), "APPROVING");

                break;
            case "notifyTransferUser":
                templateParams.put("transferRequest", transferRequest);
                processRuntimeManager.getTaskAssigneeList(bpmProcInstanceId, "transferUser").stream().forEach(a -> {
                    sendNotification("transferRequest.transferUser.notification", a, templateParams);
                });
                break;
            case "notifyNewManagerUser":
                templateParams.put("transferRequest", transferRequest);
                processRuntimeManager.getTaskAssigneeList(bpmProcInstanceId, "newManagerUser").stream().forEach(a -> {
                    sendNotification("transferRequest.newManagerUser.notification", a, templateParams);
                });
                break;
            case "notifyNewHrSpecialistUsers":
                templateParams.put("transferRequest", transferRequest);
                processRuntimeManager.getTaskAssigneeList(bpmProcInstanceId, "newHrSpecialistUser").stream().forEach(a -> {
                    sendNotification("transferRequest.newHrSpecialistUser.notification", a, templateParams);
                });
                break;
            case "cancel":
                updateTransferRequestStatus(transferRequest.getId(), "CANCELLED");
                break;
            case "approve":
                transferEmployee(transferRequest);
                updateTransferRequestStatus(transferRequest.getId(), "APPROVED");
                break;
        }

    }*/

    /*public void personEntityApproval(UUID entityId, UUID bpmProcInstanceId, String action) {
        EntityManager em = persistence.getEntityManager();
        ProcInstance procInstance = em.find(ProcInstance.class, bpmProcInstanceId);
        MetaClass entityMetaClass = metadata.getSession().getClassNN(procInstance.getEntityName());
        Entity<UUID> entity = null; *//*(Entity<UUID>) em.find(entityMetaClass.getJavaClass(), entityId, new View(entityMetaClass.getJavaClass()).addProperty("personGroup"));*//*

        if (entityMetaClass.getProperty("personGroup") != null) {
            switch (procInstance.getEntityName()) {
                case "tsadv$PersonDocument":
                    entity = em.find(PersonDocument.class, entityId, "personDocument.full");
                    break;
            }

            Map<String, Object> templateParams = new HashMap<>();
            switch (action) {
                case "assignActors":

                    for (ProcActor procActor : procInstance.getProcActors())
                        dataManager.remove(procActor);

                    Map<String, List<ProcActor>> procActors = new HashMap<>();
                    Map<String, Object> queryParams = new HashMap<>();
                    queryParams.put("personGroupId", ((HasPersonGroup) entity).getPersonGroup().getId());
                    queryParams.put("sysdate", CommonUtils.getSystemDate());

                    List<OrganizationGroupExt> organizationGroups = commonService.getEntities(OrganizationGroupExt.class,
                            "select e " +
                                    " from base$OrganizationGroupExt e, tsadv$PositionStructure ps, base$AssignmentExt a" +
                                    " where " +
                                    " a.personGroup.id = :personGroupId " +
                                    " and :sysdate between a.startDate and a.endDate " +
                                    " and ps.positionGroup.id = a.positionGroup.id " +
                                    " and :sysdate between ps.startDate and ps.endDate " +
                                    " and :sysdate between ps.posStartDate and ps.posEndDate " +
                                    " and e.id = ps.organizationGroup.id ",
                            queryParams,
                            View.LOCAL);

                    if (organizationGroups != null && !organizationGroups.isEmpty()) {
                        List<OrganizationHrUser> hrUsers = employeeService.getHrUsers(organizationGroups.get(0).getId(), "HR_SPECIALIST");

                        if (hrUsers != null && !hrUsers.isEmpty()) {
                            procActors.put("hrSpecialistUser", new ArrayList<>());
                            for (OrganizationHrUser organizationHrUser : hrUsers) {
                                ProcActor hrSpecialistUserActor = metadata.create(ProcActor.class);
                                hrSpecialistUserActor.setUser(organizationHrUser.getUser());
                                hrSpecialistUserActor.setProcInstance(procInstance);
                                hrSpecialistUserActor.setOrder(0);
                                procActors.get("hrSpecialistUser").add(hrSpecialistUserActor);
                            }
                        }
                    }

                    if (!procActors.containsKey("hrSpecialistUser")) {
                        throw new RuntimeException("No HR specialist to approve your entity!");
                    } else {
                        if (procInstance.getProcDefinition() != null)
                            for (ProcRole pr : procInstance.getProcDefinition().getProcRoles()) {
                                if (procActors.containsKey(pr.getCode()))
                                    procActors.get(pr.getCode()).forEach(pa -> pa.setProcRole(pr));
                            }

                        procInstance.setProcActors(new HashSet<>(procActors.values().stream().flatMap(List::stream).collect(Collectors.toList())));
                        List<Entity> commitInstances = new ArrayList<>(procActors.values().stream().flatMap(List::stream).collect(Collectors.toList()));
                        dataManager.commit(new CommitContext(commitInstances));
                    }
                    updateApprovalStatus(entity, "APPROVING");
                    break;
                case "notifyHrSpecialistUser":
                    String notificationCode = null;

                    switch (procInstance.getEntityName()) {
                        case "tsadv$PersonDocument":
                            notificationCode = "personDocumentApproval.hrSpecialistUser.notification";
                            templateParams.put("entity", (PersonDocument) entity);
                            break;
                    }

                    for (String userId : processRuntimeManager.getTaskAssigneeList(bpmProcInstanceId, "hrSpecialistUser")) {
                        sendNotification(notificationCode, userId, templateParams);
                    }
                    break;
                case "cancel":
                    updateApprovalStatus(entity, "CANCELLED");
                    break;
                case "approve":
                    updateApprovalStatus(entity, "APPROVED");
                    break;
            }
        }

    }*/

    public boolean isNeedBusinessPartnerApprove(UUID entityId, String direction) {
        EntityManager em = persistence.getEntityManager();
        Offer offer = em.find(Offer.class, entityId, "offer.bpm");
        return "true".equals(direction) == offer.getNeedBuisnessPartnerApprove();
    }

    public boolean hasBusinessPartner(UUID entityId, String direction) {
        EntityManager em = persistence.getEntityManager();
        Offer offer = em.find(Offer.class, entityId, "offer.bpm");
        List<OrganizationHrUser> partnersList = employeeService.getHrUsers(offer.getJobRequest().getRequisition().getOrganizationGroup().getId(),
                "HR_BUSINESS_PARTNER");
        return partnersList.size() > 0 == "true".equals(direction);
    }

    /*public void offerApproval(UUID entityId, UUID bpmProcInstanceId, String action) throws ParseException {
        EntityManager em = persistence.getEntityManager();
        Offer offer = em.find(Offer.class, entityId, "offer.bpm");
        ProcInstance procInstance = em.find(ProcInstance.class, bpmProcInstanceId);
        Map<String, Object> templateParams = new HashMap<>();
        OfferHistory offerHistory = metadata.create(OfferHistory.class);
        List<OrganizationHrUser> partnersList;
        Set<ProcActor> procActorSet = new HashSet<>();
        List<Entity> commitInstances = new ArrayList<>();
        switch (action) {
            case "assignActors":
                for (ProcActor procActor : procInstance.getProcActors()) {
                    dataManager.remove(procActor);
                }

                procActorSet.clear();
                commitInstances.clear();

                UserExt approverUser = getUserByKey("personGroup.id",
                        offer.getJobRequest().getRequisition().getManagerPersonGroup().getId());

                ProcActor approverActor = metadata.create(ProcActor.class);
                approverActor.setUser(approverUser);
                approverActor.setProcInstance(procInstance);
                approverActor.setOrder(0);

                if (procInstance.getProcDefinition() != null)
                    for (ProcRole pr : procInstance.getProcDefinition().getProcRoles()) {
                        if ("approver".equals(pr.getCode()))
                            approverActor.setProcRole(pr);
                    }

                procActorSet.add(approverActor);

                UserExt hrManagerUser = getUserByKey("personGroup.id",
                        offer.getJobRequest().getRequisition().getRecruiterPersonGroup().getId());

                ProcActor hrManagerActor = metadata.create(ProcActor.class);
                hrManagerActor.setUser(hrManagerUser);
                hrManagerActor.setProcInstance(procInstance);
                hrManagerActor.setOrder(1);

                if (procInstance.getProcDefinition() != null)
                    for (ProcRole pr : procInstance.getProcDefinition().getProcRoles()) {
                        if ("hrManager".equals(pr.getCode()))
                            hrManagerActor.setProcRole(pr);
                    }

                procActorSet.add(hrManagerActor);

                procInstance.setProcActors(procActorSet);
                commitInstances.add(approverActor);
                commitInstances.add(hrManagerActor);

                updateOfferStatus(offer, OfferStatus.ONAPPROVAL);
                offerHistory = metadata.create(OfferHistory.class);
                offerHistory.setChangedBy(AppBeans.get(UserSessionSource.class).getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP));
                offerHistory.setOffer(offer);
                offerHistory.setStatus(OfferStatus.ONAPPROVAL);
                offerHistory.setStatusChangeDate(new Date());
                commitInstances.add(offerHistory);

                dataManager.commit(new CommitContext(commitInstances));

                break;
            case "onAgreement":
                updateOfferStatus(offer, OfferStatus.ONAPPROVAL);
                offerHistory.setChangedBy(AppBeans.get(UserSessionSource.class).getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP));
                offerHistory.setOffer(offer);
                offerHistory.setStatus(OfferStatus.ONAPPROVAL);
                offerHistory.setStatusChangeDate(new Date());
                dataManager.commit(offerHistory);
                break;
            case "notifyApprover":
                templateParams.clear();
                templateParams.put("offer", offer);

                templateParams.put("personFullName", offer.getJobRequest().getRequisition().getManagerPersonGroup() != null ?
                        offer.getJobRequest().getRequisition().getManagerPersonGroup().getList().get(0).getFullName() : "");
                templateParams.put("positionName",
                        offer.getJobRequest().getRequisition().getPositionGroup() == null
                                ? offer.getJobRequest().getRequisition().getJobGroup().getJob().getJobName()
                                : offer.getJobRequest().getRequisition().getPositionGroup().getPosition().getPositionName());
                templateParams.put("requisitionNameRu", offer.getJobRequest().getRequisition().getNameForSiteLang1() != null
                        ? offer.getJobRequest().getRequisition().getNameForSiteLang1()
                        : "");
                templateParams.put("requisitionNameKz", offer.getJobRequest().getRequisition().getNameForSiteLang2() != null
                        ? offer.getJobRequest().getRequisition().getNameForSiteLang2()
                        : "");
                templateParams.put("requisitionNameEn", offer.getJobRequest().getRequisition().getNameForSiteLang3() != null
                        ? offer.getJobRequest().getRequisition().getNameForSiteLang3()
                        : "");
                templateParams.put("requisitionCode", offer.getJobRequest().getRequisition().getCode() != null ? offer.getJobRequest().getRequisition().getCode() : "");

                processRuntimeManager.getTaskAssigneeList(bpmProcInstanceId, "approver").stream().forEach(a -> {
                    SendingNotification sendingNotification = sendNotification("offer.approver.notification", a, templateParams);
                    activityService.createActivity(
                            getUserByKey("id", UUID.fromString(a)),
                            employeeService.getSystemUser(),
                            commonService.getEntity(ActivityType.class, "OFFER_APPROVE"),
                            StatusEnum.active,
                            "description",
                            null,
                            new Date(),
                            null,
                            null,
                            entityId,
                            "offer.approver.notification",
                            templateParams);
                });
                break;
            case "approve":
                if (offer.getNeedBuisnessPartnerApprove()) {
                    for (ProcActor procActor : procInstance.getProcActors()) {
                        if ("businessPartner".equals(procActor.getProcRole().getCode()))
                            dataManager.remove(procActor);
                    }
                    partnersList = employeeService.getHrUsers(offer.getJobRequest().getRequisition().getOrganizationGroup().getId(),
                            "HR_BUSINESS_PARTNER");
                    if (partnersList.size() > 0) {
                        procActorSet.clear();
                        commitInstances.clear();
                        for (OrganizationHrUser organizationHrUser : partnersList) {
                            UserExt bisinessPartnerUser = organizationHrUser.getUser();

                            ProcActor businessPartnerActor = metadata.create(ProcActor.class);
                            businessPartnerActor.setUser(bisinessPartnerUser);
                            businessPartnerActor.setProcInstance(procInstance);
                            businessPartnerActor.setOrder(0);
                            if (procInstance.getProcDefinition() != null)
                                for (ProcRole pr : procInstance.getProcDefinition().getProcRoles()) {
                                    if ("businessPartner".equals(pr.getCode()))
                                        businessPartnerActor.setProcRole(pr);
                                }
                            procActorSet.add(businessPartnerActor);
                            commitInstances.add(businessPartnerActor);
                        }
                        procInstance.setProcActors(procActorSet);
                        dataManager.commit(new CommitContext(commitInstances));

                        templateParams.clear();
                        templateParams.put("offer", offer);
                        if (partnersList != null && partnersList.get(0) != null && partnersList.get(0).getUser() != null) {
                            String lastName = partnersList.get(0).getUser().getLastName() != null ?
                                    partnersList.get(0).getUser().getLastName() : "";
                            String firstName = partnersList.get(0).getUser().getFirstName() != null ?
                                    partnersList.get(0).getUser().getFirstName() : "";
                            templateParams.put("personFullName", lastName + " " + firstName);

                        } else {
                            templateParams.put("personFullName", "");
                        }
                        templateParams.put("positionName",
                                offer.getJobRequest().getRequisition().getPositionGroup() == null
                                        ? offer.getJobRequest().getRequisition().getJobGroup().getJob().getJobName()
                                        : offer.getJobRequest().getRequisition().getPositionGroup().getPosition().getPositionName());
                        templateParams.put("requisitionNameRu", offer.getJobRequest().getRequisition().getNameForSiteLang1() != null
                                ? offer.getJobRequest().getRequisition().getNameForSiteLang1()
                                : "");
                        templateParams.put("requisitionNameKz", offer.getJobRequest().getRequisition().getNameForSiteLang2() != null
                                ? offer.getJobRequest().getRequisition().getNameForSiteLang2()
                                : "");
                        templateParams.put("requisitionNameEn", offer.getJobRequest().getRequisition().getNameForSiteLang3() != null
                                ? offer.getJobRequest().getRequisition().getNameForSiteLang3()
                                : "");
                        templateParams.put("requisitionCode", offer.getJobRequest().getRequisition().getCode());
                        processRuntimeManager.getTaskAssigneeList(bpmProcInstanceId, "businessPartner").stream().forEach(a -> {
                            sendNotification("offer.approver.notification", a, templateParams);
                            activityService.createActivity(
                                    getUserByKey("id", UUID.fromString(a)),
                                    employeeService.getSystemUser(),
                                    commonService.getEntity(ActivityType.class, "OFFER_APPROVE"),
                                    StatusEnum.active,
                                    "description",
                                    null,
                                    new Date(),
                                    null,
                                    null,
                                    entityId,
                                    "offer.approver.notification",
                                    templateParams);
                        });
                    }
                } else {
                    approveOffer(offer, templateParams, offerHistory);
                }
                processRuntimeManager.getTaskAssigneeList(bpmProcInstanceId, "approver").stream().forEach(a -> {
                    activityService.doneActivity(entityId, "OFFER_APPROVE", UUID.fromString(a));
                });
                break;
            case "reject":
                templateParams.clear();
                updateOfferStatus(offer, OfferStatus.DRAFT);
                templateParams.put("offer", offer);
//                        templateParams.put("recruiterName", offer.getJobRequest().getRequisition().getRecruiterPersonGroup().getList().get(0) != null
//                                ? offer.getJobRequest().getRequisition().getRecruiterPersonGroup().getList().get(0).getFullName() : "");

                        *//*templateParams.put("positionName",
                                offer.getJobRequest().getRequisition().getPositionGroup() == null
                                        ? offer.getJobRequest().getRequisition().getJobGroup().getJob().getJobName()
                                        : offer.getJobRequest().getRequisition().getPositionGroup().getPosition().getPositionName());*//*
                templateParams.put("requisitionCode", offer.getJobRequest().getRequisition().getCode());
                SendingNotification sendingNotification = sendNotificationWithOffer(offer, "offer.hrManager.reject.notification",
                        getUserByKey("personGroup.id",
                                offer.getJobRequest().getRequisition()
                                        .getRecruiterPersonGroup().getId()).getId().toString(),
                        templateParams);
                activityService.createActivity(
                        employeeService.getUserExtByPersonGroupId(offer.getJobRequest().getRequisition().getRecruiterPersonGroup().getId()),
                        employeeService.getSystemUser(),
                        commonService.getEntity(ActivityType.class, "NOTIFICATION"),
                        StatusEnum.active,
                        "description",
                        null,
                        new Date(),
                        null,
                        null,
                        null,
                        "offer.hrManager.reject.notification",
                        templateParams);
                activityService.doneActivity(entityId, "OFFER_APPROVE");
                break;
            case "approveBusinessPartner":
                approveOffer(offer, templateParams, offerHistory);
                break;
            default:
                break;
        }
    }*/

    private void approveOffer(Offer offer, Map<String, Object> templateParams, OfferHistory offerHistory) {
        updateOfferStatus(offer, OfferStatus.APPROVED);
        offerHistory.setChangedBy(AppBeans.get(UserSessionSource.class).getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP));
        offerHistory.setOffer(offer);
        offerHistory.setStatus(OfferStatus.APPROVED);
        offerHistory.setStatusChangeDate(new Date());
        dataManager.commit(offerHistory);
        templateParams.clear();
        templateParams.put("offer", offer);
        templateParams.put("positionName",
                offer.getJobRequest().getRequisition().getPositionGroup() == null
                        ? offer.getJobRequest().getRequisition().getJobGroup().getJob().getJobName()
                        : offer.getJobRequest().getRequisition().getPositionGroup().getPosition().getPositionName());
        templateParams.put("expireDate", offer.getExpireDate() == null ?
                "" :
                new SimpleDateFormat("dd.MM.yyyy").format(offer.getExpireDate()));
        templateParams.put("requisitionCode", offer.getJobRequest().getRequisition().getCode());
        templateParams.put("personName", offer.getJobRequest().getRequisition().getRecruiterPersonGroup().getList().get(0).getFullName());

        EmailAttachment emailAttachment;
        FileLoader fileLoader = AppBeans.get(FileLoader.class);
        try {
            emailAttachment = new EmailAttachment(IOUtils.toByteArray(fileLoader.openStream(offer.getFile())),
                    offer.getFile().getName());
        } catch (FileStorageException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SendingNotification sendingNotification = sendNotification("offer.hrManager.approve.notification",
                getUserByKey("personGroup.id",
                        offer.getJobRequest().getRequisition()
                                .getRecruiterPersonGroup().getId()).getId().toString(),
                templateParams, emailAttachment);
        activityService.createActivity(
                employeeService.getUserExtByPersonGroupId(offer.getJobRequest().getRequisition().getRecruiterPersonGroup().getId()),
                employeeService.getSystemUser(),
                commonService.getEntity(ActivityType.class, "NOTIFICATION"),
                StatusEnum.active,
                "description",
                null,
                new Date(),
                null,
                null,
                null,
                "offer.hrManager.approve.notification",
                templateParams);

        sendingNotification = sendNotification("offer.hrManager.approve.notification",
                getUserByKey("personGroup.id",
                        offer.getJobRequest().getRequisition()
                                .getManagerPersonGroup().getId()).getId().toString(),
                templateParams, emailAttachment);
        activityService.createActivity(
                employeeService.getUserExtByPersonGroupId(offer.getJobRequest().getRequisition().getManagerPersonGroup().getId()),
                employeeService.getSystemUser(),
                commonService.getEntity(ActivityType.class, "NOTIFICATION"),
                StatusEnum.active,
                "description",
                null,
                new Date(),
                null,
                null,
                null,
                "offer.hrManager.approve.notification",
                templateParams);

        if (getUserByKey("personGroup.id",
                offer.getJobRequest().getCandidatePersonGroup().getId()) != null) {

            if (offer.getProposedStartDate() != null) {
                templateParams.put("startDate", new SimpleDateFormat("dd.MM.yyyy").format(offer.getProposedStartDate()));
            } else {
                templateParams.put("startDate", "");
            }

            if (offer.getExpireDate() != null) {
                templateParams.put("endDate", new SimpleDateFormat("dd.MM.yyyy").format(offer.getExpireDate()));
            } else {
                templateParams.put("endDate", "");
            }

            PersonGroupExt candidatePersonGroup = offer.getJobRequest().getCandidatePersonGroup();

            String personNameEn = candidatePersonGroup.getPersonFirstLastNameLatin();
            String personNameRu = candidatePersonGroup.getFullName();

            Job job = offer.getJobRequest().getRequisition().getJobGroup().getJob();

            templateParams.put("personFullNameRu", personNameRu);
            templateParams.put("personFullNameKz", personNameRu);
            templateParams.put("personFullNameEn", personNameEn);

            templateParams.put("positionNameRu", job.getJobNameLang1());
            templateParams.put("positionNameKz", job.getJobNameLang2());
            templateParams.put("positionNameEn", job.getJobNameLang3());

            Requisition requisition = offer.getJobRequest().getRequisition();
            templateParams.put("requisitionNameRu", requisition.getNameForSiteLang1());
            templateParams.put("requisitionNameKz", requisition.getNameForSiteLang2());
            templateParams.put("requisitionNameEn", requisition.getNameForSiteLang3());

            sendNotification("offer.candidate.approve.notification",
                    String.valueOf(getUserByKey("personGroup.id",
                            offer.getJobRequest().getCandidatePersonGroup().getId()).getId()),
                    templateParams, emailAttachment);
            activityService.createActivity(
                    employeeService.getUserExtByPersonGroupId(offer.getJobRequest().getCandidatePersonGroup().getId()),
                    employeeService.getSystemUser(),
                    commonService.getEntity(ActivityType.class, "OFFER_ACCEPT"),
                    StatusEnum.active,
                    "description",
                    null,
                    new Date(),
                    null,
                    null,
                    offer.getId(),
                    "offer.candidate.approve.notification",
                    templateParams);
        }
        if (offer.getNeedBuisnessPartnerApprove()) {
            sendNotification("offer.hrManager.approve.notification",
                    getUserByKey("personGroup.id",
                            offer.getJobRequest().getRequisition().getManagerPersonGroup().getId()).getId().toString(),
                    templateParams, emailAttachment);
            activityService.createActivity(
                    employeeService.getUserExtByPersonGroupId(offer.getJobRequest().getRequisition().getManagerPersonGroup().getId()),
                    employeeService.getSystemUser(),
                    commonService.getEntity(ActivityType.class, "NOTIFICATION"),
                    StatusEnum.active,
                    "description",
                    null,
                    new Date(),
                    null,
                    null,
                    null,
                    "offer.hrManager.approve.notification",
                    templateParams);
        }

        Map<String, Object> map = new HashMap<>();
        List<OrganizationHrUser> hrUsers = getRmPersonGroupId(offer.getJobRequest().getRequisition().getOrganizationGroup().getId());
        hrUsers.forEach(organizationHrUser -> {
            try {
                PersonGroupExt personGroup = employeeService.getPersonGroupByUserId(organizationHrUser.getUser().getId());
                Case personNameEn = getCasePersonName(offer.getJobRequest().getRequisition().getManagerPersonGroup().getId(), "en", "Nominative");
                Case personNameKz = getCasePersonName(offer.getJobRequest().getRequisition().getManagerPersonGroup().getId(), "kz", "Атау септік");
                Case personNameRu = getCasePersonName(offer.getJobRequest().getRequisition().getManagerPersonGroup().getId(), "ru", "Именительный");
                if (personNameEn != null) {
                    map.put("personFullNameEn", personNameEn.getLongName() == null ? personGroup.getFullName() : personNameEn.getLongName());
                } else {
                    map.put("personFullNameEn", personGroup == null ? "" : personGroup.getFullName());
                }
                if (personNameKz != null) {
                    map.put("personFullNameKz", personNameKz.getLongName() == null ? personGroup.getFullName() : personNameKz.getLongName());
                } else {
                    map.put("personFullNameKz", personGroup == null ? "" : personGroup.getFullName());
                }
                if (personNameRu != null) {
                    map.put("personFullNameRu", personNameRu.getLongName() == null ? personGroup.getFullName() : personNameRu.getLongName());
                } else {
                    map.put("personFullNameRu", personGroup == null ? "" : personGroup.getFullName());
                }
                sendNotification("offer.RecrutingManager.approve.notification",
                        organizationHrUser.getUser().getId().toString(),
                        map,
                        emailAttachment);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        });
    }

    private void updateOfferStatus(Offer offer, OfferStatus offerStatus) {
        try (Transaction tx = persistence.getTransaction()) {
            if (offer != null) {
                offer.setStatus(offerStatus);
            }
            tx.commit();
        }
    }


    private void sendEmail(String userId, String caption, String template, Map<String, Serializable> templateParams) {
        UserExt u = getUserByKey("id", UUID.fromString(userId));

        if (u != null && u.getEmail() != null) {
            EmailInfo emailInfo = new EmailInfo(
                    u.getEmail(), // recipients
                    caption, // subject
                    null, // the "from" address will be taken from the "cuba.email.fromAddress" app property
                    template, // body template
                    templateParams // template parameters
            );

            emailService.sendEmailAsync(emailInfo);
        }

    }

    /*public Boolean checkFlow(UUID entityId, UUID bpmProcInstanceId, String condition) {
        switch (condition) {
            case "transferRequestApproval_hasManager":
                EntityManager em = persistence.getEntityManager();
                ProcInstance procInstance = em.find(ProcInstance.class, bpmProcInstanceId);
                return procInstance != null && procInstance.getProcActors() != null && procInstance.getProcActors().stream().anyMatch(pa -> "newManagerUser".equals(pa.getProcRole().getCode()));
            default:
                return false;
        }
    }*/

    private void updateApprovalStatus(Entity entity, String statusCode) {
        try (Transaction tx = persistence.getTransaction()) {
            if (entity != null && entity instanceof HasApprovalStatus) {
                ((HasApprovalStatus) entity).setStatus(commonService.getEntity(DicApprovalStatus.class, statusCode));
            }
            tx.commit();
        }
    }

    private void transferEmployee(TransferRequest transferRequest) {
        AssignmentGroupExt assignmentGroup = transferRequest.getAssignmentGroup();

        Optional<AssignmentExt> assignmentOptional = assignmentGroup.getList().stream().filter(a -> !transferRequest.getTransferDate().before(a.getStartDate())
                && !transferRequest.getTransferDate().after(a.getEndDate())).findFirst();

        if (assignmentOptional.isPresent()) {
            List<Entity> commitInstances = new ArrayList<>();
            PositionExt position = null;
            Optional<PositionExt> positionOptional = transferRequest.getNewPositionGroup().getList().stream().filter(a -> !transferRequest.getTransferDate().before(a.getStartDate())
                    && !transferRequest.getTransferDate().after(a.getEndDate())).findFirst();

            if (positionOptional.isPresent()) {
                position = persistence.getEntityManager().find(PositionExt.class, positionOptional.get().getId(), "position.edit");
            }

            AssignmentExt assignment = persistence.getEntityManager().find(AssignmentExt.class, assignmentOptional.get().getId(), "assignment.full");
            if (assignment != null) {

                assignment.setStartDate(transferRequest.getTransferDate());
                assignment.setPositionGroup(transferRequest.getNewPositionGroup());
                assignment.setOrganizationGroup(transferRequest.getNewOrganizationGroup());
                if (position != null) {
                    assignment.setGradeGroup(position.getGradeGroup());
                    assignment.setJobGroup(position.getJobGroup());
                    assignment.setLocation(position.getLocation());
                }
                assignment.setWriteHistory(true);
                commitInstances.add(assignment);

                dataManager.commit(new CommitContext(commitInstances));
            }
        }
    }

    private UserExt findManagerUserByPosition(UUID positionGroupId) {
        UserExt u = null;
        EntityManager em = persistence.getEntityManager();
        TypedQuery<UserExt> tq = em.createQuery("select e " +
                " from tsadv$UserExt e, base$AssignmentExt a, tsadv$PositionStructure ps, tsadv$PositionStructure psm " +
                " where ps.positionGroup.id = :positionGroupId" +
                " and ps.positionGroupPath like concat('%', concat(psm.positionGroup.id, '%')) " +
                " and :systemDate between ps.startDate and ps.endDate " +
                " and :systemDate between ps.posStartDate and ps.posEndDate " +
                " and :systemDate between psm.startDate and psm.endDate " +
                " and :systemDate between psm.posStartDate and psm.posEndDate " +
                " and e.personGroup.id = a.personGroup.id " +
                " and :systemDate between a.startDate and a.endDate " +
                " and a.positionGroup.id = psm.positionGroup.id " +
                " and a.positionGroup.id <> :positionGroupId " +
                " order by psm.lvl desc", UserExt.class)
                .setParameter("positionGroupId", positionGroupId)
                .setParameter("systemDate", CommonUtils.getSystemDate());

        for (UserExt ue : tq.getResultList()) {
            u = ue;
            break;
        }
        return u;
    }

    private void updateTransferRequestStatus(UUID entityId, String status) {
        try (Transaction tx = persistence.getTransaction()) {
            TransferRequest entity = persistence.getEntityManager().find(TransferRequest.class, entityId, "transferRequest.edit");
            if (entity != null) {
                entity.setRequestStatus((DicRequestStatus) getDictionaryValueByCode("tsadv$DicRequestStatus", status));
            }
            tx.commit();
        }
    }

    private UserExt getUserByKey(String keyName, Object keyValue) {
        UserExt u = commonService.getEntity(UserExt.class, String.format("select e from tsadv$UserExt e " +
                "where e.%s = :keyValue", keyName), Collections.singletonMap("keyValue", keyValue), "user.edit");
        return u;
    }

    private AbstractDictionary getDictionaryValueByCode(String entity, String code) {
        AbstractDictionary status = null;
        EntityManager em = persistence.getEntityManager();
        TypedQuery<AbstractDictionary> tq = em.createQuery(String.format("select e from %s e where e.code = :code", entity), AbstractDictionary.class);
        tq.setParameter("code", code);
        status = tq.getSingleResult();

        return status;
    }

    private void updateOrderStatus(UUID entityId, String status) {
        try (Transaction tx = persistence.getTransaction()) {
            Order entity = persistence.getEntityManager().find(Order.class, entityId, "order-view");
            if (entity != null) {
                entity.setOrderStatus((DicOrderStatus) getDictionaryValueByCode("tsadv$DicOrderStatus", status));
            }
            tx.commit();
        }
    }

    private void updateDismissalStatus(UUID entityId, String status) {
        try (Transaction tx = persistence.getTransaction()) {
            Dismissal entity = persistence.getEntityManager().find(Dismissal.class, entityId, "dismissal.view");
            if (entity != null) {
                entity.setStatus((DicDismissalStatus) getDictionaryValueByCode("tsadv$DicDismissalStatus", status));
            }
            tx.commit();
        }
    }

    private void sendSms(String userId, String template, Map<String, Serializable> templateParams) {
        UserExt u = getUserByKey("id", UUID.fromString(userId));
        if (u != null && u.getMobilePhone() != null) {
            try {
                smsService.sendSmsAsync(u.getMobilePhone(),
                        template,
                        templateParams
                );
            } catch (SmsException e) {
            }
        }
    }

    private SendingNotification sendNotification(String notificationCode, String userId, Map<String, Object> templateParams, EmailAttachment... emailAttachments) {
        UserExt user = getUserByKey("id", UUID.fromString(userId));
        if (templateParams != null)
            templateParams.put("user", user);
        return notificationSender.sendParametrizedNotification(notificationCode, user, templateParams, emailAttachments);
    }

    private SendingNotification sendNotificationWithOffer(Offer offer, String notificationCode, String userId, Map<String, Object> templateParams, EmailAttachment... emailAttachments) {
        SendingNotification sendingNotification = null;
        if (getUserByKey("personGroup.id",
                offer.getJobRequest().getRequisition().getRecruiterPersonGroup().getId()) != null) {
            try {
                List<UserExt> userList = null;
                List<UserExt> candidateList = null;
                userList = commonService.getEntities(UserExt.class,
                        "select e " +
                                "    from tsadvUserExt e, tsadv$Requisition r " +
                                "   where e.personGroup.id = r.recruiterPersonGroup.id " +
                                "     and r.id = :requisitionId " +
                                "",
                        Collections.singletonMap("requisitionId", offer.getJobRequest().getRequisition().getId()),
                        "user.browse");

                candidateList = commonService.getEntities(UserExt.class,
                        "select e " +
                                "    from tsadv$UserExt e, tsadv$Requisition r, tsadv$JobRequest j" +
                                "    where e.personGroup.id = j.candidatePersonGroup.id " +
                                " and j.requisition.id = r.id " +
                                "     and r.id = :requisitionId ",
                        Collections.singletonMap("requisitionId", offer.getJobRequest().getRequisition().getId()),
                        "user.browse");
                Case personNameEn = getCasePersonName(offer.getJobRequest().getRequisition().getRecruiterPersonGroup().getId(), "en", "Nominative");
                Case personNameKz = getCasePersonName(offer.getJobRequest().getRequisition().getRecruiterPersonGroup().getId(), "kz", "Атау септік");
                Case personNameRu = getCasePersonName(offer.getJobRequest().getRequisition().getRecruiterPersonGroup().getId(), "ru", "Именительный");

                Case candidateNameEn = getCasePersonName(offer.getJobRequest().getCandidatePersonGroup().getId(), "en", "Nominative");
                Case candidateNameKz = getCasePersonName(offer.getJobRequest().getCandidatePersonGroup().getId(), "kz", "Атау септік");
                Case candidateNameRu = getCasePersonName(offer.getJobRequest().getCandidatePersonGroup().getId(), "ru", "Именительный");

                Case jobNameEn = getCaseJobName(offer.getJobRequest().getRequisition().getJobGroup().getId(), "en", "Nominative");
                Case jobNameKz = getCaseJobName(offer.getJobRequest().getRequisition().getJobGroup().getId(), "kz", "Атау септік");
                Case jobNameRu = getCaseJobName(offer.getJobRequest().getRequisition().getJobGroup().getId(), "ru", "Именительный");

                PersonGroupExt personGroup = employeeService.getPersonGroupByUserId(userList.get(0).getId());
                templateParams.put("personFullNameEn", getLongValueOrFullName(personNameEn, personGroup));
                templateParams.put("personFullNameKz", getLongValueOrFullName(personNameKz, personGroup));
                templateParams.put("personFullNameRu", getLongValueOrFullName(personNameRu, personGroup));

                PersonGroupExt candidatePersonGroup = offer.getJobRequest().getCandidatePersonGroup();
                templateParams.put("candidateFullNameEn", getLongValueOrFullName(candidateNameEn, candidatePersonGroup));
                templateParams.put("candidateFullNameKz", getLongValueOrFullName(candidateNameKz, candidatePersonGroup));
                templateParams.put("candidateFullNameRu", getLongValueOrFullName(candidateNameRu, candidatePersonGroup));

                if (jobNameEn != null) {
                    templateParams.put("positionNameEn", jobNameEn.getLongName() == null ? offer.getJobRequest().getRequisition().getPositionGroup().getPosition().getPositionName() : jobNameEn.getLongName());
                } else {
                    templateParams.put("positionNameEn", offer.getJobRequest().getRequisition().getPositionGroup() == null ? "" : offer.getJobRequest().getRequisition().getPositionGroup().getPosition().getPositionName());
                }
                if (jobNameKz != null) {
                    templateParams.put("positionNameKz", jobNameKz.getLongName() == null ? offer.getJobRequest().getRequisition().getPositionGroup().getPosition().getPositionName() : jobNameKz.getLongName());
                } else {
                    templateParams.put("positionNameKz", offer.getJobRequest().getRequisition().getPositionGroup() == null ? "" : offer.getJobRequest().getRequisition().getPositionGroup().getPosition().getPositionName());
                }
                if (jobNameRu != null) {
                    templateParams.put("positionNameRu", jobNameRu.getLongName() == null ? offer.getJobRequest().getRequisition().getPositionGroup().getPosition().getPositionName() : jobNameRu.getLongName());
                } else {
                    templateParams.put("positionNameRu", offer.getJobRequest().getRequisition().getPositionGroup() == null ? "" : offer.getJobRequest().getRequisition().getPositionGroup().getPosition().getPositionName());
                }

                UserExt user = getUserByKey("id", UUID.fromString(userId));
                if (templateParams != null)
                    templateParams.put("user", user);
                sendingNotification = notificationSender.sendParametrizedNotification(notificationCode, user, templateParams, emailAttachments);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return sendingNotification;
    }

    private void dismissEmployee(List<OrdAssignment> ordAssignments) {
        for (OrdAssignment ordAssignment : ordAssignments) {

            if (ordAssignment.getDismissal() != null && !ordAssignment.getDismissal().isEmpty()) {
                Dismissal dismissal = ordAssignment.getDismissal().get(0);
                updateDismissalStatus(dismissal.getId(), "APPROVED");
            }
        }

    }

    protected Case getCasePersonName(UUID personGroupId, String language, String caseName) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("personGroupId", personGroupId);
        paramMap.put("systemDate", CommonUtils.getSystemDate());
        paramMap.put("language", language);
        paramMap.put("case", caseName);
        Case personName = commonService.getEntity(Case.class,
                "select c from tsadv$Case c " +
                        "join base$PersonExt t on t.group.id = c.personGroup.id " +
                        "join tsadv$CaseType ct on ct.id = c.caseType.id " +
                        "and ct.language = :language " +
                        "and ct.name = :case " +
                        "where :systemDate between t.startDate and t.endDate " +
                        "and c.deleteTs is null " +
                        "and t.group.id = :personGroupId",
                paramMap,
                "caseJobName");
        return personName;
    }

    protected Case getCaseJobName(UUID jobGroupId, String language, String caseName) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("jobGroupId", jobGroupId);
        paramMap.put("systemDate", CommonUtils.getSystemDate());
        paramMap.put("language", language);
        paramMap.put("case", caseName);

        Case jobName = commonService.getEntity(Case.class,
                "select c from tsadv$Case c " +
                        "join tsadv$Job j on j.group.id = c.jobGroup.id " +
                        "join tsadv$CaseType ct on ct.id = c.caseType.id " +
                        "and ct.language = :language " +
                        "and ct.name = :case " +
                        "where :systemDate between j.startDate and j.endDate " +
                        "and c.jobGroup.id = :jobGroupId " +
                        "and c.deleteTs is null",
                paramMap,
                "caseJobName");
        return jobName;
    }

    private String getLongValueOrFullName(Case personNameEn, PersonGroupExt personGroup) {
        String personFullNameEn = "";
        if (personNameEn != null) {
            if (personNameEn.getLongName() == null) {
                if (personGroup != null) {
                    personFullNameEn = personGroup.getPerson().getFullName();
                }
            } else {
                personFullNameEn = personNameEn.getLongName();
            }
        } else {
            if (personGroup != null) {
                personFullNameEn = personGroup.getPerson().getFullName();
            }
        }
        return personFullNameEn;
    }

    protected List<OrganizationHrUser> getRmPersonGroupId(UUID organizationGroupId) {
        List<OrganizationHrUser> list = employeeService.getHrUsers(organizationGroupId, "RECRUITING_MANAGER");
        return list;
    }

    protected UUID getDirectorGroupIdByPositionGroupId(UUID positionGroupId) {

        String queryString = "select pp.group_id, count(distinct ss.id) , string_agg(distinct ss.person_group_id::text,'') " +
                "from ( select el.position_group_id , hp.position_group_id as parent_position_group_id , " +
                "struct.position_group_path " +
                "from base_hierarchy_element el " +
                "join base_hierarchy_element hp " +
                "on hp.id = el.parent_id " +
                "and hp.delete_ts is null " +
                "and current_date between hp.start_date and hp.end_date " +
                "join tsadv_position_structure struct " +
                "on struct.position_group_id = el.position_group_id " +
                "and struct.parent_position_group_id = hp.position_group_id " +
                "and struct.delete_ts is null " +
                "where el.delete_ts is null " +
                "and current_date between el.start_date and el.end_date " +
                "and el.position_group_id = '" + positionGroupId + "' " +
                "order by struct.lvl desc) t " +
                "join base_position pp " +
                "on t.position_group_path like '%' || pp.group_id || '%' " +
                "and pp.group_id != t.parent_position_group_id " +
                "and current_date between pp.start_date and pp.end_date " +
                "and pp.delete_ts is null " +
                "and pp.manager_flag is true " +
                "join base_assignment ss " +
                "on ss.position_group_id = pp.group_id " +
                "and ss.delete_ts is null " +
                "and ss.primary_flag is true " +
                "and current_date between ss.start_date and ss.end_date " +
                "group by pp.group_id,t.position_group_id " +
                "limit 1 ";

        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(queryString);

            List<Object[]> rows = query.getResultList();

            for (Object[] row : rows) {
                if ((Long) row[1] == 1L)
                    return UUID.fromString((String) row[2]);
            }

        }
        return null;
    }

    /*public void sendNotificationForApproval(UUID entityId, UUID bpmProcInstanceId, String role) {
        EntityManager em = persistence.getEntityManager();

        AbsenceRequest absenceRequest = em.find(AbsenceRequest.class, entityId, "absenceRequest.view");
        ProcInstance procInstance = em.find(ProcInstance.class, bpmProcInstanceId);
        UserExt employee = getUserByKey("personGroup.id", absenceRequest.getAssignmentGroup().getAssignment().getPersonGroup());
        Map<String, Object> templateParams = new HashMap<>();

        templateParams.clear();
        templateParams.put("absenceRequest", absenceRequest);

        templateParams.put("personFullName", employee.getFullName());
        templateParams.put("dateFrom", absenceRequest.getDateFrom());
        templateParams.put("dateFrom", absenceRequest.getDateTo());

        processRuntimeManager.getTaskAssigneeList(bpmProcInstanceId, role).stream().forEach(a -> {
            sendNotification("absenceRequset.approver.notification", a, templateParams);
            activityService.doneActivity(entityId, "ABSENCE_REQUEST_APPROVE");
            activityService.createActivity(
                    getUserByKey("id", UUID.fromString(a)),
                    employeeService.getSystemUser(),
                    commonService.getEntity(ActivityType.class, "ABSENCE_REQUEST_APPROVE"),
                    StatusEnum.active,
                    "description",
                    null,
                    new Date(),
                    null,
                    null,
                    entityId,
                    "absenceRequset.approver.notification",
                    templateParams);
        });

    }*/

}