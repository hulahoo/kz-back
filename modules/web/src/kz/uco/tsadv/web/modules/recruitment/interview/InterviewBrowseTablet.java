package kz.uco.tsadv.web.modules.recruitment.interview;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.filter.Op;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.recruitment.dictionary.DicJobRequestReason;
import kz.uco.tsadv.modules.recruitment.enums.JobRequestStatus;
import kz.uco.tsadv.modules.recruitment.model.*;
import kz.uco.base.entity.extend.UserExt;
import kz.uco.tsadv.modules.recruitment.enums.InterviewStatus;
import kz.uco.base.web.components.CustomFilter;
import kz.uco.base.service.common.CommonService;
import kz.uco.base.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;

public class InterviewBrowseTablet extends AbstractWindow {

    private static final Logger log = LoggerFactory.getLogger(InterviewBrowseTablet.class);

    @Inject
    private CommonService commonService;
    @Inject
    private NotificationService notificationService;
    @Inject
    private Metadata metadata;
    @Inject
    protected GroupDatasource<Interview, UUID> interviewsDs;
    @Inject
    private CollectionDatasource<InterviewAnswer, UUID> answersDs;
    @Inject
    private CollectionDatasource<InterviewQuestionnaire, UUID> questionnairesDs;
    @Inject
    private CollectionDatasource<InterviewQuestion, UUID> questionsDs;
    @Inject
    private VBoxLayout filterBox;
    @Inject
    private Button personCardBtn;
    @Inject
    private Button startInterviewBtn;
    @Inject
    private Button completeInterviewBtn;
    @Inject
    private Button failInterviewBtn;
    @Inject
    private DataManager dataManager;

    private Map<String, CustomFilter.Element> filterMap;

    private CustomFilter customFilter;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        initFilterMap();

        customFilter = CustomFilter.init(interviewsDs, interviewsDs.getQuery(), filterMap);
        filterBox.add(customFilter.getFilterComponent());

        customFilter.selectFilter("candidateFullName");
        customFilter.selectFilter("interviewDate");

