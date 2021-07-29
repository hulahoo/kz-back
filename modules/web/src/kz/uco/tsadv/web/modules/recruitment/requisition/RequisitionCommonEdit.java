package kz.uco.tsadv.web.modules.recruitment.requisition;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.core.app.UniqueNumbersService;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.gui.AppConfig;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.*;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.entity.abstraction.AbstractTimeBasedEntity;
import kz.uco.base.entity.core.notification.NotificationTemplate;
import kz.uco.base.entity.core.notification.SendingNotification;
import kz.uco.base.service.NotificationService;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.personal.dictionary.DicCostCenter;
import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.modules.recruitment.config.DisplayAllEmployees;
import kz.uco.tsadv.modules.recruitment.config.RecruitmentConfig;
import kz.uco.tsadv.modules.recruitment.dictionary.DicPostingChannel;
import kz.uco.tsadv.modules.recruitment.enums.JobRequestStatus;
import kz.uco.tsadv.modules.recruitment.enums.RequisitionStatus;
import kz.uco.tsadv.modules.recruitment.enums.RequisitionType;
import kz.uco.tsadv.modules.recruitment.model.*;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.service.RecruitmentService;
import kz.uco.tsadv.web.modules.personal.common.Utils;
import kz.uco.tsadv.web.modules.recruitment.requisition.config.FullDicJobforManagerConig;
import kz.uco.uactivity.entity.Activity;
import kz.uco.uactivity.entity.ActivityType;
import kz.uco.uactivity.entity.StatusEnum;
import kz.uco.uactivity.service.ActivityService;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author veronika.buksha
 */
public class RequisitionCommonEdit extends AbstractEditor<Requisition> {
    private static final Logger log = LoggerFactory.getLogger(RequisitionCommonEdit.class);
    protected static final List<String> excludeProperties = Arrays.asList("requisition", "viewCount");
    @WindowParam
    protected boolean isRecruiter;

    protected boolean isActiveYes = true;
    protected boolean isSendToConfirmYes = true;

    protected CollectionDatasource optionOrganizationGroupDS;

    protected GroupDatasource optionPositionStructureDS;

    protected CollectionDatasource optionJobGroupDS;

    protected boolean browseOnly;
    protected boolean sendToConfirm;
    protected boolean approveConfirm;
    protected RecruitmentConfig recruitmentConfig;
    protected SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

    protected String page;

    protected boolean isCommited = false;
    protected boolean isDescriptionOrNameSiteChanged = true;

    /*beans*/
    @Inject
    protected DataManager dataManager;
    @Inject
    protected Metadata metadata;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected UserSession userSession;
    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    protected Events events;
    @Inject
    protected DataSupplier dataSupplier;

    /*services*/
    @Inject
    protected UniqueNumbersService uniqueNumbersService;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected RecruitmentService recruitmentService;
    @Inject
    protected CommonService commonService;
    @Inject
    protected NotificationService notificationService;
    @Inject
    protected ActivityService activityService;

    @Inject
    protected Configuration configuration;
    @Inject
    protected FullDicJobforManagerConig fullDicJobforManagerConig;

    /*datasources*/
    @Inject
    protected Datasource<Requisition> requisitionDs;
    @Inject
    protected Datasource<PositionGroupExt> positionGroupDs;
    @Inject
    protected Datasource<JobGroup> jobGroupDs;
    @Inject
    protected GroupDatasource<RequisitionCompetence, UUID> competencesDs;
    @Inject
    protected GroupDatasource<RequisitionHiringStep, UUID> hiringStepsDs;
    @Inject
    protected CollectionDatasource<JobRequest, UUID> jobRequestsDs;
    @Inject
    protected GroupDatasource<RequisitionMember, UUID> membersDs;
    @Inject
    protected GroupDatasource<RequisitionPostingChannel, UUID> postingChannelsDs;
    @Inject
    protected CollectionDatasource<RequisitionQuestionnaire, UUID> questionnairesDs;
    @Inject
    protected CollectionDatasource<RequisitionHistory, UUID> requisitionHistoryDs;
    @Inject
    protected CollectionDatasource<PersonGroupExt, UUID> substitutablePersonGroupsDs;
    @Inject
    protected CollectionDatasource<JobGroup, UUID> collectionJobGroupDs;

    /*buttons*/
    @Inject
    protected Button approve;
    @Inject
    protected Button close;
    @Inject
    protected Button reject;
    @Inject
    protected Button sendToRecruiter;
    @Inject
    protected Button windowCommit;
    @Inject
    protected RichTextArea addRichTextArea1;
    @Inject
    protected RichTextArea addRichTextArea2;
    @Inject
    protected RichTextArea addRichTextArea3;

    protected LookupPickerField organizationGroupLookupPickerField;
    protected LookupPickerField jobLookupPickerField;
    protected LookupPickerField positionGroupPickerField;

    protected DisplayAllEmployees displayAllEmployees;

    /*field configs*/
    protected FieldGroup.FieldConfig codeConfig;
    protected FieldGroup.FieldConfig startDateConfig;
    protected FieldGroup.FieldConfig requisitionTypeConfig;
    protected FieldGroup.FieldConfig requisitionTemplateConfig;
    protected FieldGroup.FieldConfig jobGroupConfig;
    protected FieldGroup.FieldConfig locationConfig;
    protected FieldGroup.FieldConfig recruiterPersonGroupConfig;
    protected FieldGroup.FieldConfig managerPersonGroupConfig;
    protected FieldGroup.FieldConfig organizationGroupConfig;
    protected FieldGroup.FieldConfig positionGroupConfig;
    protected FieldGroup.FieldConfig substitutablePersonGroupConfig;
    protected FieldGroup.FieldConfig forSubstitutionConfig;
    protected FieldGroup.FieldConfig requisitionStatusConfig;
    protected FieldGroup.FieldConfig endDateGroupConfig;
    protected FieldGroup.FieldConfig substitutableDescription;

    protected String frameId;

    protected boolean listenChangeValue = true;

    protected Boolean fromRequisitionBrowseSelf = false;
    protected String tsadv$PositionStructureBrowse = "tsadv$PositionStructure.browse";

    protected String newText = "";
    protected boolean rejectConfirm;
    protected boolean changed;
    protected Set<Entity> commitEntities;

