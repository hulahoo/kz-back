package kz.uco.tsadv.web.modules.personal.positiongroup;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.chile.core.datatypes.Datatypes;
import com.haulmont.chile.core.datatypes.impl.DoubleDatatype;
import com.haulmont.chile.core.datatypes.impl.IntegerDatatype;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.filter.Op;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.screen.OpenMode;
import kz.uco.base.entity.dictionary.DicLocation;
import kz.uco.base.entity.shared.Hierarchy;
import kz.uco.base.service.common.CommonService;
import kz.uco.base.web.components.CustomFilter;
import kz.uco.tsadv.modules.personal.dictionary.DicPayroll;
import kz.uco.tsadv.modules.personal.dictionary.DicPositionStatus;
import kz.uco.tsadv.modules.personal.group.GradeGroup;
import kz.uco.tsadv.modules.personal.group.JobGroup;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.GradeRule;
import kz.uco.tsadv.modules.personal.model.HierarchyElementExt;
import kz.uco.tsadv.modules.personal.model.PositionExt;
import kz.uco.tsadv.modules.timesheet.model.OrgAnalytics;
import kz.uco.tsadv.web.modules.filterconfig.FilterConfig;
import kz.uco.tsadv.web.modules.personal.position.PositionEdit;

import javax.inject.Inject;
import java.util.*;

public class PositionGroupBrowse extends AbstractLookup {

    @Inject
    protected GroupDatasource<PositionGroupExt, UUID> positionGroupsDs;

    @Inject
    protected CollectionDatasource<PositionExt, UUID> listDs;
    @Inject
    protected Metadata metadata;
    @Inject
    private DataManager dataManager;
    @Inject
    protected CollectionDatasource<GradeGroup, UUID> gradeGroupsDs;
    @Inject
    protected CollectionDatasource<GradeRule, UUID> gradeRulesDs;
    @Inject
    protected CollectionDatasource<JobGroup, UUID> jobGroupsDs;
    @Inject
    protected CollectionDatasource<DicLocation, UUID> locationsDs;
    @Inject
    protected CollectionDatasource<DicPayroll, UUID> payrollsDs;
    @Inject
    protected CollectionDatasource<DicPositionStatus, UUID> positionStatusesDs;
    @Inject
    protected CollectionDatasource<OrganizationGroupExt, UUID> organizationGroupsDs;
    @Inject
    protected VBoxLayout filterBox;
    @Inject
    protected Button editButton;
    protected Map<String, CustomFilter.Element> filterMap;
    protected CustomFilter customFilter;
    @Inject
    protected GroupTable<PositionGroupExt> positionGroupsTable;
    @Inject
    protected FilterConfig filterConfig;
    @Inject
    protected GroupBoxLayout groupBox;
    @Inject
    protected Filter positionGroupsFilter;
    @Inject
    private CommonService commonService;
    @WindowParam
    protected Object openedFromHierarchyElementEdit;
    @WindowParam
    protected Object selectedHierarchy;
    @WindowParam
    protected Object openedFromAssignmentHistoryEdit;
    @Inject
    protected ScreenBuilders screenBuilders;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (params.get("posOrgJob") != null && (boolean) params.get("posOrgJob")) {
            positionGroupsDs.setQuery("select e " +
                    "                           from base$PositionGroupExt e " +
                    "                           join e.list p " +
                    "                          where :session$systemDate between p.startDate and p.endDate " +
                    "                            and ( p.jobGroup.id = COALESCE(:param$jobGroupId,p.jobGroup.id)) " +
                    "                            and ( p.organizationGroupExt.id = COALESCE(:param$orgGroupId,p.organizationGroupExt.id) ) " +
                    "                          order by e.updateTs desc");
        } else if (params.containsKey("fte")) {
            positionGroupsDs.setQuery("select distinct e " +
                    "                           from base$PositionGroupExt e " +
                    "                           join e.list p " +
                    (params.containsKey("statusCode") ? " join p.positionStatus status on status.code = :param$statusCode " : " ") +
                    "                          where :session$systemDate between p.startDate and p.endDate " +
                    String.format(" and p.fte > %s ", params.get("fte")) +
                    "                          order by e.updateTs, e.id desc");
        }
        if (filterConfig.getPositionEnableCustomFilter()) {
            initFilterMap();
            customFilter = CustomFilter.init(positionGroupsDs, positionGroupsDs.getQuery(), filterMap);
            filterBox.add(customFilter.getFilterComponent());
            applyFilter();
        } else {
            groupBox.setVisible(false);
        }
        positionGroupsFilter.setVisible(filterConfig.getPositionEnableCubaFilter());

