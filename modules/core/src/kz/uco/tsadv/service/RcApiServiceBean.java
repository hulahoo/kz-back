package kz.uco.tsadv.service;

import com.google.gson.Gson;
import com.haulmont.addon.restapi.rest.ServerTokenStore;
import com.haulmont.bali.util.ParamsMap;
//import com.haulmont.bpm.entity.ProcTask;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.app.FileStorageAPI;
import com.haulmont.cuba.core.app.serialization.EntitySerializationAPI;
import com.haulmont.cuba.core.app.serialization.EntitySerializationOption;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.Role;
import com.haulmont.cuba.security.entity.UserRole;
import kz.uco.base.common.StaticVariable;
import kz.uco.base.entity.dictionary.DicCity;
import kz.uco.base.entity.dictionary.DicCountry;
import kz.uco.base.entity.dictionary.DicSex;
import kz.uco.base.entity.shared.PersonGroup;
import kz.uco.base.service.NotificationService;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.api.*;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.global.common.EnableDraftStatusInterview;
import kz.uco.tsadv.global.dictionary.DicNationality;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.learning.dictionary.DicEducationDegree;
import kz.uco.tsadv.modules.learning.dictionary.DicEducationLevel;
import kz.uco.tsadv.modules.personal.dictionary.*;
import kz.uco.tsadv.modules.personal.group.CompetenceGroup;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.tsadv.modules.recruitment.config.RecruitmentConfig;
import kz.uco.tsadv.modules.recruitment.dictionary.DicInterviewReason;
import kz.uco.tsadv.modules.recruitment.dictionary.DicJobRequestReason;
import kz.uco.tsadv.modules.recruitment.dictionary.DicSource;
import kz.uco.tsadv.modules.recruitment.enums.InterviewStatus;
import kz.uco.tsadv.modules.recruitment.enums.JobRequestStatus;
import kz.uco.tsadv.modules.recruitment.enums.OfferStatus;
import kz.uco.tsadv.modules.recruitment.enums.RcAnswerType;
import kz.uco.tsadv.modules.recruitment.model.*;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Service(RcApiService.NAME)
public class RcApiServiceBean implements RcApiService {
    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateFormat mf = new SimpleDateFormat("yyyy-MM-01");
    private static final Logger log = LoggerFactory.getLogger(RcApiServiceBean.class);

    protected Gson gson = new Gson();

    @Inject
    protected UcoCommonService ucoCommonService;

    @Inject
    protected Persistence persistence;

    /*@Inject
    protected EntityImportExportService entityImportExportService;*/

    @Inject
    protected CommonService commonService;

    @Inject
    protected EmployeeService employeeService;

    @Inject
    protected ServerTokenStore serverTokenStore;

    @Inject
    protected Metadata metadata;

    @Inject
    protected DataManager dataManager;

    @Inject
    protected PasswordEncryption passwordEncryption;

    @Inject
    protected EntitySerializationAPI entitySerialization;

    @Inject
    protected UserSessionSource userSessionSource;

    @Inject
    protected Messages messages;

    @Inject
    protected NotificationService notificationService;


    @Inject
    protected FileStorageAPI fileStorageAPI;

    protected int languageIndex = 0;

    @Inject
    protected EnableDraftStatusInterview enableDraftStatusInterview;

    /**
     * Возвращает описание Структуры данных интеграционных сущностей в формате JSON
     */
    @Override
    public String getAllObjectModel() {
        List<Entity> entityList = new ArrayList<>();
        entityList.add(new DictionaryInt());
        entityList.add(new RequisitionInt());
        entityList.add(new SimpleInt());

        QuestionnaireInt questionnaireInt = new QuestionnaireInt();
        List<QuestionInt> questionIntList = new ArrayList<>();
        QuestionInt questionInt = new QuestionInt();
        List<AnswerInt> answerIntList = new ArrayList<>();
        AnswerInt answerInt = new AnswerInt();
        answerIntList.add(answerInt);
        questionInt.setAnswers(answerIntList);
        questionIntList.add(questionInt);
        questionnaireInt.setQuestions(questionIntList);
        entityList.add(questionnaireInt);

        PersonInt personInt = new PersonInt();
        List<PersonEducationInt> personEducationIntList = new ArrayList<>();
        PersonEducationInt personEducationInt = new PersonEducationInt();
        personEducationIntList.add(personEducationInt);
        personInt.setEducation(personEducationIntList);
        List<PersonContactInt> personContactIntList = new ArrayList<>();
        PersonContactInt personContactInt = new PersonContactInt();
        personContactIntList.add(personContactInt);
        personInt.setContacts(personContactIntList);
        List<PersonExperienceInt> personExperienceIntList = new ArrayList<>();
        PersonExperienceInt personExperienceInt = new PersonExperienceInt();
        personExperienceIntList.add(personExperienceInt);
        personInt.setExperience(personExperienceIntList);
        List<PersonAttachmentInt> personAttachmentIntList = new ArrayList<>();
        PersonAttachmentInt personAttachmentInt = new PersonAttachmentInt();
        personAttachmentIntList.add(personAttachmentInt);
        personInt.setAttachments(personAttachmentIntList);
        List<PersonCompetenceInt> personCompetenceIntList = new ArrayList<>();
        PersonCompetenceInt personCompetenceInt = new PersonCompetenceInt();
        personCompetenceIntList.add(personCompetenceInt);
        personInt.setCompetences(personCompetenceIntList);
        entityList.add(personInt);

        ScheduledInterviewInt scheduledInterviewInt = new ScheduledInterviewInt();
        entityList.add(scheduledInterviewInt);

        JobRequestInt jobRequestInt = new JobRequestInt();
        entityList.add(jobRequestInt);

        InterviewInt interviewInt = new InterviewInt();
        entityList.add(interviewInt);

        UserInt userInt = new UserInt();
        userInt.setPerson(personInt);
        entityList.add(userInt);

        OfferInt offerInt = new OfferInt();
        entityList.add(offerInt);

        PersonAddressInt addressInt = new PersonAddressInt();
        entityList.add(addressInt);

        CandidateInt candidateInt = new CandidateInt();
        entityList.add(candidateInt);
        return entitySerialization.toJson(entityList, null, EntitySerializationOption.SERIALIZE_NULLS);
    }

    /**
     * Возвращает список стран
     *
     * @param languageCode Код языка RU/KZ/EN для извлечения названий
     * @return Список сущностей Страна в формате JSON <br>
     * - code (string) Код <br>
     * - name (string) Наименование <br>
     * - id   (uuid) Id сущности <br>
     */
    @Override
    public String getCountries(String languageCode) {
        Map<Integer, Object> queryParams = new HashMap<>();
        queryParams.put(1, languageCode);
        String query = "with params as (select upper(?1) as lang)" +
                "   select co.code," +
                "     case par.lang when 'RU' then co.lang_value1  when 'KZ' then co.lang_value2 when 'EN' then co.lang_value3 end as name," +
                "     co.id " +
                "     from base_dic_country co cross join params par" +
                "    where exists(select 1 " +
                "                   from tsadv_requisition r " +
                "                   join base_dic_city c on (c.id = r. location_id)" +
                "                  where r.location_id = c.id " +
                "                    and r.delete_ts is null " +
                "                    and r.requisition_status = 1 " +
                "                    and r.requisition_type = 1" +
                "                    and c.country_id = co.id)";

        return getDictionaryList(query, queryParams);
    }

    /**
     * Возвращает список городов для заданных стран
     *
     * @param countriesIds Id стран разделённых запятыми
     * @param languageCode Код языка RU/KZ/EN для извлечения названий
     * @return Список сущностей Город в формате JSON  <br>
     * - code (string) Код <br>
     * - name (string) Наименование <br>
     * - id   (uuid) Id сущности <br>
     */
    @Override
    public String getCities(String countriesIds, String languageCode) {
        Map<Integer, Object> queryParams = new HashMap<>();
        queryParams.put(1, languageCode);
        queryParams.put(2, countriesIds);

        String query = "with params as (select upper(?1) as lang)" +
                "   select c.code," +
                "     case par.lang when 'RU' then c.settlement_lang1  when 'KZ' then c.settlement_lang2 when 'EN' then c.settlement_lang3 end as name," +
                "     c.id " +
                "     from base_dic_city c cross join params par" +
                "    where exists(select 1 " +
                "                   from tsadv_requisition r " +
                "                  where r.location_id = c.id " +
                "                    and r.delete_ts is null " +
                "                    and r.requisition_status = 1 " +
                "                    and r.requisition_type = 1)" +
                "   and (?2 is null or ?2 = '' or c.country_id || '' in (select regexp_split_to_table(?2, ',')))";

        return getDictionaryList(query, queryParams);
    }

    /**
     * Возвращает список Категорий Вакансий
     *
     * @param languageCode Код языка RU/KZ/EN для извлечения названий
     * @return Список сущностей Категория должности в формате JSON  <br>
     * - code (string) Код <br>
     * - name (string) Наименование <br>
     * - id   (uuid) Id сущности <br>
     */
    @Override
    public String getRequisitionCategories(String languageCode) {
        Map<Integer, Object> queryParams = new HashMap<>();
        queryParams.put(1, languageCode);

        String query = "with params as (select upper(?1) as lang)" +
                "   select jc.code," +
                "     case par.lang when 'RU' then jc.lang_value1  when 'KZ' then jc.lang_value2 when 'EN' then jc.lang_value3 end as name," +
                "     jc.id " +
                "     from tsadv_dic_job_category jc cross join params par" +
                " where jc.delete_ts is null ";

        return getDictionaryList(query, queryParams);
    }

    /**
     * Возвращает Вакансии соответствующие заданным параметрам
     *
     * @param requisitionCodePart  Код заявки (а точнее часть кода)
     * @param positionNamePart     Часть наименования штатной единицы
     * @param countryIds           Список Id стран через запятую
     * @param cityIds              Список Id городов через запятую
     * @param jobCategoryIds       Список Id Категорий должности через запятую
     * @param requisitionStartDate Дата начала Вакансии в формате гггг-мм-дд
     * @param languageCode         RU/KZ/EN для извлечения названий
     * @return Список сущностей Вакансия в формате JSON
     */
    @Override
    public String getRequisitions(
            String requisitionCodePart,
            String positionNamePart,
            String countryIds,
            String cityIds,
            String jobCategoryIds,
            String requisitionStartDate,
            String languageCode
    ) {
        return getRequisitionsDefault(
                requisitionCodePart, positionNamePart, countryIds, cityIds, jobCategoryIds, requisitionStartDate, languageCode);
    }

    @Override
    public String getRequisitionsByPerson(String code, String job, String country, String city, String category, String startDate, String lang) {
        return getRequisitionsDefaultByPerson(code, job, country, city, category, startDate, lang);
    }

    public String getRequisitionsDefault(String code, String job, String country, String city, String category, String startDate, String lang) {
        List<RequisitionInt> entityList = new ArrayList<>();
        Map<Integer, Object> queryParams = new HashMap<>();
        queryParams.put(1, lang);
        queryParams.put(2, code);
        queryParams.put(3, job);
        queryParams.put(4, country);
        queryParams.put(5, city);
        queryParams.put(6, category);
        queryParams.put(7, startDate);

        languageIndex = languageIndex(lang);

        String query = isEmployee(null);

        List<Object[]> resultList = commonService.emNativeQueryResultList(query, queryParams);

        if (resultList != null)
            for (Object[] row : resultList) {
                int i = 0;
                RequisitionInt ri = new RequisitionInt();
                ri.setCode((String) row[i++]);
                ri.setJob((String) row[i++]);
                ri.setPosCount(((Double) row[i++]).intValue());
                ri.setCity((UUID) row[i++]);
                ri.setCityName((String) row[i++]);

                ri.setLatitude((Double) row[i++]);
                ri.setLongitude((Double) row[i++]);

                ri.setCountry((UUID) row[i++]);
                ri.setCountryName((String) row[i++]);
                ri.setCategory((UUID) row[i++]);
                ri.setCategoryName((String) row[i++]);
                ri.setStartDate((String) row[i++]);
                ri.setEndDate((String) row[i++]);
                ri.setFinalDate((String) row[i++]);
                ri.setVideoInterviewRequired((Boolean) row[i++]);
                ri.setId((UUID) row[i]);

                ri.setRequiredTest(hasPreScreenTest(ri.getId()));
                entityList.add(ri);
            }

        return toJsonList(entityList);
    }


    public String getRequisitionsDefaultByPerson(String code, String job, String country, String city, String category, String startDate, String lang) {
        List<RequisitionInt> entityList = new ArrayList<>();
        Map<Integer, Object> queryParams = new HashMap<>();
        queryParams.put(1, lang);
        queryParams.put(2, code);
        queryParams.put(3, job);
        queryParams.put(4, country);
        queryParams.put(5, city);
        queryParams.put(6, category);
        queryParams.put(7, startDate);

        languageIndex = languageIndex(lang);

        PersonGroupExt personGroupExt = getUserPersonGroup();


        Map<Integer, Object> map = new HashMap<>();
        map.put(1, personGroupExt != null ? personGroupExt.getId() : "");
        String queryForEmployee = "SELECT perType.code " +
                "   FROM base_person p " +
                "   join tsadv_dic_person_type perType on p.type_id = perType.id " +
                "   WHERE p.group_id = (?1) " +
                "   and perType.code = 'EMPLOYEE' " +
                "   and p.delete_ts is NULL " +
                "   and perType.delete_ts is NULL " +
                "   and current_timestamp BETWEEN p.start_date and p.end_date ";
        List<Object[]> list = commonService.emNativeQueryResultList(queryForEmployee, map);

        String query = isEmployee(list);

        List<Object[]> resultList = commonService.emNativeQueryResultList(query, queryParams);

        if (resultList != null)
            for (Object[] row : resultList) {
                int i = 0;
                RequisitionInt ri = new RequisitionInt();
                ri.setCode((String) row[i++]);
                ri.setJob((String) row[i++]);
                ri.setPosCount(((Double) row[i++]).intValue());
                ri.setCity((UUID) row[i++]);
                ri.setCityName((String) row[i++]);

                ri.setLatitude((Double) row[i++]);
                ri.setLongitude((Double) row[i++]);

                ri.setCountry((UUID) row[i++]);
                ri.setCountryName((String) row[i++]);
                ri.setCategory((UUID) row[i++]);
                ri.setCategoryName((String) row[i++]);
                ri.setStartDate((String) row[i++]);
                ri.setEndDate((String) row[i++]);
                ri.setFinalDate((String) row[i++]);
                ri.setVideoInterviewRequired((Boolean) row[i++]);
                ri.setId((UUID) row[i]);

                ri.setRequiredTest(hasPreScreenTest(ri.getId()));
                entityList.add(ri);
            }

        return toJsonList(entityList);
    }


    public String isEmployee(List list) {
        String query = "";
        if (list != null && !list.isEmpty()) {
            query = "with params as (select upper(?1) as lang) " +
                    "   select r.code, " +
                    "   (case par.lang when 'RU' then coalesce(r.name_for_site_lang1, p.position_name_lang1, j.job_name_lang1) " +         //columnChanged TODO
                    "   when 'KZ' then coalesce(r.name_for_site_lang2, p.position_name_lang2, j.job_name_lang2) " +                        //columnChanged TODO
                    "   when 'EN' then coalesce (r.name_for_site_lang3, p.position_name_lang3, " + " j.job_name_lang3) end) as job, " +     //columnChanged TODO
                    "   r.opened_positions_count as pos_count, " +
                    "   c.id as city, " +
                    "   case par.lang when 'RU' then c.settlement_lang1 when 'KZ' then c.settlement_lang2 when 'EN' then c.settlement_lang3 end as city_name, " +
                    "   c.latitude, " +
                    "   c.longitude, " +
                    "   co.id as country, " +
                    "   case par.lang when 'RU' then co.lang_value1 when 'KZ' then co.lang_value2 when 'EN' then co.lang_value3 end as country_name, " +
                    "   jc.id as category, " +
                    "   case par.lang when 'RU' then jc.lang_value1 when 'KZ' then jc.lang_value2 when 'EN' then jc.lang_value3 end as category_name, " +
                    "   to_char(r.start_date, 'yyyy-MM-dd') as start_date, " +
                    "   to_char(r.end_date, 'yyyy-MM-dd') as end_date, " +
                    "   to_char(r.final_collect_date, 'yyyy-MM-dd') as final_date, " +
                    "   r.video_interview_required, " +
                    "   r.id  " +
                    "   from tsadv_requisition r " +
                    "   cross join params par " +
                    "   left join BASE_POSITION p on (p.group_id = r.position_group_id and CURRENT_DATE between p.start_date and p.end_date) " +
                    "   left join tsadv_job j on (j.group_id = r.job_group_id and CURRENT_DATE between j.start_date and j.end_date) " +
                    "   left join base_dic_city c on (c.id = r.location_id) " +
                    "   left join base_dic_country co on (co.id = c.country_id) " +
                    "   left join tsadv_dic_job_category jc on (jc.id = j.job_category_id) " +
                    "   where r.delete_ts is null  " +
                    "   and r.requisition_status = 1 " +
                    "   and r.requisition_type = 1 " +
                    "   and r.FINAL_COLLECT_DATE >= current_date " +
                    "   and (?2 is null or ?2 = '' or lower(r.code) like '%' || lower(?2) ||'%') " +
                    "   and (?3 is null or ?3 = '' or p.id is not null and lower(p.position_name_lang" + languageIndex + ") like '%' || lower(?3) || '%' or p.id is null and lower(j.job_name_lang" + languageIndex + ") like '%' || lower(?3) || '%') " +   //columnChanged TODO
                    "   and (?4 is null or ?4 = '' or co.id || '' in (select regexp_split_to_table(?4, ','))) " +
                    "   and (?5 is null or ?5 = '' or c.id || '' in (select regexp_split_to_table(?5, ','))) " +
                    "   and (?6 is null or ?6 = '' or jc.id || '' in (select regexp_split_to_table(?6, ','))) " +
                    "   and (?7 is null or ?7 = '' or r.start_date >= to_date(?7, 'yyyy-MM-dd'))" +
                    "   order by r.start_date DESC ";

        } else {
            query = "with params as (select upper(?1) as lang) " +
                    "   select r.code, " +
                    "   (case par.lang when 'RU' then coalesce(r.name_for_site_lang1, p.position_name_lang1, j.job_name_lang1) " +         //columnChanged TODO
                    "   when 'KZ' then coalesce(r.name_for_site_lang2, p.position_name_lang2, j.job_name_lang2) " +                        //columnChanged TODO
                    "   when 'EN' then coalesce (r.name_for_site_lang3, p.position_name_lang3, " + " j.job_name_lang3) end) as job, " +     //columnChanged TODO
                    "   r.opened_positions_count as pos_count, " +
                    "   c.id as city, " +
                    "   case par.lang when 'RU' then c.settlement_lang1 when 'KZ' then c.settlement_lang2 when 'EN' then c.settlement_lang3 end as city_name, " +
                    "   c.latitude, " +
                    "   c.longitude, " +
                    "   co.id as country, " +
                    "   case par.lang when 'RU' then co.lang_value1 when 'KZ' then co.lang_value2 when 'EN' then co.lang_value3 end as country_name, " +
                    "   jc.id as category, " +
                    "   case par.lang when 'RU' then jc.lang_value1 when 'KZ' then jc.lang_value2 when 'EN' then jc.lang_value3 end as category_name, " +
                    "   to_char(r.start_date, 'yyyy-MM-dd') as start_date, " +
                    "   to_char(r.end_date, 'yyyy-MM-dd') as end_date, " +
                    "   to_char(r.final_collect_date, 'yyyy-MM-dd') as final_date, " +
                    "   r.video_interview_required, " +
                    "   r.id  " +
                    "   from tsadv_requisition r " +
                    "   cross join params par " +
                    "   left join BASE_POSITION p on (p.group_id = r.position_group_id and CURRENT_DATE between p.start_date and p.end_date) " +
                    "   left join tsadv_job j on (j.group_id = r.job_group_id and CURRENT_DATE between j.start_date and j.end_date) " +
                    "   left join base_dic_city c on (c.id = r.location_id) " +
                    "   left join base_dic_country co on (co.id = c.country_id) " +
                    "   left join tsadv_dic_job_category jc on (jc.id = j.job_category_id) " +
                    "   LEFT JOIN tsadv_requisition_posting_channel posCh on (posCh.requisition_id = r.id)" +
                    "   LEFT JOIN tsadv_dic_posting_channel dicCh on (dicCh.id = posCh.posting_channel_id)" +
                    "   where r.delete_ts is null  " +
                    "   and r.requisition_status = 1 " +
                    "   and r.requisition_type = 1 " +
                    "   and r.FINAL_COLLECT_DATE >= current_date " +
                    "   and (?2 is null or ?2 = '' or lower(r.code) like '%' || lower(?2) ||'%') " +
                    "   and (?3 is null or ?3 = '' or p.id is not null and lower(p.position_name_lang" + languageIndex + ") like '%' || lower(?3) || '%' or p.id is null and lower(j.job_name_lang" + languageIndex + ") like '%' || lower(?3) || '%') " +   //columnChanged TODO
                    "   and (?4 is null or ?4 = '' or co.id || '' in (select regexp_split_to_table(?4, ','))) " +
                    "   and (?5 is null or ?5 = '' or c.id || '' in (select regexp_split_to_table(?5, ','))) " +
                    "   and (?6 is null or ?6 = '' or jc.id || '' in (select regexp_split_to_table(?6, ','))) " +
                    "   and (?7 is null or ?7 = '' or r.start_date >= to_date(?7, 'yyyy-MM-dd'))" +
                    "   and dicCh.code = 'EXTERNAL' " +
                    "   and dicCh.delete_ts is null " +
                    "   and posCh.delete_ts is null" +
                    "   order by r.start_date DESC ";
        }
        return query;
    }

