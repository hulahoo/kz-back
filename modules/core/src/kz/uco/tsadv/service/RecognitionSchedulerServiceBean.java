package kz.uco.tsadv.service;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.Metadata;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.recognition.*;
import kz.uco.tsadv.modules.recognition.dictionary.DicQuality;
import kz.uco.tsadv.modules.recognition.enums.LogActionType;
import kz.uco.tsadv.modules.recognition.enums.PointOperationType;
import kz.uco.tsadv.modules.recognition.enums.RecognitionCoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service(RecognitionSchedulerService.NAME)
public class RecognitionSchedulerServiceBean implements RecognitionSchedulerService {

    protected static final Logger logger = LoggerFactory.getLogger(RecognitionSchedulerServiceBean.class.getSimpleName());

    @Inject
    protected Metadata metadata;

    @Inject
    protected Persistence persistence;

    @Override
    public void distributionOfPoints() {
//        logger.info("#Start distribution of points");

        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            Query query = em.createNativeQuery(
                    "select t1.person_group_id, t1.rule_id, t1.coin " +
                            "from " +
                            "(select t.person_group_id, " +
                            "        t.rule_id, " +
                            "        t.coin, " +
                            "         max(t.coin) over (partition by t.person_group_id) max_coin " +
                            "from (select distinct a.person_group_id, " +
                            "             coalesce(drpg.id, coalesce(drp.id, coalesce(tcdr.id,null))) rule_id, " +
                            "             coalesce(drpg.coins, coalesce(drp.coins, coalesce(tcdr.coins,0))) coin " +
                            "      from base_assignment a " +
                            "             left join tsadv_coin_distribution_person dpg on a.person_group_id = dpg.person_group_id " +
                            "        and dpg.delete_ts is null " +
                            "             left join tsadv_coin_distribution_rule drpg on dpg.coin_distribution_rule_id = drpg.id " +
                            "        and drpg.delete_ts is null " +
                            "             left join tsadv_coin_distribution_grade dg on a.grade_group_id = dg.grade_group_id " +
                            "        and dg.delete_ts is null " +
                            "             left join tsadv_coin_distribution_rule tcdr on dg.coin_distribution_rule_id = tcdr.id " +
                            "        and tcdr.delete_ts is null " +
                            "             left join tsadv_coin_distribution_position dp on a.position_group_id = dp.position_group_id " +
                            "        and dp.delete_ts is null " +
                            "             left join tsadv_coin_distribution_rule drp on dp.coin_distribution_rule_id = drp.id " +
                            "        and drp.delete_ts is null " +
                            "      where current_date between a.start_date and a.end_date " +
                            "        and a.delete_ts is null) t " +
                            "where t.rule_id is not null) t1 " +
                            "where t1.coin = t1.max_coin");

            List<Object[]> rows = query.getResultList();
            if (rows != null && !rows.isEmpty()) {
                PreparedStatement preparedStatement = em.getConnection().prepareStatement(
                        "UPDATE TSADV_PERSON_POINT SET points = ? WHERE person_group_id = ?");

                for (Object[] row : rows) {
                    UUID personGroupId = (UUID) row[0];
                    Long coins = (Long) row[2];

                    preparedStatement.clearParameters();
                    preparedStatement.setLong(1, coins);
                    preparedStatement.setObject(2, personGroupId);
                    preparedStatement.addBatch();

                    PersonCoinLog personCoinLog = metadata.create(PersonCoinLog.class);
                    personCoinLog.setActionType(LogActionType.RECEIVE_SYSTEM_POINT); //Вы получили Points от системы
                    personCoinLog.setDate(new Date());
                    personCoinLog.setOperationType(PointOperationType.RECEIPT);
                    personCoinLog.setPersonGroup(em.getReference(PersonGroupExt.class, personGroupId));
                    personCoinLog.setCoinDistributionRule(em.getReference(CoinDistributionRule.class, (UUID) row[1]));
                    personCoinLog.setQuantity(coins);
                    personCoinLog.setCoinType(RecognitionCoinType.POINT);
                    em.persist(personCoinLog);
                }

                int[] batchSize = preparedStatement.executeBatch();
                if (batchSize != null && batchSize.length == rows.size()) {
                    tx.commit();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public void checkMedals() {
        Transaction tx = null;
        try {
            tx = persistence.createTransaction();
            EntityManager em = persistence.getEntityManager();
            Query query = em.createQuery(
                    "SELECT * " +
                            "FROM ( " +
                            "       SELECT " +
                            "         rcg.receiver_id, " +
                            "         q.quality_id, " +
                            "         count(q.quality_id) q_count " +
                            "       FROM tsadv_recognition_quality q " +
                            "         JOIN tsadv_recognition rcg " +
                            "           ON rcg.id = q.recognition_id " +
                            "       GROUP BY rcg.receiver_id, q.quality_id) t " +
                            "  JOIN tsadv_medal_condition mc " +
                            "    ON mc.quality_id = t.quality_id " +
                            "WHERE t.q_count >= mc.quality_quantity " +
                            "      AND t.receiver_id = ?1");
            //query.setParameter(1,)
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (tx != null) {
                tx.end();
            }
        }
    }

    @Override
    public void checkMedals(UUID personGroupId) {
        persistence.runInTransaction(new Transaction.Runnable() {
            @Override
            public void run(EntityManager em) {
                Query query = em.createNativeQuery(
                        "WITH person_medal AS (  \n" +
                                "    SELECT  \n" +
                                "      pm.medal_id,  \n" +
                                "      count(*) cnt  \n" +
                                "    FROM tsadv_person_medal pm  \n" +
                                "    WHERE pm.person_group_id = ?1\n" +
                                "      and pm.delete_ts is NULL\n" +
                                "    GROUP BY pm.medal_id  \n" +
                                ")  \n" +
                                "SELECT  \n" +
                                "  mc.medal_id,  \n" +
                                "  sum(((t.q_count * mc.medal_quantity / mc.quality_quantity)) - coalesce(pm.cnt, 0)) difference  \n" +
                                "FROM (  \n" +
                                "       SELECT  \n" +
                                "         q.quality_id,  \n" +
                                "         count(q.quality_id) q_count  \n" +
                                "       FROM tsadv_recognition_quality q  \n" +
                                "         JOIN tsadv_recognition rcg  \n" +
                                "           ON rcg.id = q.recognition_id  \n" +
                                "       WHERE rcg.receiver_id = ?1\n" +
                                "         and rcg.delete_ts is NULL\n" +
                                "         and q.delete_ts is NULL\n" +
                                "       GROUP BY q.quality_id) t  \n" +
                                "  JOIN tsadv_medal_condition mc  \n" +
                                "    ON mc.quality_id = t.quality_id\n" +
                                "  and mc.delete_ts IS null\n" +
                                "  LEFT JOIN person_medal pm  \n" +
                                "    ON pm.medal_id = mc.medal_id  \n" +
                                "WHERE t.q_count >= mc.quality_quantity\n" +
                                " GROUP BY mc.medal_id, mc.quality_id, coalesce(pm.cnt, 0)");

                query.setParameter(1, personGroupId);

                List<Object[]> rows = query.getResultList();
                if (rows != null && !rows.isEmpty()) {
                    for (Object[] row : rows) {
                        BigDecimal difference = (BigDecimal) row[1];
                        if (difference != null) {
                            if (difference.longValue() > 0) {
                                for (long i = 0; i < difference.longValue(); i++) {
                                    UUID medalId = (UUID) row[0];

                                    PersonMedal personMedal = metadata.create(PersonMedal.class);
                                    personMedal.setMedal(em.getReference(Medal.class, medalId));
                                    personMedal.setPersonGroup(em.getReference(PersonGroupExt.class, personGroupId));
//                                    em.persist(personMedal);
                                }
                            }
                        }
                    }
                }

            }
        });
    }
}