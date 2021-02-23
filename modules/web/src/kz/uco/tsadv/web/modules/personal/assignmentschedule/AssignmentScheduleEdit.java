package kz.uco.tsadv.web.modules.personal.assignmentschedule;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import kz.uco.base.entity.shared.AssignmentGroup;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.OrganizationExt;
import kz.uco.tsadv.modules.timesheet.config.TimecardConfig;
import kz.uco.tsadv.modules.timesheet.enums.MaterialDesignColorsEnum;
import kz.uco.tsadv.modules.timesheet.model.AssignmentSchedule;
import kz.uco.tsadv.modules.timesheet.model.StandardOffset;
import kz.uco.tsadv.modules.timesheet.model.Timesheet;
import kz.uco.tsadv.web.modules.personal.common.Utils;

import javax.inject.Inject;
import javax.inject.Named;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

//AssignmentSchedule has listener
public class AssignmentScheduleEdit extends AbstractEditor<AssignmentSchedule> {

    @Inject
    private TimecardConfig timecardConfig;
    @Inject
    private CommonService commonService;

    @Named("fieldGroup.offset")
    protected PickerField<Entity> offsetField;

    @Named("fieldGroup.startDate")
    protected DateField<Date> startDateField;

    @Named("fieldGroup.endDate")
    protected DateField<Date> endDateField;

    @Named("fieldGroup.colorsSet")
    private LookupField<Object> colorsField;