        if (openedFromHierarchyElementEdit != null) {
            if (selectedHierarchy != null) {
                positionGroupsDs.setQuery("SELECT DISTINCT e FROM base$PositionGroupExt e            " +
                        "                  JOIN e.list p                                    " +
                        "                  WHERE :session$systemDate BETWEEN p.startDate and p.endDate " +
                        "                  AND e.id NOT IN (SELECT he.positionGroup.id    " +
                        "                  FROM base$HierarchyElementExt he" +
                        "                  WHERE he.hierarchy.id = :custom$selectedHierarchy) " +
                        "                  ORDER BY e.updateTs, e.id desc");
                Map<String, Object> queryParam = new HashMap<>();
                queryParam.put("selectedHierarchy", (Hierarchy) selectedHierarchy);
                positionGroupsDs.refresh(queryParam);
            } else {
                positionGroupsDs.setQuery("SELECT DISTINCT e FROM base$PositionGroupExt e            " +
                        "                  JOIN e.list p                                    " +
                        "                  WHERE :session$systemDate BETWEEN p.startDate and p.endDate " +
                        "                  AND e.id NOT IN (SELECT he.positionGroup.id    " +
                        "                  FROM base$HierarchyElementExt he)                " +
                        "                  ORDER BY e.updateTs, e.id desc");
                positionGroupsDs.refresh();
            }
        }

