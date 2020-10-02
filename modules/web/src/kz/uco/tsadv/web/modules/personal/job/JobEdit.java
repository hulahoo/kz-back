package kz.uco.tsadv.web.modules.personal.job;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.gui.components.AbstractHrEditor;
import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personal.model.Job;
import kz.uco.tsadv.modules.personal.model.JobGroupGoalLink;
import kz.uco.tsadv.modules.personal.model.PositionExt;
import kz.uco.tsadv.modules.personal.model.VacationConditions;
import kz.uco.tsadv.service.JobService;
import kz.uco.tsadv.web.modules.performance.jobgroupgoallink.JobGroupGoalLinkEdit;
import kz.uco.tsadv.web.modules.personal.common.Utils;
import kz.uco.tsadv.web.modules.personal.vacationconditions.VacationConditionsEdit;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.*;

public class JobEdit extends AbstractHrEditor<Job> {

    @Inject
    protected MetadataTools metadataTools;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected Metadata metadata;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected CommonService commonService;
    @Inject
    protected JobService jobService;
    @Inject
    protected FieldGroup fieldGroup, fieldGroup2;
    @Inject
    protected TabSheet tabSheet, tabSheetReqLang, tabSheetDescLang;
    @Named("fieldGroup.endDate")
    protected Field endDateField;
    @Named("goalsTable.edit")
    protected Action goalsTableEdit;
    @Named("jobProtectionEquipmentTable.create")
    protected CreateAction jobProtectionEquipmentTableCreate;
    @Inject
    protected Datasource<Job> jobDs;
    @Inject
    protected CollectionDatasource<VacationConditions, UUID> vacationConditionsDs;
    @Inject
    protected CollectionDatasource<JobGroupGoalLink, UUID> goalsDs;

    @Nullable
    protected Job oldJob;
    protected boolean positionsExists = false;
    protected boolean activePositionChange = true;

    @Override
    protected FieldGroup getStartEndDateFieldGroup() {
        return fieldGroup;
    }

