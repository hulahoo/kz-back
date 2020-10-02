package kz.uco.tsadv.listener;

import org.springframework.stereotype.Component;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.cuba.core.EntityManager;
import kz.uco.tsadv.modules.recognition.RecognitionQuality;
import com.haulmont.cuba.core.listener.AfterInsertEntityListener;
import java.sql.Connection;

@Component("tsadv_RecognitionQualityListener")
public class RecognitionQualityListener implements BeforeInsertEntityListener<RecognitionQuality>, AfterInsertEntityListener<RecognitionQuality> {


    @Override
    public void onBeforeInsert(RecognitionQuality entity, EntityManager entityManager) {

    }


    @Override
    public void onAfterInsert(RecognitionQuality entity, Connection connection) {
    }


}