    private Date startDateValue;
    private Date endDateValue;
    private StandardOffset offset;
    private Map<StandardOffset, MaterialDesignColorsEnum> presentedOffsetsAndColors = new HashMap<>();
    private boolean flag;
    @Inject
    private DataManager dataManager;
    @Inject
    private Metadata metadata;
    private List<Timesheet> timesheets;
    protected OrganizationGroupExt organizationGroup;
    protected PositionGroupExt positionGroup;
    protected PersonGroupExt personGroup;
    @Named("fieldGroup.assignmentGroup")
    protected PickerField<AssignmentGroupExt> assignmentGroupField;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.containsKey("presentedOffsetsAndColors")) {
            presentedOffsetsAndColors = (HashMap<StandardOffset, MaterialDesignColorsEnum>) params.get("presentedOffsetsAndColors");
        }
        timesheets = (List<Timesheet>) params.get("timesheets");
        colorsField.setOptionIconProvider(c -> "images/colors/" + ((MaterialDesignColorsEnum) c).name() + ".png");

        if (timecardConfig.getAllowableSchedulesForPositionOn()) {
            if (params.containsKey("organizationGroup")) {
                organizationGroup = (OrganizationGroupExt) params.get("organizationGroup");
                positionGroup = (PositionGroupExt) params.get("positionGroup");
                personGroup = (PersonGroupExt) params.get("personGroup");
            }
        }
        assignmentGroupField.removeAction(PickerField.LookupAction.NAME);
        assignmentGroupField.removeAction(PickerField.ClearAction.NAME);
        PickerField.LookupAction lookupAction = new PickerField.LookupAction(assignmentGroupField) {
            public AssignmentGroupExt transformValueFromLookupWindow(Entity valueFromLookupWindow) {
                return ((AssignmentExt) valueFromLookupWindow).getGroup();
            }
        };
        assignmentGroupField.addAction(lookupAction);
        assignmentGroupField.addClearAction();
        lookupAction.setLookupScreen("base$AssignmentGroup.browse");
        lookupAction.setLookupScreenOpenType(WindowManager.OpenType.THIS_TAB);
    }

    @Override
    public void ready() {
        super.ready();
        overrideCommitAction();
    }

    protected void overrideCommitAction() {
        Button windowCommitButton = (Button) getComponents().stream().filter(component -> Objects.equals(component.getId(), "windowCommit")).findFirst().get();
        windowCommitButton.setAction(new BaseAction("windowCommit") {
            @Override
            public String getCaption() {
                return getMessage("OK");
            }

            @Override
            public void actionPerform(Component component) {
                if (!PersistenceHelper.isNew(getItem()) && isCanDeleteAssignmentSchedule(getItem())) {
                    showOptionDialog(getMessage("Attention"),
                            getMessage("deleteAssignmentSchedule"),
                            MessageType.WARNING,
                            new Action[]{
                                    new DialogAction(DialogAction.Type.YES, Status.PRIMARY) {
                                        @Override
                                        public void actionPerform(Component component) {
                                            commitAndClose();
                                        }
                                    },
                                    new DialogAction(DialogAction.Type.NO, Status.NORMAL) {
                                        public void actionPerform(Component component) {
                                        }
                                    }
                            });
                } else {
                    commitAndClose();
                }
            }
        });
    }

    protected boolean isCanDeleteAssignmentSchedule(AssignmentSchedule assignmentSchedule) {
        return assignmentSchedule.getStartDate() != null && assignmentSchedule.getStartDate().before(assignmentSchedule.getEndDate()) ?
                !commonService.getEntities(AssignmentSchedule.class,
                        " select e from tsadv$AssignmentSchedule e " +
                                " where e.assignmentGroup.id = :assignmentGroupId" +
                                "       and  e.endDate <= :endDate and e.startDate >= :startDate" +
                                "       and e.id <> :id ",
                        ParamsMap.of("assignmentGroupId", assignmentSchedule.getAssignmentGroup().getId(),
                                "startDate", assignmentSchedule.getStartDate(),
                                "endDate", assignmentSchedule.getEndDate(),
                                "id", assignmentSchedule.getId()), View.MINIMAL).isEmpty()
                : !commonService.getEntities(AssignmentSchedule.class,
                " select e from tsadv$AssignmentSchedule e " +
                        " where e.assignmentGroup.id = :assignmentGroupId" +
                        "       and not (e.endDate < :startDate or e.startDate > :endDate) " +
                        "       and e.id <> :id ",
                ParamsMap.of("assignmentGroupId", assignmentSchedule.getAssignmentGroup().getId(),
                        "startDate", assignmentSchedule.getEndDate(),
                        "endDate", assignmentSchedule.getStartDate(),
                        "id", assignmentSchedule.getId()), View.MINIMAL).isEmpty();
    }

    @Override
    public boolean close(String actionId) {
        return super.close(actionId, true);
    }

    @Override
    public void postInit() {
        if (startDateField.getValue() == null) {
            startDateValue = CommonUtils.getSystemDate();
            startDateField.setValue(startDateValue);
        } else {
            startDateValue = startDateField.getValue();
        }
        try {
            if (endDateField.getValue() == null) {
                endDateValue = new SimpleDateFormat("dd.MM.yyyy").parse("31.12.9999");
                endDateField.setValue(endDateValue);
            } else {
                endDateValue = endDateField.getValue();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        customizeOffsetsList();
        Set<Integer> colorIds = new HashSet<>();
        if (!presentedOffsetsAndColors.keySet().isEmpty()) {
            colorIds = presentedOffsetsAndColors.values().stream().map(MaterialDesignColorsEnum::getId).collect(Collectors.toSet());
        }
        colorsField.setValue(MaterialDesignColorsEnum.fromId(getRandomColorId(colorIds)));

        offsetField.addValueChangeListener(e -> {
            if (flag) {
                return;
            }
            offset = (StandardOffset) offsetField.getValue();
            if (offset != null) {
                getItem().setSchedule(offset.getStandardSchedule());
                MaterialDesignColorsEnum mandatoryColor = presentedOffsetsAndColors.get(offset);
                if (mandatoryColor != null) {
                    colorsField.setValue(mandatoryColor);
                    colorsField.setEditable(false);
                    showNotification(messages.getMainMessage("AssignmentSchedule.offset.color.alreadyChosen"), NotificationType.TRAY);
                } else {
                    colorsField.setEditable(true);
                }
            }
        });

        startDateField.addValueChangeListener(e -> {
            startDateValue = startDateField.getValue();
            customizeOffsetsList();
        });
        endDateField.addValueChangeListener(e -> {
            endDateValue = endDateField.getValue();
            customizeOffsetsList();
        });
    }

    @Override
    protected boolean preCommit() {
        flag = true;
        if (getItem().getAssignmentGroup() == null) {
            /* That means that table already has offsets to that month! */
            if (!presentedOffsetsAndColors.keySet().isEmpty()) {
                showSetToPositionDialog(startDateValue, endDateValue, offset, timesheets);
            } else if (timesheets != null && !timesheets.isEmpty()) {
                createAssignmentSchedulesForAssignments(timesheets, startDateValue, endDateValue, offset);
            } else {
                close("windowClose", true);
            }
            return false;
        }
        return super.preCommit();
    }

    private void showSetToPositionDialog(Date startDate, Date endDate, StandardOffset offset, Collection<Timesheet> timesheets) {
        showOptionDialog(messages.getMainMessage("AssignmentScheduleFrame.dialog.caption"),
                messages.getMainMessage("AssignmentScheduleFrame.dialog.message"),
                MessageType.CONFIRMATION,
                new Action[]{
                        new BaseAction("rewrite")
                                .withCaption(messages.getMainMessage("AssignmentScheduleFrame.dialog.rewrite"))
                                .withHandler(actionPerformedEvent -> {
                            createAssignmentSchedulesForAssignments(timesheets, startDate, endDate, offset);
                        }),
                        new BaseAction("emptyOnly")
                                .withCaption(messages.getMainMessage("AssignmentScheduleFrame.dialog.emptyOnly"))
                                .withHandler(actionPerformedEvent -> {
                            createAssignmentSchedulesForEmptySpaceOnly(timesheets, startDate, endDate, offset);
                        }),
                        new DialogAction(DialogAction.Type.CANCEL)
                }
        );
    }

    private void createAssignmentSchedulesForEmptySpaceOnly(Collection<Timesheet> timesheets, Date startDate, Date endDate, StandardOffset offset) {
        List<Timesheet> timesheetsWithoutAssignmentSchedules = timesheets.stream().filter(e -> e.getAssignmentSchedules().isEmpty()).collect(Collectors.toList());
        createAssignmentSchedulesForAssignments(timesheetsWithoutAssignmentSchedules, startDate, endDate, offset);
    }

    private void createAssignmentSchedulesForAssignments(Collection<Timesheet> timesheets, Date startDate, Date endDate, StandardOffset offset) {
        List<Entity> commitInstances = new ArrayList<>();
        for (Timesheet timesheet : timesheets) {
            AssignmentSchedule assignmentSchedule = prepareNewAssignmentSchedule(timesheet.getAssignmentGroup(), startDate, endDate, offset);
            commitInstances.add(assignmentSchedule);
        }
        dataManager.commit(new CommitContext(commitInstances));
        close(COMMIT_ACTION_ID, true);
    }

    private AssignmentSchedule prepareNewAssignmentSchedule(AssignmentGroupExt assignmentGroup, Date startDate, Date endDate, StandardOffset offset) {
        AssignmentSchedule assignmentSchedule = metadata.create(AssignmentSchedule.class);
        assignmentSchedule.setAssignmentGroup(assignmentGroup);
        assignmentSchedule.setStartDate(startDate);
        assignmentSchedule.setEndDate(endDate);
        assignmentSchedule.setOffset(offset);
        assignmentSchedule.setColorsSet((MaterialDesignColorsEnum) colorsField.getValue());
        assignmentSchedule.setSchedule(offset.getStandardSchedule());
        return assignmentSchedule;
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        if (offset != null && startDateValue.before(offset.getStartDate())) {
            errors.add(getMessage("AssignmentSchedule.offset.dates.fields.error"));
        }
    }

    private void customizeOffsetsList() {
        Map<String, Object> params = new HashMap<>();
        params.put("startDateValue", startDateValue);
        params.put("endDateValue", endDateValue);
        if (timecardConfig.getAllowableSchedulesForPositionOn()) {
            params.put("organizationGroup", organizationGroup);
            params.put("positionGroup", positionGroup);
            params.put("personGroup", personGroup);
        }
        Utils.customizeLookup(offsetField, "tsadv$StandardOffset.browse", WindowManager.OpenType.DIALOG, params);
    }

    private int getRandomColorId(Set<Integer> excludeRows) {
        int max = MaterialDesignColorsEnum.values().length;
        int min = 1;
        Random rand = new Random();
        int range = max - min + 1;
        int random = rand.nextInt(range) + 1;

        if (excludeRows.size() < max) {
            while (excludeRows.contains(random)) {
                random = rand.nextInt(range) + 1;
            }
        }
        return random;
    }

}