package kz.uco.tsadv.web.modules.personal.persongroup;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.filter.Op;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.base.entity.dictionary.DicLocation;
import kz.uco.tsadv.modules.personal.dictionary.DicPersonType;
import kz.uco.base.entity.dictionary.DicSex;
import kz.uco.tsadv.modules.personal.group.*;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.PersonReview;
import kz.uco.base.service.common.CommonService;
import kz.uco.base.web.components.CustomFilter;
import kz.uco.tsadv.web.modules.personal.common.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("all")
public class PersonGroupReviews extends AbstractWindow {

    private static final Logger log = LoggerFactory.getLogger(PersonGroupBrowse.class);
    private static final String IMAGE_CELL_HEIGHT = "40px";

    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private GroupDatasource<PersonGroupExt, UUID> personGroupsDs;
    @Inject
    private CollectionDatasource<GradeGroup, UUID> gradeGroupsDs;
    @Inject
    private CollectionDatasource<JobGroup, UUID> jobGroupsDs;
    @Inject
    private CollectionDatasource<DicLocation, UUID> locationsDs;
    @Inject
    private CollectionDatasource<OrganizationGroupExt, UUID> organizationGroupsDs;
    @Inject
    private CollectionDatasource<PositionGroupExt, UUID> positionGroupsDs;
    @Inject
    private CollectionDatasource<DicSex, UUID> sexDs;
    @Inject
    private Messages messages;
    @Inject
    private CollectionDatasource<DicPersonType, UUID> personTypesDs;
    @Inject
    private VBoxLayout filterBox;

    private Map<String, CustomFilter.Element> filterMap;

    private CustomFilter customFilter;

    @Inject
    private DataManager dataManager;

    public Component generateUserImageCell(PersonGroupExt entity) {
        return Utils.getPersonImageEmbedded(entity.getPerson(), IMAGE_CELL_HEIGHT, null);
    }

    public void redirectReview(PersonGroupExt personGroup, String name) {
        AssignmentExt assignment = getAssignment(personGroup.getId());

        if (assignment != null) {
            openWindow("person-reviews", WindowManager.OpenType.THIS_TAB, ParamsMap.of("assignment", assignment));
        } else {
            showNotification("Assignment is NULL!");
        }
    }

    public void likeCount(PersonGroupExt personGroup) {
        LoadContext loadContext = LoadContext.create(PersonReview.class);
        loadContext.setQuery(LoadContext.createQuery("select e from tsadv$PersonReview e where e.person.id = :pId and e.liking = '1'")
                .setParameter("pId", personGroup.getId()));
        personGroup.setLikeCount(dataManager.getCount(loadContext));
    }

    public void dislikeCount(PersonGroupExt personGroup) {
        LoadContext loadContext = LoadContext.create(PersonReview.class);
        loadContext.setQuery(LoadContext.createQuery("select e from tsadv$PersonReview e where e.person.id = :pId and e.liking = '-1'")
                .setParameter("pId", personGroup.getId()));
        personGroup.setDisLikeCount(dataManager.getCount(loadContext));
    }

    private AssignmentExt getAssignment(UUID personGroupId) {
        LoadContext<AssignmentExt> loadContext = LoadContext.create(AssignmentExt.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e from base$AssignmentExt e " +
                        "where :sysDate between e.startDate and e.endDate " +
                        "  and e.primaryFlag = true " +
                        "and e.personGroupId.id = :personGroupId")
                .setParameter("personGroupId", personGroupId)
                .setParameter("sysDate", CommonUtils.getSystemDate()))
                .setView("assignment.card");
        return dataManager.load(loadContext);
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        personGroupsDs.setQuery(personGroupsDs.getQuery() + " " + params.getOrDefault("personTypeFilter", "and (p.type.code not in ('CANDIDATE', 'EXEMPLOYEE') or p.type.code is null)"));

        initFilterMap();

        customFilter = CustomFilter.init(personGroupsDs, personGroupsDs.getQuery(), filterMap);
        filterBox.add(customFilter.getFilterComponent());

        personGroupsDs.addCollectionChangeListener(new CollectionDatasource.CollectionChangeListener<PersonGroupExt, UUID>() {
            @Override
            public void collectionChanged(CollectionDatasource.CollectionChangeEvent<PersonGroupExt, UUID> e) {
                if (e.getOperation().equals(CollectionDatasource.Operation.REFRESH)) {
                    setLikeCount();
                    getDsContext().commit();
                }
            }
        });
    }

    @Override
    public void ready() {
        super.ready();
        setLikeCount();
    }

    private void setLikeCount() {
        for (PersonGroupExt personGroup : personGroupsDs.getItems()) {
            likeCount(personGroup);
            dislikeCount(personGroup);
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

    public Component generateGeneratOpenRequisitionCountCell(PersonGroupExt entity) {
        Label label = componentsFactory.createComponent(Label.class);
        Long result;
        Map<String, Object> map = new HashMap<>();
        map.put("personId", entity.getId());
        result = commonService.emQuerySingleRelult(Long.class, "Select sum(reg.openedPositionsCount)" +
                "from tsadv$Requisition reg " +
                "where req.recruiterPersonGroup.id=:personId", map);
        label.setValue(result);

        return label;
    }

}