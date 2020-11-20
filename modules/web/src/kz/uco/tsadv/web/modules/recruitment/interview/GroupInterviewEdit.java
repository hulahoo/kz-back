package kz.uco.tsadv.web.modules.recruitment.interview;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.WindowParams;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.service.NotificationService;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.Job;
import kz.uco.tsadv.modules.personal.model.OrganizationHrUser;
import kz.uco.tsadv.modules.recruitment.enums.InterviewStatus;
import kz.uco.tsadv.modules.recruitment.enums.JobRequestStatus;
import kz.uco.tsadv.modules.recruitment.model.*;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.service.TemplateService;
import kz.uco.tsadv.web.components.CustomAccordion;
import kz.uco.tsadv.web.modules.personal.common.Utils;
import kz.uco.uactivity.entity.ActivityType;
import kz.uco.uactivity.entity.StatusEnum;
import kz.uco.uactivity.service.ActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class GroupInterviewEdit extends AbstractEditor<Interview> {
    private static final Logger log = LoggerFactory.getLogger(GroupInterviewEdit.class);

    //    private boolean isApprovalNotification = false;
    private boolean isSystemNotificationAllow;
    private int isOkAproveOrPlaned;
    @Inject
    private UserSession userSession;
    @Inject
    private Metadata metadata;
    @Inject
    private DataManager dataManager;
    @Inject
    private CommonService commonService;
    @Inject
    private EmployeeService employeeService;
    @Inject
    private NotificationService notificationService;

    @Inject
    private CollectionDatasource<InterviewDetail, UUID> interviewDetailsDs;
    @Inject
    private Datasource<Interview> interviewDs;
    @Inject
    private CollectionDatasource<Interview, UUID> interviewsDs;
    @Inject
    private CollectionDatasource<InterviewQuestionnaire, UUID> questionnairesDs;
    @Inject
    private VBoxLayout vbox;
    @Inject
    protected Table<Interview> interviewsTable;
    @Inject
    private Table<InterviewDetail> interviewDetailsTable;
    @Inject
    private Table<InterviewQuestionnaire> questionnairesTable;
    @Inject
    private FieldGroup fieldGroup;
    @Named("fieldGroup.requisitionHiringStep")
    private PickerField<Entity> requisitionHiringStepField;
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private TabSheet tabs;
    @Inject
    private ButtonsPanel buttonsPanelInterviewsTable;
    @Inject
    private TemplateService templateService;

    @WindowParam
    private Set<JobRequest> jobRequests;

    private boolean isCreate = false;
    private boolean editable = false;
    @Inject
    private ActivityService activityService;
    private NotificationType tray;

    @Override
    protected void initNewItem(Interview item) {
        super.initNewItem(item);
        item.setInterviewStatus(InterviewStatus.DRAFT);
        item.setInterviewDate(new Date());
        this.isCreate = true;
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        isSystemNotificationAllow = false;
        isOkAproveOrPlaned = 1;
        CustomAccordion customAccordion = new CustomAccordion();
        vbox.add(customAccordion.getAccordionWrapper());

        ButtonsPanel buttonsPanel1 = componentsFactory.createComponent(ButtonsPanel.class);
        buttonsPanel1.add(linkButton(questionnairesTable.getAction("add"), "font-icon:SEARCH_PLUS"));
        buttonsPanel1.add(linkButton(questionnairesTable.getAction("remove"), "icons/remove.png"));

        VBoxLayout content1 = componentsFactory.createComponent(VBoxLayout.class);
        questionnairesTable.setParent(null);
        content1.add(questionnairesTable);
        customAccordion.addTab(
                messages.getMessage("kz.uco.tsadv.web.modules.recruitment.interview", "questionnairesTab"),
                null,
                buttonsPanel1,
                content1,
                null,
                false);
        VBoxLayout content2 = componentsFactory.createComponent(VBoxLayout.class);

        ButtonsPanel buttonsPanel2 = componentsFactory.createComponent(ButtonsPanel.class);
        buttonsPanel2.add(linkButton(interviewDetailsTable.getAction("create"), "icons/plus-btn.png"));
        buttonsPanel2.add(linkButton(interviewDetailsTable.getAction("edit"), "icons/edit.png"));
        buttonsPanel2.add(linkButton(interviewDetailsTable.getAction("remove"), "icons/remove.png"));

        interviewDetailsTable.setParent(null);
        interviewDetailsTable.setStyleName(null);
        content2.add(interviewDetailsTable);
        customAccordion.addTab(
                messages.getMessage("kz.uco.tsadv.web.modules.recruitment.interview", "Interview.interviewDetails"),
                null,
                buttonsPanel2,
                content2,
                null,
                false);
    }

    private LinkButton linkButton(Action action, String icon) {
        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        linkButton.setIcon(icon);
        linkButton.setAction(action);
        linkButton.setCaption("");
        return linkButton;
    }

    @Override
    protected void postInit() {
        super.postInit();
        Map<String, Object> lookupParams = new HashMap<>();
        lookupParams.put("requisitionId", getItem().getRequisition() != null ? getItem().getRequisition().getId() : null);
//        lookupParams.put("jobRequestIds", jobRequests != null ?
//                jobRequests.stream().map(jr -> jr.getId()).collect(Collectors.toList()) : null);
        Utils.customizeLookup(fieldGroup.getField("requisitionHiringStep").getComponent(), null, WindowManager.OpenType.DIALOG, lookupParams);
        if (PersistenceHelper.isNew(getItem())) {
            fillinterviewsDs((RequisitionHiringStep) requisitionHiringStepField.getValue());
        }
        calcMainInterviewer(false);
        if (getItem().getMainInterviewerPersonGroup() == null)
            calcMainInterviewer(true);

        requisitionHiringStepField.addValueChangeListener(e -> {
            calcMainInterviewer(false);

            if (e.getValue() != null) {
                RequisitionHiringStep requisitionHiringStep = (RequisitionHiringStep) e.getValue();

                if (e.getPrevValue() != null) {
                    RequisitionHiringStep prevRequisitionHiringStep = (RequisitionHiringStep) e.getPrevValue();
                    if (!prevRequisitionHiringStep.getId().equals(requisitionHiringStep.getId())) {
                        getItem().setMainInterviewerPersonGroup(null);
                    }
                }
            } else {
                getItem().setMainInterviewerPersonGroup(null);
            }

            calcMainInterviewer(true);

            fillDefaultQuestionnaires();
            fillinterviewsDs((RequisitionHiringStep) e.getValue());

        });
        if (PersistenceHelper.isNew(getItem())) {
            fillDefaultQuestionnaires();
        }
        interviewsTable.addGeneratedColumn("jobRequestCount", entity -> {
            LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);

            Map<String, Object> queryMap = new HashMap<>();

            queryMap.put("personGroupId", entity.getJobRequest().getCandidatePersonGroup().getId());
            List<JobRequest> jobRequests = new ArrayList<>();
            try {
                jobRequests = commonService.getEntities(JobRequest.class,
                        "select e from tsadv$JobRequest e"
                                + " where e.candidatePersonGroup.id = :personGroupId"
                        , queryMap, "for-jobrequest-count");
            } catch (Exception e) {
                showNotification("prob" + e.getMessage());
                e.printStackTrace();
            }
            int jobrequestCount = jobRequests.size();
            if (jobrequestCount > 0) {
                linkButton.setCaption(String.valueOf(jobrequestCount));
                linkButton.setAction(new BaseAction("openJobrequests") {
                    @Override
                    public void actionPerform(Component component) {
                        super.actionPerform(component);

//                        Person person = entity.getPerson();
//                        openEditor("base$Person.candidate", person, WindowManager.OpenType.NEW_TAB,
//                                ParamsMap.of("openTab", "jobRequests"));
                        openWindow("jobRequestForGroupInterviewWindow", WindowManager.OpenType.NEW_TAB, queryMap);
                    }
                });
            }
            return linkButton;
        });
        interviewsTable.addGeneratedColumn("cancelButton", entity -> {
            Button button = componentsFactory.createComponent(Button.class);
            button.setCaption(getMessage("cancel"));
            button.setAction(new BaseAction("cancelJobrequest") {
                @Override
                public void actionPerform(Component component) {
                    super.actionPerform(component);
                    JobRequest jobRequest = entity.getJobRequest();
                    refuseCandidateWithDialog(jobRequest);
                }
            });

            return button;
        });
        interviewsTable.addGeneratedColumn("deleteButton", entity -> {
            Button button = componentsFactory.createComponent(Button.class);
            button.setCaption(getMessage("delete"));
            button.setAction(new BaseAction("deleteInterviewfromList") {
                @Override
                public void actionPerform(Component component) {
                    super.actionPerform(component);
                    JobRequest jobRequest = entity.getJobRequest();
                    interviewsDs.removeItem(entity);
//                    interviewsDs.refresh();
                }
            });

            return button;
        });


    }

