package kz.uco.tsadv.service;


import kz.uco.tsadv.api.BaseResult;
import kz.uco.tsadv.modules.recognition.pojo.PersonAwardPojo;
import kz.uco.tsadv.modules.recognition.pojo.RecognitionCommentPojo;
import kz.uco.tsadv.modules.recognition.pojo.RecognitionCreatePojo;

public interface RecognitionRestService {
    String NAME = "tsadv_RecognitionRestService";

    /**
     * load person preferences by person employee number
     *
     * @param employeeNumber Person employee number
     */
    String loadPersonPreferences(String employeeNumber);

    /**
     * get all person preference types
     *
     * @param language UI language
     */
    String getPersonPreferenceTypes(String language);

    /**
     * create or update person preference
     *
     * @param preferenceTypeId ID DicPersonPreferenceType
     * @param description      PersonPreference description
     */
    void updatePersonPreference(String preferenceTypeId, String description);

    /**
     * load recognitions
     *
     * @param offset                offset records count
     * @param maxResult             max records count in fetch
     * @param wallType              0 - self, 1 - other profile, -1 - all
     * @param profileEmployeeNumber it's null if load all recognitions else showing profile person employy number
     * @param organizationGroupId   filtering by organization group id
     * @param filter                filter text.Filtering in sender/receiver full name
     * @param language              UI language
     * @param automaticTranslate    automatic translate all recognitions
     */
    String loadRecognitions(int offset, int maxResult, int wallType, String sessionEmployeeNumber, String profileEmployeeNumber, String organizationGroupId, String filter, String language, int automaticTranslate);

    /**
     * get recognitions count
     *
     * @param wallType              0 - self, 1 - other profile, -1 - all
     * @param language              UI language
     * @param filter                filter text. Filtering in sender/receiver full name
     * @param organizationGroupId   parent organization group id. Default '-1'
     * @param profileEmployeeNumber receiver person employee number
     */
    Long recognitionsCount(int wallType, String profileEmployeeNumber, String organizationGroupId, String filter, String language);

    /**
     * recognition comments count
     *
     * @param recognitionId recognition id
     */
    Long recognitionCommentsCount(String recognitionId);

    /**
     * get recognition comments
     *
     * @param recognitionId      recognition id
     * @param offset             offset records count
     * @param maxResults         max records count in fetch
     * @param language           UI language
     * @param automaticTranslate automatic translate comment. 0 - false, 1 - true
     */
    String recognitionComments(String recognitionId, int offset, int maxResults, String language, int automaticTranslate);

    /**
     * get session person team profiles
     *
     * @param offset     offset records count
     * @param maxResults max records count in fetch
     * @param language   UI language
     */
    String loadTeamProfiles(int offset, int maxResults, String language);

    /**
     * get session person team profiles count
     */
    Long teamProfilesCount();

    /**
     * create new recognition
     *
     * @param recognitionCreatePojo recognition create model
     * @param language              UI language
     * @see RecognitionCreatePojo
     */
    String createRecognition(RecognitionCreatePojo recognitionCreatePojo, String language);

    /**
     * get profile info by employee number
     *
     * @param employeeNumber employee number
     * @param language       UI language
     */
    String loadProfile(String employeeNumber, String language);

    /**
     * get top nominees
     *
     * @param year                year
     * @param organizationGroupId parent organization id
     */
    String loadTopNominee(int year, String organizationGroupId);

    /**
     * get all nominees count
     *
     * @param year                year
     * @param organizationGroupId parent organization id
     */
    Long allNomineesCount(int year, String organizationGroupId);

    /**
     * get all nominees
     *
     * @param offset              offset records count
     * @param maxResults          max records count in fetch
     * @param language            UI language
     * @param year                year
     * @param organizationGroupId parent organization id
     */
    String loadAllNominee(int offset, int maxResults, String language, int year, String organizationGroupId);

    /**
     * get my nominees count
     *
     * @param year                year
     * @param organizationGroupId parent organization id
     */
    Long myNomineesCount(int year, String organizationGroupId);

    /**
     * get my nominees
     *
     * @param offset              offset records count
     * @param maxResults          max records count in fetch
     * @param language            UI language
     * @param year                year
     * @param organizationGroupId parent organization id
     */
    String loadMyNominees(int offset, int maxResults, int year, String language, String organizationGroupId);

    /**
     * create / update person award (nominee)
     *
     * @param personAwardPojo person award model.
     * @param language        UI language
     * @see PersonAwardPojo
     */
    String createPersonAward(PersonAwardPojo personAwardPojo, String language);

    /**
     * get previous saved draft
     *
     * @param authorEmployeeNumber   author employee number
     * @param receiverEmployeeNumber receiver employee number
     */
    String getDraftPersonAward(String authorEmployeeNumber, String receiverEmployeeNumber);

    String loadQualities(String language);

    String loadOrganizations(String language);

    void uploadPersonImage(String imageContent);

    String loadTopSender();

    BaseResult changePersonImage(String employeeNumber, String imageContent);

    String loadTopSender(String language);

    String loadTopSender(Integer countOfMonths);

    String loadTopSender(String language, Integer countOfMonths);

    String loadTopAwarded();

    String loadTopAwarded(String language);

    String loadTopAwarded(Integer countOfMonths);

    String loadTopAwarded(String language, Integer countOfMonths);

    String loadRecognitionTypes();

    boolean hasActiveQuestion();

    String loadActiveQuestion();

    String loadAllRcgFaq(String language);

    String loadRcgFaq(String faqId, String language);

    String sendComment(RecognitionCommentPojo commentPojo);

    String sendLike(String recognitionId);

    String sendLike(String employeeNumber, String recognitionId);

    String sendQuestionAnswer(String questionId, String answerId);

    String loadInfoByQRCode(String qrCode);

    String useVoucher(String qrCode);
}