    /**
     * Возвращает Вакансию
     * <br><p>
     * Метод: GET <br>
     * Авторизация: Не требуется <br>
     * Передача параметров: В строке запроса (QUERY) <br>
     *
     * @param requisitionId Id вакансии
     * @param languageCode  Код языка RU/KZ/EN для извлечения названий
     * @return Возвращает сущность Вакансия в формате JSON <br>
     * - code (string) Код вакансии <br>
     * - requiredTest (boolean) Требуется ли Прескрининг тест <br>
     * - videoInterviewRequired(boolean)Требуется ли интервью по Skype <br>
     * - job (string) Должность <br>
     * - posCount (integer)  <br>
     * - cityName (string) Название города <br>
     * - city (uuid) Id города <br>
     * - categoryName (string) <br>
     * - category (uuid) <br>
     * - startDate (string) Дата начала вакансии <br>
     * - endDate (string) Дата окончания вакансии <br>
     * - finalDate (string <br>
     * - description (string) Описание вакансии <br>
     * - country (uuid) Id страны <br>
     * - countryName (string) Наименование страны <br>
     * - latitude (double) Широта <br>
     * - longitude (double) Долгота <br>
     */
    @Override
    public String getRequisition(UUID requisitionId, String languageCode) {
        return getRequisitionDefault(requisitionId, languageCode, null, null);
    }

    public String getRequisitionDefault(UUID requisitionId, String lang, String join, String where) {
        List<RequisitionInt> entityList = new ArrayList<>();
        Map<Integer, Object> queryParams = new HashMap<>();
        queryParams.put(1, lang);
        queryParams.put(2, requisitionId);

        String query = String.format("with params as (select upper(?1) as lang) " +
                        "                select r.code,  " +
                        "                         (case par.lang when 'RU' then coalesce(r.name_for_site_lang1, p.position_name_lang1, j.job_name_lang1) " +      //columnChanged TODO
                        "                         when 'KZ' then coalesce(r.name_for_site_lang2, p.position_name_lang2,  j.job_name_lang2) " +                    //columnChanged TODO
                        "                         when 'EN' then coalesce (r.name_for_site_lang3, p.position_name_lang3,  j.job_name_lang3) end) as job, " +      //columnChanged TODO
                        "                         r.opened_positions_count as pos_count, " +
                        "                         c.id as city, " +
                        "                         case par.lang when 'RU' then c.settlement_lang1 when 'KZ' then c.settlement_lang2 when 'EN' then c.settlement_lang3 end as city_name, " +
                        "                         c.latitude, " +
                        "                         c.longitude, " +
                        "                         co.id as country, " +
                        "                         case par.lang when 'RU' then co.lang_value1 when 'KZ' then co.lang_value2 when 'EN' then co.lang_value3 end as country_name, " +
                        "                         jc.id as category, " +
                        "                         case par.lang when 'RU' then jc.lang_value1 when 'KZ' then jc.lang_value2 when 'EN' then jc.lang_value3 end as category_name, " +
                        "                         to_char(r.start_date, 'yyyy-MM-dd') as start_date, " +
                        "                         to_char(r.end_date, 'yyyy-MM-dd') as end_date, " +
                        "                         to_char(r.final_collect_date, 'yyyy-MM-dd') as final_date, " +
                        "                         case par.lang when 'RU' then r.description_lang1 when 'KZ' then r.description_lang2 when 'EN' then r.description_lang3 end as description, " +
                        "                         r.video_interview_required, " +
                        "                         r.id  " +
                        "                           from tsadv_requisition r " +
                        "                             cross join params par " +
                        "                             left join BASE_POSITION p on (p.group_id = r.position_group_id and CURRENT_DATE between p.start_date and p.end_date) " +
                        "                             left join tsadv_job j on (j.group_id = r.job_group_id and CURRENT_DATE between j.start_date and j.end_date) " +
                        "                             left join base_dic_city c on (c.id = r.location_id) " +
                        "                             left join base_dic_country co on (co.id = c.country_id) " +
                        "                             left join tsadv_dic_job_category jc on (jc.id = j.job_category_id) " +
                        "%s " +
                        "                             where r.delete_ts is null " +
                        "                             and r.requisition_status = 1 " +
                        "                             and r.requisition_type = 1 " +
                        "                             and r.FINAL_COLLECT_DATE >= current_date " +
                        "                             and r.id = ?2 " +
                        "%s",
                StringUtils.isNotBlank(join) ? join : "",
                StringUtils.isNotBlank(where) ? where : "");
        List<Object[]> resultList = commonService.emNativeQueryResultList(query, queryParams);

        if (resultList != null) {
            for (Object[] row : resultList) {
                int i = 0;
                RequisitionInt ri = metadata.create(RequisitionInt.class);
                ri.setCode((String) row[i++]);
                ri.setJob((String) row[i++]);
                ri.setPosCount(((Double) row[i++]).intValue());
                ri.setCity((UUID) row[i++]);
                ri.setCityName((String) row[i++]);

                ri.setLatitude((Double) row[i++]);
                ri.setLongitude((Double) row[i++]);

                ri.setCountry((UUID) row[i++]);
                ri.setCountryName((String) row[i++]);
                ri.setCategory((UUID) row[i++]);
                ri.setCategoryName((String) row[i++]);
                ri.setStartDate((String) row[i++]);
                ri.setStartDate((String) row[i++]);
                ri.setEndDate((String) row[i++]);
                ri.setDescription((String) row[i++]);
                ri.setVideoInterviewRequired((Boolean) row[i++]);
                ri.setId((UUID) row[i]);
                ri.setRequiredTest(hasPreScreenTest(ri.getId()));
                entityList.add(ri);
            }

            try (Transaction tx = persistence.getTransaction()) {
                Requisition entity = persistence.getEntityManager().find(Requisition.class, requisitionId, View.LOCAL);
                if (entity != null) {
                    entity.setViewCount(entity.getViewCount() + 1);
                }
                tx.commit();
            }
        }

        return jsonListToSingleObject(toJsonList(entityList));
    }

    /**
     * Возвращает Имеет ли заданная вакансия отклики текущим пользователем
     * <br><p>
     * Метод: POST <br>
     * Авторизация: OAuth-токен должен быть передан в заголовке запроса Authorization с типом Bearer. <br>
     * Передача параметров: В теле запроса в формате JSON (BODY) <br>
     *
     * @param requisitionId Id Вакансии
     * @return Сущность Результат в формате JSON <br>
     * - result (string) Результат true - Да / false - Нет <br>
     * - errorMessage (string) Текст ошибки <br>
     */
    @Override
    public String hasJobRequestByUser(UUID requisitionId) {
        List<SimpleInt> entityList = new ArrayList<>();

        SimpleInt simpleInt = new SimpleInt();
        simpleInt.setResult(Boolean.FALSE.toString());
        entityList.add(simpleInt);

        PersonGroupExt personGroup = getUserPersonGroup();
        if (personGroup != null) {
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("rId", requisitionId);
            queryParams.put("pgId", personGroup.getId());

            Long count = commonService.getCount(JobRequest.class,
                    "select e from tsadv$JobRequest e " +
                            "join e.requisition r " +
                            "where r.id = :rId " +
                            "and e.candidatePersonGroup.id = :pgId",
                    queryParams);

            Boolean result = count != null && count > 0;
            simpleInt.setResult(result.toString());
        }

        return jsonListToSingleObject(toJsonList(entityList));
    }

    /**
     * Проверяет профиль кандидата на полноту для заданного Id пользователя
     * <br><p>
     * Метод: GET <br>
     * Авторизация: Не требуется <br>
     * Передача параметров: В строке запроса (QUERY) <br>
     *
     * @param user Id пользователя системы
     * @return Сущность Результат в формате JSON <br>
     * - result (string) Результат true - профиль кандидата полный / false - профиль кандидата не полный <br>
     * - errorMessage (string) Текст ошибки, один из: <br>
     * &nbsp;&nbsp; PersonExperience is empty! - Отсутствует информация об опыте работы <br>
     * &nbsp;&nbsp; PersonEducation is empty! - Отсутствует информация об образовании <br>
     * &nbsp;&nbsp; PersonContact is empty! - Отсутствует информация о контактных лицах кандидата <br>
     * &nbsp;&nbsp; PersonGroup is empty! - Кандитат не определён (не найден) <br>
     */
    @Override
    public String checkFullProfile(UUID user) {
        log.debug("checkFullProfile: USER_ID: {}", user);

        List<SimpleInt> entityList = new ArrayList<>();

        SimpleInt simpleInt = new SimpleInt();
        simpleInt.setResult(Boolean.FALSE.toString());
        entityList.add(simpleInt);

        PersonGroupExt personGroup = getUserPersonGroup(user);
        if (personGroup != null) {

            log.debug("personGroup: {}", personGroup.getId());

            Map<Integer, Object> params = new HashMap<>();
            params.put(1, personGroup.getId());
            params.put(2, "mobile");
            params.put(3, "email");

            String query = "SELECT " +
                    "count(*) " +
                    "FROM BASE_PERSON_GROUP pg " +
                    "JOIN tsadv_person_contact pc " +
                    "ON pc.person_group_id = pg.id " +
                    "JOIN tsadv_dic_phone_type pt " +
                    "ON pc.type_id = pt.id " +
                    "JOIN tsadv_person_contact pc2 " +
                    "ON pc2.person_group_id = pg.id " +
                    "JOIN tsadv_dic_phone_type pt2 " +
                    "ON pc2.type_id = pt2.id " +
                    "WHERE pg.id = ?1 " +
                    "AND pt.code = ?2 AND pt2.code = ?3 " +
                    "AND pc.delete_ts IS NULL AND pc2.delete_ts IS NULL";

            Long contactsCount = commonService.getCount(query, params);

            log.debug("contactsCount:{}", contactsCount);

            if (contactsCount > 0) {
                Long educationCount = commonService.getCount(PersonEducation.class,
                        "select e from tsadv$PersonEducation e " +
                                "where e.personGroup.id = :pgId",
                        Collections.singletonMap("pgId", personGroup.getId()));

                log.debug("educationCount:{}", educationCount);
                if (educationCount > 0) {
                    Long experienceCount = commonService.getCount(PersonExperience.class,
                            "select e from tsadv$PersonExperience e " +
                                    "where e.personGroup.id = :pgId",
                            Collections.singletonMap("pgId", personGroup.getId()));

                    log.debug("experienceCount:{}", experienceCount);

                    if (experienceCount > 0) {
                        simpleInt.setResult(Boolean.TRUE.toString());
                    } else {
                        simpleInt.setErrorMessage("PersonExperience is empty!");
                    }
                } else {
                    simpleInt.setErrorMessage("PersonEducation is empty!");
                }
            } else {
                simpleInt.setErrorMessage("PersonContact is empty!");
            }
        } else {
            simpleInt.setErrorMessage("PersonGroup is empty!");
        }
        return jsonListToSingleObject(toJsonList(entityList));
    }

    /**
     * Возвращает Прескрининг Опросник (Тест) для заданной вакансии
     * <br><p>
     * Метод: POST <br>
     * Авторизация: OAuth-токен должен быть передан в заголовке запроса Authorization с типом Bearer. <br>
     * Передача параметров: В теле запроса в формате JSON (BODY) <br>
     *
     * @param requisition  Id вакансии
     * @param languageCode Код языка RU/KZ/EN для извлечения названий
     * @return Сущность Опросник в формате JSON <br>
     * - name (string) Название опросника
     * - questions [question] Список вопросов
     */
    @Override
    public String getPreScreenTest(UUID requisition, String languageCode) {
        UUID questionnaireId = getPreScreenQuestionnaire(requisition);

        List<QuestionnaireInt> entityList = new ArrayList<>();

        if (questionnaireId != null) {
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("qId", questionnaireId);

            RcQuestionnaire rcQuestionnaire = commonService.getEntity(
                    RcQuestionnaire.class,
                    "select e from tsadv$RcQuestionnaire e where e.id = :qId",
                    queryParams,
                    "rcQuestionnaire.rest");

            if (rcQuestionnaire != null) {
                QuestionnaireInt questionnaire = new QuestionnaireInt();
                questionnaire.setId(questionnaireId);
                questionnaire.setName(rcQuestionnaire.getName());
                if (languageCode != null) {
                    switch (languageCode.toUpperCase()) {
                        case "KZ": {
                            questionnaire.setName(rcQuestionnaire.getName2());
                            break;
                        }
                        case "EN": {
                            questionnaire.setName(rcQuestionnaire.getName3());
                            break;
                        }
                    }
                }
                questionnaire.setInstruction(rcQuestionnaire.getInstruction());

                List<QuestionInt> questionsInts = new ArrayList<>();
                questionnaire.setQuestions(questionsInts);

                entityList.add(questionnaire);

                List<RcQuestionnaireQuestion> rcQuestions = rcQuestionnaire.getQuestions();

                if (rcQuestions != null) {
                    for (RcQuestionnaireQuestion rcQuestion : rcQuestions) {
                        RcQuestion question = rcQuestion.getQuestion();

                        if (question != null) {

                            QuestionInt questionInt = new QuestionInt();
                            questionInt.setId(question.getId());
                            String questionText = question.getQuestionText1();
                            if (languageCode != null) {
                                switch (languageCode.toUpperCase()) {
                                    case "KZ": {
                                        questionText = question.getQuestionText2();
                                        break;
                                    }
                                    case "EN": {
                                        questionText = question.getQuestionText3();
                                        break;
                                    }
                                }
                            }
                            questionInt.setQuestionText(questionText);
                            questionInt.setAnswerType(question.getAnswerType().getId());

                            List<AnswerInt> answerInts = new ArrayList<>();

                            List<RcQuestionnaireAnswer> rcAnswers = rcQuestion.getAnswers();
                            if (rcAnswers != null) {
                                for (RcQuestionnaireAnswer rcAnswer : rcAnswers) {
                                    RcAnswer answer = rcAnswer.getAnswer();

                                    AnswerInt answerInt = new AnswerInt();
                                    answerInt.setId(answer.getId());
                                    String answerText = answer.getAnswerText1();
                                    if (languageCode != null) {
                                        switch (languageCode.toUpperCase()) {
                                            case "KZ": {
                                                answerText = answer.getAnswerText2();
                                                break;
                                            }
                                            case "EN": {
                                                answerText = answer.getAnswerText3();
                                                break;
                                            }
                                        }
                                    }

                                    answerInt.setAnswerText(answerText);
                                    answerInt.setWeight(rcAnswer.getWeight() != null ? rcAnswer.getWeight().toString() : "");
                                    answerInts.add(answerInt);
                                }
                            }

                            questionInt.setAnswers(answerInts);
                            questionsInts.add(questionInt);
                        }
                    }
                }
            }
        }

        return jsonListToSingleObject(toJsonList(entityList));
    }

    protected UUID getPreScreenQuestionnaire(UUID requisitionId) {
        Map<Integer, Object> queryParams = new HashMap<>();
        queryParams.put(1, requisitionId);
        queryParams.put(2, "PRE_SCREEN_TEST");
        queryParams.put(3, "ACTIVE");

        String query = "SELECT q.id " +
                "FROM tsadv_requisition_hiring_step hs " +
                "  JOIN TSADV_HIRING_STEP_QUESTIONNAIRE hsq " +
                "    ON hs.hiring_step_id = hsq.hiring_step_id " +
                "    and hsq.delete_ts is null " +
                "  JOIN TSADV_RC_QUESTIONNAIRE q " +
                "    ON q.id = hsq.questionnaire_id " +
                "    and q.delete_ts is null " +
                "  join TSADV_DIC_RC_QUESTIONNAIRE_STATUS qs " +
                "  on q.status_id = qs.id " +
                "  and qs.delete_ts is null " +
                "  JOIN TSADV_DIC_RC_QUESTIONNAIRE_CATEGORY qc " +
                "    ON q.category_id = qc.id " +
                "    and qc.delete_ts is null " +
                "WHERE hs.requisition_id = ?1 " +
                "AND qc.code = ?2 " +
                "and qs.code = ?3 " +
                "and hs.delete_ts is null";

        return commonService.emNativeQuerySingleResult(UUID.class, query, queryParams);
    }

    protected boolean hasPreScreenTest(UUID requisitionId) {
        Map<Integer, Object> queryParams = new HashMap<>();
        queryParams.put(1, requisitionId);
        queryParams.put(2, "PRE_SCREEN_TEST");
        queryParams.put(3, "ACTIVE");

        String query = "SELECT count(*) " +
                "FROM tsadv_requisition_hiring_step hs " +
                "  JOIN TSADV_HIRING_STEP_QUESTIONNAIRE hsq " +
                "    ON hs.hiring_step_id = hsq.hiring_step_id " +
                "  JOIN TSADV_RC_QUESTIONNAIRE q " +
                "    ON q.id = hsq.questionnaire_id " +
                "  join TSADV_DIC_RC_QUESTIONNAIRE_STATUS qs " +
                "  on q.status_id = qs.id " +
                "  JOIN TSADV_DIC_RC_QUESTIONNAIRE_CATEGORY qc " +
                "    ON q.category_id = qc.id " +
                "WHERE hs.requisition_id = ?1 " +
                "AND qc.code = ?2 " +
                "and qs.code = ?3";

        Long count = commonService.getCount(query, queryParams);
        return count != null && count > 0;
    }

