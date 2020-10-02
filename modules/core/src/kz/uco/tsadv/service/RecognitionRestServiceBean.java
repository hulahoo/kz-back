package kz.uco.tsadv.service;

import com.google.gson.Gson;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.app.serialization.EntitySerializationAPI;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.service.GoogleTranslateService;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.api.BaseResult;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.global.entity.PersonPojo;
import kz.uco.tsadv.global.entity.RestResult;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.recognition.*;
import kz.uco.tsadv.modules.recognition.dictionary.DicPersonAwardType;
import kz.uco.tsadv.modules.recognition.dictionary.DicPersonPreferenceType;
import kz.uco.tsadv.modules.recognition.dictionary.DicQuality;
import kz.uco.tsadv.modules.recognition.dictionary.DicRecognitionType;
import kz.uco.tsadv.modules.recognition.enums.AwardStatus;
import kz.uco.tsadv.modules.recognition.exceptions.RecognitionException;
import kz.uco.tsadv.modules.recognition.pojo.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

@Service(RecognitionRestService.NAME)
public class RecognitionRestServiceBean implements RecognitionRestService {

    private static final Gson gson = new Gson();

    private static final Logger logger = LoggerFactory.getLogger(RecognitionRestServiceBean.class);

    @Inject
    protected RecognitionService recognitionService;

    @Inject
    private Persistence persistence;

    @Inject
    private UserSessionSource userSessionSource;
    @Inject
    private GoogleTranslateService googleTranslateService;

    @Inject
    protected EntitySerializationAPI entitySerialization;
    @Inject
    private DataManager dataManager;
    @Inject
    private Metadata metadata;
    @Inject
    private MessageTools messageTools;
    @Inject
    private CommonService commonService;

    @Override
    public String getDraftPersonAward(String authorEmployeeNumber, String receiverEmployeeNumber) {
        PersonAward personAward = recognitionService.findDraftPersonAward(
                recognitionService.getPersonGroupId(authorEmployeeNumber),
                recognitionService.getPersonGroupId(receiverEmployeeNumber));
        if (personAward != null) {
            PersonAwardPojo personAwardPojo = metadata.create(PersonAwardPojo.class);
            personAwardPojo.setStatus(personAward.getStatus().getId());
            personAwardPojo.setHistory(personAward.getHistory());
            personAwardPojo.setWhy(personAward.getWhy());

            return entitySerialization.toJson(personAwardPojo, null);
        }
        return null;
    }

    @Override
    public String loadQualities(String language) {
        return toJson(recognitionService.loadQualities(languageIndex(language)), null);
    }

    @Override
    public String loadOrganizations(String language) {
        return toJson(recognitionService.loadOrganizations(languageIndex(language)), null);
    }

    @Override
    public void uploadPersonImage(String imageContent) {
        recognitionService.uploadPersonImage(imageContent);
    }

    @Override
    public String loadTopSender() {
        return recognitionService.loadTopSender();
    }

    @Override
    public BaseResult changePersonImage(String employeeNumber, String imageContent) {
        return recognitionService.changePersonImage(employeeNumber, imageContent);
    }

    @Override
    public String loadTopSender(String language) {
        return recognitionService.loadTopSender(language);
    }

    @Override
    public String loadTopSender(Integer countOfMonths) {
        return recognitionService.loadTopSender(countOfMonths);
    }

    @Override
    public String loadTopSender(String language, Integer countOfMonths) {
        return recognitionService.loadTopSender(language, countOfMonths);
    }

    @Override
    public String loadTopAwarded() {
        return recognitionService.loadTopAwarded();
    }

    @Override
    public String loadTopAwarded(String language) {
        return recognitionService.loadTopAwarded(language);
    }

    @Override
    public String loadTopAwarded(Integer countOfMonths) {
        return recognitionService.loadTopAwarded(countOfMonths);
    }

    @Override
    public String loadTopAwarded(String language, Integer countOfMonths) {
        return recognitionService.loadTopAwarded(language, countOfMonths);
    }

    @Override
    public String loadRecognitionTypes() {
        return toJson(recognitionService.loadRecognitionTypes(), null);
    }

    @Override
    public boolean hasActiveQuestion() {
        return recognitionService.hasActiveQuestion();
    }

    @Override
    public String loadActiveQuestion() {
        RcgQuestionPojo rcgQuestionPojo = recognitionService.loadActiveQuestion(getCurrentPersonGroup().getId());
        if (rcgQuestionPojo != null) {
            return entitySerialization.toJson(rcgQuestionPojo, null);
        }
        return null;
    }

    @Override
    public String loadAllRcgFaq(String language) {
        return recognitionService.loadAllRcgFaq(language);
    }