//    public void addJobRequest() {
//        Map<String, Object> interviewParams = new HashMap<>();
//        List<Interview> interviews = new ArrayList<>();
//        interviewsDs.getItems().forEach(interviewElement1 -> {
//            interviews.add(interviewElement1);
//        });
//        interviewParams.put("alreadyExistInterview", interviews);
//        interviewParams.put("requisition", interviewDs.getItem().getRequisition().getId());
//        openLookup("tsadv$JobRequest.GroupInterview", new Lookup.Handler() {
//            @Override
//            public void handleLookup(Collection items) {
//                for (JobRequest jobRequest : (Collection<JobRequest>) items) {
//                    Interview lookUpInterview = metadata.create(Interview.class);
//                    lookUpInterview.setGroupInterview(interviewDs.getItem());
//                    lookUpInterview.setJobRequest(jobRequest);
//                    lookUpInterview.setInterviewStatus(InterviewStatus.DRAFT);
//                    interviewsDs.addItem(lookUpInterview);
//                }
//            }
//        }, WindowManager.OpenType.THIS_TAB, interviewParams);
//    }


    private void fillDefaultQuestionnaires() {
        Map<String, Object> qParams = new HashMap<>();
        if (getItem().getRequisitionHiringStep() == null) {
            return;
        }

        qParams.put("hiringStepId", getItem().getRequisitionHiringStep().getHiringStep().getId());
        qParams.put("requisitionId", getItem().getRequisition().getId());

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

    private void addQuestionnaire(RcQuestionnaire rcQuestionnaire) {
        RcQuestionnaire questionnaire = dataManager.reload(rcQuestionnaire, "rcQuestionnaire.view");
        InterviewQuestionnaire interviewQuestionnaire = metadata.create(InterviewQuestionnaire.class);
        interviewQuestionnaire.setInterview(getItem());
        interviewQuestionnaire.setQuestionnaire(questionnaire);
        questionnairesDs.addItem(interviewQuestionnaire);
    }

    private void fillinterviewsDs(RequisitionHiringStep requisitionHiringStep) {
        interviewsDs.clear();
        if (requisitionHiringStep == null) {
            return;
        }
        boolean isNotCanceled = false;
        for (JobRequest jobRequest : jobRequests) {
            if (jobRequest.getRequestStatus().equals(JobRequestStatus.REJECTED)) {
                continue;
            }
            if (!jobRequest.getInterviews().isEmpty()) {
                for (Interview interview : jobRequest.getInterviews()) {
                    if (interview.getRequisitionHiringStep().equals(requisitionHiringStep)
                            && !interview.getInterviewStatus().equals(InterviewStatus.CANCELLED)) {
                        isNotCanceled = true;
                        break;
                    }
                }
                if (isNotCanceled) {
                    isNotCanceled = false;
                    continue;
                }
            }
            Interview interview = metadata.create(Interview.class);
            interview.setInterviewStatus(InterviewStatus.DRAFT);
            interview.setRequisitionHiringStep(requisitionHiringStep);
            interview.setJobRequest(jobRequest);
            interview.setGroupInterview(getItem());
            interview.setIsGroup(false);
            interview.setIsScheduled(false);
            interviewsDs.addItem(interview);
        }
    }

    public void addQuestionnaires() {
        Map<String, Object> params = new HashMap<>();
        params.put(WindowParams.MULTI_SELECT.toString(), Boolean.TRUE);
        params.put("requisitionId", getItem().getRequisition());
        params.put("filterByRequisition", Boolean.TRUE);
        params.put("excludeRcQuestionnaireIds", questionnairesDs.getItems() != null ? questionnairesDs.getItems().stream().map(qq -> qq.getQuestionnaire().getId()).collect(Collectors.toList()) : null);

        openLookup("tsadv$RcQuestionnaire.lookup", items -> {
            for (RcQuestionnaire item : (Collection<RcQuestionnaire>) items) {
                addQuestionnaire(item);
            }
            questionnairesDs.setItem(null);

        }, WindowManager.OpenType.DIALOG, params);
    }

    private void calcMainInterviewer(Boolean calc) {
        Map<String, Object> params = new HashMap<>();
        params.put("hiringStepId", getItem() != null && getItem().getRequisitionHiringStep() != null && getItem().getRequisitionHiringStep().getHiringStep() != null ? getItem().getRequisitionHiringStep().getHiringStep().getId() : null);
        params.put("systemDate", CommonUtils.getSystemDate());
        HiringStepMember hiringStepMember = commonService.getEntity(HiringStepMember.class, "select hsm" +
                        "                              from tsadv$HiringStepMember hsm" +
                        "                             where hsm.hiringStep.id = :hiringStepId" +
                        "                               and hsm.mainInterviewer = TRUE" +
                        "                               and (hsm.startDate IS NULL or hsm.startDate <= :systemDate) " +
                        "                               and (hsm.endDate IS NULL or hsm.endDate >= :systemDate) ",
                params, "hiringStepMember.view");
        List<OrganizationHrUser> organizationHrUsers = new ArrayList<>();
        if (hiringStepMember != null && "ROLE".equals(hiringStepMember.getHiringMemberType().getCode())) {
            organizationHrUsers = employeeService.getHrUsers(
                    getItem().getRequisition().getOrganizationGroup().getId(),
                    hiringStepMember.getRole().getCode());
            if ("MANAGER".equals(hiringStepMember.getRole().getCode())) {
                params.put("managerPersonGroupId", getItem().getRequisition().getManagerPersonGroup().getId());
            } else if ("RECRUITING_SPECIALIST".equals(hiringStepMember.getRole().getCode())) {
                params.put("recruiterPersonGroupId",
                        getItem().getRequisition().getRecruiterPersonGroup() != null ?
                                getItem().getRequisition().getRecruiterPersonGroup().getId() : null);
            }
        }
        if (!calc) {
            params.put("hiringStepMember", hiringStepMember);
            params.put("organizationGroupId", organizationHrUsers.size() > 0 ?
                    organizationHrUsers.get(0).getOrganizationGroup().getId() : null);
            Utils.customizeLookup(fieldGroup.getField("mainInterviewerPersonGroup").getComponent(),
                    "interviewer.lookup",
                    WindowManager.OpenType.DIALOG,
                    params);
        } else {
            if (hiringStepMember != null && "USER".equals(hiringStepMember.getHiringMemberType().getCode())) {
                params.remove("hiringStepId");
                params.put("hsmPersonGroupId", hiringStepMember.getUserPersonGroup() != null ? hiringStepMember.getUserPersonGroup().getId() : null);
                PersonGroupExt personGroup = commonService.getEntity(PersonGroupExt.class, "select e" +
                                "                           from base$PersonGroupExt e" +
                                "                           join e.list p" +
                                "                           left join e.assignments a" +
                                "                          where :systemDate between p.startDate and p.endDate" +
                                "                            and (p.type.code <> 'EMPLOYEE' OR :systemDate between a.startDate and a.endDate)" +
                                "                            and e.id = :hsmPersonGroupId",
                        params, "personGroup.browse");
                if (personGroup != null)
                    getItem().setMainInterviewerPersonGroup(personGroup);
            } else if (hiringStepMember != null && "ROLE".equals(hiringStepMember.getHiringMemberType().getCode())) {
                if ("MANAGER".equals(hiringStepMember.getRole().getCode())) {
                    getItem().setMainInterviewerPersonGroup(getItem().getRequisition().getManagerPersonGroup());
                } else if ("RECRUITING_SPECIALIST".equals(hiringStepMember.getRole().getCode())) {
                    getItem().setMainInterviewerPersonGroup(getItem().getRequisition().getRecruiterPersonGroup());
                } else if (organizationHrUsers != null && organizationHrUsers.size() == 1) {
                    getItem().setMainInterviewerPersonGroup(employeeService.getPersonGroupByUserId(organizationHrUsers.get(0).getUser().getId())); //TODO: personGroup need to test
                }
            }
        }
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (isCreate) {
            List<Entity> commitInstances = new ArrayList<>();
            List<RcQuestionnaire> questionnaires = commonService.getEntities(RcQuestionnaire.class,
                    "select e " +
                            "    from tsadv$RcQuestionnaire e" +
                            "   where e.id in :questionnaireIds  ",
                    Collections.singletonMap("questionnaireIds", questionnairesDs.getItems().stream().map(iq -> iq.getQuestionnaire().getId()).collect(Collectors.toList())),
                    "rcQuestionnaire.view");

            for (Interview interview : interviewsDs.getItems()) {
                interview.setRequisitionHiringStep(getItem().getRequisitionHiringStep());
                interview.setInterviewDate(getItem().getInterviewDate());
                interview.setTimeFrom(getItem().getTimeFrom());
                interview.setTimeTo(getItem().getTimeTo());
                interview.setMainInterviewerPersonGroup(getItem().getMainInterviewerPersonGroup());
                interview.setPlace(getItem().getPlace());
                interview.setInterviewStatus(getItem().getInterviewStatus());

                commitInstances.add(interview);

                for (InterviewDetail groupInterviewDetail : interviewDetailsDs.getItems()) {
                    InterviewDetail interviewDetail = metadata.create(InterviewDetail.class);
                    interviewDetail.setInterview(interview);
                    interviewDetail.setInterviewerPersonGroup(groupInterviewDetail.getInterviewerPersonGroup());
                    commitInstances.add(interviewDetail);
                }

                for (RcQuestionnaire questionnaire : questionnaires) {
                    InterviewQuestionnaire interviewQuestionnaire = metadata.create(InterviewQuestionnaire.class);
                    interviewQuestionnaire.setInterview(interview);
                    interviewQuestionnaire.setQuestionnaire(questionnaire);

                    commitInstances.add(interviewQuestionnaire);

                    for (RcQuestionnaireQuestion questionnaireQuestion : questionnaire.getQuestions()) {
                        InterviewQuestion interviewQuestion = metadata.create(InterviewQuestion.class);
                        interviewQuestion.setInterviewQuestionnaire(interviewQuestionnaire);
                        interviewQuestion.setQuestion(questionnaireQuestion.getQuestion());
                        interviewQuestion.setOrder(questionnaireQuestion.getOrder());

                        commitInstances.add(interviewQuestion);

                        for (RcQuestionnaireAnswer questionnaireAnswer : questionnaireQuestion.getAnswers()) {
                            InterviewAnswer interviewAnswer = metadata.create(InterviewAnswer.class);
                            interviewAnswer.setInterviewQuestion(interviewQuestion);
                            interviewAnswer.setAnswer(questionnaireAnswer.getAnswer());
                            interviewAnswer.setWeight(questionnaireAnswer.getWeight());
                            interviewAnswer.setOrder(questionnaireAnswer.getAnswer().getOrder());

                            commitInstances.add(interviewAnswer);
                        }

                        switch (interviewQuestion.getQuestion().getAnswerType()) {
                            case DATE:
                            case TEXT:
                            case NUMBER:
                                InterviewAnswer answer = metadata.create(InterviewAnswer.class);
                                answer.setInterviewQuestion(interviewQuestion);
                                commitInstances.add(answer);
                                break;
                        }
                    }
                }
            }

            dataManager.commit(new CommitContext(commitInstances));
        } else {
            List<Entity> commitInstances = new ArrayList<>();

            List<RcQuestionnaire> questionnaires = commonService.getEntities(RcQuestionnaire.class,
                    "select e " +
                            "    from tsadv$RcQuestionnaire e" +
                            "   where e.id in :questionnaireIds  ",
                    Collections.singletonMap("questionnaireIds", questionnairesDs.getItems().stream().map(iq -> iq.getQuestionnaire().getId()).collect(Collectors.toList())),
                    "rcQuestionnaire.view");

            for (Interview interview : interviewsDs.getItems()) {
                if (!interview.getRequisitionHiringStep().equals(getItem().getRequisitionHiringStep()))
                    interview.setRequisitionHiringStep(getItem().getRequisitionHiringStep());
                if (!interview.getInterviewDate().equals(getItem().getInterviewDate()))
                    interview.setInterviewDate(getItem().getInterviewDate());
                if (!interview.getTimeFrom().equals(getItem().getTimeFrom()))
                    interview.setTimeFrom(getItem().getTimeFrom());
                if (!interview.getTimeTo().equals(getItem().getTimeTo()))
                    interview.setTimeTo(getItem().getTimeTo());
                if (!interview.getMainInterviewerPersonGroup().equals(getItem().getMainInterviewerPersonGroup()))
                    interview.setMainInterviewerPersonGroup(getItem().getMainInterviewerPersonGroup());
                if (!interview.getPlace().equals(getItem().getPlace()))
                    interview.setPlace(getItem().getPlace());

                commitInstances.add(interview);

                List<InterviewDetail> interviewDetails = commonService.getEntities(InterviewDetail.class,
                        "select e from tsadv$InterviewDetail e where e.interview.id = :interviewId",
                        Collections.singletonMap("interviewId", interview.getId()),
                        "interviewDetail.view");

                interviewDetails.stream().forEach(id -> {
                    if (interviewDetailsDs.getItems().stream().noneMatch(gid -> gid.getInterviewerPersonGroup().getId().equals(id.getInterviewerPersonGroup().getId())))
                        dataManager.remove(id);
                });

                interviewDetailsDs.getItems().forEach(gid -> {
                    if (interviewDetails.stream().noneMatch(id -> id.getInterviewerPersonGroup().getId().equals(gid.getInterviewerPersonGroup().getId()))) {
                        InterviewDetail interviewDetail = metadata.create(InterviewDetail.class);
                        interviewDetail.setInterview(interview);
                        interviewDetail.setInterviewerPersonGroup(gid.getInterviewerPersonGroup());
                        commitInstances.add(interviewDetail);
                    }
                });

                List<InterviewQuestionnaire> interviewQuestionnaires = commonService.getEntities(InterviewQuestionnaire.class,
                        "select e from tsadv$InterviewQuestionnaire e where e.interview.id = :interviewId",
                        Collections.singletonMap("interviewId", interview.getId()),
                        "interviewQuestionnaire.view");

                interviewQuestionnaires.stream().forEach(iq -> {
                    if (questionnairesDs.getItems().stream().noneMatch(giq -> giq.getQuestionnaire().getId().equals(iq.getQuestionnaire().getId()))) {
                        dataManager.remove(iq);
                    }
                });

                questionnaires.forEach(q -> {
                    if (interviewQuestionnaires.stream().noneMatch(iq -> iq.getQuestionnaire().getId().equals(q.getId()))) {
                        InterviewQuestionnaire interviewQuestionnaire = metadata.create(InterviewQuestionnaire.class);
                        interviewQuestionnaire.setInterview(interview);
                        interviewQuestionnaire.setQuestionnaire(q);

                        commitInstances.add(interviewQuestionnaire);

                        for (RcQuestionnaireQuestion questionnaireQuestion : q.getQuestions()) {
                            InterviewQuestion interviewQuestion = metadata.create(InterviewQuestion.class);
                            interviewQuestion.setInterviewQuestionnaire(interviewQuestionnaire);
                            interviewQuestion.setQuestion(questionnaireQuestion.getQuestion());
                            interviewQuestion.setOrder(questionnaireQuestion.getOrder());

                            commitInstances.add(interviewQuestion);

                            for (RcQuestionnaireAnswer questionnaireAnswer : questionnaireQuestion.getAnswers()) {
                                InterviewAnswer interviewAnswer = metadata.create(InterviewAnswer.class);
                                interviewAnswer.setInterviewQuestion(interviewQuestion);
                                interviewAnswer.setAnswer(questionnaireAnswer.getAnswer());
                                interviewAnswer.setWeight(questionnaireAnswer.getWeight());
                                interviewAnswer.setOrder(questionnaireAnswer.getAnswer().getOrder());

                                commitInstances.add(interviewAnswer);
                            }

                            switch (interviewQuestion.getQuestion().getAnswerType()) {
                                case DATE:
                                case TEXT:
                                case NUMBER:
                                    InterviewAnswer answer = metadata.create(InterviewAnswer.class);
                                    answer.setInterviewQuestion(interviewQuestion);
                                    commitInstances.add(answer);
                                    break;
                            }
                        }
                    }
                });
            }

            dataManager.commit(new CommitContext(commitInstances));
        }

//        if (committed && !close) {
//
//        }
        if (isOkAproveOrPlaned == 1) {
            frame.showNotification(
                    messages.getMessage(messages.getMainMessagePack(), "requisition.Interview.Saved.Successfully"),
                    NotificationType.TRAY);
        }
        return true;
    }

    public void removeQuestionnaires() {
        Table<InterviewQuestionnaire> questionnairesTable = (Table<InterviewQuestionnaire>) getComponent("questionnairesTable");
        showOptionDialog(getMessage("removeDialog.confirm.title"), getMessage("removeDialog.confirm.text"),
                MessageType.CONFIRMATION,
                new Action[]{
                        new DialogAction(DialogAction.Type.YES) {
                            @Override
                            public void actionPerform(Component component) {
                                Set<InterviewQuestionnaire> selected = questionnairesTable.getSelected();
                                for (InterviewQuestionnaire item : selected) {
                                    questionnairesDs.removeItem(item);
                                }
                            }
                        },
                        new DialogAction(DialogAction.Type.NO)
                });

    }

    public void sendForApproval() {
        if (validateAll()) {
            getItem().setInterviewStatus(InterviewStatus.ON_APPROVAL);
            this.commit();
            interviewsDs.refresh();
            interviewsDs.getItems().forEach(i -> {
//                isApprovalNotification=true;
                sendNotification("interview.candidate.approve.notification", i);
            });
            isOkAproveOrPlaned = 2;
            showNotification(messages.getMessage("kz.uco.tsadv.web", "aproveInterview"),
                    NotificationType.TRAY);
            this.close(COMMIT_ACTION_ID);
        }
    }

    public void planInterview() {
        if (validateAll()) {
            getItem().setInterviewStatus(InterviewStatus.PLANNED);
            this.commit();

            List<Entity> commitInstances = new ArrayList<>();
            interviewsDs.refresh();
            interviewsDs.getItems().forEach(i -> {
                if (i.getInterviewStatus() == InterviewStatus.DRAFT) {
                    i.setInterviewStatus(InterviewStatus.PLANNED);
                    commitInstances.add(i);
                }

                if (i.getInterviewStatus() == InterviewStatus.PLANNED) {
                    JobRequest jobRequest = dataManager.reload(i.getJobRequest(), "jobRequest.full");
                    jobRequest.setRequestStatus(JobRequestStatus.INTERVIEW);
                    commitInstances.add(jobRequest);
                }
            });
            dataManager.commit(new CommitContext(commitInstances));
//            isApprovalNotification=false;
            isOkAproveOrPlaned = 3;
            sendNotification("interview.planned.mainInterviewer.notification", getItem());
            showNotification(messages.getMessage("kz.uco.tsadv.web",
                    "interviewEdit.candidateUser.saved.successfully"), NotificationType.TRAY);
            this.close(COMMIT_ACTION_ID);
        }
    }


    private void sendNotification(String notificationCode, Interview interview) {
        Map<String, Object> params = new HashMap<>();
        List<UserExt> userList = null;
        SimpleDateFormat tf = new SimpleDateFormat("HH:mm");

        switch (notificationCode) {
            case "interview.planned.mainInterviewer.notification":
                userList = commonService.getEntities(UserExt.class,
                        "select e " +
                                "    from tsadv$UserExt e " +
                                "   where e.personGroup.id = :mainInterviewerPersonGroupId ",
                        Collections.singletonMap("mainInterviewerPersonGroupId", interview.getMainInterviewerPersonGroup().getId()),
                        "user.browse");
                params.put("interview", getItem());
                params.put("timeFrom", tf.format(interview.getTimeFrom()));
                params.put("timeTo", tf.format(interview.getTimeTo()));
                params.put("interviewDate", new SimpleDateFormat("dd.MM.yyyy").format(getItem().getInterviewDate()));
                params.put("personFullNameRu", interview.getMainInterviewerPersonGroup() != null ? interview.getMainInterviewerPersonGroup().getPerson().getFullName() : "");
                break;
            case "interview.candidate.approve.notification":
                userList = commonService.getEntities(UserExt.class,
                        "select e " +
                                "    from tsadv$UserExt e " +
                                "   where e.personGroup.id = :candidatePersonGroupId",
                        Collections.singletonMap("candidatePersonGroupId", interview.getJobRequest().getCandidatePersonGroup().getId()),
                        "user.browse");
                if (isSystemNotificationAllow) {
                    if (userList == null || userList.isEmpty()) {
                        showNotification(getMessage("InterviewEdit.candidateUser.notExists"));
                    } else if (userList.get(0).getEmail() == null && userList.get(0).getMobilePhone() == null)
                        showNotification(getMessage("InterviewEdit.candidateUser.noEmailPhone"));
                }

                try {
                    PersonGroupExt candidatePersonGroup = interview.getJobRequest().getCandidatePersonGroup();

                    String personNameEn = candidatePersonGroup.getPersonFirstLastNameLatin();
                    String personNameRu = candidatePersonGroup.getFullName();

                    Job job = interview.getJobRequest().getRequisition().getJobGroup().getJob();

                    params.put("personFullNameRu", personNameRu);
                    params.put("personFullNameKz", personNameRu);
                    params.put("personFullNameEn", personNameEn);

                    params.put("positionNameRu", job.getJobNameLang1());
                    params.put("positionNameKz", job.getJobNameLang2());
                    params.put("positionNameEn", job.getJobNameLang3());

                    params.put("interviewDate", new SimpleDateFormat("dd.MM.yyyy").format(getItem().getInterviewDate()));
                    params.put("interview", getItem());
                    params.put("timeFrom", tf.format(interview.getTimeFrom()));
                    params.put("timeTo", tf.format(interview.getTimeTo()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
        }

        if (userList != null && !userList.isEmpty()) {
            params.put("user", userList.get(0));
            notificationService.sendParametrizedNotification(notificationCode, userList.get(0), params);
            activityService.createActivity(
                    userList.get(0),
                    employeeService.getSystemUser(),
                    commonService.getEntity(ActivityType.class, "NOTIFICATION"),
                    StatusEnum.active,
                    "descripiton",
                    null,
                    new Date(),
                    null,
                    null,
                    null,
                    notificationCode,
                    params);
        }

    }

    private void refuseCandidateWithDialog(JobRequest jobRequest) {
        showOptionDialog(
                getMessage("msg.warning.title"),
                String.format(getMessage("questionForJobRequestStatus"), jobRequest.getCandidatePersonGroup().getFullName()),
                MessageType.CONFIRMATION,
                new Action[]{
                        new DialogAction(DialogAction.Type.YES) {
                            @Override
                            public void actionPerform(Component component) {
                                jobRequest.setRequestStatus(JobRequestStatus.REJECTED);
                                dataManager.commit(jobRequest);
                                showNotification(String.format(getMessage("notifyJobrequestRejected"), jobRequest.getCandidatePersonGroup().getFullName()));
                                fillinterviewsDs((RequisitionHiringStep) requisitionHiringStepField.getValue());

                            }
                        },
                        new DialogAction(DialogAction.Type.CANCEL)
                });

    }


}