    /**
     * Создаёт в системе кандидата, пользователя, с контактной информацией <br>
     * <br><p>
     * Метод: POST <br>
     * Авторизация: Не требуется  <br>
     * Передача параметров: В теле запроса в формате JSON (BODY) <br>
     *
     * @param user Информация о кандидате в формате JSON <br>
     *             - login (string) Логин <br>
     *             - language (string) Язык интерфейса <br>
     *             - password (string) Пароль <br>
     *             - email (string) Email адрес <br>
     *             - phoneNumber (string) Номер телефона <br>
     *             - person (Person) Персональные данные <br>
     * @return Результат операции в формате JSON <br>
     * - success (boolean) Успешно ли (true / false) <br>
     * - successMessage (String) Сообщение при успехе <br>
     * - errorMessage (String) Сообщение при ошибке <br>
     */
    @Override
    public BaseResult registerUser(UserInt user) {
        Map<String, Object> map = new HashMap<>();
        BaseResult result = new BaseResult();
        //log.info(entityImportExportService.exportEntitiesToJSON(Collections.singletonList(user)));
        ArrayList<String> errorMessages = new ArrayList<>();

        if (user.getLogin() == null || user.getLogin().length() == 0)
            errorMessages.add("Login is required!");
        else {
            List<UserExt> users = commonService.getEntities(UserExt.class,
                    "select e from tsadv$UserExt e where LOWER(e.login) = :login",
                    Collections.singletonMap("login", user.getLogin().toLowerCase()), View.LOCAL);
            if (users != null && !users.isEmpty())
                errorMessages.add("User with this login already exists!");
        }

        if (user.getPassword() == null || user.getPassword().length() == 0)
            errorMessages.add("Password is required!");

        if (user.getEmail() == null || user.getEmail().length() == 0)
            errorMessages.add("Email is required!");
        if (user.getEmail() != null && user.getEmail().length() > 0) {
            try {
                map.put("email", user.getEmail());
                UserExt userExt = commonService.getEntity(UserExt.class,
                        "select e from tsadv$UserExt e where e.email = :email ",
                        map,
                        "user.edit");
                if (userExt != null) {
                    result.setSuccess(false);
                    result.setErrorMessage("Эл. адрес уже существует");
                    return result;
                }
            } catch (Exception e) {
                e.printStackTrace();
                result.setSuccess(false);
                result.setErrorMessage(e.getMessage());
            }
        }
        if (user.getPerson() == null)
            errorMessages.add("Person information is required!");
        else {
            validatePerson(errorMessages, user.getPerson());
        }

        if (!errorMessages.isEmpty()) {
            result.setSuccess(false);
            result.setErrorMessage(errorMessages.stream().reduce((e1, e2) -> e1.concat(" ").concat(e2)).orElse(""));
        } else {
            try {
                UserExt userExt = metadata.create(UserExt.class);
                userExt.setLogin(user.getLogin());
                PersonGroupExt personGroup = metadata.create(PersonGroupExt.class);
                dataManager.commit(personGroup);


                userExt.setLanguage(user.getLanguage());

                String passwordHash = passwordEncryption.getPasswordHash(userExt.getId(), user.getPassword());
                userExt.setPassword(passwordHash);

                userExt.setGroup(commonService.getEntity(Group.class, Collections.singletonMap("name", "Employee/Candidate")));

                UserRole userRole = metadata.create(UserRole.class);
                userRole.setUser(userExt);
                userRole.setRole(commonService.getEntity(Role.class, Collections.singletonMap("name", "External Candidate")));

                userExt.setEmail(user.getEmail());
                userExt.setMobilePhone(user.getPhoneNumber());
                userExt.setFirstName(user.getPerson().getFirstName());
                userExt.setLastName(user.getPerson().getLastName());
                userExt.setMiddleName(user.getPerson().getMiddleName());
                userExt.setPersonGroup(personGroup);

                PersonExt person = metadata.create(PersonExt.class);

                person.setGroup(personGroup);
                person.setStartDate(CommonUtils.getSystemDate());
                person.setEndDate(CommonUtils.getEndOfTime());
                person.setType(commonService.getEntity(DicPersonType.class, "CANDIDATE"));
                person.setFirstName(user.getPerson().getFirstName());
                person.setLastName(user.getPerson().getLastName());
                person.setMiddleName(user.getPerson().getMiddleName());
                person.setDateOfBirth(df.parse(user.getPerson().getBirthDate()));
                person.setNationalIdentifier(user.getPerson().getNationalIdentifier());

                PersonContact contactEmail = metadata.create(PersonContact.class);
                contactEmail.setPersonGroup(personGroup);
                contactEmail.setStartDate(CommonUtils.getSystemDate());
                contactEmail.setEndDate(CommonUtils.getEndOfTime());
                contactEmail.setType(commonService.getEntity(DicPhoneType.class, "email"));
                contactEmail.setContactValue(user.getEmail());
                dataManager.commit(contactEmail);

                PersonContact contactMobile = metadata.create(PersonContact.class);
                contactMobile.setPersonGroup(personGroup);
                contactMobile.setStartDate(CommonUtils.getSystemDate());
                contactMobile.setEndDate(CommonUtils.getEndOfTime());
                contactMobile.setType(commonService.getEntity(DicPhoneType.class, "mobile"));
                contactMobile.setContactValue(user.getPhoneNumber());
                dataManager.commit(contactMobile);

                person.setSex(commonService.getEntity(DicSex.class, user.getPerson().getSex()/* UUID.fromString(user.getPerson().getSex())*/));

                if (user.getPerson().getNationality() != null /*&& user.getPerson().getNationality().length() > 0*/)
                    person.setNationality(commonService.getEntity(DicNationality.class, user.getPerson().getNationality()/*UUID.fromString(user.getPerson().getNationality())*/));
                if (user.getPerson().getCitizenship() != null /*&& user.getPerson().getCitizenship().length() > 0*/)
                    person.setCitizenship(commonService.getEntity(DicCitizenship.class, user.getPerson().getCitizenship()/*UUID.fromString(user.getPerson().getCitizenship())*/));
                if (user.getPerson().getMaritalStatus() != null /*&& user.getPerson().getMaritalStatus().length() > 0*/)
                    person.setMaritalStatus(commonService.getEntity(DicMaritalStatus.class, user.getPerson().getMaritalStatus()/*UUID.fromString(user.getPerson().getMaritalStatus())*/));

                List<Entity> commitInstances = new ArrayList<>();

                commitInstances.add(personGroup);
                commitInstances.add(person);
                commitInstances.add(userExt);
                commitInstances.add(userRole);

                Address address = registerAddress(result, user.getPerson().getCityName(), personGroup);
                if (address != null) {
                    commitInstances.add(address);
                }
                dataManager.commit(new CommitContext(commitInstances));

                result.setSuccess(true);
                result.setErrorMessage(null);
                Map<String, Object> param = new HashMap<>();
                param.put("login", userExt != null ? userExt.getLogin() : null);
                param.put("personFullName", person != null ? person.getFullName() : null);
                notificationService.sendParametrizedNotification("register.user.success", userExt, param);
            } catch (Exception e) {
                e.printStackTrace();
                result.setSuccess(false);
                result.setErrorMessage(e.getMessage());
            }
        }

        return result;
    }

    @Override
    public BaseResult addInterviewAnswer(InterviewQuestionnaireInt interviewQuestionnaireInt) {
        BaseResult baseResult = new BaseResult();
        baseResult.setSuccess(false);

        try {
            //String json = entitySerialization.toJson(interviewQuestionnaireInt);

            List<Entity> commitInstances = new ArrayList<>();

            PersonGroupExt personGroup = getUserPersonGroup();
            if (personGroup != null) {
                Requisition requisition = commonService.getEntity(Requisition.class,
                        "select e " +
                                "from tsadv$Requisition e " +
                                "where e.id = :rId ",
                        Collections.singletonMap("rId", interviewQuestionnaireInt.getRequisition()),
                        "requisition.rest");

           /*     JobRequest jobRequest = metadata.create(JobRequest.class);
                jobRequest.setCandidatePersonGroup(personGroup);
                jobRequest.setRequestDate(CommonUtils.getSystemDate());
                jobRequest.setRequisition(requisition);
                jobRequest.setRequestStatus(JobRequestStatus.INTERVIEW);
                commitInstances.add(jobRequest);*/

                UUID questionnaireId = interviewQuestionnaireInt.getQuestionnaireId();

                Map<String, Object> map = new HashMap<>();
                map.put("qId", questionnaireId);
                map.put("requisitionId", interviewQuestionnaireInt.getRequisition());
                RequisitionHiringStep requisitionHiringStep = commonService.getEntity(RequisitionHiringStep.class,
                        "select rhs from tsadv$RequisitionHiringStep rhs, " +
                                "tsadv$HiringStepQuestionnaire hsq, " +
                                "tsadv$RcQuestionnaire q " +
                                "where rhs.hiringStep.id = hsq.hiringStep.id " +
                                "and hsq.questionnaire.id = q.id " +
                                "and q.id = :qId " +
                                "and rhs.requisition.id = :requisitionId",
                        map,
                        "requisitionHiringStep.view");

                if (requisitionHiringStep == null) {
                    throw new NullPointerException(String.format(
                            "RequisitionHiringStep for PreScreenTest by ID:[%s] not found!", questionnaireId));
                }
                map.clear();
                PersonGroupExt candidatePersonGroup = getUserPersonGroup();
                map.put("requisitionId", interviewQuestionnaireInt.getRequisition());
                map.put("candidatePersonGroupId", candidatePersonGroup.getId());
                JobRequest jobRequest = commonService.getEntity(JobRequest.class,
                        "select e from tsadv$JobRequest e " +
                                "   where e.requisition.id = :requisitionId " +
                                "   and e.candidatePersonGroup.id = :candidatePersonGroupId",
                        map,
                        "jobRequest.view");
                if (jobRequest == null) {
                    baseResult.setErrorMessage("На данной вакансии отсутствует отклик");
                    baseResult.setSuccess(false);
                    return baseResult;
                }
                if (jobRequestHasPreScreenInterview(jobRequest)) {
                    baseResult.setErrorMessage("Интервью уже есть на данном отклике");
                    baseResult.setSuccess(false);
                    return baseResult;
                }
                Interview interview = metadata.create(Interview.class);
                interview.setIsScheduled(false);
                if (interviewQuestionnaireInt.getInterviewStatus() != null) {
                    String status = interviewQuestionnaireInt.getInterviewStatus();
                    if (enableDraftStatusInterview.getEnabled() == false && (InterviewStatus.valueOf(status).getId().equals(10))) {
                        baseResult.setSuccess(false);
                        baseResult.setErrorMessage("статус черновик недоступен");
                    } else {
                        interview.setInterviewStatus(InterviewStatus.valueOf(status));
                    }
                } else {
                    interview.setInterviewStatus(InterviewStatus.COMPLETED);
                }

                CandidateInt candidateInt = new CandidateInt();
                candidateInt.setRequisitionId(jobRequest.getRequisition().getId());
                candidateInt.setUserExtId(userSessionSource.getUserSession().getId());
                if ("true".equals(checkVideoInterview(candidateInt).getSuccessMessage())) {
                    jobRequest.setRequestStatus(JobRequestStatus.ON_APPROVAL);
                    commitInstances.add(jobRequest);
                    if (jobRequest.getRequestStatus() != JobRequestStatus.DRAFT) {
                        UserExt userExt = employeeService.getUserExtByPersonGroupId(candidatePersonGroup.getId(), "user.browse");
                        notificationService.sendParametrizedNotification(
                                "requisition.notify.application.candidate",
                                userExt,
                                getMapForNotification(candidatePersonGroup, requisition));
                    }
                }

                interview.setRequisition(requisition);
                interview.setMainInterviewerPersonGroup(requisition.getRecruiterPersonGroup());
                interview.setInterviewDate(new Date());
                interview.setJobRequest(jobRequest);
                interview.setTimeFrom(new Date()); //TODO
                interview.setTimeTo(new Date()); //TODO
                interview.setRequisitionHiringStep(requisitionHiringStep);
                commitInstances.add(interview);

                RcQuestionnaire rcQuestionnaire = commonService.getEntity(
                        RcQuestionnaire.class,
                        "select e from tsadv$RcQuestionnaire e where e.id = :rqId",
                        Collections.singletonMap("rqId", questionnaireId),
                        "rcQuestionnaire.rest");

                if (rcQuestionnaire == null) {
                    throw new NullPointerException(String.format(
                            "RcQuestionnaire by ID: %s not found!", questionnaireId));
                }

                InterviewQuestionnaire interviewQuestionnaire = metadata.create(InterviewQuestionnaire.class);
                interviewQuestionnaire.setInterview(interview);
                interviewQuestionnaire.setQuestionnaire(rcQuestionnaire);
                commitInstances.add(interviewQuestionnaire);

                Supplier<Stream<InterviewQuestionInt>> streamQuestionsInt = () -> interviewQuestionnaireInt.getQuestions().stream();

                Double passingScore = rcQuestionnaire.getPassingScore();

                if (passingScore == null) {
                    throw new NullPointerException(String.format(
                            "PassingScore from RcQuestionnaire [%s] is null!", questionnaireId));
                }

                Double totalScore = 0d;

                for (RcQuestionnaireQuestion questionnaireQuestion : rcQuestionnaire.getQuestions()) {
                    RcQuestion rcQuestion = questionnaireQuestion.getQuestion();
                    RcAnswerType rcAnswerType = rcQuestion.getAnswerType();

                    InterviewQuestionInt findQuestionInt = streamQuestionsInt.get().filter(e -> e.getQuestionId().equals(rcQuestion.getId())).findFirst().orElse(null);

                    if (findQuestionInt == null) {
                        throw new Exception("Question not found in JSON!");
                    }

                    if (!findQuestionInt.getType().equalsIgnoreCase(rcAnswerType.getId())) {
                        throw new Exception("Incorrect question type!");
                    }

                    /**
                     * create interviewQuestion
                     * */
                    InterviewQuestion interviewQuestion = metadata.create(InterviewQuestion.class);
                    interviewQuestion.setInterviewQuestionnaire(interviewQuestionnaire);
                    interviewQuestion.setQuestion(rcQuestion);
                    interviewQuestion.setOrder(questionnaireQuestion.getOrder());
                    commitInstances.add(interviewQuestion);

                    Supplier<Stream<InterviewAnswerInt>> answerIntStream = () -> findQuestionInt.getAnswers().stream();

                    /**
                     * create interviewAnswer
                     * */
                    for (RcQuestionnaireAnswer questionnaireAnswer : questionnaireQuestion.getAnswers()) {
                        RcAnswer rcAnswer = questionnaireAnswer.getAnswer();

                        InterviewAnswer interviewAnswer = metadata.create(InterviewAnswer.class);
                        interviewAnswer.setInterviewQuestion(interviewQuestion);
                        interviewAnswer.setAnswer(rcAnswer);
                        interviewAnswer.setWeight(questionnaireAnswer.getWeight());
                        interviewAnswer.setOrder(rcAnswer.getOrder());

                        if (answerIntStream != null) {
                            answerIntStream.get()
                                    .filter(interviewAnswerInt -> interviewAnswerInt.getAnswerId().equals(rcAnswer.getId()))
                                    .findFirst()
                                    .ifPresent(answerInt -> interviewAnswer.setBooleanAnswer(answerInt.getChecked()));
                        }

                        if (BooleanUtils.isTrue(interviewAnswer.getBooleanAnswer())) {
                            Double weight = interviewAnswer.getWeight();
                            totalScore += weight != null ? weight : 0;
                        }

                        commitInstances.add(interviewAnswer);
                    }

                    switch (rcAnswerType) {
                        case DATE:
                        case TEXT:
                        case NUMBER:
                            InterviewAnswer answer = metadata.create(InterviewAnswer.class);
                            answer.setInterviewQuestion(interviewQuestion);

                            String anotherAnswer = findQuestionInt.getAnotherAnswer();
                            if (anotherAnswer != null && anotherAnswer.trim().length() > 0) {
                                switch (rcAnswerType) {
                                    case DATE: {
                                        answer.setDateAnswer(df.parse(anotherAnswer));
                                        break;
                                    }
                                    case TEXT: {
                                        answer.setTextAnswer(anotherAnswer);
                                        break;
                                    }
                                    case NUMBER: {
                                        answer.setNumberAnswer(Double.valueOf(anotherAnswer));
                                        break;
                                    }
                                }
                            }

                            commitInstances.add(answer);
                            break;
                    }
                }

                dataManager.commit(new CommitContext(commitInstances));

                TestResult testResult = new TestResult();
                testResult.setPassed(totalScore >= passingScore);
                testResult.setScore(totalScore.toString());

                String testResultJson = gson.toJson(testResult);

                baseResult.setSuccessMessage(testResultJson);
                baseResult.setSuccess(true);
            }
        } catch (Exception ex) {
            if (ex.getMessage().contains("idx_tsadv_job_request_unq")) {
                baseResult.setErrorMessage("Ошибка - данный кандидат уже откликался на эту вакансию");
            } else {
                baseResult.setErrorMessage(ex.getMessage());
            }
            ex.printStackTrace();
        }
        return baseResult;
    }

    protected boolean jobRequestHasPreScreenInterview(JobRequest jobRequest) {
        Map<String, Object> map = new HashMap<>();
        map.put("jobRequestId", jobRequest.getId());
        Long count = commonService.getCount(Interview.class, "select e from tsadv$Interview e, IN(e.questionnaires) q " +
                "where e.jobRequest.id = :jobRequestId " +
                "and q.questionnaire.category.code = 'PRE_SCREEN_TEST'", map);
        return count > 0;
    }

    /**
     * Возвращает Справочник Пол
     * <br><p>
     * Метод: GET <br>
     * Авторизация: Не требуется <br>
     * Передача параметров: В строке запроса (QUERY) <br>
     *
     * @param languageCode Код языка RU/KZ/EN для извлечения названий
     * @return Список сущностей Пол в формате JSON <br>
     * - code (string) Код <br>
     * - name (string) Наименование <br>
     * - id   (uuid) Id сущности <br>
     */
    @Override
    public String getSex(String languageCode) {
        Map<Integer, Object> queryParams = new HashMap<>();
        queryParams.put(1, languageCode);

        String query = "with params as (select upper(?1) as lang)" +
                "   select s.code," +
                "     case par.lang when 'RU' then s.lang_value1  when 'KZ' then s.lang_value2 when 'EN' then s.lang_value3 end as name," +
                "     s.id " +
                "     from BASE_DIC_SEX s cross join params par" +
                "   where s.delete_ts is null";

        return getDictionaryList(query, queryParams);
    }

    @Override
    public String getNationalities(String languageCode) {
        Map<Integer, Object> queryParams = new HashMap<>();
        queryParams.put(1, languageCode);

        String query = "with params as (select upper(?1) as lang)" +
                "   select n.code," +
                "     case par.lang when 'RU' then n.lang_value1  when 'KZ' then n.lang_value2 when 'EN' then n.lang_value3 end as name," +
                "     n.id " +
                "     from tsadv_dic_nationality n cross join params par" +
                " where n.delete_ts is null";

        return getDictionaryList(query, queryParams);
    }

    @Override
    public String getCitizenship(String languageCode) {
        Map<Integer, Object> queryParams = new HashMap<>();
        queryParams.put(1, languageCode);

        String query = "with params as (select upper(?1) as lang)" +
                "   select c.code," +
                "     case par.lang when 'RU' then c.lang_value1  when 'KZ' then c.lang_value2 when 'EN' then c.lang_value3 end as name," +
                "     c.id " +
                "     from tsadv_dic_citizenship c cross join params par" +
                " where c.delete_ts is null ";

        return getDictionaryList(query, queryParams);
    }

    @Override
    public String getMaritalStatuses(String languageCode) {
        Map<Integer, Object> queryParams = new HashMap<>();
        queryParams.put(1, languageCode);

        String query = "with params as (select upper(?1) as lang)" +
                "   select ms.code," +
                "          case par.lang when 'RU' then ms.lang_value1  when 'KZ' then ms.lang_value2 when 'EN' then ms.lang_value3 end as name," +
                "          ms.id " +
                "     from tsadv_dic_marital_status ms cross join params par" +
                "    where ms.delete_ts is null ";
        return getDictionaryList(query, queryParams);
    }

    /**
     * Возвращает информацию о текущем Кандидате
     * <br><p>
     * Метод: GET <br>
     * Авторизация: Не требуется <br>
     * Передача параметров: В строке запроса (QUERY) <br>
     *
     * @param languageCode Код языка RU/KZ/EN для извлечения названий
     * @return Сущность Лицо в формате JSON <br>
     * - code (string) Код <br>
     * - name (string) Наименование <br>
     * - id   (uuid) Id сущности <br>
     */
    @Override
    public String getUserPerson(String languageCode) {
        List<PersonInt> entityList = new ArrayList<>();
        Map<Integer, Object> queryParams = new HashMap<>();
        queryParams.put(1, languageCode);
        queryParams.put(2, userSessionSource.getUserSession().getUser().getId());
        queryParams.put(3, CommonUtils.getSystemDate());

        String query = "with params as (select upper(?1) as lang)" +
                "     select p.group_id as id," +
                "            p.first_name," +
                "            p.last_name," +
                "            p.middle_name," +
                "            to_char(p.date_of_birth, 'yyyy-MM-dd') as birth_date," +
                "            p.national_identifier," +
                "            p.sex_id as sex," +
                "            case par.lang when 'RU' then s.lang_value1 when 'KZ' then s.lang_value2 when 'EN' then s.lang_value3 end as sex_name," +
                "            p.nationality_id as nationality," +
                "            case par.lang when 'RU' then n.lang_value1 when 'KZ' then n.lang_value2 when 'EN' then n.lang_value3 end as nationality_name," +
                "            p.citizenship_id as citizenship," +
                "            case par.lang when 'RU' then c.lang_value1 when 'KZ' then c.lang_value2 when 'EN' then c.lang_value3 end as citizenship_name," +
                "            p.marital_status_id as marital_status," +
                "            case par.lang when 'RU' then ms.lang_value1 when 'KZ' then ms.lang_value2 when 'EN' then ms.lang_value3 end as marital_status_name," +
                "            p.image_id," +
                "            addr.city as cityName" +
                "       from sec_user u " +
                " cross join params par" +
                "       join BASE_PERSON p on (p.group_id = u.person_group_id)" +
                "  left join BASE_DIC_SEX s on (s.id = p.sex_id)" +
                "  left join tsadv_dic_nationality n on (n.id = p.nationality_id)" +
                "  left join tsadv_dic_citizenship c on (c.id = p.citizenship_id)" +
                "  left join tsadv_dic_marital_status ms on (ms.id = p.marital_status_id)" +
                "  left join (select addr2.city, addr2.person_group_id from tsadv_address addr2 " +
                "               join tsadv_dic_address_type adt on addr2.address_type_id = adt.id " +
                "                   and adt.code='RESIDENCE'" +
                "                   and ?3 between addr2.start_date and addr2.end_date ) addr " +
                "       on addr.person_group_id = u.person_group_id " +
                " where u.id = ?2 " +
                "   and ?3 between p.start_date and p.end_date " +
                "   and u.delete_ts is null" +
                "   and p.delete_ts is null";

        List<Object[]> resultList = commonService.emNativeQueryResultList(query, queryParams);

        if (resultList != null)
            for (Object[] row : resultList) {
                PersonInt pi = new PersonInt();
                int i = 0;
                pi.setId((UUID) row[i++]);
                pi.setFirstName((String) row[i++]);
                pi.setLastName((String) row[i++]);
                pi.setMiddleName((String) row[i++]);
                pi.setBirthDate((String) row[i++]);
                pi.setNationalIdentifier((String) row[i++]);
                pi.setSex((UUID) row[i++]);
                pi.setSexName((String) row[i++]);
                pi.setNationality((UUID) row[i++]);
                pi.setNationalityName((String) row[i++]);
                pi.setCitizenship((UUID) row[i++]);
                pi.setCitizenshipName((String) row[i++]);
                pi.setMaritalStatus((UUID) row[i++]);
                pi.setMaritalStatusName((String) row[i++]);
//                pi.setPhoto(row[i] != null ? Base64.getEncoder().encodeToString((byte[]) row[i]) : null);

                Object imageObject = row[i++];
                if (imageObject != null) {
                    UUID imageId = (UUID) imageObject;
                    FileDescriptor fileDescriptor = ucoCommonService.getFileDescriptor(imageId);
                    if (fileDescriptor != null) {
                        try {
                            byte[] imageByte = fileStorageAPI.loadFile(fileDescriptor);

                            if (imageByte != null && imageByte.length > 0) {
                                pi.setPhoto(Base64.getEncoder().encodeToString(imageByte));
                            }
                        } catch (FileStorageException e) {
                            e.printStackTrace();
                        }
                    }
                }
                pi.setCityName((String) row[i]);

                pi.setEducation(getPersonEducation(pi.getId(), languageCode));
                pi.setContacts(getPersonContacts(pi.getId(), languageCode));
                pi.setExperience(getPersonExperience(pi.getId(), languageCode));
                pi.setAttachments(getPersonAttachments(pi.getId(), languageCode));
                pi.setCompetences(getPersonCompetences(pi.getId(), languageCode));

                entityList.add(pi);
            }
        return entityList.isEmpty() ? "" : jsonListToSingleObject(toJsonList(entityList));
    }

