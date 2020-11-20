package kz.uco.tsadv.listener;

import com.drew.lang.annotations.NotNull;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.core.listener.AfterInsertEntityListener;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import kz.uco.base.service.GoogleTranslateService;
import kz.uco.base.service.NotificationService;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.recognition.*;
import kz.uco.tsadv.modules.recognition.enums.LogActionType;
import kz.uco.tsadv.modules.recognition.enums.PointOperationType;
import kz.uco.tsadv.modules.recognition.enums.RecognitionCoinType;
import kz.uco.tsadv.modules.recognition.exceptions.RecognitionException;
import kz.uco.tsadv.service.CallStoredFunctionService;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.service.RecognitionSchedulerService;
import kz.uco.tsadv.service.RecognitionService;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Connection;
import java.util.*;

@Component("tsadv_RecognitionListener")
public class RecognitionListener implements BeforeInsertEntityListener<Recognition>, AfterInsertEntityListener<Recognition> {

    private static final Logger logger = LoggerFactory.getLogger(RecognitionListener.class);

    @Inject
    private RecognitionSchedulerService recognitionSchedulerService;

    @Inject
    private NotificationService notificationService;

    @Inject
    private EmployeeService employeeService;

    @Inject
    private RecognitionService recognitionService;

    @Inject
    private GoogleTranslateService googleTranslateService;

    @Inject
    private GlobalConfig globalConfig;

    @Inject
    private DataManager dataManager;

    @Inject
    private Metadata metadata;

    @Inject
    private Messages messages;

    @Inject
    private CallStoredFunctionService callStoredFunctionService;

    private void changeAuthorData(Recognition recognition, PersonGroupExt authorPersonGroup, PersonPoint personPoint, EntityManager em) {
        Long recognitionCoins = recognition.getCoins();

        personPoint.setPoints(personPoint.getPoints() - recognitionCoins);
        em.merge(personPoint);

        PersonCoinLog personCoinLog = metadata.create(PersonCoinLog.class);
        personCoinLog.setActionType(LogActionType.SEND_RECOGNITION); //Вы отправили Благодарность
        personCoinLog.setDate(new Date());
        personCoinLog.setOperationType(PointOperationType.ISSUE);
        personCoinLog.setPersonGroup(authorPersonGroup);
        personCoinLog.setAnotherPersonGroup(recognition.getReceiver());
        personCoinLog.setQuantity(recognitionCoins);
        personCoinLog.setRecognition(recognition);
        personCoinLog.setCoinType(RecognitionCoinType.POINT);
        em.persist(personCoinLog);
    }

    private void changeReceiverData(Recognition recognition, PersonGroupExt receiverPersonGroup, EntityManager em) {
        Long recognitionCoins = recognition.getCoins();
        UUID receiverPgId = receiverPersonGroup.getId();

        LoadContext<PersonCoin> loadContext = LoadContext.create(PersonCoin.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$PersonCoin e " +
                        "where e.personGroup.id = :pgId");
        query.setParameter("pgId", receiverPgId);
        loadContext.setQuery(query);
        loadContext.setView(View.LOCAL);

        PersonCoin personCoin = dataManager.load(loadContext);
        if (personCoin == null) {
            throw new NullPointerException(getMessage("receiver.wallet.hc.null"));
        }

        Long coins = personCoin.getCoins();
        personCoin.setCoins(coins + recognitionCoins);
        em.merge(personCoin);

        PersonCoinLog personCoinLog = metadata.create(PersonCoinLog.class);
        personCoinLog.setActionType(LogActionType.RECEIVE_RECOGNITION); // Вы получили Благодарность
        personCoinLog.setDate(new Date());
        personCoinLog.setOperationType(PointOperationType.RECEIPT);
        personCoinLog.setPersonGroup(receiverPersonGroup);
        personCoinLog.setCoinType(RecognitionCoinType.COIN);
        personCoinLog.setQuantity(recognitionCoins);
        personCoinLog.setRecognition(recognition);
        personCoinLog.setAnotherPersonGroup(recognition.getAuthor());
        em.persist(personCoinLog);
    }

    @Override
    public void onBeforeInsert(Recognition recognition, EntityManager entityManager) {
        PersonGroupExt author = recognition.getAuthor();
        PersonGroupExt receiver = recognition.getReceiver();

        List<String> errors = recognitionService.validateRecognition(recognition);

        if (!errors.isEmpty()) {
            StringBuilder errorsBuilder = new StringBuilder();
            errors.forEach(s -> errorsBuilder.append(s).append("\n"));

            throw new RecognitionException(errorsBuilder.toString());
        }

        PersonPoint personPoint = recognitionService.loadPersonPoint(author.getId());
        recognitionService.validatePoints(recognition, personPoint);

        changeAuthorData(recognition, author, personPoint, entityManager);
        changeReceiverData(recognition, receiver, entityManager);
        transformComment(recognition);
        translateComment(recognition);
//        createPersonMedal(entityManager, receiver); //add medal (badges) for person
    }

