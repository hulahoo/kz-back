package kz.uco.tsadv.web.modules.recruitment.requisition.frames;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.entity.KeyValueEntity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.gui.components.WebRowsCount;
import com.haulmont.cuba.web.gui.components.renderers.WebComponentRenderer;
import com.haulmont.cuba.web.gui.components.renderers.WebNumberRenderer;
import com.haulmont.cuba.web.widgets.CubaRowsCount;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.exception.ReportingException;
import com.haulmont.reports.gui.ReportGuiManager;
import com.vaadin.server.Page;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.common.WebCommonUtils;
import kz.uco.base.entity.extend.UserExt;
import kz.uco.base.service.NotificationService;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.filter.StoreStyleFilter;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.global.entity.UserExtPersonGroup;
import kz.uco.tsadv.modules.administration.enums.RuleStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicAddressType;
import kz.uco.tsadv.modules.personal.dictionary.DicPersonType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.modules.recruitment.config.ButtonOfferConfig;
import kz.uco.tsadv.modules.recruitment.config.RecruitmentConfig;
import kz.uco.tsadv.modules.recruitment.dictionary.DicJobRequestReason;
import kz.uco.tsadv.modules.recruitment.enums.InterviewStatus;
import kz.uco.tsadv.modules.recruitment.enums.JobRequestStatus;
import kz.uco.tsadv.modules.recruitment.enums.RcAnswerType;
import kz.uco.tsadv.modules.recruitment.enums.RequisitionStatus;
import kz.uco.tsadv.modules.recruitment.model.*;
import kz.uco.tsadv.service.BusinessRuleService;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.web.modules.recruitment.jobrequest.JobRequestFastEdit;
import kz.uco.tsadv.web.modules.recruitment.jobrequest.JobRequestFastInterviewEdit;
import kz.uco.tsadv.web.modules.recruitment.requisition.config.JobRequestRemoveCandidateConfig;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author veronika.buksha
 */
public class RequisitionJobRequest extends AbstractFrame {
    protected static SimpleDateFormat dateFormat;
    protected static SimpleDateFormat periodFormat;
    private boolean ignoreCheckBoxValueCHange;
    protected List<UserExtPersonGroup> userExtPersonGroup;
    protected List<Offer> offers;
    protected List<JobRequest> allJobRequests;
    protected List<InterviewQuestionnaire> allInterviewQuestionnaire;
    protected Report report;
    protected Report reportWithoutPhoto;

    @Inject
    protected RecruitmentConfig recruitmentConfig;
    @Named("actions.resumeBtn")
    protected Action actionsResumeBtn;
    protected ReportGuiManager reportGuiManager = AppBeans.get(ReportGuiManager.class);
    @Inject
    protected BusinessRuleService businessRuleService;
    protected JobRequest currentCardJobRequest;
    @Inject
    protected ExportDisplay exportDisplay;

    @Inject
    protected UserSessionSource userSessionSource;
    public CollectionDatasource<JobRequest, UUID> jobRequestsDs;
    public CollectionDatasource<JobRequestHistory, UUID> jobRequestHistoryDs;
    public CollectionDatasource<Offer, UUID> offerDs;
    protected CollectionDatasource<Interview, UUID> interviewsDs;
    protected CollectionDatasource<RequisitionQuestionnaire, UUID> questionnairesDs;
    protected Datasource<Requisition> requisitionDs;
    protected ComponentsFactory componentsFactory = AppBeans.get(ComponentsFactory.class);
    protected CommonService commonService = AppBeans.get(CommonService.class);
    protected Messages messages = AppBeans.get(Messages.class);

    protected SimpleDateFormat periodFormatEnd;
    protected SimpleDateFormat periodFormatEndYear;

    @Inject
    protected DataSupplier dataSupplier;
    @Named("jobRequestsTable.create")
    protected CreateAction jobRequestsTableCreate;

    @Inject
    protected Button fastCreateButton;

    @Named("jobRequestsTable.edit")
    protected EditAction jobRequestsTableEdit;

    @Named("jobRequestsTable.remove")
    protected RemoveAction jobRequestsTableRemove;

    @Named("jobRequestsTable.createInterview")
    protected Action jobRequestsTableCreateInterview;

    @Named("jobRequestsTable.excludeFromReserve")
    protected Action jobRequestsTableExcludeFromReserve;
    @Named("jobRequestsTable.sendToReserve")
    protected Action jobRequestsTableSendToReserve;
    @Named("jobRequestsTable.createGroupInterview")
    protected Action jobRequestsTableCreateGroupInterview;
    @Named("actions.jobRequestsEditAction")
    protected Action actionsJobRequestsEditAction;
    @Named("actions.jobRequestsRemoveAction")
    protected Action actionsJobRequestsRemoveAction;
    @Named("actions.excludeFromReserve")
    protected Action actionsExcludeFromReserve;
    @Named("actions.hire")
    protected Action actionsHire;
    @Named("actions.sendToReserve")
    protected Action actionsSendToReserve;
    @Named("actions.refuseCandidate")
    protected Action actionsRefuseCandidate;

    @Inject
    protected Button search;
    @Inject
    protected Metadata metadata;

    /*@Inject
    protected Table<JobRequest> jobRequestsTable;*/
    @Inject
    protected DataGrid<JobRequest> jobRequestsTable;

    @Inject
    protected PopupButton actions;
    @Inject
    protected Button fastCreateInterview;
    @Inject
    protected DataManager dataManager;

    protected Boolean isCard = false;
    @Inject
    protected VBoxLayout vbox1;
    @Inject
    protected EmployeeService employeeService;
    protected Table<JobRequest> table;
    protected HBoxLayout cardBoxLayout;
    @Inject
    protected PopupView popupView;
    @Inject
    protected GroupBoxLayout storeStyleFilterBox;
    @Inject
    protected CollectionDatasource<JobRequestCardSetting, UUID> jobRequestCardSettingsDs;
    protected Map<String, StoreStyleFilter.Element> filtersMap;
    protected StoreStyleFilter storeStyleFilter;
    @Inject
    protected OptionsGroup optionGroup;
    @Inject
    protected Button cardOrTable;
    @Inject
    protected ButtonOfferConfig buttonOfferConfig;
    @Inject
    protected Configuration configuration;
    @Inject
    protected NotificationService notificationService;
    @Inject
    protected Button createInterview;
    @Inject
    protected Button createGroupInterview;
    @Inject
    protected Button createOffer;
    @Inject
    protected FlowBoxLayout flowbox;
    @Inject
    protected HBoxLayout hbox;

    protected JobRequestRemoveCandidateConfig removeConfig;

    protected boolean isSelfService = false;
    protected Map<LookupField, LookupField> competenceMap = new HashMap<>();
    protected List<CheckBox> checkBoxList = new ArrayList<>();
    protected CollectionDatasource<KeyValueEntity, Object> cardFilterSettingsDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        ignoreCheckBoxValueCHange = false;
        RecruitmentConfig recruitmentConfig = configuration.getConfig(RecruitmentConfig.class);
        isSelfService = getComponent("isSelfService") != null ? true : false;
        periodFormat = new SimpleDateFormat("MMMM yyyy", userSessionSource.getLocale());
        dateFormat = new SimpleDateFormat("dd.MM.yyyy", userSessionSource.getLocale());
        periodFormatEnd = new SimpleDateFormat("MMMM", userSessionSource.getLocale());
        periodFormatEndYear = new SimpleDateFormat("yyyy", userSessionSource.getLocale());

        jobRequestsDs = (CollectionDatasource<JobRequest, UUID>) getDsContext().get("jobRequestsDs");
        cardFilterSettingsDs = (CollectionDatasource<KeyValueEntity, Object>) getDsContext().get("cardFilterSettingsDs");
        cardFilterSettingsDs.setAllowCommit(false);
        initCardFilterSettings();
        interviewsDs = (CollectionDatasource<Interview, UUID>) getDsContext().get("interviewsDs");
        questionnairesDs = (CollectionDatasource<RequisitionQuestionnaire, UUID>) getDsContext().get("questionnairesDs");
        requisitionDs = (Datasource<Requisition>) getDsContext().get("requisitionDs");
        jobRequestHistoryDs = (CollectionDatasource<JobRequestHistory, UUID>) getDsContext().get("jobRequestHistoryDs");

        jobRequestsTableCreate.setInitialValues(ParamsMap.of("requisition", requisitionDs.getItem(),
                "requestStatus", recruitmentConfig.getJobReuestDefaultDraft()
                        ? JobRequestStatus.DRAFT
                        : JobRequestStatus.ON_APPROVAL));
        jobRequestsTableCreate.setWindowParams(ParamsMap.of("parentFrameId", this.getFrame().getId()));
        jobRequestsTableEdit.setWindowParams(Collections.singletonMap("parentFrameId", this.getFrame().getId()));

        jobRequestsTableCreateInterview.setEnabled(false);
        createInterview.setEnabled(false);
        createOffer.setEnabled(false);
        actionsHire.setEnabled(false);
        actionsSendToReserve.setEnabled(false);
        actionsExcludeFromReserve.setEnabled(false);
        fastCreateInterview.setEnabled(false);

        RequisitionStatus requisitionStatus = requisitionDs.getItem().getRequisitionStatus();
        fastCreateButton.setEnabled(requisitionStatus.equals(RequisitionStatus.OPEN));
        jobRequestsTableCreate.setEnabled(requisitionStatus.equals(RequisitionStatus.OPEN));
        requisitionDs.addItemPropertyChangeListener(e -> {
            RequisitionStatus status = requisitionDs.getItem().getRequisitionStatus();
            jobRequestsTableCreate.setEnabled(requisitionStatus.equals(RequisitionStatus.OPEN));
            fastCreateButton.setEnabled(status.equals(RequisitionStatus.OPEN));
        });

        jobRequestsDs.addItemChangeListener(e -> {
            JobRequest jobRequest = e.getItem();
            boolean notNull = jobRequest != null, single = jobRequestsTable.getSelected().size() == 1;
            JobRequestStatus status = null;
            if (notNull) {
                status = jobRequest.getRequestStatus();
            }
            Boolean disableWhenDraft = recruitmentConfig.getdDisableWhenDraft() && JobRequestStatus.DRAFT.equals(status);

            jobRequestsTableCreateInterview.setEnabled(disableWhenDraft ? false : checkInviteInterviewAccess(e.getItem()));
            jobRequestsTableCreateGroupInterview.setEnabled(!jobRequestsTable.getSelected().isEmpty());
            createInterview.setEnabled(checkInviteInterviewAccess(e.getItem()));
            createOffer.setEnabled(disableWhenDraft ? false : (single && checkCreateOfferAccess(jobRequest)));

            fastCreateInterview.setEnabled(disableWhenDraft ? false : (notNull && status != JobRequestStatus.REJECTED));
            jobRequestsTableSendToReserve.setEnabled(notNull && status == JobRequestStatus.SELECTED && !jobRequest.getIsReserved());
            jobRequestsTableExcludeFromReserve.setEnabled(notNull && jobRequest.getIsReserved());

            actionsHire.setEnabled(disableWhenDraft || status == JobRequestStatus.REJECTED ? false : (checkHireActionAccess(jobRequest) && single));
            actionsSendToReserve.setEnabled(single && notNull && status == JobRequestStatus.SELECTED && !jobRequest.getIsReserved());
            actionsExcludeFromReserve.setEnabled(single && notNull && jobRequest.getIsReserved());
            actionsRefuseCandidate.setEnabled(notNull && single && status != JobRequestStatus.REJECTED);
            actionsJobRequestsEditAction.setEnabled(notNull);
            actionsJobRequestsRemoveAction.setEnabled(notNull);

            removeConfig = configuration.getConfig(JobRequestRemoveCandidateConfig.class);

            if (!removeConfig.getEnabled()) {
                if (notNull) {
                    UserExt userExt = getUserExt(jobRequest.getCandidatePersonGroup().getId());
                    Map<String, Object> paramsMap = new HashMap<>();
                    paramsMap.put("idJobRequest", jobRequest.getId());
                    Offer offer = commonService.getEntity(Offer.class, "select e from tsadv$Offer e where e.jobRequest.id = :idJobRequest", paramsMap, "offer.browse");
                    if (userExt != null) {
                        if (jobRequest.getCreatedBy() != null && (jobRequest.getCreatedBy().equals(userExt.getLogin()))) {
                            actionsJobRequestsRemoveAction.setEnabled(false);
                        } else {
                            actionsJobRequestsRemoveAction.setEnabled(true);
                        }
                    }
                    if (jobRequest.getInterview() != null || (offer != null && jobRequest.equals(offer.getJobRequest()))) {
                        actionsJobRequestsRemoveAction.setEnabled(false);
                    }
                }
            }
        });

        jobRequestsDs.addCollectionChangeListener(e -> {
            actionsExcludeFromReserve.setEnabled(e.getItems() != null && e.getItems().size() <= 1);
            loadLists();
        });

        initStoreStyleFilterMap();
        storeStyleFilter = StoreStyleFilter.init(jobRequestsDs, filtersMap);
        storeStyleFilterBox.add(storeStyleFilter.getFilterComponent());
        storeStyleFilter.search();

        storeStyleFilter.getRefreshButton().setAction(new BaseAction("refresh") {
            @Override
            public void actionPerform(Component component) {
                initStoreStyleFilterMap();
                storeStyleFilter.setFilterByMap(filtersMap);
                storeStyleFilter.refresh();
            }
        });

        WebRowsCount webRowsCount = componentsFactory.createComponent(WebRowsCount.class);
        webRowsCount.setDatasource(jobRequestsDs);
        webRowsCount.setSizeAuto();
        webRowsCount.setAlignment(Alignment.TOP_CENTER);
        CubaRowsCount cubaRowsCount = (CubaRowsCount) webRowsCount.getComponent();
        cubaRowsCount.getNextButton().addClickListener(event -> {
            nextButtonClickListener(event);
        });
        cubaRowsCount.getPrevButton().addClickListener(event -> {
            prevButtonClickListener(event);
        });
        cubaRowsCount.getFirstButton().addClickListener(event -> {
            firstButtonClickListener(event);
        });
        cubaRowsCount.getLastButton().addClickListener(event -> {
            lastButtonClickListener(event);
        });
        hbox.add(webRowsCount);
        hbox.setMargin(false, true, false, false);

        customizeJobRequestsTable();
        cardOrTable.setIcon("images/fa-card-id.png");

        setSpacing(true);
        toCard();
        jobRequestsTableCreate.setAfterCommitHandler(new CreateAction.AfterCommitHandler() {
            @Override
            public void handle(Entity entity) {
                customizeJobRequestsTable();
            }
        });

        jobRequestsTableEdit.setAfterCommitHandler(new EditAction.AfterCommitHandler() {
            @Override
            public void handle(Entity entity) {
                customizeJobRequestsTable();
            }
        });

        jobRequestsTableRemove.setAfterRemoveHandler(new RemoveAction.AfterRemoveHandler() {
            @Override
            public void handle(Set removedItems) {
                customizeJobRequestsTable();
            }
        });
        if (isSelfService) {
            jobRequestsTableCreate.setVisible(false);
            fastCreateButton.setVisible(false);
            createInterview.setVisible(false);
            createGroupInterview.setVisible(false);
            fastCreateInterview.setVisible(false);
            search.setVisible(false);
            createOffer.setVisible(false);
        }