    @Override
    protected void initNewItem(Requisition item) {
        super.initNewItem(item);
        requisitionDs.addItemPropertyChangeListener(e -> {
            if ("requisitionType".equals(e.getProperty())) {
                if (RequisitionType.TEMPLATE.equals(e.getValue())) {
                    codeConfig.setEditable(true);
                } else {
                    codeConfig.setEditable(!recruitmentConfig.getAutogenerateRequisitionCode());
                }
            }
        });
        item.setRequisitionStatus(RequisitionStatus.DRAFT);
        item.setFinalCollectDate(DateUtils.addDays(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH), getFinalCollectDays()));
        item.setStartDate(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH));
        item.setRequisitionType(RequisitionType.STANDARD);
        item.setViewCount(0L);
        codeConfig.setEditable(!recruitmentConfig.getAutogenerateRequisitionCode());

        if (recruitmentConfig.getCurrentDefaultManager()) {
            PersonGroupExt personGroup = userSession.getAttribute(StaticVariable.USER_PERSON_GROUP);
            if (personGroup != null) {
                personGroup = dataSupplier.reload(personGroup, "personGroupExt-view-for-requisition-edit");
            }
            if (personGroup != null) item.setManagerPersonGroup(personGroup);
        }

        try {
            //For add RequisitionPostingChannel 2 items. First: Корпоративный портал компании, Second: Внешний сайт компании)
            List<RequisitionPostingChannel> reqPostingChannelList = new ArrayList<>(); //list for two items

            RequisitionPostingChannel reqPostingChannel = metadata.create(RequisitionPostingChannel.class);
            reqPostingChannelList.add(reqPostingChannel);

            LoadContext<DicPostingChannel> loadContext = LoadContext.create(DicPostingChannel.class);
            LoadContext.Query query1 = LoadContext.createQuery("SELECT e FROM tsadv$DicPostingChannel e WHERE e.code = 'PORTAL'"); // sprosit u Yerbola zawivat naimoenovaniya
            loadContext.setQuery(query1);
            loadContext.setView("dicPostingChannel-view");
            reqPostingChannel.setPostingChannel(dataManager.load(loadContext));
            reqPostingChannel.setRequisition(item);

            RequisitionPostingChannel reqPostingChannel2 = metadata.create(RequisitionPostingChannel.class);
            reqPostingChannelList.add(reqPostingChannel2);

            LoadContext<DicPostingChannel> loadContext2 = LoadContext.create(DicPostingChannel.class);
            LoadContext.Query query2 = LoadContext.createQuery("SELECT e FROM tsadv$DicPostingChannel e WHERE e.code = 'EXTERNAL'");
            loadContext2.setQuery(query2);
            loadContext2.setView("dicPostingChannel-view");
            reqPostingChannel2.setPostingChannel(dataManager.load(loadContext2));
            reqPostingChannel2.setRequisition(item);

            item.setPostingChannels(reqPostingChannelList);
            //end adding
        } catch (Exception ex) {
            //todo: about what this exceptions, what need to do?
        }
    }

    @Override
    public void init(Map<String, Object> params) {
        frameId = this.getFrame().getId();
        super.init(params);
        if (params.get("browseOnly") != null)
            browseOnly = true;
        else {
            ((LookupField) requisitionTypeConfig.getComponent()).addValueChangeListener((valueChangedEvent) -> checkRequisitionType());

            PickerField requisitionTemplateField = (PickerField) requisitionTemplateConfig.getComponent();

            requisitionTemplateField.addAction(new PickerField.StandardAction(PickerField.ActionType.LOOKUP.getId(), requisitionTemplateField) {
                @Override
                public void actionPerform(Component component) {
                    showOptionDialog(
                            getMessage("msg.warning.title"),
                            getMessage("Requisition.dialog.change.template"),
                            MessageType.WARNING,
                            new Action[]{
                                    new DialogAction(DialogAction.Type.YES) {
                                        @Override
                                        public void actionPerform(Component component) {
                                            openLookup("tsadv$Requisition.lookup", (items) -> {
                                                        for (Object o : items) {
                                                            requisitionTemplateField.setValue(o);
                                                        }
                                                    },
                                                    WindowManager.OpenType.DIALOG,
                                                    Collections.singletonMap("requisitionType", 2));
                                        }
                                    },
                                    new DialogAction(DialogAction.Type.CANCEL)
                            });
                }

                @Override
                public String getIcon() {
                    return "font-icon:ELLIPSIS_H";
                }
            });

        }
        recruitmentConfig = configuration.getConfig(RecruitmentConfig.class);

        if (params.get("fromRequisitionBrowseSelf") != null) {
            fromRequisitionBrowseSelf = true;
        }
        /**
         * create OrganizationPickerField
         * */
        organizationGroupLookupPickerField = componentsFactory.createComponent(LookupPickerField.class);
        organizationGroupLookupPickerField.setDatasource(requisitionDs, organizationGroupConfig.getProperty());
        organizationGroupLookupPickerField.addAction(new LookupPickerField.StandardAction(LookupPickerField.ActionType.LOOKUP.getId(), organizationGroupLookupPickerField) {
            @Override
            public void actionPerform(Component component) {
                Map<String, Object> map = new HashMap<>();
                map.put("isMANAGER", recruitmentConfig.getOrganizationGenerationType().name());
                PositionGroupExt positionGroupExt = null;

                if (((HasValue) managerPersonGroupConfig.getComponent()).getValue() != null) {
                    PersonGroupExt personGroupExt = dataSupplier.reload(
                            (PersonGroupExt) ((HasValue) managerPersonGroupConfig.getComponent()).getValue(),
                            "personGroupExt.for.requisition.optionDs");
                    if (personGroupExt.getCurrentAssignment() != null) {
                        boolean isManager = personGroupExt.getCurrentAssignment().getPositionGroup().getPosition().getManagerFlag();
                        if (isManager) {
                            positionGroupExt = (PositionGroupExt) (personGroupExt.getCurrentAssignment().getPositionGroup());
                        }
                    }
                }

                map.put("managerPositionGroupId", (positionGroupExt != null) ? positionGroupExt.getId() : null);
                map.put("page", page);
                openLookup("tsadv$OrganizationStructure.browse",
                        (items) -> {
                            for (Object o : items) {
                                OrganizationStructure os = (OrganizationStructure) o;
                                getItem().setOrganizationGroup(os.getOrganizationGroup());
                            }
                        },
                        WindowManager.OpenType.DIALOG, map);
            }

            @Override
            public String getIcon() {
                return "font-icon:ELLIPSIS_H";
            }
        });
        organizationGroupLookupPickerField.addClearAction();
        organizationGroupLookupPickerField.setWidth("100%");
        organizationGroupLookupPickerField.setCaptionMode(CaptionMode.PROPERTY);
        organizationGroupLookupPickerField.setCaptionProperty("organizationName");
        organizationGroupConfig.setComponent(organizationGroupLookupPickerField);
        organizationGroupConfig.setRequired(true);

        /**
         * create jobLookupField
         * */

        jobLookupPickerField = componentsFactory.createComponent(LookupPickerField.class);
        jobLookupPickerField.setDatasource(requisitionDs, jobGroupConfig.getProperty());
        jobLookupPickerField.addAction(new BaseAction("jobLookupPrickerField") {
            @Override
            public void actionPerform(Component component) {
                Map<String, Object> map = new HashMap<>();
                map.put("groupId", userSession.getAttribute(StaticVariable.POSITION_GROUP_ID));
                map.put("fromRequisitionBrowseSelf", fromRequisitionBrowseSelf);
                openLookup("tsadv$JobGroup.browse",
                        (items -> {
                            for (Object o : items) {
                                getItem().setJobGroup(((JobGroup) o));
                            }
                        }),
                        WindowManager.OpenType.DIALOG, map);
            }

            @Override
            public String getIcon() {
                return "font-icon:ELLIPSIS_H";
            }
        });
        jobLookupPickerField.addClearAction();
        jobLookupPickerField.setWidth("100%");
        jobLookupPickerField.setCaptionMode(CaptionMode.PROPERTY);
        jobLookupPickerField.setCaptionProperty("jobName");
        jobGroupConfig.setComponent(jobLookupPickerField);
//        jobLookupPickerField.setRequired(true);
        /**
         * create PositionPickerField
         * */
        positionGroupPickerField = componentsFactory.createComponent(LookupPickerField.class);
        positionGroupPickerField.setDatasource(requisitionDs, positionGroupConfig.getProperty());
        if (!browseOnly) {
            positionGroupPickerField.addAction(new AbstractAction("customLookup") {
                @Override
                public void actionPerform(Component component) {
                    Map<String, Object> params = new HashMap<>();
                    PositionGroupExt positionGroupExt = null;
                    if (((HasValue) managerPersonGroupConfig.getComponent()).getValue() != null) {
                        PersonGroupExt personGroupExt = dataSupplier.reload(
                                ((PersonGroupExt) ((HasValue) managerPersonGroupConfig.getComponent()).getValue()),
                                "personGroupExt.for.requisition.optionDs");
                        if (personGroupExt.getCurrentAssignment() != null) {
                            positionGroupExt = personGroupExt.getCurrentAssignment().getPositionGroup();
                        }
                    }
                    params.put("managerPositionGroupId", (positionGroupExt != null) ? positionGroupExt.getId() : null);
                    params.put("page", page);
                    params.put("fromRequisition", true);
//                    personGroupConfigMap.put("personGroupConfigMap", Boolean.TRUE);
//                    personGroupConfigMap.put("filterByUser", "tsadv$Requisition.editSelf".equals(frameId));
                    openLookup("tsadv$PositionStructure.browse",
                            (items) -> {
                                for (Object o : items) {
                                    PositionExt pe = (PositionExt) o;

                                    PositionGroupExt positionGroup = pe.getGroup();
                                    if (positionGroup != null) positionGroup.getPositionInDate(pe.getStartDate());
                                    getItem().setPositionGroup(positionGroup);

                                    if (positionGroup != null) {

                                        JobGroup jobGroup = pe.getJobGroup();
                                        if (jobGroup != null) jobGroup.getJobInDate(pe.getStartDate());
                                        getItem().setJobGroup(jobGroup);

                                        if (!recruitmentConfig.getCurrentDefaultManager()) {
                                            AssignmentExt assignment = employeeService.findManagerUserByPosition(positionGroup.getId());
                                            if (assignment != null) {
                                                getItem().setManagerPersonGroup(assignment.getPersonGroup());
                                            } else {
                                                showNotification(
                                                        getMessage("msg.error.title"),
                                                        "Manager by position not found!",
                                                        NotificationType.TRAY);
                                            }
                                        }
                                    }

                                    if (pe.getOrganizationGroupExt() != null)
                                        pe.getOrganizationGroupExt().getOrganizationInDate(pe.getStartDate());

                                    getItem().setOrganizationGroup(pe.getOrganizationGroupExt());
                                }
                            },
                            WindowManager.OpenType.DIALOG, params);
                }

                @Override
                public String getIcon() {
                    return "font-icon:ELLIPSIS_H";
                }
            });
        }
        positionGroupPickerField.addClearAction();
        positionGroupPickerField.setWidth("100%");
        positionGroupPickerField.setCaptionMode(CaptionMode.PROPERTY);
        positionGroupPickerField.setCaptionProperty("fullName");
        positionGroupConfig.setComponent(positionGroupPickerField);
    }

    @Override
    protected void postInit() {
        if (!PersistenceHelper.isNew(getItem())) {
            requisitionTypeConfig.setEditable(false);
            codeConfig.setEditable(!recruitmentConfig.getAutogenerateRequisitionCode());
            if (getItem().getPositionGroup() != null && getItem().getPositionGroup().getPosition() != null) {
                setCaption(getItem().getCode() + " (" + getItem().getJobGroup().getJob().getJobName() + ")");
            } else if (getItem().getJobGroup() != null && getItem().getJobGroup().getJob() != null) {
                setCaption(getItem().getCode() + " (" + getItem().getJobGroup().getJob().getJobName() + ")");
            } else {
                setCaption(getItem().getCode());
            }
        }
        startDateConfig.setEditable((getItem().getRequisitionStatus() == RequisitionStatus.DRAFT) || !recruitmentConfig.getAutofillRequisitionStartDate());

        organizationGroupLookupPickerField.setEditable(PersistenceHelper.isNew(getItem()));

        //Utils.customizeLookup(fieldGroup1.getField("requisitionTemplateField").getComponent(), "tsadv$Requisition.lookup", WindowManager.OpenType.DIALOG, Collections.singletonMap("requisitionType", 2));

        ((LookupPickerField) locationConfig.getComponent()).addLookupAction();
        ((LookupPickerField) locationConfig.getComponent()).addClearAction();
        Utils.customizeLookup(locationConfig.getComponent(), null, WindowManager.OpenType.DIALOG, null);

//        Utils.customizeLookup(jobGroupConfig.getComponent(), null, WindowManager.OpenType.DIALOG, null);
        Map<String, Object> map = new HashMap<>();
        map.put("roleCode", " and (e.id in (select u.personGroup.id  " +
                "                       from tsadv$HrUserRole r, tsadv$UserExt u" +
                "                      where u.id = r.user.id" +
                "                        and r.role.code = 'RECRUITING_SPECIALIST'" +
                "                        and :session$systemDate between r.dateFrom and r.dateTo))");


        map.put("RequisitionEditMap", Boolean.TRUE);
        Utils.customizeLookup(recruiterPersonGroupConfig.getComponent(), null, WindowManager.OpenType.DIALOG, map);

        Map<String, Object> managerParams = new HashMap<>();
        managerParams.put("managerMap", Boolean.TRUE);

        String managerPersonIds = getManagerPersonIds();
        if (managerPersonIds != null && managerPersonIds.trim().length() != 0) {
            managerParams.put("onlyManager", "and (e.id in (" + managerPersonIds + "))");

        }

        Utils.customizeLookup(managerPersonGroupConfig.getComponent(), null, WindowManager.OpenType.DIALOG, managerParams);
        //Utils.customizeLookup(organizationGroupConfig.getComponent(), null, WindowManager.OpenType.DIALOG, null);
        //Utils.customizeLookup(positionGroupConfig.getComponent(), null, WindowManager.OpenType.DIALOG, null);
        displayAllEmployees = configuration.getConfig(DisplayAllEmployees.class);
        substitutablePersonGroupConfig.setEditable(getItem().getForSubstitution());
        ((LookupPickerField) substitutablePersonGroupConfig.getComponent()).addLookupAction();
        ((LookupPickerField) substitutablePersonGroupConfig.getComponent()).addClearAction();
        if (displayAllEmployees.getDisplayAllEmployees()) {
            Utils.customizeLookup(substitutablePersonGroupConfig.getComponent(), "base$PersonGroup.browse", WindowManager.OpenType.DIALOG, null);
        } else {
            substitutablePersonGroupsDs.setQuery("select e.personGroup from base$AssignmentExt e\n" +
                    "where e.positionGroup.id = :ds$requisitionDs.positionGroup.id");
            Utils.customizeLookup(substitutablePersonGroupConfig.getComponent(), "base$PersonGroup.browse", WindowManager.OpenType.DIALOG, Collections.singletonMap("commonFilter", (getItem().getPositionGroup() != null ? " and (a.positionGroup.id = '" + getItem().getPositionGroup().getId() + "')" : " and 1=0 ")));

        }


        jobGroupConfig.setRequired(true);
        jobGroupConfig.setEditable(getItem().getPositionGroup() == null);
//        organizationGroupConfig.setEditable(getItem().getPositionGroup() == null);

        /**
         * set template visibility true if requisitionType equals STANDARD
         * */
        checkRequisitionType();

        checkPermission();

        /*for requisition copy*/
        if (PersistenceHelper.isNew(getItem())) {
            if (getItem().getQuestionnaires() != null)
                for (RequisitionQuestionnaire requisitionQuestionnaire : getItem().getQuestionnaires())
                    questionnairesDs.addItem(requisitionQuestionnaire);
            if (getItem().getCompetences() != null)
                for (RequisitionCompetence requisitionCompetence : getItem().getCompetences())
                    competencesDs.addItem(requisitionCompetence);
            if (getItem().getHiringSteps() != null) {
                for (RequisitionHiringStep requisitionHiringStep : getItem().getHiringSteps())
                    hiringStepsDs.addItem(requisitionHiringStep);
            }
            if (getItem().getMembers() != null)
                for (RequisitionMember requisitionMember : getItem().getMembers())
                    membersDs.addItem(requisitionMember);
            if (getItem().getPostingChannels() != null)
                for (RequisitionPostingChannel requisitionPostingChannel : getItem().getPostingChannels())
                    postingChannelsDs.addItem(requisitionPostingChannel);
        }
        // использование OrganizationGenerationType
        if (recruitmentConfig.getOrganizationGenerationType().name().equals("MANAGER"))
            if (((HasValue) managerPersonGroupConfig.getComponent()).getValue() == null) {
                organizationGroupLookupPickerField.setEditable(false);
            }
    }

    @Override
    public void ready() {
        super.ready();
        ((HasValue<Object>) requisitionTemplateConfig.getComponent()).addValueChangeListener((e) -> {

            if (listenChangeValue) {
                if (e.getValue() != null) {
                    Requisition template = (Requisition) e.getValue();
                    if (template.getEmploymentType() != null)
                        getItem().setEmploymentType(template.getEmploymentType());
                    if (template.getLocation() != null)
                        getItem().setLocation(template.getLocation());
                    if (template.getOrganizationGroup() != null)
                        getItem().setOrganizationGroup(template.getOrganizationGroup());
                    if (template.getPositionGroup() != null)
                        getItem().setPositionGroup(template.getPositionGroup());
                    if (template.getJobGroup() != null)
                        getItem().setJobGroup(template.getJobGroup());
                    if (template.getOpenedPositionsCount() != null)
                        getItem().setOpenedPositionsCount(template.getOpenedPositionsCount());
                    if (template.getManagerPersonGroup() != null)
                        getItem().setManagerPersonGroup(template.getManagerPersonGroup());
                    if (template.getRecruiterPersonGroup() != null)
                        getItem().setRecruiterPersonGroup(template.getRecruiterPersonGroup());
                    if (template.getRequisitionStatus() != null)
                        getItem().setRequisitionStatus(template.getRequisitionStatus());
                    if (template.getVideoInterviewRequired() != null) {
                        getItem().setVideoInterviewRequired(template.getVideoInterviewRequired());
                    }

                    if (template.getDescriptionLang1() != null)
                        getItem().setDescriptionLang1(template.getDescriptionLang1());
                    if (template.getDescriptionLang2() != null)
                        getItem().setDescriptionLang2(template.getDescriptionLang2());
                    if (template.getDescriptionLang3() != null)
                        getItem().setDescriptionLang3(template.getDescriptionLang3());
                    if (template.getDescriptionLang4() != null)
                        getItem().setDescriptionLang4(template.getDescriptionLang4());
                    if (template.getDescriptionLang5() != null)
                        getItem().setDescriptionLang5(template.getDescriptionLang5());

                    //competences
                    for (RequisitionCompetence item : new ArrayList<>(competencesDs.getItems()))
                        competencesDs.removeItem(item);

                    for (RequisitionCompetence templateCompetence : template.getCompetences()) {
                        RequisitionCompetence competence = metadata.create(RequisitionCompetence.class);
                        competence.setRequisition(getItem());
                        competence.setCompetenceGroup(templateCompetence.getCompetenceGroup());
                        competence.setScaleLevel(templateCompetence.getScaleLevel());
                        competence.setCriticalness(templateCompetence.getCriticalness());
                        competencesDs.addItem(competence);
                    }

                    //hiring steps
                    for (RequisitionHiringStep item : new ArrayList<>(hiringStepsDs.getItems()))
                        hiringStepsDs.removeItem(item);

                    for (RequisitionHiringStep templateHiringStep : template.getHiringSteps()) {
                        RequisitionHiringStep hiringStep = metadata.create(RequisitionHiringStep.class);
                        hiringStep.setRequisition(getItem());
                        hiringStep.setHiringStep(templateHiringStep.getHiringStep());
                        hiringStep.setOrder(templateHiringStep.getOrder());
                        hiringStepsDs.addItem(hiringStep);
                    }

                    //members
                    for (RequisitionMember item : new ArrayList<>(membersDs.getItems()))
                        membersDs.removeItem(item);

                    for (RequisitionMember templateMember : template.getMembers()) {
                        RequisitionMember member = metadata.create(RequisitionMember.class);
                        member.setRequisition(getItem());
                        member.setAccessLevel(templateMember.getAccessLevel());
                        member.setPersonGroup(templateMember.getPersonGroup());
                        membersDs.addItem(member);
                    }

                    //posting channels
                    for (RequisitionPostingChannel item : new ArrayList<>(postingChannelsDs.getItems()))
                        postingChannelsDs.removeItem(item);

                    for (RequisitionPostingChannel templatePostingChannel : template.getPostingChannels()) {
                        RequisitionPostingChannel postingChannel = metadata.create(RequisitionPostingChannel.class);
                        postingChannel.setRequisition(getItem());
                        postingChannel.setPostingChannel(templatePostingChannel.getPostingChannel());
                        postingChannel.setStartDate(templatePostingChannel.getStartDate());
                        postingChannel.setEndDate(templatePostingChannel.getEndDate());
                        postingChannelsDs.addItem(postingChannel);
                    }
                    //questionnaires
                    for (RequisitionQuestionnaire item : new ArrayList<>(questionnairesDs.getItems()))
                        questionnairesDs.removeItem(item);

                    for (RequisitionQuestionnaire templateQuestionnaire : template.getQuestionnaires()) {
                        RequisitionQuestionnaire requisitionQuestionnaire = metadata.create(RequisitionQuestionnaire.class);
                        requisitionQuestionnaire.setRequisition(getItem());
                        requisitionQuestionnaire.setQuestionnaire(templateQuestionnaire.getQuestionnaire());
                        requisitionQuestionnaire.setWeight(templateQuestionnaire.getWeight());
                        questionnairesDs.addItem(requisitionQuestionnaire);
                    }
                }

            /*calcCostCenter();
            calcNamesForSite();*/
            }
        });
        ((HasValue<Object>) positionGroupConfig.getComponentNN()).addValueChangeListener(this::positionGroupValueChangeListener);

        ((HasValue) jobGroupConfig.getComponentNN()).addValueChangeListener(e -> updateNameForSiteDescription());
        ((HasValue<Object>) forSubstitutionConfig.getComponentNN()).addValueChangeListener(e ->
                handleChangeForSubstitutionConfig(e.getValue() != null ? (Boolean) e.getValue() : false));
        ((HasValue) organizationGroupConfig.getComponentNN()).addValueChangeListener(e -> {
            if (listenChangeValue) {
                calcCostCenter();
            }
        });
        optionOrganizationGroupDS = new DsBuilder(getDsContext())
                .setJavaClass(OrganizationGroupExt.class)
                .setViewName("organizationGroupExt-view-for-requisition")
                .setId("optionOrganizationGroupDS")
                .buildCollectionDatasource();
        organizationGroupLookupPickerField.setOptionsDatasource(optionOrganizationGroupDS);
        setOptionsForOrganizationGroup(true);
        optionJobGroupDS = new DsBuilder(getDsContext())
                .setJavaClass(JobGroup.class)
                .setViewName("jobGroup.browse")
                .setId("optionJobGroupDS")
                .buildCollectionDatasource();
        setOptionsForJobGroupNew();
        optionPositionStructureDS = new DsBuilder(getDsContext())
                .setJavaClass(PositionGroupExt.class)
                .setViewName("positionGroupExtReqLookupPickerField")
                .setId("optionPositionStructureDS")
                .buildGroupDatasource();
        positionGroupConfig.setOptionsDatasource(optionPositionStructureDS);
        setOptionsForPositionStructure();
        ((HasValue<Object>) managerPersonGroupConfig.getComponentNN()).addValueChangeListener(e -> {
            if (recruitmentConfig.getOrganizationGenerationType().name().equals("MANAGER")) {
                if (e.getValue() != null) {
                    organizationGroupLookupPickerField.setEditable(true);
                    setOptionsForOrganizationGroup(!e.getValue().equals(e.getPrevValue()));
                    setOptionsForPositionStructure();
                } else {
                    organizationGroupLookupPickerField.setValue(null);
                    organizationGroupLookupPickerField.setEditable(false);
                }
            } else {
                organizationGroupLookupPickerField.setEditable(true);
                setOptionsForOrganizationGroup(true);
                setOptionsForPositionStructure();
            }

            if (listenChangeValue) {
                calcCostCenter();
            }
        });
        ((HasValue<Object>) requisitionStatusConfig.getComponentNN()).addValueChangeListener(e -> {
            startDateConfig.setEditable(RequisitionStatus.DRAFT.equals(e.getValue()) || !recruitmentConfig.getAutofillRequisitionStartDate());
        });
        recruiterPersonGroupConfig.addValidator(value -> {
            if (value != null) {
                PersonGroupExt manager = getItem().getManagerPersonGroup();
                if (manager != null) {
                    if (value.equals(manager)) {
                        throw new ValidationException("");
                    }
                }
            }
        });
        managerPersonGroupConfig.addValidator(value -> {
            if (value != null) {
                PersonGroupExt recruiter = getItem().getRecruiterPersonGroup();
                if (recruiter != null) {
                    if (value.equals(recruiter)) {
                        throw new ValidationException(getMessage("requisitionValidationException"));

                    }
                }
            }
        });

        setOptionsForOrganizationGroup(true);

        managerDescriptionListener();

        commitEntities = new HashSet<>();
        getDsContext().addBeforeCommitListener(this::beforeCommitListener);
    }

    protected void beforeCommitListener(CommitContext context) {
        if (commitEntities != null)
            commitEntities.forEach(context::addInstanceToCommit);
        commitEntities = new HashSet<>();
    }

    protected void positionGroupValueChangeListener(HasValue.ValueChangeEvent<Object> e) {
        if (displayAllEmployees.getDisplayAllEmployees()) {
            Utils.customizeLookup(substitutablePersonGroupConfig.getComponent(), "base$PersonGroup.browse", WindowManager.OpenType.DIALOG, null);
        } else {
            substitutablePersonGroupsDs.setQuery("select e.personGroup from base$AssignmentExt e\n" +
                    "where e.positionGroup.id = :ds$requisitionDs.positionGroup.id");
            Utils.customizeLookup(substitutablePersonGroupConfig.getComponent(), "base$PersonGroup.browse", WindowManager.OpenType.DIALOG, Collections.singletonMap("commonFilter", (getItem().getPositionGroup() != null ? " and (a.positionGroup.id = '" + getItem().getPositionGroup().getId() + "')" : " and 1=0 ")));
        }
        jobGroupConfig.setEditable(e == null);
        organizationGroupConfig.setEditable(e == null);

        if (listenChangeValue) {
            if (e == null) {
                getItem().setJobGroup(null);
                getItem().setOrganizationGroup(null);

                if (!recruitmentConfig.getCurrentDefaultManager()) {
                    getItem().setManagerPersonGroup(null);
                }
            }

            calcCostCenter();
        }
        if (e.getValue() != null) {
            PositionGroupExt positionGroup = (PositionGroupExt) e.getValue();
//                setNameForSiteField(positionGroup);
            getItem().setPositionGroup(positionGroup);
            if (positionGroup != null) {
                PositionExt position = positionGroup.getPosition();
                if (position != null) {
                    JobGroup jobGroup = position.getJobGroup();
                    getItem().setJobGroup(jobGroup);

                    if (!recruitmentConfig.getCurrentDefaultManager()) {
                        AssignmentExt assignment = employeeService.findManagerUserByPosition(positionGroup.getId());
                        if (assignment != null) {
                            getItem().setManagerPersonGroup(assignment.getPersonGroup());
                        } else {
                            showNotification(
                                    getMessage("msg.error.title"),
                                    "Manager by position not found!",
                                    NotificationType.TRAY);
                        }
                    }
                }
            }
            getItem().setOrganizationGroup(employeeService.getOrganizationGroupExtByPositionGroup(positionGroup, "organizationGroup.browse"));
        }
    }

    protected void handleChangeForSubstitutionConfig(Boolean listenChangeValue) {
        if (BooleanUtils.isTrue(listenChangeValue))
            substitutablePersonGroupConfig.setEditable(true);
        else {
            substitutablePersonGroupConfig.setEditable(false);

            if (listenChangeValue) {
                getItem().setSubstitutablePersonGroup(null);
            }
        }
    }

    protected void updateNameForSiteDescription() {
        if (!isDescriptionOrNameSiteChanged) {
            return;
        }
        boolean nameForSite = true;

        String descriptionru = "";
        String descriptionkz = "";
        String descriptionen = "";
        PositionGroupExt positionGroupExt = getItem().getPositionGroup();
        if (positionGroupExt != null) {
            positionGroupExt = dataSupplier.reload(positionGroupExt, "positionGroupExt.for.requisition");
        }
        JobGroup jobGroup = getItem().getJobGroup();
        if (jobGroup != null) {
            jobGroup = dataSupplier.reload(jobGroup, "jobGroup.for.requisitions");
        }

        if (positionGroupExt != null && recruitmentConfig.getPositionDescription()) {
            descriptionru = concat(positionGroupExt.getPosition().getCandidateRequirementsLang1(),
                    positionGroupExt.getPosition().getJobDescriptionLang1());
            descriptionkz = concat(positionGroupExt.getPosition().getCandidateRequirementsLang2(),
                    positionGroupExt.getPosition().getJobDescriptionLang2());
            descriptionen = concat(positionGroupExt.getPosition().getCandidateRequirementsLang3(),
                    positionGroupExt.getPosition().getJobDescriptionLang3());
        } else {
            descriptionru = "";
            descriptionkz = "";
            descriptionen = "";
        }
        if (jobGroup != null) {
            if (descriptionru == null || descriptionru.length() < 1) {
                descriptionru = concat(jobGroup.getJob().getCandidateRequirementsLang1(),
                        jobGroup.getJob().getJobDescriptionLang1());
            }
            if (descriptionkz == null || descriptionkz.length() < 1) {
                descriptionkz = concat(jobGroup.getJob().getCandidateRequirementsLang2(),
                        jobGroup.getJob().getJobDescriptionLang2());
            }
            if (descriptionen == null || descriptionen.length() < 1) {
                descriptionen = concat(jobGroup.getJob().getCandidateRequirementsLang3(),
                        jobGroup.getJob().getJobDescriptionLang3());
            }
        }

        String messagerForUpdate = "";

        if (nameForSite) {
            messagerForUpdate = getMessage("Update.description.nameForSite.message");
            showUpdateQuestion(messagerForUpdate, descriptionru, descriptionkz, descriptionen);
        } else if (!nameForSite) {
            messagerForUpdate = getMessage("Update.description.message");
            showUpdateQuestion(messagerForUpdate, descriptionru, descriptionkz, descriptionen);
        } else {
            getItem().setDescriptionLang1(descriptionru);
            getItem().setDescriptionLang2(descriptionkz);
            getItem().setDescriptionLang3(descriptionen);
            inputNameForSite();
        }
    }

    protected void showUpdateQuestion(String messagerForUpdate, String descriptionru, String descriptionkz, String descriptionen) {
        showOptionDialog(getMessage("CONFIRMATION")
                , messagerForUpdate
                , MessageType.CONFIRMATION
                , new Action[]{
                        new DialogAction(DialogAction.Type.YES) {
                            @Override
                            public void actionPerform(Component component) {
                                if (messagerForUpdate.equals(getMessage("Update.description.nameForSite.message"))) {
                                    getItem().setDescriptionLang1(descriptionru);
                                    getItem().setDescriptionLang2(descriptionkz);
                                    getItem().setDescriptionLang3(descriptionen);
                                    inputNameForSite();
                                } else if (messagerForUpdate.equals(getMessage("CONFIRMATION.qwestion"))) {
                                    inputNameForSite();
                                } else if (messagerForUpdate.equals(getMessage("Update.description.message"))) {
                                    getItem().setDescriptionLang1(descriptionru);
                                    getItem().setDescriptionLang2(descriptionkz);
                                    getItem().setDescriptionLang3(descriptionen);
                                }
                            }
                        },
                        new DialogAction(DialogAction.Type.NO)
                });
    }

    protected void inputNameForSite() {
        if (getItem().getPositionGroup() != null) {
            PositionExt positionExt = getItem().getPositionGroup().getPosition();
            if (positionExt.getPositionNameLang1() != null && positionExt.getPositionNameLang1().trim().length() > 0) {
                getItem().setNameForSiteLang1(positionExt.getPositionNameLang1());
            }
            if (positionExt.getPositionNameLang2() != null && positionExt.getPositionNameLang2().trim().length() > 0) {
                getItem().setNameForSiteLang2(positionExt.getPositionNameLang2());
            } else {
                if (jobGroupDs.getItem() != null && jobGroupDs.getItem().getJob().getJobNameLang2() != null && jobGroupDs.getItem().getJob().getJobNameLang2().trim().length() > 0) {
                    getItem().setNameForSiteLang2(jobGroupDs.getItem().getJob().getJobNameLang2());
                } else if (positionExt.getPositionNameLang1() != null && positionExt.getPositionNameLang1().trim().length() > 0) {
                    getItem().setNameForSiteLang2(positionExt.getPositionNameLang1());
                }
            }
            if (positionExt.getPositionNameLang3() != null && positionExt.getPositionNameLang3().trim().length() > 0) {
                getItem().setNameForSiteLang3(positionExt.getPositionNameLang3());
            } else {
                if (jobGroupDs.getItem() != null && jobGroupDs.getItem().getJob().getJobNameLang3() != null && jobGroupDs.getItem().getJob().getJobNameLang3().trim().length() > 0) {
                    getItem().setNameForSiteLang3(jobGroupDs.getItem().getJob().getJobNameLang3());
                } else if (positionExt.getPositionNameLang1() != null && positionExt.getPositionNameLang1().trim().length() > 0) {
                    getItem().setNameForSiteLang3(positionExt.getPositionNameLang1());
                }
            }

        } else {

            if (jobGroupDs.getItem() != null) {
                Job job = jobGroupDs.getItem().getJob();
                if (job.getJobNameLang1() != null) {
                    getItem().setNameForSiteLang1(job.getJobNameLang1());
                }
                if (job.getJobNameLang2() != null && job.getJobNameLang2().trim().length() > 0) {
                    getItem().setNameForSiteLang2(job.getJobNameLang2());
                } else {
                    if (job.getJobNameLang1() != null) {
                        getItem().setNameForSiteLang2(job.getJobNameLang1());
                    }
                }
                if (job.getJobNameLang3() != null && job.getJobNameLang3().trim().length() > 0) {
                    getItem().setNameForSiteLang3(job.getJobNameLang3());
                } else {
                    if (job.getJobNameLang1() != null) {
                        getItem().setNameForSiteLang3(job.getJobNameLang1());
                    }
                }
            } else {
                getItem().setNameForSiteLang1("");
                getItem().setNameForSiteLang2("");
                getItem().setNameForSiteLang3("");
            }
        }

    }

    protected void setOptionsForPositionStructure() {
        optionPositionStructureDS.setQuery("select pe.group \n" +
                "from base$PositionExt pe\n" +
                "where pe.organizationGroupExt.id in\n" +
                "      (select oe.group.id\n" +
                "       from base$OrganizationExt oe\n" +
                "       where (:session$systemDate between oe.startDate and oe.endDate\n" +
                "         or (oe.startDate > :session$systemDate and\n" +
                "             oe.startDate in (select min(p.startDate) from base$OrganizationExt p where p.group.id = oe.group.id))))\n" +
                "  and (:session$systemDate between pe.startDate and pe.endDate\n" +
                "  or (pe.startDate > :session$systemDate and\n" +
                "      pe.startDate in (select min(p.startDate) from base$PositionExt p where p.group.id = pe.group.id)))\n" +
                "  and pe.fte > 0\n" +
                "  and pe.deleteTs is null");
        optionPositionStructureDS.refresh();

    }

    protected void setOptionsForJobGroupNew() {
        jobGroupConfig.setOptionsDatasource(collectionJobGroupDs); // TODO
    }


    protected void setOptionsForOrganizationGroup(Boolean enableList) {
        optionOrganizationGroupDS.setQuery("select e.organizationGroup from tsadv$PositionStructure e " +
                "  where e.elementType = 1 " +
                "    and :session$systemDate between e.startDate and e.endDate " +
                "    and (:custom$isFull=TRUE  " +
                "    or (:custom$isMANAGER=TRUE  " +
//                "      and  :custom$managerPositionGroupId is not null " +
                "      and e.organizationGroupPath like " +
                "         concat('%',concat((select distinct ps.organizationGroup.id " +
                "           from tsadv$PositionStructure ps " +
                "           where ps.positionGroup.id = :custom$managerPositionGroupId " +
                "           and :session$systemDate between ps.startDate and ps.endDate  " +
                "           and :session$systemDate between ps.posStartDate and ps.posEndDate " +
                "           group by ps.organizationGroup.id " +
                "          ), " +
                "        '%') " +
                "   ))) " +
                " order by e.lvl");
        Map<String, Object> params = new HashMap<>();
        params.put("isFull", recruitmentConfig.getOrganizationGenerationType().name().equals("FULL"));
        params.put("isMANAGER", recruitmentConfig.getOrganizationGenerationType().name().equals("MANAGER"));
        PositionGroupExt positionGroupExt = null;
        if (((HasValue) managerPersonGroupConfig.getComponent()).getValue() != null) {

            PersonGroupExt personGroupExt = dataSupplier.reload(
                    (PersonGroupExt) ((HasValue) managerPersonGroupConfig.getComponent()).getValue(),
                    "personGroupExt.for.requisition.optionDs");
            if (personGroupExt.getCurrentAssignment() != null) {
                boolean isManager = personGroupExt.getCurrentAssignment().getPositionGroup().getPosition().getManagerFlag();
                if (isManager) {
                    positionGroupExt = personGroupExt.getCurrentAssignment().getPositionGroup();
                }
            }
        }
        params.put("managerPositionGroupId", (positionGroupExt != null) ? positionGroupExt.getId() : null);
        optionOrganizationGroupDS.refresh(params);
    }

    @Override
    protected boolean preCommit() {
        if (getItem().getCode() == null) {
            if (PersistenceHelper.isNew(getItem()) && recruitmentConfig.getAutogenerateRequisitionCode()) {
                long currentSequenceValue = generateNextSequenceValue();
                getItem().setCode(String.format("R%07d", currentSequenceValue));
                autoFillReqPostingChannel();
            }
        }
        isDescriptionOrNameSiteChanged = false;
        return super.preCommit();
    }

    protected void autoFillReqPostingChannel() {
//            List<RequisitionPostingChannel> reqPostingChannelList = new ArrayList<>();
        for (RequisitionPostingChannel reqPostingChannel : getItem().getPostingChannels()) {
            reqPostingChannel.setStartDate(getItem().getStartDate());
        }
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        /*if (committed) {
            showNotification(String.format(getMessage("requisition.save.success"), getItem().getCode()), NotificationType.TRAY);
        }*/
        if (committed && !close) {
            if (rejectConfirm) {
                showNotification(getMessage("Requisition.vacancy.rejected"), NotificationType.TRAY);
            } else if (showSaveNotification) {
                Entity entity = ((Editor) frame).getItem();
                frame.showNotification(
                        messages.formatMessage(AppConfig.getMessagesPack(), "requisitionSavedSuccessful",
                                messages.getTools().getEntityCaption(entity.getMetaClass()), entity.getInstanceName()),
                        NotificationType.TRAY);
            }
            if (sendToConfirm) {
                showNotification(messages.getMessage(AppConfig.getMessagesPack(), "requisitionSendToRecruiterConfirm"), NotificationType.TRAY);
            }
            if (approveConfirm) {
//                showNotification(messages.getMessage(AppConfig.getMessagesPack(), "requisitionSendToApprove"), NotificationType.TRAY);
            }
            organizationGroupLookupPickerField.setEditable(false);
        }
        isDescriptionOrNameSiteChanged = true;
        return true;
    }

    @Override
    public void commitAndClose() {
        super.commit();
    }

    public void myClose() {
        this.close("close");
    }

    protected Case findCase(String lang, String caseType, List<Case> list) {
        return list.stream().filter(aCase -> lang.equals(aCase.getLanguage()) &&
                caseType.equals(aCase.getCaseType().getName())).findAny().orElse(null);
    }

    protected void checkRequisitionType() {
        LookupField<RequisitionType> requisitionTypeField = (LookupField) requisitionTypeConfig.getComponent();
        PickerField requisitionTemplateField = (PickerField) requisitionTemplateConfig.getComponent();

        RequisitionType requisitionType = requisitionTypeField.getValue();
        if (requisitionType != null && requisitionType.equals(RequisitionType.TEMPLATE)) {
            requisitionTemplateConfig.setEditable(false);
        } else {
            requisitionTemplateConfig.setEditable(true);
        }
    }


    protected String getManagerPersonIds() {
        List<PersonGroupExt> list = employeeService.getManagersList();
        StringBuilder sb = new StringBuilder();
        for (PersonGroupExt p : list) {
            sb.append("'");
            sb.append(p.getId());
            sb.append("', ");
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - 2, sb.length());
        } else {
            return "";
        }
        return sb.toString();
    }

    protected void calcCostCenter() {
        Map<String, Object> params = new HashMap<>();
        String query = null;
        if (getItem().getPositionGroup() != null) {
            params.put("positionGroupId", getItem().getPositionGroup().getId());
            params.put("systemDate", CommonUtils.getSystemDate());

            query = "  select e " +
                    "    from tsadv$DicCostCenter e " +
                    "   where e.id in (select o.costCenter.id " +
                    "                    from tsadv$PositionStructure ps, base$OrganizationExt o " +
                    "                   where ps.positionGroup.id = :positionGroupId " +
                    "                     and o.group.id = ps.organizationGroup.id" +
                    "                     and :systemDate between ps.startDate and ps.endDate" +
                    "                     and :systemDate between ps.posStartDate and ps.posEndDate" +
                    "                     and :systemDate between o.startDate and o.endDate)";
        } else if (getItem().getOrganizationGroup() != null) {
            params.put("organizationGroupId", getItem().getOrganizationGroup().getId());
            params.put("systemDate", CommonUtils.getSystemDate());

            query = "  select e " +
                    "    from tsadv$DicCostCenter e " +
                    "   where e.id in (select o.costCenter.id " +
                    "                    from base$OrganizationExt o " +
                    "                   where o.group.id = :organizationGroupId" +
                    "                     and :systemDate between o.startDate and o.endDate)";

        } else if (getItem().getManagerPersonGroup() != null) {
            params.put("personGroupId", getItem().getManagerPersonGroup().getId());
            params.put("systemDate", CommonUtils.getSystemDate());

            query = "  select e " +
                    "    from tsadv$DicCostCenter e " +
                    "   where e.id in (select o.costCenter.id " +
                    "                    from base$AssignmentExt a, tsadv$PositionStructure ps, base$OrganizationExt o " +
                    "                   where a.personGroup.id = :personGroupId " +
                    "                     and ps.positionGroup.id = a.positionGroup.id " +
                    "                     and o.group.id = ps.organizationGroup.id" +
                    "                     and a.primaryFlag = TRUE" +
                    "                     and :systemDate between a.startDate and a.endDate" +
                    "                     and :systemDate between ps.startDate and ps.endDate" +
                    "                     and :systemDate between ps.posStartDate and ps.posEndDate" +
                    "                     and :systemDate between o.startDate and o.endDate)";
        }

        if (query != null) {
            DicCostCenter costCenter = commonService.getEntity(DicCostCenter.class, query, params, View.LOCAL);
            getItem().setCostCenter(costCenter);
        } else
            getItem().setCostCenter(null);

    }


    protected static String concat(String s1, String s2) {
        StringBuilder sb = new StringBuilder("");
        if (s1 != null) sb.append(s1);
        if (s1 != null && s2 != null) sb.append("<br/>");
        if (s2 != null) sb.append(s2);
        return sb.toString();
    }


    protected void checkPermission() {
        RequisitionStatus requisitionStatus = getItem().getRequisitionStatus();
        UUID userPersonGroupId = userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP_ID);
        boolean isCreate = PersistenceHelper.isNew(getItem());
        boolean isDraft = requisitionStatus.equals(RequisitionStatus.DRAFT);
        boolean isClosed = requisitionStatus.equals(RequisitionStatus.CLOSED) || requisitionStatus.equals(RequisitionStatus.CLOSED_MANUALLY);
        boolean isManager = userPersonGroupId != null && getItem().getManagerPersonGroup() != null && getItem().getManagerPersonGroup().getId().equals(userPersonGroupId);
        boolean isRecruiter = userPersonGroupId != null && getItem().getRecruiterPersonGroup() != null && getItem().getRecruiterPersonGroup().getId().equals(userPersonGroupId);

        UUID organizationGroupId = Optional.ofNullable(getItem())
                .map(Requisition::getOrganizationGroup)
                .map(BaseUuidEntity::getId)
                .orElse(null);
        List<OrganizationHrUser> rmPersonGroupId = organizationGroupId != null ? getRmPersonGroupId(organizationGroupId) : null;

        boolean isRecruiterManager = userPersonGroupId != null && getItem().getOrganizationGroup() != null
                && rmPersonGroupId != null && !rmPersonGroupId.isEmpty()
                && rmPersonGroupId
                .stream()
                .filter(organizationHrUser -> employeeService.getPersonGroupByUserId(organizationHrUser.getUser().getId()).getId().equals(userPersonGroupId)) //TODO:personGroupId need to test
                .count() > 0;

        sendToRecruiter.setVisible(isDraft && (isManager || isRecruiter));

        close.setVisible(this.isRecruiter && !isCreate && !isClosed);
        approve.setVisible(this.isRecruiter && isRecruiterManager && requisitionStatus == RequisitionStatus.ON_APPROVAL);
        reject.setVisible(this.isRecruiter && isRecruiterManager && requisitionStatus == RequisitionStatus.ON_APPROVAL);

        if (browseOnly) {
            reject.setVisible(false);
            windowCommit.setVisible(false);
        }
    }

    /**
     * Закрыть заявку
     */
    public void closeConfirm() {
        if (validateAll()) {
            RequisitionCancel requisitionCancel = (RequisitionCancel) openWindow(
                    "requisition-cancel",
                    WindowManager.OpenType.DIALOG,
                    ParamsMap.of(StaticVariable.REQUISITION_CANCEL_MSG,
                            String.format(getMessage("Requisition.dialog.close"),
                                    getItem().getJobGroup().getJob().getJobName(),
                                    getItem().getCode())));

            requisitionCancel.addCloseListener(new CloseListener() {
                @Override
                public void windowClosed(String actionId) {
                    if (actionId.equalsIgnoreCase("ok")) {
                        getItem().setRequisitionStatus(RequisitionStatus.CLOSED_MANUALLY);
                        getItem().setReason(requisitionCancel.getReasonValue());

                        SendingNotification sendingNotification = sendNotificationAndSave(null,
                                getItem().getCreatedBy(),
                                "requisition.notify.close");
                        if (sendingNotification != null) {
                            TsadvUser assignedBy = employeeService.getUserExtByPersonGroupId(getItem().getManagerPersonGroup().getId());
                            activityService.createActivity(
                                    getUserExtByLogin(getItem().getCreatedBy()),
                                    assignedBy != null ? assignedBy : employeeService.getSystemUser(),
                                    commonService.getEntity(ActivityType.class, "NOTIFICATION"),
                                    StatusEnum.active,
                                    "description",
                                    null,
                                    new Date(),
                                    null,
                                    "",
                                    getItem().getId(),
                                    "requisition.notify.close",
                                    getParamsMap(getUserExtByLogin(getItem().getCreatedBy())));
                        }
                        for (JobRequest jobRequest : requisitionDs.getItem().getJobRequests()) {
                            if (jobRequest.getRequestStatus() != JobRequestStatus.HIRED && jobRequest.getRequestStatus() != JobRequestStatus.REJECTED) {
                                List<Offer> offerList = commonService.getEntities(Offer.class,
                                        "select e from tsadv$Offer e where e.jobRequest.id = :jobRequestId",
                                        Collections.singletonMap("jobRequestId", jobRequest.getId()),
                                        "offer.edit");
                                if (offerList.isEmpty() && offerList.size() == 0) {
                                    sendNotificationForCandidate(jobRequest.getCandidatePersonGroup().getId(),
                                            null,
                                            "requisition.notify.candidate.close");
                                    if (sendingNotification != null && getUserExt(jobRequest.getCandidatePersonGroup().getId()) != null) {
                                        TsadvUser assignedBy = employeeService.getUserExtByPersonGroupId(getItem().getManagerPersonGroup().getId());
                                        activityService.createActivity(
                                                getUserExt(jobRequest.getCandidatePersonGroup().getId()),
                                                assignedBy != null ? assignedBy : employeeService.getSystemUser(),
                                                commonService.getEntity(ActivityType.class, "NOTIFICATION"),
                                                StatusEnum.active,
                                                "description",
                                                null,
                                                new Date(),
                                                null,
                                                "",
                                                getItem().getId(),
                                                "requisition.notify.candidate.close",
                                                getParamsMap(getUserExt(jobRequest.getCandidatePersonGroup().getId())));
                                    }
                                }
                            }
                        }
                        //закрываем предыдущий task
                        activityService.doneActivity(getItem().getId(), "REQUISITION_APPROVE");
                    }
                }
            });
        }
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        if (errors.isEmpty() && getItem().getJobGroup() != null && getItem().getJobGroup().getJob() == null) {
            Date minDate = getItem().getJobGroup().getList().stream()
                    .min(Comparator.comparing(AbstractTimeBasedEntity::getStartDate))
                    .map(AbstractTimeBasedEntity::getStartDate).orElse(null);
            errors.add(formatMessage("job.exists.from.date", format.format(minDate)));
        }
    }

    /**
     * Подтвердить заявку
     */
    public void approveConfirm() {
        if (validateAll()) {
            if (getItem().getRecruiterPersonGroup() == null) {
                MetaClass requisitionMetaClass = metadata.getClass("tsadv$Requisition");
                showNotification(messages.formatMainMessage("validation.required.defaultMsg",
                        messages.getTools().getPropertyCaption(requisitionMetaClass, "recruiterPersonGroup")),
                        NotificationType.TRAY);
            } else
                showOptionDialog(getMessage("Requisition.dialog.title"),
                        String.format(getMessage("Requisition.dialog.approve"),
                                getItem().getJobGroup().getJob().getJobName(),
                                getItem().getCode()),
                        MessageType.CONFIRMATION_HTML, new Action[]{
                                new DialogAction(DialogAction.Type.OK) {
                                    @Override
                                    public void actionPerform(Component component) {
                                        getItem().setRequisitionStatus(RequisitionStatus.OPEN);

                                        if (hasBeenOpened()) {
                                            SendingNotification sendingNotification = sendNotificationAndSave(getItem().getRecruiterPersonGroup().getId(),
                                                    null,
                                                    "requisition.notify.change.approve");
                                            if (sendingNotification != null) {
                                                TsadvUser assignedBy = employeeService.getUserExtByPersonGroupId(getItem().getManagerPersonGroup().getId());
                                                activityService.createActivity(
                                                        getUserExt(getItem().getRecruiterPersonGroup().getId()),
                                                        assignedBy != null ? assignedBy : employeeService.getSystemUser(),
                                                        commonService.getEntity(ActivityType.class, "NOTIFICATION"),
                                                        StatusEnum.active,
                                                        "description",
                                                        null,
                                                        new Date(),
                                                        null,
                                                        "",
                                                        getItem().getId(),
                                                        "requisition.notify.change.approve",
                                                        getParamsMap(getUserExt(getItem().getRecruiterPersonGroup().getId())));
                                            }
                                            close(COMMIT_ACTION_ID);
                                        } else {
                                            getItem().setStartDate(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH));

                                            if (recruitmentConfig.getAutofillRequisitionFinalDate()) {
                                                Date calcFinalCollectDate = DateUtils.addDays(getItem().getStartDate(), getFinalCollectDays());
                                                if (calcFinalCollectDate.after(getItem().getFinalCollectDate()))
                                                    getItem().setFinalCollectDate(calcFinalCollectDate);
                                            }

                                            SendingNotification sendingNotification = sendNotificationAndSave(null,
                                                    getItem().getCreatedBy(),
                                                    "requisition.notify.approve");
                                            TsadvUser assignedBy = employeeService.getUserExtByPersonGroupId(getItem().getManagerPersonGroup().getId());
                                            activityService.createActivity(
                                                    getUserExtByLogin(getItem().getCreatedBy()),
                                                    assignedBy != null ? assignedBy : employeeService.getSystemUser(),
                                                    commonService.getEntity(ActivityType.class, "NOTIFICATION"),
                                                    StatusEnum.active,
                                                    "description",
                                                    null,
                                                    new Date(),
                                                    null,
                                                    "",
                                                    getItem().getId(),
                                                    "requisition.notify.approve",
                                                    getParamsMap(getUserExtByLogin(getItem().getCreatedBy())));

                                           /* sendingNotification = sendNotificationAndSave(getItem().getRecruiterPersonGroup().getId(),
                                                    null,
                                                    "requisition.notify.approve");
                                            assignedBy = employeeService.getUserExtByPersonGroupId(getItem().getManagerPersonGroup().getId());
                                            activityService.createActivity(notificationService.getNotificationTemplate("requisition.notify.approve").getName(),
                                                    getUserExt(getItem().getRecruiterPersonGroup().getId()),
                                                    assignedBy != null ? assignedBy : employeeService.getSystemUser(),
                                                    commonService.getEntity(ActivityType.class, "NOTIFICATION"),
                                                    StatusEnum.active,
                                                    "description",
                                                    null,
                                                    new Date(),
                                                    null,
                                                    "",
                                                    getItem().getId(),
                                                    "requisition.notify.approve",
                                                    getParamsMap(getUserExt(getItem().getRecruiterPersonGroup().getId())));*/

                                        }
                                        //закрываем предыдущий task
                                        activityService.doneActivity(getItem().getId(), "REQUISITION_APPROVE");
                                    }
                                },
                                new DialogAction(DialogAction.Type.CANCEL)
                        });
        }
    }


    /**
     * Отклонить заявку
     */
    public void rejectConfirm() {
        if (validateAll()) {
            RequisitionCancel requisitionCancel = (RequisitionCancel) openWindow(
                    "requisition-cancel",
                    WindowManager.OpenType.DIALOG,
                    ParamsMap.of(StaticVariable.REQUISITION_CANCEL_MSG,
                            String.format(getMessage("Requisition.dialog.reject"), getItem().getCode())));

            requisitionCancel.addCloseListener(actionId -> {
                if (actionId.equalsIgnoreCase("ok")) {

                    getItem().setReason(requisitionCancel.getReasonValue());

                    if (hasBeenOpened()) {
                        List<RequisitionTmp> reqTmpList = commonService.getEntities(RequisitionTmp.class,
                                "select e " +
                                        "    from tsadv$RequisitionTmp e " +
                                        "   where e.requisition.id = :requisitionId " +
                                        "     and e.requisitionStatus = 1 " +
                                        "order by e.createTs desc",
                                Collections.singletonMap("requisitionId", getItem().getId()),
                                "requisitionTmp.full");

                        this.listenChangeValue = false;
                        if (reqTmpList != null && reqTmpList.size() > 0) {
                            for (String field : metadata
                                    .getClassNN("tsadv$RequisitionTmp")
                                    .getOwnProperties()
                                    .stream()
                                    .map(MetaProperty::getName)
                                    .filter(p -> excludeProperties.indexOf(p) < 0)
                                    .collect(Collectors.toList())) {
//                                getItem()._persistence_set(field, reqTmpList.get(0)._persistence_get(field));
                                try {
                                    Method getMethod = new PropertyDescriptor(field, RequisitionTmp.class).getReadMethod();
                                    Method setMethod = new PropertyDescriptor(field, Requisition.class).getWriteMethod();
                                    setMethod.invoke(getItem(), getMethod.invoke(reqTmpList.get(0)));
                                } catch (Exception e) {
                                }
                            }
                        }

                        SendingNotification sendingNotification = sendNotificationAndSave(getItem().getRecruiterPersonGroup().getId(),
                                null,
                                "requisition.notify.change.reject");
                        if (sendingNotification != null) {
                            TsadvUser assigndeBy = employeeService.getUserExtByPersonGroupId(getItem().getManagerPersonGroup().getId());
                            activityService.createActivity(
                                    getUserExt(getItem().getRecruiterPersonGroup().getId()),
                                    assigndeBy != null ? assigndeBy : employeeService.getSystemUser(),
                                    commonService.getEntity(ActivityType.class, "NOTIFICATION"),
                                    StatusEnum.active,
                                    "description",
                                    null,
                                    new Date(),
                                    null,
                                    "",
                                    getItem().getId(),
                                    "requisition.notify.change.reject",
                                    getParamsMap(getUserExt(getItem().getRecruiterPersonGroup().getId())));
                        }
                        isDescriptionOrNameSiteChanged = false;
                        requisitionDs.refresh();
                        isDescriptionOrNameSiteChanged = true;
                        this.listenChangeValue = true;

                        close(COMMIT_ACTION_ID);
                        rejectConfirm = true;
                    } else {
                        rejectConfirm = true;
                        getItem().setRequisitionStatus(RequisitionStatus.CANCELED);

                        SendingNotification sendingNotification = sendNotificationAndSave(null,
                                getItem().getCreatedBy(),
                                "requisition.notify.reject");
                        if (sendingNotification != null) {
                            TsadvUser assignedBy = employeeService.getUserExtByPersonGroupId(getItem().getManagerPersonGroup().getId());
                            activityService.createActivity(
                                    getUserExtByLogin(getItem().getCreatedBy()),
                                    assignedBy != null ? assignedBy : employeeService.getSystemUser(),
                                    commonService.getEntity(ActivityType.class, "NOTIFICATION"),
                                    StatusEnum.active,
                                    "description",
                                    null,
                                    new Date(),
                                    null,
                                    "",
                                    getItem().getId(),
                                    "requisition.notify.reject",
                                    getParamsMap(getUserExtByLogin(getItem().getCreatedBy())));
                        }
                    }
                    //закрываем предыдущий task
                    activityService.doneActivity(getItem().getId(), "REQUISITION_APPROVE");
                    close(COMMIT_ACTION_ID);
                }
            });
        }
    }

    /**
     * Отправить заявку на подтверждение
     */


    public void sendToRecruiterConfirm() {
        if (validateAll()) {
            isSendToConfirmYes = false;

            if (!isSendToConfirmYes) {
                sendToRecruiter.setEnabled(false);
            }
            List<OrganizationHrUser> rmPersonGroupIdList = getRmPersonGroupId(getItem().getOrganizationGroup() != null ? getItem().getOrganizationGroup().getId() : null);

            if (PersistenceHelper.isNew(getItem()) && recruitmentConfig.getAutogenerateRequisitionCode()) {
                long currentSequenceValue = generateNextSequenceValue();
                getItem().setCode(String.format("R%07d", currentSequenceValue));
            }
//            showOptionDialog(getMessage("Requisition.dialog.title"),
//                    String.format(getMessage("Requisition.dialog.send"), getItem().getCode()),
            showOptionDialog(getMessage("Requisition.dialog.title"),
                    String.format(getMessage("Requisition.dialog.send"),
                            getItem().getJobGroup().getJob().getJobName(),
                            getItem().getCode()),
                    MessageType.CONFIRMATION_HTML,
                    new Action[]{
                            new DialogAction(DialogAction.Type.OK) {
                                @Override
                                public void actionPerform(Component component) {
                                    if (!isActiveYes) return;
                                    isActiveYes = false;
                                    getItem().setRequisitionStatus(RequisitionStatus.ON_APPROVAL);
                                    String notificationCode = hasBeenOpened() ? "requisition.notify.change.send" : "requisition.notify.send";
                                    for (OrganizationHrUser ohu : rmPersonGroupIdList) {
                                        Map<String, Object> paramsMap = new HashMap<>();
                                        paramsMap.put("code", getItem().getCode());
                                        paramsMap.put("personFullName", employeeService.getPersonGroupByUserId(ohu.getUser().getId()).getPerson().getFullName()); //TODO: personGroupId need to test
                                        paramsMap.put("jobGroup", getItem().getJobGroup().getJob().getJobName());
                                        paramsMap.put("requisition", getItem().getId());
                                        paramsMap.put("webAppUrl", AppContext.getProperty("cuba.webAppUrl"));
                                        paramsMap.put("author", getItem().getManagerPersonGroup().getPerson().getFullName());
                                        SendingNotification sendingNotification = notificationService.sendParametrizedNotification(
                                                notificationCode,
                                                ohu.getUser(),
                                                paramsMap);
                                        NotificationTemplate notificationTemplate = notificationService.getNotificationTemplate(notificationCode);
                                        TsadvUser assignedBy = employeeService.getUserExtByPersonGroupId(getItem().getManagerPersonGroup().getId());
                                        Activity activity = activityService.createActivityWithoutCommit(
                                                ohu.getUser(),
                                                assignedBy != null ? assignedBy : employeeService.getSystemUser(),
                                                commonService.getEntity(ActivityType.class, "REQUISITION_APPROVE"),
                                                StatusEnum.active,
                                                "description",
                                                null,
                                                new Date(),
                                                null,
                                                "",
                                                getItem().getId(),
                                                notificationCode,
                                                paramsMap
                                        );
                                        commitEntities.add(activity);

                                    }
                                    sendToConfirm = true;
                                    commitAndClose();
                                    close("close");
                                    sendToConfirm = false;

                                    isActiveYes = true;
                                    isSendToConfirmYes = true;
                                }
                            },
                            new DialogAction(DialogAction.Type.CANCEL) {
                                @Override
                                public void actionPerform(Component component) {
                                    getItem().setCode(null);
                                    isSendToConfirmYes = true;
                                    sendToRecruiter.setEnabled(true);

                                    super.actionPerform(component);
                                }
                            }
                    });
        }
    }

    protected long generateNextSequenceValue() {
        long currentSequenceValue = recruitmentService.getCurrentSequenceValue("requisitionCode");
        while (commonService.getEntities(Requisition.class, String.format("select e from tsadv$Requisition e" +
                " where e.code = 'R%07d'", currentSequenceValue), null, null).size() != 0) {
            currentSequenceValue = uniqueNumbersService.getNextNumber("requisitionCode");
        }
        return currentSequenceValue;
    }

    protected List<OrganizationHrUser> getRmPersonGroupId(UUID organizationGroupId) {
        List<OrganizationHrUser> list = employeeService.getHrUsers(organizationGroupId, "RECRUITING_MANAGER");
        return list;
    }

    protected SendingNotification sendNotificationAndSave(UUID personGroupId, String login, String templateCode) {
        SendingNotification sendingNotification = null;
        boolean success = false;
        String errorMessage = "";

        try {
            TsadvUser userExt = personGroupId != null ? getUserExt(personGroupId) : getUserExtByLogin(login);
            if (userExt != null) {
                Map<String, Object> paramsMap = getParamsMap(userExt);
                sendingNotification = notificationService.sendParametrizedNotification(
                        templateCode,
                        userExt,
                        paramsMap);

                success = true;
            } else {
                errorMessage = "TsadvUser is null!";
            }
        } catch (Exception ex) {
            errorMessage = ex.getMessage();
        }

        if (success) {
            approveConfirm = true;
            commitAndClose();
            approveConfirm = false;
        } else {
            showNotification(getMessage("msg.error.title"),
                    errorMessage,
                    NotificationType.TRAY);
        }
        return sendingNotification;
    }

    protected Map<String, Object> getParamsMap(TsadvUser userExt) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("code", getItem().getCode());
        Case personNameEn = getCasePersonName(userExt, "en", "Nominative");
        Case personNameKz = getCasePersonName(userExt, "kz", "Атау септік");
        Case personNameRu = getCasePersonName(userExt, "ru", "Именительный");
        PersonGroupExt personGroup = employeeService.getPersonGroupByUserId(userExt.getId());
        paramsMap.put("personFullNameEn", getLongValueOrFullName(personNameEn, personGroup));
        paramsMap.put("personFullNameKz", getLongValueOrFullName(personNameKz, personGroup));
        paramsMap.put("personFullNameRu", getLongValueOrFullName(personNameRu, personGroup));
        paramsMap.put("personFullName", personGroup == null ? "" : personGroup.getPerson().getFullName());
        paramsMap.put("reason", getItem().getReason());
        paramsMap.put("jobName", getItem().getJobGroup().getJob() != null ? getItem().getJobGroup().getJob().getJobName() : "");
        paramsMap.put("recruiterFullName", getItem().getRecruiterPersonGroup() != null ? getItem().getRecruiterPersonGroup().getPersonFirstLastNameLatin() : null);
        return paramsMap;
    }

    protected void sendNotificationForCandidate(UUID candidateId, String login, String templateCode) {
        boolean success = false;
        String errorMessage = "";

        try {
            TsadvUser userExt = candidateId != null ? getUserExt(candidateId) : getUserExtByLogin(login);
            if (userExt != null) {
                Map<String, Object> paramsMap = new HashMap<>();
                Case personNameEn = getCasePersonName(userExt, "en", "Nominative");
                Case personNameKz = getCasePersonName(userExt, "kz", "Атау септік");
                Case personNameRu = getCasePersonName(userExt, "ru", "Именительный");

                Case jobNameEn = getCaseJobName(getItem(), "en", "Nominative");
                Case jobNameKz = getCaseJobName(getItem(), "kz", "Атау септік");
                Case jobNameRu = getCaseJobName(getItem(), "ru", "Именительный");

                if (jobNameEn != null) {
                    paramsMap.put("positionNameEn", jobNameEn.getLongName() == null ? getItem().getJobGroup().getJob().getJobName() : jobNameEn.getLongName());
                } else {
                    paramsMap.put("positionNameEn", getItem().getJobGroup().getJob() == null ? "" : getItem().getJobGroup().getJob().getJobName());
                }
                if (jobNameKz != null) {
                    paramsMap.put("positionNameKz", jobNameKz.getLongName() == null ? getItem().getJobGroup().getJob().getJobName() : jobNameKz.getLongName());
                } else {
                    paramsMap.put("positionNameKz", getItem().getJobGroup().getJob() == null ? "" : getItem().getJobGroup().getJob().getJobName());
                }
                if (jobNameRu != null) {
                    paramsMap.put("positionNameRu", jobNameRu.getLongName() == null ? getItem().getJobGroup().getJob().getJobName() : jobNameRu.getLongName());
                } else {
                    paramsMap.put("positionNameRu", getItem().getJobGroup().getJob() == null ? "" : getItem().getJobGroup().getJob().getJobName());
                }
                //TODO: personGroupId need to test
                PersonGroupExt personGroup = employeeService.getPersonGroupByUserId(userExt.getId());
                paramsMap.put("personFullNameEn", getLongValueOrFullName(personNameEn, personGroup));
                paramsMap.put("personFullNameKz", getLongValueOrFullName(personNameKz, personGroup));
                paramsMap.put("personFullNameRu", getLongValueOrFullName(personNameRu, personGroup));
                paramsMap.put("code", getItem().getCode());
                //  paramsMap.put("personFullName", userExt.getPersonGroup() == null ? "" : userExt.getPersonGroup().getPerson().getFullName()); //my params
//                paramsMap.put("reason", getItem().getReason());
                notificationService.sendParametrizedNotification(
                        templateCode,
                        userExt,
                        paramsMap);
            } else {
                errorMessage = "TsadvUser is null!";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected String getLongValueOrFullName(Case personNameEn, PersonGroupExt personGroup) {
        String personFullNameEn = "";
        if (personNameEn != null) {
            if (personNameEn.getLongName() == null) {
                if (personGroup != null) {
                    personFullNameEn = personGroup.getPersonFirstLastNameLatin();
                }
            } else {
                personFullNameEn = personNameEn.getLongName();
            }
        } else {
            if (personGroup != null) {
                personFullNameEn = personGroup.getPersonFirstLastNameLatin();
            }
        }
        return personFullNameEn;
    }

    protected Case getCasePersonName(TsadvUser userExt, String language, String caseName) {
        PersonGroupExt personGroupExt = employeeService.getPersonGroupByUserId(userExt.getId());
        if (personGroupExt == null) return null;
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("personGroupId", personGroupExt.getId()); //TODO:personGroupId
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

    protected Case getCaseJobName(Requisition requisition, String language, String caseName) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("jobGroupId", requisition.getJobGroup().getId());
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

    protected TsadvUser getUserExt(UUID personGroupId) {
        LoadContext<TsadvUser> loadContext = LoadContext.create(TsadvUser.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$UserExt e where e.personGroup.id = :pgId");
        query.setParameter("pgId", personGroupId);
        loadContext.setQuery(query);
        loadContext.setView("user.browse");
        return dataManager.load(loadContext);
    }

    protected TsadvUser getUserExtByLogin(String login) {
        LoadContext<TsadvUser> loadContext = LoadContext.create(TsadvUser.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$UserExt e where LOWER(e.login) = :login ");
        query.setParameter("login", login != null ? login.toLowerCase() : null);
        loadContext.setQuery(query);
        loadContext.setView("user.browse");
        return dataManager.load(loadContext);
    }

    protected int getFinalCollectDays() {
        LoadContext<GlobalValue> loadContext = LoadContext.create(GlobalValue.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e from tsadv$GlobalValue e " +
                        "where e.name = :name and :sysDate between e.startDate and e.endDate")
                .setParameter("name", "finalCollectDate")
                .setParameter("sysDate", CommonUtils.getSystemDate()))
                .setView("globalValue.edit");

        int result = 14;

        GlobalValue globalValue = dataManager.load(loadContext);

        if (globalValue != null) {
            try {
                result = Integer.parseInt(globalValue.getValue());
            } catch (Exception ex) {
                //
            }
        }

        return result;
    }

    protected boolean hasBeenOpened() {
        requisitionHistoryDs.refresh();
        return requisitionHistoryDs.getItems().stream().filter(rh -> rh.getStatus() == RequisitionStatus.OPEN).count() > 0;
    }


    public void customWindowCommit() {
        boolean showWarning = false;
        if (requisitionDs.isModified() && getItem().getRequisitionStatus() == RequisitionStatus.OPEN
                && recruitmentService.getIsChangeStatus(getItem(), userSession.getUser().getLogin())) {
            Requisition requisition = dataManager.reload(getItem(), "requisition.view");
            for (String field : metadata
                    .getClassNN("tsadv$RequisitionTmp")
                    .getOwnProperties()
                    .stream()
                    .map(MetaProperty::getName)
                    .filter(p -> excludeProperties.indexOf(p) < 0)
                    .collect(Collectors.toList())) {
                try {
                    Method getMethod = new PropertyDescriptor(field, Requisition.class).getReadMethod();
                    if (!compare(getMethod.invoke(getItem()), getMethod.invoke(requisition)) && !"requisitionStatus".equals(field)) {
                        showWarning = true;
                        break;
                    }
                } catch (Exception e) {
                }
            }
        }
        if (showWarning) {
            showOptionDialog(
                    getMessage("msg.warning.title"),
                    getMessage("Requisition.dialog.change.open"),
                    MessageType.WARNING,
                    new Action[]{
                            new DialogAction(DialogAction.Type.YES) {
                                @Override
                                public void actionPerform(Component component) {
                                    commitAndClose();
                                }
                            },
                            new DialogAction(DialogAction.Type.CANCEL)
                    });
        } else
            commitAndClose();
    }

    protected static boolean compare(Object o1, Object o2) {
        if (o1 == null && o2 == null) return true;
        if (o1 == null || o2 == null) return false;
        return o1.equals(o2);
    }

    protected void managerDescriptionListener() {
        List<RichTextArea> richTextAreas = new ArrayList<>();
        richTextAreas.add(addRichTextArea1);
        richTextAreas.add(addRichTextArea2);
        richTextAreas.add(addRichTextArea3);

        for (RichTextArea richTextArea : richTextAreas) {
            richTextArea.addValueChangeListener(e -> {
                updateDescriptionValue(e, richTextArea);
            });
        }
    }

    protected void updateDescriptionValue(HasValue.ValueChangeEvent<String> e, RichTextArea richTextArea) {
        if (changed) {
            changed = false;
        } else {
            if (e != null && e.getValue() != null) {
                List<String> list = new ArrayList<>();
                Pattern pattern = Pattern.compile("[<][^>]*\\-[0-9]+[^>]*[>]");
                Matcher matcher = pattern.matcher(((String) e.getValue()));
                while (matcher.find()) {
                    list.add(matcher.group());
                }
                Pattern pattern2 = Pattern.compile("\\-[0-9]+");
                list.forEach(s -> {
                    Matcher matcher2 = pattern2.matcher(s);
                    while (matcher2.find()) {
                        newText = ((String) e.getValue()).replace(s, s.replaceAll("\\-[0-9]+", "0"));
                        changed = true;
                        richTextArea.setValue(newText);
                    }
                });
            }
        }
    }
}
