package kz.uco.tsadv.service;


import com.haulmont.cuba.core.entity.KeyValueEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface RecognitionChartService {
    String NAME = "tsadv_RecognitionChartService";

    List<KeyValueEntity> loadQualities(UUID personGroupId) throws SQLException;

    List<KeyValueEntity> loadGoodsOrderCount(UUID personGroupId) throws SQLException;

    List<KeyValueEntity> loadRecognitionsCount(UUID personGroupId, Integer period) throws SQLException;
}