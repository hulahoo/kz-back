package kz.uco.tsadv.service;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.haulmont.bali.db.QueryRunner;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.app.FileStorageAPI;
import com.haulmont.cuba.core.app.serialization.EntitySerializationAPI;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.entity.extend.UserExt;
import kz.uco.base.entity.shared.PersonGroup;
import kz.uco.base.service.GoogleTranslateService;
import kz.uco.base.service.NotificationService;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.api.BaseResult;
import kz.uco.tsadv.api.QRCodeInt;
import kz.uco.tsadv.config.RecognitionConfig;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.global.entity.*;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.recognition.*;
import kz.uco.tsadv.modules.recognition.dictionary.DicDeliveryAddress;
import kz.uco.tsadv.modules.recognition.dictionary.DicPersonPreferenceType;
import kz.uco.tsadv.modules.recognition.dictionary.DicRecognitionType;
import kz.uco.tsadv.modules.recognition.enums.*;
import kz.uco.tsadv.modules.recognition.exceptions.CheckoutCartException;
import kz.uco.tsadv.modules.recognition.exceptions.NotEnoughPointsException;
import kz.uco.tsadv.modules.recognition.exceptions.RecognitionException;
import kz.uco.tsadv.modules.recognition.pojo.*;
import kz.uco.tsadv.modules.recognition.shop.*;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service(RecognitionService.NAME)
public class RecognitionServiceBean implements RecognitionService {

    private static final Logger logger = LoggerFactory.getLogger(RecognitionServiceBean.class);

    private static final Locale RU_LOCALE = new Locale("ru");
    private static final Locale EN_LOCALE = new Locale("en");

    public static final DateFormatSymbols RU_SYMBOLS = new DateFormatSymbols(RU_LOCALE);
    public static final DateFormatSymbols EN_SYMBOLS = new DateFormatSymbols(EN_LOCALE);

    private static final String[] RU_MONTHS = {"января", "февраля", "марта", "апреля", "мая",
            "июня", "июля", "августа", "сентября", "октября", "ноября", "декабря"};

    private static final String[] EN_MONTHS = {"january", "february", "march", "april", "may",
            "june", "july", "august", "september", "october", "november", "december"};

    static {
        RU_SYMBOLS.setMonths(RU_MONTHS);
        EN_SYMBOLS.setMonths(EN_MONTHS);
    }

    private static DateFormat birthDateFormat = new SimpleDateFormat("dd MMMM", RU_SYMBOLS);

    private static DateFormat goodsOrderDateFormat = new SimpleDateFormat("dd.MM.yyyy, HH:mm");

    private static DateFormat dateTimeFormat = new SimpleDateFormat("dd MMMM в HH:mm", RU_SYMBOLS);

    private static Gson gson = new Gson();

    @Inject
    private ViewRepository viewRepository;
    @Inject
    protected NotificationService notificationService;
    @Inject
    private MessageTools messageTools;
    @Inject
    protected BeanValidation beanValidation;
    @Inject
    protected GoogleTranslateService googleTranslateService;
    @Inject
    protected CommonService commonService;
    @Inject
    protected FileStorageAPI fileStorageAPI;
    @Inject
    protected RecognitionConfig recognitionConfig;
    @Inject
    protected EntitySerializationAPI entitySerialization;
    @Inject
    protected Messages messages;
    @Inject
    private Metadata metadata;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    protected Persistence persistence;
    @Inject
    protected QRCodeService qRCodeService;

    protected Long qrCode = 0L;