        jobRequestsTable.addSelectionListener(event -> {
            jobRequestsTableSelectionListener(event);
        });
        report = commonService.getEntity(Report.class, "RESUME");
        reportWithoutPhoto = commonService.getEntity(Report.class, "RESUME_WHITHOUT_IMAGE");
    }

    protected void lastButtonClickListener(com.vaadin.ui.Button.ClickEvent event) {
        scrollToStart();
    }

    protected void firstButtonClickListener(com.vaadin.ui.Button.ClickEvent event) {
        scrollToStart();
    }

    protected void scrollToStart() {
        if (table != null) {
            Page.getCurrent()
                    .getJavaScript()
                    .execute("$('.c-card-table .v-scrollable, .v-table-body-wrapper, .v-table-body').animate({ " +
                            " scrollTop: 0}, 1000);");
            //table.scrollTo(jobRequestsDs.getItems().stream().findFirst().get());
        }
    }

    protected void prevButtonClickListener(com.vaadin.ui.Button.ClickEvent event) {
        scrollToStart();
    }

    protected void nextButtonClickListener(com.vaadin.ui.Button.ClickEvent event) {
        scrollToStart();
    }

    protected void initCardFilterSettings() {
        Collection<KeyValueEntity> items = cardFilterSettingsDs.getItems();
        for (CardBlockType cardBlockType : CardBlockType.values()) {
            if (!recruitmentConfig.getEnableCandidateRequirements() && cardBlockType.equals(CardBlockType.REQUIREMENTS)) {
                continue;
            }
            boolean isContains = false;
            for (KeyValueEntity item : items) {
                if (item.getId().toString().equals(cardBlockType.getMessageKey())) {
                    isContains = true;
                }
            }
            if (!isContains) {
                KeyValueEntity keyValueEntity = metadata.create(KeyValueEntity.class);
                keyValueEntity.setValue("cardBlockType", cardBlockType.getMessageKey());
                keyValueEntity.setValue("checked", cardBlockType.equals(CardBlockType.COMPETENCE) ? false : true);
                cardFilterSettingsDs.addItem(keyValueEntity);
            }
        }
    }

    protected void jobRequestsTableSelectionListener(DataGrid.SelectionEvent<JobRequest> event) {
        actionsResumeBtn.setEnabled(jobRequestsTable.getSelected().size() == 1);
    }

    protected boolean checkCreateOfferAccess(JobRequest jobRequest) {
        if (jobRequest != null) {
            Long offersCount = 0L;
            if (offers == null) {
                loadLists();
            } else {
                offersCount = offers.stream().filter(offer -> offer.getJobRequest().getId().equals(jobRequest.getId())).count();
            }

            if (offersCount == 0L && (buttonOfferConfig.getButtonOffer() || jobRequest.getRequestStatus().equals(JobRequestStatus.SELECTED))) {
                return true;
            }
        }
        return false;
    }

    public Component generateOrder(JobRequest entity) {
        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);

        linkButton.setCaption(String.valueOf(entity.getRequestStatus()));
        linkButton.setAction(new BaseAction("openCode") {
            @Override
            public void actionPerform(Component component) {
                openEditor("tsadv$Offer.edit", entity, WindowManager.OpenType.NEW_WINDOW, jobRequestsDs);
            }
        });
        return linkButton;
    }

    public void editJobRequest() {
        jobRequestsTableEdit.actionPerform(null);
    }

    public void removeJobRequest() {
        jobRequestsTableRemove.actionPerform(null);
    }

    protected Map<String, JobRequestCardSetting> initOptionsMap() { //TODO заполнять из настроек
        Map<String, JobRequestCardSetting> result = new HashMap<>();
        result.put("changeTime", metadata.create(JobRequestCardSetting.class));
        result.put("photo", metadata.create(JobRequestCardSetting.class));
        result.put("lastJob", metadata.create(JobRequestCardSetting.class));
        result.put("commentaries", metadata.create(JobRequestCardSetting.class));
        result.put("competencies", metadata.create(JobRequestCardSetting.class));
        result.put("education", metadata.create(JobRequestCardSetting.class));
        result.put("citizenship", metadata.create(JobRequestCardSetting.class));
        result.put("experience", metadata.create(JobRequestCardSetting.class));
        return result;
    }

    protected void initStoreStyleFilterMap() {
        RecruitmentConfig recruitmentConfig = configuration.getConfig(RecruitmentConfig.class);
        filtersMap = new LinkedHashMap<>();
        /*candidate*/
        filtersMap.put("candidate",
                new StoreStyleFilter.Element(StoreStyleFilter.ElementType.TEXT)
                        .setSelected(true)
                        .setCaption(getMessage("JobRequest.candidatePersonGroup.candidate"))
                        .setQueryFilter("lower(concat(cp.lastName, concat(' ', concat(cp.firstName, concat(' ', coalesce(cp.middleName, '')))))) ? " +
                                " OR " +
                                " lower(concat(cp.lastName, concat(' ', concat(cp.firstName, concat(' ', coalesce(cp.middleName, '')))))) ! " +
                                " OR " +
                                " lower(concat(cp.lastNameLatin, concat(' ', concat(cp.firstNameLatin, concat(' ', coalesce(cp.middleNameLatin, '')))))) ? " +
                                " OR " +
                                " lower(concat(cp.lastNameLatin, concat(' ', concat(cp.firstNameLatin, concat(' ', coalesce(cp.middleNameLatin, '')))))) ! "
                        )
        );

        /*requestStatus*/
        Map<Object, String> requestStatusMap = new HashMap<>();
        List<Object[]> jobRequestStatuses = commonService.emNativeQueryResultList(
                " select jr.request_status, count(1) as cnt" +
                        "     from tsadv_job_request jr " +
                        "    where jr.requisition_id = ?1" +
                        "      and jr.delete_ts is null" +
                        " group by jr.request_status"
                , Collections.singletonMap(1, requisitionDs.getItem().getId()));
        List<JobRequestStatus> jobRequestStatusList = jobRequestStatuses.stream().map(o -> JobRequestStatus.fromId((Integer) o[0])).collect(Collectors.toList());
        jobRequestStatusList.forEach(jrs -> requestStatusMap.put(jrs, messages.getMessage(jrs)));
        if (recruitmentConfig.getExcludeDraftStatusFromFilter()) {
            jobRequestStatusList.removeIf(jobRequestStatus -> jobRequestStatus == JobRequestStatus.DRAFT);
        }

        filtersMap.put("requestStatus",
                new StoreStyleFilter.Element(StoreStyleFilter.ElementType.SELECT)
                        .setSelected(true)
                        .setOptionsMap(requestStatusMap)
                        .setShowStatistics(true)
                        .setDatasourceProperty("requestStatus")
                        .setSelectedValues(jobRequestStatusList.toArray())
                        .setFetchCount(1l)
                        .setCaption(getMessage("JobRequest.requestStatus.inFilter"))
                        .setQueryFilter("e.requestStatus ?")
        );

        /*passedHiringStep*/
        LinkedHashMap<Object, String> passedHiringStepsMap = new LinkedHashMap<>();
        List<HiringStep> hiringStepsList = commonService.getEntities(HiringStep.class,
                "select e " +
                        "    from tsadv$HiringStep e " +
                        " join tsadv$RequisitionHiringStep hhr on hhr.hiringStep.id = e.id " +
                        "   where hhr.requisition.id = :requisitionId " +
                        "  and e.id in (select rhs.hiringStep.id " +
                        "                    from tsadv$JobRequest jr, tsadv$Interview i, tsadv$RequisitionHiringStep rhs" +
                        "                   where jr.requisition.id = :requisitionId " +
                        "                     and i.jobRequest.id = jr.id " +
                        "                     and i.interviewStatus = 40 " +
                        "                     and rhs.id = i.requisitionHiringStep.id" +
                        "                     and jr.deleteTs is null" +
                        "                     and i.deleteTs is null" +
                        "                     and rhs.deleteTs is null ) order by hhr.order",
                Collections.singletonMap("requisitionId", requisitionDs.getItem().getId()),
                View.LOCAL);
        hiringStepsList.forEach(hs -> {
            passedHiringStepsMap.put(hs.getId(), hs.getStepName());
        });
        filtersMap.put("passedHiringStep",
                new StoreStyleFilter.Element(StoreStyleFilter.ElementType.SELECT)
                        .setSelected(true)
                        .setOptionsMap(passedHiringStepsMap)
                        .setCaption(messages.getMainMessage("JobRequest.passedHiringSteps"))
                        .setQueryFilter("exists(select 1 " +
                                "  from tsadv$Interview i, tsadv$RequisitionHiringStep rhs" +
                                " where i.jobRequest.id = e.id " +
                                "   and i.interviewStatus = 40 " +
                                "   and rhs.id = i.requisitionHiringStep.id " +
                                "   and rhs.hiringStep.id ?" +
                                "   and i.deleteTs is null)")
        );

        /*viewLater*/
        Map<Object, String> viewLaterMap = new HashMap<>();
        viewLaterMap.put(Boolean.FALSE, messages.getMainMessage("filter.param.boolean.false"));
        viewLaterMap.put(Boolean.TRUE, messages.getMainMessage("filter.param.boolean.true"));

        filtersMap.put("viewLater",
                new StoreStyleFilter.Element(StoreStyleFilter.ElementType.SELECT)
                        .setSelected(true)
                        .setOptionsMap(viewLaterMap)
                        .setCaption(getMessage("JobRequest.viewLater.inFilter"))
                        .setQueryFilter("(FALSE ? and TRUE ? " +
                                " OR FALSE ? and not exists(select 1 from tsadv$UserExtJobRequestSeting vl where vl.jobRequest.id = e.id and vl.viewLater = TRUE and vl.userExt.id = :session$userExtId) " +
                                " OR TRUE ? and exists(select 1 from tsadv$UserExtJobRequestSeting vl where vl.jobRequest.id = e.id and vl.viewLater = TRUE and vl.userExt.id = :session$userExtId))")
        );

        /*requestDate*/
        filtersMap.put("requestDate",
                new StoreStyleFilter.Element(StoreStyleFilter.ElementType.DATE)
                        .setSelected(true)
                        .setCaption(getMessage("JobRequest.requestDate.date"))
                        .setQueryFilter("e.requestDate ?")
        );
        /*requestDate*/
        if (!competenceMap.isEmpty()) {
            filtersMap.put("candidatePersonGroup.id", new StoreStyleFilter.Element(StoreStyleFilter.ElementType.LOOKUP)
                    .setSelected(true)
                    .setCaption("test1")
                    .setQueryFilter("candidatePersonGroup.id ?"));
        }
        filtersMap.put("competenceGroup",
                new StoreStyleFilter.Element(StoreStyleFilter.ElementType.LOOKUP)
                        .setSelected(true)
                        .setCaption(getMessage("competenceGroupFilter"))
                        .setQueryFilter(" ?")
        );
        Map<Object, String> selectedByManagerMap = new HashMap<>();
        selectedByManagerMap.put(Boolean.FALSE, messages.getMainMessage("filter.param.boolean.false"));
        selectedByManagerMap.put(Boolean.TRUE, messages.getMainMessage("filter.param.boolean.true"));
        filtersMap.put("selectedByManager",
                new StoreStyleFilter.Element(StoreStyleFilter.ElementType.SELECT)
                        .setSelected(true)
                        .setOptionsMap(selectedByManagerMap)
                        .setCaption(getMessage("selectedByManager"))
                        .setQueryFilter("(TRUE ? OR FALSE ?) " +
                                "   and e.selectedByManager ?" +
                                "   OR TRUE not ? " +
                                "   and FALSE not ?")
        );
        filtersMap.put("city", new StoreStyleFilter.Element(StoreStyleFilter.ElementType.CHECKBOXCITY)
                .setSelected(true)
                .setCaption(getMessage("cityFilter"))
        );
    }

    protected boolean checkHireActionAccess(JobRequest jobRequest) {
        return jobRequest != null &&
                !requisitionDs.getItem().getWithoutOffer() &&
                allInterviewsPassed(jobRequest.getInterviews());
    }

    protected boolean allInterviewsPassed(List<Interview> list) { //TODO: bad construction, to refactor
        if (list != null && list.size() > 0) {
            for (Interview i : list) {
                if (!InterviewStatus.COMPLETED.equals(i.getInterviewStatus()))
                    return false;
            }
        } else return false;
        return true;
    }

    public void searchCandidate() {
        Window window;

        if (recruitmentConfig.getScreenForSearchCandidate() != null && recruitmentConfig.getScreenForSearchCandidate().equals("search-candidate")) {
            window = openWindow("search-candidate",
                    WindowManager.OpenType.THIS_TAB,
                    ParamsMap.of(StaticVariable.REQUISITION, requisitionDs.getItem(),
                            "jobRequestsDs", jobRequestsDs, "personGroupIds",
                            jobRequestsDs.getItems().stream().map(jobRequest -> {
                                return jobRequest.getCandidatePersonGroup().getId();
                            }).collect(Collectors.toList())));
        } else {
            window = openWindow("requisition-search-candidate",
                    WindowManager.OpenType.THIS_TAB,
                    ParamsMap.of(StaticVariable.REQUISITION, requisitionDs.getItem(),
                            "jobRequestsDs", jobRequestsDs));
        }
        window.addCloseListener(actionId -> {
            refreshFilterAndDs();
        });
    }

    public Component getAttachmentsButton(JobRequest entity) {
        HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
        hBoxLayout.setSpacing(true);
        LinkButton button = componentsFactory.createComponent(LinkButton.class);

        button.setCaption("");
        if (entity == null) {
            button.setStyleName("font-color-grey");
            button.setIcon("font-icon:PAPERCLIP");
            button.setDescription(messages.getMessage("kz.uco.tsadv.core.personal.group", "PersonGroup.personAttachment"));
        } else {
            button.setAction(new BaseAction("link")
                    .withIcon("font-icon:PAPERCLIP")
                    .withHandler(actionPerformedEvent -> {
                        Map<String, Object> params = new HashMap<>();
                        params.put("personGroupId", entity.getCandidatePersonGroup().getId());
                        openWindow("tsadv$PersonAttachment.browse", WindowManager.OpenType.DIALOG, params);
                    }));
        }
        hBoxLayout.add(button);
        return hBoxLayout;
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
                    storeStyleFilter.search();
                });
            }
        } catch (Exception e) {
            showNotification(e.getMessage());
            e.printStackTrace();
        }
    }

    protected void createInterview(JobRequest jobRequest, RequisitionHiringStep requisitionHiringStep) {
        Interview interview = metadata.create(Interview.class);
        try {
            if (requisitionHiringStep == null)
                showNotification(getMessage("RequisitionJobRequest.noAvailableHiringSteps"));
            else {
                interview.setJobRequest(jobRequest);
                interview.setRequisitionHiringStep(requisitionHiringStep);
                AbstractEditor<Interview> interviewEditor = openEditor(interview, WindowManager.OpenType.THIS_TAB);
                interviewEditor.addCloseListener(e -> {
                    storeStyleFilter.search();
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

    protected Component generatePreScreeningResult(JobRequest jobRequest) {
//        Map<String, Object> map = new HashMap<>();
//        map.put("jrId", jobRequest.getId());

        Double passingScore = null;
        InterviewQuestionnaire interviewQuestionnaire;
//        interviewQuestionnaire = commonService.emQueryFirstResult(InterviewQuestionnaire.class,
//                "select e from tsadv$InterviewQuestionnaire e " +
//                        "join e.interview i " +
//                        "join i.jobRequest jr " +
//                        "join e.questionnaire rq " +
//                        "join rq.category c " +
//                        "where c.code = 'PRE_SCREEN_TEST' and jr.id = :jrId " +
//                        "order by e.createTs desc",
//                map,
//                "interviewQuestionnaire.weight");
        interviewQuestionnaire = allInterviewQuestionnaire.stream().filter(interviewQuestionnaire1 ->
                interviewQuestionnaire1.getInterview().getJobRequest().getId().equals(jobRequest.getId()))
                .max((o1, o2) -> o1.getCreateTs().after(o2.getCreateTs()) ? 1 : -1).orElse(null);

        if (interviewQuestionnaire != null) {
            LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);

            RcQuestionnaire rcQuestionnaire = interviewQuestionnaire.getQuestionnaire();
            if (rcQuestionnaire != null) {
                passingScore = rcQuestionnaire.getPassingScore();
            }

            calculateTotalScore(interviewQuestionnaire);

            Double totalScore = interviewQuestionnaire.getTotalScore();

            linkButton.setCaption(String.format("%s / %s", totalScore, interviewQuestionnaire.getTotalMaxScore()));
            linkButton.setAction(new BaseAction("open-pre-screen") {
                @Override
                public void actionPerform(Component component) {
                    openWindow("interview-pre-screen",
                            WindowManager.OpenType.DIALOG,
                            ParamsMap.of(StaticVariable.INTERVIEW_ID, interviewQuestionnaire.getInterview().getId()));
                }
            });

            if (passingScore != null) {
                String cssClass = totalScore >= passingScore ? "pre-screen-passed" : "pre-screen-fail";
                linkButton.setStyleName(cssClass);
            }

            return linkButton;
        }
        return null;
    }

    protected void calculateTotalScore(InterviewQuestionnaire interviewQuestionnaire) {
        Collection<InterviewQuestion> propertyValues = interviewQuestionnaire.getQuestions();
        Double maxScore = 0d;
        Double score = 0d;

        for (InterviewQuestion interviewQuestion : propertyValues) {
            if (interviewQuestion.getQuestion().getAnswerType() == RcAnswerType.MULTI) {
                maxScore += interviewQuestion.getAnswers().stream().mapToDouble(a -> (a != null && a.getWeight() != null) ? a.getWeight() : 0).sum();
                score += interviewQuestion.getAnswers().stream().filter(answer -> answer != null && BooleanUtils.isTrue(answer.getBooleanAnswer())).mapToDouble(a -> a.getWeight() != null ? a.getWeight() : 0).sum();
            }
            if (interviewQuestion.getQuestion().getAnswerType() == RcAnswerType.SINGLE) {
                maxScore += interviewQuestion.getAnswers().stream().mapToDouble(a -> (a != null && a.getWeight() != null) ? a.getWeight() : 0).max().orElseGet(() -> 0d);
                score += interviewQuestion.getAnswers().stream().filter(answer -> answer != null && BooleanUtils.isTrue(answer.getBooleanAnswer())).mapToDouble(a -> a.getWeight() != null ? a.getWeight() : 0).sum();
            }
        }

        interviewQuestionnaire.setTotalScore(score);
        interviewQuestionnaire.setTotalMaxScore(maxScore);
    }

    public Component generateInterview(JobRequest entity) {
        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        Interview interview = entity.getInterview();

        if (interview != null)
            linkButton.setAction(new BaseAction("link")
                    .withCaption(interview.getRequisitionHiringStep().getHiringStep().getStepName() + " (" + messages.getMessage(interview.getInterviewStatus()) + ")")
                    .withHandler(actionPerformedEvent -> {
                        AbstractEditor<Interview> interviewEditor = openEditor("tsadv$Interview.edit", interview, WindowManager.OpenType.THIS_TAB);
                        interviewEditor.addCloseWithCommitListener(() -> {
                            storeStyleFilter.search();
                        });
                    }));
        return linkButton;
    }

    protected void refreshFilterAndDs() {
        initStoreStyleFilterMap();
        storeStyleFilter.setFilterByMap(filtersMap);
        storeStyleFilter.refresh();
        storeStyleFilter.search();
    }

    public Component generateOffer(JobRequest entity) {
        Map paramsMap = new HashMap();
        paramsMap.put("idJobRequest", entity.getId());
        List<Offer> offerList = new ArrayList<>();

        if (offers == null) {
            loadLists();
        }
        if (offers != null) {
            offerList = offers.stream().filter(offer -> offer.getJobRequest().getId().equals(entity.getId())).collect(Collectors.toList());
        }
//        offerList = commonService.getEntities(Offer.class, "select e from tsadv$Offer e where e.jobRequest.id = :idJobRequest", paramsMap, null);

        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);

        if (offerList.size() != 0) {
            linkButton.setAction(new BaseAction("link")
                    .withCaption(messages.getMessage(entity.getRequestStatus()))
                    .withHandler(actionPerformedEvent -> {
                        openWindow("tsadv$Offer.browse", WindowManager.OpenType.THIS_TAB, paramsMap);
                    }));
            return linkButton;

        } else {
            if (recruitmentConfig.getEditEnabled()) {
                LinkButton linkStatus = componentsFactory.createComponent(LinkButton.class);
                linkStatus.setCaption(getMessage(messages.getMessage(entity.getRequestStatus())));
                linkStatus.setAction(new BaseAction("editStatus")
                        .withHandler(actionPerformedEvent -> {
                            AbstractEditor abstractEditor = openEditor("tsadv$JobRequestStatus.edit", entity, WindowManager.OpenType.DIALOG,
                                    ParamsMap.of("", null));
                            abstractEditor.addCloseWithCommitListener(() -> {
                                refresh();
                            });
                        }));
                return linkStatus;
            } else {
                Label label = componentsFactory.createComponent(Label.class);
                label.setValue(messages.getMessage(entity.getRequestStatus()));
                return label;
            }
        }
    }

    public void onCreateOfferButtonClick() {
        Offer offer = metadata.create(Offer.class);
        if (!isCard) {
            offer.setJobRequest(jobRequestsDs.getItem());
        } else {
            offer.setJobRequest(currentCardJobRequest);
        }
        openEditor("tsadv$Offer.edit", offer, WindowManager.OpenType.THIS_TAB);
    }

    public Component generateContacts(JobRequest entity) {
        List<PersonContact> personContacts = commonService.getEntities(PersonContact.class,
                "select e from tsadv$PersonContact e where e.personGroup.id = :personGroupId order by e.type.code",
                Collections.singletonMap("personGroupId", entity.getCandidatePersonGroup().getId()),
                "personContact.edit");
        GridLayout gridLayout = componentsFactory.createComponent(GridLayout.class);
        if (!personContacts.isEmpty()) {
            gridLayout.setColumns(2);
            gridLayout.setRows(personContacts.size());
            gridLayout.setStyleName("person-percentage-grid");

            for (int i = 0; i < personContacts.size(); i++) {
                Label contactType = componentsFactory.createComponent(Label.class);
                Label contactValue = componentsFactory.createComponent(Label.class);

                contactType.setValue(personContacts.get(i).getType().getLangValue());
                contactValue.setValue(personContacts.get(i).getContactValue());

                gridLayout.add(contactType, 0, i);
                gridLayout.add(contactValue, 1, i);
            }
        }
        return gridLayout;
    }

    public Component generatePassedInterviews(JobRequest entity) {

        Label label = componentsFactory.createComponent(Label.class);
        if (entity.getPassedInterviews() != null && entity.getTotalInterviews() != null)
            label.setValue(String.format(messages.getMainMessage("JobRequest.interviews"),
                    entity.getPassedInterviews(),
                    entity.getTotalInterviews()));

        return label;
    }

    public void openVideo(JobRequest jobRequest) {
        Map<String, Object> map = new HashMap<>();
        map.put("jobRequest", jobRequest);
        openWindow("video-screen", WindowManager.OpenType.DIALOG, map);
    }

    public void hireCandidate(Component source) {
        if (isCard) {
            currentCardJobRequest.setRequestStatus(JobRequestStatus.HIRED);
        } else {
            jobRequestsDs.getItem().setRequestStatus(JobRequestStatus.HIRED);
        }

        jobRequestsDs.commit();
    }

    public Component generateCandidatePersonGroupCell(JobRequest entity) {
        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);

        linkButton.setCaption(entity.getCandidatePersonGroup().getPerson().getFullName());
        linkButton.setAction(new BaseAction("linkToCandidateCard") {
            @Override
            public void actionPerform(Component component) {
                AbstractEditor<PersonExt> personEditor = openEditor("base$Person.candidate", entity.getCandidatePersonGroup().getPerson(), WindowManager.OpenType.THIS_TAB);
                personEditor.addCloseWithCommitListener(() -> {
                    refreshFilterAndDs();
                });
            }
        });
        return linkButton;
    }

    protected Component getViewLaterButton(JobRequest entity) {
        if (isSelfService) {
            LinkButton shortListedButton = componentsFactory.createComponent(LinkButton.class);
            if (entity == null) {
                shortListedButton.setIcon("font-icon:CIRCLE_O");
            } else {
                shortListedButton.setIcon(entity.getSelectedByManager() != null && entity.getSelectedByManager() ? "font-icon:CHECK_CIRCLE_O" : "font-icon:CIRCLE_O");
                shortListedButton.setStyleName("c-card-navigate");
                shortListedButton.setCaption(messages.getMessage("kz.uco.tsadv.web.modules.recruitment.requisition.frames", "shortlisted"));

                shortListedButton.setAction(new BaseAction("seeLaterAction") {
                    @Override
                    public void actionPerform(Component component) {
                        boolean value = entity.getSelectedByManager();
                        if (!value) {
                            entity.setSelectedByManager(true);
                        } else {
                            entity.setSelectedByManager(false);
                        }
                        dataManager.commit(entity);
                        jobRequestsDs.refresh();
                    }
                });
            }
            return shortListedButton;
        }
        HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
        hBoxLayout.setSpacing(true);
        LinkButton viewLaterButton = componentsFactory.createComponent(LinkButton.class);
        if (entity == null) {
            viewLaterButton.setIcon("font-icon:FLAG_O");
            viewLaterButton.setStyleName("font-color-grey");
            viewLaterButton.setDescription(messages.getMessage("kz.uco.tsadv.recruitment", "JobRequest.viewLater"));
        } else {
            viewLaterButton.setIcon(entity.getViewLater() ? "font-icon:FLAG" : "font-icon:FLAG_O");
            viewLaterButton.setStyleName(entity.getViewLater() ? "font-color-green" : "font-color-grey");
            viewLaterButton.setAction(new BaseAction("seeLaterAction") {
                @Override
                public void actionPerform(Component component) {
                    if (entity.getViewLaters() != null) {
                        UserExtJobRequestSeting userExtJobRequestSeting;
                        boolean isViewLater = entity.getViewLater();
                        component.setStyleName(isViewLater ? "font-color-grey" : "font-color-green");
                        ((LinkButton) component).setIcon(isViewLater ? "font-icon:FLAG_O" : "font-icon:FLAG");
                        userExtJobRequestSeting = entity.getViewLaters()
                                .stream()
                                .filter(i -> i.getDeleteTs() == null && i.getUserExt().getId().equals(AppBeans.get(UserSession.class).getAttribute(StaticVariable.USER_EXT_ID)))
                                .findAny().orElseGet(() -> {
                                    UserExtJobRequestSeting userExtJobRequestSeting1 = metadata.create(UserExtJobRequestSeting.class);
                                    userExtJobRequestSeting1.setJobRequest(entity);
                                    userExtJobRequestSeting1.setUserExt(AppBeans.get(UserSession.class).getAttribute(StaticVariable.USER_EXT));
                                    return userExtJobRequestSeting1;
                                });
                        userExtJobRequestSeting.setViewLater(!isViewLater);
                        dataManager.commit(userExtJobRequestSeting);
                        jobRequestsDs.refresh();
                    }
                }
            });
        }
        viewLaterButton.setCaption(messages.getMainMessage("table.btn.empty"));
        hBoxLayout.add(viewLaterButton);
        return hBoxLayout;
    }

    protected Component createSelectedByManager(JobRequest entity) {
        HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
        hBoxLayout.setSpacing(true);
        Label label = componentsFactory.createComponent(Label.class);
        if (entity == null) {
            label.setIcon("font-icon:FLAG_O");
            label.setStyleName("font-color-grey");
            label.setDescription(messages.getMessage("kz.uco.tsadv.recruitment", "JobRequest.viewLater"));
        } else {
            label.setIcon(entity.getSelectedByManager() != null && entity.getSelectedByManager() ? "font-icon:BRIEFCASE" : "");
            label.setStyleName("font-color-green");
        }
//        label.setCaption(messages.getMainMessage("table.btn.empty"));
        hBoxLayout.add(label);
        return hBoxLayout;
    }

    protected PersonGroupExt getPersonGroupWithLinkedin(UUID personGroupId) {
        Map<String, Object> params = new HashMap<>();
        params.put("personGroupId", personGroupId);
        PersonGroupExt personGroup = commonService.getEntity(PersonGroupExt.class, "select e" +
                        "                          from base$PersonGroupExt e" +
                        "                          where e.id = :personGroupId",
                params, "personGroup.linkedin");
        return personGroup;
    }

    protected Component getLinkedinButton(JobRequest entity) {
        LinkButton linkedinButton = componentsFactory.createComponent(LinkButton.class);
        if (entity == null) {
            linkedinButton.setIcon("images/linkedin/color-no-border-20px.png");
            linkedinButton.setDescription(m("lookLinkedinProfile"));
            return linkedinButton;
        }
//        UUID personGroupId = entity.getCandidatePersonGroup().getId();
        String linkedinUrl = entity.getCandidatePersonGroup().getLinkedinProfileLink();
        if (StringUtils.isNotEmpty(linkedinUrl)) {
            linkedinButton.setIcon("images/linkedin/color-no-border-20px.png");
            linkedinButton.setDescription(m("lookLinkedinProfile"));
            linkedinButton.setAction(new BaseAction("goToLinkedinProfile") {
                @Override
                public void actionPerform(Component component) {
                    showWebPage(linkedinUrl, ParamsMap.of("target", "_blank"));
                }
            });
        } else {
            linkedinButton.setIcon("images/linkedin/gray-no-border-20px.png");
            linkedinButton.setDescription(m("linkedinDoesNotLinked"));
        }
        linkedinButton.setCaption("");
        return linkedinButton;
    }


    public void onFastCreateClick() {
        JobRequest jobRequest = metadata.create(JobRequest.class);
        PersonGroupExt personGroup = metadata.create(PersonGroupExt.class);
        PersonExt person = metadata.create(PersonExt.class);
        List<Interview> interviewList = new ArrayList<>();

        jobRequest.setRequisition(requisitionDs.getItem());
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
        requisitionDs.getItem().getHiringSteps().forEach(requisitionHiringStep -> {
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

  /*      map.put("excludedInterviews", jobRequestsDs.getItems() != null ? jobRequestsDs.getItems().stream().map(
                jobRequest1 -> jobRequest1.getInterviews()).collect(Collectors.toList()) : null);*/

        JobRequestFastEdit jobRequestFastEdit = (JobRequestFastEdit) openEditor("tsadv$JobRequestFast.edit", jobRequest, WindowManager.OpenType.THIS_TAB, map);
        jobRequestFastEdit.addCloseListener(actionId -> {
            refreshFilterAndDs();
            refresh();
        });
    }

    @SuppressWarnings("all")
    public void fastCreateInterview() {
        JobRequest jobRequest = jobRequestsDs.getItem();

        JobRequestFastInterviewEdit jobRequestFastEdit = (JobRequestFastInterviewEdit) openEditor(
                "tsadv$JobRequestFastInterview.edit", jobRequest, WindowManager.OpenType.THIS_TAB,
                ParamsMap.of("requisitionDs", requisitionDs));

        jobRequestFastEdit.addCloseListener(actionId -> {
            storeStyleFilter.search();
        });
    }

    public void sendToReserve() {
        JobRequest jobRequest;
        if (isCard) {
            jobRequest = currentCardJobRequest;
        } else {
            jobRequest = jobRequestsDs.getItem();
        }
        jobRequest.setIsReserved(true);
        dataManager.commit(jobRequest);
        jobRequestsDs.refresh();
        sendNotificationForCandidate(
                jobRequest.getCandidatePersonGroup(),
                "requisition.notify.application.candidate.reserve",
                requisitionDs.getItem());
    }

    public void excludeFromReserve() {
        JobRequest jobRequest;
        if (isCard) {
            jobRequest = currentCardJobRequest;
        } else {
            jobRequest = jobRequestsDs.getItem();
        }
        jobRequest.setIsReserved(false);
        dataManager.commit(jobRequest);
        jobRequestsDs.refresh();
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
                                        requisitionDs.getItem());
                            }
                        },
                        new DialogAction(DialogAction.Type.CANCEL)
                });

    }


    public void onCardOrTableClick() {
        setSpacing(true);
        if (isCard) {
            toTable();
        } else {
            toCard();
        }
    }

    protected void setVisibleExcessButtons(boolean isVisible) {
        createInterview.setVisible(isVisible);
        fastCreateInterview.setVisible(isVisible);
        createOffer.setVisible(isVisible);
        actions.setVisible(isVisible);
    }

    protected void toCard() {
        isCard = true;
        // cardOrTable.setCaption(getMessage("JobRequest.toTable"));
        cardOrTable.setIcon("font-icon:TABLE");
        remove(vbox1);

        cardBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
        cardBoxLayout.setWidth("100%");
        cardBoxLayout.setHeight("100%");
        Label dummy = componentsFactory.createComponent(Label.class);
        dummy.setValue("Здесь должен быть компонент фильтра");
        table = createCardTable();

        cardBoxLayout.add(table);
//        cardBoxLayout.add(dummy);
        add(cardBoxLayout);
        expand(cardBoxLayout);
        setVisibleExcessButtons(false);
    }

    protected Map<CardBlockType, List<Component>> checkBoxComponentMap = new HashMap<>();
    protected Map<CardBlockType, List<CheckBox>> checkBoxMap = new HashMap<>();

    protected Table<JobRequest> createCardTable() {
        table = componentsFactory.createComponent(Table.class);
        table.setStyleName("c-card-table");
        table.setDatasource(jobRequestsDs);
        while (table.getColumns().iterator().hasNext())
            table.removeColumn(table.getColumns().iterator().next());
        table.setWidth("100%");
        table.setHeight("100%");
        table.setShowSelection(false);
        table.setColumnHeaderVisible(false);

        checkBoxMap.clear();

        if (checkBoxComponentMap.isEmpty()) {
            for (CardBlockType cardBlockType : CardBlockType.values()) {
                if (!recruitmentConfig.getEnableCandidateRequirements() && cardBlockType.equals(CardBlockType.REQUIREMENTS)) {
                    continue;
                }
                checkBoxComponentMap.put(cardBlockType, new ArrayList<>());
            }
        }

        for (CardBlockType cardBlockType : CardBlockType.values()) {
            if (!recruitmentConfig.getEnableCandidateRequirements() && cardBlockType.equals(CardBlockType.REQUIREMENTS)) {
                continue;
            }
            checkBoxMap.put(cardBlockType, new ArrayList<>());
        }


        table.addGeneratedColumn("cardColumn", new Table.ColumnGenerator<JobRequest>() {
            @Override
            public Component generateCell(JobRequest entity) {
                return createCardItem(entity);
            }
        });
        return table;
    }

    protected HtmlBoxLayout createFilterDiv() {
        HtmlBoxLayout dropDownFilter = componentsFactory.createComponent(HtmlBoxLayout.class);
        dropDownFilter.setWidth("90%");
        dropDownFilter.setStyleName("c-card-dd-filter");
        dropDownFilter.setTemplateName("dropdown-filter");
        dropDownFilter.setAlignment(Alignment.MIDDLE_RIGHT);

        LinkButton filterButton = componentsFactory.createComponent(LinkButton.class);
        filterButton.setAlignment(Alignment.MIDDLE_RIGHT);
        filterButton.setId("filterButton");
        filterButton.setIcon("font-icon:FILTER");
        dropDownFilter.add(filterButton);

        VBoxLayout vBoxLayout = componentsFactory.createComponent(VBoxLayout.class);
        vBoxLayout.setId("vbox");
        dropDownFilter.add(vBoxLayout);

        for (CardBlockType cardBlockType : CardBlockType.values()) {
            if (!recruitmentConfig.getEnableCandidateRequirements() && cardBlockType.equals(CardBlockType.REQUIREMENTS)) {
                continue;
            }
            CheckBox checkBox;
            checkBox = componentsFactory.createComponent(CheckBox.class);
            checkBox.setCaption(m(cardBlockType.messageKey));
            checkBox.setId(cardBlockType.toString()); //id
            Collection<KeyValueEntity> items = cardFilterSettingsDs.getItems();
            KeyValueEntity keyValueEntity1 = items.stream().filter(keyValueEntity ->
                    keyValueEntity.getValue("cardBlockType").equals(cardBlockType.getMessageKey())
            ).findFirst().orElse(null);
            if (keyValueEntity1 != null
                    && keyValueEntity1.getValue("cardBlockType").toString()
                    .equals(cardBlockType.getMessageKey())) {
                checkBox.setValue(keyValueEntity1.getValue("checked"));
            } else {
                KeyValueEntity keyValueEntity = metadata.create(KeyValueEntity.class);
                keyValueEntity.setValue("cardBlockType", cardBlockType.getMessageKey());
                keyValueEntity.setValue("checked", cardBlockType.equals(CardBlockType.COMPETENCE) ? false : true);
                cardFilterSettingsDs.addItem(keyValueEntity);
                checkBox.setValue(keyValueEntity.getValue("checked"));
            }
//            checkBox.addValueChangeListener(new ValueChangeListener() {
//                @Override
//                public void valueChanged(ValueChangeEvent e) {
//                    if (ignoreCheckBoxValueCHange) {
//                        return;
//                    }
//                    boolean checked = e.getValue() != null && Boolean.parseBoolean(e.getValue().toString());
//                    List<Component> components = checkBoxComponentMap.get(cardBlockType);
//                    for (Component component : components) {
//                        component.setVisible(checked);
//                    }
//
//                    List<CheckBox> checkBoxList = checkBoxMap.get(cardBlockType);
//                    ignoreCheckBoxValueCHange = true;
//                    for (CheckBox box : checkBoxList) {
//                        box.setValue(checked);
//                    }
//                    ignoreCheckBoxValueCHange = false;
////                        if (checkBox != null) {
////                            checkBox.setValue(checked);
////                        }
//
////                        checkBoxList.forEach(checkBox1 -> {
////                            if (checkBox1.getId().equals(cardBlockType.toString())) {
////                                checkBox1.setValue(checked);
////                                checkBoxMap.put(cardBlockType, checkBox1);
////                            }
////                        });
//                    Collection<KeyValueEntity> items = cardFilterSettingsDs.getItems();
//                    boolean existItem = false;
//                    for (KeyValueEntity item : items) {
//                        if (item.getValue("cardBlockType").equals(cardBlockType.getMessageKey())) {
//                            item.setValue("checked", checked);
//                            existItem = true;
//                            break;
//                        }
//                    }
//                    if (!existItem) {
//                        KeyValueEntity keyValueEntity = metadata.create(KeyValueEntity.class);
//                        keyValueEntity.setValue("cardBlockType", cardBlockType.getMessageKey());
//                        keyValueEntity.setValue("checked", checked);
//                        cardFilterSettingsDs.addItem(keyValueEntity);
//                    }
//                }
//            });
            checkBoxMap.get(cardBlockType).add(checkBox);

//            checkBoxList.add(checkBox);

            vBoxLayout.add(checkBox);
        }
        return dropDownFilter;
    }

    protected Component createCardItem(JobRequest model) {
        VBoxLayout cardWrapper = componentsFactory.createComponent(VBoxLayout.class);
        cardWrapper.setWidthFull();
        cardWrapper.setStyleName("c-card-wrapper");

        VBoxLayout header = componentsFactory.createComponent(VBoxLayout.class);
        header.setStyleName("c-card-header");
        cardWrapper.add(header);

        HBoxLayout headerTop = componentsFactory.createComponent(HBoxLayout.class);
        headerTop.setWidthFull();
        headerTop.setStyleName("c-card-header-top");
        header.add(headerTop);

        Image image = WebCommonUtils.setImage(model.getCandidatePersonGroup().getPerson().getImage(), null, "45px");
        image.setStyleName("c-card-image");
        image.addStyleName("circle-image");
        image.setAlignment(Alignment.MIDDLE_CENTER);
        headerTop.add(image);

        HBoxLayout fioWithFlag = componentsFactory.createComponent(HBoxLayout.class);
        fioWithFlag.setSpacing(true);
        fioWithFlag.setStyleName("c-card-fio");
        fioWithFlag.setWidthAuto();
        fioWithFlag.setAlignment(Alignment.MIDDLE_LEFT);

        Label fio = componentsFactory.createComponent(Label.class);

        PersonExt candidatePerson = model.getCandidatePersonGroup().getPerson();

        fio.setValue(candidatePerson.getFullName() + " (" + candidatePerson.getType().getLangValue() + ")");
        fio.setAlignment(Alignment.MIDDLE_LEFT);
        fioWithFlag.add(getLinkedinButton(model));
        fioWithFlag.add(fio);
        if (!isSelfService) {
            Component viewLater = getViewLaterButton(model);
            viewLater.setAlignment(Alignment.MIDDLE_LEFT);
            fioWithFlag.add(viewLater);
            fioWithFlag.setMargin(new MarginInfo(true, true, true, false));
            fioWithFlag.expand(viewLater);

            Component selectedByManager = createSelectedByManager(model);
            selectedByManager.setAlignment(Alignment.MIDDLE_LEFT);
            fioWithFlag.add(selectedByManager);
            fioWithFlag.setMargin(new MarginInfo(true, true, true, false));
            fioWithFlag.expand(selectedByManager);
        }
        headerTop.add(fioWithFlag);

        HtmlBoxLayout dropDownFilter = createFilterDiv();
        headerTop.add(dropDownFilter);
        headerTop.expand(dropDownFilter);

        HBoxLayout links = componentsFactory.createComponent(HBoxLayout.class);
        links.setSpacing(true);
        links.setAlignment(Alignment.TOP_LEFT);
        links.setStyleName("c-card-header-bottom");
        header.add(links);
        Component rightBlock = createCardItemRight(model, links);
        cardWrapper.add(rightBlock);
        cardWrapper.expand(rightBlock);
        return cardWrapper;
    }

    protected Component mainInfoGrid(JobRequest model) {
        HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
        hBoxLayout.setWidthFull();

        GridLayout mainInfoGridLeft = componentsFactory.createComponent(GridLayout.class);
        mainInfoGridLeft.setStyleName("c-card-main-info");
        mainInfoGridLeft.setColumns(2);

        GridLayout mainInfoGridRight = componentsFactory.createComponent(GridLayout.class);
        mainInfoGridRight.setStyleName("c-card-main-info");
        mainInfoGridRight.setColumns(2);

        //Дата отклика
        addRow(mainInfoGridLeft,
                label(m(JobRequest.class, "JobRequest.requestDate")),
                label(dateFormat.format(model.getRequestDate())));

        //Статус
        addRow(mainInfoGridLeft,
                label(m("candidate.card.status")), generateOffer(model));
//                label(m(model.getRequestStatus())));


        //Причина
        if (model.getRequestStatus().equals(JobRequestStatus.REJECTED)) {
            DicJobRequestReason jobRequestReason = model.getJobRequestReason();
            String reason;
            if (jobRequestReason != null) {
                reason = jobRequestReason.getLangValue();
            } else {
                reason = model.getReason();
            }

            addRow(mainInfoGridLeft,
                    label(m("candidate.card.reason")),
                    label(reason));
        }

        Component preScreenResult = generatePreScreeningResult(model);
        if (preScreenResult != null) {
            addRow(mainInfoGridLeft,
                    label(m("candidate.card.pre.screen.result")),
                    preScreenResult);
        }


// Предыдущие отклики
        PersonExt person = model.getCandidatePersonGroup().getPerson();
//        person = commonService.getEntity(PersonExt.class,
//                "select e from base$PersonExt e where e.group.id=:candidateId and (:sysDate between e.startDate and e.endDate)",
//                ParamsMap.of("candidateId", model.getCandidatePersonGroup(), "sysDate", new Date()), "person.candidate");
        Label prevJobRequest = componentsFactory.createComponent(Label.class);
        prevJobRequest.setValue(getMessage("persondata.prevJobRequest"));
        Long count = 0L;
//        count = commonService.getCount(JobRequest.class,
//                "select e from tsadv$JobRequest e " +
//                        "where e.candidatePersonGroup.id = :personId ",
//                ParamsMap.of("personId", person.getGroup().getId()));
        if (allJobRequests == null) {
            loadLists();
        }
        if (allJobRequests != null) {
            count = allJobRequests.stream().filter(jobRequest -> jobRequest.getCandidatePersonGroup().getId().equals(model.getCandidatePersonGroup().getId())).count();
        }
        Label prevJobRequestValueLabel = componentsFactory.createComponent(Label.class);
        LinkButton prevJobRequestValueLinkButton = componentsFactory.createComponent(LinkButton.class);
        if (count > 0) {
            prevJobRequestValueLabel.setValue(count.toString());
            prevJobRequestValueLinkButton.setCaption(count.toString());
            prevJobRequestValueLinkButton.setAction(new BaseAction("openJobrequests") {
                @Override
                public void actionPerform(Component component) {
                    super.actionPerform(component);
                    openWindow("jobRequestForGroupInterviewWindow",
                            WindowManager.OpenType.NEW_TAB,
                            ParamsMap.of("personGroupId", person.getGroup().getId()));
                }
            });
        }
        addRow(mainInfoGridLeft, prevJobRequest, isSelfService ? prevJobRequestValueLabel : prevJobRequestValueLinkButton);

        //Контакты
//        int contactsCount = model.getCandidatePersonGroup().getPersonContacts().size();
//        if (contactsCount != 0) {
//            StringBuilder contacts = new StringBuilder();
//            for (PersonContact pc : model.getCandidatePersonGroup().getPersonContacts()) {
//                DicPhoneType phoneType = pc.getType();
//                if (phoneType != null && phoneType.getCode() != null && phoneType.getCode().equalsIgnoreCase("mobile")) {
//                    if (contacts.length() != 0) {
//                        contacts.append(", ");
//                    }
//
//                    contacts.append(pc.getContactValue());
//                }
//            }
//
//            if (contacts.length() != 0) {
//                addRow(mainInfoGridRight,
//                        label(messages.getMainMessage("candidate.card.mobile")),
//                        label(contacts.toString()));
//            }
//        }

        //Этап отбора
        if (model.getInterview() != null) {
            addRow(mainInfoGridRight,
                    label(messages.getMainMessage("candidate.card.step")),
                    generateInterview(model));
        }

        //Завершенные интервью
        addRow(mainInfoGridRight,
                label(messages.getMainMessage("candidate.card.interview")),
                generatePassedInterviews(model));

        //Видео интервью
        addRow(mainInfoGridRight,
                label(messages.getMainMessage("candidate.card.videoInterview")),
                generateVideoInterview(model));

        hBoxLayout.add(mainInfoGridLeft);
        hBoxLayout.add(mainInfoGridRight);

        return hBoxLayout;
    }

    protected Component generateVideoInterview(JobRequest model) {
        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        if (model.getVideoFile() != null) {
            linkButton.setCaption(model.getVideoFile().getName());
            linkButton.setAction(new BaseAction("showVideo") {
                @Override
                public void actionPerform(Component component) {
                    openVideo(model);
                }
            });
        }
        return linkButton;
    }

    protected void addRow(GridLayout mainInfoGrid, Component caption, Component value) {
        int row = mainInfoGrid.getRows() + 1;
        mainInfoGrid.setRows(row);
        caption.setStyleName("c-card-main-info-l");
        value.addStyleName("c-card-main-info-v");
        mainInfoGrid.add(caption, 0, row - 1);
        mainInfoGrid.add(value, 1, row - 1);
    }


    protected void addRowForPerson(GridLayout mainInfoGrid, Component caption, Component value) {
        int row = mainInfoGrid.getRows() + 1;
        mainInfoGrid.setRows(row);
        caption.setStyleName("c-card-main-info-l");
        value.addStyleName("c-card-main-info-v");
        mainInfoGrid.add(caption, 0, row - 1);
        mainInfoGrid.add(value, 1, row - 1);
    }

    protected String calculateAge(Date dateOfBirth) {
        String result = "";
        if (dateOfBirth == null) {
            return result;
        }
        Calendar a = Calendar.getInstance();
        a.setTime(dateOfBirth);
        Calendar b = Calendar.getInstance();
        int age = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) || (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
            age--;
        }

        int lastDigit = age % 10;
        int previousLastDigit = age / 10 % 10;
        if (previousLastDigit == 1) {
            result = age + " лет";
        } else {
            switch (lastDigit) {
                case 1:
                    result = age + " год";
                    break;
                case 2:
                case 3:
                case 4:
                    result = age + " года";
                    break;
                default:
                    result = age + " лет";
            }
        }

        return result;
    }

    protected Component createCardItemRight(JobRequest model, HBoxLayout links) {
        VBoxLayout wrapper = componentsFactory.createComponent(VBoxLayout.class);
        wrapper.setStyleName("c-card-content-wrapper");
        wrapper.setSpacing(true);
        RecruitmentConfig recruitmentConfig = configuration.getConfig(RecruitmentConfig.class);
        Boolean disableWhenDraft = recruitmentConfig.getdDisableWhenDraft() && JobRequestStatus.DRAFT.equals(model.getRequestStatus());

        wrapper.add(mainInfoGrid(model));

        List<Component> components = new LinkedList<>();

        //Персональные данные

//        PersonExt person = commonService.getEntity(PersonExt.class,
//                "select e from base$PersonExt e where e.group.id=:candidateId " +
//                        " and :sysDate between e.startDate and e.endDate ",
//                ParamsMap.of("candidateId", model.getCandidatePersonGroup(), "sysDate", CommonUtils.getSystemDate()), "person.candidate");
        PersonExt person = model.getCandidatePersonGroup().getPerson();
        VBoxLayout vBoxLayoutPD = componentsFactory.createComponent(VBoxLayout.class);
        HBoxLayout hBoxLayoutPD = componentsFactory.createComponent(HBoxLayout.class);
        hBoxLayoutPD.setWidthFull();
        GridLayout mainInfoGridLeft = componentsFactory.createComponent(GridLayout.class);
        mainInfoGridLeft.setStyleName("c-card-main-info");
        mainInfoGridLeft.setColumns(2);
        Label dateBirthCaption = componentsFactory.createComponent(Label.class);
        dateBirthCaption.setValue(getMessage("persondata.birthdate"));
        Label dateBirthValue = componentsFactory.createComponent(Label.class);
        if (person != null && person.getDateOfBirth() != null) {
            dateBirthValue.setValue(dateFormat.format(person.getDateOfBirth()) + " (" + calculateAge(person.getDateOfBirth()) + ")");
        }
        addRowForPerson(mainInfoGridLeft, dateBirthCaption, dateBirthValue);

        Label sex = componentsFactory.createComponent(Label.class);
        sex.setValue(getMessage("persondata.sex"));
        Label sexValue = componentsFactory.createComponent(Label.class);
        sexValue.setValue(person.getSex() != null ? person.getSex().getLangValue() : "");
        addRowForPerson(mainInfoGridLeft, sex, sexValue);

        Label citizenship = componentsFactory.createComponent(Label.class);
        citizenship.setValue(getMessage("persondata.citizenship"));
        Label citizenshipValue = componentsFactory.createComponent(Label.class);
        citizenshipValue.setValue(person.getCitizenship() != null ? person.getCitizenship().getLangValue() : "");
        addRowForPerson(mainInfoGridLeft, citizenship, citizenshipValue);


//        addRowForPerson(mainInfoGridLeft, prevJobRequest, prevJobRequestValue);

        GridLayout mainInfoGridRight = componentsFactory.createComponent(GridLayout.class);
        mainInfoGridRight.setStyleName("c-card-main-info");
        mainInfoGridRight.setColumns(2);

        hBoxLayoutPD.add(mainInfoGridLeft);

        Label mobile = componentsFactory.createComponent(Label.class);
        mobile.setValue(getMessage("persondata.mobile"));
        Label mobileValue = componentsFactory.createComponent(Label.class);
        Label email = componentsFactory.createComponent(Label.class);
        email.setValue(getMessage("persondata.email"));
        Link emailValue = componentsFactory.createComponent(Link.class);
        for (PersonContact personContact : model.getCandidatePersonGroup().getPersonContacts()) {
            String code = personContact.getType().getCode();
            if (code == null) continue;
            if (code.contains("mobile")) {
                mobileValue.setValue(personContact.getContactValue());
            }
            if (code.contains("email")) {
                emailValue.setCaption(personContact.getContactValue());
                emailValue.setTarget(personContact.getContactValue());
                emailValue.setUrl("mailto:" + personContact.getContactValue());
            }
        }
        Label city = componentsFactory.createComponent(Label.class);
        city.setValue(getMessage("persondata.city"));
        Label cityValue = componentsFactory.createComponent(Label.class);
        Address address = null;
        if (model.getCandidatePersonGroup() != null && model.getCandidatePersonGroup().getAddresses() != null) {
            List<Address> addresses = model.getCandidatePersonGroup().getAddresses();
            address = addresses.stream().filter(addressTemp -> {
                if (addressTemp.getStartDate() != null && addressTemp.getEndDate() != null
                        && (addressTemp.getStartDate().before(CommonUtils.getSystemDate())
                        || addressTemp.getStartDate().equals(CommonUtils.getSystemDate()))
                        && addressTemp.getEndDate().after(CommonUtils.getSystemDate())
                        && addressTemp.getAddressType() != null
                        && addressTemp.getAddressType().getCode() != null
                        && addressTemp.getAddressType().getCode().equals("RESIDENCE")) {
                    return true;
                } else {
                    return false;
                }
            }).findFirst().orElse(null);
            if (address != null && address.getCity() != null) {
                cityValue.setValue(address.getCity());
            } else {
                cityValue.setValue("");
            }
        }
        LinkButton addOrEdit = componentsFactory.createComponent(LinkButton.class);
        addOrEdit.setCaption("");
        final Address addressToEditor = address;
        if (cityValue.getValue() != null && !cityValue.getValue().equals("")) {
            addOrEdit.setIcon("font-icon:PENCIL");
        } else {
            addOrEdit.setIcon("font-icon:PLUS");
        }
        addOrEdit.setAction(new BaseAction("edit") {
            Address addressInner;

            @Override
            public void actionPerform(Component component) {
                if (addressToEditor != null) {
                    addressInner = addressToEditor;
                    if (addOrEdit.getIcon().equals("font-icon:PENCIL")) {
                        AbstractEditor fromRequisitionJobRequest = openEditor(addressToEditor, WindowManager.OpenType.DIALOG, ParamsMap.of("fromRequisitionJobRequest", new Object()));
                        fromRequisitionJobRequest.addCloseWithCommitListener(() -> {
                            cityValue.setValue(((Address) fromRequisitionJobRequest.getItem()).getCity());
                        });
                    } else if (addOrEdit.getIcon().equals("font-icon:PLUS")) {
                        Address addressNew = metadata.create(Address.class);
                        addressNew.setPersonGroup(model.getCandidatePersonGroup());
                        DicAddressType residence = commonService.getEntity(DicAddressType.class, "RESIDENCE");
                        addressNew.setAddressType(residence);
                        addressNew.setStartDate(CommonUtils.getSystemDate());
                        addressNew.setEndDate(CommonUtils.getEndOfTime());
                        AbstractEditor fromRequisitionJobRequest = openEditor(addressNew, WindowManager.OpenType.DIALOG, ParamsMap.of("fromRequisitionJobRequest", new Object()));
                        fromRequisitionJobRequest.addCloseWithCommitListener(() -> {
                            cityValue.setValue(((Address) fromRequisitionJobRequest.getItem()).getCity());
                            addOrEdit.setIcon("font-icon:PENCIL");
                        });
                    }
                } else {
                    if (addOrEdit.getIcon().equals("font-icon:PLUS")) {
                        Address addressNew = metadata.create(Address.class);
                        addressNew.setPersonGroup(model.getCandidatePersonGroup());
                        DicAddressType residence = commonService.getEntity(DicAddressType.class, "RESIDENCE");
                        addressNew.setAddressType(residence);
                        addressNew.setStartDate(CommonUtils.getSystemDate());
                        addressNew.setEndDate(CommonUtils.getEndOfTime());
                        AbstractEditor fromRequisitionJobRequest = openEditor(addressNew, WindowManager.OpenType.DIALOG, ParamsMap.of("fromRequisitionJobRequest", new Object()));
                        fromRequisitionJobRequest.addCloseWithCommitListener(() -> {
                            cityValue.setValue(((Address) fromRequisitionJobRequest.getItem()).getCity());
                            addOrEdit.setIcon("font-icon:PENCIL");
                            addressInner = (Address) fromRequisitionJobRequest.getItem();
                        });
                    } else if (addOrEdit.getIcon().equals("font-icon:PENCIL")) {
                        AbstractEditor fromRequisitionJobRequest = null;
                        if (addressToEditor != null) {

                            fromRequisitionJobRequest = openEditor(addressToEditor, WindowManager.OpenType.DIALOG, ParamsMap.of("fromRequisitionJobRequest", new Object()));
                            AbstractEditor finalFromRequisitionJobRequest = fromRequisitionJobRequest;
                            fromRequisitionJobRequest.addCloseWithCommitListener(() -> {
                                cityValue.setValue(((Address) finalFromRequisitionJobRequest.getItem()).getCity());
                            });
                        } else if (addressInner != null) {
                            fromRequisitionJobRequest = openEditor(addressInner, WindowManager.OpenType.DIALOG, ParamsMap.of("fromRequisitionJobRequest", new Object()));
                            AbstractEditor finalFromRequisitionJobRequest1 = fromRequisitionJobRequest;
                            fromRequisitionJobRequest.addCloseWithCommitListener(() -> {
                                cityValue.setValue(((Address) finalFromRequisitionJobRequest1.getItem()).getCity());
                            });
                        }
                    }
                }
            }
        });
        HBoxLayout cityValueBox = componentsFactory.createComponent(HBoxLayout.class);
        cityValueBox.add(cityValue);
        cityValueBox.add(addOrEdit);

        addRowForPerson(mainInfoGridRight, mobile, mobileValue);
        addRowForPerson(mainInfoGridRight, email, emailValue);

        addRowForPerson(mainInfoGridRight, city, cityValueBox);


        hBoxLayoutPD.add(mainInfoGridRight);
        vBoxLayoutPD.add(hBoxLayoutPD);
        Component personblock = createBlock(m(CardBlockType.PERSONDATA.messageKey), vBoxLayoutPD);
        components.add(personblock);
        putFilteringComponent(CardBlockType.PERSONDATA, personblock);

        //Интервью
        List<RequisitionHiringStep> hiringSteps = requisitionDs.getItem().getHiringSteps();
        if (hiringSteps != null && !hiringSteps.isEmpty()) {
            GridLayout gridLayout = componentsFactory.createComponent(GridLayout.class);
            gridLayout.setColumns(5);
            gridLayout.setSpacing(true);
            gridLayout.setMargin(false, false, false, true);
            gridLayout.setRows(hiringSteps.size());

            int row = 0;
            for (RequisitionHiringStep hiringStep : hiringSteps) {
                row = addInterviewRow(model, gridLayout, row, hiringStep);
            }

            Component block = createBlock(m(CardBlockType.INTERVIEWS.messageKey), gridLayout);
            components.add(block);

            putFilteringComponent(CardBlockType.INTERVIEWS, block);
        }

        //Опыт работы
        List<PersonExperience> experiences = model.getCandidatePersonGroup().getPersonExperience();
        if (experiences != null && !experiences.isEmpty()) {
            VBoxLayout vBoxLayout = componentsFactory.createComponent(VBoxLayout.class);
            vBoxLayout.setSpacing(true);

            for (PersonExperience experience : experiences) {
                HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
                hBoxLayout.setSpacing(true);
                hBoxLayout.setWidthFull();

                hBoxLayout.add(paddingLeft());
                hBoxLayout.add(recordIcon());

                String company = String.format("%s (%s)", experience.getCompany(), experience.getJob());

                Label companyLbl = label(company, "");
                hBoxLayout.add(companyLbl);
                Date endPeriod = experience.getEndMonth() == null ? new Date() : experience.getEndMonth();
                String period = null;
                if (experience.getEndMonth() == null) {
                    period = String.format(getMessage("experience.period.until.now"),
                            periodFormat.format(experience.getStartMonth()));
                } else {
                    period = String.format(getMessage("experience.period"),
                            periodFormat.format(experience.getStartMonth()),
                            periodFormatEnd.format(endPeriod).toLowerCase() +
                                    " " + periodFormatEndYear.format(endPeriod));
                }
                hBoxLayout.add(label(period, ""));
                hBoxLayout.expand(companyLbl);

                vBoxLayout.add(hBoxLayout);
            }

            Component block = createBlock(m(CardBlockType.EXPERIENCE.messageKey), vBoxLayout);
            components.add(block);

            putFilteringComponent(CardBlockType.EXPERIENCE, block);
        }

        //Образование
        List<PersonEducation> educations = model.getCandidatePersonGroup().getPersonEducation();
        /*List<PersonEducation> educations = commonService.getEntities(PersonEducation.class,
                "select e from tsadv$PersonEducation e where e.personGroup.id=:personGroupId",
                ParamsMap.of("personGroupId", model.getCandidatePersonGroup().getId()),
                "_local");*/
        if (educations != null && !educations.isEmpty()) {
            VBoxLayout vBoxLayout = componentsFactory.createComponent(VBoxLayout.class);
            vBoxLayout.setSpacing(true);

            for (PersonEducation personEducation : educations) {
                HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
                hBoxLayout.setSpacing(true);
                hBoxLayout.add(paddingLeft());
                hBoxLayout.add(recordIcon());

                String formatted = personEducation.getSchool() + " (" +
                        personEducation.getStartYear() + " - " +
                        personEducation.getEndYear() + ")";
                if (personEducation.getSpecialization() != null && !personEducation.getSpecialization().isEmpty()) {
                    formatted = formatted + ", " + personEducation.getSpecialization();
                }
                if (personEducation.getLocation() != null && !personEducation.getLocation().isEmpty()) {
                    formatted = formatted + ", " + personEducation.getLocation();
                }
                formatted = formatted + ".";

                Label valueLabel = label(formatted, "");
                hBoxLayout.add(valueLabel);
                hBoxLayout.expand(valueLabel);

                vBoxLayout.add(hBoxLayout);
            }

            Component block = createBlock(m(CardBlockType.EDUCATION.messageKey), vBoxLayout);
            components.add(block);

            putFilteringComponent(CardBlockType.EDUCATION, block);
        }

        //Requirement
        if (recruitmentConfig.getEnableCandidateRequirements()) {
            createRequirementBlock(model, components);
        }

        //Компетенции
        createCompetenceBlock(model, components);
        //Компетенции только языки
//        String query = "select e from tsadv$CompetenceElement e \n" +
//                "                       join e.competenceGroup.list c\n" +
//                "                       where c.competeceType.code = 'LANGUAGES'\n" +
//                "                       and e.personGroup.id = :personGroupId\n" +
//                "                       and :sysDate between c.startDate and c.endDate\n" +
//                "                       and :sysDate between c.competeceType.startDate and c.competeceType.endDate";
//        Map<String, Object> map = new HashMap<>();
//        map.put("personGroupId", model.getCandidatePersonGroup() != null ? model.getCandidatePersonGroup().getId() : null);
//        map.put("sysDate", CommonUtils.getSystemDate());
        List<CompetenceElement> competenceElements = model.getCandidatePersonGroup().getCompetenceElements();
        List<CompetenceElement> competenceLanguages = new ArrayList<>();
//        competenceLanguages = commonService.getEntities(CompetenceElement.class, query, map, "competenceElement.for.language");
        for (CompetenceElement competenceElement : competenceElements) {
            if (competenceElement != null && competenceElement.getCompetenceGroup() != null
                    && competenceElement.getCompetenceGroup().getCompetence() != null
                    && competenceElement.getCompetenceGroup().getCompetence().getCompeteceType() != null
                    && competenceElement.getCompetenceGroup().getCompetence().getCompeteceType().getCode() != null
                    && competenceElement.getCompetenceGroup().getCompetence().getCompeteceType().getCode().equals("LANGUAGES")
                    && competenceElement.getCompetenceGroup().getCompetence().getStartDate() != null
                    && competenceElement.getCompetenceGroup().getCompetence().getEndDate() != null
                    && competenceElement.getCompetenceGroup().getCompetence().getCompeteceType().getStartDate() != null
                    && competenceElement.getCompetenceGroup().getCompetence().getCompeteceType().getEndDate() != null
                    && !CommonUtils.getSystemDate().before(competenceElement.getCompetenceGroup().getCompetence().getStartDate())
                    && !CommonUtils.getSystemDate().after(competenceElement.getCompetenceGroup().getCompetence().getEndDate())
                    && !CommonUtils.getSystemDate().after(competenceElement.getCompetenceGroup().getCompetence().getCompeteceType().getEndDate())
                    && !CommonUtils.getSystemDate().before(competenceElement.getCompetenceGroup().getCompetence().getCompeteceType().getStartDate())) {
                competenceLanguages.add(competenceElement);
            }
        }
        if (competenceLanguages != null && !competenceLanguages.isEmpty()) {
            VBoxLayout vBoxLayout = componentsFactory.createComponent(VBoxLayout.class);
            vBoxLayout.setSpacing(true);

            for (CompetenceElement competenceElement : competenceLanguages) {
                HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
                hBoxLayout.setSpacing(true);
                hBoxLayout.add(paddingLeft());
                hBoxLayout.add(recordIcon());

                String formatted = competenceElement.getCompetenceGroup().getCompetence().getCompetenceName() + " - " + competenceElement.getScaleLevel().getLevelName();
                Label valueLabel = label(formatted, "");
                hBoxLayout.add(valueLabel);
                hBoxLayout.expand(valueLabel);
                vBoxLayout.add(hBoxLayout);

            }
            Component block = createBlock(m(CardBlockType.COMPETENCETYPECODE.messageKey), vBoxLayout);
            components.add(block);

            putFilteringComponent(CardBlockType.COMPETENCETYPECODE, block);
        }


        //Приложения
//        List<PersonAttachment> apps = commonService.getEntities(PersonAttachment.class,
//                "select e from tsadv$PersonAttachment e " +
//                        "where e.personGroup.id=:personGroupId",
//                ParamsMap.of("personGroupId", model.getCandidatePersonGroup().getId()),
//                "personAttachment-view-for-requisitionjobrequest");

        List<PersonAttachment> apps = model.getCandidatePersonGroup().getPersonAttachment();
        if (apps != null && !apps.isEmpty()) {
            VBoxLayout vBoxLayoutDocs = componentsFactory.createComponent(VBoxLayout.class);
            vBoxLayoutDocs.setSpacing(true);
            GridLayout docsList = componentsFactory.createComponent(GridLayout.class);
            docsList.setStyleName("c-card-main-info");
            docsList.setColumns(2);
            for (PersonAttachment document : apps) {
                Label docType = componentsFactory.createComponent(Label.class);
                docType.setValue(document.getCategory().getLangValue());
                LinkButton file = componentsFactory.createComponent(LinkButton.class);
                file.setCaption(document.getAttachment().getName());
                file.setAction(new BaseAction("filedownload") {
                    @Override
                    public void actionPerform(Component component) {
                        super.actionPerform(component);
                        exportDisplay.show(document.getAttachment());
                    }
                });
                addRowForPerson(docsList, docType, file);
            }
            vBoxLayoutDocs.add(docsList);
            Component documents = createBlock(getMessage(CardBlockType.DOCUMENTS.messageKey), vBoxLayoutDocs);
            components.add(documents);
            putFilteringComponent(CardBlockType.DOCUMENTS, documents);
        }


        //Кнопки
        //HBoxLayout buttonsLayout = componentsFactory.createComponent(HBoxLayout.class);
        //buttonsLayout.setSpacing(true);
        if (!isSelfService) {
            /*LinkButton invite = componentsFactory.createComponent(LinkButton.class);
            invite.setIcon("font-icon:CHECK_CIRCLE_O");
            invite.setStyleName("c-card-navigate");
            invite.setCaption(getMessage("JobRequest.invite"));
            invite.setAction(new BaseAction("invite") {
                @Override
                public void actionPerform(Component component) {
                    createInterview(model);
                }
            });
            invite.setEnabled(checkInviteInterviewAccess(model));

            links.add(invite);*/

            LinkButton renouncement = componentsFactory.createComponent(LinkButton.class);
            renouncement.setIcon("font-icon:TIMES_CIRCLE_O");
            renouncement.setStyleName("c-card-navigate");
            renouncement.setCaption(getMessage("JobRequest.renouncement.2"));
            renouncement.setAction(new BaseAction("renouncement") {
                @Override
                public void actionPerform(Component component) {
                    refuseCandidateWithDialog(model);
                }
            });
            renouncement.setEnabled(model.getRequestStatus() == JobRequestStatus.ON_APPROVAL);
            links.add(renouncement);

            LinkButton fastCreateInterview = componentsFactory.createComponent(LinkButton.class);
            fastCreateInterview.setIcon("font-icon:EDIT");
            fastCreateInterview.setStyleName("c-card-navigate");
//        fastCreateInterview.setCaption(messages.getMessage("kz.uco.tsadv.web.modules.recruitment.requisition.frames", "jobRequestsTable.fastCreateInterview.2"));
            fastCreateInterview.setCaption(messages.getMessage("kz.uco.tsadv.web.modules.recruitment.requisition.frames", "fastCreateInterviewDescr"));


            fastCreateInterview.setAction(new BaseAction("fastCreateInterview") {
                @Override
                public void actionPerform(Component component) {
                    JobRequestFastInterviewEdit jobRequestFastEdit = (JobRequestFastInterviewEdit) openEditor(
                            "tsadv$JobRequestFastInterview.edit", model, WindowManager.OpenType.THIS_TAB,
                            ParamsMap.of("requisitionDs", requisitionDs));
                    jobRequestFastEdit.addCloseListener(actionId -> {
                        storeStyleFilter.search();
                    });
                }
            });
            fastCreateInterview.setEnabled(disableWhenDraft ? false : (model.getRequestStatus() != JobRequestStatus.REJECTED));
            links.add(fastCreateInterview);

            LinkButton createOfferLink = componentsFactory.createComponent(LinkButton.class);
            createOfferLink.setStyleName("c-card-navigate");
            createOfferLink.setCaption(messages.getMessage("kz.uco.tsadv.web.modules.recruitment.requisition.frames", "createOffer"));
            createOfferLink.setAction(new BaseAction("createOfferLink") {
                @Override
                public void actionPerform(Component component) {
                    currentCardJobRequest = model;
                    onCreateOfferButtonClick();
                }
            });
            createOfferLink.setEnabled(disableWhenDraft ? false : checkCreateOfferAccess(model));
            links.add(createOfferLink);

            LinkButton isReserveButton = componentsFactory.createComponent(LinkButton.class);
            if (model.getIsReserved()) {
                isReserveButton.setIcon("font-icon:CHECK_CIRCLE_O");
            } else {
                isReserveButton.setIcon("font-icon:CIRCLE_O");
            }

            isReserveButton.setStyleName("c-card-navigate");
            isReserveButton.setCaption(messages.getMessage("kz.uco.tsadv.web.modules.recruitment.requisition.frames", "Reserved"));

            isReserveButton.setAction(new BaseAction("isReserveButton") {
                @Override
                public void actionPerform(Component component) {
                    currentCardJobRequest = model;
                    if (model.getIsReserved()) {
                        excludeFromReserve();
                    } else {
                        sendToReserve();
                    }
                    if (model.getIsReserved()) {
                        isReserveButton.setIcon("font-icon:CHECK_CIRCLE_O");
                    } else {
                        isReserveButton.setIcon("font-icon:CIRCLE_O");
                    }

                }
            });
            isReserveButton.setEnabled(model != null);
            links.add(isReserveButton);

            LinkButton hireLink = componentsFactory.createComponent(LinkButton.class);
            hireLink.setStyleName("c-card-navigate");
            hireLink.setCaption(messages.getMessage("kz.uco.tsadv.web.modules.recruitment.requisition.frames", "jobRequestsTable.hire.candidate"));

            hireLink.setAction(new BaseAction("hireLink") {
                @Override
                public void actionPerform(Component component) {
                    currentCardJobRequest = model;
                    hireCandidate(null);

                }
            });
            hireLink.setEnabled(disableWhenDraft || JobRequestStatus.REJECTED.equals(model.getRequestStatus()) ? false : checkHireActionAccess(model));
            links.add(hireLink);


            LinkButton deleteLink = componentsFactory.createComponent(LinkButton.class);
//        deleteLink.setIcon("font-icon:TIMES_CIRCLE_O");
            deleteLink.setStyleName("c-card-navigate");
            deleteLink.setCaption(messages.getMessage("kz.uco.tsadv.web.modules.recruitment.requisition.frames", "jobRequestsTableRemove"));

            deleteLink.setAction(new BaseAction("deleteLink") {
                @Override
                public void actionPerform(Component component) {
                    currentCardJobRequest = model;
                    Collection<JobRequest> selected = new ArrayList<>();
                    selected.add(currentCardJobRequest);
                    jobRequestsTable.setSelected(selected);
                    removeJobRequest();
                }
            });
            if (!(configuration.getConfig(JobRequestRemoveCandidateConfig.class).getEnabled())) {
                if (model != null) {
                    UserExt userExt = getUserExt(model.getCandidatePersonGroup().getId());
                    Map<String, Object> paramsMap = new HashMap<>();
                    paramsMap.put("idJobRequest", model.getId());

                    Offer offer = null;
                    if (offers == null) {
                        loadLists();
                    } else {
                        offer = offers.stream().filter(offer1 ->
                                offer1.getJobRequest().getId().equals(model.getId()))
                                .findFirst().orElse(null);
                    }
                    if (userExt != null) {
                        if (model.getCreatedBy() != null && (model.getCreatedBy().equals(userExt.getLogin()))) {
                            deleteLink.setEnabled(false);
                        } else {
                            deleteLink.setEnabled(true);
                        }
                    }
                    if (model.getInterview() != null || (offer != null && model.equals(offer.getJobRequest()))) {
                        deleteLink.setEnabled(false);
                    }
                }
            }

            links.add(deleteLink);
        }
        if (isSelfService) {
            links.add(getViewLaterButton(model));
        }
        links.add(getResumeLink(model));
        if (!components.isEmpty()) {
            for (Component component : components) {
                wrapper.add(component);
            }
        }

        return wrapper;
    }

    protected void createRequirementBlock(JobRequest model, List<Component> components) {

        List<RequisitionRequirement> requisitionRequirements = new ArrayList<>();
        requisitionRequirements = requisitionDs.getItem().getRequisitionRequirements();
        List<CandidateRequirement> candidateRequirements = new ArrayList<>();
        candidateRequirements = model.getCandidatePersonGroup().getCandidateRequirement();
        if (requisitionRequirements != null && !requisitionRequirements.isEmpty()) {
            VBoxLayout vBoxLayout = componentsFactory.createComponent(VBoxLayout.class);
            vBoxLayout.setSpacing(true);
            for (RequisitionRequirement requisitionRequirement : requisitionRequirements) {
                HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
                hBoxLayout.setSpacing(true);
                Label iconLabel = null;
                Label requirement = componentsFactory.createComponent(Label.class);
                requirement.setValue(requisitionRequirement.getRequirement().getQuestionText());
                requirement.setId(requisitionRequirement.getId().toString() + "label");
                LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
                linkButton.setCaption("");

                CandidateRequirement candidateRequirementForEdit = null;
                for (CandidateRequirement candidateRequirement : candidateRequirements) {
                    if (candidateRequirement.getRequirement().getId().equals(requisitionRequirement.getRequirement().getId())) {
                        candidateRequirementForEdit = candidateRequirement;
                        linkButton.setIcon("font-icon:PENCIL");
                        if (candidateRequirement.getLevel() != null) {
                            requirement.setValue(((String) requirement.getValue()) + " - " + candidateRequirement.getLevel().getAnswerText());
                            if (candidateRequirement.getLevel().getOrder() >= requisitionRequirement.getRequirementLevel().getOrder()) {
                                iconLabel = icon(requisitionRequirement.getId().toString(), "font-icon:CHECK", "c-card-record-icon green");
                            }
                        }
                        break;
                    }
                }
                if (linkButton.getIcon() == null || !linkButton.getIcon().equals("font-icon:PENCIL")) {
                    linkButton.setIcon("font-icon:PLUS");
                }
                linkButton.setId(requisitionRequirement.getId().toString() + "link");
                if (iconLabel == null) {
                    iconLabel = icon(requisitionRequirement.getId().toString(), "font-icon:CLOSE", "c-card-record-icon red");
                }
                hBoxLayout.add(iconLabel);


                final CandidateRequirement finalCandidateRequirementForEdit = candidateRequirementForEdit;
                linkButton.setAction(new BaseAction("editCandidateRequirement") {
                    CandidateRequirement candidateRequirementInner;

                    @Override
                    public void actionPerform(Component component) {
                        AbstractEditor abstractEditor = null;
                        if (finalCandidateRequirementForEdit != null) {
                            candidateRequirementInner = finalCandidateRequirementForEdit;
                            if (linkButton.getIcon().equals("font-icon:PENCIL")) {
                                CandidateRequirement reload = dataManager.reload(finalCandidateRequirementForEdit, "candidateRequirement-view");
                                abstractEditor = openEditor(reload, WindowManager.OpenType.DIALOG,
                                        ParamsMap.of("fromJobRequest", true));
                            }
                        } else {
                            if (linkButton.getIcon().equals("font-icon:PLUS")) {
                                CandidateRequirement candidateRequirement = metadata.create(CandidateRequirement.class);
                                candidateRequirement.setPersonGroup(model.getCandidatePersonGroup());
                                candidateRequirement.setRequirement(requisitionRequirement.getRequirement());
                                abstractEditor = openEditor(candidateRequirement, WindowManager.OpenType.DIALOG,
                                        ParamsMap.of("fromJobRequest", true));
                            } else if (linkButton.getIcon().equals("font-icon:PENCIL")
                                    && candidateRequirementInner != null) {
                                CandidateRequirement reload = dataManager.reload(candidateRequirementInner, "candidateRequirement-view");
                                abstractEditor = openEditor(reload, WindowManager.OpenType.DIALOG,
                                        ParamsMap.of("fromJobRequest", true));
                            }
                        }
                        if (abstractEditor != null) {

                            AbstractEditor finalAbstractEditor1 = abstractEditor;
                            abstractEditor.addCloseWithCommitListener(() -> {
                                candidateRequirementInner = (CandidateRequirement) finalAbstractEditor1.getItem();
                                Component iconLabelComponent = hBoxLayout.getOwnComponent(requisitionRequirement.getId().toString());
                                Label iconLabel = (Label) iconLabelComponent;
                                if (iconLabel != null) {
                                    if (candidateRequirementInner.getLevel().getOrder() >= requisitionRequirement.getRequirementLevel().getOrder()) {
                                        iconLabel.setIcon("font-icon:CHECK");
                                        iconLabel.setStyleName("c-card-record-icon green");
                                    } else {

                                        iconLabel.setIcon("font-icon:CLOSE");
                                        iconLabel.setStyleName("c-card-record-icon red");
                                    }

                                }
                                Component linkButtonComponent = hBoxLayout.getOwnComponent(requisitionRequirement.getId().toString() + "link");
                                LinkButton linkButton = (LinkButton) linkButtonComponent;
                                if (linkButton != null && linkButton.getIcon().equals("font-icon:PLUS")) {
                                    linkButton.setIcon("font-icon:PENCIL");
                                }
                                Component labelComponent = hBoxLayout.getOwnComponent(requisitionRequirement.getId().toString() + "label");
                                Label label = (Label) labelComponent;
                                label.setValue(candidateRequirementInner.getRequirement().getQuestionText() + " - " + candidateRequirementInner.getLevel().getAnswerText());
                            });
                        }
                    }
                });

                hBoxLayout.add(requirement);
                hBoxLayout.add(linkButton);
                vBoxLayout.add(hBoxLayout);
            }
            Component block = createBlock(m(CardBlockType.REQUIREMENTS.messageKey), vBoxLayout);
            components.add(block);

            putFilteringComponent(CardBlockType.REQUIREMENTS, block);
        }
    }

    protected int addInterviewRow(JobRequest model, GridLayout gridLayout, int row, RequisitionHiringStep hiringStep) {
        List<Interview> interviews = model.getInterviews();
        Interview findInterview = null;
        if (interviews != null && !interviews.isEmpty()) {
            for (Interview interview : interviews) {
                if (interview.getRequisitionHiringStep().equals(hiringStep)) {
                    findInterview = interview;
                    break;
                }
            }
        }

        Component icon;
        if (findInterview == null) {
            icon = icon("font-icon:CIRCLE_O", "c-card-record-icon");
        } else {
            switch (findInterview.getInterviewStatus()) {
                case COMPLETED: {
                    icon = icon("font-icon:CHECK", "c-card-record-icon green");
                    break;
                }
                case FAILED: {
                    icon = icon("font-icon:CLOSE", "c-card-record-icon red");
                    break;
                }
                default: {
                    icon = icon("font-icon:EXCLAMATION", "c-card-record-icon yellow");
                }
            }
        }

        gridLayout.add(icon, 0, row);

        LinkButton linkButton = createLinkButtonInterview(findInterview, hiringStep, model, new HashMap<String, Object>());
        gridLayout.add(linkButton, 1, row);

        Label statusLabel = componentsFactory.createComponent(Label.class);
        statusLabel.setAlignment(Alignment.TOP_LEFT);
        if (findInterview == null) {
            statusLabel.setValue("");
        } else {
            statusLabel.setValue(messages.getMessage(findInterview.getInterviewStatus()));
        }
        gridLayout.add(statusLabel, 2, row);

        VBoxLayout questionnairesWrapper = componentsFactory.createComponent(VBoxLayout.class);
        if (findInterview != null) {
            List<InterviewQuestionnaire> questionnaires = findInterview.getQuestionnaires();
            if (questionnaires != null && !questionnaires.isEmpty()) {
                for (InterviewQuestionnaire questionnaire : questionnaires) {
                    LinkButton questionnaireLink = componentsFactory.createComponent(LinkButton.class);
                    questionnaireLink.setStyleName("c-card-record-q");
                    questionnaireLink.setCaption(questionnaire.getQuestionnaire().getName());
                    questionnaireLink.setAction(new BaseAction("open-result") {
                        @Override
                        public void actionPerform(Component component) {
                            openWindow("interview-pre-screen",
                                    WindowManager.OpenType.DIALOG,
                                    ParamsMap.of("CARD_INTERVIEW_QUESTIONNAIRE_ID", questionnaire));
                        }
                    });
                    questionnairesWrapper.add(questionnaireLink);
                }
            }
        }
        gridLayout.add(questionnairesWrapper, 3, row);

        Label commentLabel = componentsFactory.createComponent(Label.class);
        commentLabel.setAlignment(Alignment.TOP_LEFT);
        commentLabel.setWidth("250px");
        commentLabel.setStyleName("c-card-record-comment");
        if (findInterview != null) {
            String comment = "";
            if (findInterview.getComment() != null && !findInterview.getComment().equals("")) {
                comment = comment + findInterview.getComment();
                if (findInterview.getInterviewStatus().equals(InterviewStatus.FAILED) &&
                        findInterview.getInterviewReason() != null &&
                        findInterview.getInterviewReason().getLangValue() != null &&
                        !findInterview.getInterviewReason().getLangValue().equals("")) {
                    comment = comment + " / " + findInterview.getInterviewReason().getLangValue();
                }
            }
            commentLabel.setValue(comment);
        }
        gridLayout.add(commentLabel, 4, row);

        row++;
        return row;
    }

    protected LinkButton createLinkButtonInterview(Interview interview, RequisitionHiringStep hiringStep, JobRequest model, Map<String, Object> params) {
        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        linkButton.setStyleName("c-card-record-hs");
        linkButton.setAlignment(Alignment.TOP_LEFT);
        linkButton.setCaption(hiringStep.getHiringStep().getStepName());
        params.put("totalInterviewsCount", model.getTotalInterviews());
        if (interview == null) {
            linkButton.addStyleName("red");
            linkButton.setAction(new BaseAction("create-interview") {
                @Override
                public void actionPerform(Component component) {
                    createInterview(model, hiringStep);
                }
            });
            linkButton.setEnabled(!model.getRequestStatus().equals(JobRequestStatus.DRAFT) && checkInviteInterviewAccess(model));
        } else {
            linkButton.setAction(new BaseAction("create-interview") {
                @Override
                public void actionPerform(Component component) {
                    AbstractEditor editor = openEditor(interview, WindowManager.OpenType.THIS_TAB, params);
                    editor.addCloseListener(actionId -> storeStyleFilter.search());
                }
            });
            linkButton.setEnabled(!model.getRequestStatus().equals(JobRequestStatus.DRAFT));
        }
        return linkButton;
    }

    protected void createCompetenceBlock(JobRequest model, List<Component> components) {
//        String query = "select e from tsadv$CompetenceElement e \n" +
//                "                       join e.competenceGroup.list c\n" +
//                "                       where c.competeceType.code <> 'LANGUAGES'\n" +
//                "                       and e.personGroup.id = :personGroupId\n" +
//                "                       and :sysDate between c.startDate and c.endDate\n" +
//                "                       and :sysDate between c.competeceType.startDate and c.competeceType.endDate";
//        Map<String, Object> map = new HashMap<>();
//        map.put("personGroupId", model.getCandidatePersonGroup() != null ? model.getCandidatePersonGroup().getId() : null);
//        map.put("sysDate", CommonUtils.getSystemDate());
        List<CompetenceElement> competences = new ArrayList<>();
        List<CompetenceElement> competenceElements = model.getCandidatePersonGroup().getCompetenceElements();
        for (CompetenceElement competenceElement : competenceElements) {
            if (competenceElement != null && competenceElement.getCompetenceGroup() != null
                    && competenceElement.getCompetenceGroup().getCompetence() != null
                    && competenceElement.getCompetenceGroup().getCompetence().getStartDate() != null
                    && competenceElement.getCompetenceGroup().getCompetence().getEndDate() != null
                    && !CommonUtils.getSystemDate().before(competenceElement.getCompetenceGroup().getCompetence().getStartDate())
                    && !CommonUtils.getSystemDate().after(competenceElement.getCompetenceGroup().getCompetence().getEndDate())) {
                if (competenceElement.getCompetenceGroup().getCompetence().getCompeteceType() == null) {
                    competences.add(competenceElement);
                } else if (competenceElement.getCompetenceGroup().getCompetence().getCompeteceType().getCode() == null) {
                    competences.add(competenceElement);
                } else if (!competenceElement.getCompetenceGroup().getCompetence().getCompeteceType().getCode().equals("LANGUAGES")) {
                    competences.add(competenceElement);
                }
            }
        }
//        competences = commonService.getEntities(CompetenceElement.class, query, map, "competenceElement.for.language");

        if (competences != null && !competences.isEmpty()) {
            VBoxLayout vBoxLayout = componentsFactory.createComponent(VBoxLayout.class);
            vBoxLayout.setSpacing(true);

            for (CompetenceElement competenceElement : competences) {
                Competence competence = competenceElement.getCompetenceGroup().getCompetence();
                if (competence != null) {
                    HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
                    hBoxLayout.setSpacing(true);
                    hBoxLayout.add(paddingLeft());
                    hBoxLayout.add(recordIcon());

                    String formatted = competence.getCompetenceName() + " - " +
                            competenceElement != null ? competenceElement.getScaleLevel() != null ? competenceElement.getScaleLevel().getLevelName() : null : null;

                    Label valueLabel = label(formatted, "");
                    hBoxLayout.add(valueLabel);
                    hBoxLayout.expand(valueLabel);
                    vBoxLayout.add(hBoxLayout);
                }
            }
            Component block = createBlock(m(CardBlockType.COMPETENCE.messageKey), vBoxLayout);
            components.add(block);

            putFilteringComponent(CardBlockType.COMPETENCE, block);

//            components.get(components.size() - 1).setVisible(false);
        }
    }

    protected Component getResumeLink(JobRequest model) {
        LinkButton resume = componentsFactory.createComponent(LinkButton.class);
        resume.setStyleName("c-card-navigate");
        resume.setCaption(getMessage("resume"));

        resume.setAction(new BaseAction("resume") {
            @Override
            public void actionPerform(Component component) {

                printResume(model.getCandidatePersonGroup());

            }
        });

        return resume;
    }

    protected void printResume(PersonGroupExt personGroupExt) {
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

    public void createResume() {
        printResume(jobRequestsTable.getSingleSelected().getCandidatePersonGroup());
    }

    protected boolean checkInviteInterviewAccess(JobRequest model) {
        if (model == null) return false;
        Requisition requisition = requisitionDs.getItem();
        if (requisition.getHiringSteps() != null) {
            if (requisition.getHiringSteps().size() > 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    protected void putFilteringComponent(CardBlockType cardBlockType, Component component) {
        checkBoxComponentMap.get(cardBlockType).add(component);
        Collection<KeyValueEntity> items = cardFilterSettingsDs.getItems();
        for (KeyValueEntity item : items) {
            if (cardBlockType.getMessageKey().equals(item.getValue("cardBlockType"))) {
                component.setVisible(item.getValue("checked"));
            }
        }
    }

    protected Label recordIcon() {
        Label label = label("", "c-card-record-icon");
        label.setAlignment(Alignment.MIDDLE_LEFT);
        label.setIcon("font-icon:CIRCLE_O");
        return label;
    }

    protected Label icon(String icon, String cssClass) {
        Label label = label("", "c-card-record-icon");
        label.setAlignment(Alignment.TOP_LEFT);
        label.setIcon(icon);
        label.addStyleName(cssClass);
        return label;
    }

    protected Label icon(String id, String icon, String cssClass) {
        Label label = icon(icon, cssClass);
        label.setId(id);
        return label;
    }

    protected Label paddingLeft() {
        Label label = label("");
        label.setAlignment(Alignment.MIDDLE_RIGHT);
        label.setWidth("10px");
        return label;
    }

    protected Component createBlock(String caption, Component component) {
        VBoxLayout vBoxLayout = componentsFactory.createComponent(VBoxLayout.class);
        vBoxLayout.setStyleName("c-card-content-item");
        if (!caption.equals(m(CardBlockType.PERSONDATA.messageKey))) {
            vBoxLayout.add(label(caption, "c-card-content-item-title"));
        }
        component.setStyleName("c-card-content-item-body");
        vBoxLayout.add(component);
        vBoxLayout.setSpacing(true);
        return vBoxLayout;
    }

    protected LinkButton link(String caption) {
        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        linkButton.setCaption(caption);
        return linkButton;
    }

    protected LinkButton link(String caption, String cssClass) {
        LinkButton linkButton = link(caption);
        linkButton.setStyleName(cssClass);
        return linkButton;
    }

    protected Label label(String value) {
        Label label = componentsFactory.createComponent(Label.class);
        label.setValue(value);
        return label;
    }

    protected Label label(String value, String cssClass) {
        Label label = label(value);
        label.setStyleName(cssClass);
        return label;
    }

    protected String m(Class caller, String key) {
        return messages.getMessage(caller, key);
    }

    protected String m(String key) {
        return getMessage(key);
    }

    protected String m(Enum enum_) {
        return messages.getMessage(enum_);
    }

    protected Component createContainer(String containerName, List<Component> containerDataList) {
        VBoxLayout container = componentsFactory.createComponent(VBoxLayout.class);
        Label containerCaption = componentsFactory.createComponent(Label.class);
        VBoxLayout containerData = componentsFactory.createComponent(VBoxLayout.class);

        container.setSpacing(true);
        containerCaption.setValue(containerName + ": ");
        containerCaption.setStyleName("small-gray");
        containerData.setMargin(false, false, false, true);
        containerData.setSpacing(true);

        for (Component component : containerDataList) {
            containerData.add(component);
        }
        container.add(containerCaption);
        container.add(containerData);

        return container;
    }

    protected void toTable() {
        isCard = false;
        cardOrTable.setIcon("images/fa-card-id.png");

        remove(cardBoxLayout);
        add(vbox1);
        expand(vbox1);
        setVisibleExcessButtons(true);
    }

    public void createGroupInterview() {
        Map<String, Object> map = new HashMap<>();
        map.put("systemDate", CommonUtils.getSystemDate());
        map.put("requisitionDs", requisitionDs.getItem().getId());

        Set<JobRequest> jobRequests = commonService.getEntities(JobRequest.class,
                "select e" +
                        " from tsadv$JobRequest e" +
                        " join e.candidatePersonGroup cpg" +
                        " join cpg.list cp" +
                        " where e.requisition.id = :requisitionDs" +
                        " and :systemDate between cp.startDate and cp.endDate"
                , map, "jobRequest.view").stream().collect(Collectors.toSet());

        RequisitionHiringStep requisitionHiringStep = null;
        try {
            requisitionHiringStep = getNextRequisitionHiringStep(jobRequests);
            if (requisitionHiringStep == null)
                showNotification(getMessage("RequisitionJobRequest.noAvailableHiringSteps"));
            else {
                Interview groupInterview = metadata.create(Interview.class);
                groupInterview.setRequisitionHiringStep(requisitionHiringStep);
                groupInterview.setIsGroup(true);
                groupInterview.setRequisition(requisitionDs.getItem());

                Map<String, Object> params = new HashMap<>();
                params.put("jobRequests", jobRequests);

                AbstractEditor<Interview> interviewEditor = openEditor(
                        "tsadv$GroupInterview.edit",
                        groupInterview, WindowManager.OpenType.THIS_TAB,
                        params);
                interviewEditor.addCloseWithCommitListener(() -> {
                    refreshFilterAndDs();
                });
            }
        } catch (Exception e) {
            showNotification(e.getMessage());
            e.printStackTrace();
        }

    }


//    public void createGroupInterviewOld() {
//        Set<JobRequest> jobRequests = jobRequestsTable.getSelected();
//
//        try {
//            RequisitionHiringStep requisitionHiringStep = getNextRequisitionHiringStep(jobRequests);
//
//            if (requisitionHiringStep == null)
//                showNotification(getMessage("RequisitionJobRequest.noAvailableHiringSteps"));
//            else {
//                Interview groupInterview = metadata.create(Interview.class);
//                groupInterview.setRequisitionHiringStep(requisitionHiringStep);
//                groupInterview.setIsGroup(true);
//                groupInterview.setRequisition(requisitionDs.getItem());
//
//                Map<String, Object> params = new HashMap<>();
//                params.put("jobRequests", jobRequests);
//
//                AbstractEditor<Interview> interviewEditor = openEditor("tsadv$GroupInterview.edit", groupInterview, WindowManager.OpenType.THIS_TAB, params);
//                interviewEditor.addCloseWithCommitListener(() -> {
//                    jobRequestsDs.refresh();
//                });
//            }
//        } catch (Exception e) {
//            showNotification(e.getMessage());
//            e.printStackTrace();
//        }
//    }

    protected RequisitionHiringStep getNextRequisitionHiringStep(Set<JobRequest> jobRequests) throws Exception {
        RequisitionHiringStep requisitionHiringStep = null;
        Map<String, Object> params = new HashMap<>();
        if (jobRequests.stream().map(jr -> jr.getId()).collect(Collectors.toList()) != null) {
            params.put("jobRequestIds", jobRequests.stream().map(jr -> jr.getId()).collect(Collectors.toList()));
        } else {
            params.put("jobRequestIds", new HashSet<>());
        }
        Map<String, Object> avparams = new HashMap<>();

        avparams.put("requisitionId", requisitionDs.getItem().getId());
        List<RequisitionHiringStep> availableSteps = null;
        try {
            availableSteps = commonService.getEntities(RequisitionHiringStep.class,
                    "select e " +
                            " from tsadv$RequisitionHiringStep e " +
                            " where e.requisition.id = :requisitionId " +
                            " order by e.order", avparams, "requisitionHiringStep.view");

        } catch (Exception e) {

        }
        if (availableSteps != null && !availableSteps.isEmpty()) {
            requisitionHiringStep = availableSteps.get(0);
        } else
            throw new Exception(messages.getMainMessage("RequisitionJobRequest.allStepsCompleted"));

        return requisitionHiringStep;
    }

//    protected RequisitionHiringStep getNextRequisitionHiringStepOld(Set<JobRequest> jobRequests) throws Exception {
//        RequisitionHiringStep requisitionHiringStep = null;
//        Map<String, Object> params = new HashMap<>();
//        if (jobRequests.stream().map(jr -> jr.getId()).collect(Collectors.toList()) != null) {
//            params.put("jobRequestIds", jobRequests.stream().map(jr -> jr.getId()).collect(Collectors.toList()));
//        } else {
//            params.put("jobRequestIds", new HashSet<>());
//        }
//        List<RequisitionHiringStep> incompleteHiringSteps = commonService.getEntities(RequisitionHiringStep.class,
//                "select e " +
//                        " from tsadv$RequisitionHiringStep e, tsadv$Interview i" +
//                        " where i.jobRequest.id in :jobRequestIds" +
//                        " and e.id = i.requisitionHiringStep.id " +
//                        " and i.interviewStatus in ( 20, 30)",
//                params,
//                View.LOCAL);
//
//        if (incompleteHiringSteps != null && !incompleteHiringSteps.isEmpty())
//            throw new Exception(messages.getMainMessage("RequisitionJobRequest.incompleteHiringStep"));
//
//        List<RequisitionHiringStep> failedHiringSteps = commonService.getEntities(RequisitionHiringStep.class,
//                "select e " +
//                        " from tsadv$RequisitionHiringStep e, tsadv$Interview i" +
//                        " where i.jobRequest.id in :jobRequestIds" +
//                        " and e.id = i.requisitionHiringStep.id " +
//                        " and i.interviewStatus = 60",
//                params,
//                View.LOCAL);
//
//        if (failedHiringSteps != null && !failedHiringSteps.isEmpty())
//            throw new Exception(messages.getMainMessage("RequisitionJobRequest.failedHiringStep"));
//
//        params.put("requisitionId", requisitionDs.getItem().getId());
//        List<RequisitionHiringStep> availableSteps = null;
//        try {
//            availableSteps = commonService.getEntities(RequisitionHiringStep.class,
//                    "select e " +
//                            " from tsadv$RequisitionHiringStep e " +
//                            " where e.requisition.id = :requisitionId " +
//                            " and not exists (select 1 " +
//                            " from tsadv$Interview i " +
//                            " where i.jobRequest.id in :jobRequestIds " +
//                            " and i.requisitionHiringStep.id = e.id " +
//                            " and i.interviewStatus <> 50) " +
//                            " and not exists (select 1 " +
//                            " from tsadv$Interview i, tsadv$RequisitionHiringStep hs" +
//                            " where i.jobRequest.id in :jobRequestIds" +
//                            " and hs.id = i.requisitionHiringStep.id " +
//                            " and hs.order < e.order " +
//                            " and i.interviewStatus in ( 20, 30, 60, 70)) " +
//                            " order by e.order ",
//                    params,
//                    "requisitionHiringStep.view");
//        } catch (Exception e) {
//
//        }
//        if (availableSteps != null && !availableSteps.isEmpty()) {
//            requisitionHiringStep = availableSteps.get(0);
//        } else
//            throw new Exception(messages.getMainMessage("RequisitionJobRequest.allStepsCompleted"));
//
//        return requisitionHiringStep;
//    }

    protected void customizeJobRequestsTable() {
        addViewLaterColumn();
        addLinkedinColumn();
        addCandidatePersonGroupColumn();
        addContactsColumn();
        addAttachmentsColumn();
        addLastStepColumn();
        addStepsColumn();
        addCompetenceQuestionnairesColumn();
        addTestResultsQuestionnairesColumn();
        addVideoInterviewColumn();
        addOfferLink();
    }

    protected void addLastStepColumn() {
        DataGrid.Column lastStepColumn = jobRequestsTable.addGeneratedColumn("lastStep", new DataGrid.ColumnGenerator<JobRequest, Component>() {
            @Override
            public Component getValue(DataGrid.ColumnGeneratorEvent<JobRequest> event) {
                return generateInterview(event.getItem());
            }

            @Override
            public Class<Component> getType() {
                return Component.class;
            }
        });
        lastStepColumn.setRenderer(new WebComponentRenderer());
    }

    protected void addOfferLink() {
        DataGrid.Column lastStepColumn = jobRequestsTable.addGeneratedColumn("requestStatus", new DataGrid.ColumnGenerator<JobRequest, Component>() {
            @Override
            public Component getValue(DataGrid.ColumnGeneratorEvent<JobRequest> event) {
                return generateOffer(event.getItem());
            }

            @Override
            public Class<Component> getType() {
                return Component.class;
            }
        });
        lastStepColumn.setRenderer(new WebComponentRenderer());
    }

    protected void addViewLaterColumn() {
        DataGrid.Column viewLaterColumn = jobRequestsTable.addGeneratedColumn("viewLater", new DataGrid.ColumnGenerator<JobRequest, Component>() {
            @Override
            public Component getValue(DataGrid.ColumnGeneratorEvent<JobRequest> event) {
                return getViewLaterButton(event.getItem());
            }

            @Override
            public Class<Component> getType() {
                return Component.class;
            }
        });
        viewLaterColumn.setRenderer(new WebComponentRenderer());
        jobRequestsTable.getHeaderRow(0).getCell("viewLater").setComponent(getViewLaterButton(null));
    }

    protected void addLinkedinColumn() {
        DataGrid.Column linkedinLinkColumn = jobRequestsTable.addGeneratedColumn("linkedin", new DataGrid.ColumnGenerator<JobRequest, Component>() {
            @Override
            public Component getValue(DataGrid.ColumnGeneratorEvent<JobRequest> event) {
                return getLinkedinButton(event.getItem());
            }

            @Override
            public Class<Component> getType() {
                return Component.class;
            }
        });
        linkedinLinkColumn.setWidth(30d);
        linkedinLinkColumn.setResizable(false);
        linkedinLinkColumn.setRenderer(new WebComponentRenderer());
        jobRequestsTable.getHeaderRow(0).getCell("linkedin").setComponent(getLinkedinButton(null));
        jobRequestsTable.setStyleName("linkedin-icons-column");
    }

    protected void addCandidatePersonGroupColumn() {
        DataGrid.Column candidatePersonGroupColumn = jobRequestsTable.addGeneratedColumn("candidatePersonGroup", new DataGrid.ColumnGenerator<JobRequest, Component>() {
            @Override
            public Component getValue(DataGrid.ColumnGeneratorEvent<JobRequest> event) {
                return generateCandidatePersonGroupCell(event.getItem());
            }

            @Override
            public Class<Component> getType() {
                return Component.class;
            }
        });
        candidatePersonGroupColumn.setRenderer(new WebComponentRenderer());
    }

    protected void addContactsColumn() {
        DataGrid.Column contactsColumn = jobRequestsTable.addGeneratedColumn("contacts", new DataGrid.ColumnGenerator<JobRequest, Component>() {
            @Override
            public Component getValue(DataGrid.ColumnGeneratorEvent<JobRequest> event) {
                return getContactsLabel(event.getItem());
            }

            @Override
            public Class<Component> getType() {
                return Component.class;
            }
        });
        contactsColumn.setRenderer(new WebComponentRenderer());
    }

    protected void addAttachmentsColumn() {
        DataGrid.Column attachmentsColumn = jobRequestsTable.addGeneratedColumn("attachments", new DataGrid.ColumnGenerator<JobRequest, Component>() {
            @Override
            public Component getValue(DataGrid.ColumnGeneratorEvent<JobRequest> event) {
                return getAttachmentsButton(event.getItem());
            }

            @Override
            public Class<Component> getType() {
                return Component.class;
            }
        });
        attachmentsColumn.setRenderer(new WebComponentRenderer());
        jobRequestsTable.getHeaderRow(0).getCell("attachments").setComponent(getAttachmentsButton(null));
    }

    protected void addStepsColumn() {
        DataGrid.Column stepsColumn = jobRequestsTable.addGeneratedColumn("steps", new DataGrid.ColumnGenerator<JobRequest, Component>() {
            @Override
            public Component getValue(DataGrid.ColumnGeneratorEvent<JobRequest> event) {
                return generatePassedInterviews(event.getItem());
            }

            @Override
            public Class<Component> getType() {
                return Component.class;
            }
        });
        stepsColumn.setRenderer(new WebComponentRenderer());
        questionnairesDs.refresh();
    }

    protected void addCompetenceQuestionnairesColumn() {
        DecimalFormat decimalFormat = getDecimalFormat();

        List<RequisitionQuestionnaire> competenceQuestionnaires = questionnairesDs.getItems().stream().filter(q -> q.getQuestionnaire().getCategory() != null && "COMPETENCE".equals(q.getQuestionnaire().getCategory().getCode())).collect(Collectors.toList());

        if (competenceQuestionnaires != null && !competenceQuestionnaires.isEmpty()) {
            String[] compColumnIds = new String[competenceQuestionnaires.size() + 1];

            for (int i = 0; i < competenceQuestionnaires.size(); i++) {
                compColumnIds[i] = "comp" + i;
                final int idx = i;
                DataGrid.Column col = jobRequestsTable.addGeneratedColumn(compColumnIds[i], new DataGrid.ColumnGenerator<JobRequest, Double>() {
                    @Override
                    public Double getValue(DataGrid.ColumnGeneratorEvent<JobRequest> event) {
                        return event.getItem().getCompetenceQuestionnaireTotals() == null ? null : event.getItem().getCompetenceQuestionnaireTotals().get(competenceQuestionnaires.get(idx).getQuestionnaire().getId());
                    }

                    @Override
                    public Class<Double> getType() {
                        return Double.class;
                    }
                }/*, 10 + i*/);
                col.setCaption(competenceQuestionnaires.get(i).getQuestionnaire().getName());
                col.setRenderer(new WebNumberRenderer(decimalFormat));
            }

            DataGrid.Column tot = jobRequestsTable.addGeneratedColumn("compTotal", new DataGrid.ColumnGenerator<JobRequest, Double>() {
                @Override
                public Double getValue(DataGrid.ColumnGeneratorEvent<JobRequest> event) {
                    return event.getItem().getCompetenceQuestionnaireTotals() == null ? null : event.getItem().getCompetenceQuestionnaireTotals().values().stream().mapToDouble(v -> v).sum();
                }

                @Override
                public Class<Double> getType() {
                    return Double.class;
                }
            });

            compColumnIds[competenceQuestionnaires.size()] = "compTotal";
            tot.setCaption(messages.getMainMessage("JobRequest.total"));
            tot.setRenderer(new WebNumberRenderer(decimalFormat));

            DataGrid.HeaderRow headerRow = jobRequestsTable.prependHeaderRow();
            DataGrid.HeaderCell headerCell = headerRow.join(compColumnIds);
            headerCell.setText(messages.getMainMessage("JobRequest.competenceQuestionnaires"));
            headerCell.setStyleName("table-column-join-header");
        }
    }

    protected void addTestResultsQuestionnairesColumn() {
        List<RequisitionQuestionnaire> testResultsQuestionnaires = questionnairesDs.getItems().stream().filter(q -> q.getQuestionnaire().getCategory() != null && "TEST_RESULTS".equals(q.getQuestionnaire().getCategory().getCode())).collect(Collectors.toList());

        if (testResultsQuestionnaires != null && !testResultsQuestionnaires.isEmpty()) {
            for (int j = 0; j < testResultsQuestionnaires.size(); j++) {

                List<RcQuestion> questions = commonService.getEntities(RcQuestion.class,
                        "select e " +
                                "   from tsadv$RcQuestion e " +
                                "  where e.id in (select qq.question.id from tsadv$RcQuestionnaireQuestion qq where qq.questionnaire.id = :questionnaireId)" +
                                "    and e.answerType in ('SINGLE', 'MULTI', 'NUM')",
                        Collections.singletonMap("questionnaireId", testResultsQuestionnaires.get(j).getQuestionnaire().getId()),
                        View.LOCAL);

                String[] testResColumnIds = new String[questions.size()];

                for (int i = 0; i < questions.size(); i++) {
                    testResColumnIds[i] = j + "question" + i;
                    final int jidx = j;
                    final int iidx = i;
                    jobRequestsDs.refresh();
                    DataGrid.Column col = jobRequestsTable.addGeneratedColumn(testResColumnIds[i], new DataGrid.ColumnGenerator<JobRequest, Double>() {
                        @Override
                        public Double getValue(DataGrid.ColumnGeneratorEvent<JobRequest> event) {

                            Map<UUID, Map<UUID, Double>> questions1 = event.getItem().getTestResultsQuestionnaireQuestions();
                            if (questions1 != null) {
                                Map<UUID, Double> uuidDoubleMap = questions1.get(testResultsQuestionnaires.get(jidx).getQuestionnaire().getId());
                                if (uuidDoubleMap != null) {
                                    Double aDouble = uuidDoubleMap.get(questions.get(iidx).getId());
                                    if (aDouble != null) {
                                        return aDouble;
                                    }
                                }
                            }
                            return null;
                        }

                        @Override
                        public Class<Double> getType() {
                            return Double.class;
                        }
                    });
                    col.setCaption(questions.get(i).getQuestionText());
                    col.setRenderer(new WebNumberRenderer(getDecimalFormat()));
                    if (questions.size() > 0 && jobRequestsDs.getItems().size() > 0) {
                        col.setVisible(true);
                        jobRequestsDs.refresh();
                    } else {
                        col.setVisible(false);
                    }
                }
                if (testResColumnIds.length > 1) {
                    DataGrid.HeaderRow headerRow = jobRequestsTable.getHeaderRowCount() == 2 ? jobRequestsTable.getHeaderRow(0) : jobRequestsTable.prependHeaderRow();
                    DataGrid.HeaderCell headerCell = headerRow.join(testResColumnIds);
                    headerCell.setText(testResultsQuestionnaires.get(j).getQuestionnaire().getName());
                    headerCell.setStyleName("table-column-join-header");
                }
            }
        }
    }

    protected void addVideoInterviewColumn() {
        DataGrid.Column videoInterview = jobRequestsTable.addGeneratedColumn("videoInterview", new DataGrid.ColumnGenerator<JobRequest, Component>() {
            @Override
            public Component getValue(DataGrid.ColumnGeneratorEvent<JobRequest> event) {
                return generateVideoInterview(event.getItem());
            }

            @Override
            public Class<Component> getType() {
                return Component.class;
            }
        });
        videoInterview.setCaption(getMessage("JobRequest.videoInterview"));
        videoInterview.setRenderer(new WebComponentRenderer());
    }

    protected DecimalFormat getDecimalFormat() {
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(userSessionSource.getLocale());
        decimalFormat.setMultiplier(1);
        decimalFormat.setMaximumFractionDigits(2);
        return decimalFormat;
    }

    public Component getContactsLabel(JobRequest entity) {
        List<PersonContact> personContacts = new ArrayList<>();
//        personContacts = commonService.getEntities(PersonContact.class,
//                "select e from tsadv$PersonContact e where e.personGroup.id = :personGroupId order by e.type.code",
//                Collections.singletonMap("personGroupId", entity.getCandidatePersonGroup().getId()),
//                "personContact.edit");
        personContacts = entity.getCandidatePersonGroup().getPersonContacts();
        personContacts.sort((o1, o2) -> o1.getType().getCode().compareTo(o2.getType().getCode()));
        HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
        if (!personContacts.isEmpty()) {
            hBoxLayout.setSpacing(true);
            personContacts
                    .stream()
                    .collect(Collectors.toMap(pc -> pc.getType().getLangValue(), PersonContact::getContactValue, (pc1, pc2) -> pc1 + ", " + pc2))
                    .forEach((pt, v) -> {
                        Label contactType = componentsFactory.createComponent(Label.class);
                        contactType.setValue(pt + ":");
                        contactType.setStyleName("small-gray");
                        hBoxLayout.add(contactType);
                        Label contactValue = componentsFactory.createComponent(Label.class);
                        contactValue.setValue(v);
                        hBoxLayout.add(contactValue);
                    });
        }
        return hBoxLayout;
    }

    public void refresh() {
        refreshFilterAndDs();
        jobRequestsDs.refresh();
        cardBoxLayout.remove(table);
        table = createCardTable();
        cardBoxLayout.add(table);
        customizeJobRequestsTable();
        jobRequestsTable.repaint();
        showNotification(getMessage("tableIsRefreshed"), NotificationType.TRAY);
    }


    protected enum CardBlockType {
        DOCUMENTS("candidate.card.documents"),
        INTERVIEWS("candidate.card.interviews"),
        PERSONDATA("candidate.card.persondata"),
        EXPERIENCE("candidate.card.experience"),
        EDUCATION("candidate.card.education"),
        COMPETENCE("candidate.card.competence"),
        COMPETENCETYPECODE("candidate.card.competenceTypeCode"),
        REQUIREMENTS("candidate.card.REQUIREMENTS");

        private String messageKey;

        CardBlockType(String messageKey) {
            this.messageKey = messageKey;
        }

        public String getMessageKey() {
            return messageKey;
        }
    }

    private class CardBlockCheckBox {
        private CardBlockType cardBlock;
        private CheckBox checkBox;

        public CardBlockCheckBox(CardBlockType cardBlock, CheckBox checkBox) {
            this.cardBlock = cardBlock;
            this.checkBox = checkBox;
        }

        public CardBlockType getCardBlock() {
            return cardBlock;
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }
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
        if (userExtPersonGroup != null && userExtPersonGroup.size() > 0) {
            for (UserExtPersonGroup extPersonGroup : userExtPersonGroup) {
                if (extPersonGroup.getPersonGroup().getId().equals(personGroupId)) {
                    return extPersonGroup.getUserExt();
                }
            }
        } else {
            loadLists();
            if (userExtPersonGroup != null && userExtPersonGroup.size() > 0) {
                for (UserExtPersonGroup extPersonGroup : userExtPersonGroup) {
                    if (extPersonGroup.getPersonGroup().getId().equals(personGroupId)) {
                        return extPersonGroup.getUserExt();
                    }
                }
            } else {
                return null;
            }
        }
        return null;
    }

    protected void loadLists() {
        List<UUID> personGroupId = jobRequestsDs.getItems().stream().map(jobRequest ->
                jobRequest.getCandidatePersonGroup().getId()
        ).collect(Collectors.toList());
        List<UUID> jrId = jobRequestsDs.getItems().stream().map(jobRequest ->
                jobRequest.getId()
        ).collect(Collectors.toList());
        userExtPersonGroup = commonService.getEntities(UserExtPersonGroup.class,
                "select e from tsadv$UserExtPersonGroup e " +
                        " where e.personGroup.id in :personGroupIs",
                ParamsMap.of("personGroupIs", personGroupId),
                "userExtPersonGroup-for-jobRequest");
        offers = commonService.getEntities(Offer.class, "select e from tsadv$Offer e " +
                " where e.jobRequest.id in :personGroupId ", ParamsMap.of("personGroupId",
                personGroupId), "offer.browse");
        allJobRequests = commonService.getEntities(JobRequest.class,
                "select e from tsadv$JobRequest e " +
                        " where e.candidatePersonGroup.id in :personGroupId",
                ParamsMap.of("personGroupId", personGroupId), "jobRequest-for-count");
        allInterviewQuestionnaire = commonService.getEntities(InterviewQuestionnaire.class,
                "select e from tsadv$InterviewQuestionnaire e " +
                        "join e.interview i " +
                        "join i.jobRequest jr " +
                        "join e.questionnaire rq " +
                        "join rq.category c " +
                        "where c.code = 'PRE_SCREEN_TEST' and jr.id in :jrId ",
                ParamsMap.of("jrId", jrId), "interviewQuestionnaire.weight");
    }

    protected UserExt getUserExtByLogin(String login) {
        LoadContext<UserExt> loadContext = LoadContext.create(UserExt.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from base$UserExt e where e.login = :login");
        query.setParameter("login", login);
        loadContext.setQuery(query);
        loadContext.setView("user.browse");
        return dataManager.load(loadContext);
    }

    public void excelImport() {
        Report report = commonService.emQuerySingleRelult(Report.class, "select e from report$Report e where e.code = 'JOB_REQUEST'", null);
        ReportGuiManager reportGuiManager = AppBeans.get(ReportGuiManager.class);
        Map<String, Object> map = new HashMap<>();
        map.put("sysDate", CommonUtils.getSystemDate());
        map.put("jobRequestId", requisitionDs.getItem().getId());
        map.put("userExt", userSessionSource.getUserSession().getAttribute(StaticVariable.USER_EXT_ID));
        map.put("test", "1");
        reportGuiManager.printReport(report, map, null, report.getLocName());
    }

}
