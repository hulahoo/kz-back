package kz.uco.tsadv.service;

import com.google.gson.Gson;
import com.haulmont.bali.db.QueryRunner;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.*;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.core.sys.AppContext;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.entity.dictionary.DicCity;
import kz.uco.base.entity.dictionary.DicCompany;
import kz.uco.base.entity.dictionary.DicLocation;
import kz.uco.base.entity.shared.Organization;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.config.IncludeExternalExperienceConfig;
import kz.uco.tsadv.config.PositionStructureConfig;
import kz.uco.tsadv.config.RecognitionConfig;
import kz.uco.tsadv.entity.dbview.MyTeam;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.performance.dto.BoardChangedItem;
import kz.uco.tsadv.modules.performance.dto.BoardUpdateType;
import kz.uco.tsadv.modules.performance.enums.MatrixType;
import kz.uco.tsadv.modules.performance.model.CalibrationMember;
import kz.uco.tsadv.modules.performance.model.CalibrationSession;
import kz.uco.tsadv.modules.personal.dictionary.*;
import kz.uco.tsadv.modules.personal.dto.OrgChartNode;
import kz.uco.tsadv.modules.personal.dto.PersonProfileDto;
import kz.uco.tsadv.modules.personal.group.*;
import kz.uco.tsadv.modules.personal.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@Service(EmployeeService.NAME)
public class EmployeeServiceBean implements EmployeeService {

    protected static final Logger log = org.slf4j.LoggerFactory.getLogger(EmployeeServiceBean.class);
    private final Log logger = LogFactory.getLog(EmployeeServiceBean.class.getName());

    @Inject
    private DataManager dataManager;

    @Inject
    private Metadata metadata;

    @Inject
    private Persistence persistence;

    @Inject
    private CommonService commonService;

    @Inject
    private Messages messages;

    @Inject
    private Resources resources;

    @Inject
    private UserSessionSource userSessionSource;

    @Inject
    private RecognitionConfig recognitionConfig;

    @Inject
    private PositionStructureConfig positionStructureConfig;

    @Inject
    private IncludeExternalExperienceConfig includeExternalExperienceConfig;

    @Inject
    private ViewRepository viewRepository;

    protected int languageIndex = 0;

    @Override
    public PersonProfileDto personProfile(UUID personGroupId) {
        PersonProfileDto dto = new PersonProfileDto();

        PersonExt person = dataManager.load(PersonExt.class)
                .query("select e from base$PersonExt e " +
                        " where e.group.id = :personGroupId " +
                        "   and :date between e.startDate and e.endDate ")
                .parameter("personGroupId", personGroupId)
                .parameter("date", CommonUtils.getSystemDate())
                .view(new View(viewRepository.getView(PersonExt.class, View.LOCAL), "", false)
                        .addProperty("sex")
                        .addProperty("image")
                        .addProperty("citizenship")
                        .addProperty("nationality"))
                .one();

        AssignmentExt assignment = dataManager.load(AssignmentExt.class)
                .query("select e from base$AssignmentExt e " +
                        " where e.personGroup.id = :personGroupId " +
                        "   and :date between e.startDate and e.endDate " +
                        "   and e.primaryFlag = 'TRUE' " +
                        "   and e.assignmentStatus.code in ('ACTIVE', 'SUSPENDED')")
                .parameter("personGroupId", personGroupId)
                .parameter("date", CommonUtils.getSystemDate())
                .view(new View(AssignmentExt.class)
                        .addProperty("group", new View(AssignmentGroupExt.class))
                        .addProperty("organizationGroup", new View(OrganizationGroupExt.class)
                                .addProperty("organizationName")
                                .addProperty("list", viewRepository.getView(OrganizationExt.class, View.LOCAL)))
                        .addProperty("positionGroup", new View(PositionGroupExt.class)
                                .addProperty("positionName")
                                .addProperty("list", viewRepository.getView(PositionExt.class, View.LOCAL))))
                .one();

        List<PersonContact> personContacts = dataManager.load(PersonContact.class)
                .query("select e from tsadv$PersonContact e " +
                        " where e.personGroup.id = :personGroupId " +
                        "   and :date between e.startDate and e.endDate ")
                .view(new View(PersonContact.class)
                        .addProperty("contactValue")
                        .addProperty("type", new View(DicPhoneType.class)
                                .addProperty("code")))
                .parameter("personGroupId", personGroupId)
                .parameter("date", CommonUtils.getSystemDate())
                .list();

        List<Address> addresses = dataManager.load(Address.class)
                .query("select e from tsadv$Address e " +
                        " where e.personGroup.id = :personGroupId " +
                        "   and :date between e.startDate and e.endDate ")
                .view(new View(viewRepository.getView(Address.class, View.LOCAL), "", false)
                        .addProperty("city", new View(DicCity.class))
                        .addProperty("kato", new View(DicKato.class))
                        .addProperty("addressType", new View(DicAddressType.class)))
                .parameter("personGroupId", personGroupId)
                .parameter("date", CommonUtils.getSystemDate())
                .list();

        dto.setGroupId(personGroupId);

        //set data from person
        dto.setId(person.getId());
        dto.setFirstLastName(person.getFirstLastName());
        dto.setId(person.getId());
        dto.setFullName(person.getFioWithEmployeeNumber());
        dto.setBirthDate(person.getDateOfBirth());
        dto.setHireDate(person.getHireDate());
        dto.setSex(person.getSex() != null ? person.getSex().getLangValue() : "");
        dto.setCitizenship(person.getCitizenship() != null ? person.getCitizenship().getLangValue() : "");
        dto.setNationality(person.getNationality() != null ? person.getNationality().getLangValue() : "");
        dto.setImageId(person.getImage() != null ? person.getImage().getId() : null);
        dto.setAssignmentGroupId(assignment.getGroup().getId());

        PositionGroupExt positionGroup = this.getPositionGroupByPersonGroupId(personGroupId, new View(PositionGroupExt.class)
                .addProperty("list", new View(PositionExt.class).addProperty("startDate").addProperty("endDate")));

        dto.setPositionGroupId(positionGroup.getId());
        dto.setPositionId(positionGroup.getPosition().getId());

        //set data from assignment
        dto.setOrganizationName(Optional.ofNullable(assignment.getOrganizationGroup())
                .map(OrganizationGroupExt::getOrganization)
                .map(Organization::getOrganizationName)
                .orElse(""));
        dto.setPositionName(Optional.ofNullable(assignment.getPositionGroup()).map(PositionGroupExt::getPositionName).orElse(""));

        //set company
        DicCompany company = this.getCompanyByPersonGroupId(personGroupId);
        dto.setCompanyCode(company != null ? company.getCode() : null);

        //set contacts
        personContacts.forEach(personContact -> {
            //todo
            if ("email".equals(personContact.getType().getCode())) dto.setEmail(personContact.getContactValue());
            else if ("mobile".equals(personContact.getType().getCode())) dto.setPhone(personContact.getContactValue());
        });

        //set address
        //todo
        addresses.stream().map(address -> address.getCityName() != null
                ? address.getCityName()
                : address.getCity() != null
                ? address.getCity().getLangValue()
                : address.getKato() != null
                ? address.getKato().getLangValue()
                : null)
                .filter(Objects::nonNull)
                .findFirst()
                .ifPresent(dto::setCityOfResidence);

        return dto;
    }

    @Override
    public PersonProfileDto personGroupInfo(UUID userId) {
        PersonGroupExt personGroup = dataManager.load(PersonGroupExt.class)
                .query("select p\n" +
                        "            from tsadv$UserExt u\n" +
                        "                inner join u.personGroup p\n" +
                        "                inner join p.assignments a\n" +
                        "            where u.id = :userId\n" +
                        "                and a.startDate <= CURRENT_DATE\n" +
                        "                and a.endDate >= CURRENT_DATE\n" +
                        "                and a.primaryFlag = true")
                .parameter("userId", userId)
                .view(View.MINIMAL)
                .one();

        return this.personProfile(personGroup.getId());
    }

    @Override
    public String generateOgrChart(String personGroupId) {
        if (personGroupId == null) {
            return "";
        }

        List<OrgChartNode> list = getOrgChartNodeHierarchy(personGroupId);
        return new Gson().toJson(list);
    }