    @Override
    public String getEducationDegrees(String languageCode) {
        Map<Integer, Object> queryParams = new HashMap<>();
        queryParams.put(1, languageCode);

        String query = " with params as (select upper(?1) as lang)" +
                "                   select ed.code as code," +
                "                          case par.lang when 'RU' then ed.lang_value1 when 'KZ' then ed.lang_value2 when 'EN' then ed.lang_value3 end as name," +
                "                          ed.id as id " +
                "                     from tsadv_dic_education_degree ed" +
                "               cross join params par" +
                "                    where ed.delete_ts is null";
        return getDictionaryList(query, queryParams);
    }

    @Override
    public String getEducationLevels(String languageCode) {
        Map<Integer, Object> queryParams = new HashMap<>();
        queryParams.put(1, languageCode);

        String query = "with params as (select upper(?1) as lang)" +
                "                   select el.code as code," +
                "                          case par.lang when 'RU' then el.lang_value1 when 'KZ' then el.lang_value2 when 'EN' then el.lang_value3 end as name," +
                "                          el.id as id " +
                "                     from tsadv_dic_education_level el" +
                "               cross join params par" +
                "                    where el.delete_ts is null";

        return getDictionaryList(query, queryParams);
    }

    @Override
    public String getContactTypes(String languageCode) {
        Map<Integer, Object> queryParams = new HashMap<>();
        queryParams.put(1, languageCode);

        String query = " with params as (select upper(?1) as lang)" +
                "                   select pt.code as code," +
                "                          case par.lang when 'RU' then pt.lang_value1 when 'KZ' then pt.lang_value2 when 'EN' then pt.lang_value3 end as name," +
                "                          pt.id as id " +
                "                     from tsadv_dic_phone_type pt " +
                "               cross join params par" +
                "                    where pt.delete_ts is null";

        return getDictionaryList(query, queryParams);
    }

    @Override
    public String getAttachmentCategories(String languageCode) {
        Map<Integer, Object> queryParams = new HashMap<>();
        queryParams.put(1, languageCode);

        String query = "with params as (select upper(?1) as lang)" +
                "                   select ac.code as code," +
                "                          case par.lang when 'RU' then ac.lang_value1 when 'KZ' then ac.lang_value2 when 'EN' then ac.lang_value3 end as name," +
                "                          ac.id as id " +
                "                     from tsadv_dic_attachment_category ac " +
                "               cross join params par" +
                "                    where ac.delete_ts is null";

        return getDictionaryList(query, queryParams);
    }

    @Override
    public String getCompetences(String languageCode) {
        languageIndex = languageIndex(languageCode);
        Map<Integer, Object> queryParams = new HashMap<>();
        queryParams.put(1, languageCode);
        queryParams.put(2, CommonUtils.getSystemDate());

        String query = "with params as (select upper(?1) as lang)" +
                "                   select null as code," +
                "                          c.competence_name_lang" + languageIndex + " as name," +
                "                          c.group_id as id, " +
                "                          dc.code " +
                "                     from tsadv_competence c " +
                "                     join tsadv_dic_competence_type dc on c.competece_type_id = dc.id " +
                "               cross join params par" +
                "                    where c.delete_ts is null  " +
                "                      and c.is_rc_available = true" +
                "                      and ?2 between c.start_date and c.end_date";

        return getCompetenceList(query, queryParams);
    }

    @Override
    public String getScaleLevels(UUID competence, String languageCode) {
        languageIndex = languageIndex(languageCode);
        Map<Integer, Object> queryParams = new HashMap<>();
        queryParams.put(1, languageCode);
        queryParams.put(2, competence);
        queryParams.put(3, CommonUtils.getSystemDate());

        String query = "with params as (select upper(?1) as lang)" +
                "                   select sl.level_number || '' as code," +
                "                          sl.level_number || ' - ' || sl.level_name_lang" + languageIndex + " as name," +
                "                          sl.id " +
                "                     from tsadv_scale_level sl" +
                "               cross join params par" +
                "                     join tsadv_competence c on (c.scale_id = sl.scale_id)" +
                "                    where sl.delete_ts is null  " +
                "                      and c.group_id = ?2" +
                "                      and ?3 between c.start_date and c.end_date";

        return getDictionaryList(query, queryParams);
    }

    @Override
    public BaseResult uploadUserPhoto(PersonInt person) {
        BaseResult result = new BaseResult();

        try {
            byte[] decodedPhoto = Base64.getDecoder().decode(person.getPhoto().getBytes("UTF-8"));

            FileDescriptor userImage = metadata.create(FileDescriptor.class);
            userImage.setCreateDate(CommonUtils.getSystemDate());
            userImage.setName(person.getFirstName() + "photo");

            fileStorageAPI.saveFile(userImage, decodedPhoto);

            Map<String, Object> params = new HashMap<>();
            params.put("userId", userSessionSource.getUserSession().getUser().getId());
            params.put("systemDate", CommonUtils.getSystemDate());

            PersonExt p = commonService.getEntity(PersonExt.class, "select e " +
                            " from tsadv$PersonExt e " +
                            " where e.id = :userId " +
                            " and e.group.id = e.personGroup.id " +
                            " and :systemDate between e.startDate and e.endDate ",
                    params,
                    "person.full"
            );
            FileDescriptor commitImage = dataManager.commit(userImage);
            p.setImage(commitImage);
//            p.setImage(CommonUtils.resize(new ByteArrayInputStream(decodedPhoto), IMAGE_SIZE.XSS)); TODO

            dataManager.commit(p);
            result.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            result.setErrorMessage(e.getMessage());
        }

        return result;
    }

    @Override
    public String getScheduledInterviews(UUID requisition, String languageCode) {
        languageIndex = languageIndex(languageCode);
        List<ScheduledInterviewInt> entityList = new ArrayList<>();
        Map<Integer, Object> queryParams = new HashMap<>();
        queryParams.put(1, languageCode);
        queryParams.put(2, userSessionSource.getUserSession().getUser().getId());
        queryParams.put(3, requisition);

        String query = "with params as (select upper(?1) as lang, " +
                "                              u.person_group_id as candidate_person_group_id" +
                "                         from sec_user u" +
                "                        where u.id = ?2)" +
                "     select i.id," +
                "            hs.step_name as hiring_step," +
                "            to_char(i.interview_date, 'yyyy-MM-dd') as interview_date," +
                "            to_char(i.time_from, 'HH24:mi') as time_from," +
                "            to_char(i.time_to, 'HH24:mi') as time_to," +
                "            case par.lang when 'RU' then l.address_lang1 when 'KZ' then l.address_lang2 when 'EN' then l.address_lang3 end as place," +
                "            i.max_candidates_count - (select count(1) " +
                "                                        from tsadv_interview i1 " +
                "                                       where i1.job_request_id is not null " +
                "                                         and i1.requisition_hiring_step_id = i.requisition_hiring_step_id" +
                "                                         and i1.interview_status = 30 /*PLANNED*/) as available_requests_count," +
                "            r.id as requisition," +
                "            r.code," +
                "            case when p.id is not null then p.position_name_lang" + languageIndex + " else j.job_name_lang" + languageIndex + " end as job            " +    //columnChanged TODO
                "       from tsadv_interview i" +
                " cross join params par" +
                "       join tsadv_requisition r on (r.id = i.requisition_id)" +
                "       join tsadv_requisition_hiring_step rhs on (rhs.id = i.requisition_hiring_step_id)" +
                "       join tsadv_hiring_step hs on (hs.id = rhs.hiring_step_id)" +
                "  left join BASE_POSITION p on (p.group_id = r.position_group_id and CURRENT_DATE between p.start_date and p.end_date) " +
                "  left join tsadv_job j on (j.group_id = r.job_group_id and CURRENT_DATE between j.start_date and j.end_date) " +
                "  left join BASE_DIC_LOCATION l on (l.id = i.place_id)" +
                "      where r.id = ?3" +
                "        and i.is_scheduled = true" +
                "        and i.delete_ts is null" +
                "        and (i.interview_date > current_date or i.interview_date = current_date and i.time_from > current_time)" +
                "        and /* нет не отмененного интервью пользователя для выбранного этапа */ " +
                "           not exists (select 1 " +
                "                         from tsadv_interview i2 " +
                "                         join tsadv_job_request jr2 on (jr2.id = i2.job_request_id)  " +
                "                        where i2.delete_ts is null" +
                "                          and jr2.delete_ts is null" +
                "                          and i2.requisition_hiring_step_id = i.requisition_hiring_step_id" +
                "                          and jr2.candidate_person_group_id = par.candidate_person_group_id" +
                "                          and i2.interview_status <> '50' /*CANCELLED*/)" +
                "        and /* нет не пройденных предыдущих шагов пользователя в выбранной заявке */ " +
                "           not exists (select 1 " +
                "                         from tsadv_requisition_hiring_step rhs3" +
                "                        where rhs3.delete_ts is null" +
                "                          and rhs3.requisition_id = rhs.requisition_id" +
                "                          and rhs3.order_ < rhs.order_" +
                "                          and not exists(select 1 " +
                "                                           from tsadv_interview i3" +
                "                                           join tsadv_job_request jr3 on (jr3.id  = i3.job_request_id)                          " +
                "                                          where i3.delete_ts is null" +
                "                                            and jr3.delete_ts is null" +
                "                                            and i3.requisition_hiring_step_id = rhs3.id" +
                "                                            and jr3.candidate_person_group_id = par.candidate_person_group_id" +
                "                                            and i3.interview_status = '40' /*COMPLETED*/))" +
                "  order by i.interview_date, i.time_from";

        List<Object[]> resultList = commonService.emNativeQueryResultList(query, queryParams);

        if (resultList != null)
            for (Object[] row : resultList) {
                ScheduledInterviewInt sii = new ScheduledInterviewInt();
                int i = 0;
                sii.setId((UUID) row[i++]);
                sii.setHiringStep((String) row[i++]);
                sii.setInterviewDate((String) row[i++]);
                sii.setTimeFrom((String) row[i++]);
                sii.setTimeTo((String) row[i++]);
                sii.setPlace((String) row[i++]);
                sii.setAvailableRequestCount(((Long) row[i++]).intValue());
                sii.setRequisition((UUID) row[i++]);
                sii.setRequisitionCode((String) row[i++]);
                sii.setRequisitionJob((String) row[i]);

                if (sii.getAvailableRequestCount() > 0)
                    entityList.add(sii);
            }

        return toJsonList(entityList);
    }

    @Inject
    protected Configuration configuration;

    @Override
    public String getUserJobRequests(String languageCode) {
        RecruitmentConfig recruitmentConfig = configuration.getConfig(RecruitmentConfig.class);
        boolean filterByCreatedByForRest = recruitmentConfig.getFilterByCreatedByForRest();
        languageIndex = languageIndex(languageCode);
        List<JobRequestInt> entityList = new ArrayList<>();
        Map<Integer, Object> queryParams = new HashMap<>();
        queryParams.put(1, languageCode);
        queryParams.put(2, getUserPersonGroup().getId());
        if (filterByCreatedByForRest) {
            queryParams.put(3, userSessionSource.getUserSession().getUser().getLogin());
        }

        String query = "with params as (select upper(?1) as lang)" +
                "                     select jr.id," +
                "                            to_char(jr.request_date, 'yyyy-MM-dd') as request_date," +
                "                            r.id as requisition," +
                "                            r.code," +
//                "                            case when p.id is not null then p.position_name_lang" + languageIndex + " else j.job_name_lang" + languageIndex + " end as job," +  //columnChanged TODO
                "                            case par.lang when 'RU' then coalesce(r.name_for_site_lang1, p.position_name_lang1, j.job_name_lang1)\n" +
                "                            when 'KZ' then coalesce(r.name_for_site_lang2, p.position_name_lang2, j.job_name_lang2)\n" +
                "                            when 'EN' then coalesce (r.name_for_site_lang3, p.position_name_lang3, j.job_name_lang3) end as job," +
                "                            jr.request_status," +
                "                            jr.video_file_id" +
                "                       from tsadv_job_request jr" +
                "                 cross join params par" +
                "                       join tsadv_requisition r on (r.id = jr.requisition_id)" +
                "                  left join BASE_POSITION p on (p.group_id = r.position_group_id and CURRENT_DATE between p.start_date and p.end_date) " +
                "                  left join tsadv_job j on (j.group_id = r.job_group_id and CURRENT_DATE between j.start_date and j.end_date) " +
                "                      where jr.candidate_person_group_id = ?2 " +
                "                        and jr.delete_ts is null  " +
                (filterByCreatedByForRest ? " and jr.created_by = ?3 " : "") +
                "                  order by jr.request_date";

        List<Object[]> resultList = commonService.emNativeQueryResultList(query, queryParams);

        if (resultList != null)
            for (Object[] row : resultList) {
                int i = 0;
                JobRequestInt jri = new JobRequestInt();
                jri.setId((UUID) row[i++]);
                jri.setRequestDate((String) row[i++]);
                jri.setRequisition((UUID) row[i++]);
                jri.setRequisitionCode((String) row[i++]);
                jri.setRequisitionJob((String) row[i++]);
                jri.setRequestStatus(JobRequestStatus.fromId((Integer) row[i++]).getId().toString());
                jri.setVideoFile((UUID) row[i]);
                entityList.add(jri);
            }
        return toJsonList(entityList);
    }

    @Override
    public String getUserInterviews(String languageCode) {
        languageIndex = languageIndex(languageCode);
        List<InterviewInt> entityList = new ArrayList<>();
        Map<Integer, Object> queryParams = new HashMap<>();
        queryParams.put(1, languageCode);
        queryParams.put(2, getUserPersonGroup().getId());

        String query = "with params as (select upper(?1) as lang)" +
                "                     select i.id," +
                "                            hs.step_name as hiring_step," +
                "                            to_char(i.interview_date, 'yyyy-MM-dd') as interview_date," +
                "                            to_char(i.time_from, 'HH24:mi') as time_from," +
                "                            to_char(i.time_to, 'HH24:mi') as time_to," +
                "                            case par.lang when 'RU' then l.address_lang1 when 'KZ' then l.address_lang2 when 'EN' then l.address_lang3 end as place," +
                "                            r.id as requisition," +
                "                            r.code," +
//                "                            case when p.id is not null then p.position_name_lang" + languageIndex + " else j.job_name_lang" + languageIndex + " end as job," +  //columnChanged TODO
                "                            case par.lang when 'RU' then coalesce(r.name_for_site_lang1, p.position_name_lang1, j.job_name_lang1)\n" +
                "                            when 'KZ' then coalesce(r.name_for_site_lang2, p.position_name_lang2, j.job_name_lang2)\n" +
                "                            when 'EN' then coalesce (r.name_for_site_lang3, p.position_name_lang3, j.job_name_lang3) end as job," +
                "                            i.interview_status," +
                "                            i.reason " +
                "                       from tsadv_interview i" +
                "                 cross join params par" +
                "                       join tsadv_job_request jr on (jr.id = i.job_request_id)" +
                "                       join tsadv_requisition r on (r.id = jr.requisition_id)" +
                "                       join tsadv_requisition_hiring_step rhs on (rhs.id = i.requisition_hiring_step_id)" +
                "                       join tsadv_hiring_step hs on (hs.id = rhs.hiring_step_id)" +
                "                  left join BASE_POSITION p on (p.group_id = r.position_group_id and CURRENT_DATE between p.start_date and p.end_date) " +
                "                  left join tsadv_job j on (j.group_id = r.job_group_id and CURRENT_DATE between j.start_date and j.end_date) " +
                "                  left join BASE_DIC_LOCATION l on (l.id = i.place_id) " +
                "                      where jr.candidate_person_group_id = ?2" +
                "                        and i.is_scheduled = false" +
                "                        and i.delete_ts is null                         " +
                "                  order by i.interview_date, i.time_from";

        List<Object[]> resultList = commonService.emNativeQueryResultList(query, queryParams);

        if (resultList != null)
            for (Object[] row : resultList) {
                int i = 0;
                InterviewInt ii = new InterviewInt();
                ii.setId((UUID) row[i++]);
                ii.setHiringStep((String) row[i++]);
                ii.setInterviewDate((String) row[i++]);
                ii.setTimeFrom((String) row[i++]);
                ii.setTimeTo((String) row[i++]);
                ii.setPlace((String) row[i++]);
                ii.setRequisition((UUID) row[i++]);
                ii.setRequisitionCode((String) row[i++]);
                ii.setRequisitionJob((String) row[i++]);
                ii.setInterviewStatus(InterviewStatus.fromId((Integer) row[i++]).getId().toString());
                ii.setReason((String) row[i]);
                entityList.add(ii);
            }
        return toJsonList(entityList);
    }