    private void createPersonMedal(@NotNull EntityManager entityManager, @NotNull PersonGroupExt receiver) {
        String sql = "select medal_id " +
                "from aa_check_medal where receiver_id='" + receiver.getId() + "'";
        callStoredFunctionService.execSqlCallProcedureList(sql).stream().forEach(medalId -> {
            PersonMedal personMedal = metadata.create(PersonMedal.class);
            personMedal.setPersonGroup(receiver);
            personMedal.setMedal(entityManager.getReference(Medal.class, (UUID) medalId));
        });
    }

    private void transformComment(Recognition recognition) {
        String comment = recognition.getComment();
        if (!StringUtils.isBlank(comment)) {
            comment = comment.trim();
            if (comment.length() > 1) {
                String firstLetter = comment.substring(0, 1).toUpperCase();
                comment = firstLetter + comment.substring(1, comment.length());
                recognition.setComment(comment);
            }
        }
    }

    private void translateComment(Recognition recognition) {
        String originalText = recognition.getComment();
        if (StringUtils.isNotBlank(originalText)) {
            try {
                recognition.setCommentEn(googleTranslateService.translate(originalText, "en"));
                recognition.setCommentRu(googleTranslateService.translate(originalText, "ru"));
            } catch (Exception ex) {
                recognition.setCommentEn(originalText);
                recognition.setCommentRu(originalText);
            }
        }
    }

    private String getMessage(String key) {
        return messages.getMainMessage(key);
    }

    @Override
    public void onAfterInsert(Recognition recognition, Connection connection) {
//        recognitionSchedulerService.checkMedals(recognition.getReceiver().getId());

        sendNotifications(recognition);
    }

    protected void sendNotifications(Recognition recognition) {
        PersonExt authorPerson = recognition.getAuthor().getPerson();
        PersonGroupExt receiverPersonGroup = recognition.getReceiver();
        PersonExt receiverPerson = receiverPersonGroup.getPerson();

        Map<String, Object> notificationParameters = new HashMap<>();
        notificationParameters.put("authorNameRu", getFirstLastName(authorPerson, "ru"));
        notificationParameters.put("receiverNameRu", getFirstLastName(receiverPerson, "ru"));
        notificationParameters.put("authorNameEn", getFirstLastName(authorPerson, "en"));
        notificationParameters.put("receiverNameEn", getFirstLastName(receiverPerson, "en"));

        notificationParameters.put("recognitionUrl", globalConfig.getWebAppUrl() + "/open?screen=recognition-window");

        /**
         * send notification to receiver
         * */
        UserExt userExt = employeeService.getUserExtByPersonGroupId(receiverPersonGroup.getId(), View.LOCAL);
        notificationService.sendParametrizedNotification(
                "recognition.notify.receiver",
                userExt,
                notificationParameters);

        if (BooleanUtils.isTrue(recognition.getNotifyManager())) {
            UUID receiverPositionGroupId = employeeService.getPersonPositionGroup(receiverPersonGroup.getId());

            if (receiverPositionGroupId == null) {
                throw new NullPointerException("Receiver doesn't have position group!");
            }

            Map<UserExt, PersonExt> receiverManager = recognitionService.findManagerByPositionGroup(receiverPositionGroupId, receiverPersonGroup.getId());
            if (receiverManager != null) {
                Map.Entry<UserExt, PersonExt> entry = receiverManager.entrySet().iterator().next();
                PersonExt personExt = entry.getValue();

                notificationParameters.put("managerNameRu", getFirstLastName(personExt, "ru"));
                notificationParameters.put("managerNameEn", getFirstLastName(personExt, "en"));

                notificationService.sendParametrizedNotification(
                        "recognition.notify.receiver.manager",
                        entry.getKey(),
                        notificationParameters);
            } else {
                logger.warn(String.format("Manager for position group: [%s] not found!", receiverPositionGroupId.toString()));
            }
        }
    }

    private String getFirstLastName(PersonExt personExt, String lang) {
        String resultFirstLastName = "";
        if (personExt != null) {
            resultFirstLastName = personExt.getFirstName() + " " + personExt.getLastName();
            if (lang.equals("en")) {
                String firstNameLatin = personExt.getFirstNameLatin(),
                        lastNameLatin = personExt.getLastNameLatin();
                if (StringUtils.isNotBlank(firstNameLatin) && StringUtils.isNotBlank(lastNameLatin)) {
                    resultFirstLastName = firstNameLatin + " " + lastNameLatin;
                }
            }
        }
        return resultFirstLastName;
    }
}