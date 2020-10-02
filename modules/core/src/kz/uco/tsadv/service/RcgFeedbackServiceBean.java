package kz.uco.tsadv.service;

import com.google.gson.Gson;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.app.serialization.EntitySerializationAPI;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.*;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.service.GoogleTranslateService;
import kz.uco.base.service.NotificationService;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.global.entity.FeedbackCommentRestResult;
import kz.uco.tsadv.global.entity.PageInfo;
import kz.uco.tsadv.global.entity.PageRangeInfo;
import kz.uco.tsadv.global.entity.PersonPojo;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.recognition.exceptions.RecognitionException;
import kz.uco.tsadv.modules.recognition.feedback.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.activation.MimetypesFileTypeMap;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.text.DateFormat;
import java.util.*;

@Service(RcgFeedbackService.NAME)
public class RcgFeedbackServiceBean implements RcgFeedbackService {

    private static final Logger logger = LoggerFactory.getLogger(RcgFeedbackServiceBean.class);

    private static Gson gson = new Gson();
    private MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
    @Inject
    private RecognitionService recognitionService;
    @Inject
    private GlobalConfig globalConfig;
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

    @Override
    public String loadFeedback(int page, long lastCount, int type, UUID personGroupId, String filter) {

        int perPageCount = PageRangeInfo.FEEDBACK.getPerPageCount();

        if (filter == null || filter.trim().length() == 0) {
            filter = null;
        }

        RcgFeedbackPageInfo recognitionPageInfo = metadata.create(RcgFeedbackPageInfo.class);

        int languageIndex = languageIndex();
        boolean isAutomaticTranslate = false;//TODO: isAutomaticTranslate();
        long recognitionsCount = feedbackCount(type, personGroupId, filter, languageIndex, isAutomaticTranslate);

        if (page != 0) {
            lastCount = recognitionsCount;
        }

        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            PageInfo pageInfo = metadata.create(PageInfo.class);
            pageInfo.setTotalRowsCount(lastCount);
            pageInfo.setPagesCount(PageInfo.getPageCount(lastCount, perPageCount));
            recognitionPageInfo.setPageInfo(pageInfo);

            long difference = 0;

            if (page != 1 && lastCount != recognitionsCount) {
                difference = recognitionsCount - lastCount;
            }

            List<RcgFeedbackPojo> list = new ArrayList<>();
            recognitionPageInfo.setFeedback(list);

            if (lastCount != 0) {
                Query query = createFeedbackQuery(type, false, languageIndex, filter, isAutomaticTranslate, personGroupId, em);
                query.setFirstResult((int) difference + ((page - 1) * perPageCount));
                query.setMaxResults(perPageCount);

                List<Object[]> resultList = query.getResultList();
                UUID sessionPersonGroupId = userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP_ID);
                boolean forMe = personGroupId.equals(sessionPersonGroupId);

                for (Object[] row : resultList) {
                    RcgFeedbackPojo feedbackPojo = metadata.create(RcgFeedbackPojo.class);
                    feedbackPojo.setId((UUID) row[0]);

                    /**
                     * theme
                     * */
                    String theme = (String) row[1];
                    String originalTheme = (String) row[2];
                    String themeEn = (String) row[3];
                    String themeRu = (String) row[4];

                    boolean translated = !theme.equals(originalTheme);
                    feedbackPojo.setTranslated(translated ? 1 : 0);

                    feedbackPojo.setTheme(theme);
                    feedbackPojo.setReverseTheme(reverseText(translated, theme, originalTheme, themeEn, themeRu));

                    /**
                     * comment
                     * */
                    String comment = (String) row[5];
                    String originalComment = (String) row[6];
                    String commentEn = (String) row[7];
                    String commentRu = (String) row[8];

                    feedbackPojo.setComment(comment);
                    feedbackPojo.setReverseComment(reverseText(translated, comment, originalComment, commentEn, commentRu));

                    UUID typeId = (UUID) row[9];
                    if (typeId != null) {
                        DicRcgFeedbackTypePojo typePojo = metadata.create(DicRcgFeedbackTypePojo.class);
                        typePojo.setId(typeId);
                        typePojo.setImageId(row[10].toString());
                        typePojo.setName((String) row[11]);
                        feedbackPojo.setType(typePojo);
                    }

                    feedbackPojo.setCreateDate(getDateTimeFormatter().format((Date) row[12]));

                    PersonPojo authorPojo = metadata.create(PersonPojo.class);
                    authorPojo.setId((UUID) row[13]);
                    authorPojo.setPersonId(row[14].toString());
                    authorPojo.setEmployeeNumber(row[15].toString());
                    authorPojo.setName((String) row[16]);
                    authorPojo.setImage("");

                    boolean isAuthorSessionUser = authorPojo.getId().equals(sessionPersonGroupId);

                    feedbackPojo.setSender(authorPojo);

                    PersonPojo receiverPojo = metadata.create(PersonPojo.class);
                    receiverPojo.setId((UUID) row[17]);
                    receiverPojo.setPersonId(row[18].toString());
                    receiverPojo.setEmployeeNumber(row[19].toString());
                    receiverPojo.setName((String) row[20]);
                    receiverPojo.setImage("");
                    feedbackPojo.setReceiver(receiverPojo);

                    feedbackPojo.setForMe(isAuthorSessionUser);
                    feedbackPojo.setSendFeedbackToAuthor(type == 2 && !isAuthorSessionUser);

                    if (type == 0) {
                        feedbackPojo.setSay(forMe ? getMessage("rf.say.self") : getMessage("rf.say"));
                    } else if (type == 1) {
                        feedbackPojo.setSay(getMessage("rf.receive"));
                    } else {
                        feedbackPojo.setSay(getMessage(isAuthorSessionUser ? "rf.me.required" : "rf.required"));
                    }

                    long commentCount = (Long) row[21];
                    long commentPages;

                    if (commentCount > 2) {
                        commentPages = PageInfo.getPageCount(commentCount, PageRangeInfo.COMMENT.getPerPageCount()) + 1;
                    } else {
                        commentPages = 1;
                    }

                    feedbackPojo.setCommentCount(commentCount);
                    feedbackPojo.setCommentPages(commentPages);
                    feedbackPojo.setLastComments(_loadFeedbackComments(feedbackPojo.getId().toString(), 1));

                    feedbackPojo.setAttachments(loadFeedbackAttachments(feedbackPojo.getId()));
                    list.add(feedbackPojo);
                }
            }
            return entitySerialization.toJson(recognitionPageInfo, null);
        }
    }

    private List<RcgFeedbackCommentPojo> _loadFeedbackComments(String recognitionId, int page) {
        int maxResults = page == 1 ? 2 : 10;
        int offset = 0;
        if (page > 1) {
            offset = 2 + (page - 2) * 10;
        }
        return loadFeedbackComments(recognitionId, offset, maxResults, languageIndex(), isAutomaticTranslate());
    }

    @Override
    public Long feedbackCommentsCount(String feedbackId) {
        LoadContext<RcgFeedbackComment> loadContext = LoadContext.create(RcgFeedbackComment.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$RcgFeedbackComment e " +
                        "where e.rcgFeedback.id = :fId");
        query.setParameter("fId", UUID.fromString(feedbackId));
        loadContext.setQuery(query);
        return dataManager.getCount(loadContext);
    }

    @Override
    public String loadFeedbackComments(String feedbackId, int page) {
        return entitySerialization.toJson(_loadFeedbackComments(feedbackId, page), null);
    }

    @Override
    public List<RcgFeedbackCommentPojo> loadFeedbackComments(String feedbackId, int offset, int maxResults, int languageIndex, boolean isAutomaticTranslate) {
        List<RcgFeedbackCommentPojo> comments = new LinkedList<>();

        LoadContext<RcgFeedbackComment> loadContext = LoadContext.create(RcgFeedbackComment.class);
        LoadContext.Query loadContextQuery = LoadContext.createQuery(
                "select e from tsadv$RcgFeedbackComment e " +
                        "where e.rcgFeedback.id = :fId " +
                        "order by e.createTs desc");
        loadContextQuery.setParameter("fId", UUID.fromString(feedbackId));
        loadContextQuery.setFirstResult(offset);
        loadContextQuery.setMaxResults(maxResults);
        loadContext.setQuery(loadContextQuery);
        loadContext.setView("rcgFeedbackComment.parse");

        List<RcgFeedbackComment> commentList = dataManager.loadList(loadContext);
        if (CollectionUtils.isNotEmpty(commentList)) {
            for (RcgFeedbackComment recognitionComment : commentList) {
                comments.add(parseToCommentPojo(recognitionComment, languageIndex, isAutomaticTranslate));
            }
        }
        return comments;
    }

    @Override
    public List<RcgFeedbackAttachmentPojo> loadFeedbackAttachments(UUID feedbackId) {
        LoadContext<FileDescriptor> loadContext = LoadContext.create(FileDescriptor.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e.file from tsadv$RcgFeedbackAttachment e " +
                        "where e.rcgFeedback.id = :fId");
        query.setParameter("fId", feedbackId);
        loadContext.setQuery(query);
        loadContext.setView(View.LOCAL);
        Collection<FileDescriptor> resultList = dataManager.loadList(loadContext);
        if (CollectionUtils.isNotEmpty(resultList)) {
            List<RcgFeedbackAttachmentPojo> attachments = new ArrayList<>();
            for (FileDescriptor fileDescriptor : resultList) {
                RcgFeedbackAttachmentPojo attachment = metadata.create(RcgFeedbackAttachmentPojo.class);
                attachment.setId(fileDescriptor.getId());
                attachment.setName(attachment.getName());

                String type = "OTHER";
                String extension = fileDescriptor.getExtension();
                if (StringUtils.isNotBlank(extension)) {
                    extension = extension.toLowerCase();
                    if (extension.matches("png|jpeg|jpg")) {
                        type = "IMAGE";
                    } else if (extension.matches("mp4|avi")) {
                        type = "VIDEO";
                    }
                }
                attachment.setType(type);

                attachments.add(attachment);
            }
            return attachments;
        }
        return null;
    }

    @Override
    public List<String> validateRcgFeedback(RcgFeedback rcgFeedback) {
        List<String> errors = new LinkedList<>();

        Validator validator = beanValidation.getValidator();
        Set<ConstraintViolation<RcgFeedback>> violations = validator.validate(rcgFeedback);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<RcgFeedback> rcgCv : violations) {
                String propertyPath = rcgCv.getPropertyPath().toString();
                if (!propertyPath.matches("commentEn|commentRu|themeEn|themeRu")) {
                    ConstraintDescriptorImpl constraintDescriptor = (ConstraintDescriptorImpl) rcgCv.getConstraintDescriptor();
                    Class annotationType = constraintDescriptor.getAnnotationType();
                    if (annotationType != null) {
                        if (annotationType.equals(Length.class)) {
                            errors.add(rcgCv.getMessage());
                        } else {
                            errors.add(messageTools.getDefaultRequiredMessage(rcgFeedback.getMetaClass(), propertyPath));
                        }
                    }
                }
            }
        }

        if (errors.isEmpty()) {
            if (rcgFeedback.getAuthor().equals(rcgFeedback.getReceiver())) {
                errors.add(getMessage("rf.your.self"));
            }

            if (rcgFeedback.getDirection().equals(RcgFeedbackDirection.SEND)) {
                if (rcgFeedback.getType() == null) {
                    errors.add(getMessage(messageTools.getDefaultRequiredMessage(rcgFeedback.getMetaClass(), "type")));
                }
            }
        }
        return errors;
    }

    @Override
    public FileDescriptor feedbackTypeImage(UUID id) {
        if (id != null) {
            return dataManager.load(LoadContext.create(FileDescriptor.class).setId(id).setView(View.LOCAL));
        }
        return null;
    }

    @Override
    public String sendComment(RcgFeedbackCommentPojo commentPojo) {
        FeedbackCommentRestResult restResult = new FeedbackCommentRestResult();
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

            RcgFeedbackComment feedbackComment = metadata.create(RcgFeedbackComment.class);
            feedbackComment.setId(newCommentId);
            feedbackComment.setText(originalText);
            feedbackComment.setAuthor(personGroupExt);

            try {
                feedbackComment.setTextEn(googleTranslateService.translate(originalText, "en"));
                feedbackComment.setTextRu(googleTranslateService.translate(originalText, "ru"));
            } catch (Exception ex) {
                feedbackComment.setTextEn(originalText);
                feedbackComment.setTextRu(originalText);
                logger.error(ex.getMessage());
            }

            UUID recognitionCommentId = commentPojo.getParentCommentId();
            if (recognitionCommentId != null) {
                RcgFeedbackComment parentComment = dataManager.load(
                        LoadContext.create(RcgFeedbackComment.class)
                                .setId(recognitionCommentId));
                if (parentComment == null) {
                    throw new RecognitionException(getMessage("parent.comment.not.found"));
                }

                feedbackComment.setParentComment(parentComment);
            }

            UUID feedbackId = commentPojo.getFeedbackId();
            if (feedbackId == null) {
                throw new RecognitionException(getMessage("rcg.id.null"));
            }

            RcgFeedback rcgFeedback = dataManager.load(
                    LoadContext.create(RcgFeedback.class).setId(feedbackId));

            if (rcgFeedback == null) {
                throw new RecognitionException(getMessage("rcg.by.id.null"));
            }

            feedbackComment.setRcgFeedback(rcgFeedback);

            feedbackComment = dataManager.commit(feedbackComment, "rcgFeedbackComment.edit");

            restResult.setSuccess(true);
            restResult.setCommentPojo(parseToCommentPojo(feedbackComment, languageIndex(), false));
        } catch (Exception ex) {
            restResult.setSuccess(false);
            restResult.setMessage(ex.getMessage());
            ex.printStackTrace();
        }

        return gson.toJson(restResult);
    }

    @Override
    public String loadChartData(String personGroupId, String direction) {
        return persistence.callInTransaction(em -> {
            int languageIndex = languageIndex();
            boolean isRequested = direction.equalsIgnoreCase("REQUEST");

            Query query = em.createNativeQuery(String.format(
                    "SELECT JSON_AGG(t.*) " +
                            "FROM ( " +
                            "       SELECT " +
                            "         rft.lang_value%d as type, " +
                            "         count(*) as cnt, " +
                            "         CONCAT('#', rft.color) AS color," +
                            "         order_ " +
                            "       FROM tsadv_rcg_feedback rf " +
                            "       left JOIN tsadv_dic_rcg_feedback_type rft " +
                            "           ON rft.id = rf.type_id " +
                            "           AND rft.delete_ts IS NULL " +
                            "       WHERE rf.delete_ts IS NULL and rf.DIRECTION = ?2 and %s " +
                            "       GROUP BY rft.lang_value%d, " +
                            "       color, " +
                            "       order_ " +
                            "       order by " +
                            "       order_ " +
                            "      ) t ",
                    languageIndex,
                    direction.equalsIgnoreCase("send") ? "rf.author_id = ?1" : "rf.receiver_id = ?1",
                    languageIndex));
            query.setParameter(1, UUID.fromString(personGroupId));
            query.setParameter(2, isRequested ? "REQUEST" : "SEND");
            try {
                return String.valueOf(query.getSingleResult());
            } catch (NoResultException | NonUniqueResultException ex) {
                //ignore this Exception
            }
            return null;
        });
    }

    @Override
    public String loadChartCategories() {
        LoadContext<DicRcgFeedbackType> loadContext = LoadContext.create(DicRcgFeedbackType.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$DicRcgFeedbackType e");
        loadContext.setQuery(query);
        loadContext.setView(View.LOCAL);
        Collection<DicRcgFeedbackType> resultList = dataManager.loadList(loadContext);
        if (CollectionUtils.isNotEmpty(resultList)) {
            Map<String, String> resultMap = new HashMap<>();
            resultList.forEach(dicRcgFeedbackType ->
                    resultMap.put("cat" + dicRcgFeedbackType.getCode(), dicRcgFeedbackType.getLangValue()));
            return gson.toJson(resultMap);
        }
        return null;
    }

    @Override
    public RcgFeedback findFeedback(String feedbackId) {
        if (StringUtils.isBlank(feedbackId)) {
            return null;
        }
        return dataManager.load(LoadContext.create(RcgFeedback.class).setId(UUID.fromString(feedbackId)).setView("rcgFeedback.edit"));
    }

    @Override
    public void sendNotification(RcgFeedback rcgFeedback) {
        PersonExt authorPerson = rcgFeedback.getAuthor().getPerson();
        PersonGroupExt receiverPersonGroup = rcgFeedback.getReceiver();
        PersonExt receiverPerson = receiverPersonGroup.getPerson();

        Map<String, Object> notificationParameters = new HashMap<>();
        notificationParameters.put("authorNameRu", employeeService.getFirstLastName(authorPerson, "ru"));
        notificationParameters.put("receiverNameRu", employeeService.getFirstLastName(receiverPerson, "ru"));
        notificationParameters.put("authorNameEn", employeeService.getFirstLastName(authorPerson, "en"));
        notificationParameters.put("receiverNameEn", employeeService.getFirstLastName(receiverPerson, "en"));

        notificationParameters.put("recognitionUrl", globalConfig.getWebAppUrl() + "/open?screen=recognition-window");

        String notificationCode = rcgFeedback.getDirection().equals(RcgFeedbackDirection.REQUEST) ?
                "recognition.feedback.notify.request" : "recognition.feedback.notify.send";

        /**
         * send notification to receiver
         * */
        notificationService.sendParametrizedNotification(
                notificationCode,
                employeeService.getUserExtByPersonGroupId(receiverPersonGroup.getId(), View.LOCAL),
                notificationParameters);
    }

    @Override
    public RcgFeedbackPojo parseFeedbackToPojo(RcgFeedback rcgFeedback) {
        int languageIndex = languageIndex();

        RcgFeedbackPojo feedbackPojo = metadata.create(RcgFeedbackPojo.class);
        feedbackPojo.setId(rcgFeedback.getId());

        feedbackPojo.setTheme(rcgFeedback.getTheme());
        feedbackPojo.setReverseTheme(languageIndex == 3 ? rcgFeedback.getThemeRu() : rcgFeedback.getThemeEn());
        feedbackPojo.setTranslated(0);

        feedbackPojo.setComment(rcgFeedback.getComment());
        feedbackPojo.setReverseComment(languageIndex == 3 ? rcgFeedback.getCommentRu() : rcgFeedback.getCommentEn());

        DicRcgFeedbackType feedbackType = rcgFeedback.getType();
        if (feedbackType != null) {
            DicRcgFeedbackTypePojo feedbackTypePojo = metadata.create(DicRcgFeedbackTypePojo.class);
            feedbackTypePojo.setId(feedbackType.getId());
            feedbackTypePojo.setImageId(feedbackType.getImage().getId().toString());
            feedbackTypePojo.setName(feedbackType.getLangValue());
            feedbackPojo.setType(feedbackTypePojo);
        }

        feedbackPojo.setAttachments(loadFeedbackAttachments(feedbackPojo.getId()));
        return feedbackPojo;
    }

    private RcgFeedbackCommentPojo parseToCommentPojo(RcgFeedbackComment feedbackComment, int languageIndex, boolean isAutomaticTranslate) {
        RcgFeedbackCommentPojo commentPojo = metadata.create(RcgFeedbackCommentPojo.class);
        commentPojo.setId(feedbackComment.getId());
        commentPojo.setCreateDate(getDateTimeFormatter().format(feedbackComment.getCreateTs()));
        commentPojo.setText(isAutomaticTranslate ? (languageIndex == 1 ? feedbackComment.getTextRu() : feedbackComment.getTextEn()) : feedbackComment.getText());
        commentPojo.setFeedbackId(feedbackComment.getRcgFeedback().getId());

        PersonGroupExt authorPersonGroup = feedbackComment.getAuthor();
        PersonExt authorPerson = authorPersonGroup.getPerson();

        PersonPojo personPojo = metadata.create(PersonPojo.class);
        personPojo.setId(authorPersonGroup.getId());

        String fullName = authorPerson.getFirstName() + " " + authorPerson.getLastName();
        if (languageIndex != 1) {
            fullName = authorPerson.getFirstNameLatin() + " " + authorPerson.getLastNameLatin();
        }
        personPojo.setName(fullName);

        personPojo.setPersonId(authorPerson.getId().toString());
        personPojo.setImage("");
        commentPojo.setAuthor(personPojo);

        String originalText = feedbackComment.getText();
        String commentRu = feedbackComment.getTextRu();
        String commentEn = feedbackComment.getTextEn();

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

    private boolean isAutomaticTranslate() {
        return recognitionService.loadProfileSettings().getAutomaticTranslate();
    }

    private Query createFeedbackQuery(int type, boolean loadCount, int languageIndex, String filter, boolean isAutomaticTranslate, UUID personGroupId, EntityManager em) {
        Query query = em.createNativeQuery(String.format("select %s from (select " +
                        "rf.id pa_id," +
                        "%s rf_theme," +
                        "rf.theme theme," +
                        "rf.theme_en theme_en," +
                        "rf.theme_ru theme_ru," +
                        "%s rf_comment," +
                        "rf.comment_," +
                        "rf.comment_en, " +
                        "rf.comment_ru, " +
                        "rft.id pa_type_id," +
                        "rft.image_id pa_type_image_id," +
                        "rft.lang_value%d pa_type_name," +
                        "rf.feedback_date," +
                        "aupg.id author_id," +//13
                        "aup.id author_person_id, " +//14
                        "aup.employee_number, " +//15
                        "%s as author_name, " +//16
                        "rupg.id receiver_id," +//17
                        "rup.id receiver_person_id, " +//18
                        "rup.employee_number, " +//19
                        "%s as receiver_name ," +//20
                        "(select count(*) from TSADV_RCG_FEEDBACK_COMMENT comm where comm.delete_ts is null and comm.RCG_FEEDBACK_ID = rf.id) comments_count " +
                        "from TSADV_RCG_FEEDBACK rf " +
                        "left join TSADV_DIC_RCG_FEEDBACK_TYPE rft " +
                        "   on rft.id = rf.type_id " +
                        "join BASE_PERSON_GROUP aupg " +
                        "   on aupg.id = rf.author_id " +
                        "left join BASE_PERSON aup " +
                        "   on aup.group_id = aupg.id " +
                        "join BASE_PERSON_GROUP rupg " +
                        "   on rupg.id = rf.receiver_id " +
                        "left join BASE_PERSON rup " +
                        "   on rup.group_id = rupg.id " +
                        "where rf.delete_ts is null " +
                        "   and rf.direction = " + (type == 2 ? "'REQUEST'" : "'SEND'") +
                        "   and rft.delete_ts is null " +
                        "   and %s " +
                        "   and ?2 BETWEEN aup.start_date AND aup.end_date AND aup.delete_ts IS NULL " +
                        "   and ?2 BETWEEN rup.start_date AND rup.end_date AND rup.delete_ts IS NULL " +
                        ") t %s %s",
                loadCount ? "count(*)" : "*",
                isAutomaticTranslate ? (languageIndex == 3 ? "theme_en" : "theme_ru") : "theme",
                isAutomaticTranslate ? (languageIndex == 3 ? "comment_en" : "comment_ru") : "comment_",
                languageIndex,
                languageIndex == 3 ? "aup.first_name_latin||' '||aup.last_name_latin" : "aup.first_name||' '||aup.last_name",
                languageIndex == 3 ? "rup.first_name_latin||' '||rup.last_name_latin" : "rup.first_name||' '||rup.last_name",
                (type == 2 && !loadCount ? "(rupg.id = ?1 or aupg.id = ?1 )" : type != 1 ? "rupg.id = ?1 " : "aupg.id = ?1 "),
                filter != null ? "where lower(t.rf_comment) like ?3 " +
                        "or lower(t.author_name) like ?3 " +
                        "or lower(t.receiver_name) like ?3 " : "",
                loadCount ? "" : " order by t.feedback_date desc"));

        query.setParameter(1, personGroupId);
        query.setParameter(2, CommonUtils.getSystemDate());

        if (filter != null) {
            query.setParameter(3, "%" + filter.toLowerCase() + "%");
        }
        return query;
    }

    private String reverseText(boolean translated, String translatedText, String originalText, String textEn, String textRu) {
        String reverseText = originalText;
        if (translated) {
            if (translatedText.equals(textEn)) {
                reverseText = textRu;
            } else {
                reverseText = textEn;
            }
        } else {
            if (reverseText.equals(textEn)) {
                reverseText = textRu;
            } else {
                reverseText = textEn;
            }
        }
        return reverseText;
    }

    @Override
    public Long feedbackCount(int type, UUID personGroupId, String filter) {
        return feedbackCount(type, personGroupId, StringUtils.isBlank(filter) ? null : filter, languageIndex(), false);
    }

    private Long feedbackCount(int type, UUID personGroupId, String filter, int languageIndex, boolean isAutomaticTranslate) {
        return persistence.callInTransaction(em ->
                (Long) createFeedbackQuery(
                        type,
                        true,
                        languageIndex,
                        filter,
                        isAutomaticTranslate,
                        personGroupId,
                        em)
                        .getFirstResult());
    }

    private PersonGroupExt getCurrentPersonGroup() {
        return userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP);
    }

    private AssignmentExt getCurrentAssignment() {
        return userSessionSource.getUserSession().getAttribute("assignment");
    }

    protected DateFormat getDateTimeFormatter() {
        return recognitionService.getDateTimeFormatter(languageIndex());
    }

    private int languageIndex() {
        return languageIndex(userSessionSource.getLocale().getLanguage().toLowerCase());
    }

    protected String getMessage(String key) {
        return messages.getMainMessage(key, userSessionSource.getLocale());
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
}