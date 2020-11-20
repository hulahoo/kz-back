package kz.uco.tsadv.service;


import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.LoadContext;
import kz.uco.base.entity.shared.PersonGroup;
import kz.uco.tsadv.api.BaseResult;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.recognition.*;
import kz.uco.tsadv.modules.recognition.dictionary.DicDeliveryAddress;
import kz.uco.tsadv.modules.recognition.enums.PointOperationType;
import kz.uco.tsadv.modules.recognition.pojo.*;
import kz.uco.tsadv.modules.recognition.shop.*;

import javax.annotation.Nullable;
import java.text.DateFormat;
import java.util.*;

public interface RecognitionService {
    String NAME = "tsadv_RecognitionService";

    String loadRecognitions(int page, long lastCount, int wallType, UUID personGroupId, String organizationGroupId, String filter);

    String loadComments(String recognitionId, int page);

    Long recognitionsCount(UUID personGroupId);

    boolean accessRecognition(UUID authorPersonGroupId, UUID receiverPersonGroupId);

    boolean checkProfileInTeam(UUID personGroupId);

    String loadWishList(int page, long lastCount, UUID personGroupId);

    Long logsCount();

    String sendComment(RecognitionCommentPojo commentPojo);

    String loadCategories();

    FileDescriptor getRecognitionTypeImage(String rcgTypeCode, boolean empty);

    FileDescriptor getRecognitionGoodsImage(String goodsId);

    FileDescriptor getRecognitionMedalImage(String medalId);

    FileDescriptor getRcgAnswerIcon(String answerId);

    String loadOrganizations();

    List<OrganizationPojo> loadOrganizations(int languageIndex);

    String loadProfiles(int page, long lastCount);

    String loadProfile(String personGroupId, int languageIndex);

    String loadTeamProfiles(int offset, int maxResults, int languageIndex);

    Long teamProfilesCount();

    boolean hasTeamProfiles();

    List<UUID> loadTeamProfiles(UUID hierarchyId, UUID positionGroupId);

    List<UUID> getPersonGroupEmployee(UUID hierarchyId, UUID personGroupId, boolean isManager);

    List<RecognitionTypePojo> loadRecognitionTypes(UUID personGroupId);

    List<RecognitionTypePojo> loadRecognitionTypes();

    String loadPersonHeartAward(UUID personGroupId);

    List<PreferencePojo> loadPersonPreferences(UUID personGroupId);

    List<PreferencePojo> savePersonPreference(PersonGroupExt personGroupExt, List<PreferencePojo> preferences);

    PersonCoin loadPersonCoin(UUID personGroupId);

    PersonPoint loadPersonPoint(UUID personGroupId);

    void sendHeartCoins(HeartCoinPojo heartCoinPojo);

    String loadCoinLogs(int page, long lastCount);

    String loadTopSender();

    String loadTopSender(String language);

    String loadTopSender(Integer countOfMonths);

    String loadTopSender(String language, Integer countOfMonths);

    String loadTopAwarded();

    String loadTopAwarded(String language);

    String loadTopAwarded(Integer countOfMonths);

    String loadTopAwarded(String language, Integer countOfMonths);

    String loadComingBirthdays();

    RcgQuestionPojo loadActiveQuestion(UUID personGroupId);

    boolean hasActiveQuestion();

    void sendQuestionAnswer(PersonGroupExt personGroup, UUID questionId, UUID answerId, Long coins);

    Long getQuestionCoins(UUID questionId);

    List<RcgFaq> loadRcgFaqs();

    String loadAllRcgFaq(String language);

    String loadRcgFaq(String faqId, String language);

    String sendLike(String recognitionId);

    String sendLike(String employeeNumber, String recognitionId);

    PersonAward findDraftPersonAward(UUID authorPersonGroupId, UUID receiverPersonGroupId);

    PersonAward findDraftPersonAward(UUID receiverPersonGroupId);

    String loadPersonMedals(String personGroupId);

    Long personMedalCount(UUID personGroupId);

    List<AwardProgramPerson> loadAwardProgramPersons(AwardProgram awardProgram, int firstResult, int maxResult);

    int getAwardProgramPersonsCount(AwardProgram awardProgram);

    List<AwardProgramPerson> loadAwardProgramPersonsWithFilter(AwardProgram awardProgram, int firstResult,
                                                               int maxResult, String filterParam);

    void addShortList(Set<AwardProgramPerson> awardProgramPersons);

    void nominate(Set<SelectedPersonAward> selectedPersonAwards);

    String loadGoods(int page, long lastCount, String categoryId, String sort);

    void addGoodsToCart(UUID goodsId);

    void addToWishList(UUID goodsId);

    void removeGoods(GoodsCart goodsCart);

    Long goodsCartCount();

    String loadCart(int page, long lastCount);

    Long getPersonBalance();