    @Override
    public String generate(String personGroupId, String lang, String systemDate) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date sd = null;
        try {
            sd = df.parse(systemDate);
        } catch (Exception e) {
        }
        if (sd == null) sd = new Date();
        AssignmentExt root = fillEmployee(personGroupId, lang, sd);
        return CommonUtils.toJson(root, lang);
    }

    @Override
    public TsadvUser findManagerByPersonGroup(UUID personGroupId, String hierarchyId) {
        UUID positionGroupId = Optional.ofNullable(getPositionGroupByPersonGroupId(personGroupId, View.MINIMAL))
                .map(BaseUuidEntity::getId)
                .orElse(null);
        if (positionGroupId == null) {
            throw new RuntimeException("Position for person not found!");
        }
        return findManagerByPositionGroup(positionGroupId, hierarchyId);
    }

    @Override
    public TsadvUser findManagerByPositionGroup(UUID positionGroupId, String hierarchyId) {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            String hierarchyFilter = "h.primary_flag = True";
            if (StringUtils.isNotBlank(hierarchyId)) {
                hierarchyFilter = "h.id = '" + hierarchyId + "'";
            }

            Query query = em.createNativeQuery(
                    "WITH RECURSIVE nodes(id,parent_id, position_group_id, path, pathName, level) AS (  " +
                            "select he.id, " +
                            "       he.parent_id, " +
                            "       he.position_group_id, " +
                            "       CAST(he.position_group_id AS VARCHAR (4000)), " +
                            "       CAST(p.position_full_name_lang1 AS VARCHAR (4000)), " +
                            "       1 " +
                            "  from base_hierarchy_element he " +
                            "  join base_hierarchy h on h.id = he.hierarchy_id " +
                            "  join base_position p on p.group_id=he.position_group_id " +
                            "  and current_date between p.start_date and p.end_date " +
                            "WHERE he.delete_ts is null " +
                            "   and he.parent_id is null  " +
                            "   and " + hierarchyFilter + " " +
                            "UNION " +
                            "select he.id, he.parent_id, he.position_group_id, " +
                            "       CAST(s1.PATH ||'->'|| he.position_group_id AS VARCHAR(4000)), " +
                            "       CAST(s1.pathName ||'->'|| p.position_full_name_lang1 AS VARCHAR(4000)), " +
                            "       LEVEL + 1 " +
                            "  from base_hierarchy_element he " +
                            "  join nodes s1 on he.parent_id = s1.id " +
                            "  join base_position p on p.group_id=he.position_group_id " +
                            "  and current_date between p.start_date and p.end_date " +
                            ") " +
                            "SELECT " +
                            "   su.id " +
                            "FROM nodes n " +
                            "join base_position_group pg " +
                            "   on n.path like concat('%', concat(pg.id, '%')) " +
                            "join nodes n1 " +
                            "   on n1.position_group_id=pg.id " +
                            "join base_assignment a " +
                            "   on a.position_group_id=pg.id " +
                            "   and current_date between a.start_date and a.end_date " +
                            "   and a.primary_flag=true " +
                            "join tsadv_dic_assignment_status das " +
                            "   on das.id=a.assignment_status_id " +
                            "   and das.code='ACTIVE' " +
                            "join sec_user su " +
                            "   on su.person_group_id=a.person_group_id " +
                            "where n.path like ?1 " +
                            "   and a.position_group_id <> ?2 " +
                            "order by n1.level desc");

            query.setParameter(1, "%" + positionGroupId.toString());
            query.setParameter(2, positionGroupId);

            List<Object[]> rows = query.getResultList();
            if (rows != null && !rows.isEmpty()) {
                for (Object[] row : rows) {
                    UUID userId = (UUID) row[0];
                    if (userId != null) {
                        try {
                            return em.find(TsadvUser.class, userId, View.LOCAL);
                        } catch (Exception ex) {
                            logger.warn(String.format("Manager User by ID: %s not found!", userId.toString()));
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public AssignmentGroupExt getAssignmentGroupByPersonGroup(PersonGroupExt personGroupExt) {
        return persistence.callInTransaction(em -> {
            Query query = em.createQuery("SELECT a.group " +
                    "FROM base$AssignmentExt a " +
                    "   WHERE a.personGroup.id = ?1 " +
                    "      AND current_date BETWEEN a.startDate AND a.endDate" +
                    "       and a.primaryFlag = true " +
                    "       and a.assignmentStatus.code in ('ACTIVE', 'SUSPENDED') ")
                    .setParameter(1, personGroupExt.getId());
            query.setView(AssignmentGroupExt.class, View.MINIMAL);
            List list = query.getResultList();
            return (AssignmentGroupExt) (list.isEmpty() ? null : list.get(0));
        });
    }

    @Override
    public PersonGroupExt getPersonGroupByAssignmentGroupId(UUID assignmentGroupId) {
        return persistence.callInTransaction(em -> {
            Query query = em.createQuery("SELECT a.personGroup " +
                    "FROM base$AssignmentExt a " +
                    "   WHERE a.group.id = ?1 " +
                    "      AND current_date BETWEEN a.startDate AND a.endDate", PersonGroupExt.class);
            query.setParameter(1, assignmentGroupId);
            query.setView(PersonGroupExt.class, View.MINIMAL);
            List list = query.getResultList();
            return (PersonGroupExt) (list.isEmpty() ? null : list.get(0));
        });
    }

    @Override
    public PersonGroupExt getPersonGroupByEmployeeNumber(String employeeNumber) {
        return persistence.callInTransaction(em -> {
            Query query = em.createQuery("SELECT e.group " +
                            "FROM base$Person e " +
                            "   WHERE e.employeeNumber = ?1 " +
                            "      AND current_date BETWEEN e.startDate AND e.endDate",
                    PersonGroupExt.class);
            query.setParameter(1, employeeNumber);
            query.setView(PersonGroupExt.class, View.MINIMAL);
            List list = query.getResultList();
            return (PersonGroupExt) (list.isEmpty() ? null : list.get(0));
        });
    }

    @Override
    public FileDescriptor getImage(String personId) {
        if (personId == null || personId.equalsIgnoreCase("null") || personId.length() == 0) {
            return null;
        }

        View view = new View(PersonExt.class);
        view.addProperty("image");

        LoadContext<PersonExt> loadContext = LoadContext.create(PersonExt.class)
                .setQuery(LoadContext.createQuery("select p from base$PersonExt p where p.id = :pId")
                        .setParameter("pId", UUID.fromString(personId)))
                .setView(view);
        PersonExt model = dataManager.load(loadContext);
        if (model != null) {
            return model.getImage();
        }
        return null;
    }

    @Override
    public FileDescriptor getImageByPersonGroupId(String personGroupId) {
        if (personGroupId == null || personGroupId.equalsIgnoreCase("null") || personGroupId.length() == 0) {
            return null;
        }

        View view = new View(PersonExt.class);
        view.addProperty("image");

        LoadContext<PersonExt> loadContext = LoadContext.create(PersonExt.class)
                .setQuery(LoadContext.createQuery(
                        "select p from base$PersonExt p " +
                                "where :currentDate between p.startDate and p.endDate " +
                                "and p.group.id = :pgId")
                        .setParameter("currentDate", CommonUtils.getSystemDate())
                        .setParameter("pgId", UUID.fromString(personGroupId)))
                .setView(view);
        PersonExt model = dataManager.load(loadContext);
        if (model != null) {
            return model.getImage();
        }
        return null;
    }

    @Override
    public FileDescriptor getImageByEmployeeNumber(String employeeNumber) {
        if (employeeNumber == null || employeeNumber.equalsIgnoreCase("null") || employeeNumber.length() == 0) {
            return null;
        }

        View view = new View(PersonExt.class);
        view.addProperty("image");

        LoadContext<PersonExt> loadContext = LoadContext.create(PersonExt.class)
                .setQuery(LoadContext.createQuery(
                        "select p from base$PersonExt p " +
                                "where :currentDate between p.startDate and p.endDate " +
                                "and p.employeeNumber = :employeeNumber")
                        .setParameter("currentDate", CommonUtils.getSystemDate())
                        .setParameter("employeeNumber", employeeNumber))
                .setView(view);
        PersonExt model = dataManager.load(loadContext);
        if (model != null) {
            return model.getImage();
        }
        return null;
    }

    private byte[] defaultImage() {
        if (resources != null) {
            try {
                Resource defaultAvatar = resources.getResource("kz/uco/tsadv/images/no-avatar.png");
                if (defaultAvatar != null)
                    return IOUtils.toByteArray(defaultAvatar.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new byte[0];
    }


    @Override
    public AssignmentExt findManagerUserByPosition(UUID positionGroupId) {
        try (Transaction tx = persistence.createTransaction()) {
            AssignmentExt u = null;
            EntityManager em = persistence.getEntityManager();
            TypedQuery<AssignmentExt> tq = em.createQuery(
                    "select a " +
                            " from base$AssignmentExt a, tsadv$PositionStructure ps, tsadv$PositionStructure psm " +
                            " where ps.positionGroup.id = :positionGroupId" +
                            " and ps.positionGroupPath like concat('%', concat(psm.positionGroup.id, '%')) " +
                            " and :systemDate between ps.startDate and ps.endDate " +
                            " and :systemDate between ps.posStartDate and ps.posEndDate " +
                            " and :systemDate between psm.startDate and psm.endDate " +
                            " and :systemDate between psm.posStartDate and psm.posEndDate " +
                            " and :systemDate between a.startDate and a.endDate " +
                            " and a.positionGroup.id = psm.positionGroup.id " +
                            " and a.positionGroup.id <> :positionGroupId " +
                            " order by psm.lvl desc", AssignmentExt.class)
                    .setParameter("positionGroupId", positionGroupId)
                    .setParameter("systemDate", CommonUtils.getSystemDate());

            tq.setView(AssignmentExt.class, "assignment.person");

            for (AssignmentExt ue : tq.getResultList()) {
                u = ue;
                break;
            }
            return u;
        }
    }

    @Override
    public TsadvUser getUserByLogin(String login, @Nullable String view) {
        if (view != null && viewRepository.getView(TsadvUser.class, view) == null) {
            view = null;
        }
        return commonService.getEntity(TsadvUser.class, "select e from tsadv$UserExt e where e.login = :userLogin",
                Collections.singletonMap("userLogin", login), view);
    }

    @Override
    public TsadvUser getUserByLogin(String login) {
        return getUserByLogin(login, null);
    }

    @Override
    public TsadvUser getSystemUser(@Nullable String view) {
        return getUserByLogin("admin", view);
    }

    @Override
    public TsadvUser getSystemUser() {
        return getSystemUser(null);
    }

    @Override
    public void changePersonType(PersonGroupExt personGroup, String dicPersonTypeCode) {
        boolean access = false;

        if (dicPersonTypeCode.equalsIgnoreCase("reserve")) {
            access = true;
        } else {
            if (!hasAnotherSuccessor(personGroup.getId())) {
                access = true;
            }
        }

        if (access) {
            try (Transaction tx = persistence.createTransaction()) {
                EntityManager em = persistence.getEntityManager();
                Query query = em.createNativeQuery(
                        "update BASE_PERSON " +
                                "set TYPE_ID = (" +
                                "   select e.id from TSADV_DIC_PERSON_TYPE e " +
                                "   where e.code = ?1) " +
                                "where id = ?2");

                query.setParameter(1, dicPersonTypeCode);
                query.setParameter(2, personGroup.getPerson().getId());
                query.executeUpdate();
                tx.commit();
            }
        }
    }

    @Override
    public List<PersonGroupExt> getManagersList() {
        LoadContext<PersonGroupExt> loadContext = LoadContext.create(PersonGroupExt.class);
        loadContext.setQuery(LoadContext.createQuery("SELECT a.personGroup " +
                " FROM base$AssignmentExt a " +
                " JOIN base$HierarchyElementExt he " +
                " JOIN base$PositionExt p " +
                "WHERE he.positionGroup.id = a.positionGroup.id " +
                "  AND he.positionGroup IS NOT NULL " +
                "  AND he.hierarchy.primaryFlag = TRUE " +
                "  AND :systemDate BETWEEN he.startDate AND he.endDate " +
                "  AND p.group.id = he.positionGroup.id " +
                "  AND :systemDate BETWEEN p.startDate AND p.endDate " +
                "  AND (he.id IN (SELECT he1.parent.id " +
                "                   FROM base$HierarchyElementExt he1 " +
                "                  WHERE he1.positionGroup.id IS NOT NULL " +
                "                    AND :systemDate BETWEEN he1.startDate AND he1.endDate) " +
                "   OR p.managerFlag = TRUE) " +
                "  AND :systemDate BETWEEN a.startDate AND a.endDate")
                .setParameter("systemDate", CommonUtils.getSystemDate()));
        return dataManager.loadList(loadContext);
    }

    @Override
    public PersonGroupExt getPersonGroupByUserId(UUID userId) {
        return persistence.callInTransaction(em -> {
            Query query = em.createQuery("select e.personGroup " +
                    "from tsadv$UserExt e " +
                    "where e.id = :uId")
                    .setParameter("uId", userId);
            query.setView(PersonGroupExt.class, "personGroupExt.edit");
            List list = query.getResultList();
            return list.isEmpty() ? null : (PersonGroupExt) list.get(0);
        });
    }

    @Override
    public PersonGroupExt getPersonGroupByUserIdExtendedView(UUID userId) {
        return persistence.callInTransaction(em -> {
            Query query = em.createQuery("select e.personGroup " +
                    "from tsadv$UserExt e " +
                    "where e.id = :uId")
                    .setParameter("uId", userId);
            query.setView(PersonGroupExt.class, "personGroupExt-view");
            List list = query.getResultList();
            return list.isEmpty() ? null : (PersonGroupExt) list.get(0);
        });
    }

    @Override
    public TsadvUser getUserExtByPersonGroupId(UUID personGroupId) {
        return getUserExtByPersonGroupId(personGroupId, null);
    }

    @Override
    public TsadvUser getUserExtByPersonGroupId(UUID personGroupId, String viewName) {
        return persistence.callInTransaction(em -> {
            Query query = em.createQuery("select e from tsadv$UserExt e where e.personGroup.id = :personGroupId")
                    .setParameter("personGroupId", personGroupId);
            query.setView(TsadvUser.class, viewName != null ? viewName : View.MINIMAL);
            List list = query.getResultList();
            return (TsadvUser) (list.isEmpty() ? null : list.get(0));
        });
    }

    @Override
    public OrganizationGroupExt getOrganizationGroupExtByPositionGroup(PositionGroupExt positionGroupExt, String viewName) {
        return persistence.callInTransaction(em -> {
            Query query = em.createQuery("select distinct e.organizationGroupExt from base$PositionExt e" +
                    "            where e.group.id = :positionGroupId " +
                    "            and :sysDate between e.startDate and e.endDate ")
                    .setParameter("positionGroupId", positionGroupExt.getId())
                    .setParameter("sysDate", CommonUtils.getSystemDate());
            query.setView(OrganizationGroupExt.class, viewName != null ? viewName : View.MINIMAL);
            List list = query.getResultList();
            return (OrganizationGroupExt) (list.isEmpty() ? null : list.get(0));
        });
    }

    @Override
    public PositionGroupExt getPositionGroupByAssignmentGroupId(UUID assignmentGroupId, String view) {
        LoadContext<PositionGroupExt> loadContext = LoadContext.create(PositionGroupExt.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select pg from base$PositionGroupExt pg " +
                        " join pg.assignments a " +
                        "  on a.positionGroup.id = pg.id " +
                        " and a.group.id = :assignmentGroupId " +
                        " and :currentDate between a.startDate and a.endDate"
        )
                .setParameter("assignmentGroupId", assignmentGroupId)
                .setParameter("currentDate", new Date()))
                .setView(view != null ? view : "_minimal");
        return dataManager.load(loadContext);
    }

    @Override
    public PositionGroupExt getPositionGroupByPersonGroupId(UUID personGroupId, String view) {
        return getPositionGroupByPersonGroupId(personGroupId, viewRepository.getView(PositionGroupExt.class, view != null ? view : "_minimal"));
    }

    public PositionGroupExt getPositionGroupByPersonGroupId(UUID personGroupId, View view) {
        LoadContext<PositionGroupExt> loadContext = LoadContext.create(PositionGroupExt.class);
        loadContext.setQuery(LoadContext.createQuery(
                " select a.positionGroup from base$AssignmentExt a " +
                        " where :currentDate between a.startDate and a.endDate" +
                        "      and a.personGroup.id = :personGroupId " +
                        "       and a.primaryFlag = 'TRUE' " +
                        "       and a.assignmentStatus.code in ('ACTIVE', 'SUSPENDED') ")
                .setParameter("personGroupId", personGroupId)
                .setParameter("currentDate", CommonUtils.getSystemDate()))
                .setView(view);
        return dataManager.load(loadContext);
    }

    private boolean hasAnotherSuccessor(UUID personGroupId) {
        LoadContext<Successor> loadContext = LoadContext.create(Successor.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e from tsadv$Successor e " +
                        "where e.personGroup.id = :pgId")
                .setParameter("pgId", personGroupId));
        return dataManager.getCount(loadContext) > 0;
    }

    @Override
    public byte[] getMatrixImage(String id) {
        return new byte[0];
    }

    @Override
    public String getPersonForScrum(String byType, String orgId, String positionId, String jobId, String calibrationSessionId, String lang) {
        if (orgId == null || orgId.equals("0")) orgId = "";
        if (positionId == null || positionId.equals("0")) positionId = "";

        LoadContext<CalibrationMember> loadContext = LoadContext.create(CalibrationMember.class);

        LoadContext.Query query = LoadContext.createQuery(
                "select p from tsadv$CalibrationMember p, base$AssignmentExt a, tsadv$PositionStructure ps " +
                        "where p.session.id = :sessionId " +
                        "and :sysDate between a.startDate and a.endDate " +
                        "and :sysDate between ps.startDate and ps.endDate " +
                        "and :sysDate between ps.posStartDate and ps.posEndDate " +
                        "and a.personGroup.id = p.person.id " +
                        "and ps.positionGroup.id = a.positionGroup.id " +
                        "and ps.organizationGroupPath like concat('%', concat(:organizationGroupId, '%')) " +
                        "and ps.positionGroupPath like concat('%', concat(:positionId, '%')) " +
                        ((jobId != null && !jobId.equals("0")) ? "and a.jobGroup.id = :jobId " : ""));

        query.setParameter("sessionId", UUID.fromString(calibrationSessionId));
        query.setParameter("sysDate", CommonUtils.getSystemDate());
        query.setParameter("organizationGroupId", orgId);
        query.setParameter("positionId", positionId);
        if (jobId != null && !jobId.equals("0")) query.setParameter("jobId", UUID.fromString(jobId));

        loadContext.setQuery(query).setView("calibrationMember.browse");

        List<CalibrationMember> list = dataManager.loadList(loadContext);
        return CommonUtils.toJson(list, byType, lang);
    }

    @Override
    public String getMatrix(String type, String calibrationSessionId, String orgId, String positionId, String jobId, String lang) {
        if (orgId == null || orgId.equals("0")) orgId = "";
        if (positionId == null || positionId.equals("0")) positionId = "";

        if (type != null && calibrationSessionId != null) {
            MatrixType matrixType = MatrixType.find(type);

            if (matrixType != null) {
                LoadContext<CalibrationMember> loadContext = LoadContext.create(CalibrationMember.class);

                LoadContext.Query query = LoadContext.createQuery(
                        "select p from tsadv$CalibrationMember p, base$AssignmentExt a, tsadv$PositionStructure ps " +
                                "where p.session.id = :sessionId " +
                                "and :sysDate between a.startDate and a.endDate " +
                                "and :sysDate between ps.startDate and ps.endDate " +
                                "and :sysDate between ps.posStartDate and ps.posEndDate " +
                                "and a.personGroup.id = p.person.id " +
                                "and ps.positionGroup.id = a.positionGroup.id " +
                                "and ps.organizationGroupPath like concat('%', concat(:organizationGroupId, '%')) " +
                                "and ps.positionGroupPath like concat('%', concat(:positionId, '%')) " +
                                ((jobId != null && !jobId.equals("0")) ? "and a.jobGroup.id = :jobId " : ""));

                query.setParameter("sessionId", UUID.fromString(calibrationSessionId));
                query.setParameter("sysDate", CommonUtils.getSystemDate());
                query.setParameter("organizationGroupId", orgId);
                query.setParameter("positionId", positionId);
                if (jobId != null && !jobId.equals("0")) query.setParameter("jobId", UUID.fromString(jobId));

                loadContext.setQuery(query).setView("calibrationMember.browse");

                List<CalibrationMember> list = dataManager.loadList(loadContext);

                List<MatrixNode> matrixNodes = new LinkedList<>();
                fillList(matrixNodes, lang);

                for (CalibrationMember calibrationMember : list) {
                    createMatrixNode(matrixNodes, calibrationMember, matrixType);
                }

                int allMatrixItemCount = list.size();

                for (MatrixNode matrixNode : matrixNodes) {
                    if (allMatrixItemCount == 0) {
                        matrixNode.setInfo("0 (0%)");
                    } else {
                        int itemSize = matrixNode.getItems().size();
                        matrixNode.setInfo(itemSize + " (" + itemSize * 100 / allMatrixItemCount + "%)");
                    }
                }

                Gson gson = new Gson();
                String json = gson.toJson(matrixNodes);
                return "{\"lists\":" + json + "}";
            }
        }

        return null;
    }

    @SuppressWarnings("all")
    @Override
    public void updatePerformanceMatrix(BoardChangedItem boardChangedItem, BoardUpdateType boardUpdateType, CalibrationSession calibrationSession) {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            Query query = generateCalibrationUpdateQuery(em, boardChangedItem, boardUpdateType, calibrationSession);
            query.executeUpdate();
            tx.commit();
        }
    }

    @Override
    public List<PersonPercentage> getPersonByPositionCompetence(Map<String, Object> params) {
        String positionGroupId = parseValue(StaticVariable.POSITION_GROUP_ID, params);
        String filterOrganizationId = parseValue(StaticVariable.FILTER_ORGANIZATION_GROUP_ID, params);
        String filterPositionId = parseValue(StaticVariable.FILTER_POSITION_GROUP_ID, params);
        String filterJobId = parseValue(StaticVariable.FILTER_JOB_GROUP_ID, params);
        String filterLocationId = parseValue(StaticVariable.FILTER_LOCATION_GROUP_ID, params);
        String filterMatrix = parseValue(StaticVariable.FILTER_MATRIX_ID, params);

        List<PersonPercentage> personPercentages = new ArrayList<>();
        PersonPercentage personPercentage;

        LoadContext<AssignmentExt> loadContext = LoadContext.create(AssignmentExt.class);

        LoadContext.Query query = LoadContext.createQuery(
                "select a from base$AssignmentExt a, tsadv$PositionStructure ps " +
                        "where :sysDate between a.startDate and a.endDate " +
                        "and :sysDate between ps.startDate and ps.endDate " +
                        "and :sysDate between ps.posStartDate and ps.posEndDate " +
                        "and ps.positionGroup.id = a.positionGroup.id " +
                        "and ps.organizationGroupPath like concat('%', concat(:organizationGroupId, '%')) " +
                        "and ps.positionGroupPath like concat('%', concat(:positionId, '%')) " +
                        ((!Objects.equals(filterJobId, "") && !filterJobId.equals("0")) ? "and a.jobGroup.id = :jobId " : ""));

        query.setParameter("sysDate", CommonUtils.getSystemDate());
        query.setParameter("organizationGroupId", filterOrganizationId);
        query.setParameter("positionId", filterPositionId);
        if (!Objects.equals(filterJobId, "") && !filterJobId.equals("0"))
            query.setParameter("jobId", UUID.fromString(filterJobId));

        loadContext.setQuery(query);
        loadContext.setView("assignment.myteam.browse");

        int minMatchPercent = getMinMatchPercent();

        for (AssignmentExt assignment : dataManager.loadList(loadContext)) {
            personPercentage = new PersonPercentage();
            personPercentage.setAssignment(assignment);

            boolean add = true;
            if (!filterLocationId.equals("")) {
                DicLocation location = getLocation(assignment);
                if (location != null && !location.getId().toString().equals(filterLocationId)) {
                    add = false;
                }
            }

            getMatrixPosition(personPercentage);

            if (!filterMatrix.equals("")) {
                if (!Objects.equals(personPercentage.getMatrix(), Integer.valueOf(filterMatrix))) {
                    add = false;
                }
            }

            calculateMatchCount(personPercentage, positionGroupId);

            if (personPercentage.getMatch() < minMatchPercent) {
                add = false;
            }

            if (add) {
                fillManager(personPercentage);
                personPercentages.add(personPercentage);
            }
        }
        return personPercentages;
    }

    private int getMinMatchPercent() {
        int minMatch = 0;
        LoadContext<GlobalValue> loadContext = LoadContext.create(GlobalValue.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e " +
                        "from tsadv$GlobalValue e " +
                        "where :sysDate between e.startDate and e.endDate " +
                        "and e.name = :name")
                .setParameter("name", StaticVariable.MIN_MATCH_PERCENT)
                .setParameter("sysDate", CommonUtils.getSystemDate()))
                .setView(View.LOCAL);
        GlobalValue globalValue = dataManager.load(loadContext);
        if (globalValue != null) {
            try {
                minMatch = Integer.parseInt(globalValue.getValue());
            } catch (NumberFormatException ex) {
                //ignore this exceptions
            }
        }
        return minMatch;
    }

    private void fillManager(PersonPercentage personPercentage) {
        LoadContext<AssignmentExt> loadContext = LoadContext.create(AssignmentExt.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select a from base$AssignmentExt a, tsadv$PositionStructure ps " +
                        "where :sysDate between a.startDate and a.endDate " +
                        "and :sysDate between ps.startDate and ps.endDate " +
                        "and :sysDate between ps.posStartDate and ps.posEndDate " +
                        "and a.positionGroup.id = coalesce(ps.parentPositionGroup.id, ps.positionGroup.id) " +
                        "and ps.positionGroup.id = :pId")
                .setParameter("pId", personPercentage.getAssignment().getPositionGroup().getId())
                .setParameter("sysDate", CommonUtils.getSystemDate()))
                .setView("assignment.myteam.browse");
        personPercentage.setManagerAssignment(dataManager.load(loadContext));
    }

    private DicLocation getLocation(AssignmentExt assignment) {
        DicLocation location = assignment.getLocation();
        if (location == null) {
            location = assignment.getOrganizationGroup().getOrganization().getLocation();

            if (location == null) {
                location = assignment.getPositionGroup().getPosition().getLocation();
            }
        }
        return location;
    }

    private void calculateMatchCount(PersonPercentage personPercentage, String positionGroupId) {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(String.format(
                    "SELECT " +
                            "  jr.id              pg_id, " +
                            "  avg(CASE WHEN rcl.level_number <= coalesce(cel.level_number, -1) " +
                            "    THEN 1 " +
                            "      ELSE 0 END) AS p_match " +
                            "FROM BASE_PERSON_GROUP jr " +
                            "  LEFT JOIN tsadv_competence_element rc " +
                            "    ON rc.position_group_id = '%s' " +
                            "  LEFT JOIN tsadv_scale_level rcl " +
                            "    ON (rcl.id = rc.scale_level_id) " +
                            "  LEFT JOIN tsadv_competence_element ce " +
                            "    ON (ce.person_group_id = jr.id " +
                            "        AND ce.competence_group_id = rc.competence_group_id " +
                            "        AND (ce.id IS NULL OR ce.delete_ts IS NULL)) " +
                            "  LEFT JOIN tsadv_scale_level cel " +
                            "    ON (cel.id = ce.scale_level_id) " +
                            "WHERE jr.delete_ts IS NULL " +
                            "      AND rc.delete_ts IS NULL " +
                            "  and jr.id='%s' " +
                            "GROUP BY jr.id",
                    positionGroupId,
                    personPercentage.getAssignment().getPersonGroup().getId()));

            List<Object[]> list = query.getResultList();
            if (!list.isEmpty()) {
                Object[] row = list.get(0);

                personPercentage.setMatch((int) (((BigDecimal) row[1]).doubleValue() * 100));
            }
        }
    }

    private void getMatrixPosition(PersonPercentage personPercentage) {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(String.format(
                    "SELECT " +
                            "((round(tcm.performance) - 1) * 3 - round(tcm.potencial)) matrix " +
                            "FROM tsadv_calibration_member tcm " +
                            "JOIN tsadv_calibration_session tcs " +
                            "ON tcs.id = tcm.session_id " +
                            "WHERE tcm.delete_ts IS NULL and tcm.person_id = '%s' " +
                            "ORDER BY tcs.date_ DESC limit 1", personPercentage.getAssignment().getPersonGroup().getId()));

            List<Double> list = query.getResultList();
            if (!list.isEmpty()) {
                Double row = list.get(0);
                personPercentage.setMatrix(Math.abs(row.intValue()));
            }
        }
    }

    private int getMatch(Integer allCount, Integer count) {
        return getMatch((long) allCount, (long) count);
    }

    private int getMatch(Long allCount, Long count) {
        int match = parseNull(allCount) == 0 ? 0
                : (parseNull(count) * 100 / parseNull(allCount));
        return match > 100 ? 100 : match;
    }

    private int parseNull(Long integer) {
        if (integer == null) integer = 0L;
        return Math.toIntExact(integer);
    }

    @Override
    public List<PositionPercentage> getPositionByPersonCompetence(Map<String, Object> params, String language) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String sysDate = String.format("to_date('%s','yyyy-MM-dd')", dateFormat.format(CommonUtils.getSystemDate()));

        String personGroupId = parseValue(StaticVariable.USER_PERSON_GROUP_ID, params);
        String filterOrganizationId = parseValue(StaticVariable.FILTER_ORGANIZATION_GROUP_ID, params);
        String filterPositionId = parseValue(StaticVariable.FILTER_POSITION_GROUP_ID, params);
        String filterJobId = parseValue(StaticVariable.FILTER_JOB_GROUP_ID, params);
        String filterLocationId = parseValue(StaticVariable.FILTER_LOCATION_GROUP_ID, params);

        languageIndex = languageIndex(language);

        QueryRunner runner = new QueryRunner(persistence.getDataSource());
        try {
            String sqlQuery = String.format("SELECT " +
                            "  pos.id            p_id, " +
                            "  pos.group_id      p_group_id, " +
                            "  pos.position_name_lang" + languageIndex + " p_name, " +  //columnChanged TODO
                            "  pos.manager_flag  p_manager_flag, " +
                            "  tj.group_id       job_group_id, " +
                            "  tj.job_name_lang" + languageIndex + " job_name," + //columnChanged TODO
                            "  tdl.lang_value1   loc_1, " +
                            "  tdl.lang_value2   loc_2, " +
                            "  tdl.lang_value3   loc_3, " +
                            "  tdl.lang_value4   loc_4, " +
                            "  tdl.lang_value5   loc_5, " +
                            "  too.id            org_id, " +
                            "  too.organization_name_lang" + languageIndex + " org_name, " + //columnChanged TODO
                            "  t.all_count, " +
                            "  t.count " +
                            "FROM ( " +
                            "       WITH person_competences AS ( " +
                            "           SELECT * " +
                            "           FROM tsadv_competence_element pc " +
                            "           WHERE pc.person_group_id = '%s' " +
                            "                 AND pc.delete_ts IS NULL) " +
                            "       SELECT " +
                            "         tp.id, " +
                            "         (SELECT count(*) AS count " +
                            "          FROM person_competences)      AS all_count, " +
                            "         count(pc.competence_group_id) AS count " +
                            "       FROM BASE_POSITION tp " +
                            "         JOIN BASE_HIERARCHY_ELEMENT the " +
                            "           ON the.position_group_id = tp.group_id AND the.element_type = 2 " +
                            "         LEFT JOIN tsadv_competence_element tce " +
                            "           ON tce.position_group_id = tp.group_id " +
                            "         LEFT JOIN person_competences pc " +
                            "           ON pc.competence_group_id = tce.competence_group_id " +
                            "       WHERE tp.delete_ts IS NULL " +
                            "       GROUP BY tp.id) t " +
                            "  JOIN BASE_POSITION pos " +
                            "    ON pos.id = t.id " +
                            "  JOIN tsadv_position_structure tps " +
                            "    ON tps.position_group_id = pos.group_id " +
                            "  JOIN BASE_ORGANIZATION too " +
                            "    ON too.group_id = tps.organization_group_id " +
                            "  JOIN tsadv_job tj " +
                            "    ON tj.group_id = pos.job_group_id " +
                            "  LEFT JOIN BASE_DIC_LOCATION tdl " +
                            "    ON tdl.id = coalesce(pos.location_id, too.location_id) " +
                            "  WHERE %s between pos.start_date and pos.end_date " +
                            "    AND %s between tps.start_Date and tps.end_Date" +
                            "    AND %s between tps.pos_Start_Date and tps.pos_End_Date " +
                            "    AND %s between too.start_date and too.end_date " +
                            "    AND %s between tj.start_date and tj.end_date " +
                            "    AND tps.organization_group_path like '%%%s%%' " +
                            "    AND tps.position_group_path like '%%%s%%' " +
                            "    %s %s " +
                            "order by (case when t.all_count = 0 then 0 else t.count*100/t.all_count end) desc",
                    personGroupId, sysDate, sysDate, sysDate, sysDate, sysDate, filterOrganizationId, filterPositionId,
                    !Objects.equals(filterJobId, "") ? String.format("and pos.job_group_id = '%s'", filterJobId) : "",
                    !Objects.equals(filterLocationId, "") ? String.format("and tdl.id = '%s'", filterLocationId) : "");

            //logger.info(sqlQuery);

            return runner.query(sqlQuery,
                    rs -> {
                        List<PositionPercentage> list = new ArrayList<>();
                        PositionPercentage model;
                        final int minMatchPercent = getMinMatchPercent();

                        while (rs.next()) {
                            model = new PositionPercentage();

                            OrganizationExt organization = metadata.create(OrganizationExt.class);
                            organization.setId(UUID.fromString(rs.getString("org_id")));
                            organization.setOrganizationName(rs.getString("org_name"));  // columnChanged TODO
                            model.setOrganization(organization);

                            PositionExt position = metadata.create(PositionExt.class);
                            position.setId(UUID.fromString(rs.getString("p_id")));
                            position.setPositionName(rs.getString("p_name"));  // columnChanged TODO
                            position.setManagerFlag(rs.getBoolean("p_manager_flag"));

                            PositionGroupExt positionGroup = metadata.create(PositionGroupExt.class);
                            positionGroup.setId(UUID.fromString(rs.getString("p_group_id")));
                            position.setGroup(positionGroup);


                            JobGroup jobGroup = metadata.create(JobGroup.class);
                            position.setJobGroup(jobGroup);
                            jobGroup.setId(UUID.fromString(rs.getString("job_group_id")));

                            Job job = metadata.create(Job.class);
                            job.setJobName(rs.getString("job_name"));
                            jobGroup.setJob(job);
                            model.setPosition(position);

                            DicLocation location = metadata.create(DicLocation.class);
                            location.setLangValue1(rs.getString("loc_1"));
                            location.setLangValue2(rs.getString("loc_2"));
                            location.setLangValue3(rs.getString("loc_3"));
                            location.setLangValue4(rs.getString("loc_4"));
                            location.setLangValue5(rs.getString("loc_5"));
                            model.setLocation(location);

                            model.setAllCount(rs.getInt("all_count"));
                            model.setCount(rs.getInt("count"));
                            model.setMatch(getMatch(model.getAllCount(), model.getCount()));

                            if (model.getMatch() > minMatchPercent) {
                                list.add(model);
                            }
                        }
                        return list;
                    });
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private int languageIndex(String language) {
        String langOrder = AppContext.getProperty("base.abstractDictionary.langOrder");

        if (langOrder != null) {
            String[] langs = langOrder.split(";");
            int count = 1;
            for (String lang : langs) {
                if (language.equals(lang)) {
                    return count;
                }
                count++;
            }
        }
        return 1;
    }

    @Override
    public long countActivePerson() {
        try (Transaction tx = persistence.createTransaction()) {
            Query query = persistence.getEntityManager().createNativeQuery(
                    "SELECT count(*)" +
                            "FROM BASE_ASSIGNMENT " +
                            "WHERE current_date BETWEEN start_date AND end_date");
            return (long) query.getSingleResult();
        }
    }

    @Override
    public long countExPerson() {
        try (Transaction tx = persistence.createTransaction()) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);


            Query query = persistence.getEntityManager().createNativeQuery(
                    "SELECT count(*) " +
                            "FROM BASE_PERSON tp " +
                            "WHERE tp.type_id = " +
                            "   (" +
                            "       SELECT id " +
                            "       FROM tsadv_dic_person_type " +
                            "       WHERE code = 'EXEMPLOYEE'" +
                            "   ) and tp.end_date between ?1 and current_date");
            query.setParameter(1, calendar.getTime());
            return (long) query.getSingleResult();
        }
    }


    @Override
    public List<OrganizationHrUser> getHrUsers(UUID organizationGroupId, String roleCodes) {
        List<OrganizationHrUser> hrUserList = new ArrayList<>();

            /*if (organizationGroup.getHrUsers() == null || organizationGroup.getHrUsers().isEmpty())
                continue;
            else {*/
                /*List<OrganizationHrUser> hrUsers = organizationGroup
                        .getHrUsers()
                        .stream()
                        .filter(u -> u.getDeleteTs() == null
                                && !u.getDateFrom().after(CommonUtils.getSystemDate())
                                && !u.getDateTo().before(CommonUtils.getSystemDate()))
                        .collect(Collectors.toList());*/
        Map<String, Object> qParams = new HashMap<>();
        qParams.put("organizationGroupId", organizationGroupId);
        qParams.put("systemDate", CommonUtils.getSystemDate());

        if (roleCodes != null && roleCodes.length() > 0)
            qParams.put("roleCodes", Arrays.asList(roleCodes.split(",")));

        List<OrganizationHrUser> hrUsers = commonService.getEntities(OrganizationHrUser.class,
                "select e " +
                        "   from tsadv$OrganizationHrUser e " +
                        "  where e.organizationGroup.id = :organizationGroupId " +
                        "    and e.deleteTs is null " +
                        "    and :systemDate between e.dateFrom and e.dateTo" +
                        "    and exists (select 1 from tsadv$HrUserRole ur, tsadv$DicHrRole r  " +
                        "  where ur.user.id = e.user.id " +
                        "    and r.id = ur.role.id" +
                        "    and :systemDate between ur.dateFrom and ur.dateTo " +
                        (qParams.containsKey("roleCodes") ? " and r.code IN :roleCodes " : "") +
                        ") ",
                qParams,
                "organizationHrUser.view");

        if (hrUsers != null && !hrUsers.isEmpty()) {
            hrUserList.addAll(hrUsers);
            return hrUserList;
        } else {

            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("organizationGroupId", organizationGroupId);
            queryParams.put("systemDate", CommonUtils.getSystemDate());

            List<OrganizationGroupExt> organizationGroupExtList = commonService.getEntities(OrganizationGroupExt.class,
                    "select e.parent.organizationGroup " +
                            " from base$HierarchyElementExt e " +
                            " where :systemDate between e.startDate and e.endDate" +
                            " and e.organizationGroup.id = :organizationGroupId " +
                            "   and e.hierarchy.primaryFlag = true",
                    queryParams,
                    View.MINIMAL/*"organizationGroup.hrUsersView"*/);
            if (!organizationGroupExtList.isEmpty()) {
                OrganizationGroupExt organizationGroupExt = organizationGroupExtList.stream().findFirst().orElse(null);
                return getHrUsers(organizationGroupExt.getId(), roleCodes);
            } else {
                return hrUserList;
            }
        }
    }

    @Override
    public String getTotalExperience(UUID personGroupId, Date onDate) {
        if (!includeExternalExperienceConfig.getIncludeExternalExperience()) {
            return getExperienceInCompany(personGroupId, onDate);
        }

        Map<Integer, Object> map = new HashMap<>();
        map.put(1, onDate);
        map.put(2, personGroupId);
        String query1 = "SELECT a.start_date, a.end_date, false " +
                "  FROM BASE_ASSIGNMENT a " +
                " WHERE a.person_group_id = ?2 " +
                "   and a.start_Date <= ?1 " +
                "   and a.delete_ts is null";

        List<Object[]> list = new ArrayList<>(commonService.emNativeQueryResultList(query1, map));//changed
        String query2 = " SELECT e.start_month, e.end_month, e.until_now " +
                "  FROM tsadv_person_experience e " +
                " WHERE e.person_group_id = ?2 " +
                "   and e.start_month <= ?1 " +
                "   and e.delete_ts is null";
        List<Object[]> list2 = (commonService.emNativeQueryResultList(query2, map));
        if (list2.size() != 0) {
            for (Object[] items : list2) {
                if (items[1] == null) {
                    items[1] = new Date();
                }
            }
            list.addAll(list2);
        }

        return getYears(list);
    }

    @Override
    public Double getTotalExperienceDouble(UUID personGroupId, Date onDate) {
        Map<Integer, Object> map = new HashMap<>();
        map.put(1, onDate);
        map.put(2, personGroupId);
        List<Object[]> list = new ArrayList<>();
        String query1 = "SELECT a.start_date, a.end_date, false " +
                "  FROM BASE_ASSIGNMENT a " +
                " WHERE a.person_group_id = ?2 " +
                "   and a.start_Date <= ?1 " +
                "   and a.delete_ts is null";

        list.addAll(commonService.emNativeQueryResultList(query1, map));//changed

        if (includeExternalExperienceConfig.getIncludeExternalExperience()) {
            String query2 = " SELECT e.start_month, e.end_month, e.until_now " +
                    "  FROM tsadv_person_experience e " +
                    " WHERE e.person_group_id = ?2 " +
                    "   and e.start_month <= ?1 " +
                    "   and e.delete_ts is null";
            list.addAll(commonService.emNativeQueryResultList(query2, map));
        }
        return getYearsDouble(list);
    }


    @Override
    public String getExperienceInCompany(UUID personGroupId, Date onDate) {
        Map<Integer, Object> map = new HashMap<>();
        map.put(1, onDate);
        map.put(2, personGroupId);
        List<Object[]> list = new ArrayList<>();
        String query = "SELECT e.start_date, e.end_date, false " +
                "  FROM BASE_ASSIGNMENT e " +
                " WHERE e.person_group_id = ?2 " +
                "   and e.start_Date < ?1 " +
                "   and e.delete_ts is null";

        list.addAll(commonService.emNativeQueryResultList(
                query, map));
        return getYears(list);
    }


    private String getYears(List<Object[]> list) {
        int experienceDayCountInCompany = 0;
        Map<Date, Date> dateDateMap;
        dateDateMap = getAllDate(list);
        for (Map.Entry<Date, Date> e : dateDateMap.entrySet())
            experienceDayCountInCompany += (e.getValue().getTime() - e.getKey().getTime()) / (1000 * 60 * 60 * 24);

        int year = experienceDayCountInCompany / 365;
        int mounth = (experienceDayCountInCompany % 365) / 30;
        String result;
        String currentLanguage = userSessionSource.getLocale().getLanguage();
        switch (currentLanguage) {
            case ("ru"):
                result = " лет: " + year + ", месяцев: " + mounth;
                break;
            case ("en"):
                result = year + " y. " + mounth + " m.";
                break;
            case ("kz"):
                result = year + " ж. " + mounth + " а.";
                break;
            default:
                result = year + " y. " + mounth + " m.";
                break;
        }
        return result;
    }


    private Double getYearsDouble(List<Object[]> list) {
        Map<Date, Date> dateDateMap = new HashMap<>();
        dateDateMap = getAllDate(list);
        Double experienceDayCountInCompany = 0.0;
        for (Map.Entry<Date, Date> e : dateDateMap.entrySet())
            experienceDayCountInCompany += (e.getValue().getTime() - e.getKey().getTime()) / (1000 * 60 * 60 * 24);
        Double result = ((Long) Math.round((experienceDayCountInCompany * 10) / 365)).doubleValue() / 10;
        return result;
    }

    private Map getAllDate(List<Object[]> list) {
        Map<Date, Date> dateDateMap = new HashMap<>();
        for (Object[] o : list) {
            Date startDate = (Date) o[0], endDate = (Date) o[1];
            boolean untilNow = false;
            if (o[2] != null) {
                untilNow = (boolean) o[2];
            }
            for (Map.Entry<Date, Date> e : dateDateMap.entrySet()) {
                startDate = (Date) o[0];
                endDate = (Date) o[1];
                if (o[2] != null) {
                    untilNow = (boolean) o[2];
                }
                if (untilNow) {
                    endDate = new Date();
                    endDate.setTime(CommonUtils.getSystemDate().getTime());
                }
                if (startDate.getTime() >= e.getKey().getTime() && startDate.getTime() <= e.getValue().getTime()) {
                    startDate.setTime(e.getValue().getTime() + (1000 * 60 * 60 * 24));
                }
                if (endDate.getTime() <= e.getValue().getTime() && endDate.getTime() >= e.getKey().getTime()) {
                    endDate.setTime(e.getKey().getTime() - (1000 * 60 * 60 * 24));
                }
            }

            if (endDate.getTime() > CommonUtils.getSystemDate().getTime()) {
                endDate.setTime(CommonUtils.getSystemDate().getTime());
            }
            if (!startDate.after(endDate)) {
                dateDateMap.put(startDate, endDate);
            }
        }
        return dateDateMap;
    }

    private String parseValue(String key, Map<String, Object> params) {
        String value = "";
        if (params.containsKey(key)) {
            value = String.valueOf(params.get(key));
        }
        //log.info(key + " " + value);
        return value;
    }

    private Query generateCalibrationUpdateQuery(EntityManager entityManager, BoardChangedItem boardChangedItem, BoardUpdateType boardUpdateType, CalibrationSession calibrationSession) {
        String queryString = "update tsadv$CalibrationMember c set %s where c.person.id = ?1 and c.session.id = ?2";

        switch (boardUpdateType) {
            case MATRIX_PERFORMANCE: {
                String[] split = getMatrixPosition(boardChangedItem.getTo()).split(",");
                queryString = String.format(queryString, String.format("c.performance = %s, c.potencial = %s", split[0], split[1]));
                break;
            }
            case MATRIX_RISK: {
                String[] split = getMatrixPosition(boardChangedItem.getTo()).split(",");
                queryString = String.format(queryString, String.format("c.riskOfLoss = %s, c.impactOfLoss = %s", split[0], split[1]));
                break;
            }
            case SCRUM_COMPETENCE: {
                queryString = String.format(queryString, String.format("c.competenceOverall = %d", boardChangedItem.getTo()));
                break;
            }
            case SCRUM_GOAL: {
                queryString = String.format(queryString, String.format("c.goalOverall = %d", boardChangedItem.getTo()));
                break;
            }
        }

        Query query = entityManager.createQuery(queryString);
        query.setParameter(1, UUID.fromString(boardChangedItem.getId()));
        query.setParameter(2, calibrationSession.getId());
        return query;
    }

    private String getMatrixPosition(int position) {
        switch (position) {
            case 1: {
                return "1,3";
            }
            case 2: {
                return "2,3";
            }
            case 3: {
                return "3,3";
            }
            case 4: {
                return "1,2";
            }
            case 5: {
                return "2,2";
            }
            case 6: {
                return "3,2";
            }
            case 7: {
                return "1,1";
            }
            case 8: {
                return "2,1";
            }
            default: {
                return "3,1";
            }
        }
    }

    private void createMatrixNode(List<MatrixNode> matrixNodes, CalibrationMember calibrationMember, MatrixType matrixType) {
        String webAppUrl = AppContext.getProperty("cuba.webAppUrl");
        MatrixNode matrixNode = matrixNodes.get(getMatrixBlock(calibrationMember, matrixType) - 1);
        MatrixNodeItem item = new MatrixNodeItem();

        PersonGroupExt personGroup = calibrationMember.getPerson();
        item.setId(personGroup.getId().toString());
        item.setTitle(personGroup.getPerson().getFullName());
        /*item.setUrl("/tal/image_api?userId=" + personGroup.getPerson().getId().toString());*/
        item.setUrl(webAppUrl + "/dispatch/person_image/" + personGroup.getPerson().getId().toString());
        matrixNode.getItems().add(item);
    }

    private int getMatrixBlock(CalibrationMember calibrationMember, MatrixType matrixType) {
        int i = calibrationMember.getRiskOfLoss(), j = calibrationMember.getImpactOfLoss();

        if (matrixType.equals(MatrixType.PERFORMANCE)) {
            i = calibrationMember.getPerformance();
            j = calibrationMember.getPotencial();
        }

        return Math.round(i) + (3 - Math.round(j)) * 3;
    }

    private void fillList(List<MatrixNode> list, String lang) {
        MatrixNode node;
        for (int i = 1; i <= 9; i++) {
            node = new MatrixNode();
            node.setListId(String.valueOf(i));
            node.setDefaultStyle("style-" + i);

            if (i <= 3) {
                node.setTitle(getMessage("matrix.title.x." + i, lang));
            }

            if (i == 1 || i == 4 || i == 7) {
                node.setvText(getMessage("matrix.title.y." + i, lang));
            }

            list.add(node);
        }
    }

    private String getMessage(String key, String lang) {
        Messages messages = AppBeans.get(Messages.class);
        return messages.getMainMessage(key, new Locale(lang));
    }

    public class MatrixNode {
        private String title;
        private String listId;
        private String info;
        private String vText;
        private String defaultStyle;
        private List<MatrixNodeItem> items = new ArrayList<>();

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getListId() {
            return listId;
        }

        public void setListId(String listId) {
            this.listId = listId;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public String getvText() {
            return vText;
        }

        public void setvText(String vText) {
            this.vText = vText;
        }

        public String getDefaultStyle() {
            return defaultStyle;
        }

        public void setDefaultStyle(String defaultStyle) {
            this.defaultStyle = defaultStyle;
        }

        public List<MatrixNodeItem> getItems() {
            return items;
        }

        public void setItems(List<MatrixNodeItem> items) {
            this.items = items;
        }
    }

    public class MatrixNodeItem {
        private String id;
        private String title;
        private String url;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }


    private void print(PersonGroupExt root) {
        if (root.getPerson().getChildren() != null && !root.getPerson().getChildren().isEmpty()) {
            for (PersonGroupExt child : root.getPerson().getChildren()) {
                if (child != null) {
                    print(child);
                }
            }
        }
    }

    private AssignmentExt parseQueryResult(Object[] row) {
        AssignmentExt assignment = metadata.create(AssignmentExt.class);

        try {
            assignment.setId((UUID) row[0]);
            AssignmentGroupExt group = metadata.create(AssignmentGroupExt.class);
            group.setId((UUID) row[1]);
            assignment.setGroup(group);

            PersonExt person = metadata.create(PersonExt.class);
            PersonGroupExt personGroup = metadata.create(PersonGroupExt.class);
            person.setId((UUID) row[2]);
            personGroup.setId((UUID) row[3]);
            person.setGroup(personGroup);
            person.setLastName((String) row[4]);
            person.setFirstName((String) row[5]);
            person.setMiddleName((String) row[6]);
            person.setEmployeeNumber((String) row[7]);
            /*personGroup.setList(new ArrayList<Person>());
            personGroup.getList().add(person);*/
            personGroup.setPerson(person);
            assignment.setPersonGroup(personGroup);

            PositionExt position = metadata.create(PositionExt.class);
            PositionGroupExt positionGroup = metadata.create(PositionGroupExt.class);
            position.setId((UUID) row[8]);
            positionGroup.setId((UUID) row[9]);
            position.setGroup(positionGroup);
            position.setPositionNameLang1((String) row[10]);
            position.setPositionNameLang2((String) row[11]);
            position.setPositionNameLang3((String) row[12]);

            OrganizationGroupExt organizationGroup = metadata.create(OrganizationGroupExt.class);
            OrganizationExt organization = metadata.create(OrganizationExt.class);
            organizationGroup.setOrganization(organization);
            organization.setGroup(organizationGroup);
            organization.setOrganizationNameLang1((String) row[13]);
            organization.setOrganizationNameLang2((String) row[14]);
            organization.setOrganizationNameLang3((String) row[15]);

            DicLocation location = metadata.create(DicLocation.class);
            location.setLangValue1((String) row[16]);
            location.setLangValue2((String) row[17]);
            location.setLangValue3((String) row[18]);
            location.setLangValue4((String) row[19]);
            location.setLangValue5((String) row[20]);
            assignment.setLocation(location);

            GradeGroup gradeGroup = metadata.create(GradeGroup.class);
            Grade grade = metadata.create(Grade.class);
            gradeGroup.setGrade(grade);
            grade.setGroup(gradeGroup);
            grade.setGradeName((String) row[21]);
            position.setGradeGroup(gradeGroup);

            assignment.setOrganizationGroup(organizationGroup);
            /*positionGroup.setList(new ArrayList<Position>());
            positionGroup.getList().add(position);*/
            positionGroup.setPosition(position);
            assignment.setPositionGroup(positionGroup);

        } catch (Exception e) {
        }

        return assignment;
    }

    private AssignmentExt fillEmployee(String personGroupId, String lang, Date systemDate) {
        languageIndex = languageIndex(lang);
        AssignmentExt root = null;
        //String query = "select * from tsadv_my_team_v;";
        String query = "SELECT  mt.id,   " +
                "    null group_id,   " +
                "    per.id person_id,   " +
                "    mt.person_group_id   AS person_group_id,   " +
                "    per.last_name,   " +
                "    per.first_name,   " +
                "    per.middle_name,   " +
                "    per.employee_number,   " +
                "    pos.id       AS position_id,   " +
                "    pos.group_id AS position_group_id,   " +
                "    pos.position_name_lang1, " +
                "    pos.position_name_lang2, " +
                "    pos.position_name_lang3, " +
                "    org.organization_name_lang1, " +
                "    org.organization_name_lang2, " +
                "    org.organization_name_lang3, " +
                "  tdl.lang_value1 loc1,   " +
                "  tdl.lang_value2 loc2,   " +
                "  tdl.lang_value3 loc3,   " +
                "  tdl.lang_value4 loc4,   " +
                "  tdl.lang_value5 loc5,   " +
                "  tg.grade_name   " +
                "from tsadv_my_team_view mt " +
                "join base_person per " +
                "on per.group_id=mt.person_group_id " +
                "and ?2 between per.start_date and per.end_date " +
                "and per.delete_ts is null " +
                "join base_position pos " +
                "on pos.group_id=mt.position_group_id " +
                "and ?2 between pos.start_date and pos.end_date " +
                "and pos.delete_ts is null " +
                "join base_organization org " +
                "on org.group_id=mt.organization_group_id " +
                "and ?2 between org.start_date and org.end_date " +
                "and org.delete_ts is null " +
                "LEFT JOIN tsadv_grade tg   " +
                " ON tg.group_id = pos.grade_group_id   " +
                "LEFT JOIN BASE_DIC_LOCATION tdl   " +
                "ON tdl.id = coalesce(pos.location_id,org.location_id) " +
                "where person_group_id = ?1;";

        try (Transaction transaction = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            /*List<Object[]> resultList = em.createNativeQuery(query).getResultList();*/
            List<Object[]> resultList = em.createNativeQuery(query)
                    .setParameter(1, UUID.fromString(personGroupId))
                    .setParameter(2, CommonUtils.getSystemDate())
                    .getResultList();
            if (!resultList.isEmpty()) {
                Object[] row = resultList.get(0);
                root = parseQueryResult(row);
                fillContacts(root);
            }
        }
        if (root != null) fillChilds(root, lang, systemDate);
        return root;
    }

    private void fillChilds(AssignmentExt root, String lang, Date systemDate) {
        languageIndex = languageIndex(lang);
        String vacancy = messages.getMainMessage("positionStructure.vacancy", new Locale(lang));
        String query = "select " +
                "    a.id, " +
                "    a.group_id, " +
                "    p.id as person_id, " +
                "    p.group_id as person_group_id, " +
                "    case " +
                "        when a.id is null then '  vacancy +  ' " +
                "        else p.last_name " +
                "    end as last_name, " +
                "    case " +
                "        when a.id is null then '  vacancy +  ' " +
                "        else p.first_name " +
                "    end as first_name, " +
                "    case " +
                "        when a.id is null then '  vacancy +  ' " +
                "        else p.middle_name " +
                "    end as middle_name, " +
                "    case " +
                "        when a.id is null then '  vacancy +  ' " +
                "        else p.employee_number " +
                "    end as employee_number, " +
                "    pos.id as position_id, " +
                "    pos.group_id as position_group_id, " +
                "    pos.position_name_lang1, " +
                "    pos.position_name_lang2, " +
                "    pos.position_name_lang3, " +
                "    too.organization_name_lang1, " +
                "    too.organization_name_lang2, " +
                "    too.organization_name_lang3, " +
                "    tdl.lang_value1 loc1, " +
                "    tdl.lang_value2 loc2, " +
                "    tdl.lang_value3 loc3, " +
                "    tdl.lang_value4 loc4, " +
                "    tdl.lang_value5 loc5, " +
                "    tg.grade_name " +
                "from tsadv_my_team_view mt " +
                "join base_assignment a " +
                "on a.person_group_id=mt.person_group_id " +
                "and a.primary_flag=true " +
                "and ?2 between a.start_date and a.end_date " +
                "and a.delete_ts is null " +
                "join base_person p " +
                "on p.group_id=mt.person_group_id " +
                "and ?2 between p.start_date and p.end_date " +
                "and p.delete_ts is null " +
                "join base_position pos " +
                "on pos.group_id=mt.position_group_id " +
                "and ?2 between pos.start_date and pos.end_date " +
                "and pos.delete_ts is null " +
                "join base_organization too " +
                "on too.group_id=mt.organization_group_id " +
                "and ?2 between too.start_date and too.end_date " +
                "and too.delete_ts is null " +
                "left join tsadv_grade tg on " +
                "    tg.group_id = pos.grade_group_id " +
                "left join BASE_DIC_LOCATION tdl on " +
                "    tdl.id = coalesce(a.location_id,pos.location_id,too.location_id) " +
                "where " +
                "    1=1 " +
                "    and mt.parent_position_group_id = ?1; ";

        try (Transaction transaction = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            List<Object[]> resultList = em.createNativeQuery(query)
                    .setParameter(1, root.getPositionGroup().getId())
                    .setParameter(2, systemDate)
                    .getResultList();
            root.setChildren(new ArrayList<>());
            for (Object[] row : resultList) {
                AssignmentExt a = parseQueryResult(row);
                root.getChildren().add(a);
                fillContacts(root);
            }
        }

        for (AssignmentExt child : root.getChildren()) {
            fillChilds(child, lang, systemDate);
        }

    }

    private void fillContacts(AssignmentExt assignment) {
        if (assignment.getPersonGroup() != null) {
            LoadContext<PersonContact> loadContext = LoadContext.create(PersonContact.class);
            loadContext.setQuery(LoadContext.createQuery(String.format("select e from tsadv$PersonContact e where e.personGroup.id = :pId"))
                    .setParameter("pId", assignment.getPersonGroup().getId()))
                    .setView("personContact.edit");
            assignment.getPersonGroup().setPersonContacts(dataManager.loadList(loadContext));
        }
    }

    public Map<TsadvUser, PersonExt> findManagerByPositionGroup(UUID positionGroupId) {
        return findManagerByPositionGroup(positionGroupId, false);
    }

    @Override
    public Map<TsadvUser, PersonExt> findManagerByPositionGroup(UUID positionGroupId, boolean showAll) {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            String hierarchyId = recognitionConfig.getHierarchyId();

            if (hierarchyId == null) {
                return null;
            }

            Query query = em.createNativeQuery(
                    "WITH RECURSIVE nodes(id,parent_id, position_group_id, path, pathName, level) AS (  " +
                            "select he.id, " +
                            "       he.parent_id, " +
                            "       he.position_group_id, " +
                            "       CAST(he.position_group_id AS VARCHAR (4000)), " +
                            "       CAST(p.position_full_name_lang1 AS VARCHAR (4000)), " +
                            "       1 " +
                            "  from base_hierarchy_element he " +
                            "  join base_hierarchy h on h.id = he.hierarchy_id " +
                            "  join base_position p on p.group_id=he.position_group_id " +
                            "  and current_date between p.start_date and p.end_date " +
                            " WHERE he.delete_ts is null " +
                            "   and he.parent_id is null  " +
                            "   and he.hierarchy_id = '" + hierarchyId + "' " +
                            " UNION " +
                            " select he.id, he.parent_id, he.position_group_id, " +
                            "       CAST(s1.PATH ||'->'|| he.position_group_id AS VARCHAR(4000)), " +
                            "       CAST(s1.pathName ||'->'|| p.position_full_name_lang1 AS VARCHAR(4000)), " +
                            "       LEVEL + 1 " +
                            "  from base_hierarchy_element he " +
                            "  join nodes s1 on he.parent_id = s1.id " +
                            "  join base_position p on p.group_id=he.position_group_id " +
                            "  and current_date between p.start_date and p.end_date " +
                            ") " +
                            "SELECT " +
                            " n1.level, " +
                            "  pg.id position_group_id, " +
                            "  a.person_group_id, " +
                            "  su.id, " +
                            "  per.id, " +
                            "  per.first_name, " +
                            "  per.last_name, " +
                            "  per.first_name_latin, " +
                            "  per.last_name_latin " +
                            "  FROM nodes n " +
                            "  join base_position_group pg " +
                            "  on n.path like concat('%', concat(pg.id, '%')) " +
                            "  join nodes n1 " +
                            "  on n1.position_group_id=pg.id " +
                            "  join base_assignment a " +
                            "  on a.position_group_id=pg.id " +
                            "  and current_date between a.start_date and a.end_date " +
                            "  and a.primary_flag=true " +
                            "  join tsadv_dic_assignment_status das " +
                            "  on das.id=a.assignment_status_id " +
                            "  and das.code='ACTIVE' " +
                            "  left join sec_user su " +
                            "  on su.person_group_id=a.person_group_id " +
                            "  join base_person per " +
                            "  on per.group_id=a.person_group_id " +
                            "  and current_date between a.start_date and a.end_date " +
                            "  where n.path like " + "'%" + positionGroupId.toString() + "' " +
                            "  and pg.id <> ?1 " +
                            "  and pg.delete_ts is null " +
                            "  and a.delete_ts is null " +
                            "  and das.delete_ts is null " +
                            "  and su.delete_ts is null " +
                            "  and per.delete_ts is null  " +
                            "  order by n1.level desc " + (showAll ? "" : "limit 1"));
            query.setParameter(1, positionGroupId);

            List<Object[]> rows = query.getResultList();
            if (rows != null && !rows.isEmpty()) {
                Map<TsadvUser, PersonExt> resultMap = new HashMap<>();
                for (Object[] row : rows) {
                    UUID userId = (UUID) row[3];
                    if (userId != null) {
                        try {
                            TsadvUser userExt = em.find(TsadvUser.class, userId, View.LOCAL);
                            if (userExt != null) {
                                PersonExt personExt = metadata.create(PersonExt.class);
                                personExt.setId((UUID) row[4]);
                                personExt.setGroup(em.getReference(PersonGroupExt.class, (UUID) row[2]));
                                personExt.setFirstName((String) row[5]);
                                personExt.setLastName((String) row[6]);
                                personExt.setFirstNameLatin((String) row[7]);
                                personExt.setLastNameLatin((String) row[8]);

                                resultMap.put(userExt, personExt);

                                if (!showAll) {
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                return resultMap;
            }
        }
        return null;
    }

    @Override
    public List<TsadvUser> recursiveFindManager(UUID positionGroupId) {
        return persistence.callInTransaction(em -> {
            List<TsadvUser> userList = new ArrayList<>();
            searchPosition(userList,
                    positionGroupId,
                    em,
                    positionStructureConfig.getPositionStructureId(),
                    false);
            return userList;
        });
    }

    @Override
    public List<TsadvUser> recursiveFindManagerInActiveOne(UUID positionGroupId) {
        return persistence.callInTransaction(em -> {
            List<TsadvUser> userList = new ArrayList<>();
            searchPosition(userList,
                    positionGroupId,
                    em,
                    positionStructureConfig.getPositionStructureId(),
                    true);
            return userList;
        });
    }

    private void searchPosition(List<TsadvUser> userList, UUID positionGroupId, EntityManager em, UUID hierarchyId, boolean onlyOneInPosition) {
        Query query = em.createNativeQuery(
                String.format(
                        "SELECT " +
                                "  p.position_group_id, " +
                                "  a.person_group_id, " +
                                "  user.id " +
                                "FROM base_hierarchy_element p " +
                                "  JOIN base_hierarchy h " +
                                "    ON h.id = p.hierarchy_id " +
                                "       AND h.delete_ts IS NULL " +
                                "  LEFT JOIN base_assignment a " +
                                "    ON p.position_group_id = a.position_group_id " +
                                "       AND p.element_type = 2 " +
                                "       AND a.delete_ts IS NULL " +
                                "       AND a.primary_flag = TRUE " +
                                "       AND current_date BETWEEN a.start_date AND a.end_date " +
                                "  LEFT JOIN sec_user user " +
                                "    ON user.person_group_id = a.person_group_id " +
                                "       AND user.delete_ts IS NULL " +
                                "  JOIN base_hierarchy_element he2 " +
                                "    ON he2.parent_id = p.id " +
                                "       AND he2.hierarchy_id = h.id " +
                                "       AND he2.element_type = 2 " +
                                "       AND he2.delete_ts IS NULL " +
                                "WHERE p.delete_ts IS NULL " +
                                "      AND he2.position_group_id = ?1 " +
                                "and %s",
                        hierarchyId != null ? "h.id = ?2" : "h.primary_flag = ?2"));
        query.setParameter(1, positionGroupId);
        query.setParameter(2, hierarchyId != null ? hierarchyId : true);

        List<Object[]> rows = query.getResultList();

        if (!CollectionUtils.isEmpty(rows)) {
            for (Object[] row : rows) {
                UUID userId = (UUID) row[2];
                if (userId != null) {
                    TsadvUser userExt = em.reload(em.getReference(TsadvUser.class, userId), View.LOCAL);
                    if (userExt != null) userList.add(userExt);
                }
            }

            if (onlyOneInPosition && userList.size() > 1) {
                userList.clear();
            }

            if (userList.isEmpty()) {
                searchPosition(userList, (UUID) rows.get(0)[0], em, hierarchyId, onlyOneInPosition);
            }
        }
    }

    @Override
    public String getExperienceOnCurrentPosition(PersonGroupExt personGroup) {
        String queryString = "SELECT a from base$AssignmentExt a" +
                "              where a.personGroup.id = :personGroupId" +
                "               and a.assignmentStatus.code='ACTIVE'" +
                "               and a.primaryFlag = 'true'" +
                "               and :currentDate between a.startDate and a.endDate";
        Map<String, Object> params = new HashMap<>();
        params.put("personGroupId", personGroup.getId());
        params.put("currentDate", CommonUtils.getSystemDate());
        AssignmentExt currentAssignment = commonService.getEntity(AssignmentExt.class, queryString, params, "_local");
        if (currentAssignment != null) {
            Object[] dates = new Object[]{currentAssignment.getStartDate(), currentAssignment.getEndDate(), null};
            List<Object[]> datesList = new ArrayList<>();
            datesList.add(dates);
            return getYears(datesList);
        }
        return ("0.0");
    }

    @Override
    public boolean employeeIsExEmployee(PersonGroupExt personGroup) {
        if (personGroup == null) {
            return false;
        }
        return (personGroup.getPerson().getType().equals(commonService.getEntity(DicPersonType.class, "EXEMPLOYEE")));

    }

    @Override
    public PersonGroupExt getDirector(PersonGroupExt personGroupExt) {
        PersonGroupExt director = null;
        MyTeam parentMyTeam;
        boolean needToContinue = false;
        do {
            parentMyTeam = getPersonGroupHigher(personGroupExt);
            if (parentMyTeam != null) {
                personGroupExt = parentMyTeam.getPersonGroup();
                needToContinue = false;
            }
            if (parentMyTeam != null && parentMyTeam.getManagerFlag() != null && parentMyTeam.getManagerFlag().equals(true)) {
                director = parentMyTeam.getPersonGroup();
                needToContinue = true;
            }
            if (parentMyTeam == null || personGroupExt == null) {
                needToContinue = true;
            }
        }
        while (!needToContinue);
        return director;
    }

    private MyTeam getPersonGroupHigher(PersonGroupExt personGroupExt) {

        List<MyTeam> leaders = commonService.getEntities(MyTeam.class,
                "select e.parent from tsadv$MyTeam e where e.personGroup.id = :personGroupId",
                ParamsMap.of("personGroupId", personGroupExt.getId()), "myTeam-parent.view");
        if (leaders.size() > 0) {
            return leaders.get(0);
        }
        return null;
    }

    @Override
    public UUID getDirectorPositionByPersonGroup(UUID personGroup) {
        UUID id = null;
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            Query query = em.createNativeQuery(
                    " WITH RECURSIVE  " +
                            "     Rec(id,position_group_id,path) AS (  " +
                            "       SELECT el.id,el.position_group_id, el.position_group_id::text as path , 0::int as lvl " +
                            "       FROM base_hierarchy_element el  " +
                            "       where el.delete_ts is null  " +
                            "         and current_date between el.start_date and el.end_date  " +
                            "         and el.parent_id is null " +
                            "         and el.position_group_id is not null " +
                            "       UNION ALL  " +
                            "       SELECT el.id,el.position_group_id, Rec.path || '*' || el.position_group_id::text as path , (Rec.lvl + 1) as lvl  " +
                            "       FROM base_hierarchy_element el  " +
                            "              JOIN Rec  " +
                            "                   ON el.parent_id = Rec.id  " +
                            "       where el.delete_ts is null  " +
                            "         and current_date between el.start_date and el.end_date )  " +
                            "select pos.group_id from base_assignment ss  " +
                            "join Rec rc " +
                            " on rc.position_group_id = ss.position_group_id " +
                            "join Rec r " +
                            " on rc.path like '%'||r.position_group_id||'%' " +
                            " and rc.position_group_id != r.position_group_id " +
                            "join base_position pos " +
                            " on pos.group_id = r.position_group_id " +
                            " and pos.delete_ts is null " +
                            " and current_date between pos.start_date and pos.end_date " +
                            " and pos.manager_flag is true " +
                            "join base_assignment posSS " +
                            " on posSS.position_group_id = pos.group_id " +
                            " and posSS.delete_ts is null " +
                            " and current_date between posSS.start_date and posSS.end_date " +
                            " and posSS.primary_flag is true " +
                            "join  tsadv_dic_assignment_status ssStatus " +
                            " on ssStatus.id = posSS.assignment_status_id " +
                            " and ssStatus.delete_ts is null " +
                            " and ssStatus.code = 'ACTIVE' " +
                            "where ss.person_group_id = ?1 " +
                            " and ss.primary_flag is true " +
                            " and current_date between ss.start_date and ss.end_date " +
                            " and ss.delete_ts is null " +
                            "order by r.lvl desc " +
                            "limit 1");
            query.setParameter(1, personGroup);

            List rows = query.getResultList();
            if (!rows.isEmpty()) {
                id = (UUID) rows.get(0);
            }
        }
        return id;
    }

    @Override
    public double getPersonExperience(UUID personGroupId, String type, Date date) {
        return 0;
    }

    @Override
    public AssignmentExt getAssignmentExt(UUID personGroupId, Date date, String view) {
        List<AssignmentExt> assignmentExtList = commonService.getEntities(AssignmentExt.class,
                " select e from base$AssignmentExt e " +
                        " where e.personGroup.id = :personGroup " +
                        " and e.assignmentStatus.code <> 'TERMINATED' " +
                        " and :createDate between e.startDate and e.endDate and e.primaryFlag = 'TRUE' ",
                ParamsMap.of("personGroup", personGroupId, "createDate", date != null ? date : CommonUtils.getSystemDate()),
                view != null ? view : View.LOCAL);
        return assignmentExtList.isEmpty() ? null : assignmentExtList.get(0);
    }

    @Override
    public Salary getSalary(AssignmentGroupExt assignmentGroupExt, Date date, String view) {
        List<Salary> salaryList = commonService.getEntities(Salary.class,
                " select e from tsadv$Salary e " +
                        " where :createDate between e.startDate and e.endDate " +
                        " and e.assignmentGroup.id = :assignmentGroupId ",
                ParamsMap.of("assignmentGroupId", assignmentGroupExt.getId(), "createDate", date),
                view);
        return salaryList.isEmpty() ? null : salaryList.get(0);
    }

    @Override
    public int calculateAge(Date birthDate) {
        Date currentDate = CommonUtils.getSystemDate();
        LocalDate birthDateLocalDate = null;
        if (birthDate != null) {
            birthDateLocalDate = new java.sql.Date(birthDate.getTime()).toLocalDate();
        }
        LocalDate currentDateLocalDate = new java.sql.Date(currentDate.getTime()).toLocalDate();
        if (birthDate != null) {
            return Period.between(birthDateLocalDate, currentDateLocalDate).getYears();
        } else {
            return 0;
        }
    }

    @Override
    public String getYearCases(int years) {
        String result = "";
        String currentLanguage = ((UserSessionSource) AppBeans.get(UserSessionSource.NAME)).getLocale().getLanguage();
        if (currentLanguage.equals("ru") || currentLanguage.equals("kz")) {
            int iTens = years % 10;
            result = ((iTens == 1) ? "год" : ((iTens < 5 & iTens != 0) ? "года" : "лет"));
        }
        if (currentLanguage.equals("en")) {
            result = "years";
        }
        return result;
    }

    private List<OrgChartNode> getOrgChartNodeHierarchy(String personGroupId) {
        List<OrgChartNode> result = new ArrayList<>();
        if (personGroupId == null) {
            return result;
        }
        String query = "SELECT mt.person_group_id," +
                "       per.last_name," +
                "       per.first_name," +
                "       per.middle_name," +
                "       per.last_name_latin," +
                "       per.first_name_latin," +
                "       per.middle_name_latin," +
                "       pos.id             AS position_id," +
                "       pos.group_id       AS position_group_id," +
                "       pos.position_name_lang1," +
                "       pos.position_name_lang2," +
                "       pos.position_name_lang3," +
                "       mt.h_id," +
                "       mt.h_parent_id " +
                " from tsadv_my_team_view_v3 mt" +
                "       left join base_person per on per.group_id = mt.person_group_id" +
                "                                 and ?2 between per.start_date and per.end_date" +
                "                                 and per.delete_ts is null" +
                "       join base_position pos on pos.group_id = mt.position_group_id" +
                "                                   and ?2 between pos.start_date and pos.end_date" +
                "                                   and pos.delete_ts is null" +
                " where mt.path like ?1 ";

        try (Transaction transaction = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            List<Object[]> resultList = em.createNativeQuery(query)
                    .setParameter(1, "%" + personGroupId + "%")
                    .setParameter(2, CommonUtils.getSystemDate())
                    .getResultList();
            if (!resultList.isEmpty()) {
                for (Object[] row : resultList) {
                    OrgChartNode node = parseOrgChartNodeFromQueryResult(row);
                    if (Objects.equals(personGroupId, node.getPersonGroupId())) {
                        node.setParentId(null);
                    }
                    result.add(node);
                }
            }
        }
        return result;
    }

    /*private OrgChartNode fillOrgChartNode(String personGroupId) {
        OrgChartNode node = null;
        if (personGroupId == null) {
            return node;
        }
        String query = "SELECT  mt.id, " +
                "null group_id, " +
                "per.id person_id, " +
                "mt.person_group_id   AS person_group_id, " +
                "per.last_name, " +
                "per.first_name, " +
                "per.middle_name, " +
                "pos.id AS position_id, " +
                "pos.group_id AS position_group_id, " +
                "pos.position_name_lang1, " +
                "pos.position_name_lang2, " +
                "pos.position_name_lang3, " +
                "per.image_id " +
                "from tsadv_my_team_view mt " +
                "join base_person per on  per.group_id=mt.person_group_id " +
                "and ?2 between per.start_date and per.end_date " +
                "and per.delete_ts is null " +
                "join base_position pos on  pos.group_id=mt.position_group_id " +
                "and current_date between pos.start_date and pos.end_date " +
                "and pos.delete_ts is null " +
                "where mt.person_group_id = ?1 ";
        try (Transaction transaction = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            List<Object[]> resultList = em.createNativeQuery(query)
                    .setParameter(1, UUID.fromString(personGroupId))
                    .setParameter(2, CommonUtils.getSystemDate())
                    .getResultList();
            if (!resultList.isEmpty()) {
                Object[] row = resultList.get(0);
                node = parseOrgChartNodeFromQueryResult(row);
                node.setChildren(new ArrayList<>());
            }
        }
        if (node != null && node.getChildren() != null) {
            fillChildren(node);
        }
        return node;
    }*/

    private OrgChartNode parseOrgChartNodeFromQueryResult(Object[] row) {
        OrgChartNode node = new OrgChartNode();
        node.setPersonGroupId(row[0] != null ? row[0].toString() : null);
        try {
            String language = userSessionSource.getLocale().getLanguage();
            String lastName;

            if (node.getPersonGroupId() == null) {
                lastName = messages.getMessage("kz.uco.tsadv.core", "positionStructure.vacancy");
            } else if ("en".equals(language)) {
                lastName = row[4] != null ? (String) row[4] : "";
            } else {
                lastName = row[1] != null ? (String) row[1] : "";
            }
            String firstName;
            if ("en".equals(language)) {
                firstName = row[5] != null ? (String) row[5] : "";
            } else {
                firstName = row[2] != null ? (String) row[2] : "";
            }
            String middleName;
            if ("en".equals(language)) {
                middleName = row[6] != null ? (String) row[6] : "";
            } else {
                middleName = row[3] != null ? (String) row[3] : "";
            }
            node.setFullName((new StringBuilder(lastName)
                    .append(" ")
                    .append(firstName)
                    .append(" ")
                    .append(middleName)).toString());
            node.setPositionGroupId(row[7].toString());
            if (language.equals("ru")) {
                node.setPositionFullName((String) row[9]);
            }
            if (language.equals("kz")) {
                node.setPositionFullName((String) row[10]);
            }
            if (language.equals("en")) {
                node.setPositionFullName((String) row[11]);
            }
            String webAppUrl = AppContext.getProperty("cuba.webAppUrl");
            node.setImage(webAppUrl + "/dispatch/person_image/" + row[0]);
            node.setId(row[12].toString());
            node.setParentId(row[13].toString());
        } catch (Exception e) {
        }
        return node;
    }

    /*private void fillChildren(OrgChartNode node) {
        String query = "SELECT  mt.id, " +
                "null group_id, " +
                "per.id person_id, " +
                "mt.person_group_id   AS person_group_id, " +
                "per.last_name, " +
                "per.first_name, " +
                "per.middle_name, " +
                "pos.id AS position_id, " +
                "pos.group_id AS position_group_id, " +
                "pos.position_name_lang1, " +
                "pos.position_name_lang2, " +
                "pos.position_name_lang3, " +
                "per.image_id " +
                "from tsadv_my_team_view mt " +
                "join base_person per on  per.group_id=mt.person_group_id " +
                "and ?2 between per.start_date and per.end_date " +
                "and per.delete_ts is null " +
                "join base_position pos on  pos.group_id=mt.position_group_id " +
                "and current_date between pos.start_date and pos.end_date " +
                "and pos.delete_ts is null " +
                "where mt.parent_person_group_id = CAST(?1 AS uuid); ";
        try (Transaction transaction = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            List<Object[]> resultList = em.createNativeQuery(query)
                    .setParameter(1, node.getId())
                    .setParameter(2, CommonUtils.getSystemDate())
                    .getResultList();
            if (!resultList.isEmpty()) {
                for (Object[] row : resultList) {
                    OrgChartNode child = parseOrgChartNodeFromQueryResult(row);
                    child.setParentId(node.getId());
                    child.setChildren(new ArrayList<>());
                    node.getChildren().add(child);
                }
            }
            *//**//*
        }

        for (OrgChartNode child : node.getChildren()) {
            fillChildren(child);
            if (child.getChildren().isEmpty()) {
                break;
            }
        }


    }*/

    private static void addAllNodes(OrgChartNode node, List<OrgChartNode> listOfNodes) {
        if (node != null) {
            listOfNodes.add(node);
            List<OrgChartNode> children = node.getChildren();
            if (children != null) {
                for (OrgChartNode child : children) {
                    addAllNodes(child, listOfNodes);
                }
            }
        }
    }

    @Override
    public PersonExt getPersonByPersonGroup(UUID personGroupId, Date date, String view) {
        return persistence.callInTransaction(em -> {
            Query query = em.createQuery("select e from base$PersonExt e where e.group.id = :id " +
                    " and :date between e.startDate and e.endDate ")
                    .setParameter("date", date)
                    .setParameter("id", personGroupId);
            query.setView(PersonExt.class, view != null ? view : View.MINIMAL);
            List list = query.getResultList();
            return (PersonExt) (list.isEmpty() ? null : list.get(0));
        });
    }

    @Override
    public PersonGroupExt getPersonGroupByEmployeeNumber(String employeeNumber, String view) {
        return persistence.callInTransaction(em -> {
            Query query = em.createQuery("SELECT e.group " +
                            "FROM base$Person e " +
                            "   WHERE e.employeeNumber = ?1 " +
                            "      AND current_date BETWEEN e.startDate AND e.endDate",
                    PersonGroupExt.class);
            query.setParameter(1, employeeNumber);
            query.setView(PersonGroupExt.class, view);
            List list = query.getResultList();
            return (PersonGroupExt) (list.isEmpty() ? null : list.get(0));
        });
    }

    public boolean isLastAssignment(AssignmentExt assignmentExt) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("assignmentGroupId", assignmentExt.getGroup().getId());
        queryParams.put("currentAssignmentEndDate", assignmentExt.getEndDate());
        long count = commonService.getCount(AssignmentExt.class, "select e from base$AssignmentExt e" +
                " where e.group.id = :assignmentGroupId" +
                "   and e.startDate > :currentAssignmentEndDate", queryParams);
        return count == 0;
    }

    @Override
    public DicCostCenter getCostCenterByPersonGroup(@Nullable PersonGroupExt personGroup, Date date, String viewName) {
        if (personGroup == null) return null;
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();

            Query query = em.createNativeQuery("select coalesce(coalesce(a.cost_center_id, p.cost_center_id), o.cost_center_id)\n" +
                    "from base_assignment a\n" +
                    "         join base_position p on p.group_id = a.position_group_id\n" +
                    "    and ?2 between p.start_date and p.end_date\n" +
                    "    and p.delete_ts is null\n" +
                    "         join base_organization o on o.group_id = a.organization_group_id\n" +
                    "    and ?2 between o.start_date and a.end_date\n" +
                    "    and o.delete_ts is null\n" +
                    "where a.delete_ts is null\n" +
                    "  and ?2 between a.start_date and a.end_date\n" +
                    "  and a.primary_flag = true\n" +
                    "  and a.person_group_id = ?1");
            query.setParameter(1, personGroup.getId());
            query.setParameter(2, date);
            UUID costCenterId = (UUID) query.getFirstResult();
            DicCostCenter dicCostCenter = em.getReference(DicCostCenter.class, costCenterId);
            return dicCostCenter != null ? em.reload(dicCostCenter, viewName) : null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tx.end();
        }
        return null;
    }

    @Override
    public String getFirstLastName(PersonExt personExt, String language) {
        String resultFirstLastName = "";
        if (personExt != null) {
            resultFirstLastName = personExt.getFirstName() + " " + personExt.getLastName();
            if (language.equals("en")) {
                String firstNameLatin = personExt.getFirstNameLatin(),
                        lastNameLatin = personExt.getLastNameLatin();
                if (StringUtils.isNotBlank(firstNameLatin) && StringUtils.isNotBlank(lastNameLatin)) {
                    resultFirstLastName = firstNameLatin + " " + lastNameLatin;
                }
            }
        }
        return resultFirstLastName;
    }

    @Override
    public Map<String, String> getDicGoodsCategories() {
        Map<String, String> map = new HashMap<>();
        //noinspection unchecked
        persistence.callInTransaction(em ->
                em.createNativeQuery(String.format("select code, lang_value%d\n" +
                                "from tsadv_dic_goods_category where delete_ts is null and code is not null",
                        languageIndex(userSessionSource.getLocale().getLanguage())))
                        .getResultList())
                .forEach(o -> {
                    Object[] row = (Object[]) o;
                    map.put((String) row[0], (String) row[1]);
                });
        return map;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public OrganizationGroupExt getOrganizationGroupByPersonGroupId(@Nonnull UUID personGroupId, String viewName) {
        return persistence.callInTransaction(em ->
                em.createQuery("select e.organizationGroup from base$AssignmentExt e " +
                        "  where e.personGroup.id = :personGroupId " +
                        "   and :systemDate between e.startDate and e.endDate" +
                        "   and e.assignmentStatus.code <> 'TERMINATED' " +
                        "   and e.primaryFlag = 'TRUE' ", OrganizationGroupExt.class)
                        .setParameter("personGroupId", personGroupId)
                        .setParameter("systemDate", CommonUtils.getSystemDate())
                        .setViewName(viewName != null ? viewName : View.MINIMAL)
                        .getFirstResult());
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public DicCompany getCompanyByPersonGroupId(@Nonnull UUID personGroupId) {
        return persistence.callInTransaction(em ->
                em.createQuery("select o.company from base$AssignmentExt e " +
                        "   join e.organizationGroup o" +
                        "   where e.personGroup.id = :personGroupId" +
                        "   and :systemDate between e.startDate and e.endDate " +
                        "   and e.assignmentStatus.code <> 'TERMINATED' " +
                        "   and e.primaryFlag = 'TRUE' ", DicCompany.class)
                        .setParameter("systemDate", CommonUtils.getSystemDate())
                        .setParameter("personGroupId", personGroupId)
                        .getFirstResult());
    }

    @Override
    public List<? extends PersonGroupExt> getPersonGroupByPositionGroupId(UUID positionGroupId, String viewName) {
        return dataManager.load(PersonGroupExt.class)
                .query("select e.personGroup from base$AssignmentExt e " +
                        "   where e.positionGroup.id = :positionGroupId" +
                        "       and :systemDate between e.startDate and e.endDate " +
                        "       and e.primaryFlag = 'TRUE' " +
                        "       and e.assignmentStatus.code <> 'TERMINATED' ")
                .setParameters(ParamsMap.of("positionGroupId", positionGroupId, "systemDate", CommonUtils.getSystemDate()))
                .view(viewName != null ? viewName : View.MINIMAL)
                .list();
    }

    @Override
    public boolean hasHrRole(String dicHrCode) {
        return !AppBeans.get(OrganizationHrUserService.class).getOrganizationList(userSessionSource.getUserSession().getUser().getId(), dicHrCode).isEmpty();
    }
}