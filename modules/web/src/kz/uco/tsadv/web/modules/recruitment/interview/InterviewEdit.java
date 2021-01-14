package kz.uco.tsadv.web.modules.recruitment.interview;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParams;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.entity.core.notification.SendingNotification;
import kz.uco.base.service.NotificationService;
import kz.uco.base.service.common.CommonService;
import kz.uco.base.web.abstraction.AbstractDictionaryDatasource;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.global.common.EnableDraftStatusInterview;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.administration.enums.HiringStepType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.Case;
import kz.uco.tsadv.modules.personal.model.Job;
import kz.uco.tsadv.modules.personal.model.OrganizationHrUser;
import kz.uco.tsadv.modules.recruitment.dictionary.DicJobRequestReason;
import kz.uco.tsadv.modules.recruitment.enums.InterviewStatus;
import kz.uco.tsadv.modules.recruitment.enums.JobRequestStatus;
import kz.uco.tsadv.modules.recruitment.enums.RcAnswerType;
import kz.uco.tsadv.modules.recruitment.model.*;
import kz.uco.tsadv.service.DatesService;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.service.TemplateService;
import kz.uco.tsadv.web.modules.personal.common.Utils;
import kz.uco.uactivity.entity.ActivityType;
import kz.uco.uactivity.entity.StatusEnum;
import kz.uco.uactivity.service.ActivityService;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.toIntExact;

public class InterviewEdit<T extends Interview> extends AbstractEditor<T> {
    private static final Logger log = LoggerFactory.getLogger(InterviewEdit.class);

    @Inject
    protected Metadata metadata;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected Messages messages;
    @Inject
    protected CommonService commonService;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected NotificationService notificationService;
    @Inject
    protected TemplateService templateService;
    @Inject
    protected ActivityService activityService;
    @Inject
    protected CollectionDatasource<InterviewQuestionnaire, UUID> questionnairesDs;
    @Inject
    protected AbstractDictionaryDatasource interviewReasonsDs;
    @Inject
    protected Datasource<Interview> interviewDs;
    @Inject
    protected FieldGroup fieldGroup1;
    @Inject
    protected FieldGroup fieldGroup2;
    @Named("fieldGroup2.interviewReason")
    protected PickerField interviewReasonField;
    @Inject
    protected Table<InterviewQuestionnaire> questionnairesTable;
    @Inject
    protected DatesService datesService;
    protected boolean isApprovalNotification = false;
    protected boolean readOnly = false;
    protected String candidateNotificationCode = "";
    protected SimpleDateFormat timeFormat;

    protected String candidateFailInterviewCode = "";

    @Inject
    protected EnableDraftStatusInterview enableDraftStatusInterview;
    @Named("fieldGroup1.interviewStatus")
    protected LookupField interviewStatusField;
    @Named("questionnairesTable.edit")
    protected EditAction questionnairesTableEdit;
    protected InterviewStatus oldInterviewStatus = null;


    @Override
    protected void initNewItem(T item) {
        super.initNewItem(item);
        item.setSendInvitationToCandidate(false);
        item.setInterviewStatus(InterviewStatus.DRAFT);
        item.setIsScheduled(false);
        item.setInterviewDate(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH));
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        timeFormat = new SimpleDateFormat("HH:mm");
        if (params.containsKey("readOnly")) {
            readOnly = true;
        }

        if (params.get("fromInterviewBrowsePlanned") != null) {
            fieldGroup1.getComponentNN("jobRequest").setVisible(false);
            fieldGroup2.setVisible(false);
        }

        interviewReasonField.addClearAction();
        interviewReasonField.removeAction(PickerField.OpenAction.NAME);
        interviewReasonField.addLookupAction().setLookupScreenOpenType(WindowManager.OpenType.DIALOG);

        interviewStatusFilter();

