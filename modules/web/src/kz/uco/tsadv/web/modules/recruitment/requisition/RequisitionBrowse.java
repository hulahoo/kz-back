package kz.uco.tsadv.web.modules.recruitment.requisition;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.core.global.filter.Op;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.gui.components.renderers.WebComponentRenderer;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.service.common.CommonService;
import kz.uco.base.web.abstraction.AbstractDictionaryDatasource;
import kz.uco.base.web.components.CustomFilter;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.recruitment.config.RecruitmentConfig;
import kz.uco.tsadv.modules.recruitment.enums.RequisitionStatus;
import kz.uco.tsadv.modules.recruitment.enums.RequisitionType;
import kz.uco.tsadv.modules.recruitment.model.*;
import kz.uco.tsadv.web.modules.recruitment.requisition.config.CheckRequisitionEndDateConfig;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class RequisitionBrowse extends AbstractLookup {

    @Inject
    protected DataManager dataManager;

    protected RecruitmentConfig recruitmentConfig;

    @Inject
    protected VBoxLayout filterBox;

    protected Boolean isSelfService = false;

    @Inject
    protected DataGrid<Requisition> requisitionsTable;

    @Named("requisitionsTable.create")
    protected CreateAction requisitionsTableCreate;
    @Named("requisitionsTable.edit")
    protected EditAction requisitionsTableEdit;
    @Inject
    protected Button removeBtn;
    @Named("requisitionsTable.copy")
    protected Action requisitionsTableCopy;
    @Inject
    protected GroupDatasource<Requisition, UUID> requisitionsDs;
    @Inject
    protected AbstractDictionaryDatasource dicEmploymentTypesDs;
    @Inject
    protected AbstractDictionaryDatasource dicLocationsDs;
    @Inject
    protected AbstractDictionaryDatasource dicCitiesDs;
    @Inject
    protected CollectionDatasource<JobGroup, UUID> jobGroupsDs;
    @Inject
    protected CollectionDatasource<OrganizationGroupExt, UUID> organizationGroupsDs;
    @Inject
    protected CollectionDatasource<PositionGroupExt, UUID> positionGroupsDs;

    @Inject
    protected UserSession userSession;

    protected Map<String, CustomFilter.Element> filterMap;
    protected CustomFilter customFilter;
    @Inject
    protected ComponentsFactory componentsFactory;

    protected Map<String, Object> paramsMap = new HashMap<>();
    @Inject
    protected Metadata metadata;
    @Inject
    protected Configuration configuration;

    @Inject
    protected CommonService commonService;
    @Inject
    protected CheckRequisitionEndDateConfig checkRequisitionEndDateConfig;
    protected String jobRequestWindowId = "";
    @Inject
    protected GroupBoxLayout groupBoxCustomFilter;
    @Inject
    protected Filter filter;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        initFilterMap();
        removeBtn.setEnabled(false);
        recruitmentConfig = configuration.getConfig(RecruitmentConfig.class);
        if (recruitmentConfig.getCandidateForm() != null && !recruitmentConfig.getCandidateForm().equals("")) {
            jobRequestWindowId = recruitmentConfig.getCandidateForm();
        }
        LoadContext<PersonGroupExt> loadContext = LoadContext.create(PersonGroupExt.class);
        loadContext.setQuery(LoadContext.createQuery("SELECT a.personGroup " +
                " FROM base$AssignmentExt a " +
                " JOIN base$HierarchyElementExt he " +
                " JOIN base$PositionExt p " +
                "WHERE he.positionGroup.id = a.positionGroup.id " +
                "  AND he.positionGroup IS NOT NULL " +
                "  AND he.hierarchy.primaryFlag = TRUE " +
                "  AND :systemDate BETWEEN he.startDate AND he.endDate " +
                "  AND p.group.id = he.positionGroup.id " +
                "  AND :systemDate BETWEEN p.startDate AND p.endDate " +
                "  AND (he.id IN (SELECT he1.parent.id " +
                "                   FROM base$HierarchyElementExt he1 " +
                "                  WHERE he1.positionGroup.id IS NOT NULL " +
                "                    AND :systemDate BETWEEN he1.startDate AND he1.endDate) " +
                "   OR p.managerFlag = TRUE) " +
                "  AND :systemDate BETWEEN a.startDate AND a.endDate")
                .setParameter("systemDate", CommonUtils.getSystemDate()));
        dataManager.loadList(loadContext);

        setQuery();
        requisitionsTableCopy.setEnabled(false);
        requisitionsDs.addItemChangeListener(e -> {
            if (e.getItem() != null) {
                requisitionsTableCopy.setEnabled(true);
                if (RequisitionStatus.DRAFT.equals(e.getItem().getRequisitionStatus()))
                    removeBtn.setEnabled(true);
                else
                    removeBtn.setEnabled(false);
            } else {
                requisitionsTableCopy.setEnabled(false);
            }
        });


        requisitionsTableCreate.setWindowParams(ParamsMap.of("isRecruiter", isRecruiter()));
        paramsMap.put("isRecruiter", isRecruiter());

        requisitionsTableCreate.setWindowParams(paramsMap);
//        paramsMap.putAll(params);
        requisitionsTableEdit.setWindowParams(paramsMap);

        requisitionsTableCreate.setAfterWindowClosedHandler((window, closeActionId) -> {
            requisitionsDs.refresh();
        });
        requisitionsTableEdit.setAfterWindowClosedHandler((window, closeActionId) -> {
            requisitionsDs.refresh();
        });

        DataGrid.Column nameForSiteLang = requisitionsTable.addGeneratedColumn("nameForSiteLang", new DataGrid.ColumnGenerator<Requisition, Component>() {
            @Override
            public Component getValue(DataGrid.ColumnGeneratorEvent<Requisition> event) {
                return generateNameForSiteLang(event.getItem());
            }

            @Override
            public Class<Component> getType() {
                return Component.class;
            }
        });
        nameForSiteLang.setRenderer(new WebComponentRenderer());

        DataGrid.Column jobRequestCountColumn = requisitionsTable.addGeneratedColumn("jobRequestCount", new DataGrid.ColumnGenerator<Requisition, Component>() {
            @Override
            public Component getValue(DataGrid.ColumnGeneratorEvent<Requisition> event) {
                return generateJobRequestCountCell(event.getItem());
            }

            @Override
            public Class<Component> getType() {
                return Component.class;
            }
        });
        jobRequestCountColumn.setRenderer(new WebComponentRenderer());

        DataGrid.Column codeColumn = requisitionsTable.addGeneratedColumn("code", new DataGrid.ColumnGenerator<Requisition, Component>() {
            @Override
            public Component getValue(DataGrid.ColumnGeneratorEvent<Requisition> event) {
                return generateCodeCell(event.getItem());
            }

            @Override
            public Class<Component> getType() {
                return Component.class;
            }
        });
        codeColumn.setRenderer(new WebComponentRenderer());
        if (recruitmentConfig.getEnabledCustomFilter()) {
            customFilter = CustomFilter.init(requisitionsDs, requisitionsDs.getQuery(), filterMap);
            filterBox.add(customFilter.getFilterComponent());

            if (params.containsKey(StaticVariable.REQUISITION_BROWSE_FILTER)) {
                Map<String, Object> filterMap = (Map) params.get(StaticVariable.REQUISITION_BROWSE_FILTER);
                for (Map.Entry<String, Object> entry : filterMap.entrySet()) {
                    customFilter.selectFilter(entry.getKey(), entry.getValue());
                }
                customFilter.applyFilter();
            }
            applyFilter();
        }
        groupBoxCustomFilter.setVisible(recruitmentConfig.getEnabledCustomFilter());
        filter.setVisible(recruitmentConfig.getEnabledCubaFilter());
    }

    @Override
    public void ready() {
        super.ready();
        if (checkRequisitionEndDateConfig.getEnabled()) {
            checkEndDateLessThanToday();
        }
    }

    protected void setQuery() {
        if (recruitmentConfig.getSortingCVDate()) {
            requisitionsDs.setQuery("select e from tsadv$Requisition e\n" +
                    "                left join e.recruiterPersonGroup rp\n" +
                    "                left join rp.list p on :session$systemDate between p.startDate and p.endDate\n" +
                    "                order by e.finalCollectDate asc");

        }
    }

    protected void applyFilter() {
        if (getFullName() != null) {
            customFilter.selectFilter("recruiterPersonGroup", getFullName());
        }
        customFilter.selectFilter("requisitionStatus", RequisitionStatus.OPEN);
        customFilter.applyFilter();
    }

    public String getFullName() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userSession.getUser().getId());
        map.put("sysDate", CommonUtils.getSystemDate());
        StringBuilder builder = new StringBuilder("");
        PersonExt person = commonService.getEntity(PersonExt.class,
                "SELECT p " +
                        "FROM base$PersonExt p " +
                        "join tsadv$UserExtPersonGroup upg ON p.group.id = upg.personGroup.id " +
                        "where upg.userExt.id = :userId " +
                        "and :sysDate between p.startDate and p.endDate",
                map,
                "person-view");
        if (person != null) {
            builder.append(person.getLastName()).append(" ");
            builder.append(person.getFirstName()).append(" ");
            if (person.getMiddleName() != null) builder.append(person.getMiddleName()).append(" ");
            return builder.toString();
        } else {
            return null;
        }
    }

    protected boolean isRecruiter() {
        return true;
    }

    protected void initFilterMap() {
        filterMap = new LinkedHashMap<>();

        filterMap.put("code",
                CustomFilter.Element
                        .initElement()
                        .setCaption(getMessage("Requisition.code"))
                        .setComponentClass(TextField.class)
                        .setOptions(Op.CONTAINS, Op.DOES_NOT_CONTAIN, Op.ENDS_WITH, Op.STARTS_WITH, Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("lower(e.code) ?")
        );

        filterMap.put("requisitionType",
                CustomFilter.Element
                        .initElement()
                        .setCaption(getMessage("Requisition.requisitionType"))
                        .setComponentClass(LookupField.class)
                        .addComponentAttribute("optionsEnum", RequisitionType.class)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("e.requisitionType ?")
        );

        filterMap.put("employmentType",
                CustomFilter.Element
                        .initElement()
                        .setCaption(getMessage("Requisition.employmentType"))
                        .setComponentClass(LookupField.class)
                        .addComponentAttribute("optionsDatasource", dicEmploymentTypesDs)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("e.employmentType.id ?")
        );

        filterMap.put("location",
                CustomFilter.Element
                        .initElement()
                        .setCaption(getMessage("Requisition.location"))
                        .setComponentClass(LookupPickerField.class)
                        .addComponentAttribute("optionsDatasource", dicCitiesDs)
                        .addComponentAttribute("captionMode", CaptionMode.PROPERTY)
                        .addComponentAttribute("captionProperty", "settlementLangValue")
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("e.location.id ?")
        );

        filterMap.put("organizationGroup",
                CustomFilter.Element
                        .initElement()
                        .setCaption(getMessage("Requisition.organizationGroup"))
                        .setComponentClass(LookupPickerField.class)
                        .addComponentAttribute("optionsDatasource", organizationGroupsDs)
                        .addComponentAttribute("captionMode", CaptionMode.PROPERTY)
                        .addComponentAttribute("captionProperty", "organizationName")
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("e.organizationGroup.id ?")
        );

        filterMap.put("positionGroup",
                CustomFilter.Element
                        .initElement()
                        .setCaption(getMessage("Requisition.positionGroup"))
                        .setComponentClass(LookupPickerField.class)
                        .addComponentAttribute("optionsDatasource", positionGroupsDs)
                        .addComponentAttribute("captionMode", CaptionMode.PROPERTY)
                        .addComponentAttribute("captionProperty", "positionName")
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("e.positionGroup.id ?")
        );

        filterMap.put("jobGroup",
                CustomFilter.Element
                        .initElement()
                        .setCaption(getMessage("Requisition.jobGroup"))
                        .setComponentClass(LookupPickerField.class)
                        .addComponentAttribute("optionsDatasource", jobGroupsDs)
                        .addComponentAttribute("captionMode", CaptionMode.PROPERTY)
                        .addComponentAttribute("captionProperty", "jobName")
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("e.jobGroup.id ?")
        );

        filterMap.put("recruiterPersonGroup",
                CustomFilter.Element
                        .initElement()
                        .setCaption(getMessage("Requisition.recruiterPersonGroup"))
                        .setComponentClass(TextField.class)
                        .setOptions(Op.CONTAINS, Op.DOES_NOT_CONTAIN, Op.ENDS_WITH, Op.STARTS_WITH, Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("lower(concat(p.lastName,concat(' ', concat(p.firstName, concat(' ', coalesce(p.middleName,'')))))) ?")
        );

        filterMap.put("startDate",
                CustomFilter.Element
                        .initElement()
                        .setCaption(getMessage("Requisition.startDate"))
                        .setComponentClass(DateField.class)
                        .addComponentAttribute("resolution", DateField.Resolution.DAY)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL, Op.GREATER, Op.GREATER_OR_EQUAL, Op.LESSER, Op.LESSER_OR_EQUAL)
                        .setQueryFilter("e.startDate ?")
        );

        filterMap.put("endDate",
                CustomFilter.Element
                        .initElement()
                        .setCaption(getMessage("Requisition.endDate"))
                        .setComponentClass(DateField.class)
                        .addComponentAttribute("resolution", DateField.Resolution.DAY)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL, Op.GREATER, Op.GREATER_OR_EQUAL, Op.LESSER, Op.LESSER_OR_EQUAL)
                        .setQueryFilter("e.endDate ?")
        );

        filterMap.put("requisitionStatus",
                CustomFilter.Element
                        .initElement()
                        .setCaption(getMessage("Requisition.requisitionStatus"))
                        .setComponentClass(LookupField.class)
                        .addComponentAttribute("optionsEnum", RequisitionStatus.class)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("e.requisitionStatus ?")
        );

        filterMap.put("finalCollectDate",
                CustomFilter.Element
                        .initElement()
                        .setCaption(getMessage("Requisition.finalCollectDate"))
                        .setComponentClass(DateField.class)
                        .addComponentAttribute("resolution", DateField.Resolution.DAY)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL, Op.GREATER, Op.GREATER_OR_EQUAL, Op.LESSER, Op.LESSER_OR_EQUAL)
                        .setQueryFilter("e.finalCollectDate ?")
        );
        filterMap.put("nameForSite",
                CustomFilter.Element
                        .initElement()
                        .setCaption(getMessage("Requisition.search.by.name"))
                        .setComponentClass(TextField.class)
                        .setOptions(Op.CONTAINS, Op.DOES_NOT_CONTAIN, Op.ENDS_WITH, Op.STARTS_WITH, Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("lower(concat(e.nameForSiteLang1,concat(' ', concat(e.nameForSiteLang2, " +
                                "concat(' ', coalesce(e.nameForSiteLang3,'')))))) ?")
        );
    }

    protected String message(String messageKey) {
        return messages.getMessage("kz.uco.tsadv.recruitment", messageKey);
    }

    public Component generateJobRequestCountCell(Requisition entity) {
        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        linkButton.setCaption(String.valueOf(entity.getJobRequests().size()));
        linkButton.setAction(new BaseAction("openJobRequests") {
            @Override
            public void actionPerform(Component component) {
                super.actionPerform(component);
                Map<String, Object> map = new HashMap<>();
                if (jobRequestWindowId.equals("jobrequest-new")) {
                    map.put("requisitionId", entity);
                    AbstractWindow abstractWindow = openWindow(jobRequestWindowId, WindowManager.OpenType.THIS_TAB, map);
                    abstractWindow.addCloseListener(actionId -> {
                        requisitionsDs.refresh();
                    });
                } else {
                    map.putAll(paramsMap);
                    map.put("isRecruiter", isRecruiter());
                    map.put("fromLink", null);
                    AbstractEditor<Requisition> requisitionEdit = openEditor(requisitionsTableEdit.getWindowId(), entity, WindowManager.OpenType.THIS_TAB, map);
                    requisitionEdit.addCloseListener(actionId -> {
                        requisitionsDs.refresh();
                    });
                }
            }
        });
        return linkButton;
    }

    public Component generateCodeCell(Requisition entity) {
        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        linkButton.setCaption(String.valueOf(entity.getCode()));
        linkButton.setAction(new BaseAction("openCode") {
            @Override
            public void actionPerform(Component component) {
                editRequisition(component, entity);
            }
        });
        return linkButton;
    }

    public Component generateNameForSiteLang(Requisition entity) {
        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        linkButton.setCaption(entity.getNameForSiteLang());
        linkButton.setAction(new BaseAction("opengetNameForSiteLang") {
            @Override
            public void actionPerform(Component component) {
                editRequisition(component, entity);
            }
        });
        return linkButton;
    }

    public void copy() {
        Requisition newRequisition = metadata.create(Requisition.class);
        copyRequisition(requisitionsDs.getItem(), newRequisition);
        AbstractEditor<Requisition> requisitionEditor = openEditor(requisitionsTableEdit.getWindowId(), newRequisition, WindowManager.OpenType.THIS_TAB);
        requisitionEditor.addCloseWithCommitListener(() -> requisitionsDs.refresh());
        requisitionEditor.addCloseListener(action -> requisitionsDs.refresh());
    }

    protected void copyRequisition(Requisition sourceRequisition, Requisition destRequisition) {

        sourceRequisition = dataManager.reload(sourceRequisition, "requisition.view");

        destRequisition.setRequisitionTemplate(sourceRequisition.getRequisitionTemplate());
        destRequisition.setRequisitionType(sourceRequisition.getRequisitionType());
        destRequisition.setEmploymentType(sourceRequisition.getEmploymentType());
        destRequisition.setLocation(sourceRequisition.getLocation());
        destRequisition.setOrganizationGroup(sourceRequisition.getOrganizationGroup());
        destRequisition.setPositionGroup(sourceRequisition.getPositionGroup());
        destRequisition.setJobGroup(sourceRequisition.getJobGroup());
        destRequisition.setOpenedPositionsCount(sourceRequisition.getOpenedPositionsCount());
        destRequisition.setManagerPersonGroup(sourceRequisition.getManagerPersonGroup());
        destRequisition.setCostCenter(sourceRequisition.getCostCenter());
        destRequisition.setRecruiterPersonGroup(sourceRequisition.getRecruiterPersonGroup());
        destRequisition.setRequisitionStatus(sourceRequisition.getRequisitionStatus());
        destRequisition.setVideoInterviewRequired(sourceRequisition.getVideoInterviewRequired());
        destRequisition.setDescriptionLang1(sourceRequisition.getDescriptionLang1());
        destRequisition.setDescriptionLang2(sourceRequisition.getDescriptionLang2());
        destRequisition.setDescriptionLang3(sourceRequisition.getDescriptionLang3());
        destRequisition.setDescriptionLang4(sourceRequisition.getDescriptionLang4());
        destRequisition.setDescriptionLang5(sourceRequisition.getDescriptionLang5());
        destRequisition.setManagerDescriptionLang1(sourceRequisition.getManagerDescriptionLang1());
        destRequisition.setManagerDescriptionLang2(sourceRequisition.getManagerDescriptionLang2());
        destRequisition.setManagerDescriptionLang3(sourceRequisition.getManagerDescriptionLang3());
        destRequisition.setManagerDescriptionLang4(sourceRequisition.getManagerDescriptionLang4());
        destRequisition.setManagerDescriptionLang5(sourceRequisition.getManagerDescriptionLang5());
        destRequisition.setNameForSiteLang1(sourceRequisition.getNameForSiteLang1());
        destRequisition.setNameForSiteLang2(sourceRequisition.getNameForSiteLang2());
        destRequisition.setNameForSiteLang3(sourceRequisition.getNameForSiteLang3());
        destRequisition.setNameForSiteLang4(sourceRequisition.getNameForSiteLang4());
        destRequisition.setNameForSiteLang5(sourceRequisition.getNameForSiteLang5());
        destRequisition.setForSubstitution(sourceRequisition.getForSubstitution());
        destRequisition.setSubstitutablePersonGroup(sourceRequisition.getSubstitutablePersonGroup());
        destRequisition.setWithoutOffer(sourceRequisition.getWithoutOffer());

        //competences
        destRequisition.setCompetences(new ArrayList<>());
        for (RequisitionCompetence sCompetence : sourceRequisition.getCompetences()) {
            RequisitionCompetence competence = metadata.create(RequisitionCompetence.class);
            competence.setRequisition(destRequisition);
            competence.setCompetenceGroup(sCompetence.getCompetenceGroup());
            competence.setScaleLevel(sCompetence.getScaleLevel());
            competence.setCriticalness(sCompetence.getCriticalness());
            destRequisition.getCompetences().add(competence);
        }

        //hiring steps
        destRequisition.setHiringSteps(new ArrayList<>());
        for (RequisitionHiringStep sHiringStep : sourceRequisition.getHiringSteps()) {
            RequisitionHiringStep hiringStep = metadata.create(RequisitionHiringStep.class);
            hiringStep.setRequisition(destRequisition);
            hiringStep.setHiringStep(sHiringStep.getHiringStep());
            hiringStep.setOrder(sHiringStep.getOrder());
            destRequisition.getHiringSteps().add(hiringStep);
        }

        //members
        destRequisition.setMembers(new ArrayList<>());
        for (RequisitionMember sMember : sourceRequisition.getMembers()) {
            RequisitionMember member = metadata.create(RequisitionMember.class);
            member.setRequisition(destRequisition);
            member.setAccessLevel(sMember.getAccessLevel());
            member.setPersonGroup(sMember.getPersonGroup());
            destRequisition.getMembers().add(member);
        }

        //posting channels
        destRequisition.setPostingChannels(new ArrayList<>());
        for (RequisitionPostingChannel sPostingChannel : sourceRequisition.getPostingChannels()) {
            RequisitionPostingChannel postingChannel = metadata.create(RequisitionPostingChannel.class);
            postingChannel.setRequisition(destRequisition);
            postingChannel.setPostingChannel(sPostingChannel.getPostingChannel());
            postingChannel.setStartDate(sPostingChannel.getStartDate());
            postingChannel.setEndDate(sPostingChannel.getEndDate());
            destRequisition.getPostingChannels().add(postingChannel);
        }
        //questionnaires
        destRequisition.setQuestionnaires(new ArrayList<>());
        for (RequisitionQuestionnaire sQuestionnaire : sourceRequisition.getQuestionnaires()) {
            RequisitionQuestionnaire requisitionQuestionnaire = metadata.create(RequisitionQuestionnaire.class);
            requisitionQuestionnaire.setRequisition(destRequisition);
            requisitionQuestionnaire.setQuestionnaire(sQuestionnaire.getQuestionnaire());
            requisitionQuestionnaire.setWeight(sQuestionnaire.getWeight());
            destRequisition.getQuestionnaires().add(requisitionQuestionnaire);
        }
    }

    protected void editRequisition(Component component, Entity entity) {
        Map<String, Object> map = new HashMap<>();
        map.putAll(paramsMap);
        map.put("isRecruiter", isRecruiter());
//        RequisitionEdit requisitionEdit = (RequisitionEdit) openEditor(requisitionsTableEdit.getWindowId(), entity, WindowManager.OpenType.THIS_TAB, map);
        AbstractEditor<Requisition> requisitionEdit = openEditor(requisitionsTableEdit.getWindowId(), entity, WindowManager.OpenType.THIS_TAB, map);
        requisitionEdit.addCloseListener(actionId -> {
            requisitionsDs.refresh();
        });
    }

    protected void checkEndDateLessThanToday() {
        requisitionsTable.addRowStyleProvider(entity -> {
            try {
                Date sysDate = CommonUtils.getSystemDate();
                if (entity.getRequisitionStatus().equals(RequisitionStatus.OPEN) &&
                        (entity.getEndDate() != null && entity.getEndDate().before(sysDate))) {
                    return "premium-grade";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public void removeRequisition() {
        if (requisitionsDs.getItem().getJobRequests().size() == 0) {
            requisitionsDs.removeItem(requisitionsDs.getItem());
            requisitionsDs.getDsContext().commit();
        } else {
            showNotification(getMessage("cannotDelete"), NotificationType.WARNING);
        }
    }
}