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
import kz.uco.tsadv.config.PersonGroupConfig;
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

public class PersonGroupBrowse extends AbstractLookup {

    private static final Logger log = LoggerFactory.getLogger(PersonGroupBrowse.class);
    private static final String IMAGE_CELL_HEIGHT = "40px";

    @Inject
    protected PersonGroupConfig personGroupConfig;
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
    protected GroupBoxLayout customFilterGroupBox;
    @Inject
    protected GroupTable<PersonGroupExt> personGroupsTable;
    @Inject
    protected Filter filter;

    protected Map<String, Object> param;

    protected Map<String, CustomFilter.Element> filterMap;

    protected CustomFilter customFilter;

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
        param = params;

        // TODO: при наличии ресурсов перенести логику в фильтры xml дескриптора, если нет,
        //  то хотя бы вывести в отдельные методы CTRL + ALT + M

        if (params.get("onlyEmployee") != null) {
            personGroupsDs.setQuery("select distinct e " +
                    "  from base$PersonGroupExt e " +
                    "  join e.list p " +
                    "  join e.assignments a " +
                    " where COALESCE(cast(:param$date date), :session$systemDate) between p.startDate and p.endDate " +
                    "   and p.type.code = 'EMPLOYEE' " +
                    "   and a.assignmentStatus.code <> 'TERMINATED' " +
                    "   and e.id <> :param$personGroupId " +
                    "   and COALESCE(cast(:param$date date), :session$systemDate) between a.startDate and a.endDate "); //coalesce(:param$date, :session$systemDate)
        }
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
        if (params.containsKey("executePersonGroup")) {
            personGroupsDs.setQuery("select e " +
                    " from base$PersonGroupExt e " +
                    "   left join tsadv$UserExt u " +
                    "       on u.personGroup = e " +
                    "  join e.list p " +
                    " where :session$systemDate between p.startDate and p.endDate\n" +
                    " and u is null ");
        }
        if (params.containsKey("administratorPersonGroup")) {
            personGroupsDs.setQuery("select e " +
                    " from base$PersonGroupExt e " +
                    "   left join tsadv$UserExt u " +
                    "       on u.personGroup = e " +
                    "   left join sec$UserRole ur " +
                    "       on ur.user = u " +
                    "   left join sec$Role r " +
                    "       on r = ur.role " +
                    "  join e.list p " +
                    " where :session$systemDate between p.startDate and p.endDate " +
                    " and r.name = 'EMPLOYEE_SELF_SERVICE' ");
        }

        if (personGroupConfig.getEnabledCustomFilter()) {
            customFilterGroupBox.setVisible(true);
            personGroupsDs.setQuery(CustomFilter.getFilteredQuery(personGroupsDs.getQuery(), (String) params.getOrDefault("roleCode", "1=1")));
            personGroupsDs.setQuery(CustomFilter.getFilteredQuery(personGroupsDs.getQuery(), (String) params.getOrDefault("onlyManager", "1=1")));
            personGroupsDs.setQuery(CustomFilter.getFilteredQuery(personGroupsDs.getQuery(), (String) params.getOrDefault("commonFilter", "1=1")));
            initFilterMap();

            customFilter = CustomFilter.init(personGroupsDs, personGroupsDs.getQuery(), filterMap);
            filterBox.add(customFilter.getFilterComponent());

            customFilter.selectFilter("fullName");
            customFilter.selectFilter("employeeNumber");
        }

        if (!params.containsKey("RequisitionEditMap")) {
            personGroupsTable.removeColumn(personGroupsTable.getColumn("generedOpenRequiestionCount"));
        } else {
            personGroupsTable.removeColumn(personGroupsTable.getColumn("person.nationalIdentifier"));
            personGroupsTable.removeColumn(personGroupsTable.getColumn("person.dateOfBirth"));
            personGroupsTable.removeColumn(personGroupsTable.getColumn("person.sex.langValue"));
            personGroupsTable.removeColumn(personGroupsTable.getColumn("person.hireDate"));
            personGroupsTable.removeColumn(personGroupsTable.getColumn("primaryAssignment.organizationGroup"));
            personGroupsTable.removeColumn(personGroupsTable.getColumn("primaryAssignment.positionGroup"));
            personGroupsTable.removeColumn(personGroupsTable.getColumn("primaryAssignment.jobGroup"));
            personGroupsTable.removeColumn(personGroupsTable.getColumn("primaryAssignment.gradeGroup"));
            personGroupsTable.removeColumn(personGroupsTable.getColumn("primaryAssignment.location.langValue"));
            personGroupsTable.removeColumn(personGroupsTable.getColumn("person.type"));
        }
        if (params.containsKey("managerMap")) {
            personGroupsTable.removeColumn(personGroupsTable.getColumn("person.nationalIdentifier"));
            personGroupsTable.removeColumn(personGroupsTable.getColumn("person.dateOfBirth"));
            personGroupsTable.removeColumn(personGroupsTable.getColumn("person.sex"));
            personGroupsTable.removeColumn(personGroupsTable.getColumn("person.type"));
            personGroupsTable.removeColumn(personGroupsTable.getColumn("person.hireDate"));
        }

        filter.setVisible(personGroupConfig.getEnabledCubaFilter());
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

        filterMap.put("fullNameLatin",
                CustomFilter.Element
                        .initElement()
                        .setCaption(messages.getMessage("kz.uco.tsadv.modules.personal.model", "Person.fullNameLatin"))
                        .setComponentClass(TextField.class)
                        .setOptions(Op.CONTAINS, Op.DOES_NOT_CONTAIN, Op.ENDS_WITH, Op.STARTS_WITH, Op.EQUAL, Op.NOT_EQUAL)
                        .setQueryFilter("lower(concat(p.lastNameLatin,concat(' ', concat(p.firstNameLatin, concat(' ', coalesce(p.middleNameLatin,'')))))) ?")
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