    @Override
    protected void initNewItem(Job item) {
        super.initNewItem(item);
        item.setStartDate(CommonUtils.getSystemDate());
        item.setEndDate(CommonUtils.getEndOfTime());
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        goalsTableEdit.setEnabled(false);
        goalsDs.addItemChangeListener(e -> goalsTableEdit.setEnabled(e.getItem() != null));

        Utils.customizeLookup(fieldGroup.getFieldNN("jobCategory").getComponent(), null, WindowManager.OpenType.DIALOG, null);

        tabSheet.addSelectedTabChangeListener(event -> {
            if (PersistenceHelper.isNew(getItem())) {
                if (!"tab".equals(event.getSelectedTab().getName())) {
                    if (getDsContext().isModified()) {
                        showOptionDialog(messages.getMainMessage("closeUnsaved.caption"),
                                messages.getMessage("kz.uco.tsadv.web.modules.personal.organization", "closeText"),
                                MessageType.WARNING,
                                new Action[]{
                                        new DialogAction(DialogAction.Type.OK, Action.Status.PRIMARY)
                                                .withCaption(messages.getMainMessage("closeUnsaved.save"))
                                                .withHandler(event1 -> {
                                            activePositionChange = true;
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
                                                .withHandler(event1 -> tabSheet.setSelectedTab("tab")),
                                        new DialogAction(DialogAction.Type.CANCEL)
                                                .withCaption(messages.getMessage("kz.uco.tsadv.web.modules.personal.organization", "closeFormButtonText"))
                                                .withHandler(event1 -> close(CLOSE_ACTION_ID))
                                });
                    }
                }
            }
        });

        tabSheetReqLang.addSelectedTabChangeListener(event -> {
            selectLangTab(tabSheetDescLang, tabSheetReqLang.getSelectedTab().getName().replaceAll("[^0-9]", ""), true);
        });

        tabSheetDescLang.addSelectedTabChangeListener(event -> {
            selectLangTab(tabSheetReqLang, tabSheetDescLang.getSelectedTab().getName().replaceAll("[^0-9]", ""), false);
        });

        endDateField.addValueChangeListener(e -> {
            Date currentDate = CommonUtils.getSystemDate();
            if (endDateField != null && ((Date) endDateField.getValue()).before(currentDate)) {
                if (jobService.checkForExistingPositionsInSameDate(jobDs.getItem().getGroup())) {
                    positionsExists = true;
                }
            }
        });
    }

    protected void selectLangTab(TabSheet tabSheet, String num, boolean lang) {
        if (lang) {
            tabSheet.setSelectedTab("descLang" + num);
        } else {
            tabSheet.setSelectedTab("reqLang" + num);
        }
    }

    @Override
    protected void postInit() {
        super.postInit();
        initUploader();

        if (!PersistenceHelper.isNew(getItem()))
            oldJob = metadataTools.copy(getItem());

        jobProtectionEquipmentTableCreate.setInitialValuesSupplier(() ->
                ParamsMap.of("jobGroup", jobDs.getItem().getGroup()));
    }

    @Override
    public void ready() {
        super.ready();

        windowCommitHistory.setAction(new BaseAction("windowCommitHistoryNew") {
            @Override
            public void actionPerform(Component component) {
                if (getItem() != null) {
                    getItem().setWriteHistory(Boolean.TRUE);
                    if (isJobNameChanged()) {
                        showOptionDialog("",
                                getMessage("PositionNameChangeMessage"),
                                MessageType.WARNING,
                                new Action[]{
                                        new DialogAction(DialogAction.Type.YES, Status.PRIMARY)
                                                .withHandler(event1 -> {
                                            activePositionChange = true;
                                            commitAndClose();
                                        }),
                                        new DialogAction(DialogAction.Type.NO, Status.PRIMARY)
                                                .withHandler(event1 -> {
                                            activePositionChange = false;
                                            commitAndClose();
                                        }),
                                        new DialogAction(DialogAction.Type.CANCEL)
                                                .withHandler(event1 -> {
                                            Utils.resetDsContext(getDsContext());
                                            close(CLOSE_ACTION_ID);
                                        })
                                });
                    } else {
                        activePositionChange = false;
                        commitAndClose();
                    }
                }
            }
        });

        getDsContext().addBeforeCommitListener(this::beforeCommitListener);
    }

    protected void beforeCommitListener(CommitContext context) {
        if (!PersistenceHelper.isNew(getItem()) && activePositionChange) {
            replaceEditedItem();
            changePositions(context);
        }
    }

    protected void replaceEditedItem() {
        Set<Job> set = new HashSet<>();
        set.add(getItem());
        if (getItem().getGroup() != null)
            set.addAll(getItem().getGroup().getList());
        getItem().getGroup().setList(new ArrayList<>(set));
    }

    protected boolean isJobNameChanged() {
        return oldJob != null
                && !Objects.equals(oldJob.getJobNameLang1(), getItem().getJobNameLang1())
                && !Objects.equals(oldJob.getJobNameLang2(), getItem().getJobNameLang2())
                && !Objects.equals(oldJob.getJobNameLang3(), getItem().getJobNameLang3())
                && !Objects.equals(oldJob.getJobNameLang4(), getItem().getJobNameLang4())
                && !Objects.equals(oldJob.getJobNameLang5(), getItem().getJobNameLang5());
    }


    @Override
    protected boolean preCommit() {
        if (positionsExists) {
            //showNotification(getMessage("Attention"), businessRuleService.getBusinessRuleMessage("jobGroupEndDateEdit"), NotificationType.TRAY);
            return false;
        } else {
            return super.preCommit();
        }
    }


    protected void initUploader() {
        HBoxLayout hBoxLayout = componentsFactory.createComponent(HBoxLayout.class);
        hBoxLayout.setSpacing(true);

        TextField fileName = componentsFactory.createComponent(TextField.class);
        fileName.setWidth("250px");
        fileName.setEditable(false);
        fileName.setValue(PersistenceHelper.isNew(jobDs.getItem()) ? "" : jobDs.getItem().getInstructionName());
        hBoxLayout.add(fileName);

        FileUploadField fileUploadField = componentsFactory.createComponent(FileUploadField.class);
        fileUploadField.setFileSizeLimit(1024000);
        fileUploadField.setUploadButtonCaption("");
        fileUploadField.setClearButtonCaption("");
        fileUploadField.setUploadButtonIcon("icons/reports-template-upload.png");
        fileUploadField.setClearButtonIcon("icons/item-remove.png");
        fileUploadField.setShowClearButton(true);
        fileUploadField.addFileUploadSucceedListener(event -> {
            Job job = jobDs.getItem();
            if (job != null && fileUploadField.getFileContent() != null) {
                try {
                    job.setInstruction(IOUtils.toByteArray(fileUploadField.getFileContent()));
                    job.setInstructionName(fileUploadField.getFileName());
                    fileName.setValue(fileUploadField.getFileName());

                } catch (IOException e) {
                    showNotification(getMessage("fileUploadErrorMessage"), NotificationType.ERROR);
                }
            }
            fileUploadField.setValue(null);
        });

        fileUploadField.addBeforeValueClearListener(beforeEvent -> {
            beforeEvent.preventClearAction();
            showOptionDialog("", "Are you sure you want to delete this file?", MessageType.CONFIRMATION,
                    new Action[]{
                            new DialogAction(DialogAction.Type.YES, Action.Status.PRIMARY) {
                                public void actionPerform(Component component) {
                                    jobDs.getItem().setInstruction(null);
                                    jobDs.getItem().setInstructionName(null);
                                    fileName.setValue("");
                                }
                            },
                            new DialogAction(DialogAction.Type.NO, Action.Status.NORMAL)
                    });
        });


        hBoxLayout.add(fileUploadField);

        fieldGroup.getFieldNN("instruction").setWidth("500px");
        fieldGroup.getFieldNN("instruction").setComponent(hBoxLayout);
    }

    public void createVacationConditions() {
        VacationConditions vacationConditions = metadata.create(VacationConditions.class);
        vacationConditions.setJobGroup(jobDs.getItem().getGroup());
        VacationConditionsEdit vacationConditionsEdit = (VacationConditionsEdit) openEditor(vacationConditions, WindowManager.OpenType.THIS_TAB);
        vacationConditionsEdit.addCloseListener(closeId -> vacationConditionsDs.refresh());
    }

    public void onGoalCreate(Component source) {
        JobGroupGoalLink jobGroupGoalLink = metadata.create(JobGroupGoalLink.class);
        jobGroupGoalLink.setJobGroup(getItem().getGroup());
        JobGroupGoalLinkEdit jobGroupGoalLinkEdit = (JobGroupGoalLinkEdit) openEditor(jobGroupGoalLink, WindowManager.OpenType.THIS_TAB, null, goalsDs);
        jobGroupGoalLinkEdit.addCloseListener(a -> goalsDs.refresh());
    }

    public void onGoalEdit(Component source) {
        openEditor(goalsDs.getItem(), WindowManager.OpenType.THIS_TAB, null, goalsDs);
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        for (int langIndex = 1; langIndex < 4; langIndex++) {                       //jobNameLang%d only 1 2 3
            if (getJobNameCount(langIndex)) {
                errors.add(getMessage("JobNamLang1Validate" + langIndex));
                break;
            }
        }
    }

    protected boolean getJobNameCount(int langIndex) {
        String jobName = ((Field<String>) fieldGroup2.getComponentNN("jobNameLangId" + langIndex)).getValue();
        if (oldJob == null || jobName != null && !jobName.equals(oldJob.getValue("jobNameLang" + langIndex))) {
            Map<String, Object> params = new HashMap<>();
            params.put("itemId", getItem().getId());
            params.put("jobName", jobName);
            Long count = commonService.getCount(JobGroup.class, "select e from tsadv$JobGroup e\n" +
                    "                    join tsadv$Job j on j.group.id = e.id\n" +
                    "                    where j.jobNameLang" + langIndex + " = :jobName" +
                    "                    and j.id <> :itemId", params);
            if (count > 0) {
                fieldGroup2.getComponentNN("jobNameLangId" + langIndex).requestFocus();
                return true;
            }
        }
        return false;
    }

    protected void changePositions(CommitContext context) {
        commonService.getEntities(PositionExt.class,
                "select e from base$PositionExt e where e.jobGroup.id = :jobGroupId",
                ParamsMap.of("jobGroupId", getItem().getGroup().getId()),
                "position.forJobEdit")
                .stream()
                .map(this::changePosition)
                .forEach(context::addInstanceToCommit);
    }

    protected PositionExt changePosition(PositionExt existingPosition) {
        existingPosition.setJobGroup(getItem().getGroup());
        existingPosition.setPositionNames(1, 2, 3);
        existingPosition.setPositionFullNames(1, 2, 3);
        existingPosition.setWriteHistory(Boolean.TRUE.equals(getItem().getWriteHistory())
                && existingPosition.getEndDate().after(CommonUtils.getSystemDate())
                && existingPosition.getStartDate().before(CommonUtils.getSystemDate()));
        return existingPosition;
    }
}