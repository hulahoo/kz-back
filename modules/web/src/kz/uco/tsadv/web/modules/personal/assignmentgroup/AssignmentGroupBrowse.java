package kz.uco.tsadv.web.modules.personal.assignmentgroup;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.chile.core.datatypes.Datatypes;
import com.haulmont.chile.core.datatypes.impl.IntegerDatatype;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.filter.Op;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowManagerProvider;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.vaadin.server.Page;
import kz.uco.base.entity.dictionary.DicLocation;
import kz.uco.base.service.common.CommonService;
import kz.uco.base.web.components.CustomFilter;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicAssignmentStatus;
import kz.uco.tsadv.modules.personal.group.*;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.service.EmployeeNumberService;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.service.OrganizationService;
import kz.uco.tsadv.web.modules.filterconfig.FilterConfig;
import kz.uco.tsadv.web.modules.personal.assignment.AssignmentHistoryEdit;
import kz.uco.tsadv.web.modules.personal.assignment.Receptionassignment;
import kz.uco.tsadv.web.modules.personal.common.Utils;

import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class AssignmentGroupBrowse extends AbstractLookup {
    private static final String IMAGE_CELL_HEIGHT = "100px";

    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    private OrganizationService organizationService;
    @Inject
    private EmployeeNumberService employeeNumberService;
    @Inject
    protected GroupDatasource<AssignmentExt, UUID> assignmentsDs;

    @Inject
    protected Metadata metadata;

    @Inject
    protected GroupTable<AssignmentExt> assignmentTable;

    @Inject
    protected DataManager dataManager;

    protected Map<String, CustomFilter.Element> filterMap;
    protected CustomFilter customFilter;

    @Inject
    protected VBoxLayout filterBox;

    @Inject
    protected CollectionDatasource<GradeGroup, UUID> gradeGroupsDs;
    @Inject
    protected CollectionDatasource<JobGroup, UUID> jobGroupsDs;
    @Inject
    protected CollectionDatasource<DicLocation, UUID> locationsDs;
    @Inject
    protected CollectionDatasource<OrganizationGroupExt, UUID> organizationGroupsDs;
    @Inject
    protected CollectionDatasource<PositionGroupExt, UUID> positionGroupsDs;
    @Inject
    protected CollectionDatasource<DicAssignmentStatus, UUID> statusDs;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected Button transfer;
    @Inject
    protected Button dismissal;
    @Inject
    protected Button history;
    @Inject
    private Button timecard;
    @Inject
    protected CommonService commonService;
    @Inject
    private FilterConfig filterConfig;
    @Inject
    private GroupBoxLayout groupBox;
    @Inject
    private Filter assignmentsFilter;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        setEnabledPermissions(assignmentsDs.getItem());
        assignmentsDs.addItemChangeListener(e -> setEnabledPermissions(e.getItem()));
        if (filterConfig.getAssignmentEnableCustomFilter()) {
            initFilterMap();
            customFilter = CustomFilter.init(assignmentsDs, assignmentsDs.getQuery(), filterMap);
            filterBox.add(customFilter.getFilterComponent());
            applyFilter();
        } else {
            groupBox.setVisible(false);
        }
        assignmentsFilter.setVisible(filterConfig.getAssignmentEnableCubaFilter());
        assignmentTable.addGeneratedColumn("personGroup",
                entity -> generatePersonGroupCell(entity));
        assignmentTable.addGeneratedColumn("positionGroup",
                entity -> getneratePositionGroupCell(entity));
        assignmentTable.addGeneratedColumn("organizationGroup",
                entity -> generateOrgPath(entity));
        assignmentTable.addGeneratedColumn("jobGroup",
                entity -> generateJobGroupCell(entity));
        assignmentTable.addGeneratedColumn("gradeGroup",
                entity -> generateGradeGroup(entity));
    }

    protected Component generateGradeGroup(AssignmentExt entity) {
        Label label = componentsFactory.createComponent(Label.class);
        Grade grade = entity.getGradeGroup() != null
                ? "TERMINATED".equals(entity.getAssignmentStatus().getCode())
                ? entity.getGradeGroup().getGradeInDate(entity.getStartDate())
                : entity.getGradeGroup().getGrade()
                : null;
        label.setValue(grade != null ? grade.getGradeName() : "");
        return label;
    }

    protected Component generateJobGroupCell(AssignmentExt entity) {
        Label label = componentsFactory.createComponent(Label.class);
        Job job = entity.getJobGroup() != null
                ? "TERMINATED".equals(entity.getAssignmentStatus().getCode())
                ? entity.getJobGroup().getJobInDate(entity.getStartDate())
                : entity.getJobGroup().getJobInDate(entity.getStartDate())
                : null;
        label.setValue(job != null ? job.getJobName() : "");
        return label;
    }

    protected Component getneratePositionGroupCell(AssignmentExt entity) {
        Label label = componentsFactory.createComponent(Label.class);
        PositionExt positionExt = entity.getPositionGroup() != null
                ? "TERMINATED".equals(entity.getAssignmentStatus().getCode())
                ? entity.getPositionGroup().getPositionInDate(entity.getStartDate())
                : entity.getPositionGroup().getPosition()
                : null;
        label.setValue(positionExt != null ? positionExt.getPositionFullName() : "");
        return label;
    }

    protected Component generatePersonGroupCell(AssignmentExt entity) {
        LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
        String caption = "TERMINATED".equals(entity.getAssignmentStatus().getCode())
                ? entity.getPersonGroup().getPersonFioWithEmployeeNumber(entity.getStartDate())
                : entity.getPersonGroup().getPersonFioWithEmployeeNumber();
        linkButton.setCaption(caption != null ? caption : "");
        linkButton.setAction(new BaseAction("redirectToCard") {
            @Override
            public void actionPerform(Component component) {
                redirectCard(entity, "");
            }
        });
        return linkButton;
    }

    protected Component generateOrgPath(AssignmentExt assignmentExt) {
        Label label = componentsFactory.createComponent(Label.class);
        OrganizationGroupExt organizationGroup = assignmentExt.getOrganizationGroup();
        if (organizationGroup == null) {
            return label;
        }
        OrganizationExt organizationExt = assignmentExt.getOrganizationGroup() != null
                ? "TERMINATED".equals(assignmentExt.getAssignmentStatus().getCode())
                ? assignmentExt.getOrganizationGroup().getOrganizationInDate(assignmentExt.getStartDate())
                : assignmentExt.getOrganizationGroup().getOrganization()
                : null;
        String organizationName = organizationExt != null ? organizationExt.getOrganizationName() : "";
        label.setValue(organizationName);

        Date date = "TERMINATED".equals(assignmentExt.getAssignmentStatus().getCode()) ? assignmentExt.getStartDate() : CommonUtils.getSystemDate();
        String organizationPathToHint = organizationService.getOrganizationPathToHint(organizationGroup.getId(), date);
        label.setDescription(organizationPathToHint);
        return label;
    }


    @Override
    public void ready() {
        super.ready();
        assignmentsFilter.setCaption(assignmentsFilter.getCaption());
        setDescription(null);
    }

    protected void applyFilter() {
        customFilter.selectFilter("fullName");
        customFilter.selectFilter("employeeNumber");
        customFilter.applyFilter();
    }

    protected void setEnabledPermissions(AssignmentExt item) {
        boolean enabled = item != null;
        transfer.setEnabled(enabled ? employeeService.isLastAssignment(item) : enabled);
        dismissal.setEnabled(enabled);
        history.setEnabled(enabled);
        timecard.setEnabled(enabled);
        assignmentTable.getAction("edit").setEnabled(enabled);
    }

    protected void initFilterMap() {
        filterMap = new LinkedHashMap<>();
        filterMap.put("fullName",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Person.fullName"))
                        .setComponentClass(TextField.class)
                        .setOptions(Op.CONTAINS, Op.DOES_NOT_CONTAIN, Op.ENDS_WITH, Op.STARTS_WITH, Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("lower(concat(p.lastName,concat(' ', concat(p.firstName, concat(' ', coalesce(p.middleName,'')))))) ?")
        );

        filterMap.put("employeeNumber",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Person.employeeNumber"))
                        .setComponentClass(TextField.class)
                        .setOptions(Op.CONTAINS, Op.DOES_NOT_CONTAIN, Op.ENDS_WITH, Op.STARTS_WITH, Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("lower(p.employeeNumber) ?")
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
                        .setQueryFilter("a.organizationGroup.id ?")
        );

        filterMap.put("position",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Position"))
                        .setComponentClass(LookupPickerField.class)
                        .addComponentAttribute("optionsDatasource", positionGroupsDs)
                        .addComponentAttribute("captionMode", CaptionMode.PROPERTY)
                        .addComponentAttribute("captionProperty", "position.positionName")
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("a.positionGroup.id ?")
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
                        .setQueryFilter("a.jobGroup.id ?")
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
                        .setQueryFilter("a.gradeGroup.id ?")
        );

        filterMap.put("location",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "DicLocation"))
                        .setComponentClass(LookupPickerField.class)
                        .addComponentAttribute("optionsDatasource", locationsDs)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("a.location.id ?")
        );

        filterMap.put("fte",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Assignment.fte"))
                        .setComponentClass(TextField.class)
                        .addComponentAttribute("datatype", Datatypes.get(IntegerDatatype.NAME))
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL, Op.GREATER, Op.GREATER_OR_EQUAL, Op.LESSER, Op.LESSER_OR_EQUAL)
                        .setQueryFilter("a.fte ?")
        );

        filterMap.put("assignDate",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "AssignmentExt.assignDate"))
                        .setComponentClass(DateField.class)
                        .addComponentAttribute("resolution", DateField.Resolution.DAY)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL, Op.GREATER, Op.GREATER_OR_EQUAL, Op.LESSER, Op.LESSER_OR_EQUAL)
                        .setQueryFilter("a.assignDate ?")
        );

        filterMap.put("assignStatus",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "AssignmentExt.assignmentStatus"))
                        .setComponentClass(LookupField.class)
                        .addComponentAttribute("optionsDatasource", statusDs)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("a.assignmentStatus.id ?")
        );
    }

    public void openAssignment() {
        Map<String, Object> map = new HashMap<>();
        map.put("combination", null);
        PersonGroupExt personGroup = assignmentsDs.getItem().getPersonGroup();
        String lastAssignmentNumber = employeeNumberService.getLastAssignmentNumber(personGroup);
        map.put("assignmentNumber", lastAssignmentNumber);
        AssignmentExt assignmentExt = metadata.create(AssignmentExt.class);
        if (assignmentsDs.getItem() == null) {
            throw new RuntimeException("selected AssignmentGroup is null");
        }
        AssignmentGroupExt assignmentGroup = metadata.create(AssignmentGroupExt.class);
        assignmentExt.setGroup(assignmentGroup);
        assignmentExt.setPersonGroup(personGroup);
        assignmentExt.setAssignmentStatus(commonService.getEntity(DicAssignmentStatus.class, "ACTIVE"));
        assignmentExt.setPrimaryFlag(false);

        openAssignmentEditor(assignmentExt, map);
    }

    public void edit() {
        AssignmentExt assignment = assignmentsDs.getItem();
        if (assignment != null) {
            openAssignmentEditor(assignment, null);
        }
    }

    protected void openAssignmentEditor(AssignmentExt assignment, Map<String, Object> params) {
        AssignmentHistoryEdit assignmentHistoryEdit = (AssignmentHistoryEdit) openEditor("base$Assignment.historyEdit", assignment, WindowManager.OpenType.THIS_TAB, params);
        assignmentHistoryEdit.addCloseListener(actionId -> assignmentsDs.refresh());
    }

    public void redirectCard(AssignmentExt assignmentExt, String name) {
        assignmentExt = dataManager.reload(assignmentExt, "assignment.card");
        openEditor("person-card", assignmentExt.getPersonGroup(), WindowManager.OpenType.THIS_TAB);
    }

    public Component generatePersonImageCell(AssignmentExt entity) {
        return Utils.getPersonImageEmbedded(entity.getPersonGroup().getPerson(), IMAGE_CELL_HEIGHT, null);
    }

    public void transfer() {
        Map params = new HashMap();
        params.put("transfer", null);
        AbstractEditor editor = openEditor("base$Assignment.historyEdit",
                assignmentsDs.getItem(),
                WindowManager.OpenType.THIS_TAB,
                params);
        editor.addCloseWithCommitListener(() -> assignmentsDs.refresh());
    }

    public void dismissal() {
        /*Dismissal dismissal = metadata.create(Dismissal.class);
        dismissal.setPersonGroup(assignmentGroupsDs.getItem().getAssignment().getPersonGroup());
        AbstractEditor editor = openEditor("tsadv$Dismissal.edit",
                dismissal,
                WindowManager.OpenType.THIS_TAB);
        editor.addCloseWithCommitListener(() -> assignmentGroupsDs.refresh());*/

        if (assignmentsDs.getItem() != null &&
                assignmentsDs.getItem().getPersonGroup() != null) {
            Window window = openWindow("tsadv$Dismissal.browse", WindowManager.OpenType.THIS_TAB,
                    ParamsMap.of("personGroup", assignmentsDs.getItem().getPersonGroup(),
                            "assignment", assignmentsDs.getItem(),
                            "notFromCard", true));
            window.addCloseListener(actionId -> assignmentsDs.refresh());
        } else {
            showNotification(messages.getMainMessage("error.assignment.group.hasn't.person.group"));
        }
    }

    public void timecard() {
        WindowManager windowManager = AppBeans.get(WindowManagerProvider.class).get();
        String urlShort = "";
        try {
            URL url = Page.getCurrent().getLocation().toURL();
            String s = url.toString();
            urlShort = s.substring(0, s.length() - 2);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String personId = assignmentsDs.getItem().getPersonGroup().getPerson().getId().toString();
        windowManager.showWebPage(urlShort + "open?screen=tsadv$Timecard.browse&openType=DIALOG&params=personId:" + personId, null);
    }


    public void openHistory() {
        Window window = openWindow("base$AssignmentExt.browse", WindowManager.OpenType.THIS_TAB, Collections.singletonMap("personGroupId", assignmentsDs.getItem().getPersonGroup()));
        window.addCloseListener(actionId -> assignmentsDs.refresh());
    }

    public void createAssignment() {
        AssignmentGroupExt assignmentGroup = metadata.create(AssignmentGroupExt.class);
        Receptionassignment receptionassignment = (Receptionassignment) openEditor("receptionAssignment", assignmentGroup, WindowManager.OpenType.THIS_TAB);
        receptionassignment.addCloseListener(actionId -> {
            assignmentsDs.refresh();
        });
    }
}