    @Override
    public BaseResult requestInterview(UUID interviewId) {
        BaseResult result = new BaseResult();
        ArrayList<String> errorMessages = new ArrayList<>();

        try {
            List<Entity> commitInstances = new ArrayList<>();

            PersonGroupExt candidatePersonGroup = getUserPersonGroup();
            Interview scheduledInterview = commonService.getEntity(Interview.class,
                    "select e from tsadv$Interview e where e.id = :interviewId",
                    Collections.singletonMap("interviewId", interviewId),
                    "interview.full");

            Map<String, Object> jrParams = new HashMap<>();
            jrParams.put("jobRequestId", scheduledInterview.getJobRequest().getId());
            jrParams.put("candidatePersonGroupId", candidatePersonGroup.getId());

            JobRequest jobRequest = commonService.getEntity(JobRequest.class,
                    "   select e " +
                            "  from tsadv$JobRequest e " +
                            " where e.requisition.id = :jobRequestId " +
                            "   and e.candidatePersonGroup.id = :candidatePersonGroupId",
                    jrParams,
                    "jobRequest.full");

            if (jobRequest == null) {
                jobRequest = metadata.create(JobRequest.class);
                jobRequest.setCandidatePersonGroup(candidatePersonGroup);
                jobRequest.setRequestDate(CommonUtils.getSystemDate());
                jobRequest.setRequisition(scheduledInterview.getRequisition());
            } else {
                Map<String, Object> iParams = new HashMap<>();
                iParams.put("jobRequestId", jobRequest.getId());
                iParams.put("interviewDate", scheduledInterview.getInterviewDate());
                iParams.put("timeFrom", scheduledInterview.getTimeFrom());
                iParams.put("timeTo", scheduledInterview.getTimeTo());
                iParams.put("requisitionHiringStepId", scheduledInterview.getRequisitionHiringStep().getId());

                List<Interview> userInterviews = commonService.getEntities(Interview.class,
                        "select e " +
                                "   from tsadv$Interview e " +
                                "  where e.deleteTs is null " +
                                "    and e.isScheduled = false " +
                                "    and e.jobRequest.id = :jobRequestId " +
                                "    and e.interviewStatus <> 50 " +
                                "    and e.interviewDate = :interviewDate " +
                                "    and e.timeFrom = :timeFrom " +
                                "    and e.timeTo = :timeTo" +
                                "    and e.requisitionHiringStep.id = :requisitionHiringStepId ",
                        iParams,
                        View.LOCAL);

                if (userInterviews != null && !userInterviews.isEmpty()) {
                    errorMessages.add("Interview for this requisition, hiring step, date and time already exists!");
                }
            }

            if (!errorMessages.isEmpty()) {
                result.setSuccess(false);
                result.setErrorMessage(errorMessages.stream().reduce((e1, e2) -> e1.concat(" ").concat(e2)).orElse(""));
            } else {

                jobRequest.setRequestStatus(JobRequestStatus.INTERVIEW);
                commitInstances.add(jobRequest);

                Interview plannedInterview = metadata.create(Interview.class);
                plannedInterview.setIsScheduled(false);
                plannedInterview.setJobRequest(jobRequest);
                plannedInterview.setRequisitionHiringStep(scheduledInterview.getRequisitionHiringStep());
                plannedInterview.setInterviewDate(scheduledInterview.getInterviewDate());
                plannedInterview.setTimeFrom(scheduledInterview.getTimeFrom());
                plannedInterview.setTimeTo(scheduledInterview.getTimeTo());
                plannedInterview.setMainInterviewerPersonGroup(scheduledInterview.getMainInterviewerPersonGroup());
                plannedInterview.setPlace(scheduledInterview.getPlace());
                plannedInterview.setInterviewStatus(InterviewStatus.PLANNED);

                commitInstances.add(plannedInterview);

                for (RcQuestionnaire questionnaire : commonService.getEntities(RcQuestionnaire.class,
                        "select e " +
                                "    from tsadv$RcQuestionnaire e, tsadv$InterviewQuestionnaire iq" +
                                "   where iq.interview.id = :interviewId" +
                                "     and e.id = iq.questionnaire.id  ",
                        Collections.singletonMap("interviewId", interviewId),
                        "rcQuestionnaire.view")) {
                    InterviewQuestionnaire plannedInterviewQuestionnaire = metadata.create(InterviewQuestionnaire.class);
                    plannedInterviewQuestionnaire.setInterview(plannedInterview);
                    plannedInterviewQuestionnaire.setQuestionnaire(questionnaire);

                    commitInstances.add(plannedInterviewQuestionnaire);

                    for (RcQuestionnaireQuestion questionnaireQuestion : questionnaire.getQuestions()) {
                        InterviewQuestion interviewQuestion = metadata.create(InterviewQuestion.class);
                        interviewQuestion.setInterviewQuestionnaire(plannedInterviewQuestionnaire);
                        interviewQuestion.setQuestion(questionnaireQuestion.getQuestion());
                        interviewQuestion.setOrder(questionnaireQuestion.getOrder());

                        commitInstances.add(interviewQuestion);

                        for (RcQuestionnaireAnswer questionnaireAnswer : questionnaireQuestion.getAnswers()) {
                            InterviewAnswer interviewAnswer = metadata.create(InterviewAnswer.class);
                            interviewAnswer.setInterviewQuestion(interviewQuestion);
                            interviewAnswer.setAnswer(questionnaireAnswer.getAnswer());
                            interviewAnswer.setWeight(questionnaireAnswer.getWeight());
                            interviewAnswer.setOrder(questionnaireAnswer.getAnswer().getOrder());

                            commitInstances.add(interviewAnswer);
                        }

                        switch (interviewQuestion.getQuestion().getAnswerType()) {
                            case DATE:
                            case TEXT:
                            case NUMBER:
                                InterviewAnswer answer = metadata.create(InterviewAnswer.class);
                                answer.setInterviewQuestion(interviewQuestion);
                                commitInstances.add(answer);
                                break;
                        }
                    }
                }

                dataManager.commit(new CommitContext(commitInstances));

                result.setSuccess(true);
            }
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
            log.error(e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Откликается на Вакансию (Создаёт в системе отклик) <br>
     * <br><p>
     * Метод: POST <br>
     * Авторизация: OAuth-токен должен быть передан в заголовке запроса Authorization с типом Bearer. <br>
     * Передача параметров: В теле запроса в формате JSON (BODY) <br>
     *
     * @param jobRequestInt Отклик в формате JSON <br>
     *                      - requestDate (String) Дата отклика <br>
     *                      - requestStatus (String) Статус отклика <br>
     *                      - requisition (UUID) Id Вакансии <br>
     *                      - requisitionCode (String) Код Вакансии <br>
     *                      - requisitionJob (String) Должность <br>
     *                      - videoFile (UUID) Id видеозаписи о себе <br>
     *                      - source (UUID) Id источника отклика <br>
     *                      - otherSource (String) Прочий источник (отсутствующий в справочнике источников) <br>
     * @return Результат выполнения операции в формате JSON <br>
     * - success (boolean) Успешно ли (true / false) <br>
     * - successMessage (String) Сообщение при успехе <br>
     * - errorMessage (String) Сообщение при ошибке <br>
     */
    @Override
    public BaseResult requestRequisition(JobRequestInt jobRequestInt) {
        BaseResult result = new BaseResult();
        ArrayList<String> errorMessages = new ArrayList<>();

        PersonGroupExt candidatePersonGroup = getUserPersonGroup();
        if (candidatePersonGroup == null) {
            errorMessages.add("User has no relation with a person!");
            result.setSuccess(false);
            result.setErrorMessage(errorMessages.stream().reduce((e1, e2) -> e1.concat(" ").concat(e2)).orElse(""));
            return result;
        }
        Requisition requisition = commonService.getEntity(Requisition.class, "select e from tsadv$Requisition e where e.id = :reqId",
                ParamsMap.of("reqId", jobRequestInt.getRequisition()), "_local");

        Map<String, Object> jrParams = new HashMap<>();
        jrParams.put("requisitionId", jobRequestInt.getRequisition());
        jrParams.put("candidatePersonGroupId", candidatePersonGroup.getId());

        List<JobRequest> jrList = commonService.getEntities(JobRequest.class,
                "select e " +
                        "    from tsadv$JobRequest e " +
                        "   where e.deleteTs is null " +
                        "     and e.requisition.id = :requisitionId " +
                        "     and e.candidatePersonGroup.id = :candidatePersonGroupId",
                jrParams,
                View.LOCAL);

        if (jrList != null && !jrList.isEmpty())
            errorMessages.add("Job request for this requisition already exists!");

        if (!errorMessages.isEmpty()) {
            result.setSuccess(false);
            result.setErrorMessage(errorMessages.stream().reduce((e1, e2) -> e1.concat(" ").concat(e2)).orElse(""));
        } else {
            try {
                JobRequest jobRequest = metadata.create(JobRequest.class);
                CandidateInt candidateInt = new CandidateInt();
                candidateInt.setRequisitionId(jobRequestInt.getRequisition());
                candidateInt.setUserExtId(userSessionSource.getUserSession().getId());
                if (("true".equals(checkPrescreeningTest(candidateInt).getSuccessMessage()))
                        && ("true".equals(checkVideoInterview(candidateInt).getSuccessMessage()))) {
                    jobRequest.setRequestStatus(JobRequestStatus.ON_APPROVAL);
                } else {
                    jobRequest.setRequestStatus(JobRequestStatus.DRAFT);
                }
                jobRequest.setRequisition(requisition);
                jobRequest.setCandidatePersonGroup(candidatePersonGroup);
                jobRequest.setRequestDate(CommonUtils.getSystemDate());
                jobRequest.setSource(commonService.getEntity(DicSource.class, jobRequestInt.getSource()));
                jobRequest.setOtherSource(jobRequestInt.getOtherSource());
                requisition = dataManager.reload(jobRequest.getRequisition(), "requisition.view");
                List<Entity> commitInstances = new ArrayList<>();
                commitInstances.add(jobRequest);
                dataManager.commit(new CommitContext(commitInstances));
                if (jobRequest.getRequestStatus() != JobRequestStatus.DRAFT) {
                    UserExt userExt = employeeService.getUserExtByPersonGroupId(candidatePersonGroup.getId(), "user.browse");
                    notificationService.sendParametrizedNotification(
                            "requisition.notify.application.candidate",
                            userExt,
                            getMapForNotification(candidatePersonGroup, requisition));
                }
                result.setSuccess(true);
                result.setErrorMessage(null);
            } catch (Exception e) {
                result.setSuccess(false);
                result.setErrorMessage(e.getMessage());
            }
        }

        return result;
    }

    protected Map<String, Object> getMapForNotification(PersonGroupExt personGroupExt, Requisition requisition) {
        Map<String, Object> paramsMap = new HashMap<>();
        Case personNameEn = getCasePersonName(personGroupExt, "en", "Nominative");
        Case personNameKz = getCasePersonName(personGroupExt, "kz", "Атау септік");
        Case personNameRu = getCasePersonName(personGroupExt, "ru", "Именительный");

        PersonGroupExt personGroup = personGroupExt;
        paramsMap.put("personFullNameEn", getLongValueOrFullName(personNameEn, personGroup));
        paramsMap.put("personFullNameKz", getLongValueOrFullName(personNameKz, personGroup));
        paramsMap.put("personFullNameRu", getLongValueOrFullName(personNameRu, personGroup));
        paramsMap.put("requisitionNameEn", requisition.getNameForSiteLang3());
        paramsMap.put("requisitionNameRu", requisition.getNameForSiteLang1());
        paramsMap.put("requisitionNameKz", requisition.getNameForSiteLang2());
        paramsMap.put("code", requisition.getCode());
        paramsMap.put("requisitionId", requisition.getId());
        return paramsMap;
    }

    protected Case getCasePersonName(PersonGroupExt personGroupExt, String language, String caseName) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("personGroupId", personGroupExt.getId()); //TODO: personGroup need test what if personGroup is null?
        paramMap.put("systemDate", CommonUtils.getSystemDate());
        paramMap.put("language", language);
        paramMap.put("case", caseName);
        Case personName = commonService.getEntity(Case.class,
                "select c from tsadv$Case c " +
                        "join base$PersonExt t on t.group.id = c.personGroup.id " +
                        "join tsadv$CaseType ct on ct.id = c.caseType.id " +
                        "and ct.language = :language " +
                        "and ct.name = :case " +
                        "where :systemDate between t.startDate and t.endDate " +
                        "and c.deleteTs is null " +
                        "and t.group.id = :personGroupId",
                paramMap,
                "caseJobName");
        return personName;
    }

    protected UserExt getUserExt(UUID personGroupId) {
        LoadContext<UserExt> loadContext = LoadContext.create(UserExt.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$UserExt e " +
                        "where e.personGroup.id = :pgId");
        query.setParameter("pgId", personGroupId);
        loadContext.setQuery(query);
        loadContext.setView("user.browse");
        return dataManager.load(loadContext);
    }

    protected String getLongValueOrFullName(Case personNameEn, PersonGroupExt personGroup) {
        String personFullNameEn = "";
        if (personNameEn != null) {
            if (personNameEn.getLongName() == null) {
                if (personGroup != null) {
                    personFullNameEn = personGroup.getPerson().getFullName();
                }
            } else {
                personFullNameEn = personNameEn.getLongName();
            }
        } else {
            if (personGroup != null) {
                personFullNameEn = personGroup.getPerson().getFullName();
            }
        }
        return personFullNameEn;
    }

    protected Case getCaseJobName(Requisition requisition, String language, String caseName) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("jobGroupId", requisition.getJobGroup().getId());
        paramMap.put("systemDate", CommonUtils.getSystemDate());
        paramMap.put("language", language);
        paramMap.put("case", caseName);

        Case jobName = commonService.getEntity(Case.class,
                "select c from tsadv$Case c " +
                        "join tsadv$Job j on j.group.id = c.jobGroup.id " +
                        "join tsadv$CaseType ct on ct.id = c.caseType.id " +
                        "and ct.language = :language " +
                        "and ct.name = :case " +
                        "where :systemDate between j.startDate and j.endDate " +
                        "and c.jobGroup.id = :jobGroupId " +
                        "and c.deleteTs is null",
                paramMap,
                "caseJobName");
        return jobName;
    }

    protected UserExt getUserExtByLogin(String login) {
        LoadContext<UserExt> loadContext = LoadContext.create(UserExt.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$UserExt e where e.login = :login");
        query.setParameter("login", login);
        loadContext.setQuery(query);
        loadContext.setView("user.browse");
        return dataManager.load(loadContext);
    }

    @Override
    public BaseResult approveInterview(UUID interviewId) {
        BaseResult result = new BaseResult();
        ArrayList<String> errorMessages = new ArrayList<>();

        try {
            List<Entity> commitInstances = new ArrayList<>();
            PersonGroupExt candidatePersonGroup = getUserPersonGroup();

            Interview interview = commonService.getEntity(Interview.class,
                    "select e from tsadv$Interview e where e.id = :interviewId",
                    Collections.singletonMap("interviewId", interviewId),
                    "interview.full");

            JobRequest jobRequest = dataManager.reload(interview.getJobRequest(), "jobRequest.full");

            if (!jobRequest.getCandidatePersonGroup().getId().equals(candidatePersonGroup.getId()))
                errorMessages.add("You cannot approve another's interview!");

            if (!interview.getInterviewStatus().equals(InterviewStatus.ON_APPROVAL))
                errorMessages.add("You cannot approve interview with this status!");

            if (!errorMessages.isEmpty()) {
                result.setSuccess(false);
                result.setErrorMessage(errorMessages.stream().reduce((e1, e2) -> e1.concat(" ").concat(e2)).orElse(""));
            } else {

                interview.setInterviewStatus(InterviewStatus.PLANNED);
                jobRequest.setRequestStatus(JobRequestStatus.INTERVIEW);

                List<UserExt> mainInterviewerUsers = commonService.getEntities(UserExt.class,
                        "select e from tsadv$UserExt e" +
                                " where e.personGroup.id = :mainInterviewerPersonGroupId",
                        Collections.singletonMap("mainInterviewerPersonGroupId", interview.getMainInterviewerPersonGroup().getId()),
                        "user.browse");

                if (mainInterviewerUsers != null && !mainInterviewerUsers.isEmpty()) {
                    UserExt mainInterviewerUser = mainInterviewerUsers.get(0);

                    if (mainInterviewerUser.getEmail() != null || mainInterviewerUser.getMobilePhone() != null) {
                        Map<String, Object> params = new HashMap<>();
                        params.put("user", mainInterviewerUser);
                        params.put("interview", interview);
                        params.put("interviewDate", new SimpleDateFormat("dd.MM.yyyy").format(interview.getInterviewDate()));

                        SimpleDateFormat tf = new SimpleDateFormat("HH:mm");
                        params.put("timeFrom", tf.format(interview.getTimeFrom()));
                        params.put("timeTo", tf.format(interview.getTimeTo()));

                        notificationService.sendParametrizedNotification("interview.planned.mainInterviewer.notification", mainInterviewerUser, params);
                    }
                }

                commitInstances.add(jobRequest);
                commitInstances.add(interview);
                dataManager.commit(new CommitContext(commitInstances));

                result.setSuccess(true);
            }
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
            log.error(e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public BaseResult cancelInterview(UUID interviewId, String reason) {
        BaseResult result = new BaseResult();
        ArrayList<String> errorMessages = new ArrayList<>();

        try {
            List<Entity> commitInstances = new ArrayList<>();
            PersonGroupExt candidatePersonGroup = getUserPersonGroup();

            Interview interview = commonService.getEntity(Interview.class,
                    "select e from tsadv$Interview e where e.id = :interviewId",
                    Collections.singletonMap("interviewId", interviewId),
                    "interview.full");

            JobRequest jobRequest = dataManager.reload(interview.getJobRequest(), "jobRequest.full");

            if (!jobRequest.getCandidatePersonGroup().getId().equals(candidatePersonGroup.getId()))
                errorMessages.add("You cannot cancel another's interview!");

            if (!interview.getInterviewStatus().equals(InterviewStatus.ON_APPROVAL))
                errorMessages.add("You cannot cancel interview with this status!");

            if (reason == null || reason.trim().length() == 0)
                errorMessages.add("Reason is required!");

            if (!errorMessages.isEmpty()) {
                result.setSuccess(false);
                result.setErrorMessage(errorMessages.stream().reduce((e1, e2) -> e1.concat(" ").concat(e2)).orElse(""));
            } else {
                interview.setInterviewStatus(InterviewStatus.CANCELLED);
                interview.setInterviewReason(commonService.getEntity(DicInterviewReason.class, "OTHER"));
                interview.setComment(reason);
//                interview.setReason(reason);

                jobRequest.setRequestStatus(JobRequestStatus.ON_APPROVAL);

                commitInstances.add(jobRequest);
                commitInstances.add(interview);
                dataManager.commit(new CommitContext(commitInstances));

                result.setSuccess(true);
            }
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
            log.error(e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public BaseResult updateUser(UserInt userInt) {
        BaseResult result = new BaseResult();
        ArrayList<String> errorMessages = new ArrayList<>();

        validateUser(errorMessages, userInt);

        if (!errorMessages.isEmpty()) {
            result.setSuccess(false);
            result.setErrorMessage(errorMessages.stream().reduce((e1, e2) -> e1.concat(" ").concat(e2)).orElse(""));
        } else {
            try {
                UserExt user = commonService.getEntity(UserExt.class,
                        "select e from tsadv$UserExt e where e.id = :id",
                        Collections.singletonMap("id", userSessionSource.getUserSession().getUser().getId()),
                        "user.edit");

                Map<String, Object> map = new HashMap<>();
                map.put("email", userInt.getEmail());
                map.put("itemId", user.getId());
                UserExt userExt = commonService.getEntity(UserExt.class,
                        "select e from tsadv$UserExt e where e.email = :email " +
                                "   and e.id <> :itemId",
                        map,
                        "user.edit");
                if (userExt != null) {
                    result.setSuccess(false);
                    result.setErrorMessage("Эл. адрес уже существует");
                    return result;
                }
                user.setEmail(userInt.getEmail());
                user.setMobilePhone(userInt.getPhoneNumber());

                List<Entity> commitInstances = new ArrayList<>();
                commitInstances.add(user);
                dataManager.commit(new CommitContext(commitInstances));

                result.setSuccess(true);
            } catch (Exception e) {
                result.setSuccess(false);
                result.setErrorMessage(e.getMessage());
            }
        }

        return result;
    }

    @Override
    public String getUser(String languageCode) {
        List<UserInt> entityList = new ArrayList<>();
        Map<Integer, Object> queryParams = new HashMap<>();
        queryParams.put(1, userSessionSource.getUserSession().getUser().getId());

        String query = "select u.id as id," +
                "              u.email," +
                "              u.mobile_phone" +
                "         from sec_user u " +
                "        where u.id = ?1 " +
                "          and u.delete_ts is null";

        List<Object[]> resultList = commonService.emNativeQueryResultList(query, queryParams);

        if (resultList != null)
            for (Object[] row : resultList) {
                UserInt ui = new UserInt();
                int i = 0;
                ui.setId((UUID) row[i++]);
                ui.setEmail((String) row[i++]);
                ui.setPhoneNumber((String) row[i]);

                entityList.add(ui);
            }
        return jsonListToSingleObject(toJsonList(entityList));
    }

    @Override
    public BaseResult changePassword(String oldPassword, String newPassword) {
        BaseResult result = new BaseResult();
        ArrayList<String> errorMessages = new ArrayList<>();

        if (oldPassword == null || oldPassword.length() == 0)
            errorMessages.add("Old password is required!");
        if (newPassword == null || newPassword.length() == 0)
            errorMessages.add("New password is required!");

        try {
            UserExt user = commonService.getEntity(UserExt.class,
                    "select e from tsadv$UserExt e where e.id = :id",
                    Collections.singletonMap("id", userSessionSource.getUserSession().getUser().getId()),
                    "user.edit");

            if (!user.getPassword().equals(passwordEncryption.getPasswordHash(user.getId(), oldPassword)))
                errorMessages.add("Old password is not correct!");
            if (!errorMessages.isEmpty()) {
                result.setSuccess(false);
                result.setErrorMessage(errorMessages.stream().reduce((e1, e2) -> e1.concat(" ").concat(e2)).orElse(""));
            } else {
                user.setPassword(passwordEncryption.getPasswordHash(user.getId(), newPassword));

                List<Entity> commitInstances = new ArrayList<>();
                commitInstances.add(user);
                dataManager.commit(new CommitContext(commitInstances));

                result.setSuccess(true);
            }
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
        }

        return result;
    }

    @Override
    public String getOffers(String languageCode) {
        List<OfferInt> entityList = new ArrayList<>();
        PersonGroupExt personGroup = getUserPersonGroup();
        if (personGroup == null) {
            return null;
        }
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("personGroupId", personGroup.getId());
        queryParams.put("APPROVED", OfferStatus.APPROVED.getId());
        queryParams.put("ACCEPTED", OfferStatus.ACCEPTED.getId());
        queryParams.put("REJECTED", OfferStatus.REJECTED.getId());
        List<Offer> offerList = commonService.getEntities(Offer.class,
                "select e from tsadv$Offer e" +
                        " where e.jobRequest.candidatePersonGroup.id = :personGroupId" +
                        " and (e.status = :APPROVED " +
                        " or e.status = :ACCEPTED " +
                        " or e.status = :REJECTED) ",
                queryParams, "offer.rest");
        for (Offer offer : offerList) {
            OfferInt offerInt = new OfferInt();
            offerInt.setId(offer.getId());
            offerInt.setRequisition(offer.getJobRequest().getRequisition().getId().toString());
            offerInt.setRequisitionCode(offer.getJobRequest().getRequisition().getCode());
            offerInt.setProposedSalary(offer.getProposedSalary() != null ? offer.getProposedSalary().toString() : null);
            offerInt.setExpireDate(offer.getExpireDate() != null ? new SimpleDateFormat("yyyy-MM-dd").format(offer.getExpireDate()) : null);
            offerInt.setFile(offer.getFile() != null ? offer.getFile().getId().toString() : null);
            offerInt.setProposedStartDate(offer.getProposedStartDate() != null ? new SimpleDateFormat("yyyy-MM-dd").format(offer.getProposedStartDate()) : null);
            offerInt.setStatus(offer.getStatus().getId().toString());
            offerInt.setFileName(offer.getFile() != null ? offer.getFile().getName() : null);
            if (languageCode != null) {
                switch (languageCode.toUpperCase()) {
                    case "RU": {
                        offerInt.setJobName(offer.getJobRequest().getRequisition().getJobGroup().getJob().getJobNameLang1());
                        break;
                    }
                    case "KZ": {
                        offerInt.setJobName(offer.getJobRequest().getRequisition().getJobGroup().getJob().getJobNameLang2());
                        break;
                    }
                    case "EN": {
                        offerInt.setJobName(offer.getJobRequest().getRequisition().getJobGroup().getJob().getJobNameLang3());
                        break;
                    }
                    default: {
                        offerInt.setJobName(offer.getJobRequest().getRequisition().getJobGroup().getJob().getJobName());
                        break;
                    }
                }
            } else {
                offerInt.setJobName(offer.getJobRequest().getRequisition().getJobGroup().getJob().getJobName());
            }
            entityList.add(offerInt);
        }
        return toJsonList(entityList);
    }

    /*public BaseResult approveUserOffer(UUID offer) {
        BaseResult result = new BaseResult();
        ArrayList<String> errorMessages = new ArrayList<>();
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("offerId", offer);
        Offer offerEntity = commonService.getEntity(Offer.class, "select e from tsadv$Offer e where e.id = :offerId",
                queryParams,
                "offer.rest");
        PersonGroupExt personGroup = getUserPersonGroup();
        if (personGroup == null) {
            errorMessages.add("user hasn't relation with person!");
            result.setSuccess(false);
            result.setErrorMessage(errorMessages.stream().reduce((e1, e2) -> e1.concat(" ").concat(e2)).orElse(""));
            return result;
        }
        if (offerEntity == null) {
            errorMessages.add("offer with this id is does not exist!");
            result.setSuccess(false);
            result.setErrorMessage(errorMessages.stream().reduce((e1, e2) -> e1.concat(" ").concat(e2)).orElse(""));
            return result;
        }
        if (!OfferStatus.APPROVED.equals(offerEntity.getStatus())) {
            errorMessages.add("offer status is not approved");
        }
        if (!personGroup.equals(offerEntity.getJobRequest().getCandidatePersonGroup())) {
            errorMessages.add("offer status is not approved");
        }
        if (!errorMessages.isEmpty()) {
            result.setSuccess(false);
            result.setErrorMessage(errorMessages.stream().reduce((e1, e2) -> e1.concat(" ").concat(e2)).orElse(""));
            return result;
        }
        if (personGroup != null && offerEntity != null) {
            List<ProcTask> procTaskList = commonService.getEntities(ProcTask.class,
                    "select e from bpm$ProcTask e, bpm$ProcInstance pi " +
                            "where pi.entity.entityId = :offerId " +
                            "and e.procInstance.id = pi.id " +
                            "order by e.createTs",
                    ParamsMap.of("offerId", offerEntity.getId()),
                    "procTask.viewForNotification");
            Map<String, Object> paramsMap = new HashMap<>();
            procTaskList.forEach(procTask -> {
                if (procTask.getProcActor().getUser() != null) {
                    paramsMap.put("fioPerson", personGroup.getFullName());
                    UserExt userExt = (UserExt) procTask.getProcActor().getUser();
                    notificationService.sendParametrizedNotification("offer.accepted.notification",
                            userExt,
                            paramsMap);
                }
            });
        }
        offerEntity.setStatus(OfferStatus.ACCEPTED);
        dataManager.commit(offerEntity);
        result.setSuccess(true);
        result.setErrorMessage(null);
        return result;
    }*/

    /*public BaseResult rejectUserOffer(OfferInt offerInt) {
        BaseResult result = new BaseResult();
        ArrayList<String> errorMessages = new ArrayList<>();
        PersonGroupExt personGroup = getUserPersonGroup();
        if (personGroup == null) {
            errorMessages.add("user hasn't relation with person!");
            result.setSuccess(false);
            result.setErrorMessage(errorMessages.stream().reduce((e1, e2) -> e1.concat(" ").concat(e2)).orElse(""));
            return result;
        }
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("offerId", offerInt.getOfferId());
        Offer offerEntity = commonService.getEntity(Offer.class, "select e from tsadv$Offer e where e.id = :offerId",
                queryParams,
                "offer.rest");
        if (offerEntity == null) {
            errorMessages.add("offer with this id is does not exist!");
            result.setSuccess(false);
            result.setErrorMessage(errorMessages.stream().reduce((e1, e2) -> e1.concat(" ").concat(e2)).orElse(""));
            return result;
        }
        if (!OfferStatus.APPROVED.equals(offerEntity.getStatus())) {
            errorMessages.add("offer status is not approved");
        }
        if (!personGroup.equals(offerEntity.getJobRequest().getCandidatePersonGroup())) {
            errorMessages.add("offer status is not approved");
        }
        if (!errorMessages.isEmpty()) {
            result.setSuccess(false);
            result.setErrorMessage(errorMessages.stream().reduce((e1, e2) -> e1.concat(" ").concat(e2)).orElse(""));
            return result;
        }
        if (personGroup != null && offerEntity != null) {
            List<ProcTask> procTaskList = commonService.getEntities(ProcTask.class,
                    "select e from bpm$ProcTask e, bpm$ProcInstance pi " +
                            "where pi.entity.entityId = :offerId " +
                            "and e.procInstance.id = pi.id " +
                            "order by e.createTs",
                    ParamsMap.of("offerId", offerEntity.getId()),
                    "procTask.viewForNotification");
            Map<String, Object> paramsMap = new HashMap<>();
            procTaskList.forEach(procTask -> {
                if (procTask.getProcActor().getUser() != null) {
                    paramsMap.put("fioPerson", personGroup.getFullName());
                    UserExt userExt = (UserExt) procTask.getProcActor().getUser();
                    notificationService.sendParametrizedNotification("offer.rejected.notification",
                            userExt,
                            paramsMap);
                }
            });
        }
        offerEntity.setStatus(OfferStatus.REJECTED);
        offerEntity.setCandidateCommentary(offerInt.getComment());
        dataManager.commit(offerEntity);
        result.setSuccess(true);
        result.setErrorMessage(null);
        return result;
    }*/

    @Override
    public BaseResult updatePerson(PersonInt personInt) {
        BaseResult result = new BaseResult();
        ArrayList<String> errorMessages = new ArrayList<>();
        validatePerson(errorMessages, personInt);

        if (!errorMessages.isEmpty()) {
            result.setSuccess(false);
            result.setErrorMessage(errorMessages.stream().reduce((e1, e2) -> e1.concat(" ").concat(e2)).orElse(""));
        } else {
            try {
                Map<String, Object> personParams = new HashMap<>();
                personParams.put("personGroupId", getUserPersonGroup().getId());
                personParams.put("systemDate", CommonUtils.getSystemDate());

                PersonExt person = commonService.getEntity(PersonExt.class,
                        "select e from base$PersonExt e where e.group.id = :personGroupId and :systemDate between e.startDate and e.endDate",
                        personParams,
                        "person.full");
                if (isEmployeeWithPersonGroupId()) {
                    result.setErrorMessage("You can't change data");
                    result.setSuccess(false);
                    return result;
                }
                person.setFirstName(personInt.getFirstName());
                person.setLastName(personInt.getLastName());
                person.setMiddleName(personInt.getMiddleName());
                person.setDateOfBirth(df.parse(personInt.getBirthDate()));
                person.setNationalIdentifier(personInt.getNationalIdentifier());

                person.setSex(commonService.getEntity(DicSex.class, personInt.getSex()));

                if (personInt.getNationality() != null)
                    person.setNationality(commonService.getEntity(DicNationality.class, personInt.getNationality()));
                else
                    person.setNationality(null);
                if (personInt.getCitizenship() != null)
                    person.setCitizenship(commonService.getEntity(DicCitizenship.class, personInt.getCitizenship()));
                else
                    person.setCitizenship(null);
                if (personInt.getMaritalStatus() != null)
                    person.setMaritalStatus(commonService.getEntity(DicMaritalStatus.class, personInt.getMaritalStatus()));
                else
                    person.setMaritalStatus(null);

                List<Entity> commitInstances = new ArrayList<>();
                if (personInt.getCityName() != null && !personInt.getCityName().equals("")) {
                    List<Address> addressList = commonService.getEntities(Address.class,
                            "select e from tsadv$Address e " +
                                    " where e.personGroup.id = :personGroupId " +
                                    " and :systemDate between e.startDate and e.endDate " +
                                    " and e.addressType.code = 'RESIDENCE' ",
                            ParamsMap.of("personGroupId", person.getId(),
                                    "systemDate", CommonUtils.getSystemDate()), View.LOCAL);
                    if (!addressList.isEmpty()) {
                        for (Address address : addressList) {
                            if (address.getCityName() != null && !address.getCityName().equals(personInt.getCityName())) {
                                address.setCityName(personInt.getCityName());
                                address.setAddress(personInt.getCityName());
                                commitInstances.add(address);
                            }
                        }
                    } else {
                        commitInstances.add(registerAddress(result, personInt.getCityName(), person.getGroup()));
                    }
                }
                commitInstances.add(person);
                dataManager.commit(new CommitContext(commitInstances));

                result.setSuccess(true);
                result.setErrorMessage(null);
            } catch (Exception e) {
                result.setSuccess(false);
                result.setErrorMessage(e.getMessage());
            }
        }
        return result;
    }

    @Override
    public Boolean isEmployeeWithPersonGroupId() {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Query query = em.createNativeQuery("select count(*) > 0\n" +
                    "from base_person p\n" +
                    "join tsadv_dic_person_type pt on p.type_id = pt.id\n" +
                    "and pt.code='EMPLOYEE'\n" +
                    "and pt.delete_ts is null\n" +
                    "where current_date between p.start_date and p.end_date\n" +
                    "and p.delete_ts is null\n" +
                    "and p.group_id = ?1;\n");
            query.setParameter(1, getUserPersonGroup().getId());
            boolean result = (boolean) query.getFirstResult();
            return result;
        }
    }

    /**
     * Создаёт контактную информацию <br>
     * <br><p>
     * Метод: POST <br>
     * Авторизация: OAuth-токен должен быть передан в заголовке запроса Authorization с типом Bearer. <br>
     * Передача параметров: В теле запроса в формате JSON (BODY) <br>
     *
     * @param contactInt Контактная информация: <br>
     *                - contactType (UUID) Id типа контактной информации <br>
     *                - contactTypeName (String) Тип контактной информации <br>
     *                - contactValue (String) Адрес/Номер <br>
     * @return Результат выполнения операции в формате JSON <br>
     * - success (boolean) Успешно ли (true / false) <br>
     * - successMessage (String) Сообщение при успехе <br>
     * - errorMessage (String) Сообщение при ошибке <br>
     */
    @Override
    public BaseResult createContact(PersonContactInt contactInt) {
        BaseResult result = new BaseResult();
        ArrayList<String> errorMessages = new ArrayList<>();
        validateContact(errorMessages, contactInt);

        if (!errorMessages.isEmpty()) {
            result.setSuccess(false);
            result.setErrorMessage(errorMessages.stream().reduce((e1, e2) -> e1.concat(" ").concat(e2)).orElse(""));
        } else {
            try {
                PersonContact contact = metadata.create(PersonContact.class);
                contact.setPersonGroup(getUserPersonGroup());
                contact.setContactValue(contactInt.getContactValue());
                contact.setType(commonService.getEntity(DicPhoneType.class, contactInt.getContactType()));
                contact.setStartDate(CommonUtils.getSystemDate());
                contact.setEndDate(CommonUtils.getEndOfTime());
                List<Entity> commitInstances = new ArrayList<>();
                commitInstances.add(contact);
                dataManager.commit(new CommitContext(commitInstances));

                result.setSuccess(true);
                result.setErrorMessage(null);
            } catch (Exception e) {
                result.setSuccess(false);
                result.setErrorMessage(e.getMessage());
            }
        }
        return result;
    }

    /**
     * Добавляет Для текущего пользователя Адрес созданный по заданному городу <br>
     *
     * @param addressInt Id Города <br>
     * @return Результат выполнения операции в формате JSON <br>
     * - success (boolean) Успешно ли (true / false) <br>
     * - successMessage (String) Сообщение при успехе <br>
     * - errorMessage (String) Сообщение при ошибке <br>
     */
    @Override
    public BaseResult addAddress(UUID addressInt) {
        BaseResult result = new BaseResult();
        ArrayList<String> errorMessages = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();

        map.put("cityId", addressInt);
        String query = "SELECT e " +
                "       FROM base$DicCity e where e.id = :cityId";
        DicCity city = commonService.getEntity(DicCity.class, query, map, "dicCity.edit");
        if (city == null)
            errorMessages.add("select return null");
        if (!errorMessages.isEmpty()) {
            result.setSuccess(true);
            result.setErrorMessage(errorMessages.stream().reduce((e1, e2) -> e1.concat(" ").concat(e2)).orElse(""));
        } else {
            try {
                Address address = metadata.create(Address.class);
                address.setPersonGroup(getUserPersonGroup());
                address.setAddressType(commonService.getEntity(DicAddressType.class, "RESIDENCE"));
                address.setAddress(city.getLangValue());
                address.setCountry(city.getCountry());
                address.setPostalCode(null);
                address.setCityName(city.getLangValue());
                address.setStartDate(CommonUtils.getSystemDate());
                address.setEndDate(new SimpleDateFormat("dd.MM.yyyy").parse("31.12.9999"));

                dataManager.commit(address);

                result.setSuccess(true);
                result.setErrorMessage(null);
            } catch (Exception e) {
                result.setSuccess(false);
                result.setErrorMessage(e.getMessage());
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public String getAddress(String languageCode) {
        List<PersonAddressInt> entitylist = new ArrayList<>();
        Map<Integer, Object> map = new HashMap<>();
        map.put(1, languageCode);
        String query = "with params as (select upper(?1) as lang)" +
                "       select e.id, e.address, e.postal_code, e.city, e.start_date, e.end_date, e.address_type_id " +
                "       from TSADV_ADDRESS e " +
                "       cross join params par " +
                "       where e.delete_ts is null";
        List<Object[]> objectList = commonService.emNativeQueryResultList(query, map);
        if (objectList != null) {
            for (Object[] row : objectList) {
                PersonAddressInt pi = new PersonAddressInt();
                int i = 0;
                pi.setCity((UUID) row[i++]);
                pi.setAddress((String) row[i++]);
                pi.setPostalCode((String) row[i++]);
                pi.setCityName((String) row[i++]);
                pi.setStartDate((Date) row[i++]);
                pi.setEndDate((Date) row[i++]);
                pi.setAddressType((UUID) row[i++]);
                entitylist.add(pi);
            }
        }
        return toJsonList(entitylist);
    }

    @Override
    public String getAddressType(String languageCode) {
        Map<Integer, Object> queryParams = new HashMap<>();
        queryParams.put(1, languageCode);
        String query = "with params as (select upper(?1) as lang)" +
                "      select c.code," +
                "      case par.lang when 'RU' then c.lang_value1  when 'KZ' then c.lang_value2 when 'EN' then c.lang_value3 end as name," +
                "      c.id " +
                "      from TSADV_DIC_ADDRESS_TYPE c cross join params par";

        return getDictionaryList(query, queryParams);
    }

    @Override
    public BaseResult deleteAddress(UUID id) {
        BaseResult result = new BaseResult();
        try {
            Address address = commonService.getEntity(Address.class, id);
            dataManager.remove(address);
            result.setSuccess(true);
            result.setSuccessMessage("Address deleted successfully");
            result.setErrorMessage(null);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
            result.setSuccessMessage(null);
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public BaseResult updateAddress(PersonAddressInt addressInt) {
        BaseResult result = new BaseResult();
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> map2 = new HashMap<>();

        map.put("addressId", addressInt.getId());
        map2.put("cityId", addressInt.getCity());

        ArrayList<String> errorMessages = new ArrayList<>();
        validateAddress(errorMessages, addressInt);

        if (!errorMessages.isEmpty()) {
            result.setSuccess(false);
            result.setErrorMessage(errorMessages.stream().reduce((e1, e2) -> e1.concat(" ").concat(e2)).orElse(""));
        } else {
            try {
                DicCity city = commonService.getEntity(DicCity.class, "select e from base$DicCity e where e.id = :cityId", map2, "dicCity.edit");
                Address address = commonService.getEntity(Address.class, "select e from tsadv$Address e where e.id =:addressId", map, "address.view");

                address.setAddress(city.getLangValue());
                address.setCountry(city.getCountry());
                address.setAddressType(commonService.getEntity(DicAddressType.class, "RESIDENCE"));
                address.setPostalCode(null);
                address.setCityName(city.getLangValue());
                address.setStartDate(CommonUtils.getSystemDate());
                address.setEndDate(new SimpleDateFormat("dd.MM.yyyy").parse("31.12.9999"));

                dataManager.commit(address);

                result.setSuccess(true);
                result.setErrorMessage(null);
            } catch (Exception e) {
                result.setSuccess(false);
                result.setErrorMessage(e.getMessage());
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public BaseResult checkVideoInterview(CandidateInt candidateInt) {
        Map<String, Object> map = new HashMap<>();
        BaseResult result = new BaseResult();
        ArrayList<String> errorMessages = new ArrayList<>();

        if (!errorMessages.isEmpty()) {
            result.setSuccess(false);
            result.setErrorMessage(errorMessages.stream().reduce((e1, e2) -> e1.concat(" ").concat(e2)).orElse(""));
        } else {
            try {
                PersonGroup personGroup = getUserPersonGroup();
                map.put("requisitionId", candidateInt.getRequisitionId());
                Requisition requisition = commonService.getEntity(Requisition.class,
                        "select e from tsadv$Requisition e " +
                                " where e.id = :requisitionId " +
                                " and e.videoInterviewRequired = TRUE ",
                        map,
                        "requisition.view");
                if (requisition != null) {
                    map.put("personGroupId", personGroup.getId());
                    JobRequest jobRequest = commonService.getEntity(JobRequest.class,
                            "select e from tsadv$JobRequest e " +
                                    "where e.requisition.id = :requisitionId " +
                                    "and e.candidatePersonGroup.id = :personGroupId " +
                                    "and e.videoFile is not null",
                            map,
                            "jobRequest.view");
                    if (jobRequest != null) {
                        result.setSuccess(true);
                        result.setSuccessMessage("true");
                        result.setErrorMessage(null);
                    } else {
                        result.setSuccess(false);
                        result.setSuccessMessage("false");
                        result.setErrorMessage("отсутствует видео интервью");
                    }
                } else {
                    result.setSuccess(true);
                    result.setSuccessMessage("true");
                    result.setErrorMessage("Видео интервью не требуется");
                }
            } catch (Exception e) {
                e.printStackTrace();
                result.setSuccess(false);
                result.setErrorMessage(e.getMessage());
            }
        }
        return result;
    }

    @Override
    public BaseResult checkPrescreeningTest(CandidateInt candidateInt) {
        Map<String, Object> map = new HashMap<>();
        BaseResult result = new BaseResult();
        ArrayList<String> errors = new ArrayList<>();
        if (!errors.isEmpty()) {
            result.setSuccess(false);
            result.setErrorMessage(errors.stream().reduce((e1, e2) -> e1.concat(" ").concat(e2)).orElse(""));
        } else {
            try {
                PersonGroup personGroup = getUserPersonGroup();
                map.put("requisitionId", candidateInt.getRequisitionId());
                map.put("codePre", "PRE_SCREEN_TEST");

                List<Requisition> requisitionList = commonService.getEntities(Requisition.class,
                        "select distinct r from tsadv$Requisition r, IN(r.hiringSteps ) hr, IN(r.questionnaires ) q where q.questionnaire.category.code = :codePre " +
                                "       and r.id = :requisitionId ",
                        map,
                        "requisition.view");
                if (requisitionList.size() > 0 || !requisitionList.isEmpty()) {
                    map.put("personGroupId", personGroup.getId());
                    List<Interview> jobRequest = commonService.getEntities(Interview.class,
                            "  select  ui from tsadv$Interview ui, tsadv$JobRequest j, IN(ui.questionnaires ) q where q.questionnaire.category.code = :codePre " +
                                    "       and ui.jobRequest.id = j.id " +
                                    "       and j.requisition.id = :requisitionId " +
                                    "       and j.candidatePersonGroup.id = :personGroupId ",
                            map,
                            "interview.view");
                    if (jobRequest.size() > 0 || !jobRequest.isEmpty()) {
                        result.setSuccess(true);
                        result.setSuccessMessage("true");
                        result.setErrorMessage(null);
                    } else {
                        result.setSuccess(false);
                        result.setSuccessMessage("false");
                        result.setErrorMessage("у кандидата нет интервью с этапом прескриниг");
                    }
                } else {
                    result.setSuccess(true);
                    result.setSuccessMessage("true");
                    result.setErrorMessage("на вакансии нет этапа прескриниг");
                }
            } catch (Exception e) {
                e.printStackTrace();
                result.setErrorMessage(e.getMessage());
                result.setSuccess(false);
            }
        }
        return result;
    }

    @Override
    public BaseResult createPersonDocument(PersonDocumentInt personDocumentInt) {
        BaseResult result = new BaseResult();
        try {
            PersonDocument personDocument = metadata.create(PersonDocument.class);
            personDocument.setIssueDate(df.parse(personDocumentInt.getIssueDate()));
            personDocument.setExpiredDate(df.parse(personDocumentInt.getExpiredDate()));
            personDocument.setIssuedBy(personDocumentInt.getIssuedBy());
            personDocument.setDescription(personDocumentInt.getDescription());
            personDocument.setDocumentType(commonService.getEntity(DicDocumentType.class,
                    "select e from tsadv$DicDocumentType e where e.legacyId = :legacyId",
                    ParamsMap.of("legacyId", personDocumentInt.getDocumentType()),
                    "_minimal")); //TODO: реализовать когда будет понятно в каком виде передают тип докумената
            personDocument.setPersonGroup(commonService.getEntity(PersonGroupExt.class,
                    "select e.group from base$PersonExt e where e.legacyId = :legacyId",
                    ParamsMap.of("legacyId", personDocumentInt.getPersonLegacyId()),
                    "_minimal")); //TODO: реализовать когда будет понятно в каком виде передают тип докумената
            personDocument.setDocumentNumber(personDocumentInt.getDocumentNumber());
            personDocument.setStatus(commonService.getEntity(DicApprovalStatus.class, "APPROVED"));//TODO: реализовать когда будет понятно в каком виде передают тип докумената
            personDocument.setFile(null);//TODO: реализовать когда будет понятно в каком виде передают тип докумената
            dataManager.commit(personDocument);
            result.setSuccess(true);
            result.setErrorMessage(null);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public BaseResult createAgreement(AgreementInt agreementInt) {
        BaseResult result = new BaseResult();
        try {
            Agreement agreement = metadata.create(Agreement.class);
            agreement.setAgreementNumber(agreementInt.getAgreementNumber());
            agreement.setAgreementType(commonService.getEntity(DicContractsType.class,
                    "select e from tsadv$DicContractsType e where e.legacyId = :legacyId",
                    ParamsMap.of("legacyId", agreementInt.getAgreementType()),
                    "_minimal"));
            agreement.setDateFrom(df.parse(agreementInt.getDateFrom()));
            agreement.setDateTo(df.parse(agreementInt.getDateTo()));
            agreement.setStatus(commonService.getEntity(DicAgreementStatus.class,
                    "select e from tsadv$DicAgreementStatus e where e.legacyId = :legacyId",
                    ParamsMap.of("legacyId", agreementInt.getStatus()),
                    "_minimal"));
            agreement.setPersonGroup(commonService.getEntity(PersonGroupExt.class,
                    "select e.group from base$PersonExt e where e.legacyId = :legacyId",
                    ParamsMap.of("legacyId", agreementInt.getPersonGroup()),
                    "_minimal"));
            dataManager.commit(agreement);
            result.setSuccess(true);
            result.setErrorMessage(null);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public BaseResult cancelJobRequest(UUID jobRequestId) {
        BaseResult result = new BaseResult();
        try {
            if (jobRequestId == null) {
                result.setErrorMessage("Job request id is null");
                result.setSuccess(false);
                return result;
            }
            JobRequest jobRequest = commonService.getEntity(JobRequest.class,
                    "select e from tsadv$JobRequest e where e.id = :jobRequestId",
                    ParamsMap.of("jobRequestId", jobRequestId),
                    "jobRequest.view");
            if (jobRequest == null) {
                result.setErrorMessage("Job request not found");
                result.setSuccess(false);
                return result;
            }
            if (JobRequestStatus.REJECTED.equals(jobRequest.getRequestStatus())) {
                result.setSuccess(false);
                result.setErrorMessage("Job request already rejected");
                return result;
            }
            if (jobRequest.getInterviews() != null && jobRequest.getInterviews().size() > 0) {
                result.setErrorMessage("Job request has interview");
                result.setSuccess(false);
                return result;
            }
            jobRequest.setRequestStatus(JobRequestStatus.REJECTED);
            RecruitmentConfig recruitmentConfig = configuration.getConfig(RecruitmentConfig.class);
            DicJobRequestReason dicJobRequestReason = commonService.getEntity(DicJobRequestReason.class, recruitmentConfig.getDefaultReasonCode());
            if (dicJobRequestReason != null) {
                jobRequest.setJobRequestReason(dicJobRequestReason);
            } else {
                dicJobRequestReason = metadata.create(DicJobRequestReason.class);
                dicJobRequestReason.setCode(recruitmentConfig.getDefaultReasonCode());
                dicJobRequestReason.setLangValue1(recruitmentConfig.getDefaultReasonLangValue1());
                dicJobRequestReason.setLangValue2(recruitmentConfig.getDefaultReasonLangValue2());
                dicJobRequestReason.setLangValue3(recruitmentConfig.getDefaultReasonLangValue3());
                dicJobRequestReason.setStartDate(new Date());
                dicJobRequestReason.setEndDate(CommonUtils.getEndOfTime());
                dicJobRequestReason.setActive(true);
                dicJobRequestReason = dataManager.commit(dicJobRequestReason);
            }
            jobRequest.setJobRequestReason(dicJobRequestReason);
            dataManager.commit(jobRequest);
            result.setSuccess(true);
            return result;
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
            return result;
        }
    }

    @Override
    public BaseResult updateContact(PersonContactInt contactInt, UUID id) {
        BaseResult result = new BaseResult();
        ArrayList<String> errorMessages = new ArrayList<>();
        validateContact(errorMessages, contactInt);

        if (!errorMessages.isEmpty()) {
            result.setSuccess(false);
            result.setErrorMessage(errorMessages.stream().reduce((e1, e2) -> e1.concat(" ").concat(e2)).orElse(""));
        } else {
            try {
                if (isEmployeeWithPersonGroupId()) {
                    result.setErrorMessage("You can't change data");
                    result.setSuccess(false);
                    return result;
                }
                PersonContact contact = commonService.getEntity(PersonContact.class,
                        "select e from tsadv$PersonContact e where e.id =:id",
                        Collections.singletonMap("id", id),
                        "personContact.full");
                contact.setContactValue(contactInt.getContactValue());
                contact.setType(commonService.getEntity(DicPhoneType.class, contactInt.getContactType()));

                List<Entity> commitInstances = new ArrayList<>();
                commitInstances.add(contact);
                dataManager.commit(new CommitContext(commitInstances));

                result.setSuccess(true);
                result.setErrorMessage(null);
            } catch (Exception e) {
                result.setSuccess(false);
                result.setErrorMessage(e.getMessage());
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public BaseResult deleteContact(UUID id) {
        BaseResult result = new BaseResult();
        try {
            if (isEmployeeWithPersonGroupId()) {
                result.setErrorMessage("You can't change data");
                result.setSuccess(false);
                return result;
            }

            PersonContact contact = commonService.getEntity(PersonContact.class, id);
            dataManager.remove(contact);
            result.setSuccess(true);
            result.setErrorMessage(null);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
        }
        return result;
    }

    /**
     * Создаёт Сведения об образовании <br>
     * <br><p>
     * Метод: POST <br>
     * Авторизация: OAuth-токен должен быть передан в заголовке запроса Authorization с типом Bearer. <br>
     * Передача параметров: В теле запроса в формате JSON (BODY) <br>
     *
     * @param educationInt Образование: <br>
     *                     - school (string) Наименование Учебного Заведения <br>
     *                     - startYear (integer) Год начала обучения <br>
     *                     - endYear (integer) Год окончания обучения <br>
     *                     - specialization (string) Специальность <br>
     *                     - level (uuid) Id уровня образования <br>
     *                     - levelName (string) Уровень образования <br>
     *                     - degree (uuid) Id Степени <br>
     *                     - degreeName (string) Степень <br>
     *                     - location (string) Адрес учебного заведения <br>
     *                     - bolashak (boolean) Участник программы Болашак (true / false) <br>
     * @return Результат выполнения операции в формате JSON <br>
     * - success (boolean) Успешно ли (true / false) <br>
     * - successMessage (String) Сообщение при успехе <br>
     * - errorMessage (String) Сообщение при ошибке <br>
     */
    @Override
    public BaseResult createEducation(PersonEducationInt educationInt) {
        BaseResult result = new BaseResult();
        ArrayList<String> errorMessages = new ArrayList<>();
        validateEducation(errorMessages, educationInt);

        if (!errorMessages.isEmpty()) {
            result.setSuccess(false);
            result.setErrorMessage(errorMessages.stream().reduce((e1, e2) -> e1.concat(" ").concat(e2)).orElse(""));
        } else {
            try {
                PersonEducation education = metadata.create(PersonEducation.class);
                education.setPersonGroup(getUserPersonGroup());
                education.setSchool(educationInt.getSchool());
                education.setStartYear(educationInt.getStartYear());
                education.setEndYear(educationInt.getEndYear());
                education.setSpecialization(educationInt.getSpecialization());
                education.setLevel(commonService.getEntity(DicEducationLevel.class, educationInt.getLevel()));
                education.setDegree(commonService.getEntity(DicEducationDegree.class, educationInt.getDegree()));
                education.setLocation(educationInt.getLocation());

                List<Entity> commitInstances = new ArrayList<>();
                commitInstances.add(education);
                dataManager.commit(new CommitContext(commitInstances));

                result.setSuccess(true);
                result.setErrorMessage(null);
            } catch (Exception e) {
                result.setSuccess(false);
                result.setErrorMessage(e.getMessage());
            }
        }
        return result;
    }

    @Override
    public BaseResult updateEducation(PersonEducationInt educationInt, UUID id) {
        BaseResult result = new BaseResult();
        ArrayList<String> errorMessages = new ArrayList<>();
        validateEducation(errorMessages, educationInt);

        if (!errorMessages.isEmpty()) {
            result.setSuccess(false);
            result.setErrorMessage(errorMessages.stream().reduce((e1, e2) -> e1.concat(" ").concat(e2)).orElse(""));
        } else {
            try {
                PersonEducation education = commonService.getEntity(PersonEducation.class,
                        "select e from tsadv$PersonEducation e where e.id =:id",
                        Collections.singletonMap("id", id),
                        "personEducation.full");
                education.setSchool(educationInt.getSchool());
                education.setStartYear(educationInt.getStartYear());
                education.setEndYear(educationInt.getEndYear());
                education.setSpecialization(educationInt.getSpecialization());
                education.setLevel(commonService.getEntity(DicEducationLevel.class, educationInt.getLevel()));
                education.setDegree(commonService.getEntity(DicEducationDegree.class, educationInt.getDegree()));
                education.setLocation(educationInt.getLocation());

                List<Entity> commitInstances = new ArrayList<>();
                commitInstances.add(education);
                dataManager.commit(new CommitContext(commitInstances));

                result.setSuccess(true);
                result.setErrorMessage(null);
            } catch (Exception e) {
                result.setSuccess(false);
                result.setErrorMessage(e.getMessage());
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public BaseResult deleteEducation(UUID id) {
        BaseResult result = new BaseResult();
        try {
            PersonEducation education = commonService.getEntity(PersonEducation.class, id);
            dataManager.remove(education);
            result.setSuccess(true);
            result.setErrorMessage(null);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
        }
        return result;
    }

    /**
     * Создаёт Сведения об Опыте работы <br>
     * <br><p>
     * Метод: POST <br>
     * Авторизация: OAuth-токен должен быть передан в заголовке запроса Authorization с типом Bearer. <br>
     * Передача параметров: В теле запроса в формате JSON (BODY) <br>
     *
     * @param experienceInt Опыт работы: <br>
     *                   - company (String) Компания <br>
     *                   - untilNow (Boolean) По настоящее время (true / false) <br>
     *                   - job (String) Должность <br>
     *                   - startMonth (String) Год, Месяц начала работы <br>
     *                   - endMonth (String) Год, Месяц окончания работы <br>
     *                   - description (String) Примечание <br>
     * @return Результат выполнения операции в формате JSON <br>
     * - success (boolean) Успешно ли (true / false) <br>
     * - successMessage (String) Сообщение при успехе <br>
     * - errorMessage (String) Сообщение при ошибке <br>
     */
    @Override
    public BaseResult createExperience(PersonExperienceInt experienceInt) {
        BaseResult result = new BaseResult();
        ArrayList<String> errorMessages = new ArrayList<>();
        validateExperience(errorMessages, experienceInt);

        if (!errorMessages.isEmpty()) {
            result.setSuccess(false);
            result.setErrorMessage(errorMessages.stream().reduce((e1, e2) -> e1.concat(" ").concat(e2)).orElse(""));
        } else {
            try {
                PersonExperience experience = metadata.create(PersonExperience.class);
                experience.setPersonGroup(getUserPersonGroup());
                experience.setCompany(experienceInt.getCompany());
                experience.setJob(experienceInt.getJob());
                experience.setStartMonth(df.parse(mf.format(df.parse(experienceInt.getStartMonth()))));
                experience.setUntilNow(experienceInt.getUntilNow());
                if (experienceInt.getUntilNow() == false) {
                    experience.setEndMonth(df.parse(mf.format(df.parse(experienceInt.getEndMonth()))));
                }
                experience.setDescription(experienceInt.getDescription());

                List<Entity> commitInstances = new ArrayList<>();
                commitInstances.add(experience);
                dataManager.commit(new CommitContext(commitInstances));

                result.setSuccess(true);
                result.setErrorMessage(null);
            } catch (Exception e) {
                result.setSuccess(false);
                result.setErrorMessage(e.getMessage());
            }
        }
        return result;
    }

    @Override
    public BaseResult updateExperience(PersonExperienceInt experienceInt, UUID id) {
        BaseResult result = new BaseResult();
        ArrayList<String> errorMessages = new ArrayList<>();
        validateExperience(errorMessages, experienceInt);

        if (!errorMessages.isEmpty()) {
            result.setSuccess(false);
            result.setErrorMessage(errorMessages.stream().reduce((e1, e2) -> e1.concat(" ").concat(e2)).orElse(""));
        } else {
            try {
                PersonExperience experience = commonService.getEntity(PersonExperience.class,
                        "select e from tsadv$PersonExperience e where e.id =:id",
                        Collections.singletonMap("id", id),
                        "personExperience.full");
                experience.setCompany(experienceInt.getCompany());
                experience.setJob(experienceInt.getJob());
                experience.setStartMonth(df.parse(mf.format(df.parse(experienceInt.getStartMonth()))));
                experience.setUntilNow(experienceInt.getUntilNow());
                if (experienceInt.getUntilNow() == false) {
                    experience.setEndMonth(df.parse(mf.format(df.parse(experienceInt.getEndMonth()))));
                } else if (experienceInt.getUntilNow() == true) {
                    experience.setEndMonth(null);
                }
                experience.setDescription(experienceInt.getDescription());

                List<Entity> commitInstances = new ArrayList<>();
                commitInstances.add(experience);
                dataManager.commit(new CommitContext(commitInstances));

                result.setSuccess(true);
                result.setErrorMessage(null);
            } catch (Exception e) {
                result.setSuccess(false);
                result.setErrorMessage(e.getMessage());
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public BaseResult deleteExperience(UUID id) {
        BaseResult result = new BaseResult();
        try {
            PersonExperience experience = commonService.getEntity(PersonExperience.class, id);
            dataManager.remove(experience);
            result.setSuccess(true);
            result.setErrorMessage(null);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
        }
        return result;
    }

    /**
     * Создаёт Приложение (Вложение)
     * <br><p>
     * Метод: POST <br>
     * Авторизация: OAuth-токен должен быть передан в заголовке запроса Authorization с типом Bearer. <br>
     * Передача параметров: В теле запроса в формате JSON (BODY) <br>
     *
     * @param attachmentInt Приложение (Вложение): <br>
     *                   - category (UUID) Id категории <br>
     *                   - categoryName (String) Категория <br>
     *                   - filename (String) Имя файла <br>
     *                   - file (UUID) Id файла <br>
     *                   - description (String) Примечание <br>
     * @return Результат выполнения операции в формате JSON <br>
     * - success (boolean) Успешно ли (true / false) <br>
     * - successMessage (String) Сообщение при успехе <br>
     * - errorMessage (String) Сообщение при ошибке <br>
     */
    @Override
    public BaseResult createAttachment(PersonAttachmentInt attachmentInt) {
        BaseResult result = new BaseResult();
        ArrayList<String> errorMessages = new ArrayList<>();
        validateAttachment(errorMessages, attachmentInt);

        if (attachmentInt.getFile() == null)
            errorMessages.add("File is required!");
        if (attachmentInt.getFilename() == null || attachmentInt.getFilename().length() == 0)
            errorMessages.add("Filename is required!");

        if (!errorMessages.isEmpty()) {
            result.setSuccess(false);
            result.setErrorMessage(errorMessages.stream().reduce((e1, e2) -> e1.concat(" ").concat(e2)).orElse(""));
        } else {
            try {
                PersonAttachment attachment = metadata.create(PersonAttachment.class);
                attachment.setPersonGroup(getUserPersonGroup());
                attachment.setCategory(commonService.getEntity(DicAttachmentCategory.class, attachmentInt.getCategory()));
                attachment.setFilename(attachmentInt.getFilename());
                attachment.setDescription(attachmentInt.getDescription());
                attachment.setAttachment(commonService.getEntity(FileDescriptor.class, attachmentInt.getFile()));

                List<Entity> commitInstances = new ArrayList<>();
                commitInstances.add(attachment);
                dataManager.commit(new CommitContext(commitInstances));

                result.setSuccess(true);
                result.setErrorMessage(null);
            } catch (Exception e) {
                result.setSuccess(false);
                result.setErrorMessage(e.getMessage());
            }
        }
        return result;
    }

    @Override
    public BaseResult updateAttachment(PersonAttachmentInt attachmentInt, UUID id) {
        BaseResult result = new BaseResult();
        ArrayList<String> errorMessages = new ArrayList<>();
        validateAttachment(errorMessages, attachmentInt);

        if (!errorMessages.isEmpty()) {
            result.setSuccess(false);
            result.setErrorMessage(errorMessages.stream().reduce((e1, e2) -> e1.concat(" ").concat(e2)).orElse(""));
        } else {
            try {
                PersonAttachment attachment = commonService.getEntity(PersonAttachment.class,
                        "select e from tsadv$PersonAttachment e where e.id =:id",
                        Collections.singletonMap("id", id),
                        "personAttachment.full");
                attachment.setCategory(commonService.getEntity(DicAttachmentCategory.class, attachmentInt.getCategory()));
                attachment.setDescription(attachmentInt.getDescription());

                if (attachmentInt.getFile() != null)
                    attachment.setAttachment(commonService.getEntity(FileDescriptor.class, attachmentInt.getFile()));
                if (attachmentInt.getFilename() != null && attachmentInt.getFilename().length() > 0)
                    attachment.setFilename(attachmentInt.getFilename());

                List<Entity> commitInstances = new ArrayList<>();
                commitInstances.add(attachment);
                dataManager.commit(new CommitContext(commitInstances));

                result.setSuccess(true);
                result.setErrorMessage(null);
            } catch (Exception e) {
                result.setSuccess(false);
                result.setErrorMessage(e.getMessage());
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public BaseResult deleteAttachment(UUID id) {
        BaseResult result = new BaseResult();
        try {
            PersonAttachment attachment = commonService.getEntity(PersonAttachment.class, id);
            dataManager.remove(attachment);
            result.setSuccess(true);
            result.setErrorMessage(null);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public BaseResult createCompetence(PersonCompetenceInt competenceInt) {
        BaseResult result = new BaseResult();
        ArrayList<String> errorMessages = new ArrayList<>();
        validateCompetence(errorMessages, competenceInt);

        if (!errorMessages.isEmpty()) {
            result.setSuccess(false);
            result.setErrorMessage(errorMessages.stream().reduce((e1, e2) -> e1.concat(" ").concat(e2)).orElse(""));
        } else {
            try {
                CompetenceElement competenceElement = metadata.create(CompetenceElement.class);
                competenceElement.setPersonGroup(getUserPersonGroup());
                competenceElement.setCompetenceGroup(commonService.getEntity(CompetenceGroup.class, competenceInt.getCompetence()));
                competenceElement.setScaleLevel(commonService.getEntity(ScaleLevel.class, competenceInt.getScaleLevel()));

                List<Entity> commitInstances = new ArrayList<>();
                commitInstances.add(competenceElement);
                dataManager.commit(new CommitContext(commitInstances));

                result.setSuccess(true);
                result.setErrorMessage(null);
            } catch (Exception e) {
                result.setSuccess(false);
                result.setErrorMessage(e.getMessage());
            }
        }
        return result;
    }

    @Override
    public BaseResult updateCompetence(PersonCompetenceInt competenceInt, UUID id) {
        BaseResult result = new BaseResult();
        ArrayList<String> errorMessages = new ArrayList<>();
        validateCompetence(errorMessages, competenceInt);

        if (!errorMessages.isEmpty()) {
            result.setSuccess(false);
            result.setErrorMessage(errorMessages.stream().reduce((e1, e2) -> e1.concat(" ").concat(e2)).orElse(""));
        } else {
            try {
                CompetenceElement competenceElement = commonService.getEntity(CompetenceElement.class,
                        "select e from tsadv$CompetenceElement e where e.id =:id",
                        Collections.singletonMap("id", id),
                        "competenceElement.full");
                competenceElement.setCompetenceGroup(commonService.getEntity(CompetenceGroup.class, competenceInt.getCompetence()));
                competenceElement.setScaleLevel(commonService.getEntity(ScaleLevel.class, competenceInt.getScaleLevel()));

                List<Entity> commitInstances = new ArrayList<>();
                commitInstances.add(competenceElement);
                dataManager.commit(new CommitContext(commitInstances));

                result.setSuccess(true);
                result.setErrorMessage(null);
            } catch (Exception e) {
                result.setSuccess(false);
                result.setErrorMessage(e.getMessage());
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public BaseResult deleteCompetence(UUID id) {
        BaseResult result = new BaseResult();
        try {
            CompetenceElement competence = commonService.getEntity(CompetenceElement.class, id);
            dataManager.remove(competence);
            result.setSuccess(true);
            result.setErrorMessage(null);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
        }
        return result;
    }

    /**
     * Возвращает справочник Источник
     *
     * @param lang Код языка RU/KZ/EN для извлечения названий
     * @return Список сущностей Источник в формате JSON
     */
    @Override
    public String getSources(String lang) {
        Map<Integer, Object> queryParams = new HashMap<>();
        queryParams.put(1, lang);
        String query = "with params as (select upper(?1) as lang)" +
                "                   select d.code as code," +
                "                          case par.lang when 'RU' then d.lang_value1 when 'KZ' then d.lang_value2 when 'EN' then d.lang_value3 end as name," +
                "                          d.id as id " +
                "                     from TSADV_DIC_SOURCE d " +
                "               cross join params par" +
                "                    where d.delete_ts is null";
        return getDictionaryList(query, queryParams);
    }

    @Override
    public String getInterviewStatusEnum(String lang) {
        List<DictionaryInt> entityList = new ArrayList<>();
        DictionaryInt dictionaryInt;
        for (InterviewStatus interviewStatus : InterviewStatus.values()) {
            dictionaryInt = new DictionaryInt();
            dictionaryInt.setCode(interviewStatus.getId().toString());
            dictionaryInt.setName(messages.getMessage(interviewStatus, new Locale(lang)));
            entityList.add(dictionaryInt);
        }
        return toJsonList(entityList);
    }

    @Override
    public String getOfferStatusEnum(String lang) {
        List<DictionaryInt> entityList = new ArrayList<>();
        DictionaryInt dictionaryInt;
        for (OfferStatus offerStatus : OfferStatus.values()) {
            dictionaryInt = new DictionaryInt();
            dictionaryInt.setCode(offerStatus.getId().toString());
            dictionaryInt.setName(messages.getMessage(offerStatus, new Locale(lang)));
            entityList.add(dictionaryInt);
        }
        return toJsonList(entityList);
    }

    @Override
    public String getJobRequestStatusEnum(String lang) {
        List<DictionaryInt> entityList = new ArrayList<>();
        DictionaryInt dictionaryInt;
        for (JobRequestStatus jobRequestStatus : JobRequestStatus.values()) {
            dictionaryInt = new DictionaryInt();
            dictionaryInt.setCode(jobRequestStatus.getId().toString());
            dictionaryInt.setName(messages.getMessage(jobRequestStatus, new Locale(lang)));
            entityList.add(dictionaryInt);
        }
        return toJsonList(entityList);
    }

    protected String getDictionaryList(String query, Map<Integer, Object> queryParams) {
        List<DictionaryInt> entityList = new ArrayList<>();
        List<Object[]> resultList = commonService.emNativeQueryResultList(query, queryParams);
        fillDictionaryIntList(resultList, entityList);
        return toJsonList(entityList);
    }

    protected String getCompetenceList(String query, Map<Integer, Object> queryParams) {
        List<DictionaryInt> entityList = new ArrayList<>();
        List<Object[]> resultList = commonService.emNativeQueryResultList(query, queryParams);
        fillCompetenceIntList(resultList, entityList);
        return toJsonList(entityList);
    }

    @Override
    public BaseResult saveVideoFile(UUID requisitionId, UUID videoFileId) {
        BaseResult result = new BaseResult();
        ArrayList<String> errorMessages = new ArrayList<>();

        PersonGroupExt candidatePersonGroup = getUserPersonGroup();
        FileDescriptor videoFile = commonService.getEntity(FileDescriptor.class, videoFileId);

        Map<String, Object> jrParams = new HashMap<>();
        jrParams.put("requisitionId", requisitionId);
        jrParams.put("candidatePersonGroupId", candidatePersonGroup.getId());

        List<JobRequest> jrList = commonService.getEntities(JobRequest.class,
                "select e " +
                        "    from tsadv$JobRequest e " +
                        "   where e.deleteTs is null " +
                        "     and e.requisition.id = :requisitionId " +
                        "     and e.candidatePersonGroup.id = :candidatePersonGroupId " +
                        "     and e.requestStatus IN (0,1,3,4,6) ",
                jrParams,
                "jobRequest.full");

                /*DRAFT(0)
                ON_APPROVAL(1),
                REJECTED(2),
                INTERVIEW(3),
                MADE_OFFER(4),
                HIRED(5),
                SELECTED(6),
                FROM_RESERVE(7)*/

        if (jrList == null || jrList.isEmpty())
            errorMessages.add("Actual job request for this requisition doesn't exists!");

        if (!errorMessages.isEmpty()) {
            result.setSuccess(false);
            result.setErrorMessage(errorMessages.stream().reduce((e1, e2) -> e1.concat(" ").concat(e2)).orElse(""));
        } else {
            try {
                JobRequest jobRequest = jrList.get(0);
                jobRequest.setVideoFile(videoFile);
                CandidateInt candidateInt = new CandidateInt();
                candidateInt.setRequisitionId(jobRequest.getRequisition().getId());
                candidateInt.setUserExtId(userSessionSource.getUserSession().getId());
                if (checkPrescreeningTest(candidateInt).isSuccess()) {
                    jobRequest.setRequestStatus(JobRequestStatus.ON_APPROVAL);
                    if (jobRequest.getRequestStatus() != JobRequestStatus.DRAFT) {
                        UserExt userExt = employeeService.getUserExtByPersonGroupId(candidatePersonGroup.getId(), "user.browse");
                        notificationService.sendParametrizedNotification(
                                "requisition.notify.application.candidate",
                                userExt,
                                getMapForNotification(candidatePersonGroup, jobRequest.getRequisition()));
                    }
                }

                List<Entity> commitInstances = new ArrayList<>();
                commitInstances.add(jobRequest);
                dataManager.commit(new CommitContext(commitInstances));

                result.setSuccess(true);
                result.setErrorMessage(null);
            } catch (Exception e) {
                result.setSuccess(false);
                result.setErrorMessage(e.getMessage());
            }
        }

        return result;
    }

    protected Address registerAddress(BaseResult result, String cityName, PersonGroupExt personGroup) {
        if (cityName == null) return null;
        Address address = null;
        try {
            address = metadata.create(Address.class);
            address.setPersonGroup(personGroup);
            address.setAddressType(commonService.getEntity(DicAddressType.class, "RESIDENCE"));
            address.setAddress(cityName);
            address.setCountry(commonService.getEntity(DicCountry.class, "398"));
            address.setPostalCode(null);
            address.setCityName(cityName);
            address.setStartDate(CommonUtils.getSystemDate());
            address.setEndDate(new SimpleDateFormat("dd.MM.yyyy").parse("31.12.9999"));

            result.setSuccess(true);
            result.setErrorMessage(null);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
            e.printStackTrace();
        }
        return address;
    }

    protected void fillDictionaryIntList(List<Object[]> resultList, List<DictionaryInt> entityList) {
        if (resultList != null)
            for (Object[] row : resultList) {
                DictionaryInt di = new DictionaryInt();
                di.setCode((String) row[0]);
                di.setName((String) row[1]);
                di.setId((UUID) row[2]);
//                di.setCompetenceTypeCode((String) row[3]);
//                di.setState(EntityIntState.S);
                entityList.add(di);
            }
    }

    protected void fillCompetenceIntList(List<Object[]> resultList, List<DictionaryInt> entityList) {
        if (resultList != null)
            for (Object[] row : resultList) {
                DictionaryInt di = new DictionaryInt();
                di.setCode((String) row[0]);
                di.setName((String) row[1]);
                di.setId((UUID) row[2]);
                di.setCompetenceTypeCode((String) row[3]);
                entityList.add(di);
            }
    }

    protected static String jsonListToSingleObject(String jsonList) {
        return jsonList.substring(1, jsonList.length() - 1);
    }

    protected String toJsonList(Collection<? extends Entity> entityList) {
        return entitySerialization.toJson(entityList, null/*, EntitySerializationOption.PRETTY_PRINT*/);
    }

    protected List<PersonEducationInt> getPersonEducation(UUID personGroupId, String lang) {
        List<PersonEducationInt> entityList = null;

        Map<Integer, Object> queryParams = new HashMap<>();
        queryParams.put(1, lang);
        queryParams.put(2, personGroupId);

        String query = "with params as (select upper(?1) as lang)" +
                "      select pe.id," +
                "             pe.school," +
                "             pe.start_year," +
                "             pe.end_year," +
                "             pe.specialization," +
                "             pe.location," +
                "             pe.degree_id as degree," +
                "             case par.lang when 'RU' then ed.lang_value1 when 'KZ' then ed.lang_value2 when 'EN' then ed.lang_value3 end as degree_name," +
                "             pe.level_id as level," +
                "             case par.lang when 'RU' then el.lang_value1 when 'KZ' then el.lang_value2 when 'EN' then el.lang_value3 end as level_name" +
                "        from tsadv_person_education pe" +
                "  cross join params par" +
                "   left join tsadv_dic_education_degree ed on (ed.id  = pe.degree_id)" +
                "   left join tsadv_dic_education_level el on (el.id  = pe.level_id)" +
                "       where pe.person_group_id = ?2" +
                "         and pe.delete_ts is null";

        List<Object[]> resultList = commonService.emNativeQueryResultList(query, queryParams);

        if (resultList != null) {
            entityList = new ArrayList<>();
            for (Object[] row : resultList) {
                PersonEducationInt pei = new PersonEducationInt();
                int i = 0;
                pei.setId((UUID) row[i++]);
                pei.setSchool((String) row[i++]);
                pei.setStartYear((Integer) row[i++]);
                pei.setEndYear((Integer) row[i++]);
                pei.setSpecialization((String) row[i++]);
                pei.setLocation((String) row[i++]);
                pei.setDegree((UUID) row[i++]);
                pei.setDegreeName((String) row[i++]);
                pei.setLevel((UUID) row[i++]);
                pei.setLevelName((String) row[i]);
                entityList.add(pei);
            }
        }
        return entityList;
    }

    protected List<PersonContactInt> getPersonContacts(UUID personGroupId, String lang) {
        List<PersonContactInt> entityList = null;

        Map<Integer, Object> queryParams = new HashMap<>();
        queryParams.put(1, lang);
        queryParams.put(2, personGroupId);

        String query = "with params as (select upper(?1) as lang)" +
                "      select pc.id," +
                "             pc.type_id as contact_type," +
                "             case par.lang when 'RU' then pt.lang_value1 when 'KZ' then pt.lang_value2 when 'EN' then pt.lang_value3 end as contact_type_name," +
                "             pc.contact_value" +
                "        from tsadv_person_contact pc" +
                "  cross join params par" +
                "   left join tsadv_dic_phone_type pt on (pt.id  = pc.type_id)" +
                "       where pc.person_group_id = ?2" +
                "         and pc.delete_ts is null";

        List<Object[]> resultList = commonService.emNativeQueryResultList(query, queryParams);

        if (resultList != null) {
            entityList = new ArrayList<>();
            for (Object[] row : resultList) {
                PersonContactInt pci = new PersonContactInt();
                int i = 0;
                pci.setId((UUID) row[i++]);
                pci.setContactType((UUID) row[i++]);
                pci.setContactTypeName((String) row[i++]);
                pci.setContactValue((String) row[i]);
                entityList.add(pci);
            }
        }
        return entityList;
    }

    protected List<PersonExperienceInt> getPersonExperience(UUID personGroupId, String lang) {
        List<PersonExperienceInt> entityList = null;

        Map<Integer, Object> queryParams = new HashMap<>();
        queryParams.put(1, lang);
        queryParams.put(2, personGroupId);

        String query = "with params as (select upper(?1) as lang)" +
                "      select pe.id," +
                "             pe.company," +
                "             pe.job," +
                "             to_char(pe.start_month, 'yyyy-MM-dd') as start_month," +
                "             to_char(pe.end_month, 'yyyy-MM-dd') as end_month," +
                "             pe.description," +
                "             pe.until_now" +
                "        from tsadv_person_experience pe" +
                "  cross join params par" +
                "       where pe.person_group_id = ?2" +
                "         and pe.delete_ts is null";

        List<Object[]> resultList = commonService.emNativeQueryResultList(query, queryParams);

        if (resultList != null) {
            entityList = new ArrayList<>();
            for (Object[] row : resultList) {
                PersonExperienceInt pei = new PersonExperienceInt();
                int i = 0;
                pei.setId((UUID) row[i++]);
                pei.setCompany((String) row[i++]);
                pei.setJob((String) row[i++]);
                pei.setStartMonth((String) row[i++]);
                pei.setEndMonth((String) row[i++]);
                pei.setDescription((String) row[i++]);
                pei.setUntilNow((Boolean) row[i]);

                entityList.add(pei);
            }
        }
        return entityList;
    }

    protected List<PersonAttachmentInt> getPersonAttachments(UUID personGroupId, String lang) {
        List<PersonAttachmentInt> entityList = null;

        Map<Integer, Object> queryParams = new HashMap<>();
        queryParams.put(1, lang);
        queryParams.put(2, personGroupId);

        String query = "with params as (select upper(?1) as lang)" +
                "      select pa.id," +
                "             pa.category_id as category," +
                "             case par.lang when 'RU' then ac.lang_value1 when 'KZ' then ac.lang_value2 when 'EN' then ac.lang_value3 end as category_name," +
                "             pa.filename," +
                "             pa.attachment_id as file," +
                "             pa.description" +
                "        from tsadv_person_attachment pa" +
                "  cross join params par" +
                "   left join tsadv_dic_attachment_category ac on (ac.id = pa.category_id)" +
                "       where pa.person_group_id = ?2" +
                "         and pa.delete_ts is null";

        List<Object[]> resultList = commonService.emNativeQueryResultList(query, queryParams);

        if (resultList != null) {
            entityList = new ArrayList<>();
            for (Object[] row : resultList) {
                PersonAttachmentInt pai = new PersonAttachmentInt();
                int i = 0;
                pai.setId((UUID) row[i++]);
                pai.setCategory((UUID) row[i++]);
                pai.setCategoryName((String) row[i++]);
                pai.setFilename((String) row[i++]);
                pai.setFile((UUID) row[i++]);
                pai.setDescription((String) row[i]);

                entityList.add(pai);
            }
        }
        return entityList;
    }

    protected List<PersonCompetenceInt> getPersonCompetences(UUID personGroupId, String lang) {
        List<PersonCompetenceInt> entityList = null;

        languageIndex = languageIndex(lang);
        Map<Integer, Object> queryParams = new HashMap<>();
        queryParams.put(1, lang);
        queryParams.put(2, personGroupId);
        queryParams.put(3, CommonUtils.getSystemDate());

        String query = "with params as (select upper(?1) as lang)" +
                "                         select ce.id as id," +
                "                                ce.competence_group_id as competence," +
                "                                c.competence_name_lang" + languageIndex + " as competence_name," +
                "                                ce.scale_level_id as scale_level," +
                "                                sl.level_number || ' - ' || sl.level_name_lang" + languageIndex + " as scale_level_name," +
                "                                t.code as typeCode" +
                "                           from tsadv_competence_element ce" +
                "                     cross join params par" +
                "                           join tsadv_competence c on (c.group_id = ce.competence_group_id)" +
                "                           join tsadv_scale_level sl on (sl.id = ce.scale_level_id)" +
                "                           left join tsadv_dic_competence_type t on (c.competece_type_id = t.id and t.delete_ts is null) " +
                "                          where ce.person_group_id = ?2" +
                "                            and ce.delete_ts is null" +
                "                            and ?3 between c.start_date and c.end_date";

        List<Object[]> resultList = commonService.emNativeQueryResultList(query, queryParams);

        if (resultList != null) {
            entityList = new ArrayList<>();
            for (Object[] row : resultList) {
                PersonCompetenceInt pci = new PersonCompetenceInt();
                int i = 0;
                pci.setId((UUID) row[i++]);
                pci.setCompetence((UUID) row[i++]);
                pci.setCompetenceName((String) row[i++]);
                pci.setScaleLevel((UUID) row[i++]);
                pci.setScaleLevelName((String) row[i++]);
                pci.setCompetenceTypeCode((String) row[i]);

                entityList.add(pci);
            }
        }
        return entityList;
    }

    protected PersonGroupExt getUserPersonGroup() {
        PersonGroupExt personGroup;
        if (userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP) == null) {
            personGroup = employeeService.getPersonGroupByUserId(userSessionSource.getUserSession().getUser().getId());
            if (personGroup != null) {
                userSessionSource.getUserSession().setAttribute(StaticVariable.USER_PERSON_GROUP,
                        personGroup);//TODO: personGroup test
            }
        } else {
            personGroup = userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP);
        }
        return personGroup;
    }

    protected int languageIndex(String language) {
        String langOrder = AppContext.getProperty("base.abstractDictionary.langOrder");

        if (langOrder != null) {
            List<String> langs = Arrays.asList(langOrder.split(";"));
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

    protected PersonGroupExt getUserPersonGroup(UUID userId) {
        if (userId != null) return employeeService.getPersonGroupByUserId(userId); //TODO: personGroup need test
        return null;
    }

    protected void validatePerson(List<String> errorMessages, PersonInt personInt) {
        if (personInt.getFirstName() == null || personInt.getFirstName().length() == 0)
            errorMessages.add("First name is required!");
        if (personInt.getLastName() == null || personInt.getLastName().length() == 0)
            errorMessages.add("Last name is required!");
        if (personInt.getBirthDate() == null)
            errorMessages.add("Birth date is required!");
        if (personInt.getSex() == null)
            errorMessages.add("Sex is required!");
    }

    protected void validateContact(List<String> errorMessages, PersonContactInt contactInt) {
        if (contactInt.getContactType() == null)
            errorMessages.add("Contact type is required!");
        if (contactInt.getContactValue() == null || contactInt.getContactValue().length() == 0)
            errorMessages.add("Contact value is required!");
    }

    protected void validateEducation(List<String> errorMessages, PersonEducationInt educationInt) {
        if (educationInt.getSchool() == null || educationInt.getSchool().length() == 0)
            errorMessages.add("School is required!");
        if (educationInt.getStartYear() == null || educationInt.getStartYear() == 0)
            errorMessages.add("Start year is required!");
        if (educationInt.getEndYear() == null || educationInt.getEndYear() == 0)
            errorMessages.add("End year is required!");

    }

    protected void validateExperience(List<String> errorMessages, PersonExperienceInt experienceInt) {
        if (experienceInt.getCompany() == null || experienceInt.getCompany().length() == 0)
            errorMessages.add("Company is required!");
        if (experienceInt.getJob() == null || experienceInt.getJob().length() == 0)
            errorMessages.add("Job is required!");
        if (experienceInt.getStartMonth() == null || experienceInt.getStartMonth().length() == 0)
            errorMessages.add("Start month is required!");
        if (!(experienceInt.getUntilNow() == true || experienceInt.getUntilNow() == false)) {
            errorMessages.add("Until now status requires true or false");
        }
        if ((experienceInt.getEndMonth() == null || experienceInt.getEndMonth().length() == 0)
                && experienceInt.getUntilNow() == false)
            errorMessages.add("End month is required!");
    }

    protected void validateCompetence(List<String> errorMessages, PersonCompetenceInt competenceInt) {
        if (competenceInt.getCompetence() == null)
            errorMessages.add("Competence is required!");
        if (competenceInt.getScaleLevel() == null)
            errorMessages.add("Scale level is required!");
    }

    protected void validateAttachment(List<String> errorMessages, PersonAttachmentInt attachmentInt) {
        if (attachmentInt.getCategory() == null)
            errorMessages.add("Category is required!");
    }

    protected void validateUser(List<String> errorMessages, UserInt userInt) {
        if (userInt.getEmail() == null || userInt.getEmail().length() == 0)
            errorMessages.add("Email is required!");
        /*if (userInt.getPhoneNumber() == null || userInt.getPhoneNumber().length() == 0)
            errorMessages.add("Phone number is required!");*/
    }

    protected void validateAddress(List<String> errorMessages, PersonAddressInt addressInt) {
        if (addressInt == null) {
            errorMessages.add("Address is required!");
        }
    }

}