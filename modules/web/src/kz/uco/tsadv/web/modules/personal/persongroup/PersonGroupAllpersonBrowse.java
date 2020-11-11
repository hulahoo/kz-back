package kz.uco.tsadv.web.modules.personal.persongroup;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.filter.Op;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.common.WebCommonUtils;
import kz.uco.base.entity.dictionary.DicLocation;
import kz.uco.base.entity.dictionary.DicSex;
import kz.uco.base.service.common.CommonService;
import kz.uco.base.web.components.CustomFilter;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.dictionary.DicPersonType;
import kz.uco.tsadv.modules.personal.group.*;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.recruitment.enums.RequisitionStatus;
import kz.uco.tsadv.web.modules.personal.assignment.Receptionassignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class PersonGroupAllpersonBrowse extends AbstractLookup {

    private static final Logger log = LoggerFactory.getLogger(PersonGroupBrowse.class);
    private static final String IMAGE_CELL_HEIGHT = "40px";

    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected GroupDatasource<PersonGroupExt, UUID> personGroupsDs;
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
    protected CollectionDatasource<DicSex, UUID> sexDs;
    @Inject
    protected Messages messages;
    @Inject
    protected CollectionDatasource<DicPersonType, UUID> personTypesDs;
    @Inject
    protected VBoxLayout filterBox;
    @Inject
    protected GroupTable<PersonGroupExt> personGroupsTable;

    protected Map<String, CustomFilter.Element> filterMap;

    protected CustomFilter customFilter;
    protected CustomFilter customFilterRemoveAllreadyAdded;

    @Inject
    protected DataManager dataManager;
    @Inject
    protected Metadata metadata;

    public Component generateUserImageCell(PersonGroupExt entity) {
        Image image = WebCommonUtils.setImage(entity.getPerson().getImage(), null, IMAGE_CELL_HEIGHT);
        image.addStyleName("circle-image");
        return image;
    }

    public void redirectCard(PersonGroupExt personGroup, String name) {
        AssignmentExt assignment = getAssignment(personGroup.getId());

        if (assignment != null) {
            openEditor("person-card", personGroup, WindowManager.OpenType.THIS_TAB);
        } else {
            showNotification("Assignment is NULL!");
        }
    }

    protected AssignmentExt getAssignment(UUID personGroupId) {
        LoadContext<AssignmentExt> loadContext = LoadContext.create(AssignmentExt.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e from base$AssignmentExt e " +
                        "where :sysDate between e.startDate and e.endDate " +
                        "  and e.primaryFlag = true " +
                        "and e.personGroup.id = :personGroupId")
                .setParameter("personGroupId", personGroupId)
                .setParameter("sysDate", CommonUtils.getSystemDate()))
                .setView("assignment.card");
        return dataManager.load(loadContext);
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.get("alreadyExist") != null) {
            personGroupsDs.setQuery("select e\n" +
                    "  from base$PersonGroupExt e\n" +
                    "  join e.list p\n" +
                    "  left join e.assignments a\n" +
                    " where :session$systemDate between p.startDate and p.endDate\n" +
                    "   and (p.type.code <> 'EMPLOYEE' OR :session$systemDate between a.startDate and a.endDate) " +
                    "and e.id not in :param$alreadyExist" +
                    "");
        }
        if (params.get("executePersonGroup") != null) {
            personGroupsDs.setQuery("select e " +
                    " from base$PersonGroupExt e " +
                    "  join e.list p\n" +
                    " where :session$systemDate between p.startDate and p.endDate\n" +
                    " and e.id not in :param$executePersonGroup ");
        }

