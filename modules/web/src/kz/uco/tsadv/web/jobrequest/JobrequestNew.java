package kz.uco.tsadv.web.jobrequest;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.gui.components.renderers.WebComponentRenderer;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.exception.ReportingException;
import com.haulmont.reports.gui.ReportGuiManager;
import kz.uco.base.entity.extend.UserExt;
import kz.uco.base.service.NotificationService;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.administration.enums.RuleStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicPersonType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.Job;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.recruitment.config.RecruitmentConfig;
import kz.uco.tsadv.modules.recruitment.enums.JobRequestStatus;
import kz.uco.tsadv.modules.recruitment.model.*;
import kz.uco.tsadv.service.BusinessRuleService;
import kz.uco.tsadv.web.modules.recruitment.jobrequest.JobRequestFastEdit;

import javax.inject.Inject;
import javax.inject.Named;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class JobrequestNew extends AbstractWindow {
    @Inject
    protected Filter jobRequestsFilter;
    @Inject
    protected DataGrid<JobRequest> jobRequestsDataGrid;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected Metadata metadata;
    protected Map<String, Object> param;
    protected Report report;
    protected Report reportWithoutPhoto;
    protected ReportGuiManager reportGuiManager = AppBeans.get(ReportGuiManager.class);
    protected Requisition requisition;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected CommonService commonService;
    @Inject
    protected CollectionDatasource<JobRequest, UUID> jobRequestsDs;
    @Inject
    protected CssLayout progressBar;

    @Inject
    protected Button createInterview;
    @Inject
    protected Button createOffer;

    @Named("actions.jobRequestsEditAction")
    protected Action actionsJobRequestsEditAction;
    @Named("actions.jobRequestsRemoveAction")
    protected Action actionsJobRequestsRemoveAction;
    @Named("actions.refuseCandidate")
    protected Action actionsRefuseCandidate;
    @Named("actions.resumeBtn")
    protected Action actionsResumeBtn;
    @Inject
    protected BusinessRuleService businessRuleService;

    @Named("jobRequestsDataGrid.create")
    protected CreateAction jobRequestsDataGridCreate;
    @Named("jobRequestsDataGrid.edit")
    protected Action jobRequestsDataGridEdit;
    @Named("jobRequestsDataGrid.remove")
    protected Action jobRequestsDataGridRemove;
    @Inject
    protected NotificationService notificationService;
    protected List<String> queryFilter = new ArrayList<>();
    protected String clearQuery;
    @Inject
    private Configuration configuration;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        requisition=null;
        param = params;
        if (params.containsKey("requisitionId")) {
            requisition = (Requisition) params.get("requisitionId");
        }
        if (requisition!=null){
            requisition = dataManager.reload(this.requisition, "requisition.view");
        }
        jobRequestsDs.addItemChangeListener(e -> {
            jobRequestsDsItemChangeListener(e);
        });
        loadProgressBar();
    }

    protected void jobRequestsDsItemChangeListener(Datasource.ItemChangeEvent<JobRequest> e) {
        boolean enable = jobRequestsDataGrid.getSingleSelected() != null;
        createOffer.setEnabled(enable);
        actionsJobRequestsEditAction.setEnabled(enable);
        actionsJobRequestsRemoveAction.setEnabled(enable);
        actionsRefuseCandidate.setEnabled(enable);
        actionsResumeBtn.setEnabled(enable);

        createInterview.setEnabled(enable && !(jobRequestsDataGrid.getSingleSelected()
                .getRequestStatus() != null) ? jobRequestsDataGrid.getSingleSelected()
                .getRequestStatus().equals(JobRequestStatus.DRAFT) : true);
    }

    @Override
    public void ready() {
        super.ready();
        RecruitmentConfig recruitmentConfig = configuration.getConfig(RecruitmentConfig.class);
        jobRequestsDataGridCreate.setInitialValues(ParamsMap.of("requisition",requisition,
                "requestStatus", (recruitmentConfig.getJobReuestDefaultDraft() ?
                        JobRequestStatus.DRAFT :
                        JobRequestStatus.ON_APPROVAL)));
        DataGrid.Column lastHiringStep = jobRequestsDataGrid.addGeneratedColumn("lastHiringStep",
                new DataGrid.ColumnGenerator<JobRequest, Label>() {

                    @Override
                    public Label getValue(DataGrid.ColumnGeneratorEvent event) {
                        Label label = componentsFactory.createComponent(Label.class);
                        JobRequest item = (JobRequest) event.getItem();
                        List<Interview> interviews = item.getInterviews();
                        int max = -1;
                        String stepName = "";
                        for (Interview interview : interviews) {
                            if (interview != null && interview.getRequisitionHiringStep() != null
                                    && interview.getInterviewStatus() != null
                                    && interview.getRequisitionHiringStep().getHiringStep() != null
                                    && interview.getRequisitionHiringStep().getOrder() != null
                                    && interview.getRequisitionHiringStep().getHiringStep().getStepName() != null
                                    && interview.getRequisitionHiringStep().getOrder() > max) {
                                max = interview.getRequisitionHiringStep().getOrder();
                                stepName = interview.getRequisitionHiringStep().getHiringStep().getStepName();
                            }
                        }
                        label.setValue(stepName);
                        return label;
                    }

                    @Override
                    public Class getType() {
                        return Label.class;
                    }
                });
        lastHiringStep.setCaption(getMessage("lastHiringStep"));
        lastHiringStep.setRenderer(new WebComponentRenderer());

        DataGrid.Column fullName = jobRequestsDataGrid.addGeneratedColumn("fullName",
                new DataGrid.ColumnGenerator<JobRequest, LinkButton>() {

                    @Override
                    public LinkButton getValue(DataGrid.ColumnGeneratorEvent event) {
                        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
                        JobRequest item = (JobRequest) event.getItem();
                        linkButton.setAction(new BaseAction("personCard") {
                            @Override
                            public void actionPerform(Component component) {
                                super.actionPerform(component);
                                if (event.getItem() != null
                                        && ((JobRequest) event.getItem()).getCandidatePersonGroup() != null
                                        && ((JobRequest) event.getItem()).getCandidatePersonGroup().getPerson() != null) {
                                    openEditor("base$Person.candidate",
                                            ((JobRequest) event.getItem()).getCandidatePersonGroup().getPerson(),
                                            WindowManager.OpenType.THIS_TAB, new HashMap<>());
                                }
                            }
                        });
                        linkButton.setCaption(item.getCandidatePersonGroup().getFullName());
                        return linkButton;
                    }

                    @Override
                    public Class getType() {
                        return LinkButton.class;
                    }
                });
        fullName.setCaption(getMessage("FIO"));
        fullName.setRenderer(new WebComponentRenderer());
        clearQuery = jobRequestsDs.getQuery();
    }

    protected void loadProgressBar() {
        progressBar.removeAll();

        List<RequisitionHiringStep> requisitionHiringSteps = commonService
                .getEntities(RequisitionHiringStep.class,
                        "select e from tsadv$RequisitionHiringStep e " +
                                "where e.requisition.id = :requisitionId",
                        ParamsMap.of("requisitionId", requisition),
                        "requisition-hiring-step-for-filter");
        if (requisitionHiringSteps != null && !requisitionHiringSteps.isEmpty()) {
            requisitionHiringSteps.sort((o1, o2) -> {
                return o1.getOrder() > o2.getOrder() ? 1 : -1;
            });
            for (RequisitionHiringStep requisitionHiringStep : requisitionHiringSteps) {
                progressBar.add(createItem(requisitionHiringStep));
            }
        }
    }

    protected Component createItem(RequisitionHiringStep requisitionHiringStep) {


        CssLayout wrapper = componentsFactory.createComponent(CssLayout.class);
        Label label = componentsFactory.createComponent(Label.class);
        label.setValue(requisitionHiringStep.getOrder().toString() + " " + requisitionHiringStep.getHiringStep().getStepName());
        label.addStyleName("progress-step-label");
        wrapper.addStyleName("progress-step progress-step-uco");
        wrapper.add(label);

//        wrapper.addLayoutClickListener(new LayoutClickListener() {
//            @Override
//            public void layoutClick(LayoutClickNotifier.LayoutClickEvent event) {
//                if (!event.getMouseEventDetails().isDoubleClick()) {
//                    boolean clicked = wrapper.getStyleName().contains("progress-step-active-filter");
//                    if (clicked) {
//                        wrapper.removeStyleName("progress-step-active-filter");
//
//                        if (queryFilter.contains(requisitionHiringStep.getId().toString())) {
//                            queryFilter.remove(requisitionHiringStep.getId().toString());
//                        }
//                    } else {
//                        queryFilter.add(requisitionHiringStep.getId().toString());
//                        wrapper.addStyleName("progress-step-active-filter");
//                    }
//
//                    reloadTable();
//                }
//            }
//        });

        return wrapper;
    }

    protected void reloadTable() {
        if (queryFilter.isEmpty()) {
            jobRequestsDs.setQuery(clearQuery);
            jobRequestsFilter.apply(false);
        } else {
            String qwery = "select e from tsadv$JobRequest e";
            String filter = "";
            int i = 1;
            for (String s : queryFilter) {
                qwery = qwery + ", in(e.interviews) i" + Integer.toString(i);
                filter = filter + "and i" + Integer.toString(i) + ".requisitionHiringStep.id = '" + s + "' ";
                i++;
            }
            qwery=qwery+" where e.requisition.id = :param$requisitionId " +filter;
            jobRequestsDs.setQuery(qwery);
            jobRequestsFilter.apply(false);
        }
    }

    public void onFastCreateClick() {
        JobRequest jobRequest = metadata.create(JobRequest.class);
        PersonGroupExt personGroup = metadata.create(PersonGroupExt.class);
        PersonExt person = metadata.create(PersonExt.class);
        List<Interview> interviewList = new ArrayList<>();

        jobRequest.setRequisition(requisition);
        personGroup.setPerson(person);
        person.setType(commonService.getEntity(DicPersonType.class, "CANDIDATE"));
        person.setStartDate(new Date());
        try {
            person.setEndDate(new SimpleDateFormat("dd.MM.yyyy").parse("31.12.9999"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        person.setGroup(personGroup);
        jobRequest.setCandidatePersonGroup(personGroup);
        jobRequest.setRequestDate(new Date());
        requisition.getHiringSteps().forEach(requisitionHiringStep -> {
            if (requisitionHiringStep.getRequired()) {
                Interview interview = metadata.create(Interview.class);

                interview.setJobRequest(jobRequest);
                interview.setInterviewDate(CommonUtils.getSystemDate());
                interview.setRequisitionHiringStep(requisitionHiringStep);
                interviewList.add(interview);
            }
        });
        jobRequest.setInterviews(interviewList);
        Map<String, Object> map = new HashMap<>();
        List<UUID> excludedPersonGroupList = new ArrayList();


        for (JobRequest jobRequest1 : jobRequestsDs.getItems()) {
            excludedPersonGroupList.add(jobRequest1.getCandidatePersonGroup().getId());
        }
        map.put("excludedPersons", excludedPersonGroupList);

        JobRequestFastEdit jobRequestFastEdit = (JobRequestFastEdit) openEditor("tsadv$JobRequestFast.edit", jobRequest, WindowManager.OpenType.THIS_TAB, map);
        jobRequestFastEdit.addCloseListener(actionId -> {
            reloadTable();
        });
    }

    public void createInterview() {
        JobRequest jobRequest = jobRequestsDs.getItem();
        createInterview(jobRequest);
    }

    protected void createInterview(JobRequest jobRequest) {
        Interview interview = metadata.create(Interview.class);
        try {
            RequisitionHiringStep requisitionHiringStep = getNextRequisitionHiringStep(jobRequest);

            if (requisitionHiringStep == null)
                showNotification(getMessage("RequisitionJobRequest.noAvailableHiringSteps"));
            else {
                interview.setJobRequest(jobRequest);
                interview.setRequisitionHiringStep(requisitionHiringStep);
                AbstractEditor<Interview> interviewEditor = openEditor("tsadv$Interview.edit", interview, WindowManager.OpenType.THIS_TAB);
                interviewEditor.addCloseListener(e -> {
                    reloadTable();
                });
            }
        } catch (Exception e) {
            showNotification(e.getMessage());
            e.printStackTrace();
        }
    }

    protected RequisitionHiringStep getNextRequisitionHiringStep(JobRequest jobRequest) throws Exception {
        RequisitionHiringStep requisitionHiringStep = null;
        Map<String, Object> params = new HashMap<>();

        params.put("jobRequestId", jobRequest.getId());

        List<RequisitionHiringStep> incompleteHiringSteps = commonService.getEntities(RequisitionHiringStep.class,
                "select e " +
                        " from tsadv$RequisitionHiringStep e, tsadv$Interview i" +
                        " where i.jobRequest.id = :jobRequestId" +
                        " and e.id = i.requisitionHiringStep.id " +
                        " and i.interviewStatus in (10, 20, 30)",
                params,
                View.LOCAL);

        if (incompleteHiringSteps != null && !incompleteHiringSteps.isEmpty() && businessRuleService.getRuleStatus("interviewCreate") != RuleStatus.DISABLE)
            throw new Exception(messages.getMainMessage("RequisitionJobRequest.incompleteHiringStep"));

        List<RequisitionHiringStep> failedHiringSteps = commonService.getEntities(RequisitionHiringStep.class,
                "select e " +
                        " from tsadv$RequisitionHiringStep e, tsadv$Interview i" +
                        " where i.jobRequest.id = :jobRequestId" +
                        " and e.id = i.requisitionHiringStep.id " +
                        " and i.interviewStatus = 60",
                params,
                View.LOCAL);

        if (failedHiringSteps != null && !failedHiringSteps.isEmpty())
            throw new Exception(messages.getMainMessage("RequisitionJobRequest.failedHiringStep"));

        params.put("requisitionId", jobRequest.getRequisition().getId());
        List<RequisitionHiringStep> availableSteps = commonService.getEntities(RequisitionHiringStep.class,
                "select e " +
                        " from tsadv$RequisitionHiringStep e " +
                        " where e.requisition.id = :requisitionId " +
                        " and not exists (select 1 " +
                        " from tsadv$Interview i " +
                        " where i.jobRequest.id = :jobRequestId " +
                        " and i.requisitionHiringStep.id = e.id " +
                        " and i.interviewStatus <> 50) " +
                        " and not exists (select 1 " +
                        " from tsadv$Interview i, tsadv$RequisitionHiringStep hs" +
                        " where i.jobRequest.id = :jobRequestId" +
                        " and hs.id = i.requisitionHiringStep.id " +
                        " and hs.order < e.order " +
                        " and i.interviewStatus in (10, 20, 30, 60, 70)) " +
                        " order by e.order ",
                params,
                "requisitionHiringStep.view");
        if (availableSteps != null && !availableSteps.isEmpty())
            requisitionHiringStep = availableSteps.get(0);
        else
            throw new Exception(messages.getMainMessage("RequisitionJobRequest.allStepsCompleted"));

        return requisitionHiringStep;
    }

    public void searchCandidate() {
    }


    public void onCreateOfferButtonClick() {
        Offer offer = metadata.create(Offer.class);
        offer.setJobRequest(jobRequestsDs.getItem());
        AbstractEditor abstractEditor = openEditor("tsadv$Offer.edit", offer, WindowManager.OpenType.THIS_TAB);
        abstractEditor.addCloseListener(actionId -> {
            reloadTable();
        });
    }

    public void refresh() {
    }

    public void editJobRequest() {
        jobRequestsDataGridEdit.actionPerform(null);
    }

    public void removeJobRequest() {
        jobRequestsDataGridRemove.actionPerform(null);
    }


    public void refuseCandidate() {
        JobRequest jobRequest = jobRequestsDs.getItem();
        refuseCandidateWithDialog(jobRequest);
    }


    protected void refuseCandidateWithDialog(JobRequest jobRequest) {
        showOptionDialog(
                getMessage("msg.warning.title"),
                getMessage("renouncement.confirm.text"),
                MessageType.CONFIRMATION,
                new Action[]{
                        new DialogAction(DialogAction.Type.YES) {
                            @Override
                            public void actionPerform(Component component) {
                                jobRequest.setRequestStatus(JobRequestStatus.REJECTED);
                                jobRequestsDs.modifyItem(jobRequest);
                                jobRequestsDs.commit();
                                jobRequestsDs.refresh();

                                sendNotificationForCandidate(jobRequest.getCandidatePersonGroup(),
                                        "requisition.notify.application.candidate.rejected",
                                        jobRequestsDs.getItem().getRequisition());
                            }
                        },
                        new DialogAction(DialogAction.Type.CANCEL)
                });

    }

    protected void sendNotificationForCandidate(PersonGroupExt candidatePersonGroup, String
            templateCode, Requisition requisition) {
        try {
            UserExt userExt = getUserExt(candidatePersonGroup.getId());

            if (userExt != null) {
                Map<String, Object> paramsMap = new HashMap<>();

                String personNameEn = candidatePersonGroup.getPersonFirstLastNameLatin();
                String personNameRu = candidatePersonGroup.getFullName();

                Job job = requisition.getJobGroup().getJob();

                paramsMap.put("personFullNameEn", personNameEn);
                paramsMap.put("personFullNameKz", personNameRu);
                paramsMap.put("personFullNameRu", personNameRu);

                paramsMap.put("requisitionNameRu", requisition.getNameForSiteLang1());
                paramsMap.put("requisitionNameKz", requisition.getNameForSiteLang2());
                paramsMap.put("requisitionNameEn", requisition.getNameForSiteLang3());

                paramsMap.put("positionNameRu", job.getJobNameLang1());
                paramsMap.put("positionNameKz", job.getJobNameLang2());
                paramsMap.put("positionNameEn", job.getJobNameLang3());

                paramsMap.put("code", requisition.getCode());

                notificationService.sendParametrizedNotification(
                        templateCode,
                        userExt,
                        paramsMap);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected UserExt getUserExt(UUID personGroupId) {
        LoadContext<UserExt> loadContext = LoadContext.create(UserExt.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from base$UserExt e " +
                        " join tsadv$UserExtPersonGroup u on u.userExt.id = e.id " +
                        "where u.personGroup.id = :pgId");
        query.setParameter("pgId", personGroupId);
        loadContext.setQuery(query);
        loadContext.setView("user.browse");
        return dataManager.load(loadContext);
    }

    public void createResume() {
        printResume(jobRequestsDataGrid.getSingleSelected().getCandidatePersonGroup());
    }

    protected void printResume(PersonGroupExt personGroupExt) {
        report = commonService.getEntity(Report.class, "RESUME");
        reportWithoutPhoto = commonService.getEntity(Report.class, "RESUME_WHITHOUT_IMAGE");
        try {

            if (report != null) {
                reportGuiManager.printReport(report,
                        ParamsMap.of("pPersonGroupId", personGroupExt));
            }

        } catch (ReportingException reportingException) {
            if (reportWithoutPhoto != null) {
                reportGuiManager.printReport(reportWithoutPhoto,
                        ParamsMap.of("pPersonGroupId", personGroupExt));
            }
        }
    }
}