package kz.uco.tsadv.web.modules.personal.person;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.common.WebCommonUtils;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.Absence;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.Dismissal;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.recruitment.enums.RequisitionStatus;
import kz.uco.tsadv.service.AssignmentService;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.service.OrganizationService;
import kz.uco.tsadv.web.modules.personal.assignment.Receptionassignment;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PersonBrowse extends AbstractLookup {
    private static final String IMAGE_CELL_HEIGHT = "40px";
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private DataManager dataManager;
    @Inject
    private Metadata metadata;
    @Inject
    private EmployeeService employeeService;
    @Inject
    private CommonService commonService;
    @Inject
    private OrganizationService organizationService;

    @Inject
    Datasource<PersonGroupExt> personGroupsDs;

    @Inject
    private GroupDatasource<PersonExt, UUID> personsDs;
    @Inject
    private Button transfer;
    @Inject
    private Button combination;
    @Inject
    private Button dismissal;
    @Inject
    private Button absence;
    @Inject
    private GroupTable<PersonExt> personsTable;
    @Inject
    private AssignmentService assignmentService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        personsDs.addItemChangeListener(e -> {
            if (e.getItem() != null) {
                AssignmentExt assignmentExt = e.getItem().getGroup().getCurrentAssignment();
                setEnabledPermissions(assignmentExt);
            }
        });
        personsTable.setMultiSelect(params.containsKey("multiselect"));
        personsTable.addGeneratedColumn("group.currentAssignment.organizationGroup",
                entity -> generateOrgPath(entity));

    }

    protected Component generateOrgPath(PersonExt personExt) {
        Label label = componentsFactory.createComponent(Label.class);
        OrganizationGroupExt organizationGroup = Optional.ofNullable(personExt)
                .map(PersonExt::getGroup)
                .map(PersonGroupExt::getCurrentAssignment)
                .map(AssignmentExt::getOrganizationGroup)
                .orElse(null);
        if (organizationGroup != null) {
            String organizationName = organizationGroup.getOrganizationName();
            label.setValue(organizationName);
            String organizationPathToHint = organizationService.getOrganizationPathToHint(organizationGroup.getId(), CommonUtils.getSystemDate());
            label.setDescription(organizationPathToHint);
        }
        return label;
    }

    @Override
    public void ready() {
        super.ready();
        /*personsDs.addItemChangeListener(e -> {
            personsDsItemChangeListener(e);
        });*/
    }

    protected void personsDsItemChangeListener(Datasource.ItemChangeEvent<PersonExt> e) {
        if (personsTable != null && personsTable.getSingleSelected() != null) {
            UUID personGroupId = Optional.ofNullable(e.getItem()).map(PersonExt::getGroup).map(PersonGroupExt::getId).orElse(null);
            if (personGroupId != null) {
                Dismissal dismis = commonService.getEntity(Dismissal.class, "select e from tsadv$Dismissal e where e.personGroupId.id = :personGroupId",
                        ParamsMap.of("personGroupId", personGroupId), "dismissal.forNotification");
                if (dismis != null) {
                    dismissal.setEnabled(false);
                } else {
                    dismissal.setEnabled(true);
                }
            }
        }
    }

    private void setEnabledPermissions(AssignmentExt item) {
        boolean enabled = item != null;
        transfer.setEnabled(enabled ? employeeService.isLastAssignment(item) : enabled);
        combination.setEnabled(enabled);
        dismissal.setEnabled(enabled);
        absence.setEnabled(enabled);
    }

    public Component generateUserImageCell(PersonExt entity) {
        Image image = WebCommonUtils.setImage(entity.getImage(), null, IMAGE_CELL_HEIGHT);
        image.addStyleName("circle-image");
        return image;
    }

    public void redirectCard(PersonExt person, String name) {
        AssignmentExt assignment = assignmentService.getAssignment(person.getGroup().getId(), "assignment.card");

        if (assignment != null) {
            openEditor("person-card", person.getGroup(), WindowManager.OpenType.THIS_TAB);
        } else {
            AbstractEditor noAssignmentEditor = openEditor("person-card-no-assignment",
                    person.getGroup(), WindowManager.OpenType.THIS_TAB);
            noAssignmentEditor.addCloseWithCommitListener(() -> personGroupsDs.refresh());
        }
    }

    public Component generateGeneredOpenRequiestionCountCell(PersonExt entity) {
        Label label = componentsFactory.createComponent(Label.class);
        Double result;
        Map<String, Object> map = new HashMap<>();
        map.put("personId", entity.getGroup().getId());
        map.put("statusOpen", RequisitionStatus.OPEN);
        result = commonService.emQuerySingleRelult(Double.class, "Select sum(req.openedPositionsCount)" +
                        "from tsadv$Requisition req " +
                        "where req.recruiterPersonGroup.id=:personId " +
                        "AND req.requisitionStatus = :statusOpen"
                , map);
        label.setValue(result);
        return label;
    }

    public void createAssignment() {
        AssignmentGroupExt assignmentGroup = metadata.create(AssignmentGroupExt.class);
        Receptionassignment receptionassignment = (Receptionassignment) openEditor("receptionAssignment", assignmentGroup, WindowManager.OpenType.THIS_TAB);
        receptionassignment.addCloseListener(actionId -> {
            personsDs.refresh();
        });

    }

    /**
     * Перевод
     */
    public void transfer() {
        Map params = new HashMap();
        params.put("transfer", null);
        openEditor("base$Assignment.historyEdit",
                assignmentService.getAssignment(personsDs.getItem().getGroup().getId(), "assignment.edit"),
                WindowManager.OpenType.THIS_TAB,
                params);
    }

    /**
     * Совмещение
     */
    public void combination() {
        Map params = new HashMap();
        params.put("combination", null);
        params.put("assignmentNumber", personsDs.getItem().getGroup().getCurrentAssignment().getGroup().getAssignmentNumber());
        AssignmentGroupExt assignmentGroupExt = metadata.create(AssignmentGroupExt.class);
        AssignmentExt assignmentExt = metadata.create(AssignmentExt.class);
        assignmentGroupExt.setAssignment(assignmentExt);
        assignmentExt.setGroup(assignmentGroupExt);
        assignmentExt.setPersonGroup(personsDs.getItem().getGroup());
        openEditor("base$Assignment.historyEdit",
                assignmentExt,
                WindowManager.OpenType.THIS_TAB,
                params);
    }

    /**
     * Увольнние
     */
    public void dismissal() {
        /*Dismissal dismissal = metadata.create(Dismissal.class);
        dismissal.setPersonGroup(personsDs.getItem().getGroup());
        AbstractEditor editor = openEditor("tsadv$Dismissal.edit",
                dismissal,
                WindowManager.OpenType.THIS_TAB);
        editor.addCloseWithCommitListener(() -> personsDs.refresh());*/
        AssignmentExt assignment = assignmentService.getAssignment(personsDs.getItem().getGroup().getId(), "assignment.card");
        Window window = openWindow("tsadv$Dismissal.browse", WindowManager.OpenType.THIS_TAB,
                ParamsMap.of("personGroup", personsDs.getItem().getGroup(),
                        "assignment", assignment,
                        "notFromCard", true));
        window.addCloseListener(actionId -> personsDs.refresh());

    }

    /**
     * Отсутствие
     */
    public void absence() {
        Absence absence = metadata.create(Absence.class);
        absence.setPersonGroup(personsDs.getItem().getGroup());
        AbstractEditor editor = openEditor("tsadv$Absence.edit",
                absence,
                WindowManager.OpenType.THIS_TAB);
        editor.addCloseWithCommitListener(() -> personsDs.refresh());
    }
}