    @Override
    public String loadRecognitions(int page, long lastCount, int wallType, UUID personGroupId, String organizationGroupId, String filter) {
        int perPageCount = PageRangeInfo.RECOGNITION.getPerPageCount();

        if ("-1".equals(organizationGroupId)) organizationGroupId = null;

        if (filter == null || filter.trim().length() == 0) {
            filter = null;
        }

        PersonGroupExt currentPersonGroup = getCurrentPersonGroup();
        if (currentPersonGroup == null) {
            throw new RecognitionException(getMessage("user.person.null"));
        }

        AssignmentExt currentAssignment = getCurrentAssignment();
        if (currentAssignment == null) {
            throw new RecognitionException(getMessage("user.person.assignment.null"));
        }

        OrganizationGroupExt currentOrganizationGroup = currentAssignment.getOrganizationGroup();
        if (currentOrganizationGroup == null) {
            throw new RecognitionException(getMessage("user.person.assignment.org.null"));
        }

        boolean self = wallType == 0;

        RecognitionPageInfo recognitionPageInfo = metadata.create(RecognitionPageInfo.class);

        int languageIndex = languageIndex();
        boolean isAutomaticTranslate = isAutomaticTranslate();
        long recognitionsCount = recognitionsCount(wallType, personGroupId, organizationGroupId, filter, languageIndex);

        if (page == 1) {
            lastCount = recognitionsCount;
        }

        PageInfo pageInfo = metadata.create(PageInfo.class);
        pageInfo.setTotalRowsCount(lastCount);
        pageInfo.setPagesCount(PageInfo.getPageCount(lastCount, perPageCount));
        recognitionPageInfo.setPageInfo(pageInfo);

        long difference = 0;

        if (page != 1 && lastCount != recognitionsCount) {
            difference = recognitionsCount - lastCount;
        }

        List<RecognitionPojo> list = new ArrayList<>();
        recognitionPageInfo.setRecognitions(list);

        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            String teamPositionPath = getPositionPath(currentAssignment.getPositionGroup().getId().toString());

            Query query = em.createNativeQuery(String.format("select * from (select " +
                            "pa.id pa_id," +
                            "%s pa_comment," +
                            "pat.id pa_type_id," +
                            "pat.lang_value%d pa_type_name," +
                            "pat.code pa_type_code," +
                            "pat.coins pa_type_coins," +
                            "pa.create_ts pa_create_ts," +
                            "aupg.id author_id," +
                            "aup.id author_person_id, " +
                            "%s as author_name, " +
                            "rupg.id receiver_id," +
                            "rup.id receiver_person_id, " +
                            "%s as receiver_name, " +
                            "(select count(*) from TSADV_RECOGNITION_COMMENT comm where comm.delete_ts is null and comm.recognition_id = pa.id) comment_count," +
                            "(select count(*) from TSADV_RECOGNITION_LIKE comm where comm.delete_ts is null and comm.recognition_id = pa.id) like_count, " +
                            "(select count(*) from TSADV_RECOGNITION_LIKE comm where comm.delete_ts is null and comm.recognition_id = pa.id and comm.person_group_id = '%s') current_like," +
                            "pa.comment_," +
                            "pa.comment_en, " +
                            "pa.comment_ru, " +
                            "aup.employee_number, " +
                            "rup.employee_number " +
                            "from TSADV_RECOGNITION pa " +
                            "join TSADV_DIC_RECOGNITION_TYPE pat " +
                            "   on pat.id = pa.RECOGNITION_TYPE_ID " +
                            "join BASE_PERSON_GROUP aupg " +
                            "   on aupg.id = pa.author_id " +
                            "left join BASE_PERSON aup " +
                            "   on aup.group_id = aupg.id " +
                            "join BASE_PERSON_GROUP rupg " +
                            "   on rupg.id = pa.receiver_id " +
                            "left join BASE_PERSON rup " +
                            "   on rup.group_id = rupg.id " +
                            "%s " +
                            "where pa.delete_ts is null " +
                            "and pat.delete_ts is null " +
                            "and ?1 BETWEEN aup.start_date AND aup.end_date AND aup.delete_ts IS NULL " +
                            "and ?1 BETWEEN rup.start_date AND rup.end_date AND rup.delete_ts IS NULL " +
                            "%s %s ) t %s order by t.pa_create_ts desc",
                    isAutomaticTranslate ? (languageIndex == 3 ? "pa.comment_en" : "pa.comment_ru") : "pa.comment_",
                    languageIndex,
                    languageIndex == 3 ? "aup.first_name_latin||' '||aup.last_name_latin" : "aup.first_name||' '||aup.last_name",
                    languageIndex == 3 ? "rup.first_name_latin||' '||rup.last_name_latin" : "rup.first_name||' '||rup.last_name",
                    currentPersonGroup.getId().toString(),
                    organizationGroupId != null ?
                            "join base_assignment a " +
                                    "ON a.person_group_id = rupg.id " : "",
                    (wallType != -1 && personGroupId != null) ? "and rupg.id = ?2 " : "",
                    organizationGroupId != null ?
                            String.format("and ?1 between a.start_date and a.end_date " +
                                    "and a.organization_group_id in (%s)", getOrganizationPath(organizationGroupId, null)) : "",
                    filter != null ? "where lower(t.pa_comment) like ?3 " +
                            "or lower(t.author_name) like ?3 " +
                            "or lower(t.receiver_name) like ?3 " : ""));

            query.setParameter(1, CommonUtils.getSystemDate());

            if (personGroupId != null) {
                query.setParameter(2, personGroupId);
            }

            if (organizationGroupId != null) {
                query.setParameter(2, UUID.fromString(organizationGroupId));
            }

            if (filter != null) {
                query.setParameter(3, "%" + filter.toLowerCase() + "%");
            }

            query.setFirstResult((int) difference + ((page - 1) * perPageCount));
            query.setMaxResults(perPageCount);

            List<Object[]> resultList = query.getResultList();

            for (Object[] row : resultList) {
                long commentCount = (long) row[13];
                long commentPages;

                if (commentCount > 2) {
                    commentPages = PageInfo.getPageCount(commentCount, PageRangeInfo.COMMENT.getPerPageCount()) + 1;
                } else {
                    commentPages = 1;
                }

                long likeCount = (Long) row[14];

                RecognitionPojo recognitionPojo = metadata.create(RecognitionPojo.class);
                recognitionPojo.setId((UUID) row[0]);
                recognitionPojo.setText((String) row[1]);
                recognitionPojo.setCommentPages(commentPages);
                recognitionPojo.setCommentCount(commentCount);
                recognitionPojo.setLikeCount(likeCount);
                recognitionPojo.setCurrentLike((Long) row[15] > 0 ? 1 : 0);

                String originalText = (String) row[16];
                String commentRu = (String) row[18];
                String commentEn = (String) row[17];

                boolean translated = !recognitionPojo.getText().equals(originalText);
                recognitionPojo.setTranslated(translated ? 1 : 0);

                String reverseText = originalText;
                if (translated) {
                    if (recognitionPojo.getText().equals(commentEn)) {
                        reverseText = commentRu;
                    } else {
                        reverseText = commentEn;
                    }
                } else {
                    if (reverseText.equals(commentEn)) {
                        reverseText = commentRu;
                    } else {
                        reverseText = commentEn;
                    }
                }
                recognitionPojo.setReverseText(reverseText);

                RecognitionTypePojo typePojo = metadata.create(RecognitionTypePojo.class);
                typePojo.setShowEmpty(false);
                typePojo.setId((UUID) row[2]);
                typePojo.setName((String) row[3]);
                typePojo.setCode((String) row[4]);
                typePojo.setCoins((Long) row[5]);
                typePojo.setImage("");
                recognitionPojo.setType(typePojo);

                recognitionPojo.setSay(self ? getMessage("rcg.say.self") : getMessage("rcg.say"));
                recognitionPojo.setCreateDate(getDateTimeFormatter().format((Date) row[6]));

                PersonPojo authorPojo = metadata.create(PersonPojo.class);
                authorPojo.setId((UUID) row[7]);
                authorPojo.setPersonId(row[8].toString());
                authorPojo.setName((String) row[9]);
                authorPojo.setImage("");
                authorPojo.setEmployeeNumber(row[19].toString());
                recognitionPojo.setSender(authorPojo);

                PersonPojo receiverPojo = metadata.create(PersonPojo.class);
                receiverPojo.setId((UUID) row[10]);
                receiverPojo.setPersonId(row[11].toString());
                receiverPojo.setName((String) row[12]);
                receiverPojo.setImage("");
                receiverPojo.setEmployeeNumber(row[20].toString());
                recognitionPojo.setReceiver(receiverPojo);

                recognitionPojo.setLastComments(loadCommentsByRecognition(recognitionPojo.getId().toString(), 1));
                recognitionPojo.setQualities(loadQualities(recognitionPojo.getId(), languageIndex, em));

                PersonPojo teamLiker = findMyTeamLiker(recognitionPojo.getId(), teamPositionPath, currentPersonGroup.getId(), languageIndex);
                if (teamLiker != null) {
                    if (!teamLiker.getId().equals(currentPersonGroup.getId())) {
                        recognitionPojo.setTeamLiker(teamLiker);

                        if (likeCount > 0) {
                            recognitionPojo.setLikeCount(likeCount - 1);
                        }
                    }
                }

                list.add(recognitionPojo);
            }

            tx.end();
        }
        return entitySerialization.toJson(recognitionPageInfo, null);
    }

    protected PersonGroupExt getCurrentPersonGroup() {
        return userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP);
    }

    private AssignmentExt getCurrentAssignment() {
        return userSessionSource.getUserSession().getAttribute("assignment");
    }

    protected DateFormat getDateTimeFormatter() {
        return getDateTimeFormatter(languageIndex());
    }

    @Override
    public DateFormat getDateTimeFormatter(int languageIndex) {
        DateFormat dateFormat = new SimpleDateFormat("dd MMMM 'at' HH:mm", EN_SYMBOLS);
        if (languageIndex == 1) {
            dateFormat = new SimpleDateFormat("dd MMMM в HH:mm", RU_SYMBOLS);
        }
        return dateFormat;
    }

    @Override
    public String loadBanners(String page) {
        List<String> images = new ArrayList<>();
        if (StringUtils.isNotBlank(page)) {
            persistence.runInTransaction(em -> {
                Query query = em.createNativeQuery(String.format(
                        "select " +
                                "t.IMAGE_LANG%d_ID " +
                                "from TSADV_BANNER t " +
                                "where t.ACTIVE = true and t.page = ?1 and t.delete_ts is null ",
                        languageIndex()));

                query.setParameter(1, page);

                List<Object> rows = query.getResultList();
                if (rows != null && !rows.isEmpty()) {
                    for (Object row : rows) {
                        images.add(row.toString());
                    }
                }
            });
        }
        return gson.toJson(images);
    }

    @Override
    public FileDescriptor getBanner(UUID id) {
        return dataManager.load(LoadContext.create(FileDescriptor.class).setView(View.MINIMAL).setId(id));
    }

    @Override
    public RecognitionProfileSetting loadProfileSettings() {
        PersonGroupExt personGroupExt = getCurrentPersonGroup();

        RecognitionProfileSetting recognitionProfileSetting = findProfileSettings(personGroupExt);
        if (recognitionProfileSetting == null) {
            recognitionProfileSetting = metadata.create(RecognitionProfileSetting.class);
            recognitionProfileSetting.setPersonGroup(personGroupExt);
            recognitionProfileSetting.setAutomaticTranslate(true);
            recognitionProfileSetting = dataManager.commit(recognitionProfileSetting, "recognitionProfileSetting.edit");

            logger.info(String.format("Created recognition profile setting for %s", personGroupExt.getId().toString()));
        }
        return recognitionProfileSetting;
    }

    @Override
    public RecognitionProfileSetting findProfileSettings(PersonGroupExt personGroupExt) {
        LoadContext<RecognitionProfileSetting> loadContext = LoadContext.create(RecognitionProfileSetting.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$RecognitionProfileSetting e " +
                        "where e.personGroup.id = :pgId");
        query.setParameter("pgId", personGroupExt.getId());
        loadContext.setQuery(query);
        loadContext.setView("recognitionProfileSetting.edit");
        return dataManager.load(loadContext);
    }

    @Override
    public String getOrganizationPath(String organizationGroupId, UUID hierarchyId) {
        List<String> list = new ArrayList<>();
        list.add(organizationGroupId);

        Object result = persistence.callInTransaction(new Transaction.Callable<Object>() {
            @Override
            public Object call(EntityManager em) {

                String hierarchyFilter = "h.primary_flag = True";
                if (hierarchyId != null) {
                    hierarchyFilter = String.format("h.id = '%s'", hierarchyId.toString());
                }

                Query query = em.createNativeQuery(
                        "WITH RECURSIVE he(lvl, id, org_group_id) AS ( " +
                                "  SELECT " +
                                "    1, " +
                                "    he.id, " +
                                "    he.organization_group_id " +
                                "  FROM base_hierarchy h " +
                                "       join base_hierarchy_element he " +
                                "       on h.id = he.HIERARCHY_ID " +
                                "  WHERE h.delete_ts is null " +
                                "       and " + hierarchyFilter + " " +
                                "       and he.delete_ts is null " +
                                "       and current_date between he.start_date and he.end_date " +
                                "       and he.organization_group_id = ?1 " +
                                "  UNION ALL " +
                                "  SELECT " +
                                "    lvl + 1, " +
                                "    g.id, " +
                                "    g.organization_group_id " +
                                "  FROM base_hierarchy h " +
                                "       join base_hierarchy_element g " +
                                "       on h.id = g.hierarchy_id " +
                                "       join he sg " +
                                "       on g.parent_id = sg.id AND g.element_type = 1 " +
                                "  WHERE h.delete_ts is null " +
                                "       and h.primary_flag = True " +
                                "       and g.delete_ts is null " +
                                "       and current_date between g.start_date and g.end_date " +
                                ") " +
                                "SELECT string_agg(org_group_id::text,'*') " +
                                "FROM he heh " +
                                "  JOIN base_organization org " +
                                "    ON org.group_id = heh.org_group_id ");
                //TODO: WHERE heh.lvl > 1

                query.setParameter(1, UUID.fromString(organizationGroupId));

                return query.getSingleResult();
            }
        });

        if (result != null) {
            list.addAll(Arrays.asList(result.toString().split("\\*")));
        }

        String organizationPath = list.stream().map(s -> "'" + s + "'").collect(Collectors.joining(","));
        logger.debug("organizationPath:{}", organizationPath);

        return organizationPath;
    }

    @Override
    public String getPositionPath(String positionGroupId) {
        String hierarchyId = recognitionConfig.getHierarchyId();

        List<String> list = new ArrayList<>();
        //list.add(positionGroupId);

        Object result = persistence.callInTransaction(new Transaction.Callable<Object>() {
            @Override
            public Object call(EntityManager em) {
                String hierarchyFilter = "AND h.primary_flag = TRUE";
                if (!StringUtils.isBlank(hierarchyId)) {
                    hierarchyFilter = "and h.id = ?2";
                }
                Query query = em.createNativeQuery(String.format(
                        "WITH RECURSIVE he(lvl, id, pos_group_id) AS ( " +
                                "  SELECT " +
                                "    1, " +
                                "    he.id, " +
                                "    he.position_group_id " +
                                "  FROM base_hierarchy h " +
                                "    JOIN base_hierarchy_element he " +
                                "      ON h.id = he.HIERARCHY_ID " +
                                "  WHERE h.delete_ts IS NULL " +
                                "        %s " +
                                "        AND he.delete_ts IS NULL " +
                                "        AND current_date BETWEEN he.start_date AND he.end_date " +
                                "        AND he.position_group_id = ?1 " +
                                "  UNION ALL " +
                                "  SELECT " +
                                "    lvl + 1, " +
                                "    g.id, " +
                                "    g.position_group_id " +
                                "  FROM base_hierarchy h " +
                                "    JOIN base_hierarchy_element g " +
                                "      ON h.id = g.hierarchy_id " +
                                "    JOIN he sg " +
                                "      ON g.parent_id = sg.id AND g.element_type = 2 " +
                                "  WHERE h.delete_ts IS NULL " +
                                "        %s " +
                                "        AND g.delete_ts IS NULL " +
                                "        AND current_date BETWEEN g.start_date AND g.end_date " +
                                ") " +
                                "SELECT string_agg(pos_group_id :: TEXT, '*') " +
                                "FROM he heh",
                        hierarchyFilter,
                        hierarchyFilter));

                query.setParameter(1, UUID.fromString(positionGroupId));

                if (!StringUtils.isBlank(hierarchyId)) {
                    query.setParameter(2, UUID.fromString(hierarchyId));
                }

                return query.getSingleResult();
            }
        });

        if (result != null) {
            list.addAll(Arrays.asList(result.toString().split("\\*")));
        }

        if (list.isEmpty()) return null;

        String positionPath = list.stream().map(s -> "'" + s + "'").collect(Collectors.joining(","));
        logger.debug("positionPath:{}", positionPath);

        return positionPath;
    }

    @Override
    public Map<UserExt, PersonExt> findManagerByPositionGroup(UUID positionGroupId, UUID receiverPersonGroupId) {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            String hierarchyFilter = "h.primary_flag = True";
            String hierarchyId = recognitionConfig.getHierarchyId();
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
                            "   n1.level, " +
                            "   pg.id position_group_id, " +
                            "   a.person_group_id, " +
                            "   u.user_ext_id, " +
                            "   per.id, " +
                            "   per.first_name, " +
                            "   per.last_name, " +
                            "   per.first_name_latin, " +
                            "   per.last_name_latin " +
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
                            "join tsadv_user_ext_person_group u " +
                            "   on u.person_group_id=a.person_group_id " +
                            "join sec_user su " +
                            "   on su.id = u.user_ext_id " +
                            "join base_person per " +
                            "   on per.group_id=a.person_group_id " +
                            "   and current_date between a.start_date and a.end_date " +
                            "where n.path like ?1 " +
                            "   and a.position_group_id <> ?2 " +
                            "order by n1.level desc");
            query.setParameter(1, "%" + positionGroupId.toString());
            query.setParameter(2, positionGroupId);

            List<Object[]> rows = query.getResultList();
            if (rows != null && !rows.isEmpty()) {
                for (Object[] row : rows) {
                    UUID userId = (UUID) row[3];
                    if (userId != null) {
                        try {
                            UserExt userExt = em.find(UserExt.class, userId, View.LOCAL);
                            if (userExt != null) {
                                PersonExt personExt = metadata.create(PersonExt.class);
                                personExt.setId((UUID) row[4]);
                                personExt.setGroup(em.getReference(PersonGroupExt.class, (UUID) row[2]));
                                personExt.setFirstName((String) row[5]);
                                personExt.setLastName((String) row[6]);
                                personExt.setFirstNameLatin((String) row[7]);
                                personExt.setLastNameLatin((String) row[8]);

                                return Collections.singletonMap(userExt, personExt);
                            }
                        } catch (Exception ex) {
                            logger.warn(String.format("Manager User by ID: %s not found!", userId.toString()));
                        }
                    }
                }
            }
        }
        return null;
    }

    protected int languageIndex() {
        return languageIndex(userSessionSource.getLocale().getLanguage().toLowerCase());
    }

    private int languageIndex(String language) {
        int languageIndex = 1;
        switch (language) {
            case "ru": {
                languageIndex = 1;
                break;
            }
            case "kz": {
                languageIndex = 2;
                break;
            }
            case "en": {
                languageIndex = 3;
                break;
            }
        }
        return languageIndex;
    }

    private List<QualityPojo> loadQualities(UUID recognitionId, int languageIndex, EntityManager em) {
        List<QualityPojo> qualities = new LinkedList<>();
        Query query = em.createNativeQuery(String.format(
                "SELECT  " +
                        "  q.lang_value%d," +
                        "  q.full_lang_value%d " +
                        "FROM tsadv_recognition_quality rq " +
                        "  JOIN tsadv_dic_quality q " +
                        "    ON q.id = rq.quality_id " +
                        "where rq.delete_ts is null " +
                        "and q.delete_ts is null " +
                        "and rq.recognition_id = ?1",
                languageIndex,
                languageIndex));

        query.setParameter(1, recognitionId);

        List<Object[]> rows = query.getResultList();
        if (rows != null && !rows.isEmpty()) {
            for (Object[] row : rows) {
                QualityPojo qualityPojo = metadata.create(QualityPojo.class);
                qualityPojo.setName((String) row[0]);

                String description = (String) row[1];
                qualityPojo.setDescription(description != null ? description : "");
                qualities.add(qualityPojo);
            }
        }
        return qualities;
    }

    @Override
    public List<QualityPojo> loadQualities(int languageIndex) {
        List<QualityPojo> qualities = new ArrayList<>();
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(String.format(
                    "SELECT  " +
                            "  q.id," +
                            "  q.lang_value%d," +
                            "  q.full_lang_value%d " +
                            "FROM tsadv_dic_quality q " +
                            "where q.delete_ts is null ",
                    languageIndex,
                    languageIndex));

            List<Object[]> rows = query.getResultList();
            if (rows != null && !rows.isEmpty()) {
                for (Object[] row : rows) {
                    QualityPojo qualityPojo = metadata.create(QualityPojo.class);
                    qualityPojo.setId((UUID) row[0]);
                    qualityPojo.setName((String) row[1]);

                    String description = (String) row[2];
                    qualityPojo.setDescription(description != null ? description : "");
                    qualities.add(qualityPojo);
                }
            }
        }
        return qualities;
    }

    @Override
    public List<QualityPojo> loadQualities(UUID recognitionId, int languageIndex) {
        return persistence.callInTransaction(em ->
                loadQualities(recognitionId, languageIndex, persistence.getEntityManager()));
    }

    @Override
    public String loadComments(String recognitionId, int page) {
        return entitySerialization.toJson(loadCommentsByRecognition(recognitionId, page), null);
    }

    @Override
    public Long recognitionsCount(UUID personGroupId) {
        LoadContext<Recognition> loadContext = LoadContext.create(Recognition.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$Recognition e " +
                        "where e.receiver.id = :pgId");
        query.setParameter("pgId", personGroupId);
        loadContext.setQuery(query);
        return dataManager.getCount(loadContext);
    }

    @Override
    public List<String> validateRecognition(Recognition recognition) {
        List<String> errors = new LinkedList<>();

        Validator validator = beanValidation.getValidator();
        Set<ConstraintViolation<Recognition>> violations = validator.validate(recognition);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<Recognition> rcgCv : violations) {
                String propertyPath = rcgCv.getPropertyPath().toString();
                if (!propertyPath.matches("commentEn|commentRu")) {
                    ConstraintDescriptorImpl constraintDescriptor = (ConstraintDescriptorImpl) rcgCv.getConstraintDescriptor();
                    Class annotationType = constraintDescriptor.getAnnotationType();
                    if (annotationType != null) {
                        if (annotationType.equals(Length.class)) {
                            errors.add(rcgCv.getMessage());
                        } else {
                            errors.add(messageTools.getDefaultRequiredMessage(recognition.getMetaClass(), propertyPath));
                        }
                    }
                }
            }
        }

        if (errors.isEmpty()) {
            if (recognition.getAuthor().equals(recognition.getReceiver())) {
                errors.add(getMessage("rcg.your.self"));
            }

            if (!accessRecognition(recognition.getAuthor().getId(), recognition.getReceiver().getId())) {
                errors.add(getMessage("max.recognition.per.month"));
            }
        }
        return errors;
    }

    @Override
    public void validatePoints(Recognition recognition, Long personPoints) {
        Long recognitionTypeCoins = recognition.getRecognitionType().getCoins();
        if (recognitionTypeCoins == null) {
            throw new RecognitionException(getMessage("recognition.coins.null"));
        }

        if (personPoints == 0 || personPoints < recognitionTypeCoins) {
            throw new NotEnoughPointsException(getMessage("wallet.points.less"));
        }
    }

    @Override
    public void validatePoints(Recognition recognition, PersonPoint personPoint) {
        if (personPoint == null) {
            throw new RecognitionException(getMessage("author.wallet.points.null"));
        }
        validatePoints(recognition, personPoint.getPoints());
    }

    @Override
    public void removeRecognition(UUID recognitionId, boolean checkAuthor, boolean softDeletion) {
        persistence.runInTransaction(em -> {
            em.setSoftDeletion(softDeletion);
            em.remove(em.getReference(Recognition.class, recognitionId));
        });
    }

    @Override
    public boolean accessRecognition(UUID authorPersonGroupId, UUID receiverPersonGroupId) {
        LoadContext<Recognition> loadContext = LoadContext.create(Recognition.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$Recognition e " +
                        "where e.author.id = :authorPgId " +
                        "and e.receiver.id = :receiverPgId " +
                        "and EXTRACT(MONTH from e.recognitionDate) = EXTRACT(MONTH from current_date)");
        query.setParameter("authorPgId", authorPersonGroupId);
        query.setParameter("receiverPgId", receiverPersonGroupId);
        loadContext.setQuery(query);
        return dataManager.getCount(loadContext) < recognitionConfig.getThanksCountInMonth();
    }

    private void checkDaysBetweenThanks(UUID authorPersonGroupId, UUID receiverPersonGroupId) {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(
                    "SELECT current_date - max(rcg.recognition_date) " +
                            "FROM tsadv_recognition rcg " +
                            "where   rcg.delete_ts IS NULL " +
                            "        AND rcg.author_id = ?1 " +
                            "        AND rcg.receiver_id = ?2");
            query.setParameter(1, authorPersonGroupId);
            query.setParameter(2, receiverPersonGroupId);
            Object lastThanksDateDays = query.getFirstResult();
            if (lastThanksDateDays != null) {
                //TODO:
            }
            tx.end();
        }
    }

    @Override
    public Long recognitionsCount(int wallType, UUID personGroupId, String organizationGroupId, String filter, int languageIndex) {
        return persistence.callInTransaction(new Transaction.Callable<Long>() {
            @Override
            public Long call(EntityManager em) {
                Query query = em.createNativeQuery(String.format("select count(*) from (" +
                                "select " +
                                "%s pa_comment," +
                                "%s as receiver_name, " +
                                "%s as author_name " +
                                "from TSADV_RECOGNITION pa " +
                                "join TSADV_DIC_RECOGNITION_TYPE pat " +
                                "   on pat.id = pa.RECOGNITION_TYPE_ID " +
                                "join BASE_PERSON_GROUP aupg " +
                                "   on aupg.id = pa.author_id " +
                                "left join BASE_PERSON aup " +
                                "   on aup.group_id = aupg.id " +
                                "join BASE_PERSON_GROUP rupg " +
                                "   on rupg.id = pa.receiver_id " +
                                "left join BASE_PERSON rup " +
                                "   on rup.group_id = rupg.id " +
                                "%s " +
                                "where pa.delete_ts is null " +
                                "and pat.delete_ts is null " +
                                "and ?1 BETWEEN aup.start_date AND aup.end_date AND aup.delete_ts IS NULL " +
                                "and ?1 BETWEEN rup.start_date AND rup.end_date AND rup.delete_ts IS NULL " +
                                "%s %s ) t %s ",
                        languageIndex == 3 ? "pa.comment_en" : "pa.comment_ru",
                        languageIndex == 3 ? "rup.first_name_latin||' '||rup.last_name_latin" : "rup.first_name||' '||rup.last_name",
                        languageIndex == 3 ? "aup.first_name_latin||' '||aup.last_name_latin" : "aup.first_name||' '||aup.last_name",
                        organizationGroupId != null ?
                                "join base_assignment a " +
                                        "ON a.person_group_id = rupg.id " : "",
                        (wallType != -1 && personGroupId != null) ? "and rupg.id = ?2 " : "",
                        organizationGroupId != null ?
                                String.format("and ?1 between a.start_date and a.end_date " +
                                        "and a.organization_group_id in (%s)", getOrganizationPath(organizationGroupId, null)) : "",
                        filter != null ? "where lower(t.pa_comment) like ?3 " +
                                "or lower(t.author_name) like ?3 " +
                                "or lower(t.receiver_name) like ?3 " : ""));

                query.setParameter(1, CommonUtils.getSystemDate());

                if (personGroupId != null) {
                    query.setParameter(2, personGroupId);
                }

                if (organizationGroupId != null) {
                    query.setParameter(2, UUID.fromString(organizationGroupId));
                }

                if (filter != null) {
                    query.setParameter(3, "%" + filter.toLowerCase() + "%");
                }

                return (Long) query.getFirstResult();
            }
        });
    }

    @Override
    public Long logsCount() {
        LoadContext<PersonCoinLog> loadContext = LoadContext.create(PersonCoinLog.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e " +
                        "from tsadv$PersonCoinLog e " +
                        "where e.personGroup.id = :pgId");
        query.setParameter("pgId", getCurrentPersonGroup().getId());
        loadContext.setQuery(query);
        return dataManager.getCount(loadContext);
    }

    @Override
    public Long teamProfilesCount() {
        AssignmentExt assignment = getCurrentAssignment();
        if (assignment != null) {
            PositionGroupExt positionGroup = assignment.getPositionGroup();
            if (positionGroup != null) {
                String hierarchyId = recognitionConfig.getHierarchyId();
                String hierarchyFilter = "AND h.primary_flag = TRUE";
                if (StringUtils.isNotBlank(hierarchyId)) {
                    hierarchyFilter = String.format("and h.id = '%s'", hierarchyId);
                }
                return profilesCount(hierarchyFilter, positionGroup.getId());
            }
        }
        return 0L;
    }

    @Override
    public String loadWishList(int page, long lastCount, UUID personGroupId) {
        int perPageCount = PageRangeInfo.GOODS.getPerPageCount();
        GoodsPageInfo goodsPageInfo = metadata.create(GoodsPageInfo.class);

        long goodsCount = wishListCount(personGroupId);
        if (page == 1) {
            lastCount = goodsCount;
        }

        PageInfo pageInfo = metadata.create(PageInfo.class);
        pageInfo.setTotalRowsCount(lastCount);
        pageInfo.setPagesCount(PageInfo.getPageCount(lastCount, perPageCount));
        goodsPageInfo.setPageInfo(pageInfo);

        long difference = 0;

        if (page != 1 && lastCount != goodsCount) {
            difference = goodsCount - lastCount;
        }

        List<GoodsPojo> list = new ArrayList<>();
        goodsPageInfo.setGoods(list);

        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            int languageIndex = languageIndex();

            Query query = em.createNativeQuery(String.format(
                    "SELECT " +
                            "  g.id, " +
                            "  g.name_lang%d, " +
                            "  g.description_lang%d, " +
                            "  g.price, " +
                            "  gc.lang_value%d " +
                            "FROM TSADV_GOODS_WISH_LIST wl " +
                            "join tsadv_goods g " +
                            "   on g.id = wl.goods_id " +
                            "JOIN tsadv_dic_goods_category gc " +
                            "   ON gc.id = g.category_id " +
                            "WHERE wl.delete_ts is null " +
                            "and g.delete_ts IS NULL " +
                            "and wl.person_group_id = ?1",
                    languageIndex,
                    languageIndex,
                    languageIndex));

            query.setParameter(1, personGroupId);

            query.setFirstResult((int) difference + ((page - 1) * perPageCount));
            query.setMaxResults(perPageCount);

            List<Object[]> resultList = query.getResultList();
            if (resultList != null && !resultList.isEmpty()) {
                for (Object[] row : resultList) {
                    GoodsPojo goodsPojo = metadata.create(GoodsPojo.class);
                    goodsPojo.setId((UUID) row[0]);
                    goodsPojo.setName((String) row[1]);
                    goodsPojo.setDescription((String) row[2]);
                    goodsPojo.setPrice((Double) row[3]);
                    goodsPojo.setCategory((String) row[4]);
                    list.add(goodsPojo);
                }
            }

            tx.end();
        }
        return entitySerialization.toJson(goodsPageInfo, null);
    }

    @Override
    public boolean checkProfileInTeam(UUID personGroupId) {
        return persistence.callInTransaction(new Transaction.Callable<Boolean>() {
            @Override
            public Boolean call(EntityManager em) {
                String hierarchyId = recognitionConfig.getHierarchyId();
                String hierarchyFilter = "AND h.primary_flag = TRUE";
                if (StringUtils.isNotBlank(hierarchyId)) {
                    hierarchyFilter = String.format("and h.id = '%s'", hierarchyId);
                }

                AssignmentExt assignment = getCurrentAssignment();
                if (assignment != null) {
                    PositionGroupExt positionGroup = assignment.getPositionGroup();
                    if (positionGroup != null) {
                        Query query = em.createNativeQuery("SELECT " +
                                "count(*) " +
                                "FROM base_assignment a " +
                                "JOIN base_person p " +
                                "   ON p.group_id = a.person_group_id " +
                                "JOIN base_organization org " +
                                "   ON org.group_id = a.organization_group_id " +
                                "WHERE a.delete_ts is null and ?1 between a.start_date and a.end_date " +
                                "and p.delete_ts is null and ?1 between p.start_date and p.end_date " +
                                "and org.delete_ts is null and ?1 between org.start_date and org.end_date " +
                                "and p.group_id = ?2 " +
                                "and a.position_group_id IN (" +
                                "   select " +
                                "       child_h.position_group_id " +
                                "   from base_hierarchy_element he " +
                                "   join base_hierarchy_element child_h " +
                                "       on child_h.parent_id = he.id " +
                                "   join base_hierarchy h " +
                                "       on h.id = he.hierarchy_id and h.delete_ts is null " +
                                "   where he.position_group_id = ?3 " +
                                hierarchyFilter +
                                "  )");

                        query.setParameter(1, CommonUtils.getSystemDate());
                        query.setParameter(2, personGroupId);
                        query.setParameter(3, positionGroup.getId());

                        return (Long) query.getSingleResult() > 0;
                    }
                }
                return false;
            }
        });
    }

    private Long profilesCount(String hierarchyFilter, UUID positionGroupId) {
        try (Transaction tx = persistence.getTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery("SELECT " +
                    "count(*) " +
                    "FROM base_assignment a " +
                    "JOIN base_person p " +
                    "   ON p.group_id = a.person_group_id " +
                    "JOIN base_organization org " +
                    "   ON org.group_id = a.organization_group_id " +
                    "WHERE a.delete_ts is null and ?1 between a.start_date and a.end_date " +
                    "and p.delete_ts is null and ?1 between p.start_date and p.end_date " +
                    "and org.delete_ts is null and ?1 between org.start_date and org.end_date " +
                    "and a.position_group_id IN (" +
                    "   select " +
                    "       child_h.position_group_id " +
                    "   from base_hierarchy_element he " +
                    "   join base_hierarchy_element child_h " +
                    "       on child_h.parent_id = he.id " +
                    "   join base_hierarchy h " +
                    "       on h.id = he.hierarchy_id and h.delete_ts is null " +
                    "   where he.position_group_id = ?2 " +
                    hierarchyFilter +
                    "  )");

            query.setParameter(1, CommonUtils.getSystemDate());
            query.setParameter(2, positionGroupId);

            Object result = query.getSingleResult();
            if (result != null) {
                return (Long) result;
            }
        }
        return 0L;
    }

    @Override
    public String sendComment(RecognitionCommentPojo commentPojo) {
        CommentRestResult restResult = new CommentRestResult();
        try {
            PersonGroupExt personGroupExt = getCurrentPersonGroup();
            if (personGroupExt == null) {
                throw new RecognitionException(getMessage("user.person.null"));
            }

            UUID newCommentId = UUID.randomUUID();
            String originalText = commentPojo.getText();

            if (StringUtils.isBlank(originalText)) {
                throw new RecognitionException(getMessage("rcg.comment.null"));
            }

            RecognitionComment recognitionComment = metadata.create(RecognitionComment.class);
            recognitionComment.setId(newCommentId);
            recognitionComment.setText(originalText);
            recognitionComment.setAuthor(personGroupExt);

            try {
                recognitionComment.setTextEn(googleTranslateService.translate(originalText, "en"));
                recognitionComment.setTextRu(googleTranslateService.translate(originalText, "ru"));
            } catch (Exception ex) {
                recognitionComment.setTextEn(originalText);
                recognitionComment.setTextRu(originalText);
                logger.error(ex.getMessage());
            }

            UUID recognitionCommentId = commentPojo.getParentCommentId();
            if (recognitionCommentId != null) {
                RecognitionComment parentComment = dataManager.load(
                        LoadContext.create(RecognitionComment.class)
                                .setId(recognitionCommentId));
                if (parentComment == null) {
                    throw new RecognitionException(getMessage("parent.comment.not.found"));
                }

                recognitionComment.setParentComment(parentComment);
            }

            UUID recognitionId = commentPojo.getRecognitionId();
            if (recognitionId == null) {
                throw new RecognitionException(getMessage("rcg.id.null"));
            }

            Recognition recognition = dataManager.load(
                    LoadContext.create(Recognition.class)
                            .setId(recognitionId));

            if (recognition == null) {
                throw new RecognitionException(getMessage("rcg.by.id.null"));
            }

            recognitionComment.setRecognition(recognition);

            recognitionComment = dataManager.commit(recognitionComment, "recognitionComment.edit");

            restResult.setSuccess(true);
            restResult.setCommentPojo(parseToCommentPojo(recognitionComment, languageIndex(), false));
        } catch (Exception ex) {
            restResult.setSuccess(false);
            restResult.setMessage(ex.getMessage());
            ex.printStackTrace();
        }

        return gson.toJson(restResult);
    }

    @Override
    public String loadCategories() {
        List<GoodsCategoryPojo> list = new LinkedList<>();

        GoodsCategoryPojo rootCategory = metadata.create(GoodsCategoryPojo.class);
        rootCategory.setName(getMessage("all.goods.category"));
        rootCategory.setAll(1);
        list.add(rootCategory);

        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            Query query = em.createNativeQuery(String.format(
                    "SELECT   " +
                            " distinct gc.id, " +
                            "   gc.parent_id, " +
                            "   gc.lang_value%d lang_value, " +
                            "   count(g.id) " +
                            "   OVER (   " +
                            "     PARTITION BY gc.id ) cnt " +
                            " FROM tsadv_dic_goods_category gc " +
                            " LEFT JOIN tsadv_goods g " +
                            "    ON gc.id = g.category_id " +
                            " where gc.delete_ts is null ",
                    languageIndex()));
            List<Object[]> rows = query.getResultList();
            if (rows != null && !rows.isEmpty()) {
                List<GoodsCategoryTemp> tempList = new LinkedList<>();
                for (Object[] row : rows) {
                    tempList.add(new GoodsCategoryTemp((UUID) row[0], (UUID) row[1], (String) row[2], (Long) row[3]));
                }

                List<GoodsCategoryTemp> parents = tempList.stream().filter(new Predicate<GoodsCategoryTemp>() {
                    @Override
                    public boolean test(GoodsCategoryTemp goodsCategoryTemp) {
                        if (goodsCategoryTemp.getParentId() == null) {
                            goodsCategoryTemp.setVisited(true);
                            return true;
                        }
                        return false;
                    }
                }).collect(Collectors.toList());


                long rootCount = 0L;

                for (GoodsCategoryTemp parent : parents) {
                    GoodsCategoryPojo parentCategoryPojo = metadata.create(GoodsCategoryPojo.class);
                    parentCategoryPojo.setId(parent.getId());
                    parentCategoryPojo.setCategoryId(parent.getId().toString());
                    parentCategoryPojo.setName(parent.getName());

                    parseTree(parentCategoryPojo, tempList);
                    list.add(parentCategoryPojo);

                    AtomicLong count = new AtomicLong(parent.getGoodsCount());
                    calculateGoodsCount(parentCategoryPojo, count);
                    parentCategoryPojo.setGoodsCount(count.get());

                    rootCount += parentCategoryPojo.getGoodsCount();
                }
                rootCategory.setGoodsCount(rootCount);

            }
            tx.end();
        }
        return entitySerialization.toJson(list, null);
    }


    private void calculateGoodsCount(GoodsCategoryPojo categoryPojo, AtomicLong count) {
        for (GoodsCategoryPojo goodsCategoryPojo : categoryPojo.getChildren()) {
            count.getAndAdd(goodsCategoryPojo.getGoodsCount());

            calculateGoodsCount(goodsCategoryPojo, count);
        }
    }

    private void parseTree(GoodsCategoryPojo parent, List<GoodsCategoryTemp> tempList) {
        List<GoodsCategoryTemp> childs = tempList.stream().filter(new Predicate<GoodsCategoryTemp>() {
            @Override
            public boolean test(GoodsCategoryTemp goodsCategoryTemp) {
                return !goodsCategoryTemp.isVisited() && goodsCategoryTemp.getParentId().equals(parent.getId());
            }
        }).collect(Collectors.toList());

        if (!childs.isEmpty()) {
            for (GoodsCategoryTemp child : childs) {
                child.setVisited(true);

                GoodsCategoryPojo categoryPojo = metadata.create(GoodsCategoryPojo.class);
                categoryPojo.setId(child.getId());
                categoryPojo.setCategoryId(child.getId().toString());
                categoryPojo.setName(child.getName());
                categoryPojo.setGoodsCount(child.getGoodsCount());
                parent.getChildren().add(categoryPojo);
                parseTree(categoryPojo, tempList);
            }
        }
    }

    @Override
    public FileDescriptor getRecognitionTypeImage(String rcgTypeCode, boolean empty) {
        if (rcgTypeCode == null || rcgTypeCode.equalsIgnoreCase("null") || rcgTypeCode.length() == 0) {
            return null;
        }

        View view = new View(DicRecognitionType.class);
        if (empty) {
            view.addProperty("emptySticker");
        } else {
            view.addProperty("sticker");
        }

        LoadContext<DicRecognitionType> loadContext = LoadContext.create(DicRecognitionType.class)
                .setQuery(LoadContext.createQuery(
                        "select p from tsadv$DicRecognitionType p " +
                                "where p.code = :code")
                        .setParameter("code", rcgTypeCode))
                .setView(view);
        DicRecognitionType model = dataManager.load(loadContext);
        if (model != null) {
            if (empty) {
                return model.getEmptySticker();
            }
            return model.getSticker();
        }
        return null;
    }

    @Override
    public FileDescriptor getRecognitionGoodsImage(String goodsId) {
        if (goodsId == null || goodsId.length() == 0) {
            return null;
        }

        LoadContext<FileDescriptor> loadContext = LoadContext.create(FileDescriptor.class)
                .setQuery(LoadContext.createQuery(
                        "select e.image from tsadv$GoodsImage e " +
                                "join e.good g " +
                                "where e.primary = True " +
                                "and g.id = :id")
                        .setParameter("id", UUID.fromString(goodsId)))
                .setView(View.MINIMAL);
        return dataManager.load(loadContext);
    }

    @Override
    public FileDescriptor getRecognitionMedalImage(String medalId) {
        if (medalId == null || medalId.equalsIgnoreCase("null") || medalId.length() == 0) {
            return null;
        }

        View view = new View(Medal.class);
        view.addProperty("icon");

        LoadContext<Medal> loadContext = LoadContext.create(Medal.class)
                .setQuery(LoadContext.createQuery(
                        "select a from tsadv$Medal a " +
                                "where a.id = :id")
                        .setParameter("id", UUID.fromString(medalId)))
                .setView(view);
        Medal model = dataManager.load(loadContext);
        if (model != null) {
            return model.getIcon();
        }
        return null;
    }

    @Override
    public FileDescriptor getRcgAnswerIcon(String answerId) {
        if (answerId == null || answerId.equalsIgnoreCase("null") || answerId.length() == 0) {
            return null;
        }

        View view = new View(RcgQuestionAnswer.class);
        view.addProperty("icon");

        LoadContext<RcgQuestionAnswer> loadContext = LoadContext.create(RcgQuestionAnswer.class)
                .setQuery(LoadContext.createQuery(
                        "select a from tsadv$RcgQuestionAnswer a " +
                                "where a.id = :id")
                        .setParameter("id", UUID.fromString(answerId)))
                .setView(view);
        RcgQuestionAnswer model = dataManager.load(loadContext);
        if (model != null) {
            return model.getIcon();
        }
        return null;
    }

    @Override
    public String loadOrganizations() {
        return toJsonList(loadOrganizations(languageIndex()));
    }

    @Override
    public List<OrganizationPojo> loadOrganizations(int languageIndex) {
        List<OrganizationPojo> list = new LinkedList<>();
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(String.format(
                    "SELECT distinct " +
                            "  org.id," +
                            "  org.organization_name_lang%d, " +
                            "  org.group_id," +
                            "  os._level " +
                            "FROM tsadv_organization_structure os " +
                            "JOIN base_organization org " +
                            "   ON org.group_id = os.organization_group_id and ?1 between org.start_date and org.end_date " +
                            "where os._level = ?2 " +
                            "AND os.delete_ts is null " +
                            "ORDER BY os._level", languageIndex));
            query.setParameter(1, CommonUtils.getSystemDate());
            query.setParameter(2, recognitionConfig.getHierarchyLevel());
            List<Object[]> resultList = query.getResultList();
            for (Object[] row : resultList) {
                OrganizationPojo organizationPojo = metadata.create(OrganizationPojo.class);
                organizationPojo.setId((UUID) row[0]);
                organizationPojo.setName((String) row[1]);
                organizationPojo.setGroupId(row[2].toString());
                list.add(organizationPojo);
            }
        }
        return list;
    }

    @Override
    public String loadProfiles(int page, long lastCount) {
        int perPageCount = PageRangeInfo.PROFILE.getPerPageCount();

        int offset = 0;
        if (page > 1) {
            offset = perPageCount;
        }

        String hierarchyId = recognitionConfig.getHierarchyId();
        String hierarchyFilter = "AND h.primary_flag = TRUE";
        if (StringUtils.isNotBlank(hierarchyId)) {
            hierarchyFilter = String.format("and h.id = '%s'", hierarchyId);
        }

        ProfilePageInfo profilePageInfo = metadata.create(ProfilePageInfo.class);

        PageInfo pageInfo = metadata.create(PageInfo.class);
        pageInfo.setTotalRowsCount(lastCount);
        pageInfo.setPagesCount(PageInfo.getPageCount(lastCount, perPageCount));
        profilePageInfo.setPageInfo(pageInfo);

        List<ProfilePojo> profiles = new ArrayList<>();
        profilePageInfo.setProfiles(profiles);

        AssignmentExt assignment = getCurrentAssignment();
        if (assignment != null) {
            PositionGroupExt positionGroup = assignment.getPositionGroup();
            if (positionGroup != null) {
                try (Transaction tx = persistence.createTransaction()) {
                    EntityManager em = persistence.getEntityManager();

                    if (page == 1) {
                        long profilesCount = profilesCount(hierarchyFilter, positionGroup.getId());
                        pageInfo.setTotalRowsCount(profilesCount);
                        pageInfo.setPagesCount(PageInfo.getPageCount(profilesCount, perPageCount));
                    }

                    loadTeamProfiles(em, languageIndex(), hierarchyFilter, offset, perPageCount, positionGroup.getId(), profiles);
                }
            }
        }
        return entitySerialization.toJson(profilePageInfo, null);
    }

    @Override
    public String loadProfile(String personGroupId, int languageIndex) {
        if (StringUtils.isBlank(personGroupId)) {
            throw new RecognitionException("PersonGroup ID is null!");
        }
        return persistence.callInTransaction(em -> {
            Query query = em.createNativeQuery(String.format("SELECT " +
                            "   p.id, " +
                            "   p.group_id, " +
                            "   %s, " +
                            "   %s, " +
                            "   org.organization_name_lang%d," +
                            "   pos.position_name_lang%d," +
                            "   (select string_agg(ap.name,'$') from TSADV_SELECTED_PERSON_AWARD pa " +
                            "       join TSADV_AWARD_PROGRAM ap " +
                            "           on ap.id = pa.AWARD_PROGRAM_ID" +
                            "       where pa.PERSON_GROUP_ID = a.person_group_id " +
                            "           and pa.delete_ts is null " +
                            "           and ap.delete_ts is null " +
                            "           and pa.awarded = true" +
                            "   ) heart_award, " +
                            " COALESCE(pc.coins, 0), " +
                            " COALESCE(points.points, 0) " +
                            "FROM base_assignment a " +
                            "JOIN base_person p " +
                            "   ON p.group_id = a.person_group_id " +
                            "JOIN base_organization org " +
                            "   ON org.group_id = a.organization_group_id " +
                            "JOIN base_position pos " +
                            "   ON pos.group_id = a.position_group_id " +
                            "left join tsadv_person_coin pc " +
                            "   on pc.person_group_id = p.group_id and pc.delete_ts is null " +
                            "left join tsadv_person_point points " +
                            "   on points.person_group_id = p.group_id and points.delete_ts is null " +
                            "WHERE a.delete_ts is null and ?1 between a.start_date and a.end_date " +
                            "and p.delete_ts is null and ?1 between p.start_date and p.end_date " +
                            "and org.delete_ts is null and ?1 between org.start_date and org.end_date " +
                            "and pos.delete_ts is null and ?1 between pos.start_date and pos.end_date " +
                            "and a.person_group_id = ?2",
                    languageIndex == 1 ? "p.first_name" : "p.first_name_latin",
                    languageIndex == 1 ? "p.last_name" : "p.last_name_latin",
                    languageIndex,
                    languageIndex
            ));

            query.setParameter(1, new Date());
            query.setParameter(2, UUID.fromString(personGroupId));
            Object[] row = (Object[]) query.getFirstResult();
            if (row != null) {
                UUID personGroupId1 = (UUID) row[1];
                ProfilePojo profile = metadata.create(ProfilePojo.class);
                profile.setId(personGroupId1);
                profile.setPId(row[0].toString());
                profile.setFirstName((String) row[2]);
                profile.setLastName((String) row[3]);
                profile.setOrganization((String) row[4]);
                profile.setPosition((String) row[5]);

                Object heartAward = row[6];
                if (heartAward != null) {
                    profile.setHeartAward(heartAward.toString());
                }

                profile.setCoins(row[7].toString());
                profile.setPoints(row[8].toString());
                profile.setRecognitionTypes(loadRecognitionTypes(personGroupId1));

                return entitySerialization.toJson(profile, null);
            }
            return null;
        });
    }

    @Override
    public List<UUID> loadTeamProfiles(UUID hierarchyId, UUID positionGroupId) {
        return persistence.callInTransaction(em -> {
            String hierarchyFilter = "AND h.primary_flag = TRUE";
            if (hierarchyId != null) {
                hierarchyFilter = String.format("and h.id = '%s'", hierarchyId);
            }

            Query query = em.createNativeQuery("SELECT " +
                    "   p.group_id " +
                    "FROM base_assignment a " +
                    "JOIN base_person p " +
                    "   ON p.group_id = a.person_group_id " +
                    "JOIN base_organization org " +
                    "   ON org.group_id = a.organization_group_id " +
                    "WHERE a.delete_ts is null and ?1 between a.start_date and a.end_date " +
                    "and p.delete_ts is null and ?1 between p.start_date and p.end_date " +
                    "and org.delete_ts is null and ?1 between org.start_date and org.end_date " +
                    "and a.position_group_id IN (" +
                    "   select " +
                    "       child_h.position_group_id " +
                    "   from base_hierarchy_element he " +
                    "   join base_hierarchy_element child_h " +
                    "       on child_h.parent_id = he.id " +
                    "   join base_hierarchy h " +
                    "       on h.id = he.hierarchy_id and h.delete_ts is null " +
                    "   where he.position_group_id = ?2 " +
                    hierarchyFilter +
                    "   )");
            query.setParameter(1, CommonUtils.getSystemDate());
            query.setParameter(2, positionGroupId);
            return query.getResultList();
        });
    }

    @Override
    public List<UUID> getPersonGroupEmployee(UUID hierarchyId, UUID personGroupId, boolean isManager) {
        return persistence.callInTransaction(em -> {
            String hierarchyFilter = "AND h.primary_flag = TRUE";
            if (hierarchyId != null) {
                hierarchyFilter = String.format("and h.id = '%s'", hierarchyId);
            }

            String[] parentFilter;
            if (isManager) {
                parentFilter = new String[]{"t.person_group_id", "t.parent_person_group_id = ?2"};
            } else {
                parentFilter = new String[]{"t.parent_person_group_id", "t.person_group_id = ?2"};
            }
            Query query = em.createNativeQuery(String.format("WITH RECURSIVE org_pos(id, hierarchy_id, parent_id, element_type, position_group_id, " +
                    "                       manager_flag, parent_position_group_id, start_date, end_date, " +
                    "                       organization_group_id, path, lvl, path_pos_name, person_group_id, " +
                    "                       parent_person_group_id) AS (SELECT he.id, " +
                    "                                                          he.hierarchy_id, " +
                    "                                                          he.parent_id, " +
                    "                                                          he.element_type, " +
                    "                                                          he.position_group_id, " +
                    "                                                          p.manager_flag, " +
                    "                                                          NULL::uuid                                                          AS parent_organization_group_id, " +
                    "                                                          he.start_date, " +
                    "                                                          he.end_date, " +
                    "                                                          a1.organization_group_id, " +
                    "                                                          (a1.person_group_id)::text                                          AS path, " +
                    "                                                          1                                                                   AS lvl, " +
                    "                                                          (COALESCE(p.position_full_name_lang1, ''::character varying))::text AS path_pos_name, " +
                    "                                                          a1.person_group_id, " +
                    "                                                          NULL::uuid                                                          AS parent_person_group_id, " +
                    "                                                          newid()                                                             AS h_id, " +
                    "                                                          NULL::uuid                                                          AS h_parent_id " +
                    "                                                   FROM (((base_hierarchy_element he JOIN base_hierarchy h ON (((h.id = he.hierarchy_id) AND (h.delete_ts IS NULL)))) JOIN base_position p ON (( " +
                    "                                                           (he.position_group_id = p.group_id) AND " +
                    "                                                           (('now'::text)::date >= p.start_date) AND " +
                    "                                                           (('now'::text)::date <= p.end_date) AND " +
                    "                                                           (p.delete_ts IS NULL)))) " +
                    "                                                            LEFT JOIN base_assignment a1 " +
                    "                                                                      ON (((a1.position_group_id = he.position_group_id) AND " +
                    "                                                                           (('now'::text)::date >= a1.start_date) AND " +
                    "                                                                           (('now'::text)::date <= a1.end_date) AND " +
                    "                                                                           (a1.primary_flag = true) AND " +
                    "                                                                           (a1.delete_ts IS NULL) AND " +
                    "                                                                           (a1.assignment_status_id <> '852609db-c23e-af4e-14f3-ea645d38f57d'::uuid)))) " +
                    "                                                   WHERE ((he.parent_id IS NULL) AND " +
                    "                                                          (he.delete_ts IS NULL) AND " +
                    "                                                          (('now'::text)::date >= he.start_date) AND " +
                    "                                                          (('now'::text)::date <= he.end_date)) " +
                    "                                                   UNION ALL " +
                    "                                                   SELECT he.id, " +
                    "                                                          he.hierarchy_id, " +
                    "                                                          he.parent_id, " +
                    "                                                          he.element_type, " +
                    "                                                          he.position_group_id, " +
                    "                                                          p.manager_flag, " +
                    "                                                          COALESCE(op.position_group_id, op.parent_position_group_id)                  AS parent_position_group_id, " +
                    "                                                          he.start_date, " +
                    "                                                          he.end_date, " +
                    "                                                          COALESCE(a1.organization_group_id, op.organization_group_id)                 AS organization_group_id, " +
                    "                                                          (((op.path || '*'::text) || " +
                    "                                                            COALESCE((a1.person_group_id)::text, 'VACANCY'::text)))::character varying AS \"varchar\", " +
                    "                                                          (op.lvl + 1), " +
                    "                                                          ((op.path_pos_name || '->'::text) || " +
                    "                                                           (COALESCE(p.position_full_name_lang1, ''::character varying))::text)        AS path_pos_name, " +
                    "                                                          a1.person_group_id, " +
                    "                                                          COALESCE(op.person_group_id, op.parent_person_group_id)                      AS parent_person_group_id, " +
                    "                                                          newid()                                                                      AS h_id, " +
                    "                                                          op.h_id                                                                      AS h_parent_id " +
                    "                                                   FROM (((base_hierarchy_element he JOIN org_pos op ON ((he.parent_id = op.id))) JOIN base_position p ON (( " +
                    "                                                           (he.position_group_id = p.group_id) AND " +
                    "                                                           (('now'::text)::date >= p.start_date) AND " +
                    "                                                           (('now'::text)::date <= p.end_date) AND " +
                    "                                                           (p.delete_ts IS NULL)))) " +
                    "                                                            LEFT JOIN base_assignment a1 " +
                    "                                                                      ON (((a1.position_group_id = he.position_group_id) AND " +
                    "                                                                           (('now'::text)::date >= a1.start_date) AND " +
                    "                                                                           (('now'::text)::date <= a1.end_date) AND " +
                    "                                                                           (a1.primary_flag = true) AND " +
                    "                                                                           (a1.delete_ts IS NULL) AND " +
                    "                                                                           (a1.assignment_status_id <> '852609db-c23e-af4e-14f3-ea645d38f57d'::uuid)))) " +
                    "                                                   WHERE ((he.element_type = 2) AND " +
                    "                                                          (he.delete_ts IS NULL) AND " +
                    "                                                          (('now'::text)::date >= he.start_date) AND " +
                    "                                                          (('now'::text)::date <= he.end_date))) " +
                    "SELECT %s " +
                    "FROM org_pos t " +
                    "         left join base_person pp " +
                    "                   on pp.group_id = t.person_group_id and " +
                    "                      ?1 between pp.start_date and pp.end_date " +
                    "         left join base_person ppp " +
                    "                   on ppp.group_id = t.parent_person_group_id and " +
                    "                      ?1 between ppp.start_date and ppp.end_date " +
                    "         join base_hierarchy h on h.id = t.hierarchy_id and h.delete_ts is null" +
                    "         inner join tsadv_user_ext_person_group ue ON ue.person_group_id = t.person_group_id " +
                    "        inner join sec_user ON sec_user.id = ue.user_ext_id " +
                    "where %s %s", parentFilter[0], parentFilter[1], hierarchyFilter));
            query.setParameter(1, CommonUtils.getSystemDate());
            query.setParameter(2, personGroupId);
            return query.getResultList();
        });
    }

    private void loadTeamProfiles(EntityManager em, int languageIndex, String hierarchyFilter, int offset, int maxResults, UUID positionGroupId, List<ProfilePojo> profiles) {
        Query query = em.createNativeQuery(String.format("SELECT " +
                        "   p.id, " +
                        "   p.group_id, " +
                        "   %s, " +
                        "   %s, " +
                        "   org.organization_name_lang%d," +
                        "   pos.position_name_lang%d," +
                        "   (select string_agg(ap.name,'$') from TSADV_SELECTED_PERSON_AWARD pa " +
                        "       join TSADV_AWARD_PROGRAM ap " +
                        "           on ap.id = pa.AWARD_PROGRAM_ID" +
                        "       where pa.PERSON_GROUP_ID = a.person_group_id " +
                        "           and pa.delete_ts is null " +
                        "           and ap.delete_ts is null " +
                        "           and pa.awarded = true" +
                        "   ) heart_award, " +
                        "   p.employee_number " +
                        "FROM base_assignment a " +
                        "JOIN tsadv_dic_assignment_status dc on a.assignment_status_id = dc.id " +
                        "  and (dc.code='ACTIVE' or dc.code='SUSPENDED') " +
                        "JOIN base_person p " +
                        "   ON p.group_id = a.person_group_id " +
                        "JOIN tsadv_dic_person_type pt on p.type_id = pt.id " +
                        "   and pt.code='EMPLOYEE' " +
                        "JOIN base_organization org " +
                        "   ON org.group_id = a.organization_group_id " +
                        "JOIN base_position pos " +
                        "   ON pos.group_id = a.position_group_id " +
                        "WHERE a.delete_ts is null and ?1 between a.start_date and a.end_date " +
                        "and p.delete_ts is null and ?1 between p.start_date and p.end_date " +
                        "and org.delete_ts is null and ?1 between org.start_date and org.end_date " +
                        "and pos.delete_ts is null and ?1 between pos.start_date and pos.end_date " +
                        "and a.position_group_id IN (" +
                        "   select " +
                        "       child_h.position_group_id " +
                        "   from base_hierarchy_element he " +
                        "   join base_hierarchy_element child_h " +
                        "       on child_h.parent_id = he.id " +
                        "   join base_hierarchy h " +
                        "       on h.id = he.hierarchy_id and h.delete_ts is null " +
                        "   where he.position_group_id = ?2 " +
                        hierarchyFilter +
                        "   )",
                languageIndex == 1 ? "p.first_name" : "p.first_name_latin",
                languageIndex == 1 ? "p.last_name" : "p.last_name_latin",
                languageIndex,
                languageIndex
        ));

        query.setParameter(1, CommonUtils.getSystemDate());
        query.setParameter(2, positionGroupId);
        query.setFirstResult(offset);
        query.setMaxResults(maxResults);

        List<Object[]> rows = query.getResultList();
        if (rows != null && !rows.isEmpty()) {
            for (Object[] row : rows) {
                UUID personGroupId = (UUID) row[1];
                ProfilePojo profile = metadata.create(ProfilePojo.class);
                profile.setId(personGroupId);
                profile.setPId(row[0].toString());
                profile.setFirstName((String) row[2]);
                profile.setLastName((String) row[3]);
                profile.setOrganization((String) row[4]);
                profile.setPosition((String) row[5]);

                Object heartAward = row[6];
                if (heartAward != null) {
                    profile.setHeartAward(heartAward.toString());
                }

                profile.setEmployeeNumber((String) row[7]);
                profile.setRecognitionTypes(loadRecognitionTypes(personGroupId));
                profiles.add(profile);
            }
        }
    }

    @Override
    public String loadTeamProfiles(int offset, int maxResults, int languageIndex) {
        List<ProfilePojo> profiles = new ArrayList<>();
        AssignmentExt assignment = getCurrentAssignment();
        if (assignment != null) {
            PositionGroupExt positionGroup = assignment.getPositionGroup();
            if (positionGroup != null) {
                persistence.runInTransaction(em -> {
                    String hierarchyId = recognitionConfig.getHierarchyId();
                    String hierarchyFilter = "AND h.primary_flag = TRUE";
                    if (StringUtils.isNotBlank(hierarchyId)) {
                        hierarchyFilter = String.format("and h.id = '%s'", hierarchyId);
                    }

                    loadTeamProfiles(em, languageIndex, hierarchyFilter, offset, maxResults, positionGroup.getId(), profiles);
                });
            }
        }
        return toJson(profiles, null);
    }

    @Override
    public boolean hasTeamProfiles() {
        AssignmentExt assignment = getCurrentAssignment();
        if (assignment != null) {
            PositionGroupExt positionGroup = assignment.getPositionGroup();
            if (positionGroup != null) {

                String hierarchyId = recognitionConfig.getHierarchyId();
                String hierarchyFilter = "AND h.primary_flag = TRUE";
                if (StringUtils.isNotBlank(hierarchyId)) {
                    hierarchyFilter = String.format("and h.id = '%s'", hierarchyId);
                }

                return profilesCount(hierarchyFilter, positionGroup.getId()) > 0;
            }
        }
        return false;
    }

    @Override
    public List<RecognitionTypePojo> loadRecognitionTypes(UUID personGroupId) {
        List<RecognitionTypePojo> typesList = new LinkedList<>();

        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(String.format(
                    "SELECT " +
                            "distinct rt.id rt_id, " +
                            "rt.lang_value%d lang_value, " +
                            "rt.code, " +
                            "  count(r.id) " +
                            "  OVER ( " +
                            "    PARTITION BY rt.id ) cnt " +
                            "FROM tsadv_dic_recognition_type rt " +
                            "LEFT JOIN tsadv_recognition r " +
                            "   ON rt.id = r.recognition_type_id " +
                            "   AND r.receiver_id = ?1 " +
                            "   AND r.delete_ts is null " +
                            "where rt.delete_ts is null",
                    languageIndex()));

            query.setParameter(1, personGroupId);

            List<Object[]> resultList = query.getResultList();

            for (Object[] row : resultList) {
                RecognitionTypePojo typePojo = metadata.create(RecognitionTypePojo.class);
                typePojo.setShowEmpty(true);
                typePojo.setId((UUID) row[0]);
                typePojo.setName((String) row[1]);
                typePojo.setCode((String) row[2]);
                typePojo.setCount((Long) row[3]);
                typesList.add(typePojo);
            }

            tx.end();
        }
        return typesList;
    }

    @Override
    public List<RecognitionTypePojo> loadRecognitionTypes() {
        List<RecognitionTypePojo> typesList = new LinkedList<>();

        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(String.format(
                    "SELECT " +
                            "rt.id rt_id, " +
                            "rt.lang_value%d lang_value, " +
                            "rt.code, " +
                            "rt.coins " +
                            "FROM tsadv_dic_recognition_type rt " +
                            "where rt.delete_ts is null",
                    languageIndex()));

            List<Object[]> resultList = query.getResultList();

            for (Object[] row : resultList) {
                RecognitionTypePojo typePojo = metadata.create(RecognitionTypePojo.class);
                typePojo.setShowEmpty(true);
                typePojo.setId((UUID) row[0]);
                typePojo.setName((String) row[1]);
                typePojo.setCode((String) row[2]);
                typePojo.setCount((Long) row[3]);
                typesList.add(typePojo);
            }

            tx.end();
        }
        return typesList;
    }

    @Override
    public String loadPersonHeartAward(UUID personGroupId) {
        String result = null;
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(
                    "select " +
                            "   string_agg(ap.name,'$') heart_award " +
                            "from TSADV_SELECTED_PERSON_AWARD pa " +
                            "join TSADV_AWARD_PROGRAM ap " +
                            "   on ap.id = pa.AWARD_PROGRAM_ID " +
                            "where pa.PERSON_GROUP_ID = ?1 " +
                            "   and pa.delete_ts is null " +
                            "   and ap.delete_ts is null " +
                            "   and pa.awarded = ?2");
            query.setParameter(1, personGroupId);
            query.setParameter(2, true);

            Object singleResult = query.getSingleResult();
            if (singleResult != null) {
                result = singleResult.toString();
            }
            tx.end();
        }
        return result;
    }

    @Override
    public List<PreferencePojo> loadPersonPreferences(UUID personGroupId) {
        List<PreferencePojo> preferences = new LinkedList<>();
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            int languageIndex = languageIndex();

            Query query = em.createNativeQuery(String.format(
                    "SELECT  " +
                            "  pp.id pp_id, " +
                            "  pt.id pt_id, " +
                            "  pt.lang_value%d, " +
                            "  %s," +
                            "  pp.description," +
                            "  pp.description_ru," +
                            "  pp.description_en," +
                            "  pt.coins " +
                            "FROM tsadv_dic_person_preference_type pt " +
                            "  LEFT JOIN tsadv_person_preference pp " +
                            "    ON pt.id = pp.preference_type_id and pp.person_group_id = ?1 and pp.delete_ts is null " +
                            "where pt.delete_ts is null ",
                    languageIndex,
                    isAutomaticTranslate() ? (languageIndex == 3 ? "pp.description_en" : "pp.description_ru") : "pp.description"));

            query.setParameter(1, personGroupId);

            List<Object[]> rows = query.getResultList();
            if (rows != null && !rows.isEmpty()) {
                for (Object[] row : rows) {
                    UUID id = (UUID) row[0];
                    Long coins = (Long) row[7];

                    PreferencePojo preference = metadata.create(PreferencePojo.class);
                    preference.setId(id);
                    preference.setTypeId((UUID) row[1]);
                    preference.setTypeName((String) row[2]);
                    preference.setDescription(wrapNull((String) row[3]));
                    preference.setCoins(coins);
                    preference.setShowCoinsDescription(id == null && coins != null && coins > 0);

                    String originalText = (String) row[4];
                    String commentRu = (String) row[5];
                    String commentEn = (String) row[6];

                    boolean translated = !preference.getDescription().equals(originalText);

                    String reverseText = originalText;
                    if (translated) {
                        if (preference.getDescription().equals(commentEn)) {
                            reverseText = commentRu;
                        } else {
                            reverseText = commentEn;
                        }
                    } else {
                        if (reverseText.equals(commentEn)) {
                            reverseText = commentRu;
                        } else {
                            reverseText = commentEn;
                        }
                    }
                    preference.setReverseText(reverseText);

                    preferences.add(preference);
                }
            }
            tx.end();
        }
        return preferences;
    }

    @Override
    public List<PreferencePojo> savePersonPreference(PersonGroupExt personGroup, List<PreferencePojo> preferences) {
        Transaction tx = null;
        try {
            tx = persistence.createTransaction();
            EntityManager em = persistence.getEntityManager();

            PersonCoin personCoin = loadPersonCoin(personGroup.getId());

            if (personCoin == null) {
                throw new RecognitionException(getMessage("author.wallet.hc.null"));
            }

            int languageIndex = languageIndex();
            boolean isAutomaticTranslate = isAutomaticTranslate();

            for (PreferencePojo preference : preferences) {
                if (preference.getId() == null && StringUtils.isBlank(preference.getDescription())) {
                    continue;
                }

                UUID personPreferenceId = preference.getId();

                String originalDescription = preference.getDescription();
                String descriptionEn = originalDescription;
                String descriptionRu = originalDescription;

                if (StringUtils.isNotBlank(originalDescription)) {
                    descriptionEn = googleTranslateService.translate(originalDescription, "en");
                    descriptionRu = googleTranslateService.translate(originalDescription, "ru");
                }

                if (personPreferenceId == null) {
                    DicPersonPreferenceType personPreferenceType = em.find(DicPersonPreferenceType.class, preference.getTypeId(), View.LOCAL);

                    if (personPreferenceType == null) {
                        throw new RecognitionException(getMessage("person.preference.type.null"));
                    }

                    PersonPreference personPreference = metadata.create(PersonPreference.class);
                    personPreference.setPersonGroup(personGroup);
                    personPreference.setPreferenceType(personPreferenceType);
                    personPreference.setDescription(originalDescription);
                    personPreference.setDescriptionEn(descriptionEn);
                    personPreference.setDescriptionRu(descriptionRu);
                    em.persist(personPreference);

                    preference.setId(personPreference.getId());

                    Long preferenceCoins = personPreferenceType.getCoins();
                    if (preferenceCoins != null && preferenceCoins > 0) {
                        Long personCoins = personCoin.getCoins();
                        if (personCoins == null) personCoins = 0L;
                        personCoin.setCoins(personCoins + preferenceCoins);

                        PersonCoinLog personCoinLog = metadata.create(PersonCoinLog.class);
                        personCoinLog.setActionType(LogActionType.ADDED_PREFERENCE); //Вы добавили предпочтение
                        personCoinLog.setDate(new Date());
                        personCoinLog.setOperationType(PointOperationType.RECEIPT);
                        personCoinLog.setPersonGroup(personGroup);
                        personCoinLog.setQuantity(preferenceCoins);
                        personCoinLog.setCoinType(RecognitionCoinType.COIN);
                        em.persist(personCoinLog);
                    }
                } else {
                    PersonPreference personPreference = em.find(PersonPreference.class, personPreferenceId);
                    if (personPreference == null) {
                        throw new RecognitionException(getMessage("person.preference.null"));
                    }
                    personPreference.setDescription(originalDescription);
                    personPreference.setDescriptionEn(descriptionEn);
                    personPreference.setDescriptionRu(descriptionRu);
                    em.merge(personPreference);
                }


                String finalDescription = isAutomaticTranslate ? (languageIndex == 1 ? descriptionRu : descriptionEn) : originalDescription;
                preference.setDescription(wrapNull(finalDescription));
            }

            em.merge(personCoin);
            tx.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RecognitionException(ex.getMessage());
        } finally {
            if (tx != null) {
                tx.end();
            }
        }
        return preferences;
    }

    private String wrapNull(String text) {
        if (StringUtils.isBlank(text)) return "";
        return text;
    }

    @Override
    public PersonCoin loadPersonCoin(UUID personGroupId) {
        LoadContext<PersonCoin> loadContext = LoadContext.create(PersonCoin.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$PersonCoin e " +
                        "where e.personGroup.id = :pgId");
        query.setParameter("pgId", personGroupId);
        loadContext.setQuery(query);
        loadContext.setView(View.LOCAL);
        return dataManager.load(loadContext);
    }

    @Override
    public PersonPoint loadPersonPoint(UUID personGroupId) {
        LoadContext<PersonPoint> loadContext = LoadContext.create(PersonPoint.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$PersonPoint e " +
                        "where e.personGroup.id = :pgId");
        query.setParameter("pgId", personGroupId);
        loadContext.setQuery(query);
        loadContext.setView(View.LOCAL);
        return dataManager.load(loadContext);
    }

    @Override
    public void sendHeartCoins(HeartCoinPojo heartCoinPojo) {
        long coins = heartCoinPojo.getCoins();
        UUID receiverPersonGroupId = UUID.fromString(heartCoinPojo.getPersonGroupId());

        if (coins < 0) {
            throw new IllegalArgumentException(getMessage("more.than.zero"));
        }

        PersonGroupExt authorPersonGroupExt = getCurrentPersonGroup();
        if (authorPersonGroupExt == null) {
            throw new RecognitionException(getMessage("user.person.assignment.null"));
        }

        PersonCoin personCoin = loadPersonCoin(authorPersonGroupExt.getId());
        if (personCoin == null) {
            throw new RecognitionException(getMessage("author.wallet.hc.null"));
        }

        Long personCoins = personCoin.getCoins();
        if (personCoins == 0 || personCoins < coins) {
            throw new IllegalArgumentException(getMessage("hc.not.enough"));
        }

        PersonCoin receiverPersonCoin = loadPersonCoin(receiverPersonGroupId);
        if (receiverPersonCoin == null) {
            throw new RecognitionException(getMessage("receiver.wallet.hc.null"));
        }

        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            personCoin.setCoins(personCoins - coins);
            em.merge(personCoin);

            Long receiverPersonCoins = receiverPersonCoin.getCoins();
            receiverPersonCoin.setCoins(receiverPersonCoins + coins);
            em.merge(receiverPersonCoin);

            PersonGroupExt receiverPersonGroupExt = em.find(PersonGroupExt.class, receiverPersonGroupId);

            PersonCoinLog authorCoinLog = metadata.create(PersonCoinLog.class);
            authorCoinLog.setActionType(LogActionType.SEND_HEART_COIN); //Вы отправили HC
            authorCoinLog.setDate(new Date());
            authorCoinLog.setOperationType(PointOperationType.ISSUE);
            authorCoinLog.setPersonGroup(authorPersonGroupExt);
            authorCoinLog.setAnotherPersonGroup(receiverPersonGroupExt);
            authorCoinLog.setQuantity(coins);
            authorCoinLog.setCoinType(RecognitionCoinType.COIN);
            authorCoinLog.setComment(heartCoinPojo.getComment());
            em.persist(authorCoinLog);

            PersonCoinLog receiverCoinLog = metadata.create(PersonCoinLog.class);
            receiverCoinLog.setActionType(LogActionType.RECEIVE_HEART_COIN); //Вы получили HC
            receiverCoinLog.setDate(new Date());
            receiverCoinLog.setOperationType(PointOperationType.RECEIPT);
            receiverCoinLog.setPersonGroup(receiverPersonGroupExt);
            receiverCoinLog.setAnotherPersonGroup(authorPersonGroupExt);
            receiverCoinLog.setCoinType(RecognitionCoinType.COIN);
            receiverCoinLog.setQuantity(coins);
            receiverCoinLog.setComment(heartCoinPojo.getComment());
            em.persist(receiverCoinLog);

            tx.commit();
        }
    }

    @Override
    public String loadCoinLogs(int page, long lastCount) {
        int perPageCount = PageRangeInfo.LOG.getPerPageCount();

        CoinLogPageInfo coinLogPageInfo = metadata.create(CoinLogPageInfo.class);

        long logsCount = logsCount();
        if (page == 1) {
            lastCount = logsCount;
        }

        long difference = 0;
        if (page != 1 && lastCount != logsCount) {
            difference = logsCount - lastCount;
        }

        PageInfo pageInfo = metadata.create(PageInfo.class);
        pageInfo.setTotalRowsCount(lastCount);
        pageInfo.setPagesCount(PageInfo.getPageCount(lastCount, perPageCount));
        coinLogPageInfo.setPageInfo(pageInfo);

        List<CoinLogPojo> list = new ArrayList<>();
        coinLogPageInfo.setCoinLogs(list);

        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            int languageIndex = languageIndex();

            Query query = em.createNativeQuery(String.format(
                    "SELECT " +
                            "  cl.operation_type, " +
                            "  cl.action_type, " +
                            "  cl.quantity, " +
                            "  cl.date_, " +
                            "  cl.coin_type, " +
                            "  cl.another_person_group_id, " +
                            "  %s," +
                            "  cl.comment_ " +
                            "FROM tsadv_person_coin_log cl " +
                            "  left join base_person p " +
                            "  on p.group_id = cl.another_person_group_id and p.delete_ts is null and current_date between p.start_date and p.end_date " +
                            "where cl.delete_ts is null " +
                            "and cl.person_group_id = ?1 " +
                            "order by cl.date_ desc",
                    languageIndex == 1 ? "p.first_name||' '||p.last_name" : "p.first_name_latin||' '||p.last_name_latin"));

            query.setParameter(1, getCurrentPersonGroup().getId());
            query.setFirstResult((int) difference + ((page - 1) * perPageCount));
            query.setMaxResults(perPageCount);

            List<Object[]> rows = query.getResultList();
            if (rows != null && !rows.isEmpty()) {
                Locale locale = userSessionSource.getLocale();
                for (Object[] row : rows) {
                    boolean isSystem = row[5] == null;

                    PointOperationType pointOperationType = PointOperationType.fromId((String) row[0]);
                    RecognitionCoinType rcgCoinType = RecognitionCoinType.fromId((String) row[4]);
                    LogActionType logActionType = LogActionType.fromId((String) row[1]);

                    CoinLogPojo coinLogPojo = metadata.create(CoinLogPojo.class);
                    coinLogPojo.setOperationType(pointOperationType.getId());
                    coinLogPojo.setCoinType(messages.getMessage(rcgCoinType, locale));
                    coinLogPojo.setActionType(messages.getMessage(logActionType, locale));
                    coinLogPojo.setCoins((pointOperationType.equals(PointOperationType.RECEIPT) ? "+" : "-") + row[2].toString());
                    coinLogPojo.setDate(getDateTimeFormatter().format((Date) row[3]));
                    coinLogPojo.setTarget(isSystem ? "System" : (String) row[6]);
                    coinLogPojo.setTargetId(isSystem ? null : row[5].toString());
                    coinLogPojo.setComment((String) row[7]);
                    list.add(coinLogPojo);
                }
            }
        }
        return entitySerialization.toJson(coinLogPageInfo, null);
    }

    @Override
    public String loadTopSender() {
        return loadTopSender(null, null);
    }

    @Override
    public String loadTopSender(String language) {
        return loadTopSender(language, null);
    }

    @Override
    public String loadTopSender(Integer countOfMonths) {
        return loadTopSender(null, countOfMonths);
    }

    @Override
    public String loadTopSender(String language, Integer countOfMonths) {
        List<TopTenPojo> list = new LinkedList<>();

        if (StringUtils.isBlank(language)) {
            language = userSessionSource.getLocale().getLanguage();
        }

        language = language.toLowerCase();

        try (Transaction tx = persistence.createTransaction()) {
            String dateOfTop = "";
            if (countOfMonths != null) {
                if (countOfMonths.equals(0))
                    dateOfTop = "and pa.recognition_date > date_trunc('month', current_date)";
                else
                    dateOfTop = String.format("and pa.recognition_date > date_trunc('month', current_date) - interval '%d month'", countOfMonths);
            }
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(String.format(
                    "SELECT DISTINCT " +
                            "  p.id, " +
                            "  p.group_id, " +
                            "  (CASE " +
                            "   WHEN 'en' = ?1 " +
                            "     THEN coalesce(p.first_name_latin, p.first_name) || ' ' || coalesce(p.last_name_latin, p.last_name) " +
                            "   ELSE p.first_name || ' ' || p.last_name " +
                            "   END)                           receiver, " +
                            "  count(pa.id) " +
                            "  OVER ( " +
                            "    PARTITION BY pa.author_id ) awards, " +
                            "  coalesce(pos.position_name_lang%d, pos.position_name_lang1) pos_name, " +
                            "  (SELECT string_agg(ap.name, '$') " +
                            "   FROM TSADV_SELECTED_PERSON_AWARD pa " +
                            "     JOIN TSADV_AWARD_PROGRAM ap " +
                            "       ON ap.id = pa.AWARD_PROGRAM_ID " +
                            "   WHERE pa.PERSON_GROUP_ID = a.person_group_id " +
                            "         AND pa.delete_ts IS NULL " +
                            "         AND ap.delete_ts IS NULL " +
                            "         AND pa.awarded = True " +
                            "  )  heart_award, " +
                            "  p.employee_number " +
                            "FROM tsadv_recognition pa " +
                            "  JOIN base_person p " +
                            "    ON p.group_id = pa.author_id " +
                            "       AND p.delete_ts IS NULL " +
                            "       AND current_date BETWEEN p.start_date AND p.end_date " +
                            "  JOIN base_assignment a " +
                            "    ON a.person_group_id = pa.author_id " +
                            "       AND a.delete_ts IS NULL " +
                            "       AND current_date BETWEEN a.start_date AND a.end_date " +
                            "       AND a.primary_flag = TRUE " +
                            "  JOIN tsadv_dic_assignment_status s " +
                            "    ON s.id = a.assignment_status_id " +
                            "       AND s.code = 'ACTIVE' " +
                            "  JOIN base_position pos " +
                            "    ON pos.group_id = a.position_group_id " +
                            "       AND pos.delete_ts is null " +
                            "       AND current_date BETWEEN pos.start_date AND pos.end_date " +
                            "where pa.delete_ts is null " +
                            " %s" +
                            "ORDER BY awards DESC",
                    languageIndex(language),
                    dateOfTop));

            query.setParameter(1, language);

            query.setFirstResult(0);
            query.setMaxResults(10);

            List<Object[]> rows = query.getResultList();
            if (rows != null && !rows.isEmpty()) {
                int order = 1;
                for (Object[] row : rows) {
                    TopTenPojo topTenPojo = metadata.create(TopTenPojo.class);
                    topTenPojo.setOrder(order);
                    topTenPojo.setPId(row[0].toString());
                    topTenPojo.setPgId(row[1].toString());
                    topTenPojo.setFullName((String) row[2]);
                    topTenPojo.setCount((Long) row[3]);
                    topTenPojo.setPosition((String) row[4]);
                    topTenPojo.setEmployeeNumber(row[6].toString());
                    Object heartAward = row[5];
                    if (heartAward != null) {
                        topTenPojo.setHeartAward(heartAward.toString());
                    }
                    list.add(topTenPojo);

                    order++;
                }
            }
        }
        return entitySerialization.toJson(list, null);
    }

    @Override
    public String loadTopAwarded() {
        return loadTopAwarded(null, null);
    }

    @Override
    public String loadTopAwarded(String language) {
        return loadTopAwarded(language, null);
    }

    @Override
    public String loadTopAwarded(Integer countOfMonths) {
        return loadTopAwarded(null, countOfMonths);
    }

    @Override
    public String loadTopAwarded(String language, Integer countOfMonths) {
        List<TopTenPojo> list = new LinkedList<>();

        if (StringUtils.isBlank(language)) {
            language = userSessionSource.getLocale().getLanguage();
        }

        language = language.toLowerCase();

        try (Transaction tx = persistence.createTransaction()) {
            String dateOfTop = "";
            if (countOfMonths != null) {
                if (countOfMonths.equals(0))
                    dateOfTop = "and pa.recognition_date > date_trunc('month', current_date)";
                else
                    dateOfTop = String.format("and pa.recognition_date > date_trunc('month', current_date) - interval '%d month'", countOfMonths);
            }

            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(String.format(
                    "SELECT DISTINCT " +
                            "  p.id, " +
                            "  p.group_id, " +
                            "  (CASE " +
                            "   WHEN 'en' = ?1 " +
                            "     THEN coalesce(p.first_name_latin, p.first_name) || ' ' || coalesce(p.last_name_latin, p.last_name) " +
                            "   ELSE p.first_name || ' ' || p.last_name " +
                            "   END)                           receiver, " +
                            "  count(pa.id) " +
                            "  OVER ( " +
                            "    PARTITION BY pa.receiver_id ) awards, " +
                            "  coalesce(pos.position_name_lang%d,pos.position_name_lang1) pos_name, " +
                            "  (select string_agg(ap.name,'$') from TSADV_SELECTED_PERSON_AWARD pa " +
                            "       join TSADV_AWARD_PROGRAM ap " +
                            "           on ap.id = pa.AWARD_PROGRAM_ID" +
                            "       where pa.PERSON_GROUP_ID = a.person_group_id " +
                            "           and pa.delete_ts is null " +
                            "           and ap.delete_ts is null " +
                            "           and pa.awarded = True" +
                            "   ) heart_award, " +
                            "  p.employee_number " +
                            "FROM tsadv_recognition pa " +
                            "  JOIN base_person p " +
                            "    ON p.group_id = pa.receiver_id " +
                            "       AND current_date BETWEEN p.start_date AND p.end_date " +
                            "      AND p.delete_ts IS NULL" +
                            "  JOIN base_assignment a " +
                            "    ON a.person_group_id = pa.receiver_id " +
                            "       AND current_date BETWEEN a.start_date AND a.end_date " +
                            "       AND a.primary_flag = TRUE " +
                            "       AND a.delete_ts IS NULL" +
                            "  JOIN tsadv_dic_assignment_status s " +
                            "    ON s.id = a.assignment_status_id " +
                            "       AND s.code = 'ACTIVE' " +
                            "  JOIN base_position pos " +
                            "    ON pos.group_id = a.position_group_id " +
                            "       AND pos.delete_ts is null " +
                            "       AND current_date BETWEEN pos.start_date AND pos.end_date " +
                            "where pa.delete_ts is null " +
                            " %s " +
                            "ORDER BY awards desc",
                    languageIndex(language),
                    dateOfTop));

            query.setParameter(1, language);

            query.setFirstResult(0);
            query.setMaxResults(10);

            List<Object[]> rows = query.getResultList();
            if (rows != null && !rows.isEmpty()) {
                int order = 1;
                for (Object[] row : rows) {
                    TopTenPojo topTenPojo = metadata.create(TopTenPojo.class);
                    topTenPojo.setOrder(order);
                    topTenPojo.setPId(row[0].toString());
                    topTenPojo.setPgId(row[1].toString());
                    topTenPojo.setFullName((String) row[2]);
                    topTenPojo.setCount((Long) row[3]);
                    topTenPojo.setPosition((String) row[4]);
                    topTenPojo.setEmployeeNumber(row[6].toString());

                    Object heartAward = row[5];
                    if (heartAward != null) {
                        topTenPojo.setHeartAward(heartAward.toString());
                    }

                    list.add(topTenPojo);

                    order++;
                }
            }
        }
        return entitySerialization.toJson(list, null);
    }

    @Override
    public String loadComingBirthdays() {
        List<TopTenPojo> list = new LinkedList<>();
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            Calendar calendar = Calendar.getInstance();
            int currentMonth = calendar.get(Calendar.MONTH) + 1;
            Query query = em.createNativeQuery(
                    "SELECT " +
                            "  p.id, " +
                            "  p.group_id, " +
                            "  p.last_name||' '||p.first_name, " +
                            "  p.date_of_birth " +
                            "FROM base_assignment a " +
                            "  JOIN base_person p " +
                            "    ON p.group_id = a.person_group_id " +
                            "where a.delete_ts is null and (?1 between a.start_date and a.end_date) " +
                            "and p.delete_ts is null and (?1 between p.start_date and p.end_date) " +
                            "and EXTRACT(MONTH FROM p.date_of_birth) = ?2");

            query.setParameter(1, CommonUtils.getSystemDate());
            query.setParameter(2, currentMonth);
            query.setFirstResult(0);
            query.setMaxResults(10);

            List<Object[]> rows = query.getResultList();

            if (rows != null && !rows.isEmpty()) {
                for (Object[] row : rows) {
                    TopTenPojo topTenPojo = metadata.create(TopTenPojo.class);
                    topTenPojo.setPId(row[0].toString());
                    topTenPojo.setPgId(row[1].toString());
                    topTenPojo.setFullName((String) row[2]);
                    topTenPojo.setBirthday(birthDateFormat.format(row[3]));
                    list.add(topTenPojo);
                }
            }
            tx.end();
        }

        return entitySerialization.toJson(list, null);
    }

    @Override
    public RcgQuestionPojo loadActiveQuestion(UUID personGroupId) {
        LoadContext<RcgQuestion> loadContext = LoadContext.create(RcgQuestion.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$RcgQuestion e " +
                        "where e.active = :active " +
                        "and e.id not in (select p.question.id from tsadv$PersonQuestionAnswer p where p.personGroup.id = :pgId and p.date = current_date)");
        query.setParameter("active", true);
        query.setParameter("pgId", personGroupId);
        loadContext.setQuery(query);
        loadContext.setView("rcgQuestion.edit");
        RcgQuestion rcgQuestion = dataManager.load(loadContext);
        if (rcgQuestion != null) {
            RcgQuestionPojo questionPojo = metadata.create(RcgQuestionPojo.class);
            questionPojo.setId(rcgQuestion.getId());
            questionPojo.setText(rcgQuestion.getText());
            questionPojo.setDescription(rcgQuestion.getDescription());
            questionPojo.setType(rcgQuestion.getAnswerType().getId());
            questionPojo.setCoins(rcgQuestion.getCoins());

            List<RcgQuestionAnswerPojo> answerPojos = new LinkedList<>();
            questionPojo.setAnswers(answerPojos);

            List<RcgQuestionAnswer> answers = rcgQuestion.getAnswers();
            if (answers != null && !answers.isEmpty()) {
                boolean isIcon = rcgQuestion.getAnswerType().equals(RcgAnswerType.ICON);
                for (RcgQuestionAnswer answer : answers) {
                    UUID answerId = answer.getId();

                    RcgQuestionAnswerPojo answerPojo = metadata.create(RcgQuestionAnswerPojo.class);
                    answerPojo.setId(answerId);

                    if (isIcon) {
                        answerPojo.setImage(String.format("./dispatch/rcg_answer_icon/%s", answerId.toString()));
                    } else {
                        answerPojo.setText(answer.getText());
                    }

                    answerPojos.add(answerPojo);
                }
            }

            return questionPojo;
        }

        return null;
    }

    @Override
    public boolean hasActiveQuestion() {
        LoadContext<RcgQuestion> loadContext = LoadContext.create(RcgQuestion.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$RcgQuestion e " +
                        "where e.active = :active " +
                        "and e.id not in (select p.question.id from tsadv$PersonQuestionAnswer p where p.personGroup.id = :pgId and p.date = current_date)");
        query.setParameter("active", true);
        query.setParameter("pgId", getCurrentPersonGroup().getId());
        loadContext.setQuery(query);
        return dataManager.getCount(loadContext) > 0;
    }

    @Override
    public void sendQuestionAnswer(PersonGroupExt personGroup, UUID questionId, UUID answerId, Long coins) {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            PersonQuestionAnswer personQuestionAnswer = metadata.create(PersonQuestionAnswer.class);
            personQuestionAnswer.setPersonGroup(personGroup);
            personQuestionAnswer.setAnswer(em.getReference(RcgQuestionAnswer.class, answerId));
            personQuestionAnswer.setDate(new Date());
            personQuestionAnswer.setQuestion(em.getReference(RcgQuestion.class, questionId));
            em.persist(personQuestionAnswer);

            if (coins != null && coins != 0) {
                PersonPoint personPoint = loadPersonPoint(personGroup.getId());
                if (personPoint == null) {
                    throw new RecognitionException(getMessage("author.wallet.points.null"));
                }

                Long personPoints = personPoint.getPoints();
                if (personPoints == null) personPoints = 0L;
                personPoint.setPoints(personPoints + coins);
                em.merge(personPoint);
            }

            tx.commit();
        }
    }

    @Override
    public Long getQuestionCoins(UUID questionId) {
        RcgQuestion rcgQuestion = dataManager.load(LoadContext.create(RcgQuestion.class).setId(questionId).setView(View.LOCAL));
        Long coins = null;
        if (rcgQuestion != null) {
            coins = rcgQuestion.getCoins();
        }
        return coins != null ? coins : 0L;
    }

    @Override
    public List<RcgFaq> loadRcgFaqs() {
        LoadContext<RcgFaq> loadContext = LoadContext.create(RcgFaq.class);
        LoadContext.Query query = LoadContext.createQuery("select e from tsadv$RcgFaq e order by e.order");
        loadContext.setQuery(query);
        loadContext.setView(View.LOCAL);
        return dataManager.loadList(loadContext);
    }

    @Override
    public String loadAllRcgFaq(String language) {
        return persistence.callInTransaction(em -> {
            int languageIndex = languageIndex(language);

            Query query = em.createNativeQuery(String.format(
                    "SELECT " +
                            "  rf.id, " +
                            "  rf.code, " +
                            "  rf.title_lang%d, " +
                            "  rf.content_lang%d " +
                            "FROM tsadv_rcg_faq rf " +
                            "WHERE rf.delete_ts IS NULL",
                    languageIndex,
                    languageIndex
            ));

            List<RcgFaqPojo> returnList = new ArrayList<>();
            List<Object[]> rows = query.getResultList();
            if (rows != null && !rows.isEmpty()) {
                for (Object[] row : rows) {
                    returnList.add(parseRcgFaqPojo(row));
                }
            }

            return new GsonBuilder().addSerializationExclusionStrategy(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    return f.getName().matches("__new|__detached|__removed");
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }
            }).disableHtmlEscaping().create().toJson(returnList);
        });
    }

    @Override
    public String loadRcgFaq(String faqId, String language) {
        return persistence.callInTransaction(em -> {
            int languageIndex = languageIndex(language);

            Query query = em.createNativeQuery(String.format(
                    "SELECT " +
                            "  rf.id, " +
                            "  rf.code, " +
                            "  rf.title_lang%d, " +
                            "  rf.content_lang%d " +
                            "FROM tsadv_rcg_faq rf " +
                            "WHERE rf.delete_ts IS NULL " +
                            "and rf.id = ?1",
                    languageIndex,
                    languageIndex
            ));

            query.setParameter(1, UUID.fromString(faqId));

            List<RcgFaqPojo> returnList = new ArrayList<>();
            List<Object[]> rows = query.getResultList();
            if (rows != null && !rows.isEmpty()) {
                for (Object[] row : rows) {
                    returnList.add(parseRcgFaqPojo(row));
                }
            }
            return new GsonBuilder().addSerializationExclusionStrategy(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    return f.getName().matches("__new|__detached|__removed");
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }
            }).disableHtmlEscaping().create().toJson(returnList);
        });
    }

    private RcgFaqPojo parseRcgFaqPojo(Object[] row) {
        RcgFaqPojo rcgFaqPojo = metadata.create(RcgFaqPojo.class);
        rcgFaqPojo.setId((UUID) row[0]);
        rcgFaqPojo.setCode((String) row[1]);
        rcgFaqPojo.setTitle((String) row[2]);
        rcgFaqPojo.setContent((String) row[3]);
        return rcgFaqPojo;
    }

    @Override
    public int uploadProfileImage(String imageContent) {
        Transaction tx = persistence.createTransaction();
        int coinCount = 0;
        try {
            EntityManager em = persistence.getEntityManager();

            byte[] decodedPhoto = Base64.getDecoder().decode(imageContent.getBytes("UTF-8"));

            PersonGroupExt personGroupExt = getCurrentPersonGroup();

            PersonExt personExt = personGroupExt.getPerson();

            boolean hasImage = employeeService.getImage(personExt.getId().toString()) != null;

            FileDescriptor userImage = metadata.create(FileDescriptor.class);
            userImage.setCreateDate(CommonUtils.getSystemDate());
            userImage.setName(personGroupExt.getId().toString());

            fileStorageAPI.saveFile(userImage, decodedPhoto);
            em.persist(userImage);

            Map<String, Object> params = new HashMap<>();
            params.put("pgId", personGroupExt.getId());
            params.put("systemDate", CommonUtils.getSystemDate());

            PersonExt p = commonService.getEntity(PersonExt.class, "select e " +
                            " from base$PersonExt e " +
                            " where e.group.id = :pgId " +
                            " and :systemDate between e.startDate and e.endDate ",
                    params,
                    "person.uploadImage.recognition");
            p.setImage(userImage);
            em.merge(p);

            if (!hasImage && isFirstChangePhoto(personGroupExt.getId(), em)) {
                PersonCoinLog personCoinLog = metadata.create(PersonCoinLog.class);
                personCoinLog.setActionType(LogActionType.ADDED_PHOTO); //Вы добавили предпочтение
                personCoinLog.setDate(new Date());
                personCoinLog.setOperationType(PointOperationType.RECEIPT);
                personCoinLog.setPersonGroup(personGroupExt);
                personCoinLog.setQuantity(10L);
                personCoinLog.setCoinType(RecognitionCoinType.COIN);
                em.persist(personCoinLog);

                PersonCoin personCoin = loadPersonCoin(personGroupExt.getId());
                if (personCoin == null) {
                    throw new RecognitionException(getMessage("author.wallet.hc.null"));
                }
                personCoin.setCoins(personCoin.getCoins() + 10);
                em.merge(personCoin);
                coinCount = 10;
            }

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RecognitionException(e.getMessage());
        } finally {
            tx.end();
        }
        return coinCount;
    }

    @Override
    public void uploadPersonImage(String imageContent) {
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();

            byte[] decodedPhoto = Base64.getDecoder().decode(imageContent.getBytes("UTF-8"));

            PersonGroupExt personGroupExt = getCurrentPersonGroup();

            FileDescriptor userImage = metadata.create(FileDescriptor.class);
            userImage.setCreateDate(CommonUtils.getSystemDate());
            userImage.setName(personGroupExt.getId().toString());

            fileStorageAPI.saveFile(userImage, decodedPhoto);
            em.persist(userImage);

            Map<String, Object> params = new HashMap<>();
            params.put("pgId", personGroupExt.getId());
            params.put("systemDate", CommonUtils.getSystemDate());

            PersonExt p = commonService.getEntity(PersonExt.class, "select e " +
                            " from base$PersonExt e " +
                            " where e.group.id = :pgId " +
                            " and :systemDate between e.startDate and e.endDate ",
                    params,
                    View.MINIMAL);
            p.setImage(userImage);
            em.merge(p);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RecognitionException(e.getMessage());
        } finally {
            tx.end();
        }
    }

    @Override
    public BaseResult changePersonImage(String employeeNumber, String imageContent) {
        BaseResult result = new BaseResult();
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();

            byte[] decodedPhoto = Base64.getDecoder().decode(imageContent.getBytes("UTF-8"));

            PersonExt person = commonService.getEntity(PersonExt.class, "select e " +
                            " from base$PersonExt e " +
                            " where e.employeeNumber = :employeeNumber " +
                            " and :systemDate between e.startDate and e.endDate ",
                    ParamsMap.of("employeeNumber", employeeNumber, "systemDate", CommonUtils.getSystemDate()),
                    "personExt.for.recognition.image");
            if (person == null) {
                throw new NullPointerException("Person not found with employeeNumber: '" + employeeNumber + "'");
            }
            FileDescriptor image = person.getImage();
            if (image == null) {
                throw new NullPointerException("user has not a image with employeeNumber: '" + employeeNumber + "'");
            }
            image.setCreateDate(CommonUtils.getSystemDate());
            image.setName(person.getGroup().getId().toString());

            fileStorageAPI.saveFile(image, decodedPhoto);
            em.merge(image);
            em.merge(person);
            tx.commit();
            returnResult(result, true, "image updated successfully", "");
        } catch (FileStorageException e) {
            e.printStackTrace();
            returnResult(result, true, "", e.getMessage());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            returnResult(result, true, "", e.getMessage());
        } catch (NullPointerException e) {
            e.printStackTrace();
            returnResult(result, true, "", e.getMessage());
        } finally {
            tx.end();
        }
        return result;
    }

    protected BaseResult returnResult(BaseResult result, boolean isSuccess, String successMessage, String errorMessage) {
        result.setSuccess(isSuccess);
        result.setSuccessMessage(successMessage);
        result.setErrorMessage(errorMessage);
        return result;
    }

    @Override
    public boolean giveCoinForPhoto(UUID personGroupId) {
        boolean hasImage = employeeService.getImageByPersonGroupId(personGroupId.toString()) != null;
        if (!hasImage) {
            try (Transaction tx = persistence.createTransaction()) {
                return isFirstChangePhoto(personGroupId, persistence.getEntityManager());
            }
        }
        return false;
    }

    @Override
    public boolean hasPersonAward(UUID authorPersonGroupId, UUID receiverPersonGroupId) {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(
                    "SELECT count(*) " +
                            "FROM tsadv_person_award rcg " +
                            "WHERE rcg.delete_ts IS NULL " +
                            "      AND rcg.author_id = ?1 " +
                            "      AND rcg.receiver_id = ?2 " +
                            "      AND rcg.status <> ?3 " +
                            "      AND extract(YEAR FROM rcg.date_) = extract(YEAR FROM current_date)");
            query.setParameter(1, authorPersonGroupId);
            query.setParameter(2, receiverPersonGroupId);
            query.setParameter(3, AwardStatus.DRAFT.getId());
            return (Long) query.getSingleResult() > 0;
        }
    }

    @Override
    public Date getCompareDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.getInstance().get(Calendar.YEAR) - 2, Calendar.JANUARY, 1);
        return calendar.getTime();
    }

    @Override
    public GoodsOrder checkoutCart(DicDeliveryAddress address) {
        PersonGroupExt personGroupExt = getCurrentPersonGroup();
        if (personGroupExt == null) {
            throw new RecognitionException(getMessage("user.person.null"));
        }

        PersonCoin personCoin = loadPersonCoin(personGroupExt.getId());
        if (personCoin == null) {
            throw new RecognitionException(getMessage("author.wallet.hc.null"));
        }

        LoadContext<GoodsCart> loadContext = LoadContext.create(GoodsCart.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$GoodsCart e " +
                        "where e.personGroup.id = :pgId " +
                        "and e.issued = False");
        query.setParameter("pgId", personGroupExt.getId());
        loadContext.setQuery(query);
        loadContext.setView("goodsCart.checkout");

        List<GoodsCart> goodsCarts = dataManager.loadList(loadContext);
        if (goodsCarts == null || goodsCarts.isEmpty()) {
            throw new RecognitionException(getMessage("checkout.gc.empty"));
        }

        List<String> errors = new ArrayList<>();

        checkAvailable(goodsCarts, errors);

        if (!errors.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append(getMessage("checkout.cart.available.error"));
            sb.append("<ul>");
            errors.forEach(s -> sb.append("<li>").append(s).append("</li>"));
            sb.append("</ul>");

            throw new CheckoutCartException(sb.toString());
        }

        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            Calendar calendar = Calendar.getInstance();

            String orderNumber = String.valueOf(calendar.getTime().getTime() + calendar.get(Calendar.MILLISECOND));
            GoodsOrder goodsOrder = metadata.create(GoodsOrder.class);
            goodsOrder.setStatus(GoodsOrderStatus.ON_APPROVAL);
            goodsOrder.setOrderNumber(orderNumber);
            goodsOrder.setOrderDate(new Date());
            goodsOrder.setPersonGroup(personGroupExt);
            goodsOrder.setDeliveryAddress(address);

            List<GoodsOrderDetail> details = new ArrayList<>();
            goodsOrder.setDetails(details);

            long totalSum = 0L;
            for (GoodsCart goodsCart : goodsCarts) {
                Goods goods = goodsCart.getGoods();

                GoodsOrderDetail detail = metadata.create(GoodsOrderDetail.class);
                detail.setGoods(goods);
                detail.setQuantity(goodsCart.getQuantity());
                detail.setGoodsOrder(goodsOrder);

                totalSum += goodsCart.getQuantity() * goods.getPrice();

                /**
                 * change issued value to TRUE
                 * */
                goodsCart.setIssued(true);

                goods = dataManager.reload(goods, "goods.for.service");
                if (goods.getCategory().getCode().equalsIgnoreCase("VOUCHER")) {
                    goodsOrder.setStatus(GoodsOrderStatus.DELIVERED);
                    detail.setQrCode(generateQrCode(em));
                    goodsOrder.setDeliveryAddress(null); //if category is voucher address have to null
                    generateQRCode(detail); // generate QR CODE image
                }
                details.add(detail);
            }

            boolean isHeartAward = checkHeartAward();

            int discount = 0;

            if (isHeartAward) {
                discount = recognitionConfig.getHeartAwardDiscount();
                if (discount != 0) {
                    totalSum = totalSum - Math.round((double) (totalSum * discount) / 100);
                }
            }

            goodsOrder.setDiscount(discount);
            goodsOrder.setTotalSum(totalSum);

            long coins = personCoin.getCoins();
            if (totalSum > coins) {
                throw new RecognitionException(getMessage("checkout.hc.enough"));
            }

            /**
             * save goods order
             * */
            em.persist(goodsOrder);

            /**
             * persist goods order details
             * */
            goodsOrder.getDetails().forEach(em::persist);

            /**
             * save goods cart with changed issued value
             * */
            goodsCarts.forEach(em::merge);

            /**
             * save person coin
             * */
            personCoin.setCoins(coins - totalSum);
            em.merge(personCoin);

            tx.commit();
            return goodsOrder;
        }
    }

    @Override
    public List<GoodsOrder> checkoutCartList(DicDeliveryAddress address) {
        List<GoodsOrder> goodsOrders = new ArrayList<>();

        PersonGroupExt personGroupExt = userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP);
        if (personGroupExt == null) {
            throw new RecognitionException(getMessage("user.person.null"));
        }

        PersonCoin personCoin = loadPersonCoin(personGroupExt.getId());
        if (personCoin == null) {
            throw new RecognitionException(getMessage("author.wallet.hc.null"));
        }

        LoadContext<GoodsCart> loadContext = LoadContext.create(GoodsCart.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$GoodsCart e " +
                        "where e.personGroup.id = :pgId " +
                        "and e.issued = False");
        query.setParameter("pgId", personGroupExt.getId());
        loadContext.setQuery(query);
        loadContext.setView("goodsCart.checkout");

        List<GoodsCart> goodsCarts = dataManager.loadList(loadContext);
        if (goodsCarts == null || goodsCarts.isEmpty()) {
            throw new RecognitionException(getMessage("checkout.gc.empty"));
        }

        List<String> errors = new ArrayList<>();

        checkAvailable(goodsCarts, errors);

        if (!errors.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append(getMessage("checkout.cart.available.error"));
            sb.append("<ul>");
            errors.forEach(s -> sb.append("<li>").append(s).append("</li>"));
            sb.append("</ul>");

            throw new CheckoutCartException(sb.toString());
        }

        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            List<GoodsOrderDetail> details = new ArrayList<>();

            GoodsOrder goodsOrder = createGoodsOrder(personGroupExt, address);
            goodsOrder.setDetails(details);
            goodsOrders.add(goodsOrder);

            boolean isHeartAward = checkHeartAward();

            long totalSum = 0L;

            boolean isFirstVoucher = true;
            for (GoodsCart goodsCart : goodsCarts) {
                Goods goods = goodsCart.getGoods();

                GoodsOrderDetail detail = metadata.create(GoodsOrderDetail.class);
                detail.setGoods(goods);
                detail.setQuantity(goodsCart.getQuantity());


                totalSum += goodsCart.getQuantity() * goods.getPrice();

                /**
                 * change issued value to TRUE
                 * */
                goodsCart.setIssued(true);
                goods = dataManager.reload(goods, "goods.for.service");

                if (goods.getCategory().getCode().equalsIgnoreCase("VOUCHER")) {
                    if (isFirstVoucher) {
                        isFirstVoucher = false;
                        setGoodsOrderDetails(em, goodsOrder, detail);
                    } else {
                        GoodsOrder goodsOrderOther = createGoodsOrder(personGroupExt, address);
                        goodsOrders.add(goodsOrderOther);

                        setGoodsOrderDetails(em, goodsOrderOther, detail);
                        List<GoodsOrderDetail> otherDetails = new ArrayList<>();
                        otherDetails.add(detail);
                        goodsOrderOther.setDetails(otherDetails);
                        persistGoodsOrder(em, goodsOrderOther, isHeartAward, totalSum, personCoin);
                        continue;
                    }
                } else {
                    detail.setGoodsOrder(goodsOrder);
                }
                details.add(detail);
            }


            /**
             * save goods cart with changed issued value
             * */
            goodsCarts.forEach(em::merge);

            persistGoodsOrder(em, goodsOrder, isHeartAward, totalSum, personCoin);

            em.merge(personCoin);

            tx.commit();
            return goodsOrders;
        }
    }

    protected void setGoodsOrderDetails(EntityManager em, GoodsOrder goodsOrder, GoodsOrderDetail detail) {
        detail.setGoodsOrder(goodsOrder);
        goodsOrder.setStatus(GoodsOrderStatus.DELIVERED);
        detail.setQrCode(generateQrCode(em));
        goodsOrder.setDeliveryAddress(null); //if category is voucher address have to null
        generateQRCode(detail); // generate QR CODE image
    }

    protected void persistGoodsOrder(EntityManager em, GoodsOrder goodsOrder, boolean isHeartAward,
                                     long totalSum, PersonCoin personCoin) {
        int discount = 0;

        if (isHeartAward) {
            discount = recognitionConfig.getHeartAwardDiscount();
            if (discount != 0) {
                totalSum = totalSum - Math.round((double) (totalSum * discount) / 100);
            }
        }

        goodsOrder.setDiscount(discount);
        goodsOrder.setTotalSum(totalSum);

        long coins = personCoin.getCoins();
        if (totalSum > coins) {
            throw new RecognitionException(getMessage("checkout.hc.enough"));
        }

        /**
         * save goods order
         * */
        em.persist(goodsOrder);

        /**
         * persist goods order details
         * */
        goodsOrder.getDetails().forEach(em::persist);

        /**
         * save person coin
         * */
        personCoin.setCoins(coins - totalSum);
    }

    public String generateQrCode(EntityManager em) {
        long count = (long) em.createNativeQuery("select count(*)\n" +
                "from tsadv_goods_order_detail where qr_code is not null").getSingleResult();
        count += 1;
        if (qrCode >= count) {
            qrCode = qrCode + 1;
        } else {
            qrCode = count;
        }
        return String.valueOf(qrCode);
    }

    protected GoodsOrder createGoodsOrder(PersonGroupExt personGroupExt, DicDeliveryAddress address) {
        Calendar calendar = Calendar.getInstance();
        String orderNumber = String.valueOf(calendar.getTime().getTime() + calendar.get(Calendar.MILLISECOND));
        GoodsOrder goodsOrder = metadata.create(GoodsOrder.class);
        goodsOrder.setStatus(GoodsOrderStatus.WAIT_DELIVERY);
        goodsOrder.setOrderNumber(orderNumber);
        goodsOrder.setOrderDate(new Date());
        goodsOrder.setPersonGroup(personGroupExt);
        goodsOrder.setDeliveryAddress(address);
        return goodsOrder;
    }

    @Override
    public void sendCheckoutNotification(GoodsOrder goodsOrder) {
        PersonGroupExt buyerPersonGroup = goodsOrder.getPersonGroup();

        UUID buyerPositionGroupId = employeeService.getPersonPositionGroup(buyerPersonGroup.getId());

        if (buyerPositionGroupId == null) {
            throw new NullPointerException(getMessage("buyer.position.null"));
        }

        UserExt buyerManagerUser = employeeService.findManagerByPositionGroup(buyerPositionGroupId, recognitionConfig.getHierarchyId());
        if (buyerManagerUser == null) {
            throw new RuntimeException(getMessage("buyer.manager.null"));
        }

        Map<String, Object> notificationParameters = new HashMap<>();
        notificationParameters.put("buyerNameRu", getFirstLastName(buyerPersonGroup, "ru"));
        notificationParameters.put("buyerNameEn", getFirstLastName(buyerPersonGroup, "en"));
        notificationParameters.put("orderNumber", goodsOrder.getOrderNumber());

        notificationService.sendParametrizedNotification(
                "recognition.checkout.notify",
                buyerManagerUser,
                notificationParameters);
    }

    @Override
    public void sendCheckoutNotifications(List<GoodsOrder> goodsOrderList) {
        for (GoodsOrder goodsOrder : goodsOrderList) {
            PersonGroupExt buyerPersonGroup = goodsOrder.getPersonGroup();

            UUID buyerPositionGroupId = employeeService.getPersonPositionGroup(buyerPersonGroup.getId());

            if (buyerPositionGroupId == null) {
                throw new NullPointerException(getMessage("buyer.position.null"));
            }

            UserExt buyerManagerUser = employeeService.findManagerByPositionGroup(buyerPositionGroupId, recognitionConfig.getHierarchyId());
            if (buyerManagerUser == null) {
                throw new RuntimeException(getMessage("buyer.manager.null"));
            }

            Map<String, Object> notificationParameters = new HashMap<>();
            notificationParameters.put("buyerNameRu", getFirstLastName(buyerPersonGroup, "ru"));
            notificationParameters.put("buyerNameEn", getFirstLastName(buyerPersonGroup, "en"));
            notificationParameters.put("orderNumber", goodsOrder.getOrderNumber());

            notificationService.sendParametrizedNotification(
                    "recognition.checkout.notify",
                    buyerManagerUser,
                    notificationParameters);
        }
    }

    private String getFirstLastName(PersonGroupExt personGroupExt, String lang) {
        PersonExt personExt = personGroupExt.getPerson();
        if (personExt == null) {
            throw new RuntimeException("Person not found!");
        }

        String resultFirstLastName = personExt.getFirstName() + " " + personExt.getLastName();
        if (lang.equals("en")) {
            String firstNameLatin = personExt.getFirstNameLatin(),
                    lastNameLatin = personExt.getLastNameLatin();
            if (StringUtils.isNotBlank(firstNameLatin) && StringUtils.isNotBlank(lastNameLatin)) {
                resultFirstLastName = firstNameLatin + " " + lastNameLatin;
            }
        }
        return resultFirstLastName;
    }

    @Override
    public LoadContext<PersonGroupExt> getSuggestionSearchLC() {
        LoadContext<PersonGroupExt> loadContext = LoadContext.create(PersonGroupExt.class);
        loadContext.getHints().put(QueryHints.READ_ONLY, HintValues.TRUE);

        LoadContext.Query query = LoadContext.createQuery(
                "select pg " +
                        "from base$PersonGroupExt pg " +
                        "join pg.list p " +
                        "join pg.assignments a " +
                        "join a.gradeGroup gg " +
                        "join gg.list g " +
                        "where :compareDate >= p.hireDate " +
                        "and current_date between g.startDate and g.endDate " +
                        "and g.recognitionNominate = True " +
                        "and current_date between p.startDate and p.endDate " +
                        "and current_date between a.startDate and a.endDate and a.primaryFlag = True " +
                        "and ((lower(p.firstName) like lower(:searchString) or lower(p.lastName) like lower(:searchString) or lower(concat(p.firstName, concat(' ', p.lastName))) like lower(:searchString)) " +
                        "or (lower(p.firstNameLatin) like lower(:searchString) or lower(p.lastNameLatin) like lower(:searchString)  or lower(concat(p.firstNameLatin,concat(' ', p.lastNameLatin))) like lower(:searchString)))");

        loadContext.setQuery(query);
        loadContext.setView("personGroup.search");
        return loadContext;
    }

    @Override
    public boolean checkAccessNominees(UUID personGroupId) {
        LoadContext<PersonGroupExt> loadContext = LoadContext.create(PersonGroupExt.class);
        loadContext.getHints().put(QueryHints.READ_ONLY, HintValues.TRUE);

        LoadContext.Query query = LoadContext.createQuery(
                "select pg " +
                        "from base$PersonGroupExt pg " +
                        "join pg.list p " +
                        "join pg.assignments a " +
                        "join a.gradeGroup gg " +
                        "join gg.list g " +
                        "where p.hireDate < :compareDate " +
                        "and current_date between g.startDate and g.endDate " +
                        "and g.recognitionNominate = True " +
                        "and current_date between p.startDate and p.endDate " +
                        "and current_date between a.startDate and a.endDate and a.primaryFlag = True " +
                        "and pg.id = :pgId");

        query.setParameter("compareDate", getCompareDate());
        query.setParameter("pgId", personGroupId);
        loadContext.setQuery(query);
        return dataManager.getCount(loadContext) > 0;
    }

    @Override
    public void resetActiveQuestions(UUID excludeQuestionId, boolean activateCurrent) {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            /**
             * deactivate all questions
             * */
            em.createQuery("update tsadv$RcgQuestion q set q.active = False").executeUpdate();

            if (activateCurrent) {
                RcgQuestion findRcgQuestion = em.find(RcgQuestion.class, excludeQuestionId, View.LOCAL);
                if (findRcgQuestion != null) {
                    findRcgQuestion.setActive(true);
                    em.merge(findRcgQuestion);
                }
            }

            tx.commit();
        }
    }

    @Override
    public Map<UUID, Boolean> checkAvailable(GoodsOrder goodsOrder) {
        Map<UUID, Boolean> resultMap = new HashMap<>();
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(
                    "SELECT " +
                            "  god.goods_id, " +
                            "  (CASE WHEN gw.quantity > -1 " +
                            "    THEN TRUE " +
                            "   ELSE FALSE END) available " +
                            "FROM tsadv_goods_order_detail god " +
                            "  LEFT JOIN tsadv_goods_warehouse gw " +
                            "    ON god.goods_id = gw.goods_id " +
                            "       AND gw.delete_ts IS NULL " +
                            "WHERE god.delete_ts IS NULL " +
                            "      AND god.goods_order_id = ?1");
            query.setParameter(1, goodsOrder.getId());

            List<Object[]> rows = query.getResultList();
            if (rows != null && !rows.isEmpty()) {
                for (Object[] row : rows) {
                    resultMap.put((UUID) row[0], (Boolean) row[1]);
                }
            }
        }
        return resultMap;
    }

    public void checkAvailable(List<GoodsCart> goodsCarts, List<String> errors) {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            String goodsSqlIds = goodsCarts.stream()
                    .map(goodsCart -> "'" + goodsCart.getId().toString() + "'")
                    .collect(Collectors.joining(","));

            Query query = em.createNativeQuery(
                    String.format(
                            "SELECT " +
                                    "  g.name_lang%d, " +
                                    "  (gw.quantity >= gc.quantity) available," +
                                    "  gw.quantity " +
                                    "FROM tsadv_goods_cart gc " +
                                    "  JOIN tsadv_goods_warehouse gw " +
                                    "    ON gw.goods_id = gc.goods_id " +
                                    "       AND gw.delete_ts IS NULL " +
                                    "  JOIN tsadv_goods g " +
                                    "    ON g.id = gc.goods_id " +
                                    "       AND g.delete_ts IS NULL " +
                                    "WHERE gc.id in (%s)",
                            languageIndex(),
                            goodsSqlIds
                    ));

            List<Object[]> rows = query.getResultList();
            if (rows != null && !rows.isEmpty()) {
                String leftMessagePattern = getMessage("checkout.cart.available.item.error");
                for (Object[] row : rows) {
                    boolean available = (boolean) row[1];
                    if (!available) {
                        errors.add(String.format(leftMessagePattern, row[0], row[2]));
                    }
                }
            }
        }
    }

    @Override
    public boolean checkHeartAward() {
        PersonGroupExt personGroupExt = getCurrentPersonGroup();
        return personGroupExt != null && checkHeartAward(personGroupExt.getId());
    }

    @Override
    public boolean checkHeartAward(UUID personGroupId) {
        if (personGroupId != null) {
            LoadContext<SelectedPersonAward> loadContext = LoadContext.create(SelectedPersonAward.class);
            LoadContext.Query query = LoadContext.createQuery(
                    "select e from tsadv$SelectedPersonAward e " +
                            "where e.personGroup.id = :pgId " +
                            "and e.awarded = True");
            query.setParameter("pgId", personGroupId);
            loadContext.setQuery(query);
            return dataManager.getCount(loadContext) > 0;
        }
        return false;
    }

    @Override
    public void removeRecognition(Recognition recognition) {
        PersonGroupExt authorPersonGroup = recognition.getAuthor();
        PersonGroupExt receiverPersonGroup = recognition.getReceiver();
        Long coins = recognition.getCoins();

        if (coins == null) {
            throw new NullPointerException();
        }

        if (authorPersonGroup == null) {
            throw new NullPointerException("Author is null or empty!");
        }

        if (receiverPersonGroup == null) {
            throw new NullPointerException("Receiver is null or empty!");
        }

        PersonPoint authorPoints = loadPersonPoint(authorPersonGroup.getId());
        if (authorPoints == null) {
            throw new NullPointerException(getMessage("author.wallet.points.null"));
        }

        PersonCoin receiverCoins = loadPersonCoin(receiverPersonGroup.getId());
        if (receiverCoins == null) {
            throw new NullPointerException(getMessage("receiver.wallet.hc.null"));
        }

        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            /**
             * update author points
             * */
            authorPoints.setPoints(authorPoints.getPoints() + coins);
            em.merge(authorPoints);

            /**
             * update receiver coins
             * */
            receiverCoins.setCoins(receiverCoins.getCoins() - coins);
            em.merge(receiverCoins);

            em.remove(recognition);

            tx.commit();
        }
    }

    @Override
    public boolean hasRecognitionLoginLog() {
        UserSession userSession = userSessionSource.getUserSession();
        LoadContext<RecognitionLoginLog> loadContext = LoadContext.create(RecognitionLoginLog.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$RecognitionLoginLog e " +
                        "where e.login = :login " +
                        "and e.sessionId = :sessionId");
        query.setParameter("login", userSession.getUser().getLogin());
        query.setParameter("sessionId", userSession.getId());
        loadContext.setQuery(query);
        return dataManager.getCount(loadContext) > 0;
    }

    @Override
    public void createRecognitionLoginLog() {
        try (Transaction tx = persistence.createTransaction()) {
            UserSession userSession = userSessionSource.getUserSession();

            EntityManager em = persistence.getEntityManager();

            RecognitionLoginLog recognitionLoginLog = metadata.create(RecognitionLoginLog.class);
            recognitionLoginLog.setLogin(userSession.getUser().getLogin());
            recognitionLoginLog.setSessionId(userSession.getId());
            recognitionLoginLog.setDateTime(new Date());
            em.persist(recognitionLoginLog);
            tx.commit();
        }
    }

    @Override
    public void refreshWarehouse(Goods goods, Long quantity, PointOperationType pointOperationType) {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            GoodsWarehouse goodsWarehouse = loadGoodsWarehouse(goods);
            if (goodsWarehouse == null) {
                if (pointOperationType.equals(PointOperationType.ISSUE)) {
                    throw new RecognitionException("Operation not support! GoodsWarehouse for goods is null or empty!");
                }
                goodsWarehouse = metadata.create(GoodsWarehouse.class);
                goodsWarehouse.setGoods(goods);
                goodsWarehouse.setQuantity(quantity);
                em.persist(goodsWarehouse);
            } else {
                Long currentQuantity = goodsWarehouse.getQuantity();
                switch (pointOperationType) {
                    case RECEIPT: {
                        goodsWarehouse.setQuantity(currentQuantity + quantity);
                        break;
                    }
                    case ISSUE: {
                        goodsWarehouse.setQuantity(currentQuantity - quantity);
                        break;
                    }
                }
                em.merge(goodsWarehouse);
            }

            tx.commit();
        }
    }

    private GoodsWarehouse loadGoodsWarehouse(Goods goods) {
        LoadContext<GoodsWarehouse> loadContext = LoadContext.create(GoodsWarehouse.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$GoodsWarehouse e " +
                        "where e.goods.id = :goodsIs");
        query.setParameter("goodsIs", goods.getId());
        loadContext.setQuery(query);
        loadContext.setView(View.LOCAL);
        return dataManager.load(loadContext);
    }

    @Override
    public boolean hasInSuggestionSearch(UUID personGroupId) {
        LoadContext<PersonGroupExt> loadContext = LoadContext.create(PersonGroupExt.class);
        loadContext.getHints().put(QueryHints.READ_ONLY, HintValues.TRUE);

        LoadContext.Query query = LoadContext.createQuery(
                "select pg " +
                        "from base$PersonGroupExt pg " +
                        "join pg.list p " +
                        "join pg.assignments a " +
                        "join a.gradeGroup gg " +
                        "join gg.list g " +
                        "where p.hireDate < :compareDate " +
                        "and current_date between g.startDate and g.endDate " +
                        "and g.recognitionNominate = True " +
                        "and current_date between p.startDate and p.endDate " +
                        "and current_date between a.startDate and a.endDate and a.primaryFlag = True " +
                        "and pg.id = :pgId");

        query.setParameter("compareDate", getCompareDate());
        query.setParameter("pgId", personGroupId);

        loadContext.setQuery(query);
        return dataManager.getCount(loadContext) > 0;
    }

    private boolean isFirstChangePhoto(UUID personGroupId, EntityManager em) {
        Query query = em.createNativeQuery(
                "SELECT count(*) " +
                        "FROM tsadv_person_coin_log log " +
                        "WHERE log.delete_ts IS NULL " +
                        "      AND log.action_type = ?1 " +
                        "      AND log.operation_type = ?2 " +
                        "      AND log.person_group_id = ?3");
        query.setParameter(1, LogActionType.ADDED_PHOTO.getId());
        query.setParameter(2, PointOperationType.RECEIPT.getId());
        query.setParameter(3, personGroupId);
        return ((Long) query.getSingleResult()) == 0;
    }

    @Override
    public String sendLike(String recognitionId) {
        RestResult restResult = new RestResult();

        try (Transaction tx = persistence.createTransaction()) {
            if (StringUtils.isBlank(recognitionId)) {
                throw new RecognitionException("Recognition ID is null or empty!");
            }

            EntityManager em = persistence.getEntityManager();
            em.setSoftDeletion(false);

            PersonGroupExt personGroupExt = getCurrentPersonGroup();
            if (personGroupExt == null) {
                throw new RecognitionException(getMessage("user.person.null"));
            }

            Recognition recognition = em.getReference(Recognition.class, UUID.fromString(recognitionId));

            RecognitionLike recognitionLike = loadRecognitionLike(recognition.getId(), personGroupExt.getId());
            if (recognitionLike != null) {
                em.remove(recognitionLike);
            } else {
                recognitionLike = metadata.create(RecognitionLike.class);
                recognitionLike.setPersonGroup(personGroupExt);
                recognitionLike.setRecognition(recognition);
                em.persist(recognitionLike);
            }

            tx.commit();

            restResult.setSuccess(true);
        } catch (Exception ex) {
            restResult.setSuccess(false);
            restResult.setMessage(ex.getMessage());
        }
        return gson.toJson(restResult);
    }

    @Override
    public String sendLike(String employeeNumber, String recognitionId) {
        RestResult restResult = new RestResult();

        try (Transaction tx = persistence.createTransaction()) {
            if (StringUtils.isBlank(recognitionId)) {
                throw new RecognitionException("Recognition ID is null or empty!");
            }

            EntityManager em = persistence.getEntityManager();
            em.setSoftDeletion(false);

            UUID personGroupId = getPersonGroupId(employeeNumber);

            PersonGroupExt personGroupExt = em.find(PersonGroupExt.class, personGroupId);

            if (personGroupExt == null) {
                throw new RecognitionException(getMessage("user.person.null"));
            }

            Recognition recognition = em.getReference(Recognition.class, UUID.fromString(recognitionId));

            RecognitionLike recognitionLike = loadRecognitionLike(recognition.getId(), personGroupExt.getId());
            if (recognitionLike != null) {
                em.remove(recognitionLike);
            } else {
                recognitionLike = metadata.create(RecognitionLike.class);
                recognitionLike.setPersonGroup(personGroupExt);
                recognitionLike.setRecognition(recognition);
                em.persist(recognitionLike);
            }

            tx.commit();

            restResult.setSuccess(true);
        } catch (Exception ex) {
            restResult.setSuccess(false);
            restResult.setMessage(ex.getMessage());
        }
        return gson.toJson(restResult);
    }

    @Override
    public PersonAward findDraftPersonAward(UUID receiverPersonGroupId) {
        PersonGroupExt currentPersonGroupExt = getCurrentPersonGroup();
        if (currentPersonGroupExt == null) {
            throw new RecognitionException(getMessage("user.person.null"));
        }

        return findDraftPersonAward(currentPersonGroupExt.getId(), receiverPersonGroupId);
    }

    @Override
    public PersonAward findDraftPersonAward(UUID authorPersonGroupId, UUID receiverPersonGroupId) {
        LoadContext<PersonAward> loadContext = LoadContext.create(PersonAward.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$PersonAward e " +
                        "join e.type t " +
                        "join e.awardProgram ap " +
                        "where e.author.id = :authorId " +
                        "and e.receiver.id = :receiverId " +
                        "and e.status = :status " +
                        "and t.code = 'HEART_AWARD' " +
                        "and ap.active = True");
        query.setParameter("authorId", authorPersonGroupId);
        query.setParameter("receiverId", receiverPersonGroupId);
        query.setParameter("status", AwardStatus.DRAFT.getId());
        loadContext.setQuery(query);
        loadContext.setView("personAward.edit");
        return dataManager.load(loadContext);
    }

    @Override
    public boolean hasDraftPersonAward(UUID authorPersonGroupId, UUID receiverPersonGroupId) {
        LoadContext<PersonAward> loadContext = LoadContext.create(PersonAward.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$PersonAward e " +
                        "join e.type t " +
                        "join e.awardProgram ap " +
                        "where e.author.id = :authorId " +
                        "and e.receiver.id = :receiverId " +
                        "and e.status = :status " +
                        "and t.code = 'HEART_AWARD' " +
                        "and ap.active = True");
        query.setParameter("authorId", authorPersonGroupId);
        query.setParameter("receiverId", receiverPersonGroupId);
        query.setParameter("status", AwardStatus.DRAFT.getId());
        loadContext.setQuery(query);
        return dataManager.getCount(loadContext) > 0;
    }

    @Override
    public String loadPersonMedals(String personGroupId) {
        List<PersonMedalPojo> list = new LinkedList<>();
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            //old query
            /*"select * from (SELECT " +
                    " distinct m.id, " +
                    "  m.lang_name%d, " +
                    "  count(pm.id) " +
                    "  OVER ( " +
                    "    PARTITION BY pm.medal_id ) cnt " +
                    "FROM tsadv_medal m " +
                    "  LEFT JOIN tsadv_person_medal pm " +
                    "    ON pm.medal_id = m.id and pm.PERSON_GROUP_ID = ?1 and pm.delete_ts is null " +
                    "where m.delete_ts is null) t " +
                    "order by t.cnt desc"*/
            Query query = em.createNativeQuery(String.format(
                    "select * from (SELECT " +
                            " distinct m.id, " +
                            "  m.lang_name%d," +
                            "  count(pm.id) " +
                            "  OVER ( " +
                            "    PARTITION BY pm.medal_id ) cnt, " +
                            "  m.sort " +
                            "FROM tsadv_medal m " +
                            "  JOIN tsadv_person_medal pm " +
                            "    ON pm.medal_id = m.id and pm.PERSON_GROUP_ID = ?1 and pm.delete_ts is null " +
                            "where m.delete_ts is null) t " +
                            "order by t.sort asc",
                    languageIndex()));
            query.setParameter(1, UUID.fromString(personGroupId));

            List<Object[]> rows = query.getResultList();
            if (rows != null && !rows.isEmpty()) {
                for (Object[] row : rows) {
                    PersonMedalPojo personMedalPojo = metadata.create(PersonMedalPojo.class);
                    personMedalPojo.setMedalId(row[0].toString());
                    personMedalPojo.setMedalName((String) row[1]);
                    personMedalPojo.setCount((Long) row[2]);
                    personMedalPojo.setSort((Integer) row[3]);
                    list.add(personMedalPojo);
                }
            }

            tx.end();
        }
        return entitySerialization.toJson(list, null);
    }

    @Override
    public Long personMedalCount(UUID personGroupId) {
        Long count;
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(
                    "select count(*) from tsadv_person_medal pm " +
                            "where pm.PERSON_GROUP_ID = ?1 and pm.delete_ts is null");
            query.setParameter(1, personGroupId);
            count = (Long) query.getSingleResult();
            tx.end();
        }
        return count;
    }

    @Override
    public List<AwardProgramPerson> loadAwardProgramPersons(AwardProgram awardProgram, int firstResult, int maxResult) {
        List<AwardProgramPerson> list = new ArrayList<>();
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            int languageIndex = languageIndex();
            Query query = em.createNativeQuery(
                    "SELECT " +
                            "  p.group_id, " +
                            "  p.id, " +
                            "  p.first_name, " +
                            "  p.last_name, " +
                            "  p.middle_name, " +
                            "  p.first_name_latin, " +
                            "  p.last_name_latin, " +
                            "  p.middle_name_latin, " +
                            "  t.cnt " +
                            "FROM ( " +
                            "       SELECT " +
                            "         pa.receiver_id, " +
                            "         count(*) cnt " +
                            "       FROM tsadv_person_award pa " +
                            "       WHERE pa.delete_ts IS NULL " +
                            "       AND pa.status = 'NOMINATED' " +
                            "       AND pa.award_program_id = ?1 " +
                            "       and not exists (select * from TSADV_SELECTED_PERSON_AWARD where delete_ts is null and PERSON_GROUP_ID = pa.receiver_id and AWARD_PROGRAM_ID = ?1) " +
                            "       GROUP BY pa.receiver_id) t " +
                            "  JOIN base_person_group pg " +
                            "    ON pg.id = t.receiver_id " +
                            "  JOIN base_person p " +
                            "    ON p.group_id = pg.id " +
                            "WHERE pg.delete_ts IS NULL " +
                            "      AND p.delete_ts IS NULL " +
                            "      AND current_date BETWEEN p.start_date AND p.end_date");

            query.setParameter(1, awardProgram.getId());
            query.setFirstResult(firstResult);
            query.setMaxResults(maxResult);

            List<Object[]> rows = query.getResultList();
            if (rows != null && !rows.isEmpty()) {
                for (Object[] row : rows) {
                    AwardProgramPerson programPerson = metadata.create(AwardProgramPerson.class);
                    programPerson.setAwardProgram(awardProgram);
                    programPerson.setCount((Long) row[8]);

                    PersonExt personExt = metadata.create(PersonExt.class);
                    personExt.setGroup(em.getReference(PersonGroupExt.class, (UUID) row[0]));
                    personExt.setId((UUID) row[1]);
                    personExt.setFirstName((String) row[2]);
                    personExt.setLastName((String) row[3]);
                    personExt.setMiddleName((String) row[4]);
                    personExt.setFirstNameLatin((String) row[5]);
                    personExt.setLastNameLatin((String) row[6]);
                    personExt.setMiddleNameLatin((String) row[7]);
                    programPerson.setPerson(personExt);

                    list.add(programPerson);
                }
            }
            tx.end();
        }
        return list;
    }

    @Override
    public int getAwardProgramPersonsCount(AwardProgram awardProgram) {
        if (awardProgram == null) return 0;
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager entityManager = persistence.getEntityManager();
            Query query = entityManager.createNativeQuery("SELECT count(p.id)" +
                    "FROM ( " +
                    "       SELECT " +
                    "         pa.receiver_id, " +
                    "         count(*) cnt " +
                    "       FROM tsadv_person_award pa " +
                    "       WHERE pa.delete_ts IS NULL " +
                    "       AND pa.status = 'NOMINATED' " +
                    "       AND pa.award_program_id = ?1 " +
                    "       and not exists (select * from TSADV_SELECTED_PERSON_AWARD where delete_ts is null and PERSON_GROUP_ID = pa.receiver_id and AWARD_PROGRAM_ID = ?1) " +
                    "       GROUP BY pa.receiver_id) t " +
                    "  JOIN base_person_group pg " +
                    "    ON pg.id = t.receiver_id " +
                    "  JOIN base_person p " +
                    "    ON p.group_id = pg.id " +
                    "WHERE pg.delete_ts IS NULL " +
                    "      AND p.delete_ts IS NULL " +
                    "      AND current_date BETWEEN p.start_date AND p.end_date");
            query.setParameter(1, awardProgram.getId());
            Long result = (Long) query.getSingleResult();
            return result.intValue();
        }
    }

    @Override
    public List<AwardProgramPerson> loadAwardProgramPersonsWithFilter(AwardProgram awardProgram, int firstResult,
                                                                      int maxResult, String filterParam) {
        List<AwardProgramPerson> list = new ArrayList<>();
        if (awardProgram == null) return list;

        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery("SELECT  " +
                    "       p.group_id, " +
                    "       p.id, " +
                    "       p.first_name, " +
                    "       p.last_name, " +
                    "       p.middle_name, " +
                    "       p.first_name_latin, " +
                    "       p.last_name_latin, " +
                    "       p.middle_name_latin, " +
                    "       t.cnt " +
                    "FROM ( " +
                    "       SELECT pa.receiver_id, " +
                    "              count(*) cnt " +
                    "       FROM tsadv_person_award pa " +
                    "       WHERE pa.delete_ts IS NULL " +
                    "         AND pa.status = 'NOMINATED' " +
                    "         AND pa.award_program_id = ?1 " +
                    "         and not exists(select * " +
                    "                        from TSADV_SELECTED_PERSON_AWARD " +
                    "                        where delete_ts is null " +
                    "                          and PERSON_GROUP_ID = pa.receiver_id " +
                    "                          and AWARD_PROGRAM_ID = ?1) " +
                    "       GROUP BY pa.receiver_id) t " +
                    "       JOIN base_person_group pg " +
                    "            ON pg.id = t.receiver_id " +
                    "       JOIN base_person p " +
                    "            ON p.group_id = pg.id " +
                    "WHERE pg.delete_ts IS NULL " +
                    "  AND p.delete_ts IS NULL " +
                    "  AND current_date BETWEEN p.start_date AND p.end_date " +
                    "  AND lower(concat(p.first_name, concat(' ', concat(p.last_name, concat(' ', coalesce(p.middle_name, '')))))) like lower(?2)");
            query.setParameter(1, awardProgram.getId());
            query.setParameter(2, "%" + filterParam + "%");

            List<Object[]> rows = query.getResultList();
            if (rows != null && !rows.isEmpty()) {
                for (Object[] row : rows) {
                    AwardProgramPerson programPerson = metadata.create(AwardProgramPerson.class);
                    programPerson.setAwardProgram(awardProgram);
                    programPerson.setCount((Long) row[8]);

                    PersonExt personExt = metadata.create(PersonExt.class);
                    personExt.setGroup(em.getReference(PersonGroupExt.class, (UUID) row[0]));
                    personExt.setId((UUID) row[1]);
                    personExt.setFirstName((String) row[2]);
                    personExt.setLastName((String) row[3]);
                    personExt.setMiddleName((String) row[4]);
                    personExt.setFirstNameLatin((String) row[5]);
                    personExt.setLastNameLatin((String) row[6]);
                    personExt.setMiddleNameLatin((String) row[7]);
                    programPerson.setPerson(personExt);

                    list.add(programPerson);
                }
            }
            tx.end();
        }
        return list;
    }

    @Override
    public void addShortList(Set<AwardProgramPerson> awardProgramPersons) {
        if (awardProgramPersons != null && !awardProgramPersons.isEmpty()) {
            try (Transaction tx = persistence.createTransaction()) {
                EntityManager em = persistence.getEntityManager();

                for (AwardProgramPerson awardProgramPerson : awardProgramPersons) {
                    SelectedPersonAward selectedPersonAward = metadata.create(SelectedPersonAward.class);
                    selectedPersonAward.setAwarded(false);
                    selectedPersonAward.setAwardProgram(awardProgramPerson.getAwardProgram());
                    selectedPersonAward.setPersonGroup(awardProgramPerson.getPerson().getGroup());
                    em.persist(selectedPersonAward);
                }
                tx.commit();
            }
        }
    }

    @Override
    public void nominate(Set<SelectedPersonAward> selectedPersonAwards) {
        if (selectedPersonAwards != null && !selectedPersonAwards.isEmpty()) {
            try (Transaction tx = persistence.createTransaction()) {
                EntityManager em = persistence.getEntityManager();
                for (SelectedPersonAward selectedPersonAward : selectedPersonAwards) {
                    if (BooleanUtils.isFalse(selectedPersonAward.getAwarded())) {
                        selectedPersonAward.setAwarded(true);
                        em.merge(selectedPersonAward);
                    }
                }
                tx.commit();
            }
        }
    }

    protected long goodsCount(String categoryId) {
        LoadContext<Goods> loadContext = LoadContext.create(Goods.class);
        LoadContext.Query query = LoadContext.createQuery(
                String.format(
                        "select e " +
                                "from tsadv$Goods e %s",
                        categoryId != null ? "where e.category.id = :cId or e.category.id in (select c.id from tsadv$DicGoodsCategory c where c.parent.id = :cId)" : ""));
        if (categoryId != null) {
            query.setParameter("cId", UUID.fromString(categoryId));
        }
        loadContext.setQuery(query);
        return dataManager.getCount(loadContext);
    }

    private long wishListCount(UUID personGroupId) {
        LoadContext<Goods> loadContext = LoadContext.create(Goods.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e " +
                        "from tsadv$GoodsWishList e " +
                        "where e.personGroup.id = :pgId");
        query.setParameter("pgId", personGroupId);
        loadContext.setQuery(query);
        return dataManager.getCount(loadContext);
    }

    @Override
    public String loadGoods(int page, long lastCount, String categoryId, String sort) {
        int perPageCount = PageRangeInfo.GOODS.getPerPageCount();

        if ("-1".equals(categoryId)) categoryId = null;
        if ("0".equals(sort)) {
            sort = null;
        } else {
            switch (sort) {
                case "1": {
                    sort = "g.name_lang${languageIndex}";
                    break;
                }
                case "2": {
                    sort = "g.price";
                    break;
                }
            }
        }

        GoodsPageInfo goodsPageInfo = metadata.create(GoodsPageInfo.class);

        long goodsCount = goodsCount(categoryId);
        if (page == 1) {
            lastCount = goodsCount;
        }

        PageInfo pageInfo = metadata.create(PageInfo.class);
        pageInfo.setTotalRowsCount(lastCount);
        pageInfo.setPagesCount(PageInfo.getPageCount(lastCount, perPageCount));
        goodsPageInfo.setPageInfo(pageInfo);

        long difference = 0;

        if (page != 1 && lastCount != goodsCount) {
            difference = goodsCount - lastCount;
        }

        List<GoodsPojo> list = new ArrayList<>();
        goodsPageInfo.setGoods(list);

        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            int languageIndex = languageIndex();

            String stringQuery = String.format(
                    "SELECT " +
                            "  g.id, " +
                            "  g.name_lang${languageIndex}, " +
                            "  g.description_lang${languageIndex}, " +
                            "  g.price, " +
                            "  gc.lang_value${languageIndex}," +
                            "  cart.id in_cart," +
                            "  wl.id in_wish_list," +
                            "  coalesce(gw.quantity,0) goods_quantity " +
                            "FROM tsadv_goods g " +
                            "  JOIN tsadv_dic_goods_category gc " +
                            "    ON gc.id = g.category_id " +
                            "left join TSADV_GOODS_CART cart " +
                            "   on cart.GOODS_ID = g.id " +
                            "       and cart.delete_ts is null " +
                            "       and cart.issued = False " +
                            "       and cart.PERSON_GROUP_ID = ?1 " +
                            "left join TSADV_GOODS_WISH_LIST wl " +
                            "   on wl.goods_id = g.id " +
                            "       and wl.delete_ts is null " +
                            "       and wl.person_group_id = ?1 " +
                            "left join tsadv_goods_warehouse gw " +
                            "   on gw.goods_id = g.id " +
                            "       and gw.delete_ts is null " +
                            "WHERE g.delete_ts IS NULL %s %s",
                    categoryId != null ?
                            " and gc.id = ?2 " +
                                    "or gc.id in (select c.id from TSADV_DIC_GOODS_CATEGORY c where c.parent_id = ?2)" : "",
                    sort != null ? "order by " + sort : "");

            Map<String, Object> valuesMap = new HashMap<>();
            valuesMap.put("languageIndex", languageIndex);

            StrSubstitutor strSubstitutor = new StrSubstitutor(valuesMap);

            Query query = em.createNativeQuery(strSubstitutor.replace(stringQuery));
            query.setParameter(1, getCurrentPersonGroup().getId());
            if (categoryId != null) {
                query.setParameter(2, UUID.fromString(categoryId));
            }

            query.setFirstResult((int) difference + ((page - 1) * perPageCount));
            query.setMaxResults(perPageCount);

            List<Object[]> resultList = query.getResultList();
            if (resultList != null && !resultList.isEmpty()) {
                for (Object[] row : resultList) {
                    GoodsPojo goodsPojo = metadata.create(GoodsPojo.class);
                    goodsPojo.setId((UUID) row[0]);
                    goodsPojo.setName((String) row[1]);
                    goodsPojo.setDescription((String) row[2]);
                    goodsPojo.setPrice((Double) row[3]);
                    goodsPojo.setCategory((String) row[4]);
                    goodsPojo.setInCart(row[5] != null ? 1 : 0);
                    goodsPojo.setInWishList(row[6] != null ? 1 : 0);
                    goodsPojo.setQuantity((Long) row[7]);
                    list.add(goodsPojo);
                }
            }

            tx.end();
        }
        return entitySerialization.toJson(goodsPageInfo, null);
    }

    @Override
    public void addGoodsToCart(UUID goodsId) {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            GoodsCart goodsCart = getGoodsCart(goodsId, getCurrentPersonGroup().getId());
            if (goodsCart == null) {
                goodsCart = metadata.create(GoodsCart.class);
                goodsCart.setGoods(em.getReference(Goods.class, goodsId));
                goodsCart.setIssued(false);
                goodsCart.setQuantity(1L);
                goodsCart.setPersonGroup(getCurrentPersonGroup());
                em.persist(goodsCart);
            } else {
                goodsCart.setQuantity(goodsCart.getQuantity() + 1);
                em.merge(goodsCart);
            }

            tx.commit();
        }
    }

    @Override
    public void addToWishList(UUID goodsId) {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            PersonGroupExt currentPersonGroup = getCurrentPersonGroup();

            GoodsWishList goodsWishList = findGoodsWishList(goodsId, currentPersonGroup.getId());
            if (goodsWishList == null) {
                goodsWishList = metadata.create(GoodsWishList.class);
                goodsWishList.setPersonGroup(currentPersonGroup);
                goodsWishList.setGoods(em.getReference(Goods.class, goodsId));
                em.persist(goodsWishList);
            } else {
                em.remove(goodsWishList);
            }
            tx.commit();
        }
    }

    private GoodsWishList findGoodsWishList(UUID goodsId, UUID personGroupId) {
        LoadContext<GoodsWishList> loadContext = LoadContext.create(GoodsWishList.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$GoodsWishList e " +
                        "where e.personGroup.id = :pgId " +
                        "and e.goods.id = :goodsId");
        query.setParameter("pgId", personGroupId);
        query.setParameter("goodsId", goodsId);
        loadContext.setQuery(query);
        loadContext.setView(View.MINIMAL);
        return dataManager.load(loadContext);
    }

    private GoodsCart getGoodsCart(UUID goodsId, UUID personGroupId) {
        LoadContext<GoodsCart> loadContext = LoadContext.create(GoodsCart.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$GoodsCart e " +
                        "where e.personGroup.id = :pgId " +
                        "and e.goods.id = :gId " +
                        "and e.issued = False");
        query.setParameter("pgId", personGroupId);
        query.setParameter("gId", goodsId);
        loadContext.setQuery(query);
        loadContext.setView(View.LOCAL);
        return dataManager.load(loadContext);
    }

    @Override
    public void removeGoods(GoodsCart goodsCart) {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            em.remove(goodsCart);
            tx.commit();
        }
    }

    @Override
    public Long goodsCartCount() {
        LoadContext<GoodsCart> loadContext = LoadContext.create(GoodsCart.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$GoodsCart e " +
                        "where e.personGroup.id = :pgId " +
                        "and e.issued = False");
        query.setParameter("pgId", getCurrentPersonGroup().getId());
        loadContext.setQuery(query);
        return dataManager.getCount(loadContext);
    }

    private RecognitionLike loadRecognitionLike(UUID recognitionId, UUID personGroupId) {
        LoadContext<RecognitionLike> loadContext = LoadContext.create(RecognitionLike.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$RecognitionLike e " +
                        "where e.recognition.id = :rcgId " +
                        "and e.personGroup.id = :pgId");
        query.setParameter("rcgId", recognitionId);
        query.setParameter("pgId", personGroupId);
        loadContext.setQuery(query);
        loadContext.setView(View.MINIMAL);
        return dataManager.load(loadContext);
    }

    private PersonPojo findMyTeamLiker(UUID recognitionId, String positionPath, UUID excludePersonGroup, int languageIndex) {
        return persistence.callInTransaction(em -> {
            Query query = em.createNativeQuery(String.format(
                    "SELECT " +
                            "  p.id, " +
                            "  p.group_id, " +
                            "  %s " +
                            "FROM base_assignment a " +
                            "  JOIN base_person p " +
                            "    ON p.group_id = a.person_group_id " +
                            "WHERE a.delete_ts IS NULL AND current_date BETWEEN a.start_date AND a.end_date " +
                            "      AND p.delete_ts IS NULL AND current_date BETWEEN p.start_date AND p.end_date " +
                            "      AND a.position_group_id IN (%s) " +
                            "      AND p.group_id != ?1 and p.group_id IN (SELECT l.person_group_id " +
                            "                         FROM tsadv_recognition_like l " +
                            "                         WHERE l.delete_ts is null and l.recognition_id = ?2 and l.person_group_id != ?1) " +
                            "limit 1",
                    languageIndex == 3 ? "p.first_name_latin||' '||p.last_name_latin" : "p.first_name||' '||p.last_name",
                    positionPath));

            query.setParameter(1, excludePersonGroup);
            query.setParameter(2, recognitionId);
            query.setFirstResult(0);
            query.setMaxResults(1);

            Object rowObject = query.getFirstResult();
            if (rowObject != null) {
                Object[] row = (Object[]) rowObject;
                PersonPojo personPojo = metadata.create(PersonPojo.class);
                personPojo.setId((UUID) row[1]);
                personPojo.setName((String) row[2]);
                return personPojo;
            }
            return null;
        });
    }

    private List<RecognitionCommentPojo> loadCommentsByRecognition(String recognitionId, int page) {
        int maxResults = page == 1 ? 2 : 10;
        int offset = 0;
        if (page > 1) {
            offset = 2 + (page - 2) * 10;
        }
        return loadCommentsByRecognition(recognitionId, offset, maxResults, languageIndex(), isAutomaticTranslate());
    }

    @Override
    public Long recognitionCommentsCount(String recognitionId) {
        LoadContext<RecognitionComment> loadContext = LoadContext.create(RecognitionComment.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$RecognitionComment e " +
                        "where e.recognition.id = :rcgId");
        query.setParameter("rcgId", UUID.fromString(recognitionId));
        loadContext.setQuery(query);
        return dataManager.getCount(loadContext);
    }

    @Override
    public List<RecognitionCommentPojo> loadCommentsByRecognition(String recognitionId, int offset, int maxResults, int languageIndex, boolean isAutomaticTranslate) {
        List<RecognitionCommentPojo> comments = new LinkedList<>();
        LoadContext<RecognitionComment> loadContext = LoadContext.create(RecognitionComment.class);
        LoadContext.Query loadContextQuery = LoadContext.createQuery(
                "select e from tsadv$RecognitionComment e " +
                        "where e.recognition.id = :paId " +
                        "order by e.createTs desc");
        loadContextQuery.setParameter("paId", UUID.fromString(recognitionId));
        loadContextQuery.setFirstResult(offset);
        loadContextQuery.setMaxResults(maxResults);
        loadContext.setQuery(loadContextQuery);
        loadContext.setView("recognitionComment.edit");

        List<RecognitionComment> commentList = dataManager.loadList(loadContext);

        if (commentList != null && !commentList.isEmpty()) {
            for (RecognitionComment recognitionComment : commentList) {
                comments.add(parseToCommentPojo(recognitionComment, languageIndex, isAutomaticTranslate));
            }
        }
        return comments;
    }

    private String toJsonList(Collection<? extends Entity> entityList) {
        return entitySerialization.toJson(entityList, null);
    }

    private RecognitionCommentPojo parseToCommentPojo(RecognitionComment recognitionComment, int languageIndex, boolean isAutomaticTranslate) {
        RecognitionCommentPojo commentPojo = metadata.create(RecognitionCommentPojo.class);
        commentPojo.setId(recognitionComment.getId());
        commentPojo.setCreateDate(getDateTimeFormatter().format(recognitionComment.getCreateTs()));
        commentPojo.setText(isAutomaticTranslate ? (languageIndex == 1 ? recognitionComment.getTextRu() : recognitionComment.getTextEn()) : recognitionComment.getText());
        commentPojo.setRecognitionId(recognitionComment.getRecognition().getId());

        RecognitionComment parentComment = recognitionComment.getParentComment();
        if (parentComment != null) {
            commentPojo.setParentCommentId(parentComment.getId());

            PersonGroupExt personGroupExt = parentComment.getAuthor();

            PersonExt personExt = personGroupExt.getPerson();

            PersonPojo parentCommentAuthor = metadata.create(PersonPojo.class);
            parentCommentAuthor.setId(personGroupExt.getId());

            String fullName = personExt.getFirstName() + " " + personExt.getLastName();
            if (languageIndex != 1) {
                fullName = personExt.getFirstNameLatin() + " " + personExt.getLastNameLatin();
            }
            parentCommentAuthor.setName(fullName + " (" + personExt.getEmployeeNumber() + ")");
            parentCommentAuthor.setPersonId(personExt.getId().toString());
            commentPojo.setParentCommentAuthor(parentCommentAuthor);
        }

        PersonGroupExt authorPersonGroup = recognitionComment.getAuthor();
        PersonExt authorPerson = authorPersonGroup.getPerson();

        PersonPojo personPojo = metadata.create(PersonPojo.class);
        personPojo.setId(authorPersonGroup.getId());

        String fullName = authorPerson.getFirstName() + " " + authorPerson.getLastName();
        if (languageIndex != 1) {
            fullName = authorPerson.getFirstNameLatin() + " " + authorPerson.getLastNameLatin();
        }
        personPojo.setName(fullName + " (" + authorPerson.getEmployeeNumber() + ")");

        personPojo.setEmployeeNumber(authorPerson.getEmployeeNumber());
        personPojo.setPersonId(authorPerson.getId().toString());
        personPojo.setImage("");
        commentPojo.setAuthor(personPojo);

        String originalText = recognitionComment.getText();
        String commentRu = recognitionComment.getTextRu();
        String commentEn = recognitionComment.getTextEn();

        boolean translated = !commentPojo.getText().equals(originalText);
        commentPojo.setTranslated(translated ? 1 : 0);

        String reverseText = originalText;
        if (translated) {
            if (commentPojo.getText().equals(commentEn)) {
                reverseText = commentRu;
            } else {
                reverseText = commentEn;
            }
        } else {
            if (reverseText.equals(commentEn)) {
                reverseText = commentRu;
            } else {
                reverseText = commentEn;
            }
        }
        commentPojo.setReverseText(reverseText);
        return commentPojo;
    }

    @Override
    public Long getPersonBalance() {
        PersonGroupExt personGroupExt = getCurrentPersonGroup();
        if (personGroupExt == null) {
            throw new RecognitionException(getMessage("user.person.null"));
        }
        PersonCoin personCoin = loadPersonCoin(personGroupExt.getId());
        if (personCoin == null) {
            throw new RecognitionException(getMessage("author.wallet.hc.null"));
        }
        return personCoin.getCoins();
    }

    @Override
    public void removeGoodsFromCart(UUID goodsId) {
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            em.setSoftDeletion(false);
            em.remove(em.getReference(GoodsCart.class, goodsId));
            tx.commit();
        } finally {
            tx.end();
        }
    }

    @Override
    public Long getTotalSum(UUID currentPersonGroupId) {
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(
                    "SELECT sum(gc.quantity * g.price)  " +
                            "       FROM tsadv_goods_cart gc " +
                            "         JOIN tsadv_goods g " +
                            "           ON g.id = gc.goods_id " +
                            "           AND g.delete_ts IS NULL " +
                            "       WHERE gc.person_group_id = ?1 " +
                            "             AND gc.delete_ts IS NULL " +
                            "             and gc.issued = False");
            query.setParameter(1, currentPersonGroupId);
            try {
                return ((Double) query.getSingleResult()).longValue();
            } catch (Exception ex) {
                return 0L;
            }
        } finally {
            tx.end();
        }
    }

    @Override
    public String loadCart(int page, long lastCount) {
        int perPageCount = PageRangeInfo.CART.getPerPageCount();

        PersonGroupExt currentPersonGroup = getCurrentPersonGroup();
        if (currentPersonGroup == null) {
            throw new RecognitionException(getMessage("user.person.null"));
        }

        GoodsPageInfo goodsPageInfo = metadata.create(GoodsPageInfo.class);

        long goodsCount = cartCount(currentPersonGroup.getId());
        if (page == 1) {
            lastCount = goodsCount;
        }

        PageInfo pageInfo = metadata.create(PageInfo.class);
        pageInfo.setTotalRowsCount(lastCount);
        pageInfo.setPagesCount(PageInfo.getPageCount(lastCount, perPageCount));
        goodsPageInfo.setPageInfo(pageInfo);

        long difference = 0;

        if (page != 1 && lastCount != goodsCount) {
            difference = goodsCount - lastCount;
        }

        List<GoodsPojo> list = new ArrayList<>();
        goodsPageInfo.setGoods(list);

        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            int languageIndex = languageIndex();

            String stringQuery = "SELECT " +
                    "  gc.id       cart_id, " +
                    "  gc.quantity, " +
                    "  gc.goods_id goods_id, " +
                    "  g.name_lang${languageIndex}, " +
                    "  g.description_lang${languageIndex}, " +
                    "  g.price " +
                    "FROM tsadv_goods_cart gc " +
                    "  JOIN tsadv_goods g " +
                    "    ON g.id = gc.goods_id and g.delete_ts is null " +
                    "WHERE gc.person_group_id = ?1 " +
                    "AND gc.delete_ts IS NULL " +
                    "and gc.issued = False";

            StrSubstitutor strSubstitutor = new StrSubstitutor(ParamsMap.of("languageIndex", languageIndex));

            Query query = em.createNativeQuery(strSubstitutor.replace(stringQuery));
            query.setParameter(1, currentPersonGroup.getId());

            query.setFirstResult((int) difference + ((page - 1) * perPageCount));
            query.setMaxResults(perPageCount);

            List<Object[]> resultList = query.getResultList();
            if (resultList != null && !resultList.isEmpty()) {
                for (Object[] row : resultList) {
                    GoodsPojo goodsPojo = metadata.create(GoodsPojo.class);
                    goodsPojo.setCartId((UUID) row[0]);
                    goodsPojo.setQuantity((Long) row[1]);
                    goodsPojo.setId((UUID) row[2]);
                    goodsPojo.setName((String) row[3]);
                    goodsPojo.setDescription((String) row[4]);
                    goodsPojo.setPrice((Double) row[5]);
                    list.add(goodsPojo);
                }
            }
        } finally {
            tx.end();
        }
        return entitySerialization.toJson(goodsPageInfo, null);
    }


    @Override
    public String loadGoodsOrders(int page, long lastCount) {
        int perPageCount = PageRangeInfo.ORDERS.getPerPageCount();

        PersonGroupExt currentPersonGroup = getCurrentPersonGroup();
        if (currentPersonGroup == null) {
            throw new RecognitionException(getMessage("user.person.null"));
        }

        GoodsOrderPageInfo goodsOrderPageInfo = metadata.create(GoodsOrderPageInfo.class);
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();

            long goodsCount = goodsOrdersCount(currentPersonGroup.getId(), em);
            if (page == 1) {
                lastCount = goodsCount;
            }

            PageInfo pageInfo = metadata.create(PageInfo.class);
            pageInfo.setTotalRowsCount(lastCount);
            pageInfo.setPagesCount(PageInfo.getPageCount(lastCount, perPageCount));
            goodsOrderPageInfo.setPageInfo(pageInfo);

            long difference = 0;

            if (page != 1 && lastCount != goodsCount) {
                difference = goodsCount - lastCount;
            }

            List<GoodsOrderPojo> list = new ArrayList<>();
            goodsOrderPageInfo.setGoodsOrders(list);

            UUID hierarchyId = null;
            if (StringUtils.isNotBlank(recognitionConfig.getHierarchyId())) {
                hierarchyId = UUID.fromString(recognitionConfig.getHierarchyId());
            }

            AssignmentExt assignmentExt = getCurrentAssignment();
            if (assignmentExt == null) {
                throw new NullPointerException("Assignment is null!");
            }

            PositionGroupExt positionGroupExt = assignmentExt.getPositionGroup();
            if (positionGroupExt == null) {
                throw new NullPointerException("Position group is null");
            }

            String hierarchyFilter = "AND h.primary_flag = TRUE";
            if (hierarchyId != null) {
                hierarchyFilter = String.format("and h.id = '%s'", hierarchyId);
            }

            Query query = em.createNativeQuery("WITH temp_employee AS (WITH RECURSIVE " +
                    "                           org_pos(id, hierarchy_id, parent_id, element_type, position_group_id, " +
                    "                                   manager_flag, parent_position_group_id, start_date, end_date, " +
                    "                                   organization_group_id, path, lvl, path_pos_name, person_group_id, " +
                    "                                   parent_person_group_id) AS (SELECT he.id, " +
                    "                                                                      he.hierarchy_id, " +
                    "                                                                      he.parent_id, " +
                    "                                                                      he.element_type, " +
                    "                                                                      he.position_group_id, " +
                    "                                                                      p.manager_flag, " +
                    "                                                                      NULL::uuid                                                          AS parent_organization_group_id, " +
                    "                                                                      he.start_date, " +
                    "                                                                      he.end_date, " +
                    "                                                                      a1.organization_group_id, " +
                    "                                                                      (a1.person_group_id)::text                                          AS path, " +
                    "                                                                      1                                                                   AS lvl, " +
                    "                                                                      (COALESCE(p.position_full_name_lang1, ''::character varying))::text AS path_pos_name, " +
                    "                                                                      a1.person_group_id, " +
                    "                                                                      NULL::uuid                                                          AS parent_person_group_id, " +
                    "                                                                      newid()                                                             AS h_id, " +
                    "                                                                      NULL::uuid                                                          AS h_parent_id " +
                    "                                                               FROM (((base_hierarchy_element he JOIN base_hierarchy h ON (((h.id = he.hierarchy_id) AND (h.delete_ts IS NULL)))) JOIN base_position p ON (( " +
                    "                                                                       (he.position_group_id = p.group_id) AND " +
                    "                                                                       (('now'::text)::date >= p.start_date) AND " +
                    "                                                                       (('now'::text)::date <= p.end_date) AND " +
                    "                                                                       (p.delete_ts IS NULL)))) " +
                    "                                                                        LEFT JOIN base_assignment a1 " +
                    "                                                                                  ON (((a1.position_group_id = he.position_group_id) AND " +
                    "                                                                                       (('now'::text)::date >= a1.start_date) AND " +
                    "                                                                                       (('now'::text)::date <= a1.end_date) AND " +
                    "                                                                                       (a1.primary_flag = true) AND " +
                    "                                                                                       (a1.delete_ts IS NULL) AND " +
                    "                                                                                       (a1.assignment_status_id <> '852609db-c23e-af4e-14f3-ea645d38f57d'::uuid)))) " +
                    "                                                               WHERE ((he.parent_id IS NULL) AND " +
                    "                                                                      (he.delete_ts IS NULL) AND " +
                    "                                                                      (('now'::text)::date >= he.start_date) AND " +
                    "                                                                      (('now'::text)::date <= he.end_date)) " +
                    "                                                               UNION ALL " +
                    "                                                               SELECT he.id, " +
                    "                                                                      he.hierarchy_id, " +
                    "                                                                      he.parent_id, " +
                    "                                                                      he.element_type, " +
                    "                                                                      he.position_group_id, " +
                    "                                                                      p.manager_flag, " +
                    "                                                                      COALESCE(op.position_group_id, op.parent_position_group_id)                  AS parent_position_group_id, " +
                    "                                                                      he.start_date, " +
                    "                                                                      he.end_date, " +
                    "                                                                      COALESCE(a1.organization_group_id, op.organization_group_id)                 AS organization_group_id, " +
                    "                                                                      (((op.path || '*'::text) || " +
                    "                                                                        COALESCE((a1.person_group_id)::text, 'VACANCY'::text)))::character varying AS \"varchar\", " +
                    "                                                                      (op.lvl + 1), " +
                    "                                                                      ((op.path_pos_name || '->'::text) || " +
                    "                                                                       (COALESCE(p.position_full_name_lang1, ''::character varying))::text)        AS path_pos_name, " +
                    "                                                                      a1.person_group_id, " +
                    "                                                                      COALESCE(op.person_group_id, op.parent_person_group_id)                      AS parent_person_group_id, " +
                    "                                                                      newid()                                                                      AS h_id, " +
                    "                                                                      op.h_id                                                                      AS h_parent_id " +
                    "                                                               FROM (((base_hierarchy_element he JOIN org_pos op ON ((he.parent_id = op.id))) JOIN base_position p ON (( " +
                    "                                                                       (he.position_group_id = p.group_id) AND " +
                    "                                                                       (('now'::text)::date >= p.start_date) AND " +
                    "                                                                       (('now'::text)::date <= p.end_date) AND " +
                    "                                                                       (p.delete_ts IS NULL)))) " +
                    "                                                                        LEFT JOIN base_assignment a1 " +
                    "                                                                                  ON (((a1.position_group_id = he.position_group_id) AND " +
                    "                                                                                       (('now'::text)::date >= a1.start_date) AND " +
                    "                                                                                       (('now'::text)::date <= a1.end_date) AND " +
                    "                                                                                       (a1.primary_flag = true) AND " +
                    "                                                                                       (a1.delete_ts IS NULL) AND " +
                    "                                                                                       (a1.assignment_status_id <> '852609db-c23e-af4e-14f3-ea645d38f57d'::uuid)))) " +
                    "                                                               WHERE ((he.element_type = 2) AND " +
                    "                                                                      (he.delete_ts IS NULL) AND " +
                    "                                                                      (('now'::text)::date >= he.start_date) AND " +
                    "                                                                      (('now'::text)::date <= he.end_date))), " +
                    "                           temp_group_employee as (SELECT t.hierarchy_id, " +
                    "                                                          t.parent_person_group_id, " +
                    "                                                          t.person_group_id, " +
                    "                                                          t.position_group_id " +
                    "                                                   FROM org_pos t " +
                    "                                                    where t.person_group_id = ?1 " +
                    "                                                   group by t.hierarchy_id, " +
                    "                                                            t.parent_person_group_id, " +
                    "                                                            t.person_group_id, " +
                    "                                                            t.position_group_id) " +
                    "                       SELECT 1            AS n, " +
                    "                              string_agg( " +
                    "                                      concat(ppp.last_name, ' ', ppp.first_name, ' ', ppp.middle_name), " +
                    "                                      ',') AS full_name " +
                    "                       FROM temp_group_employee t " +
                    "                                left join base_person pp " +
                    "                                          on pp.group_id = t.person_group_id and " +
                    "                                             ?2 between pp.start_date and pp.end_date " +
                    "                                left join base_person ppp " +
                    "                                          on ppp.group_id = t.parent_person_group_id and " +
                    "                                             ?2 between ppp.start_date and ppp.end_date " +
                    "                                join base_hierarchy h on h.id = t.hierarchy_id and h.delete_ts is null " +
                    "                       where t.position_group_id = ?3 " +
                    hierarchyFilter +
                    "), " +
                    "     temp_orders AS (select id, order_number, order_date, total_sum, status, 1 as n " +
                    "                     from tsadv_goods_order " +
                    "                     where delete_ts is null " +
                    "                       and person_group_id = ?1) " +
                    "SELECT id, order_number, order_date, total_sum, status, full_name " +
                    "FROM temp_orders o " +
                    "         left join temp_employee e ON o.n = e.n ORDER BY order_date desc;");
            query.setParameter(1, currentPersonGroup.getId());
            query.setParameter(2, CommonUtils.getSystemDate());
            query.setParameter(3, positionGroupExt.getId());

            query.setFirstResult((int) difference + ((page - 1) * perPageCount));
            query.setMaxResults(perPageCount);

            List<Object[]> resultList = query.getResultList();
            if (resultList != null && !resultList.isEmpty()) {
                for (Object[] row : resultList) {
                    GoodsOrderPojo goodsOrderPojo = metadata.create(GoodsOrderPojo.class);
                    goodsOrderPojo.setId((UUID) row[0]);
                    goodsOrderPojo.setOrderNumber((String) row[1]);
                    goodsOrderPojo.setDateTime(goodsOrderDateFormat.format((Date) row[2]));
                    goodsOrderPojo.setSum((Long) row[3]);

                    String statusCode = (String) row[4];
                    goodsOrderPojo.setStatusCode(statusCode);
                    goodsOrderPojo.setFullName(((String) ObjectUtils.defaultIfNull(row[5], "")).trim());
                    GoodsOrderStatus status = GoodsOrderStatus.fromId(statusCode);
                    if (status != null) {
                        goodsOrderPojo.setStatus(messages.getMessage(status));
                    } else {
                        goodsOrderPojo.setStatus("STATUS is null");
                    }

                    list.add(goodsOrderPojo);
                }
            }
        } finally {
            tx.end();
        }
        return entitySerialization.toJson(goodsOrderPageInfo, null);
    }

    private Long goodsOrdersCount(UUID personGroupId, EntityManager em) {
        Query query = em.createNativeQuery(
                "SELECT " +
                        "  count(*)" +
                        "FROM tsadv_goods_order ord " +
                        "WHERE ord.delete_ts IS NULL " +
                        "      AND ord.person_group_id = ?1");
        query.setParameter(1, personGroupId);
        return (Long) query.getSingleResult();
    }

    @Override
    public Long goodsOrdersCount() {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(
                    "SELECT " +
                            "  count(*)" +
                            "FROM tsadv_goods_order ord " +
                            "WHERE ord.delete_ts IS NULL " +
                            "      AND ord.person_group_id = ?1");
            query.setParameter(1, getCurrentPersonGroup().getId());
            return (Long) query.getSingleResult();
        }
    }

    @Override
    public GoodsOrder loadGoodsOrder(String goodsOrderNumber) {
        PersonGroupExt personGroupExt = getCurrentPersonGroup();
        if (personGroupExt == null) {
            throw new RecognitionException(getMessage("user.person.null"));
        }

        LoadContext<GoodsOrder> loadContext = LoadContext.create(GoodsOrder.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$GoodsOrder e " +
                        "where e.orderNumber = :orderNumber " +
                        "and e.personGroup.id = :pgId");

        query.setParameter("orderNumber", goodsOrderNumber);
        query.setParameter("pgId", personGroupExt.getId());

        loadContext.setQuery(query);
        loadContext.setView("goodsOrder.edit");
        return dataManager.load(loadContext);
    }

    @Override
    public int updateGoodsQuantity(String goodsCartId, String goodsId, long quantity) {
        PersonGroupExt personGroupExt = getCurrentPersonGroup();
        if (personGroupExt == null) {
            throw new RecognitionException(getMessage("user.person.null"));
        }
        if (StringUtils.isBlank(goodsCartId)) {
            throw new RecognitionException(getMessage("goods.cart.id.null"));
        }
        if (StringUtils.isBlank(goodsId)) {
            throw new RecognitionException(getMessage("goods.cart.goods.id.null"));
        }

        if (quantity < 1) {
            throw new RecognitionException(getMessage("goods.cart.quantity.0"));
        }

        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            Query query = em.createNativeQuery(
                    "update tsadv_goods_cart " +
                            "set quantity = ?1 " +
                            "where person_group_id = ?2 " +
                            "and id = ?3 " +
                            "and goods_id = ?4");

            query.setParameter(1, quantity);
            query.setParameter(2, personGroupExt.getId());
            query.setParameter(3, UUID.fromString(goodsCartId.trim()));
            query.setParameter(4, UUID.fromString(goodsId.trim()));

            int result = query.executeUpdate();
            tx.commit();
            return result;
        }
    }

    private Long cartCount(UUID personGroupId) {
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(
                    "SELECT sum(t.cnt) " +
                            "FROM ( " +
                            "       SELECT count(*) cnt " +
                            "       FROM tsadv_goods_cart gc " +
                            "       WHERE gc.person_group_id = ?1 " +
                            "           and gc.delete_ts is null " +
                            "           and gc.issued = False " +
                            "       GROUP BY gc.goods_id) t");
            query.setParameter(1, personGroupId);
            try {
                return ((BigDecimal) query.getSingleResult()).longValue();
            } catch (Exception ex) {
                return 0L;
            }
        } finally {
            tx.end();
        }
    }

    @Override
    public String loadTopNominee(int year, String organizationGroupId) {
        List<NomineePojo> resultList = new ArrayList<>();
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            int languageIndex = languageIndex();

            if ("-1".equals(organizationGroupId)) organizationGroupId = null;

            String organizationPath = null;
            if (organizationGroupId != null) {
                organizationPath = getOrganizationPath(organizationGroupId, null);
            }

            Query query = em.createNativeQuery(String.format(
                    "SELECT " +
                            "  p.id, " +
                            "  p.group_id, " +
                            "  %s, " +
                            "  org.organization_name_lang%d, " +
                            "  pos.position_name_lang%d, " +
                            "  ap.name," +
                            "  ap.year_," +
                            "  spa.description_lang_value%d " +
                            "FROM tsadv_selected_person_award spa " +
                            "  JOIN tsadv_award_program ap " +
                            "    ON ap.id = spa.award_program_id " +
                            "       AND ap.delete_ts is null " +
                            "  JOIN base_assignment a " +
                            "    ON a.person_group_id = spa.person_group_id " +
                            "       AND a.primary_flag = TRUE " +
                            "       AND a.delete_ts IS NULL " +
                            "       AND current_date BETWEEN a.start_date AND a.end_date " +
                            "  JOIN base_person p " +
                            "    ON p.group_id = a.person_group_id " +
                            "       AND p.delete_ts IS NULL " +
                            "       AND current_date BETWEEN p.start_date AND p.end_date " +
                            "  JOIN base_organization org " +
                            "    ON org.group_id = a.organization_group_id " +
                            "       AND org.delete_ts IS NULL " +
                            "       AND current_date BETWEEN org.start_date AND org.end_date " +
                            "  JOIN base_position pos " +
                            "    ON pos.group_id = a.position_group_id " +
                            "       AND pos.delete_ts IS NULL " +
                            "       AND current_date BETWEEN pos.start_date AND pos.end_date " +
                            "WHERE spa.awarded = TRUE " +
                            "      AND spa.delete_ts IS NULL " +
                            "      AND ap.year_ = ?1 " +
                            "%s",
                    languageIndex == 3 ? "p.first_name_latin || ' ' || p.last_name_latin" : "p.first_name || ' ' || p.last_name",
                    languageIndex,
                    languageIndex,
                    languageIndex,
                    organizationPath != null ? String.format("and a.organization_group_id in (%s)", organizationPath) : ""));

            query.setParameter(1, year);

            query.setFirstResult(0);
            query.setMaxResults(20);

            List<Object[]> rows = query.getResultList();
            if (rows != null && !rows.isEmpty()) {
                for (Object[] row : rows) {
                    resultList.add(parseNominee(row));
                }
            }
        }
        return entitySerialization.toJson(resultList, null);
    }

    @Override
    public String loadTopNomineeWithDefault(int currentYear, String organizationGroupId, int lastYear, boolean onChange) {
        List<NomineePojo> resultList = new ArrayList<>();
        boolean isLastYear = false;
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            long count = getTopNomineeCount(currentYear, organizationGroupId, em);

            if (count == 0) {
                isLastYear = true;
            }
            loadTopNominee(resultList, organizationGroupId, em, (isLastYear && !onChange) ? lastYear : currentYear);
        }
        return entitySerialization.toJson(resultList, null);
    }

    protected long getTopNomineeCount(int year, String organizationGroupId, EntityManager em) {

        if ("-1".equals(organizationGroupId)) organizationGroupId = null;

        String organizationPath = null;
        if (organizationGroupId != null) {
            organizationPath = getOrganizationPath(organizationGroupId, null);
        }

        Query query = em.createNativeQuery(String.format(
                "SELECT " +
                        "count(spa.id) " +
                        "FROM tsadv_selected_person_award spa " +
                        "  JOIN tsadv_award_program ap " +
                        "    ON ap.id = spa.award_program_id " +
                        "       AND ap.delete_ts is null " +
                        "  JOIN base_assignment a " +
                        "    ON a.person_group_id = spa.person_group_id " +
                        "       AND a.primary_flag = TRUE " +
                        "       AND a.delete_ts IS NULL " +
                        "       AND current_date BETWEEN a.start_date AND a.end_date " +
                        "  JOIN base_person p " +
                        "    ON p.group_id = a.person_group_id " +
                        "       AND p.delete_ts IS NULL " +
                        "       AND current_date BETWEEN p.start_date AND p.end_date " +
                        "  JOIN base_organization org " +
                        "    ON org.group_id = a.organization_group_id " +
                        "       AND org.delete_ts IS NULL " +
                        "       AND current_date BETWEEN org.start_date AND org.end_date " +
                        "  JOIN base_position pos " +
                        "    ON pos.group_id = a.position_group_id " +
                        "       AND pos.delete_ts IS NULL " +
                        "       AND current_date BETWEEN pos.start_date AND pos.end_date " +
                        "WHERE spa.awarded = TRUE " +
                        "      AND spa.delete_ts IS NULL " +
                        "      AND ap.year_ = ?1 " +
                        "%s",
                organizationPath != null ? String.format("and a.organization_group_id in (%s)", organizationPath) : ""));

        query.setParameter(1, year);
        return (long) query.getSingleResult();
    }

    protected void loadTopNominee(List<NomineePojo> resultList, String organizationGroupId, EntityManager em,
                                  int year) {
        int languageIndex = languageIndex();

        if ("-1".equals(organizationGroupId)) organizationGroupId = null;

        String organizationPath = null;
        if (organizationGroupId != null) {
            organizationPath = getOrganizationPath(organizationGroupId, null);
        }

        Query query = em.createNativeQuery(String.format(
                "SELECT " +
                        "  p.id, " +
                        "  p.group_id, " +
                        "  %s, " +
                        "  org.organization_name_lang%d, " +
                        "  pos.position_name_lang%d, " +
                        "  ap.name," +
                        "  ap.year_," +
                        "  spa.description_lang_value%d " +
                        "FROM tsadv_selected_person_award spa " +
                        "  JOIN tsadv_award_program ap " +
                        "    ON ap.id = spa.award_program_id " +
                        "       AND ap.delete_ts is null " +
                        "  JOIN base_assignment a " +
                        "    ON a.person_group_id = spa.person_group_id " +
                        "       AND a.primary_flag = TRUE " +
                        "       AND a.delete_ts IS NULL " +
                        "       AND current_date BETWEEN a.start_date AND a.end_date " +
                        "  JOIN base_person p " +
                        "    ON p.group_id = a.person_group_id " +
                        "       AND p.delete_ts IS NULL " +
                        "       AND current_date BETWEEN p.start_date AND p.end_date " +
                        "  JOIN base_organization org " +
                        "    ON org.group_id = a.organization_group_id " +
                        "       AND org.delete_ts IS NULL " +
                        "       AND current_date BETWEEN org.start_date AND org.end_date " +
                        "  JOIN base_position pos " +
                        "    ON pos.group_id = a.position_group_id " +
                        "       AND pos.delete_ts IS NULL " +
                        "       AND current_date BETWEEN pos.start_date AND pos.end_date " +
                        "WHERE spa.awarded = TRUE " +
                        "      AND spa.delete_ts IS NULL " +
                        "      AND ap.year_ = ?1 " +
                        "%s",
                languageIndex == 3 ? "p.first_name_latin || ' ' || p.last_name_latin" : "p.first_name || ' ' || p.last_name",
                languageIndex,
                languageIndex,
                languageIndex,
                organizationPath != null ? String.format("and a.organization_group_id in (%s)", organizationPath) : ""));

        query.setParameter(1, year);

        query.setFirstResult(0);
        query.setMaxResults(20);

        List<Object[]> rows = query.getResultList();
        if (rows != null && !rows.isEmpty()) {
            for (Object[] row : rows) {
                resultList.add(parseNominee(row));
            }
        }
    }

    @Override
    public String loadAllNominee(int page, long lastCount, int year, String organizationGroupId) {
        int perPageCount = PageRangeInfo.NOMINEE.getPerPageCount();

        if ("-1".equals(organizationGroupId)) organizationGroupId = null;

        String organizationPath = null;
        if (organizationGroupId != null) {
            organizationPath = getOrganizationPath(organizationGroupId, null);
        }

        NomineePageInfo nomineePageInfo = metadata.create(NomineePageInfo.class);

        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            int languageIndex = languageIndex();
            long nomineesCount = allNomineesCount(year, organizationPath, em);

            if (page == 1) {
                lastCount = nomineesCount;
            }

            PageInfo pageInfo = metadata.create(PageInfo.class);
            pageInfo.setTotalRowsCount(lastCount);
            pageInfo.setPagesCount(PageInfo.getPageCount(lastCount, perPageCount));
            nomineePageInfo.setPageInfo(pageInfo);

            long difference = 0;

            if (page != 1 && lastCount != nomineesCount) {
                difference = nomineesCount - lastCount;
            }

            List<NomineePojo> list = new ArrayList<>();
            nomineePageInfo.setNominees(list);

            int offset = (int) difference + ((page - 1) * perPageCount);

            loadAllNominee(offset, perPageCount, year, organizationPath, languageIndex, em, list);
        }
        return entitySerialization.toJson(nomineePageInfo, null);
    }

    @Override
    public String loadAllNomineeWithDefault(int page, long lastCount, int year, String organizationGroupId, int lastYear, boolean onChange) {
        boolean isLastY = false;
        int perPageCount = PageRangeInfo.NOMINEE.getPerPageCount();

        if ("-1".equals(organizationGroupId)) organizationGroupId = null;

        String organizationPath = null;
        if (organizationGroupId != null) {
            organizationPath = getOrganizationPath(organizationGroupId, null);
        }

        NomineePageInfo nomineePageInfo = metadata.create(NomineePageInfo.class);

        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            int languageIndex = languageIndex();
            long nomineesCount = allNomineesCount(year, organizationPath, em);

            if (nomineesCount == 0 && !onChange) {
                nomineesCount = allNomineesCount(lastYear, organizationPath, em);
                isLastY = true;
            }

            if (page == 1) {
                lastCount = nomineesCount;
            }

            PageInfo pageInfo = metadata.create(PageInfo.class);
            pageInfo.setTotalRowsCount(lastCount);
            pageInfo.setPagesCount(PageInfo.getPageCount(lastCount, perPageCount));
            nomineePageInfo.setPageInfo(pageInfo);

            long difference = 0;

            if (page != 1 && lastCount != nomineesCount) {
                difference = nomineesCount - lastCount;
            }

            List<NomineePojo> list = new ArrayList<>();
            nomineePageInfo.setNominees(list);

            int offset = (int) difference + ((page - 1) * perPageCount);

            loadAllNominee(offset, perPageCount, isLastY ? lastYear : year, organizationPath, languageIndex, em, list);
        }
        return entitySerialization.toJson(nomineePageInfo, null);
    }

    @Override
    public String loadAllNominee(int offset, int maxResults, int languageIndex, int year, String organizationGroupId) {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            List<NomineePojo> nominees = new ArrayList<>();
            if ("-1".equals(organizationGroupId)) {
                organizationGroupId = null;
            }

            String organizationPath = null;
            if (organizationGroupId != null) {
                organizationPath = getOrganizationPath(organizationGroupId, null);
            }

            loadAllNominee(offset, maxResults, year, organizationPath, languageIndex, em, nominees);
            return toJson(nominees, null);
        }
    }

    private void loadAllNominee(int offset, int maxResults, int year, String organizationPath, int languageIndex, EntityManager em, List<NomineePojo> nominees) {
        Query query = em.createNativeQuery(String.format(
                "SELECT " +
                        "  p.id, " +
                        "  p.group_id, " +
                        "  %s, " +
                        "  org.organization_name_lang%d, " +
                        "  pos.position_name_lang%d, " +
                        "  ap.name," +
                        "  ap.year_," +
                        "  spa.description_lang_value%d " +
                        "FROM tsadv_selected_person_award spa " +
                        "  JOIN tsadv_award_program ap " +
                        "    ON ap.id = spa.award_program_id " +
                        "       AND ap.delete_ts is null " +
                        "  JOIN base_assignment a " +
                        "    ON a.person_group_id = spa.person_group_id " +
                        "       AND a.primary_flag = TRUE " +
                        "       AND a.delete_ts IS NULL " +
                        "       AND current_date BETWEEN a.start_date AND a.end_date " +
                        "  JOIN base_person p " +
                        "    ON p.group_id = a.person_group_id " +
                        "       AND p.delete_ts IS NULL " +
                        "       AND current_date BETWEEN p.start_date AND p.end_date " +
                        "  JOIN base_organization org " +
                        "    ON org.group_id = a.organization_group_id " +
                        "       AND org.delete_ts IS NULL " +
                        "       AND current_date BETWEEN org.start_date AND org.end_date " +
                        "  JOIN base_position pos " +
                        "    ON pos.group_id = a.position_group_id " +
                        "       AND pos.delete_ts IS NULL " +
                        "       AND current_date BETWEEN pos.start_date AND pos.end_date " +
                        "WHERE spa.awarded = False " +
                        "      AND spa.delete_ts IS NULL " +
                        "      AND ap.year_ = ?1 " +
                        "%s",
                languageIndex == 3 ? "p.first_name_latin || ' ' || p.last_name_latin" : "p.first_name || ' ' || p.last_name",
                languageIndex,
                languageIndex,
                languageIndex,
                organizationPath != null ? String.format("and a.organization_group_id in (%s)", organizationPath) : ""));

        query.setParameter(1, year);
        query.setFirstResult(offset);
        query.setMaxResults(maxResults);

        List<Object[]> rows = query.getResultList();
        if (rows != null && !rows.isEmpty()) {
            for (Object[] row : rows) {
                nominees.add(parseNominee(row));
            }
        }
    }

    @Override
    public String loadMyNominees(int page, long lastCount, int year, String organizationGroupId) {
        int perPageCount = PageRangeInfo.NOMINEE.getPerPageCount();

        if ("-1".equals(organizationGroupId)) organizationGroupId = null;

        String organizationPath = null;
        if (organizationGroupId != null) {
            organizationPath = getOrganizationPath(organizationGroupId, null);
        }

        NomineePageInfo nomineePageInfo = metadata.create(NomineePageInfo.class);

        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            int languageIndex = languageIndex();
            long nomineesCount = myNomineesCount(year, organizationPath, em);

            if (page == 1) {
                lastCount = nomineesCount;
            }

            PageInfo pageInfo = metadata.create(PageInfo.class);
            pageInfo.setTotalRowsCount(lastCount);
            pageInfo.setPagesCount(PageInfo.getPageCount(lastCount, perPageCount));
            nomineePageInfo.setPageInfo(pageInfo);

            long difference = 0;
            if (page != 1 && lastCount != nomineesCount) {
                difference = nomineesCount - lastCount;
            }

            List<NomineePojo> nominees = new ArrayList<>();
            nomineePageInfo.setNominees(nominees);

            int offset = (int) difference + ((page - 1) * perPageCount);
            loadMyNominees(offset, perPageCount, languageIndex, year, organizationPath, em, nominees);
        }
        return entitySerialization.toJson(nomineePageInfo, null);
    }

    @Override
    public AwardProgram getActiveAwardProgram() {
        return getActiveAwardProgram(userSessionSource.getLocale().getLanguage());
    }

    @Override
    public AwardProgram getActiveAwardProgram(String language) {
        LoadContext<AwardProgram> loadContext = LoadContext.create(AwardProgram.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$AwardProgram e " +
                        "where e.active = True");
        loadContext.setQuery(query);
        loadContext.setView(View.LOCAL);

        AwardProgram awardProgram = dataManager.load(loadContext);
        if (awardProgram == null) {
            throw new RecognitionException(getMessage("award.program.null", language));
        }
        return awardProgram;
    }

    @Override
    public String loadMyNominees(int offset, int maxResults, int year, int languageIndex, String organizationGroupId) {
        if ("-1".equals(organizationGroupId) || organizationGroupId.equalsIgnoreCase("")) {
            organizationGroupId = null;
        }

        String organizationPath = null;
        if (organizationGroupId != null) {
            organizationPath = getOrganizationPath(organizationGroupId, null);
        }
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            List<NomineePojo> nominees = new ArrayList<>();
            loadMyNominees(offset, maxResults, languageIndex, year, organizationPath, em, nominees);
            return toJson(nominees, null);
        }
    }

    private void loadMyNominees(int offset, int maxResults, int languageIndex, int year, String organizationPath, EntityManager em, List<NomineePojo> nominees) {
        Query query = em.createNativeQuery(String.format(
                "select * from (SELECT " +
                        "  p.id, " +
                        "  p.group_id, " +
                        "  %s full_name, " +
                        "  org.organization_name_lang%d, " +
                        "  pos.position_name_lang%d, " +
                        "  ap.name," +
                        "  ap.year_," +
                        "  ''," +
                        "  pa.id pa_id," +
                        "  p.employee_number " +
                        "FROM tsadv_person_award pa " +
                        "  JOIN tsadv_award_program ap " +
                        "    ON ap.id = pa.award_program_id " +
                        "       AND ap.delete_ts is null " +
                        "  JOIN base_assignment a " +
                        "    ON a.person_group_id = pa.receiver_id " +
                        "       AND a.primary_flag = TRUE " +
                        "       AND a.delete_ts IS NULL " +
                        "       AND current_date BETWEEN a.start_date AND a.end_date " +
                        "  JOIN base_person p " +
                        "    ON p.group_id = a.person_group_id " +
                        "       AND p.delete_ts IS NULL " +
                        "       AND current_date BETWEEN p.start_date AND p.end_date " +
                        "  JOIN base_organization org " +
                        "    ON org.group_id = a.organization_group_id " +
                        "       AND org.delete_ts IS NULL " +
                        "       AND current_date BETWEEN org.start_date AND org.end_date " +
                        "  JOIN base_position pos " +
                        "    ON pos.group_id = a.position_group_id " +
                        "       AND pos.delete_ts IS NULL " +
                        "       AND current_date BETWEEN pos.start_date AND pos.end_date " +
                        "WHERE pa.delete_ts IS NULL " +
                        "   AND pa.author_id = ?1 " +
                        "   AND pa.status <> 'DRAFT' " +
                        "   %s " +
                        "   %s) t " +
                        "order by t.full_name",
                languageIndex == 3 ? "p.first_name_latin || ' ' || p.last_name_latin" : "p.first_name || ' ' || p.last_name",
                languageIndex,
                languageIndex,
                year != -1 ? "AND ap.year_ = ?2" : "",
                organizationPath != null ? String.format("and a.organization_group_id in (%s)", organizationPath) : ""));

        query.setParameter(1, getCurrentPersonGroup().getId());

        if (year != -1) {
            query.setParameter(2, year);
        }

        query.setFirstResult(offset);
        query.setMaxResults(maxResults);

        List<Object[]> rows = query.getResultList();
        if (rows != null && !rows.isEmpty()) {
            for (Object[] row : rows) {
                NomineePojo nomineePojo = parseNominee(row);
                nomineePojo.setPersonAwardId(row[8].toString());
                nomineePojo.setEmployeeNumber((String) row[9]);
                nominees.add(nomineePojo);
            }
        }
    }

    @Override
    public String loadNomineeYears(int awarded) {
        Set<Integer> resultSet = new HashSet<>();
        resultSet.add(Calendar.getInstance().get(Calendar.YEAR));

        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery("SELECT " +
                    "ap.year_ " +
                    "FROM tsadv_selected_person_award spa " +
                    "  JOIN tsadv_award_program ap " +
                    "    ON ap.id = spa.award_program_id AND ap.delete_ts IS NULL " +
                    "WHERE spa.delete_ts IS NULL " +
                    "      AND spa.awarded = ?1 " +
                    "GROUP BY ap.year_");

            query.setParameter(1, awarded == 1);

            List<Integer> rows = query.getResultList();
            if (rows != null && !rows.isEmpty()) {
                resultSet.addAll(rows);
            }
        }

        return gson.toJson(resultSet);
    }

    @Override
    public String loadMyNomineeYears() {
        Set<Integer> resultSet = new HashSet<>();
        resultSet.add(Calendar.getInstance().get(Calendar.YEAR));

        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery("SELECT " +
                    "ap.year_ " +
                    "FROM tsadv_person_award pa " +
                    "  JOIN tsadv_award_program ap " +
                    "    ON ap.id = pa.award_program_id AND ap.delete_ts IS NULL " +
                    "WHERE pa.delete_ts IS NULL " +
                    "      AND pa.author_id = ?1 " +
                    "GROUP BY ap.year_");

            query.setParameter(1, getCurrentPersonGroup().getId());

            List<Integer> rows = query.getResultList();
            if (rows != null && !rows.isEmpty()) {
                resultSet.addAll(rows);
            }
        }

        return gson.toJson(resultSet);
    }

    private long allNomineesCount(int year, String organizationPath, EntityManager em) {
        Query query = em.createNativeQuery(String.format(
                "SELECT " +
                        " count(*) " +
                        "FROM tsadv_selected_person_award spa " +
                        "  JOIN tsadv_award_program ap " +
                        "    ON ap.id = spa.award_program_id " +
                        "       AND ap.delete_ts is null " +
                        "  JOIN base_assignment a " +
                        "    ON a.person_group_id = spa.person_group_id " +
                        "       AND a.primary_flag = TRUE " +
                        "       AND a.delete_ts IS NULL " +
                        "       AND current_date BETWEEN a.start_date AND a.end_date " +
                        "  JOIN base_person p " +
                        "    ON p.group_id = a.person_group_id " +
                        "       AND p.delete_ts IS NULL " +
                        "       AND current_date BETWEEN p.start_date AND p.end_date " +
                        "  JOIN base_organization org " +
                        "    ON org.group_id = a.organization_group_id " +
                        "       AND org.delete_ts IS NULL " +
                        "       AND current_date BETWEEN org.start_date AND org.end_date " +
                        "  JOIN base_position pos " +
                        "    ON pos.group_id = a.position_group_id " +
                        "       AND pos.delete_ts IS NULL " +
                        "       AND current_date BETWEEN pos.start_date AND pos.end_date " +
                        "WHERE spa.awarded = False " +
                        "      AND spa.delete_ts IS NULL " +
                        "      AND ap.year_ = ?1 " +
                        "%s",
                organizationPath != null ? String.format("and a.organization_group_id in (%s)", organizationPath) : ""));

        query.setParameter(1, year);
        return (Long) query.getSingleResult();
    }

    @Override
    public Long allNomineesCount(int year, String organizationGroupId) {
        if ("-1".equals(organizationGroupId)) {
            organizationGroupId = null;
        }

        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            String organizationPath = null;

            if (organizationGroupId != null) {
                organizationPath = getOrganizationPath(organizationGroupId, null);
            }

            return allNomineesCount(year, organizationPath, em);
        }
    }

    @Override
    public Long myNomineesCount(int year, String organizationGroupId) {
        if ("-1".equals(organizationGroupId)) {
            organizationGroupId = null;
        }
        String organizationPath = null;
        if (organizationGroupId != null) {
            organizationPath = getOrganizationPath(organizationGroupId, null);
        }
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            return myNomineesCount(year, organizationPath, em);
        }
    }

    private long myNomineesCount(int year, String organizationPath, EntityManager em) {
        Query query = em.createNativeQuery(String.format(
                "SELECT " +
                        "  count(*) " +
                        "FROM tsadv_person_award pa " +
                        "  JOIN tsadv_award_program ap " +
                        "    ON ap.id = pa.award_program_id " +
                        "       AND ap.delete_ts is null " +
                        "  JOIN base_assignment a " +
                        "    ON a.person_group_id = pa.receiver_id " +
                        "       AND a.primary_flag = TRUE " +
                        "       AND a.delete_ts IS NULL " +
                        "       AND current_date BETWEEN a.start_date AND a.end_date " +
                        "  JOIN base_person p " +
                        "    ON p.group_id = a.person_group_id " +
                        "       AND p.delete_ts IS NULL " +
                        "       AND current_date BETWEEN p.start_date AND p.end_date " +
                        "  JOIN base_organization org " +
                        "    ON org.group_id = a.organization_group_id " +
                        "       AND org.delete_ts IS NULL " +
                        "       AND current_date BETWEEN org.start_date AND org.end_date " +
                        "  JOIN base_position pos " +
                        "    ON pos.group_id = a.position_group_id " +
                        "       AND pos.delete_ts IS NULL " +
                        "       AND current_date BETWEEN pos.start_date AND pos.end_date " +
                        "WHERE pa.delete_ts IS NULL " +
                        "   AND pa.author_id = ?1 " +
                        "   AND pa.status <> 'DRAFT' " +
                        "   %s " +
                        "   %s",
                year != -1 ? "AND ap.year_ = ?2" : "",
                organizationPath != null ? String.format("and a.organization_group_id in (%s)", organizationPath) : ""));

        query.setParameter(1, getCurrentPersonGroup().getId());

        if (year != -1) {
            query.setParameter(2, year);
        }

        return (Long) query.getSingleResult();
    }

    private NomineePojo parseNominee(Object[] row) {
        NomineePojo nomineePojo = metadata.create(NomineePojo.class);
        nomineePojo.setPId(row[0].toString());
        nomineePojo.setPgId(row[1].toString());
        nomineePojo.setFullName((String) row[2]);
        nomineePojo.setOrganization((String) row[3]);
        nomineePojo.setPosition((String) row[4]);
        nomineePojo.setProgram((String) row[5]);
        nomineePojo.setImage("");
        nomineePojo.setYear((Integer) row[6]);
        nomineePojo.setDescription((String) row[7]);
        return nomineePojo;
    }

    protected String getMessage(String key) {
        return messages.getMainMessage(key, userSessionSource.getLocale());
    }

    protected String getMessage(String key, Locale locale) {
        return messages.getMainMessage(key, locale);
    }

    @Override
    public String getMessage(String key, String language) {
        return messages.getMainMessage(key, new Locale(language));
    }

    @Override
    public UUID getPersonGroupId(String employeeNumber) {
        if (StringUtils.isBlank(employeeNumber)) {
            throw new RecognitionException("EmployeeNumber is null or empty!");
        }

        UUID personGroupId;
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(
                    "select p.group_id from BASE_PERSON p " +
                            "where p.EMPLOYEE_NUMBER = ?1 " +
                            "and current_date between p.start_date and p.end_date " +
                            "and p.delete_ts is null");
            query.setParameter(1, employeeNumber);
            personGroupId = (UUID) query.getSingleResult();
        }

        if (personGroupId == null) {
            throw new RecognitionException(String.format("Person by employeeNumber: %s not found!", employeeNumber));
        }
        return personGroupId;
    }

    @Override
    public List<GoodsImage> loadGoodsImages(UUID goodsId) {
        return persistence.callInTransaction(em -> {
            Query query = em.createQuery(
                    "select e from tsadv$GoodsImage e " +
                            "join e.good g " +
                            "where g.id = :goodsId");
            query.setParameter("goodsId", goodsId);
            query.setView(viewRepository.getView(GoodsImage.class, "goodsImage.edit"));
            return query.getResultList();
        });
    }

    @Override
    public Long loadGoodsOrdersCount() {
        return persistence.callInTransaction(em -> {
            UUID hierarchyId = null;
            if (StringUtils.isNotBlank(recognitionConfig.getHierarchyId())) {
                hierarchyId = UUID.fromString(recognitionConfig.getHierarchyId());
            }

            PersonGroupExt currentPersonGroup = getCurrentPersonGroup();
            if (currentPersonGroup == null) {
                throw new RecognitionException(getMessage("user.person.null"));
            }
            AssignmentExt assignmentExt = getCurrentAssignment();
            if (assignmentExt == null) {
                throw new NullPointerException("Assignment is null!");
            }

            PositionGroupExt positionGroupExt = assignmentExt.getPositionGroup();
            if (positionGroupExt == null) {
                throw new NullPointerException("Position group is null");
            }

            String hierarchyFilter = "AND h.primary_flag = TRUE";
            if (hierarchyId != null) {
                hierarchyFilter = String.format("and h.id = '%s'", hierarchyId);
            }

            Query query = em.createNativeQuery(String.format("WITH RECURSIVE org_pos(id, hierarchy_id, parent_id, element_type, position_group_id, " +
                    "                       manager_flag, parent_position_group_id, start_date, end_date, " +
                    "                       organization_group_id, path, lvl, path_pos_name, person_group_id, " +
                    "                       parent_person_group_id) AS (SELECT he.id, " +
                    "                                                          he.hierarchy_id, " +
                    "                                                          he.parent_id, " +
                    "                                                          he.element_type, " +
                    "                                                          he.position_group_id, " +
                    "                                                          p.manager_flag, " +
                    "                                                          NULL::uuid                                                          AS parent_organization_group_id, " +
                    "                                                          he.start_date, " +
                    "                                                          he.end_date, " +
                    "                                                          a1.organization_group_id, " +
                    "                                                          (a1.person_group_id)::text                                          AS path, " +
                    "                                                          1                                                                   AS lvl, " +
                    "                                                          (COALESCE(p.position_full_name_lang1, ''::character varying))::text AS path_pos_name, " +
                    "                                                          a1.person_group_id, " +
                    "                                                          NULL::uuid                                                          AS parent_person_group_id, " +
                    "                                                          newid()                                                             AS h_id, " +
                    "                                                          NULL::uuid                                                          AS h_parent_id " +
                    "                                                   FROM (((base_hierarchy_element he JOIN base_hierarchy h ON (((h.id = he.hierarchy_id) AND (h.delete_ts IS NULL)))) JOIN base_position p ON (( " +
                    "                                                           (he.position_group_id = p.group_id) AND " +
                    "                                                           (('now'::text)::date >= p.start_date) AND " +
                    "                                                           (('now'::text)::date <= p.end_date) AND " +
                    "                                                           (p.delete_ts IS NULL)))) " +
                    "                                                            LEFT JOIN base_assignment a1 " +
                    "                                                                      ON (((a1.position_group_id = he.position_group_id) AND " +
                    "                                                                           (('now'::text)::date >= a1.start_date) AND " +
                    "                                                                           (('now'::text)::date <= a1.end_date) AND " +
                    "                                                                           (a1.primary_flag = true) AND " +
                    "                                                                           (a1.delete_ts IS NULL) AND " +
                    "                                                                           (a1.assignment_status_id <> '852609db-c23e-af4e-14f3-ea645d38f57d'::uuid)))) " +
                    "                                                   WHERE ((he.parent_id IS NULL) AND " +
                    "                                                          (he.delete_ts IS NULL) AND " +
                    "                                                          (('now'::text)::date >= he.start_date) AND " +
                    "                                                          (('now'::text)::date <= he.end_date)) " +
                    "                                                   UNION ALL " +
                    "                                                   SELECT he.id, " +
                    "                                                          he.hierarchy_id, " +
                    "                                                          he.parent_id, " +
                    "                                                          he.element_type, " +
                    "                                                          he.position_group_id, " +
                    "                                                          p.manager_flag, " +
                    "                                                          COALESCE(op.position_group_id, op.parent_position_group_id)                  AS parent_position_group_id, " +
                    "                                                          he.start_date, " +
                    "                                                          he.end_date, " +
                    "                                                          COALESCE(a1.organization_group_id, op.organization_group_id)                 AS organization_group_id, " +
                    "                                                          (((op.path || '*'::text) || " +
                    "                                                            COALESCE((a1.person_group_id)::text, 'VACANCY'::text)))::character varying AS \"varchar\", " +
                    "                                                          (op.lvl + 1), " +
                    "                                                          ((op.path_pos_name || '->'::text) || " +
                    "                                                           (COALESCE(p.position_full_name_lang1, ''::character varying))::text)        AS path_pos_name, " +
                    "                                                          a1.person_group_id, " +
                    "                                                          COALESCE(op.person_group_id, op.parent_person_group_id)                      AS parent_person_group_id, " +
                    "                                                          newid()                                                                      AS h_id, " +
                    "                                                          op.h_id                                                                      AS h_parent_id " +
                    "                                                   FROM (((base_hierarchy_element he JOIN org_pos op ON ((he.parent_id = op.id))) JOIN base_position p ON (( " +
                    "                                                           (he.position_group_id = p.group_id) AND " +
                    "                                                           (('now'::text)::date >= p.start_date) AND " +
                    "                                                           (('now'::text)::date <= p.end_date) AND " +
                    "                                                           (p.delete_ts IS NULL)))) " +
                    "                                                            LEFT JOIN base_assignment a1 " +
                    "                                                                      ON (((a1.position_group_id = he.position_group_id) AND " +
                    "                                                                           (('now'::text)::date >= a1.start_date) AND " +
                    "                                                                           (('now'::text)::date <= a1.end_date) AND " +
                    "                                                                           (a1.primary_flag = true) AND " +
                    "                                                                           (a1.delete_ts IS NULL) AND " +
                    "                                                                           (a1.assignment_status_id <> '852609db-c23e-af4e-14f3-ea645d38f57d'::uuid)))) " +
                    "                                                   WHERE ((he.element_type = 2) AND " +
                    "                                                          (he.delete_ts IS NULL) AND " +
                    "                                                          (('now'::text)::date >= he.start_date) AND " +
                    "                                                          (('now'::text)::date <= he.end_date))) " +
                    "SELECT count(*) FROM tsadv_goods_order WHERE person_group_id IN (SELECT t.person_group_id " +
                    "FROM org_pos t " +
                    "         left join base_person pp " +
                    "                   on pp.group_id = t.person_group_id and " +
                    "                      ?2 between pp.start_date and pp.end_date " +
                    "         left join base_person ppp " +
                    "                   on ppp.group_id = t.parent_person_group_id and " +
                    "                      ?2 between ppp.start_date and ppp.end_date " +
                    "         join base_hierarchy h on h.id = t.hierarchy_id and h.delete_ts is null " +
                    "         inner join tsadv_user_ext_person_group ue ON ue.person_group_id = t.person_group_id " +
                    "        inner join sec_user ON sec_user.id = ue.user_ext_id " +
                    "WHERE t.parent_person_group_id = ?1 %s) AND status = 'ON_APPROVAL'", hierarchyFilter));
            query.setParameter(1, currentPersonGroup.getId());
            query.setParameter(2, CommonUtils.getSystemDate());
            return (Long) query.getSingleResult();
        });
    }

    protected String toJson(Collection<? extends Entity> entities, View view) {
        return entitySerialization.toJson(entities, view);
    }

    private boolean isAutomaticTranslate() {
        return loadProfileSettings().getAutomaticTranslate();
    }

    @Override
    public String loadGoodsOrders(int page, long lastCount, String filter, String voucherUsedFilter) {
        if (filter == null) {
            filter = "";
        }

        int perPageCount = PageRangeInfo.ORDERS.getPerPageCount();

        PersonGroupExt currentPersonGroup = getCurrentPersonGroup();
        if (currentPersonGroup == null) {
            throw new RecognitionException(getMessage("user.person.null"));
        }

        GoodsOrderPageInfo goodsOrderPageInfo = metadata.create(GoodsOrderPageInfo.class);
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();

            long goodsCount = goodsOrdersCount(currentPersonGroup.getId(), em);
            if (page == 1) {
                lastCount = goodsCount;
            }

            PageInfo pageInfo = metadata.create(PageInfo.class);
            pageInfo.setTotalRowsCount(lastCount);
            pageInfo.setPagesCount(PageInfo.getPageCount(lastCount, perPageCount));
            goodsOrderPageInfo.setPageInfo(pageInfo);

            long difference = 0;

            if (page != 1 && lastCount != goodsCount) {
                difference = goodsCount - lastCount;
            }

            List<GoodsOrderPojo> list = new ArrayList<>();
            goodsOrderPageInfo.setGoodsOrders(list);

            UUID hierarchyId = null;
            if (StringUtils.isNotBlank(recognitionConfig.getHierarchyId())) {
                hierarchyId = UUID.fromString(recognitionConfig.getHierarchyId());
            }

            AssignmentExt assignmentExt = getCurrentAssignment();
            if (assignmentExt == null) {
                throw new NullPointerException("Assignment is null!");
            }

            PositionGroupExt positionGroupExt = assignmentExt.getPositionGroup();
            if (positionGroupExt == null) {
                throw new NullPointerException("Position group is null");
            }

            String hierarchyFilter = "AND h.primary_flag = TRUE";
            if (hierarchyId != null) {
                hierarchyFilter = String.format("and h.id = '%s'", hierarchyId);
            }

            Query query = em.createNativeQuery(String.format("WITH temp_employee AS (WITH RECURSIVE " +
                    "                           org_pos(id, hierarchy_id, parent_id, element_type, position_group_id, " +
                    "                                   manager_flag, parent_position_group_id, start_date, end_date, " +
                    "                                   organization_group_id, path, lvl, path_pos_name, person_group_id, " +
                    "                                   parent_person_group_id) AS (SELECT he.id, " +
                    "                                                                      he.hierarchy_id, " +
                    "                                                                      he.parent_id, " +
                    "                                                                      he.element_type, " +
                    "                                                                      he.position_group_id, " +
                    "                                                                      p.manager_flag, " +
                    "                                                                      NULL::uuid                                                          AS parent_organization_group_id, " +
                    "                                                                      he.start_date, " +
                    "                                                                      he.end_date, " +
                    "                                                                      a1.organization_group_id, " +
                    "                                                                      (a1.person_group_id)::text                                          AS path, " +
                    "                                                                      1                                                                   AS lvl, " +
                    "                                                                      (COALESCE(p.position_full_name_lang1, ''::character varying))::text AS path_pos_name, " +
                    "                                                                      a1.person_group_id, " +
                    "                                                                      NULL::uuid                                                          AS parent_person_group_id, " +
                    "                                                                      newid()                                                             AS h_id, " +
                    "                                                                      NULL::uuid                                                          AS h_parent_id " +
                    "                                                               FROM (((base_hierarchy_element he JOIN base_hierarchy h ON (((h.id = he.hierarchy_id) AND (h.delete_ts IS NULL)))) JOIN base_position p ON (( " +
                    "                                                                       (he.position_group_id = p.group_id) AND " +
                    "                                                                       (('now'::text)::date >= p.start_date) AND " +
                    "                                                                       (('now'::text)::date <= p.end_date) AND " +
                    "                                                                       (p.delete_ts IS NULL)))) " +
                    "                                                                        LEFT JOIN base_assignment a1 " +
                    "                                                                                  ON (((a1.position_group_id = he.position_group_id) AND " +
                    "                                                                                       (('now'::text)::date >= a1.start_date) AND " +
                    "                                                                                       (('now'::text)::date <= a1.end_date) AND " +
                    "                                                                                       (a1.primary_flag = true) AND " +
                    "                                                                                       (a1.delete_ts IS NULL) AND " +
                    "                                                                                       (a1.assignment_status_id <> '852609db-c23e-af4e-14f3-ea645d38f57d'::uuid)))) " +
                    "                                                               WHERE ((he.parent_id IS NULL) AND " +
                    "                                                                      (he.delete_ts IS NULL) AND " +
                    "                                                                      (('now'::text)::date >= he.start_date) AND " +
                    "                                                                      (('now'::text)::date <= he.end_date)) " +
                    "                                                               UNION ALL " +
                    "                                                               SELECT he.id, " +
                    "                                                                      he.hierarchy_id, " +
                    "                                                                      he.parent_id, " +
                    "                                                                      he.element_type, " +
                    "                                                                      he.position_group_id, " +
                    "                                                                      p.manager_flag, " +
                    "                                                                      COALESCE(op.position_group_id, op.parent_position_group_id)                  AS parent_position_group_id, " +
                    "                                                                      he.start_date, " +
                    "                                                                      he.end_date, " +
                    "                                                                      COALESCE(a1.organization_group_id, op.organization_group_id)                 AS organization_group_id, " +
                    "                                                                      (((op.path || '*'::text) || " +
                    "                                                                        COALESCE((a1.person_group_id)::text, 'VACANCY'::text)))::character varying AS \"varchar\", " +
                    "                                                                      (op.lvl + 1), " +
                    "                                                                      ((op.path_pos_name || '->'::text) || " +
                    "                                                                       (COALESCE(p.position_full_name_lang1, ''::character varying))::text)        AS path_pos_name, " +
                    "                                                                      a1.person_group_id, " +
                    "                                                                      COALESCE(op.person_group_id, op.parent_person_group_id)                      AS parent_person_group_id, " +
                    "                                                                      newid()                                                                      AS h_id, " +
                    "                                                                      op.h_id                                                                      AS h_parent_id " +
                    "                                                               FROM (((base_hierarchy_element he JOIN org_pos op ON ((he.parent_id = op.id))) JOIN base_position p ON (( " +
                    "                                                                       (he.position_group_id = p.group_id) AND " +
                    "                                                                       (('now'::text)::date >= p.start_date) AND " +
                    "                                                                       (('now'::text)::date <= p.end_date) AND " +
                    "                                                                       (p.delete_ts IS NULL)))) " +
                    "                                                                        LEFT JOIN base_assignment a1 " +
                    "                                                                                  ON (((a1.position_group_id = he.position_group_id) AND " +
                    "                                                                                       (('now'::text)::date >= a1.start_date) AND " +
                    "                                                                                       (('now'::text)::date <= a1.end_date) AND " +
                    "                                                                                       (a1.primary_flag = true) AND " +
                    "                                                                                       (a1.delete_ts IS NULL) AND " +
                    "                                                                                       (a1.assignment_status_id <> '852609db-c23e-af4e-14f3-ea645d38f57d'::uuid)))) " +
                    "                                                               WHERE ((he.element_type = 2) AND " +
                    "                                                                      (he.delete_ts IS NULL) AND " +
                    "                                                                      (('now'::text)::date >= he.start_date) AND " +
                    "                                                                      (('now'::text)::date <= he.end_date))), " +
                    "                           temp_group_employee as (SELECT t.hierarchy_id, " +
                    "                                                          t.parent_person_group_id, " +
                    "                                                          t.person_group_id, " +
                    "                                                          t.position_group_id " +
                    "                                                   FROM org_pos t " +
                    "                                                    where t.person_group_id = ?1 " +
                    "                                                   group by t.hierarchy_id, " +
                    "                                                            t.parent_person_group_id, " +
                    "                                                            t.person_group_id, " +
                    "                                                            t.position_group_id) " +
                    "                       SELECT 1            AS n, " +
                    "                              string_agg( " +
                    "                                      concat(ppp.last_name, ' ', ppp.first_name, ' ', ppp.middle_name), " +
                    "                                      ',') AS full_name " +
                    "                       FROM temp_group_employee t " +
                    "                                left join base_person pp " +
                    "                                          on pp.group_id = t.person_group_id and " +
                    "                                             ?2 between pp.start_date and pp.end_date " +
                    "                                left join base_person ppp " +
                    "                                          on ppp.group_id = t.parent_person_group_id and " +
                    "                                             ?2 between ppp.start_date and ppp.end_date " +
                    "                                join base_hierarchy h on h.id = t.hierarchy_id and h.delete_ts is null " +
                    "                       where t.position_group_id = ?3 " +
                    hierarchyFilter +
                    "), " +
                    "     temp_orders AS (select id, order_number, order_date, total_sum, status, person_group_id, 1 as n " +
                    "                     from tsadv_goods_order " +
                    "                     where delete_ts is null " +
                    "                       and person_group_id = ?1 %s), " +
                    "       temp_goods_order_details AS ( select d.voucher_used, d.goods_order_id, d.qr_code, c.code" +
                    "                                      from tsadv_goods_order_detail d\n" + //new select added
                    "                                         join tsadv_goods g on d.goods_id = g.id\n" +
                    "                                            and g.delete_ts is null\n" +
                    "                                         left join tsadv_dic_goods_category c on g.category_id = c.id\n" +
                    "                                            and c.delete_ts is null" +
                    "                                      where d.delete_ts is null)" +
                    "SELECT distinct id, order_number, order_date, total_sum, status, full_name, person_group_id, d.voucher_used, max(d.qr_code) as qr_code " +
                    " FROM temp_orders o " +
                    "         left join temp_employee e ON o.n = e.n " +
                    "           join temp_goods_order_details d on d.goods_order_id = o.id %s " +
                    "               GROUP BY " +
                    "               id," +
                    "               order_number," +
                    "               order_date," +
                    "               total_sum," +
                    "               status, " +
                    "               full_name, " +
                    "               person_group_id, " +
                    "               d.voucher_used" +
                    "               ORDER BY order_date desc;", filter, voucherUsedFilter));
            query.setParameter(1, currentPersonGroup.getId());
            query.setParameter(2, CommonUtils.getSystemDate());
            query.setParameter(3, positionGroupExt.getId());

            query.setFirstResult((int) difference + ((page - 1) * perPageCount));
            query.setMaxResults(perPageCount);

            List<Object[]> resultList = query.getResultList();
            if (resultList != null && !resultList.isEmpty()) {
                for (Object[] row : resultList) {
                    GoodsOrderPojo goodsOrderPojo = metadata.create(GoodsOrderPojo.class);
                    goodsOrderPojo.setId((UUID) row[0]);
                    goodsOrderPojo.setOrderNumber((String) row[1]);
                    goodsOrderPojo.setDateTime(goodsOrderDateFormat.format((Date) row[2]));
                    goodsOrderPojo.setSum((Long) row[3]);

                    String statusCode = (String) row[4];
                    goodsOrderPojo.setStatusCode(statusCode);
                    goodsOrderPojo.setFullName(((String) ObjectUtils.defaultIfNull(row[5], "")).trim());
                    goodsOrderPojo.setPersonGroupId(row[6] != null ? row[6].toString() : "");
                    goodsOrderPojo.setVoucherUsed(String.valueOf(row[7]));
                    goodsOrderPojo.setVoucherQRCode((String) row[8]);
                    GoodsOrderStatus status = GoodsOrderStatus.fromId(statusCode);
                    if (status != null) {
                        goodsOrderPojo.setStatus(messages.getMessage(status));
                    } else {
                        goodsOrderPojo.setStatus("STATUS is null");
                    }

                    list.add(goodsOrderPojo);
                }
            }
        } finally {
            tx.end();
        }
        return entitySerialization.toJson(goodsOrderPageInfo, null);
    }

    @Override
    public void generateQRCode(GoodsOrderDetail goodsOrderDetail) {
        try {
            CommitContext commitContext = new CommitContext();
            String imageByte = qRCodeService.generateQRCodeImage(recognitionConfig.getQRCodeHtmlFilesURL() + "?qrCode=" + goodsOrderDetail.getQrCode(), 350, 350);
            byte[] decodedPhoto = Base64.getDecoder().decode(imageByte.getBytes("UTF-8"));
            FileDescriptor userImage = metadata.create(FileDescriptor.class);
            userImage.setCreateDate(new Date());
            userImage.setName(goodsOrderDetail.getQrCode() + " " + goodsOrderDetail.getId().toString());

            goodsOrderDetail.setQrCodeImg(userImage);

            fileStorageAPI.saveFile(userImage, decodedPhoto);
            commitContext.addInstanceToCommit(userImage);
            dataManager.commit(commitContext);
        } catch (IOException | FileStorageException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String loadInfoByQRCode(String qrCode) {
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(String.format("select " +
                    " concat(p.last_name, ' ', p.first_name,' ',p.middle_name) as fullName,\n" +
                    "  d.qr_code as QRCode,\n" +
                    "   d.quantity as quantity,\n" +
                    "     gi.image_id as image,\n" +
                    "       d.voucher_used as voucherUsed,\n" +
                    "         g.name_lang%d as goodsName\n" +
                    "from tsadv_goods_order_detail d\n" +
                    "         join tsadv_goods_order o on d.goods_order_id = o.id\n" +
                    "    and o.delete_ts is null\n" +
                    "         join base_person p on p.group_id = o.person_group_id\n" +
                    "    and p.delete_ts is null\n" +
                    "    and current_date between p.start_date and p.end_date\n" +
                    "         join tsadv_goods g on d.goods_id = g.id\n" +
                    "    and g.delete_ts is null\n" +
                    "         left join tsadv_goods_image gi on g.id = gi.good_id\n" +
                    "    and gi.delete_ts is null\n" +
                    "where d.delete_ts is null\n" +
                    "  and d.qr_code = ?1", languageIndex()));
            query.setParameter(1, qrCode);
            Object[] row = (Object[]) query.getFirstResult();
            if (row != null) {
                QRCodeInt qrCodeInt = metadata.create(QRCodeInt.class);
                qrCodeInt.setFullName((String) row[0]);
                qrCodeInt.setQrCode((String) row[1]);
                qrCodeInt.setQuantity(row[2] != null ? row[2].toString() : "");
                qrCodeInt.setVoucherUsed((row[4] != null ? row[4].toString() : null));
                qrCodeInt.setGoodsName((row[5] != null ? row[5].toString() : ""));
                try {
                    FileDescriptor image = row[3] != null ? em.find(FileDescriptor.class, (UUID) row[3]) : null;
                    if (image != null) {
                        byte[] fileBytes = fileStorageAPI.loadFile(image);
                        if (fileBytes != null) {
                            qrCodeInt.setImage(new String(Base64.getEncoder().encode(fileBytes)));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return entitySerialization.toJson(qrCodeInt);
            } else {
                return "query return null by value: " + qrCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            tx.end();
        }
    }

    @Override
    public String useVoucher(String qrCode) {
        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();

            Query query = em.createNativeQuery("select d.id " +
                    " from tsadv_goods_order_detail d\n" +
                    "      where d.delete_ts is null\n" +
                    "            and d.qr_code = ?1");
            query.setParameter(1, qrCode);

            UUID detailId = (UUID) query.getSingleResult();
            if (detailId != null) {
                GoodsOrderDetail goodsOrderDetail = em.find(GoodsOrderDetail.class, detailId, View.LOCAL);
                if (goodsOrderDetail != null) {
                    goodsOrderDetail.setVoucherUsed(true);
                    em.merge(goodsOrderDetail);
                    tx.commit();
                    return languageIndex() == 3 ? "used successfully" : "успешно обновлено";
                }
            }
            return "not found [GoodsOrderDetail] with code: " + qrCode;
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            tx.end();
        }
    }

    /**
     * @param personGroupId
     * @return if personGroupId param is null then used current perseonGroupId from session
     */
    @Override
    public boolean isContainsVoucher(@Nullable UUID personGroupId) {
        Transaction tx = persistence.createTransaction();
        try {
            if (personGroupId == null) personGroupId = getCurrentPersonGroup().getId();
            EntityManager em = persistence.getEntityManager();
            return (boolean) em.createNativeQuery("select count(c.id) > 0\n" +
                    "from tsadv_goods_cart c\n" +
                    "         join tsadv_goods g on c.goods_id = g.id\n" +
                    "    and g.delete_ts is null\n" +
                    "         join tsadv_dic_goods_category dc on g.category_id = dc.id\n" +
                    "    and dc.delete_ts is null\n" +
                    "    and dc.code = 'VOUCHER'\n" +
                    "where c.delete_ts is null\n" +
                    "  and c.issued = false\n" +
                    "  and c.person_group_id = ?").setParameter(1, personGroupId).getFirstResult();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tx.end();
        }
        return false;
    }


    @Override
    public void addMedals(@Nullable Recognition recognition) {

        if (recognition == null
                || recognition.getReceiver() == null
                || CollectionUtils.isEmpty(recognition.getRecognitionQualities())) return;

        PersonGroupExt personGroup = recognition.getReceiver();

        StringBuilder stringBuilder = new StringBuilder();

        for (RecognitionQuality recognitionQuality : recognition.getRecognitionQualities()) {
            stringBuilder.append("'").append(recognitionQuality.getId()).append("',");
        }

        String ids = "";
        if (stringBuilder.length() > 1) {
            ids = stringBuilder.substring(0, stringBuilder.toString().length() - 1);
        }

        Transaction tx = persistence.createTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(String.format("select qq.*, pm.id, count(*) over (partition by qq.medal_id) as count_m\n" +
                    "from (select q.*, mc.medal_id, mc.quality_quantity\n" +
                    "      from (select rq.id,\n" +
                    "                   rq.quality_id,\n" +
                    "                   tr.receiver_id,\n" +
                    "                   tr.author_id,\n" +
                    "                   count(rq.quality_id) over (partition by rq.quality_id) as count_q\n" +
                    "            from tsadv_recognition_quality rq\n" +
                    "                     join tsadv_recognition tr on rq.recognition_id = tr.id\n" +
                    "                and tr.delete_ts is null\n" +
                    "                and tr.receiver_id = ?1\n" +
                    "                     join tsadv_dic_quality dq on rq.quality_id = dq.id\n" +
                    "                and dq.delete_ts is null\n" +
                    "            where rq.delete_ts is null\n" +
                    "           ) q\n" +
                    "               join tsadv_medal_condition mc\n" +
                    "                    on q.quality_id = mc.quality_id\n" +
                    "                        and mc.delete_ts is null\n" +
                    "      where mc.quality_quantity <= q.count_q\n" +
                    "        and q.id in (%s)) qq\n" +
                    "         left join tsadv_person_medal pm\n" +
                    "                   on qq.receiver_id = pm.person_group_id\n" +
                    "                       and qq.medal_id = pm.medal_id", ids))
                    .setParameter(1, personGroup.getId());

            List<Object[]> rows = query.getResultList();

            if (rows != null && !rows.isEmpty()) {
                for (Object[] row : rows) {
                    if (row[7] == null) { //if row 7 is null after we have to continue
                        Long recognitionQualityCount = (Long) row[4];
                        UUID medalId = (UUID) row[5];
                        Long quality_quantity = (Long) row[6];
                        if (recognitionQualityCount + 1 >= quality_quantity) {
                            PersonMedal personMedal = metadata.create(PersonMedal.class);
                            personMedal.setMedal(em.getReference(Medal.class, medalId));
                            personMedal.setPersonGroup(personGroup);
                            em.persist(personMedal);
                        }
                    }

                }
                tx.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tx.end();
        }
    }

    @Override
    public void addBadges(Recognition recognition) {

        if (recognition == null
                || recognition.getReceiver() == null
                || CollectionUtils.isEmpty(recognition.getRecognitionQualities())) return;

        String ids = recognition.getRecognitionQualities()
                .stream()
                .map(recognitionQuality1 -> "'" + recognitionQuality1.getId().toString() + "'").collect(Collectors.joining(","));
        try {
            String queryString = String.format("select distinct " +
                    "                qq.medal_id," +
                    "                qq.count_q,\n" +
                    "                qq.count_q_rec,    " +
                    "                qq.count_q_mcn\n" +
                    "from (select q.*, mc.medal_id, mc.quality_quantity,\n" +
                    "             count(*) over (partition by q.recognition_id,mc.medal_id,mcn.quality_id) as count_q_rec\n" +
                    "           ,  mcn.count_q_mcn\n" +
                    "      from (select rq.id,\n" +
                    "                   rq.quality_id,\n" +
                    "                   tr.receiver_id,\n" +
                    "                   tr.author_id,\n" +
                    "                   tr.id as recognition_id,\n" +
                    "                   count(rq.quality_id) over (partition by rq.quality_id) as count_q\n" +
                    "            from tsadv_recognition_quality rq\n" +
                    "                     join tsadv_recognition tr on rq.recognition_id = tr.id\n" +
                    "                and tr.delete_ts is null\n" +
                    "                and tr.receiver_id = ?\n" +
                    "                     join tsadv_dic_quality dq on rq.quality_id = dq.id\n" +
                    "                and dq.delete_ts is null\n" +
                    "            where rq.delete_ts is null\n" +
                    "           ) q\n" +
                    "               join tsadv_medal_condition mc\n" +
                    "                    on q.quality_id = mc.quality_id\n" +
                    "                        and mc.delete_ts is null\n" +
                    "               join lateral\n" +
                    "          (select mcn.*, count(*) over (partition by mcn.medal_id) as count_q_mcn\n" +
                    "      from tsadv_medal_condition mcn\n" +
                    "      where  mc.medal_id= mcn.medal_id\n" +
                    "          and mcn.child_medal_id is null\n" +
                    "          and mcn.delete_ts is null\n" +
                    "          ) mcn\n" +
//                    "           -- on mc.quality_id = mcn.quality_id\n" +
                    "             on 1=1\n" +
                    "      where mcn.quality_quantity <= q.count_q\n" +
                    "        and q.id in (%s)) qq\n" +
                    "where qq.count_q_rec = qq.count_q_mcn", ids);

            QueryRunner runner = new QueryRunner(persistence.getDataSource());


            Map<Object, Object> rowsMap = runner.query(queryString, new Object[]{recognition.getReceiver().getId()}, new int[]{Types.OTHER},
                    rs -> {
                        Map<Object, Object> map = new HashMap<>();
                        while (rs.next()) {
                            UUID medalId = (UUID) rs.getObject(1); //qq.medal_id

                            map.put(medalId, recognition.getReceiver().getId());
                        }
                        return map;
                    });

            createPersonMedal(rowsMap);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    protected void createPersonMedal(Map<Object, Object> map) throws SQLException {
        if (!map.isEmpty()) {
            QueryRunner insertRunner = new QueryRunner(persistence.getDataSource());
            for (Map.Entry<Object, Object> item : map.entrySet()) {
                insertRunner.update(
                        "INSERT INTO tsadv_person_medal (id, version, create_ts, created_by, medal_id, person_group_id) \n" +
                                "VALUES (?, ?, ?, ?, ?, ?) ", new Object[]{UUID.randomUUID(),
                                1,
                                new Date(),
                                "custom",
                                item.getKey(),
                                item.getValue()},
                        new int[]{Types.OTHER, Types.INTEGER, Types.TIMESTAMP, Types.VARCHAR, Types.OTHER, Types.OTHER}
                );
            }
        }
    }


    public void addMedalWithChildMedalQuery(@Nullable PersonGroup personGroup) {
        if (personGroup == null) throw new NullPointerException("PersonGroup is null while creating personMedal");

        QueryRunner runner = new QueryRunner(persistence.getDataSource());
        String strQuery = "select t.child_medal_id, t.medal_quantity, t2.count_medal\n" +
                "from (\n" +
                "         select mc.child_medal_id, mc.medal_quantity\n" +
                "         from tsadv_medal_condition mc\n" +
                "                  join tsadv_medal m on mc.child_medal_id = m.id\n" +
                "             and m.delete_ts is null\n" +
                "         where mc.delete_ts is null\n" +
                "     ) t\n" +
                "         join\n" +
                "     (\n" +
                "         select pm.person_group_id, pm.medal_id, count(*) over (partition by pm.medal_id) as count_medal\n" +
                "         from tsadv_person_medal pm\n" +
                "         where pm.delete_ts is null\n" +
                "           and pm.person_group_id = ?) t2 on t.child_medal_id = t2.medal_id";
        try {
            Map<Object, Object> rowsMap = runner.query(strQuery, new Object[]{personGroup.getId()}, new int[]{Types.OTHER},
                    rs -> {
                        Map<Object, Object> map = new HashMap<>();
                        while (rs.next()) {
                            map.put(rs.getObject(1), personGroup.getId());
                        }
                        return map;
                    });
            createPersonMedal(rowsMap);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}