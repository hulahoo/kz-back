package kz.uco.tsadv.service;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.sys.AppContext;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.base.entity.dictionary.DicCurrency;
import kz.uco.tsadv.modules.performance.model.Assessment;
import kz.uco.tsadv.modules.performance.model.NotPersistEntity;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.personal.model.PositionExt;
import kz.uco.base.common.StaticVariable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import com.haulmont.cuba.core.entity.FileDescriptor;

import java.util.*;

@Service(PerformanceService.NAME)
public class PerformanceServiceBean implements PerformanceService {

    @Inject
    private Persistence persistence;

    @Inject
    private Metadata metadata;

    @Inject
    private UserSessionSource userSessionSource;

    protected int languageIndex = 0;

    @Override
    public List<NotPersistEntity> getNotPersistEntityList(boolean myTeam, String language) {

        languageIndex = languageIndex(language);

        //TODO filter for systemDate
        List<NotPersistEntity> list = new ArrayList<>();
        String query = "SELECT" +
                "  a.id," +
                "  p.id," +
                "  p.first_name," +
                "  p.last_name," +
                "  p.middle_name," +
                "  p.employee_number," +
                "  p.image_id," +
                "  pos.id," +
                "  pos.position_name_lang" + languageIndex + "," +   //columnChanged TODO
                "  pos.manager_flag," +
                "  assessment.id," +
                "  assessment.competence_rating," +
                "  assessment.goal_rating," +
                "  assessment.overal_rating," +
                "  assessment.performance," +
                "  assessment.potential," +
                "  assessment.risk_of_loss," +
                "  assessment.impact_of_loss," +
                "  count(DISTINCT a2.id) AS direct," +
                "  count(DISTINCT a3.id) AS total," +
                "  ts.amount," +
                "  tdc.id," +
                "  tdc.code," +
                "  tdc.lang_value1," +
                "  tdc.lang_value2," +
                "  tdc.lang_value3," +
                "  tdc.lang_value4," +
                "  tdc.lang_value5, " +
                "  sf.name, " +
                "  sf.ext, " +
                "  sf.file_size, " +
                "  sf.create_date " +
                "FROM BASE_ASSIGNMENT a" +

                "  LEFT JOIN BASE_POSITION pos" +
                "    ON pos.group_id = a.position_group_id AND" +
                "       ?1 BETWEEN pos.start_date AND pos.end_date AND" +
                "       pos.delete_ts IS NULL" +

                "  LEFT JOIN BASE_PERSON p" +
                "    ON p.group_id = a.person_group_id AND" +
                "       ?1 BETWEEN p.start_date AND p.end_date AND" +
                "       p.delete_ts IS NULL" +

                "  LEFT JOIN tsadv_assessment assessment" +
                "    ON assessment.employee_person_group_id = a.person_group_id AND" +
                "       assessment.start_date = (SELECT max(start_date) AS max_date" +
                "                                FROM tsadv_assessment" +
                "                                WHERE employee_person_group_id = a.person_group_id AND" +
                "                                      delete_ts is null) AND" +
                "       assessment.delete_ts IS NULL" +

                "  LEFT JOIN tsadv_position_structure tps" +
                "    ON tps.parent_position_group_id = a.position_group_id AND" +
                "       ?1 BETWEEN tps.start_date AND tps.end_date AND" +
                "       ?1 BETWEEN tps.pos_start_date AND tps.pos_end_date AND" +
                "       tps.delete_ts IS NULL" +

                "  LEFT JOIN BASE_ASSIGNMENT a2" +
                "    ON tps.position_group_id = a2.position_group_id AND" +
                "       ?1 BETWEEN a2.start_date AND a2.end_date AND" +
                "       a2.delete_ts IS NULL" +

                "  LEFT JOIN tsadv_position_structure tps2" +
                "    ON tps2.position_group_path LIKE '%' || a.position_group_id :: TEXT || '*%' AND" +
                "       ?1 BETWEEN tps2.start_date AND tps2.end_date AND" +
                "       ?1 BETWEEN tps2.pos_start_date AND tps2.pos_end_date AND" +
                "       tps2.delete_ts IS NULL" +

                "  LEFT JOIN BASE_ASSIGNMENT a3" +
                "    ON tps2.position_group_id = a3.position_group_id AND" +
                "       ?1 BETWEEN a3.start_date AND a3.end_date AND" +
                "       a3.delete_ts IS NULL" +

                "  LEFT JOIN tsadv_position_structure tps3" +
                "    ON tps3.position_group_id = a.position_group_id AND" +
                "       ?1 BETWEEN tps3.start_date AND tps3.end_date AND" +
                "       ?1 BETWEEN tps3.pos_start_date AND tps3.pos_end_date AND" +
                "       tps3.delete_ts IS NULL" +

                "  LEFT JOIN tsadv_salary ts" +
                "    ON ts.assignment_group_id = a.group_id AND" +
                "       ?1 BETWEEN ts.start_date AND ts.end_date AND" +
                "       ts.delete_ts IS NULL" +

                "  LEFT JOIN BASE_DIC_CURRENCY tdc" +
                "    ON tdc.id = ts.currency_id AND" +
                "       tdc.delete_ts IS NULL " +

                "  LEFT JOIN SYS_FILE sf" +
                "    ON sf.id = p.image_id AND" +
                "       sf.delete_ts IS NULL " +

                "WHERE ?1 BETWEEN a.start_date AND a.end_date AND" +
                "      (a.person_group_id = ?2 OR" +
                "      tps3.parent_position_group_id = ?3 OR" +
                "      tps3.position_group_path :: TEXT LIKE ?4) AND" +
                "      a.delete_ts IS NULL " +

                "GROUP BY" +
                "  a.id," +
                "  p.id," +
                "  p.first_name," +
                "  p.last_name," +
                "  p.middle_name," +
                "  p.employee_number," +
                "  p.image_id," +
                "  pos.id," +
                "  pos.position_name_lang" + languageIndex + "," +    //columnChanged TODO
                "  pos.manager_flag," +
                "  assessment.id," +
                "  assessment.competence_rating," +
                "  assessment.goal_rating," +
                "  assessment.overal_rating," +
                "  assessment.performance," +
                "  assessment.potential," +
                "  assessment.risk_of_loss," +
                "  assessment.impact_of_loss," +
                "  tps3.lvl," +
                "  ts.amount," +
                "  tdc.id," +
                "  tdc.code," +
                "  tdc.lang_value1," +
                "  tdc.lang_value2," +
                "  tdc.lang_value3," +
                "  tdc.lang_value4," +
                "  tdc.lang_value5, " +
                "  sf.name, " +
                "  sf.ext, " +
                "  sf.file_size, " +
                "  sf.create_date " +
                "ORDER BY tps3.lvl";

        try (Transaction transaction = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            UserSessionSource userSessionSource = AppBeans.get(UserSessionSource.class);
            List resultList = em.createNativeQuery(query)
                    .setParameter(1, CommonUtils.getSystemDate())
                    .setParameter(2, userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP_ID))
                    .setParameter(3, !myTeam ? null : userSessionSource.getUserSession().getAttribute(StaticVariable.POSITION_GROUP_ID))
                    .setParameter(4, myTeam ? null : "%" + userSessionSource.getUserSession().getAttribute(StaticVariable.POSITION_GROUP_ID) + "%")
                    .getResultList();
            if (!resultList.isEmpty())
                resultList.forEach(objects -> list.add(parseQueryResult((Object[]) objects)));
        }
        return list;
    }

    @Override
    public List<NotPersistEntity> getNotPersistEntityList(boolean myTeam, OrganizationGroupExt organizationGroupExt, String fio, String language){
        languageIndex = languageIndex(language);

        //TODO filter for systemDate
        List<NotPersistEntity> list = new ArrayList<>();
        String query = "SELECT" +
                "  a.id," +
                "  p.id," +
                "  p.first_name," +
                "  p.last_name," +
                "  p.middle_name," +
                "  p.employee_number," +
                "  p.image_id," +
                "  pos.id," +
                "  pos.position_name_lang" + languageIndex + "," +   //columnChanged TODO
                "  pos.manager_flag," +
                "  assessment.id," +
                "  assessment.competence_rating," +
                "  assessment.goal_rating," +
                "  assessment.overal_rating," +
                "  assessment.performance," +
                "  assessment.potential," +
                "  assessment.risk_of_loss," +
                "  assessment.impact_of_loss," +
                "  count(DISTINCT a2.id) AS direct," +
                "  count(DISTINCT a3.id) AS total," +
                "  ts.amount," +
                "  tdc.id," +
                "  tdc.code," +
                "  tdc.lang_value1," +
                "  tdc.lang_value2," +
                "  tdc.lang_value3," +
                "  tdc.lang_value4," +
                "  tdc.lang_value5, " +
                "  sf.name, " +
                "  sf.ext, " +
                "  sf.file_size, " +
                "  sf.create_date " +
                "FROM BASE_ASSIGNMENT a" +

                "  LEFT JOIN BASE_POSITION pos" +
                "    ON pos.group_id = a.position_group_id AND" +
                "       ?1 BETWEEN pos.start_date AND pos.end_date AND" +
                "       pos.delete_ts IS NULL" +

                "  LEFT JOIN BASE_PERSON p" +
                "    ON p.group_id = a.person_group_id AND" +
                "       ?1 BETWEEN p.start_date AND p.end_date AND" +
                "       p.delete_ts IS NULL" +

                "  LEFT JOIN tsadv_assessment assessment" +
                "    ON assessment.employee_person_group_id = a.person_group_id AND" +
                "       assessment.start_date = (SELECT max(start_date) AS max_date" +
                "                                FROM tsadv_assessment" +
                "                                WHERE employee_person_group_id = a.person_group_id AND" +
                "                                      delete_ts is null) AND" +
                "       assessment.delete_ts IS NULL" +

                "  LEFT JOIN tsadv_position_structure tps" +
                "    ON tps.parent_position_group_id = a.position_group_id AND" +
                "       ?1 BETWEEN tps.start_date AND tps.end_date AND" +
                "       ?1 BETWEEN tps.pos_start_date AND tps.pos_end_date AND" +
                "       tps.delete_ts IS NULL" +

                "  LEFT JOIN BASE_ASSIGNMENT a2" +
                "    ON tps.position_group_id = a2.position_group_id AND" +
                "       ?1 BETWEEN a2.start_date AND a2.end_date AND" +
                "       a2.delete_ts IS NULL" +

                "  LEFT JOIN tsadv_position_structure tps2" +
                "    ON tps2.position_group_path LIKE '%' || a.position_group_id :: TEXT || '*%' AND" +
                "       ?1 BETWEEN tps2.start_date AND tps2.end_date AND" +
                "       ?1 BETWEEN tps2.pos_start_date AND tps2.pos_end_date AND" +
                "       tps2.delete_ts IS NULL" +

                "  LEFT JOIN BASE_ASSIGNMENT a3" +
                "    ON tps2.position_group_id = a3.position_group_id AND" +
                "       ?1 BETWEEN a3.start_date AND a3.end_date AND" +
                "       a3.delete_ts IS NULL" +

                "  LEFT JOIN tsadv_position_structure tps3" +
                "    ON tps3.position_group_id = a.position_group_id AND" +
                "       ?1 BETWEEN tps3.start_date AND tps3.end_date AND" +
                "       ?1 BETWEEN tps3.pos_start_date AND tps3.pos_end_date AND" +
                "       tps3.delete_ts IS NULL" +

                "  LEFT JOIN tsadv_salary ts" +
                "    ON ts.assignment_group_id = a.group_id AND" +
                "       ?1 BETWEEN ts.start_date AND ts.end_date AND" +
                "       ts.delete_ts IS NULL" +

                "  LEFT JOIN BASE_DIC_CURRENCY tdc" +
                "    ON tdc.id = ts.currency_id AND" +
                "       tdc.delete_ts IS NULL " +

                "  LEFT JOIN SYS_FILE sf" +
                "    ON sf.id = p.image_id AND" +
                "       sf.delete_ts IS NULL " +

                "WHERE ?1 BETWEEN a.start_date AND a.end_date AND" +
                "      (a.person_group_id = ?2 OR" +
                "      tps3.parent_position_group_id = ?3 OR" +
                "      tps3.position_group_path :: TEXT LIKE ?4) AND" +
                "      a.delete_ts IS NULL " +

                "GROUP BY" +
                "  a.id," +
                "  p.id," +
                "  p.first_name," +
                "  p.last_name," +
                "  p.middle_name," +
                "  p.employee_number," +
                "  p.image_id," +
                "  pos.id," +
                "  pos.position_name_lang" + languageIndex + "," +    //columnChanged TODO
                "  pos.manager_flag," +
                "  assessment.id," +
                "  assessment.competence_rating," +
                "  assessment.goal_rating," +
                "  assessment.overal_rating," +
                "  assessment.performance," +
                "  assessment.potential," +
                "  assessment.risk_of_loss," +
                "  assessment.impact_of_loss," +
                "  tps3.lvl," +
                "  ts.amount," +
                "  tdc.id," +
                "  tdc.code," +
                "  tdc.lang_value1," +
                "  tdc.lang_value2," +
                "  tdc.lang_value3," +
                "  tdc.lang_value4," +
                "  tdc.lang_value5, " +
                "  sf.name, " +
                "  sf.ext, " +
                "  sf.file_size, " +
                "  sf.create_date " +
                "ORDER BY tps3.lvl";

        try (Transaction transaction = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            UserSessionSource userSessionSource = AppBeans.get(UserSessionSource.class);
            List resultList = em.createNativeQuery(query)
                    .setParameter(1, CommonUtils.getSystemDate())
                    .setParameter(2, userSessionSource.getUserSession().getAttribute(StaticVariable.USER_PERSON_GROUP_ID))
                    .setParameter(3, !myTeam ? null : userSessionSource.getUserSession().getAttribute(StaticVariable.POSITION_GROUP_ID))
                    .setParameter(4, myTeam ? null : "%" + userSessionSource.getUserSession().getAttribute(StaticVariable.POSITION_GROUP_ID) + "%")
                    .getResultList();
            if (!resultList.isEmpty())
                resultList.forEach(objects -> list.add(parseQueryResult((Object[]) objects)));
        }
        return list;
    }

    private int languageIndex(String language) {
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

    private NotPersistEntity parseQueryResult(Object[] row) {
        NotPersistEntity entity = metadata.create(NotPersistEntity.class);

        PersonExt person = metadata.create(PersonExt.class);
        person.setId((UUID) row[1]);
        person.setFirstName((String) row[2]);
        person.setLastName((String) row[3]);
        person.setMiddleName((String) row[4]);
        person.setEmployeeNumber((String) row[5]);
        if (row[6] == null) {
            person.setImage(null);
        } else {
            FileDescriptor fileDescriptor = metadata.create(FileDescriptor.class);
            fileDescriptor.setId((UUID) row[6]);
            fileDescriptor.setName((String) row[28]);
            fileDescriptor.setExtension((String) row[29]);
            fileDescriptor.setSize((Long) row[30]);
            fileDescriptor.setCreateDate((Date) row[31]);
            person.setImage(fileDescriptor);
        }

        PositionExt position = metadata.create(PositionExt.class);
        position.setId((UUID) row[7]);
        position.setPositionName((String) row[8]); // columnChanged TODO
        position.setManagerFlag((Boolean) row[9]);

        Assessment assessment = metadata.create(Assessment.class);
        assessment.setId((UUID) row[10]);
        assessment.setCompetenceRating((Double) row[11]);
        assessment.setGoalRating((Double) row[12]);
        assessment.setOveralRating((Double) row[13]);
        assessment.setPerformance((Double) row[14]);
        assessment.setPotential((Double) row[15]);
        assessment.setRiskOfLoss((Double) row[16]);
        assessment.setImpactOfLoss((Double) row[17]);

        entity.setAssignmentId((UUID) row[0]);
        entity.setPerson(person);
        entity.setPosition(position);
        entity.setAssessment(assessment);
        entity.setDirect((Long) row[18]);
        entity.setTotal((Long) row[19]);
        entity.setAmount((Integer) row[20]);

        DicCurrency dicCurrency = metadata.create(DicCurrency.class);
        dicCurrency.setId((UUID) row[21]);
        dicCurrency.setCode((String) row[22]);
        dicCurrency.setLangValue1((String) row[23]);
        dicCurrency.setLangValue2((String) row[24]);
        dicCurrency.setLangValue3((String) row[25]);
        dicCurrency.setLangValue4((String) row[26]);
        dicCurrency.setLangValue5((String) row[27]);
        return entity;
    }
}