package kz.uco.tsadv.service;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.entity.KeyValueEntity;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.UserSessionSource;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service(RecognitionChartService.NAME)
public class RecognitionChartServiceBean implements RecognitionChartService {

    @Inject
    private Persistence persistence;

    @Inject
    private Metadata metadata;

    @Inject
    private UserSessionSource userSessionSource;

    @Override
    public List<KeyValueEntity> loadGoodsOrderCount(UUID personGroupId) throws SQLException {
        List<KeyValueEntity> resultList = new ArrayList<>();
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            PreparedStatement preparedStatement = em.getConnection().prepareStatement(
                    String.format("SELECT DISTINCT " +
                            "       tgo.person_group_id,     " +
                            "       g.name_lang%s goods_name, " +
//                            "       count(tgod.id) OVER (PARTITION BY tgo.person_group_id, g.id) goods_count " +
                            "       tgod.quantity goods_count " +
                            "FROM TSADV_GOODS_ORDER tgo " +
                            "JOIN TSADV_GOODS_ORDER_DETAIL tgod " +
                            "   ON tgod.goods_order_id = tgo.id " +
                            "   AND tgod.delete_ts IS NULL " +
                            "   AND tgod.excluded = FALSE " +
                            "JOIN tsadv_goods g " +
                            "   ON tgod.goods_id = g.id " +
                            "WHERE tgo.status = 'DELIVERED' " +
                            "  AND tgo.delete_ts IS NULL " +
                            "  AND tgo.person_group_id = ? ", getUserSessionLang()));
            preparedStatement.setObject(1, personGroupId);

            ResultSetMetaData resultSetMetaData = preparedStatement.getMetaData();
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                KeyValueEntity keyValueEntity = metadata.create(KeyValueEntity.class);
                for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                    keyValueEntity.setValue(resultSetMetaData.getColumnName(i), rs.getObject(i));
                }
                resultList.add(keyValueEntity);
            }
        }
        return resultList;
    }

    @Override
    public List<KeyValueEntity> loadRecognitionsCount(UUID personGroupId, Integer period) throws SQLException {
        List<KeyValueEntity> resultList = new ArrayList<>();
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            PreparedStatement preparedStatement = em.getConnection().prepareStatement(String.format(
                    "select t.author_id, t.receiver, count(*) " +
                            "from (SELECT " +
                            "  r.author_id, " +
                            " case " +
                            "     when %d = 3 then " +
                            "        bp.first_name_latin || ' ' || bp.last_name_latin " +
                            "     else " +
                            "        bp.first_name || ' ' || bp.last_name end receiver, " +
                            "  r.recognition_date date, " +
                            "  (CASE WHEN r.recognition_date >= (current_date - INTERVAL '7' DAY) " +
                            "    THEN 4 " + //week
                            "  WHEN r.recognition_date >= (current_date - INTERVAL '1' MONTH) " +
                            "    THEN 3 " + //month
                            "  WHEN r.recognition_date >= (current_date - INTERVAL '3' MONTH) " +
                            "    THEN 2  " + //quarter
                            "  WHEN r.recognition_date >= (current_date - INTERVAL '1' YEAR) " +
                            "    THEN 1  " + //year
                            "  END) period " +
                            "FROM tsadv_recognition r " +
                            "  JOIN base_person bp " +
                            "    ON bp.group_id = r.receiver_id " +
                            "       AND current_date BETWEEN bp.start_date AND bp.end_date " +
                            "       AND bp.delete_ts IS NULL) t %s group by t.author_id, t.receiver " +
                            "having t.author_id = ? ",
                    getUserSessionLang(),
                    period != 0 ? String.format("where t.period >= %d", period) : ""));

            preparedStatement.setObject(1, personGroupId);

            ResultSetMetaData resultSetMetaData = preparedStatement.getMetaData();
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                KeyValueEntity keyValueEntity = metadata.create(KeyValueEntity.class);
                for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                    keyValueEntity.setValue(resultSetMetaData.getColumnName(i), rs.getObject(i));
                }
                resultList.add(keyValueEntity);
            }
        }
        return resultList;
    }

    public List<KeyValueEntity> loadQualities(UUID personGroupId) throws SQLException {
        List<KeyValueEntity> resultList = new ArrayList<>();
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            int langValue = getUserSessionLang();
            PreparedStatement preparedStatement = em.getConnection().prepareStatement(
                    String.format("SELECT DISTINCT " +
                            "  r.receiver_id, " +
                            "  dq.lang_value%d                                quality_name, " +
                            "  count(rq.id) " +
                            "  OVER ( " +
                            "    PARTITION BY r.receiver_id, rq.quality_id ) quality_count " +
                            "FROM tsadv_recognition_quality rq " +
                            "  JOIN tsadv_recognition r " +
                            "    ON r.id = rq.recognition_id " +
                            "       AND r.delete_ts IS NULL " +
                            "  JOIN tsadv_dic_quality dq " +
                            "    ON dq.id = rq.quality_id " +
                            "WHERE rq.delete_ts IS NULL " +
                            "   AND r.receiver_id = ? " +
                            "ORDER BY r.receiver_id," +
                            "  dq.lang_value%d", langValue, langValue));

            preparedStatement.setObject(1, personGroupId);

            ResultSetMetaData resultSetMetaData = preparedStatement.getMetaData();
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                KeyValueEntity keyValueEntity = metadata.create(KeyValueEntity.class);
                for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                    keyValueEntity.setValue(resultSetMetaData.getColumnName(i), rs.getObject(i));
                }
                resultList.add(keyValueEntity);
            }
        }
        return resultList;
    }

    class ChartPojo implements Serializable {
        String category;
        long value;
    }

    private int getUserSessionLang() {
        switch (userSessionSource.getLocale().getLanguage()) {
            case "ru": {
                return 1;
            }
            case "kz": {
                return 2;
            }
            case "en": {
                return 3;
            }
        }
        return 1;
    }
}