        initButtons();
    }

    public void completeInterview() {
        Interview interview = interviewsDs.getItem();
        if (interview != null) {

            interview.setInterviewStatus(InterviewStatus.COMPLETED);
            try {
                validateInterview(interview);
                showOptionDialog(
                        getMessage("msg.warning.title"),
                        getMessage("Interview.confirm.yes.text"),
                        MessageType.CONFIRMATION,
                        new Action[]{
                                new DialogAction(DialogAction.Type.YES) {
                                    @Override
                                    public void actionPerform(Component component) {
                                        interview.setReason(null);
                                        interview.setInterviewReason(null);
                                        interviewsDs.modifyItem(interview);
                                        interviewsDs.commit();
                                        interviewsDs.refresh();
                                        updateJobRequestStatus(interview);
                                        showNotification(
                                                getMessage("msg.success.title"),
                                                getMessage("Interview.confirm.commit"),
                                                NotificationType.TRAY);

                                        /*KCHR-348
                                        sendNotification(interview, true);*/
                                    }
                                },
                                new DialogAction(DialogAction.Type.NO) {
                                    @Override
                                    public void actionPerform(Component component) {
                                        interviewsDs.refresh();
                                    }
                                }
                        });
            } catch (Exception ex) {
                interviewsDs.refresh();

                showNotification(
                        getMessage("msg.error.title"),
                        ex.getMessage(),
                        NotificationType.TRAY);
            }
        }
    }

    public void failInterview() {
        Interview interview = interviewsDs.getItem();
        if (interview != null) {
            showOptionDialog(
                    getMessage("msg.warning.title"),
                    getMessage("Interview.confirm.yes.text"),
                    MessageType.CONFIRMATION,
                    new Action[]{
                            new DialogAction(DialogAction.Type.YES) {
                                @Override
                                public void actionPerform(Component component) {
                                    InterviewFailConfirm failConfirm = (InterviewFailConfirm) openWindow("interview-fail-confirm", WindowManager.OpenType.DIALOG);
                                    failConfirm.addCloseListener(new CloseListener() {
                                        @Override
                                        public void windowClosed(String actionId) {
                                            if (actionId.equalsIgnoreCase("yes")) {
                                                interview.setInterviewStatus(InterviewStatus.FAILED);
                                                interview.setInterviewReason(failConfirm.getDicInterviewReason());
                                                interview.setReason(failConfirm.getReason());

                                                try {
                                                    validateInterview(interview);

                                                    interviewsDs.modifyItem(interview);
                                                    interviewsDs.commit();
                                                    interviewsDs.refresh();
                                                    updateJobRequestStatus(interview);

                                                    showNotification(
                                                            getMessage("msg.success.title"),
                                                            getMessage("Interview.confirm.commit"),
                                                            NotificationType.TRAY);

                                                    /*KCHR-348
                                                    sendNotification(interview, false);*/
                                                } catch (Exception ex) {
                                                    interviewsDs.refresh();

                                                    showNotification(
                                                            getMessage("msg.error.title"),
                                                            ex.getMessage(),
                                                            NotificationType.TRAY);
                                                }

                                            }
                                        }
                                    });
                                }
                            },
                            new DialogAction(DialogAction.Type.NO) {
                                @Override
                                public void actionPerform(Component component) {
                                    interviewsDs.refresh();
                                }
                            }
                    });
        }
    }

    private void sendNotification(Interview interview, boolean success) {
        try {
            UserExt candidateUser = commonService.getEntity(UserExt.class,
                    "select e.userExt from tsadv$UserExtPersonGroup e " +
                            "where e.personGroup.id = (select jr.candidatePersonGroup.id from tsadv$JobRequest jr where jr.id = :jrId)",
                    Collections.singletonMap("jrId", interview.getJobRequest().getId()),
                    "user.browse");

            if (candidateUser != null) {
                Map<String, Object> params = new HashMap<>();
                params.put("step", interview.getRequisitionHiringStep().getHiringStep());
                params.put("personName", interview.getJobRequest().getCandidatePersonGroup().getPerson().getFullName() != null ?
                        interview.getJobRequest().getCandidatePersonGroup().getPerson().getFullName() : "");
                if (interview.getRequisitionHiringStep().getHiringStep().getStepName() != null) {
                    params.put("stepName", interview.getRequisitionHiringStep().getHiringStep().getStepName());
                } else {
                    params.put("stepName", "");
                }

                notificationService.sendDeferredParametrizedNotification(
                        success ? "interview.complete.deferred.notify" : "interview.fail.deferred.notify",
                        candidateUser,
                        params);
            } else {
                showNotification(getMessage("Interview.candidate.not.user"), NotificationType.TRAY);
                log.warn("Candidate User is null!");

            }
        } catch (Exception e) {
            showNotification(e.getMessage(), NotificationType.TRAY);
            log.error(e.getMessage());
        }
    }

    @SuppressWarnings("all")
    private void validateInterview(Interview interview) throws ValidationException {
        switch (interview.getInterviewStatus()) {
            case COMPLETED: {
                boolean unanswered = false;
                if (questionnairesDs.getState().equals(Datasource.State.INVALID)) questionnairesDs.refresh();
                questionnaireLoop:
                for (InterviewQuestionnaire questionnaire : questionnairesDs.getItems()) {
                    questionnairesDs.setItem(questionnaire);
                    for (InterviewQuestion question : questionsDs.getItems()) {
                        questionsDs.setItem(question);
                        switch (question.getQuestion().getAnswerType()) {
                            case DATE:
                                for (InterviewAnswer answer : answersDs.getItems())
                                    if (answer.getDateAnswer() == null) {
                                        unanswered = true;
                                        break questionnaireLoop;
                                    }
                                break;
                            case TEXT:
                                for (InterviewAnswer answer : answersDs.getItems())
                                    if (answer.getTextAnswer() == null) {
                                        unanswered = true;
                                        break questionnaireLoop;
                                    }
                                break;
                            case NUMBER:
                                for (InterviewAnswer answer : answersDs.getItems())
                                    if (answer.getNumberAnswer() == null) {
                                        unanswered = true;
                                        break questionnaireLoop;
                                    }
                                break;
                            case SINGLE:
                                if (answersDs.getItems() != null &&
                                        answersDs.getItems().stream().noneMatch(answer -> answer != null
                                                && answer.getBooleanAnswer() != null
                                                && answer.getBooleanAnswer())) {
                                    unanswered = true;
                                    break questionnaireLoop;
                                }
                                break;
                            case MULTI:
                                if (answersDs.getItems() != null &&
                                        answersDs.getItems().stream().noneMatch(answer -> answer != null
                                                        && answer.getBooleanAnswer() != null
                                                /*&& answer.getBooleanAnswer()*/)) {
                                    unanswered = true;
                                    break questionnaireLoop;
                                }
                                break;
                        }
                    }
                }

                if (unanswered)
                    throw new ValidationException(getMessage("Interview.questionnaireAnswers.validatorMsg"));
                break;
            }
            case FAILED: {
                MetaClass interviewMetaClass = metadata.getClass("tsadv$Interview");

                if (interview.getInterviewReason() == null) {
                    throw new ValidationException(messages.formatMainMessage("validation.required.defaultMsg",
                            messages.getTools().getPropertyCaption(interviewMetaClass, "interviewReason")));
                }

                if (interview.getInterviewReason() != null
                        && "OTHER".equals(interview.getInterviewReason().getCode())
                        && interview.getReason() == null) {
                    throw new ValidationException(messages.formatMainMessage("validation.required.defaultMsg",
                            messages.getTools().getPropertyCaption(interviewMetaClass, "reason")));
                }
                break;
            }
        }
    }

    private void initFilterMap() {
        filterMap = new LinkedHashMap<>();
        filterMap.put("requisition",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.recruitment", "JobRequest.requisition"))
                        .setComponentClass(TextField.class)
                        .setOptions(Op.CONTAINS, Op.DOES_NOT_CONTAIN, Op.ENDS_WITH, Op.STARTS_WITH, Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("lower(r.code) ?"));

        filterMap.put("candidateFullName",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.recruitment", "JobRequest.candidatePersonGroup"))
                        .setComponentClass(TextField.class)
                        .setOptions(Op.CONTAINS, Op.DOES_NOT_CONTAIN, Op.ENDS_WITH, Op.STARTS_WITH, Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("lower(concat(p.lastName,concat(' ', concat(p.firstName, concat(' ', coalesce(p.middleName,'')))))) ?")
        );

        filterMap.put("interviewDate",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.recruitment", "Interview.interviewDate"))
                        .setComponentClass(DateField.class)
                        .addComponentAttribute("resolution", DateField.Resolution.DAY)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL, Op.GREATER, Op.GREATER_OR_EQUAL, Op.LESSER, Op.LESSER_OR_EQUAL)
                        .setQueryFilter("e.interviewDate ?"));

        filterMap.put("timeFrom",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.recruitment", "Interview.timeFrom"))
                        .setComponentClass(TimeField.class)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL, Op.GREATER, Op.GREATER_OR_EQUAL, Op.LESSER, Op.LESSER_OR_EQUAL)
                        .setQueryFilter("e.timeFrom ?"));

        filterMap.put("timeTo",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.recruitment", "Interview.timeTo"))
                        .setComponentClass(TimeField.class)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL, Op.GREATER, Op.GREATER_OR_EQUAL, Op.LESSER, Op.LESSER_OR_EQUAL)
                        .setQueryFilter("e.timeTo ?"));
    }

    private void initButtons() {
        personCardBtn.setEnabled(interviewsDs.getItem() != null);
        startInterviewBtn.setEnabled(interviewsDs.getItem() != null);
        completeInterviewBtn.setEnabled(interviewsDs.getItem() != null);
        failInterviewBtn.setEnabled(interviewsDs.getItem() != null);

        interviewsDs.addItemChangeListener(new Datasource.ItemChangeListener<Interview>() {
            @Override
            public void itemChanged(Datasource.ItemChangeEvent<Interview> e) {
                personCardBtn.setEnabled(e.getItem() != null);
                startInterviewBtn.setEnabled(e.getItem() != null);
                completeInterviewBtn.setEnabled(e.getItem() != null);
                failInterviewBtn.setEnabled(e.getItem() != null);
            }
        });
    }

    public void personCard() {
        PersonExt person = reloadPerson("person.candidate");
        if (person != null) {
            InterviewCandidateCard interviewCandidateCard =
                    (InterviewCandidateCard) openEditor("interview-candidate-card", person, WindowManager.OpenType.THIS_TAB);
        } else {
            showNotification("Candidate Person in JobRequest is null!");
        }
    }

    public void startInterview() {
        AbstractWindow abstractWindow = openWindow("interview-candidate-questionnaire",
                WindowManager.OpenType.THIS_TAB,
                new HashMap<String, Object>() {{
                    put("interview", interviewsDs.getItem());
                    put("person", reloadPerson("person.minimal"));
                }});
        abstractWindow.addCloseListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                interviewsDs.refresh();
            }
        });
    }

    protected PersonExt reloadPerson(String viewName) {
        Interview interview = interviewsDs.getItem();
        if (interview != null) {
            PersonGroupExt candidatePersonGroup = interview.getJobRequest().getCandidatePersonGroup();
            if (candidatePersonGroup != null) {
                PersonExt candidatePerson = candidatePersonGroup.getPerson();
                if (candidatePerson != null) {
                    return dataManager.reload(candidatePerson, viewName);
                }
            }
        }
        return null;
    }

    //business logic for JobRequest Status
    private void updateJobRequestStatus(Interview interview) {
        JobRequest jobRequest = dataManager.reload(interview.getJobRequest(), "jobRequest.full");

        if (jobRequest.getRequestStatus() != JobRequestStatus.HIRED
                && jobRequest.getRequestStatus() != JobRequestStatus.MADE_OFFER
                && jobRequest.getRequestStatus() != JobRequestStatus.REJECTED) {
            switch (interview.getInterviewStatus()) {
                case CANCELLED:
                    jobRequest.setRequestStatus(JobRequestStatus.ON_APPROVAL);
                    dataManager.commit(jobRequest);
                    break;
                case FAILED:
                    jobRequest.setRequestStatus(JobRequestStatus.REJECTED);
                    jobRequest.setJobRequestReason(commonService.getEntity(DicJobRequestReason.class, "INTERVIEW_FAILED"));
                    jobRequest.setReason(interview.getRequisitionHiringStep().getHiringStep().getStepName());
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
                            "  group by jr.id", Collections.singletonMap(1, interview.getJobRequest().getId()));
                    if (notPassedInterviewCount != null && !notPassedInterviewCount.isEmpty()) {
                        Object[] row = notPassedInterviewCount.get(0);
                        jobRequest.setRequestStatus(((Long) row[0]).intValue() == 0 ? JobRequestStatus.SELECTED : JobRequestStatus.ON_APPROVAL);
                        dataManager.commit(jobRequest);
                    }
                    break;
            }
        }
    }
}