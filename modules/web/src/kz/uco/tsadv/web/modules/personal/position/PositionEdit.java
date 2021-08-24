package kz.uco.tsadv.web.modules.personal.position;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.screen.MapScreenOptions;
import com.haulmont.cuba.gui.screen.OpenMode;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.entity.abstraction.AbstractDictionary;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.config.PositionConfig;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.gui.components.AbstractHrEditor;
import kz.uco.tsadv.modules.hr.JobDescription;
import kz.uco.tsadv.modules.hr.JobDescriptionRequest;
import kz.uco.tsadv.modules.personal.dictionary.DicPayroll;
import kz.uco.tsadv.modules.personal.dictionary.DicPositionStatus;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.modules.timesheet.model.OrgAnalytics;
import kz.uco.tsadv.service.BusinessRuleService;
import kz.uco.tsadv.service.PositionService;
import kz.uco.tsadv.web.modules.personal.case_.Caseframe;
import kz.uco.tsadv.web.modules.personal.common.Utils;
import kz.uco.tsadv.web.modules.personal.jobgroup.JobGroupBrowse;
import kz.uco.tsadv.web.screens.jobdescriptionrequest.JobDescriptionRequestEdit;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class PositionEdit<T extends PositionExt> extends AbstractHrEditor<T> {
    @Inject
    protected CommonService commonService;
    @Inject
    protected PositionService positionService;
    @Inject
    protected BusinessRuleService businessRuleService;
    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    protected FieldGroup fieldGroup11;
    @Inject
    protected FieldGroup fieldGroup2;
    @Inject
    protected Metadata metadata;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected UserSession userSession;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected Datasource<PositionExt> positionDs;
    @Inject
    protected Datasource<PositionGroupExt> positionGroupDs;
    @Inject
    protected CollectionDatasource<VacationConditions, UUID> vacationConditionsDs;
    @Inject
    protected GroupDatasource<ParentElementsGoal, UUID> parentElementsGoalsDs;
    @Inject
    protected Datasource<GradeRuleValue> gradeGroupValueDs;
    @Inject
    protected CollectionDatasource<PositionGroupGoalLink, UUID> goalsDs;
    @Inject
    protected CollectionDatasource<SurCharge, UUID> surChargeDs;
    @Inject
    protected CollectionDatasource<HierarchyElementExt, UUID> hierarchyElementsDs;
    @Inject
    protected Datasource<OrgAnalytics> analyticsDs;
    @Named("fieldGroup2.positionFullNameLang1")
    protected TextField positionFullNameLang1;
    @Named("fieldGroup2.positionFullNameLang2")
    protected TextField positionFullNameLang2;
    @Named("fieldGroup2.positionFullNameLang3")
    protected TextField positionFullNameLang3;
    @Named("fieldGroup.baza")
    protected TextField bazaField;
    @Named("fieldGroup.costCenter")
    protected PickerField costCenterField;
    @Named("fieldGroup.extra")
    protected TextField extraField;
    //    @Inject
//    protected TabSheet jobReqTabSheet;
    @Inject
    protected TabSheet jobDescTabSheet;
    //    protected OrgAnalytics orgAnalyticsNew = null;
    @Inject
    protected TabSheet tabSheet;
    @Inject
    protected UserSessionSource userSessionSource;
    @Named("fieldGroup2.positionNameLang1")
    protected TextField positionNameLang1Field;
    @Named("fieldGroup2.positionFullNameLang2")
    protected TextField positionFullNameLang2Field;
    @Named("fieldGroup2.positionFullNameLang3")
    protected TextField positionFullNameLang3Field;
    @Named("fieldGroup2.positionFullNameLang4")
    protected TextField positionFullNameLang4Field;
    @Named("fieldGroup2.positionFullNameLang5")
    protected TextField positionFullNameLang5Field;
    @Named("fieldGroup2.positionNameLang2")
    protected TextField positionNameLang2Field;
    @Named("fieldGroup2.positionNameLang3")
    protected TextField positionNameLang3Field;
    @Named("fieldGroup2.positionNameLang4")
    protected TextField positionNameLang4Field;
    @Named("fieldGroup2.positionNameLang5")
    protected TextField positionNameLang5Field;
    @Named("addFieldGroup1.gradeGroup")
    protected PickerField gradeGroupField;
    @Named("addFieldGroup1.gradeRule")
    protected PickerField gradeRuleField;
    @Named("fieldGroup.fte")
    protected TextField fteField;
    @Named("fieldGroup.maxPersons")
    protected TextField maxPersonsField;
    @Named("analytics.calendar")
    protected LookupField calendarField;
    @Named("analytics.offset")
    protected LookupField offsetField;
    @Named("analytics.workingCondition")
    protected LookupField workingConditionField;
    @Named("schedulesTable.create")
    protected CreateAction schedulesTableCreate;
    @Named("hierarchyElementsTable.create")
    protected CreateAction hierarchyElementsTableCreate;
    @Named("hierarchyElementsTable.edit")
    protected EditAction hierarchyElementsTableEdit;
    @Named("competencePosTable.create")
    protected CreateAction competencePosTableCreate;
    //    @Named("flySurChargeTable.create")
//    protected CreateAction flySurChargeTableCreate;
    @Inject
    protected Caseframe caseFrame;
    //    @Named("flightTimeRateTable.create")
//    protected CreateAction flightTimeRateTableCreate;
    @Named("goalsTable.create")
    protected CreateAction goalsTableCreate;
    @Named("goalsTable.edit")
    protected EditAction goalsTableEdit;
    //    protected int languageIndex = languageIndex();
    protected boolean isNewItem = false;
    protected List<Entity> commitInstances = new ArrayList<>();
    @Named("hierarchyElementsTable.close")
    protected Action hierarchyElementsTableClose;
    @Named("hierarchyElementsTable.reassignElement")
    protected Action hierarchyElementsTableReassignElement;
    @Inject
    protected ScreenBuilders screenBuilders;
    @Named("harmFulConditionsTable.create")
    protected CreateAction harmFulConditionsTableCreate;
    @Named("harmFulConditionsTable.edit")
    protected EditAction harmFulConditionsTableEdit;
    @Inject
    protected TextArea<String> positionDutiesTextArea;
    @Inject
    protected TextArea<String> basicInteractionsAtWorkTextArea;
    @Inject
    protected FileUploadField jobDescriptionFile;
    @Inject
    protected TextArea<String> compulsoryQualificationRequirementsTextArea;
    @Inject
    protected TextArea<String> generalAdditionalRequirementsTextArea;
    protected boolean jobDescriptionChanged = false;
    protected JobDescription jobDescription = null;
    @Inject
    protected Screens screens;

    @Override
    protected FieldGroup getStartEndDateFieldGroup() {
        return fieldGroup;
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        Table surChargeTable = (Table) getComponentNN("surChargeTable");
        ((CreateAction) surChargeTable.getActionNN("create")).setWindowId("tsadv$SurChargePosition.edit");
        ((EditAction) surChargeTable.getActionNN("edit")).setWindowId("tsadv$SurChargePosition.edit");
        positionDs.addItemChangeListener(e -> {
            if (e.getDs().getItem() != null && e.getDs().getItem().getGroup() != null) {
                if (e.getDs().getItem().getGroup().getJobDescription() != null) {
                    jobDescription = dataManager.reload(e.getDs().getItem().getGroup().getJobDescription(),
                            "jobDescription-for-position-edit");
                    positionDutiesTextArea.setRequired(true);
                } else {
                    jobDescription = metadata.create(JobDescription.class);
                    jobDescription.setPositionGroup(e.getDs().getItem().getGroup());
                    positionDutiesTextArea.setRequired(false);
                }
                positionDutiesTextArea.setValue(jobDescription.getPositionDuties());
                basicInteractionsAtWorkTextArea.setValue(jobDescription.getBasicInteractionsAtWork());
                compulsoryQualificationRequirementsTextArea.setValue(jobDescription.getCompulsoryQualificationRequirements());
                generalAdditionalRequirementsTextArea.setValue(jobDescription.getGeneralAdditionalRequirements());
                if (jobDescription.getFile() != null) {
                    jobDescriptionFile.setValue(jobDescription.getFile());
                }
                positionDutiesTextArea.addTextChangeListener(textChangeEvent -> {
                    jobDescription.setPositionDuties(textChangeEvent.getText());
                    positionDutiesTextArea.setRequired(true);
                    jobDescriptionChanged = true;
                });
                basicInteractionsAtWorkTextArea.addTextChangeListener(textChangeEvent -> {
                    jobDescription.setBasicInteractionsAtWork(textChangeEvent.getText());
                    jobDescriptionChanged = true;
                });
                compulsoryQualificationRequirementsTextArea.addTextChangeListener(textChangeEvent -> {
                    jobDescription.setCompulsoryQualificationRequirements(textChangeEvent.getText());
                    jobDescriptionChanged = true;
                });
                generalAdditionalRequirementsTextArea.addTextChangeListener(textChangeEvent -> {
                    jobDescription.setGeneralAdditionalRequirements(textChangeEvent.getText());
                    jobDescriptionChanged = true;
                });
                jobDescriptionFile.addValueChangeListener(fileDescriptorValueChangeEvent -> {
                    jobDescription.setFile(fileDescriptorValueChangeEvent.getValue());
                    jobDescriptionChanged = true;
                });
            }
        });


        tabSheet.addSelectedTabChangeListener(event -> {
            if (PersistenceHelper.isNew(getItem())) {
                if (!"tab1".equals(event.getSelectedTab().getName())) {
                    if (getDsContext().isModified()) {
                        showOptionDialog(messages.getMainMessage("closeUnsaved.caption"),
                                messages.getMessage("kz.uco.tsadv.web.modules.personal.organization", "closeText"),
                                MessageType.WARNING,
                                new Action[]{
                                        new DialogAction(DialogAction.Type.OK, Action.Status.PRIMARY)
                                                .withCaption(messages.getMainMessage("closeUnsaved.save"))
                                                .withHandler(event1 -> {
                                            commit();
                                        }),
                                        new DialogAction(DialogAction.Type.CANCEL)
                                                .withCaption(messages.getMessage("kz.uco.tsadv.web.modules.personal.organization", "closeFormButtonText"))
                                                .withHandler(event1 -> {
                                            Utils.resetDsContext(getDsContext());
                                            close(CLOSE_ACTION_ID);
                                        })
                                });
                    } else {
                        showOptionDialog(messages.getMainMessage("closeUnsaved.caption"),
                                messages.getMessage("kz.uco.tsadv.web.modules.personal.organization", "questionIsNotFillForm"),
                                MessageType.WARNING,
                                new Action[]{
                                        new DialogAction(DialogAction.Type.OK, Action.Status.PRIMARY)
                                                .withCaption(messages.getMessage("kz.uco.tsadv.web.modules.personal.organization", "fillForm"))
                                                .withHandler(event1 -> {
                                            tabSheet.setSelectedTab("tab1");
                                        }),
                                        new DialogAction(DialogAction.Type.CANCEL)
                                                .withCaption(messages.getMessage("kz.uco.tsadv.web.modules.personal.organization", "closeFormButtonText"))
                                                .withHandler(event1 -> {
                                            close(CLOSE_ACTION_ID);
                                        })
                                });
                    }
                }
            }
        });

//        jobReqTabSheet.addSelectedTabChangeListener(event -> {
//            selectLangTab(jobDescTabSheet, jobReqTabSheet.getSelectedTab().getName().replaceAll("[^0-9]", ""), true);
//        });
//        jobDescTabSheet.addSelectedTabChangeListener(event -> {
//            selectLangTab(jobReqTabSheet, jobDescTabSheet.getSelectedTab().getName().replaceAll("[^0-9]", ""), false);
//        });

        positionDs.addItemPropertyChangeListener((e) -> {
            if (e.getProperty().equals("gradeGroup") || e.getProperty().equals("gradeRule")) {
                refreshGradeRuleValue();
            }
        });

        schedulesTableCreate.setInitialValuesSupplier(() ->
                ParamsMap.of("positionGroup", positionGroupDs.getItem()));

        getDsContext().addBeforeCommitListener(context -> context.addInstanceToCommit(analyticsDs.getItem()));

        competencePosTableCreate.setInitialValuesSupplier(() -> ParamsMap.of("positionGroup", getItem().getGroup()));
        goalsTableCreate.setInitialValuesSupplier(() -> ParamsMap.of("positionGroup", getItem().getGroup()));
        goalsTableCreate.setWindowParams(ParamsMap.of("goalsDs", goalsDs));
        goalsTableEdit.setWindowParams(ParamsMap.of("goalsDs", goalsDs));
//        flightTimeRateTableCreate.setInitialValuesSupplier(() -> ParamsMap.of("positionGroupName", getItem().getGroup()));
//        flySurChargeTableCreate.setInitialValuesSupplier(() -> ParamsMap.of("positionGroupId", getItem().getGroup()));


    }

    protected void selectLangTab(TabSheet tabSheet, String num, boolean lang) {
        tabSheet.setSelectedTab((lang ? "descLang" : "reqLang") + num);
    }

    @Override
    protected void initNewItem(PositionExt item) {
        isNewItem = true;
        item.setStartDate(CommonUtils.getSystemDate());
        item.setEndDate(CommonUtils.getEndOfTime());
        PositionGroupExt positionGroupExt = metadata.create(PositionGroupExt.class);
        OrgAnalytics analytics = metadata.create(OrgAnalytics.class);
        positionGroupExt.setAnalytics(analytics);
        item.setGroup(positionGroupExt);
        item.setWriteHistory(false);
        DicPositionStatus positionStatus = commonService.getEntity(DicPositionStatus.class, "ACTIVE");
        if (positionStatus != null) {
            item.setPositionStatus(positionStatus);
        }
        setPayroll(item);
    }

    @Override
    protected void postInit() {
        super.postInit();
//        fillParentElementsGoalsDs(); //TODO временное решение, исправить как будет время. Записи в tsadv_position_structure дублируются для позиции
        fieldGroup11.removeField("jobGroup");
        createJobGroupField();
        if (positionDs.getItem().getOrganizationGroupExt() != null) {
            fieldGroup.getFieldNN("organizationGroupExt").setEditable(false);
        }

        if (positionDs.getItem().getGradeGroup() != null && positionDs.getItem().getGradeRule() != null) {
            refreshGradeRuleValue();
        }
        harmFulConditionsTableCreate.setInitialValues(ParamsMap.of("positionGroup", getEditedEntity().getGroup()));
    }

    @Override
    public void ready() {
        ((CreateAction) ((Table) caseFrame.getComponentNN("casesTable")).getActionNN("create")).setInitialValuesSupplier(() -> ParamsMap.of("positionGroup", getItem().getGroup()));
        super.ready();
        for (TabSheet.Tab tab : tabSheet.getTabs()) {
            if (!tab.getName().equalsIgnoreCase("tab1")) {
                tab.setVisible(!isNewItem);
            }
        }
        Field[] fieldsHaveToBeListened = new Field[]{
                bazaField,
                costCenterField,
                extraField,
                positionNameLang1Field,
                positionNameLang2Field,
                positionNameLang3Field};
        for (Field field : fieldsHaveToBeListened) {
            field.addValueChangeListener(e -> {
                initFieldComponentHandler(fieldGroup, "positionNameLang1", "positionNameLang2", "positionNameLang3", "baza", "costCenter", "extra");
            });
        }
        if (gradeGroupField.getValue() != null) {
            gradeRuleField.getLookupAction().setLookupScreenParams(ParamsMap.of("gradeGroup", gradeGroupField.getValue()));
        }
        gradeGroupField.addValueChangeListener(e -> {
            if (gradeGroupField.getValue() != null)
                gradeRuleField.getLookupAction().setLookupScreenParams(ParamsMap.of("gradeGroup", gradeGroupField.getValue()));
        });
        fteField.addValueChangeListener(e -> {
            if (fteField.getValue() != null) {
                Double d = (Double) fteField.getValue();
                Integer val = d.intValue();
                maxPersonsField.setValue(val);
            }
        });
        createAnalyticsIfNotExist();
        setEnabledHierarchyElementCloseReassign(Objects.nonNull(hierarchyElementsDs.getItem()));
        hierarchyElementsDs.addItemChangeListener(e -> setEnabledHierarchyElementCloseReassign(Objects.nonNull(hierarchyElementsDs.getItem())));
        hierarchyElementsTableCreate.setWindowParams(ParamsMap.of("openedForCreateFromPositionEdit", "",
                "positionGroup", getItem().getGroup()));
        hierarchyElementsTableEdit.setWindowParams(ParamsMap.of("openedForEdit", ""));

        hierarchyElementsTableCreate.setAfterWindowClosedHandler((window, closeActionId) -> hierarchyElementsDs.refresh());
        hierarchyElementsTableEdit.setAfterWindowClosedHandler((window, closeActionId) -> hierarchyElementsDs.refresh());

        hierarchyElementsDs.addCollectionChangeListener(e -> hierarchyElementsTableCreate.setEnabled(hierarchyElementsDs.getItems().isEmpty()));
        hierarchyElementsTableCreate.setEnabled(hierarchyElementsDs.getItems().isEmpty());

        getComponentNN("editButton").setEnabled(false);
    }

    protected void setEnabledHierarchyElementCloseReassign(boolean enable) {
        hierarchyElementsTableClose.setEnabled(enable);
        hierarchyElementsTableReassignElement.setEnabled(enable);
    }

    @Override
    protected boolean preCommit() {
        List<Job> existingJobsInactiveInNearFuture = positionService.getExistingJobsInactiveInNearFuture(getItem());
        if (!existingJobsInactiveInNearFuture.isEmpty()) {
            showNotification(getMessage("Attention"), businessRuleService.getBusinessRuleMessage("positionEditJobGroupEdit"), NotificationType.TRAY);
            getItem().setEndDate(existingJobsInactiveInNearFuture.get(0).getEndDate());
        }
        return super.preCommit();
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        setWorkTomeForChildrenPositions(getItem().getGroup());
        if (!commitInstances.isEmpty()) {
            dataManager.commit(new CommitContext(commitInstances));
        }
        return super.postCommit(committed, close);
    }

    protected void createAnalyticsIfNotExist() {
        if (getItem().getGroup().getAnalytics() == null) {
            OrgAnalytics analytics = metadata.create(OrgAnalytics.class);
            getItem().getGroup().setAnalytics(analytics);
            getDsContext().addBeforeCommitListener(context -> context.addInstanceToCommit(analytics));
        }
    }

    public void redirectCard(AssignmentExt assignment, String name) {
        openEditor("person-card", assignment.getPersonGroup(), WindowManager.OpenType.THIS_TAB);
    }

    public Component generateUserImageCell(AssignmentExt entity) {
        return Utils.getPersonImageEmbedded(entity.getPersonGroup().getPerson(), "50px", null);
    }

    public void createVacationConditions() {
        VacationConditions vacationConditions = metadata.create(VacationConditions.class);
        vacationConditions.setPositionGroup((getItem().getGroup()));
        openEditor(vacationConditions, WindowManager.OpenType.THIS_TAB)
                .addCloseListener(actionId -> vacationConditionsDs.refresh());
    }

    protected void openJobGroupBrowse(Map<String, Object> params) {
        JobGroupBrowse jobGroupBrowse = (JobGroupBrowse) openLookup("tsadv$JobGroup.browse", items -> {
            for (Object o : items) {
                getItem().setJobGroup((JobGroup) o);
            }
        }, WindowManager.OpenType.THIS_TAB, params);

//        jobGroupBrowse.addAfterCloseListener(actionId -> positionDs.refresh());
//        jobGroupBrowse.addCloseListener(actionId -> positionDs.refresh());
    }

    public Component generateParentName(HierarchyElementExt element) {
        Label label = (Label) componentsFactory.createComponent(Label.NAME);
        if (element.getParent() != null) {
            HierarchyElementExt parentElement = commonService.getEntity(HierarchyElementExt.class, "select e from base$HierarchyElementExt e where e.id = :parentId", Collections.singletonMap("parentId", element.getParent().getId()), "hierarchyElement.parent");
            label.setValue(parentElement != null ? parentElement.getName() : "");
        }
        return label;
    }

//    public void onCreate(Component source) {
//        PositionGroupGoalLink positionGroupGoalLink = metadata.create(PositionGroupGoalLink.class);
//        positionGroupGoalLink.setPositionGroup(getItem().getGroup());
//        PositionGroupGoalLinkEdit positionGroupGoalLinkEdit = (PositionGroupGoalLinkEdit) openEditor(positionGroupGoalLink, WindowManager.OpenType.THIS_TAB,null,goalsDs);
//        positionGroupGoalLinkEdit.addAfterCloseListener(a -> goalsDs.refresh());
//    }

//    public void onEdit(Component source) {
//        openEditor(goalsDs.getItem(), WindowManager.OpenType.THIS_TAB, null, goalsDs);
//    }




    /*public void fillParentElementsGoalsDs() {
        Map<Integer, Object> params = new HashMap<>();
        params.put(1, CommonUtils.getSystemDate());
        if (positionDs.getItem().getGroup() == null) return;
        params.put(2, positionDs.getItem().getGroup().getId());
        params.put(3, userSession.getUser().getLanguage());
        List<Object[]> resultList = commonService.emNativeQueryResultList("WITH org AS (SELECT " +
                "               ps4.organization_group_id, " +
                "               ps4.parent_position_group_id, " +
                "               ps4.lvl " +
                "             FROM tsadv_position_structure ps4 " +
                "             WHERE ps4.element_type = 2 " +
                "                   AND  ?1 BETWEEN ps4.start_date AND ps4.end_date " +
                "                   AND  ?1 BETWEEN ps4.pos_start_date AND ps4.pos_end_date " +
                "                   AND ps4.delete_ts IS NULL " +
                "                   AND ps4.position_group_id = " +
                "                        ?2) " +
                "SELECT * " +
                "FROM ( " +
                "       SELECT " +
                "         ps.element_type          element_type, " +
                "         CASE WHEN ps.element_type = 1 " +
//                "           THEN o.organization_name " +
                "             THEN " +
                "  CASE WHEN ?3 = 'ru' THEN o.organization_name_lang1 " +
                "  WHEN ?3 = 'kz' THEN o.organization_name_lang2 " +
                "  WHEN ?3 = 'en' THEN o.organization_name_lang3 " +
                "       ELSE '' END " +
                "         ELSE p.position_name_lang" + languageIndex + " END element_name, " +  //columnChanged TODO
                "         CASE WHEN ps.element_type = 1 " +
                "           THEN og.goal_name " +
                "         ELSE pg.goal_name END    goal_name, " +
                "         coalesce(CASE WHEN ps.element_type = 1 " +
                "           THEN ogl.weight " +
                "         ELSE pgl.weight END, 0)      weight, " +
                "         ps.lvl " +
                "       FROM tsadv_position_structure ps " +
                "         LEFT JOIN BASE_ORGANIZATION o " +
                "           ON o.group_id = ps.organization_group_id " +
                "              AND  ?1 BETWEEN o.start_date AND o.end_date " +
                "              AND o.delete_ts IS NULL " +
                "         LEFT JOIN tsadv_organization_group_goal_link ogl " +
                "           ON ogl.organization_group_id = o.group_id " +
                "              AND ogl.delete_ts IS NULL " +
                "         LEFT JOIN tsadv_goal og " +
                "           ON og.id = ogl.goal_id " +
                "              AND og.delete_ts IS NULL " +
                "         LEFT JOIN BASE_POSITION p " +
                "           ON p.group_id = ps.position_group_id " +
                "              AND  ?1 BETWEEN p.start_date AND p.end_date " +
                "              AND p.delete_ts IS NULL " +
                "         LEFT JOIN tsadv_position_group_goal_link pgl " +
                "           ON pgl.position_group_id = p.group_id " +
                "              AND pgl.delete_ts IS NULL " +
                "         LEFT JOIN tsadv_goal pg " +
                "           ON pg.id = pgl.goal_id " +
                "              AND pg.delete_ts IS NULL " +
                "         JOIN org " +
                "           ON 1 = 1 " +
                "       WHERE ((ps.element_type = 1 AND " +
                "                ?1 BETWEEN ps.start_date AND ps.end_date AND " +
                "               (SELECT ps1.organization_group_path " +
                "                FROM tsadv_position_structure ps1 " +
                "                WHERE ps1.element_type = 1 " +
                "                      AND  ?1 BETWEEN ps1.start_date AND ps1.end_date " +
                "                      AND ps1.delete_ts IS NULL " +
                "                      AND ps1.organization_group_id = org.organization_group_id) LIKE " +
                "               '%' || ps.organization_group_id || '%' " +
                "              ) OR " +
                "              (ps.element_type = 2 AND " +
                "                ?1 BETWEEN ps.start_date AND ps.end_date AND " +
                "                ?1 BETWEEN ps.pos_start_date AND ps.pos_end_date AND " +
                "               (SELECT ps3.position_group_path " +
                "                FROM tsadv_position_structure ps3 " +
                "                WHERE ps3.element_type = 2 " +
                "                      AND  ?1 BETWEEN ps3.start_date AND ps3.end_date " +
                "                      AND  ?1 BETWEEN ps3.pos_start_date AND ps3.pos_end_date " +
                "                      AND ps3.delete_ts IS NULL " +
                "                      AND ps3.position_group_id = org.parent_position_group_id) LIKE " +
                "               '%' || ps.position_group_id || '%' AND " +
                "               pg.id IS NOT NULL AND " +
                "               ps.organization_group_id = org.organization_group_id)) AND " +
                "             ps.delete_ts IS NULL " +
                "       UNION ALL " +
                "       SELECT " +
                "         3, " +
                "         j.job_name_lang" + languageIndex + "    element_name, " +   //columnChanged TODO
                "         jg.goal_name  goal_name, " +
                "         coalesce(jgl.weight, 0)    weight, " +
                "         org.lvl - 0.5 lvl " +
                "       FROM tsadv_job j " +
                "         LEFT JOIN tsadv_job_group_goal_link jgl " +
                "           ON jgl.job_group_id = j.group_id " +
                "              AND jgl.delete_ts IS NULL " +
                "         LEFT JOIN tsadv_goal jg " +
                "           ON jg.id = jgl.goal_id " +
                "              AND jg.delete_ts IS NULL " +
                "         JOIN org " +
                "           ON 1 = 1 " +
                "       WHERE  ?1 BETWEEN j.start_date AND j.end_date " +
                "             AND j.delete_ts IS NULL " +
                "             AND j.group_id = (SELECT p1.job_group_id " +
                "                               FROM BASE_POSITION p1 " +
                "                               WHERE p1.group_id =  ?2 " +
                "                                     AND  ?1 BETWEEN p1.start_date AND p1.end_date " +
                "                                     AND p1.delete_ts IS NULL) " +
                "     ) foo " +
                "ORDER BY lvl desc", params);
        for (Object[] row : resultList) {
            ParentElementsGoal entity = new ParentElementsGoal();
            entity.setElementType(ElementTypeForGoals.fromId((Integer) row[0]));
            entity.setElementName((String) row[1]);
            entity.setGoalName((String) row[2]);
            entity.setGoalWeight((Integer) row[3]);
            parentElementsGoalsDs.includeItem(entity);
        }
    }*/

    protected void initFieldComponentHandler(FieldGroup fieldGroup, String... fields) {
        if (fields != null) {

            List<HasValue> componentsForLang1 = new LinkedList<>();
            List<HasValue> componentsForLang2 = new LinkedList<>();
            List<HasValue> componentsForLang3 = new LinkedList<>();

            for (String field : fields) {
                if (!field.equals("positionNameLang2") && !field.equals("positionNameLang3")) {
                    HasValue hasValue = getFieldComponent(fieldGroup, field, HasValue.class);
                    if (hasValue != null) {
                        componentsForLang1.add(hasValue);
                    }
                }
            }

            for (String field : fields) {
                if (!field.equals("positionNameLang1") && !field.equals("positionNameLang3")) {
                    HasValue hasValue = getFieldComponent(fieldGroup, field, HasValue.class);
                    if (hasValue != null) {
                        componentsForLang2.add(hasValue);
                    }
                }
            }

            for (String field : fields) {
                if (!field.equals("positionNameLang1") && !field.equals("positionNameLang2")) {
                    HasValue hasValue = getFieldComponent(fieldGroup, field, HasValue.class);
                    if (hasValue != null) {
                        componentsForLang3.add(hasValue);
                    }
                }
            }

            setPositionFullNameForLangWithValueChangeListeners(componentsForLang1, positionFullNameLang1);
            setPositionFullNameForLangWithValueChangeListeners(componentsForLang2, positionFullNameLang2);
            setPositionFullNameForLangWithValueChangeListeners(componentsForLang3, positionFullNameLang3);
        }
    }

    protected void setPositionFullNameForLangWithValueChangeListeners(List<HasValue> components, TextField positionFullNameLang) {
        components.forEach(hasValue -> {
            String positionNameValue = positionName(components.toArray(new HasValue[]{}));
            positionFullNameLang.setValue(positionNameValue);
            hasValue.addValueChangeListener(e -> {
                String positionNameValue1 = positionName(components.toArray(new HasValue[]{}));
                positionFullNameLang.setValue(positionNameValue1);
            });
        });
    }

    /*protected void initFieldComponentHandler(FieldGroup fieldGroup, String... fields) {
        if (fields != null) {

            List<HasValue> components = new LinkedList<>();
            for (String field : fields) {
                HasValue hasValue = getFieldComponent(fieldGroup, field, HasValue.class);
                if (hasValue != null) {
                    components.add(hasValue);
                }
            }

            components.forEach(new Consumer<HasValue>() {
                @Override
                public void accept(HasValue hasValue) {
                    hasValue.addValueChangeListener(new ValueChangeListener() {
                        @Override
                        public void valueChanged(ValueChangeEvent e) {
                            String positionNameValue = positionName(components.toArray(new HasValue[]{}));
                            positionFullNameLang1.setValue(positionNameValue);
                        }
                    });
                }
            });
        }
    }*/

    protected <T> T getFieldComponent(FieldGroup fieldGroup, String field, Class<T> type) {
        FieldGroup.FieldConfig fieldConfig = fieldGroup.getField(field);
        if (fieldConfig != null) {
            Component component = fieldConfig.getComponent();
            if (component != null) {
                return type.cast(component);
            }
        }
        return null;
    }

    protected String positionName(HasValue... fields) {
        String resultString = null;
        if (fields != null) {
            StringBuilder result = new StringBuilder();
            for (HasValue<String> field : fields) {
                Object fieldValue = field.getValue();

                String textFieldValue;
                if (fieldValue instanceof AbstractDictionary) {
                    textFieldValue = ((AbstractDictionary) fieldValue).getLangValue();
                } else {
                    textFieldValue = field.getValue();
                }

                if (textFieldValue != null) {
                    result.append(".").append(textFieldValue);
                    resultString = result.substring(1, result.length());
                }
            }
            return resultString;
//            return result.toString().trim();
        }
        return null;
    }

    /*protected int languageIndex() {
        String language = ((UserSessionSource) AppBeans.get(UserSessionSource.NAME)).getLocale().getLanguage();
        String langOrder = AppContext.getProperty("base.abstractDictionary.langOrder");

        if (langOrder != null) {
            List<String> langs = Arrays.asList(langOrder.split(";"));
            int count = 1;
            for (String lang : langs) {
                if (language.equals(lang)) {
                    return count;
                }
                count++;
            }
        }
        return 1;
    }*/

    protected void setPayroll(PositionExt item) {
        Configuration configuration = AppBeans.get(Configuration.NAME);
        PositionConfig config = configuration.getConfig(PositionConfig.class);
        if (!config.getFillPayrollOnPosition().equals("")) {
            UUID payrollId = UUID.fromString(config.getFillPayrollOnPosition());
            DicPayroll payroll = commonService.getEntity(DicPayroll.class, payrollId);
            if (payroll != null) {
                item.setPayroll(payroll);
            }
        }
    }


    protected void refreshGradeRuleValue() {
        Map<String, Object> paramsMap = new HashMap<>();
        if (positionDs.getItem() != null) {
            paramsMap.put("gradeGroupId", positionDs.getItem().getGradeGroup() == null ? null : positionDs.getItem().getGradeGroup().getId());
            paramsMap.put("gradeRuleId", positionDs.getItem().getGradeRule() == null ? null : positionDs.getItem().getGradeRule().getId());
            paramsMap.put("systemDate", CommonUtils.getSystemDate());

            List<GradeRuleValue> grv = commonService.getEntities(GradeRuleValue.class,
                    "select e " +
                            "    from tsadv$GradeRuleValue e " +
                            "   where e.gradeGroup.id = :gradeGroupId " +
                            "     and e.gradeRule.id = :gradeRuleId " +
                            "     and :systemDate between e.startDate and e.endDate"
                    , paramsMap
                    , "gradeRuleValue.edit");

            if (!grv.isEmpty())
                gradeGroupValueDs.setItem(grv.get(0));
            else
                gradeGroupValueDs.setItem(null);
        } else
            gradeGroupValueDs.setItem(null);
    }

    protected void setWorkTomeForChildrenPositions(PositionGroupExt positionGroup) {
        OrgAnalytics currentAnalytics = positionGroupDs.getItem().getAnalytics();
        //commitInstances.add(currentAnalytics);
        String queryString = "SELECT distinct e.positionGroup FROM base$HierarchyElementExt e\n" +
                "             WHERE e.parent = (SELECT h FROM base$HierarchyElementExt h\n" +
                "                               WHERE h.positionGroup.id = :positionGroupId" +
                "                                 and :systemDate between h.startDate and h.endDate)" +
                "               and :systemDate between e.startDate and e.endDate";
        Map<String, Object> params = new HashMap<>();
        params.put("positionGroupId", positionGroup.getId());
        params.put("systemDate", CommonUtils.getSystemDate());
        List<PositionGroupExt> positionGroupExtList = commonService.getEntities(PositionGroupExt.class, queryString, params, "position.analytic.update");
        for (PositionGroupExt childPositionGroup : positionGroupExtList) {
            if (childPositionGroup != null && childPositionGroup.getAnalytics() != null) {
                setValuesForExistingAnalytics(childPositionGroup, currentAnalytics);
            } else if (currentAnalytics != null) {
                createNewAnalitycsForChildPosition(childPositionGroup, currentAnalytics);
            }
            setAnalyticsForAssignments(childPositionGroup, currentAnalytics);
            setWorkTomeForChildrenPositions(childPositionGroup);
        }
    }

    protected void setValuesForExistingAnalytics(PositionGroupExt positionGroup, OrgAnalytics currentAnalytics) {
        OrgAnalytics childPositionAnalytics = positionGroup.getAnalytics();
        childPositionAnalytics.setCalendar(currentAnalytics.getCalendar());
        childPositionAnalytics.setOffset(currentAnalytics.getOffset());
        childPositionAnalytics.setWorkingCondition(currentAnalytics.getWorkingCondition());
        saveAnalytics(null, childPositionAnalytics);
    }

    protected void createNewAnalitycsForChildPosition(PositionGroupExt positionGroup, OrgAnalytics currentAnalytics) {
        OrgAnalytics analitycsNew = metadata.create(OrgAnalytics.class);
        analitycsNew.setCalendar(currentAnalytics.getCalendar());
        analitycsNew.setOffset(currentAnalytics.getOffset());
        analitycsNew.setWorkingCondition(currentAnalytics.getWorkingCondition());
        analitycsNew.setPositionGroup(positionGroup);
        positionGroup.setAnalytics(analitycsNew);
        saveAnalytics(positionGroup, analitycsNew);
    }

    protected void saveAnalytics(Entity relatedEntity, OrgAnalytics analytics) {
        if (commitInstances.size() > 200) {
            dataManager.commit(new CommitContext(commitInstances));
            commitInstances.clear();
        } else {
            if (relatedEntity != null && !commitInstances.contains(relatedEntity)) {
                commitInstances.add(relatedEntity);
            }
            if (!commitInstances.contains(analytics)) {
                commitInstances.add(analytics);
            }
        }
    }

    protected void setAnalyticsForAssignments(PositionGroupExt positionGroup, OrgAnalytics currentAnalytics) {
        LoadContext<AssignmentGroupExt> assignmentGroupExtLoadContext = LoadContext.create(AssignmentGroupExt.class);
        assignmentGroupExtLoadContext.setQuery(
                LoadContext.createQuery(
                        "select e.group from base$AssignmentExt e " +
                                " where e.positionGroup.id = :posGroupId" +
                                "  and :systemDate between e.startDate and e.endDate")
                        .setParameter("posGroupId", positionGroup.getId())
                        .setParameter("systemDate", CommonUtils.getSystemDate()))
                .setView("assignment.analytic.update");
        List<AssignmentGroupExt> assignmentGroupExtList = dataManager.loadList(assignmentGroupExtLoadContext);
        OrgAnalytics finalOriginalAnalytics = currentAnalytics;
        assignmentGroupExtList.forEach(assignmentGroupExt -> {
            if (assignmentGroupExt.getAnalytics() != null) {
                OrgAnalytics analyticsAssignmentBetween = assignmentGroupExt.getAnalytics();
                analyticsAssignmentBetween.setOffset(finalOriginalAnalytics.getOffset());
                analyticsAssignmentBetween.setCalendar(finalOriginalAnalytics.getCalendar());
                analyticsAssignmentBetween.setWorkingCondition(finalOriginalAnalytics.getWorkingCondition());
                saveAnalytics(null, analyticsAssignmentBetween);
            } else {
                OrgAnalytics analyticsAssignmentBetweenNew = metadata.create(OrgAnalytics.class);
                analyticsAssignmentBetweenNew.setCalendar(finalOriginalAnalytics.getCalendar());
                analyticsAssignmentBetweenNew.setOffset(finalOriginalAnalytics.getOffset());
                analyticsAssignmentBetweenNew.setWorkingCondition(finalOriginalAnalytics.getWorkingCondition());
                analyticsAssignmentBetweenNew.setAssignmentGroup(assignmentGroupExt);
                assignmentGroupExt.setAnalytics(analyticsAssignmentBetweenNew);
                saveAnalytics(assignmentGroupExt, analyticsAssignmentBetweenNew);
            }
        });
    }

    public void windowCommit() {
        if (jobDescriptionChanged) {
            positionDutiesTextArea.setRequired(true);
        } else {
            getWindow().remove(getComponent("positionDutiesTextArea"));
        }
        if (isNewItem) {
            if (validateAll()) {
                commit();
                isNewItem = false;
                positionDs.refresh();
                hierarchyElementsTableCreate.setWindowParams(ParamsMap.of("openedForCreateFromPositionEdit", "",
                        "positionGroup", getItem().getGroup()));
                for (TabSheet.Tab tab : tabSheet.getTabs()) {
                    if (!tab.getName().equalsIgnoreCase("tab1")) {
                        tab.setVisible(!isNewItem);
                    }
                }
            }
        } else {
            commitAndClose();
        }
    }

    protected void createJobGroupField() {
        FieldGroup.FieldConfig jobGroupConfig = fieldGroup11.createField("jobGroup");
        PickerField jobGroupPickerField = componentsFactory.createComponent(PickerField.class);
        //jobGroupPickerField.setDatasource(positionDs, jobGroupConfig.getProperty());
        jobGroupPickerField.setDatasource(positionDs, "jobGroup");
        if (positionDs.getItem().getJobGroup() != null) {
            jobGroupPickerField.setEditable(false);
        } else {
            jobGroupPickerField.addAction(new AbstractAction("openJobGroup") {
                @Override
                public void actionPerform(Component component) {
                    openJobGroupBrowse(Collections.singletonMap("endDatePosition", getItem().getEndDate()));
                }

                @Override
                public String getIcon() {
                    return "font-icon:ELLIPSIS_H";
                }
            });
            jobGroupPickerField.addClearAction();
        }
        jobGroupPickerField.setCaptionMode(CaptionMode.PROPERTY);
        jobGroupPickerField.setCaptionProperty("jobNameDefault");
        //
        jobGroupPickerField.setCaption("Должность");
        jobGroupConfig.setComponent(jobGroupPickerField);
        fieldGroup11.addField(jobGroupConfig);
        fieldGroup11.getFieldNN("jobGroup").setWidth("300px");
    }

    public void close() {
        openEditorHierarchyElement(hierarchyElementsDs.getItem(), WindowManager.OpenType.DIALOG, ParamsMap.of("close", Boolean.TRUE));
    }

    public void reassignElement() {
        openEditorHierarchyElement(hierarchyElementsDs.getItem(), WindowManager.OpenType.DIALOG, ParamsMap.of("reassignElement", ""));
    }

    protected AbstractEditor<HierarchyElementExt> openEditorHierarchyElement(HierarchyElementExt item, WindowManager.OpenType openType, Map<String, Object> params) {
        AbstractEditor<HierarchyElementExt> hierarchyElementAbstractEditor = openEditor(item, openType, params);
        hierarchyElementAbstractEditor.addCloseListener(actionId -> hierarchyElementsDs.refresh());
        return hierarchyElementAbstractEditor;
    }

    public void jobDescriptionSave() {
        if (jobDescriptionChanged) {
            if (positionDutiesTextArea.getValue() == null) {
                showNotification(getMessage("inputPositionDuties"), NotificationType.TRAY);
                return;
            } else {
                dataManager.commit(jobDescription);
            }
        }
        close("commit", true);
    }

    public void jobDescriptionCancel() {
        close("cancel");
    }

    public void createRequest() {
        JobDescriptionRequest jobDescriptionRequest = metadata.create(JobDescriptionRequest.class);
        jobDescriptionRequest.setPositionGroup(getItem().getGroup());
        if (jobDescription != null) {
            jobDescriptionRequest.setBasicInteractionsAtWork(jobDescription.getBasicInteractionsAtWork());
            jobDescriptionRequest.setCompulsoryQualificationRequirements(jobDescription.getCompulsoryQualificationRequirements());
            jobDescriptionRequest.setGeneralAdditionalRequirements(jobDescription.getGeneralAdditionalRequirements());
            jobDescriptionRequest.setPositionDuties(jobDescription.getPositionDuties());
        }

        JobDescriptionRequestEdit jobDescriptionRequestEdit = screens.create(JobDescriptionRequestEdit.class, OpenMode.THIS_TAB
                , new MapScreenOptions(ParamsMap.of("from", "base$Position.edit")));
        jobDescriptionRequestEdit.setEntityToEdit(jobDescriptionRequest);
        jobDescriptionRequestEdit.show();
    }
}