//        personGroupsDs.setQuery(CustomFilter.getFilteredQuery(personGroupsDs.getQuery(), (String) params.getOrDefault("personTypeFilter", "and (p.type.code not in ('CANDIDATE', 'EXEMPLOYEE') or p.type.code is null)")));
        personGroupsDs.setQuery(CustomFilter.getFilteredQuery(personGroupsDs.getQuery(), (String) params.getOrDefault("roleCode", "1=1")));
        personGroupsDs.setQuery(CustomFilter.getFilteredQuery(personGroupsDs.getQuery(), (String) params.getOrDefault("onlyManager", "1=1")));
        personGroupsDs.setQuery(CustomFilter.getFilteredQuery(personGroupsDs.getQuery(), (String) params.getOrDefault("commonFilter", "1=1")));
        initFilterMap();

        customFilter = CustomFilter.init(personGroupsDs, personGroupsDs.getQuery(), filterMap);
        filterBox.add(customFilter.getFilterComponent());

        customFilter.selectFilter("fullName");
        customFilter.selectFilter("employeeNumber");

        if (!params.containsKey("RequisitionEditMap")) {
            personGroupsTable.removeColumn(personGroupsTable.getColumn("generedOpenRequiestionCount"));
        } else {
            personGroupsTable.removeColumn(personGroupsTable.getColumn("person.nationalIdentifier"));
            personGroupsTable.removeColumn(personGroupsTable.getColumn("person.dateOfBirth"));
            personGroupsTable.removeColumn(personGroupsTable.getColumn("person.sex.langValue"));
            personGroupsTable.removeColumn(personGroupsTable.getColumn("person.hireDate"));
            personGroupsTable.removeColumn(personGroupsTable.getColumn("currentAssignment.organizationGroup"));
            personGroupsTable.removeColumn(personGroupsTable.getColumn("currentAssignment.positionGroup"));
            personGroupsTable.removeColumn(personGroupsTable.getColumn("currentAssignment.jobGroup"));
            personGroupsTable.removeColumn(personGroupsTable.getColumn("currentAssignment.gradeGroup"));
            personGroupsTable.removeColumn(personGroupsTable.getColumn("currentAssignment.location.langValue"));
            personGroupsTable.removeColumn(personGroupsTable.getColumn("person.type"));
        }
        if (params.containsKey("managerMap")) {
            personGroupsTable.removeColumn(personGroupsTable.getColumn("person.nationalIdentifier"));
            personGroupsTable.removeColumn(personGroupsTable.getColumn("person.dateOfBirth"));
            personGroupsTable.removeColumn(personGroupsTable.getColumn("person.sex"));
            personGroupsTable.removeColumn(personGroupsTable.getColumn("person.type"));
            personGroupsTable.removeColumn(personGroupsTable.getColumn("person.hireDate"));
        }
    }


    private void initFilterMap() {
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

        filterMap.put("nationalIdentifier",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Person.nationalIdentifier"))
                        .setComponentClass(TextField.class)
//                        .addComponentAttribute("datatype", Datatypes.get(IntegerDatatype.NAME))
                        .setOptions(Op.CONTAINS, Op.DOES_NOT_CONTAIN, Op.ENDS_WITH, Op.STARTS_WITH, Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("lower(concat('',p.nationalIdentifier)) ?")
        );

        filterMap.put("dateOfBirth",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Person.dateOfBirth"))
                        .setComponentClass(DateField.class)
                        .addComponentAttribute("resolution", DateField.Resolution.DAY)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL, Op.GREATER, Op.GREATER_OR_EQUAL, Op.LESSER, Op.LESSER_OR_EQUAL)
                        .setQueryFilter("p.dateOfBirth ?")
        );

        filterMap.put("sex",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Person.sex"))
                        .setComponentClass(LookupField.class)
                        .addComponentAttribute("optionsDatasource", sexDs)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("p.sex.id ?")
        );

        filterMap.put("hireDate",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Person.hireDate"))
                        .setComponentClass(DateField.class)
                        .addComponentAttribute("resolution", DateField.Resolution.DAY)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL, Op.GREATER, Op.GREATER_OR_EQUAL, Op.LESSER, Op.LESSER_OR_EQUAL)
                        .setQueryFilter("p.hireDate ?")
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
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.dictionary", "DicLocation"))
                        .setComponentClass(LookupPickerField.class)
                        .addComponentAttribute("optionsDatasource", locationsDs)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("a.location.id ?")
        );

        filterMap.put("personType",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.dictionary", "DicPersonType"))
                        .setComponentClass(LookupPickerField.class)
                        .addComponentAttribute("optionsDatasource", personTypesDs)
                        .setOptions(Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("p.type.id ?")
        );
    }

    @Inject
    private CommonService commonService;

    public Component generateGeneredOpenRequiestionCountCell(PersonGroupExt entity) {
        Label label = componentsFactory.createComponent(Label.class);
        Double result;
        Map<String, Object> map = new HashMap<>();
        map.put("personId", entity.getId());
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
            personGroupsDs.refresh();
        });

    }

}