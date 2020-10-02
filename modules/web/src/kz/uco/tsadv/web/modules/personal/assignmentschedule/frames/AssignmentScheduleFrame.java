package kz.uco.tsadv.web.modules.personal.assignmentschedule.frames;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.widgets.CubaCssActionsLayout;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Component;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.datasource.TimecardHierarchyDatasource;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.timesheet.enums.MaterialDesignColorsEnum;
import kz.uco.tsadv.modules.timesheet.model.AssignmentSchedule;
import kz.uco.tsadv.modules.timesheet.model.StandardOffset;
import kz.uco.tsadv.modules.timesheet.model.Timesheet;
import kz.uco.tsadv.service.DatesService;
import kz.uco.tsadv.service.TimesheetService;
import org.apache.commons.lang3.time.DateUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Calendar;
import java.util.*;
import java.util.function.Consumer;

public class AssignmentScheduleFrame extends AbstractFrame {

    protected static final MarginInfo MARGIN_INFO = new MarginInfo(true, false, true, false);
    protected static final Date END_OF_TIME = CommonUtils.getEndOfTime();
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected Metadata metadata;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected CommonService commonService;
    @Inject
    private TimecardHierarchyDatasource hierarchyElementsDs;
    @Inject
    private TimesheetService timesheetService;

    protected DateField<Date> month;
    protected CssLayout trashLayout;
    @Inject
    protected Table<Timesheet> tableLeft;
    @Inject
    protected Table<Timesheet> tableRight;
    @Inject
    private CheckBox enableInclusions;
    private Boolean enableInclusionsValue;

    private Map<StandardOffset, MaterialDesignColorsEnum> presentedOffsetsAndColors = new HashMap<>();
    private String styleHtmlWithFormattedString = "<div style=\"" +
            " background: %s;" +
            " padding: 3px;" +
            " border-radius: 5px;\n" +
            " border: 1px solid %s;" +
            " color:%s;" +
            " margin: 0 !important; \">%s</div>";
    @Inject
    private DatesService datesService;
    private Consumer consumer;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        presentedOffsetsAndColors = (Map<StandardOffset, MaterialDesignColorsEnum>) params.get("presentedOffsetsAndColors");
        consumer = (Consumer) params.get("consumer");
        enableInclusionsValue = (Boolean) params.get("enableInclusionsValue");
        if (enableInclusionsValue != null) {
            enableInclusions.setValue(enableInclusionsValue);
        }
        enableInclusions.addValueChangeListener(e -> consumer.accept(enableInclusions.getValue()));
        month = (DateField) getComponent("month");
        trashLayout = (CssLayout) getComponent("trashLayout");

        initColumns();
        initTrashHandler();

        showMonthColumns(datesService.getMonthBegin(month.getValue()));