        if (openedFromAssignmentHistoryEdit != null) {
            positionGroupsDs.setQuery("SELECT DISTINCT e\n" +
                    "                  FROM base$PositionGroupExt e\n" +
                    "                  JOIN e.list p\n" +
                    "                  JOIN tsadv$DicPositionStatus ps ON ps.id = p.positionStatus.id\n" +
                    "                  WHERE :session$systemDate BETWEEN p.startDate AND p.endDate\n" +
                    "                  AND ps.code = 'ACTIVE'\n" +
                    "                  ORDER BY e.updateTs, e.id DESC");
            positionGroupsDs.refresh();
        }
    }

    @Override
    public void ready() {
        super.ready();

        editButton.setEnabled(positionGroupsDs.getItem() != null);
        positionGroupsDs.addItemChangeListener(new Datasource.ItemChangeListener<PositionGroupExt>() {
            @Override
            public void itemChanged(Datasource.ItemChangeEvent<PositionGroupExt> e) {
                editButton.setEnabled(e.getItem() != null);
            }
        });

        positionGroupsFilter.setCaption(positionGroupsFilter.getCaption());
        setDescription(null);
    }

    protected void applyFilter() {
        customFilter.selectFilter("positionFullNameRu");
        customFilter.selectFilter("organization");
        customFilter.applyFilter();
    }


    protected void initFilterMap() {
        filterMap = new LinkedHashMap<>();
        filterMap.put("positionNameRu",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Position.positionNameRu"))
                        .setComponentClass(TextField.class)
                        .setOptions(Op.CONTAINS, Op.DOES_NOT_CONTAIN, Op.ENDS_WITH, Op.STARTS_WITH, Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("lower(p.positionNameLang1) ?")
        );
        filterMap.put("positionNameKz",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Position.positionNameKz"))
                        .setComponentClass(TextField.class)
                        .setOptions(Op.CONTAINS, Op.DOES_NOT_CONTAIN, Op.ENDS_WITH, Op.STARTS_WITH, Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("lower(p.positionNameLang2) ?")
        );
        filterMap.put("positionNameEn",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Position.positionNameEn"))
                        .setComponentClass(TextField.class)
                        .setOptions(Op.CONTAINS, Op.DOES_NOT_CONTAIN, Op.ENDS_WITH, Op.STARTS_WITH, Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("lower(p.positionNameLang3) ?")
        );
        filterMap.put("positionFullNameRu",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Position.positionFullName"))
                        .setComponentClass(TextField.class)
                        .setOptions(Op.CONTAINS, Op.DOES_NOT_CONTAIN, Op.ENDS_WITH, Op.STARTS_WITH, Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("lower(p.positionFullNameLang1) ?")
        );
        filterMap.put("organization",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Organization"))
                        .setComponentClass(LookupPickerField.class)
                        .addComponentAttribute("optionsDatasource", organizationGroupsDs)
                        .addComponentAttribute("captionMode", CaptionMode.PROPERTY)
                        .addComponentAttribute("captionProperty", "organization.organizationName")
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("p.organizationGroupExt.id ?")
        );
        filterMap.put("job",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Job"))
                        .setComponentClass(LookupPickerField.class)
                        .addComponentAttribute("optionsDatasource", jobGroupsDs)
                        .addComponentAttribute("captionMode", CaptionMode.PROPERTY)
                        .addComponentAttribute("captionProperty", "job.jobName")
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("p.jobGroup.id ?")
        );

        filterMap.put("grade",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Grade"))
                        .setComponentClass(LookupPickerField.class)
                        .addComponentAttribute("optionsDatasource", gradeGroupsDs)
                        .addComponentAttribute("captionMode", CaptionMode.PROPERTY)
                        .addComponentAttribute("captionProperty", "grade.gradeName")
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("p.gradeGroup.id ?")
        );

        filterMap.put("location",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.dictionary", "DicLocation"))
                        .setComponentClass(LookupPickerField.class)
                        .addComponentAttribute("optionsDatasource", locationsDs)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("p.location.id ?")
        );

        filterMap.put("fte",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Position.fte"))
                        .setComponentClass(TextField.class)
                        .addComponentAttribute("datatype", Datatypes.get(DoubleDatatype.NAME))
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL, Op.GREATER, Op.GREATER_OR_EQUAL, Op.LESSER, Op.LESSER_OR_EQUAL)
                        .setQueryFilter("p.fte ?")
        );

        filterMap.put("maxPersons",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Position.maxPersons"))
                        .setComponentClass(TextField.class)
                        .addComponentAttribute("datatype", Datatypes.get(IntegerDatatype.NAME))
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL, Op.GREATER, Op.GREATER_OR_EQUAL, Op.LESSER, Op.LESSER_OR_EQUAL)
                        .setQueryFilter("p.maxPersons ?")
        );

        filterMap.put("payroll",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.dictionary", "DicPayroll"))
                        .setComponentClass(LookupPickerField.class)
                        .addComponentAttribute("optionsDatasource", payrollsDs)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("p.payroll.id ?")
        );

        filterMap.put("managerFlag",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Position.managerFlag"))
                        .setComponentClass(CheckBox.class)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("p.managerFlag ?")
        );

        filterMap.put("gradeRule",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "GradeRule"))
                        .setComponentClass(LookupPickerField.class)
                        .addComponentAttribute("optionsDatasource", gradeRulesDs)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("p.gradeRule.id ?")
        );

        filterMap.put("positionStatus",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.dictionary", "DicPositionStatus"))
                        .setComponentClass(LookupPickerField.class)
                        .addComponentAttribute("optionsDatasource", positionStatusesDs)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("p.positionStatus.id ?")
        );

    }

    public void create() {
        openPositionEditor(metadata.create(PositionExt.class), null);
    }

    public void edit() {
        PositionGroupExt positionGroup = positionGroupsDs.getItem();
        if (positionGroup != null) {
            PositionExt position = positionGroup.getPosition();
            if (position != null) {
                openPositionEditor(position, null);
            }
        }
    }

    protected void openPositionEditor(PositionExt position, Map<String, Object> params) {
        PositionEdit positionEdit = (PositionEdit) screenBuilders.editor(PositionExt.class, this)
                .editEntity(position)
                .withLaunchMode(OpenMode.THIS_TAB)
                .withInitializer(e -> {
                    PositionGroupExt positionGroupExt = position.getGroup();
//                    positionGroupsDs.refresh();
                    positionGroupsTable.repaint();
                    if (positionGroupsDs.containsItem(positionGroupExt.getUuid())) {
                        positionGroupsTable.setSelected(positionGroupExt);
                    }
                })
                .build()
                .show();
        positionEdit.addAfterCloseListener(actionId -> {
           positionGroupsDs.refresh();
        });

//        PositionEdit positionEdit = (PositionEdit) openEditor("base$Position.edit", position, WindowManager.OpenType.THIS_TAB, params);
//        positionEdit.addCloseWithCommitListener(() -> {
//            PositionGroupExt positionGroupExt = ((PositionExt) positionEdit.getItem()).getGroup();
//            positionGroupsDs.refresh();
//            positionGroupsTable.repaint();
//            if (positionGroupsDs.containsItem(positionGroupExt.getUuid())) {
//                positionGroupsTable.setSelected(positionGroupExt);
//            }
//        });
    }

    public void editHistory() {
        PositionExt item = listDs.getItem();

        List<PositionExt> items = positionGroupsDs.getItem().getList();
        items.sort((i1, i2) -> i1.getStartDate().before(i2.getStartDate()) ? -1 : 1);

        if (item != null) {
            Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.put("editHistory", Boolean.TRUE);
            paramsMap.put("firstRow", items.indexOf(item) == 0);
            paramsMap.put("lastRow", items.indexOf(item) == items.size() - 1);

            openPositionEditor(item, paramsMap);
        }
    }

    public void removeHistory() {
        PositionExt item = listDs.getItem();
        List<PositionExt> items = positionGroupsDs.getItem().getList();
        items.sort((i1, i2) -> i1.getStartDate().before(i2.getStartDate()) ? -1 : 1);

        int index = items.indexOf(item);
        if (index == 0) {
            //showNotification(getMessage("removeHistory.deny"));
            if (!isHierarchyElementExists(item.getGroup()) && !isAssignmentExists(item.getGroup())) {
                dataManager.remove(item.getGroup());
                positionGroupsDs.refresh();
            } else {
                showNotification(getMessage("removeHistory.connectionsExists"));
            }
        } else {
            showOptionDialog(getMessage("removeDialog.confirm.title"), getMessage("removeDialog.confirm.text"),
                    MessageType.CONFIRMATION,
                    new Action[]{
                            new DialogAction(DialogAction.Type.YES) {
                                @Override
                                public void actionPerform(Component component) {
                                    if (index == items.size() - 1) {
                                        items.get(index - 1).setEndDate(item.getEndDate());
                                        listDs.modifyItem(items.get(index - 1));
                                    } else {
                                        items.get(index + 1).setStartDate(item.getStartDate());
                                        listDs.modifyItem(items.get(index + 1));
                                    }

                                    listDs.removeItem(item);

                                    listDs.getDsContext().commit();
                                    if (listDs.getItems().isEmpty()) {
                                        positionGroupsDs.removeItem(item.getGroup());
                                        positionGroupsDs.getDsContext().commit();
                                    }

                                    positionGroupsDs.refresh();
                                }
                            },
                            new DialogAction(DialogAction.Type.NO)
                    });

        }
    }

    public void removeHistories() {
        PositionExt item = listDs.getItem();
        List<PositionExt> items = positionGroupsDs.getItem().getList();
        items.sort((i1, i2) -> i1.getStartDate().before(i2.getStartDate()) ? -1 : 1);

        int index = items.indexOf(item);
        if (index == 0) {
            //showNotification(getMessage("removeHistory.deny"));
            if (!isHierarchyElementExists(item.getGroup()) && !isAssignmentExists(item.getGroup())) {
                dataManager.remove(item.getGroup());
                positionGroupsDs.refresh();
                positionGroupsTable.repaint();
            } else {
                showNotification(getMessage("removeHistory.connectionsExists"));
            }

        } else {
            showOptionDialog(getMessage("removeDialog.confirm.title"), getMessage("removeDialog.confirm.text"),
                    MessageType.CONFIRMATION,
                    new Action[]{
                            new DialogAction(DialogAction.Type.YES) {
                                @Override
                                public void actionPerform(Component component) {
                                    items.get(index - 1).setEndDate(items.get(items.size() - 1).getEndDate());
                                    listDs.modifyItem(items.get(index - 1));

                                    while (index < items.size())
                                        listDs.removeItem(items.get(index));

                                    listDs.getDsContext().commit();
                                    if (listDs.getItems().isEmpty()) {
                                        positionGroupsDs.removeItem(item.getGroup());
                                        positionGroupsDs.getDsContext().commit();
                                    }

                                    positionGroupsDs.refresh();
                                }
                            },
                            new DialogAction(DialogAction.Type.NO)
                    });

        }
    }

    private boolean isHierarchyElementExists(PositionGroupExt positionGroup) {
        String queryString = "SELECT e FROM base$HierarchyElementExt e\n" +
                "             WHERE e.positionGroup.id = :positionGroupId";
        Map<String, Object> map = new HashMap<>();
        map.put("positionGroupId", positionGroup.getId());
        List<HierarchyElementExt> list = commonService.getEntities(HierarchyElementExt.class, queryString, map, "hierarchyElement.browse");
        if (!list.isEmpty()) {
            return true;
        }
        return false;
    }

    private boolean isAssignmentExists(PositionGroupExt positionGroup) {
        String queryString = "SELECT e FROM base$AssignmentExt e\n" +
                "             WHERE e.positionGroup.id = :positionGroupId";
        Map<String, Object> map = new HashMap<>();
        map.put("positionGroupId", positionGroup.getId());
        List<AssignmentExt> list = commonService.getEntities(AssignmentExt.class, queryString, map, "assignment.view");
        if (!list.isEmpty()) {
            return true;
        }
        return false;
    }

    public void analyticsEdit() {


        OrgAnalytics orgAnalytics = commonService.getEntity(OrgAnalytics.class,
                "select e from tsadv$OrgAnalytics e where e.positionGroup.id = :posGroupId",
                ParamsMap.of("posGroupId", positionGroupsDs.getItem().getId()),
                "organalytics.edit");
        if (orgAnalytics == null) {
            orgAnalytics = metadata.create(OrgAnalytics.class);
            orgAnalytics.setPositionGroup(positionGroupsDs.getItem());
        }
        AbstractEditor abstractEditor = openEditor(orgAnalytics, WindowManager.OpenType.THIS_TAB, ParamsMap.empty());
        abstractEditor.addCloseWithCommitListener(() ->
                positionGroupsDs.refresh());
    }
}