        questionnairesTableEdit.setAfterCommitHandler(entity -> {
            calculateTotalScore((InterviewQuestionnaire) entity);
            questionnairesDs.modifyItem((InterviewQuestionnaire) entity);
            questionnairesTable.repaint();
        });
    }


    @Override
    protected void postInit() {
        super.postInit();

        calcMainInterviewer(getItem().getMainInterviewerPersonGroup() == null);

        JobRequest jobRequest = getItem().getJobRequest();

        Map<String, Object> lookupParams = new HashMap<>();
        lookupParams.put("requisitionId", jobRequest != null ? jobRequest.getRequisition().getId() : null);
        lookupParams.put("jobRequestId", jobRequest != null ? jobRequest.getId() : null);

        Utils.customizeLookup(fieldGroup1.getField("requisitionHiringStep").getComponent(),
                null,
                WindowManager.OpenType.DIALOG,
                lookupParams);

        interviewDs.addItemPropertyChangeListener(e -> {
            interviewDsItemPropertyChangeListener(e);
        });


        oldInterviewStatus = getItem().getInterviewStatus();

        if (PersistenceHelper.isNew(getItem())) {
            fillDefaultQuestionnaires();
        }
    }

    protected void interviewDsItemPropertyChangeListener(Datasource.ItemPropertyChangeEvent<Interview> e) {
        if ("interviewStatus".equals(e.getProperty())) {
            if (e.getValue() != null && (InterviewStatus.COMPLETED.equals(e.getValue()) ||
                    InterviewStatus.DRAFT.equals(e.getValue()) ||
                    InterviewStatus.ON_APPROVAL.equals(e.getValue()))) {
                interviewReasonField.setEditable(false);
            } else {
                interviewReasonField.setEditable(true);
            }
            interviewDs.getItem().setInterviewReason(null);
        } else if ("timeFrom".equals(e.getProperty())) {
            if (e.getValue() != null) {
                if (interviewDs.getItem().getTimeTo() == null) {
                    Calendar c = Calendar.getInstance();
                    c.setTime((Date) e.getValue());
                    c.add(Calendar.HOUR, 1);
                    interviewDs.getItem().setTimeTo(c.getTime());
                }
            }
        }


    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        if (errors.isEmpty()) {
            MetaClass interviewMetaClass = metadata.getClass("tsadv$Interview");
            switch (getItem().getInterviewStatus()) {
                case COMPLETED:
                    boolean unanswered = false;
                    if (questionnairesDs.getState().equals(Datasource.State.INVALID)) questionnairesDs.refresh();
                    questionnaireLoop:
                    for (InterviewQuestionnaire questionnaire : questionnairesDs.getItems()) {
                        for (InterviewQuestion question : questionnaire.getQuestions()) {
                            switch (question.getQuestion().getAnswerType()) {
                                case DATE:
                                    for (InterviewAnswer answer : question.getAnswers())
                                        if (answer.getDateAnswer() == null) {
                                            unanswered = true;
                                            break questionnaireLoop;
                                        }
                                    break;
                                case TEXT:
                                    for (InterviewAnswer answer : question.getAnswers())
                                        if (answer.getTextAnswer() == null) {
                                            unanswered = true;
                                            break questionnaireLoop;
                                        }
                                    break;
                                case NUMBER:
                                    for (InterviewAnswer answer : question.getAnswers())
                                        if (answer.getNumberAnswer() == null) {
                                            unanswered = true;
                                            break questionnaireLoop;
                                        }
                                    break;
                                case SINGLE:
                                    if (question.getAnswers() != null &&
                                            question.getAnswers().stream().noneMatch(answer -> answer != null
                                                    && answer.getBooleanAnswer() != null
                                                    && answer.getBooleanAnswer())) {
                                        unanswered = true;
                                        break questionnaireLoop;
                                    }
                                    break;
                                case MULTI:
                                    if (question.getAnswers() != null &&
                                            question.getAnswers().stream().noneMatch(answer -> answer != null
                                                    && answer.getBooleanAnswer() != null
                                                    && answer.getBooleanAnswer())) {
                                        unanswered = true;
                                        break questionnaireLoop;
                                    }
                                    break;
                            }
                        }
                    }

                    if (unanswered)
                        errors.add(getMessage("Interview.questionnaireAnswers.validatorMsg"));
                    break;
                case ON_APPROVAL:
                case PLANNED:
                    if (getItem().getTimeFrom() == null)
                        errors.add(messages.formatMainMessage("validation.required.defaultMsg",
                                messages.getTools().getPropertyCaption(interviewMetaClass, "timeFrom")));
                    if (getItem().getTimeTo() == null)
                        errors.add(messages.formatMainMessage("validation.required.defaultMsg",
                                messages.getTools().getPropertyCaption(interviewMetaClass, "timeTo")));
                    if (getItem().getInterviewDate() == null)
                        errors.add(messages.formatMainMessage("validation.required.defaultMsg",
                                messages.getTools().getPropertyCaption(interviewMetaClass, "interviewDate")));
                    if (getItem().getPlace() == null)
                        errors.add(messages.formatMainMessage("validation.required.defaultMsg",
                                messages.getTools().getPropertyCaption(interviewMetaClass, "place")));
                    break;
                case CANCELLED:
                case FAILED:
                    if (getItem().getInterviewReason() == null)
                        errors.add(messages.formatMainMessage("validation.required.defaultMsg",
                                messages.getTools().getPropertyCaption(interviewMetaClass, "interviewReason")));

                    if (getItem().getInterviewReason() != null
                            && ("OTHER".equals(getItem().getInterviewReason().getCode()) || "CANDIDATE".equals(getItem().getInterviewReason().getCode()))
                            && (getItem().getComment() == null || getItem().getComment().trim().length() == 0))
                        errors.add(messages.formatMainMessage("validation.required.defaultMsg",
                                messages.getTools().getPropertyCaption(interviewMetaClass, "comment")));

                    if (getItem().getInterviewReason() == null && getItem().getReason() == null)
                        errors.add(messages.formatMainMessage("Interview.approvalProcess.reason.required",
                                messages.getTools().getPropertyCaption(interviewMetaClass, "interviewReason"),
                                messages.getTools().getPropertyCaption(interviewMetaClass, "reason")));
                    UUID reqId = getItem().getJobRequest().getRequisition().getId();
                    Long order = commonService.getCount(RequisitionHiringStep.class, "select e from tsadv$RequisitionHiringStep e where e.requisition.id = :reqId", Collections.singletonMap("reqId", reqId));
                    if (getItem().getRequisitionHiringStep().getOrder() == toIntExact(order)) {
                        if (getItem().getJobRequest() != null) {
                            UserExt user = employeeService.getUserExtByPersonGroupId(getItem().getJobRequest().getCandidatePersonGroup() != null ?
                                    getItem().getJobRequest().getCandidatePersonGroup().getId() : null);
                            if (user != null && getItem().getJobRequest().getRequisition() != null) {
                                notificationService.sendParametrizedNotification("candidate.selection.process.completed",
                                        user,
                                        getParams(getItem()));
                            }
                        }
                    }
                    break;
            }
            super.postValidate(errors);
        }
    }

    protected Map<String, Object> getParams(Interview interview) {
        Map<String, Object> param = new HashMap<>();
        param.put("personFullName", interview.getJobRequest().getCandidatePersonGroup().getFullName());
        param.put("job", interview.getJobRequest().getRequisition().getJobGroup().getJob());
        return param;
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        updateJobRequestStatus();
        if (committed) {
            InterviewStatus newInterviewStatus = getItem().getInterviewStatus();
            if (newInterviewStatus != null && !newInterviewStatus.equals(oldInterviewStatus)) {
                processAction();
            }
            if (!close) {
                if (showSaveNotification) {
                    if (isApprovalNotification) {
                        frame.showNotification(messages.getMessage("kz.uco.tsadv.web", "aproveInterview"),
                                NotificationType.TRAY);
                        isApprovalNotification = false;
                    } else
                        frame.showNotification(
                                messages.getMessage(messages.getMainMessagePack(), "interviewEdit.candidateUser.saved.successfully"),
                                NotificationType.TRAY);
                }
                postInit();
            }
        }
        return true;
    }

    protected void fillDefaultQuestionnaires() {
        questionnairesDs.clear();
        Map<String, Object> qParams = new HashMap<>();
        qParams.put("hiringStepId", getItem().getRequisitionHiringStep().getHiringStep().getId());
        qParams.put("requisitionId", getItem().getJobRequest().getRequisition().getId());

        List<RcQuestionnaire> rcQuestionnaires = commonService.getEntities(
                RcQuestionnaire.class,
                "select e " +
                        "    from tsadv$RcQuestionnaire e " +
                        "   where e.id in (select hsq.questionnaire.id " +
                        "                    from tsadv$HiringStepQuestionnaire hsq, tsadv$RequisitionQuestionnaire rq " +
                        "                   where hsq.hiringStep.id = :hiringStepId " +
                        "                     and rq.requisition.id = :requisitionId " +
                        "                     and rq.questionnaire.id = hsq.questionnaire.id)",
                qParams,
                "rcQuestionnaire.view");

        for (RcQuestionnaire rcQuestionnaire : rcQuestionnaires) {
            addQuestionnaire(rcQuestionnaire);
        }
    }

    //отпарвить на согласование
    public void sendForApproval() {
        getItem().setReason(null);
        getItem().setInterviewReason(null);
        isApprovalNotification = true;
        Map<String, Object> params = new HashMap<>();
        UserExt candidateUser = employeeService.getUserExtByPersonGroupId(getItem().getJobRequest().getCandidatePersonGroup().getId());
        if (candidateUser == null) {
            showNotification(getMessage("InterviewEdit.candidateUser.notExists"));
        } else if (candidateUser.getEmail() == null && candidateUser.getMobilePhone() == null) {
            showNotification(getMessage("InterviewEdit.candidateUser.noEmailPhone"));
        } else {
            params.put("interview", getItem());
            params.put("timeFrom", getItem().getTimeFrom() != null ? timeFormat.format(getItem().getTimeFrom()) : null);
            params.put("timeTo", getItem().getTimeTo() != null ? timeFormat.format(getItem().getTimeTo()) : null);
            params.put("interviewDate", getItem().getInterviewDate() != null ? new SimpleDateFormat("dd.MM.yyyy").format(getItem().getInterviewDate()) : null); //TODO transfer simpleDateFromat of pattern to common
            long durationTime = (getItem().getTimeTo().getTime() - getItem().getTimeFrom().getTime()) / 1000 / 60;
            params.put("durationTime", durationTime);
            PersonGroupExt candidatePersonGroup = getItem().getJobRequest().getCandidatePersonGroup();
            String personNameEn = candidatePersonGroup.getPersonFirstLastNameLatin();
            String personNameRu = candidatePersonGroup.getFullName();
            Job job = getItem().getJobRequest().getRequisition().getJobGroup().getJob();

            params.put("personFullNameRu", personNameRu);
            params.put("personFullNameKz", personNameRu);
            params.put("personFullNameEn", personNameEn);

            params.put("positionNameRu", job.getJobNameLang1());
            params.put("positionNameKz", job.getJobNameLang2());
            params.put("positionNameEn", job.getJobNameLang3());
            params.put("user", candidateUser);

            RequisitionHiringStep requisitionHiringStep = interviewDs.getItem().getRequisitionHiringStep();
            if (requisitionHiringStep != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("hiringStepId", requisitionHiringStep.getHiringStep().getId());
                String query = "select e from tsadv$HiringStep e where e.id = :hiringStepId";
                HiringStep hiringStep = commonService.getEntity(HiringStep.class,
                        query,
                        map,
                        "hiringStep.view");
                if (hiringStep.getType() != null) {
                    if (HiringStepType.interview == hiringStep.getType()) {
                        candidateNotificationCode = "interview.candidate.approve.notification"; //Notification for INTERVIEW
                    } else {
                        candidateNotificationCode = "interview.candidate.approve.test.notification"; //Notification for TEST
                    }

                    notificationService.sendParametrizedNotification(candidateNotificationCode,
                            candidateUser, params);
                    activityService.createActivity(
                            candidateUser,
                            employeeService.getSystemUser(),
                            commonService.getEntity(ActivityType.class, "INTERVIEW_APPROVE"),
                            StatusEnum.active,
                            "description",
                            getItem().getTimeFrom(),
                            new Date(),
                            null,
                            null,
                            getItem().getId(),
                            candidateNotificationCode,
                            params);
                }
            }

        }

//
//            if (getItem().getJobRequest().getCandidatePersonGroup().getId().equals(userSession.getAttribute(StaticVariable.USER_PERSON_GROUP_ID))) {
//                initInterviewActions();
//            } else {
//                this.commit();
//            }
        //close(COMMIT_ACTION_ID);

        //PLEASE do not delete commented code
            /*Map<String, Object> templateParams = new HashMap<>();
            UserExt mainInterviewerUser = commonService.getEntity(UserExt.class,
                    "select u from sec$User u where u.personGroupId.id = :personGroupId",
                    Collections.singletonMap("personGroupId", getItem().getMainInterviewerPersonGroup().getId()),
                    "user.edit");
            templateParams.put("interview", getItem());
            templateParams.put("user", mainInterviewerUser);
            notificationService.sendParametrizedNotification("interview.mainInterviewer.notification", mainInterviewerUser, templateParams);

            if (getItem().getMainInterviewerPersonGroup().getId().equals(userSession.getAttribute(StaticVariable.USER_PERSON_GROUP_ID))) {
                initInterviewActions();
            } else
                close(COMMIT_ACTION_ID);*/


    }

    //Принять интервью
    public void planInterview() {
        Map<String, Object> params = new HashMap<>();
        UserExt userExt = null;
        if (getItem().getJobRequest().getRequisition().getRecruiterPersonGroup() != null) {
            userExt = employeeService.getUserExtByPersonGroupId(getItem().getJobRequest().getRequisition().getRecruiterPersonGroup().getId());
        }
        try {
            Case personNameEn = templateService.getCasePersonName(getItem().getJobRequest().getRequisition().getRecruiterPersonGroup().getId(), "en", "Nominative");
            Case personNameKz = templateService.getCasePersonName(getItem().getJobRequest().getRequisition().getRecruiterPersonGroup().getId(), "kz", "Атау септік");
            Case personNameRu = templateService.getCasePersonName(getItem().getJobRequest().getRequisition().getRecruiterPersonGroup().getId(), "ru", "Именительный");

            Case jobNameEn = templateService.getCaseJobName(getItem(), "en", "Nominative");
            Case jobNameKz = templateService.getCaseJobName(getItem(), "kz", "Атау септік");
            Case jobNameRu = templateService.getCaseJobName(getItem(), "ru", "Именительный");

            PersonGroupExt personGroup = getItem().getJobRequest().getRequisition().getRecruiterPersonGroup();
            params.put("personFullNameEn", templateService.getPersonFullName(personNameEn, personGroup));
            params.put("personFullNameKz", templateService.getPersonFullName(personNameKz, personGroup));
            params.put("personFullNameRu", templateService.getPersonFullName(personNameRu, personGroup));

            params.put("positionNameEn", templateService.getJobName(jobNameEn, getItem()));
            params.put("positionNameKz", templateService.getJobName(jobNameKz, getItem()));
            params.put("positionNameRu", templateService.getJobName(jobNameRu, getItem()));

            params.put("timeFrom", getItem().getTimeFrom() != null ? timeFormat.format(getItem().getTimeFrom()) : null);
            params.put("timeTo", getItem().getTimeTo() != null ? timeFormat.format(getItem().getTimeTo()) : null);
            params.put("interview", getItem());
            params.put("interviewDate", getItem().getInterviewDate() != null ? new SimpleDateFormat("dd.MM.yyyy").format(getItem().getInterviewDate()) : null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (userExt != null) {
            params.put("user", userExt);
            SendingNotification sendingNotification = notificationService
                    .sendParametrizedNotification("interview.planned.mainInterviewer.notification",
                            userExt, params);
            if (sendingNotification != null) {
                activityService.createActivity(
                        userExt,
                        employeeService.getSystemUser(),
                        commonService.getEntity(ActivityType.class, "NOTIFICATION"),
                        StatusEnum.active,
                        "descripiton",
                        null,
                        new Date(),
                        null,
                        null,
                        null,
                        "interview.planned.mainInterviewer.notification",
                        params);
            }
            activityService.doneActivity(getItem().getId(), "INTERVIEW_APPROVE",
                    employeeService.getUserExtByPersonGroupId(getItem().getJobRequest().getCandidatePersonGroup().getId()));
        }
    }

    public void cancelInterview() {
        Map<String, Object> params = new HashMap<>();
        UserExt candidateUser = employeeService.getUserExtByPersonGroupId(getItem().getJobRequest().getCandidatePersonGroup().getId());
        if (candidateUser == null) {
            showNotification(getMessage("InterviewEdit.candidateUser.notExists"));
        } else if (candidateUser.getEmail() == null && candidateUser.getMobilePhone() == null)
            showNotification(getMessage("InterviewEdit.candidateUser.noEmailPhone"));
        try {
            params.put("interview", getItem());
            params.put("timeFrom", getItem().getTimeFrom() != null ? timeFormat.format(getItem().getTimeFrom()) : null);
            params.put("timeTo", getItem().getTimeTo() != null ? timeFormat.format(getItem().getTimeTo()) : null);
            params.put("interviewDate", getItem().getInterviewDate() != null ? new SimpleDateFormat("dd.MM.yyyy").format(getItem().getInterviewDate()) : null);

            PersonGroupExt candidatePersonGroup = getItem().getJobRequest().getCandidatePersonGroup();

            String personNameEn = candidatePersonGroup.getPersonFirstLastNameLatin();
            String personNameRu = candidatePersonGroup.getFullName();

            Job job = getItem().getJobRequest().getRequisition().getJobGroup().getJob();

            params.put("personFullNameRu", personNameRu);
            params.put("personFullNameKz", personNameRu);
            params.put("personFullNameEn", personNameEn);

            params.put("positionNameRu", job.getJobNameLang1());
            params.put("positionNameKz", job.getJobNameLang2());
            params.put("positionNameEn", job.getJobNameLang3());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (candidateUser != null) {
            params.put("user", candidateUser);
            notificationService.sendParametrizedNotification("interview.candidate.cancel.notification",
                    candidateUser, params);
        }
//                sendNotification("interview.mainInterviewer.cancel.notification");
        params.clear();
        UserExt mainInterviewerUser = employeeService.getUserExtByPersonGroupId(getItem().getMainInterviewerPersonGroup().getId());
        params.put("interview", getItem());

        params.put("reason", getItem().getComment() != null ? getItem().getComment() : "");
        params.put("personName", interviewDs.getItem().getMainInterviewerPersonGroup() != null ?
                interviewDs.getItem().getMainInterviewerPersonGroup().getPerson().getFullName() : "");
        params.put("timeFrom", getItem().getTimeFrom() != null ? timeFormat.format(getItem().getTimeFrom()) : null);
        params.put("timeTo", getItem().getTimeTo() != null ? timeFormat.format(getItem().getTimeTo()) : null);
        params.put("interviewDate", getItem().getInterviewDate() != null ? new SimpleDateFormat("dd.MM.yyyy").format(getItem().getInterviewDate()) : null);

        if (mainInterviewerUser != null) {
            params.put("user", mainInterviewerUser);
            notificationService.sendParametrizedNotification("interview.mainInterviewer.cancel.notification",
                    mainInterviewerUser, params);
        }
    }

    protected void sendNotificationCompleteInterview() {
        try {
            UserExt recruiter = employeeService.getUserExtByPersonGroupId(
                    getItem().getJobRequest().getCandidatePersonGroup().getId(),
                    "user.browse");
            if (recruiter != null) {
                Map<String, Object> params = new HashMap<>();
                params.put("personName", getItem().getJobRequest().getCandidatePersonGroup().getPerson().getFullName() != null ?
                        getItem().getJobRequest().getCandidatePersonGroup().getPerson().getFullName() : "");
                if (getItem().getRequisitionHiringStep().getHiringStep().getStepName() != null) {
                    params.put("stepName", getItem().getRequisitionHiringStep().getHiringStep().getStepName());
                } else {
                    params.put("stepName", "");
                }
                notificationService.sendParametrizedNotification(
                        "interview.complete.deferred.notify",
                        recruiter,
                        params);
                activityService.createActivity(
                        recruiter,
                        employeeService.getSystemUser(),
                        commonService.getEntity(ActivityType.class, "NOTIFICATION"),
                        StatusEnum.active,
                        "descripiton",
                        null,
                        new Date(),
                        null,
                        null,
                        null,
                        "interview.complete.deferred.notify",
                        params);
                activityService.doneActivity(getItem().getId(), "INTERVIEW_APPROVE",
                        employeeService.getUserExtByPersonGroupId(getItem().getJobRequest().getCandidatePersonGroup().getId()).getId());
            } else {
                log.warn("Recruiter is null!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void sendNotificationFailInterview() {
        try {
            UserExt recruiter = employeeService.getUserExtByPersonGroupId(getItem().getJobRequest().getCandidatePersonGroup().getId(), "user.browse");
            if (recruiter != null) {
                Map<String, Object> params = new HashMap<>();
                params.put("personName", getItem().getJobRequest().getCandidatePersonGroup().getPerson().getFullName() != null ?
                        getItem().getJobRequest().getCandidatePersonGroup().getPerson().getFullName() : "");
                if (getItem().getRequisitionHiringStep().getHiringStep().getStepName() != null) {
                    params.put("stepName", getItem().getRequisitionHiringStep().getHiringStep().getStepName());
                } else {
                    params.put("stepName", "");
                }
                if (getItem().getJobRequest().getRequisition().getJobGroup() != null) {
                    params.put("positionName", getItem().getJobRequest().getRequisition().getJobGroup().getJob().getJobName());
                } else {
                    params.put("positionName", "");
                }
                RequisitionHiringStep requisitionHiringStep = getItem().getRequisitionHiringStep();
                if (requisitionHiringStep != null) {
                    if (requisitionHiringStep.getHiringStep() != null) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("hiringStepId", requisitionHiringStep.getHiringStep().getId());
                        String query = "select e from tsadv$HiringStep e where e.id = :hiringStepId";
                        HiringStep hiringStep = commonService.getEntity(HiringStep.class,
                                query,
                                map,
                                "hiringStep.view");
                        if (hiringStep.getType() != null) {
                            if (HiringStepType.interview == hiringStep.getType()) {
                                candidateFailInterviewCode = "";
                                candidateFailInterviewCode = "interview.fail.deferred.notify";
                            } else {
                                candidateFailInterviewCode = "";
                                candidateFailInterviewCode = "test.fail.deferred.notify";
                            }

                            notificationService.sendParametrizedNotification(
                                    candidateFailInterviewCode,
                                    recruiter,
                                    params);
                            activityService.createActivity(
                                    recruiter,
                                    employeeService.getSystemUser(),
                                    commonService.getEntity(ActivityType.class, "NOTIFICATION"),
                                    StatusEnum.active,
                                    "descripiton",
                                    null,
                                    new Date(),
                                    null,
                                    null,
                                    null,
                                    candidateFailInterviewCode,
                                    params);
                        }
                    }
                }
            } else {
                log.warn("Recruiter is null!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void processAction() {
        switch (getItem().getInterviewStatus()) {
            case DRAFT:
            case ON_APPROVAL:
                sendForApproval();
                break;
            case PLANNED:
                planInterview();
                break;
            /*KCHR-348
            case COMPLETED:
                sendNotificationCompleteInterview();
                break;*/
            case CANCELLED:
                cancelInterview();
                break;
            case FAILED:
                sendNotificationFailInterview();
                break;
        }
    }

    //business logic for JobRequest Status
    protected void updateJobRequestStatus() {
        JobRequest jobRequest = dataManager.reload(getItem().getJobRequest(), "jobRequest.full");

        if (jobRequest.getRequestStatus() != JobRequestStatus.HIRED
                && jobRequest.getRequestStatus() != JobRequestStatus.MADE_OFFER
                && jobRequest.getRequestStatus() != JobRequestStatus.REJECTED) {
            switch (getItem().getInterviewStatus()) {
                case CANCELLED:
                    jobRequest.setRequestStatus(JobRequestStatus.ON_APPROVAL);
                    dataManager.commit(jobRequest);
                    break;
                case FAILED:
                    jobRequest.setRequestStatus(JobRequestStatus.REJECTED);
                    jobRequest.setJobRequestReason(commonService.getEntity(DicJobRequestReason.class, "INTERVIEW_FAILED"));
                    jobRequest.setReason(getItem().getRequisitionHiringStep().getHiringStep().getStepName());
                    dataManager.commit(jobRequest);
                    break;
                case PLANNED:
                    jobRequest.setRequestStatus(JobRequestStatus.INTERVIEW);
                    dataManager.commit(jobRequest);
                    break;
                case COMPLETED:
                    List<Object[]> notPassedInterviewCount = commonService.emNativeQueryResultList("select count(rhs.id) - count(i.id) as cnt, '' as dummy" +
                            "      from tsadv_job_request jr " +
                            " left join tsadv_requisition_hiring_step rhs on (rhs.requisition_id = jr.requisition_id)" +
                            " left join tsadv_interview i on (i.job_request_id = jr.id and i.requisition_hiring_step_id = rhs.id and i.interview_status = 40)" +
                            "     where jr.id = ?1" +
                            "       and jr.delete_ts is null" +
                            "       and i.delete_ts is null" +
                            "       and rhs.delete_ts is null" +
                            "  group by jr.id", Collections.singletonMap(1, getItem().getJobRequest().getId()));
                    if (notPassedInterviewCount != null && !notPassedInterviewCount.isEmpty()) {
                        Object[] row = notPassedInterviewCount.get(0);
                        jobRequest.setRequestStatus(((Long) row[0]).intValue() == 0 ? JobRequestStatus.SELECTED : JobRequestStatus.ON_APPROVAL);
                        dataManager.commit(jobRequest);
                    }
                    break;
            }
        }
    }

    protected void calcMainInterviewer(Boolean calc) {
        RequisitionHiringStep requisitionHiringStep = getItem().getRequisitionHiringStep();
        Requisition requisition = getItem().getJobRequest().getRequisition();

        HiringStep hiringStep = null;
        if (requisitionHiringStep != null) {
            hiringStep = requisitionHiringStep.getHiringStep();
        }

        Map<String, Object> params = new HashMap<>();
        params.put("hiringStepId", hiringStep != null ? hiringStep.getId() : null);
        params.put("systemDate", CommonUtils.getSystemDate());

        HiringStepMember hiringStepMember = commonService.getEntity(HiringStepMember.class,
                "select hsm " +
                        "from tsadv$HiringStepMember hsm " +
                        "where hsm.hiringStep.id = :hiringStepId " +
                        "   and hsm.mainInterviewer = TRUE " +
                        "   and (hsm.startDate IS NULL or hsm.startDate <= :systemDate) " +
                        "   and (hsm.endDate IS NULL or hsm.endDate >= :systemDate) ",
                params, "hiringStepMember.view");

        List<OrganizationHrUser> organizationHrUsers = new ArrayList<>();

        String memberTypeCode = null;
        String memberRoleCode = null;

        if (hiringStepMember != null) {
            memberTypeCode = hiringStepMember.getHiringMemberType().getCode();
            memberRoleCode = hiringStepMember.getRole() != null ? hiringStepMember.getRole().getCode() : null;
        }

        if (hiringStepMember != null && "ROLE".equals(memberTypeCode)) {
            organizationHrUsers = employeeService.getHrUsers(
                    requisition.getOrganizationGroup().getId(),
                    memberRoleCode);

            if (memberRoleCode != null) {
                switch (memberRoleCode) {
                    case "MANAGER": {
                        params.put("managerPersonGroupId", requisition.getManagerPersonGroup().getId());
                        break;
                    }
                    case "RECRUITING_SPECIALIST": {
                        params.put("recruiterPersonGroupId",
                                requisition.getRecruiterPersonGroup() != null ?
                                        requisition.getRecruiterPersonGroup().getId() : null);
                        break;
                    }
                }
            }
        }


        if (!calc) {
            params.put("hiringStepMember", hiringStepMember);
            params.put("organizationGroupId", organizationHrUsers.size() > 0 ? organizationHrUsers.get(0).getOrganizationGroup().getId() : null);

            Utils.customizeLookup(fieldGroup1.getField("mainInterviewerPersonGroup").getComponent(),
                    "interviewer.lookup",
                    WindowManager.OpenType.DIALOG,
                    params);
        } else {
            if (hiringStepMember != null && memberTypeCode != null) {
                switch (memberTypeCode) {
                    case "USER": {
                        params.remove("hiringStepId");
                        params.put("hsmPersonGroupId", hiringStepMember.getUserPersonGroup() != null ? hiringStepMember.getUserPersonGroup().getId() : null);

                        PersonGroupExt personGroup = commonService.getEntity(PersonGroupExt.class, "select e" +
                                        "                           from base$PersonGroupExt e" +
                                        "                           join e.list p" +
                                        "                           left join e.assignments a" +
                                        "                          where :systemDate between p.startDate and p.endDate" +
                                        "                            and (p.type.code <> 'EMPLOYEE' OR :systemDate between a.startDate and a.endDate)" +
                                        "                            and e.id = :hsmPersonGroupId",
                                params, "personGroupId.browse");
                        if (personGroup != null)
                            getItem().setMainInterviewerPersonGroup(personGroup);
                        break;
                    }
                    case "ROLE": {
                        if (memberRoleCode != null) {
                            switch (memberRoleCode) {
                                case "MANAGER": {
                                    getItem().setMainInterviewerPersonGroup(requisition.getManagerPersonGroup());
                                    break;
                                }
                                case "RECRUITING_SPECIALIST": {
                                    getItem().setMainInterviewerPersonGroup(requisition.getRecruiterPersonGroup());
                                    break;
                                }
                                default: {
                                    if (organizationHrUsers != null && organizationHrUsers.size() == 1) {
                                        getItem().setMainInterviewerPersonGroup(employeeService.getPersonGroupByUserId(organizationHrUsers.get(0).getUser().getId())); //TODO:personGroupId need to test
                                    }
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    public List<RcQuestionnaire> questionnaireQuery() {
        Map<String, Object> map = new HashMap<>();
        map.put("jobRequestId", interviewDs.getItem().getJobRequest().getId());
        String query = "select e from tsadv$RcQuestionnaire e, tsadv$InterviewQuestionnaire q, tsadv$Interview i, tsadv$JobRequest jr " +
                "       where jr.id = :jobRequestId " +
                "       and e.id = q.questionnaire.id " +
                "       and q.interview.id = i.id " +
                "       and i.jobRequest.id = jr.id ";
        return commonService.getEntities(RcQuestionnaire.class, query, map, null);
    }

    public void addQuestionnaires() {
        Map<String, Object> params = new HashMap<>();
        params.put(WindowParams.MULTI_SELECT.toString(), Boolean.TRUE);
        params.put("requisitionId", getItem().getJobRequest() == null || getItem().getJobRequest().getRequisition() == null ? null : getItem().getJobRequest().getRequisition());
        params.put("filterByRequisition", Boolean.TRUE);
        params.put("excludeRcQuestionnaireIds", questionnairesDs.getItems() != null ? questionnairesDs.getItems().stream().map(qq -> qq.getQuestionnaire().getId()).collect(Collectors.toList()) : null);
        params.put("excludeQuestionnaireIds", questionnaireQuery());

        openLookup("tsadv$RcQuestionnaire.lookup", items -> {
            for (RcQuestionnaire item : (Collection<RcQuestionnaire>) items) {
                addQuestionnaire(item);
            }
            questionnairesDs.setItem(null);
            getDsContext().commit();

        }, WindowManager.OpenType.DIALOG, params);
    }

    protected void addQuestionnaire(RcQuestionnaire rcQuestionnaire) {
        RcQuestionnaire questionnaire = dataManager.reload(rcQuestionnaire, "rcQuestionnaire.view");
        InterviewQuestionnaire interviewQuestionnaire = metadata.create(InterviewQuestionnaire.class);
        interviewQuestionnaire.setInterview(getItem());
        interviewQuestionnaire.setQuestionnaire(questionnaire);
        interviewQuestionnaire.setQuestions(new ArrayList<>());
        questionnairesDs.addItem(interviewQuestionnaire);
    }

    public Component generateQuestionnaireResult(InterviewQuestionnaire interviewQuestionnaire) {
        HBoxLayout wrapper = componentsFactory.createComponent(HBoxLayout.class);
        wrapper.setSpacing(true);
        wrapper.setWidthFull();

        /*LinkButton linkButton = generatePreScreeningResult(interviewQuestionnaire);
        linkButton.setAlignment(Alignment.MIDDLE_CENTER);
        wrapper.add(linkButton);
        wrapper.expand(linkButton);*/

        HBoxLayout buttonGroupLayout = componentsFactory.createComponent(HBoxLayout.class);
        buttonGroupLayout.setAlignment(Alignment.MIDDLE_CENTER);

        LinkButton editButton = componentsFactory.createComponent(LinkButton.class);
        editButton.setIcon("icons/edit.png");
        editButton.setCaption("");
        editButton.setAction(new BaseAction("edit-link") {
            @Override
            public void actionPerform(Component component) {
                questionnairesTable.setSelected(interviewQuestionnaire);

                if (validateAll()) {
                    getDsContext().commit();
                    questionnairesTable.getActionNN(EditAction.ACTION_ID).actionPerform(component);
                }
            }
        });
        buttonGroupLayout.add(editButton);

        LinkButton removeButton = componentsFactory.createComponent(LinkButton.class);
        removeButton.setCaption("");
        removeButton.setIcon("icons/remove.png");
        removeButton.setAction(new BaseAction("remove-link") {
            @Override
            public void actionPerform(Component component) {
                questionnairesTable.setSelected(interviewQuestionnaire);
                questionnairesTable.getActionNN(RemoveAction.ACTION_ID).actionPerform(component);
            }
        });
        buttonGroupLayout.add(removeButton);

        wrapper.add(buttonGroupLayout);

        return wrapper;
    }

    protected LinkButton generatePreScreeningResult(InterviewQuestionnaire interviewQuestionnaire) {
        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);

        if (interviewQuestionnaire != null) {
            Double passingScore = null;

            RcQuestionnaire rcQuestionnaire = interviewQuestionnaire.getQuestionnaire();
            if (rcQuestionnaire != null) {
                passingScore = rcQuestionnaire.getPassingScore();
            }

            Double totalScore = 0d;
            Double maxScore = 0d;

            if (interviewQuestionnaire.getTotalScore() == null) {
                totalScore = getTotalScore(interviewQuestionnaire);
            }

            if (interviewQuestionnaire.getTotalMaxScore() == null) {
                maxScore = getMaxScore(interviewQuestionnaire);
            }


            if (!PersistenceHelper.isNew(interviewQuestionnaire)) {
                InterviewQuestionnaire interviewQuestionnaireBd = loadInterviewQuestionnaire(interviewQuestionnaire.getId());
                calculateTotalScore(interviewQuestionnaireBd);

                totalScore = interviewQuestionnaireBd.getTotalScore();
                maxScore = interviewQuestionnaireBd.getTotalMaxScore();
            }

            //calculateTotalScore(interviewQuestionnaire, passingScore, linkButton);

            linkButton.setCaption(String.format("%s / %s", totalScore, maxScore));

            if (passingScore != null) {
                String cssClass = (totalScore >= passingScore) ? "pre-screen-passed" : "pre-screen-fail";
                linkButton.setStyleName(cssClass);
            }

            linkButton.setAction(new BaseAction("open-pre-screen") {
                @Override
                public void actionPerform(Component component) {
                    openWindow("interview-pre-screen",
                            WindowManager.OpenType.DIALOG,
                            ParamsMap.of(StaticVariable.INTERVIEW_ID, interviewQuestionnaire.getInterview().getId()));
                }
            });
        }

        return linkButton;
    }

    protected void calculateTotalScore(InterviewQuestionnaire interviewQuestionnaire, Double
            passingScore, LinkButton linkButton) {
        /*InterviewQuestionnaire interviewQuestionnaireDb = loadInterviewQuestionnaire(interviewQuestionnaire.getId());

        if (interviewQuestionnaireDb != null) {
            interviewQuestionnaire = interviewQuestionnaireDb;
        }

        Collection<InterviewQuestion> propertyValues = interviewQuestionnaire.getQuestions();
        Double maxScore = 0d;
        Double totalScore = 0d;

        for (InterviewQuestion interviewQuestion : propertyValues) {
            if (interviewQuestion.getQuestion().getAnswerType() == RcAnswerType.MULTI) {
                maxScore += interviewQuestion.getAnswers().stream().mapToDouble(a -> (a != null && a.getWeight() != null) ? a.getWeight() : 0).sum();
                totalScore += interviewQuestion.getAnswers().stream().filter(answer -> answer != null && BooleanUtils.isTrue(answer.getBooleanAnswer())).mapToDouble(a -> a.getWeight() != null ? a.getWeight() : 0).sum();
            }
            if (interviewQuestion.getQuestion().getAnswerType() == RcAnswerType.SINGLE) {
                maxScore += interviewQuestion.getAnswers().stream().mapToDouble(a -> (a != null && a.getWeight() != null) ? a.getWeight() : 0).max().orElseGet(() -> 0d);
                totalScore += interviewQuestion.getAnswers().stream().filter(answer -> answer != null && BooleanUtils.isTrue(answer.getBooleanAnswer())).mapToDouble(a -> a.getWeight() != null ? a.getWeight() : 0).sum();
            }
        }*/

        /*interviewQuestionnaire.setTotalScore(totalScore);
        interviewQuestionnaire.setTotalMaxScore(maxScore);*/

        linkButton.setCaption(String.format("%s / %s", interviewQuestionnaire.getTotalScore(), interviewQuestionnaire.getTotalMaxScore()));

        if (passingScore != null) {
            String cssClass = (interviewQuestionnaire.getTotalScore() != null && interviewQuestionnaire.getTotalScore() >= passingScore) ? "pre-screen-passed" : "pre-screen-fail";
            linkButton.setStyleName(cssClass);
        }
    }

    protected Double getTotalScore(InterviewQuestionnaire interviewQuestionnaire) {
        Collection<InterviewQuestion> propertyValues = interviewQuestionnaire.getQuestions();
        Double totalScore = 0d;

        for (InterviewQuestion interviewQuestion : propertyValues) {
            if (interviewQuestion.getQuestion().getAnswerType() == RcAnswerType.MULTI) {
                totalScore += interviewQuestion.getAnswers().stream().filter(answer -> answer != null && BooleanUtils.isTrue(answer.getBooleanAnswer())).mapToDouble(a -> a.getWeight() != null ? a.getWeight() : 0).sum();
            }
            if (interviewQuestion.getQuestion().getAnswerType() == RcAnswerType.SINGLE) {
                totalScore += interviewQuestion.getAnswers().stream().filter(answer -> answer != null && BooleanUtils.isTrue(answer.getBooleanAnswer())).mapToDouble(a -> a.getWeight() != null ? a.getWeight() : 0).sum();
            }
        }
        return totalScore;
    }

    protected Double getMaxScore(InterviewQuestionnaire interviewQuestionnaire) {
        Collection<InterviewQuestion> propertyValues = interviewQuestionnaire.getQuestions();
        Double maxScore = 0d;

        for (InterviewQuestion interviewQuestion : propertyValues) {
            if (interviewQuestion.getQuestion().getAnswerType() == RcAnswerType.MULTI) {
                maxScore += interviewQuestion.getAnswers().stream().mapToDouble(a -> (a != null && a.getWeight() != null) ? a.getWeight() : 0).sum();
            }
            if (interviewQuestion.getQuestion().getAnswerType() == RcAnswerType.SINGLE) {
                maxScore += interviewQuestion.getAnswers().stream().mapToDouble(a -> (a != null && a.getWeight() != null) ? a.getWeight() : 0).max().orElseGet(() -> 0d);
            }
        }
        return maxScore;
    }

    protected void calculateTotalScore(InterviewQuestionnaire interviewQuestionnaire) {
        Collection<InterviewQuestion> propertyValues = interviewQuestionnaire.getQuestions();
        Double maxScore = 0d;
        Double totalScore = 0d;

        for (InterviewQuestion interviewQuestion : propertyValues) {
            if (interviewQuestion.getQuestion().getAnswerType() == RcAnswerType.MULTI) {
                maxScore += interviewQuestion.getAnswers().stream().mapToDouble(a -> (a != null && a.getWeight() != null) ? a.getWeight() : 0).sum();
                totalScore += interviewQuestion.getAnswers().stream().filter(answer -> answer != null && BooleanUtils.isTrue(answer.getBooleanAnswer())).mapToDouble(a -> a.getWeight() != null ? a.getWeight() : 0).sum();
            }
            if (interviewQuestion.getQuestion().getAnswerType() == RcAnswerType.SINGLE) {
                maxScore += interviewQuestion.getAnswers().stream().mapToDouble(a -> (a != null && a.getWeight() != null) ? a.getWeight() : 0).max().orElseGet(() -> 0d);
                totalScore += interviewQuestion.getAnswers().stream().filter(answer -> answer != null && BooleanUtils.isTrue(answer.getBooleanAnswer())).mapToDouble(a -> a.getWeight() != null ? a.getWeight() : 0).sum();
            }
        }

        interviewQuestionnaire.setTotalScore(totalScore);
        interviewQuestionnaire.setTotalMaxScore(maxScore);
    }

    protected InterviewQuestionnaire loadInterviewQuestionnaire(UUID interviewQuestionnaireId) {
        LoadContext<InterviewQuestionnaire> loadContext = LoadContext.create(InterviewQuestionnaire.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$InterviewQuestionnaire e " +
                        "where e.id = :iqId");
        query.setParameter("iqId", interviewQuestionnaireId);
        loadContext.setQuery(query);
        loadContext.setView("interviewQuestionnaire.calculate");
        return dataManager.load(loadContext);
    }

    protected void interviewStatusFilter() {
        if (BooleanUtils.isFalse(enableDraftStatusInterview.getEnabled())) {
            List<InterviewStatus> statusList = new ArrayList<>();
            for (InterviewStatus at : InterviewStatus.values()) {
                if (!at.getId().equals(10)) {
                    statusList.add(at);
                }
            }
            interviewStatusField.setOptionsList(statusList);
        }
    }
}