        month.addValueChangeListener(e -> showMonthColumns(datesService.getMonthBegin(month.getValue())));
    }


    protected void initColumns() {
        tableLeft.addGeneratedColumn("setSchedule", entity -> {
            Timesheet timesheet = entity;
            LinkButton button = componentsFactory.createComponent(LinkButton.class);
            button.setCaption("");
            button.setIcon("icons/plus-btn.png");
            button.setDescription(messages.getMainMessage("setSchedule"));
            button.setAction(new BaseAction("setSchedule")
                    .withHandler(actionPerformedEvent -> {
                        Map<String, Object> params = new HashMap<>();
                        params.put("presentedOffsetsAndColors", presentedOffsetsAndColors);
                        params.put("organizationGroup", hierarchyElementsDs.getItem().getOrganizationGroup());
                        params.put("positionGroup", hierarchyElementsDs.getItem().getPositionGroup());
                        params.put("personGroup", hierarchyElementsDs.getItem().getPersonGroup());
                        AssignmentSchedule assignmentSchedule = metadata.create(AssignmentSchedule.class);
                        assignmentSchedule.setAssignmentGroup(timesheet.getAssignmentGroup());
                        Window.Editor editor = openEditor("tsadv$AssignmentSchedule.edit", assignmentSchedule, WindowManager.OpenType.DIALOG, params);
                        editor.addCloseWithCommitListener(() -> consumer.accept(enableInclusions.getValue()));
                    }));
            return button;

        });

        for (int i = 1; i <= 31; i++) {
            final int ii = i;

            tableRight.addGeneratedColumn("s" + i, entity -> {
                Timesheet timesheet = entity;
                CellData cellData = new CellData(timesheet, ii, getDateOfMonth(ii, datesService.getMonthBegin(month.getValue())));
                AssignmentSchedule assignmentSchedule = getCellValue(cellData);
                CssLayout cssLayout = componentsFactory.createComponent(CssLayout.class);
                Component dragAndDropComponent;
                cssLayout.setSizeFull();
                CubaCssActionsLayout unwrappedCssLayout = cssLayout.unwrap(CubaCssActionsLayout.class);

                if (assignmentSchedule != null) {
                    cssLayout.setDescription(assignmentSchedule.getName());
                    cssLayout.setSizeFull();
                    unwrappedCssLayout = cssLayout.unwrap(CubaCssActionsLayout.class);
                    Label label = new Label();
                    label.setSizeFull();
                    label.setContentMode(ContentMode.HTML);
                    String offsetName = assignmentSchedule.getName();
                    String shortOffsetName = offsetName.substring(0, Math.min(offsetName.length(), 3));
                    if (offsetName.length() > 3) {
                        shortOffsetName += "..";
                    }
                    String color = assignmentSchedule.getColorsSet().getColorHex();
                    String fontColor = assignmentSchedule.getColorsSet().getFontColor();
                    cellData = new CellData(timesheet, ii, getDateOfMonth(ii, datesService.getMonthBegin(month.getValue())));
                    label.setValue(String.format(styleHtmlWithFormattedString, color, color, fontColor, shortOffsetName));
                    label.setData(cellData);
                    dragAndDropComponent = label;
                } else {
                    VerticalLayout vLayout = new VerticalLayout();
                    vLayout.setSizeFull();
                    vLayout.setMargin(MARGIN_INFO);
                    vLayout.setData(cellData);
                    dragAndDropComponent = vLayout;
                }

                DragAndDropWrapper dragAndDropWrapper = new DragAndDropWrapper(dragAndDropComponent);
                dragAndDropWrapper.setData(cellData);
                if (timesheet != null)
                    dragAndDropWrapper.setDragStartMode(DragAndDropWrapper.DragStartMode.COMPONENT);

                dragAndDropWrapper.setDropHandler(new DropHandler() {
                    @Override
                    public void drop(DragAndDropEvent event) {
                        DragAndDropWrapper.WrapperTargetDetails dropData = ((DragAndDropWrapper.WrapperTargetDetails) event.getTargetDetails());

                        if (timesheet != null) {
                            if (event.getTransferable().getData("component") instanceof VerticalLayout) {
                                updateAssignmentSchedule((CellData) ((VerticalLayout) event.getTransferable().getData("component")).getData(), (CellData) ((DragAndDropWrapper) dropData.getTarget()).getData());
                            } else if (event.getTransferable().getData("component") instanceof Label) {
                                Label label = (Label) event.getTransferable().getData("component");
                                if (label.getData() instanceof StandardOffset) {
                                    createAssignmentSchedule((CellData) ((DragAndDropWrapper) dropData.getTarget()).getData(), (StandardOffset) label.getData());
                                } else if (label.getData() instanceof CellData) {
                                    AssignmentSchedule assignmentSchedule = getCellValue((CellData) label.getData());
                                    if (assignmentSchedule != null) {
                                        assignmentSchedule = getFullAssignmentSchedule(assignmentSchedule);
                                        StandardOffset offset = assignmentSchedule.getOffset();
                                        createAssignmentSchedule((CellData) ((DragAndDropWrapper) dropData.getTarget()).getData(), offset);
                                    }

                                }

                            }
                        }
                    }

                    @Override
                    public AcceptCriterion getAcceptCriterion() {
                        return AcceptAll.get();
                    }
                });
                dragAndDropWrapper.addStyleName("no-vertical-drag-hints");
                dragAndDropWrapper.addStyleName("no-horizontal-drag-hints");
                dragAndDropWrapper.setSizeFull();

                if (unwrappedCssLayout != null) {
                    unwrappedCssLayout.addComponent(dragAndDropWrapper);
                }
                return cssLayout;
            });
            tableRight.getColumn("s" + i).setCaption("" + i);
            tableRight.getColumn("s" + i).setAlignment(Table.ColumnAlignment.CENTER);
            tableRight.getColumn("s" + i).setWidth(60);
        }
    }


    private AssignmentSchedule getFullAssignmentSchedule(AssignmentSchedule assignmentSchedule) {
        return dataManager.reload(assignmentSchedule, "assignmentSchedule.view");
    }

    private StandardOffset getFullOffset(StandardOffset offset) {
        return dataManager.reload(offset, "standardOffset.view");
    }


    private AssignmentGroupExt getAssignmentGroup(UUID assignmentGroupId) {
        return commonService.getEntity(AssignmentGroupExt.class, assignmentGroupId);

    }

    protected void initTrashHandler() {
        com.vaadin.ui.CssLayout unwrappedTrashLayout = trashLayout.unwrap(com.vaadin.ui.CssLayout.class);
        DragAndDropWrapper trashDropWrapper = (DragAndDropWrapper) unwrappedTrashLayout.getComponent(0);

        trashDropWrapper.setDropHandler(new DropHandler() {
            @Override
            public void drop(DragAndDropEvent event) {
                DragAndDropWrapper.WrapperTargetDetails dropData = ((DragAndDropWrapper.WrapperTargetDetails) event.getTargetDetails());
                if (event.getTransferable().getData("component") instanceof Label) {
                    Label l = (Label) event.getTransferable().getData("component");
                    if (l.getData() instanceof CellData)
                        deleteAssignmentSchedule((CellData) l.getData());
                }
            }

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }
        });
    }

    protected void showMonthColumns(Date month) {
        if (month != null) {
            Date maxMonthDate = DateUtils.addDays(DateUtils.addMonths(month, 1), -1);
            Calendar c = Calendar.getInstance();
            c.setTime(maxMonthDate);
            int maxMonthDay = c.get(Calendar.DAY_OF_MONTH);

            for (int j = 1; j <= maxMonthDay; j++) {
                tableRight.getColumn("s" + j).setCollapsed(false);
                tableRight.getColumn("s" + j).setCaption(String.format("%02d", j));
            }

            for (int j = maxMonthDay + 1; j <= 31; j++) {
                tableRight.getColumn("s" + j).setCollapsed(true);
                tableRight.getColumn("s" + j).setCaption(String.format("%02d", j));
            }
        }
    }

    private void createAssignmentSchedule(CellData cellData, StandardOffset offset) {
        List<Entity> commitInstances = new ArrayList<>();
        Date newStartDate = cellData.getDate();

        AssignmentSchedule assignmentSchedule = metadata.create(AssignmentSchedule.class);
        assignmentSchedule.setAssignmentGroup(cellData.getTimesheet().getAssignmentGroup());
        assignmentSchedule.setStartDate(newStartDate);
        assignmentSchedule.setEndDate(END_OF_TIME);
        assignmentSchedule.setOffset(offset);

        MaterialDesignColorsEnum colorsSet = presentedOffsetsAndColors.get(offset);
        assignmentSchedule.setColorsSet(colorsSet);

        assignmentSchedule.setAssignmentGroup(cellData.getTimesheet().getAssignmentGroup());

        offset = getFullOffset(offset);
        assignmentSchedule.setSchedule(offset.getStandardSchedule());
        commitInstances.add(assignmentSchedule);

        dataManager.commit(new CommitContext(commitInstances));
        consumer.accept(enableInclusions.getValue());
    }

    private void updateAssignmentSchedule(CellData sourceCellData, CellData targetCellData) {

        if (sourceCellData.equals(targetCellData)) return;

        AssignmentSchedule sourceAssignmentSchedule = getCellValue(sourceCellData);
        AssignmentSchedule targetAssignmentSchedule = getCellValue(targetCellData);

        if (sourceAssignmentSchedule == null && targetAssignmentSchedule == null) return;
        if (sourceAssignmentSchedule != null
                && targetAssignmentSchedule != null
                && sourceAssignmentSchedule.getId().equals(targetAssignmentSchedule.getId())) return;

        List<Entity> commitInstances = new ArrayList<>();

        if (sourceAssignmentSchedule != null && targetAssignmentSchedule != null) {
            targetAssignmentSchedule.setStartDate(targetCellData.getDate());
            targetAssignmentSchedule.setEndDate(END_OF_TIME);
        }
        dataManager.commit(new CommitContext(commitInstances));
        consumer.accept(enableInclusions.getValue());
    }

    private void deleteAssignmentSchedule(CellData cellData) {
        AssignmentSchedule assignmentSchedule = getCellValue(cellData);
        if (assignmentSchedule == null) {
            return;
        }
        assignmentSchedule = commonService.getEntity(AssignmentSchedule.class, assignmentSchedule.getId());
        dataManager.remove(assignmentSchedule);
        consumer.accept(enableInclusions.getValue());
    }


    @Nullable
    protected static Date getDateOfMonth(int i, Date month) {
        Date dateOfMonth = DateUtils.addDays(month, i - 1);
        if (DateUtils.truncate(dateOfMonth, Calendar.MONTH).equals(month))
            return dateOfMonth;
        else
            return null;
    }

    @Nullable
    protected AssignmentSchedule getCellValue(CellData cellData) {
        if (cellData.getDate() == null) return null;
        return cellData.getTimesheet().getAssignmentSchedules()
                .stream()
                .filter(as -> !cellData.getDate().after(as.getEndDate())
                        && !cellData.getDate().before(as.getStartDate()))
                .findFirst().orElse(null);
    }

    public void setScheduleForAll() {
        HashMap<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("organizationGroup", hierarchyElementsDs.getItem().getOrganizationGroup());
        paramsMap.put("positionGroup", hierarchyElementsDs.getItem().getPositionGroup());
        paramsMap.put("personGroup", hierarchyElementsDs.getItem().getPersonGroup());
        paramsMap.put("startDate", datesService.getMonthBegin(month.getValue()));
        paramsMap.put("endDate", datesService.getMonthEnd(month.getValue()));
        paramsMap.put("loadFullData", true);
        paramsMap.put("enableInclusions", enableInclusionsValue);
        List<Timesheet> allTimesheets = timesheetService.getTimesheets(paramsMap);

        Map<String, Object> params = new HashMap<>();
        params.put("presentedOffsetsAndColors", presentedOffsetsAndColors);
        params.put("timesheets", allTimesheets);
        params.put("organizationGroup", hierarchyElementsDs.getItem().getOrganizationGroup());
        params.put("positionGroup", hierarchyElementsDs.getItem().getPositionGroup());
        params.put("personGroup", hierarchyElementsDs.getItem().getPersonGroup());
        AssignmentSchedule assignmentSchedule = metadata.create(AssignmentSchedule.class);
        Window.Editor editor = openEditor("tsadv$AssignmentSchedule.edit", assignmentSchedule, WindowManager.OpenType.DIALOG, params);
        editor.addCloseWithCommitListener(() -> consumer.accept(enableInclusions.getValue()));
    }

    protected static class CellData {
        private Timesheet timesheet;
        private int number;
        private Date date;

        public CellData(Timesheet timesheet, int number, Date date) {
            this.timesheet = timesheet;
            this.number = number;
            this.date = date;
        }

        public Timesheet getTimesheet() {
            return timesheet;
        }

        public void setTimesheet(Timesheet timesheet) {
            this.timesheet = timesheet;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

    }

}