    @Override
    public String loadRcgFaq(String faqId, String language) {
        return recognitionService.loadRcgFaq(faqId, language);
    }

    @Override
    public String sendComment(RecognitionCommentPojo commentPojo) {
        return recognitionService.sendComment(commentPojo);
    }

    @Override
    public String sendLike(String recognitionId) {
        return recognitionService.sendLike(recognitionId);
    }

    @Override
    public String sendLike(String employeeNumber, String recognitionId) {
        return recognitionService.sendLike(employeeNumber, recognitionId);
    }

    @Override
    public String sendQuestionAnswer(String questionId, String answerId) {
        RestResult restResult = new RestResult();
        try {
            String language = userSessionSource.getLocale().getLanguage().toLowerCase();

            if (StringUtils.isBlank(questionId)) {
                throw new RecognitionException(getMessage("question.id.null", language));
            }

            if (StringUtils.isBlank(answerId)) {
                throw new RecognitionException(getMessage("answer.id.null", language));
            }

            PersonGroupExt personGroupExt = userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP);
            if (personGroupExt == null) {
                throw new RecognitionException(getMessage("user.person.null", language));
            }

            recognitionService.sendQuestionAnswer(
                    personGroupExt,
                    UUID.fromString(questionId),
                    UUID.fromString(answerId),
                    recognitionService.getQuestionCoins(UUID.fromString(questionId)));

            restResult.setSuccess(true);
            restResult.setMessage(getMessage("thanks.for.answer", language));
        } catch (Exception ex) {
            restResult.setSuccess(false);
            restResult.setMessage(ex.getMessage());
        }
        return gson.toJson(restResult);
    }

    @Override
    public String createPersonAward(PersonAwardPojo personAwardPojo, String language) {
        RestResult restResult = new RestResult();
        try {

            String authorEmployeeNumber = personAwardPojo.getAuthorEmployeeNumber();
            String receiverEmployeeNumber = personAwardPojo.getReceiverEmployeeNumber();
            String statusCode = personAwardPojo.getStatus();

            if (StringUtils.isBlank(authorEmployeeNumber)) {
                throw new RecognitionException(getMessage("author.employee.number.null", language));
            }

            if (StringUtils.isBlank(receiverEmployeeNumber)) {
                throw new RecognitionException(getMessage("receiver.employee.number.null", language));
            }

            if (StringUtils.isBlank(statusCode) || AwardStatus.fromId(statusCode) == null) {
                throw new RecognitionException(getMessage("award.status.null", language));
            }

            DicPersonAwardType personAwardType = commonService.getEntity(DicPersonAwardType.class, "HEART_AWARD");
            if (personAwardType == null) {
                throw new RecognitionException(getMessage("award.type.nf", language));
            }

            AwardProgram awardProgram = recognitionService.getActiveAwardProgram();

            AwardStatus awardStatus = AwardStatus.fromId(statusCode);
            if (awardStatus == null || awardStatus.equals(AwardStatus.AWARDED) || awardStatus.equals(AwardStatus.SHORTLIST)) {
                throw new RecognitionException("Incorrect status");
            }

            UUID authorPersonGroupId = recognitionService.getPersonGroupId(authorEmployeeNumber);
            UUID receiverPersonGroupId = recognitionService.getPersonGroupId(receiverEmployeeNumber);

            if (!recognitionService.hasInSuggestionSearch(receiverPersonGroupId)) {
                throw new RecognitionException(getMessage("award.receiver.not.correct", language));
            }
            persistence.runInTransaction(em -> {
                PersonAward personAward = recognitionService.findDraftPersonAward(authorPersonGroupId, receiverPersonGroupId);
                boolean isCreate = false;
                if (personAward == null) {
                    personAward = metadata.create(PersonAward.class);
                    personAward.setAuthor(em.getReference(PersonGroupExt.class, authorPersonGroupId));
                    personAward.setReceiver(em.getReference(PersonGroupExt.class, receiverPersonGroupId));
                    personAward.setType(personAwardType);
                    personAward.setDate(new Date());
                    personAward.setAwardProgram(awardProgram);
                    isCreate = true;
                }

                personAward.setStatus(awardStatus);
                personAward.setWhy(personAwardPojo.getWhy());
                personAward.setHistory(personAwardPojo.getHistory());

                if (isCreate) {
                    em.persist(personAward);
                } else {
                    em.merge(personAward);
                }
            });

            restResult.setSuccess(true);
        } catch (Exception ex) {
            logger.error(ex.getMessage());

            restResult.setSuccess(false);
            restResult.setMessage(ex.getMessage());
        }
        return gson.toJson(restResult);
    }

    @Override
    public String createRecognition(RecognitionCreatePojo recognitionCreatePojo, String language) {
        RestResult restResult = new RestResult();

        try {
            String authorEmployeeNumber = recognitionCreatePojo.getAuthorEmployeeNumber();
            String receiverEmployeeNumber = recognitionCreatePojo.getReceiverEmployeeNumber();
            UUID recognitionTypeId = recognitionCreatePojo.getRecognitionTypeId();

            if (StringUtils.isBlank(authorEmployeeNumber)) {
                throw new RecognitionException(getMessage("author.employee.number.null", language));
            }

            if (StringUtils.isBlank(receiverEmployeeNumber)) {
                throw new RecognitionException(getMessage("receiver.employee.number.null", language));
            }

            if (recognitionTypeId == null) {
                throw new RecognitionException(messageTools.getDefaultRequiredMessage(metadata.getClassNN(Recognition.class), "recognitionType"));
            }

            persistence.runInTransaction(em -> {
                DicRecognitionType recognitionType = em.find(DicRecognitionType.class, recognitionTypeId, View.LOCAL);

                Recognition recognition = metadata.create(Recognition.class);
                recognition.setAuthor(em.find(PersonGroupExt.class, recognitionService.getPersonGroupId(authorEmployeeNumber), "personGroup.search"));
                recognition.setReceiver(em.find(PersonGroupExt.class, recognitionService.getPersonGroupId(receiverEmployeeNumber), "personGroup.search"));
                recognition.setRecognitionDate(new Date());
                recognition.setRecognitionType(recognitionType);
                recognition.setComment(recognitionCreatePojo.getComment());

                if (recognitionType != null) {
                    recognition.setCoins(recognitionType.getCoins());
                }

                recognition.setNotifyManager(recognitionCreatePojo.getNotifyManager());
                em.persist(recognition);

                List<QualityPojo> qualityPojos = recognitionCreatePojo.getQualities();
                if (qualityPojos != null && !qualityPojos.isEmpty()) {
                    for (QualityPojo qualityPojo : qualityPojos) {
                        RecognitionQuality recognitionQuality = metadata.create(RecognitionQuality.class);
                        recognitionQuality.setRecognition(recognition);
                        recognitionQuality.setQuality(em.getReference(DicQuality.class, qualityPojo.getId()));
                        em.persist(recognitionQuality);
                    }
                }
            });

            restResult.setSuccess(true);
        } catch (Exception ex) {
            logger.error(ex.getMessage());

            restResult.setSuccess(false);
            restResult.setMessage(ex.getMessage());
        }
        return gson.toJson(restResult);
    }

    @Override
    public Long myNomineesCount(int year, String organizationGroupId) {
        return recognitionService.myNomineesCount(year, organizationGroupId);
    }

    @Override
    public String loadMyNominees(int offset, int maxResults, int year, String language, String organizationGroupId) {
        return recognitionService.loadMyNominees(offset, maxResults, year, languageIndex(language), organizationGroupId);
    }

    @Override
    public String loadAllNominee(int offset, int maxResults, String language, int year, String organizationGroupId) {
        return recognitionService.loadAllNominee(offset, maxResults, languageIndex(language), year, organizationGroupId);
    }

    @Override
    public String loadProfile(String employeeNumber, String language) {
        UUID personGroupId = recognitionService.getPersonGroupId(employeeNumber);
        return recognitionService.loadProfile(personGroupId.toString(), languageIndex(language));
    }

    @Override
    public String loadTopNominee(int year, String organizationGroupId) {
        return recognitionService.loadTopNominee(year, organizationGroupId);
    }

    @Override
    public Long allNomineesCount(int year, String organizationGroupId) {
        return recognitionService.allNomineesCount(year, organizationGroupId);
    }

    @Override
    public Long recognitionCommentsCount(String recognitionId) {
        return recognitionService.recognitionCommentsCount(recognitionId);
    }

    @Override
    public String recognitionComments(String recognitionId, int offset, int maxResults, String language, int automaticTranslate) {
        List<RecognitionCommentPojo> comments = recognitionService.loadCommentsByRecognition(recognitionId, offset, maxResults, languageIndex(language), automaticTranslate == 1);
        return toJson(comments, null);
    }

    @Override
    public String loadTeamProfiles(int offset, int maxResults, String language) {
        return recognitionService.loadTeamProfiles(offset, maxResults, languageIndex(language));
    }

    @Override
    public Long teamProfilesCount() {
        return recognitionService.teamProfilesCount();
    }

    @Override
    public String loadPersonPreferences(String employeeNumber) {
        List<PreferencePojo> preferences = recognitionService.loadPersonPreferences(recognitionService.getPersonGroupId(employeeNumber));
        return toJson(preferences, null);
    }

    @Override
    public String getPersonPreferenceTypes(String language) {
        List<PreferenceTypePojo> preferenceTypes = new ArrayList<>();
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery(String.format("select " +
                            "p.id, " +
                            "p.code," +
                            "p.LANG_VALUE%d " +
                            "from TSADV_DIC_PERSON_PREFERENCE_TYPE p " +
                            "where p.delete_ts is null ",
                    languageIndex(language)));
            List<Object[]> rows = query.getResultList();
            if (rows != null && !rows.isEmpty()) {
                for (Object[] row : rows) {
                    PreferenceTypePojo preferenceType = metadata.create(PreferenceTypePojo.class);
                    preferenceType.setId((UUID) row[0]);
                    preferenceType.setCode((String) row[1]);
                    preferenceType.setName((String) row[2]);
                    preferenceTypes.add(preferenceType);
                }
            }
        }
        return toJson(preferenceTypes, null);
    }

    @Override
    public Long recognitionsCount(int wallType, String profileEmployeeNumber, String organizationGroupId, String filter, String language) {
        UUID personGroupId = null;
        if (StringUtils.isNotBlank(profileEmployeeNumber)) {
            personGroupId = recognitionService.getPersonGroupId(profileEmployeeNumber);
        }

        if (StringUtils.isBlank(organizationGroupId) || "-1".equals(organizationGroupId)) {
            organizationGroupId = null;
        }

        if (filter == null || filter.trim().length() == 0) {
            filter = null;
        }

        return recognitionService.recognitionsCount(wallType, personGroupId, organizationGroupId, filter, languageIndex(language));
    }

    @Override
    public String loadRecognitions(int offset, int maxResult, int wallType, String sessionEmployeeNumber, String profileEmployeeNumber, String organizationGroupId, String filter, String language, int automaticTranslate) {
        UUID personGroupId = null;
        UUID sessionPersonGroupId = null;
        if (StringUtils.isNotBlank(profileEmployeeNumber)) {
            personGroupId = recognitionService.getPersonGroupId(profileEmployeeNumber);
        }

        if (StringUtils.isBlank(organizationGroupId) || "-1".equals(organizationGroupId)) {
            organizationGroupId = null;
        }

        if (filter == null || filter.trim().length() == 0) {
            filter = null;
        }
        if (StringUtils.isNotBlank(sessionEmployeeNumber)) {
            sessionPersonGroupId = recognitionService.getPersonGroupId(sessionEmployeeNumber);
        }
        boolean self = wallType == 0;

        int languageIndex = languageIndex(language);

        List<RecognitionPojo> recognitions = new ArrayList<>();

        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
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
                            "pa.comment_," +
                            "pa.comment_en, " +
                            "pa.comment_ru," +
                            "(select count(*) from TSADV_RECOGNITION_LIKE l where l.recognition_id = pa.id " +
                            "and l.delete_ts is null) like_count, " +
                            "%s as current_like, " +
                            "(select count(*) from TSADV_RECOGNITION_COMMENT comm where comm.recognition_id = pa.id " +
                            "and comm.delete_ts is null) comment_count, " +
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
                    automaticTranslate == 1 ? (languageIndex == 3 ? "pa.comment_en" : "pa.comment_ru") : "pa.comment_",
                    languageIndex,
                    languageIndex == 3 ? "aup.first_name_latin||' '||aup.last_name_latin" : "aup.first_name||' '||aup.last_name",
                    languageIndex == 3 ? "rup.first_name_latin||' '||rup.last_name_latin" : "rup.first_name||' '||rup.last_name",
                    (sessionPersonGroupId != null) ?
                            "(select count(*) ::INTEGER from TSADV_RECOGNITION_LIKE comm " +
                                    "where comm.delete_ts is null and comm.recognition_id = pa.id and comm.person_group_id = ?4) "
                            : "0",
                                    organizationGroupId != null ?
                            "join base_assignment a " +
                                    "ON a.person_group_id = rupg.id " : "",
                    (wallType != -1 && personGroupId != null) ? "and rupg.id = ?2 " : "",
                    organizationGroupId != null ?
                            String.format("and ?1 between a.start_date and a.end_date " +
                                    "and a.organization_group_id in (%s)", recognitionService.getOrganizationPath(organizationGroupId.toString(), null)) : "",
                    filter != null ? "where lower(t.pa_comment) like ?3 " +
                            "or lower(t.author_name) like ?3 " +
                            "or lower(t.receiver_name) like ?3 " : ""));

            query.setParameter(1, CommonUtils.getSystemDate());

            if (personGroupId != null) {
                query.setParameter(2, personGroupId);
            }

            if (organizationGroupId != null) {
                query.setParameter(2, organizationGroupId);
            }

            if (filter != null) {
                query.setParameter(3, "%" + filter.toLowerCase() + "%");
            }

            if (sessionPersonGroupId != null) {
                query.setParameter(4, sessionPersonGroupId);
            }

            query.setFirstResult(offset);
            query.setMaxResults(maxResult);

            List<Object[]> resultList = query.getResultList();

            for (Object[] row : resultList) {
                RecognitionPojo recognitionPojo = metadata.create(RecognitionPojo.class);
                recognitionPojo.setId((UUID) row[0]);
                recognitionPojo.setText((String) row[1]);
                recognitionPojo.setLikeCount((Long) row[16]);
                recognitionPojo.setCurrentLike((Integer) row[17] > 0 ? 1 : 0);
                recognitionPojo.setCommentCount((Long) row[18]);

                String originalText = (String) row[13];
                String commentRu = (String) row[15];
                String commentEn = (String) row[14];

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

                recognitionPojo.setSay(self ? getMessage("rcg.say.self", language) : getMessage("rcg.say", language));
                recognitionPojo.setCreateDate(recognitionService.getDateTimeFormatter(languageIndex).format((Date) row[6]));

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

                recognitionPojo.setQualities(recognitionService.loadQualities(recognitionPojo.getId(), languageIndex));

                recognitions.add(recognitionPojo);
            }
        }
        return toJson(recognitions, null);


    }

    private String getMessage(String key, String language) {
        return recognitionService.getMessage(key, language);
    }

    @Override
    public void updatePersonPreference(String preferenceTypeId, String description) {
        PersonGroupExt personGroupExt = getCurrentPersonGroup();
        if (personGroupExt == null) {
            throw new RecognitionException("Person group is null!");
        }

        if (StringUtils.isBlank(preferenceTypeId)) {
            throw new RecognitionException("Preference type ID is null or empty!");
        }

        UUID uuidPreferenceTypeId = UUID.fromString(preferenceTypeId);

        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            PersonPreference personPreference = getPersonPreference(personGroupExt.getId(), uuidPreferenceTypeId);

            String descriptionEn = description;
            String descriptionRu = description;

            if (StringUtils.isNotBlank(description)) {
                descriptionEn = googleTranslateService.translate(description, "en");
                descriptionRu = googleTranslateService.translate(description, "ru");
            }

            if (personPreference == null) {
                personPreference = metadata.create(PersonPreference.class);
                personPreference.setPersonGroup(personGroupExt);
                personPreference.setPreferenceType(em.getReference(DicPersonPreferenceType.class, uuidPreferenceTypeId));
                personPreference.setDescription(description);
                personPreference.setDescriptionEn(descriptionEn);
                personPreference.setDescriptionRu(descriptionRu);
                em.persist(personPreference);
            } else {
                personPreference.setDescription(description);
                personPreference.setDescriptionEn(descriptionEn);
                personPreference.setDescriptionRu(descriptionRu);
                em.merge(personPreference);
            }
            tx.commit();
        }
    }

    @Override
    public String loadInfoByQRCode(String qrCode) {
        return recognitionService.loadInfoByQRCode(qrCode);
    }

    @Override
    public String useVoucher(String qrCode) {
        return recognitionService.useVoucher(qrCode);
    }

    private PersonPreference getPersonPreference(UUID personGroupId, UUID preferenceTypeId) {
        LoadContext<PersonPreference> loadContext = LoadContext.create(PersonPreference.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$PersonPreference e " +
                        "join e.preferenceType pt " +
                        "where e.personGroup.id = :pgId and pt.id = :pTypeId");
        query.setParameter("pgId", personGroupId);
        query.setParameter("pTypeId", preferenceTypeId);

        loadContext.setQuery(query);
        loadContext.setView("personPreference.edit");
        return dataManager.load(loadContext);
    }

    private int languageIndex(String language) {
        if (StringUtils.isBlank(language)) {
            throw new RecognitionException("Parameter \"language\" is null or empty!");
        }
        int languageIndex = -1;
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

        if (languageIndex == -1) {
            throw new RecognitionException(String.format("Language \"%s\" not supported!", language));
        }
        return languageIndex;
    }

    protected String toJson(Collection<? extends Entity> entities, View view) {
        return entitySerialization.toJson(entities, view);
    }

    private PersonGroupExt getCurrentPersonGroup() {
        return userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP);
    }
}