    Long getTotalSum(UUID currentPersonGroupId);

    void removeGoodsFromCart(UUID goodsId);

    int uploadProfileImage(String imageContent);

    void uploadPersonImage(String imageContent);

    BaseResult changePersonImage(String employeeNumber, String imageContent);

    boolean giveCoinForPhoto(UUID personGroupId);

    boolean hasDraftPersonAward(UUID authorPersonGroupId, UUID receiverPersonGroupId);

    boolean hasPersonAward(UUID authorPersonGroupId, UUID receiverPersonGroupId);

    Date getCompareDate();

    LoadContext<PersonGroupExt> getSuggestionSearchLC();

    boolean hasInSuggestionSearch(UUID personGroupId);

    GoodsOrder checkoutCart(DicDeliveryAddress address);

    List<GoodsOrder> checkoutCartList(DicDeliveryAddress address);

    String loadGoodsOrders(int page, long lastCount);

    Long goodsOrdersCount();

    Map<UserExt, PersonExt> findManagerByPositionGroup(UUID positionGroupId, UUID receiverPersonGroupId);

    void sendCheckoutNotification(GoodsOrder goodsOrder);

    void sendCheckoutNotifications(List<GoodsOrder> goodsOrderList);

    GoodsOrder loadGoodsOrder(String goodsOrderNumber);

    int updateGoodsQuantity(String goodsCartId, String goodsId, long quantity);

    String loadTopNominee(int year, String organizationGroupId);

    String loadTopNomineeWithDefault(int currentYear, String organizationGroupId, int lastYear, boolean onChange);

    String loadAllNominee(int page, long lastCount, int year, String organizationGroupId);

    String loadAllNomineeWithDefault(int page, long lastCount, int year, String organizationGroupId, int lastYear, boolean onChange);

    String loadNomineeYears(int awarded);

    String loadMyNomineeYears();

    String loadMyNominees(int page, long lastCount, int year, String organizationGroupId);

    String getPositionPath(String positionGroupId);

    String getOrganizationPath(String organizationGroupId, UUID hierarchyId);

    Long recognitionsCount(int wallType, UUID personGroupId, String organizationGroupId, String filter, int languageIndex);

    List<QualityPojo> loadQualities(UUID recognitionId, int languageIndex);

    List<String> validateRecognition(Recognition recognition);

    void validatePoints(Recognition recognition, Long personPoints);

    void validatePoints(Recognition recognition, PersonPoint personPoint);

    UUID getPersonGroupId(String employeeNumber);

    Long allNomineesCount(int year, String organizationGroupId);

    String loadAllNominee(int offset, int maxResults, int languageIndex, int year, String organizationGroupId);

    String getMessage(String key, String language);

    DateFormat getDateTimeFormatter(int languageIndex);

    List<RecognitionCommentPojo> loadCommentsByRecognition(String recognitionId, int offset, int maxResults, int languageIndex, boolean isAutomaticTranslate);

    Long recognitionCommentsCount(String recognitionId);

    void removeRecognition(UUID recognitionId, boolean checkAuthor, boolean softDeletion);

    Long myNomineesCount(int year, String organizationGroupId);

    String loadMyNominees(int offset, int maxResults, int year, int languageIndex, String organizationGroupId);

    AwardProgram getActiveAwardProgram();

    AwardProgram getActiveAwardProgram(String language);

    List<QualityPojo> loadQualities(int languageIndex);

    RecognitionProfileSetting loadProfileSettings();

    String loadBanners(String page);

    FileDescriptor getBanner(UUID id);

    boolean checkAccessNominees(UUID personGroupId);

    void resetActiveQuestions(UUID excludeQuestionId, boolean activateCurrent);

    Map<UUID, Boolean> checkAvailable(GoodsOrder goodsOrder);

    void refreshWarehouse(Goods goods, Long count, PointOperationType pointOperationType);

    boolean checkHeartAward();

    boolean checkHeartAward(UUID personGroupId);

    boolean hasRecognitionLoginLog();

    void createRecognitionLoginLog();

    void removeRecognition(Recognition recognition);

    List<GoodsImage> loadGoodsImages(UUID goodsId);

    RecognitionProfileSetting findProfileSettings(PersonGroupExt personGroupExt);

    Long loadGoodsOrdersCount();

    String loadGoodsOrders(int page, long lastCount, String filter, String voucherUsedFilter);

    void generateQRCode(GoodsOrderDetail goodsOrderDetail);

    String loadInfoByQRCode(String qrCode);

    String useVoucher(String qrCode);

    boolean isContainsVoucher(@Nullable UUID personGroupId);

    void addMedals(@Nullable Recognition recognition); //add medals (badges) if it was comply with conditions

    void addBadges(Recognition recognition);

    void addMedalWithChildMedalQuery(@Nullable PersonGroup personGroup);
}